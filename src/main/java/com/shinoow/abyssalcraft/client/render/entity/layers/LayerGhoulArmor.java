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
 *     1.20.1 port - render layer instead of LayerArmorBase; dyed (leather) armor keeps its color and overlay
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.client.model.entity.ModelDG;
import com.shinoow.abyssalcraft.client.model.entity.ModelDGArmor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

/**
 * Armor on the ghoul model. Each armor item needs a texture registered through
 * {@link AbyssalCraftAPI#addGhoulArmorTextures}; unregistered armor shows the "missing" texture.
 */
public class LayerGhoulArmor<T extends LivingEntity, M extends ModelDG<T>> extends RenderLayer<T, M> {

	private static final ResourceLocation MISSING_ARMOR = AbyssalCraft.loc("textures/armor/ghoul/missing_1.png");
	private static final ResourceLocation MISSING_LEGGINGS = AbyssalCraft.loc("textures/armor/ghoul/missing_2.png");

	private final ModelDGArmor<T> inner, outer;

	public LayerGhoulArmor(RenderLayerParent<T, M> parent, ModelDGArmor<T> inner, ModelDGArmor<T> outer) {
		super(parent);
		this.inner = inner;
		this.outer = outer;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick,
			float ageInTicks, float netHeadYaw, float headPitch) {
		for(EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HEAD}) {
			ItemStack stack = entity.getItemBySlot(slot);
			if(!(stack.getItem() instanceof ArmorItem armor) || armor.getEquipmentSlot() != slot)
				continue;
			ModelDGArmor<T> model = slot == EquipmentSlot.LEGS ? inner : outer;
			model.young = getParentModel().young;
			model.attackTime = getParentModel().attackTime;
			model.riding = getParentModel().riding;
			model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			setVisibleParts(model, slot);
			ResourceLocation texture = texture(stack, slot, false);
			float r = 1.0F, g = 1.0F, b = 1.0F;
			if(stack.getItem() instanceof DyeableLeatherItem dyeable) {
				int color = dyeable.getColor(stack);
				r = (color >> 16 & 255) / 255.0F;
				g = (color >> 8 & 255) / 255.0F;
				b = (color & 255) / 255.0F;
			}
			render(poseStack, buffers, packedLight, model, texture, stack.hasFoil(), r, g, b);
			if(stack.getItem() instanceof DyeableLeatherItem)
				render(poseStack, buffers, packedLight, model, texture(stack, slot, true), stack.hasFoil(), 1.0F, 1.0F, 1.0F);
		}
	}

	private void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, ModelDGArmor<T> model, ResourceLocation texture, boolean foil, float r, float g, float b) {
		VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(texture), false, foil);
		model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
	}

	private static void setVisibleParts(ModelDGArmor<?> model, EquipmentSlot slot) {
		model.setAllVisible(false);
		switch(slot) {
		case FEET -> {
			model.rleg.visible = true;
			model.lleg.visible = true;
		}
		case LEGS -> {
			model.chestplate.visible = true;
			model.pelvis.visible = true;
			model.rleg.visible = true;
			model.lleg.visible = true;
		}
		case CHEST -> {
			model.chestplate.visible = true;
			model.Spine3.visible = true;
			model.rarm1.visible = true;
			model.larm1.visible = true;
		}
		case HEAD -> {
			model.Head.visible = true;
			model.jaw.visible = true;
		}
		default -> {}
		}
	}

	private static ResourceLocation texture(ItemStack stack, EquipmentSlot slot, boolean overlay) {
		ResourceLocation res = switch(slot) {
		case HEAD -> AbyssalCraftAPI.getGhoulHelmetTexture(stack.getItem());
		case CHEST -> AbyssalCraftAPI.getGhoulChestplateTexture(stack.getItem());
		case LEGS -> AbyssalCraftAPI.getGhoulLeggingsTexture(stack.getItem());
		case FEET -> AbyssalCraftAPI.getGhoulBootsTexture(stack.getItem());
		default -> null;
		};
		if(res == null)
			return slot == EquipmentSlot.LEGS ? MISSING_LEGGINGS : MISSING_ARMOR;
		if(overlay) {
			String path = res.getPath();
			return new ResourceLocation(res.getNamespace(), path.substring(0, path.length() - 4) + "_overlay.png");
		}
		return res;
	}
}
