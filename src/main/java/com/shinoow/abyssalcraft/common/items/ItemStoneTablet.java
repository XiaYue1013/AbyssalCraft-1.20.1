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

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Stone Tablet: the State Transformer stores up to 49 stacks in it ("ItemInventory"), with a PE value of the stored items
 */
public class ItemStoneTablet extends ItemACBasic {

	public ItemStoneTablet() {
		super(new Properties().stacksTo(1));
	}

	/** A tablet that went through the State Transformer (empty inventory) */
	public static ItemStack withEmptyInventory(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.put("ItemInventory", new ListTag());
		tag.putFloat("PotEnergy", 0);
		return stack;
	}

	public boolean hasInventory(ItemStack stack){
		if(stack.isEmpty()) return false;
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains("ItemInventory") && tag.contains("PotEnergy");
	}

	public boolean isCursed(ItemStack stack){
		return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("Cursed");
	}

	public void setCursed(ItemStack stack){
		if(!stack.isEmpty())
			stack.getOrCreateTag().putBoolean("Cursed", true);
	}

	@Override
	public void appendHoverText(ItemStack is, @Nullable Level level, List<Component> l, TooltipFlag flag)
	{
		if(isCursed(is))
			l.add(Component.translatable("tooltip.stonetablet.cursed").withStyle(ChatFormatting.DARK_PURPLE));
		if(hasInventory(is)){
			l.add(Component.literal((int)is.getTag().getFloat("PotEnergy") + " PE"));
			l.add(Component.translatable("tooltip.stonetablet.contents", is.getTag().getList("ItemInventory", Tag.TAG_COMPOUND).size()));
		}
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return hasInventory(stack) || super.isFoil(stack);
	}
}
