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
 *     1.20.1 port - the Darklands marker interface is a biome tag; the 1.12.2 block conversions mapped onto the
 *     flattened blocks
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import java.util.HashMap;
import java.util.Map;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Turns the Darklands around it back into regular biomes
 */
public class NecronomiconCleansingRitual extends NecronomiconRitual {

	public NecronomiconCleansingRitual() {
		super("cleansing", 4, Level.OVERWORLD, 100000F, true, new Object[]{BlockHandler.statueIngredient(), BlockHandler.statueIngredient(),
				BlockHandler.statueIngredient(), BlockHandler.statueIngredient(), BlockHandler.statueIngredient(), BlockHandler.statueIngredient(),
				BlockHandler.statueIngredient(), BlockHandler.statueIngredient()});
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return BiomeRitualHelper.anyNearby(world, pos, b -> b.is(ACBiomes.IS_DARKLANDS));
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {
		SpecialTextUtil.JzaharText("Undoing my malice on your "+ChatFormatting.ITALIC+"beloved"+ChatFormatting.RESET+" world? What a shame.");
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		Map<Block, Block> conversions = conversions();
		BiomeRitualHelper.transformColumns(world, pos, ACConfig.cleansingRitualRange * 8, b -> b.is(ACBiomes.IS_DARKLANDS), bottom -> {
			ResourceKey<Biome> b = getRealBiome(BiomeRitualHelper.biome(world, bottom.getX(), bottom.getZ()));
			for(BlockPos.MutableBlockPos p = bottom.mutable(); p.getY() < world.getMaxBuildHeight(); p.move(0, 1, 0)){
				BlockState state = world.getBlockState(p);
				if(state.isAir()) continue;
				Block result = conversions.get(state.getBlock());
				if(result != null)
					world.setBlock(p, BiomeRitualHelper.copyProperties(state, result), 2);
			}
			BiomeUtil.updateBiome(world, bottom, b);
		});
	}

	private static Map<Block, Block> conversions() {
		Map<Block, Block> map = new HashMap<>();
		map.put(ACBlocks.darkstone.get(), Blocks.STONE);
		map.put(ACBlocks.darklands_oak_leaves.get(), Blocks.OAK_LEAVES);
		map.put(ACBlocks.darklands_oak_wood.get(), Blocks.OAK_LOG);
		map.put(ACBlocks.darklands_oak_wood_2.get(), Blocks.OAK_LOG);
		map.put(ACBlocks.abyssalnite_ore.get(), Blocks.IRON_ORE);
		map.put(ACBlocks.darkstone_cobblestone.get(), Blocks.COBBLESTONE);
		map.put(ACBlocks.darkstone_bricks.get(), Blocks.STONE_BRICKS);
		map.put(ACBlocks.chiseled_darkstone_bricks.get(), Blocks.CHISELED_STONE_BRICKS);
		map.put(ACBlocks.cracked_darkstone_bricks.get(), Blocks.CRACKED_STONE_BRICKS);
		map.put(ACBlocks.darkstone_cobblestone_wall.get(), Blocks.COBBLESTONE_WALL);
		map.put(ACBlocks.ritual_altar_darkstone.get(), ACBlocks.ritual_altar_stone.get());
		map.put(ACBlocks.ritual_pedestal_darkstone.get(), ACBlocks.ritual_pedestal_stone.get());
		map.put(ACBlocks.darkstone_brick_slab.get(), Blocks.STONE_BRICK_SLAB);
		map.put(ACBlocks.darkstone_cobblestone_slab.get(), Blocks.COBBLESTONE_SLAB);
		map.put(ACBlocks.darkstone_slab.get(), Blocks.SMOOTH_STONE_SLAB);
		map.put(ACBlocks.glowing_darkstone_bricks.get(), Blocks.CHISELED_STONE_BRICKS);
		map.put(ACBlocks.darkstone_brick_stairs.get(), Blocks.STONE_BRICK_STAIRS);
		map.put(ACBlocks.darkstone_cobblestone_stairs.get(), Blocks.COBBLESTONE_STAIRS);
		map.put(ACBlocks.darklands_oak_planks.get(), Blocks.OAK_PLANKS);
		map.put(ACBlocks.darklands_oak_stairs.get(), Blocks.OAK_STAIRS);
		map.put(ACBlocks.darklands_oak_slab.get(), Blocks.OAK_SLAB);
		map.put(ACBlocks.darklands_oak_fence.get(), Blocks.OAK_FENCE);
		return map;
	}

	private static ResourceKey<Biome> getRealBiome(Holder<Biome> b){
		if(b.is(ACBiomes.darklands_forest))
			return Biomes.FOREST;
		else if(b.is(ACBiomes.darklands_hills))
			return Biomes.WINDSWEPT_HILLS;
		else if(b.is(ACBiomes.darklands_mountains))
			return Biomes.SNOWY_SLOPES;
		return Biomes.PLAINS;
	}
}
