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
package com.shinoow.abyssalcraft.client.render.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.entity.AbyssalZombieModel;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.common.entity.EntityAbyssalZombie;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/** Zombie model and armor (1.12.2 RenderBiped with ModelZombie) */
public class RenderAbyssalZombie extends HumanoidMobRenderer<EntityAbyssalZombie, AbyssalZombieModel<EntityAbyssalZombie>> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/model/abyssal_zombie.png");

	public RenderAbyssalZombie(EntityRendererProvider.Context context) {
		super(context, new AbyssalZombieModel<>(context.bakeLayer(ACModelLayers.GOLEM)), 0.5F);
		addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(EntityAbyssalZombie entity) {
		return TEXTURE;
	}
}
