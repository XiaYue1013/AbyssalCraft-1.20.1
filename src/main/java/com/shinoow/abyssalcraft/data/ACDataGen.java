/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - data generation
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.data.worldgen.ACWorldgenProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Data generation entry point (./gradlew runData)
 */
@Mod.EventBusSubscriber(modid = AbyssalCraft.modid, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ACDataGen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
		// Upstream textures/models are All Rights Reserved and not part of the repository, so model texture
		// references are not validated during generation
		ExistingFileHelper unchecked = new ExistingFileHelper(Collections.emptyList(), Collections.emptySet(), false, null, null);
		ExistingFileHelper helper = event.getExistingFileHelper();

		gen.addProvider(event.includeClient(), new ACBlockStateProvider(output, unchecked));
		gen.addProvider(event.includeClient(), new ACItemModelProvider(output, unchecked));
		gen.addProvider(event.includeClient(), new ACModelMapProvider(output));

		gen.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
				List.of(new LootTableProvider.SubProviderEntry(ACBlockLoot::new, LootContextParamSets.BLOCK),
						new LootTableProvider.SubProviderEntry(ACChestLoot::new, LootContextParamSets.CHEST),
						new LootTableProvider.SubProviderEntry(ACEntityLoot::new, LootContextParamSets.ENTITY))));
		ACBlockTagsProvider blockTags = new ACBlockTagsProvider(output, lookup, helper);
		gen.addProvider(event.includeServer(), blockTags);
		gen.addProvider(event.includeServer(), new ACRecipeProvider(output));
		gen.addProvider(event.includeServer(), new net.minecraftforge.common.data.ForgeAdvancementProvider(output, lookup, helper, List.of(new ACAdvancementProvider())));
		gen.addProvider(event.includeServer(), new ACItemTagsProvider(output, lookup, blockTags.contentsGetter(), helper));
		ACWorldgenProvider worldgen = new ACWorldgenProvider(output, lookup);
		gen.addProvider(event.includeServer(), worldgen);
		gen.addProvider(event.includeServer(), new ACBiomeTagsProvider(output, worldgen.getRegistryProvider(), helper));
	}
}
