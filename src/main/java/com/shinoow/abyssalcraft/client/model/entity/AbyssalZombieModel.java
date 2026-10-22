/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the vanilla zombie model (1.12.2 used ModelZombie) for mobs that aren't vanilla zombies
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.entity;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Mob;

/** Humanoid model with the zombie arm pose (arms raised while attacking) */
public class AbyssalZombieModel<T extends Mob> extends HumanoidModel<T> {

	public AbyssalZombieModel(ModelPart root) {
		super(root);
	}

	/** Old 64x32 skins mirror the right limb UVs; modern left-limb UVs start at v=48. */
	public static LayerDefinition createLegacyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-4, -8, -4, 8, 8, 8), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0)
				.addBox(-4, -8, -4, 8, 8, 8, new CubeDeformation(0.5F)), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
				.addBox(-4, 0, -2, 8, 12, 4), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16)
				.addBox(-3, -2, -2, 4, 12, 4), PartPose.offset(-5, 2, 0));
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror()
				.addBox(-1, -2, -2, 4, 12, 4), PartPose.offset(5, 2, 0));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
				.addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(-1.9F, 12, 0));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
				.addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(1.9F, 12, 0));
		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		AnimationUtils.animateZombieArms(leftArm, rightArm, entity.isAggressive(), attackTime, ageInTicks);
	}
}
