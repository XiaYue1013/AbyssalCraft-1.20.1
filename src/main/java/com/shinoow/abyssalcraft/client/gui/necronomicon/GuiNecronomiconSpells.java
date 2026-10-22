/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.server.OpenSpellbookMessage;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.world.InteractionHand;

/** Spellbook index: open the spellbook (inscribing), the spell compendium, the spell information */
public class GuiNecronomiconSpells extends GuiNecronomicon {

	public GuiNecronomiconSpells(int bookType) {
		super(bookType);
	}

	@Override
	protected void init() {
		currentNecro = this;
		clearWidgets();
		doneButton();
		arrow(1, 215, true, false).visible = false;
		arrow(2, 18, false, false);
		category(3, 14, 24, "Create/modify spells", false, ACItems.necronomicon.get());
		category(4, 14, 41, "Open Compendium", false, ACItems.necronomicon.get());
		category(5, 14, 58, NecronomiconText.LABEL_INFO, false, ACItems.necronomicon.get());
	}

	@Override
	protected void actionPerformed(int id) {
		if(id == 0)
			onClose();
		else if(id == 2)
			minecraft.setScreen(new GuiNecronomicon(getBookType()));
		else if(id == 3) {
			var player = minecraft.player;
			InteractionHand hand = player.getMainHandItem().getItem() instanceof ItemNecronomicon ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			if(player.getItemInHand(hand).getItem() instanceof ItemNecronomicon)
				PacketDispatcher.sendToServer(new OpenSpellbookMessage(hand));
		} else if(id == 4)
			minecraft.setScreen(new GuiNecronomiconSpellEntry(getBookType(), this));
		else if(id == 5)
			minecraft.setScreen(new GuiNecronomiconEntry(getBookType(), AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("spells"), this));
	}

	@Override
	protected void drawIndexText() {
		drawTitle(localize(NecronomiconText.LABEL_SPELLBOOK));
		writeText(2, NecronomiconText.SPELL_INFO);
	}
}
