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
package com.shinoow.abyssalcraft.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.tile.*;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block entity types (1.12.2 GameRegistry.registerTileEntity calls)
 */
public class ACBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbyssalCraft.modid);

	public static final RegistryObject<BlockEntityType<TileEntityCrystallizer>> CRYSTALLIZER = register("crystallizer", TileEntityCrystallizer::new, BlockHandler.CRYSTALLIZER);
	public static final RegistryObject<BlockEntityType<TileEntityTransmutator>> TRANSMUTATOR = register("transmutator", TileEntityTransmutator::new, BlockHandler.TRANSMUTATOR);

	public static final RegistryObject<BlockEntityType<TileEntityEngraver>> ENGRAVER = register("engraver", TileEntityEngraver::new, BlockHandler.ENGRAVER);

	public static final RegistryObject<BlockEntityType<TileEntityMaterializer>> MATERIALIZER = register("materializer", TileEntityMaterializer::new, BlockHandler.MATERIALIZER);

	public static final RegistryObject<BlockEntityType<TileEntityEnergyPedestal>> ENERGY_PEDESTAL = register("energy_pedestal", TileEntityEnergyPedestal::new,
			() -> with(BlockHandler.ENERGY_PEDESTAL, BlockHandler.TIERED_ENERGY_PEDESTALS));
	public static final RegistryObject<BlockEntityType<TileEntitySacrificialAltar>> SACRIFICIAL_ALTAR = register("sacrificial_altar", TileEntitySacrificialAltar::new,
			() -> with(BlockHandler.SACRIFICIAL_ALTAR, BlockHandler.TIERED_SACRIFICIAL_ALTARS));
	public static final RegistryObject<BlockEntityType<TileEntityEnergyCollector>> ENERGY_COLLECTOR = register("energy_collector", TileEntityEnergyCollector::new,
			() -> with(BlockHandler.ENERGY_COLLECTOR, BlockHandler.TIERED_ENERGY_COLLECTORS));
	public static final RegistryObject<BlockEntityType<TileEntityEnergyRelay>> ENERGY_RELAY = register("energy_relay", TileEntityEnergyRelay::new,
			() -> with(BlockHandler.ENERGY_RELAY, BlockHandler.TIERED_ENERGY_RELAYS));
	public static final RegistryObject<BlockEntityType<TileEntityEnergyContainer>> ENERGY_CONTAINER = register("energy_container", TileEntityEnergyContainer::new,
			() -> with(BlockHandler.ENERGY_CONTAINER, BlockHandler.TIERED_ENERGY_CONTAINERS));
	public static final RegistryObject<BlockEntityType<TileEntityStatue>> STATUE = register("statue", TileEntityStatue::new, () -> BlockHandler.STATUES);

	public static final RegistryObject<BlockEntityType<TileEntityRitualAltar>> RITUAL_ALTAR = register("ritual_altar", TileEntityRitualAltar::new, () -> BlockHandler.RITUAL_ALTARS);
	public static final RegistryObject<BlockEntityType<TileEntityRitualPedestal>> RITUAL_PEDESTAL = register("ritual_pedestal", TileEntityRitualPedestal::new, () -> BlockHandler.RITUAL_PEDESTALS);

	public static final RegistryObject<BlockEntityType<TileEntityCrate>> CRATE = register("crate", TileEntityCrate::new, BlockHandler.WOODEN_CRATE);
	public static final RegistryObject<BlockEntityType<TileEntityGhoulHead>> GHOUL_HEAD = register("ghoul_head", TileEntityGhoulHead::new,
			BlockHandler.DEPTHS_GHOUL_HEAD, BlockHandler.PETE_HEAD, BlockHandler.MR_WILSON_HEAD, BlockHandler.DR_ORANGE_HEAD);

	public static final RegistryObject<BlockEntityType<TileEntityStateTransformer>> STATE_TRANSFORMER = register("state_transformer",
			TileEntityStateTransformer::new, BlockHandler.STATE_TRANSFORMER);

	public static final RegistryObject<BlockEntityType<TileEntityEnergyDepositioner>> ENERGY_DEPOSITIONER = register("energy_depositioner",
			TileEntityEnergyDepositioner::new, BlockHandler.ENERGY_DEPOSITIONER);

	public static final RegistryObject<BlockEntityType<TileEntityRendingPedestal>> RENDING_PEDESTAL = register("rending_pedestal",
			TileEntityRendingPedestal::new, BlockHandler.RENDING_PEDESTAL);

	public static final RegistryObject<BlockEntityType<TileEntitySpiritAltar>> SPIRIT_ALTAR = register("spirit_altar",
			TileEntitySpiritAltar::new, BlockHandler.SPIRIT_ALTAR);

	public static final RegistryObject<BlockEntityType<TileEntityMultiblock>> MULTIBLOCK = register("multiblock",
			TileEntityMultiblock::new, BlockHandler.MULTI_BLOCK);

	public static final RegistryObject<BlockEntityType<TileEntityShoggothBiomass>> SHOGGOTH_BIOMASS = register("shoggoth_biomass",
			TileEntityShoggothBiomass::new, BlockHandler.SHOGGOTH_BIOMASS);
	public static final RegistryObject<BlockEntityType<TileEntitySingleMobSpawner>> SINGLE_MOB_SPAWNER = register("single_mob_spawner",
			TileEntitySingleMobSpawner::new, () -> BlockHandler.MOB_SPAWNERS);

	private static List<RegistryObject<Block>> with(RegistryObject<Block> base, List<RegistryObject<Block>> tiered) {
		List<RegistryObject<Block>> list = new ArrayList<>(tiered);
		list.add(0, base);
		return list;
	}

	@SuppressWarnings("ConstantConditions")
	private static <T extends net.minecraft.world.level.block.entity.BlockEntity> RegistryObject<BlockEntityType<T>> register(String name,
			BlockEntityType.BlockEntitySupplier<T> factory, Supplier<List<RegistryObject<Block>>> blocks) {
		return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blocks.get().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));
	}

	@SafeVarargs
	@SuppressWarnings("ConstantConditions")
	private static <T extends net.minecraft.world.level.block.entity.BlockEntity> RegistryObject<BlockEntityType<T>> register(String name,
			BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block>... blocks) {
		return BLOCK_ENTITIES.register(name, () -> {
			Block[] array = new Block[blocks.length];
			for(int i = 0; i < blocks.length; i++)
				array[i] = blocks[i].get();
			return BlockEntityType.Builder.of(factory, array).build(null);
		});
	}
}
