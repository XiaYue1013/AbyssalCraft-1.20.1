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
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.api.item.ICrystal;

/**
 * Crystal Cluster (one block per crystal element, flattened from crystalcluster/crystalcluster2)
 */
public class BlockCrystalCluster extends BlockShaped implements ICrystal {

	private final int index;

	public BlockCrystalCluster(Properties properties, int index) {
		super(properties, box(3.2, 0, 3.2, 12.8, 11.2, 12.8));
		this.index = index;
	}

	/**
	 * Crystal index (see ACItems.CRYSTAL_NAMES)
	 */
	public int getIndex() {
		return index;
	}
}
