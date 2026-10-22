/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.List;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.recipe.CrystallizerRecipes;
import com.shinoow.abyssalcraft.api.recipe.Materialization;
import com.shinoow.abyssalcraft.api.recipe.MaterializerRecipes;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonHome;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonNextPage;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;

/**
 * Machines (Abyssal Wasteland Necronomicon and up): what the machines do, and the Transmutator (AW), Crystallizer (DL)
 * and Materializer (OMT) recipe lists, 12 (6 for the Materializer) recipes a turnup
 */
public class GuiNecronomiconMachines extends GuiNecronomicon {

	private ButtonNextPage buttonNextPage, buttonNextPageLong, buttonPreviousPage, buttonPreviousPageLong;
	private Button buttonDone;
	private ButtonHome buttonHome;
	private boolean isMInfo, isTra, isCry, isMat;
	private final GuiNecronomicon parent;

	public GuiNecronomiconMachines(int bookType, GuiNecronomicon parent) {
		super(bookType);
		this.parent = parent;
	}

	@Override
	public GuiNecronomicon withBookType(int type) {
		if(type < 1)
			isInvalid = true;
		return super.withBookType(type);
	}

	@Override
	protected void init() {
		if(isInvalid) {
			minecraft.setScreen(parent.withBookType(getBookType()));
			return;
		}
		currentNecro = this;
		if(isCry && getBookType() == 1 || isMat && getBookType() <= 2) {
			isInfo = isMInfo = isTra = isCry = isMat = false;
			currTurnup = 0;
		}
		clearWidgets();
		buttonDone = doneButton();
		buttonNextPage = arrow(1, isInfo ? 215 : 220, true, false);
		buttonNextPageLong = arrow(2, 203, true, true);
		buttonPreviousPage = arrow(3, 18, false, false);
		buttonPreviousPageLong = arrow(4, 23, false, true);
		buttonHome = add(new ButtonHome(left() + 118, 2 + 167, b -> actionPerformed(5)));
		if(!isInfo) {
			category(6, 14, 24, NecronomiconText.LABEL_INFO, false, ACItems.necronomicon.get());
			category(7, 14, 41, "container.abyssalcraft.transmutator", false, getItem(1));
			category(8, 14, 58, "container.abyssalcraft.crystallizer", false, getItem(2));
			category(9, 14, 75, "container.abyssalcraft.materializer", false, getItem(3));
		}
		updateButtons();
	}

	private void updateButtons() {
		buttonNextPage.visible = currTurnup < getTurnupLimit() - 1 && isInfo;
		buttonNextPageLong.visible = currTurnup < getTurnupLimit() - 5;
		buttonPreviousPage.visible = true;
		buttonPreviousPageLong.visible = currTurnup > 4;
		buttonDone.visible = true;
		buttonHome.visible = true;
	}

