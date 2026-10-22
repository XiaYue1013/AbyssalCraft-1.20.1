/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the 14 biomes as data (1.12.2: WorldHandler + common/world/biome). Colors are the ACClientVars
 *     defaults. AbyssalCraft mobs are added to the spawn lists as they are ported.
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import static com.shinoow.abyssalcraft.data.worldgen.ACFeatureData.placed;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ACBiomeData {

	/** 1.12.2 default fog (0.7529412, 0.84705883, 1.0); the dimensions' own fog colors are in ACDimensionEffects */
	private static final int FOG = 12638463;
	private static final int DEFAULT_WATER = 4159204, DEFAULT_WATER_FOG = 329011;

	public static void bootstrap(BootstapContext<Biome> context) {
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

		// Abyssal Wasteland
		BiomeGenerationSettings.Builder aw = new BiomeGenerationSettings.Builder(features, carvers);
		aw.addCarver(GenerationStep.Carving.AIR, ACFeatureData.CAVE).addCarver(GenerationStep.Carving.AIR, ACFeatureData.CANYON);
		aw.addFeature(GenerationStep.Decoration.RAW_GENERATION, placed(ACFeatureData.DIMENSION_FLOOR));
		aw.addFeature(GenerationStep.Decoration.LAKES, placed(ACFeatureData.CORALIUM_LAKE));
		aw.addFeature(GenerationStep.Decoration.LAKES, placed(ACFeatureData.ABYSSAL_STONE_LAKE));
		for(String ore : new String[] {ACFeatureData.ORE_LIQUIFIED_CORALIUM, ACFeatureData.ORE_ABYSSAL_CORALIUM, ACFeatureData.ORE_ABYSSAL_NITRE,
				ACFeatureData.ORE_ABYSSAL_IRON, ACFeatureData.ORE_ABYSSAL_COPPER, ACFeatureData.ORE_ABYSSAL_TIN, ACFeatureData.ORE_ABYSSAL_GOLD,
				ACFeatureData.ORE_ABYSSAL_DIAMOND, ACFeatureData.ORE_PEARLESCENT_CORALIUM})
			aw.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ore));
		aw.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, placed(ACFeatureData.CHAINS));
		aw.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, placed(ACFeatureData.ABYSSAL_RUIN));
		aw.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed(ACFeatureData.LUMINOUS_THISTLE));
		aw.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed(ACFeatureData.WASTELANDS_THORN));
		MobSpawnSettings.Builder awSpawns = new MobSpawnSettings.Builder();
		awSpawns.addSpawn(MobCategory.WATER_CREATURE, monster(ACEntityTypes.CORALIUM_SQUID.get(), 10, 4, 4));
		awSpawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 50, 1, 5));
		awSpawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 50, 1, 5));
		awSpawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.DEPTHS_GHOUL.get(), 60, 1, 5));
		awSpawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.ABYSSAL_ZOMBIE.get(), 60, 1, 5));
		awSpawns.addSpawn(MobCategory.MONSTER, shoggoth());
		awSpawns.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SKELETON_GOLIATH.get(), 15, 1, 1));
		context.register(ACBiomes.abyssal_wastelands, dimensionBiome(0x24FF83, 0x447329, awSpawns, aw));

		// Dreadlands
		context.register(ACBiomes.dreadlands, dimensionBiome(DEFAULT_WATER, 0x910000, dreadlandsSpawns()
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(), 100, 1, 5)), dreadlands(features, carvers, true, false)));
		context.register(ACBiomes.purified_dreadlands, dimensionBiome(DEFAULT_WATER, 0x30217A, dreadlandsSpawns()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.DREADGUARD.get(), 1, 1, 1))
				.addSpawn(MobCategory.CREATURE, monster(ACEntityTypes.ABYSSALNITE_GOLEM.get(), 100, 3, 8)), dreadlands(features, carvers, true, true)));
		BiomeGenerationSettings.Builder forest = dreadlands(features, carvers, false, false);
		forest.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed(ACFeatureData.DREADLANDS_FOREST_TREES));
		context.register(ACBiomes.dreadlands_forest, dimensionBiome(DEFAULT_WATER, 0x910000, dreadlandsSpawns()
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREAD_SPAWN.get(), 50, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_PIG.get(), 40, 1, 3))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_COW.get(), 40, 1, 3))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_CHICKEN.get(), 40, 1, 3))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_SHEEP.get(), 40, 1, 3)), forest));
		context.register(ACBiomes.dreadlands_mountains, dimensionBiome(DEFAULT_WATER, 0x910000, dreadlandsSpawns()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.DREADGUARD.get(), 20, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.FIST_OF_CHAGAROTH.get(), 25, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SPAWN_OF_CHAGAROTH.get(), 30, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREADLING.get(), 40, 1, 3)), dreadlands(features, carvers, false, false)));

		// Omothol and the Dark Realm
		context.register(ACBiomes.omothol, dimensionBiome(14745518, 0x17375c, new MobSpawnSettings.Builder().addSpawn(MobCategory.MONSTER, shoggoth())
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.REMNANT.get(), 3, 2, 4))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.MINION_OF_THE_GATEKEEPER.get(), 3, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.OMOTHOL_GHOUL.get(), 50, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_CREATURE.get(), 18, 1, 4))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_MONSTER.get(), 12, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_BEAST.get(), 3, 1, 1)), new BiomeGenerationSettings.Builder(features, carvers)));
		BiomeGenerationSettings.Builder darkRealm = new BiomeGenerationSettings.Builder(features, carvers);
		darkRealm.addCarver(GenerationStep.Carving.AIR, ACFeatureData.CAVE).addCarver(GenerationStep.Carving.AIR, ACFeatureData.CANYON);
		context.register(ACBiomes.dark_realm, dimensionBiome(14745518, 0x17375c, shadows(new MobSpawnSettings.Builder().addSpawn(MobCategory.MONSTER, shoggoth()), 60, 1, 5, 40, 1, 3, 10), darkRealm));

		// Overworld (inserted into the overworld through TerraBlender; the rituals also convert land into them)
		context.register(ACBiomes.darklands, darklands(features, carvers, 0.5F, 0.5F, ACFeatureData.DARKLANDS_TREES, ACFeatureData.DARKSTONE));
		context.register(ACBiomes.darklands_forest, darklands(features, carvers, 0.5F, 0.5F, ACFeatureData.DARKLANDS_FOREST_TREES, ACFeatureData.DARKSTONE));
		context.register(ACBiomes.darklands_plains, darklands(features, carvers, 0.5F, 0.5F, ACFeatureData.DARKLANDS_PLAINS_TREES, ACFeatureData.DARKSTONE_LARGE));
		context.register(ACBiomes.darklands_hills, darklands(features, carvers, 0.2F, 0.3F, ACFeatureData.DARKLANDS_PLAINS_TREES, ACFeatureData.DARKSTONE_LARGE));
		context.register(ACBiomes.darklands_mountains, darklands(features, carvers, 0.2F, 0.3F, null, null));
		context.register(ACBiomes.coralium_infested_swamp, coraliumSwamp(features, carvers));
		// Created by the Ritual of Purging: dead, calcified land where nothing spawns
		context.register(ACBiomes.purged, new Biome.BiomeBuilder().hasPrecipitation(false).temperature(0.5F).downfall(0.5F)
				.specialEffects(new BiomeSpecialEffects.Builder().waterColor(0xD7D8D9).waterFogColor(DEFAULT_WATER_FOG).fogColor(FOG).skyColor(0xD7D8D9)
						.grassColorOverride(0xD7D8D9).foliageColorOverride(0xD7D8D9).build())
				.mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(new BiomeGenerationSettings.Builder(features, carvers).build()).build());
	}

	private static BiomeGenerationSettings.Builder dreadlands(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers, boolean stalagmites, boolean purified) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(features, carvers);
		builder.addCarver(GenerationStep.Carving.AIR, ACFeatureData.DREADLANDS_CAVE).addCarver(GenerationStep.Carving.AIR, ACFeatureData.DREADLANDS_CANYON)
		.addCarver(GenerationStep.Carving.AIR, ACFeatureData.DREADLANDS_NETHER_CAVE);
		builder.addFeature(GenerationStep.Decoration.RAW_GENERATION, placed(ACFeatureData.DIMENSION_FLOOR));
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ACFeatureData.ORE_DREADED_ABYSSALNITE));
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ACFeatureData.ABYSSALNITE_STONE_DREADLANDS));
		if(purified)
			for(String ore : new String[] {ACFeatureData.ORE_DREADLANDS_ABYSSALNITE_LARGE, ACFeatureData.ABYSSALNITE_STONE_LARGE, ACFeatureData.ABYSSALNITE_STONE_MEDIUM,
					ACFeatureData.ABYSSALNITE_STONE_SMALL, ACFeatureData.ORE_DREADLANDS_ABYSSALNITE_SMALL})
				builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ore));
		if(stalagmites)
			builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, placed(ACFeatureData.DREADLANDS_STALAGMITE));
		return builder;
	}

	/** Lesser Shoggoths spawn in every dimension biome (1.12.2 weight 3, alone) */
	private static MobSpawnSettings.SpawnerData shoggoth() {
		return new MobSpawnSettings.SpawnerData(ACEntityTypes.LESSER_SHOGGOTH.get(), 3, 1, 1);
	}

	/** BiomeGenDreadlandsBase spawns (the biomes add their own to these) */
	private static MobSpawnSettings.Builder dreadlandsSpawns() {
		return new MobSpawnSettings.Builder().addSpawn(MobCategory.MONSTER, shoggoth())
				.addSpawn(MobCategory.CREATURE, monster(ACEntityTypes.ABYSSALNITE_GOLEM.get(), 60, 1, 3))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(), 60, 1, 3))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREAD_SPAWN.get(), 30, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREADLING.get(), 40, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.FIST_OF_CHAGAROTH.get(), 2, 1, 1))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_PIG.get(), 5, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_COW.get(), 5, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_CHICKEN.get(), 5, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DEMON_SHEEP.get(), 5, 1, 2))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.GREATER_DREAD_SPAWN.get(), 5, 1, 1))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.DREADGUARD.get(), 8, 1, 1))
				// 1.12.2: groups of 0 - 1, which spawn codecs don't allow
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.LESSER_DREADBEAST.get(), 1, 1, 1));
	}

	/** Shadow Creatures, Monsters and Beasts: weights and group sizes (the Beast always alone) */
	private static MobSpawnSettings.Builder shadows(MobSpawnSettings.Builder spawns, int creature, int creatureMin, int creatureMax, int monster, int monsterMin,
			int monsterMax, int beast) {
		return spawns.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_CREATURE.get(), creature, creatureMin, creatureMax))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_MONSTER.get(), monster, monsterMin, monsterMax))
				.addSpawn(MobCategory.MONSTER, monster(ACEntityTypes.SHADOW_BEAST.get(), beast, 1, 1));
	}

	private static MobSpawnSettings.SpawnerData monster(net.minecraft.world.entity.EntityType<?> type, int weight, int min, int max) {
		return new MobSpawnSettings.SpawnerData(type, weight, min, max);
	}

	/** Rain disabled, 1.12.2 default temperature and rainfall, black sky (the dimensions render their own sky) */
	private static Biome dimensionBiome(int water, int grass, MobSpawnSettings.Builder spawns, BiomeGenerationSettings.Builder generation) {
		return new Biome.BiomeBuilder().hasPrecipitation(false).temperature(0.5F).downfall(0.5F)
				.specialEffects(new BiomeSpecialEffects.Builder().waterColor(water).waterFogColor(DEFAULT_WATER_FOG).fogColor(FOG).skyColor(0)
						.grassColorOverride(grass).foliageColorOverride(grass).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
				.mobSpawnSettings(spawns.build()).generationSettings(generation.build()).build();
	}

	/** BiomeGenDarklands* : vanilla overworld decoration, Darklands Oak trees, abyssalnite ore and darkstone patches, black sky */
	private static Biome darklands(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers, float temperature, float downfall,
			String trees, String darkstone) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(features, carvers);
		globalOverworldGeneration(builder);
		BiomeDefaultFeatures.addDefaultOres(builder);
		BiomeDefaultFeatures.addDefaultSoftDisks(builder);
		BiomeDefaultFeatures.addDefaultFlowers(builder);
		BiomeDefaultFeatures.addDefaultGrass(builder);
		BiomeDefaultFeatures.addDefaultMushrooms(builder);
		BiomeDefaultFeatures.addDefaultExtraVegetation(builder);
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ACFeatureData.ORE_ABYSSALNITE));
		if(darkstone != null)
			builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(darkstone));
		if(trees != null)
			builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed(trees));
		for(String structure : ACFeatureData.DARKLANDS_STRUCTURES)
			builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, placed(structure));
		MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.farmAnimals(spawns);
		BiomeDefaultFeatures.commonSpawns(spawns);
		// BiomeGenDarklands* (the Darklands Mountains have fewer Coralium mobs)
		boolean mountains = trees == null;
		spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.DEPTHS_GHOUL.get(), mountains ? 45 : 60, 1, mountains ? 2 : 5));
		spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.ABYSSAL_ZOMBIE.get(), mountains ? 45 : 60, 1, mountains ? 2 : 3));
		shadows(spawns, mountains ? 100 : 70, 3, 3, mountains ? 80 : 50, 2, 2, mountains ? 40 : 20);
		return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(temperature).downfall(downfall)
				.specialEffects(new BiomeSpecialEffects.Builder().waterColor(14745518).waterFogColor(DEFAULT_WATER_FOG).fogColor(FOG).skyColor(0)
						.grassColorOverride(0x17375c).foliageColorOverride(0x17375c).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
				.mobSpawnSettings(spawns.build()).generationSettings(builder.build()).build();
	}

	/** BiomeGenCorSwamp: a swamp with Coralium Ore and Liquid Antimatter lakes */
	private static Biome coraliumSwamp(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(features, carvers);
		BiomeDefaultFeatures.addFossilDecoration(builder);
		globalOverworldGeneration(builder);
		BiomeDefaultFeatures.addDefaultOres(builder);
		BiomeDefaultFeatures.addSwampClayDisk(builder);
		BiomeDefaultFeatures.addSwampVegetation(builder);
		BiomeDefaultFeatures.addDefaultMushrooms(builder);
		BiomeDefaultFeatures.addSwampExtraVegetation(builder);
		builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
		builder.addFeature(GenerationStep.Decoration.LAKES, placed(ACFeatureData.ANTIMATTER_LAKE));
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ACFeatureData.ORE_CORALIUM_SWAMP));
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placed(ACFeatureData.CORALIUM_ORE_SWAP));
		MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.farmAnimals(spawns);
		BiomeDefaultFeatures.commonSpawns(spawns);
		spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
		// BiomeGenCorSwamp: the antimatter mobs
		spawns.addSpawn(MobCategory.CREATURE, monster(ACEntityTypes.ANTI_PIG.get(), 5, 1, 2));
		spawns.addSpawn(MobCategory.CREATURE, monster(ACEntityTypes.ANTI_CHICKEN.get(), 5, 1, 2));
		spawns.addSpawn(MobCategory.CREATURE, monster(ACEntityTypes.ANTI_COW.get(), 5, 1, 2));
		spawns.addSpawn(MobCategory.AMBIENT, monster(ACEntityTypes.ANTI_BAT.get(), 10, 1, 2));
		for(var anti : java.util.List.of(ACEntityTypes.ABYSSAL_ANTI_ZOMBIE, ACEntityTypes.ANTI_CREEPER, ACEntityTypes.ANTI_GHOUL, ACEntityTypes.ANTI_PLAYER,
				ACEntityTypes.ANTI_SKELETON, ACEntityTypes.ANTI_SPIDER, ACEntityTypes.ANTI_ZOMBIE))
			spawns.addSpawn(MobCategory.MONSTER, monster(anti.get(), 5, 1, 2));
		spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.DEPTHS_GHOUL.get(), 60, 1, 5));
		spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ACEntityTypes.ABYSSAL_ZOMBIE.get(), 60, 1, 5));
		return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.8F).downfall(0.9F)
				.specialEffects(new BiomeSpecialEffects.Builder().waterColor(0x24FF83).waterFogColor(2302743).fogColor(FOG).skyColor(7907327)
						.grassColorOverride(0x59c6b4).foliageColorOverride(0x59c6b4).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
				.mobSpawnSettings(spawns.build()).generationSettings(builder.build()).build();
	}

	/** OverworldBiomes#globalOverworldGeneration (private in vanilla) */
	private static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
		BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
		BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
		BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
		BiomeDefaultFeatures.addDefaultSprings(builder);
		BiomeDefaultFeatures.addSurfaceFreezing(builder);
	}
}
