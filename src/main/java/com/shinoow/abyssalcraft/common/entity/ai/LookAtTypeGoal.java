/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - EntityAIWatchClosest for an AbyssalCraft mob, looked up by id (the 1.12.2 mobs watch each other
 *     by class, and not every one of them exists at the time the watcher is written)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.ai;

import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Looks at the nearest mob of an AbyssalCraft entity type (does nothing if that type isn't registered)
 */
public class LookAtTypeGoal extends LookAtPlayerGoal {

	private final String name;
	private EntityType<?> type;

	public LookAtTypeGoal(Mob mob, String name, float distance) {
		super(mob, LivingEntity.class, distance);
		this.name = name;
	}

	@Override
	public boolean canUse() {
		if(type == null && ForgeRegistries.ENTITY_TYPES.containsKey(AbyssalCraft.loc(name)))
			type = ForgeRegistries.ENTITY_TYPES.getValue(AbyssalCraft.loc(name));
		if(type == null || mob.getRandom().nextFloat() >= probability)
			return false;
		lookAt = mob.level().getNearestEntity(mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(lookDistance, 3.0D, lookDistance),
				e -> e.getType() == type), lookAtContext, mob, mob.getX(), mob.getEyeY(), mob.getZ());
		return lookAt != null;
	}
}
