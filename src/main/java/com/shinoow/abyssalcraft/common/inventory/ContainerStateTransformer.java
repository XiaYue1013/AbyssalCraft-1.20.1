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
 *     1.20.1 port - the mode button is a menu button (validated on the server), progress and mode are menu data
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityStateTransformer;
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
 * State Transformer menu: tablet slot, 7x7 storage, player inventory
 */
public class ContainerStateTransformer extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;

	public ContainerStateTransformer(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(50), new SimpleContainerData(2));
	}

	public ContainerStateTransformer(int id, Inventory inv, Container container, ContainerData data) {
		super(ACMenus.STATE_TRANSFORMER.get(), id);
		this.container = container;
		this.data = data;
		checkContainerSize(container, 50);
		checkContainerDataCount(data, 2);
		addSlot(new Slot(container, 0, 8, 71) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof ItemStoneTablet;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++)
				addSlot(new Slot(container, j + i * 7 + 1, 44 + j * 18, 17 + i * 18) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return !APIUtils.hasAnInventory(stack);
					}
				});
		for(int i = 0; i < 3; i++)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 72));
		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142 + 72));
		addDataSlots(data);
	}

	public int getProcessingTime() {
		return data.get(0);
	}

	public int getMode() {
		return data.get(1);
	}

	/** Button 0 toggles insert/extract */
	@Override
	public boolean clickMenuButton(Player player, int id) {
		return id == 0 && container instanceof TileEntityStateTransformer tile && tile.toggleMode();
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

			if (index < 50)
			{
				if (!moveItemStackTo(itemstack1, 50, 86, true))
					return ItemStack.EMPTY;
			}
			else if(itemstack1.getItem() instanceof ItemStoneTablet){
				if (!moveItemStackTo(itemstack1, 0, 1, false))
					return ItemStack.EMPTY;
			} else if(APIUtils.hasAnInventory(itemstack1) || !moveItemStackTo(itemstack1, 1, 50, false))
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
