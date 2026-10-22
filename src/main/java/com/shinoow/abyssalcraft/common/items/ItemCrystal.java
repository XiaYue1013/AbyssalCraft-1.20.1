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

import com.shinoow.abyssalcraft.api.item.ICrystal;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Crystal, Crystal Shard and Crystal Fragment (one item per element, flattened from the 1.12.2 metadata items)
 */
public class ItemCrystal extends ItemACBasic implements ICrystal {

	/** Chemical formula per crystal index (1.12.2 ACLib.crystalAtoms) */
	public static final String[] ATOMS = new String[]{"Fe", "Au", "S", "C", "O", "H", "N", "P", "K", "NO₃", "CH₄", "none", "An",
			"Cor", "Dr", "none", "Sn", "Cu", "Si", "Mg", "Al", "SiO₂", "Al₂O₃", "MgO", "Zn", "Ca", "Be", "Be₃Al₂(SiO₃)₆"};

	private final int index;

	public ItemCrystal(int index) {
		super();
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag){
		l.add(Component.translatable("tooltip.crystal").append(": " + ATOMS[index]));
	}
}
