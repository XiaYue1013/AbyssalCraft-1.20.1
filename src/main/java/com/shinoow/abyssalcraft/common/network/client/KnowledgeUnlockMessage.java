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
 *     1.20.1 port - dimension unlocks are dimension ids (strings) like the other types
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network.client;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.client.ClientPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about a single knowledge unlock
 * @param type 0 = biome, 1 = entity, 2 = dimension, 3 = artifact, 4 = page, 5 = whisper, 6 = misc
 */
public record KnowledgeUnlockMessage(int type, String data) {

	public static final int BIOME = 0, ENTITY = 1, DIMENSION = 2, ARTIFACT = 3, PAGE = 4, WHISPER = 5, MISC = 6;

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(type);
		buffer.writeUtf(data);
	}

	public static KnowledgeUnlockMessage decode(FriendlyByteBuf buffer) {
		return new KnowledgeUnlockMessage(buffer.readVarInt(), buffer.readUtf(256));
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleKnowledgeUnlock(type, data));
		context.get().setPacketHandled(true);
	}
}
