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
 *     1.20.1 port - the client only sends the action; the server works out the new mode of the held configurators
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network.server;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.common.inventory.ContainerConfigurator;
import com.shinoow.abyssalcraft.common.inventory.InventoryConfigurator;
import com.shinoow.abyssalcraft.common.items.ItemConfigurator;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

/**
 * Configurator key presses
 * @param action 0 = cycle the mode, 1 = open the filter, 2 = clear the recorded path
 */
public record ConfiguratorMessage(int action) {

	public static final int MODE = 0, FILTER = 1, CLEAR_PATH = 2;

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(action);
	}

	public static ConfiguratorMessage decode(FriendlyByteBuf buffer) {
		return new ConfiguratorMessage(buffer.readVarInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		ServerPlayer player = context.get().getSender();
		if(player != null)
			switch(action) {
			case MODE -> {
				for(InteractionHand hand : InteractionHand.values()) {
					ItemStack stack = player.getItemInHand(hand);
					if(stack.getItem() instanceof ItemConfigurator) {
						ItemConfigurator.cycleMode(stack);
						player.sendSystemMessage(Component.translatable("tooltip.staff.mode.1").append(": ")
								.append(ItemConfigurator.getModeName(ItemConfigurator.getMode(stack)).copy().withStyle(ChatFormatting.GOLD)));
					}
				}
			}
			case FILTER -> {
				for(InteractionHand hand : InteractionHand.values()) {
					ItemStack stack = player.getItemInHand(hand);
					if(stack.getItem() instanceof ItemConfigurator) {
						player.openMenu(new SimpleMenuProvider((id, inv, p) -> new ContainerConfigurator(id, inv, new InventoryConfigurator(stack, hand)),
								stack.getHoverName()));
						break;
					}
				}
			}
			case CLEAR_PATH -> {
				for(InteractionHand hand : InteractionHand.values()) {
					ItemStack stack = player.getItemInHand(hand);
					if(stack.getItem() instanceof ItemConfigurator && stack.hasTag()) {
						stack.getTag().remove("Path");
						player.sendSystemMessage(Component.translatable("message.configurator.5"));
					}
				}
			}
			default -> {}
			}
		context.get().setPacketHandled(true);
	}
}
