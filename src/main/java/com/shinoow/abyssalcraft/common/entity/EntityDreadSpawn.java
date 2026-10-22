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
 * Dread Spawn: small Dreadlands crawler. Five of them close together merge into a Greater Dread Spawn.
 */
public class EntityDreadSpawn extends EntityDreadSpawnBase {

	public EntityDreadSpawn(EntityType<? extends EntityDreadSpawn> type, Level level) {
		super(type, level, "dread_spawn", 1.5F);
		hardcore(40.0D, 12.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.ATTACK_DAMAGE, 6.0D);
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
	public void aiStep() {
		super.aiStep();
		if(level().isClientSide || !isAlive()) return;
		List<EntityDreadSpawn> dreadspawns = level().getEntitiesOfClass(EntityDreadSpawn.class, getBoundingBox().inflate(2D), EntityDreadSpawn::isAlive);
		if(dreadspawns.size() >= 5 && level().getEntitiesOfClass(EntityGreaterDreadSpawn.class, getBoundingBox().inflate(32)).size() < 10) {
			for(int i = 0; i < 5; i++)
				dreadspawns.get(i).discard();
			spawnAt(this, ACEntityTypes.GREATER_DREAD_SPAWN.get().create(level()));
			discard();
		}
	}
}
