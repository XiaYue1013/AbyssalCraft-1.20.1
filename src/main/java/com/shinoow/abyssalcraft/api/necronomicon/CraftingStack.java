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
package com.shinoow.abyssalcraft.api.necronomicon;

import java.util.function.Function;

import com.shinoow.abyssalcraft.api.APIUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;


/**
 * Simple collection of ItemStacks used for displaying crafting recipes in the Necronomicon
 * @author shinoow
 *
 * @since 1.3.5
 */
public class CraftingStack {

	private ItemStack output;
	private Object[] recipe = new Object[9];

	/**
	 * Simple collection of Objects used to display a recipe
	 * @param output The Item/Block/ItemStack that's crafted
	 * @param recipe The Items/Blocks/ItemStacks used to craft said Item/Block/ItemStack (has to contain 9 elements, all of them can be null)
	 */
	public CraftingStack(Object output, Object...recipe){
		if(output != null){
			if(recipe != null){
				if(recipe.length == 9){
					this.output = APIUtils.convertToStack(output);
					this.recipe = recipe;
				} else throw new ArrayIndexOutOfBoundsException("The array must contain preciesly 9 elements, not "+recipe.length+"!");
			} else throw new NullPointerException("This array can't be empty!");
		} else throw new NullPointerException("Output can't be null!");
	}

	/**
	 * Simple collection of Objects used to display a recipe.<br>
	 * This version looks through the CraftingManager for the<br>
	 * Item/Block's Crafting Recipe.
	 * @param output The Item/Block/ItemStack that's crafted
	 */	/**
	 * Resolves the crafting grid (9 ingredients) for an output, set by the client (recipes are per world in 1.20.1)
	 */
	public static Function<ItemStack, Object[]> recipeLookup = stack -> new Object[9];

	private boolean lookupPending;

	/**
	 * A crafting recipe that is looked up from the recipe manager the first time it's displayed
	 * @param output The output of the recipe to look up
	 */
	public CraftingStack(Object output){
		if(output != null){
			this.output = APIUtils.convertToStack(output);
			lookupPending = true;
		} else throw new NullPointerException("Output can't be null!");
	}

	public ItemStack getOutput(){
		return output;
	}

	/**
	 * Getter for the ItemStack array containing the recipe
	 * @return An array of ItemStacks representing the crafting recipe
	 * @deprecated Use the version that returns an Object array instead
	 */
	@Deprecated
	public ItemStack[] getCraftingRecipe(){
		return new ItemStack[0];
	}

	/**
	 * Getter for the first 3 positioned ItemStacks in the crafting grid
	 * @return An ItemStack array with 3 ItemStacks
	 * @deprecated Use the version that returns an Object array instead
	 */
	@Deprecated
	public ItemStack[] getFirstArray(){
		return new ItemStack[0];
	}

	/**
	 * Getter for the second 3 positioned ItemStacks in the crafting grid
	 * @return An ItemStack array with 3 ItemStacks
	 * @deprecated Use the version that returns an Object array instead
	 */
	@Deprecated
	public ItemStack[] getSecondArray(){
		return new ItemStack[0];
	}

	/**
	 * Getter for the third 3 positioned ItemStacks in the crafting grid
	 * @return An ItemStack array with 3 ItemStacks
	 * @deprecated Use the version that returns an Object array instead
	 */
	@Deprecated
	public ItemStack[] getThirdArray(){
		return new ItemStack[0];
	}

	/**
	 * Getter for the Object array containing the recipe
	 * @return An array of Objects representing the crafting recipe
	 */	public Object[] getRecipe(){
		if(lookupPending) {
			lookupPending = false;
			Object[] found = recipeLookup.apply(output);
			if(found != null && found.length == 9)
				recipe = found;
		}
		return recipe;
	}

	/**
	 * Easier way to make CraftingStack arrays
	 * @param stacks A bunch of CraftingStacks
	 * @return An Array with the CraftingStacks
	 * @deprecated The usefulness of this was superseded in the page/chapter NecroData structure
	 */
	@Deprecated
	public static CraftingStack[] arrayFrom(CraftingStack...stacks){
		return stacks;
	}

	@Override
	public String toString(){
		return "CraftingStack{Output: "+ output.toString()+ ", Recipe: "+ recipe.toString() +"}";
	}
}
