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
package com.shinoow.abyssalcraft.client.model.block;

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

public class ModelJzaharSpawner<T extends Entity> extends EntityModel<T> {

	public final ModelPart head;
	public final ModelPart mask1;
	public final ModelPart mask2;
	public final ModelPart facetentacle1;
	public final ModelPart facetentacle2;
	public final ModelPart facetentacle3;
	public final ModelPart body1;
	public final ModelPart body2;
	public final ModelPart body3;
	public final ModelPart body4;
	public final ModelPart body5;
	public final ModelPart eye1;
	public final ModelPart eye2;
	public final ModelPart tentacle1;
	public final ModelPart tentacle12;
	public final ModelPart tentacle13;
	public final ModelPart foot1;
	public final ModelPart tentacle2;
	public final ModelPart tentacle22;
	public final ModelPart tentacle23;
	public final ModelPart foot2;
	public final ModelPart tentacle3;
	public final ModelPart tentacle32;
	public final ModelPart tentacle33;
	public final ModelPart foot3;
	public final ModelPart tentacle4;
	public final ModelPart tentacle42;
	public final ModelPart tentacle43;
	public final ModelPart foot4;
	public final ModelPart tentacle5;
	public final ModelPart tentacle52;
	public final ModelPart tentacle53;
	public final ModelPart tentacle54;
	public final ModelPart tentacle55;
	public final ModelPart arm;
	public final ModelPart Staff1;
	public final ModelPart Staff2;
	public final ModelPart Staff3;
	public final ModelPart Staff4;
	public final ModelPart Staff5;
	public final ModelPart Staff6;
	public final ModelPart Cube;
	public final ModelPart tentacle6;
	public final ModelPart tentacle62;
	public final ModelPart tentacle63;
	public final ModelPart foot5;
	public final ModelPart tentacle7;
	public final ModelPart tentacle72;
	public final ModelPart tentacle73;
	public final ModelPart tentacle74;
	public final ModelPart tentacle75;
	public final ModelPart tentacle76;
	public final ModelPart tentacle77;
	public final ModelPart abyssalnomicon;

