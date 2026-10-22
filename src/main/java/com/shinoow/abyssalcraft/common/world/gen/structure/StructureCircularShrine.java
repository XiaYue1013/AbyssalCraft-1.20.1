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

public class StructureCircularShrine extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState brick = state(ACBlocks.darkstone_bricks);
		BlockState chiseled_brick = state(ACBlocks.chiseled_darkstone_bricks);
		BlockState brick_slab = true ? slab(ACBlocks.darkstone_brick_slab, 0) : AIR;
		BlockState cobble = state(ACBlocks.darkstone_cobblestone);
		BlockState cobble_slab = true ? slab(ACBlocks.darkstone_cobblestone_slab, 0) : AIR;
		BlockState ooze = state(ACBlocks.shoggoth_ooze).setValue(BlockShoggothOoze.LAYERS, 8);
		BlockState air = AIR;

		for(int i = -4; i < 5; i++)
			for(int j = -6; j < 7; j++){

				boolean flag = i > -2 && i < 2;
				boolean flag1 = i > -4 && i < 4;

				if(j == -6 || j == 6)
					if(flag){
						setAir(worldIn, pos.offset(i, 1, j));
						setAir(worldIn, pos.offset(j, 1, i));
					}
				if(j == -5 || j == 5)
					if(flag1){
						setAir(worldIn, pos.offset(i, 1, j));
						setAir(worldIn, pos.offset(j, 1, i));
					}
				if(j > -5 && j < -2 || j > 2 && j < 5){
					setAir(worldIn, pos.offset(i, 1, j));
					setAir(worldIn, pos.offset(j, 1, i));
				}

				if(j == -6 || j == 6)
					if(flag){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), brick);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), brick);
					}
				if(j == -5 || j == 5)
					if(flag1){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), flag ? cobble_slab : brick);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), flag ? cobble_slab : brick);
					}

				if(j == -4 || j == 4){
					if(flag){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, -1, i), cobble);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, -1, j), cobble);
					}
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), i == -4 || i == 4 ? brick : flag ? air : cobble_slab);
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), i == -4 || i == 4 ? brick : flag ? air : cobble_slab);
				}
				if(j == -3 || j == 3)
					if(flag1){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, -1, i), flag ? brick : cobble);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, -1, j), flag ? brick : cobble);

						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), flag ? brick_slab : air);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), flag ? brick_slab : air);

						if(i == 0)
							for(int k = 0; k < 2; k++){
								setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), k == 1 ? chiseled_brick : brick);
								setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), k == 1 ? chiseled_brick : brick);
							}
					}
				if(j == -2 || j == 2)
					if(i > -3 && i < 3){
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, -1, i), flag ? ooze : brick);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, -1, j), flag ? ooze : brick);

						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 0, i), flag ? air : brick);
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 0, j), flag ? air : brick);
						for(int k = 0; k < 2; k++){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), flag ? air : k == 1 ? chiseled_brick : brick);
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), flag ? air : k == 1 ? chiseled_brick : brick);
						}
					}
				if(j > -2 && j < 2 && flag)
					for(int k = -1; k < 2; k++)
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), k == -1 ? ooze : air);
			}

		placeStatue(worldIn, rand, pos);

		return true;
	}
}
