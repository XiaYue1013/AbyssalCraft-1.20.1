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
package com.shinoow.abyssalcraft.common.items;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * AbyssalCraft food with an on-eaten effect (plague flesh, anti-food, plate food etc.)
 */
public class ItemACFood extends ItemACBasic {

	private final BiConsumer<Player, Level> onEaten;

	public ItemACFood(int nutrition, float saturation, boolean meat, int stackSize, BiConsumer<Player, Level> onEaten) {
		super(new Properties().stacksTo(stackSize).food(food(nutrition, saturation, meat)));
		this.onEaten = onEaten;
	}

	public ItemACFood(int nutrition, float saturation, boolean meat, BiConsumer<Player, Level> onEaten) {
		this(nutrition, saturation, meat, 64, onEaten);
	}

	private static FoodProperties food(int nutrition, float saturation, boolean meat) {
		FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
		if(meat) builder.meat();
		// 1.12.2 food with 0 hunger (anti food) could always be eaten
		if(nutrition == 0) builder.alwaysEat();
		return builder.build();
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);
		if(!level.isClientSide && entity instanceof Player player && onEaten != null)
			onEaten.accept(player, level);
		return result;
	}
}
