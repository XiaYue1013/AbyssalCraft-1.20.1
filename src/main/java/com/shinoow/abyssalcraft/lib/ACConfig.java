/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2026 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 *     1.20.1 port - ForgeConfigSpec rewrite
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * AbyssalCraft configuration.<br>
 * The values are "baked" into the static fields below whenever a config is (re)loaded, so game code keeps the
 * upstream {@code ACConfig.field} access pattern.<br>
 * Removed compared to 1.12.2 (documented in API_MIGRATION.md): numeric dimension IDs, "Prevent unloading" toggles,
 * per-block disable toggles (registries are frozen before configs load in 1.20.1) and the Hardcore Darkness compat.
 */
public class ACConfig {

	// General / gameplay
	public static boolean shouldSpread, shouldInfect, breakLogic, destroyOcean, demonAnimalFire, hardcoreMode,
	antiItemDisintegration, smeltingRecipes, purgeMobSpawns, mimicFire, armorPotionEffects, nuclearAntimatterExplosions,
	syncDataOnBookOpening, dreadGrassSpread, portalSpawnsNearPlayer, showBossDialogs, jzaharBreaksFourthWall,
	lootTableContent, depthsGhoulBiomeDictSpawn, abyssalZombieBiomeDictSpawn, useAmplifiedWorldType, generateStatuesInLairs,
	enchantBooks, nightVisionEverywhere, antiPlayersPickupLoot, demonAnimalsSpawnOnDeath, evilAnimalNewMoonSpawning,
	enchantMergedBooks, no_potion_clouds, displayItemNames;
	public static int evilAnimalSpawnWeight, portalCooldown, demonAnimalSpawnWeight, shoggothLairSpawnRate, acidSpitFrequency,
	knowledgeSyncDelay, shoggothLairSpawnRateRivers, darkOffspringSpawnWeight, monolithBuildingCooldown, corruptionRitualRange,
	cleansingRitualRange, purgingRitualRange, odbExplosionSize, antimatterExplosionSize, enchantmentMaxLevel, curingRitualRange,
	shoggothLairGenerationDistance, jzaharHealingPace, jzaharHealingAmount, chagarothHealingPace, chagarothHealingAmount,
	sacthothHealingPace, sacthothHealingAmount, biomassPlayerDistance, biomassMaxSpawn, biomassCooldown, biomassShoggothDistance;
	public static double damageAmpl, acidResistanceHardness;
	public static List<String> interdimensionalCageBlacklist = new ArrayList<>(), blackHoleBlacklist = new ArrayList<>(),
			dreadPlagueImmunityList = new ArrayList<>(), dreadPlagueCarrierList = new ArrayList<>(),
			coraliumPlagueImmunityList = new ArrayList<>(), coraliumPlagueCarrierList = new ArrayList<>(),
			itemTransportBlacklist = new ArrayList<>(), mobItemPickupBlacklist = new ArrayList<>(),
			transformationList = new ArrayList<>(), dimensionMappings = new ArrayList<>(),
			oreGenDimBlacklist = new ArrayList<>(), structureGenDimBlacklist = new ArrayList<>();
	public static int[] coraliumOreGeneration = {12, 8, 40};

	// Biomes (overworld, through TerraBlender)
	public static boolean darklands, darklandsForest, darklandsPlains, darklandsHighland, darklandsMountain, coraliumSwamp;
	public static int darklandsWeight, darklandsForestWeight, darklandsPlainsWeight, darklandsHighlandWeight,
	darklandsMountainWeight, coraliumSwampWeight;

	// Shoggoth
	public static boolean shoggothOoze, oozeExpire, consumeItems, shieldsBlockAcid;

	// Worldgen
	public static boolean generateDarklandsStructures, generateShoggothLairs, generateAbyssalWastelandPillars,
	generateAbyssalWastelandRuins, generateAntimatterLake, generateCoraliumLake, generateDreadlandsStalagmite,
	generateOmotholStructures;
	public static boolean generateCoraliumOre, generateNitreOre, generateAbyssalniteOre, generateAbyssalCoraliumOre,
	generateDreadlandsAbyssalniteOre, generateDreadedAbyssalniteOre, generateAbyssalIronOre, generateAbyssalGoldOre,
	generateAbyssalDiamondOre, generateAbyssalNitreOre, generateAbyssalTinOre, generateAbyssalCopperOre,
	generatePearlescentCoraliumOre, generateLiquifiedCoraliumOre;

