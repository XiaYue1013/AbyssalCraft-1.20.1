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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

/**
 * Up to eight fireballs fall down around the position
 */
public class DisruptionFireRain extends DisruptionEntry {

	public DisruptionFireRain() {
		super("fireRain", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		double x = pos.getX(), y = pos.getY() + 10, z = pos.getZ();
		double[][] offsets = {{2.5, 2.5}, {-2.5, 2.5}, {2.5, -2.5}, {-2.5, -2.5}, {2.5, 0}, {-2.5, 0}, {0, 2.5}, {0, -2.5}};
		for(double[] offset : offsets)
			if(world.random.nextBoolean())
				world.addFreshEntity(positionFireball(world, x + offset[0], y, z + offset[1], 0, -0.5, 0));
	}

	private LargeFireball positionFireball(Level world, double x, double y, double z, double accx, double accy, double accz){
		LargeFireball fireball = new LargeFireball(EntityType.FIREBALL, world);
		fireball.moveTo(x, y, z, fireball.getYRot(), fireball.getXRot());
		double d6 = Math.sqrt(accx * accx + accy * accy + accz * accz);
		fireball.xPower = accx / d6 * 0.1D;
		fireball.yPower = accy / d6 * 0.1D;
		fireball.zPower = accz / d6 * 0.1D;
		return fireball;
	}
}
