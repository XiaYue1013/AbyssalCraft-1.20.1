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
 *     1.20.1 port - biome classes replaced by biome tags; the 1.12.2 block conversions mapped onto the flattened blocks
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;


import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.common.util.DarklandsCorruption;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

/**
 * Turns plains, forests, hills and mountains around it into Darklands
 */
public class NecronomiconCorruptionRitual extends NecronomiconRitual {

	public NecronomiconCorruptionRitual() {
		super("corruption", 4, Level.OVERWORLD, 10000F, true, new Object[]{BlockHandler.statueIngredient(), ACBlocks.darkstone.get(), BlockHandler.statueIngredient(),
				ACBlocks.darkstone.get(), BlockHandler.statueIngredient(), ACBlocks.darkstone.get(), BlockHandler.statueIngredient(), ACBlocks.darkstone.get()});
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	private static boolean isApplicable(Holder<Biome> b) {
		return b.is(Tags.Biomes.IS_PLAINS) || b.is(BiomeTags.IS_FOREST) || b.is(BiomeTags.IS_HILL) || b.is(BiomeTags.IS_MOUNTAIN);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return BiomeRitualHelper.anyNearby(world, pos, NecronomiconCorruptionRitual::isApplicable);
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {
		SpecialTextUtil.JzaharText("Behold, as the darkness consumes the Overworld! Bit by bit, this world will crumble.");
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		boolean b1 = world.random.nextBoolean();
		BiomeRitualHelper.transformColumns(world, pos, ACConfig.corruptionRitualRange * 8, NecronomiconCorruptionRitual::isApplicable, bottom -> {
			ResourceKey<Biome> b = getDarklandsBiome(BiomeRitualHelper.biome(world, bottom.getX(), bottom.getZ()), b1);
			DarklandsCorruption.corruptColumn(world, bottom, false);
			BiomeUtil.updateBiome(world, bottom, b);
		});
	}

	private static ResourceKey<Biome> getDarklandsBiome(Holder<Biome> b, boolean b1){
		if(b.is(Tags.Biomes.IS_PLAINS))
			return b1 ? ACBiomes.darklands_plains : ACBiomes.darklands;
		else if(b.is(BiomeTags.IS_FOREST))
			return ACBiomes.darklands_forest;
		else if(b.is(Biomes.SNOWY_SLOPES) || b.is(BiomeTags.IS_MOUNTAIN) || b.is(BiomeTags.IS_HILL) && b1)
			return ACBiomes.darklands_mountains;
		else if(b.is(BiomeTags.IS_HILL))
			return ACBiomes.darklands_hills;
		return ACBiomes.darklands;
	}
}
