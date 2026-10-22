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
package com.shinoow.abyssalcraft.api.block;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for block entities holding a single ItemStack (pedestals, altars etc.)
 *
 * @author shinoow
 *
 */
public interface ISingletonInventory {

	/**
	 * Fetches the contained ItemStack
	 */
	ItemStack getItem();

	/**
	 * Sets the contained ItemStack (implementations sync the change to clients)
	 */
	void setItem(ItemStack item);

	/**
	 * Whether or not the item rendered on top of the block should rotate
	 */
	default boolean shouldItemRotate() {
		return true;
	}
}
