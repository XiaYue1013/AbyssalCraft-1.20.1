/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - biome tags (1.12.2: IDarklandsBiome/IDreadlandsBiome and BiomeDictionary types)
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.concurrent.CompletableFuture;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ACBiomeTagsProvider extends BiomeTagsProvider {

	public ACBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
		super(output, lookup, AbyssalCraft.modid, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ACTags.Biomes.SHUB_OFFSPRING_SPAWNS).add(net.minecraft.world.level.biome.Biomes.FOREST,
				net.minecraft.world.level.biome.Biomes.BIRCH_FOREST, net.minecraft.world.level.biome.Biomes.OLD_GROWTH_BIRCH_FOREST,
				net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA, net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA,
				net.minecraft.world.level.biome.Biomes.FLOWER_FOREST);
		tag(ACTags.Biomes.SHUB_OFFSPRING_DENSE_SPAWNS).add(ACBiomes.darklands_forest, net.minecraft.world.level.biome.Biomes.DARK_FOREST);
		tag(ACBiomes.IS_DARKLANDS).add(ACBiomes.darklands, ACBiomes.darklands_forest, ACBiomes.darklands_plains, ACBiomes.darklands_hills, ACBiomes.darklands_mountains);
		tag(ACBiomes.IS_DREADLANDS).add(ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest, ACBiomes.dreadlands_mountains);

		// 1.12.2: Shoggoth Lairs in swamps and rivers of the overworld, and in every dimension biome except Omothol's own
		tag(ACTags.Biomes.HAS_SHOGGOTH_LAIR).add(ACBiomes.abyssal_wastelands, ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest,
				ACBiomes.dreadlands_mountains, ACBiomes.dark_realm);
		tag(ACTags.Biomes.HAS_SHOGGOTH_LAIR_OVERWORLD).addTag(Tags.Biomes.IS_SWAMP).addTag(BiomeTags.IS_RIVER);
		tag(ACTags.Biomes.HAS_OMOTHOL_STRUCTURES).add(ACBiomes.omothol);
		tag(ACTags.Biomes.HAS_ABYSSAL_STRONGHOLD).add(ACBiomes.abyssal_wastelands);
		tag(ACTags.Biomes.HAS_DREADLANDS_MINE).addTag(ACBiomes.IS_DREADLANDS);
		tag(ACTags.Biomes.EVIL_ANIMAL_SPAWNS).add(net.minecraft.world.level.biome.Biomes.TAIGA, net.minecraft.world.level.biome.Biomes.PLAINS,
				net.minecraft.world.level.biome.Biomes.FOREST, net.minecraft.world.level.biome.Biomes.SAVANNA, net.minecraft.world.level.biome.Biomes.BEACH,
				net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS, net.minecraft.world.level.biome.Biomes.JUNGLE, net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU,
				net.minecraft.world.level.biome.Biomes.SWAMP, net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS, net.minecraft.world.level.biome.Biomes.BIRCH_FOREST,
				net.minecraft.world.level.biome.Biomes.DARK_FOREST);
		tag(ACTags.Biomes.CORALIUM_MOB_SPAWNS).addTag(BiomeTags.IS_OCEAN).addTag(BiomeTags.IS_RIVER).addTag(BiomeTags.IS_BEACH).addTag(Tags.Biomes.IS_SWAMP);
		tag(ACTags.Biomes.CORALIUM_ORE_OCEANS).add(net.minecraft.world.level.biome.Biomes.OCEAN, net.minecraft.world.level.biome.Biomes.COLD_OCEAN,
				net.minecraft.world.level.biome.Biomes.LUKEWARM_OCEAN, net.minecraft.world.level.biome.Biomes.WARM_OCEAN, net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN);

		tag(BiomeTags.IS_OVERWORLD).add(ACBiomes.darklands, ACBiomes.darklands_forest, ACBiomes.darklands_plains, ACBiomes.darklands_hills,
				ACBiomes.darklands_mountains, ACBiomes.coralium_infested_swamp, ACBiomes.purged);
		tag(BiomeTags.IS_FOREST).add(ACBiomes.darklands_forest);
		tag(BiomeTags.IS_HILL).add(ACBiomes.darklands_hills);
		tag(BiomeTags.IS_MOUNTAIN).add(ACBiomes.darklands_mountains);
		tag(Tags.Biomes.IS_SWAMP).add(ACBiomes.coralium_infested_swamp);
		tag(Tags.Biomes.IS_PLAINS).add(ACBiomes.darklands_plains);
		tag(Tags.Biomes.IS_WASTELAND).add(ACBiomes.abyssal_wastelands, ACBiomes.purged);
		tag(Tags.Biomes.IS_SPOOKY).add(ACBiomes.darklands, ACBiomes.darklands_forest, ACBiomes.darklands_plains, ACBiomes.darklands_hills,
				ACBiomes.darklands_mountains, ACBiomes.dark_realm, ACBiomes.omothol);
		tag(Tags.Biomes.IS_DEAD).add(ACBiomes.abyssal_wastelands, ACBiomes.purged, ACBiomes.dreadlands, ACBiomes.dreadlands_mountains);
		tag(Tags.Biomes.IS_MOUNTAIN).add(ACBiomes.darklands_mountains, ACBiomes.dreadlands_mountains);
		tag(Tags.Biomes.IS_DRY).add(ACBiomes.abyssal_wastelands, ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest,
				ACBiomes.dreadlands_mountains, ACBiomes.omothol, ACBiomes.dark_realm, ACBiomes.purged);
	}
}
