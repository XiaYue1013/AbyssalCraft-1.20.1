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
import com.shinoow.abyssalcraft.api.event.ACEvents;
import com.shinoow.abyssalcraft.api.item.ItemEngraving;
import com.shinoow.abyssalcraft.api.recipe.EngraverRecipes;
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
 * Engraver menu
 */
public class ContainerEngraver extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;

	public ContainerEngraver(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(3), new SimpleContainerData(1));
	}

	public ContainerEngraver(int id, Inventory inv, Container container, ContainerData data) {
		super(ACMenus.ENGRAVER.get(), id);
		this.container = container;
		this.data = data;
		checkContainerSize(container, 3);
		addSlot(new Slot(container, 0, 56, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return APIUtils.isCoin(stack);
			}
		});
		addSlot(new Slot(container, 1, 56, 53) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof ItemEngraving;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		addSlot(new SlotXPOutput(inv.player, container, 2, 116, 35, EngraverRecipes.instance()::getExperience, ACEvents.ItemEngravedEvent::new));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));

		addDataSlots(data);
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	public int getProgressScaled(int pixels) {
		return data.get(0) * pixels / 200;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index == 2)
			{
				if (!moveItemStackTo(itemstack1, 3, 39, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			}
			else if (index != 1 && index != 0)
			{
				if (itemstack1.getItem() instanceof ItemEngraving)
				{
					if (!moveItemStackTo(itemstack1, 1, 2, false))
						return ItemStack.EMPTY;
				}
				else if (APIUtils.isCoin(itemstack1))
				{
					if (!moveItemStackTo(itemstack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
				else if (index >= 3 && index < 30)
				{
					if (!moveItemStackTo(itemstack1, 30, 39, false))
						return ItemStack.EMPTY;
				}
				else if (index >= 30 && index < 39 && !moveItemStackTo(itemstack1, 3, 30, false))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(itemstack1, 3, 39, false))
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
}
