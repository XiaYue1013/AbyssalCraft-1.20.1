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
 * A ritual completed (clients run the client side part of the ritual) or failed (clients get the failure event)
 * @param name Ritual id
 * @param disruption Disruption name when failed
 */
public record RitualMessage(String name, BlockPos pos, boolean failed, String disruption) {

	public RitualMessage(String name, BlockPos pos) {
		this(name, pos, false, "");
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(name);
		buffer.writeBoolean(failed);
		buffer.writeBlockPos(pos);
		buffer.writeUtf(disruption);
	}

	public static RitualMessage decode(FriendlyByteBuf buffer) {
		String name = buffer.readUtf(256);
		boolean failed = buffer.readBoolean();
		return new RitualMessage(name, buffer.readBlockPos(), failed, buffer.readUtf(256));
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleRitual(name, pos, failed, disruption));
		context.get().setPacketHandled(true);
	}
}
