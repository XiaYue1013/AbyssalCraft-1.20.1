/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - base for the block-by-block structures of 1.12.2 (WorldGenerator subclasses), so their code could
 *     be translated almost line by line. They're small enough (at most 13 blocks from their origin) to be placed as
 *     features.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.structure;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public abstract class LegacyStructure {

	protected static final BlockState AIR = Blocks.AIR.defaultBlockState();

	/** @param pos usually the surface block the structure stands on */
	public abstract boolean generate(WorldGenLevel world, RandomSource rand, BlockPos pos);

	protected void setBlock(WorldGenLevel world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, Block.UPDATE_CLIENTS);
	}

	protected void setBlock(WorldGenLevel world, BlockPos pos, BlockState state, int flags) {
		setBlock(world, pos, state);
	}

	protected void setBlockAndNotifyAdequately(WorldGenLevel world, BlockPos pos, BlockState state) {
		setBlock(world, pos, state);
	}

	protected void setAir(WorldGenLevel world, BlockPos pos) {
		setBlock(world, pos, AIR);
	}

	protected static BlockState state(Supplier<? extends Block> block) {
		return block.get().defaultBlockState();
	}

	/** 1.12.2 stair metadata: bits 0-1 facing (east, west, south, north), bit 2 upside down */
	protected static BlockState stairs(Supplier<? extends Block> block, int meta) {
		Direction facing = Direction.from3DDataValue(5 - (meta & 3));
		return state(block).setValue(StairBlock.FACING, facing).setValue(StairBlock.HALF, (meta & 4) > 0 ? Half.TOP : Half.BOTTOM);
	}

	/** 1.12.2 slab metadata: bit 3 top half */
	protected static BlockState slab(Supplier<? extends Block> block, int meta) {
		return state(block).setValue(SlabBlock.TYPE, (meta & 8) != 0 ? SlabType.TOP : SlabType.BOTTOM);
	}

	/** 1.12.2 torch metadata: 1 east, 2 west, 3 south, 4 north (attached to the opposite side), 5 standing */
	protected static BlockState torch(Block standing, Block wall, int meta) {
		return switch(meta) {
		case 1 -> wall.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
		case 2 -> wall.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
		case 3 -> wall.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH);
		case 4 -> wall.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH);
		default -> standing.defaultBlockState();
		};
	}

	protected static BlockState ladder(int meta) {
		return Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.from3DDataValue(Math.max(2, meta)));
	}

	protected static BlockState chest(int meta) {
		return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.from3DDataValue(Math.max(2, meta)));
	}

	protected static void setLootTable(WorldGenLevel world, BlockPos pos, ResourceLocation lootTable, RandomSource rand) {
		RandomizableContainerBlockEntity.setLootTable(world, rand, pos, lootTable);
	}
}
