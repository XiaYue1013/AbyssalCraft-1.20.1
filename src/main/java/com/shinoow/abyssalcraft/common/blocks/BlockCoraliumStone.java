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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Coralium Stone, slowly solidifies adjacent Liquid Coralium into more Coralium Stone
 */
public class BlockCoraliumStone extends Block {

	public BlockCoraliumStone(Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		for(Direction face : Direction.values())
			if (level.getBlockState(pos.relative(face)).is(ACBlocks.liquid_coralium.get()) && random.nextFloat() < 0.3)
				level.setBlockAndUpdate(pos.relative(face), state);
	}
}
