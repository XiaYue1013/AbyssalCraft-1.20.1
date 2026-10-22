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
 *     1.20.1 port - BlockTieredEnergyCollector merged in (tier = constructor parameter)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyCollector;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.world.level.block.Block;

/**
 * Collects PE from statues for relays to pull from
 */
public class BlockEnergyCollector extends BlockPE<TileEntityEnergyCollector> {

	public BlockEnergyCollector(Properties properties, int tier) {
		super(properties, tier, Block.box(4, 0, 4, 12, 12.8, 12), TileEntityEnergyCollector::new, ACBlockEntities.ENERGY_COLLECTOR, null);
	}
}
