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
package com.shinoow.abyssalcraft.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Sapling tree grower pointing at an AbyssalCraft configured feature (data/abyssalcraft/worldgen/configured_feature)
 */
public class ACTreeGrower extends AbstractTreeGrower {

	private final ResourceKey<ConfiguredFeature<?, ?>> feature;

	public ACTreeGrower(String feature) {
		this.feature = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation("abyssalcraft", feature));
	}

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
		return feature;
	}
}
