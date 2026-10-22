/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - dimension types and levels (1.12.2: WorldProviderAbyss, WorldProviderDreadlands,
 *     WorldProviderOmothol, WorldProviderDarkRealm)
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import java.util.List;
import java.util.OptionalLong;

import com.mojang.datafixers.util.Pair;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.init.ACDimensions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class ACDimensionData {

	/**
	 * Shared 1.12.2 provider behaviour: sky light, no clocks/compasses (not a surface world), no respawning (beds
	 * explode), rain disabled through the biomes, y 0 - 256. Ambient light is the 1.12.2 light brightness table offset,
	 * fixed time is the 1.12.2 celestial angle (0.5 in the Abyssal Wasteland, 0 elsewhere).
	 */
	public static void bootstrapTypes(BootstapContext<DimensionType> context) {
		context.register(ACDimensions.ABYSSAL_WASTELAND_TYPE, type(18000L, 0.25F, ACDimensions.ABYSSAL_WASTELAND_TYPE));
		context.register(ACDimensions.DREADLANDS_TYPE, type(6000L, 0.35F, ACDimensions.DREADLANDS_TYPE));
		context.register(ACDimensions.OMOTHOL_TYPE, type(6000L, 0.25F, ACDimensions.OMOTHOL_TYPE));
		context.register(ACDimensions.DARK_REALM_TYPE, type(6000L, 0.1F, ACDimensions.DARK_REALM_TYPE));
	}

	private static DimensionType type(long fixedTime, float ambientLight, ResourceKey<DimensionType> effects) {
		return new DimensionType(OptionalLong.of(fixedTime), true, false, false, false, 1.0D, false, false, 0, 256, 256,
				BlockTags.INFINIBURN_OVERWORLD, effects.location(), ambientLight, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0));
	}

	public static void bootstrapLevels(BootstapContext<LevelStem> context) {
		HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<NoiseGeneratorSettings> noise = context.lookup(Registries.NOISE_SETTINGS);

		context.register(stem(ACDimensions.ABYSSAL_WASTELAND), new LevelStem(types.getOrThrow(ACDimensions.ABYSSAL_WASTELAND_TYPE),
				new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ACBiomes.abyssal_wastelands)), noise.getOrThrow(ACNoiseSettings.ABYSSAL_WASTELAND))));

		// The two Dreadlands biome noises are routed as temperature (A) and continentalness (B), see ACNoiseSettings
		Climate.Parameter low = Climate.Parameter.span(-1.0F, 0.0F), high = Climate.Parameter.span(0.0F, 1.0F), any = Climate.Parameter.span(-1.0F, 1.0F);
		Climate.ParameterList<Holder<Biome>> dreadlands = new Climate.ParameterList<>(List.of(
				Pair.of(Climate.parameters(low, any, low, any, any, any, 0.0F), biomes.getOrThrow(ACBiomes.dreadlands)),
				Pair.of(Climate.parameters(high, any, low, any, any, any, 0.0F), biomes.getOrThrow(ACBiomes.purified_dreadlands)),
				Pair.of(Climate.parameters(low, any, high, any, any, any, 0.0F), biomes.getOrThrow(ACBiomes.dreadlands_forest)),
				Pair.of(Climate.parameters(high, any, high, any, any, any, 0.0F), biomes.getOrThrow(ACBiomes.dreadlands_mountains))));
		context.register(stem(ACDimensions.DREADLANDS), new LevelStem(types.getOrThrow(ACDimensions.DREADLANDS_TYPE),
				new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromList(dreadlands), noise.getOrThrow(ACNoiseSettings.DREADLANDS))));

		context.register(stem(ACDimensions.OMOTHOL), new LevelStem(types.getOrThrow(ACDimensions.OMOTHOL_TYPE),
				new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ACBiomes.omothol)), noise.getOrThrow(ACNoiseSettings.OMOTHOL))));
		context.register(stem(ACDimensions.DARK_REALM), new LevelStem(types.getOrThrow(ACDimensions.DARK_REALM_TYPE),
				new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ACBiomes.dark_realm)), noise.getOrThrow(ACNoiseSettings.DARK_REALM))));
	}

	private static ResourceKey<LevelStem> stem(ResourceKey<Level> level) {
		return ResourceKey.create(Registries.LEVEL_STEM, level.location());
	}
}
