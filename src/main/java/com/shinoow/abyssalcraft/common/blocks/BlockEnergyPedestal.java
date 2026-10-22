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
 *     1.20.1 port - BlockTieredEnergyPedestal merged in (tier = constructor parameter)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyPedestal;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.lib.util.blocks.SingletonInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Collects PE from statues; holds an item that gets charged with it
 */
public class BlockEnergyPedestal extends BlockPE<TileEntityEnergyPedestal> {

	public BlockEnergyPedestal(Properties properties, int tier) {
		super(properties, tier, Block.box(4, 0, 4, 12, 16, 12), TileEntityEnergyPedestal::new, ACBlockEntities.ENERGY_PEDESTAL, TileEntityEnergyPedestal::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide)
			return InteractionResult.SUCCESS;
		return SingletonInventoryUtil.handleBlockActivation(level, pos, player, player.getItemInHand(hand)) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}
}
