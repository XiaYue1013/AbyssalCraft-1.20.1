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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DisruptionFreeze extends DisruptionEntry {

	public DisruptionFreeze() {
		super("freeze", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		for(Player player : players)
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 14));
	}
}
