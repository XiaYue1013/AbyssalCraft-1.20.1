/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAntiCreeper, EntityAIAntiCreeperSwell)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.anti;

import java.util.EnumSet;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Anti-Creeper: a creeper with a shorter fuse and a size 10 explosion, hunting creepers and players. Killed by an
 * Anti-Skeleton it drops a music disc.
 */
public class EntityAntiCreeper extends Monster implements IAntiEntity, PowerableMob {

	private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EntityAntiCreeper.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(EntityAntiCreeper.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IGNITED = SynchedEntityData.defineId(EntityAntiCreeper.class, EntityDataSerializers.BOOLEAN);
	private int oldSwell;
	private int swell;
	private int maxSwell = 30;
	private int explosionRadius = 10;

	public EntityAntiCreeper(EntityType<? extends EntityAntiCreeper> type, Level level) {
		super(type, level);
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(80.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 40.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new SwellGoal());
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
		goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(3, new HurtByTargetGoal(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(STATE, -1);
		entityData.define(POWERED, false);
		entityData.define(IGNITED, false);
	}

	@Override
	public int getMaxFallDistance() {
		return getTarget() == null ? 3 : 3 + (int)(getHealth() - 1.0F);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		boolean flag = super.causeFallDamage(distance, multiplier, source);
		swell += (int)(distance * 1.5F);
		if(swell > maxSwell - 5)
			swell = maxSwell - 5;
		return flag;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(isPowered())
			tag.putBoolean("powered", true);
		tag.putShort("Fuse", (short)maxSwell);
		tag.putByte("ExplosionRadius", (byte)explosionRadius);
		tag.putBoolean("ignited", isIgnited());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		entityData.set(POWERED, tag.getBoolean("powered"));
		if(tag.contains("Fuse", 99))
			maxSwell = tag.getShort("Fuse");
		if(tag.contains("ExplosionRadius", 99))
			explosionRadius = tag.getByte("ExplosionRadius");
		if(tag.getBoolean("ignited"))
			ignite();
	}

	@Override
	public void tick() {
		if(isAlive()) {
			oldSwell = swell;
			if(isIgnited())
				setSwellDir(1);
			int i = getSwellDir();
			if(i > 0 && swell == 0)
				playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
			swell += i;
			if(swell < 0)
				swell = 0;
			if(swell >= maxSwell) {
				swell = maxSwell;
				explode();
			}
		}
		super.tick();
	}

	@Override
	protected void doPush(Entity entity) {
		if(!AntiMatter.collide(this, entity, Creeper.class))
			super.doPush(entity);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.CREEPER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

	/** Killed by an Anti-Skeleton, it drops a music disc */
	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
		super.dropCustomDeathLoot(source, looting, recentlyHit);
		if(source.getEntity() instanceof AntiUndead.AntiSkeleton)
			BuiltInRegistries.ITEM.getTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).flatMap(tag -> tag.getRandomElement(random))
			.ifPresent(disc -> spawnAtLocation(disc.value()));
	}

	/** It only explodes, its touch does no damage */
	@Override
	public boolean doHurtTarget(Entity target) {
		return true;
	}

	@Override
	public boolean isPowered() {
		return entityData.get(POWERED);
	}

	public float getSwelling(float partialTicks) {
		return Mth.lerp(partialTicks, oldSwell, swell) / (maxSwell - 2);
	}

	public int getSwellDir() {
		return entityData.get(STATE);
	}

	public void setSwellDir(int state) {
		entityData.set(STATE, state);
	}

	@Override
	public void thunderHit(ServerLevel level, LightningBolt bolt) {
		super.thunderHit(level, bolt);
		entityData.set(POWERED, true);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(stack.is(Items.FLINT_AND_STEEL)) {
			level().playSound(player, getX(), getY(), getZ(), SoundEvents.FLINTANDSTEEL_USE, getSoundSource(), 1.0F, random.nextFloat() * 0.4F + 0.8F);
			if(!level().isClientSide) {
				ignite();
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	private void explode() {
		if(!level().isClientSide) {
			float f = isPowered() ? 2.0F : 1.0F;
			dead = true;
			level().explode(this, getX(), getY(), getZ(), explosionRadius * f, Level.ExplosionInteraction.MOB);
			discard();
		}
	}

	public boolean isIgnited() {
		return entityData.get(IGNITED);
	}

	public void ignite() {
		entityData.set(IGNITED, true);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/anti_creeper");
	}

	/** EntityAIAntiCreeperSwell / SwellGoal: swells near its target */
	private class SwellGoal extends Goal {

		@Nullable
		private LivingEntity target;

		SwellGoal() {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			LivingEntity living = getTarget();
			return getSwellDir() > 0 || living != null && distanceToSqr(living) < 9.0D;
		}

		@Override
		public void start() {
			getNavigation().stop();
			target = getTarget();
		}

		@Override
		public void stop() {
			target = null;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if(target == null || distanceToSqr(target) > 49.0D || !getSensing().hasLineOfSight(target))
				setSwellDir(-1);
			else
				setSwellDir(1);
		}
	}
}
