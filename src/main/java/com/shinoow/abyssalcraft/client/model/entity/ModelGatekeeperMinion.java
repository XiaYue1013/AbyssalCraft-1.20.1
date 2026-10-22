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

public class ModelGatekeeperMinion<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart mask1;
	public final ModelPart maskpart1;
	public final ModelPart maskpart2;
	public final ModelPart mask2;
	public final ModelPart maskpart3;
	public final ModelPart maskpart4;
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart rightshoulder;
	public final ModelPart leftshoulder;
	public final ModelPart rightarm1;
	public final ModelPart rightarm2;
	public final ModelPart leftarm1;
	public final ModelPart leftarm2;
	public final ModelPart tentacle1;
	public final ModelPart tentacle2;
	public final ModelPart tentacle3;
	public final ModelPart tentacle4;
	public final ModelPart rltentacle1;
	public final ModelPart rltentacle2;
	public final ModelPart rltentacle3;
	public final ModelPart rltentacle4;
	public final ModelPart lltentacle1;
	public final ModelPart lltentacle2;
	public final ModelPart lltentacle3;
	public final ModelPart lltentacle4;
	public final ModelPart lowerbody;

	public ModelGatekeeperMinion(ModelPart root) {
		mask1 = root.getChild("head").getChild("mask1");
		maskpart1 = root.getChild("head").getChild("maskpart1");
		maskpart2 = root.getChild("head").getChild("maskpart2");
		mask2 = root.getChild("head").getChild("mask2");
		maskpart3 = root.getChild("head").getChild("maskpart3");
		maskpart4 = root.getChild("head").getChild("maskpart4");
		head = root.getChild("head");
		body = root.getChild("body");
		rightshoulder = root.getChild("body").getChild("rightshoulder");
		leftshoulder = root.getChild("body").getChild("leftshoulder");
		rightarm1 = root.getChild("rightarm1");
		rightarm2 = root.getChild("rightarm1").getChild("rightarm2");
		leftarm1 = root.getChild("leftarm1");
		leftarm2 = root.getChild("leftarm1").getChild("leftarm2");
		tentacle1 = root.getChild("leftarm1").getChild("leftarm2").getChild("tentacle1");
		tentacle2 = root.getChild("leftarm1").getChild("leftarm2").getChild("tentacle2");
		tentacle3 = root.getChild("leftarm1").getChild("leftarm2").getChild("tentacle3");
		tentacle4 = root.getChild("leftarm1").getChild("leftarm2").getChild("tentacle4");
		rltentacle1 = root.getChild("rltentacle1");
		rltentacle2 = root.getChild("rltentacle2");
		rltentacle3 = root.getChild("rltentacle3");
		rltentacle4 = root.getChild("rltentacle4");
		lltentacle1 = root.getChild("lltentacle1");
		lltentacle2 = root.getChild("lltentacle2");
		lltentacle3 = root.getChild("lltentacle3");
		lltentacle4 = root.getChild("lltentacle4");
		lowerbody = root.getChild("body").getChild("lowerbody");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F), PartPose.offset(0F, -12F, 0F));
		p_head.addOrReplaceChild("mask1", CubeListBuilder.create().texOffs(32, 0).addBox(-3.4F, -8F, -6F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.5235988F, 0F));
		p_head.addOrReplaceChild("maskpart1", CubeListBuilder.create().texOffs(26, 0).addBox(1.6F, -8F, -7F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.5235988F, 0F));
		p_head.addOrReplaceChild("maskpart2", CubeListBuilder.create().texOffs(26, 0).addBox(1.6F, -1F, -7F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.5235988F, 0F));
		p_head.addOrReplaceChild("mask2", CubeListBuilder.create().texOffs(32, 0).addBox(-2.6F, -8F, -6F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.5235988F, 0F));
		p_head.addOrReplaceChild("maskpart3", CubeListBuilder.create().texOffs(26, 0).addBox(-2.6F, -8F, -7F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.5235988F, 0F));
		p_head.addOrReplaceChild("maskpart4", CubeListBuilder.create().texOffs(26, 0).addBox(-2.6F, -1F, -7F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.5235988F, 0F));
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(0F, 0F, 0F, 8F, 22F, 6F), PartPose.offset(-4F, -12F, -3F));
		p_body.addOrReplaceChild("rightshoulder", CubeListBuilder.create().texOffs(44, 9).addBox(-7F, -2F, -2F, 7F, 4F, 6F), PartPose.offsetAndRotation(0.5F, 2F, 2F, 0F, 0F, -0.1745329F));
		p_body.addOrReplaceChild("leftshoulder", CubeListBuilder.create().texOffs(44, 9).addBox(0F, -2F, -2F, 7F, 4F, 6F), PartPose.offsetAndRotation(7.5F, 2F, 2F, 0F, 0F, 0.1745329F));
		p_body.addOrReplaceChild("lowerbody", CubeListBuilder.create().texOffs(8, 44).addBox(0F, 0F, 0F, 8F, 8F, 4F), PartPose.offset(0F, 22F, 1F));
		PartDefinition p_rightarm1 = root.addOrReplaceChild("rightarm1", CubeListBuilder.create().texOffs(0, 20).addBox(-4F, -2F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(-5F, -8F, 0F, -0.3839724F, 0F, 0F));
		p_rightarm1.addOrReplaceChild("rightarm2", CubeListBuilder.create().texOffs(0, 26).addBox(-4F, 5.5F, -1F, 4F, 10F, 4F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.174533F, 0F, 0F));
		PartDefinition p_leftarm1 = root.addOrReplaceChild("leftarm1", CubeListBuilder.create().texOffs(0, 20).addBox(0F, -2F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(5F, -8F, 0F, -0.3839724F, 0F, 0F));
		PartDefinition p_leftarm2 = p_leftarm1.addOrReplaceChild("leftarm2", CubeListBuilder.create().texOffs(0, 26).addBox(0F, 5.5F, -1F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.174533F, 0F, 0F));
		p_leftarm2.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 9F, 2F));
		p_leftarm2.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 9F, 2F));
		p_leftarm2.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 9F, 0F));
		p_leftarm2.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 9F, 0F));
		root.addOrReplaceChild("rltentacle1", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(-3F, 18F, 1F));
		root.addOrReplaceChild("rltentacle2", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(-3F, 18F, -1F));
		root.addOrReplaceChild("rltentacle3", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(-1F, 18F, 1F));
		root.addOrReplaceChild("rltentacle4", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(-1F, 18F, -1F));
		root.addOrReplaceChild("lltentacle1", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 18F, 1F));
		root.addOrReplaceChild("lltentacle2", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 18F, -1F));
		root.addOrReplaceChild("lltentacle3", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 18F, 1F));
		root.addOrReplaceChild("lltentacle4", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 18F, -1F));
		return LayerDefinition.create(mesh, 128, 64);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		rightarm1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftarm1.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightarm1.zRot = 0.0F;
		leftarm1.zRot = 0.0F;
		float f16 = 0.03F * (entity.getId() % 10);
		tentacle1.xRot = Mth.cos(entity.tickCount * f16) * 10.5F * (float)Math.PI / 180.0F;
		tentacle1.yRot = 0.0F;
		tentacle1.zRot = Mth.sin(entity.tickCount * f16) * 6.5F * (float)Math.PI / 180.0F;
		float f17 = 0.03F * (entity.getId() % 10);
		tentacle2.xRot = Mth.sin(entity.tickCount * f17) * 10.5F * (float)Math.PI / 180.0F;
		tentacle2.yRot = 0.0F;
		tentacle2.zRot = Mth.cos(entity.tickCount * f17) * 6.5F * (float)Math.PI / 180.0F;
		float f18 = 0.03F * (entity.getId() % 10);
		tentacle3.xRot = Mth.sin(entity.tickCount * f18) * 10.5F * (float)Math.PI / 180.0F;
		tentacle3.yRot = 0.0F;
		tentacle3.zRot = Mth.cos(entity.tickCount * f18) * 6.5F * (float)Math.PI / 180.0F;
		float f19 = 0.03F * (entity.getId() % 10);
		tentacle4.xRot = Mth.cos(entity.tickCount * f19) * 10.5F * (float)Math.PI / 180.0F;
		tentacle4.yRot = 0.0F;
		tentacle4.zRot = Mth.sin(entity.tickCount * f19) * 6.5F * (float)Math.PI / 180.0F;
		rltentacle1.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rltentacle1.zRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rltentacle1.yRot = 0.0F;
		rltentacle2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rltentacle2.zRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rltentacle2.yRot = 0.0F;
		rltentacle3.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rltentacle3.zRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rltentacle3.yRot = 0.0F;
		rltentacle4.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rltentacle4.zRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rltentacle4.yRot = 0.0F;
		lltentacle1.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		lltentacle1.zRot = -Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		lltentacle1.yRot = 0.0F;
		lltentacle2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		lltentacle2.zRot = -Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		lltentacle2.yRot = 0.0F;
		lltentacle3.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		lltentacle3.zRot = -Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		lltentacle3.yRot = 0.0F;
		lltentacle4.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		lltentacle4.zRot = -Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		lltentacle4.yRot = 0.0F;
		rightarm1.yRot = 0.0F;
		leftarm1.yRot = 0.0F;
		float f6;
		float f7;
		if (attackTime > -9990.0F)
		{
		f6 = attackTime;
		body.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightarm1.yRot += body.yRot;
		leftarm1.yRot += body.yRot;
		f6 = 1.0F - attackTime;
		f6 *= f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		f7 = Mth.sin(f6 * (float)Math.PI);
		float f8 = Mth.sin(attackTime * (float)Math.PI) * -(head.xRot - 0.7F) * 0.75F;
		rightarm1.xRot = (float)(rightarm1.xRot - (f7 * 1.2D + f8));
		rightarm1.yRot += body.yRot * 2.0F;
		rightarm1.zRot = Mth.sin(attackTime * (float)Math.PI) * -0.4F;
		leftarm1.xRot = (float)(leftarm1.xRot - (f7 * 1.2D + f8));
		leftarm1.yRot += body.yRot * -2.0F;
		leftarm1.zRot = Mth.sin(attackTime * (float)Math.PI) * 0.4F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rltentacle1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rltentacle2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rltentacle3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rltentacle4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		lltentacle1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		lltentacle2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		lltentacle3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		lltentacle4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}
