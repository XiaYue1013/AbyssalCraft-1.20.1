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

public class ModelChagarothFist<T extends Entity> extends EntityModel<T> {

	public final ModelPart arm1;
	public final ModelPart ground;
	public final ModelPart arm2;
	public final ModelPart arm3;
	public final ModelPart palm1;
	public final ModelPart palm2;
	public final ModelPart finger1;
	public final ModelPart finger2;
	public final ModelPart finger3;
	public final ModelPart finger4;
	public final ModelPart finger5;
	public final ModelPart finger12;
	public final ModelPart finger22;
	public final ModelPart finger32;
	public final ModelPart finger42;
	public final ModelPart finger52;
	public final ModelPart finger13;
	public final ModelPart finger23;
	public final ModelPart finger33;
	public final ModelPart finger43;
	public final ModelPart finger53;
	public final ModelPart eye;

	public ModelChagarothFist(ModelPart root) {
		arm1 = root.getChild("arm1");
		ground = root.getChild("ground");
		arm2 = root.getChild("arm2");
		arm3 = root.getChild("arm3");
		palm1 = root.getChild("palm1");
		palm2 = root.getChild("palm2");
		finger1 = root.getChild("finger1");
		finger2 = root.getChild("finger2");
		finger3 = root.getChild("finger3");
		finger4 = root.getChild("finger4");
		finger5 = root.getChild("finger5");
		finger12 = root.getChild("finger12");
		finger22 = root.getChild("finger22");
		finger32 = root.getChild("finger32");
		finger42 = root.getChild("finger42");
		finger52 = root.getChild("finger52");
		finger13 = root.getChild("finger13");
		finger23 = root.getChild("finger23");
		finger33 = root.getChild("finger33");
		finger43 = root.getChild("finger43");
		finger53 = root.getChild("finger53");
		eye = root.getChild("eye");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 2F, 14F, 2F), PartPose.offset(-1F, 15F, -1F));
		root.addOrReplaceChild("ground", CubeListBuilder.create().texOffs(8, 0).addBox(0F, 0F, 0F, 6F, 1F, 6F), PartPose.offset(-3F, 23F, -3F));
		root.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(0F, 15.5F, 0F, -0.3346075F, 0F, 0F));
		root.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offset(0F, 11F, 1.5F));
		root.addOrReplaceChild("palm1", CubeListBuilder.create().texOffs(26, 0).addBox(0F, 0F, 0F, 6F, 1F, 4F), PartPose.offset(-3F, 5F, 0.5F));
		root.addOrReplaceChild("palm2", CubeListBuilder.create().texOffs(42, 0).addBox(0F, 0F, 0F, 4F, 1F, 3F), PartPose.offset(-2F, 6F, 0.5F));
		root.addOrReplaceChild("finger1", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(-3F, 5.5F, 4F, -0.4833219F, 0F, 0F));
		root.addOrReplaceChild("finger2", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(-1.5F, 5.5F, 4F, -0.4833219F, 0F, 0F));
		root.addOrReplaceChild("finger3", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 5.5F, 4F, -0.4833219F, 0F, 0F));
		root.addOrReplaceChild("finger4", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(1.5F, 5.5F, 4F, -0.4833219F, 0F, 0F));
		root.addOrReplaceChild("finger5", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(2F, 5.5F, 3F, -0.4833219F, 1.570796F, 0F));
		root.addOrReplaceChild("finger12", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offset(-3F, 3.5F, 5F));
		root.addOrReplaceChild("finger22", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offset(-1.5F, 3.5F, 5F));
		root.addOrReplaceChild("finger32", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offset(0F, 3.5F, 5F));
		root.addOrReplaceChild("finger42", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offset(1.5F, 3.5F, 5F));
		root.addOrReplaceChild("finger52", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offset(3F, 3.5F, 2F));
		root.addOrReplaceChild("finger13", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(-3F, 1F, 5F, 0.4833166F, 0F, 0F));
		root.addOrReplaceChild("finger23", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(-1.5F, 1F, 5F, 0.4833166F, 0F, 0F));
		root.addOrReplaceChild("finger33", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 1F, 5F, 0.4833166F, 0F, 0F));
		root.addOrReplaceChild("finger43", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -3F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(1.5F, 1F, 5F, 0.4833166F, 0F, 0F));
		root.addOrReplaceChild("finger53", CubeListBuilder.create().texOffs(8, 0).addBox(0F, -1F, 0F, 1F, 1F, 1F), PartPose.offsetAndRotation(3F, 1F, 3F, 0.4833166F, 1.570796F, 0F));
		root.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(8, 7).addBox(-1.5F, -3F, -1.5F, 3F, 3F, 3F), PartPose.offset(0F, 6F, 1F));
		return LayerDefinition.create(mesh, 64, 32);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		eye.yRot = netHeadYaw / (180F / (float)Math.PI);
		eye.xRot = netHeadYaw / (180F / (float)Math.PI);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		ground.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		arm3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		palm1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		palm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger5.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger12.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger22.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger32.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger42.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger52.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger13.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger23.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger33.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger43.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		finger53.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		eye.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
