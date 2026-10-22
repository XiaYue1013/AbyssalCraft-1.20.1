/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the overworld ores of AbyssalCraftWorldGenerator, added to every matching biome (also other mods'
 *     biomes) through Forge biome modifiers
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import static com.shinoow.abyssalcraft.data.worldgen.ACFeatureData.placed;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.world.ConfigSpawnsBiomeModifier;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ACBiomeModifiers {

	public static void bootstrap(BootstapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, AbyssalCraft.loc("spectral_dragon_spawns")),
				new ConfigSpawnsBiomeModifier(HolderSet.direct(biomes.getOrThrow(com.shinoow.abyssalcraft.api.biome.ACBiomes.abyssal_wastelands)), "spectralDragons",
						new MobSpawnSettings.SpawnerData(ACEntityTypes.SPECTRAL_DRAGON.get(), 1, 1, 1), ""));
		ores(context, biomes, features, "coralium_ore_swamps", Tags.Biomes.IS_SWAMP, ACFeatureData.ORE_CORALIUM_SWAMPS);
		ores(context, biomes, features, "coralium_ore_oceans", ACTags.Biomes.CORALIUM_ORE_OCEANS, ACFeatureData.ORE_CORALIUM_OCEANS);
		ores(context, biomes, features, "coralium_ore_deep_oceans", BiomeTags.IS_DEEP_OCEAN, ACFeatureData.ORE_CORALIUM_DEEP_OCEANS);
		ores(context, biomes, features, "nitre_ore", BiomeTags.IS_OVERWORLD, ACFeatureData.ORE_NITRE);
		spawns(context, biomes, "depths_ghoul_spawns", ACTags.Biomes.CORALIUM_MOB_SPAWNS, "depthsGhoulBiomeTagSpawning",
				new MobSpawnSettings.SpawnerData(ACEntityTypes.DEPTHS_GHOUL.get(), 10, 1, 3));
		spawns(context, biomes, "abyssal_zombie_spawns", ACTags.Biomes.CORALIUM_MOB_SPAWNS, "abyssalZombieBiomeTagSpawning",
				new MobSpawnSettings.SpawnerData(ACEntityTypes.ABYSSAL_ZOMBIE.get(), 10, 1, 3));
		// weights from the config (0 disables)
		weighted(context, biomes, "shub_offspring_spawns", ACTags.Biomes.SHUB_OFFSPRING_SPAWNS, "darkOffspringSpawnWeight",
				new MobSpawnSettings.SpawnerData(ACEntityTypes.SHUB_OFFSPRING.get(), 1, 1, 3));
		weighted(context, biomes, "shub_offspring_dense_spawns", ACTags.Biomes.SHUB_OFFSPRING_DENSE_SPAWNS, "darkOffspringSpawnWeightDouble",
				new MobSpawnSettings.SpawnerData(ACEntityTypes.SHUB_OFFSPRING.get(), 2, 1, 3));
		for(var evil : java.util.List.of(ACEntityTypes.EVIL_PIG, ACEntityTypes.EVIL_COW, ACEntityTypes.EVIL_CHICKEN, ACEntityTypes.EVIL_SHEEP))
			weighted(context, biomes, evil.getId().getPath() + "_spawns", ACTags.Biomes.EVIL_ANIMAL_SPAWNS, "evilAnimalSpawnWeight",
					new MobSpawnSettings.SpawnerData(evil.get(), 15, 1, 3));
		for(var demon : java.util.List.of(ACEntityTypes.DEMON_PIG, ACEntityTypes.DEMON_COW, ACEntityTypes.DEMON_CHICKEN, ACEntityTypes.DEMON_SHEEP))
			weighted(context, biomes, demon.getId().getPath() + "_nether_spawns", BiomeTags.IS_NETHER, "demonAnimalSpawnWeight",
					new MobSpawnSettings.SpawnerData(demon.get(), 15, 1, 3));
	}

	private static void weighted(BootstapContext<BiomeModifier> context, HolderGetter<Biome> biomes, String name, TagKey<Biome> tag, String weightOption,
			MobSpawnSettings.SpawnerData spawn) {
		context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, AbyssalCraft.loc(name)),
				new ConfigSpawnsBiomeModifier(biomes.getOrThrow(tag), "always", spawn, weightOption));
	}

	private static void spawns(BootstapContext<BiomeModifier> context, HolderGetter<Biome> biomes, String name, TagKey<Biome> tag, String option,
			MobSpawnSettings.SpawnerData spawn) {
		context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, AbyssalCraft.loc(name)),
				new ConfigSpawnsBiomeModifier(biomes.getOrThrow(tag), option, spawn, ""));
	}

	private static void ores(BootstapContext<BiomeModifier> context, HolderGetter<Biome> biomes, HolderGetter<PlacedFeature> features, String name,
			TagKey<Biome> tag, String feature) {
		context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, AbyssalCraft.loc(name)), new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomes.getOrThrow(tag), HolderSet.direct(features.getOrThrow(placed(feature))), GenerationStep.Decoration.UNDERGROUND_ORES));
	}
}
