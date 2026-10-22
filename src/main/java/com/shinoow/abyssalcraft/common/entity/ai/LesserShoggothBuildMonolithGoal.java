/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAILesserShoggothBuildMonolith)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.common.world.gen.ShoggothMonolith;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Once its monolith timer has run out, an adult Lesser Shoggoth with three others nearby gathers them at a spot within
 * 10 blocks and they build a Shoggoth Monolith there.
 */
public class LesserShoggothBuildMonolithGoal extends Goal {

	private EntityLesserShoggoth shoggoth;
	private double locationX, locationY, locationZ;
	private final Level world;
	private List<EntityLesserShoggoth> shoggoths;
	private int attemptTimer;

	public LesserShoggothBuildMonolithGoal(EntityLesserShoggoth shoggoth) {
		this.shoggoth = shoggoth;
		world = shoggoth.level();
		shoggoths = new ArrayList<>();
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if(ACConfig.monolithBuildingCooldown == 0 || shoggoth.isBaby() || shoggoth.isAssisting || shoggoth.isBuilding)
			return false;
		else if(shoggoth.getMonolithTimer() < ACConfig.monolithBuildingCooldown || helpers().size() < 3)
			return false;
		else {
			Vec3 vec3d = findPossibleLocation();

			if(vec3d == null)
				return false;
			else {
				for(EntityLesserShoggoth shoggoth1 : helpers()) {
					if(shoggoths.size() == 3)
						break;
					if(shoggoth1.distanceTo(shoggoth) <= 12) {
						shoggoth1.reduceMonolithTimer();
						shoggoth1.isAssisting = true;
						shoggoths.add(shoggoth1);
					}
				}
				if(shoggoths.size() != 3) {
					for(EntityLesserShoggoth shoggoth : shoggoths)
						shoggoth.isAssisting = false;
					shoggoths.clear();
					return false;
				}
				shoggoth.isBuilding = true;
				locationX = vec3d.x;
				locationY = vec3d.y;
				locationZ = vec3d.z;
				return true;
			}
		}
	}

	/** Shoggoths within 16 blocks that aren't busy */
	private List<EntityLesserShoggoth> helpers() {
		return world.getEntitiesOfClass(EntityLesserShoggoth.class, shoggoth.getBoundingBox().inflate(16D, 16D, 16D), e -> !e.equals(shoggoth) && !e.isAssisting && !e.isBuilding);
	}

	@Override
	public boolean canContinueToUse()
	{
		if(!shoggoth.isBuilding)
			return false;
		if(attemptTimer >= 0)
			return distanceToLocation(shoggoth) > 8 || !allShoggothsNearby();
		return false;
	}

	@Override
	public boolean isInterruptable()
	{
		return false;
	}

	@Override
	public void start()
	{
		attemptTimer = 400;
		shoggoth.getNavigation().moveTo(locationX + (shoggoth.getRandom().nextBoolean() ? 3 : -3), locationY, locationZ + (shoggoth.getRandom().nextBoolean() ? 3 : -3), 0.38D);
		for(EntityLesserShoggoth helper : shoggoths)
			helper.getNavigation().moveTo(shoggoth, 0.38D);
	}

	@Override
	public void tick()
	{
		--attemptTimer;
		if(distanceToLocation(shoggoth) <= 8 && allShoggothsNearby()) {
			shoggoth.resetMonolithTimer();
			// everyone gets out of the way
			for(EntityLesserShoggoth shoggoth : world.getEntitiesOfClass(EntityLesserShoggoth.class, shoggoth.getBoundingBox().inflate(32D, 32D, 32D)))
				if(distanceToLocation(shoggoth) <= 5)
					shoggoth.getNavigation().moveTo(locationX + (shoggoth.getRandom().nextBoolean() ? 7 : -7), shoggoth.getY(), locationZ + (shoggoth.getRandom().nextBoolean() ? 7 : -7), 0.7F);
			if(!world.isClientSide) {
				BlockPos pos = BlockPos.containing(Mth.floor(locationX), Mth.floor(locationY), Mth.floor(locationZ));
				if(world.getBlockState(pos.below()).is(ACBlocks.shoggoth_ooze.get()))
					pos = pos.below();
				ShoggothMonolith.generate(world, shoggoth.getRandom(), pos);
			}
			shoggoth.isBuilding = false;
		}
	}

	@Override
	public void stop()
	{
		for(EntityLesserShoggoth shoggoth1 : shoggoths)
			shoggoth1.isBuilding = shoggoth1.isAssisting = false;
		shoggoths.clear();
		shoggoth.isBuilding = shoggoth.isAssisting = false;
		locationX = locationY = locationZ = 0;
	}

	private double distanceToLocation(EntityLesserShoggoth shoggoth) {
		return Math.sqrt(shoggoth.distanceToSqr(locationX, locationY, locationZ));
	}

	private boolean allShoggothsNearby() {
		boolean temp = true;
		for(EntityLesserShoggoth shoggoth1 : shoggoths)
			if(shoggoth1.distanceTo(shoggoth) > 8)
				temp = false;
		return temp;
	}

	/** A free spot on solid ground (not a monolith or biomass) within 10 blocks, preferring Shoggoth Ooze next to it */
	@Nullable
	private Vec3 findPossibleLocation()
	{
		RandomSource random = shoggoth.getRandom();
		BlockPos blockpos = BlockPos.containing(shoggoth.getX(), shoggoth.getBoundingBox().minY, shoggoth.getZ());

		for (int i = 0; i < 10; ++i)
		{
			BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (world.isEmptyBlock(blockpos1.above())
					&& (world.getBlockState(blockpos1).canBeReplaced() || world.isEmptyBlock(blockpos1))
					&& !world.getBlockState(blockpos1.below()).is(ACBlocks.monolith_stone.get())
					&& !world.isEmptyBlock(blockpos1.below()) && world.getBlockState(blockpos1.below()).isFaceSturdy(world, blockpos1.below(), Direction.UP)
					&& !world.getBlockState(blockpos1.below()).is(ACBlocks.shoggoth_biomass.get())) {
				for(BlockPos pos : BlockPos.betweenClosed(blockpos1.north().west().below(), blockpos1.south().east().above()))
					if(world.getBlockState(pos).is(ACBlocks.shoggoth_ooze.get()))
						return new Vec3(pos.getX(), pos.getY(), pos.getZ());
				return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
			}
		}

		return null;
	}
}
