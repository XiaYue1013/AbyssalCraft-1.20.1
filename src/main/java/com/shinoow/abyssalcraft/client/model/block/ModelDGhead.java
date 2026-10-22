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
 *     1.20.1 port - layer definition with the same boxes, offsets and rotations
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

/**
 * Depths Ghoul head (block)
 */
public class ModelDGhead extends Model {

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(AbyssalCraft.loc("ghoul_head"), "main");

	private final ModelPart root;

	public ModelDGhead(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5F, -9F, -5F, 9, 9, 9),
				PartPose.offsetAndRotation(1F, 22F, 1F, -0.2974289F, 0F, 0F));
		root.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(-4F, 0F, -9F, 9, 1, 9), PartPose.offset(0F, 23F, 5F));
		for(int i = 0; i < 5; i++)
			root.addOrReplaceChild("tooth" + (i + 1), CubeListBuilder.create().texOffs(48, 11).mirror().addBox(0F, -2F, 0F, 1, 2, 1),
					PartPose.offset(-4F + 2 * i, 23F, -4F));
		return LayerDefinition.create(mesh, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
