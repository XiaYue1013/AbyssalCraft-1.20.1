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

import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.client.ClientPacketHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Sends a player's complete Necronomicon knowledge to their client
 */
public record NecroDataCapMessage(CompoundTag properties) {

	public NecroDataCapMessage(Player player) {
		this(NecroDataCapability.write(NecroDataCapability.getCap(player)));
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(properties);
	}

	public static NecroDataCapMessage decode(FriendlyByteBuf buffer) {
		CompoundTag tag = buffer.readNbt();
		return new NecroDataCapMessage(tag != null ? tag : new CompoundTag());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleNecroData(properties));
		context.get().setPacketHandled(true);
	}
}
