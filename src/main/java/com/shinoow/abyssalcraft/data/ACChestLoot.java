/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the 1.12.2 chest loot tables (assets/abyssalcraft/loot_tables/chests) with the flattened item ids.
 *     Metadata ranges ("set_data") became one entry per item, splitting the weight. Items that aren't registered yet
 *     (scrolls, Oblivion Catalyst) are left out until they are, so re-running datagen adds them.
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

public class ACChestLoot implements LootTableSubProvider {

	/** 1.12.2 ACLib.crystalNames, the order of the crystal metadata */
	private static final String[] CRYSTALS = {"iron", "gold", "sulfur", "carbon", "oxygen", "hydrogen", "nitrogen", "phosphorus", "potassium", "nitrate",
			"methane", "redstone", "abyssalnite", "coralium", "dreadium", "blaze", "tin", "copper", "silicon", "magnesium", "aluminium", "silica", "alumina",
			"magnesia", "zinc", "calcium", "beryllium", "beryl"};

	public static final List<String> SKIPPED = new ArrayList<>();

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
		output.accept(ACLoot.CHEST_DREADLANDS_MINESHAFT, LootTable.lootTable()
				.withPool(pool(ConstantValue.exactly(1), ac("abyssalnite_ingot", 10, count(1, 5)), mc("diamond", 5, count(1, 3)), ac("coralium_gem", 5, count(4, 9)),
						ac("shadow_shard", 5, count(4, 9)), ac("dreaded_shard_of_abyssalnite", 10, count(3, 8)), ac("refined_coralium_pickaxe", 1),
						ac("transmutation_gem", 1, damage()), ac("oblivion_catalyst", 1), ac("zinc_crystal", 8, count(1, 5))))
				.withPool(pool(UniformGenerator.between(2, 4), ac("abyssalnite_ingot", 10, count(1, 5)), ac("refined_coralium_ingot", 5, count(1, 3)),
						ac("shadow_shard", 5, count(4, 9)), mc("diamond", 3, count(1, 2)), ac("coralium_gem", 10, count(3, 8)), mc("bread", 15, count(1, 3)),
						ac("chunk_of_abyssalnite", 10, count(2, 4)), ac("dreaded_chunk_of_abyssalnite", 10, count(2, 4))))
				.withPool(pool(ConstantValue.exactly(3), mc("rail", 20, count(4, 8)), mc("powered_rail", 5, count(1, 4)), mc("detector_rail", 5, count(1, 4)),
						mc("activator_rail", 5, count(1, 4)), mc("torch", 15, count(1, 16)))));

		output.accept(ACLoot.CHEST_ABYSSAL_STRONGHOLD_CORRIDOR, LootTable.lootTable()
				.withPool(pool(UniformGenerator.between(2, 3), mc("ender_pearl", 10), ac("transmutation_gem", 1, damage()), ac("abyssalnite_ingot", 10, count(1, 5)),
						ac("refined_coralium_ingot", 5, count(1, 3)), ac("coralium_pearl", 5, count(1, 3)), mc("bread", 15, count(1, 3)),
						mc("golden_apple", 15, count(1, 3)), ac("abyssalnite_pickaxe", 5), ac("abyssalnite_chestplate", 5), ac("abyssalnite_helmet", 5),
						ac("abyssalnite_leggings", 5), ac("abyssalnite_boots", 5), ac("oblivion_catalyst", 1), ac("zinc_crystal", 8, count(1, 5)))));
		output.accept(ACLoot.CHEST_ABYSSAL_STRONGHOLD_CROSSING, LootTable.lootTable()
				.withPool(pool(UniformGenerator.between(1, 4), ac("abyssalnite_ingot", 10, count(1, 5)), ac("refined_coralium_ingot", 5, count(1, 3)),
						ac("coralium_pearl", 5, count(1, 3)), ac("coralium_gem", 10, count(3, 8)), mc("golden_apple", 15, count(1, 3)),
						ac("abyssalnite_pickaxe", 1), ac("zinc_crystal", 8, count(1, 5)))));

