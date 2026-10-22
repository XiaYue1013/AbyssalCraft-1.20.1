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

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Dread Slug: the spit of the Greater Dread Spawn, Lesser Dreadbeast and Cha'garoth (6 damage and the Dread Plague).
 * Cha'garoth also throws mobs riding them; they jump off when the slug lands.
 */
public class EntityDreadSlug extends ThrowableItemProjectile {

	public EntityDreadSlug(EntityType<? extends EntityDreadSlug> type, Level level) {
		super(type, level);
	}

	public EntityDreadSlug(Level level, LivingEntity thrower) {
		super(ACEntityTypes.DREAD_SLUG.get(), thrower, level);
	}

	@Override
	protected Item getDefaultItem() {
		return ACItems.dread_fragment.get();
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if(level().isClientSide) return;
		if(isVehicle() && (isInWall() || result instanceof BlockHitResult && result.getType() == HitResult.Type.BLOCK)) {
			for(Entity entity : getPassengers()) {
				entity.stopRiding();
				entity.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
				entity.setDeltaMovement(entity.getDeltaMovement().add(random.nextGaussian(), 0.7D, random.nextGaussian()));
			}
			discard();
			return;
		}
		if(result instanceof EntityHitResult hit && tickCount > 5) {
			Entity target = hit.getEntity();
			if(isPassengerOfSameVehicle(target) || hasPassenger(target)) return;
			if(ACConfig.hardcoreMode && target instanceof Player)
				target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, getOwner() != null ? getOwner() : this), 1F);
			if(target instanceof LivingEntity living && !EntityUtil.isEntityDread(living)) {
				living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
				target.hurt(damageSources().thrown(this, getOwner()), 6);
				discard();
			}
		} else if(result.getType() == HitResult.Type.BLOCK && !isVehicle())
			// (1.12.2 slugs that hit a block flew on through it)
			discard();
	}
}
