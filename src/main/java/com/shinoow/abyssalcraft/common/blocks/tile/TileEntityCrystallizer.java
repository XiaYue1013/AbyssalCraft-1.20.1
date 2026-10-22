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
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.FuelType;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.recipe.CrystallizerRecipes;
import com.shinoow.abyssalcraft.common.inventory.ContainerProcessor;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Crystallizer: turns items into crystals (two outputs)
 */
public class TileEntityCrystallizer extends TileEntityProcessor {

	public TileEntityCrystallizer(BlockPos pos, BlockState state) {
		super(ACBlockEntities.CRYSTALLIZER.get(), pos, state, 2, "container.abyssalcraft.crystallizer");
	}

	@Override
	protected ItemStack[] getResults(ItemStack input) {
		return CrystallizerRecipes.instance().getCrystallizationResult(input);
	}

	@Override
	public int getFuelTime(ItemStack stack) {
		return getCrystallizationTime(stack);
	}

	public static int getCrystallizationTime(ItemStack stack)
	{
		if (stack.isEmpty())
			return 0;
		int fuelValue = AbyssalCraftAPI.getFuelValue(stack, FuelType.CRYSTALLIZER);
		if(fuelValue >= 0) return fuelValue;
		Item item = stack.getItem();
		if (item == ACItems.dread_fragment.get()) return 100;
		if (item == ACItems.dreaded_shard_of_abyssalnite.get()) return 1000;
		if (item == ACItems.dreaded_chunk_of_abyssalnite.get()) return 1600;
		if (item == Items.BLAZE_POWDER) return 1200;
		if (item == Items.BLAZE_ROD) return 2400;
		if (item == ACItems.methane.get()) return 10000;
		if (item == ACItems.carbon_cluster.get()) return 1600;
		if (item == ACItems.dense_carbon_cluster.get()) return 12800;
		if (item == ACItems.crystal(3).get()) return 200;
		if (ACBlocks.crystalCluster(3).isPresent() && item == ACBlocks.crystalCluster(3).get().asItem()) return 1800;
		return 0;
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return ContainerProcessor.crystallizer(id, inventory, this, data);
	}
}
