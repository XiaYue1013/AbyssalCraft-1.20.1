/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (ACSkyRenderer, the WorldProviders' fog colors)
 *     1.20.1 port - dimension special effects
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.sky;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * Sky and fog of the AbyssalCraft dimensions: a tinted, tiled sky box (like the End's) and the 1.12.2 fog colors.
 * No clouds, the 1.12.2 dimensions weren't "surface worlds".
 */
public class ACDimensionEffects extends DimensionSpecialEffects {

	private final ResourceLocation texture;
	private final int r, g, b;
	private final Vec3 fog;
	private final boolean foggy;

	/**
	 * @param fog a fixed fog color, or null for the default (time of day dependent) fog
	 */
	public ACDimensionEffects(String texture, int r, int g, int b, Vec3 fog, boolean foggy) {
		super(Float.NaN, false, SkyType.NONE, false, false);
		this.texture = AbyssalCraft.loc("textures/environment/" + texture + ".png");
		this.r = r;
		this.g = g;
		this.b = b;
		this.fog = fog;
		this.foggy = foggy;
	}

	/** Abyssal Wasteland: green sky, default fog darkened by the fixed midnight */
	public static ACDimensionEffects abyssalWasteland() {
		return new ACDimensionEffects("abyssal_wasteland_sky", 0, 105, 45, null, false);
	}

	/** Dreadlands: red sky and fog */
	public static ACDimensionEffects dreadlands() {
		return new ACDimensionEffects("dreadlands_sky", 100, 14, 14, new Vec3(0.2D, 0.03D, 0.03D), true);
	}

	/** Omothol: purple-black sky and fog (10518688 * 0.15) */
	public static ACDimensionEffects omothol() {
		return new ACDimensionEffects("omothol_sky", 40, 30, 40, purpleFog(), true);
	}

	/** Dark Realm: darker version of Omothol's sky */
	public static ACDimensionEffects darkRealm() {
		return new ACDimensionEffects("omothol_sky", 30, 20, 30, purpleFog(), false);
	}

	private static Vec3 purpleFog() {
		int color = 10518688;
		return new Vec3((color >> 16 & 255) / 255.0D * 0.15D, (color >> 8 & 255) / 255.0D * 0.15D, (color & 255) / 255.0D * 0.15D);
	}

	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
		if(fog != null)
			return fog;
		return color.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
	}

	@Override
	public boolean isFoggyAt(int x, int y) {
		return foggy;
	}

	@Override
	public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, texture);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buffer = tesselator.getBuilder();
		for(int i = 0; i < 6; ++i) {
			poseStack.pushPose();
			switch(i) {
			case 1 -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			case 2 -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
			case 3 -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
			case 4 -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
			case 5 -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
			default -> {}
			}
			Matrix4f matrix = poseStack.last().pose();
			buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buffer.vertex(matrix, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(r, g, b, 255).endVertex();
			buffer.vertex(matrix, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(r, g, b, 255).endVertex();
			buffer.vertex(matrix, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(r, g, b, 255).endVertex();
			buffer.vertex(matrix, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(r, g, b, 255).endVertex();
			tesselator.end();
			poseStack.popPose();
		}
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		return true;
	}
}
