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
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Abyssal Wasteland plants (Luminous Thistle, Wastelands Thorn)
 */
public class BlockACPlant extends BushBlock {

	private static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
	private final boolean thorny;

	public BlockACPlant(Properties properties, boolean thorny) {
		super(properties);
		this.thorny = thorny;
	}

	@Override
	protected boolean mayPlaceOn(BlockState ground, BlockGetter level, BlockPos pos) {
		return ground.is(ACBlocks.fused_abyssal_sand.get()) || ground.is(ACBlocks.abyssal_sand.get()) ||
				ground.is(BlockTags.DIRT) || super.mayPlaceOn(ground, level, pos);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		// Wastelands Thorn hurts players walking through it without leg protection
		if(thorny && !level.isClientSide && entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET).isEmpty() &&
				player.getItemBySlot(EquipmentSlot.LEGS).isEmpty())
			entity.hurt(level.damageSources().cactus(), 1.0F);
	}
}
