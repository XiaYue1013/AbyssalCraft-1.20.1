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
 *     1.20.1 port - part tree converted by tools/port_model.py
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelACDragon<T extends com.shinoow.abyssalcraft.common.entity.EntityACDragon> extends EntityModel<T> {

	public final ModelPart head;
	public final ModelPart jaw;
	public final ModelPart neck;
	public final ModelPart body;
	public final ModelPart wing;
	public final ModelPart wingTip;
	public final ModelPart frontLeg;
	public final ModelPart frontLegTip;
	public final ModelPart frontFoot;
	public final ModelPart rearLeg;
	public final ModelPart rearLegTip;
	public final ModelPart rearFoot;

	public ModelACDragon(ModelPart root) {
		head = root.getChild("head");
		jaw = root.getChild("head").getChild("jaw");
		neck = root.getChild("neck");
		body = root.getChild("body");
		wing = root.getChild("wing");
		wingTip = root.getChild("wing").getChild("wingtip");
		frontLeg = root.getChild("frontleg");
		frontLegTip = root.getChild("frontleg").getChild("frontlegtip");
		frontFoot = root.getChild("frontleg").getChild("frontlegtip").getChild("frontfoot");
		rearLeg = root.getChild("rearleg");
		rearLegTip = root.getChild("rearleg").getChild("rearlegtip");
		rearFoot = root.getChild("rearleg").getChild("rearlegtip").getChild("rearfoot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(176, 44).addBox(-6F, -1F, -24F, 12F, 5F, 16F).texOffs(112, 30).addBox(-8F, -8F, -10F, 16F, 16F, 16F).texOffs(0, 0).mirror(true).addBox(-5F, -12F, -4F, 2F, 4F, 6F).mirror(false).texOffs(112, 0).mirror(true).addBox(-5F, -3F, -22F, 2F, 2F, 4F).mirror(false).texOffs(0, 0).addBox(3F, -12F, -4F, 2F, 4F, 6F).texOffs(112, 0).addBox(3F, -3F, -22F, 2F, 2F, 4F), PartPose.offset(0F, 0F, 0F));
		p_head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(176, 65).addBox(-6F, 0F, -16F, 12F, 4F, 16F), PartPose.offset(0F, 4F, -8F));
		root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(192, 104).addBox(-5F, -5F, -5F, 10F, 10F, 10F).texOffs(48, 0).addBox(-1F, -9F, -3F, 2F, 4F, 6F), PartPose.offset(0F, 0F, 0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, 0F, -16F, 24F, 24F, 64F).texOffs(220, 53).addBox(-1F, -6F, -10F, 2F, 6F, 12F).texOffs(220, 53).addBox(-1F, -6F, 10F, 2F, 6F, 12F).texOffs(220, 53).addBox(-1F, -6F, 30F, 2F, 6F, 12F), PartPose.offset(0F, 4F, 8F));
		PartDefinition p_wing = root.addOrReplaceChild("wing", CubeListBuilder.create().texOffs(112, 88).addBox(-56F, -4F, -4F, 56F, 8F, 8F).texOffs(-56, 88).addBox(-56F, 0F, 2F, 56F, 0F, 56F), PartPose.offset(-12F, 5F, 2F));
		p_wing.addOrReplaceChild("wingtip", CubeListBuilder.create().texOffs(112, 136).addBox(-56F, -2F, -2F, 56F, 4F, 4F).texOffs(-56, 144).addBox(-56F, 0F, 2F, 56F, 0F, 56F), PartPose.offset(-56F, 0F, 0F));
		PartDefinition p_frontLeg = root.addOrReplaceChild("frontleg", CubeListBuilder.create().texOffs(112, 104).addBox(-4F, -4F, -4F, 8F, 24F, 8F), PartPose.offset(-12F, 20F, 2F));
		PartDefinition p_frontLegTip = p_frontLeg.addOrReplaceChild("frontlegtip", CubeListBuilder.create().texOffs(226, 138).addBox(-3F, -1F, -3F, 6F, 24F, 6F), PartPose.offset(0F, 20F, -1F));
		p_frontLegTip.addOrReplaceChild("frontfoot", CubeListBuilder.create().texOffs(144, 104).addBox(-4F, 0F, -12F, 8F, 4F, 16F), PartPose.offset(0F, 23F, 0F));
		PartDefinition p_rearLeg = root.addOrReplaceChild("rearleg", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -4F, -8F, 16F, 32F, 16F), PartPose.offset(-16F, 16F, 42F));
		PartDefinition p_rearLegTip = p_rearLeg.addOrReplaceChild("rearlegtip", CubeListBuilder.create().texOffs(196, 0).addBox(-6F, -2F, 0F, 12F, 32F, 12F), PartPose.offset(0F, 32F, -4F));
		p_rearLegTip.addOrReplaceChild("rearfoot", CubeListBuilder.create().texOffs(112, 0).addBox(-9F, 0F, -20F, 18F, 6F, 24F), PartPose.offset(0F, 31F, 4F));
		return LayerDefinition.create(mesh, 256, 256);
	}


	private T entity;
	private float partialTicks;
	@Override public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.entity = entity; partialTicks = partialTick;
	}
	@Override public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) { this.entity = entity; }
	@Override public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if(entity == null) return;
		if(!entity.isAsorah()) alpha *= 0.5F;
		poseStack.pushPose();
		T entitydragon = entity;
		float f6 = entitydragon.prevAnimTime + (entitydragon.animTime - entitydragon.prevAnimTime) * partialTicks;
		jaw.xRot = (float)(Math.sin(f6 * (float)Math.PI * 2.0F) + 1.0D) * 0.2F;
		float f7 = (float)(Math.sin(f6 * (float)Math.PI * 2.0F - 1.0F) + 1.0D);
		f7 = (f7 * f7 * 1.0F + f7 * 2.0F) * 0.05F;
		poseStack.translate(0.0F, f7 - 2.0F, -3.0F);
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(f7 * 2.0F));
		float f8 = -30.0F;
		float f9 = 0.0F;
		float f10 = 1.5F;
		double[] adouble = entitydragon.getMovementOffsets(6, partialTicks);
		float f11 = updateRotations(entitydragon.getMovementOffsets(5, partialTicks)[0] - entitydragon.getMovementOffsets(10, partialTicks)[0]);
		float f12 = updateRotations(entitydragon.getMovementOffsets(5, partialTicks)[0] + f11 / 2.0F);
		f8 += 2.0F;
		float f13 = f6 * (float)Math.PI * 2.0F;
		f8 = 20.0F;
		float f14 = -12.0F;
		float f15;

		for (int i = 0; i < 5; ++i)
		{
			double[] adouble1 = entitydragon.getMovementOffsets(5 - i, partialTicks);
			f15 = (float)Math.cos(i * 0.45F + f13) * 0.15F;
			neck.yRot = updateRotations(adouble1[0] - adouble[0]) * (float)Math.PI / 180.0F * f10;
			neck.xRot = f15 + (float)(adouble1[1] - adouble[1]) * (float)Math.PI / 180.0F * f10 * 5.0F;
			neck.zRot = -updateRotations(adouble1[0] - f12) * (float)Math.PI / 180.0F * f10;
			neck.y = f8;
			neck.z = f14;
			neck.x = f9;
			f8 = (float)(f8 + Math.sin(neck.xRot) * 10.0D);
			f14 = (float)(f14 - Math.cos(neck.yRot) * Math.cos(neck.xRot) * 10.0D);
			f9 = (float)(f9 - Math.sin(neck.yRot) * Math.cos(neck.xRot) * 10.0D);
			neck.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}

		head.y = f8;
		head.z = f14;
		head.x = f9;
		double[] adouble2 = entitydragon.getMovementOffsets(0, partialTicks);
		head.yRot = updateRotations(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
		head.zRot = -updateRotations(adouble2[0] - f12) * (float)Math.PI / 180.0F * 1.0F;
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.pushPose();
		poseStack.translate(0.0F, 1.0F, 0.0F);
		poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-f11 * f10 * 1.0F));
		poseStack.translate(0.0F, -1.0F, 0.0F);
		body.zRot = 0.0F;
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		for (int j = 0; j < 2; ++j)
		{
			f15 = f6 * (float)Math.PI * 2.0F;
			wing.xRot = 0.125F - (float)Math.cos(f15) * 0.2F;
			wing.yRot = 0.25F;
			wing.zRot = (float)(Math.sin(f15) + 0.125D) * 0.8F;
			wingTip.zRot = -((float)(Math.sin(f15 + 2.0F) + 0.5D)) * 0.75F;
			rearLeg.xRot = 1.0F + f7 * 0.1F;
			rearLegTip.xRot = 0.5F + f7 * 0.1F;
			rearFoot.xRot = 0.75F + f7 * 0.1F;
			frontLeg.xRot = 1.3F + f7 * 0.1F;
			frontLegTip.xRot = -0.5F - f7 * 0.1F;
			frontFoot.xRot = 0.75F + f7 * 0.1F;
			wing.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			frontLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			rearLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.scale(-1.0F, 1.0F, 1.0F);
		}

		poseStack.popPose();
		
		float f16 = -((float)Math.sin(f6 * (float)Math.PI * 2.0F)) * 0.0F;
		f13 = f6 * (float)Math.PI * 2.0F;
		f8 = 10.0F;
		f14 = 60.0F;
		f9 = 0.0F;
		adouble = entitydragon.getMovementOffsets(11, partialTicks);

		for (int k = 0; k < 12; ++k)
		{
			adouble2 = entitydragon.getMovementOffsets(12 + k, partialTicks);
			f16 = (float)(f16 + Math.sin(k * 0.45F + f13) * 0.05000000074505806D);
			neck.yRot = (updateRotations(adouble2[0] - adouble[0]) * f10 + 180.0F) * (float)Math.PI / 180.0F;
			neck.xRot = f16 + (float)(adouble2[1] - adouble[1]) * (float)Math.PI / 180.0F * f10 * 5.0F;
			neck.zRot = updateRotations(adouble2[0] - f12) * (float)Math.PI / 180.0F * f10;
			neck.y = f8;
			neck.z = f14;
			neck.x = f9;
			f8 = (float)(f8 + Math.sin(neck.xRot) * 10.0D);
			f14 = (float)(f14 - Math.cos(neck.yRot) * Math.cos(neck.xRot) * 10.0D);
			f9 = (float)(f9 - Math.sin(neck.yRot) * Math.cos(neck.xRot) * 10.0D);
			neck.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}

		poseStack.popPose();
	}
	private float updateRotations(double angle) { return (float) Mth.wrapDegrees(angle); }
}
