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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Swaps the positions of the living things nearby
 */
public class DisruptionDisplaceEntities extends DisruptionEntry {

	public DisruptionDisplaceEntities() {
		super("displace", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(16, 16, 16));
		for(LivingEntity entity : entities){
			LivingEntity other = entities.get(world.random.nextInt(entities.size()));
			double posX = entity.getX();
			double posY = entity.getY();
			double posZ = entity.getZ();
			entity.teleportTo(other.getX(), other.getY(), other.getZ());
			other.teleportTo(posX, posY, posZ);
		}
	}
}
