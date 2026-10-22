/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemEoA extends ItemACBasic {
	public ItemEoA() { super(new Properties().stacksTo(1)); color(ChatFormatting.AQUA); }
	@Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
		super.appendHoverText(stack, level, lines, flag); lines.add(Component.translatable("tooltip.eoa"));
	}
}
