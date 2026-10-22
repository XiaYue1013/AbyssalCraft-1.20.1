/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the 1.12.2 RenderLiving subclasses that only chose a model, a texture and a scale
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** A mob drawn with one model and one texture, optionally scaled */
public class RenderSimpleMob<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {

	private final ResourceLocation texture;
	private final float scale;
	private boolean alwaysRender;

	/** @param texture path below textures/model/, without .png */
	public RenderSimpleMob(EntityRendererProvider.Context context, M model, String texture, float scale) {
		super(context, model, 0.5F);
		this.texture = AbyssalCraft.loc("textures/model/" + texture + ".png");
		this.scale = scale;
	}

	/** Rendered even when its bounding box is out of view (big bosses whose model is larger than the hitbox) */
	public RenderSimpleMob<T, M> alwaysRender() {
		alwaysRender = true;
		return this;
	}

	@Override
	public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
		return alwaysRender && entity.shouldRender(x, y, z) || super.shouldRender(entity, frustum, x, y, z);
	}

	@Override
	protected void scale(T entity, PoseStack poseStack, float partialTick) {
		if(scale != 1.0F)
			poseStack.scale(scale, scale, scale);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return texture;
	}
}
