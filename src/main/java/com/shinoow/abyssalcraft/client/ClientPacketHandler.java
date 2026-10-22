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
 *     1.20.1 port - client side handling of the client bound messages (kept out of the message classes so
 *     dedicated servers never load client classes)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client;

import java.util.List;

import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.api.event.ACEvents.DisruptionEvent;
import com.shinoow.abyssalcraft.api.event.ACEvents.RitualEvent;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.INecroDataCapability;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.KnowledgeUnlockMessage;
import com.shinoow.abyssalcraft.common.network.server.PrepareSyncMessage;
import com.shinoow.abyssalcraft.init.ACParticles;
import com.shinoow.abyssalcraft.lib.util.ParticleUtil;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

public class ClientPacketHandler {

	/** Spacing of particle lines (1 = all particles, 2 = decreased, 3 = minimal) */
	public static int particleIncrements() {
		return 1 + Minecraft.getInstance().options.particles().get().getId();
	}

	public static void spawnPEStream(BlockPos from, BlockPos to) {
		ClientLevel level = Minecraft.getInstance().level;
		if(level == null) return;
		int increments = particleIncrements();
		ParticleUtil.spawnParticleLine(from, to, 15, increments, (dir, pos) -> {
			level.addParticle(ACParticles.PE_STREAM.get(), pos.x, pos.y, pos.z, dir.x * .1, .15, dir.z * .1);
			return false;
		});
	}

	public static void handleDisruption(String deity, String name, BlockPos pos) {
		ClientLevel level = Minecraft.getInstance().level;
		if(level == null) return;
		DisruptionEntry disruption = DisruptionHandler.instance().disruptionFromName(name);
		if(disruption == null) return;
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(16));
		if(!MinecraftForge.EVENT_BUS.post(new DisruptionEvent(!deity.isEmpty() ? DeityType.valueOf(deity) : null, level, pos, players, disruption)))
			disruption.disrupt(level, pos, players);
	}

	/** Named particles of the 1.12.2 proxy */
	public static void spawnParticle(String particleName, double posX, double posY, double posZ, double velX, double velY, double velZ) {
		ClientLevel level = Minecraft.getInstance().level;
		if(level == null) return;
		if(particleName.equals("PEStream"))
			level.addParticle(ACParticles.PE_STREAM.get(), posX, posY, posZ, velX, velY, velZ);
		else if(particleName.equals("BlueFlame"))
			level.addParticle(ACParticles.BLUE_FLAME.get(), posX, posY, posZ, velX, velY, velZ);
	}

	public static void handleNecroData(CompoundTag properties) {
		Player player = Minecraft.getInstance().player;
		if(player != null)
			NecroDataCapability.read(NecroDataCapability.getCap(player), properties);
	}

	public static void handleKnowledgeUnlock(int type, String data) {
		Player player = Minecraft.getInstance().player;
		if(player == null) return;
		INecroDataCapability cap = NecroDataCapability.getCap(player);
		switch(type) {
		case KnowledgeUnlockMessage.BIOME -> cap.triggerBiomeUnlock(data);
		case KnowledgeUnlockMessage.ENTITY -> cap.triggerEntityUnlock(data);
		case KnowledgeUnlockMessage.DIMENSION -> cap.triggerDimensionUnlock(data);
		case KnowledgeUnlockMessage.ARTIFACT -> cap.triggerArtifactUnlock(data);
		case KnowledgeUnlockMessage.PAGE -> cap.triggerPageUnlock(data);
		case KnowledgeUnlockMessage.WHISPER -> cap.triggerWhisperUnlock(data);
		case KnowledgeUnlockMessage.MISC -> cap.triggerMiscUnlock(data);
		default -> {}
		}
	}

	public static void handleShouldSync(long time) {
		Player player = Minecraft.getInstance().player;
		if(player != null && NecroDataCapability.getCap(player).getLastSyncTime() < time)
			PacketDispatcher.sendToServer(new PrepareSyncMessage());
	}

	public static void handleRitualStart(BlockPos pos, String id, int sacrifice) {
		ClientLevel level = Minecraft.getInstance().level;
		if(level != null && level.getBlockEntity(pos) instanceof IRitualAltar altar)
			altar.setRitualFields(RitualRegistry.instance().getRitual(id), level.getEntity(sacrifice) instanceof Mob mob ? mob : null);
	}

	public static void handleRitual(String id, BlockPos pos, boolean failed, String disruption) {
		Player player = Minecraft.getInstance().player;
		NecronomiconRitual ritual = RitualRegistry.instance().getRitual(id);
		if(player == null || ritual == null) return;
		if(failed)
			MinecraftForge.EVENT_BUS.post(new RitualEvent.Failed(player, ritual, DisruptionHandler.instance().disruptionFromName(disruption), player.level(), pos));
		else if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Post(player, ritual, player.level(), pos)))
			ritual.completeRitual(player.level(), pos, player);
	}

	/** A message from someone (J'zahar, Asorah...), shown to the local player only */
	public static void customText(String name, String...text) {
		Player player = Minecraft.getInstance().player;
		if(player != null)
			player.sendSystemMessage(Component.literal(name + ": " + SpecialTextUtil.pick(player, text)));
	}

	/**
	 * Draws a route as a line of red dust
	 * @param marker Whether or not to mark the last position (the configurator's path preview)
	 */
	public static void showRoute(BlockPos[] route, int density, boolean marker) {
		ClientLevel level = Minecraft.getInstance().level;
		if(level == null) return;
		BlockPos prevPos = null;
		for(BlockPos pos : route) {
			if(prevPos != null)
				ParticleUtil.spawnParticleLine(prevPos, pos, density, (dir, p) -> {
					level.addParticle(net.minecraft.core.particles.DustParticleOptions.REDSTONE, p.x, p.y, p.z, 0, 0, 0);
					return false;
				});
			prevPos = pos;
		}
		if(marker && prevPos != null)
			level.addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, prevPos.getX()+0.5, prevPos.getY()+1.5, prevPos.getZ()+0.5, 0, 0, 0);
	}

	/** The configurator key bindings, for its tooltip */
	public static void addConfiguratorKeyTooltip(java.util.List<Component> tooltip) {
		tooltip.add(Component.translatable("tooltip.staff.mode.2", ACKeyBindings.CONFIGURATOR_MODE.getTranslatedKeyMessage().copy().withStyle(net.minecraft.ChatFormatting.GOLD)));
		tooltip.add(Component.translatable("tooltip.configurator.1", ACKeyBindings.CONFIGURATOR_FILTER.getTranslatedKeyMessage().copy().withStyle(net.minecraft.ChatFormatting.GOLD)));
		tooltip.add(Component.translatable("tooltip.configurator.2", ACKeyBindings.CONFIGURATOR_PATH.getTranslatedKeyMessage().copy().withStyle(net.minecraft.ChatFormatting.GOLD)));
	}
}
