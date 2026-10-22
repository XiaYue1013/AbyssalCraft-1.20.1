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
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.client.model.entity.ModelDG;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Items in the ghoul's claws (the claws hang lower than a biped's hands) */
public class LayerGhoulHeldItem<T extends LivingEntity, M extends ModelDG<T>> extends RenderLayer<T, M> {

	private final ItemInHandRenderer itemInHandRenderer;

	public LayerGhoulHeldItem(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
		super(parent);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick,
			float ageInTicks, float netHeadYaw, float headPitch) {
		boolean rightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
		ItemStack left = rightHanded ? entity.getOffhandItem() : entity.getMainHandItem();
		ItemStack right = rightHanded ? entity.getMainHandItem() : entity.getOffhandItem();
		if(left.isEmpty() && right.isEmpty())
			return;
		poseStack.pushPose();
		if(getParentModel().young) {
			poseStack.translate(0.0F, 0.625F, 0.0F);
			poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
			poseStack.scale(0.5F, 0.5F, 0.5F);
		}
		renderArm(entity, right, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, buffers, packedLight);
		renderArm(entity, left, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, poseStack, buffers, packedLight);
		poseStack.popPose();
	}

	private void renderArm(T entity, ItemStack stack, ItemDisplayContext context, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		if(stack.isEmpty())
			return;
		poseStack.pushPose();
		getParentModel().translateToHand(arm, poseStack);
		if(!getParentModel().young) {
			poseStack.translate(-0.08F, 0.55F, -0.16F);
			poseStack.mulPose(Axis.XP.rotationDegrees(-45.0F));
		} else {
			poseStack.translate(-0.05F, 0.65F, 0.0F);
			poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F));
		}
		if(entity.isCrouching())
			poseStack.translate(0.0F, 0.2F, 0.0F);
		poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		boolean leftArm = arm == HumanoidArm.LEFT;
		poseStack.translate(leftArm ? -0.0625F : 0.0625F, 0.125F, -0.625F);
		itemInHandRenderer.renderItem(entity, stack, context, leftArm, poseStack, buffers, packedLight);
		poseStack.popPose();
	}
}
