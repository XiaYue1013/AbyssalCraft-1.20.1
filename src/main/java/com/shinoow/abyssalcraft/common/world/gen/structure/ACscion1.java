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
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ACscion1 extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos) {

		BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
		BlockState glowing_brick = state(ACBlocks.glowing_darkstone_bricks);
		BlockState chiseled_brick = state(ACBlocks.chiseled_darkstone_bricks);
		BlockState brick_slab = true ? slab(ACBlocks.darkstone_brick_slab, 0) : AIR;

		boolean shouldGrass = world.getBlockState(pos).is(Blocks.GRASS_BLOCK);

		for(int i = -3; i < 4; i++)
			for(int j = -4; j < 5; j++){

				boolean flag = i > -3 && i < 3;
				boolean flag1 = i == -3 || i == 3;
				boolean flag2 = i > -2 && i < 2;

				if(j == -3 || j == 3 && flag)
					for(int k = 0; k < 4; k++){
						setAir(world, pos.offset(j, k+2, i));
						setAir(world, pos.offset(i, k+2, j));
					}
				if(j == -2 || j == 2 && !flag2)
					for(int k = 0; k < 4; k++){
						setAir(world, pos.offset(j, k+2, i));
						setAir(world, pos.offset(i, k+2, j));
					}

				if(j == -4 || j == 4)
					if(flag && shouldGrass){
						setBlockAndNotifyAdequately(world, pos.offset(j, 1, i), grass);
						setBlockAndNotifyAdequately(world, pos.offset(i, 1, j), grass);
					}
				if(j == -3 || j == 3)
					if(shouldGrass){
						setBlockAndNotifyAdequately(world, pos.offset(j, 1, i), flag1 ? grass : getBrick(random));
						setBlockAndNotifyAdequately(world, pos.offset(i, 1, j), flag1 ? grass : getBrick(random));
					} else if(!flag1){
						setBlockAndNotifyAdequately(world, pos.offset(j, 1, i), getBrick(random));
						setBlockAndNotifyAdequately(world, pos.offset(i, 1, j), getBrick(random));
					}
				if(j > -3 && j < 3){
					setBlockAndNotifyAdequately(world, pos.offset(j, 1, i), getBrick(random));
					if((j == -2 || j == 2) && flag2)
						for(int k = 0; k < 4; k++){
							if(k == 0){
								setBlockAndNotifyAdequately(world, pos.offset(j, k+2, i), i == 0 && true ? stairs(ACBlocks.darkstone_brick_stairs, j > 0 ? 1 : 0) : glowing_brick);
								setBlockAndNotifyAdequately(world, pos.offset(i, k+2, j), i == 0 && true ? stairs(ACBlocks.darkstone_brick_stairs, j > 0 ? 3 : 2) : glowing_brick);
							}
							if(k == 1){
								setBlockAndNotifyAdequately(world, pos.offset(j, k+2, i), i == 0 ? AIR : brick_slab);
								setBlockAndNotifyAdequately(world, pos.offset(i, k+2, j), i == 0 ? AIR : brick_slab);
							}
							if(k == 2 && true){
								setBlockAndNotifyAdequately(world, pos.offset(j, k+2, i), i == 0 ? slab(ACBlocks.darkstone_brick_slab, 8) : stairs(ACBlocks.darkstone_brick_stairs, j > 0 ? 5 : 4));
								setBlockAndNotifyAdequately(world, pos.offset(i, k+2, j), i == 0 ? slab(ACBlocks.darkstone_brick_slab, 8) : stairs(ACBlocks.darkstone_brick_stairs, j > 0 ? 7 : 6));
							}
							if(k == 3){
								setBlockAndNotifyAdequately(world, pos.offset(j, k+2, i), brick_slab);
								setBlockAndNotifyAdequately(world, pos.offset(i, k+2, j), brick_slab);
							}
						}
					if(j > -2 && j < 2 && flag2)
						for(int k = 0; k < 4; k++)
							setBlockAndNotifyAdequately(world, pos.offset(j, k+2, i), (j == 0 && i != 0 || j != 0 && i == 0) && k == 1 ? chiseled_brick : k == 3 ? brick_slab : getBrick(random));
				}
			}

		if(random.nextFloat() < 0.1){
			setBlockAndNotifyAdequately(world, pos.above(4), Blocks.DIRT.defaultBlockState());
			ACFeatures.DARKLANDS_TREE.get().grow(world, random, pos.above(5));
		}

		return true;
	}
}
