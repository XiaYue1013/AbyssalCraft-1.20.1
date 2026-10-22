/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (MiscHandler); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The six enchantments (looked up through {@link com.shinoow.abyssalcraft.api.AbyssalCraftAPI}).
 * Port change: Coralium / Dread are always registered (a config dependent registry breaks client/server syncing);
 * the "plague_enchantments" option now only removes their rituals.
 */
public class ACEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AbyssalCraft.modid);

	static {
		ENCHANTMENTS.register("coralium", () -> new EnchantmentWeaponInfusion(false));
		ENCHANTMENTS.register("dread", () -> new EnchantmentWeaponInfusion(true));
		ENCHANTMENTS.register("light_pierce", EnchantmentLightPierce::new);
		ENCHANTMENTS.register("iron_wall", EnchantmentIronWall::new);
		ENCHANTMENTS.register("sapping", EnchantmentSapping::new);
		ENCHANTMENTS.register("multi_rend", EnchantmentMultiRend::new);
	}
}
