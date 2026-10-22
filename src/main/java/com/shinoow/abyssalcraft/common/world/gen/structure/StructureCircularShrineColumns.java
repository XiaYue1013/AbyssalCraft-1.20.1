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
 *     1.20.1 port - translated to 1.20.1 blocks/API (see LegacyStructure)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.structure;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.BlockShoggothOoze;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StructureCircularShrineColumns extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState chiseled_brick = state(ACBlocks.chiseled_darkstone_bricks);
		BlockState brick_slab = true ? slab(ACBlocks.darkstone_brick_slab, 0) : AIR;

		for(int i = -3; i < 4; i++)
			for(int j = -5; j < 6; j++){

				boolean flag = i > -3 && i < 3;
				boolean flag1 = i > -4 && i < 4;

				for(int k = 0; k < 4; k++){
					if(j == -5 || j == 5)
						if(flag){
							setAir(worldIn, pos.offset(i, k+2, j));
							setAir(worldIn, pos.offset(j, k+2, i));
						}
					if(j == -4 || j == 4)
						if(flag1){
							setAir(worldIn, pos.offset(i, k+2, j));
							setAir(worldIn, pos.offset(j, k+2, i));
						}
					if(j > -3 && j < 3 && flag){
						setAir(worldIn, pos.offset(i, k+2, j));
						setAir(worldIn, pos.offset(j, k+2, i));
					}
				}

				if(j == -5 || j == 5)
					if(flag){
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), brick_slab);
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), brick_slab);
					}
				if(j == -4 || j == 4){
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), flag ? getBrick(rand) : brick_slab);
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), flag ? getBrick(rand) : brick_slab);
					if(flag){
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 5, j), brick_slab);
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 5, i), brick_slab);
					}
					if(i == 0)
						for(int k = 0; k < 3; k++){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k+2, j), k == 1 ? chiseled_brick : getBrick(rand));
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k+2, i), k == 1 ? chiseled_brick : getBrick(rand));
						}
				}
				if(j == -3 || j == 3){
					if(flag1)
						for(int k = 1; k < 6; k++){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), k == 1 ? getBrick(rand) : (i < -1 || i > 1) && k == 5 ? brick_slab : AIR);
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), k == 1 ? getBrick(rand) : (i < -1 || i > 1) && k == 5 ? brick_slab : AIR);
						}
					if(i == -3 || i == 3)
						for(int k = 0; k < 3; k++){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k+2, j), k == 1 ? chiseled_brick : getBrick(rand));
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k+2, i), k == 1 ? chiseled_brick : getBrick(rand));
						}
				}
				if(j == -2 || j == 2){
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), i > -2 && i < 2 ? state(ACBlocks.monolith_stone) : getBrick(rand));
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), i > -2 && i < 2 ? state(ACBlocks.monolith_stone) : getBrick(rand));
				}
				if(j > -2 && j < 2 && i > -2 && i < 2)
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), state(ACBlocks.shoggoth_ooze).setValue(BlockShoggothOoze.LAYERS, 8));
			}

		placeStatue(worldIn, rand, pos.above(2));

		return true;
	}
}
