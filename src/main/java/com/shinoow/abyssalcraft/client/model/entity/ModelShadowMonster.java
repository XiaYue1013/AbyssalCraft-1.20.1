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

public class ModelShadowMonster<T extends Entity> extends EntityModel<T> implements net.minecraft.client.model.HeadedModel {

	public final ModelPart Head;
	public final ModelPart Body1;
	public final ModelPart Body2;
	public final ModelPart Body3;
	public final ModelPart Lshoulder1;
	public final ModelPart Rshoulder1;
	public final ModelPart Lshoulder2;
	public final ModelPart Rshoulder2;
	public final ModelPart Larm1;
	public final ModelPart Larm2;
	public final ModelPart Rarm1;
	public final ModelPart Rarm2;
	public final ModelPart Back1;
	public final ModelPart Back2;
	public final ModelPart Back3;
	public final ModelPart Back4;
	public final ModelPart Back5;
	public final ModelPart Back6;
	public final ModelPart Back7;
	public final ModelPart Back8;
	public final ModelPart Back9;

	public ModelShadowMonster(ModelPart root) {
		Head = root.getChild("head");
		Body1 = root.getChild("body1");
		Body2 = root.getChild("body2");
		Body3 = root.getChild("body3");
		Lshoulder1 = root.getChild("lshoulder1");
		Rshoulder1 = root.getChild("rshoulder1");
		Lshoulder2 = root.getChild("lshoulder2");
		Rshoulder2 = root.getChild("rshoulder2");
		Larm1 = root.getChild("larm1");
		Larm2 = root.getChild("larm2");
		Rarm1 = root.getChild("rarm1");
		Rarm2 = root.getChild("rarm2");
		Back1 = root.getChild("back1");
		Back2 = root.getChild("back2");
		Back3 = root.getChild("back3");
		Back4 = root.getChild("back4");
		Back5 = root.getChild("back5");
		Back6 = root.getChild("back6");
		Back7 = root.getChild("back7");
		Back8 = root.getChild("back8");
		Back9 = root.getChild("back9");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -7F, -3.5F, 7F, 7F, 7F), PartPose.offset(0F, 0F, 0F));
		root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(20, 14).addBox(-1.5F, -1F, -1.5F, 3F, 10F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.4461433F, 0F, 0F));
		root.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(28, 0).addBox(0F, 0F, 0F, 3F, 6F, 3F), PartPose.offset(-1.5F, 7F, 2F));
		root.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(28, 0).addBox(0F, 0F, 0F, 3F, 6F, 3F), PartPose.offsetAndRotation(-1.5F, 11F, 2.5F, -0.5948578F, 0F, 0F));
		root.addOrReplaceChild("lshoulder1", CubeListBuilder.create().texOffs(40, 0).addBox(0F, 0F, 0F, 3F, 2F, 2F), PartPose.offset(1.5F, 0F, 0F));
		root.addOrReplaceChild("rshoulder1", CubeListBuilder.create().texOffs(40, 0).addBox(0F, 0F, 0F, 3F, 2F, 2F), PartPose.offset(-4.5F, 0F, 0F));
		root.addOrReplaceChild("lshoulder2", CubeListBuilder.create().texOffs(40, 0).addBox(0F, 0F, 0F, 3F, 2F, 2F), PartPose.offset(1.5F, 5F, 2F));
		root.addOrReplaceChild("rshoulder2", CubeListBuilder.create().texOffs(40, 0).addBox(0F, 0F, 0F, 3F, 2F, 2F), PartPose.offset(-4.5F, 5F, 2F));
		root.addOrReplaceChild("larm1", CubeListBuilder.create().texOffs(0, 14).addBox(0F, -1F, -7F, 2F, 2F, 8F), PartPose.offset(4.5F, 1F, 1F));
		root.addOrReplaceChild("larm2", CubeListBuilder.create().texOffs(0, 14).addBox(0F, -1F, -7F, 2F, 2F, 8F), PartPose.offset(4.5F, 6F, 3F));
		root.addOrReplaceChild("rarm1", CubeListBuilder.create().texOffs(0, 14).addBox(-2F, -1F, -7F, 2F, 2F, 8F), PartPose.offset(-4.5F, 1F, 1F));
		root.addOrReplaceChild("rarm2", CubeListBuilder.create().texOffs(0, 14).addBox(-2F, -1F, -7F, 2F, 2F, 8F), PartPose.offset(-4.5F, 6F, 3F));
		root.addOrReplaceChild("back1", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 2F, 4F, -1.041002F, 0F, 0F));
		root.addOrReplaceChild("back2", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 4F, 5F, -1.041002F, 0F, 0F));
		root.addOrReplaceChild("back3", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 0F, 3F, -1.041002F, 0F, 0F));
		root.addOrReplaceChild("back4", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 1F, 2F), PartPose.offset(-0.5F, 8F, 4.5F));
		root.addOrReplaceChild("back5", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 1F, 2F), PartPose.offset(-0.5F, 10F, 4.5F));
		root.addOrReplaceChild("back6", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 1F, 2F), PartPose.offset(-0.5F, 12F, 4.5F));
		root.addOrReplaceChild("back7", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 14F, 3.5F, 1.00382F, 0F, 0F));
		root.addOrReplaceChild("back8", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 15.5F, 2.5F, 1.00382F, 0F, 0F));
		root.addOrReplaceChild("back9", CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.5F, 17F, 1.5F, 1.00382F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		Head.yRot = netHeadYaw / (180F / (float)Math.PI);
		Head.xRot = headPitch / (180F / (float)Math.PI);
		Rarm1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		Larm1.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		Rarm2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F * 0.1F;
		Larm2.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F * 0.1F;
		Rarm1.zRot = 0.0F;
		Larm1.zRot = 0.0F;
		Rarm2.zRot = 0.0F;
		Larm2.zRot = 0.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Body1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Body2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Body3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Lshoulder1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Rshoulder1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Lshoulder2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Rshoulder2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Larm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Larm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Rarm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Rarm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back6.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back7.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back8.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		Back9.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getHead() {
		return Head;
	}
}
