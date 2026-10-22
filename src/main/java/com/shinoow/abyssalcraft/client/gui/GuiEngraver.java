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
package com.shinoow.abyssalcraft.client.gui;

import com.shinoow.abyssalcraft.common.inventory.ContainerEngraver;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiEngraver extends AbstractContainerScreen<ContainerEngraver> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("abyssalcraft", "textures/gui/container/engraver.png");

	public GuiEngraver(ContainerEngraver menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752, false);
		graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int k = (width - imageWidth) / 2;
		int l = (height - imageHeight) / 2;
		graphics.blit(TEXTURE, k, l, 0, 0, imageWidth, imageHeight);
		int i1 = menu.getProgressScaled(24);
		graphics.blit(TEXTURE, k + 79, l + 34, 176, 14, i1 + 1, 16);
	}
}
