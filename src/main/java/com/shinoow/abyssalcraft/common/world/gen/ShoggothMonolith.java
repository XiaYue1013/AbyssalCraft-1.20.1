/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (WorldGenShoggothMonolith)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The monolith a group of Lesser Shoggoths builds: a 3x3 Monolith Stone pillar, 5 - 12 blocks tall, with Shoggoth
 * Biomass at its base and a random statue on top.
 */
public class ShoggothMonolith {

	public static boolean generate(Level world, RandomSource rand, BlockPos pos) {
		while(world.isEmptyBlock(pos) && pos.getY() > world.getMinBuildHeight() + 2)
			pos = pos.below();

		// Instead of "only if block is Shoggoth Ooze", block can't be air,
		// and block underneath must be solid
		if(world.isEmptyBlock(pos) || !world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP))
			return false;
		else {
			BlockState monolith = ACBlocks.monolith_stone.get().defaultBlockState();
			int max = rand.nextInt(8) + 5;
			for(int i = 0; i < max; i++)
				for(int x = -1; x < 2; x++)
					for(int z = -1; z < 2; z++)
						world.setBlock(pos.offset(x, i, z), monolith, 2);
			world.setBlock(pos, ACBlocks.shoggoth_biomass.get().defaultBlockState(), 2);
			world.setBlock(pos.above(max), getStatue(rand).setValue(BlockStatue.FACING, Direction.from2DDataValue(rand.nextInt(3))), 2);
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 2, world.random.nextFloat() * 0.1F * 0.9F);
			return true;
		}
	}

	private static BlockState getStatue(RandomSource rand){
		return BlockHandler.STATUES.get(rand.nextInt(BlockHandler.STATUES.size())).get().defaultBlockState();
	}
}
