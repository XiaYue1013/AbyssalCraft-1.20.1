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

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

/**
 * Sacthoth, Harbinger of Doom: the shadow boss summoned in the Dark Realm. Climbs walls, blinds everyone around, keeps
 * teleporting after players who run, breathes shadow flames; explosions and projectiles don't hurt him. His death
 * releases shadows and leaves his Soul Harvesting Blade.
 */
public class EntitySacthoth extends EntityShadowMob {

	private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntitySacthoth.class, EntityDataSerializers.BYTE);
	public int deathTicks;
	private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

	public EntitySacthoth(EntityType<? extends EntitySacthoth> type, Level level) {
		super(type, level, "sacthoth", 0.6F);
		hardcore(600.0D, 30.0D);
		xpReward = 0; // dropped during the death animation instead (5,000)
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 160.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.4D)
				.add(Attributes.MOVEMENT_SPEED, 0.799D).add(Attributes.MAX_HEALTH, 300.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
	protected Component getTypeName() {
		return super.getTypeName().copy().withStyle(ChatFormatting.DARK_RED);
	}

	@Override
	public void tick() {
		super.tick();
		if(!level().isClientSide)
			entityData.set(CLIMBING, (byte)(horizontalCollision ? 1 : 0));
	}

	@Override
	public boolean onClimbable() {
		return (entityData.get(CLIMBING) & 1) != 0;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		bossInfo.setProgress(getHealth() / getMaxHealth());
		float health = getHealth(), max = getMaxHealth();
		if(health > max * 0.75)
			bossInfo.setColor(BossEvent.BossBarColor.BLUE);
		else if(health < max * 0.75 && health > max / 2)
			bossInfo.setColor(BossEvent.BossBarColor.GREEN);
		else if(health < max / 2 && health > max / 4)
			bossInfo.setColor(BossEvent.BossBarColor.YELLOW);
		else if(health < max / 4 && health > 0)
			bossInfo.setColor(BossEvent.BossBarColor.RED);
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	protected void onHit(LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60));
	}

	@Override
	protected float hardcoreDamage() {
		return 4.5F;
	}

	@Override
	protected boolean halloweenPumpkin() {
		return false;
	}

	@Override
	protected double halloweenDamage() {
		return 8.0D;
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLAZE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BLAZE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.sacthoth_death.get();
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	@Override
	public int getArmorValue() {
		return 20;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return false;
	}

	/**
	 * Big hits are cut to 10 - 19; suffocating makes him teleport; explosions and projectiles don't hurt him (he tells
	 * the player so; 1.12.2 only showed that on the client, where it rarely happened)
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount > 30) amount = 10 + random.nextInt(10);
		if(source.is(DamageTypes.IN_WALL)) {
			teleportRandomly();
			return false;
		} else if(source.is(DamageTypeTags.IS_EXPLOSION)) {
			tell(source, "message.sacthoth.damage.explosion");
			return false;
		} else if(source.is(DamageTypeTags.IS_PROJECTILE)) {
			tell(source, "message.sacthoth.damage.projectile");
			return false;
		} else if(source.is(DamageTypes.FELL_OUT_OF_WORLD) && getY() <= level().getMinBuildHeight() && !level().isClientSide) {
			discard();
			return false;
		}
		return super.hurt(source, amount);
	}

	private void tell(DamageSource source, String key) {
		if(ACConfig.showBossDialogs && !level().isClientSide && source.getEntity() instanceof Player player)
			player.sendSystemMessage(Component.literal("Sacthoth: ").append(Component.translatable(key)));
	}

	@Override
	public void kill() {
		super.hurt(damageSources().fellOutOfWorld(), Float.MAX_VALUE);
	}

	protected boolean teleportRandomly() {
		return teleportNear(getX() + (random.nextDouble() - 0.5D) * 64.0D, getY() + (random.nextInt(64) - 32), getZ() + (random.nextDouble() - 0.5D) * 64.0D);
	}

	/** Like an enderman: onto the ground below, if there's room; leaves a trail of smoke */
	protected boolean teleportNear(double x, double y, double z) {
		EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
		if(event.isCanceled()) return false;
		double ox = getX(), oy = getY(), oz = getZ();
		if(!randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false))
			return false;
		if(ACConfig.particleEntity && level() instanceof ServerLevel server)
			for(int l = 0; l < 128; ++l) {
				double d6 = l / 127.0D;
				server.sendParticles(ParticleTypes.LARGE_SMOKE, ox + (getX() - ox) * d6 + (random.nextDouble() - 0.5D) * getBbWidth() * 2.0D,
						oy + (getY() - oy) * d6 + random.nextDouble() * getBbHeight(), oz + (getZ() - oz) * d6 + (random.nextDouble() - 0.5D) * getBbWidth() * 2.0D,
						1, 0, 0, 0, 0);
			}
		level().playSound(null, ox, oy, oz, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1.0F, 1.0F);
		playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		return true;
	}

	@Override
	public void die(DamageSource source) {
		bossInfo.setProgress(getHealth() / getMaxHealth());
		super.die(source);
	}

	/** Shadows pushed into him don't move while he dies */
	@Override
	protected void doPush(Entity entity) {
		if(deathTicks == 0)
			super.doPush(entity);
	}

	@Override
	public void aiStep() {
		if(!level().isClientSide) {
			if(ACConfig.sacthothHealingAmount > 0 && isAlive() && tickCount % ACConfig.sacthothHealingPace == 0)
				heal(ACConfig.sacthothHealingAmount);

			if(deathTicks == 0)
				for(Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30.0D), p -> p.isAlive() && !p.isCreative()))
					player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));

			// players who run get him teleported after them
			Player player = level().getNearestPlayer(this, 160D);
			if(player != null && player.distanceTo(this) >= 50D && !player.isCreative()) {
				if(player.getX() - getX() > 50)
					teleportNear(player.getX() + 30, player.getY(), player.getZ());
				if(player.getX() - getX() < -50)
					teleportNear(player.getX() - 30, player.getY(), player.getZ());
				if(player.getZ() - getZ() > 50)
					teleportNear(player.getX(), player.getY(), player.getZ() - 30);
				if(player.getZ() - getZ() < -50)
					teleportNear(player.getX(), player.getY(), player.getZ() + 30);
				if(Math.abs(player.getY() - getY()) > 50)
					teleportNear(player.getX(), player.getY(), player.getZ());
			}

			breatheShadowFlame(7.5D);
		}
		super.aiStep();
	}

	@Override
	protected int breathParticles() {
		return 45;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(deathTicks > 0)
			tag.putInt("DeathTicks", deathTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		deathTicks = tag.getInt("DeathTicks");
		if(hasCustomName())
			bossInfo.setName(getDisplayName());
	}

	/** 10 seconds: smoke, 5,000 XP, shadow materials; 11 Shadow Creatures, 6 Shadow Monsters and a Shadow Beast break free; then the blade */
	@Override
	protected void tickDeath() {
		++deathTicks;

		if(level().isClientSide) {
			if(deathTicks <= 200 && ACConfig.particleEntity) {
				float f = (random.nextFloat() - 0.5F) * 8.0F;
				float f1 = (random.nextFloat() - 0.5F) * 4.0F;
				float f2 = (random.nextFloat() - 0.5F) * 8.0F;
				level().addParticle(ParticleTypes.SMOKE, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
				level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
				level().addParticle(ParticleTypes.POOF, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
				if(deathTicks >= 190)
					level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			}
			return;
		}

		if(deathTicks > 150 && deathTicks % 5 == 0) {
			int i = 500;
			while(i > 0) {
				int j = ExperienceOrb.getExperienceValue(i);
				i -= j;
				level().addFreshEntity(new ExperienceOrb(level(), getX(), getY(), getZ(), j));
				if(deathTicks == 160 || deathTicks == 180) {
					drop(new ItemStack(ACItems.shadow_fragment.get(), 4));
					drop(new ItemStack(ACItems.shadow_shard.get(), 2));
					drop(new ItemStack(ACItems.shadow_gem.get()));
					drop(new ItemStack(ACItems.shard_of_oblivion.get()));
				}
			}
		}
		if(deathTicks >= 100 && deathTicks <= 110)
			release(ACEntityTypes.SHADOW_CREATURE.get());
		if(deathTicks >= 160 && deathTicks <= 165)
			release(ACEntityTypes.SHADOW_MONSTER.get());
		if(deathTicks == 200) {
			release(ACEntityTypes.SHADOW_BEAST.get());
			if(ACItems.sacthoths_soul_harvesting_blade.isPresent())
				level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ACItems.sacthoths_soul_harvesting_blade.get())));
			remove(RemovalReason.KILLED);
		}
	}

	private void release(EntityType<? extends Mob> type) {
		Mob mob = type.create(level());
		if(mob == null) return;
		mob.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
		level().addFreshEntity(mob);
	}

	private void drop(ItemStack stack) {
		level().addFreshEntity(new ItemEntity(level(), getX() + posneg(3), getY() + random.nextInt(3), getZ() + posneg(3), stack));
	}

	private int posneg(int num) {
		return random.nextBoolean() ? random.nextInt(num) : -1 * random.nextInt(num);
	}

	/** It's always night when Sacthoth arrives */
	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		if(level.getLevel().isDay())
			level.getLevel().setDayTime(14000L);
		return data;
	}
}
