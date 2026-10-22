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
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityShoggothBiomass;
import com.shinoow.abyssalcraft.common.entity.EntityAcidProjectile;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.common.world.gen.ShoggothMonolith;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Lesser Shoggoth, its acid, ooze, monoliths and biomass
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class ShoggothTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	private static EntityLesserShoggoth shoggoth(GameTestHelper helper, BlockPos pos) {
		EntityLesserShoggoth shoggoth = helper.spawn(ACEntityTypes.LESSER_SHOGGOTH.get(), pos);
		shoggoth.setNoAi(true);
		return shoggoth;
	}

	/** The type (texture, loot, attack effect) comes from the dimension; Dark Realm ones are shadow mobs; babies are half size */
	@GameTest(template = "empty")
	public static void shoggothTypes(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		@SuppressWarnings("unchecked")
		ResourceKey<Level>[] dims = new ResourceKey[] {Level.OVERWORLD, ACDimensions.ABYSSAL_WASTELAND, ACDimensions.DREADLANDS, ACDimensions.OMOTHOL, ACDimensions.DARK_REALM};
		String[] tables = {"lesser_shoggoth", "lesser_abyssal_shoggoth", "lesser_dreaded_shoggoth", "lesser_omothol_shoggoth", "lesser_shadow_shoggoth"};
		for(int i = 0; i < dims.length; i++) {
			ServerLevel level = server.getLevel(dims[i]);
			EntityLesserShoggoth shoggoth = ACEntityTypes.LESSER_SHOGGOTH.get().create(level);
			shoggoth.moveTo(0, 100, 0);
			shoggoth.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.ZERO), MobSpawnType.NATURAL, new SpawnGroupData() {}, null);
			helper.assertTrue(shoggoth.getShoggothType() == i, "type " + shoggoth.getShoggothType() + " in " + dims[i].location());
			helper.assertTrue(shoggoth.getLootTable().equals(AbyssalCraft.loc("entities/" + tables[i])), "loot table " + shoggoth.getLootTable());
			helper.assertTrue(server.getLootData().getLootTable(shoggoth.getLootTable()) != LootTable.EMPTY, shoggoth.getLootTable() + " doesn't exist");
			helper.assertTrue(shoggoth.getMobType() == (i == 4 ? AbyssalCraftAPI.SHADOW : MobType.UNDEFINED), "mob type of type " + i);
		}
		EntityLesserShoggoth baby = shoggoth(helper, new BlockPos(4, 1, 4));
		helper.assertTrue(Math.abs(baby.getBbWidth() - 1.8F) < 0.01F && Math.abs(baby.getBbHeight() - 2.6F) < 0.01F, "adult size " + baby.getBbWidth() + "x" + baby.getBbHeight());
		baby.setBaby(true);
		helper.assertTrue(Math.abs(baby.getBbWidth() - 0.9F) < 0.01F && Math.abs(baby.getBbHeight() - 1.3F) < 0.01F, "baby size " + baby.getBbWidth() + "x" + baby.getBbHeight());
		helper.assertTrue(baby.getMaxHealth() == (ACConfig.hardcoreMode ? 100.0F : 50.0F), "baby health " + baby.getMaxHealth());
		helper.assertTrue(baby.getParts().length == 2, "no head/body hitboxes");
		helper.succeed();
	}

	/** They spawn in all the dimension biomes (1.12.2 weight 3) */
	@GameTest(template = "empty")
	public static void shoggothSpawns(GameTestHelper helper) {
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		for(ResourceKey<Biome> key : java.util.List.of(ACBiomes.abyssal_wastelands, ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest,
				ACBiomes.dreadlands_mountains, ACBiomes.omothol, ACBiomes.dark_realm))
			helper.assertTrue(biomes.getOrThrow(key).getMobSettings().getMobs(MobCategory.MONSTER).unwrap().stream()
					.anyMatch(m -> m.type == ACEntityTypes.LESSER_SHOGGOTH.get() && m.getWeight().asInt() == 3), "no Lesser Shoggoths in " + key.location());
		helper.succeed();
	}

	/** Shoggoths leave ooze where they go (a 2x2 patch for adults) */
	@GameTest(template = "empty")
	public static void shoggothSpreadsOoze(GameTestHelper helper) {
		floor(helper);
		shoggoth(helper, new BlockPos(4, 1, 4));
		helper.succeedWhen(() -> {
			for(BlockPos pos : new BlockPos[] {new BlockPos(4, 1, 4), new BlockPos(3, 1, 4), new BlockPos(4, 1, 3), new BlockPos(3, 1, 3)})
				helper.assertBlockPresent(ACBlocks.shoggoth_ooze.get(), pos);
		});
	}

	/** Eating a pig is worth 4 food; at 8 food an adult gives birth to a baby of its type */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void shoggothBreeds(GameTestHelper helper) {
		floor(helper);
		EntityLesserShoggoth shoggoth = shoggoth(helper, new BlockPos(4, 1, 4));
		shoggoth.setShoggothType(3);
		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 1));
		shoggoth.killedEntity(helper.getLevel(), pig);
		helper.assertTrue(shoggoth.getFoodLevel() == 4, "pig gave " + shoggoth.getFoodLevel() + " food");
		pig.discard();
		shoggoth.setFoodLevel(8);
		helper.succeedWhen(() -> {
			var babies = helper.getLevel().getEntitiesOfClass(EntityLesserShoggoth.class, shoggoth.getBoundingBox().inflate(4), EntityLesserShoggoth::isBaby);
			helper.assertTrue(babies.size() == 1, babies.size() + " babies");
			helper.assertTrue(babies.get(0).getShoggothType() == 3, "baby type " + babies.get(0).getShoggothType());
			helper.assertTrue(shoggoth.getFoodLevel() == 0, "food left " + shoggoth.getFoodLevel());
			// the baby has AI and hunts animals, keep it away from the other tests
			babies.forEach(EntityLesserShoggoth::discard);
		});
	}

	/** Projectiles and cactus don't hurt them, melee does */
	@GameTest(template = "empty")
	public static void shoggothDamageImmunities(GameTestHelper helper) {
		floor(helper);
		EntityLesserShoggoth shoggoth = shoggoth(helper, new BlockPos(4, 1, 4));
		Level level = helper.getLevel();
		if(!ACConfig.no_projectile_damage_immunity)
			helper.assertFalse(shoggoth.hurt(AbyssalCraftAPI.damage(level, DamageTypes.ARROW), 5), "hurt by an arrow");
		helper.assertFalse(shoggoth.hurt(AbyssalCraftAPI.damage(level, DamageTypes.CACTUS), 5), "hurt by a cactus");
		helper.assertTrue(shoggoth.hurt(AbyssalCraftAPI.damage(level, DamageTypes.GENERIC), 5), "not hurt by generic damage");
		helper.succeed();
	}

	/** Spraying acid dissolves soft blocks at the top of its hitbox (not obsidian or Monolith Stone) and hurts what's next to it */
	@GameTest(template = "empty")
	public static void shoggothSpraysAcid(GameTestHelper helper) {
		floor(helper);
		EntityLesserShoggoth shoggoth = shoggoth(helper, new BlockPos(4, 1, 4));
		// adult hitbox 1.8 x 2.6, grown by 1 sideways: top layer at y 3
		helper.setBlock(new BlockPos(4, 3, 4), Blocks.DIRT);
		helper.setBlock(new BlockPos(3, 3, 4), Blocks.OBSIDIAN);
		helper.setBlock(new BlockPos(5, 3, 4), ACBlocks.monolith_stone.get());
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(6, 1, 4));
		zombie.setNoAi(true);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		shoggoth.sprayAcid(true);
		helper.assertBlockPresent(Blocks.AIR, new BlockPos(4, 3, 4));
		helper.assertBlockPresent(Blocks.OBSIDIAN, new BlockPos(3, 3, 4));
		helper.assertBlockPresent(ACBlocks.monolith_stone.get(), new BlockPos(5, 3, 4));
		helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "zombie wasn't hurt");
		helper.assertTrue(zombie.getItemBySlot(EquipmentSlot.HEAD).getDamageValue() == 8, "helmet damage " + zombie.getItemBySlot(EquipmentSlot.HEAD).getDamageValue());
		helper.succeed();
	}

	/** Acid spit hurts what it hits and corrodes its armor */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void acidProjectileHits(GameTestHelper helper) {
		floor(helper);
		EntityLesserShoggoth shoggoth = shoggoth(helper, new BlockPos(1, 1, 1));
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(6, 1, 6));
		zombie.setNoAi(true);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		EntityAcidProjectile acid = new EntityAcidProjectile(helper.getLevel(), shoggoth);
		BlockPos start = helper.absolutePos(new BlockPos(6, 2, 3));
		acid.moveTo(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
		acid.shoot(0, 0, 1, 0.5F, 0);
		acid.setNoGravity(true);
		helper.getLevel().addFreshEntity(acid);
		helper.succeedWhen(() -> {
			helper.assertTrue(zombie.getHealth() == zombie.getMaxHealth() - 6, "zombie health " + zombie.getHealth());
			helper.assertTrue(zombie.getItemBySlot(EquipmentSlot.HEAD).getDamageValue() == 2, "helmet damage " + zombie.getItemBySlot(EquipmentSlot.HEAD).getDamageValue());
			helper.assertTrue(acid.isRemoved(), "acid still flying");
		});
	}

	/** A monolith: 3x3 Monolith Stone pillar, Shoggoth Biomass at its base, a statue on top */
	@GameTest(template = "empty")
	public static void shoggothMonolith(GameTestHelper helper) {
		floor(helper);
		helper.assertTrue(ShoggothMonolith.generate(helper.getLevel(), helper.getLevel().random, helper.absolutePos(new BlockPos(4, 1, 4))), "monolith not built");
		helper.assertBlockPresent(ACBlocks.shoggoth_biomass.get(), new BlockPos(4, 0, 4));
		for(int x = 3; x < 6; x++)
			for(int z = 3; z < 6; z++)
				for(int y = 0; y < 5; y++)
					if(x != 4 || z != 4 || y > 0)
						helper.assertBlockPresent(ACBlocks.monolith_stone.get(), new BlockPos(x, y, z));
		int top = -1;
		for(int y = 5; y < 13; y++)
			if(helper.getBlockState(new BlockPos(4, y, 4)).getBlock() instanceof BlockStatue) {
				top = y;
				break;
			}
		helper.assertTrue(top > 0, "no statue on top");
		for(int y = 5; y < top; y++)
			helper.assertBlockPresent(ACBlocks.monolith_stone.get(), new BlockPos(4, y, 4));
		helper.succeed();
	}

	/**
	 * Biomass only spawns when a player is near (the GameTest server has none) and its cooldown runs out; it stops at the
	 * spawn limit, and the fifth Shoggoth turns it into Monolith Stone
	 */
	@GameTest(template = "empty", batch = "shoggoth_biomass")
	public static void biomassSpawnsShoggoths(GameTestHelper helper) {
		floor(helper);
		helper.setBlock(new BlockPos(4, 1, 4), ACBlocks.shoggoth_biomass.get());
		BlockPos pos = helper.absolutePos(new BlockPos(4, 1, 4));
		ServerLevel level = helper.getLevel();
		TileEntityShoggothBiomass biomass = (TileEntityShoggothBiomass) level.getBlockEntity(pos);
		helper.assertTrue(biomass != null, "no block entity");
		AABB area = new AABB(pos).inflate(ACConfig.biomassShoggothDistance);
		Difficulty difficulty = level.getDifficulty();
		GameRules.BooleanValue spawning = level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING);
		boolean wasSpawning = spawning.get();
		try {
			level.getServer().setDifficulty(Difficulty.NORMAL, true);
			spawning.set(true, level.getServer());
			biomass.setCooldown(ACConfig.biomassCooldown - 1);
			TileEntityShoggothBiomass.serverTick(level, pos, level.getBlockState(pos), biomass);
			helper.assertTrue(biomass.getCooldown() < 10, "cooldown didn't run out");
			helper.assertTrue(biomass.getSpawnedShoggoths() == 0, "spawned a Shoggoth without a player near");
		} finally {
			spawning.set(wasSpawning, level.getServer());
			level.getServer().setDifficulty(difficulty, true);
		}
		// at the spawn limit nothing spawns
		java.util.List<EntityLesserShoggoth> crowd = new java.util.ArrayList<>();
		for(int i = level.getEntitiesOfClass(EntityLesserShoggoth.class, area).size(); i < ACConfig.biomassMaxSpawn; i++)
			crowd.add(shoggoth(helper, new BlockPos(1, 1, 1)));
		biomass.spawnShoggoth();
		helper.assertTrue(biomass.getSpawnedShoggoths() == 0, "spawned past the limit");
		crowd.forEach(EntityLesserShoggoth::discard);
		for(int i = 0; i < 5; i++) {
			biomass.spawnShoggoth();
			var spawned = level.getEntitiesOfClass(EntityLesserShoggoth.class, new AABB(pos).inflate(4));
			helper.assertTrue(spawned.size() == 1, spawned.size() + " Shoggoths next to the Biomass after spawn " + (i + 1));
			spawned.forEach(EntityLesserShoggoth::discard);
		}
		helper.assertTrue(biomass.getSpawnedShoggoths() == 5, biomass.getSpawnedShoggoths() + " Shoggoths spawned");
		helper.assertBlockPresent(ACBlocks.monolith_stone.get(), new BlockPos(4, 1, 4));
		helper.succeed();
	}
}
