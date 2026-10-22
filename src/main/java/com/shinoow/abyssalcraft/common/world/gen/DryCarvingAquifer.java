/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - see below
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * 1.20.1 carvers ask the aquifer what fills a carved block, and below sea level a disabled aquifer answers with the
 * dimension's "fluid": Liquid Coralium in the Abyssal Wasteland, and the solid fillers of the Dreadlands (abyssalnite
 * stone) and the Dark Realm (darkstone). 1.12.2 caves were always carved as air there (its ocean check only looked
 * for water), so this answers with air instead. Lava below the carver's lava level is decided before the aquifer.
 */
public record DryCarvingAquifer(Aquifer aquifer) implements Aquifer {

	@Override
	public BlockState computeSubstance(DensityFunction.FunctionContext context, double density) {
		BlockState state = aquifer.computeSubstance(context, density);
		return state == null || state.isAir() ? state : Blocks.CAVE_AIR.defaultBlockState();
	}

	@Override
	public boolean shouldScheduleFluidUpdate() {
		return aquifer.shouldScheduleFluidUpdate();
	}
}
