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
 *     1.20.1 port - the mode is switched through the menu (server side) instead of a client supplied mode message
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.common.blocks.BlockStateTransformer;
import com.shinoow.abyssalcraft.common.inventory.ContainerStateTransformer;
import com.shinoow.abyssalcraft.common.items.ItemStoneTablet;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * State Transformer: in insert mode it packs the 49 stacks into the Stone Tablet in slot 0 (PE value = the item count,
 * scaled by stack size), in extract mode it unpacks a filled tablet again. Takes 200 ticks.
 */
public class TileEntityStateTransformer extends TileEntityACInventory {

	public static final int INSERT = 0, EXTRACT = 1;
	private static final int[] TABLET = {0}, CONTENTS = java.util.stream.IntStream.range(1, 50).toArray(),
			ALL = java.util.stream.IntStream.range(0, 50).toArray(), NONE = new int[0];

	private int processingTime;
	private int mode;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? processingTime : mode;
		}

		@Override
		public void set(int index, int value) {
			if(index == 0) processingTime = value;
			else mode = value;
		}

		@Override
		public int getCount() {
			return 2;
		}
	};

	public TileEntityStateTransformer(BlockPos pos, BlockState state) {
		super(ACBlockEntities.STATE_TRANSFORMER.get(), pos, state, 50, "container.abyssalcraft.statetransformer");
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		processingTime = tag.getInt("ProcessingTime");
		mode = tag.getInt("Mode");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("ProcessingTime", processingTime);
		tag.putInt("Mode", mode);
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerStateTransformer(id, inventory, this, data);
	}

	public int getMode() {
		return mode;
	}

	/** Switches between insert and extract (not while processing) */
	public boolean toggleMode() {
		if(processingTime > 0) return false;
		mode = mode == INSERT ? EXTRACT : INSERT;
		setChanged();
		return true;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		if(slot == 0)
			updateTabletState();
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = super.removeItem(slot, count);
		if(slot == 0)
			updateTabletState();
		return stack;
	}

	/** Shows the tablet on top of the block while one is inserted */
	private void updateTabletState() {
		if(level == null || level.isClientSide) return;
		BlockState state = getBlockState();
		boolean b = !items.get(0).isEmpty();
		if(state.hasProperty(BlockStateTransformer.TABLET) && state.getValue(BlockStateTransformer.TABLET) != b)
			level.setBlock(worldPosition, state.setValue(BlockStateTransformer.TABLET, b), 3);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityStateTransformer tile) {
		if(tile.canDoThing()){
			++tile.processingTime;
			if(tile.processingTime == 200){
				tile.processingTime = 0;
				tile.processItem();
				tile.setChanged();
			}
		} else tile.processingTime = 0;
	}

	private void processItem(){
		ItemStack stack = items.get(0);
		CompoundTag tag = stack.getOrCreateTag();
		if(mode == INSERT){
			ListTag list = new ListTag();
			float pe = 0;
			for (int i = 1; i < getContainerSize(); ++i)
				if (!getItem(i).isEmpty())
				{
					CompoundTag item = new CompoundTag();
					item.putInt("Slot", i);
					getItem(i).save(item);
					list.add(item);
					ItemStack is = getItem(i);
					pe += is.getCount() * (64/is.getMaxStackSize());
					removeItemNoUpdate(i);
				}
			tag.put("ItemInventory", list);
			tag.putFloat("PotEnergy", pe);
		} else if(mode == EXTRACT){
			ListTag list = tag.getList("ItemInventory", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); ++i)
			{
				CompoundTag item = list.getCompound(i);
				int slot = item.getInt("Slot");
				if (slot > 0 && slot < getContainerSize())
					items.set(slot, ItemStack.of(item));
			}
			tag.remove("ItemInventory");
			tag.remove("PotEnergy");
		}
	}

	private boolean canDoThing(){
		ItemStack stack = items.get(0);
		if(!(stack.getItem() instanceof ItemStoneTablet tablet)) return false;
		if(mode == INSERT){
			boolean b = false;
			for(int i = 1; i < items.size(); i++)
				if(!items.get(i).isEmpty())
					b = true;
			if(b)
				return !stack.hasTag() || stack.getTag().isEmpty();
		} else if(mode == EXTRACT){
			for(int i = 1; i < items.size(); i++)
				if(!items.get(i).isEmpty())
					return false;
			return tablet.hasInventory(stack);
		}
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return index == 0 ? stack.getItem() instanceof ItemStoneTablet : !(stack.getItem() instanceof ItemStoneTablet);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		switch(mode){
		case INSERT:
			return side == Direction.UP ? ALL : TABLET;
		case EXTRACT:
			return side == Direction.UP ? TABLET : side == Direction.DOWN ? ALL : NONE;
		default:
			return NONE;
		}
	}

	private static boolean hasInventory(ItemStack stack) {
		return stack.getItem() instanceof ItemStoneTablet tablet && tablet.hasInventory(stack);
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		if(processingTime > 0 || direction == null) return false;
		switch(mode){
		case INSERT:
			return index > 0 && direction == Direction.UP && canPlaceItem(index, stack) || index == 0 &&
			direction != Direction.UP && direction != Direction.DOWN && stack.getItem() instanceof ItemStoneTablet && !hasInventory(stack);
		case EXTRACT:
			return index == 0 && hasInventory(stack) && direction != Direction.DOWN;
		default:
			return false;
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if(processingTime > 0) return false;
		switch(mode){
		case INSERT:
			return index == 0 && hasInventory(stack) && direction == Direction.DOWN;
		case EXTRACT:
			return direction == Direction.DOWN && index == 0 ? stack.getItem() instanceof ItemStoneTablet && !hasInventory(stack) : true;
		default:
			return false;
		}
	}
}
