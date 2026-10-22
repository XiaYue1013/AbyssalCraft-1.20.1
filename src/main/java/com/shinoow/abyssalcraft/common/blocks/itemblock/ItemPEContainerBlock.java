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
 *     1.20.1 port - ItemPEContainerBlock, ItemMetadataPEContainerBlock and the ItemTiered*Block classes merged
 *     (capacity from the block and its tier)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.itemblock;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyCollector;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyContainer;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyPedestal;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyRelay;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.common.blocks.BlockRendingPedestal;
import com.shinoow.abyssalcraft.common.blocks.BlockSacrificialAltar;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Block item of a PE storing block. Keeps the stored PE ("PotEnergy") and can be charged/drained like any PE item.
 */
public class ItemPEContainerBlock extends ItemBlockAC implements IEnergyContainerItem {

	public ItemPEContainerBlock(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if(getBlock() instanceof BlockEnergyRelay relay)
			tooltip.add(Component.literal(String.format("Range: %d Blocks", relay.getTier() == BlockPE.BASE ? 4 : 6 + 2 * relay.getTier())));
	}

	@Override
	public float getContainedEnergy(ItemStack stack) {
		return PEUtils.getContainedEnergy(stack);
	}

	@Override
	public int getMaxEnergy(ItemStack stack) {
		Block block = getBlock();
		int tier = BlockPE.getTier(block.defaultBlockState());
		if(block instanceof BlockRendingPedestal)
			return 5000;
		if(block instanceof BlockEnergyPedestal || block instanceof BlockSacrificialAltar)
			return BlockPE.tieredCapacity(5000, tier);
		if(block instanceof BlockEnergyCollector)
			return BlockPE.tieredCapacity(1000, tier);
		if(block instanceof BlockEnergyRelay)
			return tier == BlockPE.BASE ? 500 : 600 + 100 * tier;
		if(block instanceof BlockEnergyContainer)
			return switch(tier) {
			case BlockPE.OVERWORLD -> 20000;
			case BlockPE.ABYSSAL_WASTELAND -> 60000;
			case BlockPE.DREADLANDS -> 240000;
			case BlockPE.OMOTHOL -> 1200000;
			default -> 10000;
			};
		return 0;
	}

	@Override
	public void addEnergy(ItemStack stack, float energy) {
		PEUtils.addEnergy(this, stack, energy);
	}

	@Override
	public float consumeEnergy(ItemStack stack, float energy) {
		return PEUtils.consumeEnergy(stack, energy);
	}

	@Override
	public boolean canAcceptPE(ItemStack stack) {
		return !(getBlock() instanceof BlockSacrificialAltar) && getContainedEnergy(stack) < getMaxEnergy(stack);
	}

	@Override
	public boolean canTransferPE(ItemStack stack) {
		return getContainedEnergy(stack) > 0;
	}
}
