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
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySingleMobSpawner;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Invisible block placed by structures (Dreadguards, Cha'garoth, J'zahar and his minions) that turns into its mob as
 * soon as a player who isn't in creative or spectator mode comes close. Drops nothing.<br>
 * (1.12.2 had one block entity class per mob; they only differed in the mob, the range and J'zahar's height check)
 */
public class BlockSingleMobSpawner extends BaseEntityBlock {

	private final String entity;
	private final int range;
	private final boolean playerNotBelow;

	/**
	 * @param entity AbyssalCraft entity name (nothing spawns until that entity type is registered)
	 * @param range Activation range
	 * @param playerNotBelow If the player has to be at most one block below the spawner
	 */
	public BlockSingleMobSpawner(Properties properties, String entity, int range, boolean playerNotBelow) {
		super(properties);
		this.entity = entity;
		this.range = range;
		this.playerNotBelow = playerNotBelow;
	}

	public String getEntity() {
		return entity;
	}

	public int getRange() {
		return range;
	}

	public boolean playerNotBelow() {
		return playerNotBelow;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntitySingleMobSpawner(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, ACBlockEntities.SINGLE_MOB_SPAWNER.get(), TileEntitySingleMobSpawner::serverTick);
	}
}
