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
 *     1.20.1 port - capability registration/attachment on the 1.20.1 events; dimension unlocks use dimension ids
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.handlers;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.INecroDataCapability;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapabilityProvider;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.KnowledgeUnlockMessage;
import com.shinoow.abyssalcraft.common.network.client.NecroDataCapMessage;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Necronomicon knowledge: stores it on players, keeps it through death, unlocks biomes/entities/dimensions and syncs
 * it to the client
 */
public class KnowledgeEventHandler {
	/** The twelve condition processors registered by the 1.12.2 MiscHandler. */
	@SuppressWarnings("unchecked")
	public static void registerConditions() {
		var registry = com.shinoow.abyssalcraft.api.necronomicon.condition.ConditionProcessorRegistry.instance();
		registry.registerProcessor(0, (c, cap, player) -> cap.getBiomeTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(1, (c, cap, player) -> cap.getEntityTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(2, (c, cap, player) -> cap.getDimensionTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(3, (c, cap, player) -> java.util.Arrays.stream((String[]) c.getConditionObject()).anyMatch(cap.getBiomeTriggers()::contains));
		registry.registerProcessor(4, (c, cap, player) -> java.util.Arrays.stream((String[]) c.getConditionObject()).anyMatch(cap.getEntityTriggers()::contains));
		registry.registerProcessor(5, (c, cap, player) -> {
			var biomes = player.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
			var predicate = (java.util.function.Predicate<net.minecraft.world.level.biome.Biome>) c.getConditionObject();
			return cap.getBiomeTriggers().stream().map(ResourceLocation::tryParse).filter(java.util.Objects::nonNull)
					.map(biomes::get).filter(java.util.Objects::nonNull).anyMatch(predicate);
		});
		registry.registerProcessor(6, (c, cap, player) -> {
			var predicate = (java.util.function.Predicate<net.minecraft.world.entity.EntityType<?>>) c.getConditionObject();
			return cap.getEntityTriggers().stream().map(ResourceLocation::tryParse).filter(java.util.Objects::nonNull)
					.filter(ForgeRegistries.ENTITY_TYPES::containsKey).map(ForgeRegistries.ENTITY_TYPES::getValue).anyMatch(predicate);
		});
		registry.registerProcessor(7, (c, cap, player) -> cap.getArtifactTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(8, (c, cap, player) -> cap.getPageTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(9, (c, cap, player) -> cap.getWhisperTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(10, (c, cap, player) -> cap.getMiscTriggers().contains(c.getConditionObject()));
		registry.registerProcessor(11, (c, cap, player) -> java.util.Arrays.stream((String[]) c.getConditionObject()).allMatch(cap.getEntityTriggers()::contains));
	}

	private static final ResourceLocation KEY = new ResourceLocation(AbyssalCraft.modid, "necrodata");

	/** Mod bus */
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(INecroDataCapability.class);
		event.register(com.shinoow.abyssalcraft.api.transfer.caps.IItemTransferCapability.class);
	}

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof Player) {
			NecroDataCapabilityProvider provider = new NecroDataCapabilityProvider();
			event.addCapability(KEY, provider);
			event.addListener(provider::invalidate);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingTickEvent event){
		if(event.getEntity() instanceof ServerPlayer player){
			INecroDataCapability cap = NecroDataCapability.getCap(player);
			if(cap.getSyncTimer() < ACConfig.knowledgeSyncDelay)
				cap.incrementSyncTimer();
			else if(cap.getSyncTimer() == ACConfig.knowledgeSyncDelay) {
				cap.incrementSyncTimer();
				cap.setLastSyncTime(System.currentTimeMillis());
				PacketDispatcher.sendTo(new NecroDataCapMessage(player), player);
			}
			if(player.tickCount % 200 == 0)
				player.level().getBiome(player.blockPosition()).unwrapKey().ifPresent(key -> {
					String name = key.location().toString();
					if(!cap.getBiomeTriggers().contains(name)) {
						cap.triggerBiomeUnlock(name);
						PacketDispatcher.sendTo(new KnowledgeUnlockMessage(KnowledgeUnlockMessage.BIOME, name), player);
					}
				});
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
		if(event.getEntity() instanceof ServerPlayer player) {
			INecroDataCapability cap = NecroDataCapability.getCap(player);
			String dim = event.getTo().location().toString();
			if(!cap.getDimensionTriggers().contains(dim)) {
				cap.triggerDimensionUnlock(dim);
				PacketDispatcher.sendTo(new KnowledgeUnlockMessage(KnowledgeUnlockMessage.DIMENSION, dim), player);
			}
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		LivingEntity e = event.getEntity();
		if(!(e instanceof Player) && !e.level().isClientSide && event.getSource().getEntity() instanceof ServerPlayer player) {
			ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
			if(key != null) {
				String name = key.toString();
				INecroDataCapability cap = NecroDataCapability.getCap(player);
				if(!cap.getEntityTriggers().contains(name)) {
					cap.triggerEntityUnlock(name);
					PacketDispatcher.sendTo(new KnowledgeUnlockMessage(KnowledgeUnlockMessage.ENTITY, name), player);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoin(EntityJoinLevelEvent event){
		if(event.getEntity() instanceof ServerPlayer player)
			NecroDataCapability.getCap(player).resetSyncTimer();
	}

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone event) {
		event.getOriginal().reviveCaps();
		NecroDataCapability.getCap(event.getEntity()).copy(NecroDataCapability.getCap(event.getOriginal()));
		event.getOriginal().invalidateCaps();
	}
}
