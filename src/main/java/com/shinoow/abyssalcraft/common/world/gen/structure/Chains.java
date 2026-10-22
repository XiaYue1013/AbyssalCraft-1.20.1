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

public class Chains extends LegacyStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState abyssal_stone = state(ACBlocks.abyssal_stone);
		BlockState coralium_cobblestone = state(ACBlocks.coralium_cobblestone);
		int h = 5 + rand.nextInt(35);
		int h2 = 255 - h * 6;
		int t = 0;
		while(!worldIn.isEmptyBlock(new BlockPos(pos.getX(), h2+1, pos.getZ()))){
			h2++;
			t++;
		}

		h -= t/6;

		int height = 255 - h2;
		BlockPos pos1 = new BlockPos(pos.getX(), h2, pos.getZ());
		boolean b = !worldIn.isEmptyBlock(pos1) || !worldIn.isEmptyBlock(pos1.below());

		for(int i = -3; i < 4; i++)
			for(int j = -3; j < 4; j++){

				boolean flag = i > -2 && i < 2;

				for(int k = 0; k < 3; k++)
					if((j == -3 || j == 3) && flag){
						if(k == 0){
							if(b){
								setBlockAndNotifyAdequately(worldIn, pos1.offset(j, 0, i), abyssal_stone);
								setBlockAndNotifyAdequately(worldIn, pos1.offset(i, 0, j), abyssal_stone);
							}

							setBlockAndNotifyAdequately(worldIn, pos1.offset(j, height, i), abyssal_stone);
							setBlockAndNotifyAdequately(worldIn, pos1.offset(i, height, j), abyssal_stone);
						}
					} else if(j == -2 || j == 2){
						if(k == 1){
							if(flag){
								if(b){
									setBlockAndNotifyAdequately(worldIn, pos1.offset(j, k, i), abyssal_stone);
									setBlockAndNotifyAdequately(worldIn, pos1.offset(i, k, j), abyssal_stone);
								}

								setBlockAndNotifyAdequately(worldIn, pos1.offset(j, height - k, i), abyssal_stone);
								setBlockAndNotifyAdequately(worldIn, pos1.offset(i, height - k, j), abyssal_stone);
							}
						} else if(k < 2 && i > -3 && i < 3){
							if(b){
								setBlockAndNotifyAdequately(worldIn, pos1.offset(j, k, i), abyssal_stone);
								setBlockAndNotifyAdequately(worldIn, pos1.offset(i, k, j), abyssal_stone);
							}

							setBlockAndNotifyAdequately(worldIn, pos1.offset(j, height - k, i), abyssal_stone);
							setBlockAndNotifyAdequately(worldIn, pos1.offset(i, height - k, j), abyssal_stone);
						}
					}
					else if(j > -2 && j < 2 && flag){
						if(b){
							setBlockAndNotifyAdequately(worldIn, pos1.offset(j, k, i), abyssal_stone);
							setBlockAndNotifyAdequately(worldIn, pos1.offset(i, k, j), abyssal_stone);
						}

						setBlockAndNotifyAdequately(worldIn, pos1.offset(j, height - k, i), abyssal_stone);
						setBlockAndNotifyAdequately(worldIn, pos1.offset(i, height - k, j), abyssal_stone);
					}
			}

		for(int i = 0; i < h; i++){
			setBlockAndNotifyAdequately(worldIn, pos1.above(i*6), coralium_cobblestone);

			setBlockAndNotifyAdequately(worldIn, pos1.offset(0, 1 + i*6, 1), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(0, 1 + i*6, -1), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(0, 2 + i*6, 1), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(0, 2 + i*6, -1), coralium_cobblestone);

			setBlockAndNotifyAdequately(worldIn, pos1.above(3 + i*6), coralium_cobblestone);

			setBlockAndNotifyAdequately(worldIn, pos1.offset(1, 4 + i*6, 0), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(-1, 4 + i*6, 0), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(1, 5 + i*6, 0), coralium_cobblestone);
			setBlockAndNotifyAdequately(worldIn, pos1.offset(-1, 5 + i*6, 0), coralium_cobblestone);

			if(i + 1 == h)
				setBlockAndNotifyAdequately(worldIn, pos1.above(6 + i*6), coralium_cobblestone);
		}

		return true;
	}
}
