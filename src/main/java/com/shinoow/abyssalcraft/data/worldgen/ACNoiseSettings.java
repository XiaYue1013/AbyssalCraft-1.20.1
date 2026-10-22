/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the four dimensions' terrain as noise settings (1.12.2: ChunkProviderAbyss, ChunkProviderDreadlands,
 *     ChunkProviderOmothol, ChunkProviderDarkRealm)
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * The 1.12.2 dimensions used the classic "limit noise" terrain generator. 1.20.1's {@link BlendedNoise}
 * ({@code old_blended_noise}) is that same noise blend, returning the 1.12.2 density divided by 128, so the 1.12.2
 * formulas are rebuilt here with density functions (all numbers below are 1.12.2 values / 128).
 * <ul>
 * <li>Abyssal Wasteland, Dreadlands, Dark Realm: density = noise - slope * (y - height), four times steeper below the
 * height, with height/slope from the biome's 1.12.2 base height and height variation. Below y 63 the Abyssal Wasteland
 * is filled with Liquid Coralium, the Dreadlands with abyssalnite stone and the Dark Realm with darkstone.</li>
 * <li>Omothol: the 1.12.2 (pre 1.9) End generator with a much longer top slide, so land is densest just above y 32
 * and thins out upwards.</li>
 * <li>Dark Realm: Omothol's layer shifted 30 blocks up is cut out of the terrain as air.</li>
 * </ul>
 * Exact terrain parity isn't possible (different random generators), but the shapes match.
 */
public class ACNoiseSettings {

	public static final ResourceKey<NoiseGeneratorSettings> ABYSSAL_WASTELAND = key("abyssal_wasteland");
	public static final ResourceKey<NoiseGeneratorSettings> DREADLANDS = key("dreadlands");
	public static final ResourceKey<NoiseGeneratorSettings> OMOTHOL = key("omothol");
	public static final ResourceKey<NoiseGeneratorSettings> DARK_REALM = key("dark_realm");

	/** Dreadlands biome selection (two independent noises, a quarter of the area each) */
	public static final ResourceKey<NormalNoise.NoiseParameters> DREADLANDS_BIOME_A = noise("dreadlands_biome_a");
	public static final ResourceKey<NormalNoise.NoiseParameters> DREADLANDS_BIOME_B = noise("dreadlands_biome_b");

	public static final int SEA_LEVEL = 63;

	/** 1.12.2 overworld style terrain noise (ChunkProviderAbyss: 684.412 per 4x8x4 cell, main noise /80 and /160) */
	private static final DensityFunction TERRAIN_NOISE = BlendedNoise.createUnseeded(0.25D, 0.125D, 80.0D, 160.0D, 8.0D);
	/** 1.12.2 End style noise (ChunkProviderOmothol: 684.412 * 2 per 8 blocks horizontally, 684.412 per 4 blocks vertically) */
	private static final DensityFunction FLOATING_NOISE = BlendedNoise.createUnseeded(0.25D, 0.25D, 80.0D, 160.0D, 4.0D);
	private static final DensityFunction NEGATIVE_Y = DensityFunctions.yClampedGradient(-4064, 4062, 4064.0D, -4062.0D);

	public static void bootstrapNoises(BootstapContext<NormalNoise.NoiseParameters> context) {
		context.register(DREADLANDS_BIOME_A, new NormalNoise.NoiseParameters(-7, 1.0D, 1.0D));
		context.register(DREADLANDS_BIOME_B, new NormalNoise.NoiseParameters(-7, 1.0D, 1.0D));
	}

