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
import com.shinoow.abyssalcraft.init.ACMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Crystal Bag menu
 */
public class ContainerCrystalBag extends AbstractContainerMenu {

	private final Container inventory;
	private final int rows;

	/** Client side */
	public static ContainerCrystalBag client(int id, Inventory inv, FriendlyByteBuf buf) {
		buf.readEnum(InteractionHand.class);
		return new ContainerCrystalBag(id, inv, new SimpleContainer(buf.readVarInt()));
	}

	public ContainerCrystalBag(int id, Inventory inventoryPlayer, Container inventoryItem)
	{
		super(ACMenus.CRYSTAL_BAG.get(), id);
		inventory = inventoryItem;
		rows = inventoryItem.getContainerSize() / 9;
		int i = (rows - 4) * 18;

		for (int j = 0; j < rows; ++j)
			for (int k = 0; k < 9; ++k)
				addSlot(new Slot(inventoryItem, k + j * 9, 8 + k * 18, 17 + j * 18) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return APIUtils.isCrystal(stack);
					}
				});

		for (int j = 0; j < 3; ++j)
			for (int k = 0; k < 9; ++k)
				addSlot(new Slot(inventoryPlayer, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));

		for (int j = 0; j < 9; ++j)
			addSlot(new Slot(inventoryPlayer, j, 8 + j * 18, 161 + i));
	}

	public int getRows() {
		return rows;
	}

	@Override
	public boolean stillValid(Player player) {
		return inventory.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index < rows * 9)
			{
				if (!moveItemStackTo(itemstack1, rows * 9, slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (APIUtils.isCrystal(itemstack1)) {
				if (!moveItemStackTo(itemstack1, 0, rows * 9, false))
					return ItemStack.EMPTY;
			} else
				return ItemStack.EMPTY;

			if (itemstack1.isEmpty())
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void clicked(int slot, int button, ClickType clickType, Player player) {
		// the open bag can't be moved (that would duplicate/delete its contents)
		if (slot >= 0 && slot < slots.size() && getSlot(slot).getItem().getItem() instanceof ItemCrystalBag)
			return;
		if (clickType == ClickType.SWAP && player.getInventory().getItem(button).getItem() instanceof ItemCrystalBag)
			return;
		super.clicked(slot, button, clickType, player);
	}
}
