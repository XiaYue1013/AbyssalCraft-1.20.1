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
 *     1.20.1 port - the PE amount is synced through menu data (two 16 bit halves) instead of WindowPropertyMessage
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
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
 * Energy Container menu
 */
public class ContainerEnergyContainer extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;

	public ContainerEnergyContainer(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(2), new SimpleContainerData(4));
	}

	public ContainerEnergyContainer(int id, Inventory inv, Container container, ContainerData data) {
		super(ACMenus.ENERGY_CONTAINER.get(), id);
		this.container = container;
		this.data = data;
		checkContainerSize(container, 2);
		checkContainerDataCount(data, 4);
		addSlot(new SlotEnergyContainer(container, 0, 44, 38));
		addSlot(new SlotEnergyContainer(container, 1, 116, 38));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));

		addDataSlots(data);
	}

	public int getContainedEnergy() {
		return data.get(0) & 0xFFFF | (data.get(1) & 0xFFFF) << 16;
	}

	public int getMaxEnergy() {
		return data.get(2) & 0xFFFF | (data.get(3) & 0xFFFF) << 16;
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

			if (index == 0 || index == 1)
			{
				if (!moveItemStackTo(itemstack1, 2, 38, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(itemstack1, itemstack);
			}
			else if(itemstack1.getItem() instanceof IEnergyContainerItem){
				if (!moveItemStackTo(itemstack1, 0, 2, false))
					return ItemStack.EMPTY;
			} else if (index < 29){
				if (!moveItemStackTo(itemstack1, 29, 38, false))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(itemstack1, 2, 29, false))
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

	/** Only accepts PE items */
	public static class SlotEnergyContainer extends Slot {

		public SlotEnergyContainer(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return stack.getItem() instanceof IEnergyContainerItem;
		}
	}
}
