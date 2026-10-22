/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (ItemStaff, StaffModeMessage); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Staff of the Gatekeeper: a R'lyehian Gateway Key (mode 0) or a Staff of Rending that drains 5 (mode 1),
 * switched with the staff mode key.
 */
public class ItemStaff extends ItemACBasic implements IStaffOfRending {

	public ItemStaff() {
		super(new Properties().stacksTo(1));
		color(ChatFormatting.BLUE);
	}

	public static int getMode(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt("Mode") : 0;
	}

	/** Toggles the mode, returns the new one */
	public static int switchMode(ItemStack stack) {
		int mode = getMode(stack) == 0 ? 1 : 0;
		stack.getOrCreateTag().putInt("Mode", mode);
		return mode;
	}

	public static Component modeName(int mode) {
		return Component.translatable(mode == 1 ? "item.abyssalcraft.staff_of_rending" : "item.abyssalcraft.gateway_key");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(getMode(stack) == 1 && !level.isClientSide)
			ItemDrainStaff.useStaff(level, player, stack);
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return getMode(context.getItemInHand()) == 0 ? ACItems.rlyehian_gateway_key.get().useOn(context) : InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.staff"));
		tooltip.add(Component.translatable("tooltip.staff.mode.1").append(": ").append(modeName(getMode(stack)).copy().withStyle(ChatFormatting.GOLD)));
		tooltip.add(Component.translatable("tooltip.staff.mode.2", Component.keybind("key.staff_mode.desc").withStyle(ChatFormatting.GOLD)));
		if(getMode(stack) == 1)
			ItemDrainStaff.addEnergyTooltip(this, stack, tooltip);
		else ACItems.rlyehian_gateway_key.get().appendHoverText(stack, level, tooltip, flag);
	}

	@Override
	public int getDrainAmount(ItemStack stack) {
		return 5 + Mth.clamp(ItemDrainStaff.level(AbyssalCraftAPI.sapping, stack), 0, 3);
	}

	@Override
	public void increaseEnergy(ItemStack stack, String type) {
		stack.getOrCreateTag().putInt("energy" + type, getEnergy(stack, type) + getDrainAmount(stack));
	}

	@Override
	public void setEnergy(int amount, ItemStack stack, String type) {
		stack.getOrCreateTag().putInt("energy" + type, amount);
	}

	@Override
	public int getEnergy(ItemStack stack, String type) {
		return stack.hasTag() ? stack.getTag().getInt("energy" + type) : 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 25;
	}
}
