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

public class ModelDreadSpawn<T extends Entity> extends EntityModel<T> {

	public final ModelPart body;
	public final ModelPart head;
	public final ModelPart jaw;
	public final ModelPart thing;
	public final ModelPart t1;
	public final ModelPart t11;
	public final ModelPart t12;
	public final ModelPart t13;
	public final ModelPart t2;
	public final ModelPart t21;
	public final ModelPart t22;
	public final ModelPart t23;
	public final ModelPart t3;
	public final ModelPart t31;
	public final ModelPart t32;
	public final ModelPart t33;
	public final ModelPart arm;
	public final ModelPart arm1;
	public final ModelPart arm2;
	public final ModelPart arm3;

	public ModelDreadSpawn(ModelPart root) {
		body = root.getChild("body");
		head = root.getChild("head");
		jaw = root.getChild("head").getChild("jaw");
		thing = root.getChild("thing");
		t1 = root.getChild("thing").getChild("t1");
		t11 = root.getChild("thing").getChild("t1").getChild("t11");
		t12 = root.getChild("thing").getChild("t1").getChild("t11").getChild("t12");
		t13 = root.getChild("thing").getChild("t1").getChild("t11").getChild("t12").getChild("t13");
		t2 = root.getChild("thing").getChild("t2");
		t21 = root.getChild("thing").getChild("t2").getChild("t21");
		t22 = root.getChild("thing").getChild("t2").getChild("t21").getChild("t22");
		t23 = root.getChild("thing").getChild("t2").getChild("t21").getChild("t22").getChild("t23");
		t3 = root.getChild("thing").getChild("t3");
		t31 = root.getChild("thing").getChild("t3").getChild("t31");
		t32 = root.getChild("thing").getChild("t3").getChild("t31").getChild("t32");
		t33 = root.getChild("thing").getChild("t3").getChild("t31").getChild("t32").getChild("t33");
		arm = root.getChild("arm");
		arm1 = root.getChild("arm").getChild("arm1");
		arm2 = root.getChild("arm").getChild("arm1").getChild("arm2");
		arm3 = root.getChild("arm").getChild("arm1").getChild("arm2").getChild("arm3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-3F, -3F, -3F, 6F, 5F, 6F), PartPose.offset(0F, 22F, 0F));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 4).addBox(-1.5F, -4F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(0F, 19F, 0F, -0.4833219F, 0F, 0F));
		p_head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(12, 4).addBox(-1.5F, -1F, -1.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.4833219F, 0F, 0F));
		PartDefinition p_thing = root.addOrReplaceChild("thing", CubeListBuilder.create().texOffs(0, 10).addBox(0F, -1F, -3F, 6F, 1F, 6F), PartPose.offset(3F, 21F, 2F));
		PartDefinition p_t1 = p_thing.addOrReplaceChild("t1", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offsetAndRotation(4F, -1F, 2F, 0F, 0.3F, 0F));
		PartDefinition p_t11 = p_t1.addOrReplaceChild("t11", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_t12 = p_t11.addOrReplaceChild("t12", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		p_t12.addOrReplaceChild("t13", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_t2 = p_thing.addOrReplaceChild("t2", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offsetAndRotation(4F, -1F, 4F, 0F, 0.7F, 0F));
		PartDefinition p_t21 = p_t2.addOrReplaceChild("t21", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_t22 = p_t21.addOrReplaceChild("t22", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		p_t22.addOrReplaceChild("t23", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_t3 = p_thing.addOrReplaceChild("t3", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offsetAndRotation(1F, -1F, 0F, 0F, -0.3F, 0F));
		PartDefinition p_t31 = p_t3.addOrReplaceChild("t31", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_t32 = p_t31.addOrReplaceChild("t32", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		p_t32.addOrReplaceChild("t33", CubeListBuilder.create().texOffs(10, 5).addBox(0F, -1F, -3F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 0F));
		PartDefinition p_arm = root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -1F, -3F, 2F, 2F, 2F), PartPose.offset(-2F, 22F, 0F));
		PartDefinition p_arm1 = p_arm.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -1F, -3F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		PartDefinition p_arm2 = p_arm1.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(8, 0).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -3F));
		p_arm2.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(16, 0).addBox(-1F, -1F, -2F, 2F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
		return LayerDefinition.create(mesh, 32, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		arm.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		float f15 = 0.02F * (entity.getId() % 10);
		arm1.yRot = Mth.sin(entity.tickCount * f15) * 4.5F * (float)Math.PI / 180.0F;
		arm2.yRot = Mth.sin(entity.tickCount * f15) * 4.5F * (float)Math.PI / 180.0F;
		arm3.yRot = Mth.sin(entity.tickCount * f15) * 4.5F * (float)Math.PI / 180.0F;
		thing.zRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		float f16 = 0.03F;
		t1.zRot = Mth.cos(entity.tickCount * f16) * 4.25F * (float)Math.PI / 180.0F;
		t11.zRot = Mth.cos(entity.tickCount * f16) * 4.25F * (float)Math.PI / 180.0F;
		t12.zRot = Mth.cos(entity.tickCount * f16) * 4.25F * (float)Math.PI / 180.0F;
		t13.zRot = Mth.cos(entity.tickCount * f16) * 4.25F * (float)Math.PI / 180.0F;
		float f17 = 0.04F;
		t2.zRot = Mth.cos(entity.tickCount * f17) * 4.25F * (float)Math.PI / 180.0F;
		t21.zRot = Mth.cos(entity.tickCount * f17) * 4.25F * (float)Math.PI / 180.0F;
		t22.zRot = Mth.cos(entity.tickCount * f17) * 4.25F * (float)Math.PI / 180.0F;
		t23.zRot = Mth.cos(entity.tickCount * f17) * 4.25F * (float)Math.PI / 180.0F;
		float f18 = -0.04F;
		t3.zRot = Mth.cos(entity.tickCount * f18) * 4.25F * (float)Math.PI / 180.0F;
		t31.zRot = Mth.cos(entity.tickCount * f18) * 4.25F * (float)Math.PI / 180.0F;
		t32.zRot = Mth.cos(entity.tickCount * f18) * 4.25F * (float)Math.PI / 180.0F;
		t33.zRot = Mth.cos(entity.tickCount * f18) * 4.25F * (float)Math.PI / 180.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		thing.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
