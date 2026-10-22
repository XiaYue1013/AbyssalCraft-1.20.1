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
 *     1.20.1 port - flammability through the Forge block state hooks, neighbour updates keep the mimic fire
 *     (vanilla FireBlock would turn it into regular fire)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Fire spread by Demon Animals. Looks like regular fire, but ticks up to 30 times as often and only
 * spreads to flammable blocks.
 */
public class BlockMimicFire extends FireBlock {

	public BlockMimicFire(Properties properties) {
		super(properties);
	}

	private static int tickRate(RandomSource rand) {
		return 1 + rand.nextInt(10);
	}

	/** Same as regular fire (can light nether portals), but without FireBlock's slower first tick */
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if(!oldState.is(state.getBlock())) {
			if(level.dimension() == Level.OVERWORLD || level.dimension() == Level.NETHER) {
				Optional<PortalShape> optional = ForgeEventFactory.onTrySpawnPortal(level, pos, PortalShape.findEmptyPortalShape(level, pos, Direction.Axis.X));
				if(optional.isPresent()) {
					optional.get().createPortalBlocks();
					return;
				}
			}

			if(!state.canSurvive(level, pos)) {
				level.removeBlock(pos, false);
				return;
			}
		}
		level.scheduleTick(pos, this, tickRate(level.random));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return canSurvive(state, level, pos) ? getStateForPlacement(level, pos).setValue(AGE, state.getValue(AGE)) : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand)
	{
		if (worldIn.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK))
		{
			if (!state.canSurvive(worldIn, pos))
				worldIn.removeBlock(pos, false);

			if (worldIn.isRaining() && isNearRain(worldIn, pos))
				worldIn.removeBlock(pos, false);
			else
			{
				int i = state.getValue(AGE);

				if (i < 4)
				{
					state = state.setValue(AGE, i + rand.nextInt(3) / 2);
					worldIn.setBlock(pos, state, 4);
				}

				worldIn.scheduleTick(pos, this, tickRate(rand));


				if (!canNeighborCatchFire(worldIn, pos))
				{
					if (!worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP) || i > 3)
						worldIn.removeBlock(pos, false);

					return;
				}

				if (!canCatchFire(worldIn, pos.below(), Direction.UP) && i >= 4 && rand.nextInt(4) == 0)
				{
					worldIn.removeBlock(pos, false);
					return;
				}


				boolean flag1 = worldIn.getBiome(pos).value().getModifiedClimateSettings().downfall() > 0.85F;
				int j = 0;

				if (flag1)
					j = -50;

				if(rand.nextFloat() < 0.2){
					tryCatchFire(worldIn, pos.east(), 300 + j, rand, i, Direction.WEST);
					tryCatchFire(worldIn, pos.west(), 300 + j, rand, i, Direction.EAST);
					tryCatchFire(worldIn, pos.below(), 250 + j, rand, i, Direction.UP);
					tryCatchFire(worldIn, pos.above(), 250 + j, rand, i, Direction.DOWN);
					tryCatchFire(worldIn, pos.north(), 300 + j, rand, i, Direction.SOUTH);
					tryCatchFire(worldIn, pos.south(), 300 + j, rand, i, Direction.NORTH);

					for (int k = -1; k <= 1; ++k)
						for (int l = -1; l <= 1; ++l)
							for (int i1 = -1; i1 <= 4; ++i1)
								if (k != 0 || i1 != 0 || l != 0)
								{
									int j1 = 100;

									if (i1 > 1)
										j1 += (i1 - 1) * 100;

									BlockPos blockpos = pos.offset(k, i1, l);
									int k1 = getNeighborEncouragement(worldIn, blockpos);

									if (k1 > 0)
									{
										int l1 = (k1 + 40 + worldIn.getDifficulty().getId() * 7) / (i + 30);

										if (flag1)
											l1 /= 2;

										if (l1 > 0 && rand.nextInt(j1) <= l1 && (!worldIn.isRaining() || !isNearRain(worldIn, blockpos)))
										{
											int i2 = i + rand.nextInt(5) / 4;

											if (i2 > 4)
												i2 = 4;

											worldIn.setBlock(blockpos, getStateForPlacement(worldIn, blockpos).setValue(AGE, i2), 3);
										}
									}
								}
				}
			}
		}
	}

	private void tryCatchFire(Level worldIn, BlockPos pos, int chance, RandomSource random, int age, Direction face)
	{
		int i = worldIn.getBlockState(pos).getFlammability(worldIn, pos, face);

		if (random.nextInt(chance) < i)
		{
			BlockState iblockstate = worldIn.getBlockState(pos);
			iblockstate.onCaughtFire(worldIn, pos, face, null);

			if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos))
			{
				int j = age + random.nextInt(5) / 4;

				if (j > 15)
					j = 15;

				worldIn.setBlock(pos, getStateForPlacement(worldIn, pos).setValue(AGE, j), 3);
			} else
				worldIn.removeBlock(pos, false);
		}
	}

	private boolean canNeighborCatchFire(LevelReader worldIn, BlockPos pos)
	{
		for (Direction enumfacing : Direction.values())
			if (canCatchFire(worldIn, pos.relative(enumfacing), enumfacing.getOpposite()))
				return true;

		return false;
	}

	private int getNeighborEncouragement(LevelReader worldIn, BlockPos pos)
	{
		if (!worldIn.isEmptyBlock(pos))
			return 0;
		else
		{
			int i = 0;

			for (Direction enumfacing : Direction.values())
				i = Math.max(worldIn.getBlockState(pos.relative(enumfacing)).getFlammability(worldIn, pos.relative(enumfacing), enumfacing.getOpposite()), i);

			return i;
		}
	}
}
