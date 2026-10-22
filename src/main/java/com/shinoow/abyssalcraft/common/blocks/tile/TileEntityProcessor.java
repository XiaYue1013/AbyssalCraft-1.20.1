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
 *     1.20.1 port - common logic of the Crystallizer and Transmutator
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.common.blocks.BlockProcessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Furnace-like machine: slot 0 input, slot 1 fuel, slots 2+ outputs. 200 ticks per operation (1.12.2 behaviour).
 */
public abstract class TileEntityProcessor extends TileEntityACInventory {

	public static final int PROCESS_TIME = 200;

	/** Remaining fuel ticks */
	public int fuelTime;
	/** Fuel ticks of the last consumed fuel item */
	public int currentFuelTime;
	/** Progress of the current operation */
	public int progress;

	private final int outputs;

	protected final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
			case 0 -> fuelTime;
			case 1 -> currentFuelTime;
			case 2 -> progress;
			default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0 -> fuelTime = value;
			case 1 -> currentFuelTime = value;
			case 2 -> progress = value;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	};

	protected TileEntityProcessor(BlockEntityType<?> type, BlockPos pos, BlockState state, int outputs, String name) {
		super(type, pos, state, 2 + outputs, name);
		this.outputs = outputs;
	}

	/**
	 * Fuel value (in ticks) of a stack, 0 if it's not a fuel
	 */
	public abstract int getFuelTime(ItemStack stack);

	/**
	 * The outputs for an input (array with one entry per output slot, empty stacks if there's no recipe)
	 */
	protected abstract ItemStack[] getResults(ItemStack input);

	public boolean isFuel(ItemStack stack) {
		return getFuelTime(stack) > 0;
	}

	public boolean isProcessing() {
		return fuelTime > 0;
	}

	public ContainerData getData() {
		return data;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		fuelTime = tag.getShort("BurnTime");
		progress = tag.getShort("ProcessTime");
		currentFuelTime = getFuelTime(items.get(1));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putShort("BurnTime", (short)fuelTime);
		tag.putShort("ProcessTime", (short)progress);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityProcessor tile) {
		boolean wasProcessing = tile.fuelTime > 0;
		boolean changed = false;

		if (tile.fuelTime > 0)
			--tile.fuelTime;

		ItemStack[] results = tile.getResults(tile.items.get(0));
		boolean canProcess = tile.canProcess(results);

		if (tile.fuelTime == 0 && canProcess)
		{
			ItemStack stack = tile.items.get(1);
			tile.currentFuelTime = tile.fuelTime = tile.getFuelTime(stack);

			if (tile.fuelTime > 0)
			{
				changed = true;
				if (!stack.isEmpty())
				{
					ItemStack remainder = stack.getCraftingRemainingItem();
					stack.shrink(1);
					if (stack.isEmpty())
						tile.items.set(1, remainder);
				}
			}
		}

		if (tile.isProcessing() && canProcess)
		{
			++tile.progress;

			if (tile.progress == PROCESS_TIME)
			{
				tile.progress = 0;
				tile.process(results);
				changed = true;
			}
		} else
			tile.progress = 0;

		if (wasProcessing != tile.fuelTime > 0)
		{
			changed = true;
			level.setBlock(pos, state.setValue(BlockProcessor.LIT, tile.fuelTime > 0), 3);
		}

		if (changed)
			tile.setChanged();
	}

	private boolean canProcess(ItemStack[] results) {
		if(results[0].isEmpty()) return false;
		for(int i = 0; i < outputs; i++) {
			ItemStack result = results[i];
			if(result.isEmpty()) continue;
			ItemStack out = items.get(2 + i);
			if(out.isEmpty()) continue;
			if(!ItemStack.isSameItemSameTags(out, result)) return false;
			int count = out.getCount() + result.getCount();
			if(count > getMaxStackSize() || count > out.getMaxStackSize()) return false;
		}
		return true;
	}

	private void process(ItemStack[] results) {
		for(int i = 0; i < outputs; i++) {
			ItemStack result = results[i];
			if(result.isEmpty()) continue;
			ItemStack out = items.get(2 + i);
			if (out.isEmpty())
				items.set(2 + i, result.copy());
			else
				out.grow(result.getCount());
		}
		ItemStack input = items.get(0);
		ItemStack remainder = input.getItem() == Items.POTION ? new ItemStack(Items.GLASS_BOTTLE) : input.getCraftingRemainingItem();
		input.shrink(1);
		if (input.isEmpty())
			items.set(0, remainder);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot >= 2 ? false : slot == 1 ? isFuel(stack) : true;
	}

	@Override
	public int[] getSlotsForFace(Direction face) {
		if(face == Direction.UP) return new int[] {0};
		if(face == Direction.DOWN) return outputs == 2 ? new int[] {2, 1, 3} : new int[] {2, 1};
		return new int[] {1};
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
		if (face == Direction.DOWN && slot == 1)
			return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET);
		return true;
	}
}
