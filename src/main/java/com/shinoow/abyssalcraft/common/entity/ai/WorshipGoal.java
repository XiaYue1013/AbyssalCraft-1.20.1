/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAIWorship)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.ai;

import java.util.EnumSet;

import com.shinoow.abyssalcraft.common.blocks.BlockDecorativeStatue;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Now and then a mob near a statue walks up to it, chants and stares at it for a while
 */
public class WorshipGoal extends Goal
{
	/** The entity that is looking idle. */
	private final Mob idleEntity;
	/** A decrementing tick that stops the entity from being idle once it reaches 0. */
	private int idleTime;
	private BlockPos statuePos;

	public WorshipGoal(Mob entitylivingIn)
	{
		idleEntity = entitylivingIn;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse()
	{
		if(idleEntity.getRandom().nextFloat() < 0.01F) {
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			Level world = idleEntity.level();
			BlockPos pos1 = idleEntity.blockPosition();
			for(int x = -8; x < 9; x++)
				for(int y = -8; y < 9; y++)
					for(int z = -8; z < 9; z++){
						pos.set(pos1.getX() + x, pos1.getY() + y, pos1.getZ() + z);
						Block block = world.getBlockState(pos).getBlock();
						if(block instanceof BlockStatue || block instanceof BlockDecorativeStatue) {
							statuePos = pos.immutable();
							return true;
						}
					}
		}
		return false;
	}

	@Override
	public boolean canContinueToUse()
	{
		return idleTime >= 0;
	}

	@Override
	public void start()
	{
		idleTime = 20 + idleEntity.getRandom().nextInt(20);
		if(statuePos != null) {
			idleEntity.getNavigation().moveTo(statuePos.getX() + (idleEntity.getRandom().nextBoolean() ? 1 : -1), idleEntity.getY(), statuePos.getZ()+ (idleEntity.getRandom().nextBoolean() ? 1 : -1), 0.5F);
			if(!idleEntity.level().isClientSide)
				idleEntity.playSound(ACSounds.remnant_priest_chant.get(), 1.0F, idleEntity instanceof com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth ? idleEntity.isBaby() ? 1.5F : 0.8F : (idleEntity.getRandom().nextFloat() - idleEntity.getRandom().nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick()
	{
		--idleTime;
		idleEntity.getLookControl().setLookAt(statuePos.getX(), statuePos.getY(), statuePos.getZ(), idleEntity.getMaxHeadYRot(), idleEntity.getMaxHeadXRot());
	}
}
