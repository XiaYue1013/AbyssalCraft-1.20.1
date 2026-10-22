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
 *     1.20.1 port - the dread spawn kinds are looked up by entity id
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import java.util.List;
import java.util.Set;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Doubles the Dread Spawns, Greater Dread Spawns and Lesser Dreadbeasts nearby
 */
public class NecronomiconDreadSpawnRitual extends NecronomiconRitual {

	private static final Set<ResourceLocation> DREAD_SPAWNS = Set.of(new ResourceLocation(AbyssalCraft.modid, ACEntities.dread_spawn),
			new ResourceLocation(AbyssalCraft.modid, ACEntities.greater_dread_spawn), new ResourceLocation(AbyssalCraft.modid, ACEntities.lesser_dreadbeast));

	public NecronomiconDreadSpawnRitual() {
		super("dreadSpawn", 2, 500F, new Object[]{ACItems.dread_fragment.get(), ACBlocks.dreadstone.get(), ACBlocks.abyssalnite_stone.get(),
				ACItems.chunk_of_abyssalnite.get(), ACItems.dreaded_chunk_of_abyssalnite.get(), Items.ROTTEN_FLESH, ACItems.dreaded_shard_of_abyssalnite.get(),
				ACItems.abyssalnite_ingot.get()});
		setRitualParticle(EnumRitualParticle.SMOKE_PILLARS);
	}

	private static List<Entity> dreadSpawns(Level world, BlockPos pos, Player player) {
		return world.getEntities(player, new AABB(pos).inflate(16, 3, 16), e -> DREAD_SPAWNS.contains(ForgeRegistries.ENTITY_TYPES.getKey(e.getType())));
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return !dreadSpawns(world, pos, player).isEmpty();
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		for(Entity spawn : dreadSpawns(world, pos, player)) {
			Entity copy = spawn.getType().create(world);
			if(copy != null) {
				copy.moveTo(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYRot(), spawn.getXRot());
				world.addFreshEntity(copy);
			}
		}
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}
}
