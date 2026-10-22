/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - the advancements; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.function.Consumer;

import com.shinoow.abyssalcraft.AbyssalCraft;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;

/** The nine 1.12.2 advancements (getting the Necronomicon, then the ores and metals of each dimension); any listed item completes one */
public class ACAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper helper) {
		Advancement root = make(saver, helper, null, "root", "necro", "necronomicon", AbyssalCraft.loc("textures/block/ds.png"), "necronomicon");
		Advancement abyssalnite = make(saver, helper, root, "mine_abyssalnite", "mineaby", "abyssalnite_ore", null, "abyssalnite_ore");
		Advancement coralium = make(saver, helper, abyssalnite, "mine_coralium", "minecorgem", "coralium_gem", null, "coralium_gem", "coralium_ore",
				"coralium_gem_cluster_2", "coralium_gem_cluster_3", "coralium_gem_cluster_4", "coralium_gem_cluster_5", "coralium_gem_cluster_6",
				"coralium_gem_cluster_7", "coralium_gem_cluster_8", "coralium_gem_cluster_9");
		Advancement shadowGems = make(saver, helper, coralium, "shadow_gems", "shadowgems", "shadow_gem", null, "shadow_gem");
		Advancement abyssalCoralium = make(saver, helper, shadowGems, "mine_abyssal_coralium", "minecor", "abyssal_coralium_ore", null,
				"abyssal_coralium_ore", "pearlescent_coralium_ore", "liquified_coralium_ore");
		Advancement abyssalOres = make(saver, helper, abyssalCoralium, "mine_abyssal_ores", "mineabyores", "abyssal_diamond_ore", null,
				"abyssal_diamond_ore", "abyssal_gold_ore", "abyssal_iron_ore", "abyssal_copper_ore", "abyssal_tin_ore", "abyssal_nitre_ore");
		Advancement dreadlandsOres = make(saver, helper, abyssalOres, "mine_dreadlands_ores", "minedread", "dreaded_abyssalnite_ore", null,
				"dreadlands_abyssalnite_ore", "dreaded_abyssalnite_ore");
		Advancement dreadium = make(saver, helper, dreadlandsOres, "dreadium", "dreadium", "dreadium_ingot", null, "dreadium_ingot");
		make(saver, helper, dreadium, "ethaxium", "ethaxium", "ethaxium_ingot", null, "ethaxium_ingot");
	}

	private static Item item(String name) {
		return ForgeRegistries.ITEMS.getValue(AbyssalCraft.loc(name));
	}

	private static Advancement make(Consumer<Advancement> saver, ExistingFileHelper helper, Advancement parent, String id, String lang, String icon,
			ResourceLocation background, String... items) {
		Advancement.Builder builder = Advancement.Builder.advancement()
				.display(item(icon), Component.translatable("advancements.abyssalcraft." + lang + ".title"),
						Component.translatable("advancements.abyssalcraft." + lang + ".description"), background, FrameType.TASK, true, true, false)
				.requirements(RequirementsStrategy.OR);
		if(parent != null)
			builder.parent(parent);
		for(String name : items)
			builder.addCriterion(name, InventoryChangeTrigger.TriggerInstance.hasItems(item(name)));
		return builder.save(saver, AbyssalCraft.loc(id), helper);
	}
}
