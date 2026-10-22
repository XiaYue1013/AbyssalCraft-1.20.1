/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (TileEntityDreadguardSpawner, TileEntityChagarothSpawner, TileEntityJzaharSpawner,
 *                TileEntityGatekeeperMinionSpawner)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.BlockSingleMobSpawner;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntitySingleMobSpawner extends BlockEntity {

	public TileEntitySingleMobSpawner(BlockPos pos, BlockState state) {
		super(ACBlockEntities.SINGLE_MOB_SPAWNER.get(), pos, state);
	}

	/** A player in survival/adventure mode is within range */
	public static boolean isActivated(Level level, BlockPos pos, BlockSingleMobSpawner spawner) {
		Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, spawner.getRange(), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
		return player != null && (!spawner.playerNotBelow() || player.getY() >= pos.getY() - 1);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntitySingleMobSpawner tile) {
		if(state.getBlock() instanceof BlockSingleMobSpawner spawner && isActivated(level, pos, spawner))
			spawn((ServerLevel) level, pos, spawner);
	}

	/** Replaces the spawner with its mob; does nothing while that mob isn't registered */
	public static boolean spawn(ServerLevel level, BlockPos pos, BlockSingleMobSpawner spawner) {
		ResourceLocation id = AbyssalCraft.loc(spawner.getEntity());
		if(!ForgeRegistries.ENTITY_TYPES.containsKey(id))
			return false;
		Entity entity = ForgeRegistries.ENTITY_TYPES.getValue(id).create(level);
		if(entity == null)
			return false;
		// 1.12.2 used the block's corner
		entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), Mth.wrapDegrees(level.random.nextFloat() * 360.0F), 10.0F);
		if(entity instanceof Mob mob)
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);
		level.addFreshEntityWithPassengers(entity);
		level.removeBlock(pos, false);
		return true;
	}
}
