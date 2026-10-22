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
 *     1.20.1 port - TileEntityPedestalBlockRenderer and TileEntityAltarBlockRenderer merged (they only differed
 *     in the item height)
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.api.block.ISingletonInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Renders the item placed on pedestal/altar blocks<br>
 * (the block rendering itself is done through JSON models).
 * @author shinoow
 *
 */
public class TileEntityPedestalBlockRenderer<T extends BlockEntity & ISingletonInventory> implements BlockEntityRenderer<T> {

	private final float blockHeight, itemHeight;

	/**
	 * @param blockHeight Height (above the block's base) of a block item
	 * @param itemHeight Height (above the block's base) of other items
	 */
	public TileEntityPedestalBlockRenderer(float blockHeight, float itemHeight) {
		this.blockHeight = blockHeight;
		this.itemHeight = itemHeight;
	}

	/** Pedestals (Energy/Ritual/Rending Pedestal) */
	public static <T extends BlockEntity & ISingletonInventory> BlockEntityRendererProvider<T> pedestal() {
		return context -> new TileEntityPedestalBlockRenderer<>(0.94F, 1.13F);
	}

	/** Altars (Sacrificial/Ritual Altar) */
	public static <T extends BlockEntity & ISingletonInventory> BlockEntityRendererProvider<T> altar() {
		return context -> new TileEntityPedestalBlockRenderer<>(0.88F, 1.07F);
	}

	@Override
	public void render(T te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		ItemStack stack = te.getItem();
		if(stack.isEmpty()) return;

		poseStack.pushPose();
		poseStack.translate(0.5F, stack.getItem() instanceof BlockItem ? blockHeight : itemHeight, 0.5F);
		if(te.shouldItemRotate() && te.getLevel() != null)
			poseStack.mulPose(Axis.YP.rotationDegrees((te.getLevel().getGameTime() + partialTicks) % 360));
		poseStack.mulPose(Axis.YP.rotationDegrees(180));
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffer, te.getLevel(), 0);
		poseStack.popPose();
	}
}
