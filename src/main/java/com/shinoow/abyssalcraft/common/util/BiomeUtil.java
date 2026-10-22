/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 *     1.20.1 port - biomes are 3D (4x4x4 cells) in 1.20.1; columns are rewritten the same way /fillbiome does it and
 *     the changed chunks are resent with the vanilla biome packet instead of a custom message
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

/**
 * Utility class that handles biome transformations (converting Darklands back and forth, turning biomes into Dreadlands, purging Dreadlands)
 * @author sk2048
 *
 */
public class BiomeUtil {

	/**
	 * Changes the biome of the (full height) column at the specified position with the specified one
	 * @param worldIn Current World
	 * @param pos Current Position
	 * @param b Replacement biome
	 */
	public static void updateBiome(Level worldIn, BlockPos pos, ResourceKey<Biome> b) {
		updateBiomes(worldIn, Collections.singleton(pos), b);
	}

	/**
	 * Changes the biome of several (full height) columns at once, resending every affected chunk only once.
	 * Positions in chunks that aren't loaded are skipped (this never loads or generates chunks).
	 * @param worldIn Current World
	 * @param positions Positions of the columns
	 * @param b Replacement biome
	 * @return The amount of columns that were changed
	 */
	public static int updateBiomes(Level worldIn, Collection<BlockPos> positions, ResourceKey<Biome> b) {
		if(!(worldIn instanceof ServerLevel level)) return 0;
		Holder<Biome> biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolder(b).orElse(null);
		if(biome == null) return 0;

		Map<ChunkPos, Set<Long>> columns = new LinkedHashMap<>();
		for(BlockPos pos : positions) {
			int qx = QuartPos.fromBlock(pos.getX()), qz = QuartPos.fromBlock(pos.getZ());
			columns.computeIfAbsent(new ChunkPos(pos), c -> new HashSet<>()).add(ChunkPos.asLong(qx, qz));
		}

		List<ChunkAccess> changed = new ArrayList<>();
		int count = 0;
		for(Map.Entry<ChunkPos, Set<Long>> entry : columns.entrySet()) {
			ChunkAccess chunk = level.getChunk(entry.getKey().x, entry.getKey().z, ChunkStatus.FULL, false);
			if(chunk == null) continue;
			Set<Long> targets = entry.getValue();
			int[] modified = {0};
			chunk.fillBiomesFromNoise((x, y, z, sampler) -> {
				Holder<Biome> current = chunk.getNoiseBiome(x, y, z);
				if(targets.contains(ChunkPos.asLong(x, z)) && !current.is(b)) {
					modified[0]++;
					return biome;
				}
				return current;
			}, level.getChunkSource().randomState().sampler());
			if(modified[0] > 0) {
				chunk.setUnsaved(true);
				changed.add(chunk);
				count += targets.size();
			}
		}
		if(!changed.isEmpty())
			level.getChunkSource().chunkMap.resendBiomesForChunks(changed);
		return count;
	}

	/**
	 * Checks the biome of a column (sampled at sea level, like the 2D biomes of 1.12.2)
	 */
	public static boolean isBiome(Level level, BlockPos pos, ResourceKey<Biome> b) {
		return level.getBiome(pos.atY(level.getSeaLevel())).is(b);
	}
}
