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
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.common.blocks.BlockSingleMobSpawner;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySingleMobSpawner;
import com.shinoow.abyssalcraft.common.entity.EntityAbyssalZombie;
import com.shinoow.abyssalcraft.common.entity.EntityDepthsGhoul;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the AbyssalCraft mobs
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class EntityTests {

	/** In the Abyssal Wasteland, Abyssal Zombies and Depths Ghouls give what they hit the Coralium Plague */
	@GameTest(template = "empty")
	public static void coraliumMobsInfect(GameTestHelper helper) {
		ServerLevel aw = helper.getLevel().getServer().getLevel(ACDimensions.ABYSSAL_WASTELAND);
		aw.getChunk(0, 0);
		for(EntityType<? extends net.minecraft.world.entity.Mob> type : new EntityType[] {ACEntityTypes.ABYSSAL_ZOMBIE.get(), ACEntityTypes.DEPTHS_GHOUL.get()}) {
			var mob = type.create(aw);
			Pig pig = EntityType.PIG.create(aw);
			mob.moveTo(8, 120, 8);
			pig.moveTo(9, 120, 8);
			aw.addFreshEntity(mob);
			aw.addFreshEntity(pig);
			mob.doHurtTarget(pig);
			boolean infected = pig.hasEffect(AbyssalCraftAPI.coralium_plague.get());
			mob.discard();
			pig.discard();
			helper.assertTrue(infected, type.getDescriptionId() + " didn't infect");
		}
		helper.succeed();
	}

	/** On Hard, villagers killed by an Abyssal Zombie turn into one */
	@GameTest(template = "empty")
	public static void abyssalZombieConvertsVillagers(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Difficulty old = level.getDifficulty();
		level.getServer().setDifficulty(Difficulty.HARD, true);
		try {
			EntityAbyssalZombie zombie = helper.spawn(ACEntityTypes.ABYSSAL_ZOMBIE.get(), new BlockPos(1, 1, 1));
			Villager villager = helper.spawn(EntityType.VILLAGER, new BlockPos(4, 1, 4));
			zombie.killedEntity(level, villager);
			helper.assertTrue(villager.isRemoved(), "villager wasn't converted");
			helper.assertTrue(level.getEntities(ACEntityTypes.ABYSSAL_ZOMBIE.get(), new net.minecraft.world.phys.AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16), e -> true).size() == 2, "no new Abyssal Zombie");
		} finally {
			level.getServer().setDifficulty(old, true);
		}
		helper.succeed();
	}

	/** Pete, Mr. Wilson and Dr. Orange have their own name and loot table; babies are half as big */
	@GameTest(template = "empty")
	public static void ghoulVariants(GameTestHelper helper) {
		String[] tables = {"depths_ghoul", "depths_ghoul_pete", "depths_ghoul_wilson", "depths_ghoul_orange"};
		for(int type = 0; type < 4; type++) {
			EntityDepthsGhoul ghoul = helper.spawn(ACEntityTypes.DEPTHS_GHOUL.get(), new BlockPos(1 + type, 1, 1));
			ghoul.setGhoulType(type);
			ResourceLocation id = ghoul.getLootTable();
			helper.assertTrue(id.equals(AbyssalCraft.loc("entities/" + tables[type])), "loot table " + id);
			helper.assertTrue(helper.getLevel().getServer().getLootData().getLootTable(id) != LootTable.EMPTY, id + " doesn't exist");
		}
		EntityDepthsGhoul pete = helper.spawn(ACEntityTypes.DEPTHS_GHOUL.get(), new BlockPos(5, 1, 5));
		pete.setGhoulType(1);
		helper.assertTrue(pete.getName().getString().equals("Pete"), "Pete is called " + pete.getName().getString());
		EntityAbyssalZombie baby = helper.spawn(ACEntityTypes.ABYSSAL_ZOMBIE.get(), new BlockPos(6, 1, 1));
		baby.setBaby(true);
		helper.assertTrue(Math.abs(baby.getBbHeight() - 0.9F) < 0.01F, "baby height " + baby.getBbHeight());
		helper.succeed();
	}

	/** They spawn in the Abyssal Wasteland (1.12.2 weight 60) and, with the default config, in overworld oceans */
	@GameTest(template = "empty")
	public static void coraliumMobSpawns(GameTestHelper helper) {
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		for(var key : new net.minecraft.resources.ResourceKey[] {ACBiomes.abyssal_wastelands, Biomes.OCEAN}) {
			Biome biome = biomes.getOrThrow(key);
			var mobs = biome.getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
			helper.assertTrue(mobs.stream().anyMatch(m -> m.type == ACEntityTypes.DEPTHS_GHOUL.get()), "no Depths Ghouls in " + key.location());
			helper.assertTrue(mobs.stream().anyMatch(m -> m.type == ACEntityTypes.ABYSSAL_ZOMBIE.get()), "no Abyssal Zombies in " + key.location());
		}
		helper.succeed();
	}

	/**
	 * The single mob spawners wait for a player (the GameTest server has none) and then turn into their mob, which they
	 * can't do before that mob is registered
	 */
	@GameTest(template = "empty")
	public static void singleMobSpawners(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		int x = 1;
		for(var block : BlockHandler.MOB_SPAWNERS) {
			BlockPos rel = new BlockPos(x, 1, 1), pos = helper.absolutePos(rel);
			x += 2;
			helper.setBlock(rel, block.get());
			BlockSingleMobSpawner spawner = (BlockSingleMobSpawner) block.get();
			helper.assertTrue(level.getBlockEntity(pos) instanceof TileEntitySingleMobSpawner, "no block entity for " + block.getId());
			helper.assertFalse(TileEntitySingleMobSpawner.isActivated(level, pos, spawner), block.getId() + " activated without a player");
			boolean registered = ForgeRegistries.ENTITY_TYPES.containsKey(AbyssalCraft.loc(spawner.getEntity()));
			helper.assertTrue(TileEntitySingleMobSpawner.spawn(level, pos, spawner) == registered, block.getId() + " spawn with mob registered: " + registered);
			if(registered) {
				helper.assertBlockNotPresent(block.get(), rel);
				level.getEntities((net.minecraft.world.entity.Entity) null, new net.minecraft.world.phys.AABB(pos).inflate(2),
						e -> e.getType() == ForgeRegistries.ENTITY_TYPES.getValue(AbyssalCraft.loc(spawner.getEntity()))).forEach(e -> e.discard());
			} else
				helper.assertBlockPresent(block.get(), rel);
		}
		helper.succeed();
	}
}
