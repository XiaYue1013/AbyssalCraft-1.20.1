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
 *     1.20.1 port - damage sources are data driven, keyed on the damage type
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.util;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ArmorUtil {

	/**
	 * Checks if the target has a helmet with resistance to the source
	 * @param target Entity about to be attacked
	 * @param source Probably a plague or other damage type
	 * @return Whether or not the target has resistance against the source
	 */
	public static boolean hasHelmetWithResistance(LivingEntity target, ResourceKey<DamageType> source) {

		ItemStack helmet = target.getItemBySlot(EquipmentSlot.HEAD);

		if(!helmet.isEmpty())
			if(source == AbyssalCraftAPI.coralium) {
				if(helmet.is(ACItems.plated_coralium_helmet.get()) ||
						helmet.is(ACItems.depths_helmet.get()) ||
						helmet.is(ACItems.ethaxium_helmet.get()))
					return true;
			}
			else if(source == AbyssalCraftAPI.dread)
				if(helmet.is(ACItems.dreaded_abyssalnite_helmet.get())
				|| helmet.is(ACItems.dreadium_samurai_helmet.get())
				|| helmet.is(ACItems.ethaxium_helmet.get()))
					return true;

		return false;
	}
}
