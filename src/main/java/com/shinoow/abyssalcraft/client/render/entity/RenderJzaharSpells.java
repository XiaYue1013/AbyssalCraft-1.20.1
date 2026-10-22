/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (RenderBlackHole, RenderImplosion)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.entity.EntityBlackHole;
import com.shinoow.abyssalcraft.common.entity.EntityImplosion;
import com.shinoow.abyssalcraft.lib.ACClientVars;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

/** The two spell entities of J'zahar */
public class RenderJzaharSpells {

	/** A spinning, wobbling 4x4 disc facing the camera */
	public static class BlackHole extends EntityRenderer<EntityBlackHole> {

		private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/black_hole.png");

		public BlackHole(EntityRendererProvider.Context context) {
			super(context);
		}

		@Override
		public boolean shouldRender(EntityBlackHole entity, Frustum frustum, double x, double y, double z) {
			return true;
		}

		@Override
		public void render(EntityBlackHole entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
			float ticks = entity.tickCount + partialTicks;
			float wobble = ticks / 10f;
			float wobbleX = (float) (Math.sin(wobble) * 0.05F) + 1;
			float wobbleY = (float) (Math.sin(wobble) * -1 * 0.05F) + 1;
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.scale(wobbleX, wobbleY, 1);
			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
			poseStack.mulPose(Axis.ZP.rotationDegrees(ticks * 2));
			Matrix4f m = poseStack.last().pose();
			Matrix3f n = poseStack.last().normal();
			VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
			float s = 2;
			vertex(consumer, m, n, -s, -s, 0, 1);
			vertex(consumer, m, n, s, -s, 1, 1);
			vertex(consumer, m, n, s, s, 1, 0);
			vertex(consumer, m, n, -s, s, 0, 0);
			poseStack.popPose();
		}

		private static void vertex(VertexConsumer consumer, Matrix4f m, Matrix3f n, float x, float y, float u, float v) {
			consumer.vertex(m, x, y, 0).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0)
					.normal(n, 0, 0, 1).endVertex();
		}

		@Override
		public ResourceLocation getTextureLocation(EntityBlackHole entity) {
			return TEXTURE;
		}
	}

	/** A growing, turning ball of light beams that bursts after 380 ticks */
	public static class Implosion extends EntityRenderer<EntityImplosion> {

		public Implosion(EntityRendererProvider.Context context) {
			super(context);
		}

		@Override
		public boolean shouldRender(EntityImplosion entity, Frustum frustum, double x, double y, double z) {
			return true;
		}

		@Override
		public void render(EntityImplosion entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
			if(entity.tickCount <= 0) return;
			float f = (entity.tickCount + partialTicks) / 380.0F;
			float fade = f > 1.0F ? (f - 1.0F) / 0.2F : 0.0F;
			poseStack.pushPose();
			poseStack.scale(2F + f, 2F + f, 2F + f);
			poseStack.translate(0.0F, 0.375F, 0.0F);
			poseStack.mulPose(Axis.of(new org.joml.Vector3f(1, 1, 1).normalize()).rotationDegrees(entity.tickCount * 2F));
			int[] color = ACClientVars.IMPLOSION;
			DeathBeams.render(poseStack, buffers, (int) Math.ceil((f + f * f) / 2.0F * 160.0F), fade, 720.0F, f * 90.0F, 40.0F, 5.0F + fade * 10.0F,
					1.0F + fade * 2.0F, true, color[0], color[1], color[2]);
			poseStack.popPose();
		}

		@Override
		@SuppressWarnings("deprecation")
		public ResourceLocation getTextureLocation(EntityImplosion entity) {
			return TextureAtlas.LOCATION_BLOCKS;
		}
	}
}
