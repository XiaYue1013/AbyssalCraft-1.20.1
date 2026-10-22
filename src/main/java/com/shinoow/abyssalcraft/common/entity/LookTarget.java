/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the getHeadLookTarget that the breath attacks of 1.12.2 (Dreadguard, Cha'garoth, Shadow Beast,
 *                   Sacthoth) each had a copy of
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** The living entity a mob is looking at, for breath attacks */
public class LookTarget {

	/**
	 * @param minRange the ray is minRange + random * extraRange long (stopped by blocks)
	 * @param grow how far around the end of the ray to look for entities
	 * @param valid which entities count
	 */
	@Nullable
	public static Entity find(Mob mob, double minRange, double extraRange, float grow, Predicate<LivingEntity> valid) {
		Entity pointedEntity = null;
		double range = minRange + mob.getRandom().nextDouble() * extraRange;
		Vec3 srcVec = mob.getEyePosition();
		Vec3 lookVec = mob.getViewVector(1.0F);
		Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
		BlockHitResult raytrace = mob.level().clip(new ClipContext(srcVec, destVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
		BlockPos hitpos = raytrace.getType() != HitResult.Type.MISS ? raytrace.getBlockPos() : null;
		double rx = hitpos == null ? range : Math.min(range, Math.abs(mob.getX() - hitpos.getX()));
		double ry = hitpos == null ? range : Math.min(range, Math.abs(mob.getY() - hitpos.getY()));
		double rz = hitpos == null ? range : Math.min(range, Math.abs(mob.getZ() - hitpos.getZ()));
		List<Entity> possibleList = mob.level().getEntities(mob, mob.getBoundingBox().move(lookVec.x * rx, lookVec.y * ry, lookVec.z * rz).inflate(grow));
		double hitDist = 0.0D;
		for(Entity possibleEntity : possibleList)
			if(possibleEntity != mob && possibleEntity instanceof LivingEntity living && valid.test(living)) {
				AABB collisionBB = possibleEntity.getBoundingBox().inflate(possibleEntity.getPickRadius());
				Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);
				if(collisionBB.contains(srcVec)) {
					if(0.0D < hitDist || hitDist == 0.0D) {
						pointedEntity = possibleEntity;
						hitDist = 0.0D;
					}
				} else if(interceptPos.isPresent()) {
					double possibleDist = srcVec.distanceTo(interceptPos.get());
					if(possibleDist < hitDist || hitDist == 0.0D) {
						pointedEntity = possibleEntity;
						hitDist = possibleDist;
					}
				}
			}
		return pointedEntity;
	}
}
