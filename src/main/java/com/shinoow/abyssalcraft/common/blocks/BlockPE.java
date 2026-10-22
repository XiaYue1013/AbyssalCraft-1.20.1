/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - shared base of the Potential Energy blocks. The 1.12.2 tiered blocks used the metadata for the
 *     dimension tier; every tier is its own block now and the tier is a constructor parameter.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.lib.util.blocks.SingletonInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Potential Energy block with a block entity
 * @param <T> Block entity type
 */
public abstract class BlockPE<T extends BlockEntity> extends BaseEntityBlock {

	/** Untiered block (the original pedestals, altars, collectors etc.) */
	public static final int BASE = -1;
	public static final int OVERWORLD = 0, ABYSSAL_WASTELAND = 1, DREADLANDS = 2, OMOTHOL = 3;
	public static final String[] TIER_NAMES = {"overworld", "abyssal_wasteland", "dreadlands", "omothol"};

	private final int tier;
	private final BiFunction<BlockPos, BlockState, T> factory;
	private final Supplier<? extends BlockEntityType<T>> type;
	@Nullable
	private final BlockEntityTicker<T> serverTicker;
	private final VoxelShape shape;

	protected BlockPE(Properties properties, int tier, VoxelShape shape, BiFunction<BlockPos, BlockState, T> factory,
			Supplier<? extends BlockEntityType<T>> type, @Nullable BlockEntityTicker<T> serverTicker) {
		super(properties.noOcclusion());
		this.tier = tier;
		this.shape = shape;
		this.factory = factory;
		this.type = type;
		this.serverTicker = serverTicker;
	}

	/**
	 * @return The dimension tier (0 = Overworld, 1 = Abyssal Wasteland, 2 = Dreadlands, 3 = Omothol), or {@link #BASE}
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * Tier of the block at a block entity's position
	 */
	public static int getTier(BlockState state) {
		return state.getBlock() instanceof BlockPE<?> pe ? pe.getTier() : state.getBlock() instanceof BlockEnergyContainer c ? c.getTier() : BASE;
	}

	/**
	 * 1.12.2 tier multipliers of the pedestals, sacrificial altars and collectors
	 */
	public static int tieredCapacity(int base, int tier) {
		return switch(tier) {
		case OVERWORLD -> (int) (base * 1.5);
		case ABYSSAL_WASTELAND -> base * 2;
		case DREADLANDS -> (int) (base * 2.5);
		case OMOTHOL -> base * 3;
		default -> base;
		};
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
		return level.isClientSide || serverTicker == null ? null : createTickerHelper(blockEntityType, type.get(), serverTicker);
	}

	/** Restores the PE stored in the item */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		restoreEnergy(level, pos, stack);
	}

	/** Restores the PE stored in a PE block item ("PotEnergy") into the placed block */
	public static void restoreEnergy(Level level, BlockPos pos, ItemStack stack) {
		if(stack.hasTag() && stack.getTag().contains("PotEnergy") && level.getBlockEntity(pos) instanceof IEnergyContainer container)
			container.addEnergy(stack.getTag().getFloat("PotEnergy"));
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.is(newState.getBlock()))
			SingletonInventoryUtil.dropItem(level, pos);
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
