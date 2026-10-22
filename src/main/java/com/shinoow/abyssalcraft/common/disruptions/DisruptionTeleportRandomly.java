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
 *     1.20.1 port - uses the vanilla random teleport (same rules as chorus fruit/endermen: solid ground, no
 *     collisions, no liquids) instead of a copy of the 1.12.2 enderman code
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.disruptions;

import java.util.List;

import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

/**
 * Teleports nearby players up to 32 blocks away
 */
public class DisruptionTeleportRandomly extends DisruptionEntry {

	public DisruptionTeleportRandomly() {
		super("randomTeleport", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		for(Player player : players)
			teleportRandomly(player, world);
	}

	protected boolean teleportRandomly(Player player, Level world)
	{
		double d0 = player.getX() + (world.random.nextDouble() - 0.5D) * 64.0D;
		double d1 = player.getY() + (world.random.nextInt(64) - 32);
		double d2 = player.getZ() + (world.random.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(d0, d1, d2, player, world);
	}

	protected boolean teleportTo(double x, double y, double z, Player player, Level world)
	{
		EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(player, x, y, z);
		if (event.isCanceled())
			return false;
		double d3 = player.getX();
		double d4 = player.getY();
		double d5 = player.getZ();
		if(player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
			world.playSound(null, d3, d4, d5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
			player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			return true;
		}
		return false;
	}
}
