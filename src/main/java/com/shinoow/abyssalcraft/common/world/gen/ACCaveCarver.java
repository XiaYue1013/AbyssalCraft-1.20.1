/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (MapGenCavesAC)
 *     1.20.1 port - the AC stones are replaceable through the configured carver's block tag
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;

/**
 * Cave carver that always carves air, like the 1.12.2 one did below sea level (see {@link DryCarvingAquifer})
 */
public class ACCaveCarver extends CaveWorldCarver {

	public ACCaveCarver(Codec<CaveCarverConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomes, CarvingMask mask,
			BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface) {
		return super.carveBlock(context, config, chunk, biomes, mask, pos, checkPos, new DryCarvingAquifer(aquifer), reachedSurface);
	}
}
