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
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.common.inventory.ContainerProcessor;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Transmutator: transmutes items into other items (one output), fueled by Coralium
 */
public class TileEntityTransmutator extends TileEntityProcessor {

	public TileEntityTransmutator(BlockPos pos, BlockState state) {
		super(ACBlockEntities.TRANSMUTATOR.get(), pos, state, 1, "container.abyssalcraft.transmutator");
	}

	@Override
	protected ItemStack[] getResults(ItemStack input) {
		return new ItemStack[] {TransmutatorRecipes.instance().getTransmutationResult(input)};
	}

	@Override
	public int getFuelTime(ItemStack stack) {
		return getItemBurnTime(stack);
	}

	public static int getItemBurnTime(ItemStack stack)
	{
		if (stack.isEmpty())
			return 0;
		int fuelValue = AbyssalCraftAPI.getFuelValue(stack, FuelType.TRANSMUTATOR);
		if(fuelValue >= 0) return fuelValue;
		Item item = stack.getItem();

		if (item == ACItems.coralium_plagued_flesh.get()) return 100;
		if (item == ACItems.coralium_plagued_flesh_on_a_bone.get()) return 100;
		if (item == ACItems.coralium_brick.get()) return 200;
		if (item == ACItems.coralium_gem.get()) return 200;
		if (item == ACItems.coralium_gem_cluster_2.get()) return 400;
		if (item == ACItems.coralium_gem_cluster_3.get()) return 600;
		if (item == ACItems.coralium_gem_cluster_4.get()) return 800;
		if (item == ACItems.coralium_gem_cluster_5.get()) return 1000;
		if (item == ACItems.coralium_gem_cluster_6.get()) return 1200;
		if (item == ACItems.coralium_gem_cluster_7.get()) return 1400;
		if (item == ACItems.coralium_gem_cluster_8.get()) return 1600;
		if (item == ACItems.coralium_gem_cluster_9.get()) return 1800;
		if (item == ACItems.coralium_pearl.get()) return 2000;
		if (item == ACItems.transmutation_gem.get()) return 10000;
		if (item == ACItems.chunk_of_coralium.get()) return 16200;
		if (item == Items.BLAZE_POWDER) return 1200;
		if (item == Items.BLAZE_ROD) return 2400;
		if (item == ACItems.methane.get()) return 10000;

		// Liquid Coralium containers: 20000 ticks per bucket
		return FluidUtil.getFluidContained(stack).map(fs -> {
			ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fs.getFluid());
			if(id != null && id.getNamespace().equals("abyssalcraft") && id.getPath().equals("liquid_coralium"))
				return (int)(fs.getAmount() / 1000F * 20000F);
			return 0;
		}).orElse(0);
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return ContainerProcessor.transmutator(id, inventory, this, data);
	}
}
