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

import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Dreadlands Infused Powerstone, found in the Abyssal Stronghold (tracked by the Powerstone Tracker)
 */
public class BlockPSDL extends BlockShaped {

	public BlockPSDL(Properties properties) {
		super(properties, box(3.2, 0, 3.2, 12.8, 16, 12.8));
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if(ACConfig.particleBlock)
			if (rand.nextInt(10) == 0)
				level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + rand.nextFloat(), pos.getY() + 1.1F, pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
	}
}
