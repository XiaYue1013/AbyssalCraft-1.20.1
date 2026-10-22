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
package com.shinoow.abyssalcraft.api.energy.disruption;

import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A Disruption Entry that spawns four of each supplied mob near the disruption source
 * @author shinoow
 *
 * @since 1.5
 */
public class DisruptionSwarm extends DisruptionEntry {

	private Supplier<? extends EntityType<? extends Mob>>[] entities;

	@SafeVarargs
	public DisruptionSwarm(String unlocalizedName, DeityType deity, Supplier<? extends EntityType<? extends Mob>>...entities) {
		super(unlocalizedName, deity);
		this.entities = entities;
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world instanceof ServerLevel level)
			for(Supplier<? extends EntityType<? extends Mob>> type : entities)
				for(int i = 0; i < 4; i++)
					DisruptionSpawn.spawnNear(level, pos, type.get());
	}
}
