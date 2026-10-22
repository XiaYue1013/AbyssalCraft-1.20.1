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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.entity.EntityDreadedCharge;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/** A camera facing quad twice the size of a block, like the dragon fireball */
public class RenderDreadedCharge extends EntityRenderer<EntityDreadedCharge> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/dreaded_fireball.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

	public RenderDreadedCharge(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected int getBlockLightLevel(EntityDreadedCharge entity, BlockPos pos) {
		return 15;
	}

	@Override
	public void render(EntityDreadedCharge entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
		poseStack.pushPose();
		poseStack.scale(2.0F, 2.0F, 2.0F);
		poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		VertexConsumer consumer = buffers.getBuffer(RENDER_TYPE);
		vertex(consumer, matrix, normal, light, 0.0F, 0, 0, 1);
		vertex(consumer, matrix, normal, light, 1.0F, 0, 1, 1);
		vertex(consumer, matrix, normal, light, 1.0F, 1, 1, 0);
		vertex(consumer, matrix, normal, light, 0.0F, 1, 0, 0);
		poseStack.popPose();
		super.render(entity, yaw, partialTicks, poseStack, buffers, light);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, int light, float x, int y, int u, int v) {
		consumer.vertex(matrix, x - 0.5F, y - 0.25F, 0.0F).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityDreadedCharge entity) {
		return TEXTURE;
	}
}
