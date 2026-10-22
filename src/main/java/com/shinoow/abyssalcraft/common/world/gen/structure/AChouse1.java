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

public class AChouse1 extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos) {

		pos = pos.offset(-3, 1, -4);

		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if(true)
			setBlock(world, new BlockPos(i + 0, j + 0, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 0, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 0, j + 0, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 1, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 0, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 1, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 1, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 1, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 1, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 0, j + 1, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 2, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 0, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 2, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 2, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 2, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 2, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 2, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 0, j + 2, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 3, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 0, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 3, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 3, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 3, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 3, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 3, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 0, j + 3, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 4, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 0, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 0, j + 4, k + 7), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 0, j + 4, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 4), AIR, 2);
		if(true)
			setBlock(world, new BlockPos(i + 1, j + 1, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 1, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 4), AIR, 2);
		if(true)
			setBlock(world, new BlockPos(i + 2, j + 1, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 4, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 3, j + 0, k + 0), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 2), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 3), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 4), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 5), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 6), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 7), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 0, k + 0), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 2), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 3), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 4), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 5), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 6), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 7), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 1), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 4), AIR, 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 1, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 4), AIR, 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 1, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 0, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 0, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 7, j + 1, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 1, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 7, j + 2, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 2, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 7, j + 3, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 3, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 7, j + 4, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 4, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 7), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 4, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 1, j + 2, k + 7), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 5), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 2), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 3), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 2), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 3), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 7), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 5), 2);

		return true;
	}
}
