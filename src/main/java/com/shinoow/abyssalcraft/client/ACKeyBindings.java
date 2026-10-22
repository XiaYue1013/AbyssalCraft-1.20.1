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
 *     1.20.1 port - key mappings (1.12.2 ClientProxy key bindings + AbyssalCraftClientEventHooks key handling)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.common.items.ItemConfigurator;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.server.ConfiguratorMessage;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbyssalCraft.modid, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ACKeyBindings {

	private static final String CATEGORY = "key.abyssalcraft.category";

	public static final KeyMapping CONFIGURATOR_MODE = key("key.configurator_mode.desc", GLFW.GLFW_KEY_V);
	public static final KeyMapping CONFIGURATOR_FILTER = key("key.configurator_filter.desc", GLFW.GLFW_KEY_B);
	public static final KeyMapping CONFIGURATOR_PATH = key("key.configurator_path.desc", GLFW.GLFW_KEY_G);
	public static final KeyMapping STAFF_MODE = key("key.staff_mode.desc", GLFW.GLFW_KEY_M);
	public static final KeyMapping USE_CAGE = key("key.use_cage.desc", GLFW.GLFW_KEY_N);

	private static KeyMapping key(String name, int key) {
		return new KeyMapping(name, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, key, CATEGORY);
	}

	/** Mod bus, registered from ClientSetup */
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(CONFIGURATOR_MODE);
		event.register(CONFIGURATOR_FILTER);
		event.register(CONFIGURATOR_PATH);
		event.register(STAFF_MODE);
		event.register(USE_CAGE);
	}

	private static boolean holdsConfigurator(Player player) {
		return player.getMainHandItem().getItem() instanceof ItemConfigurator || player.getOffhandItem().getItem() instanceof ItemConfigurator;
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase != TickEvent.Phase.END) return;
		Player player = Minecraft.getInstance().player;
		if(player == null) return;
		while(STAFF_MODE.consumeClick())
			if(player.getMainHandItem().getItem() instanceof com.shinoow.abyssalcraft.common.items.ItemStaff
					|| player.getOffhandItem().getItem() instanceof com.shinoow.abyssalcraft.common.items.ItemStaff)
				PacketDispatcher.sendToServer(new com.shinoow.abyssalcraft.common.network.server.StaffModeMessage());
		while(USE_CAGE.consumeClick()) {
			var hand = player.getMainHandItem().getItem() instanceof com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage
					? net.minecraft.world.InteractionHand.MAIN_HAND : net.minecraft.world.InteractionHand.OFF_HAND;
			if(player.getItemInHand(hand).getItem() instanceof com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage
					&& Minecraft.getInstance().hitResult instanceof net.minecraft.world.phys.EntityHitResult hit)
				PacketDispatcher.sendToServer(new com.shinoow.abyssalcraft.common.network.server.InterdimensionalCageMessage(hit.getEntity().getId(), hand));
		}
		while(CONFIGURATOR_MODE.consumeClick())
			if(holdsConfigurator(player))
				PacketDispatcher.sendToServer(new ConfiguratorMessage(ConfiguratorMessage.MODE));
		while(CONFIGURATOR_FILTER.consumeClick())
			if(holdsConfigurator(player))
				PacketDispatcher.sendToServer(new ConfiguratorMessage(ConfiguratorMessage.FILTER));
		while(CONFIGURATOR_PATH.consumeClick())
			if(holdsConfigurator(player))
				PacketDispatcher.sendToServer(new ConfiguratorMessage(ConfiguratorMessage.CLEAR_PATH));
	}

	/** Stored PE of every PE item (1.12.2 AbyssalCraftClientEventHooks.tooltipStuff) */
	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() instanceof IEnergyContainerItem pe && !event.getToolTip().isEmpty())
			event.getToolTip().add(1, Component.literal(String.format("%d/%d PE", (int) pe.getContainedEnergy(stack), pe.getMaxEnergy(stack))));
	}
}
