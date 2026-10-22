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

import com.shinoow.abyssalcraft.common.inventory.ContainerConfigurator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiConfigurator extends AbstractContainerScreen<ContainerConfigurator> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("abyssalcraft", "textures/gui/container/configurator.png");

	private final Button[] buttons = new Button[2];

	public GuiConfigurator(ContainerConfigurator menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		for(int i = 0; i < 2; i++) {
			int id = i;
			buttons[i] = addRenderableWidget(Button.builder(label(i), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id))
					.bounds(leftPos + 122, topPos + 36 + 21 * i, 40, 20).build());
		}
	}

	private Component label(int id) {
		return Component.literal(menu.getFlag(id) ? "true" : "false");
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		for(int i = 0; i < 2; i++)
			buttons[i].setMessage(label(i));
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
		graphics.drawString(font, Component.translatable("tooltip.configurator.filter.0"), 8, imageHeight - 125 + 2, 4210752, false);
		graphics.drawString(font, Component.translatable("tooltip.configurator.filter.1"), 8, imageHeight - 108 + 2, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
