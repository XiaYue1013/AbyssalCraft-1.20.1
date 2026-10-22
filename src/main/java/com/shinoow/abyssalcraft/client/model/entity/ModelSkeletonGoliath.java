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

public class ModelSkeletonGoliath<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart base1;
	public final ModelPart base2;
	public final ModelPart base3;
	public final ModelPart base4;
	public final ModelPart spike1;
	public final ModelPart spike2;
	public final ModelPart spike3;
	public final ModelPart spike4;
	public final ModelPart spike5;
	public final ModelPart spike6;
	public final ModelPart spike7;
	public final ModelPart spike8;
	public final ModelPart spike9;
	public final ModelPart spike10;
	public final ModelPart spike11;
	public final ModelPart thing1;
	public final ModelPart thing2;
	public final ModelPart head;
	public final ModelPart leftjaw;
	public final ModelPart rightjaw;
	public final ModelPart jaw1;
	public final ModelPart jaw2;
	public final ModelPart jaw3;
	public final ModelPart tooth1;
	public final ModelPart tooth2;
	public final ModelPart tooth3;
	public final ModelPart tooth4;
	public final ModelPart tooth5;
	public final ModelPart shoulders;
	public final ModelPart spine;
	public final ModelPart leftarm;
	public final ModelPart rightarm;
	public final ModelPart leftrib1;
	public final ModelPart leftrib12;
	public final ModelPart leftrib13;
	public final ModelPart leftrib2;
	public final ModelPart leftrib22;
	public final ModelPart leftrib23;
	public final ModelPart leftrib3;
	public final ModelPart leftrib32;
	public final ModelPart leftrib33;
	public final ModelPart leftrib4;
	public final ModelPart leftrib42;
	public final ModelPart leftrib43;
	public final ModelPart leftrib5;
	public final ModelPart leftrib52;
	public final ModelPart leftrib53;
	public final ModelPart leftrib6;
	public final ModelPart leftrib62;
	public final ModelPart leftrib63;
	public final ModelPart rightrib1;
	public final ModelPart rightrib12;
	public final ModelPart rightrib13;
	public final ModelPart rightrib2;
	public final ModelPart rightrib22;
	public final ModelPart rightrib23;
	public final ModelPart rightrib3;
	public final ModelPart rightrib32;
	public final ModelPart rightrib33;
	public final ModelPart rightrib4;
	public final ModelPart rightrib42;
	public final ModelPart rightrib43;
	public final ModelPart rightrib5;
	public final ModelPart rightrib52;
	public final ModelPart rightrib53;
	public final ModelPart rightrib6;
	public final ModelPart rightrib62;
	public final ModelPart rightrib63;
	public final ModelPart sternum;
	public final ModelPart pelvis;
	public final ModelPart leftleg;
	public final ModelPart rightleg;

	public ModelSkeletonGoliath(ModelPart root) {
		base1 = root.getChild("rightarm").getChild("base1");
		base2 = root.getChild("rightarm").getChild("base1").getChild("base2");
		base3 = root.getChild("rightarm").getChild("base1").getChild("base3");
		base4 = root.getChild("rightarm").getChild("base1").getChild("base4");
		spike1 = root.getChild("rightarm").getChild("base1").getChild("spike1");
		spike2 = root.getChild("rightarm").getChild("base1").getChild("spike2");
		spike3 = root.getChild("rightarm").getChild("base1").getChild("spike3");
		spike4 = root.getChild("rightarm").getChild("base1").getChild("spike4");
		spike5 = root.getChild("rightarm").getChild("base1").getChild("spike5");
		spike6 = root.getChild("rightarm").getChild("base1").getChild("spike6");
		spike7 = root.getChild("rightarm").getChild("base1").getChild("spike7");
		spike8 = root.getChild("rightarm").getChild("base1").getChild("spike8");
		spike9 = root.getChild("rightarm").getChild("base1").getChild("spike9");
		spike10 = root.getChild("rightarm").getChild("base1").getChild("spike10");
		spike11 = root.getChild("rightarm").getChild("base1").getChild("spike11");
		thing1 = root.getChild("rightarm").getChild("base1").getChild("thing1");
		thing2 = root.getChild("rightarm").getChild("base1").getChild("thing2");
		head = root.getChild("head");
		leftjaw = root.getChild("head").getChild("leftjaw");
		rightjaw = root.getChild("head").getChild("rightjaw");
		jaw1 = root.getChild("head").getChild("jaw1");
		jaw2 = root.getChild("head").getChild("jaw2");
		jaw3 = root.getChild("head").getChild("jaw3");
		tooth1 = root.getChild("head").getChild("tooth1");
		tooth2 = root.getChild("head").getChild("tooth2");
		tooth3 = root.getChild("head").getChild("tooth3");
		tooth4 = root.getChild("head").getChild("tooth4");
		tooth5 = root.getChild("head").getChild("tooth5");
		shoulders = root.getChild("shoulders");
		spine = root.getChild("spine");
		leftarm = root.getChild("leftarm");
		rightarm = root.getChild("rightarm");
		leftrib1 = root.getChild("leftrib1");
		leftrib12 = root.getChild("leftrib12");
		leftrib13 = root.getChild("leftrib13");
		leftrib2 = root.getChild("leftrib2");
		leftrib22 = root.getChild("leftrib22");
		leftrib23 = root.getChild("leftrib23");
		leftrib3 = root.getChild("leftrib3");
		leftrib32 = root.getChild("leftrib32");
		leftrib33 = root.getChild("leftrib33");
		leftrib4 = root.getChild("leftrib4");
		leftrib42 = root.getChild("leftrib42");
		leftrib43 = root.getChild("leftrib43");
		leftrib5 = root.getChild("leftrib5");
		leftrib52 = root.getChild("leftrib52");
		leftrib53 = root.getChild("leftrib53");
		leftrib6 = root.getChild("leftrib6");
		leftrib62 = root.getChild("leftrib62");
		leftrib63 = root.getChild("leftrib63");
		rightrib1 = root.getChild("rightrib1");
		rightrib12 = root.getChild("rightrib12");
		rightrib13 = root.getChild("rightrib13");
		rightrib2 = root.getChild("rightrib2");
		rightrib22 = root.getChild("rightrib22");
		rightrib23 = root.getChild("rightrib23");
		rightrib3 = root.getChild("rightrib3");
		rightrib32 = root.getChild("rightrib32");
		rightrib33 = root.getChild("rightrib33");
		rightrib4 = root.getChild("rightrib4");
		rightrib42 = root.getChild("rightrib42");
		rightrib43 = root.getChild("rightrib43");
		rightrib5 = root.getChild("rightrib5");
		rightrib52 = root.getChild("rightrib52");
		rightrib53 = root.getChild("rightrib53");
		rightrib6 = root.getChild("rightrib6");
		rightrib62 = root.getChild("rightrib62");
		rightrib63 = root.getChild("rightrib63");
		sternum = root.getChild("sternum");
		pelvis = root.getChild("pelvis");
		leftleg = root.getChild("leftleg");
		rightleg = root.getChild("rightleg");
	}

	public static LayerDefinition createBodyLayer(CubeDeformation deformation) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(53, 0).addBox(-4F, -9F, -4F, 9F, 9F, 9F), PartPose.offset(0F, -15F, 0F));
		p_head.addOrReplaceChild("leftjaw", CubeListBuilder.create().texOffs(49, 16).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(4F, 0F, 4F));
		p_head.addOrReplaceChild("rightjaw", CubeListBuilder.create().texOffs(49, 16).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-4F, 0F, 4F));
		p_head.addOrReplaceChild("jaw1", CubeListBuilder.create().texOffs(53, 18).addBox(-4F, 0F, -1F, 9F, 1F, 1F), PartPose.offset(0F, 2F, -3F));
		p_head.addOrReplaceChild("jaw2", CubeListBuilder.create().texOffs(73, 18).addBox(0F, 0F, 0F, 1F, 1F, 8F), PartPose.offset(4F, 2F, -4F));
		p_head.addOrReplaceChild("jaw3", CubeListBuilder.create().texOffs(73, 18).addBox(0F, 0F, 0F, 1F, 1F, 8F), PartPose.offset(-4F, 2F, -4F));
		p_head.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(49, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(4F, 1F, -4F));
		p_head.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(49, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(2F, 1F, -4F));
		p_head.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(49, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(0F, 1F, -4F));
		p_head.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(49, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-2F, 1F, -4F));
		p_head.addOrReplaceChild("tooth5", CubeListBuilder.create().texOffs(49, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(-4F, 1F, -4F));
		root.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(68, 27).addBox(-6F, 0F, 0F, 13F, 3F, 3F), PartPose.offset(0F, -11F, -1F));
		root.addOrReplaceChild("spine", CubeListBuilder.create().texOffs(89, 0).addBox(-2F, 0F, -2F, 3F, 20F, 3F), PartPose.offset(1F, -15F, 1F));
		root.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(101, 0).addBox(0F, -1F, -1.5F, 3F, 16F, 3F), PartPose.offset(7F, -10F, 0.5F));
		PartDefinition p_rightarm = root.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(101, 0).addBox(-3F, -1F, -1.5F, 3F, 16F, 3F), PartPose.offset(-6F, -10F, 0.5F));
		PartDefinition p_base1 = p_rightarm.addOrReplaceChild("base1", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -1F, -7F, 2F, 2F, 10F), PartPose.offset(-1.5F, 13F, 0F));
		p_base1.addOrReplaceChild("base2", CubeListBuilder.create().texOffs(24, 2).addBox(0F, 0F, 0F, 3F, 3F, 3F), PartPose.offsetAndRotation(-1.5F, -2.5F, -8F, -0.4089647F, 0F, 0F));
		p_base1.addOrReplaceChild("base3", CubeListBuilder.create().texOffs(24, 8).addBox(0F, 0F, 0F, 4F, 4F, 6F), PartPose.offsetAndRotation(-0.5F, -5F, -13F, -0.3717861F, 0F, 0.2602503F));
		p_base1.addOrReplaceChild("base4", CubeListBuilder.create().texOffs(0, 12).addBox(0F, 0F, 0F, 5F, 5F, 7F), PartPose.offsetAndRotation(-1F, -9F, -15F, -0.8551081F, 0.0743572F, 0F));
		p_base1.addOrReplaceChild("spike1", CubeListBuilder.create().texOffs(27, 0).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(1F, -1F, -8F));
		p_base1.addOrReplaceChild("spike2", CubeListBuilder.create().texOffs(27, 0).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -1F, -8F));
		p_base1.addOrReplaceChild("spike3", CubeListBuilder.create().texOffs(48, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(-0.5F, -5F, -7F, -0.3346075F, 0F, 0F));
		p_base1.addOrReplaceChild("spike4", CubeListBuilder.create().texOffs(48, 0).addBox(0F, 0F, 0F, 1F, 3F, 1F), PartPose.offset(-0.5F, 0F, -9F));
		p_base1.addOrReplaceChild("spike5", CubeListBuilder.create().texOffs(48, 0).addBox(0F, 0F, 0F, 1F, 4F, 1F), PartPose.offsetAndRotation(1F, -9F, -11F, -0.8179294F, 0F, 0F));
		p_base1.addOrReplaceChild("spike6", CubeListBuilder.create().texOffs(26, 0).addBox(0F, 0F, 0F, 4F, 1F, 1F), PartPose.offsetAndRotation(-4F, -5F, -13F, -0.3717861F, 0.2602503F, 0F));
		p_base1.addOrReplaceChild("spike7", CubeListBuilder.create().texOffs(26, 0).addBox(0F, 0F, 0F, 4F, 1F, 1F), PartPose.offsetAndRotation(-4F, -9F, -14F, -0.4461433F, 0.4089647F, 0.5576792F));
		p_base1.addOrReplaceChild("spike8", CubeListBuilder.create().texOffs(26, 0).addBox(0F, 0F, 0F, 4F, 1F, 1F), PartPose.offsetAndRotation(4F, -3F, -15F, -0.0371786F, -0.7807508F, -0.4461433F));
		p_base1.addOrReplaceChild("spike9", CubeListBuilder.create().texOffs(48, 0).addBox(0F, 0F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, -4F, -15F, -0.9294653F, -0.6320364F, 0.8551081F));
		p_base1.addOrReplaceChild("spike10", CubeListBuilder.create().texOffs(36, 0).addBox(0F, 0F, 0F, 1F, 1F, 5F), PartPose.offsetAndRotation(3F, -9F, -20F, -0.3717861F, 0F, 0.7435722F));
		p_base1.addOrReplaceChild("spike11", CubeListBuilder.create().texOffs(24, 0).addBox(0F, 0F, 0F, 5F, 1F, 1F), PartPose.offsetAndRotation(3F, -4F, -16F, 0F, 0F, -0.5205006F));
		p_base1.addOrReplaceChild("thing1", CubeListBuilder.create().texOffs(24, 18).addBox(0F, 0F, 0F, 4F, 4F, 1F), PartPose.offset(-2F, -2F, -6F));
		p_base1.addOrReplaceChild("thing2", CubeListBuilder.create().texOffs(34, 18).addBox(0F, 0F, 0F, 6F, 6F, 1F), PartPose.offsetAndRotation(-1.5F, -5F, -10F, -0.2602503F, 0F, 0.260246F));
		root.addOrReplaceChild("leftrib1", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -7F, 0F));
		root.addOrReplaceChild("leftrib12", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, -7F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib13", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -7F, -3F));
		root.addOrReplaceChild("leftrib2", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -5F, 0F));
		root.addOrReplaceChild("leftrib22", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, -5F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib23", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -5F, -3F));
		root.addOrReplaceChild("leftrib3", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -3F, 0F));
		root.addOrReplaceChild("leftrib32", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, -3F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib33", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -3F, -3F));
		root.addOrReplaceChild("leftrib4", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -1F, 0F));
		root.addOrReplaceChild("leftrib42", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, -1F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib43", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, -1F, -3F));
		root.addOrReplaceChild("leftrib5", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, 1F, 0F));
		root.addOrReplaceChild("leftrib52", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, 1F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib53", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(3F, 1F, -3F));
		root.addOrReplaceChild("leftrib6", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(2F, 3F, 0F));
		root.addOrReplaceChild("leftrib62", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(4F, 3F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("leftrib63", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(3F, 3F, -3F));
		root.addOrReplaceChild("rightrib1", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -7F, 0F));
		root.addOrReplaceChild("rightrib12", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, -7F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib13", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -7F, -3F));
		root.addOrReplaceChild("rightrib2", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -5F, 0F));
		root.addOrReplaceChild("rightrib22", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, -5F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib23", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -5F, -3F));
		root.addOrReplaceChild("rightrib3", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -3F, 0F));
		root.addOrReplaceChild("rightrib32", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, -3F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib33", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -3F, -3F));
		root.addOrReplaceChild("rightrib4", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -1F, 0F));
		root.addOrReplaceChild("rightrib42", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, -1F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib43", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, -1F, -3F));
		root.addOrReplaceChild("rightrib5", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, 1F, 0F));
		root.addOrReplaceChild("rightrib52", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, 1F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib53", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(-4F, 1F, -3F));
		root.addOrReplaceChild("rightrib6", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offset(-4F, 3F, 0F));
		root.addOrReplaceChild("rightrib62", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offsetAndRotation(-4F, 3F, 0F, 0F, 1.570796F, 0F));
		root.addOrReplaceChild("rightrib63", CubeListBuilder.create().texOffs(60, 20).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(-4F, 3F, -3F));
		root.addOrReplaceChild("sternum", CubeListBuilder.create().texOffs(113, 11).addBox(0F, 0F, 0F, 3F, 7F, 1F), PartPose.offset(-1F, -7F, -3F));
		root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(99, 21).addBox(0F, 0F, 0F, 9F, 3F, 3F), PartPose.offset(-4F, 5F, -1F));
		root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(54, 22).addBox(-1.5F, 0F, -1.5F, 3F, 16F, 3F), PartPose.offset(3.5F, 8F, 0.5F));
		root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(54, 22).addBox(-1.5F, 0F, -1.5F, 3F, 16F, 3F), PartPose.offset(-2.5F, 8F, 0.5F));
		return LayerDefinition.create(mesh, 128, 64);
	}

	// (1.12.2 only attached the cudgel, base1, when "renderCudgel" was set; the Skeleton Goliath was always drawn with it)


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		rightarm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftarm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightarm.zRot = 0.0F;
		leftarm.zRot = 0.0F;
		rightleg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rightleg.yRot = 0.0F;
		leftleg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		leftleg.yRot = 0.0F;
		if (riding)
		{
		rightarm.xRot += -((float)Math.PI / 5F);
		leftarm.xRot += -((float)Math.PI / 5F);
		rightleg.xRot = -((float)Math.PI * 2F / 5F);
		leftleg.xRot = -((float)Math.PI * 2F / 5F);
		rightleg.yRot = (float)Math.PI / 10F;
		leftleg.yRot = -((float)Math.PI / 10F);
		}
		rightarm.yRot = 0.0F;
		leftarm.yRot = 0.0F;
		float f6;
		float f7;
		if (attackTime > -9990.0F)
		{
		f6 = attackTime;
		shoulders.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		spine.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib1.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib12.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib13.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib2.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib22.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib23.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib3.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib32.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib33.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib4.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib42.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib43.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib5.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib52.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib53.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib6.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib62.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		leftrib63.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib1.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib12.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib13.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib2.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib22.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib23.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib3.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib32.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib33.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib4.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib42.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib43.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib5.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib52.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib53.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib6.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib62.xRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightrib63.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		sternum.yRot = Mth.sin(Mth.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;
		rightarm.yRot += spine.yRot;
		leftarm.yRot += spine.yRot;
		leftarm.xRot += spine.yRot;
		f6 = 1.0F - attackTime;
		f6 *= f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		f7 = Mth.sin(f6 * (float)Math.PI);
		float f8 = Mth.sin(attackTime * (float)Math.PI) * -(head.xRot - 0.7F) * 0.75F;
		rightarm.xRot = (float)(rightarm.xRot - (f7 * 1.2D + f8));
		rightarm.yRot += spine.yRot * 2.0F;
		rightarm.zRot = Mth.sin(attackTime * (float)Math.PI) * -0.4F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		shoulders.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		spine.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib12.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib13.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib22.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib23.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib32.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib33.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib42.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib43.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib52.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib53.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib6.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib62.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftrib63.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib12.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib13.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib22.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib23.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib32.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib33.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib42.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib43.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib52.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib53.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib6.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib62.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightrib63.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		sternum.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		pelvis.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}
