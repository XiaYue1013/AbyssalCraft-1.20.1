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
package com.shinoow.abyssalcraft.common.ritual;

import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Clear -> rain -> thunder -> clear
 */
public class NecronomiconWeatherRitual extends NecronomiconRitual {

	public NecronomiconWeatherRitual() {
		super("weather", 0, 100F, Items.FEATHER, Items.FEATHER, Items.FEATHER, Items.FEATHER,
				Items.FEATHER, Items.FEATHER, Items.FEATHER, Items.FEATHER);
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return world.getBiome(pos).value().getPrecipitationAt(pos) != Biome.Precipitation.NONE;
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		if(!(world instanceof ServerLevel level)) return;
		int duration = 6000 + level.random.nextInt(12000);
		if(!level.isRaining())
			level.setWeatherParameters(0, duration, true, false);
		else if(!level.isThundering())
			level.setWeatherParameters(0, duration, true, true);
		else
			level.setWeatherParameters(duration, 0, false, false);
	}
}
