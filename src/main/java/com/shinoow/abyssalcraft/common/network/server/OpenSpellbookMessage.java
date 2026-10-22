/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.network.server;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.common.inventory.ContainerSpellbook;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

/** The Necronomicon's spellbook button: opens the spellbook of the held Necronomicon */
public record OpenSpellbookMessage(InteractionHand hand) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeEnum(hand);
	}

	public static OpenSpellbookMessage decode(FriendlyByteBuf buffer) {
		return new OpenSpellbookMessage(buffer.readEnum(InteractionHand.class));
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		var player = context.get().getSender();
		if(player != null && player.getItemInHand(hand).getItem() instanceof ItemNecronomicon)
			NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, p) -> new ContainerSpellbook(id, inv, hand),
					Component.translatable("container.abyssalcraft.spellbook")), buf -> buf.writeEnum(hand));
	}
}
