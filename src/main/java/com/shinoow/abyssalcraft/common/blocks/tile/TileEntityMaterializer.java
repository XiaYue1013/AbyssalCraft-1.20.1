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
 *     1.20.1 port - the 548 virtual output slots live in the menu instead of the block entity
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.common.inventory.ContainerMaterializer;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Materializer: turns the crystals stored in a Crystal Bag back into items.
 * Slot 0 holds the Crystal Bag, slot 1 a Necronomicon (only used for Clippy).
 */
public class TileEntityMaterializer extends TileEntityACInventory {

	public TileEntityMaterializer(BlockPos pos, BlockState state) {
		super(ACBlockEntities.MATERIALIZER.get(), pos, state, 2, "container.abyssalcraft.materializer");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerMaterializer(id, inventory, this);
	}
}
