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
 *     1.20.1 port - OreDictionary names replaced by Forge tags, crafting/smelting moved to data generation
 ******************************************************************************/
package com.shinoow.abyssalcraft.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionPotion;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionSpawn;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionSwarm;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.item.ItemEngraving;
import com.shinoow.abyssalcraft.api.item.ItemUpgradeKit;
import com.shinoow.abyssalcraft.api.recipe.CrystallizerRecipes;
import com.shinoow.abyssalcraft.api.recipe.MaterializerRecipes;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.api.rending.Rending;
import com.shinoow.abyssalcraft.api.rending.RendingRegistry;
import com.shinoow.abyssalcraft.api.spell.SpellRegistry;
import com.shinoow.abyssalcraft.common.disruptions.*;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers the AbyssalCraft machine recipes (Crystallizer, Transmutator, Engraver, Materializer).<br>
 * Crafting and smelting recipes are data generated (see the data package).
 */
public class AbyssalCrafting {

	// crystal indices (1.12.2 crystal metadata)
	public static final int IRON = 0, GOLD = 1, SULFUR = 2, CARBON = 3, OXYGEN = 4, HYDROGEN = 5, NITROGEN = 6, PHOSPHORUS = 7,
			POTASSIUM = 8, NITRATE = 9, METHANE = 10, REDSTONE = 11, ABYSSALNITE = 12, CORALIUM = 13, DREADIUM = 14, BLAZE = 15,
			TIN = 16, COPPER = 17, SILICON = 18, MAGNESIUM = 19, ALUMINIUM = 20, SILICA = 21, ALUMINA = 22, MAGNESIA = 23,
			ZINC = 24, CALCIUM = 25, BERYLLIUM = 26, BERYL = 27;

	/** Materializations whose output is "any item in a tag" (1.12.2 OreDictionary outputs), expanded when tags load */
	private static final List<TagMaterialization> TAG_MATERIALIZATIONS = new ArrayList<>();

	private record TagMaterialization(TagKey<Item> output, ItemStack[] input) {}

	public static void addRecipes() {
		addCrystallization();
		addTransmutation();
		addEngraving();
		addMaterialization();
		addDisruptions();
		addRendings();
		addSpells();
		addUpgrades();
		RitualRecipes.addRitualRecipes();
	}

	/**
	 * Upgrade Kit anvil upgrades (1.12.2 addItemCrafting / addArmor). Always registered; the anvil only uses them while
	 * "upgradeKits" is on.
	 */
	private static void addUpgrades() {
		String[] tools = {"pickaxe", "axe", "shovel", "sword", "hoe"}, armor = {"helmet", "chestplate", "leggings", "boots"};
		String[][] tiers = {{"cobblestone", "minecraft:wooden", "minecraft:stone"}, {"iron", "minecraft:stone", "minecraft:iron"},
				{"gold", "minecraft:iron", "minecraft:golden"}, {"diamond", "minecraft:golden", "minecraft:diamond"},
				{"abyssalnite", "minecraft:diamond", "abyssalcraft:abyssalnite"}, {"coralium", "abyssalcraft:abyssalnite", "abyssalcraft:refined_coralium"},
				{"dreadium", "abyssalcraft:refined_coralium", "abyssalcraft:dreadium"}, {"ethaxium", "abyssalcraft:dreadium", "abyssalcraft:ethaxium"}};
		for(String[] tier : tiers) {
			ItemUpgradeKit kit = (ItemUpgradeKit) ForgeRegistries.ITEMS.getValue(AbyssalCraft.loc(tier[0] + "_upgrade_kit"));
			for(String tool : tools)
				upgrade(kit, tier[1] + "_" + tool, tier[2] + "_" + tool);
			// armor: iron -> gold and up (1.12.2 had no leather/chain kits)
			if(!tier[1].endsWith("wooden") && !tier[1].endsWith("stone"))
				for(String piece : armor)
					upgrade(kit, tier[1] + "_" + piece, tier[2] + "_" + piece);
		}
	}

	private static void upgrade(ItemUpgradeKit kit, String from, String to) {
		Item in = ForgeRegistries.ITEMS.getValue(new ResourceLocation(from)), out = ForgeRegistries.ITEMS.getValue(new ResourceLocation(to));
		if(in == null || in == Items.AIR || out == null || out == Items.AIR)
			throw new IllegalStateException("Upgrade " + from + " -> " + to);
		AbyssalCraftAPI.addUpgrade(kit, new ItemStack(in), new ItemStack(out));
	}

	/** The 13 spells (each can be turned off in the config) and the scrolls they can be inscribed on */
	private static void addSpells() {
		for(var scroll : List.of(ACItems.basic_scroll, ACItems.lesser_scroll, ACItems.moderate_scroll, ACItems.greater_scroll, ACItems.antimatter_scroll, ACItems.oblivion_scroll))
			SpellRegistry.instance().addScroll(scroll.get());
		spell(ACConfig.entropy_spell, com.shinoow.abyssalcraft.common.spells.EntropySpell::new);
		spell(ACConfig.life_drain_spell, com.shinoow.abyssalcraft.common.spells.LifeDrainSpell::new);
		spell(ACConfig.mining_spell, com.shinoow.abyssalcraft.common.spells.MiningSpell::new);
		spell(ACConfig.grasp_of_cthulhu_spell, com.shinoow.abyssalcraft.common.spells.GraspofCthulhuSpell::new);
		spell(ACConfig.invisibility_spell, com.shinoow.abyssalcraft.common.spells.InvisibilitySpell::new);
		spell(ACConfig.detachment_spell, com.shinoow.abyssalcraft.common.spells.DetachmentSpell::new);
		spell(ACConfig.steal_vigor_spell, com.shinoow.abyssalcraft.common.spells.StealVigorSpell::new);
		spell(ACConfig.sirens_song_spell, com.shinoow.abyssalcraft.common.spells.SirensSongSpell::new);
		spell(ACConfig.undeath_to_dust_spell, com.shinoow.abyssalcraft.common.spells.UndeathtoDustSpell::new);
		spell(ACConfig.ooze_removal_spell, com.shinoow.abyssalcraft.common.spells.OozeRemovalSpell::new);
		spell(ACConfig.teleport_hostile_spell, com.shinoow.abyssalcraft.common.spells.TeleportHostilesSpell::new);
		spell(ACConfig.floating_spell, com.shinoow.abyssalcraft.common.spells.FloatingSpell::new);
		spell(ACConfig.teleport_home_spell, com.shinoow.abyssalcraft.common.spells.TeleportHomeSpell::new);
	}

