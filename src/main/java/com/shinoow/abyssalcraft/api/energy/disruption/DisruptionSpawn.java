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
package com.shinoow.abyssalcraft.api.energy.disruption;

import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * A Disruption Entry that spawns a single mob near the disruption source
 * @author shinoow
 *
 * @since 1.5
 */
public class DisruptionSpawn extends DisruptionEntry {

	private Supplier<? extends EntityType<? extends Mob>> entity;

	public DisruptionSpawn(String unlocalizedName, DeityType deity, Supplier<? extends EntityType<? extends Mob>> entity) {
		super(unlocalizedName, deity);
		this.entity = entity;
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world instanceof ServerLevel level)
			spawnNear(level, pos, entity.get());
	}

	/**
	 * Attempts to find a valid spawn position within 16 blocks of the position (ten tries, same walk as the
	 * 1.12.2 implementation), falling back to a spot right next to the position. Does nothing if the type is null.
	 */
	public static void spawnNear(ServerLevel world, BlockPos pos, EntityType<? extends Mob> type) {
		if(type == null) return;
		Mob entityliving = type.create(world);
		if(entityliving == null) return;
		int i = pos.getX() + world.random.nextInt(16);
		int j = pos.getY() + world.random.nextInt(16);
		int k = pos.getZ() + world.random.nextInt(16);
		int l = i;
		int m = j;
		int i1 = k;
		boolean flag = false;

		for (int k1 = 0; !flag && k1 < 10; ++k1)
		{
			BlockPos blockpos = new BlockPos(i, j, k);
			if(world.canSeeSky(blockpos))
				blockpos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos);
			if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, world, blockpos, type))
			{
				entityliving.moveTo(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, world.random.nextFloat() * 360.0F, 0.0F);
				entityliving.finalizeSpawn(world, world.getCurrentDifficultyAt(blockpos), MobSpawnType.EVENT, null, null);
				world.addFreshEntity(entityliving);
				flag = true;
			}

			i += world.random.nextInt(5) - world.random.nextInt(5);

			for (k += world.random.nextInt(5) - world.random.nextInt(5); i < pos.getX() - 16 || i >= pos.getX() + 16 || k < pos.getZ() - 16 || k >= pos.getZ() + 16 || j < pos.getY() - 16 || j >= pos.getY() + 16; k = i1 + world.random.nextInt(5) - world.random.nextInt(16))
			{
				i = l + world.random.nextInt(5) - world.random.nextInt(16);
				j = m + world.random.nextInt(5) - world.random.nextInt(16);
			}
		}
		if(!flag) {
			double d0 = pos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * 4 + 0.5D;
			double d1 = pos.getY() + world.random.nextInt(3) - 1;
			double d2 = pos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * 4 + 0.5D;
			entityliving.moveTo(d0, d1, d2, world.random.nextFloat() * 360.0F, 0.0F);
			entityliving.finalizeSpawn(world, world.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.EVENT, null, null);
			world.addFreshEntity(entityliving);
		}
	}
}
