/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the block conversions shared by the Ritual of Corruption and the Energy Depositioner
 *     (both turned vanilla building blocks into their Darklands counterparts), mapped onto the flattened blocks
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.ritual.BiomeRitualHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DarklandsCorruption {

	private static Map<Block, Block> conversions;

	private static Map<Block, Block> conversions() {
		if(conversions != null) return conversions;
		Map<Block, Block> map = new HashMap<>();
		for(Block stone : new Block[] {Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE})
			map.put(stone, ACBlocks.darkstone.get());
		for(Block leaves : new Block[] {Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES})
			map.put(leaves, ACBlocks.darklands_oak_leaves.get());
		for(Block log : new Block[] {Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG})
			map.put(log, ACBlocks.darklands_oak_wood.get());
		map.put(Blocks.COBBLESTONE, ACBlocks.darkstone_cobblestone.get());
		map.put(Blocks.STONE_BRICKS, ACBlocks.darkstone_bricks.get());
		map.put(Blocks.MOSSY_STONE_BRICKS, ACBlocks.darkstone_bricks.get());
		map.put(Blocks.CHISELED_STONE_BRICKS, ACBlocks.chiseled_darkstone_bricks.get());
		map.put(Blocks.CRACKED_STONE_BRICKS, ACBlocks.cracked_darkstone_bricks.get());
		map.put(Blocks.COBBLESTONE_WALL, ACBlocks.darkstone_cobblestone_wall.get());
		map.put(ACBlocks.ritual_altar_stone.get(), ACBlocks.ritual_altar_darkstone.get());
		map.put(ACBlocks.ritual_pedestal_stone.get(), ACBlocks.ritual_pedestal_darkstone.get());
		map.put(Blocks.STONE_BRICK_SLAB, ACBlocks.darkstone_brick_slab.get());
		map.put(Blocks.COBBLESTONE_SLAB, ACBlocks.darkstone_cobblestone_slab.get());
		map.put(Blocks.SMOOTH_STONE_SLAB, ACBlocks.darkstone_slab.get());
		map.put(Blocks.STONE_BRICK_STAIRS, ACBlocks.darkstone_brick_stairs.get());
		map.put(Blocks.COBBLESTONE_STAIRS, ACBlocks.darkstone_cobblestone_stairs.get());
		for(Block planks : new Block[] {Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS})
			map.put(planks, ACBlocks.darklands_oak_planks.get());
		for(Block slab : new Block[] {Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB})
			map.put(slab, ACBlocks.darklands_oak_slab.get());
		map.put(Blocks.OAK_STAIRS, ACBlocks.darklands_oak_stairs.get());
		map.put(Blocks.OAK_FENCE, ACBlocks.darklands_oak_fence.get());
		return conversions = map;
	}

	/**
	 * The Darklands counterpart of a block, or null
	 * @param plainStone Whether or not plain stone is converted too (the Ritual of Corruption only converted the
	 * other stone variants, the Energy Depositioner every kind of stone)
	 */
	@Nullable
	public static BlockState corrupt(BlockState state, boolean plainStone, RandomSource random) {
		if(plainStone && state.is(Blocks.STONE))
			return ACBlocks.darkstone.get().defaultBlockState();
		Block result = conversions().get(state.getBlock());
		if(result == ACBlocks.darklands_oak_wood.get() && random.nextInt(10) == 0 && !plainStone)
			result = ACBlocks.darklands_oak_wood_2.get();
		return result != null ? BiomeRitualHelper.copyProperties(state, result) : null;
	}

	/** Corrupts a whole block column, from the bottom of the world to the top */
	public static void corruptColumn(Level world, BlockPos bottom, boolean plainStone) {
		for(BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(bottom.getX(), world.getMinBuildHeight(), bottom.getZ()); p.getY() < world.getMaxBuildHeight(); p.move(0, 1, 0)){
			BlockState state = world.getBlockState(p);
			if(state.isAir()) continue;
			BlockState result = corrupt(state, plainStone, world.random);
			if(result != null)
				world.setBlock(p, result, 2);
		}
	}
}
