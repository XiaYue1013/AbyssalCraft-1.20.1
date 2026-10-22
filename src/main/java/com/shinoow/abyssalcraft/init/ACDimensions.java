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
package com.shinoow.abyssalcraft.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Keys of the four AbyssalCraft dimensions (defined in data/abyssalcraft/dimension and dimension_type)
 */
public class ACDimensions {

	public static final ResourceKey<Level> ABYSSAL_WASTELAND = level("abyssal_wasteland");
	public static final ResourceKey<Level> DREADLANDS = level("dreadlands");
	public static final ResourceKey<Level> OMOTHOL = level("omothol");
	public static final ResourceKey<Level> DARK_REALM = level("dark_realm");

	public static final ResourceKey<DimensionType> ABYSSAL_WASTELAND_TYPE = type("abyssal_wasteland");
	public static final ResourceKey<DimensionType> DREADLANDS_TYPE = type("dreadlands");
	public static final ResourceKey<DimensionType> OMOTHOL_TYPE = type("omothol");
	public static final ResourceKey<DimensionType> DARK_REALM_TYPE = type("dark_realm");

	private static ResourceKey<Level> level(String name) {
		return ResourceKey.create(Registries.DIMENSION, new ResourceLocation("abyssalcraft", name));
	}

	private static ResourceKey<DimensionType> type(String name) {
		return ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation("abyssalcraft", name));
	}

	/**
	 * Checks if the dimension is one of AbyssalCraft's four dimensions
	 */
	public static boolean isACDimension(ResourceKey<Level> dim) {
		return dim == ABYSSAL_WASTELAND || dim == DREADLANDS || dim == OMOTHOL || dim == DARK_REALM;
	}
}
