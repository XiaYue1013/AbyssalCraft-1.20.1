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

/** Back to the Necronomicon's index */
public class ButtonHome extends Button {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/gui/necronomicon.png");

	public ButtonHome(int x, int y, OnPress press) {
		super(x, y, 18, 10, Component.empty(), press, DEFAULT_NARRATION);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTick) {
		graphics.blit(TEXTURE, getX(), getY(), isHoveredOrFocused() ? 124 : 106, 192, 18, 10);
	}
}
