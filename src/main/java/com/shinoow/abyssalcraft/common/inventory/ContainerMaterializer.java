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
 *     1.20.1 port - server authoritative: outputs are computed and consumed on the server, the client only scrolls
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import java.util.Collections;
import java.util.List;

import com.shinoow.abyssalcraft.api.event.ACEvents;
import com.shinoow.abyssalcraft.api.recipe.Materialization;
import com.shinoow.abyssalcraft.api.recipe.MaterializerRecipes;
import com.shinoow.abyssalcraft.common.items.ItemCrystalBag;
import com.shinoow.abyssalcraft.init.ACMenus;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * Materializer menu. Slot 0 Crystal Bag, slot 1 Necronomicon, 18 output slots (3 rows of 6, scrollable),
 * then the player inventory.
 */
public class ContainerMaterializer extends AbstractContainerMenu {

	public static final int OUTPUTS = 18;

	private final Container tile;
	private final Player player;
	private final SimpleContainer outputs = new SimpleContainer(OUTPUTS);
	private final DataSlot scrollRow = DataSlot.standalone();
	private final DataSlot resultCount = DataSlot.standalone();
	private List<ItemStack> results = Collections.emptyList();
	private ItemStack lastBag = ItemStack.EMPTY;
	private int lastRow = -1;

	public ContainerMaterializer(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(2));
	}

	public ContainerMaterializer(int id, Inventory inv, Container tile) {
		super(ACMenus.MATERIALIZER.get(), id);
		this.tile = tile;
		player = inv.player;
		checkContainerSize(tile, 2);

		addSlot(new Slot(tile, 0, 14, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof ItemCrystalBag;
			}
		});
		addSlot(new Slot(tile, 1, 14, 53) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ACTags.Items.NECRONOMICONS);
			}
		});

		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 6; ++j)
				addSlot(new OutputSlot(outputs, j + i * 6, 44 + j * 17, 17 + i * 17));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));

		addDataSlot(scrollRow);
		addDataSlot(resultCount);
	}

	@Override
	public boolean stillValid(Player player) {
		return tile.stillValid(player);
	}

	/**
	 * Recomputes the outputs on the server if the bag or the scroll position changed
	 */
	private void refresh(boolean force) {
		if(player.level().isClientSide) return;
		ItemStack bag = tile.getItem(0);
		if(force || lastRow != scrollRow.get() || !ItemStack.matches(bag, lastBag)) {
			lastBag = bag.copy();
			lastRow = scrollRow.get();
			results = bag.isEmpty() ? Collections.emptyList() : MaterializerRecipes.instance().getMaterializationResult(bag);
			resultCount.set(results.size());
			int start = scrollRow.get() * 6;
			for(int i = 0; i < OUTPUTS; i++)
				outputs.setItem(i, start + i < results.size() ? results.get(start + i).copy() : ItemStack.EMPTY);
		}
	}

	@Override
	public void broadcastChanges() {
		refresh(false);
		super.broadcastChanges();
	}

	/**
	 * Scrolling (the client sends the new top row as a menu button click)
	 */
	@Override
	public boolean clickMenuButton(Player player, int row) {
		int maxRow = Math.max(0, (resultCount.get() + 5) / 6 - 3);
		scrollRow.set(Math.max(0, Math.min(row, maxRow)));
		refresh(true);
		return true;
	}

	public int getScrollRow() {
		return scrollRow.get();
	}

	public int getMaxScrollRow() {
		return Math.max(0, (resultCount.get() + 5) / 6 - 3);
	}

	public boolean canScroll() {
		return resultCount.get() > OUTPUTS;
	}

	public boolean hasNecronomicon() {
		return !tile.getItem(1).isEmpty();
	}

	/**
	 * Consumes the crystals for {@code count} materializations of the output (server side)
	 */
	private void materialize(ItemStack output, int count) {
		if(player.level().isClientSide || count <= 0) return;
		ItemStack request = output.copy();
		request.setCount(count);
		MaterializerRecipes.instance().processMaterialization(request, tile.getItem(0));
		tile.setChanged();
		MinecraftForge.EVENT_BUS.post(new ACEvents.ItemMaterializedEvent(player, request));
		refresh(true);
	}

	private int getMaxCount(ItemStack stack) {
		ItemStack[] inventory = MaterializerRecipes.instance().extractItemsFromBag(tile.getItem(0).copy());
		if(inventory == null) return 0;
		Materialization mat = MaterializerRecipes.instance().getMaterializationFor(stack);
		if(mat == null) return 0;
		int count = 0;
		while(count < stack.getMaxStackSize() && MaterializerRecipes.instance().consumeCrystals(inventory, mat.input.clone()))
			count++;
		return count;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		int invStart = 2 + OUTPUTS, invEnd = invStart + 27, hotbarEnd = invStart + 36;

		if (slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index >= 2 && index < invStart) {
				// Materialize as many as the bag allows (one stack at most)
				int count = itemstack1.getMaxStackSize() > 1 ? getMaxCount(itemstack1) : 1;
				if(count <= 0) return ItemStack.EMPTY;
				ItemStack result = itemstack1.copy();
				result.setCount(count);
				int before = result.getCount();
				if (!moveItemStackTo(result, invStart, hotbarEnd, true))
					return ItemStack.EMPTY;
				materialize(itemstack1, before - result.getCount());
				return ItemStack.EMPTY;
			}
			else if (index > 1)
			{
				if(itemstack1.getItem() instanceof ItemCrystalBag) {
					if(!moveItemStackTo(itemstack1, 0, 1, false))
						return ItemStack.EMPTY;
				} else if(itemstack1.is(ACTags.Items.NECRONOMICONS)) {
					if(!moveItemStackTo(itemstack1, 1, 2, false))
						return ItemStack.EMPTY;
				} else if (index >= invStart && index < invEnd) {
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

	/**
	 * Output slot: taking an item materializes it from the bag's crystals
	 */
	private class OutputSlot extends Slot {

		OutputSlot(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public ItemStack remove(int amount) {
			if(!player.level().isClientSide) {
				// re-validate against the current bag contents, so a stale output (eg. another player used
				// the same Materializer this tick) can't be taken without paying for it
				refresh(false);
				ItemStack display = getItem();
				if(display.isEmpty() || getMaxCount(display) < Math.min(amount, display.getCount()))
					return ItemStack.EMPTY;
			}
			ItemStack stack = super.remove(amount);
			if(!stack.isEmpty())
				materialize(stack, stack.getCount());
			return stack;
		}
	}
}
