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
import com.shinoow.abyssalcraft.client.model.entity.AbyssalZombieModel;
import com.shinoow.abyssalcraft.common.entity.EntityDreadguard;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/** Zombie model and armor, 1.5 times the size */
public class RenderDreadguard extends HumanoidMobRenderer<EntityDreadguard, AbyssalZombieModel<EntityDreadguard>> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/elite/dread_guard.png");
	private static final float SCALE = 1.5F;

	public RenderDreadguard(EntityRendererProvider.Context context) {
		super(context, new AbyssalZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
		addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(EntityDreadguard entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(SCALE, SCALE, SCALE);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityDreadguard entity) {
		return TEXTURE;
	}
}
