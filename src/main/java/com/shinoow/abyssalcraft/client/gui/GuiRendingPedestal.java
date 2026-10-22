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

import com.shinoow.abyssalcraft.common.inventory.ContainerRendingPedestal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiRendingPedestal extends AbstractContainerScreen<ContainerRendingPedestal> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("abyssalcraft", "textures/gui/container/rendingpedestal.png");

	public GuiRendingPedestal(ContainerRendingPedestal menu, Inventory inventory, Component title) {
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
		String s2 = String.format("%d/%d PE", menu.getContainedEnergy(), 5000);
		graphics.drawString(font, s2, imageWidth / 2 - font.width(s2) / 2, 20, 4210752, false);
		graphics.drawString(font, "A: " + menu.getEnergy(1) + "/100", 55, 29, 4210752, false);
		graphics.drawString(font, "D: " + menu.getEnergy(2) + "/100", 108, 29, 4210752, false);
		graphics.drawString(font, "O: " + menu.getEnergy(3) + "/100", 55, 37, 4210752, false);
		graphics.drawString(font, "S: " + menu.getEnergy(0) + "/200", 108, 37, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
