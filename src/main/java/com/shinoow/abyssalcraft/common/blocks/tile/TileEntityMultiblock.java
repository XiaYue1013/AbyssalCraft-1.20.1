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

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureBase;
import com.shinoow.abyssalcraft.api.energy.structure.StructureHandler;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Remembers which Place of Power was formed here, and periodically checks that it's still intact
 */
public class TileEntityMultiblock extends BlockEntity implements IStructureBase {

	private int ticksExisted;
	private IPlaceOfPower place;

	public TileEntityMultiblock(BlockPos pos, BlockState state) {
		super(ACBlockEntities.MULTIBLOCK.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		place = StructureHandler.instance().getStructureByName(nbttagcompound.getString("Structure"));
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		if(place != null)
			nbttagcompound.putString("Structure", place.getIdentifier());
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		return saveWithoutMetadata();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityMultiblock tile) {
		++tile.ticksExisted;
		if(tile.ticksExisted % 100 == 0 && tile.place != null)
			tile.place.validate(level, pos);
	}

	@Override
	public IPlaceOfPower getMultiblock() {
		return place;
	}

	@Override
	public void setMultiblock(IPlaceOfPower multiblock) {
		place = multiblock;
		setChanged();
	}

	@Override
	public float getAmplifier(AmplifierType type) {
		if(place != null)
			return place.getAmplifier(type);
		return 0;
	}
}