		CompoundTag shield = new CompoundTag();
		shield.put("BlockEntityTag", bannerPatterns());
		shield.getCompound("BlockEntityTag").putInt("Base", DyeColor.BLACK.getId());
		CompoundTag display = new CompoundTag();
		display.putString("Name", "{\"text\":\"Abyssalshield\"}");
		shield.put("display", display);
		output.accept(ACLoot.CHEST_OMOTHOL_BLACKSMITH, LootTable.lootTable()
				.withPool(pool(UniformGenerator.between(3, 8), ac("abyssalnite_ingot", 10, count(1, 3)), ac("refined_coralium_ingot", 10, count(1, 3)),
						ac("dreadium_ingot", 10, count(1, 3)), ac("ethaxium_ingot", 2, count(1, 2)), ac("tin_ingot", 10, count(1, 5)),
						ac("copper_ingot", 10, count(1, 5)), ac("coralium_brick", 10, count(1, 5)), ac("ethaxium_brick", 10, count(1, 5)),
						mc("shield", 1, SetNbtFunction.setTag(shield)))));
		output.accept(ACLoot.CHEST_OMOTHOL_FARMHOUSE, LootTable.lootTable()
				.withPool(crystalPool(UniformGenerator.between(5, 10), "_crystal_fragment", count(1, 3))));
		CompoundTag banner = new CompoundTag();
		banner.put("BlockEntityTag", bannerPatterns());
		output.accept(ACLoot.CHEST_OMOTHOL_HOUSE, LootTable.lootTable()
				.withPool(pool(UniformGenerator.between(1, 3), mc("black_banner", 1, SetNbtFunction.setTag(banner)), mc("light_blue_dye", 10, count(1, 3))))
				.withPool(pool(UniformGenerator.between(1, 3), mc("black_banner", 10), mc("ink_sac", 10, count(1, 3))))
				.withPool(pool(UniformGenerator.between(1, 3), mc("light_blue_banner", 10), mc("light_blue_dye", 10, count(1, 3)))));
		output.accept(ACLoot.CHEST_OMOTHOL_LIBRARY, LootTable.lootTable()
				.withPool(pool(UniformGenerator.between(3, 6), mc("book", 10, count(1, 4)), mc("writable_book", 10),
						mc("enchanted_book", 1, EnchantRandomlyFunction.randomEnchantment().withEnchantment(Enchantments.BINDING_CURSE).withEnchantment(Enchantments.VANISHING_CURSE)),
						ac("basic_scroll", 2), ac("lesser_scroll", 2), ac("moderate_scroll", 1))));

