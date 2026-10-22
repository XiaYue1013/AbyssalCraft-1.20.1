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
package com.shinoow.abyssalcraft.data;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.ItemHandler;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * Writes tools/generated/item_model_map.json: item id -> upstream 1.12.2 item model name.<br>
 * The asset importer uses it to convert the (All Rights Reserved) upstream item models and textures for local builds.
 */
public class ACModelMapProvider implements DataProvider {

	private final Path out;

	public ACModelMapProvider(PackOutput output) {
		out = output.getOutputFolder().resolve("../../../tools/generated/item_model_map.json").normalize();
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		JsonObject json = new JsonObject();
		ItemHandler.ITEM_MODELS.forEach(json::addProperty);
		BlockHandler.INFO.forEach((name, info) -> {
			if(info.hasItem && info.itemModel != null)
				json.addProperty(name, info.itemModel);
		});
		return DataProvider.saveStable(cache, json, out);
	}

	@Override
	public String getName() {
		return "AbyssalCraft item model map";
	}
}
