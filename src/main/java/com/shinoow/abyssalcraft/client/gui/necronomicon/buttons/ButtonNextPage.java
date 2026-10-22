/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon.buttons;

import com.shinoow.abyssalcraft.AbyssalCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Page arrow (short: one page, long: five pages) */
public class ButtonNextPage extends Button {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/gui/necronomicon.png");
	private final boolean forward, isLong;

	public ButtonNextPage(int x, int y, boolean forward, boolean isLong, OnPress press) {
		super(x, y, isLong ? 30 : 23, 13, Component.empty(), press, DEFAULT_NARRATION);
		this.forward = forward;
		this.isLong = isLong;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTick) {
		int u = isLong ? 46 : 0, v = forward ? 192 : 205;
		if(isHoveredOrFocused())
			u += isLong ? 30 : 23;
		graphics.blit(TEXTURE, getX(), getY(), u, v, isLong ? 30 : 23, 13);
	}
}
