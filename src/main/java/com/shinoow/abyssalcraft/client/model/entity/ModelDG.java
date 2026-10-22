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
 *     1.20.1 port - part tree converted by tools/port_model.py, animation and baby scaling by hand
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
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/**
 * Depths Ghoul (also used for the Anti-Ghoul and, with a chestplate, the ghoul armor)
 */
public class ModelDG<T extends LivingEntity> extends EntityModel<T> implements HeadedModel, ArmedModel {

	public final ModelPart Head;
	public final ModelPart jaw;
	public final ModelPart tooth1;
	public final ModelPart tooth2;
	public final ModelPart tooth3;
	public final ModelPart tooth4;
	public final ModelPart tooth5;
	public final ModelPart Spine1;
	public final ModelPart Spine2;
	public final ModelPart lrib1;
	public final ModelPart lrib2;
	public final ModelPart lrib3;
	public final ModelPart rrib1;
	public final ModelPart rrib2;
	public final ModelPart rrib3;
	public final ModelPart pelvis;
	public final ModelPart Spine3;
	public final ModelPart larm1;
	public final ModelPart larm2;
	public final ModelPart clawl1;
	public final ModelPart clawl2;
	public final ModelPart clawl3;
	public final ModelPart clawl4;
	public final ModelPart rarm1;
	public final ModelPart rarm2;
	public final ModelPart clawr1;
	public final ModelPart clawr2;
	public final ModelPart clawr3;
	public final ModelPart clawr4;
	public final ModelPart lleg;
	public final ModelPart rleg;
	public final ModelPart back;
	public final ModelPart lside;
	public final ModelPart rside;

	public ModelDG(ModelPart root) {
		Head = root.getChild("head");
		jaw = root.getChild("head").getChild("jaw");
		tooth1 = root.getChild("head").getChild("jaw").getChild("tooth1");
		tooth2 = root.getChild("head").getChild("jaw").getChild("tooth2");
		tooth3 = root.getChild("head").getChild("jaw").getChild("tooth3");
		tooth4 = root.getChild("head").getChild("jaw").getChild("tooth4");
		tooth5 = root.getChild("head").getChild("jaw").getChild("tooth5");
		Spine1 = root.getChild("spine1");
		Spine2 = root.getChild("spine2");
		lrib1 = root.getChild("lrib1");
		lrib2 = root.getChild("lrib2");
		lrib3 = root.getChild("lrib3");
		rrib1 = root.getChild("rrib1");
		rrib2 = root.getChild("rrib2");
		rrib3 = root.getChild("rrib3");
		pelvis = root.getChild("pelvis");
		Spine3 = root.getChild("spine3");
		larm1 = root.getChild("larm1");
		larm2 = root.getChild("larm1").getChild("larm2");
		clawl1 = root.getChild("larm1").getChild("larm2").getChild("clawl1");
		clawl2 = root.getChild("larm1").getChild("larm2").getChild("clawl2");
		clawl3 = root.getChild("larm1").getChild("larm2").getChild("clawl3");
		clawl4 = root.getChild("larm1").getChild("larm2").getChild("clawl4");
		rarm1 = root.getChild("rarm1");
		rarm2 = root.getChild("rarm1").getChild("rarm2");
		clawr1 = root.getChild("rarm1").getChild("rarm2").getChild("clawr1");
		clawr2 = root.getChild("rarm1").getChild("rarm2").getChild("clawr2");
		clawr3 = root.getChild("rarm1").getChild("rarm2").getChild("clawr3");
		clawr4 = root.getChild("rarm1").getChild("rarm2").getChild("clawr4");
		lleg = root.getChild("lleg");
		rleg = root.getChild("rleg");
		back = root.getChild("back");
		lside = root.getChild("lside");
		rside = root.getChild("rside");
	}

	public static LayerDefinition createBodyLayer(CubeDeformation deformation) {
		return LayerDefinition.create(createMesh(deformation), 128, 64);
	}

