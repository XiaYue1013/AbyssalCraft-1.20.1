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

import java.util.List;

import com.shinoow.abyssalcraft.init.ACEntityTypes;

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

/**
 * Greater Dread Spawn: five Dread Spawn merged. Spits Dread Slugs at range, spawns a Dread Spawn now and then, splits
 * into two Dread Spawn when killed, and five of them merge into a Lesser Dreadbeast.
 */
public class EntityGreaterDreadSpawn extends EntityDreadSpawnBase.RangedOrMelee {

	public EntityGreaterDreadSpawn(EntityType<? extends EntityGreaterDreadSpawn> type, Level level) {
		super(type, level, "greater_dread_spawn", 3.0F, 0.4D, 8.0F);
		hardcore(200.0D, 24.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.2D).add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 12.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.4D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.4D));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + 0.6F;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if(level().isClientSide || !isAlive()) return;
		List<EntityGreaterDreadSpawn> greaterspawns = level().getEntitiesOfClass(EntityGreaterDreadSpawn.class, getBoundingBox().inflate(5D), EntityGreaterDreadSpawn::isAlive);
		if(greaterspawns.size() >= 5 && level().getEntitiesOfClass(EntityLesserDreadbeast.class, getBoundingBox().inflate(32)).size() < 4) {
			for(int i = 0; i < 5; i++)
				greaterspawns.get(i).discard();
			spawnAt(this, ACEntityTypes.LESSER_DREADBEAST.get().create(level()));
			discard();
			return;
		}
		if(tickCount % 2000 == 0 && level().getEntitiesOfClass(EntityDreadSpawn.class, getBoundingBox().inflate(32)).size() < 20)
			spawnAt(this, ACEntityTypes.DREAD_SPAWN.get().create(level()));
	}

	@Override
	public void die(DamageSource source) {
		if(!level().isClientSide && !dead && !isRemoved()) {
			spawnAt(this, ACEntityTypes.DREAD_SPAWN.get().create(level()));
			spawnAt(this, ACEntityTypes.DREAD_SPAWN.get().create(level()));
		}
		super.die(source);
	}
}
