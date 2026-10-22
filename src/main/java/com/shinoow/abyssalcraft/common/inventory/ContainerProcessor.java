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

import com.shinoow.abyssalcraft.api.recipe.CrystallizerRecipes;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrystallizer;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityProcessor;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityTransmutator;
import com.shinoow.abyssalcraft.init.ACMenus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import com.shinoow.abyssalcraft.api.event.ACEvents;

/**
 * Menu of the Crystallizer (2 outputs) and Transmutator (1 output), same layout as ContainerCrystallizer/ContainerTransmutator
 */
public class ContainerProcessor extends AbstractContainerMenu {

	private final Container container;
	private final ContainerData data;
	private final boolean crystallizer;
	private final int machineSlots;

	public static ContainerProcessor crystallizer(int id, Inventory inv, Container container, ContainerData data) {
		return new ContainerProcessor(ACMenus.CRYSTALLIZER.get(), id, inv, container, data, true);
	}

	public static ContainerProcessor transmutator(int id, Inventory inv, Container container, ContainerData data) {
		return new ContainerProcessor(ACMenus.TRANSMUTATOR.get(), id, inv, container, data, false);
	}

	/** Client side constructors */
	public static ContainerProcessor crystallizerClient(int id, Inventory inv) {
		return crystallizer(id, inv, new SimpleContainer(4), new SimpleContainerData(3));
	}

	public static ContainerProcessor transmutatorClient(int id, Inventory inv) {
		return transmutator(id, inv, new SimpleContainer(3), new SimpleContainerData(3));
	}

	private ContainerProcessor(MenuType<?> type, int id, Inventory inv, Container container, ContainerData data, boolean crystallizer) {
		super(type, id);
		this.container = container;
		this.data = data;
		this.crystallizer = crystallizer;
		machineSlots = crystallizer ? 4 : 3;
		checkContainerSize(container, machineSlots);
		addSlot(new Slot(container, 0, 56, 17));
		addSlot(new Slot(container, 1, 56, 53) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return isFuel(stack);
			}
		});
		addSlot(output(inv, container, 2, 116, crystallizer));
		if(crystallizer)
			addSlot(output(inv, container, 3, 132, true));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));

		addDataSlots(data);
	}

	private static SlotXPOutput output(Inventory inv, Container container, int index, int x, boolean crystallizer) {
		return crystallizer ? new SlotXPOutput(inv.player, container, index, x, 35, CrystallizerRecipes.instance()::getExperience, ACEvents.ItemCrystallizedEvent::new)
				: new SlotXPOutput(inv.player, container, index, x, 35, TransmutatorRecipes.instance()::getExperience, ACEvents.ItemTransmutedEvent::new);
	}

	private boolean isFuel(ItemStack stack) {
		return crystallizer ? TileEntityCrystallizer.getCrystallizationTime(stack) > 0 : TileEntityTransmutator.getItemBurnTime(stack) > 0;
	}

	private boolean hasRecipe(ItemStack stack) {
		return crystallizer ? !CrystallizerRecipes.instance().getCrystallizationResult(stack)[0].isEmpty()
				: !TransmutatorRecipes.instance().getTransmutationResult(stack).isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		int invStart = machineSlots, invEnd = machineSlots + 27, hotbarEnd = machineSlots + 36;

		if (slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index >= 2 && index < machineSlots)
			{
				if (!moveItemStackTo(itemstack1, invStart, hotbarEnd, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			}
			else if (index != 1 && index != 0)
			{
				if (hasRecipe(itemstack1))
				{
					if (!moveItemStackTo(itemstack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
				else if (isFuel(itemstack1))
				{
					if (!moveItemStackTo(itemstack1, 1, 2, false))
						return ItemStack.EMPTY;
				}
				else if (index >= invStart && index < invEnd)
				{
					if (!moveItemStackTo(itemstack1, invEnd, hotbarEnd, false))
						return ItemStack.EMPTY;
				}
				else if (index >= invEnd && index < hotbarEnd && !moveItemStackTo(itemstack1, invStart, invEnd, false))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(itemstack1, invStart, hotbarEnd, false))
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

	public boolean isCrystallizer() {
		return crystallizer;
	}

	/** Fuel gauge height (0..pixels) */
	public int getFuelScaled(int pixels) {
		int current = data.get(1);
		if (current == 0)
			current = TileEntityProcessor.PROCESS_TIME;
		return data.get(0) * pixels / current;
	}

	/** Progress arrow width (0..pixels) */
	public int getProgressScaled(int pixels) {
		return data.get(2) * pixels / TileEntityProcessor.PROCESS_TIME;
	}

	public boolean isBurning() {
		return data.get(0) > 0;
	}
}
