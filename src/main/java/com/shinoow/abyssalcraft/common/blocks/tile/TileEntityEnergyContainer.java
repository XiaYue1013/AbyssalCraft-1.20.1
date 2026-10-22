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
 *     1.20.1 port - TileEntityTieredEnergyContainer merged in (capacity from the block's tier)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.common.inventory.ContainerEnergyContainer;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stores PE. Slot 0 drains a PE item into the container, slot 1 charges a PE item from it.
 */
public class TileEntityEnergyContainer extends TileEntityACInventory implements IEnergyContainer {

	private static final int[] SLOTS = {0, 1};

	private float energy;

	/** Energy and capacity, split into 16 bit halves (menu data is sent as shorts) */
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			int value = index < 2 ? (int) energy : getMaxEnergy();
			return index % 2 == 0 ? value & 0xFFFF : value >>> 16;
		}

		@Override
		public void set(int index, int value) {}

		@Override
		public int getCount() {
			return 4;
		}
	};

	public TileEntityEnergyContainer(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENERGY_CONTAINER.get(), pos, state, 2,
				BlockPE.getTier(state) == BlockPE.BASE ? "container.abyssalcraft.energycontainer" : "container.abyssalcraft.tieredenergycontainer");
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityEnergyContainer tile) {
		ItemStack input = tile.getItem(0);
		if(!input.isEmpty() && input.getItem() instanceof IEnergyContainerItem item)
			if(item.canTransferPE(input) && tile.canAcceptPE())
				tile.addEnergy(item.consumeEnergy(input, 20));

		ItemStack output = tile.getItem(1);
		if(!output.isEmpty() && output.getItem() instanceof IEnergyContainerItem item)
			if(item.canAcceptPE(output) && tile.canTransferPE())
				item.addEnergy(output, tile.consumeEnergy(20));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		energy = tag.getFloat("PotEnergy");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putFloat("PotEnergy", energy);
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerEnergyContainer(id, inventory, this, data);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public float getContainedEnergy() {
		return energy;
	}

	@Override
	public int getMaxEnergy() {
		int base = 10000;
		return switch(BlockPE.getTier(getBlockState())) {
		case BlockPE.OVERWORLD -> base * 2;
		case BlockPE.ABYSSAL_WASTELAND -> base * 6;
		case BlockPE.DREADLANDS -> base * 24;
		case BlockPE.OMOTHOL -> base * 120;
		default -> base;
		};
	}

	@Override
	public void addEnergy(float energy) {
		this.energy += energy;
		if(this.energy > getMaxEnergy()) this.energy = getMaxEnergy();
		setChanged();
	}

	@Override
	public float consumeEnergy(float energy) {
		setChanged();
		if(energy < this.energy){
			this.energy -= energy;
			return energy;
		} else {
			float ret = this.energy;
			this.energy = 0;
			return ret;
		}
	}

	@Override
	public boolean canAcceptPE() {
		return getContainedEnergy() < getMaxEnergy();
	}

	@Override
	public boolean canTransferPE() {
		return getContainedEnergy() > 0;
	}

	@Override
	public BlockEntity getContainerTile() {
		return this;
	}
}
