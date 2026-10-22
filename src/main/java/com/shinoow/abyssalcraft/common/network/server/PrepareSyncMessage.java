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
 *     1.20.1 port - the sender is taken from the network context (no client supplied player UUID)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network.server;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.NecroDataCapMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

/**
 * Client asks for its own Necronomicon knowledge
 */
public record PrepareSyncMessage() {

	private static final Map<UUID, Integer> LAST_REQUEST = Collections.synchronizedMap(new WeakHashMap<>());

	public void encode(FriendlyByteBuf buffer) {}

	public static PrepareSyncMessage decode(FriendlyByteBuf buffer) {
		return new PrepareSyncMessage();
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		ServerPlayer player = context.get().getSender();
		// at most one full sync per second and player
		if(player != null && player.getServer() != null) {
			int tick = player.getServer().getTickCount();
			Integer last = LAST_REQUEST.get(player.getUUID());
			if(last == null || tick - last >= 20 || tick < last) {
				LAST_REQUEST.put(player.getUUID(), tick);
				PacketDispatcher.sendTo(new NecroDataCapMessage(player), player);
			}
		}
		context.get().setPacketHandled(true);
	}
}
