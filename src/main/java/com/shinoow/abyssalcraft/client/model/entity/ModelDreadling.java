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

public class ModelDreadling<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart rightarm;
	public final ModelPart leftarm;
	public final ModelPart rightleg;
	public final ModelPart leftleg;

	public ModelDreadling(ModelPart root) {
		head = root.getChild("head");
		body = root.getChild("body");
		rightarm = root.getChild("rightarm");
		leftarm = root.getChild("leftarm");
		rightleg = root.getChild("rightleg");
		leftleg = root.getChild("leftleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F), PartPose.offset(0F, 10F, 0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F), PartPose.offset(0F, 10F, 0F));
		root.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(40, 16).addBox(-3F, -2F, -2F, 4F, 7F, 4F), PartPose.offset(-5F, 12F, 0F));
		root.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(40, 16).addBox(-1F, -2F, -2F, 4F, 11F, 4F), PartPose.offsetAndRotation(5F, 12F, 0F, -0.3717861F, 0F, 0F));
		root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(0, 16).addBox(-2F, 0F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-2F, 21F, 0F, 2.119181F, 0F, 0F));
		root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(0, 16).addBox(-2F, 0F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(2F, 21F, 0F, 1.524323F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw / (180F / (float)Math.PI);
		head.xRot = headPitch / (180F / (float)Math.PI);
		rightarm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftarm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightarm.zRot = 0.0F;
		leftarm.zRot = 0.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftarm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}
