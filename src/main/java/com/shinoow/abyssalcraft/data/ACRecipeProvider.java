/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - the recipes (AbyssalCrafting, MiscHandler); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.crafting.ConfigCondition;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Crafting and smelting recipes. The 436 JSON crafting recipes of 1.12.2 are in the generated {@link ACCraftingRecipes};
 * the recipes 1.12.2 registered in code (smelting, the bucket recipes) are below.
 */
public class ACRecipeProvider extends RecipeProvider {

	public ACRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> out) {
		ACCraftingRecipes.build(this, out);
		bucketRecipes(out);
		smelting(out);
	}

	/** 1.12.2 MiscHandler.registerRecipes (ShapedFluidContainerRecipe: the buckets come back empty) */
	private void bucketRecipes(Consumer<FinishedRecipe> out) {
		Ingredient antimatter = ing(ACItems.liquid_antimatter_bucket), coralium = ing(ACItems.liquid_coralium_bucket), obsidian = Ingredient.of(Items.OBSIDIAN);
		shaped(out, "oblivion_deathbomb_0", null, "abyssalcraft:oblivion_deathbomb", 1, "abyssalcraft:odb_core", new String[] {"#%%", "&$%", "@%%"},
				'#', antimatter, '@', coralium, '%', obsidian, '&', ing(ACItems.oblivion_catalyst), '$', ing(ACBlocks.odb_core));
		shaped(out, "oblivion_deathbomb_1", null, "abyssalcraft:oblivion_deathbomb", 1, "abyssalcraft:odb_core", new String[] {"#%%", "&$%", "@%%"},
				'#', coralium, '@', antimatter, '%', obsidian, '&', ing(ACItems.oblivion_catalyst), '$', ing(ACBlocks.odb_core));
		shaped(out, "transmutator", null, "abyssalcraft:transmutator", 1, "abyssalcraft:transmutation_gem", new String[] {"###", "#%#", "&$&"},
				'#', ing(ACItems.coralium_brick), '%', ing(ACItems.transmutation_gem), '&', ing(ACBlocks.refined_coralium_block), '$', coralium);
		shaped(out, "materializer", null, "abyssalcraft:materializer", 1, "abyssalcraft:ethaxium_brick", new String[] {"###", "#%#", "&$&"},
				'#', ing(ACItems.ethaxium_brick), '%', obsidian, '&', ing(ACBlocks.ethaxium_block), '$', antimatter);
	}

	/** 1.12.2 AbyssalCrafting.addBlockSmelting / addItemSmelting / addArmor */
	private void smelting(Consumer<FinishedRecipe> out) {
		smelt(out, null, ACBlocks.darkstone_cobblestone, ACBlocks.darkstone, 1, 0.1F);
		smelt(out, null, ACBlocks.abyssal_cobblestone, ACBlocks.abyssal_stone, 1, 0.1F);
		smelt(out, null, ACBlocks.dreadstone_cobblestone, ACBlocks.dreadstone, 1, 0.1F);
		smelt(out, null, ACBlocks.abyssalnite_cobblestone, ACBlocks.abyssalnite_stone, 1, 0.1F);
		smelt(out, null, ACBlocks.coralium_cobblestone, ACBlocks.coralium_stone, 1, 0.1F);
		smelt(out, null, ACBlocks.darklands_oak_wood, Items.CHARCOAL, 1, 1F);
		smelt(out, null, ACBlocks.darklands_oak_wood_2, Items.CHARCOAL, 1, 1F);
		smelt(out, null, ACBlocks.coralium_infused_stone, ACItems.coralium_pearl, 1, 3F);
		smelt(out, null, ACBlocks.pearlescent_coralium_ore, ACItems.coralium_pearl, 1, 3F);
		smelt(out, null, ACBlocks.liquified_coralium_ore, ACItems.refined_coralium_ingot, 1, 3F);
		smelt(out, null, ACBlocks.dreaded_abyssalnite_ore, ACItems.abyssalnite_ingot, 1, 3F);
		smelt(out, null, ACBlocks.coralium_stone, ACItems.coralium_brick, 1, 0.1F);
		smelt(out, null, ACBlocks.nitre_ore, ACItems.nitre, 1, 1F);
		smelt(out, null, ACBlocks.abyssal_iron_ore, Items.IRON_INGOT, 1, 0.7F);
		smelt(out, null, ACBlocks.abyssal_gold_ore, Items.GOLD_INGOT, 1, 1F);
		smelt(out, null, ACBlocks.abyssal_diamond_ore, Items.DIAMOND, 1, 1F);
		smelt(out, null, ACBlocks.abyssal_nitre_ore, ACItems.nitre, 1, 1F);
		smelt(out, null, ACBlocks.abyssal_tin_ore, ACItems.tin_ingot, 1, 0.7F);
		smelt(out, null, ACBlocks.abyssal_copper_ore, ACItems.copper_ingot, 1, 0.7F);
		smelt(out, null, ACBlocks.ethaxium, ACItems.ethaxium_brick, 1, 0.2F);
		smelt(out, null, ACBlocks.ethaxium_bricks, ACBlocks.cracked_ethaxium_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.dark_ethaxium_bricks, ACBlocks.cracked_dark_ethaxium_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.darkstone_bricks, ACBlocks.cracked_darkstone_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.abyssal_stone_bricks, ACBlocks.cracked_abyssal_stone_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.dreadstone_bricks, ACBlocks.cracked_dreadstone_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.abyssalnite_stone_bricks, ACBlocks.cracked_abyssalnite_stone_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.coralium_stone_bricks, ACBlocks.cracked_coralium_stone_bricks, 1, 0.1F);
		smelt(out, null, ACBlocks.abyssal_sand, ACBlocks.abyssal_sand_glass, 1, 0.1F);
		smelt(out, null, ACBlocks.dreadlands_log, ACItems.charcoal, 1, 1F);
		smelt(out, null, ACItems.chunk_of_abyssalnite, ACItems.abyssalnite_ingot, 1, 3F);
		smelt(out, null, ACItems.chunk_of_coralium, ACItems.refined_coralium_ingot, 2, 3F);
		smelt(out, null, ACItems.dreaded_chunk_of_abyssalnite, ACItems.abyssalnite_ingot, 1, 3F);
		smelt(out, null, ACItems.coin, Items.IRON_INGOT, 4, 0.5F);
		smelt(out, "foodstuffs", Items.EGG, ACItems.fried_egg, 1, 0F);
		for(Item armor : new Item[] {Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS})
			smelt(out, "smeltingRecipes", armor, Items.LEATHER, 1, 1F);
		String[][] armor = {{"abyssalnite", "abyssalnite"}, {"refined_coralium", "refined_coralium"}, {"dreadium", "dreadium"}, {"ethaxium", "ethaxium"}};
		for(String[] set : armor)
			for(String piece : new String[] {"helmet", "chestplate", "leggings", "boots"})
				smelt(out, "smeltingRecipes", item("abyssalcraft:" + set[0] + "_" + piece), item("abyssalcraft:" + set[1] + "_nugget"), 1, 1F);
	}

	// ---- helpers (also used by ACCraftingRecipes) ----

	static Item item(String id) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
		if(item == null || item == Items.AIR)
			throw new IllegalStateException("Recipe item " + id + " isn't registered");
		return item;
	}

	static Ingredient i(String id) {
		return Ingredient.of(item(id));
	}

	static Ingredient t(String tag) {
		return Ingredient.of(ItemTags.create(new ResourceLocation(tag)));
	}

	static Ingredient any(Ingredient... ingredients) {
		return CompoundIngredient.of(ingredients);
	}

	/** 1.12.2 minecraft:item_nbt {Potion:"minecraft:water"} */
	static Ingredient water() {
		return StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
	}

	private static Ingredient ing(RegistryObject<? extends ItemLike> obj) {
		return Ingredient.of(obj.get());
	}

	private static CriterionTriggerInstance unlock(String spec) {
		return spec.startsWith("#") ? has(ItemTags.create(new ResourceLocation(spec.substring(1)))) : has(item(spec));
	}

	void shaped(Consumer<FinishedRecipe> out, String name, @Nullable String option, String result, int count, String unlock, String[] pattern, Object... keys) {
		if(pattern.length == 1 && pattern[0].length() == 1) {
			// a 1x1 shaped recipe is a one ingredient shapeless one (1.20 refuses it as shaped)
			shapeless(out, name, option, result, count, unlock, (Ingredient) keys[1]);
			return;
		}
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item(result), count);
		for(String row : pattern)
			builder.pattern(row);
		for(int k = 0; k < keys.length; k += 2)
			builder.define((Character) keys[k], (Ingredient) keys[k + 1]);
		save(out, builder.unlockedBy("has_ingredient", unlock(unlock)), name, option);
	}

	void shapeless(Consumer<FinishedRecipe> out, String name, @Nullable String option, String result, int count, String unlock, Ingredient... ingredients) {
		ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item(result), count);
		for(Ingredient ingredient : ingredients)
			builder.requires(ingredient);
		save(out, builder.unlockedBy("has_ingredient", unlock(unlock)), name, option);
	}

	private void smelt(Consumer<FinishedRecipe> out, @Nullable String option, Object input, Object result, int count, float xp) {
		Item in = asItem(input), res = asItem(result);
		String name = "smelting/" + ForgeRegistries.ITEMS.getKey(res).getPath() + "_from_" + ForgeRegistries.ITEMS.getKey(in).getPath();
		RecipeBuilder builder = SimpleCookingRecipeBuilder.smelting(Ingredient.of(in), RecipeCategory.MISC, res, xp, 200).unlockedBy("has_ingredient", has(in));
		if(count > 1 && option != null)
			throw new IllegalArgumentException("conditional smelting with a result count isn't supported");
		save(count == 1 ? out : recipe -> out.accept(new CountedResult(recipe, res, count)), builder, name, option);
	}

	private static Item asItem(Object o) {
		if(o instanceof RegistryObject<?> obj)
			return ((ItemLike) obj.get()).asItem();
		return ((ItemLike) o).asItem();
	}

	private static void save(Consumer<FinishedRecipe> out, RecipeBuilder builder, String name, @Nullable String option) {
		ResourceLocation id = AbyssalCraft.loc(name);
		if(option == null)
			builder.save(out, id);
		else ConditionalRecipe.builder().addCondition(new ConfigCondition(option)).addRecipe(c -> builder.save(c, id)).generateAdvancement().build(out, id);
	}

	/** Cooking recipe with a result count (the Forge cooking serializer reads {"item", "count"}) */
	private record CountedResult(FinishedRecipe recipe, Item result, int count) implements FinishedRecipe {
		@Override
		public void serializeRecipeData(JsonObject json) {
			recipe.serializeRecipeData(json);
			JsonObject res = new JsonObject();
			res.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
			res.addProperty("count", count);
			json.add("result", res);
		}

		@Override public ResourceLocation getId() { return recipe.getId(); }
		@Override public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() { return recipe.getType(); }
		@Override public JsonObject serializeAdvancement() { return recipe.serializeAdvancement(); }
		@Override public ResourceLocation getAdvancementId() { return recipe.getAdvancementId(); }
	}
}
