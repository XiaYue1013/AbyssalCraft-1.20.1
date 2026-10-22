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

import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DisruptionLightning extends DisruptionEntry {

	public DisruptionLightning() {
		super("lightning", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world instanceof ServerLevel level)
			for(Player player : players)
				if(world.random.nextInt(10) == 0) {
					LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
					if(bolt != null) {
						bolt.moveTo(player.getX(), player.getY(), player.getZ());
						level.addFreshEntity(bolt);
					}
				}
	}
}
