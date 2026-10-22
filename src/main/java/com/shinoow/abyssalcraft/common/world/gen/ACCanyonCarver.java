/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (MapGenRavineAC)
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
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

/**
 * Canyon carver that always carves air, like the 1.12.2 one did below sea level (see {@link DryCarvingAquifer})
 */
public class ACCanyonCarver extends CanyonWorldCarver {

	public ACCanyonCarver(Codec<CanyonCarverConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean carveBlock(CarvingContext context, CanyonCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomes, CarvingMask mask,
			BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface) {
		return super.carveBlock(context, config, chunk, biomes, mask, pos, checkPos, new DryCarvingAquifer(aquifer), reachedSurface);
	}
}
