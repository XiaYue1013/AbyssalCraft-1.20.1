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
 *     1.20.1 port - TileEntityTieredEnergyPedestal merged in (capacity from the block's tier)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.block.ISingletonInventory;
import com.shinoow.abyssalcraft.api.energy.IEnergyCollector;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Collects PE from nearby statues and charges the item placed on it
 */
public class TileEntityEnergyPedestal extends TileEntityPEStorage implements IEnergyCollector, ISingletonInventory {

	private ItemStack item = ItemStack.EMPTY;

	public TileEntityEnergyPedestal(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENERGY_PEDESTAL.get(), pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		item = ItemStack.of(tag.getCompound("Item"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag nbtItem = new CompoundTag();
		if(!item.isEmpty())
			item.save(nbtItem);
		tag.put("Item", nbtItem);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityEnergyPedestal tile) {
		ItemStack item = tile.item;
		if(!item.isEmpty() && item.getItem() instanceof IEnergyContainerItem container)
			if(container.canAcceptPE(item) && tile.canTransferPE())
				container.addEnergy(item, tile.consumeEnergy(20));
	}

	@Override
	public ItemStack getItem(){
		return item;
	}

	@Override
	public void setItem(ItemStack item){
		this.item = item;
		markForUpdate();
	}

	@Override
	public int getMaxEnergy() {
		return BlockPE.tieredCapacity(5000, getTier());
	}
}
