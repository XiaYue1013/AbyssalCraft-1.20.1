/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.INecroDataCapability;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.NecroDataCapMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** /acunlockallknowledge: toggles unlocking all Necronomicon knowledge for the player running it (op level 4, as in 1.12.2) */
@Mod.EventBusSubscriber(modid = "abyssalcraft")
public class CommandUnlockAllKnowledge {

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		register(event.getDispatcher());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("acunlockallknowledge").requires(src -> src.hasPermission(4))
				.executes(ctx -> toggle(ctx.getSource().getPlayerOrException())));
	}

	public static int toggle(ServerPlayer player) throws CommandSyntaxException {
		INecroDataCapability cap = NecroDataCapability.getCap(player);
		boolean unlock = !cap.hasUnlockedAllKnowledge();
		cap.unlockAllKnowledge(unlock);
		player.sendSystemMessage(Component.literal(unlock ? "All knowledge has been unlocked!" : "All knowledge has been re-locked... kinda!"));
		cap.setLastSyncTime(System.currentTimeMillis());
		PacketDispatcher.sendTo(new NecroDataCapMessage(player), player);
		return 1;
	}
}
