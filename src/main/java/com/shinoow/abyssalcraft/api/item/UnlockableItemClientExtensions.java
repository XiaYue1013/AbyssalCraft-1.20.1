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
package com.shinoow.abyssalcraft.api.item;

import com.shinoow.abyssalcraft.api.APIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Client item extension that renders the name of locked {@link IUnlockableItem}s in Aklo.<br>
 * Only ever instantiated on the client (from {@code Item#initializeClient}).
 */
public class UnlockableItemClientExtensions implements IClientItemExtensions {

	public static final UnlockableItemClientExtensions INSTANCE = new UnlockableItemClientExtensions();

	private static Font akloFont;

	/**
	 * Sets the Aklo font (done by AbyssalCraft during client setup)
	 */
	public static void setAkloFont(Font font) {
		if(akloFont == null)
			akloFont = font;
	}

	public static Font getAkloFont() {
		return akloFont;
	}

	@Override
	public Font getFont(ItemStack stack, FontContext context) {
		if(akloFont != null && APIUtils.isLocked(stack, Minecraft.getInstance().player))
			return akloFont;
		return null;
	}
}
