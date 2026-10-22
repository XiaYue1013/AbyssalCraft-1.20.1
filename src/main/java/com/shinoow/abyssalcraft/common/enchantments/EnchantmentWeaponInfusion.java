/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.enchantments;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Coralium / Dread weapon infusion: hits give 5 seconds of the plague. Only from the infusion rituals, never on books. */
public class EnchantmentWeaponInfusion extends Enchantment {

	private final boolean dread;

	public EnchantmentWeaponInfusion(boolean dread) {
		super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
		this.dread = dread;
	}

	@Override
	public int getMinCost(int level) {
		return Short.MAX_VALUE;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 30;
	}

	@Override
	public void doPostAttack(LivingEntity user, Entity target, int level) {
		if(user.level().isClientSide || !(target instanceof LivingEntity entity)) return;
		if(user instanceof Player attacker && target instanceof Player victim && !attacker.canHarmPlayer(victim)) return;
		if(dread ? !EntityUtil.isEntityDread(entity) : !EntityUtil.isEntityCoralium(entity))
			entity.addEffect(new MobEffectInstance(dread ? AbyssalCraftAPI.dread_plague.get() : AbyssalCraftAPI.coralium_plague.get(), 100));
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return category.canEnchant(stack.getItem());
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return !(other instanceof EnchantmentWeaponInfusion) && super.checkCompatibility(other);
	}
}
