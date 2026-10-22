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

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Damageable crafting tool that loses one durability each time it's used in a recipe (Washcloth, Transmutation Gem)
 */
public class ItemCraftingTool extends ItemACBasic {

	private final String tooltip;

	public ItemCraftingTool(int uses, String tooltip) {
		super(new Properties().durability(uses).setNoRepair());
		this.tooltip = tooltip;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		ItemStack result = stack.copy();
		result.setDamageValue(stack.getDamageValue() + 1);
		return result.getDamageValue() >= result.getMaxDamage() ? ItemStack.EMPTY : result;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag) {
		if(tooltip != null)
			l.add(Component.translatable(tooltip, is.getDamageValue(), is.getMaxDamage()));
		if(!flag.isAdvanced())
			l.add(Component.literal(String.format("%d/%d", is.getMaxDamage() - is.getDamageValue(), is.getMaxDamage())));
	}
}
