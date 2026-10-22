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

public class StructureRitualGroundsColumns extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState chiseled_brick = state(ACBlocks.chiseled_darkstone_bricks);
		BlockState brick_slab = true ? slab(ACBlocks.darkstone_brick_slab, 0) : AIR;
		BlockState cobble = state(ACBlocks.darkstone_cobblestone);

		for(int i = -3; i < 4; i++)
			for(int j = -5; j < 6; j++){

				boolean flag = i > -2 && i < 2;
				boolean flag1 = i > -4 && i < 4;

				for(int k = 0; k < 4; k++){
					if(j == -5 || j == 5)
						if(flag){
							setAir(worldIn, pos.offset(i, k+1, j));
							setAir(worldIn, pos.offset(j, k+1, i));
						}
					if(j > -5 && j < 5){
						setAir(worldIn, pos.offset(i, k+1, j));
						setAir(worldIn, pos.offset(j, k+1, i));
					}
				}

				if(j == -5 || j == 5)
					if(flag){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), getBrick(rand));
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), getBrick(rand));

						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 4, i), brick_slab);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 4, j), brick_slab);
					}
				if(j == -4 || j == 4)
					if(flag1){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i),  getBrick(rand));
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), getBrick(rand));
						if(i != 0){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, 4, i), brick_slab);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, 4, j), brick_slab);
						}
					}
				if(j > -4 && j < 4)
					if(flag1){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), getBrick(rand));
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), getBrick(rand));
						if((j == -3 || j == 3) && (i == -3 || i == 3)){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, 4, i), brick_slab);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, 4, j), brick_slab);
						}
					}

			}

		setBlockAndNotifyAdequately(worldIn, pos.above(), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(3, 1, 0), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(-3, 1, 0), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(0, 1, 3), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(0, 1, -3), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(2, 1, 2), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(2, 1, -2), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(-2, 1, 2), cobble);
		setBlockAndNotifyAdequately(worldIn, pos.offset(-2, 1, -2), cobble);

		for(int i = 0; i < 3; i++){
			setBlockAndNotifyAdequately(worldIn, pos.offset(4, 1 + i, 2), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(4, 1 + i, -2), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(-4, 1 + i, 2), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(-4, 1 + i, -2), i == 1 ? chiseled_brick : getBrick(rand));

			setBlockAndNotifyAdequately(worldIn, pos.offset(2, 1 + i, 4), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(-2, 1 + i, 4), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(2, 1 + i, -4), i == 1 ? chiseled_brick : getBrick(rand));
			setBlockAndNotifyAdequately(worldIn, pos.offset(-2, 1 + i, -4), i == 1 ? chiseled_brick : getBrick(rand));
		}

		return true;
	}

}
