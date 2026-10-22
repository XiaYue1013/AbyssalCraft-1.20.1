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

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

/**
 * Transmutator recipe registry.<br>
 * Inputs are {@link Ingredient}s (so tags resolve lazily), outputs are suppliers.
 * @author shinoow
 *
 * @since 1.0
 */
public class TransmutatorRecipes {

	private static final TransmutatorRecipes transmutationBase = new TransmutatorRecipes();

	private final List<Transmutation> transmutationList = new ArrayList<>();

	public static TransmutatorRecipes instance()
	{
		return transmutationBase;
	}

	private TransmutatorRecipes()
	{
		transmute(Ingredient.of(Items.DIAMOND), new ItemStack(Items.COAL, 64), 0.2F);
		transmute(Ingredient.of(Items.WATER_BUCKET), new ItemStack(Blocks.ICE, 8), 0.0F);
		transmute(Ingredient.of(ItemTags.WOOL), new ItemStack(Items.STRING, 4), 0.0F);
		transmute(Ingredient.of(Blocks.GRAVEL), new ItemStack(Items.FLINT, 2), 0.0F);
		transmute(Ingredient.of(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ, 4), 0.0F);
		transmute(Ingredient.of(Blocks.NETHER_BRICKS), new ItemStack(Items.NETHER_BRICK, 4), 0.0F);
		transmute(Ingredient.of(Items.NETHER_BRICK), new ItemStack(Blocks.NETHERRACK), 0.0F);
		transmute(Ingredient.of(Items.WHEAT), new ItemStack(Items.WHEAT_SEEDS), 0.0F);
		transmute(Ingredient.of(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT), 0.0F);
	}

	public void transmute(Ingredient input, ItemStack output, float xp)
	{
		ItemStack o = output.copy();
		transmute(input, () -> o, xp);
	}

	public void transmute(Ingredient input, Supplier<ItemStack> output, float xp)
	{
		transmutationList.add(new Transmutation(input, output, xp));
	}

	/**
	 * Fetches the transmutation result of an ItemStack (or an empty stack if there's no recipe)
	 */
	public ItemStack getTransmutationResult(ItemStack stack)
	{
		if(stack.isEmpty()) return ItemStack.EMPTY;
		for(Transmutation recipe : transmutationList)
			if(recipe.input().test(stack) && !recipe.output().get().isEmpty())
				return recipe.output().get().copy();
		return ItemStack.EMPTY;
	}

	public List<Transmutation> getTransmutationList()
	{
		return transmutationList;
	}

	/**
	 * Experience gained when taking this output out of the Transmutator
	 */
	public float getExperience(ItemStack output)
	{
		for(Transmutation recipe : transmutationList)
			if(ItemStack.isSameItem(recipe.output().get(), output))
				return recipe.xp();
		return 0.0F;
	}

	public record Transmutation(Ingredient input, Supplier<ItemStack> output, float xp) {}
}
