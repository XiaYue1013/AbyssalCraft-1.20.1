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

import com.shinoow.abyssalcraft.common.inventory.ContainerStateTransformer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiStateTransformer extends AbstractContainerScreen<ContainerStateTransformer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("abyssalcraft", "textures/gui/container/statetransformer.png");

	private Button mode;

	public GuiStateTransformer(ContainerStateTransformer menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageHeight = 238;
		inventoryLabelY = imageHeight - 94;
	}

	private Component modeLabel() {
		return Component.translatable("gui.abyssalcraft.statetransformer." + (menu.getMode() == 0 ? "insert" : "extract"));
	}

	@Override
	protected void init() {
		super.init();
		mode = addRenderableWidget(Button.builder(modeLabel(), b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0))
				.bounds(leftPos + 2, topPos + 95, 40, 20).build());
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		mode.active = menu.getProcessingTime() == 0;
		mode.setMessage(modeLabel());
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
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		int i1 = menu.getProcessingTime() * 24 / 200;
		graphics.blit(TEXTURE, leftPos + 6, topPos + 43, 176, 14, i1 + 1, 16);
	}
}