	private void reset() {
		isInfo = isMInfo = isTra = isCry = isMat = false;
		setTurnupLimit(2);
		init();
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
			else if(currTurnup == 0)
				reset();
			else --currTurnup;
		} else if(id == 4) {
			if(currTurnup > 4) currTurnup -= 5;
		} else if(id == 5) {
			if(!isInfo)
				minecraft.setScreen(new GuiNecronomicon(getBookType()));
			else {
				currTurnup = 0;
				reset();
			}
		} else if(id == 6) {
			isInfo = isMInfo = true;
			init();
		} else if(id == 7 && getBookType() >= 1) {
			isInfo = isTra = true;
			init();
		} else if(id == 8 && getBookType() >= 2) {
			isInfo = isCry = true;
			init();
		} else if(id == 9 && getBookType() >= 3) {
			isInfo = isMat = true;
			init();
		}
		if(buttonDone != null && minecraft.screen == this)
			updateButtons();
	}

	@Override
	protected void drawIndexText() {
		drawTitle(localize(NecronomiconText.LABEL_INFORMATION_MACHINES));
		writeText(2, NecronomiconText.MACHINES_INFO);
	}

	private void setTurnups(int size, boolean materializer) {
		int i = materializer ? 1 : 2;
		setTurnupLimit((size + 6 * i) / (6 * i));
	}

	@Override
	protected void drawInformationText(int x, int y) {
		int k = left();
		tooltipStack = ItemStack.EMPTY;
		if(isMInfo) {
			drawTitle(localize(NecronomiconText.LABEL_INFO));
			setTurnupLimit(2);
			ItemStack[] machines = currTurnup == 0 ? new ItemStack[] {new ItemStack(ACBlocks.transmutator.get()), new ItemStack(ACBlocks.crystallizer.get())}
			: new ItemStack[] {new ItemStack(ACBlocks.engraver.get()), new ItemStack(ACBlocks.materializer.get())};
			writeText(1, currTurnup == 0 ? NecronomiconText.MACHINE_INFO_1 : NecronomiconText.MACHINE_INFO_3, 50);
			writeText(2, currTurnup == 0 ? NecronomiconText.MACHINE_INFO_2 : NecronomiconText.MACHINE_INFO_4, 50);
			drawPicture(NecronomiconResources.ITEM, 0);
			renderItem(k + 60, 2 + 28, machines[0], x, y);
			drawPicture(NecronomiconResources.ITEM, 123);
			renderItem(k + 183, 2 + 28, machines[1], x, y);
		}
		if(isTra) {
			drawTitle(localize("container.abyssalcraft.transmutator"));
			List<TransmutatorRecipes.Transmutation> trans = TransmutatorRecipes.instance().getTransmutationList();
			setTurnups(trans.size(), false);
			drawPicture(NecronomiconResources.TRANSMUTATION, 0);
			for(int n = 0; n < trans.size(); n++)
				drawRow(n, currTurnup * 12, currTurnup * 12 + 6, (currTurnup + 1) * 12, x, y, trans.get(n).input(), trans.get(n).output().get(), null);
		}
		if(isCry) {
			drawTitle(localize("container.abyssalcraft.crystallizer"));
			List<CrystallizerRecipes.Crystallization> cryst = CrystallizerRecipes.instance().getCrystallizationList();
			setTurnups(cryst.size(), false);
			drawPicture(NecronomiconResources.TRANSMUTATION, 0);
			drawPicture(NecronomiconResources.CRYSTALLIZATION, 0);
			for(int n = 0; n < cryst.size(); n++)
				drawRow(n, currTurnup * 12, currTurnup * 12 + 6, (currTurnup + 1) * 12, x, y, cryst.get(n).input(), cryst.get(n).output1().get(), cryst.get(n).output2().get());
		}
		if(isMat) {
			drawTitle(localize("container.abyssalcraft.materializer"));
			List<Materialization> mat = MaterializerRecipes.instance().getMaterializationList();
			setTurnups(mat.size(), true);
			drawPicture(NecronomiconResources.MATERIALIZATION, 0);
			for(int n = currTurnup * 6; n < Math.min(mat.size(), (currTurnup + 1) * 6); n++) {
				Materialization m = mat.get(n);
				int row = 2 + 28 + (n - currTurnup * 6) * 21;
				int[] xs = {38, 59, 80, 145, 166};
				for(int i = 0; i < Math.min(5, m.input.length); i++)
					renderItem(k + xs[i], row, m.input[i].copy(), x, y);
				renderItem(k + 210, row, m.output.copy(), x, y);
			}
		}
		renderTooltip(x, y);
	}

	/** One recipe of the two-column Transmutator / Crystallizer lists */
	private void drawRow(int num, int low, int mid, int high, int x, int y, Object input, ItemStack out1, ItemStack out2) {
		int k = left();
		if(num >= low && num < mid) {
			int row = 2 + 28 + (num - low) * 21;
			renderObject(k + 18, row, input, x, y);
			renderItem(k + 62, row, out1, x, y);
			if(out2 != null) renderItem(k + 95, row, out2, x, y);
		} else if(num >= mid && num < high) {
			int row = 2 + 28 + (num - mid) * 21;
			renderObject(k + 141, row, input, x, y);
			renderItem(k + 185, row, out1, x, y);
			if(out2 != null) renderItem(k + 219, row, out2, x, y);
		}
	}
}
