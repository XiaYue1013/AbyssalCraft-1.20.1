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
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.init.ACDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Abyssal Sand, fuses into Fused Abyssal Sand when exposed (only in the Abyssal Wasteland)
 */
public class BlockAbyssalSand extends Block {

	public BlockAbyssalSand(Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (!level.isAreaLoaded(pos, 3)) return; // don't load unloaded chunks
		if(level.dimension() != ACDimensions.ABYSSAL_WASTELAND) return;
		BlockState above = level.getBlockState(pos.above());
		if (rand.nextInt(10) == 0
				&& above.getFluidState().isEmpty()
				&& above.getLightBlock(level, pos.above()) <= 2)
			level.setBlockAndUpdate(pos, ACBlocks.fused_abyssal_sand.get().defaultBlockState());
	}
}
