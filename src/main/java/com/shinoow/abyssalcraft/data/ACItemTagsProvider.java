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

import java.util.concurrent.CompletableFuture;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACTags;
import com.shinoow.abyssalcraft.init.ItemHandler;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Item tags (replacement for the 1.12.2 OreDictionary entries)
 */
public class ACItemTagsProvider extends ItemTagsProvider {

	public ACItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper helper) {
		super(output, lookup, blockTags, AbyssalCraft.modid, helper);
	}

	private static TagKey<Item> forge(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}

	private static TagKey<Block> forgeBlock(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		copy(BlockTags.SLABS, ItemTags.SLABS);
		copy(BlockTags.STAIRS, ItemTags.STAIRS);
		copy(BlockTags.WALLS, ItemTags.WALLS);
		copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		copy(BlockTags.FENCES, ItemTags.FENCES);
		copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
		copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		copy(BlockTags.LEAVES, ItemTags.LEAVES);
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		copy(BlockTags.PLANKS, ItemTags.PLANKS);
		copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		copy(Tags.Blocks.STONE, Tags.Items.STONE);
		copy(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
		copy(Tags.Blocks.GLASS, Tags.Items.GLASS);
		copy(Tags.Blocks.ORES, Tags.Items.ORES);
		for(String s : new String[] {"abyssalnite", "refined_coralium", "dreadium", "ethaxium"})
			copy(forgeBlock("storage_blocks/" + s), forge("storage_blocks/" + s));
		for(String s : new String[] {"abyssalnite", "coralium", "saltpeter", "iron", "gold", "diamond", "tin", "copper"})
			copy(forgeBlock("ores/" + s), forge("ores/" + s));

		tag(ACTags.Items.ABYSSALNITE_INGOTS).add(ACItems.abyssalnite_ingot.get());
		tag(ACTags.Items.REFINED_CORALIUM_INGOTS).add(ACItems.refined_coralium_ingot.get());
		tag(ACTags.Items.DREADIUM_INGOTS).add(ACItems.dreadium_ingot.get());
		tag(ACTags.Items.ETHAXIUM_INGOTS).add(ACItems.ethaxium_ingot.get());
		tag(ACTags.Items.TIN_INGOTS).add(ACItems.tin_ingot.get());
		tag(ACTags.Items.COPPER_INGOTS).add(ACItems.copper_ingot.get());
		tag(Tags.Items.INGOTS).addTags(ACTags.Items.ABYSSALNITE_INGOTS, ACTags.Items.REFINED_CORALIUM_INGOTS, ACTags.Items.DREADIUM_INGOTS,
				ACTags.Items.ETHAXIUM_INGOTS, ACTags.Items.TIN_INGOTS, ACTags.Items.COPPER_INGOTS);
		tag(forge("nuggets/abyssalnite")).add(ACItems.abyssalnite_nugget.get());
		tag(forge("nuggets/refined_coralium")).add(ACItems.refined_coralium_nugget.get());
		tag(forge("nuggets/dreadium")).add(ACItems.dreadium_nugget.get());
		tag(forge("nuggets/ethaxium")).add(ACItems.ethaxium_nugget.get());
		tag(ACTags.Items.NITRE).add(ACItems.nitre.get());
		tag(ACTags.Items.SULFUR).add(ACItems.sulfur.get());
		tag(forge("gems/coralium")).add(ACItems.coralium_gem.get());
		tag(forge("gems/coralium_pearl")).add(ACItems.coralium_pearl.get());
		tag(Tags.Items.GEMS).add(ACItems.coralium_gem.get(), ACItems.coralium_pearl.get());
		tag(ItemTags.COALS).add(ACItems.charcoal.get());

		var crystals = tag(ACTags.Items.CRYSTALS);
		ItemHandler.CRYSTALS.forEach(c -> crystals.add(c.get()));
		ItemHandler.CRYSTAL_SHARDS.forEach(c -> crystals.add(c.get()));
		ItemHandler.CRYSTAL_FRAGMENTS.forEach(c -> crystals.add(c.get()));
		for(int i = 0; i < ACItems.CRYSTAL_NAMES.length; i++)
			tag(ItemTags.create(new ResourceLocation("abyssalcraft", "crystals/" + ACItems.CRYSTAL_NAMES[i]))).add(ACItems.crystal(i).get());

		tag(ACTags.Items.COINS).add(ACItems.coin.get(), ACItems.cthulhu_engraved_coin.get(), ACItems.elder_engraved_coin.get(),
				ACItems.jzahar_engraved_coin.get(), ACItems.hastur_engraved_coin.get(), ACItems.azathoth_engraved_coin.get(),
				ACItems.nyarlathotep_engraved_coin.get(), ACItems.yog_sothoth_engraved_coin.get(), ACItems.shub_niggurath_engraved_coin.get());
	}
}