	private static void spell(boolean enabled, Supplier<com.shinoow.abyssalcraft.api.spell.Spell> factory) {
		var spell = factory.get();
		if(enabled && SpellRegistry.instance().getSpell(spell.getUnlocalizedName()) == null)
			SpellRegistry.instance().registerSpell(spell);
	}

	/** Staff of Rending targets (bosses excluded, like 1.12.2's isNonBoss check) */
	private static void addRendings() {
		RendingRegistry.instance().registerRending(new Rending("Abyssal", 100, st(ACItems.abyssal_wasteland_essence, 1),
				e -> e.level().dimension() == ACDimensions.ABYSSAL_WASTELAND && EntityUtil.isCoraliumPlagueCarrier(e) && e.canChangeDimensions(),
				"ac.rending.essence_aw", ACDimensions.ABYSSAL_WASTELAND));
		RendingRegistry.instance().registerRending(new Rending("Dread", 100, st(ACItems.dreadlands_essence, 1),
				e -> e.level().dimension() == ACDimensions.DREADLANDS && EntityUtil.isDreadPlagueCarrier(e) && e.canChangeDimensions(),
				"ac.rending.essence_dl", ACDimensions.DREADLANDS));
		RendingRegistry.instance().registerRending(new Rending("Omothol", 100, st(ACItems.omothol_essence, 1),
				e -> e.level().dimension() == ACDimensions.OMOTHOL && e instanceof IOmotholEntity && e.getMobType() != AbyssalCraftAPI.SHADOW
				&& e.canChangeDimensions(), "ac.rending.essence_omt", ACDimensions.OMOTHOL));
		RendingRegistry.instance().registerRending(new Rending("Shadow", 200, st(ACItems.shadow_gem, 1),
				e -> e.getMobType() == AbyssalCraftAPI.SHADOW && e.canChangeDimensions(), "ac.rending.shadowgem", null));
	}

	/**
	 * Lazy lookup of an AbyssalCraft mob (null while it isn't registered, the disruption then spawns nothing)
	 */
	@SuppressWarnings("unchecked")
	private static Supplier<EntityType<? extends Mob>> mob(String id) {
		return () -> {
			ResourceLocation key = new ResourceLocation(AbyssalCraft.modid, id);
			return ForgeRegistries.ENTITY_TYPES.containsKey(key) ? (EntityType<? extends Mob>) ForgeRegistries.ENTITY_TYPES.getValue(key) : null;
		};
	}

