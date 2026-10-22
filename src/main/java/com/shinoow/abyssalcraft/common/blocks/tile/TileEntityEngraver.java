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
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.item.ItemEngraving;
import com.shinoow.abyssalcraft.api.recipe.EngraverRecipes;
import com.shinoow.abyssalcraft.common.inventory.ContainerEngraver;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Engraver: engraves coins with engraving templates (slot 0 coin, slot 1 template, slot 2 output)
 */
public class TileEntityEngraver extends TileEntityACInventory {

	public int engraverProcessTime;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return engraverProcessTime;
		}

		@Override
		public void set(int index, int value) {
			engraverProcessTime = value;
		}

		@Override
		public int getCount() {
			return 1;
		}
	};

	public TileEntityEngraver(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENGRAVER.get(), pos, state, 3, "container.abyssalcraft.engraver");
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		engraverProcessTime = tag.getShort("ProcessTime");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putShort("ProcessTime", (short)engraverProcessTime);
	}

	public boolean isEngraving(){
		return items.get(1).getItem() instanceof ItemEngraving;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityEngraver tile) {
		boolean changed = false;

		if (tile.isEngraving() && tile.canEngrave())
		{
			++tile.engraverProcessTime;

			if (tile.engraverProcessTime == 200)
			{
				tile.engraverProcessTime = 0;
				tile.engraveItem();
				changed = true;
				ItemStack template = tile.items.get(1);
				template.setDamageValue(template.getDamageValue() + 1);
				if (template.getDamageValue() >= template.getMaxDamage())
					tile.items.set(1, template.getCraftingRemainingItem());
			}
		} else
			tile.engraverProcessTime = 0;

		if (tile.engraverProcessTime > 0)
			changed = true;

		if (changed)
			tile.setChanged();
	}

	private ItemStack getResult() {
		if(items.get(0).isEmpty() || !isEngraving()) return ItemStack.EMPTY;
		return EngraverRecipes.instance().getEngravingResult(items.get(0), (ItemEngraving)items.get(1).getItem());
	}

	private boolean canEngrave()
	{
		ItemStack itemstack = getResult();
		if (itemstack.isEmpty()) return false;
		ItemStack out = items.get(2);
		if (out.isEmpty()) return true;
		if (!ItemStack.isSameItem(out, itemstack)) return false;
		int result = out.getCount() + itemstack.getCount();
		return result <= getMaxStackSize() && result <= out.getMaxStackSize();
	}

	public void engraveItem()
	{
		if (canEngrave())
		{
			ItemStack itemstack = getResult();

			if (items.get(2).isEmpty())
				items.set(2, itemstack.copy());
			else if (items.get(2).getItem() == itemstack.getItem())
				items.get(2).grow(itemstack.getCount());

			items.get(0).shrink(1);
		}
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack)
	{
		return slot == 2 ? false : slot == 1 ? stack.getItem() instanceof ItemEngraving : true;
	}

	@Override
	public int[] getSlotsForFace(Direction side){
		return side == Direction.DOWN ? new int[] {2, 1} : side == Direction.UP ? new int[] {0} : new int[] {1};
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerEngraver(id, inventory, this, data);
	}
}
