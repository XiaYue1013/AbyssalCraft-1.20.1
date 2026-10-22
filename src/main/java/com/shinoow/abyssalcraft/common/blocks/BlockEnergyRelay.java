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
 *     1.20.1 port - BlockTieredEnergyRelay merged in (tier = constructor parameter, facing always a block state property)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyRelay;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Moves PE from the block behind it to the container it faces. Placed pointing away from the block it was placed against,
 * right-clicking cycles the facing.
 */
public class BlockEnergyRelay extends BlockPE<TileEntityEnergyRelay> {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	private static final VoxelShape EAST = Block.box(0, 1.6, 1.6, 9.6, 14.4, 14.4), WEST = Block.box(6.4, 1.6, 1.6, 16, 14.4, 14.4),
			SOUTH = Block.box(1.6, 1.6, 0, 14.4, 14.4, 9.6), NORTH = Block.box(1.6, 1.6, 6.4, 14.4, 14.4, 16),
			UP = Block.box(1.6, 0, 1.6, 14.4, 9.6, 14.4), DOWN = Block.box(1.6, 6.4, 1.6, 14.4, 16, 14.4);

	public BlockEnergyRelay(Properties properties, int tier) {
		super(properties, tier, UP, TileEntityEnergyRelay::new, ACBlockEntities.ENERGY_RELAY, TileEntityEnergyRelay::serverTick);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch(state.getValue(FACING)) {
		case EAST -> EAST;
		case WEST -> WEST;
		case SOUTH -> SOUTH;
		case NORTH -> NORTH;
		case UP -> UP;
		case DOWN -> DOWN;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(!level.isClientSide)
			level.setBlock(pos, state.cycle(FACING), 3);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
