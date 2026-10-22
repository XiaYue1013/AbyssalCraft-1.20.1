/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import com.shinoow.abyssalcraft.api.necronomicon.CraftingStack;
import com.shinoow.abyssalcraft.api.necronomicon.GuiInstance;
import com.shinoow.abyssalcraft.api.necronomicon.INecroData;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData.Chapter;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData.Page;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonCategory;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonHome;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonNextPage;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** A NecroData: index of its chapters/pages/sub-entries, and the chapter pages themselves */
public class GuiNecronomiconEntry extends GuiNecronomicon {

	private ButtonNextPage buttonNextPage, buttonNextPageLong, buttonPreviousPage, buttonPreviousPageLong;
	private Button buttonDone;
	private ButtonHome buttonHome;
	private ButtonCategory[] buttons;
	protected NecroData data;
	protected GuiNecronomicon parent;
	private int currentData = -1;

	public GuiNecronomiconEntry(int bookType, NecroData nd, GuiNecronomicon gui) {
		super(bookType);
		data = nd;
		parent = gui;
		buttons = new ButtonCategory[nd.getContainedData().size()];
		if(!isUnlockedSafe(nd))
			isInvalid = true;
	}

	public GuiNecronomiconEntry(int bookType, NecroData nd, GuiNecronomicon gui, Item item) {
		this(bookType, nd, gui);
	}

	private boolean isUnlockedSafe(NecroData nd) {
		return minecraft == null || isUnlocked(nd.getCondition());
	}

	@Override
	public GuiNecronomicon withBookType(int type) {
		super.withBookType(type);
		if(data != null && minecraft != null && !isUnlocked(data.getCondition()))
			isInvalid = true;
		return this;
	}

	@Override
	protected void init() {
		if(isInvalid) {
			minecraft.setScreen(parent.withBookType(getBookType()));
			return;
		}
		currentNecro = this;
		clearWidgets();
		buttonDone = doneButton();
		buttonNextPage = arrow(1, 215, true, false);
		buttonNextPageLong = arrow(2, 203, true, true);
		buttonPreviousPage = arrow(3, 18, false, false);
		buttonPreviousPageLong = arrow(4, 23, false, true);
		buttonHome = add(new ButtonHome(left() + 118, 2 + 167, b -> actionPerformed(5)));
		if(!isInfo && data != null)
			for(int n = 0; n < data.getContainedData().size(); n++) {
				INecroData nd = data.getContainedData().get(n);
				buttons[n] = category(6 + n, n < 7 ? 14 : 132, 24 + 17 * (n < 7 ? n : n - 7), nd.getTitle(), !isUnlocked(nd.getCondition()), getItem(nd.getDisplayIcon()));
			}
		updateButtons();
		updateSidebarIndex(this);
	}

