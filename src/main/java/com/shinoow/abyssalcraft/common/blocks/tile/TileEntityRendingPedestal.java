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
 *     1.20.1 port - multipart entities are Forge PartEntities; the energy counters are menu data
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ISingletonInventory;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.inventory.ContainerRendingPedestal;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

/**
 * Rending Pedestal: with a Staff of Rending on it, it uses its PE to rend the energy out of shadow mobs and the plague
 * carriers of the dimension it's in, turning it into Shadow Gems and dimension essences
 */
public class TileEntityRendingPedestal extends TileEntityACInventory implements IEnergyContainer, ISingletonInventory {

	private static final int[] OUTPUTS = {2, 3, 4, 5}, NONE = new int[0];
	public static final int SHADOW = 0, ABYSSAL = 1, DREAD = 2, OMOTHOL = 3;

	private float energy;
	private int ticksExisted;
	private final int[] energies = new int[4];

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return index < 4 ? energies[index] : (int) energy;
		}

		@Override
		public void set(int index, int value) {}

		@Override
		public int getCount() {
			return 5;
		}
	};

	public TileEntityRendingPedestal(BlockPos pos, BlockState state) {
		super(ACBlockEntities.RENDING_PEDESTAL.get(), pos, state, 6, "container.abyssalcraft.rendingpedestal");
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		energy = nbttagcompound.getFloat("PotEnergy");
		energies[SHADOW] = nbttagcompound.getInt("energyShadow");
		energies[ABYSSAL] = nbttagcompound.getInt("energyAbyssal");
		energies[DREAD] = nbttagcompound.getInt("energyDread");
		energies[OMOTHOL] = nbttagcompound.getInt("energyOmothol");
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		nbttagcompound.putFloat("PotEnergy", energy);
		nbttagcompound.putInt("energyShadow", energies[SHADOW]);
		nbttagcompound.putInt("energyAbyssal", energies[ABYSSAL]);
		nbttagcompound.putInt("energyDread", energies[DREAD]);
		nbttagcompound.putInt("energyOmothol", energies[OMOTHOL]);
	}

	/** The staff is rendered on top */
	@Override
	protected boolean syncContents() {
		return true;
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerRendingPedestal(id, inventory, this, data);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityRendingPedestal tile) {
		++tile.ticksExisted;
		ItemStack input = tile.getItem(0);
		if(input.getItem() instanceof IEnergyContainerItem container && container.canTransferPE(input) && tile.canAcceptPE())
			tile.addEnergy(container.consumeEnergy(input, 20));

		ItemStack stack = tile.getItem(1);
		if(stack.getItem() instanceof IStaffOfRending staff && tile.ticksExisted % 40 == 0){
			String[] names = {"Shadow", "Abyssal", "Dread", "Omothol"};
			for(int i = 0; i < 4; i++)
				if(staff.getEnergy(stack, names[i]) > 0){
					tile.increaseEnergy(i, staff.getEnergy(stack, names[i]));
					staff.setEnergy(0, stack, names[i]);
				}

			AABB area = new AABB(pos).inflate(15, 3, 15);
			for(LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area))
				tile.rend(level, target, target, staff, stack);
			for(PartEntity<?> part : level.getEntitiesOfClass(PartEntity.class, area))
				if(part.getParent() instanceof LivingEntity parent)
					tile.rend(level, part, parent, staff, stack);
			tile.setChanged();
		}

		tile.convert(SHADOW, 200, 2, ACItems.shadow_gem.get());
		tile.convert(ABYSSAL, 100, 3, ACItems.abyssal_wasteland_essence.get());
		tile.convert(DREAD, 100, 4, ACItems.dreadlands_essence.get());
		tile.convert(OMOTHOL, 100, 5, ACItems.omothol_essence.get());
	}

	/**
	 * Hurts the target (an entity or a part of a multipart entity) if it's a shadow mob or a carrier of the dimension's
	 * plague, at the cost of half its max health in PE
	 */
	private void rend(Level level, Entity target, LivingEntity living, IStaffOfRending staff, ItemStack stack) {
		if(!living.canChangeDimensions() || !target.isAlive()) return;
		int type;
		if(living.getMobType() == AbyssalCraftAPI.SHADOW)
			type = SHADOW;
		else if(level.dimension() == ACDimensions.ABYSSAL_WASTELAND && EntityUtil.isCoraliumPlagueCarrier(living))
			type = ABYSSAL;
		else if(level.dimension() == ACDimensions.DREADLANDS && EntityUtil.isDreadPlagueCarrier(living))
			type = DREAD;
		else if(level.dimension() == ACDimensions.OMOTHOL && living instanceof IOmotholEntity)
			type = OMOTHOL;
		else return;
		if(getContainedEnergy() >= living.getMaxHealth()/2 && target.hurt(level.damageSources().generic(), staff.getDrainAmount(stack))){
			consumeEnergy(living.getMaxHealth()/2);
			increaseEnergy(type, staff.getDrainAmount(stack));
		}
	}

	/** Turns the collected energy of a type into its item */
	private void convert(int type, int needed, int slot, Item result) {
		if(energies[type] >= needed){
			ItemStack output = getItem(slot);
			if(output.is(result) && output.getCount() < output.getMaxStackSize())
				output.grow(1);
			else if(output.isEmpty())
				items.set(slot, new ItemStack(result));
			else return; // output full, keep the energy until there's room
			energies[type] = 0;
			setChanged();
		}
	}

	public int getEnergy(int type){
		return type >= 0 && type < 4 ? energies[type] : 0;
	}

	public void increaseEnergy(int type, int amount){
		if(type >= 0 && type < 4)
			energies[type] += amount;
	}

	public void setEnergy(int type, int amount){
		if(type >= 0 && type < 4)
			energies[type] = amount;
	}

	@Override
	public float getContainedEnergy() {
		return energy;
	}

	@Override
	public int getMaxEnergy() {
		return 5000;
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
		return false;
	}

	@Override
	public BlockEntity getContainerTile() {
		return this;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return switch(index) {
		case 0 -> stack.getItem() instanceof IEnergyContainerItem;
		case 1 -> stack.getItem() instanceof IStaffOfRending;
		default -> false;
		};
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		super.setItem(index, stack);
		if(index == 1)
			markForUpdate();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? OUTPUTS : NONE;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return direction == Direction.DOWN && index > 1;
	}

	/** The staff (rendered on top) */
	@Override
	public ItemStack getItem() {
		return getItem(1);
	}

	@Override
	public void setItem(ItemStack item) {
		setItem(1, item);
	}
}
