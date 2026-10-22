/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (WorldGenAbyLake, WorldGenAntimatterLake)
 *     1.20.1 port - both lakes in one configurable feature
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * The 1.12.2 lake generator with a configurable filling. Abyssal Wasteland lakes turn the exposed abyssal stone
 * below their dry upper half into Liquified Coralium Ore when they're low enough (y <= 32), other lakes grow grass on
 * exposed dirt like vanilla lakes did.
 */
public class ACLakeFeature extends Feature<ACLakeFeature.Configuration> {

	public record Configuration(BlockState fill, boolean coraliumRim) implements FeatureConfiguration {
		public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(i -> i.group(
				BlockState.CODEC.fieldOf("fill").forGetter(Configuration::fill),
				Codec.BOOL.optionalFieldOf("coralium_rim", false).forGetter(Configuration::coraliumRim)).apply(i, Configuration::new));
	}

	public ACLakeFeature(Codec<Configuration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Configuration> context) {
		WorldGenLevel level = context.level();
		RandomSource rand = context.random();
		BlockState fill = context.config().fill();
		BlockPos position = context.origin().offset(-8, 0, -8);
		int bottom = level.getMinBuildHeight();
		while(position.getY() > bottom + 5 && level.isEmptyBlock(position))
			position = position.below();
		if(position.getY() <= bottom + 4)
			return false;
		position = position.below(4);
		int n = position.getY();

		boolean[] shape = new boolean[2048];
		int blobs = rand.nextInt(4) + 4;
		for(int j = 0; j < blobs; ++j) {
			double d0 = rand.nextDouble() * 6.0D + 3.0D;
			double d1 = rand.nextDouble() * 4.0D + 2.0D;
			double d2 = rand.nextDouble() * 6.0D + 3.0D;
			double d3 = rand.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
			double d4 = rand.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
			double d5 = rand.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;
			for(int l = 1; l < 15; ++l)
				for(int i1 = 1; i1 < 15; ++i1)
					for(int j1 = 1; j1 < 7; ++j1) {
						double d6 = (l - d3) / (d0 / 2.0D);
						double d7 = (j1 - d4) / (d1 / 2.0D);
						double d8 = (i1 - d5) / (d2 / 2.0D);
						if(d6 * d6 + d7 * d7 + d8 * d8 < 1.0D)
							shape[(l * 16 + i1) * 8 + j1] = true;
					}
		}

		for(int x = 0; x < 16; ++x)
			for(int z = 0; z < 16; ++z)
				for(int y = 0; y < 8; ++y) {
					boolean edge = !shape[(x * 16 + z) * 8 + y] && (x < 15 && shape[((x + 1) * 16 + z) * 8 + y] || x > 0 && shape[((x - 1) * 16 + z) * 8 + y]
							|| z < 15 && shape[(x * 16 + z + 1) * 8 + y] || z > 0 && shape[(x * 16 + z - 1) * 8 + y]
							|| y < 7 && shape[(x * 16 + z) * 8 + y + 1] || y > 0 && shape[(x * 16 + z) * 8 + y - 1]);
					if(edge) {
						BlockState state = level.getBlockState(position.offset(x, y, z));
						if(y >= 4 && !state.getFluidState().isEmpty())
							return false;
						if(y < 4 && !state.isSolid() && state != fill)
							return false;
					}
				}

		for(int x = 0; x < 16; ++x)
			for(int z = 0; z < 16; ++z)
				for(int y = 0; y < 8; ++y)
					if(shape[(x * 16 + z) * 8 + y])
						level.setBlock(position.offset(x, y, z), y >= 4 ? Blocks.AIR.defaultBlockState() : fill, Block.UPDATE_CLIENTS);

		for(int x = 0; x < 16; ++x)
			for(int z = 0; z < 16; ++z)
				for(int y = 4; y < 8; ++y)
					if(shape[(x * 16 + z) * 8 + y]) {
						BlockPos below = position.offset(x, y - 1, z);
						if(level.getBrightness(LightLayer.SKY, position.offset(x, y, z)) <= 0)
							continue;
						BlockState state = level.getBlockState(below);
						if(context.config().coraliumRim()) {
							if(state.is(ACBlocks.abyssal_stone.get()) && n <= 32)
								level.setBlock(below, ACBlocks.liquified_coralium_ore.get().defaultBlockState(), Block.UPDATE_CLIENTS);
						} else if(state.is(Blocks.DIRT))
							level.setBlock(below, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
					}
		return true;
	}
}
