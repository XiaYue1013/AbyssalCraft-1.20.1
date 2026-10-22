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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the overworld: biomes inserted through TerraBlender, the overworld ores
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class OverworldTests {

	/**
	 * Biome search grid. The Darklands Mountains are rare (a few samples in 25,000 columns), so the grid reaches 30,000
	 * blocks out; the world seed changes every run
	 */
	private static final int STEP = 96, RADIUS = 160;

	private static void log(String text) {
		AbyssalCraft.LOGGER.info("[OverworldTests] {}", text);
	}

	/** First surface column (on a grid around the origin) whose biome matches */
	private static BlockPos find(ServerLevel level, Predicate<Holder<Biome>> biome) {
		BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
		Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
		for(int r = 0; r <= RADIUS; r++)
			for(int x = -r; x <= r; x++)
				for(int z = -r; z <= r; z++) {
					if(Math.abs(x) != r && Math.abs(z) != r) continue;
					BlockPos pos = new BlockPos(x * STEP, 0, z * STEP);
					// cheap noise check first, the surface height only for candidates
					if(!biome.test(source.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(64), QuartPos.fromBlock(pos.getZ()), sampler))
							&& !biome.test(source.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(140), QuartPos.fromBlock(pos.getZ()), sampler)))
						continue;
					int y = level.getChunkSource().getGenerator().getBaseHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, level,
							level.getChunkSource().randomState());
					if(biome.test(source.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(y), QuartPos.fromBlock(pos.getZ()), sampler)))
						return pos.atY(y);
				}
		return null;
	}

	private static int count(ServerLevel level, BlockPos center, int minY, int maxY, Block block) {
		for(int cx = -1; cx <= 1; cx++)
			for(int cz = -1; cz <= 1; cz++)
				level.getChunk((center.getX() >> 4) + cx, (center.getZ() >> 4) + cz);
		int count = 0;
		for(BlockPos p : BlockPos.betweenClosed(center.getX() - 24, minY, center.getZ() - 24, center.getX() + 24, maxY, center.getZ() + 24))
			if(level.getBlockState(p).is(block))
				count++;
		return count;
	}

	/** The TerraBlender region produces all six AbyssalCraft overworld biomes */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void overworldBiomes(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
		Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
		List<ResourceKey<Biome>> wanted = List.of(ACBiomes.darklands, ACBiomes.darklands_forest, ACBiomes.darklands_plains, ACBiomes.darklands_hills,
				ACBiomes.darklands_mountains, ACBiomes.coralium_infested_swamp);
		Map<ResourceKey<Biome>, Integer> found = new HashMap<>();
		int samples = 0;
		for(int x = -RADIUS; x <= RADIUS; x++)
			for(int z = -RADIUS; z <= RADIUS; z++) {
				samples++;
				for(int y : new int[] {64, 140})
					source.getNoiseBiome(QuartPos.fromBlock(x * STEP), QuartPos.fromBlock(y), QuartPos.fromBlock(z * STEP), sampler).unwrapKey()
					.filter(wanted::contains).ifPresent(key -> found.merge(key, 1, Integer::sum));
			}
		log("AbyssalCraft biomes in " + samples + " columns over " + (2 * RADIUS * STEP) + " blocks: " + found);
		for(ResourceKey<Biome> key : wanted)
			helper.assertTrue(found.containsKey(key), key.location() + " doesn't generate");
		helper.succeed();
	}

	/** Darklands Mountains are covered in darkstone (BiomeGenDarklandsMountains top/filler) */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void darklandsMountainsSurface(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = find(level, b -> b.is(ACBiomes.darklands_mountains));
		if(pos == null)
			throw new GameTestAssertException("no Darklands Mountains found");
		// surface rules use the (fuzzy) biome of the surface block itself, so judge the columns around whose surface is in the biome
		int columns = 0, darkstone = 0;
		BlockState sample = null;
		for(int dx = -16; dx <= 16; dx += 4)
			for(int dz = -16; dz <= 16; dz += 4) {
				BlockPos column = pos.offset(dx, 0, dz);
				level.getChunk(column.getX() >> 4, column.getZ() >> 4);
				BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column).below();
				if(!level.getBiome(top).is(ACBiomes.darklands_mountains)) continue;
				columns++;
				BlockState state = level.getBlockState(top);
				if(state.is(ACBlocks.darkstone.get()) || state.is(net.minecraft.world.level.block.Blocks.SNOW) || state.is(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
						|| state.is(net.minecraft.world.level.block.Blocks.POWDER_SNOW))
					darkstone++;
				else
					sample = state;
			}
		log("Darklands Mountains around " + pos + ": " + darkstone + " of " + columns + " surface columns darkstone/snow" + (sample != null ? ", other: " + sample : ""));
		helper.assertTrue(columns > 0 && darkstone >= columns * 0.8, darkstone + " of " + columns + " columns darkstone, other surface " + sample);
		helper.succeed();
	}

	/** Nitre Ore everywhere below y 30, Coralium Ore in oceans */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void overworldOres(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos land = find(level, b -> b.is(BiomeTags.IS_OVERWORLD) && !b.is(BiomeTags.IS_OCEAN) && !b.is(BiomeTags.IS_RIVER));
		BlockPos ocean = find(level, b -> b.is(net.minecraft.world.level.biome.Biomes.OCEAN) || b.is(net.minecraft.world.level.biome.Biomes.LUKEWARM_OCEAN)
				|| b.is(net.minecraft.world.level.biome.Biomes.COLD_OCEAN) || b.is(net.minecraft.world.level.biome.Biomes.WARM_OCEAN));
		if(land == null || ocean == null)
			throw new GameTestAssertException("no land / ocean found");
		int nitre = count(level, land, 0, 29, ACBlocks.nitre_ore.get());
		int coralium = count(level, ocean, 0, 40, ACBlocks.coralium_ore.get());
		log("nitre ore near " + land + ": " + nitre + ", coralium ore near " + ocean + ": " + coralium);
		helper.assertTrue(nitre > 0, "no nitre ore");
		helper.assertTrue(coralium > 0, "no coralium ore in the ocean");
		helper.succeed();
	}
}
