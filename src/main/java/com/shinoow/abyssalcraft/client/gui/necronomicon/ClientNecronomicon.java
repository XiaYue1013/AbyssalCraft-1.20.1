/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (GuiHandler, InternalNecroDataHandler); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.necronomicon.CraftingStack;
import com.shinoow.abyssalcraft.api.necronomicon.GuiInstance;
import com.shinoow.abyssalcraft.api.necronomicon.INecroData;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

/** Client side Necronomicon setup and opening */
public class ClientNecronomicon {

	/** Client setup: the client-only book entries and the crafting page recipe lookup */
	public static void init() {
		NecroData pe = AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("potentialenergy");
		if(pe != null)
			for(INecroData data : pe.getContainedData())
				if(data instanceof NecroData places && places.getIdentifier().equals("placesofpower"))
					places.addData(new GuiInstance(0, NecronomiconText.LABEL_STRUCTURES, "structures") {
						@Override public IUnlockCondition getCondition() { return new DefaultCondition(); }
						@Override public Screen getOpenGui(int bookType, Screen parent) { return new GuiNecronomiconPlacesOfPower(bookType, (GuiNecronomicon) parent); }
					});
		CraftingStack.recipeLookup = ClientNecronomicon::findRecipe;
	}

	/** The 3x3 grid of the first crafting recipe making the stack (1.12.2 searched the recipe registry) */
	private static Object[] findRecipe(ItemStack output) {
		Object[] grid = new Object[9];
		var level = Minecraft.getInstance().level;
		if(level == null) return grid;
		for(CraftingRecipe recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
			ItemStack result = recipe.getResultItem(level.registryAccess());
			if(result.isEmpty() || !ItemStack.isSameItemSameTags(result, output)) continue;
			var ingredients = recipe.getIngredients();
			int width = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
			for(int i = 0; i < ingredients.size() && i < 9; i++) {
				Ingredient ingredient = ingredients.get(i);
				int slot = i / width * 3 + i % width;
				if(slot < 9 && !ingredient.isEmpty())
					grid[slot] = ingredient;
			}
			return grid;
		}
		return grid;
	}

	/** Reopens the last Necronomicon page (1.12.2 GuiHandler), for this book's tier */
	public static void open(int bookType) {
		GuiNecronomicon gui = GuiNecronomicon.currentNecro == null ? new GuiNecronomicon(bookType) : GuiNecronomicon.currentNecro.withBookType(bookType);
		Minecraft.getInstance().setScreen(gui);
	}
}