	public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
		HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);

		DensityFunction plains = terrain(DensityFunctions.constant(height(0.1F)), DensityFunctions.constant(slope(0.2F)));
		context.register(ABYSSAL_WASTELAND, settings(NoiseSettings.create(0, 256, 1, 2), ACBlocks.abyssal_stone.get().defaultBlockState(),
				ACBlocks.liquid_coralium.get().defaultBlockState(), router(plains, plains), abyssalWastelandSurface(), SEA_LEVEL));

		// Dreadlands Mountains (base height 1.3, variation 0.9) where both biome noises are positive, the other three
		// Dreadlands biomes have the default 0.1 / 0.2
		DensityFunction biomeA = DensityFunctions.cache2d(DensityFunctions.noise(noises.getOrThrow(DREADLANDS_BIOME_A), 1.0D, 0.0D));
		DensityFunction biomeB = DensityFunctions.cache2d(DensityFunctions.noise(noises.getOrThrow(DREADLANDS_BIOME_B), 1.0D, 0.0D));
		DensityFunction mountains = DensityFunctions.min(biomeA, biomeB);
		DensityFunction dreadlands = terrain(step(mountains, height(0.1F), height(1.3F)), step(mountains, slope(0.2F), slope(0.9F)));
		NoiseRouter dreadlandsRouter = new NoiseRouter(zero(), zero(), zero(), zero(), biomeA, zero(), biomeB, zero(), zero(), zero(),
				dreadlands, DensityFunctions.interpolated(dreadlands), zero(), zero(), zero());
		context.register(DREADLANDS, settings(NoiseSettings.create(0, 256, 1, 2), ACBlocks.dreadstone.get().defaultBlockState(),
				ACBlocks.abyssalnite_stone.get().defaultBlockState(), dreadlandsRouter, dreadlandsSurface(), SEA_LEVEL));

		DensityFunction omothol = floatingLayer(0);
		context.register(OMOTHOL, settings(NoiseSettings.create(0, 256, 2, 1), ACBlocks.omothol_stone.get().defaultBlockState(),
				Blocks.AIR.defaultBlockState(), router(omothol, omothol), SurfaceRules.state(ACBlocks.omothol_stone.get().defaultBlockState()), 0));

		DensityFunction darkRealm = DensityFunctions.min(plains, DensityFunctions.mul(DensityFunctions.constant(-1.0D), floatingLayer(30)));
		context.register(DARK_REALM, settings(NoiseSettings.create(0, 256, 1, 2), ACBlocks.darkstone.get().defaultBlockState(),
				ACBlocks.darkstone.get().defaultBlockState(), router(plains, darkRealm), darkRealmSurface(), SEA_LEVEL));
	}

	/** 1.12.2 surface height in blocks for a biome base height (ChunkProviderAbyss#generateNoise, d5) */
	static double height(float baseHeight) {
		double d12 = (baseHeight * 4.0F - 1.0F) / 8.0F * 8.5D / 8.0D;
		return 8.0D * (8.5D + d12 * 4.0D);
	}

	/** 1.12.2 density falloff per block above the surface for a biome height variation (d6 / 128) */
	static double slope(float heightVariation) {
		double d14 = heightVariation * 0.9F + 0.1F;
		return 12.0D * 128.0D / 256.0D / d14 / 8.0D / 128.0D;
	}

	/** noise + slope * (height - y), 4x steeper below the height, sliding to -10 between y 232 and 256 */
	private static DensityFunction terrain(DensityFunction height, DensityFunction slope) {
		DensityFunction t = DensityFunctions.add(height, NEGATIVE_Y);
		DensityFunction steeper = DensityFunctions.add(t, DensityFunctions.mul(DensityFunctions.constant(3.0D), DensityFunctions.max(t, DensityFunctions.zero())));
		DensityFunction density = DensityFunctions.add(TERRAIN_NOISE, DensityFunctions.mul(slope, steeper));
		return DensityFunctions.lerp(DensityFunctions.yClampedGradient(232, 256, 0.0D, 1.0D), density, DensityFunctions.constant(-10.0D / 128.0D));
	}

	/**
	 * ChunkProviderOmothol#initializeNoiseField: noise - 8, sliding to -30 from y 4 up to y 128 and from y 32 down to
	 * y 4 (both shifted up by offset). The 1.12.2 code also computed a distance to the middle and a thickness noise,
	 * but never used them.
	 */
	private static DensityFunction floatingLayer(int offset) {
		DensityFunction base = DensityFunctions.add(FLOATING_NOISE, DensityFunctions.constant(-8.0D / 128.0D));
		DensityFunction top = DensityFunctions.lerp(DensityFunctions.yClampedGradient(4 + offset, 128 + offset, 0.0D, 1.0D), base, DensityFunctions.constant(-30.0D / 128.0D));
		return DensityFunctions.lerp(DensityFunctions.yClampedGradient(4 + offset, 32 + offset, 1.0D, 0.0D), top, DensityFunctions.constant(-30.0D / 128.0D));
	}

	/** low below -0.03, high above 0.03 (the 1.12.2 biome height blending only reached 8 blocks into a neighbour) */
	private static DensityFunction step(DensityFunction coordinate, double low, double high) {
		return DensityFunctions.spline(CubicSpline.builder(new DensityFunctions.Spline.Coordinate(Holder.direct(coordinate)))
				.addPoint(-0.03F, (float) low).addPoint(0.03F, (float) high).build());
	}

	private static NoiseRouter router(DensityFunction initial, DensityFunction density) {
		return new NoiseRouter(zero(), zero(), zero(), zero(), zero(), zero(), zero(), zero(), zero(), zero(),
				initial, DensityFunctions.interpolated(density), zero(), zero(), zero());
	}

	private static DensityFunction zero() {
		return DensityFunctions.zero();
	}

	private static NoiseGeneratorSettings settings(NoiseSettings noise, BlockState block, BlockState fluid, NoiseRouter router, SurfaceRules.RuleSource surface, int seaLevel) {
		return new NoiseGeneratorSettings(noise, block, fluid, router, surface, List.of(), seaLevel, false, false, false, false);
	}

	/**
	 * BiomeGenAbywasteland#generateAbyssalWastelandTerrain: fused abyssal sand on surfaces from y 62 up, abyssal sand
	 * below it and on surfaces down to y 53 (the bedrock/pillar floor is {@code abyssalcraft:dimension_floor})
	 */
	private static SurfaceRules.RuleSource abyssalWastelandSurface() {
		return topAndFiller(SurfaceRules.state(ACBlocks.fused_abyssal_sand.get().defaultBlockState()), SurfaceRules.state(ACBlocks.abyssal_sand.get().defaultBlockState()));
	}

	/** BiomeGenDreadlandsBase#genDreadlandsTerrain with the top/filler blocks of the Dreadlands Forest and Purified Dreadlands */
	private static SurfaceRules.RuleSource dreadlandsSurface() {
		SurfaceRules.RuleSource abyssalnite = SurfaceRules.state(ACBlocks.abyssalnite_stone.get().defaultBlockState());
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.isBiome(ACBiomes.dreadlands_forest), topAndFiller(SurfaceRules.state(ACBlocks.dreadlands_grass.get().defaultBlockState()),
						SurfaceRules.state(ACBlocks.dreadlands_dirt.get().defaultBlockState()))),
				SurfaceRules.ifTrue(SurfaceRules.isBiome(ACBiomes.purified_dreadlands), topAndFiller(abyssalnite, abyssalnite)));
	}

	private static SurfaceRules.RuleSource topAndFiller(SurfaceRules.RuleSource top, SurfaceRules.RuleSource filler) {
		SurfaceRules.ConditionSource aboveSea = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(SEA_LEVEL - 1), 0);
		SurfaceRules.ConditionSource nearSea = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(SEA_LEVEL - 10), 0);
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveSea, top), SurfaceRules.ifTrue(nearSea, filler))),
				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(nearSea, filler)));
	}

	/** Vanilla 1.12.2 biome terrain: a bedrock floor up to y 4 */
	private static SurfaceRules.RuleSource darkRealmSurface() {
		return SurfaceRules.ifTrue(SurfaceRules.verticalGradient("abyssalcraft:dark_realm_bedrock", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
				SurfaceRules.state(Blocks.BEDROCK.defaultBlockState()));
	}

	private static ResourceKey<NoiseGeneratorSettings> key(String name) {
		return ResourceKey.create(Registries.NOISE_SETTINGS, AbyssalCraft.loc(name));
	}

	private static ResourceKey<NormalNoise.NoiseParameters> noise(String name) {
		return ResourceKey.create(Registries.NOISE, AbyssalCraft.loc(name));
	}
}