	public ModelJzaharSpawner(ModelPart root) {
		head = root.getChild("head");
		mask1 = root.getChild("head").getChild("mask1");
		mask2 = root.getChild("head").getChild("mask2");
		facetentacle1 = root.getChild("head").getChild("facetentacle1");
		facetentacle2 = root.getChild("head").getChild("facetentacle2");
		facetentacle3 = root.getChild("head").getChild("facetentacle3");
		body1 = root.getChild("body1");
		body2 = root.getChild("body2");
		body3 = root.getChild("body3");
		body4 = root.getChild("body4");
		body5 = root.getChild("body5");
		eye1 = root.getChild("eye1");
		eye2 = root.getChild("eye2");
		tentacle1 = root.getChild("tentacle1");
		tentacle12 = root.getChild("tentacle12");
		tentacle13 = root.getChild("tentacle13");
		foot1 = root.getChild("foot1");
		tentacle2 = root.getChild("tentacle2");
		tentacle22 = root.getChild("tentacle22");
		tentacle23 = root.getChild("tentacle23");
		foot2 = root.getChild("foot2");
		tentacle3 = root.getChild("tentacle3");
		tentacle32 = root.getChild("tentacle32");
		tentacle33 = root.getChild("tentacle33");
		foot3 = root.getChild("foot3");
		tentacle4 = root.getChild("tentacle4");
		tentacle42 = root.getChild("tentacle42");
		tentacle43 = root.getChild("tentacle43");
		foot4 = root.getChild("foot4");
		tentacle5 = root.getChild("tentacle5");
		tentacle52 = root.getChild("tentacle52");
		tentacle53 = root.getChild("tentacle53");
		tentacle54 = root.getChild("tentacle54");
		tentacle55 = root.getChild("tentacle55");
		arm = root.getChild("arm");
		Staff1 = root.getChild("arm").getChild("staff1");
		Staff2 = root.getChild("arm").getChild("staff1").getChild("staff2");
		Staff3 = root.getChild("arm").getChild("staff1").getChild("staff3");
		Staff4 = root.getChild("arm").getChild("staff1").getChild("staff4");
		Staff5 = root.getChild("arm").getChild("staff1").getChild("staff5");
		Staff6 = root.getChild("arm").getChild("staff1").getChild("staff6");
		Cube = root.getChild("arm").getChild("staff1").getChild("cube");
		tentacle6 = root.getChild("tentacle6");
		tentacle62 = root.getChild("tentacle62");
		tentacle63 = root.getChild("tentacle63");
		foot5 = root.getChild("foot5");
		tentacle7 = root.getChild("tentacle7");
		tentacle72 = root.getChild("tentacle72");
		tentacle73 = root.getChild("tentacle73");
		tentacle74 = root.getChild("tentacle74");
		tentacle75 = root.getChild("tentacle75");
		tentacle76 = root.getChild("tentacle76");
		tentacle77 = root.getChild("tentacle77");
		abyssalnomicon = root.getChild("abyssalnomicon");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 0).addBox(-5F, -10F, -5F, 10F, 10F, 10F), PartPose.offset(2F, -28F, 1F));
		p_head.addOrReplaceChild("mask1", CubeListBuilder.create().texOffs(102, 0).addBox(-3.5F, -10F, -7F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.3490659F, 0F));
		p_head.addOrReplaceChild("mask2", CubeListBuilder.create().texOffs(102, 0).addBox(-2.5F, -10F, -7F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.3490659F, 0F));
		p_head.addOrReplaceChild("facetentacle1", CubeListBuilder.create().texOffs(116, 0).addBox(0F, 0F, 0F, 2F, 6F, 2F), PartPose.offset(-4F, -2F, -7F));
		p_head.addOrReplaceChild("facetentacle2", CubeListBuilder.create().texOffs(116, 0).addBox(0F, 0F, 0F, 2F, 6F, 2F), PartPose.offset(-1F, -2F, -7F));
		p_head.addOrReplaceChild("facetentacle3", CubeListBuilder.create().texOffs(116, 0).addBox(0F, 0F, 0F, 2F, 6F, 2F), PartPose.offset(2F, -2F, -7F));
		root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 18F, 22F, 5F), PartPose.offset(-7F, -28F, 0F));
		root.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(46, 0).addBox(0F, 0F, 0F, 5F, 26F, 1F), PartPose.offset(-7F, -28F, -1F));
		root.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(58, 0).addBox(0F, 0F, 0F, 5F, 26F, 1F), PartPose.offset(6F, -28F, -1F));
		root.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(46, 0).addBox(0F, 0F, 0F, 4F, 26F, 1F), PartPose.offset(-7F, -28F, -2F));
		root.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(59, 0).addBox(0F, 0F, 0F, 4F, 26F, 1F), PartPose.offset(7F, -28F, -2F));
		root.addOrReplaceChild("eye1", CubeListBuilder.create().texOffs(70, 0).addBox(0F, 0F, 0F, 5F, 5F, 1F), PartPose.offset(-0.5F, -21F, -1F));
		root.addOrReplaceChild("eye2", CubeListBuilder.create().texOffs(70, 6).addBox(-1F, -1F, 0F, 2F, 2F, 1F), PartPose.offset(2F, -18.5F, -1.5F));
		root.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-2.5F, -6.5F, 2F, -0.0872665F, 0F, 0.0872665F));
		root.addOrReplaceChild("tentacle12", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-3.3F, 2.3F, 1.2F, -0.0872665F, 0F, 0.1745329F));
		root.addOrReplaceChild("tentacle13", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-4.8F, 10.8F, 0.4F, -0.0872665F, 0F, 0F));
		root.addOrReplaceChild("foot1", CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, 0F, -5F, 3F, 3F, 6F), PartPose.offsetAndRotation(-4.8F, 19.1F, -0.2F, 0.2617994F, 0F, 0F));
		root.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(0.5F, -6.5F, 2F, -0.0872665F, 0F, 0.0349066F));
		root.addOrReplaceChild("tentacle22", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(0.2F, 2.3F, 1.2F, -0.1745329F, 0F, 0.0523599F));
		root.addOrReplaceChild("tentacle23", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-0.2F, 10.7F, -0.4F, 0.0872665F, 0F, 0.0872665F));
		root.addOrReplaceChild("foot2", CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, 0F, -5F, 3F, 3F, 6F), PartPose.offsetAndRotation(-1F, 19F, 0.7F, 0.2617994F, 0F, 0F));
		root.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(3.5F, -6.5F, 2F, -0.0872665F, 0F, -0.0349066F));
		root.addOrReplaceChild("tentacle32", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(3.8F, 2.3F, 1.2F, -0.0698132F, 0F, -0.0698132F));
		root.addOrReplaceChild("tentacle33", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(4.4F, 11F, 0.6F, -0.1745329F, 0F, 0F));
		root.addOrReplaceChild("foot3", CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, 0F, -5F, 3F, 3F, 6F), PartPose.offsetAndRotation(4.4F, 19.1F, -0.7F, 0.2617994F, 0F, 0F));
		root.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(6.5F, -6.5F, 2F, -0.0872665F, 0F, -0.0872665F));
		root.addOrReplaceChild("tentacle42", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(7.2F, 2F, 1.2F, 0.0872665F, 0F, -0.1745329F));
		root.addOrReplaceChild("tentacle43", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(8.7F, 10.6F, 2F, 0.1745329F, 0F, -0.0872665F));
		root.addOrReplaceChild("foot4", CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, 0F, -5F, 3F, 3F, 6F), PartPose.offsetAndRotation(9.4F, 19F, 4F, 0.2617994F, 0F, 0F));
		root.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-1F, -6.5F, 3F, 0.0872665F, 0F, 0.0872665F));
		root.addOrReplaceChild("tentacle52", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-1.7F, 2F, 3.8F, 0F, 0F, 0.2617994F));
		root.addOrReplaceChild("tentacle53", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-2.3F, 4.7F, 3.8F, 0F, 0F, 0.4363323F));
		root.addOrReplaceChild("tentacle54", CubeListBuilder.create().texOffs(12, 27).addBox(-5F, -1.5F, -1.5F, 5F, 3F, 3F), PartPose.offset(-2.2F, 6.6F, 3.8F));
		root.addOrReplaceChild("tentacle55", CubeListBuilder.create().texOffs(0, 30).addBox(-3F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-6.4F, 6.7F, 3.9F, 0F, -0.2617994F, 0.2617994F));
		PartDefinition p_arm = root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -9F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(-8.2F, 6.7F, 3.4F, 0F, -0.2617994F, -0.5235988F));
		PartDefinition p_Staff1 = p_arm.addOrReplaceChild("staff1", CubeListBuilder.create().texOffs(54, 27).addBox(-4.8F, -20.7F, -1.4F, 1F, 18F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.2617994F, 0.5235988F));
		p_Staff1.addOrReplaceChild("staff2", CubeListBuilder.create().texOffs(62, 27).addBox(6F, -23.8F, -1.4F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.5235988F));
		p_Staff1.addOrReplaceChild("staff3", CubeListBuilder.create().texOffs(66, 27).addBox(-6.7F, -25.6F, -1.4F, 1F, 2F, 1F), PartPose.offset(0F, 0F, 0F));
		p_Staff1.addOrReplaceChild("staff4", CubeListBuilder.create().texOffs(66, 30).addBox(-21.6F, -19.3F, -1.4F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.6981317F));
		p_Staff1.addOrReplaceChild("staff5", CubeListBuilder.create().texOffs(62, 32).addBox(18.3F, -21.5F, -1.4F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.8726646F));
		p_Staff1.addOrReplaceChild("staff6", CubeListBuilder.create().texOffs(66, 35).addBox(-3.9F, 1F, -4.1F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.8922867F, 0.5948578F, 0.2230717F));
		p_Staff1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(62, 42).addBox(-14F, -20F, -8.5F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.4833219F, 0.5205006F));
		root.addOrReplaceChild("tentacle6", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(2F, -6.5F, 3F, 0.0872665F, 0F, 0F));
		root.addOrReplaceChild("tentacle62", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(2F, 2.3F, 3.8F, 0.1745329F, 0F, 0.0872665F));
		root.addOrReplaceChild("tentacle63", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(1.2F, 10.5F, 5.3F, -0.0872665F, 0F, -0.0872665F));
		root.addOrReplaceChild("foot5", CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, 0F, -1F, 3F, 3F, 6F), PartPose.offsetAndRotation(1.9F, 19.2F, 4.1F, -0.2617994F, 0F, 0F));
		root.addOrReplaceChild("tentacle7", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 9F, 3F), PartPose.offsetAndRotation(5F, -6.5F, 3F, 0.0872665F, -0.0872665F, -0.0872665F));
		root.addOrReplaceChild("tentacle72", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 4F, 3F), PartPose.offsetAndRotation(5.7F, 1.9F, 3.8F, 0F, -0.1745329F, -0.3490659F));
		root.addOrReplaceChild("tentacle73", CubeListBuilder.create().texOffs(0, 27).addBox(-1.5F, 0F, -1.5F, 3F, 5F, 3F), PartPose.offsetAndRotation(6.7F, 5.1F, 3.8F, 0F, -0.1745329F, -0.7853982F));
		root.addOrReplaceChild("tentacle74", CubeListBuilder.create().texOffs(0, 30).addBox(0F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(9.2F, 8.2F, 3.9F, 0F, -0.1745329F, 0F));
		root.addOrReplaceChild("tentacle75", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -6F, -1.5F, 3F, 6F, 3F), PartPose.offsetAndRotation(10.9F, 9F, 4.5F, 0F, -0.1745329F, 0.5235988F));
		root.addOrReplaceChild("tentacle76", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -3F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(13.7F, 4.3F, 4.5F, 0F, -0.1745329F, 0.2617994F));
		root.addOrReplaceChild("tentacle77", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -6F, -1.5F, 3F, 6F, 3F), PartPose.offsetAndRotation(14.3F, 1.9F, 4.7F, 0.3490659F, -0.1745329F, 0.2617994F));
		root.addOrReplaceChild("abyssalnomicon", CubeListBuilder.create().texOffs(28, 27).addBox(-5F, -12F, -1.5F, 10F, 12F, 3F), PartPose.offset(17F, -5F, 3F));
		return LayerDefinition.create(mesh, 128, 64);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		for(int i = 0; i < 4; i++)
		abyssalnomicon.y = -5.5F + Mth.cos((i * 2 + ageInTicks) * 0.25F);
		arm.setRotation(0F, -0.2617994F, -0.5235988F);
		tentacle12.xRot = -((float)Math.PI * 2F / 5F);
		tentacle22.xRot = -((float)Math.PI * 2F / 5F);
		tentacle32.xRot = -((float)Math.PI * 2F / 5F);
		tentacle42.xRot = -((float)Math.PI * 2F / 5F);
		tentacle62.xRot = -((float)Math.PI * 2F / 5F);
		tentacle13.xRot = -((float)Math.PI * 2F / 5F);
		tentacle23.xRot = -((float)Math.PI * 2F / 5F);
		tentacle33.xRot = -((float)Math.PI * 2F / 5F);
		tentacle43.xRot = -((float)Math.PI * 2F / 5F);
		tentacle63.xRot = -((float)Math.PI * 2F / 5F);
		tentacle12.yRot = (float)Math.PI / 10F;
		tentacle22.yRot = (float)Math.PI / 10F;
		tentacle32.yRot = -(float)Math.PI / 10F;
		tentacle42.yRot = -((float)Math.PI / 10F);
		tentacle13.yRot = (float)Math.PI / 10F;
		tentacle23.yRot = (float)Math.PI / 10F;
		tentacle33.yRot = -(float)Math.PI / 10F;
		tentacle43.yRot = -((float)Math.PI / 10F);
		foot1.xRot = -((float)Math.PI * 2F / 5F);
		foot2.xRot = -((float)Math.PI * 2F / 5F);
		foot3.xRot = -((float)Math.PI * 2F / 5F);
		foot4.xRot = -((float)Math.PI * 2F / 5F);
		foot5.xRot = -((float)Math.PI * 2F / 5F);
		foot1.yRot = (float)Math.PI / 10F;
		foot2.yRot = (float)Math.PI / 10F;
		foot3.yRot = -(float)Math.PI / 10F;
		foot4.yRot = -((float)Math.PI / 10F);
		foot5.yRot = (float)Math.PI / 10F;
		tentacle12.z =+ 2;
		tentacle22.z =+ 2;
		tentacle32.z =+ 2;
		tentacle42.z =+ 2;
		tentacle62.z =+ 4.5F;
		tentacle12.x =- 3.5F;
		tentacle22.x =+ 0.5F;
		tentacle32.x =+ 3.3F;
		tentacle42.x =+ 6.6F;
		tentacle13.y =+ 4.6F;
		tentacle23.y =+ 4.8F;
		tentacle33.y =+ 5F;
		tentacle43.y =+ 4.3F;
		tentacle63.y =+ 5.3F;
		tentacle13.z =- 5.5F;
		tentacle23.z =- 5.5F;
		tentacle33.z =- 6;
		tentacle43.z =- 6;
		tentacle63.z =- 4;
		tentacle13.x =- 6.2F;
		tentacle23.x =- 2.2F;
		tentacle33.x =+ 6F;
		tentacle43.x =+ 9.6F;
		tentacle63.x =+ 2F;
		foot1.x =- 8.2F;
		foot2.x =- 4.5F;
		foot3.x =+ 7.7F;
		foot4.x =+ 11.6F;
		foot1.y =+ 7F;
		foot2.y =+ 7F;
		foot3.y =+ 7.5F;
		foot4.y =+ 6.5F;
		foot5.y =+ 3.7F;
		foot1.z =- 12;
		foot2.z =- 12;
		foot3.z =- 12;
		foot4.z =- 12;
		foot5.z =- 12;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		eye1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		eye2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle12.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle13.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		foot1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle22.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle23.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		foot2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle32.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle33.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		foot3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle42.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle43.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		foot4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle52.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle53.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle54.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle55.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle6.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle62.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle63.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		foot5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle7.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle72.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle73.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle74.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle75.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle76.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		tentacle77.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		abyssalnomicon.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
