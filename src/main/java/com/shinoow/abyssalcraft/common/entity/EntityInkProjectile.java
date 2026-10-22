/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EntityInkProjectile extends ThrowableItemProjectile {
	public EntityInkProjectile(EntityType<? extends EntityInkProjectile> type, Level level) {
		super(type, level);
	}

	public EntityInkProjectile(Level level, LivingEntity owner) {
		super(ACEntityTypes.INK_PROJECTILE.get(), owner, level);
	}

	@Override
	protected Item getDefaultItem() { return Items.INK_SAC; }

	@Override
	public void tick() {
		if(level().isClientSide && ACConfig.particleEntity)
			for(int i = 0; i < 4; i++) {
				var v = getDeltaMovement();
				double x = getX() - v.x * 0.25D + random.nextDouble() * 0.6D - 0.3D;
				double y = getY() - v.y * 0.25D - 0.5D;
				double z = getZ() - v.z * 0.25D + random.nextDouble() * 0.6D - 0.3D;
				level().addParticle(ParticleTypes.BUBBLE, x, y, z, v.x, v.y, v.z);
				level().addParticle(ParticleTypes.SPLASH, x, y, z, v.x, v.y, v.z);
				level().addParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, 0, 0, 0);
			}
		super.tick();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if(level().isClientSide) return;
		var hit = result.getEntity();
		if(hit instanceof LivingEntity living) {
			if(random.nextBoolean() && !EntityUtil.isEntityCoralium(living))
				living.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 100));
			if(random.nextInt(4) == 0) living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
			if(random.nextInt(5) == 0) living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
		}
		if(!(hit instanceof EntityCoraliumSquid)) hit.hurt(damageSources().thrown(this, getOwner()), 2);
		if(ACConfig.hardcoreMode && hit instanceof Player)
			hit.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, getOwner()), 1);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if(!level().isClientSide) discard();
	}
}
