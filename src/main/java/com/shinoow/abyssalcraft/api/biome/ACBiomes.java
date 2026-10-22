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
package com.shinoow.abyssalcraft.api.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Keys for the AbyssalCraft biomes (biomes are data driven in 1.20.1, see data/abyssalcraft/worldgen/biome)
 * @author shinoow
 *
 */
public class ACBiomes {

	public static final ResourceKey<Biome> darklands = key("darklands");
	public static final ResourceKey<Biome> darklands_forest = key("darklands_forest");
	public static final ResourceKey<Biome> darklands_plains = key("darklands_plains");
	public static final ResourceKey<Biome> darklands_hills = key("darklands_hills");
	public static final ResourceKey<Biome> darklands_mountains = key("darklands_mountains");
	public static final ResourceKey<Biome> coralium_infested_swamp = key("coralium_infested_swamp");
	public static final ResourceKey<Biome> abyssal_wastelands = key("abyssal_wastelands");
	public static final ResourceKey<Biome> dreadlands = key("dreadlands");
	public static final ResourceKey<Biome> purified_dreadlands = key("purified_dreadlands");
	public static final ResourceKey<Biome> dreadlands_mountains = key("dreadlands_mountains");
	public static final ResourceKey<Biome> dreadlands_forest = key("dreadlands_forest");
	public static final ResourceKey<Biome> omothol = key("omothol");
	public static final ResourceKey<Biome> dark_realm = key("dark_realm");
	public static final ResourceKey<Biome> purged = key("purged");

	/** Replaces the 1.12.2 IDarklandsBiome marker interface */
	public static final TagKey<Biome> IS_DARKLANDS = TagKey.create(Registries.BIOME, new ResourceLocation("abyssalcraft", "is_darklands"));
	/** Replaces the 1.12.2 IDreadlandsBiome marker interface */
	public static final TagKey<Biome> IS_DREADLANDS = TagKey.create(Registries.BIOME, new ResourceLocation("abyssalcraft", "is_dreadlands"));

	private static ResourceKey<Biome> key(String name) {
		return ResourceKey.create(Registries.BIOME, new ResourceLocation("abyssalcraft", name));
	}
}
