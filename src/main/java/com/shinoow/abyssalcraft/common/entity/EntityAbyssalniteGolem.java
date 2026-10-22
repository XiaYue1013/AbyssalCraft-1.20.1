/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAbygolem, EntityDreadgolem)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Dreadlands golems. The Abyssalnite Golem only fights the Dreaded Abyssalnite Golems (and what hurts it); the
 * dreaded ones also go after players.
 */
public class EntityAbyssalniteGolem extends EntityACMonster {

	public EntityAbyssalniteGolem(EntityType<? extends EntityAbyssalniteGolem> type, Level level) {
		this(type, level, "abyssalnite_golem");
	}

	protected EntityAbyssalniteGolem(EntityType<? extends EntityAbyssalniteGolem> type, Level level, String lootTable) {
		super(type, level, lootTable);
		hardcore(40.0D, 8.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 4.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Dreaded.class, true));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ACSounds.golem_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.golem_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.golem_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}

	/** Dreaded Abyssalnite Golem */
	public static class Dreaded extends EntityAbyssalniteGolem implements IDreadEntity {

		public Dreaded(EntityType<? extends Dreaded> type, Level level) {
			super(type, level, "dreaded_abyssalnite_golem");
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
			goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
			goalSelector.addGoal(5, new RandomStrollGoal(this, 0.35D));
			goalSelector.addGoal(6, new RandomLookAroundGoal(this));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
			targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityAbyssalniteGolem.class, true, e -> !(e instanceof Dreaded)));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		}
	}
}
