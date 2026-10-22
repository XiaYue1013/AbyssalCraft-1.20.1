/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the energy bookkeeping every 1.12.2 PE block entity duplicated
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity storing Potential Energy ("PotEnergy", kept when the block is broken)
 */
public abstract class TileEntityPEStorage extends BlockEntity implements IEnergyContainer {

	private float energy;

	protected TileEntityPEStorage(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/** Dimension tier of the block, see {@link BlockPE} */
	protected int getTier() {
		return BlockPE.getTier(getBlockState());
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
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** Saves and sends the block entity to clients */
	protected void markForUpdate() {
		setChanged();
		if(level != null && !level.isClientSide)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public float getContainedEnergy() {
		return energy;
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

	/** Clamps the stored energy (used when the capacity is lower than the stored amount) */
	protected void setEnergy(float energy) {
		this.energy = Math.max(0, Math.min(energy, getMaxEnergy()));
		setChanged();
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
