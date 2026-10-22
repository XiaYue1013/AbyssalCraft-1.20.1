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
 *     1.20.1 port - tags replace OreDictionary entries and harvest levels
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

/**
 * AbyssalCraft tags
 */
public class ACTags {

	public static class Blocks {
		/** Harvest levels above diamond (1.12.2 harvest levels 4, 5, 6 and 8) */
		public static final TagKey<Block> NEEDS_ABYSSALNITE_TOOL = tag("needs_abyssalnite_tool");
		public static final TagKey<Block> NEEDS_REFINED_CORALIUM_TOOL = tag("needs_refined_coralium_tool");
		public static final TagKey<Block> NEEDS_DREADIUM_TOOL = tag("needs_dreadium_tool");
		public static final TagKey<Block> NEEDS_ETHAXIUM_TOOL = tag("needs_ethaxium_tool");

		/** Blocks Shoggoth acid can't dissolve regardless of hardness */
		public static final TagKey<Block> ACID_RESISTANT = tag("acid_resistant");
		/** Blocks Lesser Shoggoths won't cover in ooze */
		public static final TagKey<Block> OOZE_BLACKLIST = tag("ooze_blacklist");
		/** Blocks the AbyssalCraft dimensions' caves and canyons can carve through */
		public static final TagKey<Block> CARVER_REPLACEABLES = tag("carver_replaceables");

		private static TagKey<Block> tag(String name) {
			return TagKey.create(Registries.BLOCK, new ResourceLocation("abyssalcraft", name));
		}
	}

	public static class Items {
		public static final TagKey<Item> CRYSTALS = tag("crystals");
		public static final TagKey<Item> COINS = tag("coins");
		public static final TagKey<Item> ABYSSALNITE_INGOTS = forge("ingots/abyssalnite");
		public static final TagKey<Item> REFINED_CORALIUM_INGOTS = forge("ingots/refined_coralium");
		public static final TagKey<Item> DREADIUM_INGOTS = forge("ingots/dreadium");
		public static final TagKey<Item> ETHAXIUM_INGOTS = forge("ingots/ethaxium");
		public static final TagKey<Item> TIN_INGOTS = forge("ingots/tin");
		public static final TagKey<Item> COPPER_INGOTS = forge("ingots/copper");
		public static final TagKey<Item> NITRE = forge("dusts/saltpeter");
		public static final TagKey<Item> SULFUR = forge("dusts/sulfur");
		public static final TagKey<Item> NECRONOMICONS = tag("necronomicons");

		private static TagKey<Item> tag(String name) {
			return TagKey.create(Registries.ITEM, new ResourceLocation("abyssalcraft", name));
		}

		private static TagKey<Item> forge(String name) {
			return TagKey.create(Registries.ITEM, new ResourceLocation("forge", name));
		}
	}

	public static class Biomes {
		public static final TagKey<Biome> HAS_SHOGGOTH_LAIR = tag("has_structure/shoggoth_lair");
		public static final TagKey<Biome> HAS_SHOGGOTH_LAIR_OVERWORLD = tag("has_structure/shoggoth_lair_overworld");
		public static final TagKey<Biome> HAS_OMOTHOL_STRUCTURES = tag("has_structure/omothol");
		public static final TagKey<Biome> HAS_ABYSSAL_STRONGHOLD = tag("has_structure/abyssal_stronghold");
		public static final TagKey<Biome> HAS_DREADLANDS_MINE = tag("has_structure/dreadlands_mine");
		/** 1.12.2 BiomeDictionary WATER, BEACH and SWAMP: Depths Ghouls and Abyssal Zombies spawn there (if enabled) */
		public static final TagKey<Biome> CORALIUM_MOB_SPAWNS = tag("coralium_mob_spawns");
		/** 1.12.2 EntityHandler: where the Evil Animals spawn (taiga, plains, forest, savanna...) */
		public static final TagKey<Biome> EVIL_ANIMAL_SPAWNS = tag("evil_animal_spawns");
		public static final TagKey<Biome> SHUB_OFFSPRING_SPAWNS = tag("shub_offspring_spawns");
		public static final TagKey<Biome> SHUB_OFFSPRING_DENSE_SPAWNS = tag("shub_offspring_dense_spawns");
		/** Oceans that get Coralium Ore at the normal height (deep oceans get it 20 blocks lower) */
		public static final TagKey<Biome> CORALIUM_ORE_OCEANS = tag("coralium_ore_oceans");

		private static TagKey<Biome> tag(String name) {
			return TagKey.create(Registries.BIOME, new ResourceLocation("abyssalcraft", name));
		}
	}

	public static class Entities {
		/** Dread entities (replaces IDreadEntity checks on entity types, eg. for unlock conditions) */
		public static final TagKey<EntityType<?>> DREAD_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("abyssalcraft", "dread_entities"));
		/** Mobs Lesser Shoggoths may eat (in addition to the API shoggoth food list) */
		public static final TagKey<EntityType<?>> SHOGGOTH_FOOD = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("abyssalcraft", "shoggoth_food"));
	}

	public static class DamageTypes {
		/** Radiation-like damage that AbyssalCraft armor protects against (replaces the NuclearCraft/HBM checks) */
		public static final TagKey<DamageType> RADIATION = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("abyssalcraft", "radiation"));
	}
}
