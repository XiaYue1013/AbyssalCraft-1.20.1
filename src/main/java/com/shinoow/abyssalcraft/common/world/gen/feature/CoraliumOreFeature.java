/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - ore veins with the size of the "coraliumOreVeinSize" config option
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

/** A vanilla ore vein whose size comes from the config (the configured size is ignored) */
public class CoraliumOreFeature extends Feature<OreConfiguration> {

	public CoraliumOreFeature(Codec<OreConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<OreConfiguration> context) {
		OreConfiguration config = context.config();
		return Feature.ORE.place(new OreConfiguration(config.targetStates, ACConfig.coraliumOreGeneration[1], config.discardChanceOnAirExposure),
				context.level(), context.chunkGenerator(), context.random(), context.origin());
	}
}
