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
package com.shinoow.abyssalcraft.api.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Crystallizer recipe registry.<br>
 * Inputs are {@link Ingredient}s (so tags resolve lazily), outputs are suppliers so tag based outputs
 * can be resolved after tags have loaded.
 * @author shinoow
 *
 * @since 1.0
 */
public class CrystallizerRecipes {

	private static final CrystallizerRecipes crystallizationBase = new CrystallizerRecipes();

	private final List<Crystallization> crystallizationList = new ArrayList<>();

	public static CrystallizerRecipes instance()
	{
		return crystallizationBase;
	}

	private CrystallizerRecipes(){}

	public void crystallize(Ingredient input, ItemStack output1, ItemStack output2, float xp)
	{
		ItemStack o1 = output1.copy(), o2 = output2.copy();
		crystallize(input, () -> o1, () -> o2, xp);
	}

	public void crystallize(Ingredient input, Supplier<ItemStack> output1, Supplier<ItemStack> output2, float xp)
	{
		crystallizationList.add(new Crystallization(input, output1, output2, xp));
	}

	/**
	 * Fetches the crystallization results of an ItemStack
	 * @return An array of two stacks (the second can be empty), or two empty stacks if there's no recipe
	 */
	public ItemStack[] getCrystallizationResult(ItemStack stack)
	{
		Crystallization recipe = getRecipe(stack);
		if(recipe == null) return new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY};
		return new ItemStack[] {recipe.output1().get().copy(), recipe.output2().get().copy()};
	}

	public Crystallization getRecipe(ItemStack stack)
	{
		if(stack.isEmpty()) return null;
		for(Crystallization recipe : crystallizationList)
			if(recipe.input().test(stack) && !recipe.output1().get().isEmpty())
				return recipe;
		return null;
	}

	public List<Crystallization> getCrystallizationList()
	{
		return crystallizationList;
	}

	/**
	 * Experience gained when taking an output of this crystallization out of the Crystallizer
	 */
	public float getExperience(ItemStack output)
	{
		for(Crystallization recipe : crystallizationList)
			if(ItemStack.isSameItem(recipe.output1().get(), output))
				return recipe.xp();
		return 0.0F;
	}

	public record Crystallization(Ingredient input, Supplier<ItemStack> output1, Supplier<ItemStack> output2, float xp) {}
}
