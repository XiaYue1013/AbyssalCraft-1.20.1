/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - carvers and features of the 1.12.2 chunk providers and biome decorators. Counts, vein sizes and
 *     heights are the 1.12.2 ones (random vein sizes use their average); config toggles through abyssalcraft:config.
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.world.gen.ConfigFilter;
import com.shinoow.abyssalcraft.common.world.gen.CoraliumOrePlacement;
import com.shinoow.abyssalcraft.common.world.gen.OreDimensionFilter;
import com.shinoow.abyssalcraft.common.world.gen.feature.ACLakeFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.LegacyStructureFeature;
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ACFeatureData {

	// Carvers
	/** Abyssal Wasteland / Dark Realm caves (1.12.2 MapGenCavesAC: lava below y 10) */
	public static final ResourceKey<ConfiguredWorldCarver<?>> CAVE = carver("cave");
	public static final ResourceKey<ConfiguredWorldCarver<?>> CANYON = carver("canyon");
	/** Dreadlands caves: every carved block below y 32 becomes lava (1.12.2 ChunkProviderDreadlands#genLava) */
	public static final ResourceKey<ConfiguredWorldCarver<?>> DREADLANDS_CAVE = carver("dreadlands_cave");
	public static final ResourceKey<ConfiguredWorldCarver<?>> DREADLANDS_CANYON = carver("dreadlands_canyon");
	/** 1.12.2 MapGenCavesDreadlands was the Nether cave generator */
	public static final ResourceKey<ConfiguredWorldCarver<?>> DREADLANDS_NETHER_CAVE = carver("dreadlands_nether_cave");

	// Features, configured and placed under the same name
	public static final String DIMENSION_FLOOR = "dimension_floor";
	public static final String DARKLANDS_TREE = "darklands_oak_tree", DREADLANDS_TREE = "dreadlands_tree";
	public static final String DREADLANDS_STALAGMITE = "dreadlands_stalagmite";
	public static final String CORALIUM_LAKE = "coralium_lake", ABYSSAL_STONE_LAKE = "abyssal_stone_lake", ANTIMATTER_LAKE = "antimatter_lake";
	public static final String LUMINOUS_THISTLE = "patch_luminous_thistle", WASTELANDS_THORN = "patch_wastelands_thorn";
	public static final String ORE_LIQUIFIED_CORALIUM = "ore_liquified_coralium", ORE_ABYSSAL_CORALIUM = "ore_abyssal_coralium",
			ORE_ABYSSAL_NITRE = "ore_abyssal_nitre", ORE_ABYSSAL_IRON = "ore_abyssal_iron", ORE_ABYSSAL_COPPER = "ore_abyssal_copper",
			ORE_ABYSSAL_TIN = "ore_abyssal_tin", ORE_ABYSSAL_GOLD = "ore_abyssal_gold", ORE_ABYSSAL_DIAMOND = "ore_abyssal_diamond",
			ORE_PEARLESCENT_CORALIUM = "ore_pearlescent_coralium";
	public static final String ORE_DREADED_ABYSSALNITE = "ore_dreaded_abyssalnite", ABYSSALNITE_STONE_DREADLANDS = "abyssalnite_stone_dreadlands";
	public static final String ORE_DREADLANDS_ABYSSALNITE_LARGE = "ore_dreadlands_abyssalnite_large", ORE_DREADLANDS_ABYSSALNITE_SMALL = "ore_dreadlands_abyssalnite_small",
			ABYSSALNITE_STONE_LARGE = "abyssalnite_stone_large", ABYSSALNITE_STONE_MEDIUM = "abyssalnite_stone_medium", ABYSSALNITE_STONE_SMALL = "abyssalnite_stone_small";
	public static final String DARKLANDS_TREES = "darklands_trees", DARKLANDS_FOREST_TREES = "darklands_forest_trees", DARKLANDS_PLAINS_TREES = "darklands_plains_trees",
			DREADLANDS_FOREST_TREES = "dreadlands_forest_trees";
	public static final String CHAINS = "chains", ABYSSAL_RUIN = "abyssal_ruin";
	/** DarklandsStructureGenerator, in its 1.12.2 order */
	public static final List<String> DARKLANDS_STRUCTURES = List.of("darklands_shrine", "darklands_ritual_grounds", "darklands_house", "darklands_misc",
			"darklands_structure");
	/** AbyssalCraftWorldGenerator (added to vanilla biomes through biome modifiers) and BiomeGenCorSwamp's ore swap */
	public static final String OVERWORLD_CORALIUM_ORE = "overworld_coralium_ore", ORE_CORALIUM_SWAMPS = "ore_coralium_swamps", ORE_CORALIUM_OCEANS = "ore_coralium_oceans",
			ORE_CORALIUM_DEEP_OCEANS = "ore_coralium_deep_oceans", ORE_NITRE = "ore_nitre", CORALIUM_ORE_SWAP = "coralium_ore_swap";
	public static final String ORE_ABYSSALNITE = "ore_abyssalnite", DARKSTONE = "darkstone", DARKSTONE_LARGE = "darkstone_large", ORE_CORALIUM_SWAMP = "ore_coralium_swamp";

	public static void bootstrapCarvers(BootstapContext<ConfiguredWorldCarver<?>> context) {
		HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
		var replaceable = blocks.getOrThrow(ACTags.Blocks.CARVER_REPLACEABLES);
		context.register(CAVE, cave(ACFeatures.CAVE.get(), 0.15F, VerticalAnchor.absolute(10), replaceable));
		context.register(CANYON, canyon(VerticalAnchor.absolute(10), replaceable));
		context.register(DREADLANDS_CAVE, cave(ACFeatures.CAVE.get(), 0.15F, VerticalAnchor.absolute(31), replaceable));
		context.register(DREADLANDS_CANYON, canyon(VerticalAnchor.absolute(31), replaceable));
		context.register(DREADLANDS_NETHER_CAVE, WorldCarver.NETHER_CAVE.configured(new CaveCarverConfiguration(0.2F, UniformHeight.of(VerticalAnchor.aboveBottom(6),
				VerticalAnchor.absolute(127)), ConstantFloat.of(0.5F), VerticalAnchor.absolute(31), replaceable, ConstantFloat.of(1.0F), ConstantFloat.of(1.0F),
				ConstantFloat.of(-0.7F))));
	}

	private static ConfiguredWorldCarver<?> cave(WorldCarver<CaveCarverConfiguration> carver, float probability, VerticalAnchor lava, HolderSet<Block> replaceable) {
		return carver.configured(new CaveCarverConfiguration(probability, UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(127)),
				UniformFloat.of(0.1F, 0.9F), lava, CarverDebugSettings.DEFAULT, replaceable, UniformFloat.of(0.7F, 1.4F), UniformFloat.of(0.8F, 1.3F),
				UniformFloat.of(-1.0F, -0.4F)));
	}

	private static ConfiguredWorldCarver<?> canyon(VerticalAnchor lava, HolderSet<Block> replaceable) {
		return ACFeatures.CANYON.get().configured(new CanyonCarverConfiguration(0.02F, UniformHeight.of(VerticalAnchor.absolute(20), VerticalAnchor.absolute(67)),
				ConstantFloat.of(3.0F), lava, CarverDebugSettings.DEFAULT, replaceable, UniformFloat.of(-0.125F, 0.125F),
				new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3,
						UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F)));
	}

	public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
		register(context, DIMENSION_FLOOR, ACFeatures.DIMENSION_FLOOR.get(), NoneFeatureConfiguration.INSTANCE);
		register(context, DARKLANDS_TREE, ACFeatures.DARKLANDS_TREE.get(), NoneFeatureConfiguration.INSTANCE);
		register(context, DREADLANDS_TREE, ACFeatures.DREADLANDS_TREE.get(), NoneFeatureConfiguration.INSTANCE);
		register(context, DREADLANDS_STALAGMITE, ACFeatures.DREADLANDS_STALAGMITE.get(), NoneFeatureConfiguration.INSTANCE);
		register(context, CORALIUM_LAKE, ACFeatures.LAKE.get(), new ACLakeFeature.Configuration(state(ACBlocks.liquid_coralium), true));
		register(context, ABYSSAL_STONE_LAKE, ACFeatures.LAKE.get(), new ACLakeFeature.Configuration(state(ACBlocks.abyssal_stone), true));
		register(context, ANTIMATTER_LAKE, ACFeatures.LAKE.get(), new ACLakeFeature.Configuration(state(ACBlocks.liquid_antimatter), false));
		register(context, CHAINS, ACFeatures.CODED_STRUCTURE.get(), LegacyStructureFeature.Type.CHAINS);
		register(context, ABYSSAL_RUIN, ACFeatures.CODED_STRUCTURE.get(), LegacyStructureFeature.Type.ABYSSAL_RUIN);
		LegacyStructureFeature.Type[] darklands = {LegacyStructureFeature.Type.DARKLANDS_SHRINE, LegacyStructureFeature.Type.DARKLANDS_RITUAL_GROUNDS,
				LegacyStructureFeature.Type.DARKLANDS_HOUSE, LegacyStructureFeature.Type.DARKLANDS_MISC, LegacyStructureFeature.Type.DARKLANDS_ANY};
		for(int i = 0; i < darklands.length; i++)
			register(context, DARKLANDS_STRUCTURES.get(i), ACFeatures.CODED_STRUCTURE.get(), darklands[i]);
		register(context, LUMINOUS_THISTLE, Feature.RANDOM_PATCH, bush(state(ACBlocks.luminous_thistle)));
		register(context, WASTELANDS_THORN, Feature.RANDOM_PATCH, bush(state(ACBlocks.wastelands_thorn)));

		RuleTest abyssalStone = new BlockMatchTest(ACBlocks.abyssal_stone.get());
		ore(context, ORE_LIQUIFIED_CORALIUM, ACBlocks.liquified_coralium_ore, abyssalStone, 3);
		ore(context, ORE_ABYSSAL_CORALIUM, ACBlocks.abyssal_coralium_ore, abyssalStone, 6);
		ore(context, ORE_ABYSSAL_NITRE, ACBlocks.abyssal_nitre_ore, abyssalStone, 5);
		ore(context, ORE_ABYSSAL_IRON, ACBlocks.abyssal_iron_ore, abyssalStone, 5);
		ore(context, ORE_ABYSSAL_COPPER, ACBlocks.abyssal_copper_ore, abyssalStone, 5);
		ore(context, ORE_ABYSSAL_TIN, ACBlocks.abyssal_tin_ore, abyssalStone, 5);
		ore(context, ORE_ABYSSAL_GOLD, ACBlocks.abyssal_gold_ore, abyssalStone, 3);
		ore(context, ORE_ABYSSAL_DIAMOND, ACBlocks.abyssal_diamond_ore, abyssalStone, 3);
		ore(context, ORE_PEARLESCENT_CORALIUM, ACBlocks.pearlescent_coralium_ore, abyssalStone, 3);

		RuleTest dreadstone = new BlockMatchTest(ACBlocks.dreadstone.get());
		ore(context, ORE_DREADED_ABYSSALNITE, ACBlocks.dreaded_abyssalnite_ore, dreadstone, 10);
		ore(context, ABYSSALNITE_STONE_DREADLANDS, ACBlocks.abyssalnite_stone, dreadstone, 16);
		ore(context, ORE_DREADLANDS_ABYSSALNITE_LARGE, ACBlocks.dreadlands_abyssalnite_ore, dreadstone, 14);
		ore(context, ORE_DREADLANDS_ABYSSALNITE_SMALL, ACBlocks.dreadlands_abyssalnite_ore, dreadstone, 4);
		ore(context, ABYSSALNITE_STONE_LARGE, ACBlocks.abyssalnite_stone, dreadstone, 48);
		ore(context, ABYSSALNITE_STONE_MEDIUM, ACBlocks.abyssalnite_stone, dreadstone, 36);
		ore(context, ABYSSALNITE_STONE_SMALL, ACBlocks.abyssalnite_stone, dreadstone, 24);

		RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
		ore(context, ORE_ABYSSALNITE, ACBlocks.abyssalnite_ore, stone, 2);
		ore(context, DARKSTONE, ACBlocks.darkstone, stone, 20);
		ore(context, DARKSTONE_LARGE, ACBlocks.darkstone, stone, 32);
		ore(context, ORE_CORALIUM_SWAMP, ACBlocks.coralium_ore, stone, 5);
		register(context, OVERWORLD_CORALIUM_ORE, ACFeatures.CORALIUM_ORE.get(), new OreConfiguration(stone, ACBlocks.coralium_ore.get().defaultBlockState(), 8));
		ore(context, ORE_NITRE, ACBlocks.nitre_ore, stone, 5);
		register(context, CORALIUM_ORE_SWAP, Feature.REPLACE_SINGLE_BLOCK, new ReplaceBlockConfiguration(List.of(
				OreConfiguration.target(new BlockMatchTest(Blocks.IRON_ORE), ACBlocks.coralium_ore.get().defaultBlockState()),
				OreConfiguration.target(new BlockMatchTest(Blocks.COAL_ORE), ACBlocks.coralium_ore.get().defaultBlockState()))));
	}

	public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);

		place(context, features, DIMENSION_FLOOR, DIMENSION_FLOOR);
		// The trees are also grown from saplings, so the placed tree features have their own names
		place(context, features, DARKLANDS_TREES, DARKLANDS_TREE, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(),
				SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
		place(context, features, DARKLANDS_FOREST_TREES, DARKLANDS_TREE, PlacementUtils.countExtra(10, 0.1F, 1), InSquarePlacement.spread(),
				SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
		place(context, features, DARKLANDS_PLAINS_TREES, DARKLANDS_TREE, RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(),
				SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
		place(context, features, DREADLANDS_FOREST_TREES, DREADLANDS_TREE, PlacementUtils.countExtra(6, 0.1F, 1), InSquarePlacement.spread(),
				PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
		place(context, features, DREADLANDS_STALAGMITE, DREADLANDS_STALAGMITE, ConfigFilter.of("dreadlandsStalagmites"), InSquarePlacement.spread(),
				PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

		place(context, features, CORALIUM_LAKE, CORALIUM_LAKE, ConfigFilter.of("liquidCoraliumLakes"), RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
				range(0, 127), BiomeFilter.biome());
		place(context, features, ABYSSAL_STONE_LAKE, ABYSSAL_STONE_LAKE, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), range(0, 127), BiomeFilter.biome());
		place(context, features, ANTIMATTER_LAKE, ANTIMATTER_LAKE, ConfigFilter.of("liquidAntimatterLakes"), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
				range(0, 59), BiomeFilter.biome());
		// ChunkProviderAbyss#populate: pillars in 1 of 100 chunks, ruins in 1 of 200
		place(context, features, CHAINS, CHAINS, ConfigFilter.of("abyssalWastelandPillars"), RarityFilter.onAverageOnceEvery(100), InSquarePlacement.spread(),
				BiomeFilter.biome());
		place(context, features, ABYSSAL_RUIN, ABYSSAL_RUIN, ConfigFilter.of("abyssalWastelandRuins"), RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(),
				PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
		// AbyssalCraftWorldGenerator: five attempts per Darklands chunk, each with a 3 % chance once the ground is flat
		for(String name : DARKLANDS_STRUCTURES)
			place(context, features, name, name, ConfigFilter.of("darklandsStructures"), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
		place(context, features, LUMINOUS_THISTLE, LUMINOUS_THISTLE, CountPlacement.of(5), RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
				PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
		place(context, features, WASTELANDS_THORN, WASTELANDS_THORN, CountPlacement.of(5), RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
				PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

		// BiomeGenAbywasteland#decorate
		placeOre(context, features, ORE_LIQUIFIED_CORALIUM, "liquifiedCoraliumOre", CountPlacement.of(8), 5, 34);
		placeOre(context, features, ORE_ABYSSAL_CORALIUM, "abyssalCoraliumOre", CountPlacement.of(UniformInt.of(8, 15)), 5, 79);
		placeOre(context, features, ORE_ABYSSAL_NITRE, "abyssalNitreOre", CountPlacement.of(8), 5, 64);
		placeOre(context, features, ORE_ABYSSAL_IRON, "abyssalIronOre", CountPlacement.of(8), 5, 64);
		placeOre(context, features, ORE_ABYSSAL_COPPER, "abyssalCopperOre", CountPlacement.of(8), 5, 64);
		placeOre(context, features, ORE_ABYSSAL_TIN, "abyssalTinOre", CountPlacement.of(8), 5, 64);
		placeOre(context, features, ORE_ABYSSAL_GOLD, "abyssalGoldOre", CountPlacement.of(5), 5, 39);
		placeOre(context, features, ORE_ABYSSAL_DIAMOND, "abyssalDiamondOre", CountPlacement.of(5), 5, 24);
		placeOre(context, features, ORE_PEARLESCENT_CORALIUM, "pearlescentCoraliumOre", CountPlacement.of(UniformInt.of(3, 8)), 5, 19);
		// BiomeGenDreadlandsBase#decorate and BiomeGenAbyDreadlands#decorate
		placeOre(context, features, ORE_DREADED_ABYSSALNITE, "dreadedAbyssalniteOre", CountPlacement.of(8), 5, 64);
		placeOre(context, features, ABYSSALNITE_STONE_DREADLANDS, null, CountPlacement.of(3), 5, 59);
		placeOre(context, features, ORE_DREADLANDS_ABYSSALNITE_LARGE, "dreadlandsAbyssalniteOre", CountPlacement.of(10), 5, 64);
		placeOre(context, features, ORE_DREADLANDS_ABYSSALNITE_SMALL, "dreadlandsAbyssalniteOre", CountPlacement.of(8), 5, 59);
		placeOre(context, features, ABYSSALNITE_STONE_LARGE, null, CountPlacement.of(7), 5, 68);
		placeOre(context, features, ABYSSALNITE_STONE_MEDIUM, null, CountPlacement.of(7), 5, 68);
		placeOre(context, features, ABYSSALNITE_STONE_SMALL, null, CountPlacement.of(7), 5, 68);
		// Darklands biomes and the Coralium Infested Swamp
		placeOre(context, features, ORE_ABYSSALNITE, "abyssalniteOre", CountPlacement.of(UniformInt.of(3, 8)), 4, 31);
		placeOre(context, features, DARKSTONE, null, CountPlacement.of(7), 0, 63);
		placeOre(context, features, DARKSTONE_LARGE, null, CountPlacement.of(7), 0, 63);
		placeOre(context, features, ORE_CORALIUM_SWAMP, "coraliumOre", CountPlacement.of(9), 0, 62);
		// overworld: half the veins in swamps, all of them in oceans, 20 blocks lower in deep oceans; nitre everywhere
		place(context, features, ORE_CORALIUM_SWAMPS, OVERWORLD_CORALIUM_ORE, ConfigFilter.of("coraliumOre"), OreDimensionFilter.INSTANCE,
				new CoraliumOrePlacement(0.5F, 0), BiomeFilter.biome());
		place(context, features, ORE_CORALIUM_OCEANS, OVERWORLD_CORALIUM_ORE, ConfigFilter.of("coraliumOre"), OreDimensionFilter.INSTANCE,
				new CoraliumOrePlacement(1.0F, 0), BiomeFilter.biome());
		place(context, features, ORE_CORALIUM_DEEP_OCEANS, OVERWORLD_CORALIUM_ORE, ConfigFilter.of("coraliumOre"), OreDimensionFilter.INSTANCE,
				new CoraliumOrePlacement(1.0F, -20), BiomeFilter.biome());
		place(context, features, ORE_NITRE, ORE_NITRE, ConfigFilter.of("nitreOre"), OreDimensionFilter.INSTANCE, CountPlacement.of(3), InSquarePlacement.spread(),
				range(0, 29), BiomeFilter.biome());
		place(context, features, CORALIUM_ORE_SWAP, CORALIUM_ORE_SWAP, ConfigFilter.of("coraliumOre"), CountPlacement.of(UniformInt.of(3, 8)), InSquarePlacement.spread(),
				range(4, 31), BiomeFilter.biome());
	}

	private static RandomPatchConfiguration bush(BlockState state) {
		return new RandomPatchConfiguration(64, 7, 3, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(state)),
				BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.AIR), BlockPredicate.wouldSurvive(state, BlockPos.ZERO))));
	}

	private static void ore(BootstapContext<ConfiguredFeature<?, ?>> context, String name, Supplier<? extends Block> ore, RuleTest target, int size) {
		register(context, name, Feature.ORE, new OreConfiguration(target, ore.get().defaultBlockState(), size));
	}

	private static void placeOre(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> features, String name, String option,
			PlacementModifier count, int minY, int maxY) {
		List<PlacementModifier> modifiers = new ArrayList<>();
		if(option != null)
			modifiers.add(ConfigFilter.of(option));
		modifiers.addAll(List.of(count, InSquarePlacement.spread(), range(minY, maxY), BiomeFilter.biome()));
		context.register(placed(name), new PlacedFeature(features.getOrThrow(configured(name)), modifiers));
	}

	private static PlacementModifier range(int minY, int maxY) {
		return HeightRangePlacement.uniform(VerticalAnchor.absolute(minY), VerticalAnchor.absolute(maxY));
	}

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, String name, F feature, FC config) {
		context.register(configured(name), new ConfiguredFeature<>(feature, config));
	}

	private static void place(BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> features, String name, String feature, PlacementModifier... modifiers) {
		context.register(placed(name), new PlacedFeature(features.getOrThrow(configured(feature)), List.of(modifiers)));
	}

	private static BlockState state(Supplier<? extends Block> block) {
		return block.get().defaultBlockState();
	}

	public static ResourceKey<ConfiguredFeature<?, ?>> configured(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, AbyssalCraft.loc(name));
	}

	public static ResourceKey<PlacedFeature> placed(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, AbyssalCraft.loc(name));
	}

	private static ResourceKey<ConfiguredWorldCarver<?>> carver(String name) {
		return ResourceKey.create(Registries.CONFIGURED_CARVER, AbyssalCraft.loc(name));
	}
}
