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
 *     1.20.1 port - SimpleChannel instead of SimpleNetworkWrapper; messages are records with encode/decode/handle
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.network.client.DisplayRoutesMessage;
import com.shinoow.abyssalcraft.common.network.client.DisruptionMessage;
import com.shinoow.abyssalcraft.common.network.client.KnowledgeUnlockMessage;
import com.shinoow.abyssalcraft.common.network.client.NecroDataCapMessage;
import com.shinoow.abyssalcraft.common.network.client.PEStreamMessage;
import com.shinoow.abyssalcraft.common.network.client.RitualMessage;
import com.shinoow.abyssalcraft.common.network.client.RitualStartMessage;
import com.shinoow.abyssalcraft.common.network.client.ShouldSyncMessage;
import com.shinoow.abyssalcraft.common.network.server.ConfiguratorMessage;
import com.shinoow.abyssalcraft.common.network.server.PrepareSyncMessage;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * AbyssalCraft's network channel. Client bound messages only carry what the client needs to display;
 * every message sent by a client is validated on the server.
 */
public class PacketDispatcher {

	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(AbyssalCraft.loc("main"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	private static int id;

	public static void registerPackets() {
		toServer(com.shinoow.abyssalcraft.common.network.server.InterdimensionalCageMessage.class,
				com.shinoow.abyssalcraft.common.network.server.InterdimensionalCageMessage::encode,
				com.shinoow.abyssalcraft.common.network.server.InterdimensionalCageMessage::decode,
				com.shinoow.abyssalcraft.common.network.server.InterdimensionalCageMessage::handle);
		toClient(PEStreamMessage.class, PEStreamMessage::encode, PEStreamMessage::decode, PEStreamMessage::handle);
		toClient(DisruptionMessage.class, DisruptionMessage::encode, DisruptionMessage::decode, DisruptionMessage::handle);
		toClient(NecroDataCapMessage.class, NecroDataCapMessage::encode, NecroDataCapMessage::decode, NecroDataCapMessage::handle);
		toClient(KnowledgeUnlockMessage.class, KnowledgeUnlockMessage::encode, KnowledgeUnlockMessage::decode, KnowledgeUnlockMessage::handle);
		toClient(ShouldSyncMessage.class, ShouldSyncMessage::encode, ShouldSyncMessage::decode, ShouldSyncMessage::handle);
		toClient(RitualStartMessage.class, RitualStartMessage::encode, RitualStartMessage::decode, RitualStartMessage::handle);
		toClient(RitualMessage.class, RitualMessage::encode, RitualMessage::decode, RitualMessage::handle);
		toClient(DisplayRoutesMessage.class, DisplayRoutesMessage::encode, DisplayRoutesMessage::decode, DisplayRoutesMessage::handle);
		toServer(com.shinoow.abyssalcraft.common.network.server.StaffModeMessage.class, com.shinoow.abyssalcraft.common.network.server.StaffModeMessage::encode,
				com.shinoow.abyssalcraft.common.network.server.StaffModeMessage::decode, com.shinoow.abyssalcraft.common.network.server.StaffModeMessage::handle);
		toServer(com.shinoow.abyssalcraft.common.network.server.OpenSpellbookMessage.class, com.shinoow.abyssalcraft.common.network.server.OpenSpellbookMessage::encode,
				com.shinoow.abyssalcraft.common.network.server.OpenSpellbookMessage::decode, com.shinoow.abyssalcraft.common.network.server.OpenSpellbookMessage::handle);
		toServer(ConfiguratorMessage.class, ConfiguratorMessage::encode, ConfiguratorMessage::decode, ConfiguratorMessage::handle);
		toServer(PrepareSyncMessage.class, PrepareSyncMessage::encode, PrepareSyncMessage::decode, PrepareSyncMessage::handle);
	}

	private static <T> void toClient(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
		CHANNEL.messageBuilder(type, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(encoder).decoder(decoder).consumerMainThread(handler).add();
	}

	private static <T> void toServer(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
		CHANNEL.messageBuilder(type, id++, NetworkDirection.PLAY_TO_SERVER).encoder(encoder).decoder(decoder).consumerMainThread(handler).add();
	}

	public static void sendTo(Object message, ServerPlayer player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendToAllAround(Object message, ResourceKey<Level> dimension, BlockPos pos, double range) {
		CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, range, dimension)), message);
	}

	public static void sendToDimension(Object message, ResourceKey<Level> dimension) {
		CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
	}

	public static void sendToServer(Object message) {
		CHANNEL.sendToServer(message);
	}
}
