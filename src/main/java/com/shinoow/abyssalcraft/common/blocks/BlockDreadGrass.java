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
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

/**
 * Dreadlands Grass, spreads onto Dreadlands Dirt (and, if enabled, regular grass and dirt)
 */
public class BlockDreadGrass extends SnowyDirtBlock implements BonemealableBlock {

	public BlockDreadGrass(Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (!level.isAreaLoaded(pos, 3)) return;
		if (level.getMaxLocalRawBrightness(pos.above()) < 4 && level.getBlockState(pos.above()).getLightBlock(level, pos.above()) > 2)
			level.setBlockAndUpdate(pos, ACBlocks.dreadlands_dirt.get().defaultBlockState());
		else
			for (int i = 0; i < 4; ++i)
			{
				BlockPos blockpos = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
				BlockState above = level.getBlockState(blockpos.above());
				BlockState iblockstate = level.getBlockState(blockpos);

				if ((iblockstate.is(Blocks.GRASS_BLOCK) && ACConfig.dreadGrassSpread || iblockstate.is(ACBlocks.dreadlands_dirt.get()) || iblockstate.is(Blocks.DIRT) && ACConfig.dreadGrassSpread) && above.getLightBlock(level, blockpos.above()) <= 2)
					level.setBlockAndUpdate(blockpos, defaultBlockState());
				else if(ACConfig.dreadGrassSpread && iblockstate.is(Blocks.DIRT) && iblockstate.isFaceSturdy(level, blockpos, Direction.UP) && above.getFluidState().isEmpty())
					level.setBlockAndUpdate(blockpos, ACBlocks.dreadlands_dirt.get().defaultBlockState());
			}
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return canSustain(world, pos, facing, plantable);
	}

	/**
	 * Dreadlands grass and dirt sustain plains plants, and beach plants next to water
	 */
	public static boolean canSustain(BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		boolean hasWater = world.getFluidState(pos.east()).is(FluidTags.WATER) ||
				world.getFluidState(pos.west()).is(FluidTags.WATER) ||
				world.getFluidState(pos.north()).is(FluidTags.WATER) ||
				world.getFluidState(pos.south()).is(FluidTags.WATER);
		PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return type == PlantType.PLAINS || type == PlantType.BEACH && hasWater;
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		return level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		BlockPos blockpos = pos.above();

		for (int i = 0; i < 128; ++i)
		{
			BlockPos blockpos1 = blockpos;
			int j = 0;

			while (true)
			{
				if (j >= i / 16)
				{
					if (worldIn.isEmptyBlock(blockpos1))
						if (rand.nextInt(8) == 0) {
							BlockState flower = rand.nextBoolean() ? Blocks.POPPY.defaultBlockState() : Blocks.DANDELION.defaultBlockState();
							if(flower.canSurvive(worldIn, blockpos1))
								worldIn.setBlock(blockpos1, flower, 3);
						} else
						{
							BlockState iblockstate1 = Blocks.GRASS.defaultBlockState();

							if (iblockstate1.canSurvive(worldIn, blockpos1))
								worldIn.setBlock(blockpos1, iblockstate1, 3);
						}

					break;
				}

				blockpos1 = blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if (!worldIn.getBlockState(blockpos1.below()).is(this) || worldIn.getBlockState(blockpos1).isCollisionShapeFullBlock(worldIn, blockpos1))
					break;

				++j;
			}
		}
	}
}
