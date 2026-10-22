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
package com.shinoow.abyssalcraft.common.disruptions;

import java.util.List;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Drains a tenth of the PE of the first charged PE item in each player's inventory, hurting them
 */
public class DisruptionPotentialEnergy extends DisruptionEntry {

	public DisruptionPotentialEnergy() {
		super("potentialEnergy", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		for(Player player : players)
			for(ItemStack item : player.getInventory().items)
				if(item.getItem() instanceof IEnergyContainerItem container && container.getContainedEnergy(item) > 0){
					container.consumeEnergy(item, container.getContainedEnergy(item)/10);
					player.hurt(world.damageSources().magic(), 2);
					break;
				}
	}
}
