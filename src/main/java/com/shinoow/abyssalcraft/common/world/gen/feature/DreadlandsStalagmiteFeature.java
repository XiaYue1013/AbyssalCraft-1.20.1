/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (WorldGenDreadlandsStalagmite)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A cluster of dreadstone (abyssalnite stone in the Purified Dreadlands) spikes, the middle one being the tallest
 */
public class DreadlandsStalagmiteFeature extends Feature<NoneFeatureConfiguration> {

	public DreadlandsStalagmiteFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource rand = context.random();
		BlockPos pos = context.origin();
		while(pos.getY() > level.getMinBuildHeight() && level.isEmptyBlock(pos))
			pos = pos.below();
		BlockState ground = level.getBlockState(pos);
		if(!ground.is(ACBlocks.abyssalnite_stone.get()) && !ground.is(ACBlocks.dreadstone.get()))
			return false;
		BlockState state = (level.getBiome(pos).is(ACBiomes.purified_dreadlands) ? ACBlocks.abyssalnite_stone : ACBlocks.dreadstone).get().defaultBlockState();

		placeBlocks(level, state, pos, 7 + rand.nextInt(5));
		placeBlocks(level, state, pos.east(), 5 + rand.nextInt(5));
		placeBlocks(level, state, pos.west(), 5 + rand.nextInt(5));
		placeBlocks(level, state, pos.south(), 5 + rand.nextInt(5));
		placeBlocks(level, state, pos.north(), 5 + rand.nextInt(5));
		placeBlocks(level, state, pos.south().east(), 3 + rand.nextInt(5));
		placeBlocks(level, state, pos.north().west(), 3 + rand.nextInt(5));
		placeBlocks(level, state, pos.south().west(), 3 + rand.nextInt(5));
		placeBlocks(level, state, pos.north().east(), 3 + rand.nextInt(5));
		return true;
	}

	private void placeBlocks(WorldGenLevel level, BlockState state, BlockPos pos, int height) {
		for(int i = 0; i < height; i++)
			if(!level.isOutsideBuildHeight(pos.above(i)))
				level.setBlock(pos.above(i), state, Block.UPDATE_CLIENTS);
	}
}