	private void updateButtons() {
		buttonNextPage.visible = currTurnup < getTurnupLimit() - 1 && isInfo;
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
			if(currTurnup == 0 && !isInfo)
				minecraft.setScreen(parent.withBookType(getBookType()));
			else if(currTurnup == 0) {
				isInfo = false;
				currentData = -1;
				setTurnupLimit(2);
				init();
			} else --currTurnup;
		} else if(id == 4) {
			if(currTurnup > 4) currTurnup -= 5;
		} else if(id == 5) {
			if(!isInfo)
				minecraft.setScreen(new GuiNecronomicon(getBookType()));
			else {
				currTurnup = 0;
				isInfo = false;
				currentData = -1;
				setTurnupLimit(2);
				init();
			}
		} else if(id >= 6 && data.getContainedData().size() >= id - 5) {
			INecroData nd = data.getContainedData().get(id - 6);
			if(isUnlocked(nd.getCondition()))
				if(nd instanceof GuiInstance gi)
					minecraft.setScreen(gi.getOpenGui(getBookType(), this));
				else if(nd instanceof NecroData sub)
					minecraft.setScreen(new GuiNecronomiconEntry(getBookType(), sub, this));
				else {
					currentData = id - 6;
					isInfo = true;
					init();
				}
		}
		if(buttonDone != null && minecraft.screen == this)
			updateButtons();
	}

	@Override
	protected void drawInformationText(int x, int y) {
		if(currentData != -1) {
			INecroData nd = data.getContainedData().get(currentData);
			if(nd instanceof Chapter chapter) {
				drawTitle(localize(chapter.getTitle()));
				setTurnupLimit(chapter.getTurnupAmount());
				int num = (currTurnup + 1) * 2;
				addPage(chapter.getPage(num - 1), chapter.getPage(num), num, x, y);
			} else {
				setTurnupLimit(1);
				addPage((Page) nd, null, 2, x, y);
			}
		}
		updateButtons();
	}

	private void addPage(Page page1, Page page2, int displayNum, int x, int y) {
		String text1 = page1 != null ? page1.getText() : "", text2 = page2 != null ? page2.getText() : "";
		Object icon1 = page1 != null ? page1.getIcon() : null, icon2 = page2 != null ? page2.getIcon() : null;
		boolean locked1 = page1 != null && !isUnlocked(page1.getCondition()), locked2 = page2 != null && !isUnlocked(page2.getCondition());
		tooltipStack = ItemStack.EMPTY;
		writeTexts(icon1, icon2, text1, text2, locked1, locked2);
		writeText(1, String.valueOf(displayNum - 1), 165, 50);
		writeText(2, String.valueOf(displayNum), 165, 50);
		drawIcon(icon1, locked1, 0, x, y);
		drawIcon(icon2, locked2, 123, x, y);
		renderTooltip(x, y);
	}

	private void drawIcon(Object icon, boolean locked, int n, int x, int y) {
		int k = left();
		if(icon instanceof ItemStack stack) {
			drawPicture(locked ? MISSING_ITEM : NecronomiconResources.ITEM, n);
			if(!locked)
				renderItem(k + 60 + n, 2 + 28, stack, x, y);
		} else if(icon instanceof ResourceLocation res)
			drawPicture(locked ? MISSING_PICTURE : res, n);
		else if(icon instanceof CraftingStack crafting) {
			drawPicture(locked ? MISSING_RECIPE : NecronomiconResources.CRAFTING, n);
			if(!locked) {
				renderItem(k + 93 + n, 2 + 52, crafting.getOutput(), x, y);
				Object[] recipe = crafting.getRecipe();
				for(int i = 0; i <= 2; i++) {
					renderObject(k + 24 + n + i * 21, 2 + 31, recipe[i], x, y);
					renderObject(k + 24 + n + i * 21, 2 + 52, recipe[3 + i], x, y);
					renderObject(k + 24 + n + i * 21, 2 + 73, recipe[6 + i], x, y);
				}
			}
		} else if(icon instanceof String)
			// remote images aren't downloaded in the port
			drawPicture(MISSING_PICTURE, n);
	}

	private void writeTexts(Object icon1, Object icon2, String text1, String text2, boolean locked1, boolean locked2) {
		writePageText(1, icon1, text1, locked1);
		writePageText(2, icon2, text2, locked2);
	}

	private void writePageText(int page, Object icon, String text, boolean locked) {
		if(icon instanceof ItemStack)
			writeText(page, locked ? unknown95 : text, 50, locked);
		else if(icon instanceof ResourceLocation || icon instanceof String)
			writeText(page, locked ? unknown50_1 : text, 100, locked);
		else if(icon instanceof CraftingStack)
			writeText(page, locked ? unknown50_2 : text, 95, locked);
		else writeText(page, locked ? unknownFull : text, locked);
	}

	@Override
	protected void drawIndexText() {
		boolean locked = !isUnlocked(data.getCondition());
		graphics.drawWordWrap(font, styled(locked ? NecronomiconText.LABEL_TEST : localize(data.getTitle()), locked), left() + 17, 18, 116, 0xC40000);
		if(data.hasText())
			writeText(2, locked ? unknownFull : data.getText(), locked);
	}
}
