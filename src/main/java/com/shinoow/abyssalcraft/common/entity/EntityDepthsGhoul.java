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

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.entity.ICoraliumEntity;
import com.shinoow.abyssalcraft.common.entity.ai.LookAtTypeGoal;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Depths Ghoul: a tall Coralium undead. One in five is one of the named ghouls (Pete, Mr. Wilson, Dr. Orange), with
 * their own texture, sounds and loot.
 */
public class EntityDepthsGhoul extends EntityACZombie implements ICoraliumEntity {

	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityDepthsGhoul.class, EntityDataSerializers.INT);
	private static final String[] NAMES = {null, "Pete", "Mr. Wilson", "Dr. Orange"};
	private static final String[] LOOT = {"depths_ghoul", "depths_ghoul_pete", "depths_ghoul_wilson", "depths_ghoul_orange"};

	public EntityDepthsGhoul(EntityType<? extends EntityDepthsGhoul> type, Level level) {
		super(type, level, 60.0D, 10.0D, 8);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 42.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
				.add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 5.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
		goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0D));
		goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityDepthsGhoul.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityAbyssalZombie.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Zombie.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Skeleton.class, 8.0F));
		goalSelector.addGoal(7, new LookAtTypeGoal(this, ACEntities.skeleton_goliath, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(TYPE, 0);
	}

	/** 0 normal, 1 Pete, 2 Mr. Wilson, 3 Dr. Orange */
	public int getGhoulType() {
		return entityData.get(TYPE);
	}

	public void setGhoulType(int type) {
		entityData.set(TYPE, type);
	}

	@Override
	protected Component getTypeName() {
		int type = getGhoulType();
		return type > 0 && type < NAMES.length ? Component.literal(NAMES[type]) : super.getTypeName();
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		swing(InteractionHand.MAIN_HAND);
		swing(InteractionHand.OFF_HAND);
		return super.doHurtTarget(target);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		setGhoulType(random.nextFloat() < 0.2F ? random.nextInt(4) : 0);
		return super.finalizeSpawn(level, difficulty, reason, data, tag);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		int type = getGhoulType();
		return AbyssalCraft.loc("entities/" + LOOT[type >= 0 && type < LOOT.length ? type : 0]);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return switch(getGhoulType()) {
		case 1 -> ACSounds.ghoul_pete_ambient.get();
		case 2 -> ACSounds.ghoul_wilson_ambient.get();
		case 3 -> ACSounds.ghoul_orange_ambient.get();
		default -> ACSounds.ghoul_normal_ambient.get();
		};
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.ghoul_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.ghoul_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("GhoulType", getGhoulType());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if(tag.contains("GhoulType"))
			setGhoulType(tag.getInt("GhoulType"));
	}
}
