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
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.BlockACFire;
import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;
import com.shinoow.abyssalcraft.init.ACTags;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.BlockInfo;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Block tags: tool/harvest level tags (1.12.2 harvest levels), dragon/wither immunity, and the vanilla block families
 */
public class ACBlockTagsProvider extends BlockTagsProvider {

	public ACBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper helper) {
		super(output, lookup, AbyssalCraft.modid, helper);
	}

	private static TagKey<Block> forge(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		BlockHandler.INFO.forEach((name, info) -> {
			Block block = info.block.get();
			switch(info.tool) {
			case PICKAXE -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
			case AXE -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
			case SHOVEL -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
			case HOE -> tag(BlockTags.MINEABLE_WITH_HOE).add(block);
			case NONE -> {}
			}
			if(info.tool != BlockInfo.Tool.NONE)
				switch(info.harvestLevel) {
				case 1 -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
				case 2 -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
				case 3 -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
				case 4 -> tag(ACTags.Blocks.NEEDS_ABYSSALNITE_TOOL).add(block);
				case 5 -> tag(ACTags.Blocks.NEEDS_REFINED_CORALIUM_TOOL).add(block);
				case 6, 7 -> tag(ACTags.Blocks.NEEDS_DREADIUM_TOOL).add(block);
				case 8 -> tag(ACTags.Blocks.NEEDS_ETHAXIUM_TOOL).add(block);
				default -> {}
				}
			if(info.dragonImmune)
				tag(BlockTags.DRAGON_IMMUNE).add(block);
			if(info.witherImmune)
				tag(BlockTags.WITHER_IMMUNE).add(block);

			if(block instanceof SlabBlock) tag(BlockTags.SLABS).add(block);
			if(block instanceof StairBlock) tag(BlockTags.STAIRS).add(block);
			if(block instanceof WallBlock) tag(BlockTags.WALLS).add(block);
			if(block instanceof FenceBlock) tag(info.tool == BlockInfo.Tool.AXE ? BlockTags.WOODEN_FENCES : BlockTags.FENCES).add(block);
			if(block instanceof ButtonBlock) tag(info.tool == BlockInfo.Tool.AXE ? BlockTags.WOODEN_BUTTONS : BlockTags.STONE_BUTTONS).add(block);
			if(block instanceof PressurePlateBlock) tag(info.tool == BlockInfo.Tool.AXE ? BlockTags.WOODEN_PRESSURE_PLATES : BlockTags.STONE_PRESSURE_PLATES).add(block);
			if(block instanceof DoorBlock) tag(BlockTags.WOODEN_DOORS).add(block);
			if(block instanceof LeavesBlock) tag(BlockTags.LEAVES).add(block);
			if(block instanceof SaplingBlock) tag(BlockTags.SAPLINGS).add(block);
			if(name.endsWith("_ore")) tag(Tags.Blocks.ORES).add(block);
			if(block instanceof BlockACFire || block instanceof FireBlock) tag(BlockTags.FIRE).add(block);
			if(block instanceof BlockACPortal) tag(BlockTags.PORTALS).add(block);
		});

		tag(BlockTags.WOODEN_SLABS).add(ACBlocks.darklands_oak_slab.get());
		tag(BlockTags.WOODEN_STAIRS).add(ACBlocks.darklands_oak_stairs.get());
		tag(BlockTags.PLANKS).add(ACBlocks.darklands_oak_planks.get(), ACBlocks.dreadlands_planks.get());
		tag(BlockTags.LOGS_THAT_BURN).add(ACBlocks.darklands_oak_wood.get(), ACBlocks.darklands_oak_wood_2.get(), ACBlocks.dreadlands_log.get());
		tag(BlockTags.BEACON_BASE_BLOCKS).add(ACBlocks.abyssalnite_block.get(), ACBlocks.refined_coralium_block.get(),
				ACBlocks.dreadium_block.get(), ACBlocks.ethaxium_block.get());
		tag(BlockTags.DIRT).add(ACBlocks.dreadlands_grass.get(), ACBlocks.dreadlands_dirt.get());
		tag(BlockTags.ANIMALS_SPAWNABLE_ON).add(ACBlocks.dreadlands_grass.get());
		tag(Tags.Blocks.STONE).add(ACBlocks.darkstone.get(), ACBlocks.abyssal_stone.get(), ACBlocks.dreadstone.get(),
				ACBlocks.abyssalnite_stone.get(), ACBlocks.coralium_stone.get(), ACBlocks.omothol_stone.get(), ACBlocks.monolith_stone.get());
		tag(Tags.Blocks.COBBLESTONE).add(ACBlocks.darkstone_cobblestone.get(), ACBlocks.abyssal_cobblestone.get(), ACBlocks.dreadstone_cobblestone.get(),
				ACBlocks.abyssalnite_cobblestone.get(), ACBlocks.coralium_cobblestone.get());
		tag(Tags.Blocks.GLASS).add(ACBlocks.abyssal_sand_glass.get());
		tag(forge("storage_blocks/abyssalnite")).add(ACBlocks.abyssalnite_block.get());
		tag(forge("storage_blocks/refined_coralium")).add(ACBlocks.refined_coralium_block.get());
		tag(forge("storage_blocks/dreadium")).add(ACBlocks.dreadium_block.get());
		tag(forge("storage_blocks/ethaxium")).add(ACBlocks.ethaxium_block.get());
		tag(forge("ores/abyssalnite")).add(ACBlocks.abyssalnite_ore.get(), ACBlocks.dreadlands_abyssalnite_ore.get());
		tag(forge("ores/coralium")).add(ACBlocks.coralium_ore.get(), ACBlocks.abyssal_coralium_ore.get());
		tag(forge("ores/saltpeter")).add(ACBlocks.nitre_ore.get(), ACBlocks.abyssal_nitre_ore.get());
		tag(forge("ores/iron")).add(ACBlocks.abyssal_iron_ore.get());
		tag(forge("ores/gold")).add(ACBlocks.abyssal_gold_ore.get());
		tag(forge("ores/diamond")).add(ACBlocks.abyssal_diamond_ore.get());
		tag(forge("ores/tin")).add(ACBlocks.abyssal_tin_ore.get());
		tag(forge("ores/copper")).add(ACBlocks.abyssal_copper_ore.get());

		// 1.12.2 MapGenCavesAC: every AC stone except ethaxium, abyssal sand and the Dreadlands soil, on top of vanilla's
		tag(ACTags.Blocks.CARVER_REPLACEABLES).addTag(BlockTags.OVERWORLD_CARVER_REPLACEABLES).add(ACBlocks.darkstone.get(), ACBlocks.abyssal_stone.get(),
				ACBlocks.dreadstone.get(), ACBlocks.abyssalnite_stone.get(), ACBlocks.coralium_stone.get(), ACBlocks.omothol_stone.get(),
				ACBlocks.monolith_stone.get(), ACBlocks.abyssal_sand.get(), ACBlocks.fused_abyssal_sand.get(), ACBlocks.dreadlands_dirt.get(),
				ACBlocks.dreadlands_grass.get(), ACBlocks.liquified_coralium_ore.get(), ACBlocks.abyssal_coralium_ore.get(), ACBlocks.abyssal_nitre_ore.get(),
				ACBlocks.abyssal_iron_ore.get(), ACBlocks.abyssal_gold_ore.get(), ACBlocks.abyssal_diamond_ore.get(), ACBlocks.abyssal_tin_ore.get(),
				ACBlocks.abyssal_copper_ore.get(), ACBlocks.pearlescent_coralium_ore.get(), ACBlocks.dreaded_abyssalnite_ore.get(),
				ACBlocks.dreadlands_abyssalnite_ore.get());

		// Blocks acid and ooze never affect (in 1.12.2: unbreakable blocks and ethaxium)
		tag(ACTags.Blocks.ACID_RESISTANT).addTag(BlockTags.WITHER_IMMUNE);
		tag(ACTags.Blocks.OOZE_BLACKLIST).addTag(BlockTags.WITHER_IMMUNE);
	}
}
