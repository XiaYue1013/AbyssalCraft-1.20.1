/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the collideWithEntity that every 1.12.2 antimatter mob repeated
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.anti;

import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.common.world.ACExplosion;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

/** Matter meets antimatter */
public class AntiMatter {

	/**
	 * An antimatter mob touching its normal counterpart blows up: a size 5 explosion, or with the
	 * "nuclearAntimatterExplosions" option an antimatter crater (size "antimatterExplosionSize"). Blocks only break if
	 * mobGriefing is on.
	 * @return true if it blew up (don't push the other entity then)
	 */
	public static boolean collide(LivingEntity anti, Entity other, Class<? extends Entity> counterpart) {
		if(anti.level().isClientSide || !counterpart.isInstance(other) || other instanceof IAntiEntity)
			return false;
		explode(anti);
		return true;
	}

	public static void explode(LivingEntity anti) {
		Level level = anti.level();
		boolean griefing = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
		if(ACConfig.nuclearAntimatterExplosions)
			ACExplosion.explode(level, anti, anti.getX(), anti.getY(), anti.getZ(), ACConfig.antimatterExplosionSize, true, griefing);
		else
			level.explode(anti, anti.getX(), anti.getY(), anti.getZ(), 5, griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
		anti.discard();
	}
}
