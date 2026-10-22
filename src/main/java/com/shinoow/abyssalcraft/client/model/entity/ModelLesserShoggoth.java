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
 *     1.20.1 port - part tree converted by tools/port_model.py, animation and render() translated by hand
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Lesser Shoggoth model: a mass of tentacles, eyes and three mouths. Adults are drawn 1.5x, babies 0.75x the size of
 * the part tree.
 */
public class ModelLesserShoggoth extends EntityModel<EntityLesserShoggoth> {

	/** Dark Realm (shadow) Shoggoths fade with the light around them */
	private float shadowAlpha = 1.0F;

	public final ModelPart lHindTentacle01a;
	public final ModelPart mouth01Upper;
	public final ModelPart rArm01c;
	public final ModelPart lHindTentacle03b;
	public final ModelPart mouth01Lower;
	public final ModelPart lHindTentacle03;
	public final ModelPart lBodyTentacle04a;
	public final ModelPart mouth02Snout;
	public final ModelPart lBackTentacle01a;
	public final ModelPart eye17;
	public final ModelPart lHindTentacle02b;
	public final ModelPart mouth03Snout;
	public final ModelPart mouth03Teeth01;
	public final ModelPart rBackTentacle02b_1;
	public final ModelPart eye21;
	public final ModelPart mouth03Tooth05;
	public final ModelPart mouth02Lower;
	public final ModelPart lBackTentacle02c;
	public final ModelPart rBodyTentacle04b;
	public final ModelPart eye09;
	public final ModelPart eye01;
	public final ModelPart rBodyTentacle03a;
	public final ModelPart mouth03Tooth02;
	public final ModelPart lArm01a;
	public final ModelPart rBackTentacle01a;
	public final ModelPart lBackTentacle01c;
	public final ModelPart rArm01a;
	public final ModelPart lArm01b;
	public final ModelPart mouth03Lower;
	public final ModelPart eye05;
	public final ModelPart lArm01d;
	public final ModelPart headJoint;
	public final ModelPart lBodyTentacle01b;
	public final ModelPart rArm01b;
	public final ModelPart rBodyTentacle02b;
	public final ModelPart eye20;
	public final ModelPart rBackTentacle02a;
	public final ModelPart lBackTentacle02c_1;
	public final ModelPart eye19;
	public final ModelPart eye28;
	public final ModelPart eye23;
	public final ModelPart tail01c;
	public final ModelPart rHindTentacle02b;
	public final ModelPart bodyBase;
	public final ModelPart rHindTentacle01e;
	public final ModelPart tail01d;
	public final ModelPart lHindTentacle03c;
	public final ModelPart mouth01Teeth03;
	public final ModelPart mouth02Tooth02;
	public final ModelPart rBackTentacle01b;
	public final ModelPart lBodyTentacle01a;
	public final ModelPart mouth02Teeth02;
	public final ModelPart lBackTentacle02b;
	public final ModelPart eye11;
	public final ModelPart lHindTentacle01b;
	public final ModelPart eye15;
	public final ModelPart bodyUpper;
	public final ModelPart eye08;
	public final ModelPart lHindTentacle02c;
	public final ModelPart eye31;
	public final ModelPart lArm01c;
	public final ModelPart rBodyTentacle04a;
	public final ModelPart rBodyTentacle02c;
	public final ModelPart rHindTentacle03b;
	public final ModelPart lHindTentacle02e;
	public final ModelPart mouth02Teeth03_1;
	public final ModelPart rHindTentacle01b;
	public final ModelPart rBodyTentacle03b;
	public final ModelPart mouth03Tooth01;
	public final ModelPart bodyMid;
	public final ModelPart rBodyTentacle01a;
	public final ModelPart mouth02Tooth01;
	public final ModelPart lHindTentacle01d;
	public final ModelPart eye29;
	public final ModelPart mouth03Tooth04;
	public final ModelPart mouth01Tooth05;
	public final ModelPart mouth01Teeth02;
	public final ModelPart lBodyTentacle02a;
	public final ModelPart eye27;
	public final ModelPart mouth01Tooth03;
	public final ModelPart rHindTentacle01c;
	public final ModelPart rHindTentacle02e;
	public final ModelPart rHindTentacle03c;
	public final ModelPart eye30;
	public final ModelPart eye03;
	public final ModelPart rBackTentacle01c;
	public final ModelPart rHindTentacle03a;
	public final ModelPart rHindTentacle01a;
	public final ModelPart lBodyTentacle04b;
	public final ModelPart rHindTentacle01d;
	public final ModelPart mouth01Snout;
	public final ModelPart lBodyTentacle01c;
	public final ModelPart rHindTentacle02d;
	public final ModelPart lBackTentacle02a;
	public final ModelPart eye02;
	public final ModelPart tail01b;
	public final ModelPart mouth02Upper;
	public final ModelPart eye32;
	public final ModelPart lBodyTentacle03a;
	public final ModelPart mouth01Tooth02;
	public final ModelPart mouth01Tooth04;
	public final ModelPart lHindTentacle01e;
	public final ModelPart eye06;
	public final ModelPart mouth02Tooth03;
	public final ModelPart rBodyTentacle01c;
	public final ModelPart mouth01Tooth01;
	public final ModelPart lHindTentacle01c;
	public final ModelPart eye04_1;
	public final ModelPart mouth01Tooth06;
	public final ModelPart lBackTentacle01b;
	public final ModelPart mouth02Teeth03;
	public final ModelPart eye12;
	public final ModelPart eye14;
	public final ModelPart lBodyTentacle02b;
	public final ModelPart eye18;
	public final ModelPart mouth03Teeth02;
	public final ModelPart eye16;
	public final ModelPart eye26;
	public final ModelPart eye22;
	public final ModelPart tail01e;
	public final ModelPart lHindTentacle03a;
	public final ModelPart lHindTentacle02d;
	public final ModelPart lHindTentacle02a;
	public final ModelPart rHindTentacle03;
	public final ModelPart lBackTentacle02a_1;
	public final ModelPart rBackTentacle02b;
	public final ModelPart eye07;
	public final ModelPart rBackTentacle02c_1;
	public final ModelPart rArm01d;
	public final ModelPart mouth02Teeth01;
	public final ModelPart mouth01Teeth01;
	public final ModelPart rBodyTentacle01b;
	public final ModelPart eye10;
	public final ModelPart rHindTentacle02c;
	public final ModelPart eye25;
	public final ModelPart eye13;
	public final ModelPart eye24;
	public final ModelPart lBackTentacle02b_1;
	public final ModelPart mouth03Tooth03;
	public final ModelPart rHindTentacle02a;
	public final ModelPart lBodyTentacle02c;
	public final ModelPart mouth02Tooth04;
	public final ModelPart rBodyTentacle02a;
	public final ModelPart tail01a;
	public final ModelPart mouth02Tooth04_1;
	public final ModelPart rBackTentacle02c;
	public final ModelPart lBodyTentacle03b;
	public final ModelPart eye04;
	public final ModelPart rBackTentacle02a_1;
	public final ModelPart mouth03Upper;

