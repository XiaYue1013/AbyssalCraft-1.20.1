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

public class StructureElevatedShrine extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel worldIn, RandomSource rand, BlockPos pos) {

		BlockState brick_slab = true ?  slab(ACBlocks.darkstone_brick_slab, 0) : AIR;
		BlockState cobble = state(ACBlocks.darkstone_cobblestone);
		BlockState cobble_wall = true ? state(ACBlocks.darkstone_cobblestone_wall) : AIR;

		for(int i = -2; i < 3; i++)
			for(int j = -4; j < 5; j++){

				boolean flag = i > -2 && i < 2;

				if(j > -3 && j < 3 && flag)
					for(int k = 1; k < 7; k++)
						if(k != 2){
							setAir(worldIn, pos.offset(i, k, j));
							setAir(worldIn, pos.offset(j, k, i));
						}

				if((j == -4 || j == 4) && flag && true){
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 1 : 0));
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 3 : 2));
				}
				if((j == -3 || j == 3) && flag){
					setBlockAndNotifyAdequately(worldIn, pos.offset(j, 1, i), cobble);
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 1, j), cobble);
					if(true) {
						setBlockAndNotifyAdequately(worldIn, pos.offset(j, 2, i), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 1 : 0));
						setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), stairs(ACBlocks.darkstone_cobblestone_stairs, j > 0 ? 3 : 2));
					}
				}
				if(j == -2 || j == 2)
					for(int k = 1; k < 7; k++){
						if(i == -2 || i == 2){
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), cobble_wall);
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), cobble_wall);
						}
						if(k == 2 && flag){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), state(ACBlocks.monolith_stone));
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), state(ACBlocks.monolith_stone));
						}
						if(k == 6){
							setBlockAndNotifyAdequately(worldIn, pos.offset(i, k, j), brick_slab);
							setBlockAndNotifyAdequately(worldIn, pos.offset(j, k, i), brick_slab);
						}
					}
				if(j > -2 && j < 2 && flag)
					setBlockAndNotifyAdequately(worldIn, pos.offset(i, 2, j), state(ACBlocks.shoggoth_ooze).setValue(BlockShoggothOoze.LAYERS, 8));
			}

		placeStatue(worldIn, rand, pos.above(3));

		return true;
	}
}
