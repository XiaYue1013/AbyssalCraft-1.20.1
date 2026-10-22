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
 * Spawn of Cha'garoth: a fast spiky arthropod that Cha'garoth spawns and throws, also found in the Dreadlands
 * Mountains.
 */
public class EntityChagarothSpawn extends EntityDreadSpawnBase {

	public EntityChagarothSpawn(EntityType<? extends EntityChagarothSpawn> type, Level level) {
		super(type, level, "spawn_of_chagaroth", 3.0F);
		hardcore(80.0D, 16.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.1D).add(Attributes.MOVEMENT_SPEED, 0.45D)
				.add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.ATTACK_DAMAGE, 8.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(3, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected boolean infectsDreadMobs() {
		return true;
	}

	/** 1.12.2 had no spawn cap for these */
	@Override
	protected boolean capsSpawns() {
		return false;
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + 0.6F;
	}

	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}
}
