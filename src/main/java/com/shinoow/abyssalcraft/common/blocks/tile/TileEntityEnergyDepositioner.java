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
 *     1.20.1 port - inventory through TileEntityACInventory, progress/PE as menu data, block conversions shared with
 *     the Ritual of Corruption (DarklandsCorruption)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.IEnergyManipulator;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.common.inventory.ContainerEnergyDepositioner;
import com.shinoow.abyssalcraft.common.items.ItemStoneTablet;
import com.shinoow.abyssalcraft.common.ritual.BiomeRitualHelper;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.common.util.DarklandsCorruption;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Energy Depositioner: drains the PE out of a filled Stone Tablet (from the State Transformer) over 200 ticks, sends
 * it to nearby collectors like a statue, and corrupts the land around it into Darklands. The more Darklands there
 * already are, the more likely the emptied tablet comes out cursed.
 */
public class TileEntityEnergyDepositioner extends TileEntityACInventory implements IEnergyManipulator {

	private static final int[] TOP = {0}, BOTTOM = {1}, NONE = new int[0];

	private AmplifierType currentAmplifier;
	private int tolerance;
	private float energy;
	private int processingTime;
	private ItemStack processingStack = ItemStack.EMPTY;
	private Set<BlockPos> positions = new HashSet<>();

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? processingTime : (int) energy;
		}

		@Override
		public void set(int index, int value) {}

		@Override
		public int getCount() {
			return 2;
		}
	};

	public TileEntityEnergyDepositioner(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENERGY_DEPOSITIONER.get(), pos, state, 2, "container.abyssalcraft.energydepositioner");
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		tolerance = nbttagcompound.getInt("Tolerance");
		energy = nbttagcompound.getFloat("PotEnergy");
		PEUtils.readManipulatorNBT(this, nbttagcompound);
		processingTime = nbttagcompound.getInt("ProcessingTime");
		processingStack = ItemStack.of(nbttagcompound.getCompound("ProcessingStack"));
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		nbttagcompound.putInt("Tolerance", tolerance);
		nbttagcompound.putFloat("PotEnergy", energy);
		PEUtils.writeManipulatorNBT(this, nbttagcompound);
		nbttagcompound.putInt("ProcessingTime", processingTime);
		CompoundTag nbtItem = new CompoundTag();
		if(!processingStack.isEmpty())
			processingStack.save(nbtItem);
		nbttagcompound.put("ProcessingStack", nbtItem);
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ContainerEnergyDepositioner(id, inventory, this, data);
	}

	@Override
	public float getEnergyQuanta() {
		float f = isActive() ? 20 * Math.max(getAmplifier(AmplifierType.POWER), 1.0F) : 15;
		setChanged();
		if(f < energy){
			energy -= f;
			return f;
		} else {
			float ret = energy;
			energy = 0;
			return ret;
		}
	}

	@Override
	public void setActive(AmplifierType amp, DeityType deity) {
		if(!isActive())
			setActiveAmplifier(amp);
	}

	@Override
	public boolean isActive() {
		return currentAmplifier != null;
	}

	@Override
	public DeityType getDeity(BlockState state) {
		return null;
	}

	@Override
	public float getAmplifier(AmplifierType type){
		if(type == currentAmplifier)
			switch(type){
			case DURATION:
				return 2;
			case POWER:
				return 1.5F;
			case RANGE:
				return 4;
			default:
				return 0;
			}
		else return 0;
	}

	@Override
	public DeityType getActiveDeity() {
		return null;
	}

	@Override
	public AmplifierType getActiveAmplifier() {
		return currentAmplifier;
	}

	@Override
	public void setActiveDeity(DeityType deity) {}

	@Override
	public void setActiveAmplifier(AmplifierType amplifier) {
		currentAmplifier = amplifier;
	}

	@Override
	public void addTolerance(int num) {
		tolerance += num;
		setChanged();
	}

	@Override
	public boolean canTransferPE() {
		return energy > 0;
	}

	@Override
	public int getTolerance() {
		return tolerance;
	}

	@Override
	public void disrupt() {
		tolerance = 0;
		setChanged();
		if(!ACConfig.no_disruptions && level instanceof ServerLevel serverLevel) {
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
			if(bolt != null) {
				bolt.moveTo(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ());
				bolt.setVisualOnly(true);
				serverLevel.addFreshEntity(bolt);
			}
			DisruptionHandler.instance().generateDisruption(DeityType.values()[level.random.nextInt(DeityType.values().length)], level, worldPosition,
					level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(16, 16, 16)));
		}
	}

	@Override
	public Set<BlockPos> getEnergyCollectors(){
		return positions;
	}

	public float getContainedEnergy(){
		return energy;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityEnergyDepositioner tile) {
		if(tile.isActive())
			((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 0, 0, 0, 0, 1.0);
		else PEUtils.clearManipulatorData(tile);

		if(tile.canProcess()){
			++tile.processingTime;
			tile.energy += tile.drainPEFromInput();
			if(tile.energy > 10000) tile.energy = 10000;
			if(tile.processingTime == 200){
				tile.processingTime = 0;
				tile.processItem();
			}
			tile.setChanged();
		} else tile.processingTime = 0;

		if(PEUtils.checkForAdjacentManipulators(level, pos)) {
			if(level.getGameTime() % 200 == 0)
				PEUtils.locateCollectors(level, pos, tile);
			PEUtils.transferPEToCollectors(level, pos, tile);
		}
		if(tile.tolerance >= 200)
			tile.disrupt();
	}

	private boolean canProcess(){
		if(!processingStack.isEmpty()) return true;
		ItemStack stack = items.get(0);
		if(stack.getItem() instanceof ItemStoneTablet tablet && !tablet.isCursed(stack) && tablet.hasInventory(stack))
			processingStack = removeItemNoUpdate(0);
		return !processingStack.isEmpty();
	}

	private float drainPEFromInput(){
		if(processingStack.isEmpty() || !processingStack.hasTag()) return 0;
		return processingStack.getTag().getFloat("PotEnergy") / 200;
	}

	private void processItem(){
		float n = 0;
		List<BlockPos> columns = new ArrayList<>();
		for(int x = worldPosition.getX() -8; x < worldPosition.getX() + 9; x++)
			for(int z = worldPosition.getZ() - 8; z < worldPosition.getZ() + 9; z++){
				if(BiomeRitualHelper.biome(level, x, z).is(ACBiomes.IS_DARKLANDS)){
					n++;
					continue;
				}
				columns.add(new BlockPos(x, level.getMinBuildHeight(), z));
			}
		List<BlockPos> corrupted = new ArrayList<>();
		for(BlockPos column : columns){
			if(level.random.nextInt(289) > n) continue;
			n++;
			DarklandsCorruption.corruptColumn(level, column, true);
			corrupted.add(column);
		}
		BiomeUtil.updateBiomes(level, corrupted, ACBiomes.darklands);

		if(canProcess()){
			ItemStack stack = processingStack;
			stack.getTag().remove("ItemInventory");
			stack.getTag().remove("PotEnergy");
			if(level.random.nextFloat() < n/289)
				((ItemStoneTablet)stack.getItem()).setCursed(stack);
			items.set(1, stack);
			processingStack = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return index == 0 && stack.getItem() instanceof ItemStoneTablet;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? BOTTOM : side == Direction.UP ? TOP : NONE;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return direction == Direction.UP && index == 0 && canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return direction == Direction.DOWN && index == 1;
	}
}
