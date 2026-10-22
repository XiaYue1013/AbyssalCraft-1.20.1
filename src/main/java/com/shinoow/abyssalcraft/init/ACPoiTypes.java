/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - points of interest used to link gateways (replaces TeleporterAC's block scan and cache)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ACPoiTypes {

	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, AbyssalCraft.modid);

	public static final ResourceKey<PoiType> ABYSSAL_GATEWAY = key("abyssal_gateway");
	public static final ResourceKey<PoiType> DREADED_GATEWAY = key("dreaded_gateway");
	public static final ResourceKey<PoiType> OMOTHOL_GATEWAY = key("omothol_gateway");

	// block lookups are lazy, BlockHandler may still be initializing when this class loads
	static {
		register(ABYSSAL_GATEWAY, () -> BlockHandler.ABYSSAL_GATEWAY.get());
		register(DREADED_GATEWAY, () -> BlockHandler.DREADED_GATEWAY.get());
		register(OMOTHOL_GATEWAY, () -> BlockHandler.OMOTHOL_GATEWAY.get());
	}

	private static ResourceKey<PoiType> key(String name) {
		return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, AbyssalCraft.loc(name));
	}

	private static void register(ResourceKey<PoiType> key, Supplier<Block> block) {
		POI_TYPES.register(key.location().getPath(), () -> new PoiType(ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates()), 0, 1));
	}
}
