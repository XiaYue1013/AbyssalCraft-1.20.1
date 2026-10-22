/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconCreationRitual;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconTransformationRitual;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import com.shinoow.abyssalcraft.lib.util.IHiddenRitual;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** The rituals of one book tier the player has unlocked: offerings around the altar, sacrifice, result, PE and place */
public class GuiNecronomiconRitualEntry extends GuiNecronomiconPagedList {

	private final Map<ResourceKey<Level>, String> dimToString = new HashMap<>();
	private final int ritualnum;
	private final List<NecronomiconRitual> rituals = new ArrayList<>();

	public GuiNecronomiconRitualEntry(int bookType, GuiNecronomicon gui, int ritualnum) {
		super(bookType, gui);
		this.ritualnum = ritualnum;
	}

	@Override
	public GuiNecronomicon withBookType(int type) {
		if(getBookType() > type)
			isInvalid = true;
		return super.withBookType(type);
	}

	@Override
	protected boolean hasEntries() {
		return !rituals.isEmpty();
	}

	@Override
	protected void initEntries() {
		dimToString.putAll(RitualRegistry.instance().getDimensionNameMappings());
		for(NecronomiconRitual ritual : RitualRegistry.instance().getRituals())
			if(ritual.getBookType() == ritualnum && isUnlocked(ritual.getUnlockCondition()) && !(ritual instanceof IHiddenRitual))
				rituals.add(ritual);
		setTurnupLimit(rituals.size());
	}

	private String dimension(ResourceKey<Level> dim) {
		if(dim == null) return localize(NecronomiconText.LABEL_ANYWHERE);
		return localize(dimToString.getOrDefault(dim, dim.location().toString()));
	}

	@Override
	protected void drawEntry(int index, int x, int y) {
		NecronomiconRitual ritual = rituals.get(index);
		int k = left();
		drawTitle(localize(ritual.getUnlocalizedName()));
		if(ritual.requiresSacrifice())
			graphics.drawWordWrap(font, styled(localize(NecronomiconText.LABEL_SACRIFICE), false), k + 138, 164, 107, 0xC40000);
		writeText(1, localize(NecronomiconText.LABEL_REQUIRED_ENERGY) + ": " + ritual.getReqEnergy() + " PE", 125);
		writeText(2, localize(NecronomiconText.LABEL_LOCATION) + ": " + dimension(ritual.getDimension()));
		if(!ritual.getDescription().startsWith("ac.ritual."))
			writeText(2, ritual.getDescription(), 48);
		drawPicture(NecronomiconResources.RITUAL, 0);
		if(ritual.getSacrifice() != null)
			drawPicture(NecronomiconResources.RITUAL_INFUSION, 0);
		if(ritual instanceof NecronomiconCreationRitual)
			drawPicture(NecronomiconResources.RITUAL_CREATION, 0);
		tooltipStack = ItemStack.EMPTY;
		Object[] offerings = new Object[8];
		Object[] own = ritual instanceof NecronomiconTransformationRitual t ? t.getCombinedContent() : ritual.getOfferings();
		System.arraycopy(own, 0, offerings, 0, Math.min(8, own.length));
		int[][] spots = {{58, 30}, {84, 40}, {94, 66}, {84, 92}, {58, 103}, {32, 92}, {22, 66}, {32, 40}};
		for(int i = 0; i < 8; i++)
			renderObject(k + spots[i][0], 2 + spots[i][1], offerings[i], x, y);
		renderItem(k + 58, 2 + 66, APIUtils.convertToStack(ritual.getSacrifice()), x, y);
		if(ritual instanceof NecronomiconCreationRitual creation)
			renderItem(k + 58, 2 + 139, creation.getItem(), x, y);
		renderTooltip(x, y);
	}
}
