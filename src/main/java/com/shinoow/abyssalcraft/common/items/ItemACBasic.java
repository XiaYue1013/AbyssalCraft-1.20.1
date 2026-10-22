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

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.item.UnlockableItemClientExtensions;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Basic AbyssalCraft item: optional name color, optional furnace burn time, and Necronomicon unlock condition
 */
public class ItemACBasic extends Item implements IUnlockableItem {

	private IUnlockCondition condition = new DefaultCondition();
	@Nullable
	private ChatFormatting format;
	private int burnTime = -1;
	private boolean foil;

	public ItemACBasic(Properties properties) {
		super(properties);
	}

	public ItemACBasic() {
		this(new Properties());
	}

	public ItemACBasic color(ChatFormatting format) {
		this.format = format;
		return this;
	}

	public ItemACBasic burnTime(int ticks) {
		burnTime = ticks;
		return this;
	}

	public ItemACBasic foil() {
		foil = true;
		return this;
	}

	@Override
	public Component getName(ItemStack stack) {
		Component name = super.getName(stack);
		return format != null ? name.copy().withStyle(format) : name;
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return burnTime;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return foil || super.isFoil(stack);
	}

	@Override
	public Item setUnlockCondition(IUnlockCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public IUnlockCondition getUnlockCondition(ItemStack stack) {
		return condition;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(UnlockableItemClientExtensions.INSTANCE);
	}
}
