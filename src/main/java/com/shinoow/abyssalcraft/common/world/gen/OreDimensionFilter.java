/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the "oreGenDimensionBlacklist" config option for the overworld ores
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/** Keeps the AbyssalCraft overworld ores out of the dimensions listed in "oreGenDimensionBlacklist" */
public class OreDimensionFilter extends PlacementFilter {

	public static final OreDimensionFilter INSTANCE = new OreDimensionFilter();
	public static final Codec<OreDimensionFilter> CODEC = Codec.unit(() -> INSTANCE);

	private OreDimensionFilter() {}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		return !ACConfig.oreGenDimBlacklist.contains(context.getLevel().getLevel().dimension().location().toString());
	}

	@Override
	public PlacementModifierType<?> type() {
		return ACFeatures.ORE_DIMENSION_FILTER.get();
	}
}
