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

public class StructureElevatedShrineLarge extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState brick_slab = true ? slab(ACBlocks.darkstone_brick_slab, 0) : AIR;
		BlockState cobble = state(ACBlocks.darkstone_cobblestone);
		BlockState cobble_wall = true ? state(ACBlocks.darkstone_cobblestone_wall) : AIR;

		for(int i = -3; i < 4; i++)
			for(int j = -5; j < 6; j++){

				boolean flag = i > -2 && i < 2;
				boolean flag1 = i > -3 && i < 3;

				if(j == -4 || j == 4 && flag)
					for(int k = 1; k < 4; k++){
						setAir(worldIn, pos.offset(i, k+2, j));
						setAir(worldIn, pos.offset(j, k+2, i));
					}
				if(j > -4 && j < 4 && flag1)
					for(int k = 1; k < 10; k++)
						if(k > 6){
							if(flag){
								setAir(worldIn, pos.offset(i, k, j));
								setAir(worldIn, pos.offset(j, k, i));
							}
						} else if(k != 2){
							setAir(worldIn, pos.offset(i, k, j));
							setAir(worldIn, pos.offset(j, k, i));
						}

				if((j == -5 || j == 5) && flag && true){
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 1 : 0));
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 3 : 2));
				}
				if((j == -4 || j == 4) && flag){
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), cobble);
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), cobble);
					if(true) {
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 2, i), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 1 : 0));
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 3 : 2));
					}

					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 6, i), brick_slab);
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 6, j), brick_slab);
				}
				if(j == -3 || j == 3){
					if(flag1){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 2, i), cobble);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), cobble);
						if(flag){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, 9, i), brick_slab);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, 9, j), brick_slab);
						}
						if(i == 0)
							for(int k = 1; k < 3; k++){
								setBlockAndNotifyAdequately(worldIn, pos.offset(j, k+6, i), cobble_wall);
								setBlockAndNotifyAdequately(worldIn, pos.offset(i, k+6, j), cobble_wall);
							}
					} else
						for(int k = 1; k < 6; k++){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), cobble_wall);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), cobble_wall);
						}
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 6, i), i == 0 ? cobble : brick_slab);
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 6, j), i == 0 ? cobble : brick_slab);

				}
				if((j == -2 || j == 2) && flag1){
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), flag ? state(ACBlocks.monolith_stone) : cobble);
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 2, i), flag ? state(ACBlocks.monolith_stone) : cobble);

					for(int k = 0; k < 4; k++)
						if(k < 3){
							if(!flag){
								setBlockAndNotifyAdequately(worldIn, pos.offset(i, k+6, j), k == 0 ? cobble : cobble_wall);
								setBlockAndNotifyAdequately(worldIn, pos.offset(j, k+6, i), k == 0 ? cobble : cobble_wall);
							}
						} else if(i != 0){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k+6, i), brick_slab);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k+6, j), brick_slab);
						}
				}

				if(j > -2 && j < 2 && flag)
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), state(ACBlocks.shoggoth_ooze).setValue(BlockShoggothOoze.LAYERS, 8));
			}

		placeStatue(worldIn, rand, pos.above(3));

		return true;
	}
}
