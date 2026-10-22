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
 *     1.20.1 port - TileEntityTieredEnergyCollector merged in (capacity from the block's tier)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.energy.IEnergyCollector;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Collects PE from nearby statues, which relays then pull from
 */
public class TileEntityEnergyCollector extends TileEntityPEStorage implements IEnergyCollector {

	public TileEntityEnergyCollector(BlockPos pos, BlockState state) {
		super(ACBlockEntities.ENERGY_COLLECTOR.get(), pos, state);
	}

	@Override
	public int getMaxEnergy() {
		return BlockPE.tieredCapacity(1000, getTier());
	}
}
