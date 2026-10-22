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
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * The Depths Ghoul model with a chestplate, inflated for armor
 */
public class ModelDGArmor<T extends LivingEntity> extends ModelDG<T> {

	public final ModelPart chestplate;

	public ModelDGArmor(ModelPart root) {
		super(root);
		chestplate = root.getChild("chestplate");
	}

	public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
		MeshDefinition mesh = ModelDG.createMesh(deformation);
		mesh.getRoot().addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(0, 18).addBox(-6F, -7.5F, -3F, 12F, 15F, 6F, deformation),
				PartPose.offset(0F, -3F, 3F));
		return LayerDefinition.create(mesh, 128, 64);
	}

	@Override
	protected ModelPart[] bodyParts() {
		ModelPart[] parts = super.bodyParts();
		ModelPart[] all = java.util.Arrays.copyOf(parts, parts.length + 1);
		all[parts.length] = chestplate;
		return all;
	}

	@Override
	public void setAllVisible(boolean visible) {
		super.setAllVisible(visible);
		chestplate.visible = visible;
	}
}
