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
package com.shinoow.abyssalcraft.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.shinoow.abyssalcraft.api.item.UnlockableItemClientExtensions;
import com.shinoow.abyssalcraft.common.items.armor.ACArmorMaterials;
import com.shinoow.abyssalcraft.common.items.armor.ItemACArmor;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Client item extensions (Aklo names for locked items, Depths helmet overlay).
 * Only loaded on the client, from {@code Item#initializeClient}.
 */
public class ACClientItemExtensions {

	private static final ResourceLocation CORALIUM_BLUR = new ResourceLocation("abyssalcraft", "textures/misc/coraliumblur.png");

	public static IClientItemExtensions armor(ItemACArmor item) {
		if(item.getACMaterial() == ACArmorMaterials.DEPTHS && item.getType() == ArmorItem.Type.HELMET)
			return new DepthsHelmet();
		return UnlockableItemClientExtensions.INSTANCE;
	}

	private static class DepthsHelmet extends UnlockableItemClientExtensions {

		@Override
		public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
			if(!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
			GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			graphics.setColor(1.0F, 1.0F, 1.0F, (float)ACConfig.depthsHelmetOverlayOpacity);
			graphics.blit(CORALIUM_BLUR, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
			graphics.flush();
			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
		}
	}
}