	private static void addDisruptions(){
		DisruptionHandler.instance().registerDisruption(new DisruptionLightning());
		DisruptionHandler.instance().registerDisruption(new DisruptionFire());
		DisruptionHandler.instance().registerDisruption(new DisruptionSpawn("spawnShoggoth", null, mob(ACEntities.lesser_shoggoth)));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotion("poisonPotion", null, () -> MobEffects.POISON));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotion("slownessPotion", null, () -> MobEffects.MOVEMENT_SLOWDOWN));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotion("weaknessPotion", null, () -> MobEffects.WEAKNESS));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotion("witherPotion", null, () -> MobEffects.WITHER));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotion("coraliumPotion", null, AbyssalCraftAPI.coralium_plague));
		DisruptionHandler.instance().registerDisruption(new DisruptionPotentialEnergy());
		DisruptionHandler.instance().registerDisruption(new DisruptionFreeze());
		DisruptionHandler.instance().registerDisruption(new DisruptionSwarm("swarmShadow", null, mob(ACEntities.shadow_creature), mob(ACEntities.shadow_monster),
				mob(ACEntities.shadow_beast)));
		DisruptionHandler.instance().registerDisruption(new DisruptionFireRain());
		DisruptionHandler.instance().registerDisruption(new DisruptionDisplaceEntities());
		DisruptionHandler.instance().registerDisruption(new DisruptionTeleportRandomly());
		DisruptionHandler.instance().registerDisruption(new DisruptionDrainNearbyPE());
		DisruptionHandler.instance().registerDisruption(new DisruptionSwarm("swarmSheep", DeityType.SHUBNIGGURATH, mob(ACEntities.evil_sheep), () -> EntityType.SHEEP));
		DisruptionHandler.instance().registerDisruption(new DisruptionAnimalCorruption());
		DisruptionHandler.instance().registerDisruption(new DisruptionOoze());
		DisruptionHandler.instance().registerDisruption(new DisruptionRandomSwarm());
		DisruptionHandler.instance().registerDisruption(new DisruptionRandomSpawn());
		DisruptionHandler.instance().registerDisruption(new DisruptionSpawn("spawnShubOffspring", DeityType.SHUBNIGGURATH, mob(ACEntities.shub_offspring)));
	}

	/**
	 * Stack of a registered AbyssalCraft item/block, or an empty stack (logged) if it isn't registered
	 */
	public static ItemStack st(RegistryObject<? extends ItemLike> obj, int count) {
		if(!obj.isPresent()) {
			AbyssalCraft.LOGGER.error("Recipe references unregistered {}, recipe skipped", obj.getId());
			return ItemStack.EMPTY;
		}
		return new ItemStack(obj.get(), count);
	}

	public static ItemStack crystal(int i, int count) {
		return new ItemStack(ACItems.crystal(i).get(), count);
	}

	public static ItemStack shard(int i, int count) {
		return new ItemStack(ACItems.crystalShard(i).get(), count);
	}

	public static ItemStack fragment(int i, int count) {
		return new ItemStack(ACItems.crystalFragment(i).get(), count);
	}

	public static ItemStack cluster(int i, int count) {
		return new ItemStack(ACBlocks.crystalCluster(i).get(), count);
	}

	public static TagKey<Item> forge(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

	private static Ingredient in(Object o) {
		if(o instanceof TagKey<?> tag) return Ingredient.of((TagKey<Item>) tag);
		if(o instanceof ItemStack stack) return Ingredient.of(stack);
		if(o instanceof Ingredient i) return i;
		if(o instanceof ItemLike item) return Ingredient.of(item);
		if(o instanceof RegistryObject<?> ro && !ro.isPresent()) {
			AbyssalCraft.LOGGER.error("Recipe references unregistered {}, recipe skipped", ro.getId());
			return null;
		}
		if(o instanceof java.util.function.Supplier<?> s) return in(s.get());
		throw new IllegalArgumentException(String.valueOf(o));
	}

	private static void cryst(Object input, ItemStack out1, ItemStack out2, float xp) {
		Ingredient in = in(input);
		if(in != null && !out1.isEmpty())
			CrystallizerRecipes.instance().crystallize(in, out1, out2, xp);
	}

	private static void cryst(Object input, ItemStack out, float xp) {
		cryst(input, out, ItemStack.EMPTY, xp);
	}

	private static void trans(Object input, ItemStack out, float xp) {
		Ingredient in = in(input);
		if(in != null && !out.isEmpty())
			TransmutatorRecipes.instance().transmute(in, out, xp);
	}

	/** Transmutation with a tag output (first item of the tag, resolved when used) */
	private static void trans(Object input, TagKey<Item> out, float xp) {
		Ingredient in = in(input);
		if(in != null)
			TransmutatorRecipes.instance().transmute(in, () -> AbyssalCraftAPI.firstOf(out, 1), xp);
	}

	private static void addCrystallization(){
		cryst(Items.BLAZE_POWDER, shard(BLAZE, 4), 0.1F);
		cryst(Items.BONE_MEAL, shard(PHOSPHORUS, 4), 0.0F);
		cryst(ACItems.dreaded_chunk_of_abyssalnite, shard(ABYSSALNITE, 4), shard(DREADIUM, 4), 0.2F);
		cryst(Items.WATER_BUCKET, shard(HYDROGEN, 12), shard(OXYGEN, 6), 0.1F);
		cryst(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), shard(HYDROGEN, 6), shard(OXYGEN, 3), 0.1F);
		cryst(ACItems.methane, shard(OXYGEN, 4), crystal(HYDROGEN, 16), 0.1F);
		cryst(Items.GUNPOWDER, shard(NITRATE, 16), shard(SULFUR, 4), 0.1F);
		cryst(Blocks.OBSIDIAN, shard(SILICA, 4), shard(MAGNESIA, 4), 0.1F);
		cryst(Blocks.STONE, shard(SILICA, 4), shard(MAGNESIA, 4), 0.1F);
		cryst(crystal(SILICA, 1), crystal(SILICON, 1), crystal(OXYGEN, 2), 0.1F);
		cryst(shard(SILICA, 1), shard(SILICON, 1), shard(OXYGEN, 2), 0.1F);
		cryst(crystal(ALUMINA, 1), crystal(ALUMINIUM, 2), crystal(OXYGEN, 3), 0.1F);
		cryst(shard(ALUMINA, 1), shard(ALUMINIUM, 2), shard(OXYGEN, 3), 0.1F);
		cryst(crystal(MAGNESIA, 1), crystal(MAGNESIUM, 1), crystal(OXYGEN, 1), 0.1F);
		cryst(shard(MAGNESIA, 1), shard(MAGNESIUM, 1), shard(OXYGEN, 1), 0.1F);
		cryst(crystal(METHANE, 1), crystal(CARBON, 1), crystal(HYDROGEN, 4), 0.1F);
		cryst(shard(METHANE, 1), shard(CARBON, 1), shard(HYDROGEN, 4), 0.1F);
		cryst(crystal(NITRATE, 1), crystal(NITROGEN, 1), crystal(OXYGEN, 3), 0.1F);
		cryst(shard(NITRATE, 1), shard(NITROGEN, 1), shard(OXYGEN, 3), 0.1F);
		cryst(Items.ROTTEN_FLESH, shard(PHOSPHORUS, 8), 0.1F);
		cryst(ACItems.shoggoth_flesh_overworld, shard(PHOSPHORUS, 8), 0.2F);
		cryst(ACItems.shoggoth_flesh_abyssal_wasteland, shard(PHOSPHORUS, 8), fragment(CORALIUM, 2), 0.2F);
		cryst(ACItems.shoggoth_flesh_dreadlands, shard(PHOSPHORUS, 8), fragment(DREADIUM, 2), 0.2F);
		cryst(ACItems.shoggoth_flesh_omothol, shard(PHOSPHORUS, 8), fragment(CARBON, 2), 0.2F);
		cryst(ACItems.shoggoth_flesh_dark_realm, shard(PHOSPHORUS, 8), st(ACItems.shadow_gem, 1), 0.2F);
		cryst(ACItems.coralium_plagued_flesh, shard(PHOSPHORUS, 8), fragment(CORALIUM, 1), 0.2F);
		cryst(ACItems.coralium_plagued_flesh_on_a_bone, shard(PHOSPHORUS, 12), fragment(CORALIUM, 1), 0.2F);
		cryst(ACItems.skin_of_the_abyssal_wasteland, shard(PHOSPHORUS, 8), st(ACItems.abyssal_wasteland_essence, 1), 0.2F);
		cryst(ACItems.skin_of_the_dreadlands, shard(PHOSPHORUS, 8), st(ACItems.dreadlands_essence, 1), 0.2F);
		cryst(ACItems.skin_of_omothol, shard(PHOSPHORUS, 8), st(ACItems.omothol_essence, 1), 0.2F);
		cryst(ACItems.dreaded_shard_of_abyssalnite, shard(ABYSSALNITE, 1), shard(DREADIUM, 4), 0.2F);
		cryst(Items.BONE, shard(CALCIUM, 4), 0.2F);
		cryst(Items.PRISMARINE_SHARD, shard(SILICA, 4), shard(BERYL, 4), 0.1F);
		cryst(Items.PRISMARINE_CRYSTALS, shard(SILICA, 4), shard(BERYL, 4), 0.1F);
		cryst(Blocks.PRISMARINE, shard(SILICA, 16), shard(BERYL, 16), 0.1F);
		cryst(Blocks.PRISMARINE_BRICKS, crystal(SILICA, 4), crystal(BERYL, 4), 0.1F);
		cryst(Blocks.DARK_PRISMARINE, shard(SILICA, 32), shard(BERYL, 32), 0.1F);
		cryst(Items.EGG, shard(CALCIUM, 4), shard(PHOSPHORUS, 4), 0.1F);
		cryst(ACItems.charcoal, shard(CARBON, 4), shard(DREADIUM, 4), 0.1F);
		if(ACConfig.crystal_rework) {
			cryst(ACItems.refined_coralium_ingot, crystal(CORALIUM, 1), 0.1F);
			cryst(ACItems.chunk_of_coralium, crystal(CORALIUM, 1), 0.1F);
			cryst(ACBlocks.liquified_coralium_ore, crystal(CORALIUM, 1), 0.1F);
			cryst(ACItems.abyssalnite_ingot, crystal(ABYSSALNITE, 1), 0.1F);
			cryst(ACItems.chunk_of_abyssalnite, crystal(ABYSSALNITE, 1), 0.1F);
			cryst(ACItems.dreadium_ingot, crystal(DREADIUM, 1), 0.1F);
			cryst(Items.IRON_INGOT, crystal(IRON, 1), 0.1F);
			cryst(Items.GOLD_INGOT, crystal(GOLD, 1), 0.1F);
			cryst(Items.REDSTONE, crystal(REDSTONE, 1), 0.1F);
			cryst(ItemTags.COALS, crystal(CARBON, 1), 0.1F);
			cryst(forge("ores/abyssalnite"), crystal(ABYSSALNITE, 2), 0.1F);
			cryst(forge("ores/coralium"), crystal(CORALIUM, 2), 0.1F);
			cryst(forge("ores/iron"), crystal(IRON, 2), 0.1F);
			cryst(forge("ores/gold"), crystal(GOLD, 2), 0.1F);
			cryst(forge("ingots/tin"), crystal(TIN, 1), 0.1F);
			cryst(forge("ores/tin"), crystal(TIN, 2), 0.1F);
			cryst(forge("ingots/copper"), crystal(COPPER, 1), 0.1F);
			cryst(forge("ores/copper"), crystal(COPPER, 2), 0.1F);
			cryst(forge("ingots/aluminum"), crystal(ALUMINIUM, 1), 0.1F);
			cryst(forge("ores/aluminum"), crystal(ALUMINIUM, 2), 0.1F);
			cryst(forge("storage_blocks/copper"), cluster(COPPER, 1), 0.9F);
			cryst(forge("storage_blocks/tin"), cluster(TIN, 1), 0.9F);
			cryst(Blocks.GOLD_BLOCK, cluster(GOLD, 1), 0.9F);
			cryst(Blocks.IRON_BLOCK, cluster(IRON, 1), 0.9F);
			cryst(ACBlocks.abyssalnite_block, cluster(ABYSSALNITE, 1), 0.9F);
			cryst(ACBlocks.refined_coralium_block, cluster(CORALIUM, 1), 0.9F);
			cryst(ACBlocks.dreadium_block, cluster(DREADIUM, 1), 0.9F);
			cryst(Blocks.COAL_ORE, crystal(CARBON, 2), 0.1F);
			cryst(Blocks.COAL_BLOCK, cluster(CARBON, 1), 0.9F);
			cryst(Blocks.REDSTONE_ORE, crystal(REDSTONE, 2), 0.1F);
			cryst(Blocks.REDSTONE_BLOCK, cluster(REDSTONE, 1), 0.9F);
			cryst(forge("ingots/zinc"), crystal(ZINC, 1), 0.1F);
			cryst(forge("ores/zinc"), crystal(ZINC, 2), 0.1F);
			cryst(ACBlocks.dreaded_abyssalnite_ore, crystal(ABYSSALNITE, 2), crystal(DREADIUM, 2), 0.2F);
			cryst(forge("dusts/saltpeter"), crystal(POTASSIUM, 1), crystal(NITRATE, 1), 0.1F);
			cryst(forge("ores/saltpeter"), crystal(POTASSIUM, 2), crystal(NITRATE, 2), 0.1F);
			cryst(forge("ingots/bronze"), crystal(COPPER, 3), crystal(TIN, 1), 0.4F);
			cryst(Items.LAPIS_LAZULI, crystal(SILICA, 6), crystal(SULFUR, 4), 0.15F);
			cryst(Blocks.LAPIS_ORE, crystal(SILICA, 12), crystal(SULFUR, 8), 0.15F);
			cryst(Blocks.LAPIS_BLOCK, cluster(SILICA, 6), cluster(SULFUR, 4), 1.0F);
			cryst(forge("ingots/brass"), crystal(COPPER, 3), crystal(ZINC, 2), 0.5F);
			cryst(forge("ores/brass"), crystal(COPPER, 6), crystal(ZINC, 4), 0.5F);
			cryst(Items.GOLD_NUGGET, shard(GOLD, 1), 0.1F);
			cryst(ACItems.abyssalnite_nugget, shard(ABYSSALNITE, 1), 0.1F);
			cryst(ACItems.refined_coralium_nugget, shard(CORALIUM, 1), 0.1F);
			cryst(ACItems.dreadium_nugget, shard(DREADIUM, 1), 0.1F);
			cryst(forge("nuggets/iron"), shard(IRON, 1), 0.1F);
			cryst(forge("nuggets/copper"), shard(COPPER, 1), 0.1F);
			cryst(forge("nuggets/tin"), shard(TIN, 1), 0.1F);
			cryst(forge("nuggets/aluminum"), shard(ALUMINIUM, 1), 0.1F);
			cryst(forge("nuggets/bronze"), shard(COPPER, 3), shard(TIN, 1), 0.1F);
			cryst(forge("nuggets/brass"), shard(COPPER, 3), shard(ZINC, 2), 0.1F);
			cryst(ACItems.coralium_gem, shard(CORALIUM, 1), 0.1F);
			cryst(forge("nuggets/zinc"), shard(ZINC, 1), 0.1F);
			cryst(forge("nuggets/magnesium"), shard(MAGNESIUM, 1), 0.1F);
			cryst(forge("ingots/magnesium"), crystal(MAGNESIUM, 1), 0.1F);
			cryst(forge("nuggets/calcium"), shard(CALCIUM, 1), 0.1F);
			cryst(forge("ingots/calcium"), crystal(CALCIUM, 1), 0.1F);
			//Crystallization for dusts
			cryst(forge("dusts/iron"), crystal(IRON, 1), 0.1F);
			cryst(forge("dusts/gold"), crystal(GOLD, 1), 0.1F);
			cryst(forge("dusts/tin"), crystal(TIN, 1), 0.1F);
			cryst(forge("dusts/copper"), crystal(COPPER, 1), 0.1F);
			cryst(forge("dusts/coal"), crystal(CARBON, 1), 0.1F);
			cryst(forge("dusts/aluminum"), crystal(ALUMINIUM, 1), 0.1F);
			cryst(forge("dusts/sulfur"), crystal(SULFUR, 1), 0.1F);
			cryst(forge("dusts/bronze"), crystal(COPPER, 3), crystal(TIN, 1), 0.1F);
			cryst(forge("dusts/brass"), crystal(COPPER, 3), crystal(ZINC, 2), 0.1F);
		} else {
			cryst(ACItems.refined_coralium_ingot, shard(CORALIUM, 4), 0.1F);
			cryst(ACItems.chunk_of_coralium, shard(CORALIUM, 4), 0.1F);
			cryst(ACBlocks.liquified_coralium_ore, shard(CORALIUM, 4), 0.1F);
			cryst(ACItems.abyssalnite_ingot, shard(ABYSSALNITE, 4), 0.1F);
			cryst(ACItems.chunk_of_abyssalnite, shard(ABYSSALNITE, 4), 0.1F);
			cryst(ACItems.dreadium_ingot, shard(DREADIUM, 4), 0.1F);
			cryst(Items.IRON_INGOT, shard(IRON, 4), 0.1F);
			cryst(Items.GOLD_INGOT, shard(GOLD, 4), 0.1F);
			cryst(Items.REDSTONE, shard(REDSTONE, 4), 0.1F);
			cryst(ItemTags.COALS, shard(CARBON, 4), 0.1F);
			cryst(forge("ores/abyssalnite"), shard(ABYSSALNITE, 4), 0.1F);
			cryst(forge("ores/coralium"), shard(CORALIUM, 4), 0.1F);
			cryst(forge("ores/iron"), shard(IRON, 4), 0.1F);
			cryst(forge("ores/gold"), shard(GOLD, 4), 0.1F);
			cryst(forge("ingots/tin"), shard(TIN, 4), 0.1F);
			cryst(forge("ores/tin"), shard(TIN, 4), 0.1F);
			cryst(forge("ingots/copper"), shard(COPPER, 4), 0.1F);
			cryst(forge("ores/copper"), shard(COPPER, 4), 0.1F);
			cryst(forge("ingots/aluminum"), shard(ALUMINIUM, 4), 0.1F);
			cryst(forge("ores/aluminum"), shard(ALUMINIUM, 4), 0.1F);
			cryst(forge("storage_blocks/copper"), crystal(COPPER, 4), 0.9F);
			cryst(forge("storage_blocks/tin"), crystal(TIN, 4), 0.9F);
			cryst(Blocks.GOLD_BLOCK, crystal(GOLD, 4), 0.9F);
			cryst(Blocks.IRON_BLOCK, crystal(IRON, 4), 0.9F);
			cryst(ACBlocks.abyssalnite_block, crystal(ABYSSALNITE, 4), 0.9F);
			cryst(ACBlocks.refined_coralium_block, crystal(CORALIUM, 4), 0.9F);
			cryst(ACBlocks.dreadium_block, crystal(DREADIUM, 4), 0.9F);
			cryst(Blocks.COAL_ORE, shard(CARBON, 4), 0.1F);
			cryst(Blocks.COAL_BLOCK, crystal(CARBON, 4), 0.9F);
			cryst(Blocks.REDSTONE_ORE, shard(REDSTONE, 4), 0.1F);
			cryst(Blocks.REDSTONE_BLOCK, crystal(REDSTONE, 4), 0.9F);
			cryst(forge("ingots/zinc"), crystal(ZINC, 1), 0.1F);
			cryst(forge("ores/zinc"), crystal(ZINC, 1), 0.1F);
			cryst(ACBlocks.dreaded_abyssalnite_ore, shard(ABYSSALNITE, 4), shard(DREADIUM, 4), 0.2F);
			cryst(forge("dusts/saltpeter"), shard(POTASSIUM, 4), shard(NITRATE, 4), 0.1F);
			cryst(forge("ores/saltpeter"), shard(POTASSIUM, 4), shard(NITRATE, 4), 0.1F);
			cryst(forge("ingots/bronze"), shard(COPPER, 3), shard(TIN, 1), 0.4F);
			cryst(Items.LAPIS_LAZULI, crystal(SILICA, 2), shard(SULFUR, 16), 0.15F);
			cryst(Blocks.LAPIS_ORE, shard(SILICA, 24), shard(SULFUR, 16), 0.15F);
			cryst(Blocks.LAPIS_BLOCK, crystal(SILICA, 24), crystal(SULFUR, 16), 1.0F);
			cryst(forge("ingots/brass"), shard(COPPER, 12), shard(ZINC, 8), 0.5F);
			cryst(forge("ores/brass"), shard(COPPER, 12), shard(ZINC, 8), 0.5F);
			cryst(Items.GOLD_NUGGET, shard(GOLD, 1), 0.1F);
			cryst(ACItems.abyssalnite_nugget, shard(ABYSSALNITE, 1), 0.1F);
			cryst(ACItems.refined_coralium_nugget, shard(CORALIUM, 1), 0.1F);
			cryst(ACItems.dreadium_nugget, shard(DREADIUM, 1), 0.1F);
			cryst(forge("nuggets/iron"), shard(IRON, 1), 0.1F);
			cryst(forge("nuggets/copper"), shard(COPPER, 1), 0.1F);
			cryst(forge("nuggets/tin"), shard(TIN, 1), 0.1F);
			cryst(forge("nuggets/aluminum"), shard(ALUMINIUM, 1), 0.1F);
			cryst(forge("nuggets/bronze"), shard(COPPER, 3), shard(TIN, 1), 0.1F);
			cryst(forge("nuggets/brass"), shard(COPPER, 3), shard(ZINC, 2), 0.1F);
			cryst(ACItems.coralium_gem, shard(CORALIUM, 1), 0.1F);
			cryst(forge("nuggets/zinc"), shard(ZINC, 1), 0.1F);
			cryst(forge("nuggets/magnesium"), shard(MAGNESIUM, 1), 0.1F);
			cryst(forge("ingots/magnesium"), shard(MAGNESIUM, 4), 0.1F);
			cryst(forge("nuggets/calcium"), shard(CALCIUM, 1), 0.1F);
			cryst(forge("ingots/calcium"), shard(CALCIUM, 4), 0.1F);
			//Crystallization for dusts
			cryst(forge("dusts/iron"), shard(IRON, 4), 0.1F);
			cryst(forge("dusts/gold"), shard(GOLD, 4), 0.1F);
			cryst(forge("dusts/tin"), shard(TIN, 4), 0.1F);
			cryst(forge("dusts/copper"), shard(COPPER, 4), 0.1F);
			cryst(forge("dusts/coal"), shard(CARBON, 4), 0.1F);
			cryst(forge("dusts/aluminum"), shard(ALUMINIUM, 4), 0.1F);
			cryst(forge("dusts/sulfur"), shard(SULFUR, 4), 0.1F);
			cryst(forge("dusts/bronze"), shard(COPPER, 12), shard(TIN, 4), 0.1F);
			cryst(forge("dusts/brass"), shard(COPPER, 12), shard(ZINC, 8), 0.1F);
		}
	}

	private static void addTransmutation(){
		trans(ACBlocks.darkstone, new ItemStack(Blocks.STONE), 0.0F);
		trans(ACItems.dreaded_shard_of_abyssalnite, st(ACItems.dreadium_ingot, 1), 0.2F);
		trans(Blocks.STONE, st(ACBlocks.darkstone, 1), 0.0F);
		trans(Blocks.STONE_BRICKS, st(ACBlocks.darkstone_bricks, 1), 0.0F);
		trans(ACBlocks.darkstone_bricks, new ItemStack(Blocks.STONE_BRICKS), 0.0F);
		trans(Blocks.COBBLESTONE, st(ACBlocks.darkstone_cobblestone, 1), 0.0F);
		trans(ACBlocks.darkstone_cobblestone, new ItemStack(Blocks.COBBLESTONE), 0.0F);
		trans(ACBlocks.coralium_stone_bricks, st(ACItems.coralium_brick, 4), 0.0F);
		trans(ACItems.coralium_brick, st(ACBlocks.coralium_stone, 1), 0.0F);
		trans(ACItems.dense_carbon_cluster, new ItemStack(Items.DIAMOND), 0.5F);
		trans(ACItems.dread_plagued_gateway_key, st(ACItems.rlyehian_gateway_key, 1), 1.0F);
		trans(Items.LAVA_BUCKET, st(ACBlocks.solid_lava, 1), 0.0F);
		trans(ACBlocks.ethaxium, new ItemStack(Blocks.END_STONE), 0.0F);
		trans(ACBlocks.ethaxium_bricks, st(ACBlocks.ethaxium, 1), 0.0F);
		trans(ACItems.anti_beef, new ItemStack(Items.COOKED_BEEF), 0.3F);
		trans(ACItems.anti_pork, new ItemStack(Items.COOKED_PORKCHOP), 0.3F);
		trans(ACItems.anti_chicken, new ItemStack(Items.COOKED_CHICKEN), 0.3F);
		trans(ACItems.anti_bone, new ItemStack(Items.BONE, 2), 0.3F);
		trans(ACItems.rotten_anti_flesh, new ItemStack(Items.ROTTEN_FLESH, 2), 0.3F);
		trans(ACItems.anti_spider_eye, new ItemStack(Items.SPIDER_EYE, 2), 0.3F);
		trans(ACItems.anti_plagued_flesh, st(ACItems.coralium_plagued_flesh, 2), 0.3F);
		trans(ACItems.anti_plagued_flesh_on_a_bone, st(ACItems.coralium_plagued_flesh_on_a_bone, 2), 0.3F);
		if(ACConfig.crystal_rework) {
			trans(cluster(ABYSSALNITE, 1), st(ACItems.abyssalnite_ingot, 1), 0.2F);
			trans(cluster(CORALIUM, 1), st(ACItems.refined_coralium_ingot, 1), 0.2F);
			trans(cluster(DREADIUM, 1), st(ACItems.dreadium_ingot, 1), 0.2F);
			trans(cluster(IRON, 1), new ItemStack(Items.IRON_INGOT), 0.2F);
			trans(cluster(GOLD, 1), new ItemStack(Items.GOLD_INGOT), 0.2F);
			trans(cluster(SULFUR, 1), st(ACItems.sulfur, 1), 0.2F);
			trans(cluster(BLAZE, 1), new ItemStack(Items.BLAZE_POWDER), 0.2F);
			trans(cluster(REDSTONE, 1), new ItemStack(Items.REDSTONE), 0.2F);
			trans(cluster(METHANE, 1), st(ACItems.methane, 1), 0.2F);
			trans(cluster(TIN, 1), st(ACItems.tin_ingot, 1), 0.2F);
			trans(cluster(COPPER, 1), st(ACItems.copper_ingot, 1), 0.2F);
			trans(cluster(ALUMINIUM, 1), forge("ingots/aluminum"), 0.2F);
			trans(cluster(ZINC, 1), forge("ingots/zinc"), 0.2F);
			trans(crystal(ABYSSALNITE, 1), st(ACItems.abyssalnite_nugget, 1), 0.1F);
			trans(crystal(CORALIUM, 1), st(ACItems.refined_coralium_nugget, 1), 0.1F);
			trans(crystal(DREADIUM, 1), st(ACItems.dreadium_nugget, 1), 0.1F);
			trans(crystal(ALUMINIUM, 1), forge("nuggets/aluminum"), 0.1F);
			trans(crystal(IRON, 1), new ItemStack(Items.IRON_NUGGET), 0.2F);
			trans(crystal(GOLD, 1), new ItemStack(Items.GOLD_NUGGET), 0.2F);
			trans(crystal(COPPER, 1), forge("nuggets/copper"), 0.2F);
			trans(crystal(TIN, 1), forge("nuggets/tin"), 0.2F);
			trans(crystal(ZINC, 1), forge("nuggets/zinc"), 0.2F);
			trans(crystal(MAGNESIUM, 1), forge("nuggets/magnesium"), 0.2F);
			trans(cluster(MAGNESIUM, 1), forge("ingots/magnesium"), 0.2F);
			trans(crystal(CALCIUM, 1), forge("nuggets/calcium"), 0.2F);
			trans(cluster(CALCIUM, 1), forge("ingots/calcium"), 0.2F);
		} else {
			trans(crystal(ABYSSALNITE, 1), st(ACItems.abyssalnite_ingot, 1), 0.2F);
			trans(crystal(CORALIUM, 1), st(ACItems.refined_coralium_ingot, 1), 0.2F);
			trans(crystal(DREADIUM, 1), st(ACItems.dreadium_ingot, 1), 0.2F);
			trans(crystal(IRON, 1), new ItemStack(Items.IRON_INGOT), 0.2F);
			trans(crystal(GOLD, 1), new ItemStack(Items.GOLD_INGOT), 0.2F);
			trans(crystal(SULFUR, 1), st(ACItems.sulfur, 1), 0.2F);
			trans(crystal(BLAZE, 1), new ItemStack(Items.BLAZE_POWDER), 0.2F);
			trans(crystal(REDSTONE, 1), new ItemStack(Items.REDSTONE), 0.2F);
			trans(crystal(HYDROGEN, 1), crystal(HYDROGEN, 1), 0.2F);
			trans(crystal(OXYGEN, 1), crystal(OXYGEN, 1), 0.2F);
			trans(crystal(NITROGEN, 1), crystal(NITROGEN, 1), 0.2F);
			trans(crystal(METHANE, 1), st(ACItems.methane, 1), 0.2F);
			trans(crystal(TIN, 1), st(ACItems.tin_ingot, 1), 0.2F);
			trans(crystal(COPPER, 1), st(ACItems.copper_ingot, 1), 0.2F);
			trans(crystal(ALUMINIUM, 1), forge("ingots/aluminum"), 0.2F);
			trans(crystal(ZINC, 1), forge("ingots/zinc"), 0.2F);
			trans(shard(ABYSSALNITE, 1), st(ACItems.abyssalnite_nugget, 1), 0.1F);
			trans(shard(CORALIUM, 1), st(ACItems.refined_coralium_nugget, 1), 0.1F);
			trans(shard(DREADIUM, 1), st(ACItems.dreadium_nugget, 1), 0.1F);
			trans(shard(ALUMINIUM, 1), forge("nuggets/aluminum"), 0.1F);
			trans(shard(IRON, 1), new ItemStack(Items.IRON_NUGGET), 0.2F);
			trans(shard(GOLD, 1), new ItemStack(Items.GOLD_NUGGET), 0.2F);
			trans(shard(COPPER, 1), forge("nuggets/copper"), 0.2F);
			trans(shard(TIN, 1), forge("nuggets/tin"), 0.2F);
			trans(shard(ZINC, 1), forge("nuggets/zinc"), 0.2F);
			trans(shard(MAGNESIUM, 1), forge("nuggets/magnesium"), 0.2F);
			trans(crystal(MAGNESIUM, 1), forge("ingots/magnesium"), 0.2F);
			trans(shard(CALCIUM, 1), forge("nuggets/calcium"), 0.2F);
			trans(crystal(CALCIUM, 1), forge("ingots/calcium"), 0.2F);
		}
	}

	private static void addEngraving(){
		for(var coin : List.of(ACItems.coin, ACItems.cthulhu_engraved_coin, ACItems.elder_engraved_coin, ACItems.jzahar_engraved_coin,
				ACItems.hastur_engraved_coin, ACItems.azathoth_engraved_coin, ACItems.nyarlathotep_engraved_coin,
				ACItems.yog_sothoth_engraved_coin, ACItems.shub_niggurath_engraved_coin))
			AbyssalCraftAPI.addCoin(coin.get());
		AbyssalCraftAPI.addEngraving(ACItems.coin.get(), (ItemEngraving)ACItems.blank_engraving.get(), 0.0F);
		AbyssalCraftAPI.addEngraving(ACItems.cthulhu_engraved_coin.get(), (ItemEngraving)ACItems.cthulhu_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.elder_engraved_coin.get(), (ItemEngraving)ACItems.elder_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.jzahar_engraved_coin.get(), (ItemEngraving)ACItems.jzahar_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.hastur_engraved_coin.get(), (ItemEngraving)ACItems.hastur_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.azathoth_engraved_coin.get(), (ItemEngraving)ACItems.azathoth_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.nyarlathotep_engraved_coin.get(), (ItemEngraving)ACItems.nyarlathotep_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.yog_sothoth_engraved_coin.get(), (ItemEngraving)ACItems.yog_sothoth_engraving.get(), 0.5F);
		AbyssalCraftAPI.addEngraving(ACItems.shub_niggurath_engraved_coin.get(), (ItemEngraving)ACItems.shub_niggurath_engraving.get(), 0.5F);
	}

	private static void mat(ItemLike output, ItemStack...input) {
		AbyssalCraftAPI.addMaterialization(new ItemStack(output), input);
	}

	private static void mat(ItemStack output, ItemStack...input) {
		if(!output.isEmpty())
			AbyssalCraftAPI.addMaterialization(output, input);
	}

	/** Materialization of every item in a tag (1.12.2 OreDictionary output) */
	private static void mat(TagKey<Item> output, ItemStack...input) {
		TAG_MATERIALIZATIONS.add(new TagMaterialization(output, input));
	}

	private static void addMaterialization(){
		mat(Items.BONE, crystal(CALCIUM, 1));
		mat(Items.ROTTEN_FLESH, crystal(PHOSPHORUS, 1));
		mat(st(ACItems.coralium_plagued_flesh, 1), crystal(PHOSPHORUS, 1), crystal(CORALIUM, 1));
		mat(st(ACItems.coralium_plagued_flesh_on_a_bone, 1), crystal(PHOSPHORUS, 1), crystal(CALCIUM, 1), crystal(CORALIUM, 1));
		mat(Items.COAL, crystal(CARBON, 1));
		mat(Items.CHARCOAL, crystal(CARBON, 1));
		mat(Items.IRON_INGOT, crystal(IRON, 1));
		mat(Items.GOLD_INGOT, crystal(GOLD, 1));
		mat(Items.BLAZE_POWDER, crystal(BLAZE, 1));
		mat(Items.BLAZE_ROD, crystal(BLAZE, 2));
		mat(st(ACItems.abyssalnite_ingot, 1), crystal(ABYSSALNITE, 1));
		mat(st(ACItems.refined_coralium_ingot, 1), crystal(CORALIUM, 1));
		mat(st(ACItems.dreadium_ingot, 1), crystal(DREADIUM, 1));
		mat(forge("ingots/tin"), crystal(TIN, 1));
		mat(forge("ingots/copper"), crystal(COPPER, 1));
		mat(st(ACItems.methane, 1), crystal(METHANE, 1));
		mat(st(ACItems.nitre, 1), crystal(POTASSIUM, 1), crystal(NITRATE, 1));
		mat(st(ACItems.sulfur, 1), crystal(SULFUR, 1));
		mat(Items.REDSTONE, crystal(REDSTONE, 1));
		mat(st(ACItems.dread_fragment, 1), crystal(PHOSPHORUS, 2), crystal(DREADIUM, 1));
		mat(Blocks.STONE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(forge("ores/gold"), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(GOLD, 1));
		mat(forge("ores/iron"), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(IRON, 1));
		mat(forge("ores/coal"), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(CARBON, 1));
		mat(forge("ores/redstone"), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(REDSTONE, 1));
		mat(st(ACBlocks.coralium_ore, 1), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(CORALIUM, 1));
		mat(st(ACBlocks.abyssalnite_ore, 1), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(ABYSSALNITE, 1));
		mat(st(ACBlocks.coralium_infused_stone, 1), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(CORALIUM, 2));
		mat(st(ACBlocks.nitre_ore, 1), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(POTASSIUM, 1), crystal(NITRATE, 1));
		mat(Blocks.SPONGE, crystal(SILICA, 1), crystal(CARBON, 1));
		mat(Items.ELYTRA, crystal(CARBON, 8), crystal(HYDROGEN, 13), crystal(OXYGEN, 5), crystal(NITROGEN, 1));
		mat(Items.PRISMARINE_SHARD, crystal(SILICA, 1), crystal(BERYL, 1));
		mat(Items.PRISMARINE_CRYSTALS, crystal(SILICA, 1), crystal(BERYL, 1));
		mat(Blocks.PRISMARINE, crystal(SILICA, 4), crystal(BERYL, 4));
		mat(Blocks.PRISMARINE_BRICKS, crystal(SILICA, 9), crystal(BERYL, 9));
		mat(Blocks.DARK_PRISMARINE, crystal(SILICA, 8), crystal(BERYL, 8), crystal(CARBON, 1));
		mat(Blocks.SEA_LANTERN, crystal(SILICA, 9), crystal(BERYL, 9));
		mat(Items.EGG, crystal(CALCIUM, 1), crystal(PHOSPHORUS, 1));
		mat(st(ACItems.charcoal, 1), crystal(CARBON, 1), crystal(DREADIUM, 1));
		mat(Blocks.COAL_BLOCK, crystal(CARBON, 9));
		mat(Blocks.REDSTONE_BLOCK, crystal(REDSTONE, 9));
		mat(Items.DIAMOND, crystal(CARBON, 64));
		mat(forge("ores/diamond"), crystal(SILICA, 1), crystal(MAGNESIA, 1), crystal(CARBON, 64));
		mat(Blocks.GRANITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Blocks.POLISHED_GRANITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Blocks.DIORITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Blocks.POLISHED_DIORITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Blocks.ANDESITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Blocks.POLISHED_ANDESITE, crystal(SILICA, 1), crystal(MAGNESIA, 1));
		mat(Items.LEATHER, crystal(PHOSPHORUS, 1));
		mat(Items.RABBIT_HIDE, crystal(PHOSPHORUS, 1));
		mat(Items.RABBIT_FOOT, crystal(PHOSPHORUS, 1), crystal(CALCIUM, 1));
		mat(ItemTags.LOGS, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(ItemTags.PLANKS, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(ItemTags.SAPLINGS, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(ItemTags.LEAVES, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(Blocks.VINE, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(Blocks.DEAD_BUSH, crystal(CARBON, 6), crystal(HYDROGEN, 10), crystal(OXYGEN, 5));
		mat(Items.LAPIS_LAZULI, crystal(SILICA, 6), crystal(SULFUR, 4));
		mat(forge("ores/lapis"), crystal(SILICA, 7), crystal(SULFUR, 4), crystal(MAGNESIA, 1));
		mat(Blocks.LAPIS_BLOCK, crystal(SILICA, 54), crystal(SULFUR, 36));
	}

	/**
	 * Expands the tag based Materializer recipes. Called whenever tags are (re)loaded, on both sides.
	 */
	public static void onTagsLoaded() {
		MaterializerRecipes.instance().clearTagMaterializations();
		for(TagMaterialization m : TAG_MATERIALIZATIONS)
			for(var holder : ForgeRegistries.ITEMS.tags().getTag(m.output()))
				MaterializerRecipes.instance().materializeFromTag(m.input(), new ItemStack(holder));
	}
}
