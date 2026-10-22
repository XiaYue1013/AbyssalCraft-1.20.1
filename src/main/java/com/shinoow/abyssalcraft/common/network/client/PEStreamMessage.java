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
 * Displays a stream of Potential Energy particles between two positions
 */
public record PEStreamMessage(BlockPos posFrom, BlockPos posTo) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(posFrom);
		buffer.writeBlockPos(posTo);
	}

	public static PEStreamMessage decode(FriendlyByteBuf buffer) {
		return new PEStreamMessage(buffer.readBlockPos(), buffer.readBlockPos());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.spawnPEStream(posFrom, posTo));
		context.get().setPacketHandled(true);
	}
}
