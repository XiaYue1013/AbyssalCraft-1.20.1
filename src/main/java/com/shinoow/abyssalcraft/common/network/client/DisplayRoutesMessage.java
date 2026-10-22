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
package com.shinoow.abyssalcraft.common.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.client.ClientPacketHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Shows the item transfer routes of a Spirit Altar
 */
public record DisplayRoutesMessage(List<BlockPos[]> routes) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(routes.size());
		for(BlockPos[] route : routes) {
			buffer.writeVarInt(route.length);
			for(BlockPos pos : route)
				buffer.writeBlockPos(pos);
		}
	}

	public static DisplayRoutesMessage decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		List<BlockPos[]> routes = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			BlockPos[] route = new BlockPos[buffer.readVarInt()];
			for(int j = 0; j < route.length; j++)
				route[j] = buffer.readBlockPos();
			routes.add(route);
		}
		return new DisplayRoutesMessage(routes);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> routes.forEach(route -> ClientPacketHandler.showRoute(route, 15, false)));
		context.get().setPacketHandled(true);
	}
}
