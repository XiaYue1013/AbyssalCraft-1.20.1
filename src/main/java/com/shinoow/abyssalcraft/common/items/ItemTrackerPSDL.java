/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ItemTrackerPSDL extends ItemACBasic {
	@Override public boolean isFoil(ItemStack stack) { return true; }

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		var hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
		if(hit.getType() == HitResult.Type.BLOCK && level.getBlockState(hit.getBlockPos()).is(ACBlocks.dreadlands_infused_powerstone.get()))
			return InteractionResultHolder.pass(stack);
		if(level.isClientSide) return InteractionResultHolder.success(stack);
		if(level instanceof ServerLevel server) {
			var structures = server.registryAccess().registryOrThrow(Registries.STRUCTURE);
			var stronghold = structures.getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE, AbyssalCraft.loc("abyssal_stronghold")));
			var nearest = server.getChunkSource().getGenerator().findNearestMapStructure(server, HolderSet.direct(stronghold), player.blockPosition(), 100, true);
			if(nearest != null) {
				var tracker = ACEntityTypes.PSDL_TRACKER.get().create(server);
				tracker.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
				tracker.moveTowards(nearest.getFirst());
				server.addFreshEntity(tracker);
				level.playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
				if(!player.getAbilities().instabuild) stack.shrink(1);
				return InteractionResultHolder.consume(stack);
			}
		}
		return InteractionResultHolder.fail(stack);
	}
}
