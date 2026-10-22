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
package com.shinoow.abyssalcraft.common.disruptions;

import java.util.List;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Covers the area with Shoggoth Ooze
 */
public class DisruptionOoze extends DisruptionEntry {

	public DisruptionOoze() {
		super("ooze", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world.isClientSide) return;
		Block ooze = ACBlocks.shoggoth_ooze.get();
		int num = world.random.nextInt(4) + 2;
		for(int i = -1*num; i < num+1; i++)
			for(int j = -1*num; j < num+1; j++)
				for(int k = -1*num; k < num+1; k++) {
					BlockPos pos1 = pos.offset(i, j, k);
					if(i > -2 && i < 2 && j > -2 && j < 2 && k > -2 && k < 2 && world.random.nextBoolean() || world.random.nextInt(3) == 0) {
						BlockState state = world.getBlockState(pos1);
						if(state.canBeReplaced() && state.getFluidState().isEmpty() && ooze.defaultBlockState().canSurvive(world, pos1))
							if(!state.is(ooze))
								world.setBlockAndUpdate(pos1, ooze.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1 + world.random.nextInt(5)));
							else if(state.getValue(SnowLayerBlock.LAYERS) < 8)
								world.setBlockAndUpdate(pos1, state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) + 1));
					}
				}
	}
}
