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
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;
import net.minecraftforge.eventbus.api.Event;

/**
 * Machine output slot: awards experience like a furnace and fires an event (Crystallizer, Transmutator, Engraver outputs)
 */
public class SlotXPOutput extends Slot {

	private final Player player;
	private final ToDoubleFunction<ItemStack> xp;
	private final BiFunction<Player, ItemStack, ? extends Event> event;
	private int removeCount;

	public SlotXPOutput(Player player, Container container, int index, int x, int y, ToDoubleFunction<ItemStack> xp, BiFunction<Player, ItemStack, ? extends Event> event) {
		super(container, index, x, y);
		this.player = player;
		this.xp = xp;
		this.event = event;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack remove(int amount) {
		if (hasItem())
			removeCount += Math.min(amount, getItem().getCount());
		return super.remove(amount);
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		checkTakeAchievements(stack);
		super.onTake(player, stack);
	}

	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
		removeCount += amount;
		checkTakeAchievements(stack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(player.level(), player, removeCount);
		if (player.level() instanceof ServerLevel level) {
			int i = removeCount;
			float f = (float) xp.applyAsDouble(stack);
			if (f == 0.0F)
				i = 0;
			else if (f < 1.0F) {
				int j = Mth.floor(i * f);
				if (j < Mth.ceil(i * f) && Math.random() < i * f - j)
					++j;
				i = j;
			}
			ExperienceOrb.award(level, player.position(), i);
		}
		removeCount = 0;
		MinecraftForge.EVENT_BUS.post(event.apply(player, stack));
	}
}
