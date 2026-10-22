/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (StructureDarklandsBase)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.structure;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public abstract class DarklandsStructure extends LegacyStructure {

	/** 30 %: a statue (random deity, facing south/west/north as in 1.12.2) on a monolith pillar */
	protected void placeStatue(WorldGenLevel world, RandomSource rand, BlockPos pos) {
		if(rand.nextFloat() < 0.3F) {
			setBlockAndNotifyAdequately(world, pos, state(ACBlocks.monolith_pillar));
			setBlockAndNotifyAdequately(world, pos.above(), getStatue(rand).setValue(BlockStatue.FACING, Direction.from2DDataValue(rand.nextInt(3))));
		}
	}

	protected BlockState getBrick(RandomSource rand) {
		return rand.nextFloat() < 0.2 ? state(ACBlocks.cracked_darkstone_bricks) : state(ACBlocks.darkstone_bricks);
	}

	private BlockState getStatue(RandomSource rand) {
		return state(BlockHandler.STATUES.get(rand.nextInt(BlockHandler.STATUES.size())));
	}
}
