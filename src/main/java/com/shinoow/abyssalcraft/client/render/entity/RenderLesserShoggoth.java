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

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelLesserShoggoth;
import com.shinoow.abyssalcraft.client.render.entity.layers.LayerLesserShoggothEyes;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Lesser Shoggoth renderer: one texture per type, glowing eyes, and the shadow Shoggoths are translucent
 */
public class RenderLesserShoggoth extends MobRenderer<EntityLesserShoggoth, ModelLesserShoggoth> {

	private static final ResourceLocation[] TEXTURES = {texture("lessershoggoth"), texture("abyssalshoggoth"), texture("dreadedshoggoth"),
			texture("omotholshoggoth"), texture("shadowshoggoth")};

	public RenderLesserShoggoth(EntityRendererProvider.Context context) {
		super(context, new ModelLesserShoggoth(context.bakeLayer(ACModelLayers.LESSER_SHOGGOTH)), 1.6F);
		addLayer(new LayerLesserShoggothEyes(this));
	}

	static ResourceLocation texture(String name) {
		return AbyssalCraft.loc("textures/model/shoggoth/" + name + ".png");
	}

	@Nullable
	@Override
	protected RenderType getRenderType(EntityLesserShoggoth entity, boolean bodyVisible, boolean translucent, boolean glowing) {
		if(entity.getShoggothType() == 4 && bodyVisible)
			return RenderType.entityTranslucent(getTextureLocation(entity));
		return super.getRenderType(entity, bodyVisible, translucent, glowing);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityLesserShoggoth entity) {
		int type = entity.getShoggothType();
		return TEXTURES[type >= 0 && type < TEXTURES.length ? type : 0];
	}
}
