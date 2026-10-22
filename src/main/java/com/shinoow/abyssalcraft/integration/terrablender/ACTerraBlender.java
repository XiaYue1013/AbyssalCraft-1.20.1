/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - overworld biomes through TerraBlender (1.12.2 added them with BiomeManager weights). Only loaded
 *     when TerraBlender is installed.
 ******************************************************************************/
package com.shinoow.abyssalcraft.integration.terrablender;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

/**
 * One overworld region in which vanilla biomes are replaced by their closest AbyssalCraft counterparts: forests become
 * Darklands, dark forests Darklands Forests, plains Darklands Plains, windswept hills Darklands Highlands, stony peaks
 * Darklands Mountains and swamps Coralium Infested Swamps. TerraBlender places regions instead of single biomes, so the
 * 1.12.2 biome weights decide the region's weight (half the highest enabled weight).
 */
public class ACTerraBlender {

	public static void register() {
		int weight = 0;
		if(ACConfig.darklands) weight = Math.max(weight, ACConfig.darklandsWeight);
		if(ACConfig.darklandsForest) weight = Math.max(weight, ACConfig.darklandsForestWeight);
		if(ACConfig.darklandsPlains) weight = Math.max(weight, ACConfig.darklandsPlainsWeight);
		if(ACConfig.darklandsHighland) weight = Math.max(weight, ACConfig.darklandsHighlandWeight);
		if(ACConfig.darklandsMountain) weight = Math.max(weight, ACConfig.darklandsMountainWeight);
		if(ACConfig.coraliumSwamp) weight = Math.max(weight, ACConfig.coraliumSwampWeight);
		if(weight <= 0) {
			AbyssalCraft.LOGGER.info("All AbyssalCraft overworld biomes are disabled, no TerraBlender region added");
			return;
		}
		Regions.register(new OverworldRegion(Math.max(1, weight / 2)));
		SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, AbyssalCraft.modid, surfaceRules());
	}

	/** BiomeGenDarklandsMountains: darkstone top and filler; BiomeGenDarklandsHills: darkstone filler */
	private static SurfaceRules.RuleSource surfaceRules() {
		SurfaceRules.RuleSource darkstone = SurfaceRules.state(ACBlocks.darkstone.get().defaultBlockState());
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.isBiome(ACBiomes.darklands_mountains), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, darkstone)),
				SurfaceRules.ifTrue(SurfaceRules.isBiome(ACBiomes.darklands_hills), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
						SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.ON_FLOOR), darkstone))));
	}

	private static class OverworldRegion extends Region {

		OverworldRegion(int weight) {
			super(AbyssalCraft.loc("overworld"), RegionType.OVERWORLD, weight);
		}

		@Override
		public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
			addModifiedVanillaOverworldBiomes(mapper, builder -> {
				if(ACConfig.darklands) builder.replaceBiome(Biomes.FOREST, ACBiomes.darklands);
				if(ACConfig.darklandsForest) builder.replaceBiome(Biomes.DARK_FOREST, ACBiomes.darklands_forest);
				if(ACConfig.darklandsPlains) builder.replaceBiome(Biomes.PLAINS, ACBiomes.darklands_plains);
				if(ACConfig.darklandsHighland) builder.replaceBiome(Biomes.WINDSWEPT_HILLS, ACBiomes.darklands_hills);
				if(ACConfig.darklandsMountain) builder.replaceBiome(Biomes.STONY_PEAKS, ACBiomes.darklands_mountains);
				if(ACConfig.coraliumSwamp) builder.replaceBiome(Biomes.SWAMP, ACBiomes.coralium_infested_swamp);
			});
		}
	}
}
