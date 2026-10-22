/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (RenderJzahar, LayerJzaharDeath)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelJzahar;
import com.shinoow.abyssalcraft.common.entity.EntityJzahar;
import com.shinoow.abyssalcraft.lib.ACClientVars;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/** J'zahar at 1.5 times the model size, with light beams breaking out of him while he dies */
public class RenderJzahar extends MobRenderer<EntityJzahar, ModelJzahar> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/boss/jzahar.png");

	public RenderJzahar(EntityRendererProvider.Context context) {
		super(context, new ModelJzahar(context.bakeLayer(ACModelLayers.JZAHAR)), 1.0F);
		addLayer(new DeathLayer(this));
	}

	@Override
	protected void scale(EntityJzahar entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(1.5F, 1.5F, 1.5F);
	}

	@Override
	public boolean shouldRender(EntityJzahar entity, Frustum frustum, double x, double y, double z) {
		return entity.deathTicks > 400 && entity.shouldRender(x, y, z) || super.shouldRender(entity, frustum, x, y, z);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityJzahar entity) {
		return TEXTURE;
	}

	/** 1.12.2 LayerJzaharDeath: the beams of the last 400 death ticks */
	private static class DeathLayer extends RenderLayer<EntityJzahar, ModelJzahar> {

		DeathLayer(RenderLayerParent<EntityJzahar, ModelJzahar> parent) {
			super(parent);
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, int light, EntityJzahar entity, float limbSwing, float limbSwingAmount,
				float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			if(entity.deathTicks <= 400) return;
			float f = (entity.deathTicks + partialTicks) / 400.0F;
			float fade = f > 0.8F ? (f - 0.8F) / 0.2F : 0.0F;
			poseStack.pushPose();
			poseStack.scale(0.25F, 0.25F, 0.25F);
			int[] color = ACClientVars.JZAHAR_DEATH;
			DeathBeams.render(poseStack, buffers, (int) Math.ceil((f + f * f) / 2.0F * 30.0F), fade, 360.0F, f * 90.0F, 20.0F, 5.0F + fade, 1.0F + fade,
					false, color[0], color[1], color[2]);
			poseStack.popPose();
		}
	}
}
