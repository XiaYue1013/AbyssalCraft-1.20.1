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

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Coralium Gem Cluster (2 to 9 gems)
 */
public class ItemCoraliumCluster extends ItemACBasic {

	public final String gemName;

	public ItemCoraliumCluster(String gems) {
		super(new Properties().stacksTo(16));
		gemName = gems;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.literal(gemName + " Gems"));
	}

	@Override
	public String getDescriptionId() {
		return "item.abyssalcraft.coralium_gem_cluster";
	}
}
