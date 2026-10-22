/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.lib.NecronomiconText;

/** NecroData added by other mods (AbyssalCraftAPI.registerNecronomiconData), 14 per page */
public class GuiNecronomiconOther extends GuiNecronomicon {

	private final List<Map.Entry<NecroData, Integer>> entries = new ArrayList<>();

	public GuiNecronomiconOther(int bookType) {
		super(bookType);
		entries.addAll(AbyssalCraftAPI.getNecronomiconData().entrySet());
		setTurnupLimit(Math.max(1, (entries.size() + 13) / 14));
	}

	@Override
	protected void init() {
		currentNecro = this;
		clearWidgets();
		doneButton();
		arrow(1, 215, true, false).visible = currTurnup < getTurnupLimit() - 1;
		arrow(2, 18, false, false);
		for(int n = currTurnup * 14; n < Math.min(entries.size(), (currTurnup + 1) * 14); n++) {
			var entry = entries.get(n);
			int slot = n - currTurnup * 14;
			// a tier can see its own and lower tiers' entries
			category(3 + n, slot < 7 ? 14 : 132, 24 + 17 * (slot % 7), entry.getValue() > getBookType() ? NecronomiconText.LABEL_LOCKED : entry.getKey().getTitle(),
					false, getItem(entry.getValue())).visible = entry.getValue() <= getBookType();
		}
	}

	@Override
	protected void actionPerformed(int id) {
		if(id == 0)
			onClose();
		else if(id == 1) {
			if(currTurnup < getTurnupLimit() - 1) ++currTurnup;
			init();
		} else if(id == 2) {
			if(currTurnup == 0)
				minecraft.setScreen(new GuiNecronomicon(getBookType()));
			else {
				--currTurnup;
				init();
			}
		} else if(id >= 3 && id - 3 < entries.size()) {
			var entry = entries.get(id - 3);
			minecraft.setScreen(new GuiNecronomiconEntry(getBookType(), entry.getKey(), this, getItem(entry.getValue())));
		}
	}

	@Override
	protected void drawIndexText() {
		drawTitle(localize(NecronomiconText.LABEL_OTHER));
	}
}
