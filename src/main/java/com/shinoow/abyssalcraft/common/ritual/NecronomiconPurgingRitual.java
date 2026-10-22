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

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;

/**
 * Wipes out the Dreadlands around it: every non-player living thing disappears, plants and liquids are removed and the
 * ground turns into Calcified Stone (Purged biome)
 */
public class NecronomiconPurgingRitual extends NecronomiconRitual {

	public NecronomiconPurgingRitual() {
		super("purging", 2, 10000F, ACBlocks.crystalCluster(25).get(), null, ACBlocks.crystalCluster(25).get(), null,
				ACBlocks.crystalCluster(25).get(), null, ACBlocks.crystalCluster(25).get());
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return world.dimension() != ACDimensions.DREADLANDS && BiomeRitualHelper.anyNearby(world, pos, b -> b.is(ACBiomes.IS_DREADLANDS));
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		BiomeRitualHelper.transformColumns(world, pos, ACConfig.purgingRitualRange * 8, b -> b.is(ACBiomes.IS_DREADLANDS), bottom -> {
			AABB column = new AABB(bottom.getX(), world.getMinBuildHeight(), bottom.getZ(), bottom.getX() + 1, world.getMaxBuildHeight(), bottom.getZ() + 1);
			for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, column, LivingEntity::isAlive))
				if(!(e instanceof Player))
					e.discard();
			for(BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(bottom.getX(), world.getMaxBuildHeight() - 1, bottom.getZ()); p.getY() >= world.getMinBuildHeight(); p.move(0, -1, 0)){
				BlockState state = world.getBlockState(p);
				if(state.isAir() || state.hasBlockEntity() || state.getDestroySpeed(world, p) == -1) continue;
				if(state.getBlock() instanceof IPlantable || !state.getFluidState().isEmpty() || !state.isCollisionShapeFullBlock(world, p))
					world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
				else
					world.setBlock(p, ACBlocks.calcified_stone.get().defaultBlockState(), 2);
			}
			BiomeUtil.updateBiome(world, bottom, ACBiomes.purged);
		});
	}
}
