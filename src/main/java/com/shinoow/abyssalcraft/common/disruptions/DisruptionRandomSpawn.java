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
import java.util.Optional;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionSpawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Spawns a random monster that can spawn in the biome
 */
public class DisruptionRandomSpawn extends DisruptionEntry {

	public DisruptionRandomSpawn() {
		this("randomSpawn");
	}

	protected DisruptionRandomSpawn(String name) {
		super(name, null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(world instanceof ServerLevel level)
			spawnRandom(level, pos);
	}

	/**
	 * Spawns a random monster from the biome's spawn list near the position (Lesser Dreadbeasts are skipped)
	 */
	@SuppressWarnings("unchecked")
	protected static void spawnRandom(ServerLevel level, BlockPos pos) {
		var list = level.getBiome(pos).value().getMobSettings().getMobs(MobCategory.MONSTER);
		if(list.isEmpty()) return;
		for(int tries = 0; tries <= 10; tries++) {
			Optional<SpawnerData> entry = list.getRandom(level.random);
			if(entry.isEmpty()) return;
			EntityType<?> type = entry.get().type;
			if(isLesserDreadbeast(type))
				continue;
			// only mobs can be spawned through the disruption spawn rules
			if(type.create(level) instanceof Mob mob) {
				mob.discard();
				DisruptionSpawn.spawnNear(level, pos, (EntityType<? extends Mob>) type);
			}
			return;
		}
	}

	private static boolean isLesserDreadbeast(EntityType<?> type) {
		var key = ForgeRegistries.ENTITY_TYPES.getKey(type);
		return key != null && key.getNamespace().equals(AbyssalCraft.modid) && key.getPath().equals(ACEntities.lesser_dreadbeast);
	}
}
