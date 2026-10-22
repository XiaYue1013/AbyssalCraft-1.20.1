/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the 1.12.2 entity loot tables (assets/abyssalcraft/loot_tables/entities) with the flattened item ids
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import java.util.List;
import java.util.stream.Stream;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

public class ACEntityLoot extends EntityLootSubProvider {

	public ACEntityLoot() {
		super(FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	public void generate() {
		add(ACEntityTypes.ABYSSAL_ZOMBIE.get(), LootTable.lootTable()
				.withPool(drop(ACItems.coralium_plagued_flesh.get(), 0, 2))
				.withPool(rare(item(Items.BONE), item(ACItems.darkstone_sword.get()), counted(ACItems.coralium_gem.get(), 0, 3))));
		// the named ghouls drop their own heads
		String[] variants = {"depths_ghoul", "depths_ghoul_pete", "depths_ghoul_wilson", "depths_ghoul_orange"};
		ItemLike[] heads = {ACBlocks.depths_ghoul_head.get(), ACBlocks.pete_head.get(), ACBlocks.mr_wilson_head.get(), ACBlocks.dr_orange_head.get()};
		for(int i = 0; i < variants.length; i++) {
			LootTable.Builder table = LootTable.lootTable().withPool(drop(ACItems.coralium_plagued_flesh_on_a_bone.get(), 0, 2)).withPool(rare(item(heads[i])));
			if(i == 0)
				add(ACEntityTypes.DEPTHS_GHOUL.get(), table);
			else
				add(ACEntityTypes.DEPTHS_GHOUL.get(), AbyssalCraft.loc("entities/" + variants[i]), table);
		}
		// one Lesser Shoggoth table per dimension type, each dropping that dimension's flesh
		String[] shoggoths = {"lesser_shoggoth", "lesser_abyssal_shoggoth", "lesser_dreaded_shoggoth", "lesser_omothol_shoggoth", "lesser_shadow_shoggoth"};
		ItemLike[] flesh = {ACItems.shoggoth_flesh_overworld.get(), ACItems.shoggoth_flesh_abyssal_wasteland.get(), ACItems.shoggoth_flesh_dreadlands.get(),
				ACItems.shoggoth_flesh_omothol.get(), ACItems.shoggoth_flesh_dark_realm.get()};
		for(int i = 0; i < shoggoths.length; i++) {
			LootTable.Builder table = LootTable.lootTable().withPool(drop(flesh[i], 0, 2));
			if(i == 0)
				add(ACEntityTypes.LESSER_SHOGGOTH.get(), table);
			else
				add(ACEntityTypes.LESSER_SHOGGOTH.get(), AbyssalCraft.loc("entities/" + shoggoths[i]), table);
		}
		add(ACEntityTypes.DREADGUARD.get(), LootTable.lootTable().withPool(drop(ACItems.dreaded_shard_of_abyssalnite.get(), 0, 2)));
		add(ACEntityTypes.DREAD_SPAWN.get(), LootTable.lootTable().withPool(drop(ACItems.dread_fragment.get(), 0, 2)));
		add(ACEntityTypes.SPAWN_OF_CHAGAROTH.get(), LootTable.lootTable().withPool(drop(ACItems.dread_fragment.get(), 0, 2)));
		add(ACEntityTypes.GREATER_DREAD_SPAWN.get(), LootTable.lootTable().withPool(drop(ACItems.dreaded_shard_of_abyssalnite.get(), 0, 2)));
		add(ACEntityTypes.LESSER_DREADBEAST.get(), LootTable.lootTable().withPool(drop(ACItems.dreaded_shard_of_abyssalnite.get(), 0, 2)));
		// empty in 1.12.2 (Cha'garoth drops its loot while dying)
		add(ACEntityTypes.FIST_OF_CHAGAROTH.get(), LootTable.lootTable());
		add(ACEntityTypes.CHAGAROTH.get(), LootTable.lootTable());
		add(ACEntityTypes.JZAHAR.get(), LootTable.lootTable());
		add(ACEntityTypes.MINION_OF_THE_GATEKEEPER.get(), LootTable.lootTable().withPool(drop(ACItems.eldritch_scale.get(), 0, 2))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer())
						.when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1F, 0.1F)).add(counted(ACItems.ethaxium_ingot.get(), 0, 1))));
		add(ACEntityTypes.DREADLING.get(), LootTable.lootTable().withPool(drop(ACItems.dread_fragment.get(), 0, 2)));
		add(ACEntityTypes.ABYSSALNITE_GOLEM.get(), LootTable.lootTable().withPool(drop(ACItems.chunk_of_abyssalnite.get(), 0, 2).when(LootItemKilledByPlayerCondition.killedByPlayer())));
		add(ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(), LootTable.lootTable().withPool(drop(ACItems.dreaded_chunk_of_abyssalnite.get(), 0, 2).when(LootItemKilledByPlayerCondition.killedByPlayer())));
		add(ACEntityTypes.SKELETON_GOLIATH.get(), LootTable.lootTable().withPool(drop(Items.BONE, 0, 2)).withPool(rare(item(ACItems.cudgel.get()))));
		add(ACEntityTypes.OMOTHOL_GHOUL.get(), LootTable.lootTable().withPool(drop(ACItems.omothol_flesh.get(), 0, 2)));
		for(var demon : List.of(ACEntityTypes.DEMON_PIG, ACEntityTypes.DEMON_COW, ACEntityTypes.DEMON_CHICKEN, ACEntityTypes.DEMON_SHEEP))
			add(demon.get(), LootTable.lootTable().withPool(drop(Items.ROTTEN_FLESH, 0, 2)));
		add(ACEntityTypes.EVIL_PIG.get(), LootTable.lootTable().withPool(drop(Items.PORKCHOP, 1, 3)));
		add(ACEntityTypes.EVIL_COW.get(), LootTable.lootTable().withPool(drop(Items.LEATHER, 0, 2)).withPool(drop(Items.BEEF, 1, 3)));
		add(ACEntityTypes.EVIL_CHICKEN.get(), LootTable.lootTable().withPool(drop(Items.FEATHER, 0, 2)).withPool(drop(Items.CHICKEN, 1, 1)));
		add(ACEntityTypes.EVIL_SHEEP.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(item(Items.WHITE_WOOL)))
				.withPool(drop(Items.MUTTON, 1, 2)));
		add(ACEntityTypes.ANTI_PIG.get(), LootTable.lootTable().withPool(drop(ACItems.anti_pork.get(), 1, 3)));
		add(ACEntityTypes.ANTI_COW.get(), LootTable.lootTable().withPool(drop(Items.LEATHER, 0, 2)).withPool(drop(ACItems.anti_beef.get(), 1, 3)));
		add(ACEntityTypes.ANTI_CHICKEN.get(), LootTable.lootTable().withPool(drop(Items.FEATHER, 0, 2)).withPool(drop(ACItems.anti_chicken.get(), 1, 1)));
		add(ACEntityTypes.ANTI_BAT.get(), LootTable.lootTable());
		add(ACEntityTypes.ANTI_ZOMBIE.get(), LootTable.lootTable().withPool(drop(ACItems.rotten_anti_flesh.get(), 0, 2)));
		add(ACEntityTypes.ABYSSAL_ANTI_ZOMBIE.get(), LootTable.lootTable().withPool(drop(ACItems.anti_plagued_flesh.get(), 0, 2)));
		add(ACEntityTypes.ANTI_SKELETON.get(), LootTable.lootTable().withPool(drop(Items.ARROW, 0, 2)).withPool(drop(ACItems.anti_bone.get(), 0, 2)));
		add(ACEntityTypes.ANTI_SPIDER.get(), LootTable.lootTable().withPool(drop(Items.STRING, 0, 2))
				.withPool(drop(ACItems.anti_spider_eye.get(), -1, 1).when(LootItemKilledByPlayerCondition.killedByPlayer())));
		add(ACEntityTypes.ANTI_CREEPER.get(), LootTable.lootTable().withPool(drop(Items.GUNPOWDER, 0, 2)));
		add(ACEntityTypes.ANTI_GHOUL.get(), LootTable.lootTable().withPool(drop(ACItems.anti_plagued_flesh_on_a_bone.get(), 0, 2)));
		add(ACEntityTypes.ANTI_PLAYER.get(), LootTable.lootTable());
		add(ACEntityTypes.CORALIUM_SQUID.get(), LootTable.lootTable().withPool(drop(Items.INK_SAC, 1, 3)));
		add(ACEntityTypes.ASORAH.get(), LootTable.lootTable());
		add(ACEntityTypes.SPECTRAL_DRAGON.get(), LootTable.lootTable());
		add(ACEntityTypes.SHUB_OFFSPRING.get(), LootTable.lootTable());
		add(ACEntityTypes.SHADOW_CREATURE.get(), LootTable.lootTable().withPool(drop(ACItems.shadow_fragment.get(), 1, 3)));
		add(ACEntityTypes.SHADOW_MONSTER.get(), LootTable.lootTable().withPool(drop(ACItems.shadow_shard.get(), 0, 2)));
		add(ACEntityTypes.SHADOW_BEAST.get(), LootTable.lootTable().withPool(drop(ACItems.shadow_gem.get(), 0, 2)));
		add(ACEntityTypes.SACTHOTH.get(), LootTable.lootTable());
		// one table per profession, all the same in 1.12.2
		String[] remnants = {"remnant", "remnant_librarian", "remnant_priest", "remnant_blacksmith", "remnant_butcher", "remnant_banker", "remnant_master_blacksmith"};
		for(int i = 0; i < remnants.length; i++) {
			LootTable.Builder table = LootTable.lootTable().withPool(drop(ACItems.eldritch_scale.get(), 0, 2));
			if(i == 0)
				add(ACEntityTypes.REMNANT.get(), table);
			else
				add(ACEntityTypes.REMNANT.get(), AbyssalCraft.loc("entities/" + remnants[i]), table);
		}
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return ACEntityTypes.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
	}

	/** 0 - n of an item, +0 - 1 per looting level */
	private static LootPool.Builder drop(ItemLike item, int min, int max) {
		return LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(counted(item, min, max));
	}

	private static LootPoolSingletonContainer.Builder<?> counted(ItemLike item, int min, int max) {
		return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
				.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)));
	}

	private static LootPoolSingletonContainer.Builder<?> item(ItemLike item) {
		return LootItem.lootTableItem(item);
	}

	/** 1.12.2 rare drops: killed by a player, 2.5 % (+1 % per looting level) */
	private static LootPool.Builder rare(LootPoolSingletonContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(LootItemKilledByPlayerCondition.killedByPlayer())
				.when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025F, 0.01F));
		for(LootPoolSingletonContainer.Builder<?> entry : entries)
			pool.add(entry);
		return pool;
	}
}
