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

import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

import java.util.Set;

import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.ChatFormatting;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ethaxium Pickaxe, mines ethaxium blocks ten times as fast
 */
public class ItemEthaxiumPickaxe extends ItemACPickaxe {

	private static Set<Block> effectiveBlocks;

	public ItemEthaxiumPickaxe(Tier tier) {
		super(tier, 1, -2.8F, ChatFormatting.AQUA);
	}

	private static Set<Block> getEffectiveBlocks() {
		if(effectiveBlocks == null)
			effectiveBlocks = java.util.stream.Stream.of(ACBlocks.ethaxium_bricks, ACBlocks.chiseled_ethaxium_bricks, ACBlocks.cracked_ethaxium_bricks,
					ACBlocks.ethaxium_pillar, ACBlocks.ethaxium_brick_fence, ACBlocks.ethaxium_brick_slab, ACBlocks.ethaxium_brick_stairs,
					ACBlocks.materializer, ACBlocks.dark_ethaxium_bricks, ACBlocks.chiseled_dark_ethaxium_bricks, ACBlocks.cracked_dark_ethaxium_bricks,
					ACBlocks.dark_ethaxium_pillar, ACBlocks.dark_ethaxium_brick_stairs, ACBlocks.dark_ethaxium_brick_slab,
					ACBlocks.dark_ethaxium_brick_fence, ACBlocks.ethaxium_block, ACBlocks.ethaxium)
					.filter(RegistryObject::isPresent).map(RegistryObject::get).collect(Collectors.toSet());
		return effectiveBlocks;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if(getEffectiveBlocks().contains(state.getBlock()))
			return getTier().getSpeed() * 10;
		if(state.is(BlockTags.MINEABLE_WITH_PICKAXE))
			return getTier().getSpeed();
		return super.getDestroySpeed(stack, state);
	}
}
