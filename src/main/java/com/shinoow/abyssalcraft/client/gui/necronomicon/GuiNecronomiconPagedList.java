/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonHome;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonNextPage;
import net.minecraft.client.gui.components.Button;

/**
 * The navigation shared by the one-entry-per-turnup screens (rituals, spells, places of power): page arrows, five-page
 * arrows, home and back to the parent from the first page. 1.12.2 had a copy in each screen.
 */
public abstract class GuiNecronomiconPagedList extends GuiNecronomicon {

	private ButtonNextPage buttonNextPage, buttonNextPageLong, buttonPreviousPage, buttonPreviousPageLong;
	private Button buttonDone;
	private ButtonHome buttonHome;
	protected final GuiNecronomicon parent;

	protected GuiNecronomiconPagedList(int bookType, GuiNecronomicon parent) {
		super(bookType);
		this.parent = parent;
		isInfo = true;
	}

	/** Fills the entries (once) and sets the turnup limit */
	protected abstract void initEntries();

	protected abstract boolean hasEntries();

	@Override
	protected void init() {
		if(isInvalid) {
			minecraft.setScreen(parent.withBookType(getBookType()));
			return;
		}
		currentNecro = this;
		if(!hasEntries())
			initEntries();
		clearWidgets();
		buttonDone = doneButton();
		buttonNextPage = arrow(1, 215, true, false);
		buttonNextPageLong = arrow(2, 203, true, true);
		buttonPreviousPage = arrow(3, 18, false, false);
		buttonPreviousPageLong = arrow(4, 23, false, true);
		buttonHome = add(new ButtonHome(left() + 118, 2 + 167, b -> actionPerformed(5)));
		updateButtons();
	}

	protected void updateButtons() {
		buttonNextPage.visible = currTurnup < getTurnupLimit() - 1;
		buttonNextPageLong.visible = currTurnup < getTurnupLimit() - 5;
		buttonPreviousPage.visible = true;
		buttonPreviousPageLong.visible = currTurnup > 4;
		buttonDone.visible = true;
		buttonHome.visible = true;
	}

	@Override
	protected void actionPerformed(int id) {
		if(id == 0)
			onClose();
		else if(id == 1) {
			if(currTurnup < getTurnupLimit() - 1) ++currTurnup;
		} else if(id == 2) {
			if(currTurnup < getTurnupLimit() - 5) currTurnup += 5;
		} else if(id == 3) {
			if(currTurnup == 0) {
				isInfo = false;
				minecraft.setScreen(parent.withBookType(getBookType()));
			} else --currTurnup;
		} else if(id == 4) {
			if(currTurnup > 4) currTurnup -= 5;
		} else if(id == 5)
			minecraft.setScreen(new GuiNecronomicon(getBookType()));
		if(buttonDone != null && minecraft.screen == this)
			updateButtons();
	}

	@Override
	protected void drawInformationText(int x, int y) {
		if(currTurnup < getTurnupLimit())
			drawEntry(currTurnup, x, y);
	}

	protected abstract void drawEntry(int index, int x, int y);
}
