/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - shared block for block entities with a menu
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityACInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

/**
 * Block with a block entity that opens a menu when used and drops its inventory when broken
 * (Engraver, Materializer, Crate etc.). Optionally horizontally facing.
 */
public class BlockMachine<T extends BlockEntity> extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	private final BiFunction<BlockPos, BlockState, T> factory;
	private final Supplier<? extends BlockEntityType<T>> type;
	@Nullable
	private final BlockEntityTicker<T> serverTicker, clientTicker;
	private final VoxelShape shape;
	private final boolean facing;

	public BlockMachine(Properties properties, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends BlockEntityType<T>> type,
			@Nullable BlockEntityTicker<T> serverTicker, @Nullable BlockEntityTicker<T> clientTicker, @Nullable VoxelShape shape, boolean facing) {
		super(shape != null ? properties.noOcclusion() : properties);
		this.factory = factory;
		this.type = type;
		this.serverTicker = serverTicker;
		this.clientTicker = clientTicker;
		this.shape = shape != null ? shape : Shapes.block();
		this.facing = facing;
		if(facing)
			registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, facing ? context.getHorizontalDirection().getOpposite() : Direction.NORTH);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return factory.apply(pos, state);
	}

	@Nullable
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state, BlockEntityType<E> blockEntityType) {
		BlockEntityTicker<T> ticker = level.isClientSide ? clientTicker : serverTicker;
		return ticker == null ? null : createTickerHelper(blockEntityType, type.get(), ticker);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider provider)
			NetworkHooks.openScreen((ServerPlayer) player, provider, pos);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof TileEntityACInventory tile)
			tile.setCustomName(stack.getHoverName());
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof net.minecraft.world.Container container) {
				Containers.dropContents(level, pos, container);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return facing ? state.setValue(FACING, rotation.rotate(state.getValue(FACING))) : state;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return facing ? state.rotate(mirror.getRotation(state.getValue(FACING))) : state;
	}
}
