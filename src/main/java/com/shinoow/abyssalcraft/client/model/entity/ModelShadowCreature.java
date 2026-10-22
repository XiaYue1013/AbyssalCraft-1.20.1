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

public class ModelShadowCreature<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart Body;
	public final ModelPart Head1;
	public final ModelPart Tail1;
	public final ModelPart Tail2;
	public final ModelPart Tail3;
	public final ModelPart Tail4;
	public final ModelPart Tail5;
	public final ModelPart Tail6;
	public final ModelPart Tail7;
	public final ModelPart LeftArm1;
	public final ModelPart RightArm1;
	public final ModelPart LeftArm2;
	public final ModelPart RightArm2;

	public ModelShadowCreature(ModelPart root) {
		Body = root.getChild("body");
		Head1 = root.getChild("head1");
		Tail1 = root.getChild("tail1");
		Tail2 = root.getChild("tail2");
		Tail3 = root.getChild("tail3");
		Tail4 = root.getChild("tail4");
		Tail5 = root.getChild("tail5");
		Tail6 = root.getChild("tail6");
		Tail7 = root.getChild("tail7");
		LeftArm1 = root.getChild("leftarm1");
		RightArm1 = root.getChild("rightarm1");
		LeftArm2 = root.getChild("leftarm2");
		RightArm2 = root.getChild("rightarm2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 22).addBox(0F, 0F, 0F, 3F, 3F, 7F), PartPose.offset(-3F, 12F, -1F));
		root.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -5F, -3F, 5F, 5F, 5F), PartPose.offset(-1F, 12F, 0F));
		root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(18, 15).addBox(0F, 0F, 0F, 2F, 2F, 5F), PartPose.offsetAndRotation(-2.466667F, 12.5F, 6F, -0.3717861F, 0F, 0F));
		root.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(26, 12).addBox(0F, 0F, 0F, 1F, 1F, 2F), PartPose.offsetAndRotation(-2F, 14F, 9F, 1.115358F, 0F, 0F));
		root.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(23, 7).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offsetAndRotation(-1F, 14.5F, 9F, -0.4089647F, 0F, 0F));
		root.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(23, 7).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offsetAndRotation(-1F, 13.5F, 7F, -0.4089647F, 0F, 0F));
		root.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(23, 7).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offsetAndRotation(-5F, 14.5F, 9F, -0.4089647F, 0F, 0F));
		root.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(23, 7).addBox(0F, 0F, 0F, 3F, 1F, 1F), PartPose.offsetAndRotation(-5F, 13.5F, 7F, -0.4089647F, 0F, 0F));
		root.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(26, 12).addBox(0F, 0F, 0F, 1F, 1F, 2F), PartPose.offsetAndRotation(-2F, 13F, 7F, 1.115358F, 0F, 0F));
		root.addOrReplaceChild("leftarm1", CubeListBuilder.create().texOffs(11, 19).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(0F, 13F, 0F));
		root.addOrReplaceChild("rightarm1", CubeListBuilder.create().texOffs(11, 19).addBox(0F, 0F, 0F, 2F, 1F, 1F), PartPose.offset(-5F, 13F, 0F));
		root.addOrReplaceChild("leftarm2", CubeListBuilder.create().texOffs(0, 22).addBox(0F, 0F, -5F, 1F, 1F, 5F), PartPose.offset(1F, 13F, 0F));
		root.addOrReplaceChild("rightarm2", CubeListBuilder.create().texOffs(0, 22).addBox(0F, 0F, -5F, 1F, 1F, 5F), PartPose.offset(-5F, 13F, 0F));
		return LayerDefinition.create(mesh, 32, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		Head1.yRot = netHeadYaw / (180F / (float)Math.PI);
		Head1.xRot = headPitch / (180F / (float)Math.PI);
		RightArm2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		LeftArm2.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		RightArm2.zRot = 0.0F;
		LeftArm2.zRot = 0.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Head1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail6.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail7.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return Head1;
	}
}
