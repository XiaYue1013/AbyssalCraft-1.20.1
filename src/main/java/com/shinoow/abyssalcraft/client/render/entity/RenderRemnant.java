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
package com.shinoow.abyssalcraft.client.render.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelRemnant;
import com.shinoow.abyssalcraft.common.entity.EntityRemnant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

/** One texture per profession; a head slot item (the Halloween pumpkins) is drawn on the head */
public class RenderRemnant extends MobRenderer<EntityRemnant, ModelRemnant<EntityRemnant>> {

	private static final ResourceLocation[] TEXTURES = {texture("remnant"), texture("remnant_librarian"), texture("remnant_priest"),
			texture("remnant_blacksmith"), texture("remnant_butcher"), texture("remnant_banker"), texture("remnant_master_blacksmith")};

	public RenderRemnant(EntityRendererProvider.Context context) {
		super(context, new ModelRemnant<>(context.bakeLayer(ACModelLayers.REMNANT)), 0.5F);
		addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
	}

	private static ResourceLocation texture(String name) {
		return AbyssalCraft.loc("textures/model/remnant/" + name + ".png");
	}

	@Override
	public ResourceLocation getTextureLocation(EntityRemnant entity) {
		int profession = entity.getProfession();
		return TEXTURES[profession >= 0 && profession < TEXTURES.length ? profession : 0];
	}
}
