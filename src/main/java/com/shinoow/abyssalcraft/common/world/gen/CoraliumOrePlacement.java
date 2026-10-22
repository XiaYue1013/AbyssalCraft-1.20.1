/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (AbyssalCraftWorldGenerator#generateSurface)
 *     1.20.1 port - the Coralium Ore vein count and height of the config as a placement modifier
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * "coraliumOreVeinCount" times the count factor veins (half in swamps), anywhere in the chunk, from the bottom of
 * the 1.12.2 world (y 0) up to "coraliumOreMaxHeight", shifted by the y offset (deep oceans: 20 blocks lower)
 */
public class CoraliumOrePlacement extends PlacementModifier {

	public static final Codec<CoraliumOrePlacement> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.FLOAT.fieldOf("count_factor").forGetter(p -> p.countFactor),
			Codec.INT.optionalFieldOf("y_offset", 0).forGetter(p -> p.yOffset)).apply(i, CoraliumOrePlacement::new));

	private final float countFactor;
	private final int yOffset;

	public CoraliumOrePlacement(float countFactor, int yOffset) {
		this.countFactor = countFactor;
		this.yOffset = yOffset;
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		int count = (int) (ACConfig.coraliumOreGeneration[0] * countFactor);
		int maxHeight = Math.max(1, ACConfig.coraliumOreGeneration[2]);
		return IntStream.range(0, count).mapToObj(i -> new BlockPos(pos.getX() + random.nextInt(16), random.nextInt(maxHeight) + yOffset, pos.getZ() + random.nextInt(16)));
	}

	@Override
	public PlacementModifierType<?> type() {
		return ACFeatures.CORALIUM_ORE_PLACEMENT.get();
	}
}
