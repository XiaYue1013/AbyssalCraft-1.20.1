/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the vanilla sheep model reads these from Sheep; the Evil and Demon Sheep aren't Sheep
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.demon;

/** Head animation of a sheep shaped mob (grazing) */
public interface ISheepAnimation {

	/** 0 - 1, how far the head is lowered */
	float getHeadEatPositionScale(float partialTick);

	/** Head pitch in radians */
	float getHeadEatAngleScale(float partialTick);
}
