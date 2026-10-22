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

import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.MiscCondition;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Coralium/Dread Plague Antidote, 10 drinks. Cures effects that list it as a curative item (the matching plague)
 */
public class ItemAntidote extends ItemACBasic {

	private final boolean dread;

	public ItemAntidote(boolean dread) {
		super(new Properties().stacksTo(1));
		this.dread = dread;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}

	/**
	 * Fill level for the item model ("content" property)
	 */
	public static float getContentToFloat(ItemStack stack) {
		if(!stack.hasTag() || stack.getTag().getInt("content") <= 0) return 0;
		int content = stack.getTag().getInt("content");
		if(content < 9 && content > 6)
			return 0.2f;
		if(content < 7 && content > 4)
			return 0.4f;
		if(content < 5 && content > 2)
			return 0.6f;
		if(content < 3 && content > 0)
			return 0.8f;
		return 0;
	}

	private void drink(ItemStack stack) {
		if(stack.getOrCreateTag().contains("content"))
			stack.getTag().putInt("content", stack.getTag().getInt("content") - 1);
		else
			stack.getTag().putInt("content", 9);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (!worldIn.isClientSide) entityLiving.curePotionEffects(stack);
		Player entityplayer = entityLiving instanceof Player ? (Player)entityLiving : null;
		if (entityplayer == null || !entityplayer.getAbilities().instabuild) {
			drink(stack);
			if (stack.getTag().getInt("content") == 0)
				return new ItemStack(Items.GLASS_BOTTLE);
		}
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag){
		if(is.hasTag() && is.getTag().contains("content"))
			l.add(Component.literal(is.getTag().getInt("content")+" uses left"));
		else l.add(Component.literal("10 uses left"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
	}

	@Override
	public IUnlockCondition getUnlockCondition(ItemStack stack) {
		return new MiscCondition((dread ? "dread" : "coralium")+"_plague");
	}
}
