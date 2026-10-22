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
import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.EntityOmotholGhoul;
import com.shinoow.abyssalcraft.common.entity.anti.AntiHumanoids;
import com.shinoow.abyssalcraft.common.entity.anti.AntiUndead;
import com.shinoow.abyssalcraft.common.entity.anti.EntityAntiCreeper;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the antimatter mobs
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class AntiMobTests {

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

	/** All eleven are antimatter mobs with a loot table, and spawn in the Coralium Infested Swamp */
	@GameTest(template = "empty")
	public static void antiMobBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		List<EntityType<? extends Mob>> types = List.of(ACEntityTypes.ANTI_PIG.get(), ACEntityTypes.ANTI_COW.get(), ACEntityTypes.ANTI_CHICKEN.get(),
				ACEntityTypes.ANTI_BAT.get(), ACEntityTypes.ANTI_ZOMBIE.get(), ACEntityTypes.ABYSSAL_ANTI_ZOMBIE.get(), ACEntityTypes.ANTI_SKELETON.get(),
				ACEntityTypes.ANTI_SPIDER.get(), ACEntityTypes.ANTI_CREEPER.get(), ACEntityTypes.ANTI_GHOUL.get(), ACEntityTypes.ANTI_PLAYER.get());
		var swamp = level.registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.coralium_infested_swamp).getMobSettings();
		for(EntityType<? extends Mob> type : types) {
			Mob mob = mob(helper, type, new BlockPos(1, 1, 1));
			helper.assertTrue(mob instanceof IAntiEntity, type + " isn't an antimatter mob");
			helper.assertTrue(level.getServer().getLootData().getLootTable(mob.getLootTable()) != LootTable.EMPTY, type + " has no loot table");
			boolean spawns = false;
			for(MobCategory category : MobCategory.values())
				spawns |= swamp.getMobs(category).unwrap().stream().anyMatch(m -> m.type == type);
			helper.assertTrue(spawns, type + " doesn't spawn in the Coralium Infested Swamp");
			mob.discard();
		}
		helper.succeed();
	}

	/** An Anti-Pig touching a pig blows up (mobGriefing off: alone in its batch) */
	@GameTest(template = "empty", batch = "antimatter", timeoutTicks = 60)
	public static void antimatterAnnihilates(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
		boolean was = griefing.get();
		griefing.set(false, level.getServer());
		Mob anti = mob(helper, ACEntityTypes.ANTI_PIG.get(), new BlockPos(4, 1, 4));
		Pig pig = mob(helper, EntityType.PIG, new BlockPos(4, 1, 4));
		helper.succeedWhen(() -> {
			helper.assertTrue(anti.isRemoved(), "the Anti-Pig is still here");
			helper.assertTrue(pig.isRemoved() || pig.getHealth() < pig.getMaxHealth(), "the pig wasn't hurt by the explosion");
			helper.assertBlockPresent(Blocks.STONE, new BlockPos(4, 0, 4));
			pig.discard();
			griefing.set(was, level.getServer());
		});
	}

	/** An ignited Anti-Creeper explodes after its 30 tick fuse */
	@GameTest(template = "empty", batch = "antimatter", timeoutTicks = 80)
	public static void antiCreeperExplodes(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
		boolean was = griefing.get();
		griefing.set(false, level.getServer());
		EntityAntiCreeper creeper = mob(helper, ACEntityTypes.ANTI_CREEPER.get(), new BlockPos(4, 1, 4));
		creeper.ignite();
		helper.runAtTickTime(10, () -> helper.assertFalse(creeper.isRemoved(), "exploded too early"));
		helper.succeedWhen(() -> {
			helper.assertTrue(creeper.isRemoved(), "still fusing");
			griefing.set(was, level.getServer());
		});
	}

	/** Milking an Anti-Cow gives Liquid Antimatter */
	@GameTest(template = "empty")
	public static void antiCowGivesAntimatter(GameTestHelper helper) {
		floor(helper);
		Mob cow = mob(helper, ACEntityTypes.ANTI_COW.get(), new BlockPos(4, 1, 4));
		Player player = helper.makeMockPlayer();
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
		cow.interact(player, InteractionHand.MAIN_HAND);
		helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).is(ACItems.liquid_antimatter_bucket.get()), "got " + player.getItemInHand(InteractionHand.MAIN_HAND));
		cow.discard();
		helper.succeed();
	}

	/** On Hard, an Anti-Zombie killed by an Abyssal Anti-Zombie joins it; an Anti-Ghoul killed by Coralium becomes an Omothol Ghoul */
	@GameTest(template = "empty")
	public static void antiConversions(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		Difficulty old = level.getDifficulty();
		try {
			level.getServer().setDifficulty(Difficulty.HARD, true);
			AntiUndead.AntiAbyssalZombie killer = mob(helper, ACEntityTypes.ABYSSAL_ANTI_ZOMBIE.get(), new BlockPos(1, 1, 1));
			AntiUndead.AntiZombie victim = mob(helper, ACEntityTypes.ANTI_ZOMBIE.get(), new BlockPos(2, 1, 1));
			killer.killedEntity(level, victim);
			helper.assertTrue(victim.isRemoved(), "the Anti-Zombie wasn't converted");
			helper.assertTrue(level.getEntitiesOfClass(AntiUndead.AntiAbyssalZombie.class, area(helper)).size() == 2, "no second Abyssal Anti-Zombie");
		} finally {
			level.getServer().setDifficulty(old, true);
		}
		AntiHumanoids.AntiGhoul ghoul = mob(helper, ACEntityTypes.ANTI_GHOUL.get(), new BlockPos(5, 1, 5));
		ghoul.hurt(AbyssalCraftAPI.damage(level, AbyssalCraftAPI.coralium), 1000);
		helper.assertTrue(level.getEntitiesOfClass(EntityOmotholGhoul.class, area(helper)).size() == 1, "no Omothol Ghoul");
		level.getEntitiesOfClass(Mob.class, area(helper), e -> e instanceof IAntiEntity || e instanceof EntityOmotholGhoul).forEach(Entity::discard);
		helper.succeed();
	}
}
