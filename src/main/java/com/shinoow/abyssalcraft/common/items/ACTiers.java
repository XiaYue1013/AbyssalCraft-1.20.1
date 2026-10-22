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
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

/**
 * Tool tiers (1.12.2 tool materials). Abyssalnite and above sort after netherite, so blocks needing
 * 1.12.2 harvest level 4+ can only be mined with AbyssalCraft tools, like in 1.12.2.
 */
public class ACTiers {

	public static final Tier DARKSTONE = TierSortingRegistry.registerTier(
			new ForgeTier(1, 180, 5.0F, 1.0F, 5, BlockTags.NEEDS_STONE_TOOL, () -> Ingredient.of(ACBlocks.darkstone_cobblestone.get())),
			AbyssalCraft.loc("darkstone"), List.of(Tiers.STONE), List.of(Tiers.IRON));
	public static final Tier ABYSSALNITE = TierSortingRegistry.registerTier(
			new ForgeTier(4, 1261, 10.0F, 4.0F, 12, ACTags.Blocks.NEEDS_ABYSSALNITE_TOOL, () -> Ingredient.of(ACItems.abyssalnite_ingot.get())),
			AbyssalCraft.loc("abyssalnite"), List.of(Tiers.NETHERITE), List.of());
	public static final Tier REFINED_CORALIUM = TierSortingRegistry.registerTier(
			new ForgeTier(5, 1800, 12.0F, 5.0F, 13, ACTags.Blocks.NEEDS_REFINED_CORALIUM_TOOL, () -> Ingredient.of(ACItems.refined_coralium_ingot.get())),
			AbyssalCraft.loc("refined_coralium"), List.of(AbyssalCraft.loc("abyssalnite")), List.of());
	public static final Tier DREADIUM = TierSortingRegistry.registerTier(
			new ForgeTier(6, 2300, 14.0F, 6.0F, 14, ACTags.Blocks.NEEDS_DREADIUM_TOOL, () -> Ingredient.of(ACItems.dreadium_ingot.get())),
			AbyssalCraft.loc("dreadium"), List.of(AbyssalCraft.loc("refined_coralium")), List.of());
	public static final Tier ETHAXIUM = TierSortingRegistry.registerTier(
			new ForgeTier(8, 2800, 16.0F, 8.0F, 20, ACTags.Blocks.NEEDS_ETHAXIUM_TOOL, () -> Ingredient.of(ACItems.ethaxium_ingot.get())),
			AbyssalCraft.loc("ethaxium"), List.of(AbyssalCraft.loc("dreadium")), List.of());

	/**
	 * Dreadium Katana parts (1.12.2 materials dreadiumkatana_hilt / dreadiumkatana_katana, repaired with Dreadium Crystals).
	 * Swords only, so they aren't sorted with the mining tiers.
	 */
	public static final Tier DREADIUM_KATANA_HILT = new ForgeTier(0, 200, 1.0F, 1.0F, 0, Tags.Blocks.NEEDS_WOOD_TOOL,
			() -> Ingredient.of(ACItems.crystal(14).get()));
	public static final Tier DREADIUM_KATANA = new ForgeTier(0, 1000, 1.0F, 11.0F, 14, Tags.Blocks.NEEDS_WOOD_TOOL,
			() -> Ingredient.of(ACItems.crystal(14).get()));

	/** Forces class initialization (tiers have to be registered during mod construction) */
	public static void init() {}
}
