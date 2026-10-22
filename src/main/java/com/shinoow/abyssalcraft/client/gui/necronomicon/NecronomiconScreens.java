/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.function.Consumer;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Opens the Necronomicon screens in given states, for the client smoke test (presses its buttons by id) */
public class NecronomiconScreens {

	/** A named way to reach a screen: open it, then press the given buttons (ids of the screen showing at that moment) */
	public record Step(String name, int bookType, String start, int... presses) {}

	public static final Step[] STEPS = {
			new Step("necro_index", 4, "index"),
			new Step("necro_huh", 4, "index", 7),
			new Step("necro_information", 4, "index", 3),
			new Step("necro_info_acinfo", 4, "index", 3, 6),
			new Step("necro_info_overworld", 4, "index", 3, 11),
			new Step("necro_overworld_materials", 4, "index", 3, 11, 6),
			new Step("necro_overworld_specialmaterials", 4, "index", 3, 11, 9),
			new Step("necro_greatoldones", 4, "index", 3, 7, 6),
			new Step("necro_rituals", 4, "index", 5),
			new Step("necro_rituals_overworld", 4, "index", 5, 7),
			new Step("necro_rituals_omothol", 4, "index", 5, 10),
			new Step("necro_spells", 4, "index", 4),
			new Step("necro_spell_compendium", 4, "index", 4, 4),
			new Step("necro_pe", 4, "index", 6),
			new Step("necro_pe_places", 4, "index", 6, 9),
			new Step("necro_places_of_power", 4, "index", 6, 9, 7),
			new Step("necro_machines", 4, "index", 3, 10),
			new Step("necro_machines_transmutator", 4, "index", 3, 10, 7),
			new Step("necro_machines_crystallizer", 4, "index", 3, 10, 8),
			new Step("necro_machines_materializer", 4, "index", 3, 10, 9),
			new Step("necro_misc", 4, "index", 9),
			new Step("necro_misc_enchantments", 4, "index", 9, 7),
			new Step("necro_basic_locked", 0, "index", 3, 12)};

	public static void open(Step step, Consumer<String> log) {
		Minecraft mc = Minecraft.getInstance();
		GuiNecronomicon.currentNecro = null;
		mc.setScreen(new GuiNecronomicon(step.bookType()));
		for(int id : step.presses())
			if(mc.screen instanceof GuiNecronomicon gui)
				gui.actionPerformed(id);
			else log.accept(step.name() + ": no Necronomicon screen to press " + id + " on (" + mc.screen + ")");
		log.accept(step.name() + ": " + (mc.screen == null ? "no screen" : mc.screen.getClass().getSimpleName()));
	}

	/** True once every internal entry is available (common setup done) */
	public static boolean ready() {
		return AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("overworld") != null;
	}

	public static Screen current() {
		return Minecraft.getInstance().screen;
	}
}
