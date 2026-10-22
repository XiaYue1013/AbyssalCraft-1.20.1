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

import java.util.*;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.item.ItemEngraving;

import net.minecraft.world.item.ItemStack;

/**
 * Engraver recipe registry
 * @author shinoow
 *
 * @since 1.2
 */
public class EngraverRecipes {

	private static final EngraverRecipes engravingBase = new EngraverRecipes();
	/** List of coins that can be engraved */
	private final List<ItemStack> coins = new ArrayList<>();
	/** Engraving template -> resulting coin */
	private final Map<ItemEngraving, ItemStack> engravings = new LinkedHashMap<>();
	/** Resulting coin -> experience */
	private final List<Map.Entry<ItemStack, Float>> experienceList = new ArrayList<>();

	public static EngraverRecipes instance()
	{
		return engravingBase;
	}

	private EngraverRecipes(){}

	public void addCoin(ItemStack coin){
		coins.add(coin);
	}

	public void addEngraving(ItemStack coin, ItemEngraving engraving, float xp){
		engravings.put(engraving, coin);
		experienceList.add(new AbstractMap.SimpleEntry<>(coin, xp));
	}

	/**
	 * Fetches the coin (from the coin list) that matches the stack
	 */
	public ItemStack getEngravingResult(ItemStack stack)
	{
		return coins.stream().filter(i -> areStacksEqual(stack, i)).findFirst().orElse(ItemStack.EMPTY);
	}

	/**
	 * Fetches the result of engraving a coin with a template<br>
	 * Blank Engravings only work on already engraved coins (turning them back), every other template
	 * only works on a plain coin.
	 */
	public ItemStack getEngravingResult(ItemStack coin, ItemEngraving engraving)
	{
		for(ItemStack stack : coins)
			if(areStacksEqual(coin, stack))
				if(engravings.get(engraving) != null)
					if(coin.getItem() != ACItems.coin.get() && engraving == ACItems.blank_engraving.get() ||
					coin.getItem() == ACItems.coin.get() && engraving != ACItems.blank_engraving.get())
						return engravings.get(engraving);
		return ItemStack.EMPTY;
	}

	private boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return stack2.getItem() == stack1.getItem() || stack1.getItem() == ACItems.coin.get();
	}

	public List<ItemStack> getCoinList(){
		return coins;
	}

	public Map<ItemEngraving, ItemStack> getEngravings(){
		return engravings;
	}

	public float getExperience(ItemStack stack)
	{
		return experienceList.stream()
				.filter(e -> ItemStack.isSameItem(stack, e.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(0.0F);
	}
}
