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
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelRemnant<T extends Entity> extends EntityModel<T> implements HeadedModel {

	public final ModelPart mask1;
	public final ModelPart mask2;
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart leg1;
	public final ModelPart leg11;
	public final ModelPart leg12;
	public final ModelPart leg13;
	public final ModelPart leg14;
	public final ModelPart leg2;
	public final ModelPart leg21;
	public final ModelPart leg22;
	public final ModelPart leg23;
	public final ModelPart leg24;
	public final ModelPart leg3;
	public final ModelPart leg31;
	public final ModelPart leg32;
	public final ModelPart leg33;
	public final ModelPart leg34;
	public final ModelPart leg4;
	public final ModelPart leg41;
	public final ModelPart leg42;
	public final ModelPart leg43;
	public final ModelPart leg44;
	public final ModelPart leftarm;
	public final ModelPart tentacle1;
	public final ModelPart tentacle2;
	public final ModelPart tentacle3;
	public final ModelPart tentacle4;
	public final ModelPart rightarm;

	public ModelRemnant(ModelPart root) {
		mask1 = root.getChild("head").getChild("mask1");
		mask2 = root.getChild("head").getChild("mask2");
		head = root.getChild("head");
		body = root.getChild("body");
		leg1 = root.getChild("leg1");
		leg11 = root.getChild("leg1").getChild("leg11");
		leg12 = root.getChild("leg1").getChild("leg11").getChild("leg12");
		leg13 = root.getChild("leg1").getChild("leg11").getChild("leg12").getChild("leg13");
		leg14 = root.getChild("leg1").getChild("leg11").getChild("leg12").getChild("leg13").getChild("leg14");
		leg2 = root.getChild("leg2");
		leg21 = root.getChild("leg2").getChild("leg21");
		leg22 = root.getChild("leg2").getChild("leg21").getChild("leg22");
		leg23 = root.getChild("leg2").getChild("leg21").getChild("leg22").getChild("leg23");
		leg24 = root.getChild("leg2").getChild("leg21").getChild("leg22").getChild("leg23").getChild("leg24");
		leg3 = root.getChild("leg3");
		leg31 = root.getChild("leg3").getChild("leg31");
		leg32 = root.getChild("leg3").getChild("leg31").getChild("leg32");
		leg33 = root.getChild("leg3").getChild("leg31").getChild("leg32").getChild("leg33");
		leg34 = root.getChild("leg3").getChild("leg31").getChild("leg32").getChild("leg33").getChild("leg34");
		leg4 = root.getChild("leg4");
		leg41 = root.getChild("leg4").getChild("leg41");
		leg42 = root.getChild("leg4").getChild("leg41").getChild("leg42");
		leg43 = root.getChild("leg4").getChild("leg41").getChild("leg42").getChild("leg43");
		leg44 = root.getChild("leg4").getChild("leg41").getChild("leg42").getChild("leg43").getChild("leg44");
		leftarm = root.getChild("leftarm");
		tentacle1 = root.getChild("leftarm").getChild("tentacle1");
		tentacle2 = root.getChild("leftarm").getChild("tentacle2");
		tentacle3 = root.getChild("leftarm").getChild("tentacle3");
		tentacle4 = root.getChild("leftarm").getChild("tentacle4");
		rightarm = root.getChild("rightarm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F), PartPose.offset(0F, 0F, 0F));
		p_head.addOrReplaceChild("mask1", CubeListBuilder.create().texOffs(32, 0).addBox(-3.4F, -8F, -6F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.5235988F, 0F));
		p_head.addOrReplaceChild("mask2", CubeListBuilder.create().texOffs(32, 0).addBox(-2.6F, -8F, -6F, 6F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.5235988F, 0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 22F, 4F), PartPose.offset(0F, 0F, 0F));
		PartDefinition p_leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 42).addBox(0F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(1F, 23F, 0F));
		PartDefinition p_leg11 = p_leg1.addOrReplaceChild("leg11", CubeListBuilder.create().texOffs(0, 42).addBox(0F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2F, 0F, 0F));
		PartDefinition p_leg12 = p_leg11.addOrReplaceChild("leg12", CubeListBuilder.create().texOffs(0, 42).addBox(0F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2F, 0F, 0F));
		PartDefinition p_leg13 = p_leg12.addOrReplaceChild("leg13", CubeListBuilder.create().texOffs(0, 42).addBox(0F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2F, 0F, 0F));
		p_leg13.addOrReplaceChild("leg14", CubeListBuilder.create().texOffs(0, 42).addBox(0F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(2F, 0F, 0F));
		PartDefinition p_leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-1F, 23F, 0F));
		PartDefinition p_leg21 = p_leg2.addOrReplaceChild("leg21", CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, 0F, 0F));
		PartDefinition p_leg22 = p_leg21.addOrReplaceChild("leg22", CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, 0F, 0F));
		PartDefinition p_leg23 = p_leg22.addOrReplaceChild("leg23", CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, 0F, 0F));
		p_leg23.addOrReplaceChild("leg24", CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -1F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, 0F, 0F));
		PartDefinition p_leg3 = root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, 0F, 2F, 2F, 2F), PartPose.offset(0F, 23F, 1F));
		PartDefinition p_leg31 = p_leg3.addOrReplaceChild("leg31", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, 0F, 2F, 2F, 2F), PartPose.offset(0F, 0F, 2F));
		PartDefinition p_leg32 = p_leg31.addOrReplaceChild("leg32", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, 0F, 2F, 2F, 2F), PartPose.offset(0F, 0F, 2F));
		PartDefinition p_leg33 = p_leg32.addOrReplaceChild("leg33", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, 0F, 2F, 2F, 2F), PartPose.offset(0F, 0F, 2F));
		p_leg33.addOrReplaceChild("leg34", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, 0F, 2F, 2F, 2F), PartPose.offset(0F, 0F, 2F));
		PartDefinition p_leg4 = root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 23F, -1F));
		PartDefinition p_leg41 = p_leg4.addOrReplaceChild("leg41", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		PartDefinition p_leg42 = p_leg41.addOrReplaceChild("leg42", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		PartDefinition p_leg43 = p_leg42.addOrReplaceChild("leg43", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		p_leg43.addOrReplaceChild("leg44", CubeListBuilder.create().texOffs(2, 42).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		PartDefinition p_leftarm = root.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(0, 20).addBox(0F, -2F, -2F, 4F, 10F, 4F), PartPose.offset(4F, 2F, 0F));
		p_leftarm.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 8F, 1F));
		p_leftarm.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 8F, 1F));
		p_leftarm.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(1F, 8F, -1F));
		p_leftarm.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offset(3F, 8F, -1F));
		root.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(0, 20).addBox(-4F, -2F, -2F, 4F, 16F, 4F), PartPose.offset(-4F, 2F, 0F));
		return LayerDefinition.create(mesh, 128, 64);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
		head.xRot = headPitch * Mth.DEG_TO_RAD;

		rightarm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftarm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightarm.zRot = 0.0F;
		leftarm.zRot = 0.0F;

		// tentacles sway at a speed that depends on the entity id
		float speed = 0.03F * (entity.getId() % 10);
		float ticks = entity.tickCount;
		ModelPart[] tentacles = {tentacle1, tentacle2, tentacle3, tentacle4};
		for(int i = 0; i < 4; i++) {
			boolean cos = i == 0 || i == 3;
			tentacles[i].xRot = (cos ? Mth.cos(ticks * speed) : Mth.sin(ticks * speed)) * 10.5F * Mth.DEG_TO_RAD;
			tentacles[i].yRot = 0.0F;
			tentacles[i].zRot = (cos ? Mth.sin(ticks * speed) : Mth.cos(ticks * speed)) * 6.5F * Mth.DEG_TO_RAD;
		}

		ModelPart[][] legs = {{leg1, leg11, leg12, leg13, leg14}, {leg2, leg21, leg22, leg23, leg24}, {leg3, leg31, leg32, leg33, leg34}, {leg4, leg41, leg42, leg43, leg44}};
		for(int l = 0; l < 4; l++) {
			float swing = Mth.cos(limbSwing * 0.6662F + (l < 2 ? 0.0F : Mth.PI));
			for(int i = 0; i < 5; i++)
				legs[l][i].yRot = (i == 0 ? 0.6662F : 0.0F) + swing * (2.0F - 0.1F * i) * limbSwingAmount * 0.5F;
		}

		if(riding) {
			rightarm.xRot += -(Mth.PI / 5F);
			leftarm.xRot += -(Mth.PI / 5F);
			leg1.yRot = Mth.PI / 10F;
			leg2.yRot = -(Mth.PI / 10F);
			leg3.yRot = Mth.PI / 10F;
			leg4.yRot = -(Mth.PI / 10F);
		}

		rightarm.yRot = 0.0F;
		leftarm.yRot = 0.0F;

		// attack swing (1.12.2 swingProgress was never below -9990, so this always ran)
		body.yRot = Mth.sin(Mth.sqrt(attackTime) * Mth.TWO_PI) * 0.2F;
		rightarm.yRot += body.yRot;
		leftarm.yRot += body.yRot;
		float f6 = 1.0F - attackTime;
		f6 *= f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		float f7 = Mth.sin(f6 * Mth.PI);
		float f8 = Mth.sin(attackTime * Mth.PI) * -(head.xRot - 0.7F) * 0.75F;
		rightarm.xRot = (float)(rightarm.xRot - (f7 * 1.2D + f8));
		rightarm.yRot += body.yRot * 2.0F;
		rightarm.zRot = Mth.sin(attackTime * Mth.PI) * -0.4F;
		leftarm.xRot = (float)(leftarm.xRot - (f7 * 1.2D + f8));
		leftarm.yRot += body.yRot * -2.0F;
		leftarm.zRot = Mth.sin(attackTime * Mth.PI) * 0.4F;
	}

	@Override
	public ModelPart getHead() {
		return head;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leg4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
