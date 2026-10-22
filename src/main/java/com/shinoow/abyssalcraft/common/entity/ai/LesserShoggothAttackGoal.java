/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAILesserShoggothAttackMelee)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.ai;

import java.util.EnumSet;

import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.pathfinder.Path;

/**
 * Melee attack of the Lesser Shoggoth. Out of reach it sprays acid on the blocks between it and the target when it's
 * stuck, and adults (or every Shoggoth in Hardcore Mode) spit acid at targets more than ~5.7 blocks away.
 */
public class LesserShoggothAttackGoal extends Goal
{
	protected EntityLesserShoggoth attacker;
	/** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
	protected int attackTick, rangedAttackTick;
	/** The speed with which the mob will approach the target */
	double speedTowardsTarget;
	/** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
	boolean longMemory;
	/** The PathEntity of our entity. */
	Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;

	public LesserShoggothAttackGoal(EntityLesserShoggoth creature, double speedIn, boolean useLongMemory)
	{
		attacker = creature;
		speedTowardsTarget = speedIn;
		longMemory = useLongMemory;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse()
	{
		LivingEntity entitylivingbase = attacker.getTarget();

		if (entitylivingbase == null || !entitylivingbase.isAlive())
			return false;
		path = attacker.getNavigation().createPath(entitylivingbase, 0);

		if (path != null)
			return true;
		else
			return getAttackReachSqr(entitylivingbase) >= attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ());
	}

	@Override
	public boolean canContinueToUse()
	{
		LivingEntity entitylivingbase = attacker.getTarget();

		if (entitylivingbase == null || !entitylivingbase.isAlive())
			return false;
		else if (!longMemory)
			return !attacker.getNavigation().isDone();
		else if (!attacker.isWithinRestriction(entitylivingbase.blockPosition()))
			return false;
		else
			return !(entitylivingbase instanceof Player player) || !player.isSpectator() && !player.isCreative();
	}

	@Override
	public void start()
	{
		attacker.getNavigation().moveTo(path, speedTowardsTarget);
		delayCounter = 0;
	}

	@Override
	public void stop()
	{
		LivingEntity entitylivingbase = attacker.getTarget();

		if (entitylivingbase instanceof Player player && (player.isSpectator() || player.isCreative()))
			attacker.setTarget(null);

		attacker.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick()
	{
		LivingEntity entitylivingbase = attacker.getTarget();
		if(entitylivingbase == null)
			return;
		attacker.getLookControl().setLookAt(entitylivingbase, 30.0F, 30.0F);
		double d0 = attacker.distanceToSqr(entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ());
		--delayCounter;

		if ((longMemory || attacker.getSensing().hasLineOfSight(entitylivingbase)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.distanceToSqr(targetX, targetY, targetZ) >= 1.0D || attacker.getRandom().nextFloat() < 0.05F))
		{
			targetX = entitylivingbase.getX();
			targetY = entitylivingbase.getY();
			targetZ = entitylivingbase.getZ();
			delayCounter = 4 + attacker.getRandom().nextInt(7);

			if (d0 > 1024.0D)
				delayCounter += 10;
			else if (d0 > 256.0D)
				delayCounter += 5;

			if (!attacker.getNavigation().moveTo(entitylivingbase, speedTowardsTarget))
				delayCounter += 15;
		}

		attackTick = Math.max(attackTick - 1, 0);
		if((!attacker.isBaby() || ACConfig.hardcoreMode) && ACConfig.acidSpitFrequency > 0)
			rangedAttackTick = Math.max(rangedAttackTick - 1, 0);
		checkAndPerformAttack(entitylivingbase, d0);
	}

	protected void checkAndPerformAttack(LivingEntity target, double distanceSq)
	{
		double d0 = getAttackReachSqr(target);

		if(attackTick <= 0)
			if (distanceSq <= d0)
			{
				attackTick = 20;
				attacker.swing(InteractionHand.MAIN_HAND);
				attacker.doHurtTarget(target);
			} else {
				attackTick = 20;
				// stuck: dissolve the blocks above or below
				if(attacker.getDeltaMovement().x == 0 && attacker.getDeltaMovement().z == 0 && attacker.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
					attacker.sprayAcid(target.getY() >= attacker.getY());
			}
		if(rangedAttackTick <= 0 && (!attacker.isBaby() || ACConfig.hardcoreMode) && ACConfig.acidSpitFrequency > 0)
			if(attacker.distanceToSqr(target) > 32D) {
				rangedAttackTick = ACConfig.acidSpitFrequency;
				attacker.sprayAcidAt(target);
			}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget)
	{
		return attacker.getBbWidth() * 1.5F * attacker.getBbWidth() * 1.5F + attackTarget.getBbWidth();
	}
}
