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
package com.shinoow.abyssalcraft.lib;

/**
 * Client-side visual constants (1.12.2 defaults). Biome grass/foliage/water colors live in the biome JSON in 1.20.1;
 * the remaining values are used by the color handlers, dimension fog and particle effects.
 */
public class ACClientVars {

	private static final int[] crystalColors = new int[]{0xD9D9D9, 0xF3CC3E, 0xF6FF00, 0x3D3D36, 16777215, 16777215, 16777215, 0x996A18,
			0xD9D9D9, 0x1500FF, 0x19FC00, 0xFF0000, 0x4a1c89, 0x00FFEE, 0x880101, 0xFFCC00, 0xD9D8D7, 0xE89207, 0xD9D9D9,
			0xD9D9D9, 0xD9D9D9, 16777215, 0xD9D8D9, 16777215, 0xD7D8D9, 0xD7D8D9, 0xD9D9D9, 16777215};

	/** Fog colors of the dimensions (r, g, b in 0-255) */
	public static final int[] ABYSSAL_WASTELAND_FOG = {0, 105, 45};
	public static final int[] DREADLANDS_FOG = {100, 14, 14};
	public static final int[] OMOTHOL_FOG = {40, 30, 40};
	public static final int[] DARK_REALM_FOG = {30, 20, 30};

	public static final int CORALIUM_PLAGUE_COLOR = 0x00FFFF;
	public static final int DREAD_PLAGUE_COLOR = 0xAD1313;
	public static final int ANTIMATTER_COLOR = 0xFFFFFF;

	public static final int[] ASORAH_DEATH = {0, 255, 255};
	public static final int[] JZAHAR_DEATH = {81, 189, 178};
	public static final int[] IMPLOSION = {255, 255, 255};

	/**
	 * Tint colors of the crystals, by crystal index
	 */
	public static int[] getCrystalColors() {
		return crystalColors;
	}
}
