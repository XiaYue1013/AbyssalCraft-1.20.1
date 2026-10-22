/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port - the server switches the held staffs and reports the new mode
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network.server;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.common.items.ItemStaff;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

/** Staff mode key press: switches every held Staff of the Gatekeeper */
public record StaffModeMessage() {

	public void encode(FriendlyByteBuf buffer) {}

	public static StaffModeMessage decode(FriendlyByteBuf buffer) {
		return new StaffModeMessage();
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		ServerPlayer player = context.get().getSender();
		if(player != null)
			for(InteractionHand hand : InteractionHand.values()) {
				ItemStack stack = player.getItemInHand(hand);
				if(stack.getItem() instanceof ItemStaff)
					player.sendSystemMessage(Component.translatable("tooltip.staff.mode.1").append(": ")
							.append(ItemStaff.modeName(ItemStaff.switchMode(stack)).copy().withStyle(ChatFormatting.GOLD)));
			}
	}
}
