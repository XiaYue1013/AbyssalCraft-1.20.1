/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon.buttons;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.client.gui.necronomicon.GuiNecronomicon;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** An index entry: the book's icon and a label (Aklo when locked) */
public class ButtonCategory extends Button {

	private final Item icon;
	private final boolean locked;

	public ButtonCategory(int x, int y, String label, boolean locked, Item icon, OnPress press) {
		super(x, y, 110, 16, Component.literal(I18n.get(label)), press, DEFAULT_NARRATION);
		this.icon = icon;
		this.locked = locked;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTick) {
		if(isHoveredOrFocused())
			graphics.fillGradient(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF, 0x00505000);
		graphics.blit(texture(locked ? ACItems.oblivion_catalyst.get() : icon), getX(), getY(), 0, 0, 16, 16, 16, 16);
		graphics.drawString(net.minecraft.client.Minecraft.getInstance().font,
				GuiNecronomicon.styled(locked ? NecronomiconText.LABEL_TEST : getMessage().getString(), locked), getX() + 17, getY() + 3, 0, false);
	}

	private static ResourceLocation texture(Item item) {
		String name = item == ACItems.abyssalnomicon.get() ? "abyssalnomicon"
				: item == ACItems.abyssal_wasteland_necronomicon.get() ? "necronomicon_cor"
						: item == ACItems.dreadlands_necronomicon.get() ? "necronomicon_dre"
								: item == ACItems.omothol_necronomicon.get() ? "necronomicon_omt"
										: item == ACItems.oblivion_catalyst.get() ? "necronahicon" : "necronomicon";
		return AbyssalCraft.loc("textures/item/" + name + ".png");
	}
}
