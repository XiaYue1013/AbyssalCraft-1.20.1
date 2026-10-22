/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.enchantments;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

/** Iron Wall (chestplates): the wearer stops dead when hit, which cancels the knockback */
public class EnchantmentIronWall extends Enchantment {

	public EnchantmentIronWall() {
		super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST});
	}

	@Override
	public int getMinCost(int level) {
		return 14;
	}

	@Override
	public int getMaxCost(int level) {
		return super.getMinCost(level) + 30;
	}

	@Override
	public void doPostHurt(LivingEntity user, Entity attacker, int level) {
		user.setDeltaMovement(Vec3.ZERO);
	}
}
