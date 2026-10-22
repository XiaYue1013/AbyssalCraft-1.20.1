/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityShadowCreature, EntityShadowMonster, EntityShadowBeast)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The shadow mobs of the Darklands, Omothol and the Dark Realm: fire immune, poison immune, see-through in the dark
 * and smoking in the light. The Shadow Beast (and Sacthoth) also breathe shadow flames.
 */
public class EntityShadowMob extends EntityACMonster implements IOmotholEntity {

	/** Client: breath particles */
	private static final byte BREATH_EVENT = 23;
	private final float pitch;
	protected int shadowFlameShootTimer;

	/** @param pitch voice pitch offset (1.3 Shadow Creature, 0.6 Shadow Beast, 1.0 Shadow Monster) */
	protected EntityShadowMob(EntityType<? extends EntityShadowMob> type, Level level, String lootTable, float pitch) {
		super(type, level, lootTable);
		this.pitch = pitch;
	}

	public static EntityShadowMob creature(EntityType<? extends EntityShadowMob> type, Level level) {
		EntityShadowMob mob = new EntityShadowMob(type, level, "shadow_creature", 1.3F);
		mob.hardcore(40.0D, 12.0D);
		return mob;
	}

	public static EntityShadowMob monster(EntityType<? extends EntityShadowMob> type, Level level) {
		EntityShadowMob mob = new EntityShadowMob(type, level, "shadow_monster", 1.0F);
		mob.hardcore(100.0D, 16.0D);
		return mob;
	}

	public static AttributeSupplier.Builder creatureAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.2D).add(Attributes.ATTACK_DAMAGE, 6.0D);
	}

	public static AttributeSupplier.Builder monsterAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.2D).add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.ATTACK_DAMAGE, 8.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(5, new FleeSunGoal(this, 0.35D));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + pitch;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.shadow_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.shadow_death.get();
	}

	@Override
	public MobType getMobType() {
		return AbyssalCraftAPI.SHADOW;
	}

	/** Smoke in the light */
	@Override
	public void aiStep() {
		if(level().isClientSide && ACConfig.particleEntity) {
			float brightness = getLightLevelDependentMagicValue();
			for(int i = 0; i < 2 * (brightness > 0.1F ? brightness : 0); ++i)
				level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + (random.nextDouble() - 0.5D) * getBbWidth(), getY() + random.nextDouble() * getBbHeight(),
						getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0.0D, 0.0D, 0.0D);
		}
		super.aiStep();
	}

	/**
	 * The shadow flame breath: within 8 blocks of its target, every 20 seconds it stands still for 5 seconds breathing
	 * at what it looks at: Shadow damage, Blindness and Slowness II
	 * @param maxDamage damage at the breather (less further away)
	 */
	protected void breatheShadowFlame(double maxDamage) {
		if(getTarget() != null && distanceToSqr(getTarget()) <= 64D && shadowFlameShootTimer <= -300) shadowFlameShootTimer = 100;
		if(shadowFlameShootTimer > 0) {
			setDeltaMovement(getDeltaMovement().multiply(0.05D, 1.0D, 0.05D));
			level().broadcastEntityEvent(this, BREATH_EVENT);
			if(tickCount % 5 == 0)
				level().playSound(null, BlockPos.containing(getX() + 0.5D, getEyeY(), getZ() + 0.5D), SoundEvents.GHAST_SHOOT, getSoundSource(),
						0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
			Entity target = LookTarget.find(this, 4D, 8D, 4.0F, e -> true);
			if(target != null) {
				// (1.12.2 also hit the breather itself when it stood next to the target)
				for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.0D), e -> e.isAlive() && e != this))
					if(random.nextInt(3) == 0 && entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.shadow), breathDamage(entity, maxDamage))) {
						entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
						entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
					}
				if(target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.shadow), breathDamage(target, maxDamage)) && target instanceof LivingEntity living) {
					living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200));
					living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
				}
			}
		}
		--shadowFlameShootTimer;
	}

	/** Ticks of breathing left (negative: ticks since the last breath; it can breathe again at -300) */
	public int getBreathTimer() {
		return shadowFlameShootTimer;
	}

	public void setBreathTimer(int timer) {
		shadowFlameShootTimer = timer;
	}

	private float breathDamage(Entity entity, double maxDamage) {
		return Math.max(0.0F, (float)(maxDamage - distanceTo(entity)));
	}

	protected int breathParticles() {
		return 15;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == BREATH_EVENT) {
			Vec3 vector = getLookAngle();
			for(int i = 0; i < breathParticles(); i++) {
				double spread = 5.0D + random.nextDouble() * 2.5D;
				double velocity = 0.5D + random.nextDouble() * 0.5D;
				double dx = (vector.x + random.nextGaussian() * 0.007499999832361937D * spread) * velocity;
				double dy = (vector.y + random.nextGaussian() * 0.007499999832361937D * spread) * velocity;
				double dz = (vector.z + random.nextGaussian() * 0.007499999832361937D * spread) * velocity;
				level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + random.nextDouble() - 0.5D, getEyeY() + random.nextDouble() - 0.5D,
						getZ() + random.nextDouble() - 0.5D, dx, dy, dz);
			}
		} else
			super.handleEntityEvent(id);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("BreathTimer", shadowFlameShootTimer);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		shadowFlameShootTimer = tag.getInt("BreathTimer");
	}

	/** Shadow Beast: big, hits hard, breathes shadow flames */
	public static class ShadowBeast extends EntityShadowMob {

		public ShadowBeast(EntityType<? extends ShadowBeast> type, Level level) {
			super(type, level, "shadow_beast", 0.6F);
			hardcore(200.0D, 20.0D);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.3D).add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 10.0D);
		}

		@Override
		protected float hardcoreDamage() {
			return 3.0F;
		}

		@Override
		public boolean doHurtTarget(Entity target) {
			swing(InteractionHand.MAIN_HAND);
			swing(InteractionHand.OFF_HAND);
			return super.doHurtTarget(target);
		}

		@Override
		public void aiStep() {
			if(!level().isClientSide)
				breatheShadowFlame(4.5D);
			super.aiStep();
		}
	}
}
