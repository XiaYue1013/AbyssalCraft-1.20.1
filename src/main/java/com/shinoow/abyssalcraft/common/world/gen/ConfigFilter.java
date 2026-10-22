/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - placement filter for the world generation config toggles
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import java.util.Map;
import java.util.function.BooleanSupplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

/**
 * Only lets a placed feature generate while its config option (same names as in abyssalcraft-common.toml, section
 * "worldgen") is enabled. Usage: <code>{"type": "abyssalcraft:config", "option": "liquidCoraliumLakes"}</code>
 */
public class ConfigFilter extends PlacementFilter {

	private static final Map<String, BooleanSupplier> OPTIONS = Map.ofEntries(
			Map.entry("darklandsStructures", () -> ACConfig.generateDarklandsStructures),
			Map.entry("shoggothLairs", () -> ACConfig.generateShoggothLairs),
			Map.entry("abyssalWastelandPillars", () -> ACConfig.generateAbyssalWastelandPillars),
			Map.entry("abyssalWastelandRuins", () -> ACConfig.generateAbyssalWastelandRuins),
			Map.entry("liquidAntimatterLakes", () -> ACConfig.generateAntimatterLake),
			Map.entry("liquidCoraliumLakes", () -> ACConfig.generateCoraliumLake),
			Map.entry("dreadlandsStalagmites", () -> ACConfig.generateDreadlandsStalagmite),
			Map.entry("omotholCity", () -> ACConfig.generateOmotholStructures),
			Map.entry("coraliumOre", () -> ACConfig.generateCoraliumOre),
			Map.entry("nitreOre", () -> ACConfig.generateNitreOre),
			Map.entry("abyssalniteOre", () -> ACConfig.generateAbyssalniteOre),
			Map.entry("abyssalCoraliumOre", () -> ACConfig.generateAbyssalCoraliumOre),
			Map.entry("dreadlandsAbyssalniteOre", () -> ACConfig.generateDreadlandsAbyssalniteOre),
			Map.entry("dreadedAbyssalniteOre", () -> ACConfig.generateDreadedAbyssalniteOre),
			Map.entry("abyssalIronOre", () -> ACConfig.generateAbyssalIronOre),
			Map.entry("abyssalGoldOre", () -> ACConfig.generateAbyssalGoldOre),
			Map.entry("abyssalDiamondOre", () -> ACConfig.generateAbyssalDiamondOre),
			Map.entry("abyssalNitreOre", () -> ACConfig.generateAbyssalNitreOre),
			Map.entry("abyssalTinOre", () -> ACConfig.generateAbyssalTinOre),
			Map.entry("abyssalCopperOre", () -> ACConfig.generateAbyssalCopperOre),
			Map.entry("pearlescentCoraliumOre", () -> ACConfig.generatePearlescentCoraliumOre),
			Map.entry("liquifiedCoraliumOre", () -> ACConfig.generateLiquifiedCoraliumOre));

	public static final Codec<ConfigFilter> CODEC = Codec.STRING.comapFlatMap(
			option -> OPTIONS.containsKey(option) ? DataResult.success(new ConfigFilter(option)) : DataResult.error(() -> "Unknown AbyssalCraft worldgen option: " + option),
			filter -> filter.option).fieldOf("option").codec();

	private final String option;

	public ConfigFilter(String option) {
		this.option = option;
	}

	public static ConfigFilter of(String option) {
		if(!OPTIONS.containsKey(option))
			throw new IllegalArgumentException("Unknown AbyssalCraft worldgen option: " + option);
		return new ConfigFilter(option);
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		return OPTIONS.get(option).getAsBoolean();
	}

	@Override
	public PlacementModifierType<?> type() {
		return ACFeatures.CONFIG.get();
	}
}
