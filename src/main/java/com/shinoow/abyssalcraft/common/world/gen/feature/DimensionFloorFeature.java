/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (BiomeGenAbywasteland/BiomeGenDreadlandsBase terrain blocks)
 *     1.20.1 port - one feature per chunk instead of a step of the 1.12.2 biome surface builder
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The bottom of the Abyssal Wasteland and the Dreadlands: bedrock, then a 4 block tall room held up by darkstone
 * brick pillars (chiseled in the middle) every 4 blocks, under a darkstone cobblestone ceiling.
 * Placed once per chunk, at the chunk's origin.
 */
public class DimensionFloorFeature extends Feature<NoneFeatureConfiguration> {

	public DimensionFloorFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos origin = context.origin();
		int minY = level.getMinBuildHeight();
		BlockState bricks = ACBlocks.darkstone_bricks.get().defaultBlockState();
		BlockState chiseled = ACBlocks.chiseled_darkstone_bricks.get().defaultBlockState();
		BlockState ceiling = ACBlocks.darkstone_cobblestone.get().defaultBlockState();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++) {
				int bx = (origin.getX() & ~15) + x, bz = (origin.getZ() & ~15) + z;
				boolean pillar = x % 4 == 2 && z % 4 == 2;
				level.setBlock(pos.set(bx, minY, bz), Blocks.BEDROCK.defaultBlockState(), Block.UPDATE_CLIENTS);
				for(int y = 1; y < 5; y++)
					level.setBlock(pos.set(bx, minY + y, bz), pillar ? y == 3 ? chiseled : bricks : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
				level.setBlock(pos.set(bx, minY + 5, bz), ceiling, Block.UPDATE_CLIENTS);
			}
		return true;
	}
}
