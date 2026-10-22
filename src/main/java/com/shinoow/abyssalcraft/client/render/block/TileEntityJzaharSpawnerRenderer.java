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
package com.shinoow.abyssalcraft.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.block.ModelJzaharSpawner;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySingleMobSpawner;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** The J'zahar spawner shows J'zahar sitting on his throne until a player comes close (the other spawners are invisible) */
public class TileEntityJzaharSpawnerRenderer implements BlockEntityRenderer<TileEntitySingleMobSpawner> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/boss/jzahar.png");
	private final ModelJzaharSpawner<?> model;

	public TileEntityJzaharSpawnerRenderer(BlockEntityRendererProvider.Context context) {
		model = new ModelJzaharSpawner<>(context.bakeLayer(ACModelLayers.JZAHAR_SPAWNER));
	}

	@Override
	public void render(TileEntitySingleMobSpawner tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
		if(!tile.getBlockState().is(BlockHandler.JZAHAR_SPAWNER.get())) return;
		poseStack.pushPose();
		poseStack.translate(0.675F, 0.9F, 0.5F);
		poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
		poseStack.scale(1.5F, 1.5F, 1.5F);
		model.setupAnim(null, 0.0F, 0.0F, 8.0F, 0.0F, 0.0F);
		model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(TileEntitySingleMobSpawner tile) {
		return true;
	}
}
