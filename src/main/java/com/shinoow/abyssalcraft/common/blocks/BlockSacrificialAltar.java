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
 *     1.20.1 port - BlockTieredSacrificialAltar merged in (tier = constructor parameter)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySacrificialAltar;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.lib.util.blocks.SingletonInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Collects PE from mobs killed near it; holds an item that gets charged with it
 */
public class BlockSacrificialAltar extends BlockPE<TileEntitySacrificialAltar> {

	public BlockSacrificialAltar(Properties properties, int tier) {
		super(properties, tier, Block.box(2.4, 0, 2.4, 13.6, 14.4, 13.6), TileEntitySacrificialAltar::new, ACBlockEntities.SACRIFICIAL_ALTAR,
				TileEntitySacrificialAltar::serverTick);
	}

	/** Lava particles while cooling down */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if(level.getBlockEntity(pos) instanceof TileEntitySacrificialAltar altar && altar.getCooldownTimer() > 0)
			level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide)
			return InteractionResult.SUCCESS;
		return SingletonInventoryUtil.handleBlockActivation(level, pos, player, player.getItemInHand(hand)) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}
}
