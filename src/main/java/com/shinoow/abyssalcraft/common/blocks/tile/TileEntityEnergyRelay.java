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
 *     1.20.1 port - TileEntityTieredEnergyRelay merged in; the facing is always the block state's FACING
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.api.energy.IEnergyTransporter;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyRelay;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Pulls PE from the collector/container behind it and sends it to the container it faces (disabled by redstone)
 */
public class TileEntityEnergyRelay extends TileEntityPEStorage implements IEnergyTransporter {

	private int ticksExisted = -1;

	public TileEntityEnergyRelay(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENERGY_RELAY.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityEnergyRelay tile) {
		if(tile.ticksExisted < 0)
			tile.ticksExisted = level.random.nextInt(100);
		if(level.hasNeighborSignal(pos))
			return;

		++tile.ticksExisted;

		Direction facing = state.getValue(BlockEnergyRelay.FACING);
		if(tile.ticksExisted % 20 == 0)
			PEUtils.collectNearbyPE(tile, level, pos, facing.getOpposite(), tile.getDrainQuanta());
		if(tile.ticksExisted % 40 == 0 && tile.canTransferPE())
			tile.transferPE(facing, tile.getTransferQuanta());
	}

	@Override
	public void transferPE(Direction facing, float energy) {
		if(PEUtils.canTransfer(level, worldPosition, facing, getRange())){
			IEnergyContainer container = PEUtils.getContainer(level, worldPosition, facing, getRange());
			if(container != null)
				if(container.canAcceptPE()){
					container.addEnergy(consumeEnergy(energy));
					AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(worldPosition, container.getContainerTile().getBlockPos(), level);
				}
		}
	}

	@Override
	public int getMaxEnergy() {
		int tier = getTier();
		return tier == BlockPE.BASE ? 500 : 600 + 100 * tier;
	}

	/** How far away the target container can be */
	public int getRange(){
		int tier = getTier();
		return tier == BlockPE.BASE ? 4 : 6 + 2 * tier;
	}

	protected float getDrainQuanta(){
		int tier = getTier();
		return tier == BlockPE.BASE ? 5 : 20 + 10 * tier;
	}

	protected float getTransferQuanta(){
		int tier = getTier();
		return tier == BlockPE.BASE ? 10 : 30 + 10 * tier;
	}
}
