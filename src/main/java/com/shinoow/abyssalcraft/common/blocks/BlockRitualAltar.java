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
 *     1.20.1 port - one block per material (was the MATERIAL property); it drops the block it was made from
 *     through its loot table
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityRitualAltar;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACParticles;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.blocks.SingletonInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ritual Altar, created with a Necronomicon from a ring of blocks
 */
public class BlockRitualAltar extends BaseEntityBlock {

	private static final VoxelShape SHAPE = Block.box(2.4, 0, 2.4, 13.6, 16, 13.6);

	public BlockRitualAltar(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityRitualAltar(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, ACBlockEntities.RITUAL_ALTAR.get(), level.isClientSide ? TileEntityRitualAltar::clientTick : TileEntityRitualAltar::serverTick);
	}

	/** Candle flames, blue while a ritual is performed */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if(ACConfig.particleBlock){
			boolean performing = level.getBlockEntity(pos) instanceof IRitualAltar altar && altar.isPerformingRitual();
			ParticleOptions flame = performing ? ACParticles.BLUE_FLAME.get() : ParticleTypes.FLAME;
			for(double[] c : new double[][] {{0.75, 0.75}, {0.25, 0.75}, {0.25, 0.25}, {0.75, 0.25}}) {
				level.addParticle(ParticleTypes.SMOKE, pos.getX() + c[0], pos.getY() + 1.05, pos.getZ() + c[1], 0.0D, 0.0D, 0.0D);
				level.addParticle(flame, pos.getX() + c[0], pos.getY() + 1.05, pos.getZ() + c[1], 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide)
			return InteractionResult.SUCCESS;
		return SingletonInventoryUtil.handleBlockActivation(level, pos, player, player.getItemInHand(hand)) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.is(newState.getBlock()))
			SingletonInventoryUtil.dropItem(level, pos);
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
