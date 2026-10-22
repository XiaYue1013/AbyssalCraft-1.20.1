/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (WorldGenDLT, WorldGenDrT)
 *     1.20.1 port - both trees in one feature; branch logs lie along their branch (1.12.2 used all-bark logs)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Darklands Oak and Dreadlands trees: a 9-11 block trunk with four roots and 4-11 rising branches
 */
public class ACTreeFeature extends Feature<NoneFeatureConfiguration> {

	public enum Type { DARKLANDS, DREADLANDS }

	private final Type type;

	public ACTreeFeature(Type type) {
		super(NoneFeatureConfiguration.CODEC);
		this.type = type;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return grow(context.level(), context.random(), context.origin());
	}

	/** Grows the tree with its trunk starting at origin (also used by the Darklands scion structure) */
	public boolean grow(WorldGenLevel level, RandomSource rand, BlockPos origin) {
		int x = origin.getX(), y = origin.getY(), z = origin.getZ();
		int height = rand.nextInt(3) + 9;
		int leaveheight = rand.nextInt(3);
		int branches = rand.nextInt(8) + 4;
		int branchLength = 6;

		BlockState soil = level.getBlockState(origin.below());
		if(!canGrowOn(soil) || y >= level.getMaxBuildHeight() - height - 1)
			return false;
		setBlock(level, origin.below(), type == Type.DREADLANDS && (soil.is(ACBlocks.dreadlands_grass.get()) || soil.is(ACBlocks.dreadstone.get())
				|| soil.is(ACBlocks.dreadlands_dirt.get())) ? ACBlocks.dreadlands_dirt.get().defaultBlockState() : Blocks.DIRT.defaultBlockState());

		for(int i = 0; i < height; i++)
			setBlock(level, new BlockPos(x, y + i, z), log(rand, Direction.Axis.Y));
		setBlock(level, new BlockPos(x, y + height, z), leaves());
		createTrunk(level, rand, x, y, z);

		int dir = rand.nextInt((int) (360f / branches));
		for(int b = 0; b < branches; b++) {
			float c = 0;
			float hd = height - rand.nextFloat() * leaveheight - 2f;
			dir += (int) (360f / branches);
			float xd = (float) Math.cos(dir * Math.PI / 180f);
			float zd = (float) Math.sin(dir * Math.PI / 180f);
			Direction.Axis axis = Math.abs(xd) > Math.abs(zd) ? Direction.Axis.X : Direction.Axis.Z;
			while(c < branchLength) {
				c++;
				hd += 0.5f;
				BlockPos pos = new BlockPos(x + (int) (c * xd), y + (int) hd, z + (int) (c * zd));
				setBlock(level, pos, log(rand, axis));
				if(level.isEmptyBlock(pos.above()))
					setBlock(level, pos.above(), leaves());
			}
		}
		return true;
	}

	private void createTrunk(WorldGenLevel level, RandomSource rand, int x, int y, int z) {
		int[] pos = new int[] {1, 0, 0, 1, -1, 0, 0, -1};
		for(int t = 0; t < 4; t++) {
			int sh = rand.nextInt(3) + y;
			int top = sh;
			while(sh > y - 1) {
				setBlock(level, new BlockPos(x + pos[t * 2], sh, z + pos[t * 2 + 1]), log(rand, top == sh ? pos[t * 2] != 0 ? Direction.Axis.X : Direction.Axis.Z : Direction.Axis.Y));
				sh--;
			}
		}
	}

	private boolean canGrowOn(BlockState soil) {
		if(soil.is(BlockTags.DIRT))
			return true;
		return type == Type.DREADLANDS && (soil.is(ACBlocks.dreadlands_dirt.get()) || soil.is(ACBlocks.dreadlands_grass.get()) || soil.is(ACBlocks.dreadstone.get()));
	}

	private BlockState log(RandomSource rand, Direction.Axis axis) {
		BlockState log = type == Type.DREADLANDS ? ACBlocks.dreadlands_log.get().defaultBlockState()
				: (rand.nextInt(10) == 0 ? ACBlocks.darklands_oak_wood_2 : ACBlocks.darklands_oak_wood).get().defaultBlockState();
		return log.hasProperty(RotatedPillarBlock.AXIS) ? log.setValue(RotatedPillarBlock.AXIS, axis) : log;
	}

	/** Every leaf block touches a log, so its distance is set right away (they'd decay otherwise) */
	private BlockState leaves() {
		BlockState leaves = (type == Type.DREADLANDS ? ACBlocks.dreadlands_leaves : ACBlocks.darklands_oak_leaves).get().defaultBlockState();
		return leaves.hasProperty(LeavesBlock.DISTANCE) ? leaves.setValue(LeavesBlock.DISTANCE, 1) : leaves;
	}
}
