/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.Set;
import java.util.stream.Collectors;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.BlockInfo;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Block loot tables (1.12.2 getItemDropped/quantityDropped logic expressed as loot tables)
 */
public class ACBlockLoot extends BlockLootSubProvider {

	public ACBlockLoot() {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	private static Item item(String id) {
		ResourceLocation loc = id.contains(":") ? new ResourceLocation(id) : new ResourceLocation("abyssalcraft", id);
		return ForgeRegistries.ITEMS.getValue(loc);
	}

	@Override
	protected void generate() {
		BlockHandler.INFO.forEach((name, info) -> {
			Block block = info.block.get();
			switch(info.drop) {
			case SELF -> dropSelf(block);
			case NONE -> { if(block.getLootTable() != net.minecraft.world.level.storage.loot.BuiltInLootTables.EMPTY) add(block, noDrop()); }
			case OTHER -> dropOther(block, item(info.dropItem));
			case SLAB -> add(block, createSlabItemTable(block));
			case DOOR -> add(block, createDoorTable(block));
			case LEAVES -> add(block, createLeavesDrops(block, ForgeRegistries.BLOCKS.getValue(new ResourceLocation("abyssalcraft", info.dropItem)), NORMAL_LEAVES_SAPLING_CHANCES));
			case ORE -> {
				Item drop = item(info.dropItem);
				if(info.dropMax > 1 || info.dropMin > 1)
					add(block, createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(drop)
							.apply(SetItemCountFunction.setCount(UniformGenerator.between(info.dropMin, info.dropMax)))
							.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
				else
					add(block, createOreDrop(block, drop));
			}
			case CUSTOM -> custom(name, block);
			case PE -> add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
					.add(LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("PotEnergy", "PotEnergy"))))));
			}
		});
	}

	private void custom(String name, Block block) {
		switch(name) {
		// Calcified Stone: 1-3 Calcium Crystals or Calcium Crystal Shards (1.12.2: crystal/crystal_shard meta 25)
		case "calcified_stone" -> add(block, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(block, LootItem.lootTableItem(ACItems.crystal(25).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))
				.add(applyExplosionDecay(block, LootItem.lootTableItem(ACItems.crystalShard(25).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))));
		default -> dropSelf(block);
		}
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return BlockHandler.INFO.values().stream().map(i -> i.block.get()).collect(Collectors.toList());
	}
}
