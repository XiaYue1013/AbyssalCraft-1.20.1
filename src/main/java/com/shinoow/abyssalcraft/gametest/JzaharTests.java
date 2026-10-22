/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - automated verification (./gradlew runGameTestServer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.common.entity.*;
import com.shinoow.abyssalcraft.common.ritual.NecronomiconRespawnJzaharRitual;
import com.shinoow.abyssalcraft.common.world.structure.ACTemplatePiece;
import com.shinoow.abyssalcraft.common.world.structure.JzaharTempleStructure;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for J'zahar, his minions, spells and temple. They run in their own batches: J'zahar banishes every boss
 * within 64 blocks, including those of other tests.
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class JzaharTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	private static <T extends Mob> T mob(GameTestHelper helper, EntityType<T> type, BlockPos pos) {
		T mob = helper.spawn(type, pos);
		mob.setNoAi(true);
		return mob;
	}

	private static AABB area(GameTestHelper helper) {
		return new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
	}

	/** Big hits are cut to 5 - 9, he's immune to fire, magic and potion effects, and only takes damage every 1.5 seconds */
	@GameTest(template = "empty", batch = "jzahar")
	public static void jzaharBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntityJzahar jzahar = mob(helper, ACEntityTypes.JZAHAR.get(), new BlockPos(4, 1, 4));
		helper.assertFalse(jzahar.hurt(level.damageSources().onFire(), 10), "hurt by fire");
		helper.assertFalse(jzahar.hurt(level.damageSources().magic(), 10), "hurt by magic");
		helper.assertFalse(jzahar.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), "got an effect");
		float health = jzahar.getHealth();
		helper.assertTrue(jzahar.hurt(level.damageSources().generic(), 500), "not hurt");
		float lost = health - jzahar.getHealth();
		helper.assertTrue(lost >= 5 && lost <= 9, "a 500 hit did " + lost); // (generic damage ignores armor)
		helper.assertFalse(jzahar.hurt(level.damageSources().generic(), 5), "hurt again right away");
		helper.assertTrue(level.getServer().getLootData().getLootTable(jzahar.getLootTable()) != LootTable.EMPTY, "no loot table");
		jzahar.discard();
		helper.succeed();
	}

	/** Other bosses near J'zahar are banished */
	@GameTest(template = "empty", batch = "jzahar")
	public static void jzaharBanishesBosses(GameTestHelper helper) {
		floor(helper);
		mob(helper, ACEntityTypes.JZAHAR.get(), new BlockPos(1, 1, 1));
		WitherBoss wither = mob(helper, EntityType.WITHER, new BlockPos(6, 1, 6));
		EntityChagaroth chagaroth = mob(helper, ACEntityTypes.CHAGAROTH.get(), new BlockPos(6, 1, 1));
		helper.succeedWhen(() -> {
			helper.assertTrue(wither.isRemoved(), "the Wither is still here");
			helper.assertTrue(chagaroth.isRemoved(), "Cha'garoth is still here");
			helper.getLevel().getEntitiesOfClass(EntityJzahar.class, area(helper)).forEach(Entity::discard);
		});
	}

	/** The Minion: poison immune, loot table with eldritch scales, spawns in Omothol */
	@GameTest(template = "empty", batch = "jzahar")
	public static void gatekeeperMinionBasics(GameTestHelper helper) {
		floor(helper);
		EntityGatekeeperMinion minion = mob(helper, ACEntityTypes.MINION_OF_THE_GATEKEEPER.get(), new BlockPos(4, 1, 4));
		helper.assertFalse(minion.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), "poisoned");
		helper.assertFalse(minion.removeWhenFarAway(1000), "despawns");
		helper.assertTrue(helper.getLevel().getServer().getLootData().getLootTable(minion.getLootTable()) != LootTable.EMPTY, "no loot table");
		helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.omothol).getMobSettings()
				.getMobs(MobCategory.MONSTER).unwrap().stream().anyMatch(m -> m.type == ACEntityTypes.MINION_OF_THE_GATEKEEPER.get() && m.getWeight().asInt() == 3),
				"no Minion spawns in Omothol");
		helper.succeed();
	}

	/** An implosion hurts what's next to it and bursts after 18 seconds */
	@GameTest(template = "empty", batch = "jzahar", timeoutTicks = 420)
	public static void implosionBursts(GameTestHelper helper) {
		floor(helper);
		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(4, 1, 4));
		pig.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
		pig.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);
		pig.setHealth(1000);
		EntityImplosion implosion = ACEntityTypes.IMPLOSION.get().create(helper.getLevel());
		BlockPos pos = helper.absolutePos(new BlockPos(4, 1, 4));
		implosion.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		helper.getLevel().addFreshEntity(implosion);
		helper.runAtTickTime(100, () -> helper.assertTrue(pig.getHealth() < 1000, "the pig next to it isn't hurt"));
		helper.succeedWhen(() -> {
			helper.assertTrue(implosion.isRemoved(), "still imploding");
			pig.discard();
		});
	}

	/** A black hole throws living things that come within 3 blocks into another dimension */
	@GameTest(template = "empty", batch = "black_hole", timeoutTicks = 100)
	public static void blackHoleThrowsAway(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntityJzahar jzahar = mob(helper, ACEntityTypes.JZAHAR.get(), new BlockPos(1, 1, 1));
		Pig pig = mob(helper, EntityType.PIG, new BlockPos(6, 1, 6));
		EntityBlackHole hole = new EntityBlackHole(level, jzahar);
		hole.setPos(pig.getX(), pig.getY() + 1, pig.getZ());
		level.addFreshEntity(hole);
		helper.succeedWhen(() -> {
			helper.assertTrue(pig.isRemoved() && pig.getRemovalReason() == Entity.RemovalReason.CHANGED_DIMENSION, "the pig is still here");
			hole.discard();
			jzahar.discard();
			level.getEntitiesOfClass(net.minecraft.world.entity.item.FallingBlockEntity.class, area(helper)).forEach(Entity::discard);
		});
	}

	/**
	 * J'zahar's 40 second death: XP at the end, the living things next to him gone, the area cleared (mobGriefing),
	 * removed after 800 ticks (alone in its batch, with mobGriefing off so the neighbors survive)
	 */
	@GameTest(template = "empty", batch = "jzahar_death", timeoutTicks = 900)
	public static void jzaharDeath(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
		boolean wasGriefing = griefing.get();
		griefing.set(false, level.getServer());
		EntityJzahar jzahar = mob(helper, ACEntityTypes.JZAHAR.get(), new BlockPos(4, 1, 4));
		Pig pig = mob(helper, EntityType.PIG, new BlockPos(5, 1, 4));
		jzahar.kill();
		helper.assertTrue(jzahar.isDeadOrDying(), "/kill didn't kill him");
		helper.runAtTickTime(400, () -> helper.assertFalse(jzahar.isRemoved(), "removed early"));
		helper.succeedWhen(() -> {
			helper.assertTrue(jzahar.isRemoved(), "still dying");
			helper.assertTrue(pig.isRemoved(), "the pig next to him survived");
			helper.assertFalse(level.getEntitiesOfClass(ExperienceOrb.class, area(helper)).isEmpty(), "no XP");
			level.getEntitiesOfClass(ExperienceOrb.class, area(helper)).forEach(Entity::discard);
			griefing.set(wasGriefing, level.getServer());
		});
	}

	/** The Respawn J'zahar ritual only works on the temple altar spot, and rebuilds the temple with J'zahar's spawner */
	@GameTest(template = "empty", batch = "jzahar", timeoutTicks = 200)
	public static void templeRebuilds(GameTestHelper helper) {
		ServerLevel omothol = helper.getLevel().getServer().getLevel(ACDimensions.OMOTHOL);
		helper.assertTrue(NecronomiconRespawnJzaharRitual.ALTAR.equals(new BlockPos(4, 54, 85)), "altar spot moved");
		helper.assertTrue(NecronomiconRespawnJzaharRitual.rebuildTemple(omothol), "temple not rebuilt");
		BlockPos ground = new BlockPos(4, 53, 7);
		while(omothol.isEmptyBlock(ground) && ground.getY() > 2)
			ground = ground.below();
		int spawners = 0;
		for(ACTemplatePiece piece : JzaharTempleStructure.pieces(omothol.getStructureManager(), ground)) {
			BoundingBox box = piece.getBoundingBox();
			for(BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()))
				if(omothol.getBlockState(pos).is(BlockHandler.JZAHAR_SPAWNER.get()))
					spawners++;
		}
		helper.assertTrue(spawners >= 1, "no J'zahar spawner in the temple");
		helper.succeed();
	}
}
