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

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
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
 * Fist of Cha'garoth: a hand with an eye growing out of the ground, spawned by Cha'garoth. Drops nothing.
 */
public class EntityChagarothFist extends Monster implements IDreadEntity {

	public EntityChagarothFist(EntityType<? extends EntityChagarothFist> type, Level level) {
		super(type, level);
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.3D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D)
				.add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.ATTACK_DAMAGE, 7.5D);
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
	public boolean doHurtTarget(Entity target) {
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 3 * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return super.doHurtTarget(target);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/fist_of_chagaroth");
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}
}
