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
 *     1.20.1 port - the two filter flags are menu data, toggled with menu buttons on the server
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.common.items.ItemConfigurator;
import com.shinoow.abyssalcraft.init.ACMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Configurator filter menu
 */
public class ContainerConfigurator extends AbstractContainerMenu {

	private final Container inventory;
	private final ContainerData data;

	public ContainerConfigurator(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(5), new SimpleContainerData(2));
	}

	public ContainerConfigurator(int id, Inventory inv, InventoryConfigurator configurator) {
		this(id, inv, configurator, new ContainerData() {
			@Override
			public int get(int index) {
				return configurator.getFlag(index) ? 1 : 0;
			}

			@Override
			public void set(int index, int value) {}

			@Override
			public int getCount() {
				return 2;
			}
		});
	}

	private ContainerConfigurator(int id, Inventory inv, Container inventory, ContainerData data)
	{
		super(ACMenus.CONFIGURATOR.get(), id);
		this.inventory = inventory;
		this.data = data;
		for(int i = 0; i < 5; i++)
			addSlot(new Slot(inventory, i, 44 + i * 18, 17) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return !APIUtils.hasAnInventory(stack);
				}
			});
		for(int i = 0; i < 3; i++)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));
		addDataSlots(data);
	}

	/** 0 = filter by subtypes, 1 = filter by NBT */
	public boolean getFlag(int id) {
		return data.get(id) == 1;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if((id == 0 || id == 1) && inventory instanceof InventoryConfigurator configurator) {
			configurator.toggleFlag(id);
			return true;
		}
		return false;
	}

	@Override
	public boolean stillValid(Player player) {
		return inventory.stillValid(player);
	}

	/** The configurator itself can't be moved while its filter is open */
	@Override
	public void clicked(int slot, int button, ClickType type, Player player) {
		if(slot >= 0 && slot < slots.size() && getSlot(slot).getItem().getItem() instanceof ItemConfigurator)
			return;
		if(type == ClickType.SWAP)
			return;
		super.clicked(slot, button, type, player);
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
			if (index < 5)
			{
				if (!moveItemStackTo(itemstack1, 5, 41, true))
					return ItemStack.EMPTY;
			}
			else if(APIUtils.hasAnInventory(itemstack1) || !moveItemStackTo(itemstack1, 0, 5, false))
				return ItemStack.EMPTY;
			if (itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
}
