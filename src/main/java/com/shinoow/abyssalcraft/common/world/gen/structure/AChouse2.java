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

public class AChouse2 extends DarklandsStructure {

	@Override
	public boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos) {

		pos = pos.offset(-6, 1, -6);

		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		if(true) {
			setBlock(world, new BlockPos(i + 0, j + 0, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 0, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 0, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 0, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 0, j + 0, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 1, j + 0, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 1, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 1, j + 0, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 1, j + 0, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 2, j + 0, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 2, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 0, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 2, j + 0, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 2, j + 3, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 2, j + 4, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 2, j + 4, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 2, j + 4, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 2, j + 4, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 2, j + 4, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 0, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 3, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 0, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 3, j + 0, k + 11), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 1, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 2, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 3, j + 3, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 3, j + 4, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 4, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 3, j + 4, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 5, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 3, j + 5, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 5, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 5, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 5, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 5, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 3, j + 5, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 6, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 3, j + 6, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 6, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 6, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 6, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 6, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 3, j + 6, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 7, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 3, j + 7, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 7, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 7, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 7, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 3, j + 7, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 3, j + 7, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 4, j + 0, k + 0), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 3, j + 8, k + 4), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 6), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
			setBlock(world, new BlockPos(i + 3, j + 8, k + 8), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
		}
		setBlock(world, new BlockPos(i + 4, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 0, k + 11), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 0, k + 12), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 1, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 3, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 4, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 4, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 4, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 4), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 5), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 6), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 7), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 8), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 4, j + 5, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 6, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 8, k + 3), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 4, j + 8, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 8, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 8, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 8, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 8, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 4, j + 8, k + 9), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
		setBlock(world, new BlockPos(i + 4, j + 9, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 9, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 9, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 9, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 4, j + 9, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 4, j + 10, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 4, j + 10, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 4, j + 10, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 4, j + 10, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
			setBlock(world, new BlockPos(i + 4, j + 10, k + 7), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
			setBlock(world, new BlockPos(i + 5, j + 0, k + 0), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		}
		setBlock(world, new BlockPos(i + 5, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 0, k + 11), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 0, k + 12), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 1, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 3, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 4, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 4, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 4, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 4), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 8), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 5, j + 5, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 6, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 7, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 8, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 5, j + 8, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 8, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 8, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 8, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 5, j + 8, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 8, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 5, j + 9, k + 4), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 5, j + 9, k + 5), slab(ACBlocks.darkstone_brick_slab, 13), 2);
			setBlock(world, new BlockPos(i + 5, j + 9, k + 6), slab(ACBlocks.darkstone_brick_slab, 13), 2);
			setBlock(world, new BlockPos(i + 5, j + 9, k + 7), slab(ACBlocks.darkstone_brick_slab, 13), 2);
		}
		setBlock(world, new BlockPos(i + 5, j + 9, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 5, j + 10, k + 4), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 5, j + 10, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 10, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 5, j + 10, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 5, j + 11, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 5, j + 11, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 5, j + 10, k + 8), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
			setBlock(world, new BlockPos(i + 5, j + 11, k + 6), stairs(ACBlocks.darkstone_brick_stairs, 0), 2);
			setBlock(world, new BlockPos(i + 6, j + 0, k + 0), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		}
		setBlock(world, new BlockPos(i + 6, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 2), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 3), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 4), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 5), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 6), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 7), Blocks.RED_WOOL.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 0, k + 11), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 0, k + 12), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 2), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 4, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 4, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 4), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 6), random.nextBoolean() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.ENCHANTING_TABLE.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 8), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 6, j + 5, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 6, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 8, k + 3), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 8, k + 9), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
		setBlock(world, new BlockPos(i + 6, j + 9, k + 4), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 9, k + 5), slab(ACBlocks.darkstone_brick_slab, 13), 2);
		setBlock(world, new BlockPos(i + 6, j + 9, k + 6), state(ACBlocks.glowing_darkstone_bricks), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 9, k + 7), slab(ACBlocks.darkstone_brick_slab, 13), 2);
		setBlock(world, new BlockPos(i + 6, j + 9, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 10, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 6, j + 10, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 6, j + 10, k + 6), state(ACBlocks.darkstone), 2);
		setBlock(world, new BlockPos(i + 6, j + 10, k + 7), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 10, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 11, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 6, j + 11, k + 6), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 6, j + 12, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 6, j + 11, k + 7), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
			setBlock(world, new BlockPos(i + 7, j + 0, k + 0), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 0, k + 11), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 0, k + 12), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 1, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 3, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 4, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 4, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 4, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 4), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 8), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 7, j + 5, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 6, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 7, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 8, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 8, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 8, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 8, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 8, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 7, j + 8, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 8, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 7, j + 9, k + 4), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 9, k + 5), slab(ACBlocks.darkstone_brick_slab, 13), 2);
			setBlock(world, new BlockPos(i + 7, j + 9, k + 6), slab(ACBlocks.darkstone_brick_slab, 13), 2);
			setBlock(world, new BlockPos(i + 7, j + 9, k + 7), slab(ACBlocks.darkstone_brick_slab, 13), 2);
		}
		setBlock(world, new BlockPos(i + 7, j + 9, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 7, j + 10, k + 4), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 7, j + 10, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 10, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 7, j + 10, k + 7), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 11, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 7, j + 11, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 8, j + 0, k + 0), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 7, j + 10, k + 8), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
			setBlock(world, new BlockPos(i + 7, j + 11, k + 6), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
		}
		setBlock(world, new BlockPos(i + 8, j + 0, k + 1), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 0, k + 11), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 8, j + 0, k + 12), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 1, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 10), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 3), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 9), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 3, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 8, j + 4, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 4, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 8, j + 4, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 4), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 5), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 6), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 7), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 8), random.nextBoolean() ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.BOOKSHELF.defaultBlockState(), 2);
		setBlock(world, new BlockPos(i + 8, j + 5, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 6, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 8, j + 8, k + 3), stairs(ACBlocks.darkstone_brick_stairs, 2), 2);
		setBlock(world, new BlockPos(i + 8, j + 8, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 8, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 8, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 8, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 8, k + 8), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 8, j + 8, k + 9), stairs(ACBlocks.darkstone_brick_stairs, 3), 2);
		setBlock(world, new BlockPos(i + 8, j + 9, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 9, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 9, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 9, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 8, j + 9, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 8, j + 10, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 8, j + 10, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 8, j + 10, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 0, k + 1), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 8, j + 10, k + 5), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
			setBlock(world, new BlockPos(i + 8, j + 10, k + 7), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
		}
		setBlock(world, new BlockPos(i + 9, j + 0, k + 2), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 0, k + 10), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 9, j + 0, k + 11), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 1, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 9), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 4), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 5), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 6), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 7), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 8), AIR, 2);
		setBlock(world, new BlockPos(i + 9, j + 3, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 9, j + 4, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 9, j + 4, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 4, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 4, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 4, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 4, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 9, j + 4, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 5, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 9, j + 5, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 5, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 5, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 5, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 5, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 9, j + 5, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 6, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 9, j + 6, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 6, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 6, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 6, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 6, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 9, j + 6, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 7, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 9, j + 7, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 7, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 7, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 7, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 9, j + 7, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 9, j + 7, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 10, j + 0, k + 2), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		if(true) {
			setBlock(world, new BlockPos(i + 9, j + 8, k + 4), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 6), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
			setBlock(world, new BlockPos(i + 9, j + 8, k + 8), stairs(ACBlocks.darkstone_brick_stairs, 1), 2);
		}
		setBlock(world, new BlockPos(i + 10, j + 0, k + 3), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 0, k + 9), getBrick(random), 2);
		if(true)
			setBlock(world, new BlockPos(i + 10, j + 0, k + 10), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		setBlock(world, new BlockPos(i + 10, j + 1, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 1, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 1, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 1, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 1, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 2, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 2, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 2, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 2, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 2, k + 8), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 3, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 3, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 3, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 3, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 10, j + 3, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 10, j + 4, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 10, j + 4, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 10, j + 4, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 10, j + 4, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 10, j + 4, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 11, j + 0, k + 3), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 11, j + 0, k + 4), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 11, j + 0, k + 5), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 11, j + 0, k + 6), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 11, j + 0, k + 7), getBrick(random), 2);
		setBlock(world, new BlockPos(i + 11, j + 0, k + 8), getBrick(random), 2);
		if(true) {
			setBlock(world, new BlockPos(i + 11, j + 0, k + 9), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 12, j + 0, k + 4), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 12, j + 0, k + 5), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 12, j + 0, k + 6), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 12, j + 0, k + 7), slab(ACBlocks.darkstone_brick_slab, 5), 2);
			setBlock(world, new BlockPos(i + 12, j + 0, k + 8), slab(ACBlocks.darkstone_brick_slab, 5), 2);
		}
		setBlock(world, new BlockPos(i + 3, j + 2, k + 6), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 1), 2);
		setBlock(world, new BlockPos(i + 4, j + 2, k + 9), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 4), 2);
		setBlock(world, new BlockPos(i + 4, j + 7, k + 6), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 1), 2);
		setBlock(world, new BlockPos(i + 5, j + 2, k + 3), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 3), 2);
		setBlock(world, new BlockPos(i + 6, j + 1, k + 7), ladder(2), 2);
		setBlock(world, new BlockPos(i + 6, j + 2, k + 7), ladder(2), 2);
		setBlock(world, new BlockPos(i + 6, j + 3, k + 7), ladder(2), 2);
		setBlock(world, new BlockPos(i + 6, j + 4, k + 7), ladder(2), 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 4), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 3), 2);
		setBlock(world, new BlockPos(i + 6, j + 7, k + 8), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 4), 2);
		setBlock(world, new BlockPos(i + 7, j + 2, k + 3), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 3), 2);
		setBlock(world, new BlockPos(i + 8, j + 2, k + 9), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 4), 2);
		setBlock(world, new BlockPos(i + 8, j + 7, k + 6), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 2), 2);
		setBlock(world, new BlockPos(i + 9, j + 2, k + 6), torch(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, 2), 2);

		return true;
	}
}