	public ModelLesserShoggoth(ModelPart root) {
		lHindTentacle01a = root.getChild("bodybase").getChild("lhindtentacle01a");
		mouth01Upper = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper");
		rArm01c = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("rarm01b").getChild("rarm01c");
		lHindTentacle03b = root.getChild("bodybase").getChild("lhindtentacle03a").getChild("lhindtentacle03b");
		mouth01Lower = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01lower");
		lHindTentacle03 = root.getChild("bodybase").getChild("lhindtentacle03a").getChild("lhindtentacle03");
		lBodyTentacle04a = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle04a");
		mouth02Snout = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02snout");
		lBackTentacle01a = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle01a");
		eye17 = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("rarm01b").getChild("eye17");
		lHindTentacle02b = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b");
		mouth03Snout = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03snout");
		mouth03Teeth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03teeth01");
		rBackTentacle02b_1 = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a_1").getChild("rbacktentacle02b_1");
		eye21 = root.getChild("bodybase").getChild("bodymid").getChild("eye21");
		mouth03Tooth05 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03tooth05");
		mouth02Lower = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02lower");
		lBackTentacle02c = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a").getChild("lbacktentacle02b").getChild("lbacktentacle02c");
		rBodyTentacle04b = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle04a").getChild("rbodytentacle04b");
		eye09 = root.getChild("headjoint").getChild("bodyupper").getChild("eye09");
		eye01 = root.getChild("headjoint").getChild("bodyupper").getChild("eye01");
		rBodyTentacle03a = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle03a");
		mouth03Tooth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03tooth02");
		lArm01a = root.getChild("bodybase").getChild("bodymid").getChild("larm01a");
		rBackTentacle01a = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle01a");
		lBackTentacle01c = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle01a").getChild("lbacktentacle01b").getChild("lbacktentacle01c");
		rArm01a = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a");
		lArm01b = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("larm01b");
		mouth03Lower = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03lower");
		eye05 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("eye05");
		lArm01d = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("larm01b").getChild("larm01c").getChild("larm01d");
		headJoint = root.getChild("headjoint");
		lBodyTentacle01b = root.getChild("bodybase").getChild("lbodytentacle01a").getChild("lbodytentacle01b");
		rArm01b = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("rarm01b");
		rBodyTentacle02b = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle02a").getChild("rbodytentacle02b");
		eye20 = root.getChild("bodybase").getChild("bodymid").getChild("eye20");
		rBackTentacle02a = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a");
		lBackTentacle02c_1 = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a_1").getChild("lbacktentacle02b_1").getChild("lbacktentacle02c_1");
		eye19 = root.getChild("bodybase").getChild("bodymid").getChild("eye19");
		eye28 = root.getChild("bodybase").getChild("tail01a").getChild("eye28");
		eye23 = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b").getChild("eye23");
		tail01c = root.getChild("bodybase").getChild("tail01a").getChild("tail01b").getChild("tail01c");
		rHindTentacle02b = root.getChild("bodybase").getChild("rhindtentacle02a").getChild("rhindtentacle02b");
		bodyBase = root.getChild("bodybase");
		rHindTentacle01e = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b").getChild("rhindtentacle01c").getChild("rhindtentacle01d").getChild("rhindtentacle01e");
		tail01d = root.getChild("bodybase").getChild("tail01a").getChild("tail01b").getChild("tail01c").getChild("tail01d");
		lHindTentacle03c = root.getChild("bodybase").getChild("lhindtentacle03a").getChild("lhindtentacle03b").getChild("lhindtentacle03c");
		mouth01Teeth03 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01lower").getChild("mouth01teeth03");
		mouth02Tooth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02tooth02");
		rBackTentacle01b = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle01a").getChild("rbacktentacle01b");
		lBodyTentacle01a = root.getChild("bodybase").getChild("lbodytentacle01a");
		mouth02Teeth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02lower").getChild("mouth02teeth02");
		lBackTentacle02b = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a").getChild("lbacktentacle02b");
		eye11 = root.getChild("headjoint").getChild("bodyupper").getChild("eye11");
		lHindTentacle01b = root.getChild("bodybase").getChild("lhindtentacle01a").getChild("lhindtentacle01b");
		eye15 = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("eye15");
		bodyUpper = root.getChild("headjoint").getChild("bodyupper");
		eye08 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("eye08");
		lHindTentacle02c = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b").getChild("lhindtentacle02c");
		eye31 = root.getChild("bodybase").getChild("rhindtentacle02a").getChild("rhindtentacle02b").getChild("rhindtentacle02c").getChild("eye31");
		lArm01c = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("larm01b").getChild("larm01c");
		rBodyTentacle04a = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle04a");
		rBodyTentacle02c = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle02a").getChild("rbodytentacle02b").getChild("rbodytentacle02c");
		rHindTentacle03b = root.getChild("bodybase").getChild("rhindtentacle03a").getChild("rhindtentacle03b");
		lHindTentacle02e = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b").getChild("lhindtentacle02c").getChild("lhindtentacle02d").getChild("lhindtentacle02e");
		mouth02Teeth03_1 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03lower").getChild("mouth02teeth03_1");
		rHindTentacle01b = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b");
		rBodyTentacle03b = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle03a").getChild("rbodytentacle03b");
		mouth03Tooth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03tooth01");
		bodyMid = root.getChild("bodybase").getChild("bodymid");
		rBodyTentacle01a = root.getChild("bodybase").getChild("rbodytentacle01a");
		mouth02Tooth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02tooth01");
		lHindTentacle01d = root.getChild("bodybase").getChild("lhindtentacle01a").getChild("lhindtentacle01b").getChild("lhindtentacle01c").getChild("lhindtentacle01d");
		eye29 = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b").getChild("rhindtentacle01c").getChild("eye29");
		mouth03Tooth04 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03tooth04");
		mouth01Tooth05 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth05");
		mouth01Teeth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01lower").getChild("mouth01teeth02");
		lBodyTentacle02a = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle02a");
		eye27 = root.getChild("bodybase").getChild("tail01a").getChild("tail01b").getChild("eye27");
		mouth01Tooth03 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth03");
		rHindTentacle01c = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b").getChild("rhindtentacle01c");
		rHindTentacle02e = root.getChild("bodybase").getChild("rhindtentacle02a").getChild("rhindtentacle02b").getChild("rhindtentacle02c").getChild("rhindtentacle02d").getChild("rhindtentacle02e");
		rHindTentacle03c = root.getChild("bodybase").getChild("rhindtentacle03a").getChild("rhindtentacle03b").getChild("rhindtentacle03c");
		eye30 = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b").getChild("rhindtentacle01c").getChild("rhindtentacle01d").getChild("eye30");
		eye03 = root.getChild("headjoint").getChild("bodyupper").getChild("eye03");
		rBackTentacle01c = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle01a").getChild("rbacktentacle01b").getChild("rbacktentacle01c");
		rHindTentacle03a = root.getChild("bodybase").getChild("rhindtentacle03a");
		rHindTentacle01a = root.getChild("bodybase").getChild("rhindtentacle01a");
		lBodyTentacle04b = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle04a").getChild("lbodytentacle04b");
		rHindTentacle01d = root.getChild("bodybase").getChild("rhindtentacle01a").getChild("rhindtentacle01b").getChild("rhindtentacle01c").getChild("rhindtentacle01d");
		mouth01Snout = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01snout");
		lBodyTentacle01c = root.getChild("bodybase").getChild("lbodytentacle01a").getChild("lbodytentacle01b").getChild("lbodytentacle01c");
		rHindTentacle02d = root.getChild("bodybase").getChild("rhindtentacle02a").getChild("rhindtentacle02b").getChild("rhindtentacle02c").getChild("rhindtentacle02d");
		lBackTentacle02a = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a");
		eye02 = root.getChild("headjoint").getChild("bodyupper").getChild("eye02");
		tail01b = root.getChild("bodybase").getChild("tail01a").getChild("tail01b");
		mouth02Upper = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper");
		eye32 = root.getChild("bodybase").getChild("rhindtentacle03a").getChild("rhindtentacle03b").getChild("eye32");
		lBodyTentacle03a = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle03a");
		mouth01Tooth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth02");
		mouth01Tooth04 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth04");
		lHindTentacle01e = root.getChild("bodybase").getChild("lhindtentacle01a").getChild("lhindtentacle01b").getChild("lhindtentacle01c").getChild("lhindtentacle01d").getChild("lhindtentacle01e");
		eye06 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("eye06");
		mouth02Tooth03 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02tooth03");
		rBodyTentacle01c = root.getChild("bodybase").getChild("rbodytentacle01a").getChild("rbodytentacle01b").getChild("rbodytentacle01c");
		mouth01Tooth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth01");
		lHindTentacle01c = root.getChild("bodybase").getChild("lhindtentacle01a").getChild("lhindtentacle01b").getChild("lhindtentacle01c");
		eye04_1 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("eye04_1");
		mouth01Tooth06 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01tooth06");
		lBackTentacle01b = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle01a").getChild("lbacktentacle01b");
		mouth02Teeth03 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02lower").getChild("mouth02teeth03");
		eye12 = root.getChild("headjoint").getChild("bodyupper").getChild("eye12");
		eye14 = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("larm01b").getChild("larm01c").getChild("eye14");
		lBodyTentacle02b = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle02a").getChild("lbodytentacle02b");
		eye18 = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("eye18");
		mouth03Teeth02 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03lower").getChild("mouth03teeth02");
		eye16 = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("rarm01b").getChild("eye16");
		eye26 = root.getChild("bodybase").getChild("tail01a").getChild("tail01b").getChild("tail01c").getChild("eye26");
		eye22 = root.getChild("bodybase").getChild("lbodytentacle01a").getChild("eye22");
		tail01e = root.getChild("bodybase").getChild("tail01a").getChild("tail01b").getChild("tail01c").getChild("tail01d").getChild("tail01e");
		lHindTentacle03a = root.getChild("bodybase").getChild("lhindtentacle03a");
		lHindTentacle02d = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b").getChild("lhindtentacle02c").getChild("lhindtentacle02d");
		lHindTentacle02a = root.getChild("bodybase").getChild("lhindtentacle02a");
		rHindTentacle03 = root.getChild("bodybase").getChild("rhindtentacle03a").getChild("rhindtentacle03");
		lBackTentacle02a_1 = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a_1");
		rBackTentacle02b = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a").getChild("rbacktentacle02b");
		eye07 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("eye07");
		rBackTentacle02c_1 = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a_1").getChild("rbacktentacle02b_1").getChild("rbacktentacle02c_1");
		rArm01d = root.getChild("bodybase").getChild("bodymid").getChild("rarm01a").getChild("rarm01b").getChild("rarm01c").getChild("rarm01d");
		mouth02Teeth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02teeth01");
		mouth01Teeth01 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth01upper").getChild("mouth01teeth01");
		rBodyTentacle01b = root.getChild("bodybase").getChild("rbodytentacle01a").getChild("rbodytentacle01b");
		eye10 = root.getChild("headjoint").getChild("bodyupper").getChild("eye10");
		rHindTentacle02c = root.getChild("bodybase").getChild("rhindtentacle02a").getChild("rhindtentacle02b").getChild("rhindtentacle02c");
		eye25 = root.getChild("bodybase").getChild("lhindtentacle01a").getChild("lhindtentacle01b").getChild("eye25");
		eye13 = root.getChild("bodybase").getChild("bodymid").getChild("larm01a").getChild("eye13");
		eye24 = root.getChild("bodybase").getChild("lhindtentacle02a").getChild("lhindtentacle02b").getChild("lhindtentacle02c").getChild("lhindtentacle02d").getChild("eye24");
		lBackTentacle02b_1 = root.getChild("bodybase").getChild("bodymid").getChild("lbacktentacle02a_1").getChild("lbacktentacle02b_1");
		mouth03Tooth03 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper").getChild("mouth03tooth03");
		rHindTentacle02a = root.getChild("bodybase").getChild("rhindtentacle02a");
		lBodyTentacle02c = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle02a").getChild("lbodytentacle02b").getChild("lbodytentacle02c");
		mouth02Tooth04 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02tooth04");
		rBodyTentacle02a = root.getChild("bodybase").getChild("bodymid").getChild("rbodytentacle02a");
		tail01a = root.getChild("bodybase").getChild("tail01a");
		mouth02Tooth04_1 = root.getChild("headjoint").getChild("bodyupper").getChild("mouth02upper").getChild("mouth02tooth04_1");
		rBackTentacle02c = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a").getChild("rbacktentacle02b").getChild("rbacktentacle02c");
		lBodyTentacle03b = root.getChild("bodybase").getChild("bodymid").getChild("lbodytentacle03a").getChild("lbodytentacle03b");
		eye04 = root.getChild("headjoint").getChild("bodyupper").getChild("eye04");
		rBackTentacle02a_1 = root.getChild("bodybase").getChild("bodymid").getChild("rbacktentacle02a_1");
		mouth03Upper = root.getChild("headjoint").getChild("bodyupper").getChild("mouth03upper");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_headJoint = root.addOrReplaceChild("headjoint", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offset(0F, 1.6F, -4F));
		PartDefinition p_bodyUpper = p_headJoint.addOrReplaceChild("bodyupper", CubeListBuilder.create().texOffs(0, 1).addBox(-5.5F, -17F, -5.5F, 11F, 17F, 11F), PartPose.offsetAndRotation(0F, 3.1F, 2F, 1.5707963F, 0F, 0F));
		PartDefinition p_mouth01Upper = p_bodyUpper.addOrReplaceChild("mouth01upper", CubeListBuilder.create().texOffs(59, 0).addBox(-4F, -6F, -1F, 8F, 6F, 2F), PartPose.offset(0F, -15.3F, 0.3F));
		PartDefinition p_mouth01Lower = p_mouth01Upper.addOrReplaceChild("mouth01lower", CubeListBuilder.create().texOffs(59, 11).addBox(-3.5F, -4F, -0.5F, 7F, 5F, 1F), PartPose.offsetAndRotation(0F, -1.3F, -3.5F, 0.2617994F, 0F, 0F));
		p_mouth01Lower.addOrReplaceChild("mouth01teeth03", CubeListBuilder.create().texOffs(96, 19).addBox(2.4F, -3.9F, 0.3F, 1F, 4F, 1F), PartPose.offset(0F, 0F, 0F));
		p_mouth01Lower.addOrReplaceChild("mouth01teeth02", CubeListBuilder.create().texOffs(80, 19).addBox(-3.4F, -3.9F, 0.3F, 6F, 4F, 1F), PartPose.offset(0F, 0F, 0F));
		p_mouth01Upper.addOrReplaceChild("eye05", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2.3F, -3.2F, 2.9F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth05", CubeListBuilder.create().texOffs(105, 10).mirror(true).addBox(-0.5F, -0.5F, -2.9F, 1F, 1F, 3F).mirror(false), PartPose.offsetAndRotation(3.4F, -3.4F, -0.4F, 0F, -0.1919862F, 0.5235988F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth03", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -2.9F, 1F, 1F, 3F), PartPose.offsetAndRotation(-3.1F, -3.4F, -0.4F, 0F, 0.1919862F, 0F));
		p_mouth01Upper.addOrReplaceChild("mouth01snout", CubeListBuilder.create().texOffs(81, 0).addBox(-3.5F, -4.5F, -1.5F, 7F, 6F, 3F), PartPose.offsetAndRotation(0F, -1.3F, 2.4F, 0.5235988F, 0F, 0F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth02", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -3F, 1F, 1F, 3F), PartPose.offsetAndRotation(0.4F, -5.5F, -0.5F, -0.3141593F, 0.2792527F, 0.7853982F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth04", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -2.2F, 1F, 1F, 3F), PartPose.offsetAndRotation(-3.1F, -5.3F, -0.5F, 0F, 0F, 0.2617994F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth01", CubeListBuilder.create().texOffs(105, 17).mirror(true).addBox(-0.5F, -0.5F, -3.7F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(-2F, -5.5F, -0.7F, -0.3141593F, -0.2792527F, -0.7853982F));
		p_mouth01Upper.addOrReplaceChild("eye04_1", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2.3F, -3.2F, 2.9F));
		p_mouth01Upper.addOrReplaceChild("mouth01tooth06", CubeListBuilder.create().texOffs(105, 17).addBox(-0.5F, -0.5F, -4.4F, 1F, 1F, 5F), PartPose.offsetAndRotation(2.7F, -5.5F, -0.5F, -0.3141593F, 0.2792527F, 0.7853982F));
		p_mouth01Upper.addOrReplaceChild("mouth01teeth01", CubeListBuilder.create().texOffs(79, 10).addBox(-3.5F, -0.7F, -2.9F, 7F, 5F, 2F), PartPose.offset(0F, -5F, 0F));
		p_bodyUpper.addOrReplaceChild("eye09", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-1.2F, -11.9F, 6.1F));
		p_bodyUpper.addOrReplaceChild("eye01", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(5.5F, -14F, 5F));
		p_bodyUpper.addOrReplaceChild("eye11", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2.1F, -3.9F, 6.3F));
		p_bodyUpper.addOrReplaceChild("eye03", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(5.6F, -5.4F, 2.6F));
		p_bodyUpper.addOrReplaceChild("eye02", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-5.5F, -14F, 5F));
		PartDefinition p_mouth02Upper = p_bodyUpper.addOrReplaceChild("mouth02upper", CubeListBuilder.create().texOffs(59, 0).addBox(-4F, -6F, -1F, 8F, 6F, 2F), PartPose.offsetAndRotation(3.8F, -12.2F, -1.8F, 0F, 0F, 0.9599311F));
		p_mouth02Upper.addOrReplaceChild("mouth02snout", CubeListBuilder.create().texOffs(81, 0).addBox(-3.5F, -4.5F, -1.5F, 7F, 6F, 3F), PartPose.offsetAndRotation(0F, -1.3F, 2.4F, 0.5235988F, 0F, 0F));
		PartDefinition p_mouth02Lower = p_mouth02Upper.addOrReplaceChild("mouth02lower", CubeListBuilder.create().texOffs(59, 11).addBox(-3.5F, -4F, -0.5F, 7F, 5F, 1F), PartPose.offsetAndRotation(0F, -1.3F, -3.5F, 0.2617994F, 0F, 0F));
		p_mouth02Lower.addOrReplaceChild("mouth02teeth02", CubeListBuilder.create().texOffs(80, 19).addBox(-3.4F, -3.9F, 0.3F, 6F, 4F, 1F), PartPose.offset(0F, 0F, 0F));
		p_mouth02Lower.addOrReplaceChild("mouth02teeth03", CubeListBuilder.create().texOffs(96, 19).addBox(2.4F, -3.9F, 0.3F, 1F, 4F, 1F), PartPose.offset(0F, 0F, 0F));
		p_mouth02Upper.addOrReplaceChild("mouth02tooth02", CubeListBuilder.create().texOffs(105, 17).addBox(-0.5F, -0.5F, -4.4F, 1F, 1F, 5F), PartPose.offsetAndRotation(2.7F, -5.5F, -0.5F, -0.3141593F, 0.2792527F, 0.7853982F));
		p_mouth02Upper.addOrReplaceChild("mouth02tooth01", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -3F, 1F, 1F, 3F), PartPose.offsetAndRotation(0.3F, -5.5F, -0.5F, -0.3141593F, 0.2792527F, 0.7853982F));
		p_mouth02Upper.addOrReplaceChild("eye06", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(0.4F, -3F, 3.5F));
		p_mouth02Upper.addOrReplaceChild("mouth02tooth03", CubeListBuilder.create().texOffs(105, 17).mirror(true).addBox(-0.5F, -0.5F, -3.7F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(-3.4F, -5.5F, -0.7F, -0.3141593F, -0.2792527F, -0.7853982F));
		p_mouth02Upper.addOrReplaceChild("mouth02teeth01", CubeListBuilder.create().texOffs(79, 10).addBox(-3.5F, -0.7F, -2.9F, 7F, 5F, 2F), PartPose.offset(0F, -5F, 0F));
		p_mouth02Upper.addOrReplaceChild("mouth02tooth04", CubeListBuilder.create().texOffs(105, 17).mirror(true).addBox(-0.5F, -0.5F, -4.7F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(-1.6F, -5.5F, -0.6F, -0.3141593F, -0.2792527F, -0.7853982F));
		p_mouth02Upper.addOrReplaceChild("mouth02tooth04_1", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -2.9F, 1F, 1F, 3F), PartPose.offsetAndRotation(-3.4F, -3.4F, -0.4F, 0.1919862F, -0.1919862F, 0.8726646F));
		p_bodyUpper.addOrReplaceChild("eye12", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-6F, -6.4F, 3.9F));
		p_bodyUpper.addOrReplaceChild("eye10", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(3.4F, -7.7F, 6F));
		p_bodyUpper.addOrReplaceChild("eye04", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(4.8F, -16.8F, 2.7F));
		PartDefinition p_mouth03Upper = p_bodyUpper.addOrReplaceChild("mouth03upper", CubeListBuilder.create().texOffs(59, 0).mirror(true).addBox(-4F, -6F, -1F, 8F, 6F, 2F).mirror(false), PartPose.offsetAndRotation(-3.8F, -12.2F, -1.8F, 0F, 0F, -0.9599311F));
		p_mouth03Upper.addOrReplaceChild("mouth03snout", CubeListBuilder.create().texOffs(81, 0).mirror(true).addBox(-3.5F, -4.5F, -1.5F, 7F, 6F, 3F).mirror(false), PartPose.offsetAndRotation(0F, -1.3F, 2.4F, 0.5235988F, 0F, 0F));
		p_mouth03Upper.addOrReplaceChild("mouth03teeth01", CubeListBuilder.create().texOffs(79, 10).mirror(true).addBox(-3.5F, -0.7F, -2.9F, 7F, 5F, 2F).mirror(false), PartPose.offset(0F, -5F, 0F));
		p_mouth03Upper.addOrReplaceChild("mouth03tooth05", CubeListBuilder.create().texOffs(105, 17).mirror(true).addBox(-0.5F, -0.5F, -3.8F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(3.4F, -5.1F, -0.4F, 0.0872665F, -0.3141593F, 0.5235988F));
		p_mouth03Upper.addOrReplaceChild("mouth03tooth02", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -3F, 1F, 1F, 3F), PartPose.offsetAndRotation(0.2F, -5.5F, -0.4F, -0.3141593F, 0.2792527F, 0.7853982F));
		PartDefinition p_mouth03Lower = p_mouth03Upper.addOrReplaceChild("mouth03lower", CubeListBuilder.create().texOffs(59, 11).mirror(true).addBox(-3.5F, -4F, -0.5F, 7F, 5F, 1F).mirror(false), PartPose.offsetAndRotation(0F, -1.3F, -3.5F, 0.2617994F, 0F, 0F));
		p_mouth03Lower.addOrReplaceChild("mouth02teeth03_1", CubeListBuilder.create().texOffs(96, 19).mirror(true).addBox(-3.3F, -3.9F, 0.3F, 1F, 4F, 1F).mirror(false), PartPose.offset(0F, 0F, 0F));
		p_mouth03Lower.addOrReplaceChild("mouth03teeth02", CubeListBuilder.create().texOffs(80, 19).mirror(true).addBox(-2.7F, -3.9F, 0.3F, 6F, 4F, 1F).mirror(false), PartPose.offset(0F, 0F, 0F));
		p_mouth03Upper.addOrReplaceChild("eye08", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(3.3F, -4.8F, 1.4F));
		p_mouth03Upper.addOrReplaceChild("mouth03tooth01", CubeListBuilder.create().texOffs(105, 17).mirror(true).addBox(-0.5F, -0.5F, -4.8F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(-1.7F, -5.5F, -0.7F, -0.3141593F, -0.2792527F, -0.7853982F));
		p_mouth03Upper.addOrReplaceChild("mouth03tooth04", CubeListBuilder.create().texOffs(105, 10).addBox(-0.5F, -0.5F, -3F, 1F, 1F, 3F), PartPose.offsetAndRotation(2.2F, -5.5F, -0.4F, -0.418879F, 0.0872665F, 0.7853982F));
		p_mouth03Upper.addOrReplaceChild("eye07", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-1.8F, -3F, 3.5F));
		p_mouth03Upper.addOrReplaceChild("mouth03tooth03", CubeListBuilder.create().texOffs(105, 10).mirror(true).addBox(-0.5F, -0.5F, -3F, 1F, 1F, 3F).mirror(false), PartPose.offsetAndRotation(-3.3F, -5.5F, -0.4F, -0.3141593F, 0.1919862F, -0.9560914F));
		PartDefinition p_bodyBase = root.addOrReplaceChild("bodybase", CubeListBuilder.create().texOffs(0, 64).addBox(-7F, 0F, -4F, 14F, 12F, 16F), PartPose.offset(0F, 12F, 0F));
		PartDefinition p_lHindTentacle01a = p_bodyBase.addOrReplaceChild("lhindtentacle01a", CubeListBuilder.create().texOffs(0, 96).addBox(-3F, -3F, 0F, 6F, 6F, 10F), PartPose.offsetAndRotation(4.1F, 6.7F, 4.9F, -0.1396263F, 0.2094395F, 0F));
		PartDefinition p_lHindTentacle01b = p_lHindTentacle01a.addOrReplaceChild("lhindtentacle01b", CubeListBuilder.create().texOffs(34, 96).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 9.5F, 0F, -0.1396263F, 0F));
		PartDefinition p_lHindTentacle01c = p_lHindTentacle01b.addOrReplaceChild("lhindtentacle01c", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 7.5F, 0.1396263F, -0.0698132F, 0F));
		PartDefinition p_lHindTentacle01d = p_lHindTentacle01c.addOrReplaceChild("lhindtentacle01d", CubeListBuilder.create().texOffs(92, 96).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F), PartPose.offsetAndRotation(0F, 0.4F, 7.6F, 0F, -0.0523599F, 0F));
		p_lHindTentacle01d.addOrReplaceChild("lhindtentacle01e", CubeListBuilder.create().texOffs(0, 113).addBox(-1F, -1F, 0F, 2F, 2F, 10F), PartPose.offsetAndRotation(0F, 0.4F, 8.9F, 0F, 0.0698132F, 0F));
		p_lHindTentacle01b.addOrReplaceChild("eye25", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(0.8F, -2.8F, 3.8F));
		PartDefinition p_lBodyTentacle01a = p_bodyBase.addOrReplaceChild("lbodytentacle01a", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, -8F, 4F, 4F, 8F), PartPose.offsetAndRotation(4.8F, 4.1F, 0.7F, 0.3665191F, -0.7853982F, 0F));
		PartDefinition p_lBodyTentacle01b = p_lBodyTentacle01a.addOrReplaceChild("lbodytentacle01b", CubeListBuilder.create().texOffs(93, 97).addBox(-1.5F, -1.5F, -8F, 3F, 3F, 8F), PartPose.offsetAndRotation(0F, 0F, -7F, 0.0523599F, 0.3665191F, 0F));
		p_lBodyTentacle01b.addOrReplaceChild("lbodytentacle01c", CubeListBuilder.create().texOffs(1, 114).addBox(-1F, -1F, -9F, 2F, 2F, 9F), PartPose.offsetAndRotation(0F, 0F, -7.2F, 0.4537856F, -0.0872665F, 0F));
		p_lBodyTentacle01a.addOrReplaceChild("eye22", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(1.6F, -1.7F, -3.4F));
		PartDefinition p_bodyMid = p_bodyBase.addOrReplaceChild("bodymid", CubeListBuilder.create().texOffs(0, 31).addBox(-6.5F, -16F, -7F, 13F, 18F, 14F), PartPose.offsetAndRotation(0F, 3.6F, 4.4F, 0.7853982F, 0F, 0F));
		PartDefinition p_lBodyTentacle04a = p_bodyMid.addOrReplaceChild("lbodytentacle04a", CubeListBuilder.create().texOffs(6, 119).addBox(-1F, -1F, -4F, 2F, 2F, 4F), PartPose.offsetAndRotation(1.5F, -6F, -6.7F, 0.2792527F, -0.1047198F, 0F));
		p_lBodyTentacle04a.addOrReplaceChild("lbodytentacle04b", CubeListBuilder.create().texOffs(17, 115).addBox(-0.5F, -0.5F, -4F, 1F, 1F, 4F), PartPose.offsetAndRotation(0F, 0F, -3.8F, 0.3141593F, 0F, 0F));
		PartDefinition p_lBackTentacle01a = p_bodyMid.addOrReplaceChild("lbacktentacle01a", CubeListBuilder.create().texOffs(94, 98).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 7F), PartPose.offsetAndRotation(4.7F, -8.9F, 3.4F, -0.5235988F, 0.5235988F, -0.2792527F));
		PartDefinition p_lBackTentacle01b = p_lBackTentacle01a.addOrReplaceChild("lbacktentacle01b", CubeListBuilder.create().texOffs(3, 116).addBox(-1F, -1F, 0F, 2F, 2F, 7F), PartPose.offsetAndRotation(0F, 0F, 6.3F, -0.418879F, -0.2268928F, 0F));
		p_lBackTentacle01b.addOrReplaceChild("lbacktentacle01c", CubeListBuilder.create().texOffs(16, 114).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, 6.6F, -0.1396263F, -0.2268928F, 0F));
		p_bodyMid.addOrReplaceChild("eye21", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(6.7F, -10.7F, 1.7F));
		PartDefinition p_rBodyTentacle03a = p_bodyMid.addOrReplaceChild("rbodytentacle03a", CubeListBuilder.create().texOffs(6, 119).mirror(true).addBox(-1F, -1F, -4F, 2F, 2F, 4F).mirror(false), PartPose.offsetAndRotation(-4.8F, -7.8F, -6.6F, 0.1396263F, 0.4363323F, 0F));
		p_rBodyTentacle03a.addOrReplaceChild("rbodytentacle03b", CubeListBuilder.create().texOffs(17, 114).mirror(true).addBox(-0.5F, -0.5F, -5F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -3.8F, 0.1919862F, -0.2617994F, 0F));
		PartDefinition p_lArm01a = p_bodyMid.addOrReplaceChild("larm01a", CubeListBuilder.create().texOffs(34, 96).addBox(-2.5F, -2.5F, -8.1F, 5F, 5F, 8F), PartPose.offsetAndRotation(4F, -11.9F, -2.7F, -0.2443461F, -0.3665191F, 0F));
		PartDefinition p_lArm01b = p_lArm01a.addOrReplaceChild("larm01b", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, -8F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, -7.8F, 0.0698132F, 0.0698132F, 0F));
		PartDefinition p_lArm01c = p_lArm01b.addOrReplaceChild("larm01c", CubeListBuilder.create().texOffs(93, 97).addBox(-1.5F, -1.5F, -8F, 3F, 3F, 8F), PartPose.offsetAndRotation(0F, 0F, -7F, 0.1745329F, 0.1396263F, 0F));
		p_lArm01c.addOrReplaceChild("larm01d", CubeListBuilder.create().texOffs(1, 114).addBox(-1F, -1F, -9F, 2F, 2F, 9F), PartPose.offsetAndRotation(0F, 0F, -7.2F, 0.1396263F, 0.1396263F, 0F));
		p_lArm01c.addOrReplaceChild("eye14", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-1F, -1.2F, -3.8F));
		p_lArm01a.addOrReplaceChild("eye15", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2.6F, 1.1F, -7.6F));
		p_lArm01a.addOrReplaceChild("eye13", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2.6F, -2F, -3.8F));
		PartDefinition p_rBackTentacle01a = p_bodyMid.addOrReplaceChild("rbacktentacle01a", CubeListBuilder.create().texOffs(94, 98).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 7F).mirror(false), PartPose.offsetAndRotation(-4.7F, -8.9F, 3.4F, -0.5235988F, -0.5235988F, 0.2792527F));
		PartDefinition p_rBackTentacle01b = p_rBackTentacle01a.addOrReplaceChild("rbacktentacle01b", CubeListBuilder.create().texOffs(3, 116).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 7F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 6.3F, -0.418879F, 0.2268928F, 0F));
		p_rBackTentacle01b.addOrReplaceChild("rbacktentacle01c", CubeListBuilder.create().texOffs(16, 114).mirror(true).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 6.6F, -0.1396263F, 0.2268928F, 0F));
		PartDefinition p_rArm01a = p_bodyMid.addOrReplaceChild("rarm01a", CubeListBuilder.create().texOffs(34, 96).mirror(true).addBox(-2.5F, -2.5F, -8.1F, 5F, 5F, 8F).mirror(false), PartPose.offsetAndRotation(-4F, -11.9F, -2.7F, -0.2443461F, 0.3665191F, 0F));
		PartDefinition p_rArm01b = p_rArm01a.addOrReplaceChild("rarm01b", CubeListBuilder.create().texOffs(63, 96).mirror(true).addBox(-2F, -2F, -8F, 4F, 4F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -7.8F, 0.0698132F, -0.0698132F, 0F));
		PartDefinition p_rArm01c = p_rArm01b.addOrReplaceChild("rarm01c", CubeListBuilder.create().texOffs(93, 97).mirror(true).addBox(-1.5F, -1.5F, -8F, 3F, 3F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -7F, 0.1745329F, -0.1396263F, 0F));
		p_rArm01c.addOrReplaceChild("rarm01d", CubeListBuilder.create().texOffs(1, 114).mirror(true).addBox(-1F, -1F, -9F, 2F, 2F, 9F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -7.2F, 0.1396263F, -0.1396263F, 0F));
		p_rArm01b.addOrReplaceChild("eye17", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2F, 1.1F, -8.2F));
		p_rArm01b.addOrReplaceChild("eye16", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-1.6F, -1.9F, -5.1F));
		p_rArm01a.addOrReplaceChild("eye18", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-2.6F, 1.7F, -3.4F));
		p_bodyMid.addOrReplaceChild("eye20", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-6.7F, -6.6F, 0.7F));
		PartDefinition p_rBackTentacle02a = p_bodyMid.addOrReplaceChild("rbacktentacle02a", CubeListBuilder.create().texOffs(94, 98).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 7F).mirror(false), PartPose.offsetAndRotation(-5.4F, -0.7F, 5.8F, -0.7330383F, -0.3665191F, 0F));
		PartDefinition p_rBackTentacle02b = p_rBackTentacle02a.addOrReplaceChild("rbacktentacle02b", CubeListBuilder.create().texOffs(4, 117).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 6F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 6.3F, -0.418879F, 0.0872665F, 0F));
		p_rBackTentacle02b.addOrReplaceChild("rbacktentacle02c", CubeListBuilder.create().texOffs(16, 114).mirror(true).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 5.8F, -0.1396263F, -0.1396263F, 0F));
		p_bodyMid.addOrReplaceChild("eye19", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-6.7F, -13.3F, 3.9F));
		PartDefinition p_rBodyTentacle04a = p_bodyMid.addOrReplaceChild("rbodytentacle04a", CubeListBuilder.create().texOffs(6, 119).mirror(true).addBox(-1F, -1F, -4F, 2F, 2F, 4F).mirror(false), PartPose.offsetAndRotation(-1.5F, -6F, -6.7F, 0.2792527F, 0.1047198F, 0F));
		p_rBodyTentacle04a.addOrReplaceChild("rbodytentacle04b", CubeListBuilder.create().texOffs(17, 115).mirror(true).addBox(-0.5F, -0.5F, -4F, 1F, 1F, 4F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -3.8F, 0.3141593F, 0F, 0F));
		PartDefinition p_lBodyTentacle02a = p_bodyMid.addOrReplaceChild("lbodytentacle02a", CubeListBuilder.create().texOffs(96, 100).addBox(-1.5F, -1.5F, -5F, 3F, 3F, 5F), PartPose.offsetAndRotation(3.5F, -10.4F, -6.8F, 0.0523599F, -0.1047198F, 0F));
		PartDefinition p_lBodyTentacle02b = p_lBodyTentacle02a.addOrReplaceChild("lbodytentacle02b", CubeListBuilder.create().texOffs(5, 118).addBox(-1F, -1F, -5F, 2F, 2F, 5F), PartPose.offsetAndRotation(0F, 0.3F, -4.7F, 0.1919862F, 0.0872665F, 0F));
		p_lBodyTentacle02b.addOrReplaceChild("lbodytentacle02c", CubeListBuilder.create().texOffs(17, 115).addBox(-0.5F, -0.5F, -4F, 1F, 1F, 4F), PartPose.offsetAndRotation(0F, 0F, -4.8F, 0.1047198F, 0F, 0F));
		PartDefinition p_lBackTentacle02a = p_bodyMid.addOrReplaceChild("lbacktentacle02a", CubeListBuilder.create().texOffs(95, 99).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F), PartPose.offsetAndRotation(3.5F, -14.5F, 5.9F, -0.5235988F, 0.1047198F, 0F));
		PartDefinition p_lBackTentacle02b = p_lBackTentacle02a.addOrReplaceChild("lbacktentacle02b", CubeListBuilder.create().texOffs(5, 118).addBox(-1F, -1F, 0F, 2F, 2F, 5F), PartPose.offsetAndRotation(0F, 0F, 5.2F, -0.2443461F, -0.0872665F, 0F));
		p_lBackTentacle02b.addOrReplaceChild("lbacktentacle02c", CubeListBuilder.create().texOffs(17, 115).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 4F), PartPose.offsetAndRotation(0F, 0F, 4.7F, 0.1570796F, 0.1396263F, 0F));
		PartDefinition p_lBodyTentacle03a = p_bodyMid.addOrReplaceChild("lbodytentacle03a", CubeListBuilder.create().texOffs(6, 119).addBox(-1F, -1F, -4F, 2F, 2F, 4F), PartPose.offsetAndRotation(4.8F, -7.8F, -6.6F, 0.1396263F, -0.4363323F, 0F));
		p_lBodyTentacle03a.addOrReplaceChild("lbodytentacle03b", CubeListBuilder.create().texOffs(17, 114).addBox(-0.5F, -0.5F, -5F, 1F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, -3.8F, 0.1919862F, 0.2617994F, 0F));
		PartDefinition p_lBackTentacle02a_1 = p_bodyMid.addOrReplaceChild("lbacktentacle02a_1", CubeListBuilder.create().texOffs(94, 98).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 7F), PartPose.offsetAndRotation(5.4F, -0.7F, 5.8F, -0.7330383F, 0.3665191F, 0F));
		PartDefinition p_lBackTentacle02b_1 = p_lBackTentacle02a_1.addOrReplaceChild("lbacktentacle02b_1", CubeListBuilder.create().texOffs(4, 117).addBox(-1F, -1F, 0F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 6.3F, -0.418879F, -0.0872665F, 0F));
		p_lBackTentacle02b_1.addOrReplaceChild("lbacktentacle02c_1", CubeListBuilder.create().texOffs(16, 114).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, 5.8F, -0.1396263F, 0.1396263F, 0F));
		PartDefinition p_rBodyTentacle02a = p_bodyMid.addOrReplaceChild("rbodytentacle02a", CubeListBuilder.create().texOffs(96, 100).mirror(true).addBox(-1.5F, -1.5F, -5F, 3F, 3F, 5F).mirror(false), PartPose.offsetAndRotation(-3.5F, -10.4F, -6.8F, 0.0523599F, 0.1047198F, 0F));
		PartDefinition p_rBodyTentacle02b = p_rBodyTentacle02a.addOrReplaceChild("rbodytentacle02b", CubeListBuilder.create().texOffs(5, 118).mirror(true).addBox(-1F, -1F, -5F, 2F, 2F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0.3F, -4.7F, 0.1919862F, -0.0872665F, 0F));
		p_rBodyTentacle02b.addOrReplaceChild("rbodytentacle02c", CubeListBuilder.create().texOffs(17, 115).mirror(true).addBox(-0.5F, -0.5F, -4F, 1F, 1F, 4F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -4.8F, 0.1047198F, 0F, 0F));
		PartDefinition p_rBackTentacle02a_1 = p_bodyMid.addOrReplaceChild("rbacktentacle02a_1", CubeListBuilder.create().texOffs(95, 99).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F).mirror(false), PartPose.offsetAndRotation(-3.5F, -14.5F, 5.9F, -0.5235988F, -0.1047198F, 0F));
		PartDefinition p_rBackTentacle02b_1 = p_rBackTentacle02a_1.addOrReplaceChild("rbacktentacle02b_1", CubeListBuilder.create().texOffs(5, 118).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 5.2F, -0.2443461F, 0.0872665F, 0F));
		p_rBackTentacle02b_1.addOrReplaceChild("rbacktentacle02c_1", CubeListBuilder.create().texOffs(17, 115).mirror(true).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 4F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 4.7F, 0.1570796F, -0.1396263F, 0F));
		PartDefinition p_rBodyTentacle01a = p_bodyBase.addOrReplaceChild("rbodytentacle01a", CubeListBuilder.create().texOffs(63, 96).mirror(true).addBox(-2F, -2F, -8F, 4F, 4F, 8F).mirror(false), PartPose.offsetAndRotation(-4.8F, 4.1F, 0.7F, 0.3665191F, 0.7853982F, 0F));
		PartDefinition p_rBodyTentacle01b = p_rBodyTentacle01a.addOrReplaceChild("rbodytentacle01b", CubeListBuilder.create().texOffs(93, 97).mirror(true).addBox(-1.5F, -1.5F, -8F, 3F, 3F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -7F, 0.0523599F, -0.3665191F, 0F));
		p_rBodyTentacle01b.addOrReplaceChild("rbodytentacle01c", CubeListBuilder.create().texOffs(1, 114).mirror(true).addBox(-1F, -1F, -9F, 2F, 2F, 9F).mirror(false), PartPose.offsetAndRotation(0F, 0F, -7.2F, 0.4537856F, 0.0872665F, 0F));
		PartDefinition p_rHindTentacle03a = p_bodyBase.addOrReplaceChild("rhindtentacle03a", CubeListBuilder.create().texOffs(63, 96).mirror(true).addBox(-2F, -2F, 0F, 4F, 4F, 8F).mirror(false), PartPose.offsetAndRotation(-4.2F, 10F, -3.5F, 0F, -1.0471976F, 0F));
		PartDefinition p_rHindTentacle03b = p_rHindTentacle03a.addOrReplaceChild("rhindtentacle03b", CubeListBuilder.create().texOffs(92, 96).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 7.2F, 0F, 0.5235988F, 0F));
		p_rHindTentacle03b.addOrReplaceChild("rhindtentacle03c", CubeListBuilder.create().texOffs(0, 113).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 10F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 8.6F, 0F, 0.2617994F, 0F));
		p_rHindTentacle03b.addOrReplaceChild("eye32", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-1.1F, -1.2F, 3.4F));
		p_rHindTentacle03a.addOrReplaceChild("rhindtentacle03", CubeListBuilder.create().texOffs(66, 99).mirror(true).addBox(-2F, -2F, -3.5F, 4F, 4F, 5F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7853982F, 0F));
		PartDefinition p_rHindTentacle01a = p_bodyBase.addOrReplaceChild("rhindtentacle01a", CubeListBuilder.create().texOffs(0, 96).mirror(true).addBox(-3F, -3F, 0F, 6F, 6F, 10F).mirror(false), PartPose.offsetAndRotation(-4.1F, 6.7F, 4.9F, -0.1396263F, -0.2094395F, 0F));
		PartDefinition p_rHindTentacle01b = p_rHindTentacle01a.addOrReplaceChild("rhindtentacle01b", CubeListBuilder.create().texOffs(34, 96).mirror(true).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 9.5F, 0F, 0.1396263F, 0F));
		PartDefinition p_rHindTentacle01c = p_rHindTentacle01b.addOrReplaceChild("rhindtentacle01c", CubeListBuilder.create().texOffs(63, 96).mirror(true).addBox(-2F, -2F, 0F, 4F, 4F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 7.5F, 0.1396263F, 0.0698132F, 0F));
		p_rHindTentacle01c.addOrReplaceChild("eye29", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(-1F, -2F, 0.7F));
		PartDefinition p_rHindTentacle01d = p_rHindTentacle01c.addOrReplaceChild("rhindtentacle01d", CubeListBuilder.create().texOffs(92, 96).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 7.6F, 0F, 0.0523599F, 0F));
		p_rHindTentacle01d.addOrReplaceChild("rhindtentacle01e", CubeListBuilder.create().texOffs(0, 113).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 10F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 8.9F, 0F, -0.0698132F, 0F));
		p_rHindTentacle01d.addOrReplaceChild("eye30", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-0.1F, -1.8F, 3.8F));
		PartDefinition p_lHindTentacle03a = p_bodyBase.addOrReplaceChild("lhindtentacle03a", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(4.2F, 10F, -3.5F, 0F, 1.0471976F, 0F));
		PartDefinition p_lHindTentacle03b = p_lHindTentacle03a.addOrReplaceChild("lhindtentacle03b", CubeListBuilder.create().texOffs(92, 96).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F), PartPose.offsetAndRotation(0F, 0.4F, 7.2F, 0F, -0.5235988F, 0F));
		p_lHindTentacle03b.addOrReplaceChild("lhindtentacle03c", CubeListBuilder.create().texOffs(0, 113).addBox(-1F, -1F, 0F, 2F, 2F, 10F), PartPose.offsetAndRotation(0F, 0.4F, 8.6F, 0F, -0.2617994F, 0F));
		p_lHindTentacle03a.addOrReplaceChild("lhindtentacle03", CubeListBuilder.create().texOffs(66, 99).addBox(-2F, -2F, -3.5F, 4F, 4F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7853982F, 0F));
		PartDefinition p_lHindTentacle02a = p_bodyBase.addOrReplaceChild("lhindtentacle02a", CubeListBuilder.create().texOffs(0, 96).addBox(-3F, -3F, 0F, 6F, 6F, 10F), PartPose.offsetAndRotation(4.2F, 9.5F, -1.9F, 0F, 0.5235988F, 0F));
		PartDefinition p_lHindTentacle02b = p_lHindTentacle02a.addOrReplaceChild("lhindtentacle02b", CubeListBuilder.create().texOffs(34, 96).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 9.5F, 0F, -0.1047198F, 0F));
		p_lHindTentacle02b.addOrReplaceChild("eye23", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2.1F, -2.5F, 3.5F));
		PartDefinition p_lHindTentacle02c = p_lHindTentacle02b.addOrReplaceChild("lhindtentacle02c", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 7.5F, 0F, -0.3490659F, 0F));
		PartDefinition p_lHindTentacle02d = p_lHindTentacle02c.addOrReplaceChild("lhindtentacle02d", CubeListBuilder.create().texOffs(92, 96).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F), PartPose.offsetAndRotation(0F, 0.4F, 7.6F, 0F, -0.0698132F, 0F));
		p_lHindTentacle02d.addOrReplaceChild("lhindtentacle02e", CubeListBuilder.create().texOffs(0, 113).addBox(-1F, -1F, 0F, 2F, 2F, 10F), PartPose.offsetAndRotation(0F, 0.4F, 8.9F, 0F, 0.0698132F, 0F));
		p_lHindTentacle02d.addOrReplaceChild("eye24", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(1.6F, -0.2F, 3.6F));
		PartDefinition p_rHindTentacle02a = p_bodyBase.addOrReplaceChild("rhindtentacle02a", CubeListBuilder.create().texOffs(0, 96).mirror(true).addBox(-3F, -3F, 0F, 6F, 6F, 10F).mirror(false), PartPose.offsetAndRotation(-4.2F, 9.5F, -1.9F, 0F, -0.5235988F, 0F));
		PartDefinition p_rHindTentacle02b = p_rHindTentacle02a.addOrReplaceChild("rhindtentacle02b", CubeListBuilder.create().texOffs(34, 96).mirror(true).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 9.5F, 0F, 0.1047198F, 0F));
		PartDefinition p_rHindTentacle02c = p_rHindTentacle02b.addOrReplaceChild("rhindtentacle02c", CubeListBuilder.create().texOffs(63, 96).mirror(true).addBox(-2F, -2F, 0F, 4F, 4F, 8F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 7.5F, 0F, 0.3490659F, 0F));
		p_rHindTentacle02c.addOrReplaceChild("eye31", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-1.5F, -1.8F, 3.8F));
		PartDefinition p_rHindTentacle02d = p_rHindTentacle02c.addOrReplaceChild("rhindtentacle02d", CubeListBuilder.create().texOffs(92, 96).mirror(true).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 7.6F, 0F, 0.0698132F, 0F));
		p_rHindTentacle02d.addOrReplaceChild("rhindtentacle02e", CubeListBuilder.create().texOffs(0, 113).mirror(true).addBox(-1F, -1F, 0F, 2F, 2F, 10F).mirror(false), PartPose.offsetAndRotation(0F, 0.4F, 8.9F, 0F, -0.0698132F, 0F));
		PartDefinition p_tail01a = p_bodyBase.addOrReplaceChild("tail01a", CubeListBuilder.create().texOffs(0, 96).addBox(-3F, -3F, 0F, 6F, 6F, 10F), PartPose.offsetAndRotation(0F, 3.7F, 11.3F, -0.2792527F, 0F, 0F));
		p_tail01a.addOrReplaceChild("eye28", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-0.9F, -3.3F, 3.8F));
		PartDefinition p_tail01b = p_tail01a.addOrReplaceChild("tail01b", CubeListBuilder.create().texOffs(34, 96).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 9.5F, -0.0698132F, 0F, 0F));
		PartDefinition p_tail01c = p_tail01b.addOrReplaceChild("tail01c", CubeListBuilder.create().texOffs(63, 96).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0.4F, 7.5F, 0.2792527F, 0F, 0F));
		PartDefinition p_tail01d = p_tail01c.addOrReplaceChild("tail01d", CubeListBuilder.create().texOffs(92, 96).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 9F), PartPose.offsetAndRotation(0F, 0.4F, 7.6F, 0.0872665F, 0F, 0F));
		p_tail01d.addOrReplaceChild("tail01e", CubeListBuilder.create().texOffs(0, 113).addBox(-1F, -1F, 0F, 2F, 2F, 10F), PartPose.offset(0F, 0.4F, 8.9F));
		p_tail01c.addOrReplaceChild("eye26", CubeListBuilder.create().texOffs(61, 30).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offset(1F, -2F, 3.8F));
		p_tail01b.addOrReplaceChild("eye27", CubeListBuilder.create().texOffs(75, 30).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, -2.5F, 3.8F));
		return LayerDefinition.create(mesh, 128, 128);
	}


	@Override
	public void setupAnim(EntityLesserShoggoth entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		shadowAlpha = entity.getShoggothType() == 4 ? entity.getLightLevelDependentMagicValue() : 1.0F;
		headJoint.yRot = netHeadYaw / (180F / (float)Math.PI);
		headJoint.xRot = headPitch / (180F / (float)Math.PI);
		mouth01Upper.xRot = -Mth.cos(ageInTicks * 0.25F) * 0.2F + 0.25F;
		mouth01Upper.yRot = 0.0F;
		mouth01Lower.xRot = 0.2617993877991494F + (Mth.cos(ageInTicks * 0.25F) * 0.2F + 0.25F) * 2;
		mouth01Lower.yRot = 0.0F;
		mouth02Upper.xRot = -Mth.cos(ageInTicks * 0.23F) * 0.2F + 0.25F;
		mouth02Upper.yRot = 0.0F;
		mouth02Lower.xRot = 0.2617993877991494F + (Mth.cos(ageInTicks * 0.23F) * 0.2F + 0.25F) * 2;
		mouth02Lower.yRot = 0.0F;
		mouth03Upper.xRot = -Mth.cos(ageInTicks * 0.27F) * 0.2F + 0.25F;
		mouth03Upper.yRot = 0.0F;
		mouth03Lower.xRot = 0.2617993877991494F + (Mth.cos(ageInTicks * 0.27F) * 0.2F + 0.25F) * 2;
		mouth03Lower.yRot = 0.0F;
		float animation = Mth.sin((limbSwing * 0.4F + 2) * 1.5F) * 0.3F * limbSwingAmount * 0.3F;
		float flap = Mth.sin(entity.tickCount * 0.2F) * 0.3F;
		float flap2 = Mth.cos(entity.tickCount * 0.2F) * 0.4F;
		lHindTentacle01a.yRot = flap *0.05F + 0.1f + animation * 0.4f;// + 0.20943951023931953F
		lHindTentacle01b.yRot = lHindTentacle01a.yRot * 1.5F - 0.1f;
		lHindTentacle01c.yRot = lHindTentacle01b.yRot * 1.75F - 0.1f;
		lHindTentacle01d.yRot = lHindTentacle01c.yRot * 2F;
		lHindTentacle01e.yRot = lHindTentacle01d.yRot * 2.25F;
		lHindTentacle02a.yRot = flap *0.05F + 0.3f + animation * 0.4f;// + 0.20943951023931953F
		lHindTentacle02b.yRot = lHindTentacle02a.yRot * 1.5F - 0.35f;
		lHindTentacle02c.yRot = lHindTentacle02b.yRot * 1.65F - 0.2f;
		lHindTentacle02d.yRot = lHindTentacle02c.yRot * 1.8F - 0.1f;
		lHindTentacle02e.yRot = lHindTentacle02d.yRot * 2.05F;
		lHindTentacle03a.yRot = flap *0.05F + 0.9f + animation * 0.4f;// + 0.20943951023931953F
		lHindTentacle03b.yRot = lHindTentacle03a.yRot * 1.5F - 1.7f;
		lHindTentacle03c.yRot = lHindTentacle03b.yRot * 1.75F + 0.3f;
		rHindTentacle01a.yRot = flap *0.05F - 0.1f + animation * 0.4f;// + 0.20943951023931953F
		rHindTentacle01b.yRot = rHindTentacle01a.yRot * 1.5F + 0.1f;
		rHindTentacle01c.yRot = rHindTentacle01b.yRot * 1.75F + 0.1f;
		rHindTentacle01d.yRot = rHindTentacle01c.yRot * 2F;
		rHindTentacle01e.yRot = rHindTentacle01d.yRot * 2.25F;
		rHindTentacle02a.yRot = flap *0.05F - 0.3f + animation * 0.4f;// + 0.20943951023931953F
		rHindTentacle02b.yRot = rHindTentacle02a.yRot * 1.5F + 0.35f;
		rHindTentacle02c.yRot = rHindTentacle02b.yRot * 1.65F + 0.2f;
		rHindTentacle02d.yRot = rHindTentacle02c.yRot * 1.8F + 0.1f;
		rHindTentacle02e.yRot = rHindTentacle02d.yRot * 2.05F;
		rHindTentacle03a.yRot = flap *0.05F - 0.9f + animation * 0.4f;// + 0.20943951023931953F
		rHindTentacle03b.yRot = rHindTentacle03a.yRot * 1.5F + 1.7f;
		rHindTentacle03c.yRot = rHindTentacle03b.yRot * 1.75F - 0.3f;
		tail01a.yRot = flap *0.1F + animation * 0.4f;// + 0.20943951023931953F
		tail01b.yRot = tail01a.yRot * 1.5F;
		tail01c.yRot = tail01b.yRot * 1.75F;
		tail01d.yRot = tail01c.yRot * 2F;
		tail01e.yRot = tail01d.yRot * 2.25F;
		lBackTentacle01a.yRot = flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		lBackTentacle01b.yRot = lBackTentacle01a.yRot * 1.5F;
		lBackTentacle01c.yRot = lBackTentacle01b.yRot * 1.75F;
		lBackTentacle01a.xRot = -0.13962634015954636F - flap2 * 0.5F;
		lBackTentacle01b.xRot = -0.41887902047863906F - flap2 * 0.75F;
		lBackTentacle01c.xRot = -0.13962634015954636F - flap2 * 1F;
		lBackTentacle02a.yRot = flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		lBackTentacle02b.yRot = lBackTentacle02a.yRot * 1.5F;
		lBackTentacle02c.yRot = lBackTentacle02b.yRot * 1.75F;
		lBackTentacle02a.xRot = -0.5235987755982988F - flap2 * 0.5F;
		lBackTentacle02b.xRot = -0.24434609527920614F - flap2 * 0.75F;
		lBackTentacle02c.xRot = 0.15707963267948966F - flap2 * 1F;
		lBackTentacle02a_1.yRot = flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		lBackTentacle02b_1.yRot = lBackTentacle02a_1.yRot * 1.5F;
		lBackTentacle02c_1.yRot = lBackTentacle02b_1.yRot * 1.75F;
		lBackTentacle02a_1.xRot = -0.7330382858376184F - flap2 * 0.5F;
		lBackTentacle02b_1.xRot = -0.41887902047863906F - flap2 * 0.75F;
		lBackTentacle02c_1.xRot = -0.13962634015954636F - flap2 * 1F;
		rBackTentacle01a.yRot = -flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		rBackTentacle01b.yRot = rBackTentacle01a.yRot * 1.5F;
		rBackTentacle01c.yRot = rBackTentacle01b.yRot * 1.75F;
		rBackTentacle01a.xRot = -0.13962634015954636F - flap2 * 0.5F;
		rBackTentacle01b.xRot = -0.41887902047863906F - flap2 * 0.75F;
		rBackTentacle01c.xRot = -0.13962634015954636F - flap2 * 1F;
		rBackTentacle02a.yRot = -flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		rBackTentacle02b.yRot = rBackTentacle02a.yRot * 1.5F;
		rBackTentacle02c.yRot = rBackTentacle02b.yRot * 1.75F;
		rBackTentacle02a.xRot = -0.7330382858376184F - flap2 * 0.5F;
		rBackTentacle02b.xRot = -0.41887902047863906F - flap2 * 0.75F;
		rBackTentacle02c.xRot = -0.13962634015954636F - flap2 * 1F;
		rBackTentacle02a_1.yRot = -flap *0.2F + animation * 0.5f;// + 0.5235987755982988F
		rBackTentacle02b_1.yRot = rBackTentacle02a_1.yRot * 1.5F;
		rBackTentacle02c_1.yRot = rBackTentacle02b_1.yRot * 1.75F;
		rBackTentacle02a_1.xRot = -0.5235987755982988F - flap2 * 0.5F;
		rBackTentacle02b_1.xRot = -0.24434609527920614F - flap2 * 0.75F;
		rBackTentacle02c_1.xRot = 0.15707963267948966F - flap2 * 1F;
		rArm01a.xRot = -0.24434609527920614F + Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		lArm01a.xRot = -0.24434609527920614F + Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		lArm01a.yRot = flap *0.1F - 0.3f;// + 0.20943951023931953F
		lArm01b.yRot = lArm01a.yRot * 1.5F + 0.36f;
		lArm01c.yRot = lArm01b.yRot * 1.75F + 0.2f;
		lArm01d.yRot = lArm01c.yRot * 2F + 0.1f;
		rArm01a.yRot = -flap *0.1F + 0.3f;// + 0.20943951023931953F
		rArm01b.yRot = rArm01a.yRot * 1.5F - 0.36f;
		rArm01c.yRot = rArm01b.yRot * 1.75F - 0.2f;
		rArm01d.yRot = rArm01c.yRot * 2F - 0.1f;
		rArm01a.zRot = 0.0F;
		lArm01a.zRot = 0.0F;
		rBodyTentacle01a.xRot = 0.3665191429188092F + Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		lBodyTentacle01a.xRot = 0.3665191429188092F + Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rBodyTentacle01a.yRot = -flap *0.1F + 0.78f;// + 0.20943951023931953F
		rBodyTentacle01b.yRot = rBodyTentacle01a.yRot * 1.5F - 1.26f;
		rBodyTentacle01c.yRot = rBodyTentacle01b.yRot * 1.75F + 0.1f;
		lBodyTentacle01a.yRot = flap *0.1F - 0.78f;// + 0.20943951023931953F
		lBodyTentacle01b.yRot = lBodyTentacle01a.yRot * 1.5F + 1.26f;
		lBodyTentacle01c.yRot = lBodyTentacle01b.yRot * 1.75F - 0.1f;
		rBodyTentacle01a.zRot = 0.0F;
		lBodyTentacle01a.zRot = 0.0F;
		lBodyTentacle02a.yRot = flap *0.2F + animation;// + 0.20943951023931953F
		lBodyTentacle02b.yRot = lBodyTentacle02a.yRot * 1.5F;
		lBodyTentacle02c.yRot = lBodyTentacle02b.yRot * 1.75F;
		lBodyTentacle03a.yRot = -flap *0.3F - 0.4f + 0.05f + animation;// + 0.20943951023931953F
		lBodyTentacle03b.yRot = lBodyTentacle03a.yRot * 1.5F + 0.8f;
		lBodyTentacle04a.yRot = -flap *0.4F - 0.08f + animation;// + 0.20943951023931953F
		lBodyTentacle04b.yRot = lBodyTentacle04a.yRot * 1.5F;
		rBodyTentacle02a.yRot = -flap *0.2F + animation;// + 0.20943951023931953F
		rBodyTentacle02b.yRot = rBodyTentacle02a.yRot * 1.5F;
		rBodyTentacle02c.yRot = rBodyTentacle02b.yRot * 1.75F;
		rBodyTentacle03a.yRot = flap *0.3F + 0.4f - 0.07f + animation;// + 0.20943951023931953F
		rBodyTentacle03b.yRot = rBodyTentacle03a.yRot * 1.5F - 0.8f;
		rBodyTentacle04a.yRot = flap *0.4F + 0.03f + animation;// + 0.20943951023931953F
		rBodyTentacle04b.yRot = rBodyTentacle04a.yRot * 1.5F;
		float f6;
		float f7;
		if (attackTime > -9990.0F)
		{
			f6 = attackTime;
			f6 = 1.0F - attackTime;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			f7 = Mth.sin(f6 * (float)Math.PI);
			float f8 = Mth.sin(attackTime * (float)Math.PI) * -(headJoint.xRot - 0.7F) * 0.75F;
			rArm01a.xRot = (float)(rArm01a.xRot + (f7 * 1.2D +  f8));
			rBodyTentacle01a.xRot = (float)(rBodyTentacle01a.xRot + (f7 * 1.2D + f8));
			rArm01a.zRot = Mth.sin(attackTime * (float)Math.PI) * -0.4F;
			rBodyTentacle01a.zRot = Mth.sin(attackTime * (float)Math.PI) * -0.4F;
			lArm01a.xRot = (float)(lArm01a.xRot + (f7 * 1.2D + f8));
			lBodyTentacle01a.xRot = (float)(lBodyTentacle01a.xRot + (f7 * 1.2D + f8));
			lArm01a.zRot = Mth.sin(attackTime * (float)Math.PI) * 0.4F;
			lBodyTentacle01a.zRot = Mth.sin(attackTime * (float)Math.PI) * 0.4F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		if(young) {
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(0.0F, 0.5F, 0.5F);
		} else {
			poseStack.scale(1.5F, 1.5F, 1.5F);
			poseStack.translate(0.0F, -0.5F, 0.5F);
		}
		headJoint.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha * shadowAlpha);
		bodyBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha * shadowAlpha);
		poseStack.popPose();
	}
}
