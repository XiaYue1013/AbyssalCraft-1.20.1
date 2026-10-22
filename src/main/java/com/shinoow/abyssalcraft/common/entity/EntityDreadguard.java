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

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Dreadguard: a big armored Dreadlands undead that spreads the Dread Plague. Now and then it stops and vomits at what it
 * looks at, hurting everything around that (Dread damage, Dread Plague).
 */
public class EntityDreadguard extends Monster implements IDreadEntity {

	private static final UUID attackDamageBoostUUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
	private static final AttributeModifier attackDamageBoost = new AttributeModifier(attackDamageBoostUUID, "Halloween Attack Damage Boost", 5.0D, AttributeModifier.Operation.ADDITION);
	private static final EntityDataAccessor<Integer> BARF_TIMER = SynchedEntityData.defineId(EntityDreadguard.class, EntityDataSerializers.INT);
	/** Client: starts the vomit particles */
	private static final byte BARF_EVENT = 23;

	public EntityDreadguard(EntityType<? extends EntityDreadguard> type, Level level) {
		super(type, level);
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(240.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.MAX_HEALTH, 120.0D)
				.add(Attributes.ATTACK_DAMAGE, 10.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(BARF_TIMER, 0);
	}

	public int getBarfTimer() {
		return entityData.get(BARF_TIMER);
	}

	public void setBarfTimer(int time) {
		entityData.set(BARF_TIMER, time);
	}

	public void decrementBarfTimer() {
		setBarfTimer(getBarfTimer() - 1);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);

		if(flag && target instanceof LivingEntity living && !EntityUtil.isEntityDread(living))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));

		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 3 * (float) Math.max(ACConfig.damageAmpl, 1.0D));

		return flag;
	}

	@Override
	public int getArmorValue() {
		return Math.min(super.getArmorValue() + 2, 20);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ACSounds.dreadguard_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.dreadguard_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.dreadguard_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/dreadguard");
	}

	@Override
	public void aiStep() {
		if(!level().isClientSide) {
			int timer = getBarfTimer();
			if(getTarget() != null && distanceToSqr(getTarget()) <= 64D && timer <= -200) setBarfTimer(60);

			if(timer > 0)
				barf(timer);

			decrementBarfTimer();
		} else if(getBarfTimer() > 0)
			setDeltaMovement(0, getDeltaMovement().y, 0);

		super.aiStep();
	}

	/** Stands still, vomits and hurts what's around the entity it looks at */
	private void barf(int timer) {
		setDeltaMovement(0, getDeltaMovement().y, 0);
		setSpeed(0F);
		level().broadcastEntityEvent(this, BARF_EVENT);
		if(tickCount % 5 == 0 && timer > 30)
			level().playSound(null, BlockPos.containing(getX() + 0.5D, getEyeY() + 0.5D, getZ() + 0.5D), ACSounds.dreadguard_barf.get(), getSoundSource(),
					0.7F + random.nextFloat(), random.nextFloat() * 0.5F + 0.2F);
		Entity target = LookTarget.find(this, 4D, 8D, 4.0F, e -> true);
		if(target != null) {
			// (1.12.2 also hit the Dreadguard itself when it stood next to the target)
			for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.0D),
					e -> e.isAlive() && e != this))
				if(random.nextInt(3) == 0)
					if(entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.dread), barfDamage(entity)))
						entity.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));

			if(target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.dread), barfDamage(target)))
				if(target instanceof LivingEntity living)
					living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
		}
	}

	/** 4.5 at the Dreadguard, less further away (1.12.2 also passed the negative values on, which hurt nothing) */
	private float barfDamage(Entity entity) {
		return Math.max(0.0F, (float)(4.5D - distanceTo(entity)));
	}

	protected void addMouthParticles() {
		Vec3 vector = getLookAngle();

		double px = getX();
		double py = getEyeY();
		double pz = getZ();

		ItemParticleOption shard = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItems.dreaded_shard_of_abyssalnite.get()));
		ItemParticleOption fragment = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItems.dread_fragment.get()));
		for(int i = 0; i < 15; i++) {
			double dx = vector.x;
			double dy = vector.y;
			double dz = vector.z;

			double spread = 5.0D + random.nextDouble() * 2.5D;
			double velocity = 0.5D + random.nextDouble() * 0.5D;

			dx += random.nextGaussian() * 0.007499999832361937D * spread;
			dy += random.nextGaussian() * 0.007499999832361937D * spread;
			dz += random.nextGaussian() * 0.007499999832361937D * spread;
			dx *= velocity;
			dy *= velocity;
			dz *= velocity;

			level().addParticle(shard, px + random.nextDouble() - 0.5D, py + random.nextDouble() - 0.5D, pz + random.nextDouble() - 0.5D, dx, dy, dz);
			level().addParticle(fragment, px + random.nextDouble() - 0.5D, py + random.nextDouble() - 0.5D, pz + random.nextDouble() - 0.5D, dx, dy, dz);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == BARF_EVENT) addMouthParticles();
		else
			super.handleEntityEvent(id);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		tag.putInt("BarfTimer", getBarfTimer());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		setBarfTimer(tag.getInt("BarfTimer"));
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);

		setItemSlot(EquipmentSlot.HEAD, new ItemStack(ACItems.dreaded_abyssalnite_helmet.get()));
		setItemSlot(EquipmentSlot.CHEST, new ItemStack(ACItems.dreaded_abyssalnite_chestplate.get()));
		setItemSlot(EquipmentSlot.LEGS, new ItemStack(ACItems.dreaded_abyssalnite_leggings.get()));
		setItemSlot(EquipmentSlot.FEET, new ItemStack(ACItems.dreaded_abyssalnite_boots.get()));

		AttributeInstance attribute = getAttribute(Attributes.ATTACK_DAMAGE);
		LocalDate date = LocalDate.now();

		attribute.removeModifier(attackDamageBoost);

		if(date.getMonthValue() == 10 && date.getDayOfMonth() == 31) {
			attribute.addPermanentModifier(attackDamageBoost);
			if(random.nextFloat() < 0.25F) {
				setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				setDropChance(EquipmentSlot.HEAD, 0.0F);
			}
		}

		return data;
	}
}
