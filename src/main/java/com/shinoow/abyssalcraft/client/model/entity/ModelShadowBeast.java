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
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelShadowBeast<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart head;
	public final ModelPart btooth1;
	public final ModelPart btooth2;
	public final ModelPart btooth3;
	public final ModelPart btooth4;
	public final ModelPart btooth5;
	public final ModelPart jaw;
	public final ModelPart stooth1;
	public final ModelPart stooth2;
	public final ModelPart stooth3;
	public final ModelPart stooth4;
	public final ModelPart spine;
	public final ModelPart leftshoulder;
	public final ModelPart lsspike;
	public final ModelPart larm1;
	public final ModelPart larm2;
	public final ModelPart laspike1;
	public final ModelPart laspike2;
	public final ModelPart lfinger1;
	public final ModelPart lfinger3;
	public final ModelPart lfinger2;
	public final ModelPart lfinger4;
	public final ModelPart rightshoulder;
	public final ModelPart rsspike;
	public final ModelPart rarm1;
	public final ModelPart rarm2;
	public final ModelPart raspike1;
	public final ModelPart raspike2;
	public final ModelPart rfinger1;
	public final ModelPart rfinger2;
	public final ModelPart rfinger3;
	public final ModelPart rfinger4;
	public final ModelPart pelvis;
	public final ModelPart leftleg;
	public final ModelPart rightleg;

	public ModelShadowBeast(ModelPart root) {
		head = root.getChild("head");
		btooth1 = root.getChild("head").getChild("btooth1");
		btooth2 = root.getChild("head").getChild("btooth2");
		btooth3 = root.getChild("head").getChild("btooth3");
		btooth4 = root.getChild("head").getChild("btooth4");
		btooth5 = root.getChild("head").getChild("btooth5");
		jaw = root.getChild("head").getChild("jaw");
		stooth1 = root.getChild("head").getChild("jaw").getChild("stooth1");
		stooth2 = root.getChild("head").getChild("jaw").getChild("stooth2");
		stooth3 = root.getChild("head").getChild("jaw").getChild("stooth3");
		stooth4 = root.getChild("head").getChild("jaw").getChild("stooth4");
		spine = root.getChild("spine");
		leftshoulder = root.getChild("leftshoulder");
		lsspike = root.getChild("lsspike");
		larm1 = root.getChild("larm1");
		larm2 = root.getChild("larm1").getChild("larm2");
		laspike1 = root.getChild("larm1").getChild("laspike1");
		laspike2 = root.getChild("larm1").getChild("laspike2");
		lfinger1 = root.getChild("larm1").getChild("lfinger1");
		lfinger3 = root.getChild("larm1").getChild("lfinger3");
		lfinger2 = root.getChild("larm1").getChild("lfinger2");
		lfinger4 = root.getChild("larm1").getChild("lfinger4");
		rightshoulder = root.getChild("rightshoulder");
		rsspike = root.getChild("rsspike");
		rarm1 = root.getChild("rarm1");
		rarm2 = root.getChild("rarm1").getChild("rarm2");
		raspike1 = root.getChild("rarm1").getChild("raspike1");
		raspike2 = root.getChild("rarm1").getChild("raspike2");
		rfinger1 = root.getChild("rarm1").getChild("rfinger1");
		rfinger2 = root.getChild("rarm1").getChild("rfinger2");
		rfinger3 = root.getChild("rarm1").getChild("rfinger3");
		rfinger4 = root.getChild("rarm1").getChild("rfinger4");
		pelvis = root.getChild("pelvis");
		leftleg = root.getChild("leftleg");
		rightleg = root.getChild("rightleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9F, -4.5F, 9F, 9F, 9F), PartPose.offset(0F, -13F, -1F));
		p_head.addOrReplaceChild("btooth1", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, 0F, -4.5F));
		p_head.addOrReplaceChild("btooth2", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-2.5F, 0F, -4.5F));
		p_head.addOrReplaceChild("btooth3", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-0.5F, 0F, -4.5F));
		p_head.addOrReplaceChild("btooth4", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(1.5F, 0F, -4.5F));
		p_head.addOrReplaceChild("btooth5", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, 0F, -4.5F));
		PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(36, 0).addBox(-4.5F, 1.9F, -5F, 9F, 1F, 9F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.4461433F, 0F, 0F));
		p_jaw.addOrReplaceChild("stooth1", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-3.5F, 0.9F, -5F));
		p_jaw.addOrReplaceChild("stooth2", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-1.5F, 0.9F, -5F));
		p_jaw.addOrReplaceChild("stooth3", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(0.5F, 0.9F, -5F));
		p_jaw.addOrReplaceChild("stooth4", CubeListBuilder.create().texOffs(37, 4).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(2.5F, 0.9F, -5F));
		root.addOrReplaceChild("spine", CubeListBuilder.create().texOffs(72, 0).addBox(-2.5F, 0F, -2.5F, 5F, 20F, 5F), PartPose.offsetAndRotation(0F, -12F, -1F, 0.2974289F, 0F, 0F));
		root.addOrReplaceChild("leftshoulder", CubeListBuilder.create().texOffs(41, 11).addBox(-8F, 0F, 0F, 8F, 5F, 7F), PartPose.offsetAndRotation(9.5F, -8F, -3F, 0F, 0F, 0.111544F));
		root.addOrReplaceChild("lsspike", CubeListBuilder.create().texOffs(0, 0).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(7F, -9F, 0F, 0F, 0F, 0.111544F));
		PartDefinition p_larm1 = root.addOrReplaceChild("larm1", CubeListBuilder.create().texOffs(0, 19).addBox(0F, -1F, -1.5F, 3F, 11F, 3F), PartPose.offset(5F, -6.5F, 0.5F));
		p_larm1.addOrReplaceChild("larm2", CubeListBuilder.create().texOffs(0, 19).addBox(0F, 6F, 5F, 3F, 7F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807508F, 0F, 0F));
		p_larm1.addOrReplaceChild("laspike1", CubeListBuilder.create().texOffs(0, 0).addBox(6F, -1F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.8179311F));
		p_larm1.addOrReplaceChild("laspike2", CubeListBuilder.create().texOffs(34, 0).addBox(-1F, 10F, 5.5F, 4F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7744724F, -0.2617994F, -0.2617994F));
		p_larm1.addOrReplaceChild("lfinger1", CubeListBuilder.create().texOffs(30, 0).addBox(3F, 11F, 6F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_larm1.addOrReplaceChild("lfinger3", CubeListBuilder.create().texOffs(30, 0).addBox(1F, 11F, 4F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_larm1.addOrReplaceChild("lfinger2", CubeListBuilder.create().texOffs(30, 0).addBox(-1F, 11F, 6F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_larm1.addOrReplaceChild("lfinger4", CubeListBuilder.create().texOffs(30, 0).addBox(1F, 11F, 8F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		root.addOrReplaceChild("rightshoulder", CubeListBuilder.create().texOffs(41, 11).addBox(0F, 0F, 0F, 8F, 5F, 7F), PartPose.offsetAndRotation(-9.5F, -8F, -3F, 0F, 0F, -0.1115358F));
		root.addOrReplaceChild("rsspike", CubeListBuilder.create().texOffs(0, 0).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(-8F, -9F, 0F, 0F, 0F, -0.111544F));
		PartDefinition p_rarm1 = root.addOrReplaceChild("rarm1", CubeListBuilder.create().texOffs(0, 19).addBox(-3F, -1F, -1.5F, 3F, 11F, 3F), PartPose.offset(-5F, -6.5F, 0.5F));
		p_rarm1.addOrReplaceChild("rarm2", CubeListBuilder.create().texOffs(0, 19).addBox(-3F, 6F, 5F, 3F, 7F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807508F, 0F, 0F));
		p_rarm1.addOrReplaceChild("raspike1", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -1F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.8179294F));
		p_rarm1.addOrReplaceChild("raspike2", CubeListBuilder.create().texOffs(34, 0).addBox(-3F, 10F, 5.5F, 4F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0.2617994F, 0.2617994F));
		p_rarm1.addOrReplaceChild("rfinger1", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 11F, 6F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_rarm1.addOrReplaceChild("rfinger2", CubeListBuilder.create().texOffs(30, 0).addBox(-4F, 11F, 6F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_rarm1.addOrReplaceChild("rfinger3", CubeListBuilder.create().texOffs(30, 0).addBox(-2F, 11F, 4F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		p_rarm1.addOrReplaceChild("rfinger4", CubeListBuilder.create().texOffs(30, 0).addBox(-2F, 11F, 8F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.7807556F, 0F, 0F));
		root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(37, 24).addBox(0F, 0F, -1F, 12F, 5F, 5F), PartPose.offsetAndRotation(-6F, 7F, 3F, 0.2974216F, 0F, 0F));
		root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(16, 18).addBox(-2.5F, 0F, -2.5F, 5F, 8F, 5F), PartPose.offsetAndRotation(3.5F, 11F, 6F, 0.2974216F, 0F, 0F));
		root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(16, 18).addBox(-2.5F, 0F, -2.5F, 5F, 8F, 5F), PartPose.offsetAndRotation(-3.5F, 11F, 6F, 0.2974216F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 64);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		rightleg.xRot = Mth.cos(limbSwing * 0.3331F) * 0.07F * limbSwingAmount;
		rightleg.yRot = 0.0F;
		leftleg.xRot = Mth.cos(limbSwing * 0.3331F + (float)Math.PI) * 0.07F * limbSwingAmount;
		leftleg.yRot = 0.0F;
		float f6 = Mth.sin(attackTime * (float)Math.PI);
		float f7 = Mth.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float)Math.PI);
		rarm1.zRot = 0.0F;
		larm1.zRot = 0.0F;
		rarm1.yRot = -(0.1F - f6 * 0.6F);
		larm1.yRot = 0.1F - f6 * 0.6F;
		rarm1.xRot = -((float)Math.PI / 7.2F);
		larm1.xRot = -((float)Math.PI / 7.2F);
		rarm1.xRot -= f6 * 1.2F - f7 * 0.4F;
		larm1.xRot -= f6 * 1.2F - f7 * 0.4F;
		rarm1.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		larm1.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rarm1.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
		larm1.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		spine.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftshoulder.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		lsspike.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		larm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightshoulder.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rsspike.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		pelvis.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}
