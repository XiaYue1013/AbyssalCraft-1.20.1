/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (RenderShadowCreature, RenderShadowMonster, RenderShadowBeast, RenderSacthoth,
 *                LayerSacthothHeldItem)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.client.model.entity.ModelSacthoth;
import com.shinoow.abyssalcraft.common.entity.EntitySacthoth;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** The shadow mobs are translucent: the darker it is around them, the less you see them */
public class RenderShadow<T extends Mob> extends MobRenderer<T, RenderShadow.Translucent<T>> {

	private final ResourceLocation texture;
	private final boolean alwaysRender;

	public RenderShadow(EntityRendererProvider.Context context, EntityModel<T> model, String texture, boolean head, boolean alwaysRender) {
		super(context, new Translucent<>(model), 0.0F);
		this.texture = AbyssalCraft.loc("textures/model/" + texture + ".png");
		this.alwaysRender = alwaysRender;
		if(head && model instanceof HeadedModel)
			addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
	}

	public static RenderShadow<EntitySacthoth> sacthoth(EntityRendererProvider.Context context, ModelSacthoth model) {
		RenderShadow<EntitySacthoth> renderer = new RenderShadow<>(context, model, "boss/sacthoth", false, true);
		renderer.addLayer(new BladeLayer(renderer, model, context.getItemInHandRenderer()));
		return renderer;
	}

	@Override
	public void render(T entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
		getModel().alpha = entity.getLightLevelDependentMagicValue();
		super.render(entity, yaw, partialTicks, poseStack, buffers, light);
	}

	@Override
	public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
		return alwaysRender && entity.shouldRender(x, y, z) || super.shouldRender(entity, frustum, x, y, z);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return texture;
	}

	/** Wraps a model: translucent render type, alpha set per entity */
	public static class Translucent<T extends Mob> extends EntityModel<T> implements HeadedModel {

		private final EntityModel<T> model;
		float alpha = 1.0F;

		Translucent(EntityModel<T> model) {
			super(RenderType::entityTranslucent);
			this.model = model;
		}

		@Override
		public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
			copyPropertiesTo(model);
			model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		}

		@Override
		public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			copyPropertiesTo(model);
			model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
			model.renderToBuffer(poseStack, buffer, light, overlay, red, green, blue, alpha * this.alpha);
		}

		@Override
		public ModelPart getHead() {
			return ((HeadedModel) model).getHead();
		}
	}

	/** LayerSacthothHeldItem: the Soul Harvesting Blade in his left claw */
	private static class BladeLayer extends RenderLayer<EntitySacthoth, Translucent<EntitySacthoth>> {

		private final ModelSacthoth model;
		private final ItemInHandRenderer itemRenderer;

		BladeLayer(RenderLayerParent<EntitySacthoth, Translucent<EntitySacthoth>> parent, ModelSacthoth model, ItemInHandRenderer itemRenderer) {
			super(parent);
			this.model = model;
			this.itemRenderer = itemRenderer;
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, int light, EntitySacthoth entity, float limbSwing, float limbSwingAmount,
				float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			if(entity.isInvisible() || !entity.isAlive() || !ACItems.sacthoths_soul_harvesting_blade.isPresent()) return;
			poseStack.pushPose();
			model.leftarm1.translateAndRotate(poseStack);
			poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
			poseStack.scale(1.2F, 1.2F, 1.2F);
			poseStack.translate(-0.125F, 0.325F, 0.825F);
			itemRenderer.renderItem(entity, new ItemStack(ACItems.sacthoths_soul_harvesting_blade.get()), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false,
					poseStack, buffers, light);
			poseStack.popPose();
		}
	}
}
