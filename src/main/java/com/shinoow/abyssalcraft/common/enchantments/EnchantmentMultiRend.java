/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.enchantments;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

/** Multi-Rend (Staffs of Rending): also drains the mobs within 3 blocks of the target */
public class EnchantmentMultiRend extends Enchantment {

	public EnchantmentMultiRend() {
		super(Rarity.RARE, AbyssalCraftAPI.STAFF_OF_RENDING, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinCost(int level) {
		return 20;
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + 20;
	}
}
