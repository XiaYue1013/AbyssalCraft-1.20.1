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

import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Lesser Dreadbeast: five Greater Dread Spawn merged. Spits Dread Slugs, keeps spawning Dread Spawn (and rarely a Greater
 * Dread Spawn), splits into two Greater Dread Spawn when killed.
 */
public class EntityLesserDreadbeast extends EntityDreadSpawnBase.RangedOrMelee {

	public EntityLesserDreadbeast(EntityType<? extends EntityLesserDreadbeast> type, Level level) {
		super(type, level, "lesser_dreadbeast", 3.0F, 0.4D, 15.0F);
		hardcore(600.0D, 36.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.4D).add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.MAX_HEALTH, 300.0D).add(Attributes.ATTACK_DAMAGE, 18.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + 0.3F;
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if(level().isClientSide || !isAlive()) return;
		if(tickCount % 400 == 0 && level().getEntitiesOfClass(EntityDreadSpawn.class, getBoundingBox().inflate(32)).size() < 20)
			spawnAt(this, ACEntityTypes.DREAD_SPAWN.get().create(level()));
		if(tickCount % 10000 == 0 && level().getEntitiesOfClass(EntityGreaterDreadSpawn.class, getBoundingBox().inflate(32)).size() < 10)
			spawnAt(this, ACEntityTypes.GREATER_DREAD_SPAWN.get().create(level()));
	}

	@Override
	public void die(DamageSource source) {
		if(!level().isClientSide && !dead && !isRemoved()) {
			spawnAt(this, ACEntityTypes.GREATER_DREAD_SPAWN.get().create(level()));
			spawnAt(this, ACEntityTypes.GREATER_DREAD_SPAWN.get().create(level()));
		}
		super.die(source);
	}
}
