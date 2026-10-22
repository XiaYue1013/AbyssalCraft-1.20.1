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
 *     1.20.1 port - AbyssalCrafting.addRitualRecipes. Metadata variants are separate items/blocks, ore dictionary
 *     names are tags. A ritual whose result, offerings, entity or enchantment isn't registered is skipped (and logged),
 *     so nothing is registered with empty stacks.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.*;
import com.shinoow.abyssalcraft.common.ritual.*;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RitualRecipes {

	/** Thrown while building a ritual that needs content that isn't registered */
	private static class Missing extends RuntimeException {
		Missing(String what) {
			super(what, null, false, false);
		}
	}

	private static final List<String> skipped = new ArrayList<>();

	/** The rituals that couldn't be registered (content not ported yet) */
	public static List<String> getSkipped() {
		return skipped;
	}

	private static void reg(String name, Supplier<NecronomiconRitual> ritual) {
		try {
			RitualRegistry.instance().registerRitual(ritual.get());
		} catch(Missing e) {
			skipped.add(name);
			AbyssalCraft.LOGGER.debug("Ritual {} skipped, {} isn't registered", name, e.getMessage());
		}
	}

	/** The registered item/block, or skip the ritual */
	private static ItemLike o(RegistryObject<? extends ItemLike> obj) {
		if(!obj.isPresent())
			throw new Missing(obj.getId().toString());
		return obj.get();
	}

	private static ItemStack s(RegistryObject<? extends ItemLike> obj) {
		return new ItemStack(o(obj));
	}

	@SuppressWarnings("unchecked")
	private static Supplier<EntityType<? extends LivingEntity>> mob(String id) {
		ResourceLocation key = new ResourceLocation(AbyssalCraft.modid, id);
		if(!ForgeRegistries.ENTITY_TYPES.containsKey(key))
			throw new Missing(key.toString());
		EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) ForgeRegistries.ENTITY_TYPES.getValue(key);
		return () -> type;
	}

	private static Supplier<EnchantmentInstance> ench(RegistryObject<Enchantment> enchantment, int level) {
		if(!enchantment.isPresent())
			throw new Missing(enchantment.getId().toString());
		return () -> new EnchantmentInstance(enchantment.get(), level);
	}

	private static ItemStack water() {
		return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
	}

	private static Object[] fill(Object... offerings) {
		return offerings;
	}

	public static void addRitualRecipes(){
		skipped.clear();
		ResourceKeys k = new ResourceKeys();

		//Overworld progression
		reg("transmutationGem", () -> new NecronomiconInfusionRitual("transmutationGem", 0, 300F, s(ACItems.transmutation_gem), o(ACItems.coralium_pearl),
				fill(Items.DIAMOND, Items.BLAZE_POWDER, Items.ENDER_PEARL, Items.BLAZE_POWDER, Items.DIAMOND, Items.BLAZE_POWDER, Items.ENDER_PEARL, Items.BLAZE_POWDER)));
		reg("oblivionCatalyst", () -> new NecronomiconInfusionRitual("oblivionCatalyst", 0, null, 5000F, true, s(ACItems.oblivion_catalyst), Items.ENDER_EYE,
				fill(Items.REDSTONE, o(ACItems.shard_of_oblivion), Items.REDSTONE, o(ACItems.shard_of_oblivion), Items.REDSTONE, o(ACItems.shard_of_oblivion),
						Items.REDSTONE, o(ACItems.shard_of_oblivion))));

		//Abyssal Wasteland progression
		reg("summonAsorah", () -> new NecronomiconSummonRitual("summonAsorah", 1, k.aw, 1000F, mob(ACEntities.asorah),
				fill(Items.GOLD_INGOT, o(ACItems.transmutation_gem), Items.GOLD_INGOT, s(ACItems.liquid_coralium_bucket), Items.GOLD_INGOT, Blocks.ENCHANTING_TABLE,
						Items.GOLD_INGOT)).setNBTSensitive());
		reg("asorahGatewayKey", () -> new NecronomiconInfusionRitual("asorahGatewayKey", 1, k.aw, 10000F, s(ACItems.dreaded_gateway_key), o(ACItems.gateway_key),
				fill(o(ACItems.transmutation_gem), null, o(ACBlocks.dreadlands_infused_powerstone), null, o(ACItems.eye_of_the_abyss))));

		//Dreadlands Progression
		reg("altarOfChagarothBottom", () -> new NecronomiconCreationRitual("altarOfChagarothBottom", 2, k.dl, 20000F, true, s(ACBlocks.chagaroth_altar_bottom),
				fill(o(ACItems.dread_cloth), Items.BONE, o(ACItems.dreadium_ingot), o(ACBlocks.dreadstone), o(ACItems.dreaded_gateway_key), o(ACBlocks.dreadstone),
						o(ACItems.dreadium_ingot), Items.BONE)));
		reg("altarOfChagarothTop", () -> new NecronomiconCreationRitual("altarOfChagarothTop", 2, k.dl, 20000F, true, s(ACBlocks.chagaroth_altar_top),
				fill(Items.BUCKET, Tags.Items.RODS_WOODEN, o(ACItems.dread_cloth), o(ACItems.dreadium_ingot), o(ACItems.dread_cloth), o(ACItems.dreadium_ingot),
						o(ACItems.dread_cloth), Tags.Items.RODS_WOODEN)));
		reg("interdimensionalcage", () -> new NecronomiconInfusionRitual("interdimensionalcage", 2, 1000F, s(ACItems.interdimensional_cage), o(ACItems.shard_of_oblivion),
				fill(Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS, Blocks.IRON_BARS)));

		//Omothol progression
		reg("respawnJzahar", com.shinoow.abyssalcraft.common.ritual.NecronomiconRespawnJzaharRitual::new);

		//Everything else
		Supplier<Object[]> depths = () -> fill(o(ACItems.coralium_gem_cluster_9), o(ACItems.coralium_gem_cluster_9), s(ACItems.liquid_coralium_bucket),
				Blocks.VINE, Blocks.LILY_PAD, o(ACItems.transmutation_gem), o(ACItems.coralium_plagued_flesh));
		reg("depthsHelmet", () -> new NecronomiconInfusionRitual("depthsHelmet", 1, k.aw, 300F, s(ACItems.depths_helmet), o(ACItems.refined_coralium_helmet), depths.get()).setNBTSensitive());
		reg("depthsChestplate", () -> new NecronomiconInfusionRitual("depthsChestplate", 1, k.aw, 300F, s(ACItems.depths_chestplate), o(ACItems.refined_coralium_chestplate), depths.get()).setNBTSensitive());
		reg("depthsLeggings", () -> new NecronomiconInfusionRitual("depthsLeggings", 1, k.aw, 300F, s(ACItems.depths_leggings), o(ACItems.refined_coralium_leggings), depths.get()).setNBTSensitive());
		reg("depthsBoots", () -> new NecronomiconInfusionRitual("depthsBoots", 1, k.aw, 300F, s(ACItems.depths_boots), o(ACItems.refined_coralium_boots), depths.get()).setNBTSensitive());
		reg("breeding", NecronomiconBreedingRitual::new);
		reg("summonSacthoth", () -> new NecronomiconSummonRitual("summonSacthoth", 1, 1000F, mob(ACEntities.sacthoth),
				fill(o(ACItems.oblivion_catalyst), Blocks.OBSIDIAN, s(ACItems.liquid_coralium_bucket), Blocks.OBSIDIAN, s(ACItems.liquid_antimatter_bucket),
						Blocks.OBSIDIAN, o(ACBlocks.odb_core), Blocks.OBSIDIAN)).setNBTSensitive());
		reg("dreadSpawn", NecronomiconDreadSpawnRitual::new);
		reg("corPotionAoE", () -> new NecronomiconPotionAoERitual("corPotionAoE", 1, 300F, AbyssalCraftAPI.coralium_plague,
				fill(o(ACItems.coralium_plagued_flesh), water(), o(ACItems.coralium_plagued_flesh), water(), o(ACItems.coralium_plagued_flesh), water(),
						o(ACItems.coralium_plagued_flesh), Items.GUNPOWDER)));
		reg("drePotionAoE", () -> new NecronomiconPotionAoERitual("drePotionAoE", 2, 300F, AbyssalCraftAPI.dread_plague,
				fill(o(ACItems.dread_fragment), water(), o(ACItems.dread_fragment), water(), o(ACItems.dread_fragment), water(), o(ACItems.dread_fragment), Items.GUNPOWDER)));
		reg("antiPotionAoE", () -> new NecronomiconPotionAoERitual("antiPotionAoE", 0, 300F, AbyssalCraftAPI.antimatter_potion,
				fill(o(ACItems.rotten_anti_flesh), water(), o(ACItems.rotten_anti_flesh), water(), o(ACItems.rotten_anti_flesh), water(), o(ACItems.rotten_anti_flesh),
						Items.GUNPOWDER)));
		if(ACConfig.plague_enchantments) {
			reg("corEnchantment", () -> new NecronomiconEnchantmentRitual("corEnchantment", 1, k.aw, 5000F, ench(AbyssalCraftAPI.coralium_enchantment, 1),
					fill(o(ACItems.abyssal_wasteland_essence), o(ACItems.shoggoth_flesh_abyssal_wasteland), o(ACItems.refined_coralium_ingot), o(ACItems.coralium_plagued_flesh),
							Items.BOOK, o(ACItems.coralium_plagued_flesh), o(ACItems.refined_coralium_ingot), o(ACItems.shoggoth_flesh_abyssal_wasteland))));
			reg("dreEnchantment", () -> new NecronomiconEnchantmentRitual("dreEnchantment", 2, k.dl, 10000F, ench(AbyssalCraftAPI.dread_enchantment, 1),
					fill(o(ACItems.dreadlands_essence), o(ACItems.shoggoth_flesh_dreadlands), o(ACItems.dreadium_ingot), o(ACItems.dread_fragment), Items.BOOK,
							o(ACItems.dread_fragment), o(ACItems.dreadium_ingot), o(ACItems.shoggoth_flesh_dreadlands))));
		}
		reg("resurrection", NecronomiconResurrectionRitual::new);
		reg("cleansing", NecronomiconCleansingRitual::new);
		reg("corruption", NecronomiconCorruptionRitual::new);
		reg("curing", NecronomiconCuringRitual::new);
		reg("massEnchantment", NecronomiconMassEnchantRitual::new);
		reg("spiritTablet", () -> {
			Ingredient shard = Ingredient.of(o(ACItems.configurator_shard_0), o(ACItems.configurator_shard_1), o(ACItems.configurator_shard_2), o(ACItems.configurator_shard_3));
			return new NecronomiconInfusionRitual("spiritTablet", 3, null, 5000F, s(ACItems.configurator), o(ACItems.life_crystal),
					fill(null, shard, null, shard, null, shard, null, shard));
		});
		reg("spiritAltar", () -> new NecronomiconInfusionRitual("spiritAltar", 1, null, 1000F, true, s(ACBlocks.spirit_altar), o(ACItems.shadow_gem),
				fill(Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, o(ACBlocks.darkstone_cobblestone), o(ACBlocks.darkstone_cobblestone),
						o(ACBlocks.darkstone_cobblestone), o(ACBlocks.darkstone_cobblestone), o(ACBlocks.darkstone_cobblestone))));

		// statues (from Monolith Stone)
		RegistryObject<?>[] flesh = {ACItems.shoggoth_flesh_overworld, ACItems.shoggoth_flesh_abyssal_wasteland, ACItems.shoggoth_flesh_dreadlands,
				ACItems.shoggoth_flesh_omothol, ACItems.shoggoth_flesh_dark_realm};
		String[] statues = {"cthulhuStatue", "hasturStatue", "jzaharStatue", "azathothStatue", "nyarlathotepStatue", "yogsothothStatue", "shubniggurathStatue"};
		for(int i = 0; i < statues.length; i++) {
			int index = i;
			reg(statues[i], () -> {
				Object[] five = new Object[5];
				for(int j = 0; j < 5; j++)
					five[j] = switch(index) {
					case 0 -> o(ACItems.shoggoth_flesh_overworld);
					case 1 -> o(ACItems.shoggoth_flesh_abyssal_wasteland);
					case 2 -> o(ACItems.eldritch_scale);
					case 3 -> o(cast(flesh[j]));
					case 4 -> o(ACItems.shoggoth_flesh_dreadlands);
					case 5 -> o(ACItems.shoggoth_flesh_omothol);
					default -> o(ACItems.shoggoth_flesh_dark_realm);
					};
				return new NecronomiconInfusionRitual(statues[index], 4, null, 20000F, true, s(BlockHandler.STATUES.get(index)), o(ACBlocks.monolith_stone),
						fill(five[0], five[1], five[2], five[3], five[4], o(ACItems.abyssal_wasteland_essence), o(ACItems.dreadlands_essence), o(ACItems.omothol_essence)));
			});
		}

		reg("powerStone", () -> {
			ItemLike e = o(ACItems.dreadlands_essence);
			return new NecronomiconInfusionRitual("powerStone", 2, k.dl, 5000F, s(ACBlocks.dreadlands_infused_powerstone), o(ACBlocks.coralium_infused_stone),
					fill(e, e, e, e, e, e, e, e));
		});
		reg("purging", NecronomiconPurgingRitual::new);
		reg("ethaxium", () -> new NecronomiconTransformationRitual("ethaxium", 3, k.omt, 1000.0F, new ItemStack(Blocks.END_STONE), s(ACBlocks.ethaxium)));
		reg("ethaxiumIngot", () -> new NecronomiconCreationRitual("ethaxiumIngot", 3, k.omt, 1000F, s(ACItems.ethaxium_ingot),
				fill(o(ACItems.ethaxium_brick), o(ACItems.ethaxium_brick), o(ACItems.life_crystal), o(ACItems.ethaxium_brick), o(ACItems.ethaxium_brick))));
		Supplier<Object[]> dread = () -> {
			ItemLike shard = o(ACItems.dreaded_shard_of_abyssalnite);
			return fill(o(ACItems.dreadlands_essence), shard, shard, shard, shard, shard, shard, shard);
		};
		reg("dreadHelmet", () -> new NecronomiconInfusionRitual("dreadHelmet", 2, k.dl, 500F, s(ACItems.dreaded_abyssalnite_helmet), o(ACItems.abyssalnite_helmet), dread.get()));
		reg("dreadChestplate", () -> new NecronomiconInfusionRitual("dreadChestplate", 2, k.dl, 500F, s(ACItems.dreaded_abyssalnite_chestplate), o(ACItems.abyssalnite_chestplate), dread.get()));
		reg("dreadLeggings", () -> new NecronomiconInfusionRitual("dreadLeggings", 2, k.dl, 500F, s(ACItems.dreaded_abyssalnite_leggings), o(ACItems.abyssalnite_leggings), dread.get()));
		reg("dreadBoots", () -> new NecronomiconInfusionRitual("dreadBoots", 2, k.dl, 500F, s(ACItems.dreaded_abyssalnite_boots), o(ACItems.abyssalnite_boots), dread.get()));

		// charms
		Supplier<Object[]> range = () -> fill(o(ACItems.shadow_fragment), Items.ARROW, o(ACItems.shadow_fragment), Items.ARROW, o(ACItems.shadow_fragment), Items.ARROW,
				o(ACItems.shadow_fragment), Items.ARROW);
		Supplier<Object[]> duration = () -> fill(o(ACItems.shadow_fragment), Items.REDSTONE, o(ACItems.shadow_fragment), Items.REDSTONE, o(ACItems.shadow_fragment),
				Items.REDSTONE, o(ACItems.shadow_fragment), Items.REDSTONE);
		Supplier<Object[]> power = () -> fill(o(ACItems.shadow_fragment), Items.GLOWSTONE_DUST, o(ACItems.shadow_fragment), Items.GLOWSTONE_DUST,
				o(ACItems.shadow_fragment), Items.GLOWSTONE_DUST, o(ACItems.shadow_fragment), Items.GLOWSTONE_DUST);
		reg("rangeCharm", () -> new NecronomiconInfusionRitual("rangeCharm", 0, 100F, s(ACItems.ritual_charm_range), o(ACItems.ritual_charm), range.get()));
		reg("durationCharm", () -> new NecronomiconInfusionRitual("durationCharm", 0, 100F, s(ACItems.ritual_charm_duration), o(ACItems.ritual_charm), duration.get()));
		reg("powerCharm", () -> new NecronomiconInfusionRitual("powerCharm", 0, 100F, s(ACItems.ritual_charm_power), o(ACItems.ritual_charm), power.get()));
		// deity charms: letter, base charm, range, duration, power, engraved coin
		Object[][] deities = {
				{"c", "cthulhu", ACItems.cthulhu_charm, ACItems.cthulhu_charm_range, ACItems.cthulhu_charm_duration, ACItems.cthulhu_charm_power, ACItems.cthulhu_engraved_coin},
				{"h", "hastur", ACItems.hastur_charm, ACItems.hastur_charm_range, ACItems.hastur_charm_duration, ACItems.hastur_charm_power, ACItems.hastur_engraved_coin},
				{"j", "jzahar", ACItems.jzahar_charm, ACItems.jzahar_charm_range, ACItems.jzahar_charm_duration, ACItems.jzahar_charm_power, ACItems.jzahar_engraved_coin},
				{"a", "azathoth", ACItems.azathoth_charm, ACItems.azathoth_charm_range, ACItems.azathoth_charm_duration, ACItems.azathoth_charm_power, ACItems.azathoth_engraved_coin},
				{"n", "nyarlathotep", ACItems.nyarlathotep_charm, ACItems.nyarlathotep_charm_range, ACItems.nyarlathotep_charm_duration, ACItems.nyarlathotep_charm_power,
					ACItems.nyarlathotep_engraved_coin},
				{"y", "yogsothoth", ACItems.yog_sothoth_charm, ACItems.yog_sothoth_charm_range, ACItems.yog_sothoth_charm_duration, ACItems.yog_sothoth_charm_power,
						ACItems.yog_sothoth_engraved_coin},
				{"s", "shubniggurath", ACItems.shub_niggurath_charm, ACItems.shub_niggurath_charm_range, ACItems.shub_niggurath_charm_duration,
							ACItems.shub_niggurath_charm_power, ACItems.shub_niggurath_engraved_coin}};
		for(Object[] d : deities) {
			String l = (String) d[0];
			RegistryObject<? extends ItemLike> base = cast(d[2]), coin = cast(d[6]);
			reg(l + "rangeCharm", () -> new NecronomiconInfusionRitual(l + "rangeCharm", 3, k.omt, 200F, s(cast(d[3])), o(base), range.get()));
			reg(l + "durationCharm", () -> new NecronomiconInfusionRitual(l + "durationCharm", 3, k.omt, 200F, s(cast(d[4])), o(base), duration.get()));
			reg(l + "powerCharm", () -> new NecronomiconInfusionRitual(l + "powerCharm", 3, k.omt, 200F, s(cast(d[5])), o(base), power.get()));
			reg(d[1] + "Charm", () -> {
				ItemLike c = o(coin);
				return new NecronomiconInfusionRitual(d[1] + "Charm", 4, 2000F, s(base), o(ACItems.ritual_charm), fill(c, c, c, c, c, c, c, c));
			});
		}

		// tier upgrades of the PE blocks
		Supplier<Object[]> ow = () -> fill(o(ACItems.shadow_shard), Blocks.COBBLESTONE, o(ACItems.coralium_gem), o(ACBlocks.darkstone_cobblestone), o(ACItems.shadow_shard),
				Blocks.COBBLESTONE, o(ACItems.coralium_gem), o(ACBlocks.darkstone_cobblestone));
		Supplier<Object[]> aw = () -> fill(o(ACItems.shadow_shard), o(ACBlocks.abyssal_stone_bricks), o(ACItems.coralium_gem), o(ACBlocks.coralium_stone_bricks),
				o(ACItems.shadow_shard), o(ACBlocks.abyssal_stone_bricks), o(ACItems.coralium_gem), o(ACBlocks.coralium_stone_bricks));
		Supplier<Object[]> dl = () -> fill(o(ACItems.shadow_shard), o(ACBlocks.dreadstone_bricks), o(ACItems.coralium_gem), o(ACBlocks.abyssalnite_stone_bricks),
				o(ACItems.shadow_shard), o(ACBlocks.dreadstone_bricks), o(ACItems.coralium_gem), o(ACBlocks.abyssalnite_stone_bricks));
		Supplier<Object[]> omt = () -> fill(o(ACItems.shadow_shard), o(ACBlocks.ethaxium_bricks), o(ACItems.coralium_gem), o(ACBlocks.dark_ethaxium_bricks),
				o(ACItems.shadow_shard), o(ACBlocks.ethaxium_bricks), o(ACItems.coralium_gem), o(ACBlocks.dark_ethaxium_bricks));
		List<Supplier<Object[]>> tiers = List.of(ow, aw, dl, omt);
		String[] tierNames = {"OW", "AW", "DL", "OMT"};
		Object[][] upgrades = {
				{"ep", BlockHandler.ENERGY_PEDESTAL, BlockHandler.TIERED_ENERGY_PEDESTALS, new String[] {"PotEnergy"}},
				{"sa", BlockHandler.SACRIFICIAL_ALTAR, BlockHandler.TIERED_SACRIFICIAL_ALTARS, new String[] {"PotEnergy", "CollectionLimit", "CoolDown"}},
				{"ecol", BlockHandler.ENERGY_COLLECTOR, BlockHandler.TIERED_ENERGY_COLLECTORS, new String[] {"PotEnergy"}},
				{"er", BlockHandler.ENERGY_RELAY, BlockHandler.TIERED_ENERGY_RELAYS, new String[] {"PotEnergy"}},
				{"econ", BlockHandler.ENERGY_CONTAINER, BlockHandler.TIERED_ENERGY_CONTAINERS, new String[] {"PotEnergy"}}};
		for(Object[] u : upgrades) {
			RegistryObject<? extends ItemLike> base = cast(u[1]);
			List<RegistryObject<? extends ItemLike>> tiered = cast(u[2]);
			String[] tags = (String[]) u[3];
			for(int i = 0; i < 4; i++) {
				int tier = i;
				String name = u[0] + tierNames[i] + "upgrade";
				reg(name, () -> new NecronomiconInfusionRitual(name, tier, 400F * (tier + 1), s(tiered.get(tier)), o(tier == 0 ? base : tiered.get(tier - 1)),
						tiers.get(tier).get()).setTags(tags));
			}
		}

		reg("weather", NecronomiconWeatherRitual::new);
		reg("house", com.shinoow.abyssalcraft.common.ritual.NecronomiconHouseRitual::new);
		reg("energyContainer", () -> {
			ItemLike collector = o(ACBlocks.energy_collector), shard = o(ACItems.shadow_shard);
			return new NecronomiconInfusionRitual("energyContainer", 0, 100F, s(ACBlocks.energy_container), o(ACBlocks.energy_pedestal),
					fill(collector, shard, collector, shard, collector, shard, collector, shard)).setTags("PotEnergy");
		});

		// staffs (the collected energies and the enchantments, 1.12.2 "ench", go along)
		String[] staffTags = {"energyShadow", "energyAbyssal", "energyDread", "energyOmothol", "Enchantments"};
		reg("jzaharStaff", () -> {
			ItemLike scale = o(ACItems.eldritch_scale), ethaxium = o(ACItems.ethaxium_ingot);
			return new NecronomiconInfusionRitual("jzaharStaff", 4, k.omt, 15000F, s(ACItems.staff_of_the_gatekeeper), o(ItemHandler.OMOTHOL_STAFF_OF_RENDING),
					fill(o(ACItems.dreadlands_essence), o(ACItems.omothol_essence), scale, ethaxium, o(ACItems.rlyehian_gateway_key), ethaxium, scale,
							o(ACItems.abyssal_wasteland_essence))).setTags(staffTags);
		});
		reg("sorAWupgrade", () -> {
			ItemLike stone = o(ACBlocks.abyssal_stone), plagued = o(ACItems.coralium_plagued_flesh);
			return new NecronomiconInfusionRitual("sorAWupgrade", 1, k.aw, 1000F, s(ItemHandler.ABYSSAL_WASTELAND_STAFF_OF_RENDING), o(ACItems.staff_of_rending),
					fill(o(ACItems.shadow_gem), stone, plagued, stone, plagued, stone, plagued, stone)).setTags(staffTags);
		});
		reg("sorDLupgrade", () -> {
			ItemLike stone = o(ACBlocks.dreadstone), fragment = o(ACItems.dread_fragment);
			return new NecronomiconInfusionRitual("sorDLupgrade", 2, k.dl, 2000F, s(ItemHandler.DREADLANDS_STAFF_OF_RENDING), o(ItemHandler.ABYSSAL_WASTELAND_STAFF_OF_RENDING),
					fill(o(ACItems.shadow_gem), stone, fragment, stone, fragment, stone, fragment, stone)).setTags(staffTags);
		});
		reg("sorOMTupgrade", () -> {
			ItemLike stone = o(ACBlocks.omothol_stone), omotholFlesh = o(ACItems.omothol_flesh);
			return new NecronomiconInfusionRitual("sorOMTupgrade", 3, k.omt, 3000F, s(ItemHandler.OMOTHOL_STAFF_OF_RENDING), o(ItemHandler.DREADLANDS_STAFF_OF_RENDING),
					fill(o(ACItems.shadow_gem), stone, omotholFlesh, stone, omotholFlesh, stone, omotholFlesh, stone)).setTags(staffTags);
		});

		// scrolls
		reg("basicScroll", () -> new NecronomiconCreationRitual("basicScroll", 0, 100.0F, s(ACItems.basic_scroll), fill(Items.BOOK, null, Items.BOOK, null, Items.BOOK, null, Items.BOOK)));
		reg("lesserScroll", () -> new NecronomiconCreationRitual("lesserScroll", 1, 1000F, s(ACItems.lesser_scroll),
				fill(Items.BOOK, null, o(ACBlocks.wastelands_thorn), null, Items.BOOK, null, o(ACBlocks.luminous_thistle))));
		reg("moderateScroll", () -> new NecronomiconCreationRitual("moderateScroll", 2, 2000F, s(ACItems.moderate_scroll),
				fill(Items.BOOK, null, o(ACItems.dread_fragment), null, Items.BOOK, null, o(ACItems.dreaded_shard_of_abyssalnite))));

		AbyssalCraft.LOGGER.info("Registered {} rituals, {} waiting for content that isn't ported yet: {}", RitualRegistry.instance().getRituals().size(),
				skipped.size(), skipped);
	}

	@SuppressWarnings("unchecked")
	private static <T> T cast(Object o) {
		return (T) o;
	}

	/** Dimension keys used by the rituals */
	private static class ResourceKeys {
		final net.minecraft.resources.ResourceKey<Level> aw = ACDimensions.ABYSSAL_WASTELAND, dl = ACDimensions.DREADLANDS, omt = ACDimensions.OMOTHOL;
	}
}
