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
package com.shinoow.abyssalcraft.common.inventory;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.common.items.ItemCrystalBag;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Inventory stored in a Crystal Bag's NBT ("ItemInventory" list with "Slot" entries, same layout as 1.12.2)
 */
public class InventoryCrystalBag extends SimpleContainer {

	private final ItemStack bag;
	private final InteractionHand hand;

	public InventoryCrystalBag(ItemStack bag, InteractionHand hand) {
		super(ItemCrystalBag.getInventorySize(bag));
		this.bag = bag;
		this.hand = hand;
		readFromNBT(bag.getOrCreateTag());
	}

	public ItemStack getBag() {
		return bag;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return APIUtils.isCrystal(stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return player.getItemInHand(hand) == bag;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		writeToNBT(bag.getOrCreateTag());
	}

	public void readFromNBT(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", Tag.TAG_COMPOUND);
		for (int i = 0; i < items.size(); ++i)
		{
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");
			if (slot >= 0 && slot < getContainerSize())
				setItem(slot, ItemStack.of(item));
		}
	}

	public void writeToNBT(CompoundTag tag) {
		ListTag items = new ListTag();
		for (int i = 0; i < getContainerSize(); ++i)
			if (!getItem(i).isEmpty())
			{
				CompoundTag item = new CompoundTag();
				item.putInt("Slot", i);
				getItem(i).save(item);
				items.add(item);
			}
		tag.put("ItemInventory", items);
	}
}
