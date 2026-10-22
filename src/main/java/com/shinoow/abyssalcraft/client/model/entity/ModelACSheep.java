/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (ModelEvilSheep1/2, ModelDemonSheep1/2: copies of the vanilla sheep models)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.entity.demon.ISheepAnimation;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * The vanilla sheep body / wool model for mobs that aren't vanilla Sheep. Uses the vanilla sheep layers, plus the
 * Evil Sheep's "player skin" wool (the 1.12.2 layer remapped the boxes onto a 64x64 player skin).
 */
public class ModelACSheep<T extends LivingEntity & ISheepAnimation> extends QuadrupedModel<T> {

	public static final ModelLayerLocation SKIN_WOOL = new ModelLayerLocation(AbyssalCraft.loc("evil_sheep"), "skin_wool");
	private float headXRot;

	public ModelACSheep(ModelPart root) {
		super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	/** LayerEvilSheepWool#setupModelStuff: the wool boxes on the head, body and legs of a player skin */
	public static LayerDefinition createSkinWoolLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.6F)),
				PartPose.offset(0.0F, 6.0F, -8.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 10).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(1.75F)),
				PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float)Math.PI / 2F, 0.0F, 0.0F));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F));
		root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, 12.0F, -5.0F));
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, 12.0F, -5.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		head.y = 6.0F + entity.getHeadEatPositionScale(partialTick) * 9.0F;
		headXRot = entity.getHeadEatAngleScale(partialTick);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		head.xRot = headXRot;
	}
}
