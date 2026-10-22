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

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityRendingPedestal;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rending Pedestal (keeps its PE when broken)
 */
public class BlockRendingPedestal extends BlockMachine<TileEntityRendingPedestal> {

	public BlockRendingPedestal(Properties properties) {
		super(properties, TileEntityRendingPedestal::new, ACBlockEntities.RENDING_PEDESTAL, TileEntityRendingPedestal::serverTick, null,
				Block.box(4, 0, 4, 12, 16, 12), false);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockPE.restoreEnergy(level, pos, stack);
	}
}
