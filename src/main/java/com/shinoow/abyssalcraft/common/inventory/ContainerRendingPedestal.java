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

import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.init.ACMenus;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;

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
 * Rending Pedestal menu: PE item, staff, four outputs
 */
public class ContainerRendingPedestal extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;

	public ContainerRendingPedestal(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(6), new SimpleContainerData(5));
	}

	public ContainerRendingPedestal(int id, Inventory inv, Container container, ContainerData data) {
		super(ACMenus.RENDING_PEDESTAL.get(), id);
		this.container = container;
		this.data = data;
		checkContainerSize(container, 6);
		checkContainerDataCount(data, 5);
		addSlot(new ContainerEnergyContainer.SlotEnergyContainer(container, 0, 26, 52));
		addSlot(new Slot(container, 1, 26, 21) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof IStaffOfRending;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		int[] xs = {73, 94, 114, 135};
		for(int i = 0; i < 4; i++)
			addSlot(new Slot(container, 2 + i, xs[i], 52) {
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

	/** 0 = shadow, 1 = abyssal, 2 = dread, 3 = omothol */
	public int getEnergy(int type) {
		return data.get(type) & 0xFFFF;
	}

	public int getContainedEnergy() {
		return data.get(4) & 0xFFFF;
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

			if (index < 6)
			{
				if (!moveItemStackTo(itemstack1, 6, 42, true))
					return ItemStack.EMPTY;
			}
			else if(itemstack1.getItem() instanceof IStaffOfRending){
				if (!moveItemStackTo(itemstack1, 1, 2, false))
					return ItemStack.EMPTY;
			} else if(itemstack1.getItem() instanceof IEnergyContainerItem){
				if (!moveItemStackTo(itemstack1, 0, 1, false))
					return ItemStack.EMPTY;
			} else if (index < 33){
				if (!moveItemStackTo(itemstack1, 33, 42, false))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(itemstack1, 6, 33, false))
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
