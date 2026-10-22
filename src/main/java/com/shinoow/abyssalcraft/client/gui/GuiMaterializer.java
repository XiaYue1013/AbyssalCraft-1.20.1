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

import com.shinoow.abyssalcraft.common.inventory.ContainerMaterializer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * Materializer screen with the scrollbar and Clippy (shown while a Necronomicon is in the Materializer)
 */
public class GuiMaterializer extends AbstractContainerScreen<ContainerMaterializer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("abyssalcraft", "textures/gui/container/materializer.png");
	private static final int SCROLL_X = 151, SCROLL_Y = 15, SCROLL_W = 12, SCROLL_H = 54, SCROLLER_HEIGHT = 15;

	private static final String[][] CLIPPY = {
			{"Hi, I'm Clippy! What are we", "materializing today?"},
			{"It looks like you're trying to", "create air. Do you need help?"},
			{"The Necronomicon has a recipe", "list!"},
			{"Do you need assistance?", null},
			{"Did you know gold consists of", "gold atoms?"},
			{"I see that you have been using", "your mouse."},
			{"Did you mean 'Aluminium'?", null}};

	private boolean scrolling;
	private boolean hadNecronomicon;
	private String[] clippy;

	public GuiMaterializer(ContainerMaterializer menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		boolean necronomicon = menu.hasNecronomicon();
		if(necronomicon && !hadNecronomicon)
			clippy = CLIPPY[minecraft.level.random.nextInt(CLIPPY.length)];
		else if(!necronomicon)
			clippy = null;
		hadNecronomicon = necronomicon;
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		Component name = clippy != null ? Component.literal(clippy[0]) : title;
		graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752, false);
		Component second = clippy != null && clippy[1] != null ? Component.literal(clippy[1]) : playerInventoryTitle;
		graphics.drawString(font, second, 8, imageHeight - 96 + 2, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		int max = menu.getMaxScrollRow();
		int offset = max == 0 ? 0 : (int) Math.min(SCROLL_H - SCROLLER_HEIGHT, (float) menu.getScrollRow() / max * (SCROLL_H - SCROLLER_HEIGHT));
		graphics.blit(TEXTURE, leftPos + SCROLL_X, topPos + SCROLL_Y + offset, menu.canScroll() ? 176 : 188, 14, 12, 15);
		if(clippy != null)
			graphics.blit(TEXTURE, leftPos - 17, topPos + 2, 176, 29, 15, 22);
	}

	private void scrollTo(int row) {
		row = Mth.clamp(row, 0, menu.getMaxScrollRow());
		if(row != menu.getScrollRow())
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, row);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if(menu.canScroll()) {
			scrollTo(menu.getScrollRow() - (int) Math.signum(delta));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	private boolean inScrollbar(double mouseX, double mouseY) {
		double x = mouseX - leftPos, y = mouseY - topPos;
		return x >= SCROLL_X && x <= SCROLL_X + SCROLL_W && y >= SCROLL_Y && y <= SCROLL_Y + SCROLL_H;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button == 0 && menu.canScroll() && inScrollbar(mouseX, mouseY)) {
			scrolling = true;
			dragScroll(mouseY);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if(scrolling) {
			dragScroll(mouseY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		scrolling = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private void dragScroll(double mouseY) {
		float f = (float) (mouseY - topPos - SCROLL_Y - SCROLLER_HEIGHT / 2.0) / (SCROLL_H - SCROLLER_HEIGHT);
		scrollTo(Math.round(Mth.clamp(f, 0, 1) * menu.getMaxScrollRow()));
	}
}
