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

import com.shinoow.abyssalcraft.common.items.ItemStoneTablet;
import com.shinoow.abyssalcraft.init.ACMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Energy Depositioner menu: filled tablet in, emptied tablet out
 */
public class ContainerEnergyDepositioner extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;

	public ContainerEnergyDepositioner(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(2), new SimpleContainerData(2));
	}

	public ContainerEnergyDepositioner(int id, Inventory inv, Container container, ContainerData data) {
		super(ACMenus.ENERGY_DEPOSITIONER.get(), id);
		this.container = container;
		this.data = data;
		checkContainerSize(container, 2);
		checkContainerDataCount(data, 2);
		addSlot(new Slot(container, 0, 44, 38) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof ItemStoneTablet;
			}
		});
		addSlot(new Slot(container, 1, 116, 38) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});
		for(int i = 0; i < 3; i++)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));
		addDataSlots(data);
	}

	public int getProcessingTime() {
		return data.get(0);
	}

	public int getEnergy() {
		return data.get(1) & 0xFFFF;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
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

			if (index < 2)
			{
				if (!moveItemStackTo(itemstack1, 2, 38, true))
					return ItemStack.EMPTY;
			}
			else if(itemstack1.getItem() instanceof ItemStoneTablet){
				if (!moveItemStackTo(itemstack1, 0, 1, false))
					return ItemStack.EMPTY;
			} else if (index < 29){
				if (!moveItemStackTo(itemstack1, 29, 38, false))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(itemstack1, 2, 29, false))
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
