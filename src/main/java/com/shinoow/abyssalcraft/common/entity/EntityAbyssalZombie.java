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

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.entity.ICoraliumEntity;
import com.shinoow.abyssalcraft.common.entity.ai.LookAtTypeGoal;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityAbyssalZombie extends EntityACZombie implements ICoraliumEntity {

	public EntityAbyssalZombie(EntityType<? extends EntityAbyssalZombie> type, Level level) {
		super(type, level, 50.0D, 12.0D, 16);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 42.0D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D)
				.add(Attributes.MAX_HEALTH, 25.0D).add(Attributes.ATTACK_DAMAGE, 6.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		goalSelector.addGoal(4, new FleeSunGoal(this, 1.0D));
		goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, EntityAbyssalZombie.class, 8.0F));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, Zombie.class, 8.0F));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, EntityDepthsGhoul.class, 8.0F));
		goalSelector.addGoal(8, new LookAtPlayerGoal(this, Skeleton.class, 8.0F));
		goalSelector.addGoal(8, new LookAtTypeGoal(this, ACEntities.skeleton_goliath, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
	}

	@Override
	protected boolean canConvert(LivingEntity killed) {
		return killed instanceof Zombie || killed instanceof Player || killed instanceof AbstractVillager;
	}

	@Override
	protected EntityACZombie createConversion(ServerLevel level) {
		return ACEntityTypes.ABYSSAL_ZOMBIE.get().create(level);
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
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}
}
