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
package com.shinoow.abyssalcraft.common.entity;

import java.util.List;

import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

/**
 * J'zahar's implosion: for 18 seconds it pulls everything within 64 blocks in (hurting what gets close), then throws it
 * all up and away. Omothol entities aren't affected.
 */
public class EntityImplosion extends Entity {

	private static final float SIZE = 64F;

	public EntityImplosion(EntityType<? extends EntityImplosion> type, Level level) {
		super(type, level);
	}

	public EntityImplosion(Level level, EntityJzahar jzahar) {
		this(ACEntityTypes.IMPLOSION.get(), level);
		moveTo(jzahar.getX(), jzahar.getY(), jzahar.getZ(), jzahar.getYRot(), jzahar.getXRot());
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	public void tick() {
		super.tick();
		List<Entity> list = level().getEntities(this, getBoundingBox().inflate(SIZE));
		if(tickCount > 360) {
			level().levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, blockPosition(), 0);
			playSound(ACSounds.jzahar_blast.get(), 5F, 1F);
			for(Entity entity : list) {
				double scale = (SIZE - entity.distanceTo(this)) / SIZE;
				Vec3 dir = entity.position().subtract(position()).normalize();
				if(entity.isAlive() && !(entity instanceof IOmotholEntity))
					entity.push(dir.x * 2.5D * scale, 1.5D + random.nextDouble(), dir.z * 2.5D * scale);
			}
			discard();
		}
		if(tickCount == 1)
			playSound(ACSounds.jzahar_charge.get(), 5F, 1F);
		for(Entity entity : list) {
			double scale = (SIZE - entity.distanceTo(this)) / SIZE;
			Vec3 dir = entity.position().subtract(position()).normalize();
			if(entity.isAlive() && !(entity instanceof IOmotholEntity)) {
				entity.push(dir.x * -tickCount * 0.0005D * scale, dir.y * -tickCount * 0.0005D * scale, dir.z * -tickCount * 0.0005D * scale);
				if(entity.distanceToSqr(this) <= 4D)
					entity.hurt(damageSources().lightningBolt(), 4F);
			}
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}
}
