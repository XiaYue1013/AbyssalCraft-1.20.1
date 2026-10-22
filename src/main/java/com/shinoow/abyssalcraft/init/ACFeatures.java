/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - world generation types (1.12.2 called its WorldGenerator classes from the chunk providers/biomes;
 *     1.20.1 places them through data-driven configured/placed features)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.world.gen.ACCanyonCarver;
import com.shinoow.abyssalcraft.common.world.gen.ACCaveCarver;
import com.shinoow.abyssalcraft.common.world.gen.ConfigFilter;
import com.shinoow.abyssalcraft.common.world.gen.CoraliumOrePlacement;
import com.shinoow.abyssalcraft.common.world.gen.OreDimensionFilter;
import com.shinoow.abyssalcraft.common.world.gen.feature.CoraliumOreFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.ACLakeFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.ACTreeFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.DimensionFloorFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.DreadlandsStalagmiteFeature;
import com.shinoow.abyssalcraft.common.world.gen.feature.LegacyStructureFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACFeatures {

	public static final DeferredRegister<net.minecraft.world.level.levelgen.feature.Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, AbyssalCraft.modid);
	public static final DeferredRegister<net.minecraft.world.level.levelgen.carver.WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, AbyssalCraft.modid);
	public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, AbyssalCraft.modid);

	/** The bedrock/darkstone floor with the hidden pillar room below the Abyssal Wasteland and the Dreadlands */
	public static final RegistryObject<DimensionFloorFeature> DIMENSION_FLOOR = FEATURES.register("dimension_floor", () -> new DimensionFloorFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<ACTreeFeature> DARKLANDS_TREE = FEATURES.register("darklands_tree", () -> new ACTreeFeature(ACTreeFeature.Type.DARKLANDS));
	public static final RegistryObject<ACTreeFeature> DREADLANDS_TREE = FEATURES.register("dreadlands_tree", () -> new ACTreeFeature(ACTreeFeature.Type.DREADLANDS));
	public static final RegistryObject<DreadlandsStalagmiteFeature> DREADLANDS_STALAGMITE = FEATURES.register("dreadlands_stalagmite", () -> new DreadlandsStalagmiteFeature(NoneFeatureConfiguration.CODEC));
	/** The block-by-block 1.12.2 structures (Abyssal Wasteland chains and ruins, Darklands shrines, houses and scions) */
	public static final RegistryObject<LegacyStructureFeature> CODED_STRUCTURE = FEATURES.register("coded_structure", LegacyStructureFeature::new);
	/** Coralium Ore with the vein size of the config */
	public static final RegistryObject<CoraliumOreFeature> CORALIUM_ORE = FEATURES.register("coralium_ore", () -> new CoraliumOreFeature(OreConfiguration.CODEC));
	public static final RegistryObject<ACLakeFeature> LAKE = FEATURES.register("lake", () -> new ACLakeFeature(ACLakeFeature.Configuration.CODEC));

	/** Caves and canyons that stay dry below sea level, like the 1.12.2 ones (see DryCarvingAquifer) */
	public static final RegistryObject<ACCaveCarver> CAVE = CARVERS.register("cave", () -> new ACCaveCarver(CaveCarverConfiguration.CODEC));
	public static final RegistryObject<ACCanyonCarver> CANYON = CARVERS.register("canyon", () -> new ACCanyonCarver(CanyonCarverConfiguration.CODEC));

	/** Lets data-driven features follow the world generation toggles of the config */
	public static final RegistryObject<PlacementModifierType<CoraliumOrePlacement>> CORALIUM_ORE_PLACEMENT = PLACEMENT_MODIFIERS.register("coralium_ore", () -> () -> CoraliumOrePlacement.CODEC);
	public static final RegistryObject<PlacementModifierType<OreDimensionFilter>> ORE_DIMENSION_FILTER = PLACEMENT_MODIFIERS.register("ore_dimension", () -> () -> OreDimensionFilter.CODEC);
	public static final RegistryObject<PlacementModifierType<ConfigFilter>> CONFIG = PLACEMENT_MODIFIERS.register("config", () -> () -> ConfigFilter.CODEC);
}