	public static MeshDefinition createMesh(CubeDeformation deformation) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_Head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.5F, -4.5F, 9F, 9F, 9F, deformation), PartPose.offset(0F, -17F, 1F));
		PartDefinition p_jaw = p_Head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(36, 0).addBox(-4.5F, -0.5F, -4.5F, 9F, 1F, 9F, deformation), PartPose.offsetAndRotation(0F, 1F, 0F, 0.2365561F, 0F, 0F));
		p_jaw.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -1F, -0.5F, 1F, 2F, 1F, deformation), PartPose.offset(-4F, -1.5F, -4F));
		p_jaw.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -1F, -0.5F, 1F, 2F, 1F, deformation), PartPose.offset(-2F, -1.5F, -4F));
		p_jaw.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -1F, -0.5F, 1F, 2F, 1F, deformation), PartPose.offset(0F, -1.5F, -4F));
		p_jaw.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -1F, -0.5F, 1F, 2F, 1F, deformation), PartPose.offset(2F, -1.5F, -4F));
		p_jaw.addOrReplaceChild("tooth5", CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -1F, -0.5F, 1F, 2F, 1F, deformation), PartPose.offset(4F, -1.5F, -4F));
		root.addOrReplaceChild("spine1", CubeListBuilder.create().texOffs(0, 42).addBox(-2.5F, -4F, -3F, 5F, 8F, 6F, deformation), PartPose.offsetAndRotation(0F, -14F, 2F, 0.2590069F, 0F, 0F));
		root.addOrReplaceChild("spine2", CubeListBuilder.create().texOffs(0, 42).addBox(-2.5F, -8F, -3F, 5F, 16F, 6F, deformation), PartPose.offset(0F, -2.854666F, 2.884038F));
		root.addOrReplaceChild("lrib1", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(4F, -6.977365F, 2.8262F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("lrib2", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(4F, -3.821312F, 2.627918F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("lrib3", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(4F, -0.6646652F, 2.434038F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("rrib1", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(-4F, -6.977365F, 2.826201F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("rrib2", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(-4F, -3.82131F, 2.627918F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("rrib3", CubeListBuilder.create().texOffs(101, 47).addBox(-1.5F, -1F, -1F, 3F, 2F, 2F, deformation), PartPose.offsetAndRotation(-4F, -0.6652546F, 2.429635F, -0.0702351F, 0F, 0F));
		root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(80, 14).addBox(-6F, -1F, -3F, 12F, 2F, 6F, deformation), PartPose.offsetAndRotation(0F, 5.390734F, 2.999714F, -0.0000002F, 0F, 0F));
		root.addOrReplaceChild("spine3", CubeListBuilder.create().texOffs(76, 28).addBox(-9F, -2F, -2F, 18F, 4F, 4F, deformation), PartPose.offsetAndRotation(0F, -11.10007F, 2.768362F, 0.1290269F, 0F, 0F));
		PartDefinition p_larm1 = root.addOrReplaceChild("larm1", CubeListBuilder.create().texOffs(46, 48).addBox(0F, -2F, -2F, 4F, 12F, 4F, deformation), PartPose.offsetAndRotation(9F, -11F, 2F, -0.5595525F, 0F, 0F));
		PartDefinition p_larm2 = p_larm1.addOrReplaceChild("larm2", CubeListBuilder.create().texOffs(64, 52).addBox(-2F, 1F, 3F, 4F, 8F, 4F, deformation), PartPose.offsetAndRotation(2F, 4F, -2F, -0.8901175F, 0F, 0F));
		p_larm2.addOrReplaceChild("clawl1", CubeListBuilder.create().texOffs(110, 57).addBox(-0.5F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(-1F, 8F, 3F));
		p_larm2.addOrReplaceChild("clawl2", CubeListBuilder.create().texOffs(110, 57).addBox(-0.5F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(1F, 8F, 3F));
		p_larm2.addOrReplaceChild("clawl3", CubeListBuilder.create().texOffs(110, 57).addBox(-1F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(-2F, 8F, 5F));
		p_larm2.addOrReplaceChild("clawl4", CubeListBuilder.create().texOffs(110, 57).addBox(0F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(2F, 8F, 5F));
		PartDefinition p_rarm1 = root.addOrReplaceChild("rarm1", CubeListBuilder.create().texOffs(46, 48).addBox(-4F, -2F, -2F, 4F, 12F, 4F, deformation), PartPose.offsetAndRotation(-9F, -11F, 2F, -0.559472F, 0F, 0F));
		PartDefinition p_rarm2 = p_rarm1.addOrReplaceChild("rarm2", CubeListBuilder.create().texOffs(64, 52).addBox(-2F, 1F, 3F, 4F, 8F, 4F, deformation), PartPose.offsetAndRotation(-2F, 4F, -2F, -0.89007F, 0F, 0F));
		p_rarm2.addOrReplaceChild("clawr1", CubeListBuilder.create().texOffs(110, 57).addBox(-0.5F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(-1F, 8F, 3F));
		p_rarm2.addOrReplaceChild("clawr2", CubeListBuilder.create().texOffs(110, 57).addBox(-0.5F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(1F, 8F, 3F));
		p_rarm2.addOrReplaceChild("clawr3", CubeListBuilder.create().texOffs(110, 57).addBox(0F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(-3F, 8F, 5F));
		p_rarm2.addOrReplaceChild("clawr4", CubeListBuilder.create().texOffs(110, 57).addBox(-1F, -0.5F, -1F, 1F, 5F, 1F, deformation), PartPose.offset(3F, 8F, 5F));
		root.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(22, 40).addBox(-3F, -1F, -3F, 6F, 18F, 6F, deformation), PartPose.offset(3F, 7F, 3F));
		root.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(22, 40).addBox(-3F, -1F, -3F, 6F, 18F, 6F, deformation), PartPose.offset(-3F, 7F, 3F));
		root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 18).addBox(-6F, -7.5F, 0F, 12F, 15F, 0F, deformation), PartPose.offset(0F, -3F, 6F));
		root.addOrReplaceChild("lside", CubeListBuilder.create().texOffs(30, 12).addBox(0F, -7.5F, -3F, 0F, 15F, 6F, deformation), PartPose.offset(6F, -3F, 3F));
		root.addOrReplaceChild("rside", CubeListBuilder.create().texOffs(42, 12).addBox(0F, -7.5F, -3F, 0F, 15F, 6F, deformation), PartPose.offset(-6F, -3F, 3F));
		return mesh;
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		Head.yRot = netHeadYaw / (180F / (float)Math.PI);
		Head.xRot = headPitch / (180F / (float)Math.PI);
		rleg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		rleg.yRot = 0.0F;
		lleg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		lleg.yRot = 0.0F;
		float f6 = Mth.sin(attackTime * (float)Math.PI);
		float f7 = Mth.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float)Math.PI);
		rarm1.zRot = 0.0F;
		larm1.zRot = 0.0F;
		rarm1.yRot = -(0.1F - f6 * 0.6F);
		larm1.yRot = 0.1F - f6 * 0.6F;
		rarm1.xRot = -((float)Math.PI / 3.7F);
		larm1.xRot = -((float)Math.PI / 3.7F);
		rarm1.xRot -= f6 * 1.2F - f7 * 0.4F;
		larm1.xRot -= f6 * 1.2F - f7 * 0.4F;
		rarm1.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		larm1.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rarm1.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
		larm1.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
		if(riding) {
			rarm1.xRot += -((float)Math.PI / 5F);
			larm1.xRot += -((float)Math.PI / 5F);
			rleg.xRot = -((float)Math.PI * 2F / 5F);
			lleg.xRot = -((float)Math.PI * 2F / 5F);
			rleg.yRot = (float)Math.PI / 10F;
			lleg.yRot = -((float)Math.PI / 10F);
		}
	}

	/** The body parts (the head is rendered separately, it scales differently for babies) */
	protected ModelPart[] bodyParts() {
		return new ModelPart[] {Spine1, Spine2, lrib1, lrib2, lrib3, rrib1, rrib2, rrib3, pelvis, Spine3, larm1, rarm1, lleg, rleg, back, lside, rside};
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if(young) {
			poseStack.pushPose();
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(0.0F, 21.0F / 16.0F, 0.0F);
			Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
			poseStack.pushPose();
			poseStack.scale(0.5F, 0.5F, 0.5F);
			poseStack.translate(0.0F, 24.0F / 16.0F, 0.0F);
			for(ModelPart part : bodyParts())
				part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		} else {
			Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			for(ModelPart part : bodyParts())
				part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	/** 1.12.2 setInvisible: shows or hides every part (the armor layer then shows the parts of one slot) */
	public void setAllVisible(boolean visible) {
		Head.visible = visible;
		for(ModelPart part : bodyParts())
			part.visible = visible;
	}

	@Override
	public ModelPart getHead() {
		return Head;
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		(arm == HumanoidArm.LEFT ? larm1 : rarm1).translateAndRotate(poseStack);
	}
}
