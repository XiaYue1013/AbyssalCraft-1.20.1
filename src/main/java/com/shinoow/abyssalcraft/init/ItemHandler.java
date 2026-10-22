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
 *     1.20.1 port - DeferredRegister + flattened metadata items
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.item.ItemEngraving;
import com.shinoow.abyssalcraft.api.item.ItemUpgradeKit;
import com.shinoow.abyssalcraft.api.necronomicon.condition.*;
import com.shinoow.abyssalcraft.common.blocks.itemblock.ItemBlockAC;
import com.shinoow.abyssalcraft.common.items.*;
import com.shinoow.abyssalcraft.common.items.armor.ACArmorMaterials;
import com.shinoow.abyssalcraft.common.items.armor.ItemACArmor;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all AbyssalCraft items and block items.<br>
 * Unlock conditions are the 1.12.2 ones (Necronomicon knowledge needed to read the item names).
 */
public class ItemHandler {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AbyssalCraft.modid);

	/** Item registry name -> upstream (1.12.2) item model name, used by tools/import_assets.py to import the item models */
	public static final Map<String, String> ITEM_MODELS = new LinkedHashMap<>();
	/** Items rendered as handheld tools */
	public static final Set<String> HANDHELD = new HashSet<>();

	private static final IUnlockCondition AW = dim(ACDimensions.ABYSSAL_WASTELAND), DL = dim(ACDimensions.DREADLANDS), OMT = dim(ACDimensions.OMOTHOL);
	private static final IUnlockCondition DARKLANDS = new BiomePredicateCondition(b -> b.is(ACBiomes.IS_DARKLANDS));
	private static final IUnlockCondition SHADOW_MOBS = new MultiEntityCondition("abyssalcraft:shadow_creature", "abyssalcraft:shadow_monster", "abyssalcraft:shadow_beast");
	private static final IUnlockCondition SWAMP = new BiomeCondition(ACBiomes.coralium_infested_swamp);

	static {
		ACTiers.init();
	}

	// Misc items
	public static final RegistryObject<Item> CORALIUM_BRICK = basic("coralium_brick", "cbrick", new MultiBiomeCondition(ACBiomes.abyssal_wastelands, ACBiomes.coralium_infested_swamp));
	public static final RegistryObject<Item> CARBON_CLUSTER = basic("carbon_cluster", "carboncluster", DL);
	public static final RegistryObject<Item> DENSE_CARBON_CLUSTER = basic("dense_carbon_cluster", "densecarboncluster", DL);
	public static final RegistryObject<Item> METHANE = item("methane", "methane", () -> new ItemACBasic().burnTime(10000), null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> NITRE = basic("nitre", "nitre", null);
	public static final RegistryObject<Item> SULFUR = basic("sulfur", "sulfur", null);
	public static final RegistryObject<Item> TIN_INGOT = basic("tin_ingot", "tiningot", null);
	public static final RegistryObject<Item> COPPER_INGOT = basic("copper_ingot", "copperingot", null);
	public static final RegistryObject<Item> LIFE_CRYSTAL = basic("life_crystal", "lifecrystal", DL);
	public static final RegistryObject<Item> ELDRITCH_SCALE = basic("eldritch_scale", "eldritchscale", OMT);
	public static final RegistryObject<Item> ESSENCE_OF_THE_GATEKEEPER = item("essence_of_the_gatekeeper", "gatekeeperessence", () -> new ItemACBasic().color(ChatFormatting.BLUE), OMT, ACTabs.Tab.ITEMS);

	// Shoggoth flesh
	public static final RegistryObject<Item> SHOGGOTH_FLESH_OVERWORLD = basic("shoggoth_flesh_overworld", "shoggothflesh_overworld", null);
	public static final RegistryObject<Item> SHOGGOTH_FLESH_ABYSSAL_WASTELAND = basic("shoggoth_flesh_abyssal_wasteland", "shoggothflesh_abyssalwasteland", null);
	public static final RegistryObject<Item> SHOGGOTH_FLESH_DREADLANDS = basic("shoggoth_flesh_dreadlands", "shoggothflesh_dreadlands", null);
	public static final RegistryObject<Item> SHOGGOTH_FLESH_OMOTHOL = basic("shoggoth_flesh_omothol", "shoggothflesh_omothol", null);
	public static final RegistryObject<Item> SHOGGOTH_FLESH_DARK_REALM = basic("shoggoth_flesh_dark_realm", "shoggothflesh_darkrealm", null);

	// Nuggets, essences, skins
	public static final RegistryObject<Item> ABYSSALNITE_NUGGET = basic("abyssalnite_nugget", "nugget_abyssalnite", DARKLANDS);
	public static final RegistryObject<Item> REFINED_CORALIUM_NUGGET = basic("refined_coralium_nugget", "nugget_coralium", AW);
	public static final RegistryObject<Item> DREADIUM_NUGGET = basic("dreadium_nugget", "nugget_dreadium", DL);
	public static final RegistryObject<Item> ETHAXIUM_NUGGET = basic("ethaxium_nugget", "nugget_ethaxium", OMT);
	public static final RegistryObject<Item> ABYSSAL_WASTELAND_ESSENCE = basic("abyssal_wasteland_essence", "essence_abyssalwasteland", AW);
	public static final RegistryObject<Item> DREADLANDS_ESSENCE = basic("dreadlands_essence", "essence_dreadlands", DL);
	public static final RegistryObject<Item> OMOTHOL_ESSENCE = basic("omothol_essence", "essence_omothol", OMT);
	public static final RegistryObject<Item> SKIN_OF_THE_ABYSSAL_WASTELAND = basic("skin_of_the_abyssal_wasteland", "skin_abyssalwasteland", AW);
	public static final RegistryObject<Item> SKIN_OF_THE_DREADLANDS = basic("skin_of_the_dreadlands", "skin_dreadlands", DL);
	public static final RegistryObject<Item> SKIN_OF_OMOTHOL = basic("skin_of_omothol", "skin_omothol", OMT);
	public static final RegistryObject<Item> CONFIGURATOR_SHARD_0 = basic("configurator_shard_0", "configurator_shard_0", OMT);
	public static final RegistryObject<Item> CONFIGURATOR_SHARD_1 = basic("configurator_shard_1", "configurator_shard_1", OMT);
	public static final RegistryObject<Item> CONFIGURATOR_SHARD_2 = basic("configurator_shard_2", "configurator_shard_2", OMT);
	public static final RegistryObject<Item> CONFIGURATOR_SHARD_3 = basic("configurator_shard_3", "configurator_shard_3", OMT);

	// Crystal bags
	public static final RegistryObject<Item> SMALL_CRYSTAL_BAG = item("small_crystal_bag", "crystalbag_small", () -> new ItemCrystalBag(18), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> MEDIUM_CRYSTAL_BAG = item("medium_crystal_bag", "crystalbag_medium", () -> new ItemCrystalBag(36), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> LARGE_CRYSTAL_BAG = item("large_crystal_bag", "crystalbag_large", () -> new ItemCrystalBag(54), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> HUGE_CRYSTAL_BAG = item("huge_crystal_bag", "crystalbag_huge", () -> new ItemCrystalBag(72), DL, ACTabs.Tab.ITEMS);

	// Buckets
	public static final RegistryObject<Item> LIQUID_CORALIUM_BUCKET = item("liquid_coralium_bucket", null,
			() -> new BucketItem(ACFluids.LIQUID_CORALIUM, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)), null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> LIQUID_ANTIMATTER_BUCKET = item("liquid_antimatter_bucket", null,
			() -> new BucketItem(ACFluids.LIQUID_ANTIMATTER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)), null, ACTabs.Tab.ITEMS);

	// Antidotes
	public static final RegistryObject<Item> CORALIUM_ANTIDOTE = item("coralium_antidote", "coralium_antidote", () -> new ItemAntidote(false), null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREAD_ANTIDOTE = item("dread_antidote", "dread_antidote", () -> new ItemAntidote(true), null, ACTabs.Tab.ITEMS);

	// Coins and engravings
	public static final RegistryObject<Item> COIN = coin("coin", "coin");
	public static final RegistryObject<Item> CTHULHU_ENGRAVED_COIN = coin("cthulhu_engraved_coin", "cthulhucoin");
	public static final RegistryObject<Item> ELDER_ENGRAVED_COIN = coin("elder_engraved_coin", "eldercoin");
	public static final RegistryObject<Item> JZAHAR_ENGRAVED_COIN = coin("jzahar_engraved_coin", "jzaharcoin");
	public static final RegistryObject<Item> HASTUR_ENGRAVED_COIN = coin("hastur_engraved_coin", "hasturcoin");
	public static final RegistryObject<Item> AZATHOTH_ENGRAVED_COIN = coin("azathoth_engraved_coin", "azathothcoin");
	public static final RegistryObject<Item> NYARLATHOTEP_ENGRAVED_COIN = coin("nyarlathotep_engraved_coin", "nyarlathotepcoin");
	public static final RegistryObject<Item> YOG_SOTHOTH_ENGRAVED_COIN = coin("yog_sothoth_engraved_coin", "yogsothothcoin");
	public static final RegistryObject<Item> SHUB_NIGGURATH_ENGRAVED_COIN = coin("shub_niggurath_engraved_coin", "shubniggurathcoin");
	public static final RegistryObject<Item> BLANK_ENGRAVING = engraving("blank_engraving", "engraving_blank", 50);
	public static final RegistryObject<Item> CTHULHU_ENGRAVING = engraving("cthulhu_engraving", "engraving_cthulhu", 10);
	public static final RegistryObject<Item> ELDER_ENGRAVING = engraving("elder_engraving", "engraving_elder", 10);
	public static final RegistryObject<Item> JZAHAR_ENGRAVING = engraving("jzahar_engraving", "engraving_jzahar", 10);
	public static final RegistryObject<Item> HASTUR_ENGRAVING = engraving("hastur_engraving", "engraving_hastur", 10);
	public static final RegistryObject<Item> AZATHOTH_ENGRAVING = engraving("azathoth_engraving", "engraving_azathoth", 10);
	public static final RegistryObject<Item> NYARLATHOTEP_ENGRAVING = engraving("nyarlathotep_engraving", "engraving_nyarlathotep", 10);
	public static final RegistryObject<Item> YOG_SOTHOTH_ENGRAVING = engraving("yog_sothoth_engraving", "engraving_yogsothoth", 10);
	public static final RegistryObject<Item> SHUB_NIGGURATH_ENGRAVING = engraving("shub_niggurath_engraving", "engraving_shubniggurath", 10);

	// Charms
	public static final List<RegistryObject<Item>> CHARMS = new ArrayList<>();
	static {
		charms("ritual_charm", "ritualcharm", null);
		charms("cthulhu_charm", "cthulhucharm", DeityType.CTHULHU);
		charms("hastur_charm", "hasturcharm", DeityType.HASTUR);
		charms("jzahar_charm", "jzaharcharm", DeityType.JZAHAR);
		charms("azathoth_charm", "azathothcharm", DeityType.AZATHOTH);
		charms("nyarlathotep_charm", "nyarlathotepcharm", DeityType.NYARLATHOTEP);
		charms("yog_sothoth_charm", "yogsothothcharm", DeityType.YOGSOTHOTH);
		charms("shub_niggurath_charm", "shubniggurathcharm", DeityType.SHUBNIGGURATH);
	}

	// Ethaxium
	public static final RegistryObject<Item> ETHAXIUM_BRICK = basic("ethaxium_brick", "ethbrick", OMT);
	public static final RegistryObject<Item> ETHAXIUM_INGOT = item("ethaxium_ingot", "ethaxiumingot", () -> new ItemACBasic().color(ChatFormatting.AQUA), OMT, ACTabs.Tab.ITEMS);

	// Anti items
	public static final RegistryObject<Item> ANTI_BEEF = food("anti_beef", "antibeef", () -> new ItemACFood(0, 0, true, (p, w) -> effect(p, MobEffects.HUNGER, 600, 1)), SWAMP);
	public static final RegistryObject<Item> ANTI_CHICKEN = food("anti_chicken", "antichicken", () -> new ItemACFood(0, 0, true, (p, w) -> effect(p, MobEffects.HUNGER, 600, 1)), SWAMP);
	public static final RegistryObject<Item> ANTI_PORK = food("anti_pork", "antipork", () -> new ItemACFood(0, 0, true, (p, w) -> effect(p, MobEffects.HUNGER, 600, 1)), SWAMP);
	public static final RegistryObject<Item> ROTTEN_ANTI_FLESH = food("rotten_anti_flesh", "antiflesh", () -> new ItemACFood(0, 0, true, (p, w) -> effect(p, MobEffects.SATURATION, 600, 1)), SWAMP);
	public static final RegistryObject<Item> ANTI_BONE = basic("anti_bone", "antibone", SWAMP);
	public static final RegistryObject<Item> ANTI_SPIDER_EYE = food("anti_spider_eye", "antispidereye", () -> new ItemACFood(0, 0, false, (p, w) -> effect(p, MobEffects.REGENERATION, 400, 0)), SWAMP);
	public static final RegistryObject<Item> ANTI_PLAGUED_FLESH = food("anti_plagued_flesh", "anticorflesh", () -> new ItemACFood(0, 0, false, (p, w) -> {
		effect(p, MobEffects.SATURATION, 600, 1);
		effect(p, MobEffects.REGENERATION, 600, 0);
	}), SWAMP);
	public static final RegistryObject<Item> ANTI_PLAGUED_FLESH_ON_A_BONE = food("anti_plagued_flesh_on_a_bone", "anticorbone", () -> new ItemACFood(0, 0, false, (p, w) -> {
		effect(p, MobEffects.SATURATION, 600, 1);
		effect(p, MobEffects.REGENERATION, 600, 0);
		give(p, new ItemStack(ACItems.anti_bone.get()));
	}), SWAMP);

	// Crystals (28 elements x crystal/shard/fragment)
	public static final List<RegistryObject<Item>> CRYSTALS = new ArrayList<>(), CRYSTAL_SHARDS = new ArrayList<>(), CRYSTAL_FRAGMENTS = new ArrayList<>();
	static {
		for(int i = 0; i < ACItems.CRYSTAL_NAMES.length; i++) {
			int index = i;
			String name = ACItems.CRYSTAL_NAMES[i];
			CRYSTALS.add(item(name + "_crystal", "crystal", () -> new ItemCrystal(index), DL, ACTabs.Tab.CRYSTALS));
			CRYSTAL_SHARDS.add(item(name + "_crystal_shard", "crystalshard", () -> new ItemCrystal(index), DL, ACTabs.Tab.CRYSTALS));
			CRYSTAL_FRAGMENTS.add(item(name + "_crystal_fragment", "crystalfragment", () -> new ItemCrystal(index), DL, ACTabs.Tab.CRYSTALS));
		}
	}

	// Shadow items
	public static final RegistryObject<Item> SHADOW_FRAGMENT = basic("shadow_fragment", "shadowfragment", SHADOW_MOBS);
	public static final RegistryObject<Item> SHADOW_SHARD = basic("shadow_shard", "shadowshard", SHADOW_MOBS);
	public static final RegistryObject<Item> SHADOW_GEM = basic("shadow_gem", "shadowgem", SHADOW_MOBS);
	public static final RegistryObject<Item> SHARD_OF_OBLIVION = basic("shard_of_oblivion", "oblivionshard", SHADOW_MOBS);

	// Dread items
	private static final IUnlockCondition DREAD_MOBS = new MultiEntityCondition("abyssalcraft:dreadguard", "abyssalcraft:greater_dread_spawn", "abyssalcraft:lesser_dreadbeast");
	public static final RegistryObject<Item> DREADED_SHARD_OF_ABYSSALNITE = item("dreaded_shard_of_abyssalnite", "dreadshard", () -> new ItemACBasic().color(ChatFormatting.DARK_RED), DREAD_MOBS, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREADED_CHUNK_OF_ABYSSALNITE = item("dreaded_chunk_of_abyssalnite", "dreadchunk", () -> new ItemACBasic().color(ChatFormatting.DARK_RED), new EntityCondition("abyssalcraft:dreaded_abyssalnite_golem"), ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREADIUM_INGOT = item("dreadium_ingot", "dreadiumingot", () -> new ItemACBasic().color(ChatFormatting.DARK_RED), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREAD_FRAGMENT = item("dread_fragment", "dreadfragment", () -> new ItemACBasic().color(ChatFormatting.DARK_RED),
			new EntityPredicateCondition(e -> e.is(ACTags.Entities.DREAD_ENTITIES)), ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREAD_CLOTH = basic("dread_cloth", "dreadcloth", DL);
	public static final RegistryObject<Item> DREADIUM_PLATE = basic("dreadium_plate", "dreadplate", DL);
	public static final RegistryObject<Item> DREADIUM_KATANA_BLADE = basic("dreadium_katana_blade", "dreadblade", DL);
	// 1.12.2 swords do 4 + material damage: 5 (hilt) and 15 (katana)
	public static final RegistryObject<Item> DREADIUM_KATANA_HILT = item("dreadium_katana_hilt", "dreadhilt",
			() -> new ItemACSword(ACTiers.DREADIUM_KATANA_HILT, 3, -2.4F, null), DL, ACTabs.Tab.COMBAT);
	public static final RegistryObject<Item> DREADIUM_KATANA = item("dreadium_katana", "dreadkatana",
			() -> new ItemACSword(ACTiers.DREADIUM_KATANA, 3, -2.4F, null), DL, ACTabs.Tab.COMBAT);
	public static final RegistryObject<Item> DREAD_PLAGUED_GATEWAY_KEY = basic("dread_plagued_gateway_key", "dreadkey", DL);
	public static final RegistryObject<Item> CHARCOAL = item("charcoal", "charcoal", () -> new ItemACBasic().burnTime(1600), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> POWERSTONE_TRACKER = item("powerstone_tracker", "powerstonetracker",
			com.shinoow.abyssalcraft.common.items.ItemTrackerPSDL::new, AW, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> OBLIVION_CATALYST = item("oblivion_catalyst", "oc",
			com.shinoow.abyssalcraft.common.items.ItemOC::new, null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> EYE_OF_THE_ABYSS = item("eye_of_the_abyss", "eoa",
			com.shinoow.abyssalcraft.common.items.ItemEoA::new, AW, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> STAFF_OF_THE_GATEKEEPER = item("staff_of_the_gatekeeper", "staff",
			com.shinoow.abyssalcraft.common.items.ItemStaff::new, OMT, ACTabs.Tab.TOOLS);
	// 1.12.2 drainstaff metadata 0 - 3
	public static final RegistryObject<Item> STAFF_OF_RENDING = item("staff_of_rending", "drainstaff",
			() -> new com.shinoow.abyssalcraft.common.items.ItemDrainStaff(0), SHADOW_MOBS, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> ABYSSAL_WASTELAND_STAFF_OF_RENDING = item("abyssal_wasteland_staff_of_rending", "drainstaff_aw",
			() -> new com.shinoow.abyssalcraft.common.items.ItemDrainStaff(1), SHADOW_MOBS, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> DREADLANDS_STAFF_OF_RENDING = item("dreadlands_staff_of_rending", "drainstaff_dl",
			() -> new com.shinoow.abyssalcraft.common.items.ItemDrainStaff(2), SHADOW_MOBS, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> OMOTHOL_STAFF_OF_RENDING = item("omothol_staff_of_rending", "drainstaff_omt",
			() -> new com.shinoow.abyssalcraft.common.items.ItemDrainStaff(3), SHADOW_MOBS, ACTabs.Tab.TOOLS);
	// 1.12.2 "scroll" metadata 0 - 3 and "unique_scroll" 0 - 1
	public static final RegistryObject<Item> BASIC_SCROLL = scroll("basic_scroll", "scroll_basic", ScrollType.BASIC);
	public static final RegistryObject<Item> LESSER_SCROLL = scroll("lesser_scroll", "scroll_lesser", ScrollType.LESSER);
	public static final RegistryObject<Item> MODERATE_SCROLL = scroll("moderate_scroll", "scroll_moderate", ScrollType.MODERATE);
	public static final RegistryObject<Item> GREATER_SCROLL = scroll("greater_scroll", "scroll_greater", ScrollType.GREATER);
	public static final RegistryObject<Item> ANTIMATTER_SCROLL = scroll("antimatter_scroll", "scroll_unique_anti", ScrollType.UNIQUE);
	public static final RegistryObject<Item> OBLIVION_SCROLL = scroll("oblivion_scroll", "scroll_unique_oblivion", ScrollType.UNIQUE);
	public static final RegistryObject<Item> INTERDIMENSIONAL_CAGE = item("interdimensional_cage", "interdimensionalcage",
			com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage::new, AW, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> SOUL_HARVESTING_BLADE = item("sacthoths_soul_harvesting_blade", "soulreaper",
			com.shinoow.abyssalcraft.common.items.ItemSoulReaper::new, new EntityCondition("abyssalcraft:sacthoth"), ACTabs.Tab.COMBAT);

	// Abyssalnite
	public static final RegistryObject<Item> CHUNK_OF_ABYSSALNITE = basic("chunk_of_abyssalnite", "abychunk", new EntityCondition("abyssalcraft:abyssalnite_golem"));
	public static final RegistryObject<Item> ABYSSALNITE_INGOT = item("abyssalnite_ingot", "abyingot", () -> new ItemACBasic().color(ChatFormatting.DARK_AQUA), DARKLANDS, ACTabs.Tab.ITEMS);

	// Coralium
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_2 = cluster(2);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_3 = cluster(3);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_4 = cluster(4);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_5 = cluster(5);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_6 = cluster(6);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_7 = cluster(7);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_8 = cluster(8);
	public static final RegistryObject<Item> CORALIUM_GEM_CLUSTER_9 = cluster(9);
	public static final RegistryObject<Item> CORALIUM_PEARL = item("coralium_pearl", "cpearl", () -> new ItemACBasic().color(ChatFormatting.AQUA), null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> CHUNK_OF_CORALIUM = basic("chunk_of_coralium", "cchunk", AW);
	public static final RegistryObject<Item> CORALIUM_LONGBOW = item("coralium_longbow", "corbow",
			com.shinoow.abyssalcraft.common.items.ItemCoraliumBow::new, AW, ACTabs.Tab.COMBAT);
	public static final RegistryObject<Item> REFINED_CORALIUM_INGOT = item("refined_coralium_ingot", "cingot", () -> new ItemACBasic().color(ChatFormatting.AQUA), AW, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> CORALIUM_PLATE = basic("coralium_plate", "platec", AW);
	public static final RegistryObject<Item> CORALIUM_GEM = basic("coralium_gem", "coralium", null);
	public static final RegistryObject<Item> TRANSMUTATION_GEM = item("transmutation_gem", "transmutationgem", () -> ((ItemACBasic) new ItemCraftingTool(10, "tooltip.corb")).color(ChatFormatting.AQUA).foil(), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> CORALIUM_PLAGUED_FLESH = food("coralium_plagued_flesh", "corflesh", () -> new ItemACFood(2, 0.1F, false, ItemHandler::coraliumFlesh),
			new EntityCondition("abyssalcraft:abyssal_zombie"));
	public static final RegistryObject<Item> CORALIUM_PLAGUED_FLESH_ON_A_BONE = food("coralium_plagued_flesh_on_a_bone", "corbone", () -> new ItemACFood(2, 0.1F, false, (p, w) -> {
		coraliumFlesh(p, w);
		give(p, new ItemStack(Items.BONE));
	}), new EntityCondition("abyssalcraft:depths_ghoul"));
	public static final RegistryObject<Item> OMOTHOL_FLESH = food("omothol_flesh", "omotholflesh", () -> new ItemACFood(3, 0.3F, false, (p, w) -> {
		w.playSound(null, p.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_BURP, net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, w.random.nextFloat() * 0.1F + 0.9F);
		if(com.shinoow.abyssalcraft.api.entity.EntityUtil.isPlayerCoralium(p)){
			effect(p, MobEffects.WEAKNESS, 100, 0);
			effect(p, MobEffects.HUNGER, 300, 1);
			effect(p, MobEffects.CONFUSION, 200, 0);
		} else {
			effect(p, MobEffects.WEAKNESS, 100, 0);
			effect(p, MobEffects.HUNGER, 400, 1);
			effect(p, MobEffects.CONFUSION, 300, 0);
		}
		effect(p, MobEffects.BLINDNESS, 40, 0);
		effect(p, MobEffects.NIGHT_VISION, 40, 0);
	}), new EntityCondition("abyssalcraft:omothol_ghoul"));

	// Necronomicons (book type, PE capacity)
	public static final RegistryObject<Item> NECRONOMICON = item("necronomicon", "necronomicon", () -> new ItemNecronomicon(0, 5000), null, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> ABYSSAL_WASTELAND_NECRONOMICON = item("abyssal_wasteland_necronomicon", "necronomicon_cor",
			() -> new ItemNecronomicon(1, 10000), AW, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> DREADLANDS_NECRONOMICON = item("dreadlands_necronomicon", "necronomicon_dre",
			() -> new ItemNecronomicon(2, 20000), DL, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> OMOTHOL_NECRONOMICON = item("omothol_necronomicon", "necronomicon_omt",
			() -> new ItemNecronomicon(3, 40000), OMT, ACTabs.Tab.ITEMS);
	public static final RegistryObject<Item> ABYSSALNOMICON = item("abyssalnomicon", "abyssalnomicon", () -> new ItemNecronomicon(4, 100000), OMT, ACTabs.Tab.ITEMS);

	public static final RegistryObject<Item> CUDGEL = item("cudgel", "cudgel", com.shinoow.abyssalcraft.common.items.ItemCudgel::new,
			new com.shinoow.abyssalcraft.api.necronomicon.condition.EntityCondition("abyssalcraft:skeleton_goliath"), ACTabs.Tab.COMBAT);

	public static final RegistryObject<Item> STONE_TABLET = item("stone_tablet", "stonetablet", ItemStoneTablet::new, null, ACTabs.Tab.ITEMS);

	/** Spawn eggs of the AbyssalCraft mobs (shown in the vanilla spawn egg tab) */
	public static final List<RegistryObject<Item>> SPAWN_EGGS = new ArrayList<>();
	static {
		for(ACEntityTypes.SpawnEgg egg : ACEntityTypes.SPAWN_EGGS)
			SPAWN_EGGS.add(ITEMS.register(egg.name() + "_spawn_egg", () -> new net.minecraftforge.common.ForgeSpawnEggItem(egg.type(), egg.background(), egg.spots(),
					new Item.Properties())));
	}

	/** MiscHandler: textures of the armor on the Depths Ghoul model */
	public static void registerGhoulArmorTextures() {
		ghoulArmor("leather", net.minecraft.world.item.Items.LEATHER_HELMET, net.minecraft.world.item.Items.LEATHER_CHESTPLATE,
				net.minecraft.world.item.Items.LEATHER_LEGGINGS, net.minecraft.world.item.Items.LEATHER_BOOTS);
		ghoulArmor("chainmail", net.minecraft.world.item.Items.CHAINMAIL_HELMET, net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE,
				net.minecraft.world.item.Items.CHAINMAIL_LEGGINGS, net.minecraft.world.item.Items.CHAINMAIL_BOOTS);
		ghoulArmor("iron", net.minecraft.world.item.Items.IRON_HELMET, net.minecraft.world.item.Items.IRON_CHESTPLATE,
				net.minecraft.world.item.Items.IRON_LEGGINGS, net.minecraft.world.item.Items.IRON_BOOTS);
		ghoulArmor("gold", net.minecraft.world.item.Items.GOLDEN_HELMET, net.minecraft.world.item.Items.GOLDEN_CHESTPLATE,
				net.minecraft.world.item.Items.GOLDEN_LEGGINGS, net.minecraft.world.item.Items.GOLDEN_BOOTS);
		ghoulArmor("diamond", net.minecraft.world.item.Items.DIAMOND_HELMET, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE,
				net.minecraft.world.item.Items.DIAMOND_LEGGINGS, net.minecraft.world.item.Items.DIAMOND_BOOTS);
		String[][] sets = {{"abyssalnite", "abyssalnite"}, {"refined_coralium", "coralium"}, {"dreadium", "dreadium"}, {"ethaxium", "ethaxium"},
				{"dreaded_abyssalnite", "dread"}, {"depths", "depths"}, {"plated_coralium", "coraliump"}, {"dreadium_samurai", "dreadiums"}};
		for(String[] set : sets) {
			Item[] pieces = java.util.stream.Stream.of("_helmet", "_chestplate", "_leggings", "_boots")
					.map(s -> net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(AbyssalCraft.loc(set[0] + s))).toArray(Item[]::new);
			ghoulArmor(set[1], pieces[0], pieces[1], pieces[2], pieces[3]);
		}
	}

	private static void ghoulArmor(String texture, Item helmet, Item chestplate, Item leggings, Item boots) {
		com.shinoow.abyssalcraft.api.AbyssalCraftAPI.addGhoulArmorTextures(helmet, chestplate, leggings, boots,
				AbyssalCraft.loc("textures/armor/ghoul/" + texture + "_1.png"), AbyssalCraft.loc("textures/armor/ghoul/" + texture + "_2.png"));
	}

	public static final RegistryObject<Item> CONFIGURATOR = item("configurator", "configurator", ItemConfigurator::new, OMT, ACTabs.Tab.TOOLS);

	// Gateway keys
	public static final RegistryObject<Item> GATEWAY_KEY = tool("gateway_key", "gatewaykey", () -> new ItemPortalPlacer(0), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> DREADED_GATEWAY_KEY = tool("dreaded_gateway_key", "gatewaykeydl", () -> new ItemPortalPlacer(1), AW, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> RLYEHIAN_GATEWAY_KEY = tool("rlyehian_gateway_key", "gatewaykeyjzh", () -> new ItemPortalPlacer(2), DL, ACTabs.Tab.TOOLS);

	// Tools
	public static final RegistryObject<Item> DARKSTONE_PICKAXE = tool("darkstone_pickaxe", "dpick", () -> new ItemACPickaxe(ACTiers.DARKSTONE, 1, -2.8F, null), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> DARKSTONE_AXE = tool("darkstone_axe", "daxe", () -> new ItemACAxe(ACTiers.DARKSTONE, 7.0F, -3.2F, null), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> DARKSTONE_SHOVEL = tool("darkstone_shovel", "dshovel", () -> new ItemACShovel(ACTiers.DARKSTONE, 1.5F, -3.0F, null), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> DARKSTONE_SWORD = tool("darkstone_sword", "dsword", () -> new ItemACSword(ACTiers.DARKSTONE, 3, -2.4F, null), null, ACTabs.Tab.COMBAT);
	public static final RegistryObject<Item> DARKSTONE_HOE = tool("darkstone_hoe", "dhoe", () -> new ItemACHoe(ACTiers.DARKSTONE, -1, -2.0F, null), null, ACTabs.Tab.TOOLS);
	public static final RegistryObject<Item> ABYSSALNITE_PICKAXE = toolSet("abyssalnite", "a", ACTiers.ABYSSALNITE, ChatFormatting.DARK_AQUA, DARKLANDS);
	public static final RegistryObject<Item> REFINED_CORALIUM_PICKAXE = toolSet("refined_coralium", "cor", ACTiers.REFINED_CORALIUM, ChatFormatting.AQUA, AW);
	public static final RegistryObject<Item> DREADIUM_PICKAXE = toolSet("dreadium", "dreadium", ACTiers.DREADIUM, ChatFormatting.DARK_RED, DL);
	public static final RegistryObject<Item> ETHAXIUM_PICKAXE = tool("ethaxium_pickaxe", "ethaxiumpickaxe", () -> new ItemEthaxiumPickaxe(ACTiers.ETHAXIUM), OMT, ACTabs.Tab.TOOLS);
	static {
		tool("ethaxium_axe", "ethaxiumaxe", () -> new ItemACAxe(ACTiers.ETHAXIUM, 5.0F, -3.0F, ChatFormatting.AQUA), OMT, ACTabs.Tab.TOOLS);
		tool("ethaxium_shovel", "ethaxiumshovel", () -> new ItemACShovel(ACTiers.ETHAXIUM, 1.5F, -3.0F, ChatFormatting.AQUA), OMT, ACTabs.Tab.TOOLS);
		tool("ethaxium_sword", "ethaxiumsword", () -> new ItemACSword(ACTiers.ETHAXIUM, 3, -2.4F, ChatFormatting.AQUA), OMT, ACTabs.Tab.COMBAT);
		tool("ethaxium_hoe", "ethaxiumhoe", () -> new ItemACHoe(ACTiers.ETHAXIUM, -8, 0.0F, ChatFormatting.AQUA), OMT, ACTabs.Tab.TOOLS);
	}

	// Armor
	static {
		armorSet("abyssalnite", ACArmorMaterials.ABYSSALNITE, ChatFormatting.DARK_AQUA, "abyssalnite", new String[] {"ahelmet", "aplate", "alegs", "aboots"}, DARKLANDS);
		armorSet("dreaded_abyssalnite", ACArmorMaterials.DREAD, ChatFormatting.DARK_RED, "dread", new String[] {"dhelmet", "dplate", "dlegs", "dboots"}, DL);
		armorSet("refined_coralium", ACArmorMaterials.REFINED_CORALIUM, ChatFormatting.AQUA, "coralium", new String[] {"corhelmet", "corplate", "corlegs", "corboots"}, AW);
		armorSet("plated_coralium", ACArmorMaterials.PLATED_CORALIUM, ChatFormatting.GREEN, "coraliump", new String[] {"corhelmetp", "corplatep", "corlegsp", "corbootsp"}, AW);
		armorSet("depths", ACArmorMaterials.DEPTHS, ChatFormatting.DARK_RED, "depths", new String[] {"depthshelmet", "depthsplate", "depthslegs", "depthsboots"}, AW);
		armorSet("dreadium", ACArmorMaterials.DREADIUM, ChatFormatting.DARK_RED, "dreadium", new String[] {"dreadiumhelmet", "dreadiumplate", "dreadiumlegs", "dreadiumboots"}, DL);
		armorSet("dreadium_samurai", ACArmorMaterials.DREADIUM_SAMURAI, ChatFormatting.DARK_RED, "dreadiums", new String[] {"dreadiumsamuraihelmet", "dreadiumsamuraiplate", "dreadiumsamurailegs", "dreadiumsamuraiboots"}, DL);
		armorSet("ethaxium", ACArmorMaterials.ETHAXIUM, ChatFormatting.AQUA, "ethaxium", new String[] {"ethaxiumhelmet", "ethaxiumplate", "ethaxiumlegs", "ethaxiumboots"}, OMT);
	}

	// Upgrade kits
	public static final RegistryObject<Item> COBBLESTONE_UPGRADE_KIT = kit("cobblestone_upgrade_kit", "cobbleu", "Wood", "Cobblestone", null);
	public static final RegistryObject<Item> IRON_UPGRADE_KIT = kit("iron_upgrade_kit", "ironu", "Cobblestone", "Iron", null);
	public static final RegistryObject<Item> GOLD_UPGRADE_KIT = kit("gold_upgrade_kit", "goldu", "Iron", "Gold", null);
	public static final RegistryObject<Item> DIAMOND_UPGRADE_KIT = kit("diamond_upgrade_kit", "diamondu", "Gold", "Diamond", null);
	public static final RegistryObject<Item> ABYSSALNITE_UPGRADE_KIT = kit("abyssalnite_upgrade_kit", "abyssalniteu", "Diamond", "Abyssalnite", DARKLANDS);
	public static final RegistryObject<Item> CORALIUM_UPGRADE_KIT = kit("coralium_upgrade_kit", "coraliumu", "Abyssalnite", "Coralium", AW);
	public static final RegistryObject<Item> DREADIUM_UPGRADE_KIT = kit("dreadium_upgrade_kit", "dreadiumu", "Coralium", "Dreadium", DL);
	public static final RegistryObject<Item> ETHAXIUM_UPGRADE_KIT = kit("ethaxium_upgrade_kit", "ethaxiumu", "Dreadium", "Ethaxium", OMT);

	// Foodstuffs
	public static final RegistryObject<Item> IRON_PLATE = basic("iron_plate", "ironp", null);
	public static final RegistryObject<Item> MRE = plate("mre", "mre", 20, 1F);
	public static final RegistryObject<Item> CHICKEN_ON_A_PLATE = plate("chicken_on_a_plate", "chickenp", 9, 0.9F);
	public static final RegistryObject<Item> PORK_ON_A_PLATE = plate("pork_on_a_plate", "porkp", 12, 1.2F);
	public static final RegistryObject<Item> BEEF_ON_A_PLATE = plate("beef_on_a_plate", "beefp", 12, 1.2F);
	public static final RegistryObject<Item> FISH_ON_A_PLATE = plate("fish_on_a_plate", "fishp", 8, 0.9F);
	public static final RegistryObject<Item> DIRTY_PLATE = basic("dirty_plate", "dirtyplate", null);
	public static final RegistryObject<Item> FRIED_EGG = food("fried_egg", "friedegg", () -> new ItemACFood(5, 0.6F, false, null), null);
	public static final RegistryObject<Item> FRIED_EGG_ON_A_PLATE = plate("fried_egg_on_a_plate", "eggp", 8, 0.9F);
	public static final RegistryObject<Item> WASHCLOTH = item("washcloth", "cloth", () -> new ItemCraftingTool(20, "tooltip.washcloth"), null, ACTabs.Tab.ITEMS);

	private static IUnlockCondition dim(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> key) {
		return new DimensionCondition(key);
	}

	/**
	 * Registers an item
	 * @param name registry name
	 * @param texture upstream texture name (textures/item/&lt;texture&gt;.png), null if the model is provided elsewhere
	 * @param condition Necronomicon unlock condition (null = always unlocked)
	 * @param tab creative tab (null = none)
	 */
	public static RegistryObject<Item> item(String name, String texture, Supplier<? extends Item> sup, IUnlockCondition condition, ACTabs.Tab tab) {
		RegistryObject<Item> obj = ITEMS.register(name, () -> {
			Item item = sup.get();
			if(condition != null && item instanceof IUnlockableItem)
				((IUnlockableItem) item).setUnlockCondition(condition);
			return item;
		});
		if(texture != null)
			ITEM_MODELS.put(name, texture);
		if(tab != null)
			tab.add(obj);
		return obj;
	}

	private static RegistryObject<Item> scroll(String name, String texture, ScrollType type) {
		return item(name, texture, () -> new com.shinoow.abyssalcraft.common.items.ItemScroll(type), null, ACTabs.Tab.SPELLS);
	}

	private static RegistryObject<Item> basic(String name, String texture, IUnlockCondition condition) {
		return item(name, texture, ItemACBasic::new, condition, ACTabs.Tab.ITEMS);
	}

	private static RegistryObject<Item> food(String name, String texture, Supplier<? extends Item> sup, IUnlockCondition condition) {
		return item(name, texture, sup, condition, ACTabs.Tab.FOOD);
	}

	private static RegistryObject<Item> tool(String name, String texture, Supplier<? extends Item> sup, IUnlockCondition condition, ACTabs.Tab tab) {
		HANDHELD.add(name);
		return item(name, texture, sup, condition, tab);
	}

	private static RegistryObject<Item> toolSet(String material, String prefix, Tier tier, ChatFormatting color, IUnlockCondition condition) {
		boolean longName = prefix.length() > 3;
		RegistryObject<Item> pick = tool(material + "_pickaxe", longName ? prefix + "pickaxe" : prefix + "pick", () -> new ItemACPickaxe(tier, 1, -2.8F, color), condition, ACTabs.Tab.TOOLS);
		tool(material + "_axe", prefix + "axe", () -> new ItemACAxe(tier, 5.0F, -3.0F, color), condition, ACTabs.Tab.TOOLS);
		tool(material + "_shovel", prefix + "shovel", () -> new ItemACShovel(tier, 1.5F, -3.0F, color), condition, ACTabs.Tab.TOOLS);
		tool(material + "_sword", prefix + "sword", () -> new ItemACSword(tier, 3, -2.4F, color), condition, ACTabs.Tab.COMBAT);
		tool(material + "_hoe", prefix + "hoe", () -> new ItemACHoe(tier, (int) -tier.getAttackDamageBonus(), 0.0F, color), condition, ACTabs.Tab.TOOLS);
		return pick;
	}

	private static void armorSet(String material, ACArmorMaterials mat, ChatFormatting color, String texture, String[] textures, IUnlockCondition condition) {
		item(material + "_helmet", textures[0], () -> new ItemACArmor(mat, ArmorItem.Type.HELMET, color, texture), condition, ACTabs.Tab.COMBAT);
		item(material + "_chestplate", textures[1], () -> new ItemACArmor(mat, ArmorItem.Type.CHESTPLATE, color, texture), condition, ACTabs.Tab.COMBAT);
		item(material + "_leggings", textures[2], () -> new ItemACArmor(mat, ArmorItem.Type.LEGGINGS, color, texture), condition, ACTabs.Tab.COMBAT);
		item(material + "_boots", textures[3], () -> new ItemACArmor(mat, ArmorItem.Type.BOOTS, color, texture), condition, ACTabs.Tab.COMBAT);
	}

	private static RegistryObject<Item> coin(String name, String texture) {
		return item(name, texture, ItemACBasic::new, dim(ACDimensions.OMOTHOL), ACTabs.Tab.COINS);
	}

	private static RegistryObject<Item> engraving(String name, String texture, int uses) {
		return item(name, texture, () -> new ItemEngraving(uses), dim(ACDimensions.OMOTHOL), ACTabs.Tab.COINS);
	}

	private static RegistryObject<Item> cluster(int gems) {
		return item("coralium_gem_cluster_" + gems, "ccluster" + gems, () -> new ItemCoraliumCluster(String.valueOf(gems)), null, ACTabs.Tab.ITEMS);
	}

	private static RegistryObject<Item> kit(String name, String texture, String from, String to, IUnlockCondition condition) {
		return item(name, texture, () -> new ItemUpgradeKit(from, to), condition, ACTabs.Tab.ITEMS);
	}

	private static RegistryObject<Item> plate(String name, String texture, int nutrition, float saturation) {
		return food(name, texture, () -> new ItemACFood(nutrition, saturation, false, 4, (p, w) -> give(p, new ItemStack(ACItems.dirty_plate.get()))), null);
	}

	private static void charms(String name, String texture, DeityType deity) {
		boolean ritual = deity == null;
		CHARMS.add(item(name, ritual ? texture + "_empty" : texture, () -> new ItemCharm(null, deity), null, ACTabs.Tab.ITEMS));
		CHARMS.add(item(name + "_range", ritual ? texture + "_range" : texture, () -> new ItemCharm(AmplifierType.RANGE, deity), null, ACTabs.Tab.ITEMS));
		CHARMS.add(item(name + "_duration", ritual ? texture + "_duration" : texture, () -> new ItemCharm(AmplifierType.DURATION, deity), null, ACTabs.Tab.ITEMS));
		CHARMS.add(item(name + "_power", ritual ? texture + "_power" : texture, () -> new ItemCharm(AmplifierType.POWER, deity), null, ACTabs.Tab.ITEMS));
	}

	private static void effect(Player player, net.minecraft.world.effect.MobEffect effect, int duration, int amplifier) {
		player.addEffect(new MobEffectInstance(effect, duration, amplifier));
	}

	private static void give(Player player, ItemStack stack) {
		if(!player.getInventory().add(stack))
			player.drop(stack, false);
	}

	private static void coraliumFlesh(Player p, net.minecraft.world.level.Level w) {
		effect(p, MobEffects.HUNGER, 600, 1);
		if(!com.shinoow.abyssalcraft.api.entity.EntityUtil.isPlayerCoralium(p))
			p.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 600, 0));
		effect(p, MobEffects.CONFUSION, 600, 0);
	}

	/**
	 * Registers the block items for every block in {@link BlockHandler#INFO} (called from the mod constructor,
	 * after all blocks have been declared)
	 */
	public static void registerBlockItems() {
		BlockHandler.INFO.forEach((name, info) -> {
			if(!info.hasItem) return;
			RegistryObject<Item> obj = ITEMS.register(name, () -> {
				Item item = info.itemFactory != null ? info.itemFactory.apply(info.block.get(), new Item.Properties())
						: info.block.get() instanceof DoorBlock ? new DoubleHighBlockItem(info.block.get(), new Item.Properties())
								: new ItemBlockAC(info.block.get(), new Item.Properties());
				if(info.unlock != null && item instanceof IUnlockableItem unlockable)
					unlockable.setUnlockCondition(info.unlock);
				return item;
			});
			if(info.tab != null)
				info.tab.add(obj);
		});
	}
}
