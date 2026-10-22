/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAntiZombie, EntityAntiAbyssalZombie, EntityAntiSkeleton, EntityAntiSpider)
 *     1.20.1 port - built on the vanilla mobs, with what 1.12.2 didn't have switched off
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.anti;

import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.common.entity.EntityACMonster;
import com.shinoow.abyssalcraft.common.entity.EntityAbyssalZombie;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** The hostile antimatter mobs built on vanilla ones */
public class AntiUndead {

	private static final UUID HALLOWEEN_BOOST_UUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");

	private static ResourceLocation loot(String name) {
		return AbyssalCraft.loc("entities/" + name);
	}

	private static void hardcoreHit(Mob mob, Entity target, float damage) {
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(mob.level(), AbyssalCraftAPI.hardcore_attack, mob), damage * (float) Math.max(ACConfig.damageAmpl, 1.0D));
	}

	private static void hardcore(Mob mob, double health, double damage) {
		if(ACConfig.hardcoreMode) {
			if(health > 0) mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
			if(damage > 0) mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
			mob.setHealth(mob.getMaxHealth());
		}
	}

	private static void halloweenBoost(Mob mob, double amount) {
		AttributeInstance attribute = mob.getAttribute(Attributes.ATTACK_DAMAGE);
		attribute.removeModifier(HALLOWEEN_BOOST_UUID);
		if(EntityACMonster.isHalloween())
			attribute.addPermanentModifier(new AttributeModifier(HALLOWEEN_BOOST_UUID, "Halloween Attack Damage Boost", amount, AttributeModifier.Operation.ADDITION));
	}

	/**
	 * Anti-Zombie: a zombie (that doesn't call for help, burn in daylight, drown into a Drowned or turn villagers into
	 * zombie villagers) hunting zombies, players and villagers
	 */
	public static class AntiZombie extends Zombie implements IAntiEntity {

		public AntiZombie(EntityType<? extends AntiZombie> type, Level level) {
			super(type, level);
			hardcore(this, 0, 6.0D);
			if(ACConfig.hardcoreMode)
				getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(80.0D);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Zombie.createAttributes().add(Attributes.FOLLOW_RANGE, 40.0D).add(Attributes.ATTACK_DAMAGE, 3.0D);
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(0, new FloatGoal(this));
			goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
			goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
			goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, false, 4, this::canBreakDoors));
			goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
			goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
			goalSelector.addGoal(8, new RandomLookAroundGoal(this));
			targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, true, e -> !(e instanceof IAntiEntity)));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
			targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Zombie.class))
				super.doPush(entity);
		}

		@Override
		public boolean doHurtTarget(Entity target) {
			boolean flag = super.doHurtTarget(target);
			hardcoreHit(this, target, 1.5F);
			return flag;
		}

		@Override
		protected boolean convertsInWater() {
			return false;
		}

		@Override
		protected boolean isSunSensitive() {
			return false;
		}

		/** No zombie villagers */
		@Override
		public boolean killedEntity(ServerLevel level, LivingEntity entity) {
			return true;
		}

		/** No reinforcements (1.12.2 registered the attribute but never used it) */
		@Override
		protected void handleAttributes(float difficulty) {
			super.handleAttributes(difficulty);
			AttributeInstance reinforcements = getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
			reinforcements.removeModifiers();
			reinforcements.setBaseValue(0.0D);
		}

		@Nullable
		@Override
		public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
			data = super.finalizeSpawn(level, difficulty, reason, data, tag);
			setCanPickUpLoot(ACConfig.hardcoreMode || getRandom().nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
			if(ACConfig.hardcoreMode)
				EntityUtil.suitUp(this, false);
			halloweenBoost(this, 3.0D);
			return data;
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_zombie");
		}
	}

	/** Abyssal Anti-Zombie: hunts Anti-Zombies (killed ones join it) and Abyssal Zombies */
	public static class AntiAbyssalZombie extends AntiZombie {

		public AntiAbyssalZombie(EntityType<? extends AntiAbyssalZombie> type, Level level) {
			super(type, level);
			hardcore(this, 100.0D, 12.0D);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Zombie.createAttributes().add(Attributes.FOLLOW_RANGE, 42.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.0D).add(Attributes.MAX_HEALTH, 50.0D)
					.add(Attributes.ATTACK_DAMAGE, 6.0D);
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(0, new FloatGoal(this));
			goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, true));
			goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
			goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D));
			goalSelector.addGoal(8, new RandomLookAroundGoal(this));
			goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
			goalSelector.addGoal(8, new LookAtPlayerGoal(this, AntiZombie.class, 8.0F));
			goalSelector.addGoal(8, new LookAtPlayerGoal(this, AntiSkeleton.class, 8.0F));
			targetSelector.addGoal(1, new HurtByTargetGoal(this));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AntiZombie.class, true, e -> !(e instanceof AntiAbyssalZombie)));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityAbyssalZombie.class, true));
			targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
		}

		@Override
		protected void doPush(Entity entity) {
			// (not super: Anti-Zombies explode on vanilla zombies, this one on Abyssal Zombies)
			if(!AntiMatter.collide(this, entity, EntityAbyssalZombie.class))
				entity.push(this);
		}

		@Override
		public boolean supportsBreakDoorGoal() {
			return false;
		}

		/** An Anti-Zombie it kills becomes an Abyssal Anti-Zombie (Hard, or half the time on Normal) */
		@Override
		public boolean killedEntity(ServerLevel level, LivingEntity entity) {
			if((level.getDifficulty() == Difficulty.HARD || level.getDifficulty() == Difficulty.NORMAL) && entity instanceof AntiZombie
					&& !(entity instanceof AntiAbyssalZombie) && (level.getDifficulty() == Difficulty.HARD || random.nextBoolean())) {
				AntiAbyssalZombie zombie = ACEntityTypes.ABYSSAL_ANTI_ZOMBIE.get().create(level);
				if(zombie != null) {
					zombie.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
					zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.CONVERSION, null, null);
					zombie.setBaby(entity.isBaby());
					entity.discard();
					level.addFreshEntity(zombie);
					level.levelEvent(null, 1026, blockPosition(), 0);
				}
				return false;
			}
			return true;
		}

		@Override
		protected SoundEvent getAmbientSound() {
			return ACSounds.abyssal_zombie_ambient.get();
		}

		@Override
		protected SoundEvent getHurtSound(DamageSource source) {
			return ACSounds.abyssal_zombie_hurt.get();
		}

		@Override
		protected SoundEvent getDeathSound() {
			return ACSounds.abyssal_zombie_death.get();
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("abyssal_anti_zombie");
		}
	}

	/** Anti-Skeleton: a skeleton archer hunting skeletons and players, not burning in daylight */
	public static class AntiSkeleton extends AbstractSkeleton implements IAntiEntity {

		public AntiSkeleton(EntityType<? extends AntiSkeleton> type, Level level) {
			super(type, level);
			hardcore(this, 80.0D, 0);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 40.0D);
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(1, new FloatGoal(this));
			goalSelector.addGoal(2, new RestrictSunGoal(this));
			goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
			goalSelector.addGoal(6, new RandomLookAroundGoal(this));
			targetSelector.addGoal(1, new HurtByTargetGoal(this));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Skeleton.class, true));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Skeleton.class))
				super.doPush(entity);
		}

		@Override
		protected boolean isSunBurnTick() {
			return false;
		}

		@Override
		protected SoundEvent getAmbientSound() {
			return SoundEvents.SKELETON_AMBIENT;
		}

		@Override
		protected SoundEvent getHurtSound(DamageSource source) {
			return SoundEvents.SKELETON_HURT;
		}

		@Override
		protected SoundEvent getDeathSound() {
			return SoundEvents.SKELETON_DEATH;
		}

		@Override
		protected SoundEvent getStepSound() {
			return SoundEvents.SKELETON_STEP;
		}

		@Nullable
		@Override
		public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
			data = super.finalizeSpawn(level, difficulty, reason, data, tag);
			if(ACConfig.hardcoreMode)
				EntityUtil.suitUp(this, true);
			setCanPickUpLoot(ACConfig.hardcoreMode || random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
			return data;
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_skeleton");
		}
	}

	/** Anti-Spider: a spider hunting spiders, players and iron golems in the dark */
	public static class AntiSpider extends Spider implements IAntiEntity {

		public AntiSpider(EntityType<? extends AntiSpider> type, Level level) {
			super(type, level);
			hardcore(this, 64.0D, 0);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Spider.createAttributes().add(Attributes.MAX_HEALTH, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896D);
		}

		@Override
		protected void registerGoals() {
			super.registerGoals();
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Spider.class, true, e -> !(e instanceof IAntiEntity)) {
				@Override
				public boolean canUse() {
					return mob.getLightLevelDependentMagicValue() < 0.5F && super.canUse();
				}
			});
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Spider.class))
				super.doPush(entity);
		}

		@Override
		public boolean doHurtTarget(Entity target) {
			boolean flag = super.doHurtTarget(target);
			hardcoreHit(this, target, 1.5F);
			return flag;
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_spider");
		}
	}
}