	// Wet noodle (destructive / difficulty toggles)
	public static boolean no_dreadlands_spread, no_acid_breaking_blocks, no_spectral_dragons, no_projectile_damage_immunity,
	no_disruptions, no_black_holes, no_odb_explosions;

	// Modules
	public static boolean foodstuff, upgrade_kits, plague_enchantments, crystal_rework;

	// Spells
	public static boolean entropy_spell, life_drain_spell, mining_spell, grasp_of_cthulhu_spell, invisibility_spell,
	detachment_spell, steal_vigor_spell, sirens_song_spell, undeath_to_dust_spell, ooze_removal_spell, teleport_hostile_spell,
	floating_spell, teleport_home_spell;

	// Client
	public static boolean particleBlock, particleEntity, shoggothGlowingEyes;
	public static double depthsHelmetOverlayOpacity;

	public static final ForgeConfigSpec COMMON_SPEC, CLIENT_SPEC;

	private static final List<Runnable> COMMON_BAKERS = new ArrayList<>();
	private static final List<Runnable> CLIENT_BAKERS = new ArrayList<>();

	static {
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		buildCommon(b);
		COMMON_SPEC = b.build();
		ForgeConfigSpec.Builder c = new ForgeConfigSpec.Builder();
		buildClient(c);
		CLIENT_SPEC = c.build();
	}

	public static void onConfigLoad(ModConfigEvent event) {
		if(event.getConfig().getSpec() == COMMON_SPEC) {
			COMMON_BAKERS.forEach(Runnable::run);
			applyDimensionMappings();
		}
		else if(event.getConfig().getSpec() == CLIENT_SPEC)
			CLIENT_BAKERS.forEach(Runnable::run);
	}

