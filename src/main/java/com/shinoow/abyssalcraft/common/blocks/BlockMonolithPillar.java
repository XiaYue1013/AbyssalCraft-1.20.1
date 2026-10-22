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

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.IEnergyAmplifier;

/**
 * Monolith Pillar, amplifies the range of PE manipulators placed on top of it
 */
public class BlockMonolithPillar extends BlockShaped implements IEnergyAmplifier {

	public BlockMonolithPillar(Properties properties) {
		super(properties, box(4, 0, 4, 12, 16, 12));
	}

	@Override
	public AmplifierType getAmplifierType() {
		return AmplifierType.RANGE;
	}
}
