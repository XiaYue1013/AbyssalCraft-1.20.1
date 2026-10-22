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
 *     1.20.1 port - the Dreadlands marker interface is a biome tag
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;

/**
 * Turns the Dreadlands around it into the nearest regular biome, killing the dread plague carriers on the way
 */
public class NecronomiconCuringRitual extends NecronomiconRitual {

	@Nullable
	private ResourceKey<Biome> replacementBiome;

	public NecronomiconCuringRitual() {
		super("curing", 4, 20000F, ACItems.dread_antidote.get(), null, ACItems.dread_antidote.get(), null,
				ACItems.dread_antidote.get(), null, ACItems.dread_antidote.get());
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		boolean ret = false;
		if(world.dimension() != ACDimensions.DREADLANDS)
			for(int x = pos.getX() - 24; x < pos.getX() + 25; x++)
				for(int z = pos.getZ() - 24; z < pos.getZ() + 25; z++){
					Holder<Biome> biome = BiomeRitualHelper.biome(world, x, z);
					if(!ret) {
						if(biome.is(ACBiomes.IS_DREADLANDS))
							ret = true;
					} else if(!biome.is(ACBiomes.IS_DREADLANDS) && !biome.is(ACBiomes.purged) && biome.unwrapKey().isPresent()) {
						replacementBiome = biome.unwrapKey().get();
						return true;
					}
				}
		return false;
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		ResourceKey<Biome> replacement = replacementBiome;
		if(replacement == null) return;
		BiomeRitualHelper.transformColumns(world, pos, ACConfig.curingRitualRange * 8, b -> b.is(ACBiomes.IS_DREADLANDS), bottom -> {
			AABB area = new AABB(bottom.getX() - 3, world.getMinBuildHeight(), bottom.getZ() - 3, bottom.getX() + 4, world.getMaxBuildHeight(), bottom.getZ() + 4);
			for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive() && EntityUtil.isDreadPlagueCarrier(e)))
				e.kill();
			BiomeUtil.updateBiome(world, bottom, replacement);
		});
	}
}