	/**
	 * Config defined dimension to Necronomicon book type (and name) mappings, format "dimension;bookType[;name]"
	 */
	private static void applyDimensionMappings() {
		com.shinoow.abyssalcraft.api.ritual.RitualRegistry registry = com.shinoow.abyssalcraft.api.ritual.RitualRegistry.instance();
		registry.wipeConfig();
		for(String str : dimensionMappings)
			if(!str.isEmpty()) {
				String[] stuff = str.split(";");
				net.minecraft.resources.ResourceLocation id = stuff.length >= 2 ? net.minecraft.resources.ResourceLocation.tryParse(stuff[0]) : null;
				Integer bookType = null;
				try {
					bookType = stuff.length >= 2 ? Integer.parseInt(stuff[1]) : null;
				} catch(NumberFormatException e) {}
				if(id == null || bookType == null) {
					com.shinoow.abyssalcraft.AbyssalCraft.LOGGER.error("Invalid Dimension Book Type Mapping: {}", str);
					continue;
				}
				net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim =
						net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);
				if(stuff.length > 2)
					registry.addDimensionToBookTypeConfig(dim, bookType, stuff[2]);
				else registry.addDimensionToBookTypeConfig(dim, bookType);
			}
	}

	private interface Setter<T> { void set(T value); }

	private static void bool(ForgeConfigSpec.Builder b, List<Runnable> bakers, String name, boolean def, String comment, Setter<Boolean> setter) {
		BooleanValue v = b.comment(comment).define(name, def);
		setter.set(def);
		bakers.add(() -> setter.set(v.get()));
	}

	private static void integer(ForgeConfigSpec.Builder b, List<Runnable> bakers, String name, int def, int min, int max, String comment, Setter<Integer> setter) {
		IntValue v = b.comment(comment).defineInRange(name, def, min, max);
		setter.set(def);
		bakers.add(() -> setter.set(v.get()));
	}

	private static void dbl(ForgeConfigSpec.Builder b, List<Runnable> bakers, String name, double def, double min, double max, String comment, Setter<Double> setter) {
		DoubleValue v = b.comment(comment).defineInRange(name, def, min, max);
		setter.set(def);
		bakers.add(() -> setter.set(v.get()));
	}

	private static void list(ForgeConfigSpec.Builder b, List<Runnable> bakers, String name, List<String> def, String comment, Supplier<List<String>> target) {
		ConfigValue<List<? extends String>> v = b.comment(comment).defineListAllowEmpty(List.of(name), () -> def, o -> o instanceof String);
		bakers.add(() -> {
			List<String> t = target.get();
			t.clear();
			t.addAll(v.get());
		});
	}

	private static void buildCommon(ForgeConfigSpec.Builder b) {
		List<Runnable> k = COMMON_BAKERS;

		b.comment("General configuration (misc things).").push("general");
		bool(b, k, "liquidCoraliumTransmutation", true, "Set true for the Liquid Coralium to convert other liquids into itself and transmute blocks into their Abyssal Wasteland counterparts outside of the Abyssal Wasteland.", v -> shouldSpread = v);
		bool(b, k, "coraliumPlagueSpreading", false, "Set true to allow the Coralium Plague to spread outside The Abyssal Wasteland.", v -> shouldInfect = v);
		bool(b, k, "oceanicCoraliumPollution", false, "Set true to allow the Liquid Coralium to spread across oceans. WARNING: can be very heavy on the server.", v -> destroyOcean = v);
		bool(b, k, "demonAnimalBurning", false, "Set to true to make Demon Animals (Pigs, Cows, Chickens, Sheep) burn in the Overworld.", v -> demonAnimalFire = v);
		integer(b, k, "evilAnimalSpawnWeight", 15, 0, 100, "Spawn weight for the Evil Animals, keep under 35 to avoid complete annihilation.", v -> evilAnimalSpawnWeight = v);
		bool(b, k, "hardcoreMode", false, "Toggles Hardcore mode. If set to true, all mobs (in the mod) will become tougher.", v -> hardcoreMode = v);
		bool(b, k, "liquidAntimatterItemDisintegration", true, "Toggles whether or not Liquid Antimatter will disintegrate any items dropped into a pool of it.", v -> antiItemDisintegration = v);
		integer(b, k, "portalCooldown", 100, 10, 300, "Cooldown after using a portal, in ticks (20 ticks = 1 second).", v -> portalCooldown = v);
		integer(b, k, "demonAnimalSpawnWeight", 15, 0, 100, "Spawn weight for the Demon Animals spawning in the Nether.", v -> demonAnimalSpawnWeight = v);
		bool(b, k, "smeltingRecipes", true, "Toggles whether or not armor pieces can be smelted back into ingots.", v -> smeltingRecipes = v);
		bool(b, k, "purgeMobSpawns", false, "Toggles whether or not to clear and repopulate the monster spawn list of all dimension biomes to ensure no mob from another mod got in there.", v -> purgeMobSpawns = v);
		list(b, k, "interdimensionalCageBlacklist", List.of(), "Entity ids added to this list can't be captured with the Interdimensional Cage.", () -> interdimensionalCageBlacklist);
		dbl(b, k, "hardcoreModeDamageAmplifier", 1.0D, 1.0D, 10.0D, "When Hardcore Mode is enabled, amplifies the armor-piercing damage mobs deal.", v -> damageAmpl = v);
		bool(b, k, "mimicFire", true, "Toggles whether or not Demon Animals will spread Mimic Fire instead of regular Fire.", v -> mimicFire = v);
		bool(b, k, "armorPotionEffects", true, "Toggles any interactions where armor sets either give certain effects, or dispel others.", v -> armorPotionEffects = v);
		bool(b, k, "necronomiconDataSyncing", true, "Toggles whether or not the Necronomicon knowledge syncs from the server to the client each time a player opens their Necronomicon.", v -> syncDataOnBookOpening = v);
		bool(b, k, "dreadlandsGrassSpread", true, "Toggles whether or not Dreadlands Grass can spread onto normal grass and dirt.", v -> dreadGrassSpread = v);
		bool(b, k, "displayItemNames", false, "Toggles whether or not to override the name locking and display item names regardless of knowledge.", v -> displayItemNames = v);
		list(b, k, "realityMaelstromBlacklist", List.of(), "Dimension ids (e.g. minecraft:the_end) that won't be used as destinations for black holes created by J'zahar.", () -> blackHoleBlacklist);
		bool(b, k, "portalMobSpawningNearPlayers", true, "Toggles whether or not portals require a player to be nearby in order to rarely spawn mobs.", v -> portalSpawnsNearPlayer = v);
		bool(b, k, "showBossDialogs", true, "Toggles whether or not boss dialogs are displayed during their fights.", v -> showBossDialogs = v);
		integer(b, k, "knowledgeSyncDelay", 60, 20, 400, "Delay in ticks until Knowledge is synced to the client upon changing dimensions.", v -> knowledgeSyncDelay = v);
		list(b, k, "dreadPlagueImmunityList", List.of(), "Entity ids considered immune to the Dread Plague.", () -> dreadPlagueImmunityList);
		list(b, k, "dreadPlagueCarrierList", List.of(), "Entity ids considered carriers of the Dread Plague (also makes them immune).", () -> dreadPlagueCarrierList);
		list(b, k, "coraliumPlagueImmunityList", List.of(), "Entity ids considered immune to the Coralium Plague.", () -> coraliumPlagueImmunityList);
		list(b, k, "coraliumPlagueCarrierList", List.of(), "Entity ids considered carriers of the Coralium Plague (also makes them immune).", () -> coraliumPlagueCarrierList);
		bool(b, k, "lootTableContent", true, "Toggles whether or not AbyssalCraft items are inserted into vanilla loot tables (dungeons, strongholds etc).", v -> lootTableContent = v);
		bool(b, k, "depthsGhoulBiomeTagSpawning", true, "Toggles whether Depths Ghouls spawn in any biome tagged ocean/beach/swamp/river (may include other mods' dimensions).", v -> depthsGhoulBiomeDictSpawn = v);
		bool(b, k, "abyssalZombieBiomeTagSpawning", true, "Toggles whether Abyssal Zombies spawn in any biome tagged ocean/beach/swamp/river.", v -> abyssalZombieBiomeDictSpawn = v);
		integer(b, k, "darkOffspringSpawnWeight", 5, 0, 50, "Spawn weight for Dark Offspring. They spawn twice as likely in Darklands Forests and Dark Forests.", v -> darkOffspringSpawnWeight = v);
		integer(b, k, "corruptionRitualRange", 32, 3, 100, "The range (in chunks) affected by the Ritual of Corruption.", v -> corruptionRitualRange = v);
		integer(b, k, "cleansingRitualRange", 32, 3, 100, "The range (in chunks) affected by the Ritual of Cleansing.", v -> cleansingRitualRange = v);
		integer(b, k, "purgingRitualRange", 32, 3, 100, "The range (in chunks) affected by the Ritual of Purging.", v -> purgingRitualRange = v);
		integer(b, k, "curingRitualRange", 32, 3, 100, "The range (in chunks) affected by the Ritual of Curing.", v -> curingRitualRange = v);
		integer(b, k, "massEnchantmentMaxLevel", 10, 1, 100, "The combined max level a single enchantment applied through the Mass Enchantment ritual can have.", v -> enchantmentMaxLevel = v);
		bool(b, k, "massEnchantmentBooks", true, "Toggles whether or not Books can be enchanted through the Mass Enchantment ritual.", v -> enchantBooks = v);
		bool(b, k, "massEnchantmentMergedBooks", true, "Toggles whether or not anvil-merged Enchanted Books can be used in the Mass Enchantment ritual.", v -> enchantMergedBooks = v);
		bool(b, k, "platedCoraliumHelmetNightVisionEverywhere", true, "Toggles whether the Plated Coralium Helmet night vision applies in all dimensions.", v -> nightVisionEverywhere = v);
		bool(b, k, "demonAnimalsSpawnOnDeath", true, "Toggles whether or not an Evil Animal spawns a Demon Animal on death.", v -> demonAnimalsSpawnOnDeath = v);
		bool(b, k, "evilAnimalNewMoonSpawning", true, "Toggles whether or not Evil Animals only spawn at night during a new moon.", v -> evilAnimalNewMoonSpawning = v);
		list(b, k, "itemTransportBlacklist", List.of(), "Block entity type ids (modid:name) that can't be used with the Item Transportation System.", () -> itemTransportBlacklist);
		bool(b, k, "noPlaguePotionClouds", false, "Toggles whether or not victims dying to the plagues create effect clouds (disable to save performance).", v -> no_potion_clouds = v);
		integer(b, k, "jzaharHealingPace", 200, 20, 1200, "The pace (in ticks) at which J'zahar regenerates health.", v -> jzaharHealingPace = v);
		integer(b, k, "jzaharHealAmount", 1, 0, 100, "HP J'zahar heals when he regenerates (0 disables).", v -> jzaharHealingAmount = v);
		integer(b, k, "chagarothHealingPace", 200, 20, 1200, "The pace (in ticks) at which Cha'garoth regenerates health.", v -> chagarothHealingPace = v);
		integer(b, k, "chagarothHealAmount", 1, 0, 100, "HP Cha'garoth heals when he regenerates (0 disables).", v -> chagarothHealingAmount = v);
		integer(b, k, "sacthothHealingPace", 200, 20, 1200, "The pace (in ticks) at which Sacthoth regenerates health.", v -> sacthothHealingPace = v);
		integer(b, k, "sacthothHealAmount", 1, 0, 100, "HP Sacthoth heals when he regenerates (0 disables).", v -> sacthothHealingAmount = v);
		list(b, k, "dimensionBookTypeMappings", List.of(), "Enables rituals in other dimensions. Format: dimensionId;bookType;name"
				+ "\n bookType: 0 = normal, 1 = Abyssal Wasteland, 2 = Dreadlands, 3 = Omothol, 4 = Abyssalnomicon (also selects the altar material); name is optional.", () -> dimensionMappings);
		list(b, k, "demonAnimalTransformations", List.of(), "Mobs that may spawn a Demon Animal on death. Format: entityid;demonanimal;chance"
				+ "\n demonanimal: 0 = Demon Pig, 1 = Demon Cow, 2 = Demon Chicken, 3 = Demon Sheep; chance is optional (0.0 - 1.0, default 1.0).", () -> transformationList);
		b.pop();

		b.comment("Overworld biome configuration (requires TerraBlender). Changes take effect after a restart.").push("biomes");
		bool(b, k, "darklands", true, "Set true for the Darklands biome to generate.", v -> darklands = v);
		bool(b, k, "darklandsForest", true, "Set true for the Darklands Forest biome to generate.", v -> darklandsForest = v);
		bool(b, k, "darklandsPlains", true, "Set true for the Darklands Plains biome to generate.", v -> darklandsPlains = v);
		bool(b, k, "darklandsHighland", true, "Set true for the Darklands Highland biome to generate.", v -> darklandsHighland = v);
		bool(b, k, "darklandsMountain", true, "Set true for the Darklands Mountain biome to generate.", v -> darklandsMountain = v);
		bool(b, k, "coraliumInfestedSwamp", true, "Set true for the Coralium Infested Swamp to generate.", v -> coraliumSwamp = v);
		integer(b, k, "darklandsWeight", 4, 0, 100, "Relative weight of the Darklands biome.", v -> darklandsWeight = v);
		integer(b, k, "darklandsForestWeight", 4, 0, 100, "Relative weight of the Darklands Forest biome.", v -> darklandsForestWeight = v);
		integer(b, k, "darklandsPlainsWeight", 4, 0, 100, "Relative weight of the Darklands Plains biome.", v -> darklandsPlainsWeight = v);
		integer(b, k, "darklandsHighlandWeight", 4, 0, 100, "Relative weight of the Darklands Highland biome.", v -> darklandsHighlandWeight = v);
		integer(b, k, "darklandsMountainWeight", 4, 0, 100, "Relative weight of the Darklands Mountain biome.", v -> darklandsMountainWeight = v);
		integer(b, k, "coraliumInfestedSwampWeight", 6, 0, 100, "Relative weight of the Coralium Infested Swamp.", v -> coraliumSwampWeight = v);
		b.pop();

		b.comment("Shoggoth configuration.").push("shoggoth");
		bool(b, k, "shoggothOozeSpread", true, "Toggles whether or not Lesser Shoggoths spread their ooze when walking around.", v -> shoggothOoze = v);
		bool(b, k, "oozeExpiration", true, "Toggles whether or not Shoggoth Ooze slowly reverts to dirt after constant light exposure.", v -> oozeExpire = v);
		bool(b, k, "itemConsumption", false, "Toggles whether or not Lesser Shoggoths consume any dropped item they run into.", v -> consumeItems = v);
		bool(b, k, "shieldsBlockAcid", true, "Toggles whether or not shields can block Shoggoth acid.", v -> shieldsBlockAcid = v);
		dbl(b, k, "acidResistanceHardness", 3.0D, 2.1D, 51.0D, "The minimum block hardness required for a block to not be destroyed by Shoggoth acid.", v -> acidResistanceHardness = v);
		integer(b, k, "acidSpitFrequency", 120, 0, 300, "Ticks between Lesser Shoggoth acid spit attacks (0 disables).", v -> acidSpitFrequency = v);
		integer(b, k, "monolithBuildingCooldown", 1500, 0, 2400, "Ticks between Lesser Shoggoth monolith building attempts (0 disables).", v -> monolithBuildingCooldown = v);
		integer(b, k, "biomassPlayerDistance", 16, 5, 48, "Max distance a player can be from a Biomass for it to spawn Shoggoths.", v -> biomassPlayerDistance = v);
		integer(b, k, "biomassSpawnLimit", 6, 1, 10, "Nearby Shoggoth count at which a Biomass stops spawning.", v -> biomassMaxSpawn = v);
		integer(b, k, "biomassCooldown", 400, 40, 1200, "Ticks between Biomass spawn attempts.", v -> biomassCooldown = v);
		integer(b, k, "biomassShoggothDistance", 32, 5, 48, "Distance to check for nearby Shoggoths before spawning more.", v -> biomassShoggothDistance = v);
		b.pop();

		b.comment("World generation configuration.").push("worldgen");
		bool(b, k, "darklandsStructures", true, "Generate random Darklands structures.", v -> generateDarklandsStructures = v);
		bool(b, k, "shoggothLairs", true, "Generate Shoggoth Lairs (they still generate in Omothol).", v -> generateShoggothLairs = v);
		bool(b, k, "abyssalWastelandPillars", true, "Generate tall obsidian pillars in the Abyssal Wasteland.", v -> generateAbyssalWastelandPillars = v);
		bool(b, k, "abyssalWastelandRuins", true, "Generate small ruins in the Abyssal Wasteland.", v -> generateAbyssalWastelandRuins = v);
		bool(b, k, "liquidAntimatterLakes", true, "Generate Liquid Antimatter lakes in Coralium Infested Swamps.", v -> generateAntimatterLake = v);
		bool(b, k, "liquidCoraliumLakes", true, "Generate Liquid Coralium lakes in the Abyssal Wasteland.", v -> generateCoraliumLake = v);
		bool(b, k, "dreadlandsStalagmites", true, "Generate stalagmites in Dreadlands biomes.", v -> generateDreadlandsStalagmite = v);
		bool(b, k, "omotholCity", true, "Generate the Temple City of Omothol (except the Temple of J'zahar).", v -> generateOmotholStructures = v);
		bool(b, k, "coraliumOre", true, "Generate Coralium Ore in the Overworld.", v -> generateCoraliumOre = v);
		bool(b, k, "nitreOre", true, "Generate Nitre Ore in the Overworld.", v -> generateNitreOre = v);
		bool(b, k, "abyssalniteOre", true, "Generate Abyssalnite Ore in Darklands biomes.", v -> generateAbyssalniteOre = v);
		bool(b, k, "abyssalCoraliumOre", true, "Generate Coralium Ore in the Abyssal Wasteland.", v -> generateAbyssalCoraliumOre = v);
		bool(b, k, "dreadlandsAbyssalniteOre", true, "Generate Abyssalnite Ore in the Dreadlands.", v -> generateDreadlandsAbyssalniteOre = v);
		bool(b, k, "dreadedAbyssalniteOre", true, "Generate Dreaded Abyssalnite Ore in the Dreadlands.", v -> generateDreadedAbyssalniteOre = v);
		bool(b, k, "abyssalIronOre", true, "Generate Iron Ore in the Abyssal Wasteland.", v -> generateAbyssalIronOre = v);
		bool(b, k, "abyssalGoldOre", true, "Generate Gold Ore in the Abyssal Wasteland.", v -> generateAbyssalGoldOre = v);
		bool(b, k, "abyssalDiamondOre", true, "Generate Diamond Ore in the Abyssal Wasteland.", v -> generateAbyssalDiamondOre = v);
		bool(b, k, "abyssalNitreOre", true, "Generate Nitre Ore in the Abyssal Wasteland.", v -> generateAbyssalNitreOre = v);
		bool(b, k, "abyssalTinOre", true, "Generate Tin Ore in the Abyssal Wasteland.", v -> generateAbyssalTinOre = v);
		bool(b, k, "abyssalCopperOre", true, "Generate Copper Ore in the Abyssal Wasteland.", v -> generateAbyssalCopperOre = v);
		bool(b, k, "pearlescentCoraliumOre", true, "Generate Pearlescent Coralium Ore in the Abyssal Wasteland.", v -> generatePearlescentCoraliumOre = v);
		bool(b, k, "liquifiedCoraliumOre", true, "Generate Liquified Coralium Ore in the Abyssal Wasteland.", v -> generateLiquifiedCoraliumOre = v);
		integer(b, k, "shoggothLairChanceSwamps", 35, 0, 1000, "Generation chance (1 in n) of a Shoggoth Lair in swamp biomes.", v -> shoggothLairSpawnRate = v);
		integer(b, k, "shoggothLairChanceRivers", 30, 0, 1000, "Generation chance (1 in n) of a Shoggoth Lair in river biomes.", v -> shoggothLairSpawnRateRivers = v);
		integer(b, k, "coraliumOreVeinCount", 12, 0, 64, "Coralium Ore veins per chunk in oceans (half in swamps).", v -> coraliumOreGeneration[0] = v);
		integer(b, k, "coraliumOreVeinSize", 8, 1, 32, "Coralium Ore blocks per vein.", v -> coraliumOreGeneration[1] = v);
		integer(b, k, "coraliumOreMaxHeight", 40, -64, 320, "Max Y for Coralium Ore.", v -> coraliumOreGeneration[2] = v);
		bool(b, k, "useAmplifiedWorldType", true, "Kept for compatibility; dimension terrain is defined by noise settings in 1.20.1.", v -> useAmplifiedWorldType = v);
		bool(b, k, "generateStatuesInLairs", true, "Toggles whether or not statues may generate inside Shoggoth Lairs.", v -> generateStatuesInLairs = v);
		integer(b, k, "shoggothLairGenerationDistance", 100, 40, 1000, "The minimum distance between two Shoggoth Lairs.", v -> shoggothLairGenerationDistance = v);
		list(b, k, "oreGenDimensionBlacklist", List.of(), "Dimension ids that won't receive AbyssalCraft Overworld ores.", () -> oreGenDimBlacklist);
		list(b, k, "structureGenDimensionBlacklist", List.of(), "Dimension ids that won't receive AbyssalCraft Overworld structures.", () -> structureGenDimBlacklist);
		b.pop();

		b.comment("Entity item pickup blacklist.").push("item_blacklist");
		bool(b, k, "antiPlayersCanPickUpLoot", true, "Toggles whether or not Anti-Players can pick up loot.", v -> antiPlayersPickupLoot = v);
		list(b, k, "mobPickupItemBlacklist", List.of("minecraft:rotten_flesh", "minecraft:bone", "abyssalcraft:anti_flesh", "abyssalcraft:coralium_plagued_flesh", "abyssalcraft:anti_plagued_flesh"),
				"Item ids that AbyssalCraft mobs won't pick up.", () -> mobItemPickupBlacklist);
		b.pop();

		b.comment("Settings that are out of place but exist because 'what if X did this?'").push("silly_settings");
		bool(b, k, "liquidCoraliumPhysics", false, "Set true to allow Liquid Coralium to break the laws of physics in terms of movement.", v -> breakLogic = v);
		bool(b, k, "nuclearAntimatterExplosions", false, "Antimatter mobs colliding with their normal counterpart cause big explosions. Chaos.", v -> nuclearAntimatterExplosions = v);
		bool(b, k, "jzaharCanBreakTheFourthWall", true, "Toggles whether or not J'zahar can break the fourth wall.", v -> jzaharBreaksFourthWall = v);
		integer(b, k, "odbExplosionSize", 160, 80, 800, "The explosion size of an ODB.", v -> odbExplosionSize = v);
		integer(b, k, "antimatterExplosionSize", 80, 40, 200, "The explosion size of nuclear antimatter explosions.", v -> antimatterExplosionSize = v);
		b.pop();

		b.comment("Disable destructive features, or features that make the mod harder.").push("wet_noodle");
		bool(b, k, "disableDreadlandsSpread", false, "Disable the spreading of the Dreadlands through the Dread Plague.", v -> no_dreadlands_spread = v);
		bool(b, k, "disableAcidBreakingBlocks", false, "Disable Lesser Shoggoth acid breaking blocks.", v -> no_acid_breaking_blocks = v);
		bool(b, k, "disableSpectralDragons", false, "Disable Spectral Dragons spawning in the Abyssal Wasteland.", v -> no_spectral_dragons = v);
		bool(b, k, "disableProjectileDamageImmunity", false, "Disable Lesser Shoggoth projectile immunity.", v -> no_projectile_damage_immunity = v);
		bool(b, k, "disableDisruptions", false, "Disable disruptions from statues and failing rituals.", v -> no_disruptions = v);
		bool(b, k, "disableBlackHoles", false, "Disable J'zahar's black hole attack.", v -> no_black_holes = v);
		bool(b, k, "disableOdbExplosions", false, "Disable Oblivion Deathbomb (and ODB Core) explosions.", v -> no_odb_explosions = v);
		b.pop();

		b.comment("Content modules.").push("modules");
		bool(b, k, "foodstuffs", true, "Enable AbyssalCraft food recipes.", v -> foodstuff = v);
		bool(b, k, "upgradeKits", true, "Enable Upgrade Kits.", v -> upgrade_kits = v);
		bool(b, k, "plagueEnchantments", true, "Enable the Coralium and Dread enchantments.", v -> plague_enchantments = v);
		bool(b, k, "crystalRework", true, "Set to false to revert to the old crystal recipes (and them being fuels).", v -> crystal_rework = v);
		b.pop();

		b.comment("Spell configuration.").push("spells");
		bool(b, k, "entropy", true, "Enable the Entropy spell.", v -> entropy_spell = v);
		bool(b, k, "lifeDrain", true, "Enable the Life Drain spell.", v -> life_drain_spell = v);
		bool(b, k, "mining", true, "Enable the Mining spell.", v -> mining_spell = v);
		bool(b, k, "graspOfCthulhu", true, "Enable the Grasp of Cthulhu spell.", v -> grasp_of_cthulhu_spell = v);
		bool(b, k, "hideFromTheEye", true, "Enable the Hide from the Eye spell.", v -> invisibility_spell = v);
		bool(b, k, "detachment", true, "Enable the Detachment spell.", v -> detachment_spell = v);
		bool(b, k, "stealVigor", true, "Enable the Steal Vigor spell.", v -> steal_vigor_spell = v);
		bool(b, k, "sirensSong", true, "Enable the Siren's Song spell.", v -> sirens_song_spell = v);
		bool(b, k, "undeathToDust", true, "Enable the Undeath to Dust spell.", v -> undeath_to_dust_spell = v);
		bool(b, k, "oozeRemoval", true, "Enable the Ooze Removal spell.", v -> ooze_removal_spell = v);
		bool(b, k, "sacrificialInterdiction", true, "Enable the Sacrificial Interdiction spell.", v -> teleport_hostile_spell = v);
		bool(b, k, "floating", true, "Enable the Floating spell.", v -> floating_spell = v);
		bool(b, k, "teleportHome", true, "Enable the Teleport Home spell.", v -> teleport_home_spell = v);
		b.pop();
	}

	private static void buildClient(ForgeConfigSpec.Builder b) {
		List<Runnable> k = CLIENT_BAKERS;
		b.push("client");
		bool(b, k, "blockParticles", true, "Toggles whether blocks that emit particles should do so.", v -> particleBlock = v);
		bool(b, k, "entityParticles", true, "Toggles whether entities that emit particles should do so.", v -> particleEntity = v);
		bool(b, k, "shoggothGlowingEyes", true, "Toggles whether or not the eyes of Lesser Shoggoths glow.", v -> shoggothGlowingEyes = v);
		dbl(b, k, "depthsHelmetOverlayOpacity", 1.0D, 0.5D, 1.0D, "Opacity of the Visage of The Depths overlay.", v -> depthsHelmetOverlayOpacity = v);
		b.pop();
	}
}
