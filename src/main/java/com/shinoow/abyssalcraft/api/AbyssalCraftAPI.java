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
 ******************************************************************************/
package com.shinoow.abyssalcraft.api;

import java.util.*;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.event.FuelBurnTimeEvent;
import com.shinoow.abyssalcraft.api.internal.*;
import com.shinoow.abyssalcraft.api.item.ItemEngraving;
import com.shinoow.abyssalcraft.api.item.ItemUpgradeKit;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.api.recipe.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Main API class for AbyssalCraft, has child references to most other API classes.
 * Contains various methods for interacting with the mod.
 *
 * @author shinoow
 *
 */
public class AbyssalCraftAPI {

	public static final String API_VERSION = "1.31.0-port";

	/** Damage types (data driven, see data/abyssalcraft/damage_type) */
	public static final ResourceKey<DamageType> coralium = damageType("coralium");
	public static final ResourceKey<DamageType> dread = damageType("dread");
	public static final ResourceKey<DamageType> antimatter = damageType("antimatter");
	public static final ResourceKey<DamageType> shadow = damageType("shadow");
	public static final ResourceKey<DamageType> acid = damageType("shoggoth_acid");
	/** The extra armor and protection ignoring damage AbyssalCraft mobs deal to players in Hardcore Mode */
	public static final ResourceKey<DamageType> hardcore_attack = damageType("hardcore_attack");

	/** AbyssalCraft effects (registered by the mod) */
	public static final RegistryObject<MobEffect> coralium_plague = RegistryObject.create(new ResourceLocation("abyssalcraft", "coralium_plague"), ForgeRegistries.MOB_EFFECTS);
	public static final RegistryObject<MobEffect> dread_plague = RegistryObject.create(new ResourceLocation("abyssalcraft", "dread_plague"), ForgeRegistries.MOB_EFFECTS);
	public static final RegistryObject<MobEffect> antimatter_potion = RegistryObject.create(new ResourceLocation("abyssalcraft", "antimatter"), ForgeRegistries.MOB_EFFECTS);

	/** AbyssalCraft enchantments (registered by the mod) */
	public static final RegistryObject<Enchantment> coralium_enchantment = RegistryObject.create(new ResourceLocation("abyssalcraft", "coralium"), ForgeRegistries.ENCHANTMENTS);
	public static final RegistryObject<Enchantment> dread_enchantment = RegistryObject.create(new ResourceLocation("abyssalcraft", "dread"), ForgeRegistries.ENCHANTMENTS);
	public static final RegistryObject<Enchantment> light_pierce = RegistryObject.create(new ResourceLocation("abyssalcraft", "light_pierce"), ForgeRegistries.ENCHANTMENTS);
	public static final RegistryObject<Enchantment> iron_wall = RegistryObject.create(new ResourceLocation("abyssalcraft", "iron_wall"), ForgeRegistries.ENCHANTMENTS);
	public static final RegistryObject<Enchantment> sapping = RegistryObject.create(new ResourceLocation("abyssalcraft", "sapping"), ForgeRegistries.ENCHANTMENTS);
	public static final RegistryObject<Enchantment> multi_rend = RegistryObject.create(new ResourceLocation("abyssalcraft", "multi_rend"), ForgeRegistries.ENCHANTMENTS);
	/** Items Sapping and Multi-Rend go on */
	public static final EnchantmentCategory STAFF_OF_RENDING = EnchantmentCategory.create("ABYSSALCRAFT_STAFF_OF_RENDING",
			item -> item instanceof com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending);

	/** Shadow creature attribute (used by shadow mobs, Light Pierce enchantment etc.) */
	public static final MobType SHADOW = new MobType();

	private static final List<ItemStack> crystals = new ArrayList<>();

	private static final Map<NecroData, Integer> necroData = new HashMap<>();

	private static final Map<Item, ResourceLocation> ghoul_helmet = new HashMap<>();
	private static final Map<Item, ResourceLocation> ghoul_chestplate = new HashMap<>();
	private static final Map<Item, ResourceLocation> ghoul_leggings = new HashMap<>();
	private static final Map<Item, ResourceLocation> ghoul_boots = new HashMap<>();

	private static final Map<ResourceKey<Level>, Integer> gateway_key_overrides = new HashMap<>();

	private static IInternalNecroDataHandler internalNDHandler = new DummyNecroDataHandler();

	private static IInternalMethodHandler internalMethodHandler = new DummyMethodHandler();

	public static final Logger logger = LogManager.getLogger("AbyssalCraftAPI");

