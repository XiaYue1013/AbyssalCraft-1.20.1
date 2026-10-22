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

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.*;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.common.world.gen.ChagarothLair;
import com.shinoow.abyssalcraft.common.world.structure.ACTemplatePiece;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for Cha'garoth, the Dread spawn family, the Altar of Cha'garoth and the lair
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class ChagarothTests {

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

	private static Pig sturdyPig(GameTestHelper helper, BlockPos pos) {
		Pig pig = mob(helper, EntityType.PIG, pos);
		pig.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500);
		pig.setHealth(500);
		return pig;
	}

	private static AABB area(GameTestHelper helper) {
		return new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
	}

	/** All six are fire immune Dread mobs, take no fall damage (the spawn family), give the Dread Plague and have loot tables */
	@GameTest(template = "empty")
	public static void dreadFamilyBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		List<EntityType<? extends Mob>> types = List.of(ACEntityTypes.DREAD_SPAWN.get(), ACEntityTypes.SPAWN_OF_CHAGAROTH.get(),
				ACEntityTypes.GREATER_DREAD_SPAWN.get(), ACEntityTypes.LESSER_DREADBEAST.get(), ACEntityTypes.FIST_OF_CHAGAROTH.get());
		for(EntityType<? extends Mob> type : types) {
			Mob mob = mob(helper, type, new BlockPos(1, 1, 1));
			helper.assertTrue(mob.fireImmune() && mob instanceof com.shinoow.abyssalcraft.api.entity.IDreadEntity, type + " isn't a fire immune Dread mob");
			if(mob instanceof EntityDreadSpawnBase base)
				helper.assertFalse(base.causeFallDamage(20, 1, level.damageSources().fall()), type + " takes fall damage");
			if(type != ACEntityTypes.FIST_OF_CHAGAROTH.get()) {
				helper.assertTrue(level.getServer().getLootData().getLootTable(mob.getLootTable()) != LootTable.EMPTY, type + " has no loot table");
				Pig pig = sturdyPig(helper, new BlockPos(2, 1, 1));
				helper.assertTrue(mob.doHurtTarget(pig), type + " missed");
				helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.dread_plague.get()), type + " didn't give the Dread Plague");
				pig.discard();
			}
			mob.discard();
		}
		helper.succeed();
	}

	/** Five Dread Spawn merge into a Greater Dread Spawn, which splits into two Dread Spawn when killed */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void dreadSpawnsMerge(GameTestHelper helper) {
		floor(helper);
		for(int i = 0; i < 5; i++)
			mob(helper, ACEntityTypes.DREAD_SPAWN.get(), new BlockPos(4, 1, 4));
		helper.succeedWhen(() -> {
			var greater = helper.getLevel().getEntitiesOfClass(EntityGreaterDreadSpawn.class, area(helper));
			helper.assertTrue(greater.size() == 1, greater.size() + " Greater Dread Spawn");
			helper.assertTrue(helper.getLevel().getEntitiesOfClass(EntityDreadSpawn.class, area(helper)).isEmpty(), "Dread Spawn left");
			greater.get(0).hurt(helper.getLevel().damageSources().genericKill(), Float.MAX_VALUE);
			var split = helper.getLevel().getEntitiesOfClass(EntityDreadSpawn.class, area(helper));
			helper.assertTrue(split.size() == 2, "killed, it split into " + split.size());
			split.forEach(EntityDreadSpawn::discard);
		});
	}

	/** Five Greater Dread Spawn merge into a Lesser Dreadbeast */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void greaterDreadSpawnsMerge(GameTestHelper helper) {
		floor(helper);
		for(int i = 0; i < 5; i++)
			mob(helper, ACEntityTypes.GREATER_DREAD_SPAWN.get(), new BlockPos(4, 1, 4));
		helper.succeedWhen(() -> {
			helper.assertTrue(helper.getLevel().getEntitiesOfClass(EntityLesserDreadbeast.class, area(helper)).size() == 1, "no Lesser Dreadbeast");
			helper.assertTrue(helper.getLevel().getEntitiesOfClass(EntityGreaterDreadSpawn.class, area(helper)).isEmpty(), "Greater Dread Spawn left");
		});
	}

	/** Dread Slugs do 6 damage and give the Dread Plague */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void dreadSlugHits(GameTestHelper helper) {
		floor(helper);
		EntityGreaterDreadSpawn thrower = mob(helper, ACEntityTypes.GREATER_DREAD_SPAWN.get(), new BlockPos(1, 1, 1));
		Zombie zombie = mob(helper, EntityType.ZOMBIE, new BlockPos(6, 1, 6));
		EntityDreadSlug slug = new EntityDreadSlug(helper.getLevel(), thrower);
		BlockPos start = helper.absolutePos(new BlockPos(6, 2, 2));
		slug.setPos(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
		slug.shoot(0, 0, 1, 0.4F, 0);
		slug.setNoGravity(true);
		helper.getLevel().addFreshEntity(slug);
		helper.succeedWhen(() -> {
			// 6 less the zombie's 2 armor points (5.9), then the plague
			helper.assertTrue(zombie.getHealth() <= zombie.getMaxHealth() - 5.9F, "zombie health " + zombie.getHealth());
			helper.assertTrue(zombie.hasEffect(AbyssalCraftAPI.dread_plague.get()), "no Dread Plague");
			helper.assertTrue(slug.isRemoved(), "slug still flying");
		});
	}

	/** Cha'garoth: big hits are cut to 10 - 19, no potion effects, rooted, spreads the Dreadlands biome around it */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void chagarothBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntityChagaroth boss = mob(helper, ACEntityTypes.CHAGAROTH.get(), new BlockPos(4, 1, 4));
		BlockPos pos = boss.blockPosition();
		float health = boss.getHealth();
		boss.hurt(level.damageSources().generic(), 500);
		float lost = health - boss.getHealth();
		// cut to 10 - 19 damage (generic damage bypasses armor)
		helper.assertTrue(lost >= 10 && lost <= 19, "a 500 hit did " + lost);
		helper.assertFalse(boss.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), "got an effect");
		boss.setDeltaMovement(new Vec3(1, 0, 1));
		helper.succeedWhen(() -> {
			helper.assertTrue(boss.position().distanceTo(Vec3.atBottomCenterOf(pos)) < 0.6, "moved to " + boss.position());
			helper.assertTrue(level.getBiome(pos).is(ACBiomes.dreadlands), "the ground isn't Dreadlands");
			// put the test area back
			List<BlockPos> columns = new java.util.ArrayList<>();
			for(int x = -3; x <= 3; x++)
				for(int z = -3; z <= 3; z++)
					columns.add(pos.offset(x, 0, z));
			BiomeUtil.updateBiomes(level, columns, Biomes.PLAINS);
			boss.discard();
		});
	}

	/** Killed, Cha'garoth dies for 10 seconds, then leaves XP, Dreadlands materials and the Dread-plagued Gateway Key */
	@GameTest(template = "empty", timeoutTicks = 300)
	public static void chagarothDeath(GameTestHelper helper) {
		floor(helper);
		EntityChagaroth boss = mob(helper, ACEntityTypes.CHAGAROTH.get(), new BlockPos(4, 1, 4));
		boss.kill();
		helper.assertTrue(boss.isDeadOrDying(), "/kill didn't kill it");
		helper.runAtTickTime(100, () -> helper.assertFalse(boss.isRemoved(), "removed before the 200 death ticks"));
		helper.succeedWhen(() -> {
			helper.assertTrue(boss.isRemoved(), "still dying");
			var items = helper.getLevel().getEntitiesOfClass(ItemEntity.class, area(helper));
			helper.assertTrue(items.stream().anyMatch(i -> i.getItem().is(ACItems.dread_plagued_gateway_key.get())), "no key");
			helper.assertTrue(items.stream().anyMatch(i -> i.getItem().is(ACItems.dreadium_ingot.get())), "no dreadium");
			// (orbs of the same value merge, so their values don't add up to the 5,000 XP)
			helper.assertFalse(helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, area(helper)).isEmpty(), "no XP");
			items.forEach(ItemEntity::discard);
			helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, area(helper)).forEach(ExperienceOrb::discard);
		});
	}

	/** The altar gives the Dread Plague to what stands in it */
	@GameTest(template = "empty")
	public static void dreadAltarInfects(GameTestHelper helper) {
		floor(helper);
		helper.setBlock(new BlockPos(4, 1, 4), BlockHandler.CHAGAROTH_ALTAR_TOP.get());
		// (mobs without AI don't move, and only moving entities touch blocks) a pig that can't walk
		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(4, 1, 4));
		pig.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
		helper.succeedWhen(() -> helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.dread_plague.get()), "no Dread Plague"));
	}

	/** The lair: five templates with dreadstone bricks (some cracked), the Cha'garoth spawner and the treasure chest */
	@GameTest(template = "empty", timeoutTicks = 200)
	public static void chagarothLairBuilds(GameTestHelper helper) {
		ServerLevel level = helper.getLevel().getServer().getLevel(ACDimensions.DREADLANDS);
		BlockPos altar = new BlockPos(30000, 41, 30000);
		ChagarothLair.generate(level, altar);
		int bricks = 0, cracked = 0, spawners = 0;
		ChestBlockEntity treasure = null;
		for(ACTemplatePiece piece : ChagarothLair.pieces(level.getStructureManager(), altar)) {
			BoundingBox box = piece.getBoundingBox();
			for(BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
				var state = level.getBlockState(pos);
				if(state.is(ACBlocks.dreadstone_bricks.get())) bricks++;
				else if(state.is(ACBlocks.cracked_dreadstone_bricks.get())) cracked++;
				else if(state.is(BlockHandler.CHAGAROTH_SPAWNER.get())) spawners++;
				else if(state.is(Blocks.CHEST) && level.getBlockEntity(pos) instanceof ChestBlockEntity chest && chest.getItem(13).hasCustomHoverName())
					treasure = chest;
			}
		}
		AbyssalCraft.LOGGER.info("[ChagarothTests] lair: {} dreadstone bricks, {} cracked, {} Cha'garoth spawners", bricks, cracked, spawners);
		helper.assertTrue(bricks > 500, "only " + bricks + " dreadstone bricks");
		double share = cracked / (double)(bricks + cracked);
		helper.assertTrue(share > 0.3 && share < 0.55, "cracked share " + share);
		helper.assertTrue(spawners >= 1, "no Cha'garoth spawner");
		helper.assertTrue(treasure != null, "no Legendary Treasure chest");
		helper.succeed();
	}

	/** Dreadlands spawns of the family (1.12.2 BiomeGenDreadlandsBase + forest/mountain additions) */
	@GameTest(template = "empty")
	public static void dreadFamilySpawns(GameTestHelper helper) {
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		for(var key : List.of(ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest, ACBiomes.dreadlands_mountains)) {
			var mobs = biomes.getOrThrow(key).getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
			java.util.function.ToIntFunction<EntityType<?>> weight = type -> mobs.stream().filter(m -> m.type == type).mapToInt(m -> m.getWeight().asInt()).sum();
			helper.assertTrue(weight.applyAsInt(ACEntityTypes.DREAD_SPAWN.get()) == (key == ACBiomes.dreadlands_forest ? 80 : 30), key.location() + " Dread Spawn");
			helper.assertTrue(weight.applyAsInt(ACEntityTypes.FIST_OF_CHAGAROTH.get()) == (key == ACBiomes.dreadlands_mountains ? 27 : 2), key.location() + " Fist");
			helper.assertTrue(weight.applyAsInt(ACEntityTypes.SPAWN_OF_CHAGAROTH.get()) == (key == ACBiomes.dreadlands_mountains ? 30 : 0), key.location() + " Spawn of Cha'garoth");
			helper.assertTrue(weight.applyAsInt(ACEntityTypes.GREATER_DREAD_SPAWN.get()) == 5 && weight.applyAsInt(ACEntityTypes.LESSER_DREADBEAST.get()) == 1,
					key.location() + " Greater Dread Spawn / Lesser Dreadbeast");
		}
		helper.succeed();
	}
}
