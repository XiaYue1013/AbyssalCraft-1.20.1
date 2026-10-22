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
 *     1.20.1 port - what EntityDreadSpawn, EntityChagarothSpawn, EntityGreaterDreadSpawn and EntityLesserDreadbeast
 *                   had in common
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Dread spawn family: they climb walls like spiders, take no fall damage, are fire immune (entity type) and give
 * the Dread Plague with their hits.
 */
public abstract class EntityDreadSpawnBase extends Monster implements IDreadEntity {

	private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityDreadSpawnBase.class, EntityDataSerializers.BYTE);
	private final String lootTable;
	private final float hardcoreDamage;

	/**
	 * @param lootTable entity loot table name
	 * @param hardcoreDamage extra (absolute) damage to players in Hardcore Mode, times the damage amplifier
	 */
	protected EntityDreadSpawnBase(EntityType<? extends EntityDreadSpawnBase> type, Level level, String lootTable, float hardcoreDamage) {
		super(type, level);
		this.lootTable = lootTable;
		this.hardcoreDamage = hardcoreDamage;
	}

	/** Sets the Hardcore Mode health and damage (1.12.2 applyEntityAttributes) */
	protected void hardcore(double health, double damage) {
		if(ACConfig.hardcoreMode) {
			getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(health);
			getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(damage);
			setHealth(getMaxHealth());
		}
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WallClimberNavigation(this, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(CLIMBING, (byte)0);
	}

	@Override
	public void tick() {
		super.tick();
		if(!level().isClientSide)
			setBesideClimbableBlock(horizontalCollision);
	}

	@Override
	public boolean onClimbable() {
		return isBesideClimbableBlock();
	}

	public boolean isBesideClimbableBlock() {
		return (entityData.get(CLIMBING) & 1) != 0;
	}

	public void setBesideClimbableBlock(boolean climbing) {
		byte b0 = entityData.get(CLIMBING);
		if(climbing)
			b0 = (byte)(b0 | 1);
		else
			b0 &= -2;
		entityData.set(CLIMBING, b0);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	/** If the Dread Plague also goes to other Dread mobs (the Spawn of Cha'garoth didn't check) */
	protected boolean infectsDreadMobs() {
		return false;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if(flag && target instanceof LivingEntity living && (infectsDreadMobs() || !EntityUtil.isEntityDread(living)))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), hardcoreDamage * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	/** If natural spawns stop at 4 of the kind within 32 blocks (1.12.2 getCanSpawnHere) */
	protected boolean capsSpawns() {
		return true;
	}

	@Override
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
		return (!capsSpawns() || level.getEntitiesOfClass(getClass(), getBoundingBox().inflate(32)).size() < 4) && super.checkSpawnRules(level, reason);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ACSounds.dread_spawn_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.dread_spawn_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.dread_spawn_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/" + lootTable);
	}

	/** Spawns a mob of the family at this mob's position (server side) */
	protected static void spawnAt(Entity at, @Nullable Mob mob) {
		if(mob == null) return;
		mob.moveTo(at.getX(), at.getY(), at.getZ(), at.getYRot(), at.getXRot());
		at.level().addFreshEntity(mob);
	}

	/** Shoots a Dread Slug (the ranged attack of the Greater Dread Spawn and the Lesser Dreadbeast) */
	protected void shootDreadSlug(LivingEntity target) {
		EntityDreadSlug slug = new EntityDreadSlug(level(), this);
		double d0 = target.getX() - getX();
		double d1 = target.getY() + target.getEyeHeight() - 1.100000023841858D - slug.getY();
		double d2 = target.getZ() - getZ();
		float f1 = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.2F;
		slug.shoot(d0, d1 + f1, d2, 1.6F, 12.0F);
		playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
		level().addFreshEntity(slug);
	}

	/**
	 * Melee up close, Dread Slugs at range: targets further than ~3.9 blocks, flying or more than 4 blocks above get
	 * the ranged attack (1.12.2 swapped the two AI tasks every tick)
	 */
	protected static abstract class RangedOrMelee extends EntityDreadSpawnBase implements RangedAttackMob {

		private final Goal rangedAttack, meleeAttack;
		private Boolean ranged;

		protected RangedOrMelee(EntityType<? extends RangedOrMelee> type, Level level, String lootTable, float hardcoreDamage, double rangedSpeed,
				float rangedDistance) {
			super(type, level, lootTable, hardcoreDamage);
			rangedAttack = new RangedAttackGoal(this, rangedSpeed, 20, rangedDistance);
			meleeAttack = new MeleeAttackGoal(this, 0.35D, true);
		}

		@Override
		public void aiStep() {
			super.aiStep();
			LivingEntity target = getTarget();
			if(!level().isClientSide && target != null) {
				boolean useRanged = distanceToSqr(target) > 15D || target instanceof net.minecraft.world.entity.FlyingMob || target.getY() > getY() + 4D;
				if(ranged == null || ranged != useRanged) {
					ranged = useRanged;
					goalSelector.removeGoal(useRanged ? meleeAttack : rangedAttack);
					goalSelector.addGoal(2, useRanged ? rangedAttack : meleeAttack);
				}
			}
		}

		@Override
		public void performRangedAttack(LivingEntity target, float velocity) {
			shootDreadSlug(target);
		}
	}
}
