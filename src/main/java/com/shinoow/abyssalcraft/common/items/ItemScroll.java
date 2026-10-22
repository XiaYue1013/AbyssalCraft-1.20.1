/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.spell.IScroll;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import com.shinoow.abyssalcraft.api.spell.SpellUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Scroll (1.12.2 "scroll" basic/lesser/moderate/greater and "unique_scroll" antimatter/oblivion). An inscribed scroll
 * casts its spell: instant spells on use, charging spells after holding use for 50 ticks. PE comes from a PE item in
 * the off hand or hotbar.
 */
public class ItemScroll extends ItemACBasic implements IScroll {

	private final ScrollType type;

	public ItemScroll(ScrollType type) {
		super(new Properties().stacksTo(1));
		this.type = type;
	}

	@Override
	public ScrollType getScrollType(ItemStack stack) {
		return type;
	}

	public static boolean isInscribed(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Spell");
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		Spell spell = SpellUtils.getSpell(stack);
		return spell != null && spell.requiresCharging() ? 50 : 0;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if(entity instanceof Player player)
			SpellUtils.castChargingSpell(stack, level, player);
		return stack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		SpellUtils.castInstantSpell(stack, level, player, hand);
		Spell spell = SpellUtils.getSpell(stack);
		return spell != null && spell.requiresCharging() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		Spell spell = SpellUtils.getSpell(stack);
		if(spell != null) {
			tooltip.add(Component.literal("Spell: ").append(Component.translatable(spell.getUnlocalizedName()).withStyle(ChatFormatting.AQUA)));
			tooltip.add(Component.literal("Required PE per cast: " + (int) spell.getReqEnergy()));
			tooltip.add(Component.literal("Cast type: ").append(Component.literal(spell.requiresCharging() ? "Charge" : "Instant").withStyle(ChatFormatting.GOLD)));
		}
	}
}
