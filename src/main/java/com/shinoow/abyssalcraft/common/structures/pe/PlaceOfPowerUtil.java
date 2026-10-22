/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - shared helpers for the Places of Power
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.structures.pe;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureBase;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureComponent;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

final class PlaceOfPowerUtil {

	private PlaceOfPowerUtil() {}

	static boolean isStatue(Level world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof BlockStatue;
	}

	static boolean isMonolithStone(BlockState state) {
		return state.is(ACBlocks.monolith_stone.get());
	}

	static boolean isMultiblock(Level world, BlockPos pos) {
		return world.getBlockState(pos).is(ACBlocks.multi_block.get());
	}

	/** Replaces the activation point with the multiblock and remembers the structure there */
	static void formBase(Level world, BlockPos pos, IPlaceOfPower place) {
		world.setBlockAndUpdate(pos, ACBlocks.multi_block.get().defaultBlockState());
		if(world.getBlockEntity(pos) instanceof IStructureBase base)
			base.setMultiblock(place);
	}

	/** Marks a structure component (statue) as part of (or no longer part of) the structure based at basePos */
	static void setComponent(Level world, BlockPos pos, boolean valid, BlockPos basePos) {
		if(world.getBlockEntity(pos) instanceof IStructureComponent component) {
			component.setInMultiblock(valid);
			component.setBasePosition(valid ? basePos : null);
		}
	}
}
