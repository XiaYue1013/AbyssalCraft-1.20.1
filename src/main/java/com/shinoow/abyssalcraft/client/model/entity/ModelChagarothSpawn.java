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

public class ModelChagarothSpawn<T extends Entity> extends EntityModel<T> {

	public final ModelPart body;
	public final ModelPart smallspike1;
	public final ModelPart smallspike2;
	public final ModelPart smallspike3;
	public final ModelPart smallspike4;
	public final ModelPart bigspike1;
	public final ModelPart bigspike2;
	public final ModelPart bigspike3;
	public final ModelPart bigspike4;
	public final ModelPart leftleg1;
	public final ModelPart leftleg2;
	public final ModelPart leftleg3;
	public final ModelPart backleg1;
	public final ModelPart backleg2;
	public final ModelPart backleg3;
	public final ModelPart frontleg1;
	public final ModelPart frontleg2;
	public final ModelPart frontleg3;
	public final ModelPart rightleg1;
	public final ModelPart rightleg2;
	public final ModelPart rightleg3;

	public ModelChagarothSpawn(ModelPart root) {
		body = root.getChild("body");
		smallspike1 = root.getChild("smallspike1");
		smallspike2 = root.getChild("smallspike2");
		smallspike3 = root.getChild("smallspike3");
		smallspike4 = root.getChild("smallspike4");
		bigspike1 = root.getChild("bigspike1");
		bigspike2 = root.getChild("bigspike2");
		bigspike3 = root.getChild("bigspike3");
		bigspike4 = root.getChild("bigspike4");
		leftleg1 = root.getChild("leftleg1");
		leftleg2 = root.getChild("leftleg2");
		leftleg3 = root.getChild("leftleg3");
		backleg1 = root.getChild("backleg1");
		backleg2 = root.getChild("backleg2");
		backleg3 = root.getChild("backleg3");
		frontleg1 = root.getChild("frontleg1");
		frontleg2 = root.getChild("frontleg2");
		frontleg3 = root.getChild("frontleg3");
		rightleg1 = root.getChild("rightleg1");
		rightleg2 = root.getChild("rightleg2");
		rightleg3 = root.getChild("rightleg3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 7).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offset(0F, 21F, 0F));
		root.addOrReplaceChild("smallspike1", CubeListBuilder.create().texOffs(18, 1).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(2F, 18F, -3F, 0.3346075F, -0.7435722F, 0F));
		root.addOrReplaceChild("smallspike2", CubeListBuilder.create().texOffs(18, 1).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(-3F, 18F, -2F, 0.3346145F, 0.8922867F, 0F));
		root.addOrReplaceChild("smallspike3", CubeListBuilder.create().texOffs(18, 1).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(-2F, 18F, 3F, 0.3346075F, 2.41661F, 0F));
		root.addOrReplaceChild("smallspike4", CubeListBuilder.create().texOffs(18, 1).addBox(0F, -4F, 0F, 1F, 5F, 1F), PartPose.offsetAndRotation(3F, 18F, 2F, 0.3346075F, -2.379431F, 0F));
		root.addOrReplaceChild("bigspike1", CubeListBuilder.create().texOffs(12, 0).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(0F, 21F, 2F, -0.8922867F, 0F, 0F));
		root.addOrReplaceChild("bigspike2", CubeListBuilder.create().texOffs(12, 0).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(0F, 21F, -2F, 0.8922821F, 0F, 0F));
		root.addOrReplaceChild("bigspike3", CubeListBuilder.create().texOffs(12, 0).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(-2F, 21F, 0F, 0F, 0F, -0.8922821F));
		root.addOrReplaceChild("bigspike4", CubeListBuilder.create().texOffs(12, 0).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(2F, 21F, 0F, 0F, 0F, 0.8922821F));
		root.addOrReplaceChild("leftleg1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(3F, 23F, -2.5F));
		root.addOrReplaceChild("leftleg2", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(3F, 23F, -0.5F));
		root.addOrReplaceChild("leftleg3", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(3F, 23F, 1.5F));
		root.addOrReplaceChild("backleg1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 1F, 5F), PartPose.offset(1.5F, 23F, 3F));
		root.addOrReplaceChild("backleg2", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 1F, 5F), PartPose.offset(-0.5F, 23F, 3F));
		root.addOrReplaceChild("backleg3", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 1F, 5F), PartPose.offset(-2.5F, 23F, 3F));
		root.addOrReplaceChild("frontleg1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, -5F, 1F, 1F, 5F), PartPose.offset(1.5F, 23F, -3F));
		root.addOrReplaceChild("frontleg2", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, -5F, 1F, 1F, 5F), PartPose.offset(-0.5F, 23F, -3F));
		root.addOrReplaceChild("frontleg3", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, -5F, 1F, 1F, 5F), PartPose.offset(-2.5F, 23F, -3F));
		root.addOrReplaceChild("rightleg1", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(-3F, 23F, 1.5F));
		root.addOrReplaceChild("rightleg2", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(-3F, 23F, -0.5F));
		root.addOrReplaceChild("rightleg3", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, 0F, 5F, 1F, 1F), PartPose.offset(-3F, 23F, -2.5F));
		return LayerDefinition.create(mesh, 32, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		frontleg1.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		frontleg2.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		frontleg3.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		leftleg1.yRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		leftleg2.yRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		leftleg3.yRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		backleg1.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		backleg2.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		backleg3.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rightleg1.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rightleg2.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		rightleg3.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		smallspike1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		smallspike2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		smallspike3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		smallspike4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bigspike1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bigspike2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bigspike3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bigspike4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		leftleg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		backleg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		backleg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		backleg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		frontleg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		frontleg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		frontleg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightleg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
