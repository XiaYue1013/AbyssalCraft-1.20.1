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
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.ai.LookAtTypeGoal;
import com.shinoow.abyssalcraft.init.ACDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Skeleton Goliath: a giant skeleton with a cudgel, burns in daylight (except in the Abyssal Wasteland)
 */
public class EntitySkeletonGoliath extends EntityACMonster {

	public EntitySkeletonGoliath(EntityType<? extends EntitySkeletonGoliath> type, Level level) {
		super(type, level, "skeleton_goliath");
		hardcore(200.0D, 40.0D);
		setDropChance(EquipmentSlot.MAINHAND, 0.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
				.add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 20.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(5, new FleeSunGoal(this, 1.0D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityAbyssalZombie.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Zombie.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityDepthsGhoul.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Skeleton.class, 8.0F));
		goalSelector.addGoal(7, new LookAtTypeGoal(this, ACEntities.skeleton_goliath, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected float hardcoreDamage() {
		return 3.0F;
	}

	@Override
	protected double halloweenDamage() {
		return 5.0D;
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + 0.6F;
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
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
	}

	/** It always holds its cudgel (1.12.2 getHeldItem; it's never dropped from the hand, only through the loot table) */
	@Override
	public ItemStack getItemBySlot(EquipmentSlot slot) {
		if(slot == EquipmentSlot.MAINHAND && ACItems.cudgel.isPresent())
			return new ItemStack(ACItems.cudgel.get());
		return super.getItemBySlot(slot);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	/** Burns in daylight unless its helmet takes the damage (not in the Abyssal Wasteland) */
	@Override
	public void aiStep() {
		if(level().isDay() && !level().isClientSide && level().dimension() != ACDimensions.ABYSSAL_WASTELAND) {
			float f = getLightLevelDependentMagicValue();
			if(f > 0.5F && random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && level().canSeeSky(BlockPos.containing(getX(), getEyeY(), getZ()))) {
				boolean flag = true;
				ItemStack helmet = super.getItemBySlot(EquipmentSlot.HEAD);
				if(!helmet.isEmpty()) {
					if(helmet.isDamageableItem()) {
						helmet.setDamageValue(helmet.getDamageValue() + random.nextInt(2));
						if(helmet.getDamageValue() >= helmet.getMaxDamage()) {
							broadcastBreakEvent(EquipmentSlot.HEAD);
							setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
						}
					}
					flag = false;
				}
				if(flag)
					setSecondsOnFire(8);
			}
		}
		super.aiStep();
	}
}
