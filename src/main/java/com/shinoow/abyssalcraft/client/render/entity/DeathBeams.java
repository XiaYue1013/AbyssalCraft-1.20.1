/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the ender dragon style light beams of LayerJzaharDeath and RenderImplosion
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import java.util.Random;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/** Random light beams fanning out from a point, white at the center and fading into a color */
public class DeathBeams {

	/**
	 * @param count number of beams
	 * @param fade 0 - 1, fades the centers out
	 * @param maxRotation random rotation per axis (1.12.2: 360 for J'zahar, 720 for the implosion)
	 * @param spin extra rotation of the last axis
	 * @param length random length range and minimum: length = random * lengthRange + lengthMin
	 * @param square square beams (the implosion) instead of triangular ones
	 */
	public static void render(PoseStack poseStack, MultiBufferSource buffers, int count, float fade, float maxRotation, float spin, float lengthRange,
			float lengthMin, float widthMin, boolean square, int r, int g, int b) {
		Random random = new Random(432L);
		VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
		poseStack.pushPose();
		for(int i = 0; i < count; ++i) {
			poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * maxRotation));
			poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * maxRotation));
			poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * maxRotation));
			poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * maxRotation));
			poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * maxRotation));
			poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * maxRotation + spin));
			float length = random.nextFloat() * lengthRange + lengthMin;
			float width = random.nextFloat() * 2.0F + widthMin;
			int alpha = (int)(255.0F * (1.0F - fade));
			Matrix4f m = poseStack.last().pose();
			float ax = square ? -1.0F : -0.866F, az = square ? -1.0F : -0.5F, bx = square ? 1.0F : 0.866F;
			// three triangles: center, then two of the three far corners (1.12.2 drew one triangle fan)
			float[][] corners = {{ax * width, az * width}, {bx * width, az * width}, {0.0F, width}};
			for(int c = 0; c < 3; c++) {
				float[] p = corners[c], q = corners[(c + 1) % 3];
				consumer.vertex(m, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
				consumer.vertex(m, p[0], length, p[1]).color(r, g, b, 0).endVertex();
				consumer.vertex(m, q[0], length, q[1]).color(r, g, b, 0).endVertex();
			}
		}
		poseStack.popPose();
	}
}
