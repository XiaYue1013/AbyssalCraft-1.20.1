package com.shinoow.abyssalcraft.common.network.server;

import java.util.function.Supplier;
import com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public record InterdimensionalCageMessage(int targetId, InteractionHand hand) {
	public static void encode(InterdimensionalCageMessage message, FriendlyByteBuf buffer) { buffer.writeVarInt(message.targetId); buffer.writeEnum(message.hand); }
	public static InterdimensionalCageMessage decode(FriendlyByteBuf buffer) { return new InterdimensionalCageMessage(buffer.readVarInt(), buffer.readEnum(InteractionHand.class)); }
	public static void handle(InterdimensionalCageMessage message, Supplier<NetworkEvent.Context> context) {
		var player = context.get().getSender();
		if(player != null && player.getItemInHand(message.hand).getItem() instanceof ItemInterdimensionalCage cage
				&& player.level().getEntity(message.targetId) instanceof LivingEntity target) cage.capture(player, message.hand, target);
		context.get().setPacketHandled(true);
	}
}
