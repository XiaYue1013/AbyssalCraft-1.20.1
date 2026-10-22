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

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Spawns four random monsters that can spawn in the biome
 */
public class DisruptionRandomSwarm extends DisruptionRandomSpawn {

	public DisruptionRandomSwarm() {
		super("randomSwarm");
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world instanceof ServerLevel level)
			for(int i = 0; i < 4; i++)
				spawnRandom(level, pos);
	}
}
