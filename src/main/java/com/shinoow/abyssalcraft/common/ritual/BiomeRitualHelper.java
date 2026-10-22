/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - shared code of the rituals that transform the land around them (Corruption, Cleansing, Purging,
 *     Curing). They process one block column per scheduled step, 256 columns every 2 ticks, like 1.12.2.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.shinoow.abyssalcraft.lib.util.ScheduledProcess;
import com.shinoow.abyssalcraft.lib.util.Scheduler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BiomeRitualHelper {

	/** Biome of a column (biomes were 2D in 1.12.2, sampled at sea level) */
	public static Holder<Biome> biome(Level world, int x, int z) {
		return world.getBiome(new BlockPos(x, world.getSeaLevel(), z));
	}

	/** Whether a column in the 49x49 area around the position matches */
	public static boolean anyNearby(Level world, BlockPos pos, Predicate<Holder<Biome>> test) {
		for(int x = pos.getX() - 24; x < pos.getX() + 25; x++)
			for(int z = pos.getZ() - 24; z < pos.getZ() + 25; z++)
				if(test.test(biome(world, x, z)))
					return true;
		return false;
	}

	/**
	 * Schedules the transformation of every matching column within the range (in blocks) of the position
	 * @param column Called with the bottom of the column (the minimum build height)
	 */
	public static void transformColumns(Level world, BlockPos pos, int range, Predicate<Holder<Biome>> applicable, Consumer<BlockPos> column) {
		int num = 1, num2 = 0;
		for(int x = pos.getX() - range; x < pos.getX() + range + 1; x++)
			for(int z = pos.getZ() - range; z < pos.getZ() + range + 1; z++){
				if(!applicable.test(biome(world, x, z))) continue;
				BlockPos bottom = new BlockPos(x, world.getMinBuildHeight(), z);
				Scheduler.schedule(ScheduledProcess.of(num * 2, () -> {
					if(world.isLoaded(bottom))
						column.accept(bottom);
				}));
				num2++;
				if(num2 % 256 == 0)
					num++;
			}
	}

	/**
	 * The state of another block with every property the two blocks share copied over (stairs keep their facing etc.)
	 */
	public static BlockState copyProperties(BlockState from, Block to) {
		BlockState state = to.defaultBlockState();
		for(Property<?> property : from.getProperties())
			if(state.hasProperty(property))
				state = copy(from, state, property);
		return state;
	}

	private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}
}
