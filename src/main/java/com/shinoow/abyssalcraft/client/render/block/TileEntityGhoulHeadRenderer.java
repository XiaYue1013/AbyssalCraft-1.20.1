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
 *     1.20.1 port - TileEntityDirectionalRenderer for the ghoul heads (texture from the block, rotation from FACING)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.client.model.block.ModelDGhead;
import com.shinoow.abyssalcraft.common.blocks.BlockGhoulHead;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityGhoulHead;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityGhoulHeadRenderer implements BlockEntityRenderer<TileEntityGhoulHead> {

	private final ModelDGhead model;

	public TileEntityGhoulHeadRenderer(BlockEntityRendererProvider.Context context) {
		model = new ModelDGhead(context.bakeLayer(ModelDGhead.LAYER));
	}

	@Override
	public void render(TileEntityGhoulHead te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		BlockState state = te.getBlockState();
		if(!(state.getBlock() instanceof BlockGhoulHead head)) return;
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.5F, 0.5F);
		poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
		poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(BlockGhoulHead.FACING).get2DDataValue() * 90));
		model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(head.getTexture())), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
	}
}
