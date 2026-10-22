/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.crafting;

import java.util.Map;
import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * Recipe condition {"type": "abyssalcraft:config", "option": "..."}: the recipe only loads while that config option
 * is on. 1.12.2 didn't register the items behind "foodstuffs" / "upgradeKits" when they were off (so their recipes
 * vanished); this port always registers them and switches the recipes off instead.
 */
public record ConfigCondition(String option) implements ICondition {

	public static final ResourceLocation ID = AbyssalCraft.loc("config");
	private static final Map<String, BooleanSupplier> OPTIONS = Map.of(
			"foodstuffs", () -> ACConfig.foodstuff,
			"upgradeKits", () -> ACConfig.upgrade_kits,
			"smeltingRecipes", () -> ACConfig.smeltingRecipes);

	public ConfigCondition {
		if(!OPTIONS.containsKey(option))
			throw new IllegalArgumentException("Unknown config option " + option);
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		return OPTIONS.get(option).getAsBoolean();
	}

	public static class Serializer implements IConditionSerializer<ConfigCondition> {

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ConfigCondition value) {
			json.addProperty("option", value.option());
		}

		@Override
		public ConfigCondition read(JsonObject json) {
			return new ConfigCondition(GsonHelper.getAsString(json, "option"));
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	}
}
