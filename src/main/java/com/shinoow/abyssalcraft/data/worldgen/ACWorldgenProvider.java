/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - generates the worldgen datapack entries (data/abyssalcraft/worldgen, dimension, dimension_type)
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

public class ACWorldgenProvider extends DatapackBuiltinEntriesProvider {

	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.NOISE, ACNoiseSettings::bootstrapNoises)
			.add(Registries.NOISE_SETTINGS, ACNoiseSettings::bootstrap)
			.add(Registries.DIMENSION_TYPE, ACDimensionData::bootstrapTypes)
			.add(Registries.CONFIGURED_CARVER, ACFeatureData::bootstrapCarvers)
			.add(Registries.CONFIGURED_FEATURE, ACFeatureData::bootstrapConfigured)
			.add(Registries.PLACED_FEATURE, ACFeatureData::bootstrapPlaced)
			.add(Registries.BIOME, ACBiomeData::bootstrap)
			.add(Registries.STRUCTURE, ACStructureData::bootstrapStructures)
			.add(Registries.STRUCTURE_SET, ACStructureData::bootstrapSets)
			.add(Registries.LEVEL_STEM, ACDimensionData::bootstrapLevels)
			.add(net.minecraftforge.registries.ForgeRegistries.Keys.BIOME_MODIFIERS, ACBiomeModifiers::bootstrap);

	public ACWorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(AbyssalCraft.modid));
	}
}
