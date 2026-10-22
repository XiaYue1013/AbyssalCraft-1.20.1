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

import com.shinoow.abyssalcraft.common.entity.EntitySacthoth;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ModelSacthoth extends EntityModel<EntitySacthoth> {

	public final ModelPart head;
	public final ModelPart leftshoulder;
	public final ModelPart leftarm1;
	public final ModelPart leftarm2;
	public final ModelPart rightshoulder;
	public final ModelPart rightarm1;
	public final ModelPart rightarm2;
	public final ModelPart body;
	public final ModelPart core;
	public final ModelPart leftleg;
	public final ModelPart rightleg;
	public final ModelPart spikerow11;
	public final ModelPart spikerow12;
	public final ModelPart spikerow13;
	public final ModelPart spikerow14;
	public final ModelPart spikerow15;
	public final ModelPart spikerow16;
	public final ModelPart spikerow17;
	public final ModelPart spikerow18;
	public final ModelPart spikerow19;
	public final ModelPart spikerow110;
	public final ModelPart spikerow111;
	public final ModelPart spikerow112;
	public final ModelPart spikerow113;
	public final ModelPart spikerow114;
	public final ModelPart spikerow115;
	public final ModelPart spikerow116;
	public final ModelPart spikerow21;
	public final ModelPart spikerow22;
	public final ModelPart spikerow23;
	public final ModelPart spikerow24;
	public final ModelPart spikerow25;
	public final ModelPart spikerow26;
	public final ModelPart spikerow27;
	public final ModelPart spikerow28;
	public final ModelPart spikerow29;
	public final ModelPart spikerow210;
	public final ModelPart spikerow211;
	public final ModelPart spikerow212;
	public final ModelPart spikerow31;
	public final ModelPart spikerow32;
	public final ModelPart spikerow33;
	public final ModelPart spikerow34;
	public final ModelPart spikerow35;
	public final ModelPart spikerow36;
	public final ModelPart spikerow37;
	public final ModelPart spikerow38;
	public final ModelPart spikerow41;
	public final ModelPart spikerow42;
	public final ModelPart spikerow43;
	public final ModelPart spikerow44;
	public final ModelPart tbase1;
	public final ModelPart tbase2;
	public final ModelPart tbase3;
	public final ModelPart tbase4;
	public final ModelPart tbase5;
	public final ModelPart tentacle1;
	public final ModelPart tentacle2;
	public final ModelPart tentacle3;
	public final ModelPart tentacle4;
	public final ModelPart tentacle5;

	public ModelSacthoth(ModelPart root) {
		head = root.getChild("head");
		leftshoulder = root.getChild("leftshoulder");
		leftarm1 = root.getChild("leftarm1");
		leftarm2 = root.getChild("leftarm1").getChild("leftarm2");
		rightshoulder = root.getChild("rightshoulder");
		rightarm1 = root.getChild("rightarm1");
		rightarm2 = root.getChild("rightarm1").getChild("rightarm2");
		body = root.getChild("body");
		core = root.getChild("core");
		leftleg = root.getChild("leftleg");
		rightleg = root.getChild("rightleg");
		spikerow11 = root.getChild("head").getChild("spikerow11");
		spikerow12 = root.getChild("head").getChild("spikerow12");
		spikerow13 = root.getChild("head").getChild("spikerow13");
		spikerow14 = root.getChild("head").getChild("spikerow14");
		spikerow15 = root.getChild("head").getChild("spikerow15");
		spikerow16 = root.getChild("head").getChild("spikerow16");
		spikerow17 = root.getChild("head").getChild("spikerow17");
		spikerow18 = root.getChild("head").getChild("spikerow18");
		spikerow19 = root.getChild("head").getChild("spikerow19");
		spikerow110 = root.getChild("head").getChild("spikerow110");
		spikerow111 = root.getChild("head").getChild("spikerow111");
		spikerow112 = root.getChild("head").getChild("spikerow112");
		spikerow113 = root.getChild("head").getChild("spikerow113");
		spikerow114 = root.getChild("head").getChild("spikerow114");
		spikerow115 = root.getChild("head").getChild("spikerow115");
		spikerow116 = root.getChild("head").getChild("spikerow116");
		spikerow21 = root.getChild("head").getChild("spikerow21");
		spikerow22 = root.getChild("head").getChild("spikerow22");
		spikerow23 = root.getChild("head").getChild("spikerow23");
		spikerow24 = root.getChild("head").getChild("spikerow24");
		spikerow25 = root.getChild("head").getChild("spikerow25");
		spikerow26 = root.getChild("head").getChild("spikerow26");
		spikerow27 = root.getChild("head").getChild("spikerow27");
		spikerow28 = root.getChild("head").getChild("spikerow28");
		spikerow29 = root.getChild("head").getChild("spikerow29");
		spikerow210 = root.getChild("head").getChild("spikerow210");
		spikerow211 = root.getChild("head").getChild("spikerow211");
		spikerow212 = root.getChild("head").getChild("spikerow212");
		spikerow31 = root.getChild("head").getChild("spikerow31");
		spikerow32 = root.getChild("head").getChild("spikerow32");
		spikerow33 = root.getChild("head").getChild("spikerow33");
		spikerow34 = root.getChild("head").getChild("spikerow34");
		spikerow35 = root.getChild("head").getChild("spikerow35");
		spikerow36 = root.getChild("head").getChild("spikerow36");
		spikerow37 = root.getChild("head").getChild("spikerow37");
		spikerow38 = root.getChild("head").getChild("spikerow38");
		spikerow41 = root.getChild("head").getChild("spikerow41");
		spikerow42 = root.getChild("head").getChild("spikerow42");
		spikerow43 = root.getChild("head").getChild("spikerow43");
		spikerow44 = root.getChild("head").getChild("spikerow44");
		tbase1 = root.getChild("head").getChild("tbase1");
		tbase2 = root.getChild("head").getChild("tbase2");
		tbase3 = root.getChild("head").getChild("tbase3");
		tbase4 = root.getChild("head").getChild("tbase4");
		tbase5 = root.getChild("head").getChild("tbase5");
		tentacle1 = root.getChild("head").getChild("tbase1").getChild("tentacle1");
		tentacle2 = root.getChild("head").getChild("tbase2").getChild("tentacle2");
		tentacle3 = root.getChild("head").getChild("tbase3").getChild("tentacle3");
		tentacle4 = root.getChild("head").getChild("tbase4").getChild("tentacle4");
		tentacle5 = root.getChild("head").getChild("tbase5").getChild("tentacle5");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -10F, -4.5F, 9F, 10F, 9F), PartPose.offset(0F, -22F, 0F));
		p_head.addOrReplaceChild("spikerow11", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, -12F, -4.5F));
		p_head.addOrReplaceChild("spikerow12", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-2.5F, -12F, -4.5F));
		p_head.addOrReplaceChild("spikerow13", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-0.5F, -12F, -4.5F));
		p_head.addOrReplaceChild("spikerow14", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(1.5F, -12F, -4.5F));
		p_head.addOrReplaceChild("spikerow15", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, -12F, -4.5F));
		p_head.addOrReplaceChild("spikerow16", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, -12F, -2.5F));
		p_head.addOrReplaceChild("spikerow17", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, -12F, -0.5F));
		p_head.addOrReplaceChild("spikerow18", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, -12F, 1.5F));
		p_head.addOrReplaceChild("spikerow19", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(3.5F, -12F, 3.5F));
		p_head.addOrReplaceChild("spikerow110", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(1.5F, -12F, 3.5F));
		p_head.addOrReplaceChild("spikerow111", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-0.5F, -12F, 3.5F));
		p_head.addOrReplaceChild("spikerow112", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-2.5F, -12F, 3.5F));
		p_head.addOrReplaceChild("spikerow113", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, -12F, 3.5F));
		p_head.addOrReplaceChild("spikerow114", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, -12F, 1.5F));
		p_head.addOrReplaceChild("spikerow115", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, -12F, -0.5F));
		p_head.addOrReplaceChild("spikerow116", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offset(-4.5F, -12F, -2.5F));
		p_head.addOrReplaceChild("spikerow21", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-3.5F, -13F, -3.5F));
		p_head.addOrReplaceChild("spikerow22", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-1.5F, -13F, -3.5F));
		p_head.addOrReplaceChild("spikerow23", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(0.5F, -13F, -3.5F));
		p_head.addOrReplaceChild("spikerow24", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(2.5F, -13F, -3.5F));
		p_head.addOrReplaceChild("spikerow25", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(2.5F, -13F, -1.5F));
		p_head.addOrReplaceChild("spikerow26", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(2.5F, -13F, 0.5F));
		p_head.addOrReplaceChild("spikerow27", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(2.5F, -13F, 2.5F));
		p_head.addOrReplaceChild("spikerow28", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(0.5F, -13F, 2.5F));
		p_head.addOrReplaceChild("spikerow29", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-1.5F, -13F, 2.5F));
		p_head.addOrReplaceChild("spikerow210", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-3.5F, -13F, 2.5F));
		p_head.addOrReplaceChild("spikerow211", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-3.5F, -13F, 0.5F));
		p_head.addOrReplaceChild("spikerow212", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-3.5F, -13F, -1.5F));
		p_head.addOrReplaceChild("spikerow31", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-2.5F, -14F, -2.5F));
		p_head.addOrReplaceChild("spikerow32", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-0.5F, -14F, -2.5F));
		p_head.addOrReplaceChild("spikerow33", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(1.5F, -14F, -2.5F));
		p_head.addOrReplaceChild("spikerow34", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(1.5F, -14F, -0.5F));
		p_head.addOrReplaceChild("spikerow35", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(1.5F, -14F, 1.5F));
		p_head.addOrReplaceChild("spikerow36", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-0.5F, -14F, 1.5F));
		p_head.addOrReplaceChild("spikerow37", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-2.5F, -14F, 1.5F));
		p_head.addOrReplaceChild("spikerow38", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-2.5F, -14F, -0.5F));
		p_head.addOrReplaceChild("spikerow41", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(-1.5F, -15F, -1.5F));
		p_head.addOrReplaceChild("spikerow42", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0.5F, -15F, -1.5F));
		p_head.addOrReplaceChild("spikerow43", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(-1.5F, -15F, 0.5F));
		p_head.addOrReplaceChild("spikerow44", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offset(0.5F, -15F, 0.5F));
		PartDefinition p_tbase1 = p_head.addOrReplaceChild("tbase1", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-4.5F, -1F, -5.5F));
		p_tbase1.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0F, 0F, -1F));
		PartDefinition p_tbase2 = p_head.addOrReplaceChild("tbase2", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-2.5F, -1F, -5.5F));
		p_tbase2.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0F, 0F, -1F));
		PartDefinition p_tbase3 = p_head.addOrReplaceChild("tbase3", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-0.5F, -1F, -5.5F));
		p_tbase3.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0F, 0F, -1F));
		PartDefinition p_tbase4 = p_head.addOrReplaceChild("tbase4", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(1.5F, -1F, -5.5F));
		p_tbase4.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0F, 0F, -1F));
		PartDefinition p_tbase5 = p_head.addOrReplaceChild("tbase5", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(3.5F, -1F, -5.5F));
		p_tbase5.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offset(0F, 0F, -1F));
		root.addOrReplaceChild("leftshoulder", CubeListBuilder.create().texOffs(72, 0).addBox(0F, 0F, 0F, 8F, 5F, 7F), PartPose.offsetAndRotation(5.5F, -22F, -3.5F, 0F, 0F, 0.185895F));
		PartDefinition p_leftarm1 = root.addOrReplaceChild("leftarm1", CubeListBuilder.create().texOffs(72, 12).addBox(-5F, -1F, -2.5F, 5F, 12F, 5F), PartPose.offset(-6.5F, -19F, 0F));
		p_leftarm1.addOrReplaceChild("leftarm2", CubeListBuilder.create().texOffs(72, 12).addBox(-5F, 8F, 3F, 5F, 12F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.5576792F, 0F, 0F));
		root.addOrReplaceChild("rightshoulder", CubeListBuilder.create().texOffs(72, 0).addBox(-8F, 0F, 0F, 8F, 5F, 7F), PartPose.offsetAndRotation(-5.5F, -22F, -3.5F, 0F, 0F, -0.1858931F));
		PartDefinition p_rightarm1 = root.addOrReplaceChild("rightarm1", CubeListBuilder.create().texOffs(72, 12).addBox(-0.5F, -1F, -2.5F, 5F, 12F, 5F), PartPose.offset(7F, -19F, 0F));
		p_rightarm1.addOrReplaceChild("rightarm2", CubeListBuilder.create().texOffs(72, 12).addBox(-0.5F, 8F, 3F, 5F, 12F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.5576851F, 0F, 0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(36, 0).addBox(0F, 0F, 0F, 11F, 24F, 7F), PartPose.offset(-5.5F, -22F, -3.5F));
		root.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 20).addBox(0F, 0F, 0F, 5F, 5F, 1F), PartPose.offset(-2.5F, -15F, -4.5F));
		root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(102, 0).addBox(-2.5F, 0F, -3.5F, 5F, 22F, 7F), PartPose.offset(-3F, 2F, 0F));
		root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(102, 0).addBox(-2.5F, 0F, -3.5F, 5F, 22F, 7F), PartPose.offset(3F, 2F, 0F));
		return LayerDefinition.create(mesh, 128, 64);
	}


	@Override
	public void setupAnim(EntitySacthoth entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		float f6 = 0.01F * (entity.getId() % 10);
		tentacle1.xRot = Mth.sin(entity.tickCount * f6) * 4.5F * (float)Math.PI / 180.0F;
		tentacle1.yRot = 0.0F;
		tentacle1.zRot = Mth.cos(entity.tickCount * f6) * 2.5F * (float)Math.PI / 180.0F;
		float f7 = 0.02F * (entity.getId() % 10);
		tentacle2.xRot = Mth.sin(entity.tickCount * f7) * 4.5F * (float)Math.PI / 180.0F;
		tentacle2.yRot = 0.0F;
		tentacle2.zRot = Mth.cos(entity.tickCount * f7) * 2.5F * (float)Math.PI / 180.0F;
		float f8 = 0.03F * (entity.getId() % 10);
		tentacle3.xRot = Mth.sin(entity.tickCount * f8) * 4.5F * (float)Math.PI / 180.0F;
		tentacle3.yRot = 0.0F;
		tentacle3.zRot = Mth.cos(entity.tickCount * f8) * 2.5F * (float)Math.PI / 180.0F;
		float f9 = 0.04F * (entity.getId() % 10);
		tentacle4.xRot = Mth.sin(entity.tickCount * f9) * 4.5F * (float)Math.PI / 180.0F;
		tentacle4.yRot = 0.0F;
		tentacle4.zRot = Mth.cos(entity.tickCount * f9) * 2.5F * (float)Math.PI / 180.0F;
		float f10 = 0.04F * (entity.getId() % 10);
		tentacle5.xRot = Mth.sin(entity.tickCount * f10) * 4.5F * (float)Math.PI / 180.0F;
		tentacle5.yRot = 0.0F;
		tentacle5.zRot = Mth.cos(entity.tickCount * f10) * 2.5F * (float)Math.PI / 180.0F;
		rightarm1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftarm1.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightarm1.zRot = 0.0F;
		leftarm1.zRot = 0.0F;
		rightleg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rightleg.yRot = 0.0F;
		leftleg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		leftleg.yRot = 0.0F;
		leftarm1.xRot = leftarm1.xRot * 0.5F - (float)Math.PI / 10F;
		leftarm1.yRot = 0.0F;
		rightarm1.yRot = 0.0F;
		body.yRot = 0.0F;
		if (riding)
		{
		rightarm1.xRot += -((float)Math.PI / 5F);
		leftarm1.xRot += -((float)Math.PI / 5F);
		leftleg.xRot = -((float)Math.PI * 2F / 5F);
		rightleg.xRot = -((float)Math.PI * 2F / 5F);
		leftleg.yRot = (float)Math.PI / 10F;
		rightleg.yRot = -((float)Math.PI / 10F);
		}
		if (attackTime > 0.0F)
		{
		float f11 = attackTime;
		body.yRot = Mth.sin(Mth.sqrt(limbSwingAmount) * ((float)Math.PI * 2F)) * 0.2F;
		rightarm1.yRot += body.yRot;
		leftarm1.yRot += body.yRot;
		leftarm1.xRot += body.yRot;
		f11 = 1.0F - attackTime;
		f11 = f11 * f11;
		f11 = f11 * f11;
		f11 = 1.0F - f11;
		float f12 = Mth.sin(f11 * (float)Math.PI);
		float f13 = Mth.sin(attackTime * (float)Math.PI) * -(head.xRot - 0.7F) * 0.75F;
		leftarm1.xRot = (float)(leftarm1.xRot - (f12 * 1.2D + f13));
		leftarm1.yRot += body.yRot * 2.0F;
		leftarm1.zRot += Mth.sin(attackTime * (float)Math.PI) * -0.4F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftshoulder.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightshoulder.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		core.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
