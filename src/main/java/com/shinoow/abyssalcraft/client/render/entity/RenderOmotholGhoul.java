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

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelDG;
import com.shinoow.abyssalcraft.client.model.entity.ModelDGArmor;
import com.shinoow.abyssalcraft.client.render.entity.layers.LayerGhoulArmor;
import com.shinoow.abyssalcraft.client.render.entity.layers.LayerGhoulHeldItem;
import com.shinoow.abyssalcraft.common.entity.EntityOmotholGhoul;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

/** The Depths Ghoul model, 1.2 times the size, with its armor and held item layers */
public class RenderOmotholGhoul extends MobRenderer<EntityOmotholGhoul, ModelDG<EntityOmotholGhoul>> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/omothol_ghoul.png");

	public RenderOmotholGhoul(EntityRendererProvider.Context context) {
		super(context, new ModelDG<>(context.bakeLayer(ACModelLayers.DEPTHS_GHOUL)), 0.8F);
		addLayer(new LayerGhoulHeldItem<>(this, context.getItemInHandRenderer()));
		addLayer(new LayerGhoulArmor<>(this, new ModelDGArmor<>(context.bakeLayer(ACModelLayers.GHOUL_INNER_ARMOR)),
				new ModelDGArmor<>(context.bakeLayer(ACModelLayers.GHOUL_OUTER_ARMOR))));
		addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
	}

	@Override
	protected void scale(EntityOmotholGhoul entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(1.2F, 1.2F, 1.2F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityOmotholGhoul entity) {
		return TEXTURE;
	}
}
