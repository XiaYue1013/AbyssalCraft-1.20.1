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

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The five filter slots of a Configurator (stored in its NBT, "Items" like 1.12.2 ItemStackHelper)
 */
public class InventoryConfigurator extends SimpleContainer {

	private final ItemStack configurator;
	private final InteractionHand hand;

	public InventoryConfigurator(ItemStack configurator, InteractionHand hand) {
		super(5);
		this.configurator = configurator;
		this.hand = hand;
		NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(configurator.getOrCreateTag(), items);
		for(int i = 0; i < 5; i++)
			setItem(i, items.get(i));
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return !APIUtils.hasAnInventory(stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return player.getItemInHand(hand) == configurator;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
		for(int i = 0; i < 5; i++)
			items.set(i, getItem(i));
		ContainerHelper.saveAllItems(configurator.getOrCreateTag(), items);
	}

	/** 0 = filter by subtypes, 1 = filter by NBT */
	public boolean getFlag(int id) {
		CompoundTag tag = configurator.getOrCreateTag();
		return tag.getBoolean(id == 0 ? "FilterSubtype" : "FilterNBT");
	}

	public void toggleFlag(int id) {
		configurator.getOrCreateTag().putBoolean(id == 0 ? "FilterSubtype" : "FilterNBT", !getFlag(id));
	}
}
