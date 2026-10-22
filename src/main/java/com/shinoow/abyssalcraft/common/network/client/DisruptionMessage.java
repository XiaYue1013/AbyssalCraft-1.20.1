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
 * Runs the client side part of a Disruption (the server already applied its effects)
 * @param deity Name of the deity, or an empty string
 * @param name Disruption name (without the "ac.disruption." prefix)
 */
public record DisruptionMessage(String deity, String name, BlockPos pos) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(deity);
		buffer.writeUtf(name);
		buffer.writeBlockPos(pos);
	}

	public static DisruptionMessage decode(FriendlyByteBuf buffer) {
		return new DisruptionMessage(buffer.readUtf(64), buffer.readUtf(256), buffer.readBlockPos());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleDisruption(deity, name, pos));
		context.get().setPacketHandled(true);
	}
}
