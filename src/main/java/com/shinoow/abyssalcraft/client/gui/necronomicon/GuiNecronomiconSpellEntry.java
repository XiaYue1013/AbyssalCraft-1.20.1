/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellRegistry;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import net.minecraft.world.item.ItemStack;

/** Spell compendium: every unlocked spell of this book tier with its reagents and the scrolls it fits on */
public class GuiNecronomiconSpellEntry extends GuiNecronomiconPagedList {

	private final List<Spell> spells = new ArrayList<>();

	public GuiNecronomiconSpellEntry(int bookType, GuiNecronomicon gui) {
		super(bookType, gui);
	}

	@Override
	public GuiNecronomicon withBookType(int type) {
		// also called by the super constructor, before the list exists
		if(getBookType() != type && spells != null) {
			currTurnup = 0;
			spells.clear();
		}
		return super.withBookType(type);
	}

	@Override
	protected boolean hasEntries() {
		return !spells.isEmpty();
	}

	@Override
	protected void initEntries() {
		for(Spell spell : SpellRegistry.instance().getSpells())
			if(isUnlocked(spell.getUnlockCondition()) && spell.getBookType() <= getBookType())
				spells.add(spell);
		setTurnupLimit(spells.size());
	}

	@Override
	protected void drawEntry(int index, int x, int y) {
		Spell spell = spells.get(index);
		int k = left();
		drawTitle(localize(spell.getUnlocalizedName()));
		writeText(1, "PE per cast: " + spell.getReqEnergy() + " PE", 125);
		writeText(1, "Spell Type: " + (spell.requiresCharging() ? "Charging" : "Instant"), 135);
		writeText(2, localize(spell.getUnlocalizedName() + ".desc"));
		drawPicture(NecronomiconResources.SPELL, 0);
		tooltipStack = ItemStack.EMPTY;
		Object[] reagents = spell.getReagents();
		int[][] spots = {{58, 41}, {83, 62}, {72, 91}, {45, 91}, {33, 62}};
		for(int i = 0; i < Math.min(5, reagents.length); i++)
			renderObject(k + spots[i][0], 2 + spots[i][1], reagents[i] instanceof ItemStack || reagents[i] instanceof net.minecraft.tags.TagKey
					|| reagents[i] instanceof ItemStack[] ? reagents[i] : APIUtils.convertToStack(reagents[i]), x, y);
		if(!spell.getParchment().isEmpty())
			renderItem(k + 58, 2 + 66, spell.getParchment(), x, y);
		else renderObject(k + 58, 2 + 66, SpellRegistry.instance().getScrolls(spell.getScrollType()), x, y);
		renderTooltip(x, y);
	}
}
