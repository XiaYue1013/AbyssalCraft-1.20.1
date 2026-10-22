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
 *     1.20.1 port - dimension ids replaced by levels; the plague lists come from the common config.
 *     Darklands structures and ritual completion are inherited no-ops until worldgen/rituals are ported.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.handlers;

import java.util.List;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.internal.DummyMethodHandler;
import com.shinoow.abyssalcraft.client.ClientPacketHandler;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.DisruptionMessage;
import com.shinoow.abyssalcraft.common.network.client.PEStreamMessage;
import com.shinoow.abyssalcraft.common.network.client.RitualMessage;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class InternalMethodHandler extends DummyMethodHandler {

	@Override
	public void sendDisruption(DeityType deity, String name, BlockPos pos, Level world){
		if(!world.isClientSide)
			PacketDispatcher.sendToDimension(new DisruptionMessage(deity != null ? deity.name() : "", name, pos), world.dimension());
	}

	@Override
	public void spawnParticle(String particleName, Level world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
		if(world.isClientSide)
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.spawnParticle(particleName, posX, posY, posZ, velX, velY, velZ));
	}

	@Override
	public void spawnPEStream(BlockPos posFrom, BlockPos posTo, Level world) {
		if(!world.isClientSide)
			PacketDispatcher.sendToAllAround(new PEStreamMessage(posFrom, posTo), world.dimension(), posFrom, 30);
	}

	@Override
	public void spawnPEStream(BlockPos posFrom, Entity target, Level world) {
		spawnPEStream(posFrom, target.blockPosition(), world);
	}

	@Override
	public void completeRitualClient(BlockPos pos, Player player, String ritual) {
		if(!player.level().isClientSide)
			PacketDispatcher.sendToAllAround(new RitualMessage(ritual, pos), player.level().dimension(), player.blockPosition(), 5);
	}

	@Override
	public boolean isImmuneOrCarrier(String entity, int list) {
		List<String> entries = switch(list) {
		case 0 -> ACConfig.dreadPlagueImmunityList;
		case 1 -> ACConfig.dreadPlagueCarrierList;
		case 2 -> ACConfig.coraliumPlagueImmunityList;
		case 3 -> ACConfig.coraliumPlagueCarrierList;
		default -> List.of();
		};
		// carriers are immune too
		if(list == 0 && ACConfig.dreadPlagueCarrierList.contains(entity) || list == 2 && ACConfig.coraliumPlagueCarrierList.contains(entity))
			return true;
		return entries.contains(entity);
	}
}
