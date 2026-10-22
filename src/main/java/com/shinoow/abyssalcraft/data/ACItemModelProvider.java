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

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Generates block item models. Plain item models are imported from the upstream models (see {@link ACModelMapProvider}).
 */
public class ACItemModelProvider extends ItemModelProvider {

	public ACItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, AbyssalCraft.modid, helper);
	}

	private ResourceLocation tex(String name) {
		return name.contains(":") ? new ResourceLocation(name) : modLoc("block/" + name);
	}

	@Override
	protected void registerModels() {
		for(String fluid : new String[] {"liquid_coralium", "liquid_antimatter"})
			withExistingParent(fluid + "_bucket", new ResourceLocation("forge", "item/bucket_drip"))
			.customLoader(net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin)
			.fluid(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(modLoc(fluid))).end();
		for(var egg : com.shinoow.abyssalcraft.init.ItemHandler.SPAWN_EGGS)
			withExistingParent(egg.getId().getPath(), mcLoc("item/template_spawn_egg"));
		BlockHandler.INFO.forEach((name, info) -> {
			if(!info.hasItem || info.itemModel != null) return;
			switch(info.model) {
			case WALL, FENCE, BUTTON:
				withExistingParent(name, modLoc("block/" + name + "_inventory"));
				break;
			case CROSS:
				withExistingParent(name, mcLoc("item/generated")).texture("layer0", tex(info.textures[0]));
				break;
			case DOOR:
				break;
			case CUSTOM, CUSTOM_FACING:
				// the block model is the imported upstream model
				withExistingParent(name, info.textures[0].contains(":") ? new ResourceLocation(info.textures[0]) : modLoc("block/" + info.textures[0]));
				break;
			case LAYERS:
				withExistingParent(name, modLoc("block/" + name + "_height2"));
				break;
			case RELAY:
				withExistingParent(name, modLoc("block/" + (info.textures.length > 1 ? name : info.textures[0])));
				break;
			case SLAB, STAIRS, PRESSURE_PLATE, ORIENTABLE, ORIENTABLE_LIT, CUBE, GLASS, COLUMN, BOTTOM_TOP, PILLAR, LEAVES, NONE:
				withExistingParent(name, modLoc("block/" + name));
				break;
			}
		});
	}
}