	private static ResourceKey<DamageType> damageType(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("abyssalcraft", name));
	}

	/**
	 * Creates a DamageSource of one of AbyssalCraft's damage types
	 * @param level Level the damage happens in
	 * @param type Damage type key (eg. {@link #coralium})
	 */
	public static DamageSource damage(Level level, ResourceKey<DamageType> type) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
	}

	/**
	 * Creates a DamageSource of one of AbyssalCraft's damage types, caused by an entity
	 */
	public static DamageSource damage(Level level, ResourceKey<DamageType> type, Entity attacker) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker);
	}

	private static boolean isAbyssalCraftLoading() {
		return "abyssalcraft".equals(ModLoadingContext.get().getActiveNamespace());
	}

	/**
	 * Internal Necronomicon data handler, only set by AbyssalCraft
	 */
	public static void setInternalNDHandler(IInternalNecroDataHandler handler){
		if(internalNDHandler instanceof DummyNecroDataHandler && isAbyssalCraftLoading())
			internalNDHandler = handler;
	}

	/**
	 * Internal method handler, only set by AbyssalCraft
	 */
	public static void setInternalMethodHandler(IInternalMethodHandler handler){
		if(internalMethodHandler instanceof DummyMethodHandler && isAbyssalCraftLoading())
			internalMethodHandler = handler;
	}

	public static IInternalNecroDataHandler getInternalNDHandler(){
		return internalNDHandler;
	}

	public static IInternalMethodHandler getInternalMethodHandler(){
		return internalMethodHandler;
	}

	/**
	 * Adds a crystallization recipe
	 * @param input The input
	 * @param output1 The first output
	 * @param output2 The second output (can be empty)
	 * @param xp Amount of exp given
	 */
	public static void addCrystallization(Block input, ItemStack output1, ItemStack output2, float xp){
		CrystallizerRecipes.instance().crystallize(Ingredient.of(input), output1, output2, xp);
	}

	public static void addCrystallization(Item input, ItemStack output1, ItemStack output2, float xp){
		CrystallizerRecipes.instance().crystallize(Ingredient.of(input), output1, output2, xp);
	}

	public static void addCrystallization(ItemStack input, ItemStack output1, ItemStack output2, float xp){
		CrystallizerRecipes.instance().crystallize(Ingredient.of(input), output1, output2, xp);
	}

	/**
	 * Adds a crystallization recipe with a tag input (the tag is resolved lazily, so this is safe before tags load)
	 */
	public static void addCrystallization(TagKey<Item> input, Supplier<ItemStack> output1, Supplier<ItemStack> output2, float xp){
		CrystallizerRecipes.instance().crystallize(Ingredient.of(input), output1, output2, xp);
	}

	public static void addSingleCrystallization(Block input, ItemStack output, float xp){
		addCrystallization(input, output, ItemStack.EMPTY, xp);
	}

	public static void addSingleCrystallization(Item input, ItemStack output, float xp){
		addCrystallization(input, output, ItemStack.EMPTY, xp);
	}

	public static void addSingleCrystallization(ItemStack input, ItemStack output, float xp){
		addCrystallization(input, output, ItemStack.EMPTY, xp);
	}

	public static void addSingleCrystallization(TagKey<Item> input, Supplier<ItemStack> output, float xp){
		addCrystallization(input, output, () -> ItemStack.EMPTY, xp);
	}

	/**
	 * Adds a transmutation recipe
	 * @param input The input
	 * @param output The output
	 * @param xp Amount of exp given
	 */
	public static void addTransmutation(Block input, ItemStack output, float xp){
		TransmutatorRecipes.instance().transmute(Ingredient.of(input), output, xp);
	}

	public static void addTransmutation(Item input, ItemStack output, float xp){
		TransmutatorRecipes.instance().transmute(Ingredient.of(input), output, xp);
	}

	public static void addTransmutation(ItemStack input, ItemStack output, float xp){
		TransmutatorRecipes.instance().transmute(Ingredient.of(input), output, xp);
	}

	/**
	 * Adds a transmutation recipe with a tag input and a lazily resolved output
	 */
	public static void addTransmutation(TagKey<Item> input, Supplier<ItemStack> output, float xp){
		TransmutatorRecipes.instance().transmute(Ingredient.of(input), output, xp);
	}

	/**
	 * Resolves the first item in a tag (used for tag based recipe outputs, the replacement for OreDictionary outputs)
	 * @param tag Item tag
	 * @param count Stack size
	 * @return The first item of the tag, or an empty stack if the tag is empty (or not loaded yet)
	 */
	public static ItemStack firstOf(TagKey<Item> tag, int count){
		ItemStack[] stacks = Ingredient.of(tag).getItems();
		if(stacks.length == 0) return ItemStack.EMPTY;
		ItemStack stack = stacks[0].copy();
		stack.setCount(count);
		return stack;
	}

	/**
	 * Adds a coin that can be engraved
	 */
	public static void addCoin(ItemStack coin){
		if(EngraverRecipes.instance().getCoinList().stream().noneMatch(c -> ItemStack.isSameItem(c, coin)))
			EngraverRecipes.instance().addCoin(coin);
		else logger.error("This Coin is already registered!");
	}

	public static void addCoin(Item coin){
		addCoin(new ItemStack(coin));
	}

	/**
	 * Adds an engraving recipe
	 * @param coin The resulting coin
	 * @param engraving The engraving template
	 * @param xp Amount of exp given
	 */
	public static void addEngraving(ItemStack coin, ItemEngraving engraving, float xp){
		if(!EngraverRecipes.instance().getEngravings().containsKey(engraving) &&
				EngraverRecipes.instance().getEngravings().values().stream().noneMatch(c -> ItemStack.isSameItem(c, coin)))
			EngraverRecipes.instance().addEngraving(coin, engraving, xp);
		else logger.error("This Engraving Template and/or Engraved Coin is already registered!");
	}

	public static void addEngraving(Item coin, ItemEngraving engraving, float xp){
		addEngraving(new ItemStack(coin), engraving, xp);
	}

	/**
	 * Adds a Materializer recipe
	 * @param output The output
	 * @param input Up to 5 Crystals
	 */
	public static void addMaterialization(ItemStack output, ItemStack...input){
		for(ItemStack item : input)
			if(!APIUtils.isCrystal(item)) throw new ClassCastException("All of the input items has to be Crystals!");
		if(input != null && input.length > 0)
			if(input.length <= 5)
				MaterializerRecipes.instance().materialize(input, output);
			else logger.error("This Materializer recipe has more than 5 inputs! ({})", input.length);
		else logger.error("This Materializer recipe has no inputs!");
	}

	public static void addMaterialization(Materialization materialization){
		MaterializerRecipes.instance().materialize(materialization);
	}

	public enum FuelType{
		CRYSTALLIZER, TRANSMUTATOR, FURNACE
	}

	/**
	 * Fetches the burn time of an ItemStack for the specified fuel type
	 */
	public static int getFuelValue(ItemStack itemStack, FuelType type){
		if(type == FuelType.FURNACE)
			return ForgeHooks.getBurnTime(itemStack, null);
		else {
			FuelBurnTimeEvent event = new FuelBurnTimeEvent(itemStack, type);
			MinecraftForge.EVENT_BUS.post(event);
			return event.getBurnTime();
		}
	}

	/**
	 * Registers an ItemStack as a crystal (useable in the Materializer, Crystal Bags etc.)
	 */
	public static void addCrystal(ItemStack crystal){
		crystals.add(crystal);
	}

	public static List<ItemStack> getCrystals(){
		return crystals;
	}

	/**
	 * Registers Necronomicon data (a page group) for a book type
	 * @param data The NecroData
	 * @param bookType 0 = Necronomicon, 1 = Abyssal Wasteland, 2 = Dreadlands, 3 = Omothol, 4 = Abyssalnomicon
	 */
	public static void registerNecronomiconData(NecroData data, int bookType){
		if(bookType <= 4 && bookType >= 0)
			necroData.put(data, bookType);
		else logger.error("Necronomicon book type does not exist: {}", bookType);
	}

	public static Map<NecroData,Integer> getNecronomiconData(){
		return necroData;
	}

	/**
	 * Ghoul armor textures for armor not rendered on the ghoul model by default
	 */
	public static void addGhoulHelmetTexture(Item helmet, ResourceLocation res){
		if(helmet == null || res == null) return;
		ghoul_helmet.put(helmet, res);
	}

	public static void addGhoulChestplateTexture(Item chestplate, ResourceLocation res){
		if(chestplate == null || res == null) return;
		ghoul_chestplate.put(chestplate, res);
	}

	public static void addGhoulLeggingsTexture(Item leggings, ResourceLocation res){
		if(leggings == null || res == null) return;
		ghoul_leggings.put(leggings, res);
	}

	public static void addGhoulBootsTexture(Item boots, ResourceLocation res){
		if(boots == null || res == null) return;
		ghoul_boots.put(boots, res);
	}

	public static void addGhoulArmorTextures(Item helmet, Item chestplate, Item leggings, Item boots, ResourceLocation res1, ResourceLocation res2){
		addGhoulHelmetTexture(helmet, res1);
		addGhoulChestplateTexture(chestplate, res1);
		addGhoulLeggingsTexture(leggings, res2);
		addGhoulBootsTexture(boots, res1);
	}

	public static ResourceLocation getGhoulHelmetTexture(Item helmet){
		return ghoul_helmet.get(helmet);
	}

	public static ResourceLocation getGhoulChestplateTexture(Item chestplate){
		return ghoul_chestplate.get(chestplate);
	}

	public static ResourceLocation getGhoulLeggingsTexture(Item leggings){
		return ghoul_leggings.get(leggings);
	}

	public static ResourceLocation getGhoulBootsTexture(Item boots){
		return ghoul_boots.get(boots);
	}

	/**
	 * Allows a dimension from another mod to behave as one of the AbyssalCraft dimensions for the Gateway Keys
	 * @param dim Dimension key
	 * @param type 0 = Abyssal Wasteland, 1 = Dreadlands, 2 = Omothol
	 */
	public static void addGatewayKeyOverride(ResourceKey<Level> dim, int type){
		gateway_key_overrides.put(dim, type);
	}

	/**
	 * Fetches the Gateway Key override for a dimension
	 * @return The override type, or -1 if none is registered
	 */
	public static int getGatewayKeyOverride(ResourceKey<Level> dim){
		return gateway_key_overrides.getOrDefault(dim, -1);
	}

	/**
	 * Adds an Upgrade Kit recipe
	 * @param kit The kit
	 * @param input Item to upgrade
	 * @param output Upgraded item
	 */
	public static void addUpgrade(ItemUpgradeKit kit, ItemStack input, ItemStack output){
		UpgradeKitRecipes.instance().addUpgrade(kit, input, output);
	}

	public static void addUpgradeKit(ItemUpgradeKit kit){
		UpgradeKitRecipes.instance().addUpgradeKit(kit);
	}

	/**
	 * Entity ids (registry paths in the abyssalcraft namespace) of the mod's mobs
	 */
	public static class ACEntities {
		public static final String depths_ghoul = "depths_ghoul";
		public static final String evil_pig = "evil_pig";
		public static final String abyssal_zombie = "abyssal_zombie";
		public static final String jzahar = "jzahar";
		public static final String abyssalnite_golem = "abyssalnite_golem";
		public static final String dreaded_abyssalnite_golem = "dreaded_abyssalnite_golem";
		public static final String dreadguard = "dreadguard";
		public static final String spectral_dragon = "spectral_dragon";
		public static final String asorah = "asorah";
		public static final String shadow_creature = "shadow_creature";
		public static final String shadow_monster = "shadow_monster";
		public static final String dreadling = "dreadling";
		public static final String dread_spawn = "dread_spawn";
		public static final String demon_pig = "demon_pig";
		public static final String skeleton_goliath = "skeleton_goliath";
		public static final String spawn_of_chagaroth = "spawn_of_chagaroth";
		public static final String fist_of_chagaroth = "fist_of_chagaroth";
		public static final String chagaroth = "chagaroth";
		public static final String shadow_beast = "shadow_beast";
		public static final String sacthoth = "sacthoth";
		public static final String abyssal_anti_zombie = "abyssal_anti_zombie";
		public static final String anti_bat = "anti_bat";
		public static final String anti_chicken = "anti_chicken";
		public static final String anti_cow = "anti_cow";
		public static final String anti_creeper = "anti_creeper";
		public static final String anti_ghoul = "anti_ghoul";
		public static final String anti_pig = "anti_pig";
		public static final String anti_player = "anti_player";
		public static final String anti_skeleton = "anti_skeleton";
		public static final String anti_spider = "anti_spider";
		public static final String anti_zombie = "anti_zombie";
		public static final String lesser_shoggoth = "lesser_shoggoth";
		public static final String minion_of_the_gatekeeper = "minion_of_the_gatekeeper";
		public static final String omothol_ghoul = "omothol_ghoul";
		public static final String remnant = "remnant";
		public static final String greater_dread_spawn = "greater_dread_spawn";
		public static final String lesser_dreadbeast = "lesser_dreadbeast";
		public static final String evil_cow = "evil_cow";
		public static final String evil_chicken = "evil_chicken";
		public static final String demon_cow = "demon_cow";
		public static final String demon_chicken = "demon_chicken";
		public static final String evil_sheep = "evil_sheep";
		public static final String demon_sheep = "demon_sheep";
		public static final String coralium_squid = "coralium_squid";
		public static final String shub_offspring = "shub_offspring";
	}
}
