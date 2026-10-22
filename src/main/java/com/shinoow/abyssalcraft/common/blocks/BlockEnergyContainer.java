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
 *     1.20.1 port - BlockTieredEnergyContainer merged in (tier = constructor parameter)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyContainer;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stores PE, with slots to drain/charge PE items
 */
public class BlockEnergyContainer extends BlockMachine<TileEntityEnergyContainer> {

	private final int tier;

	public BlockEnergyContainer(Properties properties, int tier) {
		super(properties, TileEntityEnergyContainer::new, ACBlockEntities.ENERGY_CONTAINER, TileEntityEnergyContainer::serverTick, null,
				Block.box(0, 0, 0, 16, 12, 16), false);
		this.tier = tier;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockPE.restoreEnergy(level, pos, stack);
	}

	/** @see BlockPE#getTier() */
	public int getTier() {
		return tier;
	}
}
