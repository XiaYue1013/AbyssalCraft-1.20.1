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
 *     1.20.1 port - the altar material is one block per material instead of metadata; the free space check
 *     is the same list of positions as 1.12.2
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib.util;

import java.util.Arrays;
import java.util.List;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.block.IRitualPedestal;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Utility class used for assembling Ritual grounds.
 * @author shinoow
 *
 */
public class RitualUtil {

	public static final List<BlockPos> PEDESTAL_POSITIONS = Arrays.asList(
			new BlockPos(-3, 0, 0), new BlockPos(0, 0, -3),
			new BlockPos(3, 0, 0), new BlockPos(0, 0, 3),
			new BlockPos(-2, 0, 2), new BlockPos(-2, 0, -2),
			new BlockPos(2, 0, 2), new BlockPos(2, 0, -2)
			);

	/** Positions around the altar that have to be free of full blocks */
	private static final int[][] FREE = {{-3, -1}, {-3, 1}, {-4, 0}, {-4, -1}, {-4, 1}, {-3, -2}, {-3, -3}, {-2, -3}, {-1, -3}, {-1, -4},
			{0, -4}, {1, -4}, {1, -3}, {2, -3}, {3, -3}, {3, -2}, {3, -1}, {4, -1}, {4, 0}, {4, 1}, {3, 1}, {3, 2}, {3, 3}, {2, 3}, {1, 3},
			{1, 4}, {0, 4}, {-1, 4}, {-1, 3}, {-2, 3}, {-3, 3}, {-3, 2}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {-2, 0}, {-2, -1},
			{-1, -1}, {-1, -2}, {0, -2}, {1, -2}, {1, -1}, {2, -1}, {2, 0}, {2, 1}, {1, 1}, {1, 2}, {0, 2}, {-1, 2}};

	/**
	 * Ritual ground materials: index = altar material (see {@link BlockHandler#RITUAL_ALTARS}), value = lowest book type able to use it
	 */
	private static Block[] materials() {
		return new Block[] {Blocks.COBBLESTONE, ACBlocks.darkstone_cobblestone.get(), ACBlocks.abyssal_cobblestone.get(),
				ACBlocks.coralium_cobblestone.get(), ACBlocks.dreadstone_cobblestone.get(), ACBlocks.abyssalnite_cobblestone.get(),
				ACBlocks.ethaxium_bricks.get(), ACBlocks.dark_ethaxium_bricks.get()};
	}

	private static final int[] BOOK_TYPES = {0, 0, 1, 1, 2, 2, 3, 3};

	/**
	 * @return The altar material index of the block, or -1 if it can't be used for ritual grounds
	 */
	public static int getMaterial(BlockState state) {
		Block[] materials = materials();
		for(int i = 0; i < materials.length; i++)
			if(state.is(materials[i]))
				return i;
		return -1;
	}

	/**
	 * Lowest book type able to build ritual grounds of the material
	 */
	public static int getRequiredBookType(int material) {
		return BOOK_TYPES[material];
	}

	/**
	 * The block a ritual altar/pedestal of the material was made from
	 */
	public static Block getMaterialBlock(int material) {
		return materials()[material];
	}

	/**
	 * Checks if an altar can be created, and creates it (server side)
	 * @param world World object
	 * @param pos Position of the block that becomes the altar
	 * @param bookType Level of the current Necronomicon held
	 * @return True if a Ritual Altar can be constructed, otherwise false
	 */
	public static boolean tryAltar(Level world, BlockPos pos, int bookType, Player player){
		BlockState ritualBlock = world.getBlockState(pos);
		int material = getMaterial(ritualBlock);
		if(material < 0 || bookType < BOOK_TYPES[material])
			return false;
		for(BlockPos p : PEDESTAL_POSITIONS)
			if(world.getBlockState(pos.offset(p)) != ritualBlock)
				return false;
		for(int[] f : FREE) {
			BlockPos p = pos.offset(f[0], 0, f[1]);
			if(world.getBlockState(p).isCollisionShapeFullBlock(world, p))
				return false;
		}
		if(RitualRegistry.instance().sameBookType(world.dimension(), BOOK_TYPES[material]) && sameChunk(world, pos, player)) {
			if(!world.isClientSide)
				createAltar(world, pos, material);
			return true;
		}
		return false;
	}

	/**
	 * Replaces the center and the eight pedestal blocks with the ritual altar and pedestals of the material
	 */
	private static void createAltar(Level world, BlockPos pos, int material){
		world.destroyBlock(pos, false);
		for(BlockPos p : PEDESTAL_POSITIONS)
			world.destroyBlock(pos.offset(p), false);
		world.setBlock(pos, BlockHandler.RITUAL_ALTARS.get(material).get().defaultBlockState(), 2);
		for(BlockPos p : PEDESTAL_POSITIONS) {
			world.setBlock(pos.offset(p), BlockHandler.RITUAL_PEDESTALS.get(material).get().defaultBlockState(), 2);
			if(world.getBlockEntity(pos.offset(p)) instanceof IRitualPedestal pedestal)
				pedestal.setAltar(pos);
		}
	}

	/**
	 * Validates all blocks are within the same chunk
	 * @param world World object
	 * @param pos Block Position
	 * @return True if the blocks are in the same chunk, otherwise false with particles on the affected blocks
	 */
	private static boolean sameChunk(Level world, BlockPos pos, Player player) {
		ChunkPos chunk = new ChunkPos(pos);
		if(PEDESTAL_POSITIONS.stream().map(p -> new ChunkPos(pos.offset(p))).allMatch(chunk::equals))
			return true;
		if(world.isClientSide) {
			PEDESTAL_POSITIONS.stream()
			.map(pos::offset)
			.filter(p -> !new ChunkPos(p).equals(chunk))
			.forEach(p -> world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState()),
					p.getX()+0.5, p.getY()+1.5, p.getZ()+0.5, 0, 0, 0));
			player.displayClientMessage(Component.translatable("message.ritual.notsamechunk"), true);
		}
		return false;
	}
}
