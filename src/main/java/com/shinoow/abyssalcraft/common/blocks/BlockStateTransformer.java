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

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityStateTransformer;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * State Transformer (shows the inserted Stone Tablet on top)
 */
public class BlockStateTransformer extends BlockMachine<TileEntityStateTransformer> {

	public static final BooleanProperty TABLET = BooleanProperty.create("tablet");

	public BlockStateTransformer(Properties properties) {
		super(properties, TileEntityStateTransformer::new, ACBlockEntities.STATE_TRANSFORMER, TileEntityStateTransformer::serverTick, null,
				Block.box(0, 0, 0, 16, 16, 16), true);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TABLET, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(TABLET, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(TABLET);
	}
}
