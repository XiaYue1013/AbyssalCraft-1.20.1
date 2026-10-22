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

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.client.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Sent when a player opens their Necronomicon: if the client's knowledge is older than the server's last sync,
 * the client asks for a full sync ({@link com.shinoow.abyssalcraft.common.network.server.PrepareSyncMessage})
 */
public record ShouldSyncMessage(long time) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(time);
	}

	public static ShouldSyncMessage decode(FriendlyByteBuf buffer) {
		return new ShouldSyncMessage(buffer.readLong());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleShouldSync(time));
		context.get().setPacketHandled(true);
	}
}
