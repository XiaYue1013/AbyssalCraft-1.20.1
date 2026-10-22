/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.item.UnlockableItemClientExtensions;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.common.entity.EntityCoraliumArrow;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Coralium Longbow: a vanilla bow (same draw time, ammo and enchantments) that shoots Coralium arrows whatever the ammo,
 * 637 durability, enchantability 1, repaired with Refined Coralium Ingots.
 */
public class ItemCoraliumBow extends BowItem implements IUnlockableItem {

	private IUnlockCondition condition = new DefaultCondition();

	public ItemCoraliumBow() {
		super(new Item.Properties().durability(637));
	}

	@Override
	public AbstractArrow customArrow(AbstractArrow arrow) {
		return arrow.getOwner() instanceof LivingEntity shooter ? new EntityCoraliumArrow(arrow.level(), shooter) : arrow;
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(ChatFormatting.AQUA);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.corbow.1"));
		tooltip.add(Component.translatable("tooltip.corbow.2"));
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
		return repair.is(ACItems.refined_coralium_ingot.get()) || super.isValidRepairItem(stack, repair);
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
