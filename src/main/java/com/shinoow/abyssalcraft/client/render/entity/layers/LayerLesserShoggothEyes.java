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
package com.shinoow.abyssalcraft.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.entity.ModelLesserShoggoth;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * The glowing (full bright, additive) eyes of the Lesser Shoggoth ("shoggothGlowingEyes" option)
 */
public class LayerLesserShoggothEyes extends RenderLayer<EntityLesserShoggoth, ModelLesserShoggoth>
{
	private static final RenderType[] EYES = {eyes("lessershoggoth"), eyes("abyssalshoggoth"), eyes("dreadedshoggoth"), eyes("omotholshoggoth"),
			eyes("shadowshoggoth")};

	public LayerLesserShoggothEyes(RenderLayerParent<EntityLesserShoggoth, ModelLesserShoggoth> shoggothRendererIn)
	{
		super(shoggothRendererIn);
	}

	private static RenderType eyes(String name) {
		return RenderType.eyes(AbyssalCraft.loc("textures/model/shoggoth/" + name + "_eyes.png"));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EntityLesserShoggoth entity, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!ACConfig.shoggothGlowingEyes || entity.isInvisible()) return;
		int type = entity.getShoggothType();
		// the shadow Shoggoth's eyes fade with it (the model already scales the alpha, the additive blend needs the color)
		float brightness = type == 4 ? entity.getLightLevelDependentMagicValue() : 1.0F;
		getParentModel().renderToBuffer(poseStack, buffer.getBuffer(EYES[type >= 0 && type < EYES.length ? type : 0]), 15728640, OverlayTexture.NO_OVERLAY,
				brightness, brightness, brightness, 1.0F);
	}
}
