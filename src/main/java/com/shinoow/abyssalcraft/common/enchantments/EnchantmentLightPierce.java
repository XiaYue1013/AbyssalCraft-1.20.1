/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.enchantments;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.DamageEnchantment;

/**
 * Light Pierce: like Sharpness, +2.5 damage per level against shadow mobs. A damage enchantment, so it doesn't go
 * together with Sharpness / Smite / Bane of Arthropods, and also fits axes.
 */
public class EnchantmentLightPierce extends DamageEnchantment {

	public EnchantmentLightPierce() {
		// type 0 ("all"): the methods that read the type's table are overridden, the arthropod check doesn't apply
		super(Rarity.COMMON, 0, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getMinCost(int level) {
		return 5 + (level - 1) * 8;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 20;
	}

	@Override
	public float getDamageBonus(int level, MobType type) {
		return type == AbyssalCraftAPI.SHADOW ? level * 2.5F : 0.0F;
	}
}