		output.accept(ACLoot.CHEST_OMOTHOL_STORAGE_JUNK, LootTable.lootTable()
				.withPool(pool(ConstantValue.exactly(1), ac("coralium_plagued_flesh", 10, count(1, 4)), ac("coralium_plagued_flesh_on_a_bone", 10, count(1, 4)),
						ac("anti_plagued_flesh", 10, count(1, 4)), ac("anti_plagued_flesh_on_a_bone", 10, count(1, 4)), ac("omothol_flesh", 10),
						ac("dread_fragment", 10, count(1, 4)), ac("rotten_anti_flesh", 10, count(1, 4)), mc("rotten_flesh", 10, count(1, 4)),
						mc("leather", 10, count(1, 4)), mc("rabbit_hide", 10, count(1, 4)), ac("skin_of_the_abyssal_wasteland", 1, count(1, 3)),
						ac("skin_of_the_dreadlands", 1, count(1, 3))))
				.withPool(pool(ConstantValue.exactly(1), ac("fried_egg", 10, count(1, 4)), ac("anti_beef", 10, count(1, 4)), ac("anti_chicken", 10, count(1, 4)),
						ac("anti_pork", 10, count(1, 4)), mc("porkchop", 10, count(1, 4)), mc("beef", 10, count(1, 4)), mc("chicken", 10, count(1, 4)),
						mc("rabbit", 10, count(1, 4)), mc("mutton", 10, count(1, 4))))
				.withPool(pool(ConstantValue.exactly(1), mc("cod", 1, count(1, 4)), mc("salmon", 1, count(1, 4)), mc("tropical_fish", 1, count(1, 4)),
						mc("pufferfish", 1, count(1, 4))))
				.withPool(pool(ConstantValue.exactly(1), mc("bone", 10, count(1, 4)), ac("anti_bone", 10, count(1, 4)), mc("bone_meal", 10, count(1, 4))))
				.withPool(pool(ConstantValue.exactly(1), mc("apple", 10, count(1, 4)), mc("brown_mushroom", 10, count(1, 4)), mc("red_mushroom", 10, count(1, 4)),
						mc("golden_apple", 5, count(1, 4)), mc("beetroot", 10, count(1, 4)), mc("carrot", 10, count(1, 4)), mc("potato", 10, count(1, 4)),
						mc("poisonous_potato", 10, count(1, 4))))
				// 1.12.2 "dye" without data is ink sac
				.withPool(pool(ConstantValue.exactly(1), mc("paper", 10, count(1, 4)), mc("book", 10, count(1, 4)), mc("ink_sac", 10, count(1, 4))))
				.withPool(crystalPool(ConstantValue.exactly(1), "_crystal_fragment", count(1, 3)))
				.withPool(crystalPool(ConstantValue.exactly(1), "_crystal_shard", count(1, 3))));
		output.accept(ACLoot.CHEST_OMOTHOL_STORAGE_TREASURE, LootTable.lootTable()
				.withPool(pool(ConstantValue.exactly(1), ac("configurator_shard_0", 25), ac("configurator_shard_1", 25), ac("configurator_shard_2", 25),
						ac("configurator_shard_3", 25))));
	}

	/** Omothol banner: light blue flower, black top stripe and bottom stripe (1.12.2 colors were dye damage values) */
	private static CompoundTag bannerPatterns() {
		ListTag patterns = new ListTag();
		patterns.add(pattern("flo", DyeColor.LIGHT_BLUE));
		patterns.add(pattern("ts", DyeColor.BLACK));
		patterns.add(pattern("bs", DyeColor.BLACK));
		CompoundTag tag = new CompoundTag();
		tag.put("Patterns", patterns);
		return tag;
	}

	private static CompoundTag pattern(String pattern, DyeColor color) {
		CompoundTag tag = new CompoundTag();
		tag.put("Pattern", StringTag.valueOf(pattern));
		tag.putInt("Color", color.getId());
		return tag;
	}

	private static LootPool.Builder crystalPool(NumberProvider rolls, String suffix, LootItemFunction.Builder function) {
		List<LootPoolSingletonContainer.Builder<?>> entries = new ArrayList<>();
		for(String crystal : CRYSTALS)
			entries.add(ac(crystal + suffix, 1, function));
		return pool(rolls, entries.toArray(LootPoolSingletonContainer.Builder<?>[]::new));
	}

	private static LootPool.Builder pool(NumberProvider rolls, LootPoolSingletonContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().setRolls(rolls);
		for(LootPoolSingletonContainer.Builder<?> entry : entries)
			if(entry != null)
				pool.add(entry);
		return pool;
	}

	private static LootItemFunction.Builder count(int min, int max) {
		return SetItemCountFunction.setCount(UniformGenerator.between(min, max));
	}

	/** Transmutation Gems have 10 - 30 % of their durability left (same meaning in 1.12.2 and 1.20.1) */
	private static LootItemFunction.Builder damage() {
		return SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.3F));
	}

	private static LootPoolSingletonContainer.Builder<?> ac(String name, int weight, LootItemFunction.Builder... functions) {
		return entry(AbyssalCraft.loc(name), weight, functions);
	}

	private static LootPoolSingletonContainer.Builder<?> mc(String name, int weight, LootItemFunction.Builder... functions) {
		return entry(new ResourceLocation(name), weight, functions);
	}

	private static LootPoolSingletonContainer.Builder<?> entry(ResourceLocation id, int weight, LootItemFunction.Builder... functions) {
		Item item = ForgeRegistries.ITEMS.containsKey(id) ? ForgeRegistries.ITEMS.getValue(id) : null;
		if(item == null) {
			if(!SKIPPED.contains(id.toString()))
				SKIPPED.add(id.toString());
			return null;
		}
		LootPoolSingletonContainer.Builder<?> entry = LootItem.lootTableItem(item).setWeight(weight);
		for(LootItemFunction.Builder function : functions)
			entry.apply(function);
		return entry;
	}
}
