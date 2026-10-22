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

import net.minecraft.core.BlockPos;

/**
 * Interface for Ritual Pedestal block entities
 *
 * @author shinoow
 *
 */
public interface IRitualPedestal extends ISingletonInventory {

	IRitualAltar getAltar();

	void setAltar(BlockPos pos);

	void consumeItem();
}
