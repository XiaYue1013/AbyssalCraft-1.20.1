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
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ItemACHoe extends HoeItem implements IUnlockableItem {

	private IUnlockCondition condition = new DefaultCondition();
	@Nullable
	private final ChatFormatting format;

	public ItemACHoe(Tier tier, int attackDamage, float attackSpeed, @Nullable ChatFormatting format) {
		super(tier, attackDamage, attackSpeed, new Item.Properties());
		this.format = format;
	}

	@Override
	public Component getName(ItemStack stack) {
		Component name = super.getName(stack);
		return format != null ? name.copy().withStyle(format) : name;
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
