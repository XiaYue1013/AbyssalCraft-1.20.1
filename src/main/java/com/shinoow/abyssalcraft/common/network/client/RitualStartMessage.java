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

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * A ritual started at the altar (clients run the ritual timer and particles)
 * @param ritual Ritual id
 * @param sacrifice Entity id of the sacrifice, or 0
 */
public record RitualStartMessage(BlockPos pos, String ritual, int sacrifice) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeUtf(ritual);
		buffer.writeVarInt(sacrifice);
	}

	public static RitualStartMessage decode(FriendlyByteBuf buffer) {
		return new RitualStartMessage(buffer.readBlockPos(), buffer.readUtf(256), buffer.readVarInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleRitualStart(pos, ritual, sacrifice));
		context.get().setPacketHandled(true);
	}
}
