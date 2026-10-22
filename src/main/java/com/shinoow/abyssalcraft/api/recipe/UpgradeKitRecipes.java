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

import com.shinoow.abyssalcraft.api.item.ItemUpgradeKit;

import net.minecraft.world.item.ItemStack;

/**
 * Upgrade Kit recipe registry
 * @author shinoow
 *
 * @since 1.3
 */
public class UpgradeKitRecipes {

	private static final UpgradeKitRecipes instance = new UpgradeKitRecipes();

	private final Map<ItemUpgradeKit, Map<ItemStack, ItemStack>> upgrades = new LinkedHashMap<>();

	public static UpgradeKitRecipes instance(){
		return instance;
	}

	private UpgradeKitRecipes() {}

	public void addUpgradeKit(ItemUpgradeKit kit){
		upgrades.putIfAbsent(kit, new LinkedHashMap<>());
	}

	public void addUpgrade(ItemUpgradeKit kit, ItemStack input, ItemStack output){
		upgrades.computeIfAbsent(kit, k -> new LinkedHashMap<>()).put(input, output);
	}

	public Map<ItemStack, ItemStack> getUpgrades(ItemUpgradeKit kit){
		return upgrades.getOrDefault(kit, Collections.emptyMap());
	}

	/**
	 * Fetches the upgraded version of the input for the kit (or an empty stack)
	 */
	public ItemStack getUpgrade(ItemUpgradeKit kit, ItemStack input){
		return getUpgrades(kit).entrySet().stream()
				.filter(e -> ItemStack.isSameItem(input, e.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(ItemStack.EMPTY).copy();
	}

	public Map<ItemUpgradeKit, Map<ItemStack, ItemStack>> getAllUpgrades(){
		return upgrades;
	}
}
