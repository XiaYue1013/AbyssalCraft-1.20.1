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
 *     1.20.1 port - DeferredRegister + flattened variants
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DimensionCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.common.blocks.itemblock.ItemPEContainerBlock;
import com.shinoow.abyssalcraft.common.blocks.*;
import com.shinoow.abyssalcraft.init.BlockInfo.Drop;
import com.shinoow.abyssalcraft.init.BlockInfo.Model;
import com.shinoow.abyssalcraft.init.BlockInfo.Tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all AbyssalCraft blocks (the block items are registered by {@link ItemHandler} from {@link #INFO}).<br>
 * Hardness values are the 1.12.2 ones. 1.12.2 resistance values given to setResistance(r) are converted to
 * 1.20.1 explosion resistance as r * 3 / 5 (what 1.12.2 effectively used), except for the stone/cobblestone
 * types which returned their raw value from an override.
 */
public class BlockHandler {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AbyssalCraft.modid);

	/** Registration metadata, in registration order (used for block items, creative tabs and data generation) */
	public static final Map<String, BlockInfo> INFO = new LinkedHashMap<>();

	public static final float UNBREAKABLE_RESISTANCE = 3600000.0F;
	public static final RegistryObject<Block> OBLIVION_DEATHBOMB = register("oblivion_deathbomb",
			() -> new BlockODB(Properties.of().mapColor(MapColor.METAL).strength(3, 0).sound(SoundType.METAL).noOcclusion(), false),
			b -> b.custom("odb"));
	public static final RegistryObject<Block> ODB_CORE = register("odb_core",
			() -> new BlockODB(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3, 0).sound(SoundType.METAL).noOcclusion(), true),
			b -> b.custom("odbcore").level(3).itemModel("odbcore"));

	// Stone types (BlockACStone)
	public static final RegistryObject<Block> DARKSTONE = stone("darkstone", 1.65F, 12.0F, 0, MapColor.COLOR_BLACK, "ds", "darkstone_cobblestone");
	public static final RegistryObject<Block> ABYSSAL_STONE = stone("abyssal_stone", 1.8F, 12.0F, 2, MapColor.COLOR_GREEN, "as", "abyssal_cobblestone");
	public static final RegistryObject<Block> DREADSTONE = stone("dreadstone", 2.5F, 20.0F, 4, MapColor.COLOR_RED, "drs", "dreadstone_cobblestone");
	public static final RegistryObject<Block> ABYSSALNITE_STONE = stone("abyssalnite_stone", 2.5F, 20.0F, 4, MapColor.COLOR_PURPLE, "abydrs", "abyssalnite_cobblestone");
	public static final RegistryObject<Block> CORALIUM_STONE = register("coralium_stone", () -> new BlockCoraliumStone(rock(MapColor.COLOR_CYAN, 1.5F, 10.0F)), b -> b.cube("cstone").level(0).dropOther("coralium_cobblestone"));
	public static final RegistryObject<Block> ETHAXIUM = stone("ethaxium", 100.0F, UNBREAKABLE_RESISTANCE, 8, MapColor.WOOL, "eth", null);
	public static final RegistryObject<Block> OMOTHOL_STONE = stone("omothol_stone", 10.0F, 12.0F, 6, MapColor.COLOR_BLACK, "os", null);
	public static final RegistryObject<Block> MONOLITH_STONE = stone("monolith_stone", 6.0F, 24.0F, 0, MapColor.COLOR_BLACK, "monolithstone", null);

	// Cobblestone types (BlockACCobblestone)
	public static final RegistryObject<Block> DARKSTONE_COBBLESTONE = cobble("darkstone_cobblestone", 2.2F, 12.0F, 0, MapColor.COLOR_BLACK, "dsc");
	public static final RegistryObject<Block> ABYSSAL_COBBLESTONE = cobble("abyssal_cobblestone", 2.6F, 12.0F, 2, MapColor.COLOR_GREEN, "abyssalcobblestone");
	public static final RegistryObject<Block> DREADSTONE_COBBLESTONE = cobble("dreadstone_cobblestone", 3.3F, 20.0F, 4, MapColor.COLOR_RED, "dreadstonecobblestone");
	public static final RegistryObject<Block> ABYSSALNITE_COBBLESTONE = cobble("abyssalnite_cobblestone", 3.3F, 20.0F, 4, MapColor.COLOR_PURPLE, "abyssalnitecobblestone");
	public static final RegistryObject<Block> CORALIUM_COBBLESTONE = cobble("coralium_cobblestone", 2.0F, 10.0F, 0, MapColor.COLOR_CYAN, "coraliumcobblestone");

	// Bricks (BlockACBrick normal/chiseled/cracked)
	public static final RegistryObject<Block> DARKSTONE_BRICKS = bricks("darkstone_bricks", 1.65F, 12.0F, 0, MapColor.COLOR_BLACK, "dsb", false);
	public static final RegistryObject<Block> CHISELED_DARKSTONE_BRICKS = bricks("chiseled_darkstone_bricks", 1.65F, 12.0F, 0, MapColor.COLOR_BLACK, "dsbc", false);
	public static final RegistryObject<Block> CRACKED_DARKSTONE_BRICKS = bricks("cracked_darkstone_bricks", 1.65F, 12.0F, 0, MapColor.COLOR_BLACK, "dsbcr", false);
	public static final RegistryObject<Block> ABYSSAL_STONE_BRICKS = bricks("abyssal_stone_bricks", 1.8F, 12.0F, 2, MapColor.COLOR_GREEN, "asb", false);
	public static final RegistryObject<Block> CHISELED_ABYSSAL_STONE_BRICKS = bricks("chiseled_abyssal_stone_bricks", 1.8F, 12.0F, 2, MapColor.COLOR_GREEN, "asbc", false);
	public static final RegistryObject<Block> CRACKED_ABYSSAL_STONE_BRICKS = bricks("cracked_abyssal_stone_bricks", 1.8F, 12.0F, 2, MapColor.COLOR_GREEN, "asbcr", false);
	public static final RegistryObject<Block> DREADSTONE_BRICKS = bricks("dreadstone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_RED, "drsb", false);
	public static final RegistryObject<Block> CHISELED_DREADSTONE_BRICKS = bricks("chiseled_dreadstone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_RED, "drsbc", false);
	public static final RegistryObject<Block> CRACKED_DREADSTONE_BRICKS = bricks("cracked_dreadstone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_RED, "drsbcr", false);
	public static final RegistryObject<Block> ABYSSALNITE_STONE_BRICKS = bricks("abyssalnite_stone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_PURPLE, "abydrsb", false);
	public static final RegistryObject<Block> CHISELED_ABYSSALNITE_STONE_BRICKS = bricks("chiseled_abyssalnite_stone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_PURPLE, "abydrsbc", false);
	public static final RegistryObject<Block> CRACKED_ABYSSALNITE_STONE_BRICKS = bricks("cracked_abyssalnite_stone_bricks", 2.5F, 20.0F, 4, MapColor.COLOR_PURPLE, "abydrsbcr", false);
	public static final RegistryObject<Block> CORALIUM_STONE_BRICKS = bricks("coralium_stone_bricks", 1.5F, 10.0F, 0, MapColor.COLOR_CYAN, "cstonebrick", false);
	public static final RegistryObject<Block> CHISELED_CORALIUM_STONE_BRICKS = bricks("chiseled_coralium_stone_bricks", 1.5F, 10.0F, 0, MapColor.COLOR_CYAN, "cstonebrickc", false);
	public static final RegistryObject<Block> CRACKED_CORALIUM_STONE_BRICKS = bricks("cracked_coralium_stone_bricks", 1.5F, 10.0F, 0, MapColor.COLOR_CYAN, "cstonebrickcr", false);
	public static final RegistryObject<Block> ETHAXIUM_BRICKS = bricks("ethaxium_bricks", 100.0F, Float.MAX_VALUE, 8, MapColor.WOOL, "eb", true);
	public static final RegistryObject<Block> CHISELED_ETHAXIUM_BRICKS = bricks("chiseled_ethaxium_bricks", 100.0F, Float.MAX_VALUE, 8, MapColor.WOOL, "ebc", true);
	public static final RegistryObject<Block> CRACKED_ETHAXIUM_BRICKS = bricks("cracked_ethaxium_bricks", 100.0F, Float.MAX_VALUE, 8, MapColor.WOOL, "ebcr", true);
	public static final RegistryObject<Block> DARK_ETHAXIUM_BRICKS = bricks("dark_ethaxium_bricks", 150.0F, Float.MAX_VALUE, 8, MapColor.STONE, "deb", true);
	public static final RegistryObject<Block> CHISELED_DARK_ETHAXIUM_BRICKS = bricks("chiseled_dark_ethaxium_bricks", 150.0F, Float.MAX_VALUE, 8, MapColor.STONE, "debc", true);
	public static final RegistryObject<Block> CRACKED_DARK_ETHAXIUM_BRICKS = bricks("cracked_dark_ethaxium_bricks", 150.0F, Float.MAX_VALUE, 8, MapColor.STONE, "debcr", true);
	public static final RegistryObject<Block> GLOWING_DARKSTONE_BRICKS = register("glowing_darkstone_bricks", () -> new Block(rock(MapColor.COLOR_BLACK, 55.0F, r(3000.0F)).lightLevel(s -> 15)),
			b -> b.column("dsglow", "dsb").level(3));

	// Slabs
	public static final RegistryObject<Block> DARKSTONE_SLAB = slab("darkstone_slab", DARKSTONE, 1.65F, 12.0F, 0, "ds");
	public static final RegistryObject<Block> DARKSTONE_BRICK_SLAB = slab("darkstone_brick_slab", DARKSTONE_BRICKS, 1.65F, 12.0F, 0, "dsb");
	public static final RegistryObject<Block> DARKSTONE_COBBLESTONE_SLAB = slab("darkstone_cobblestone_slab", DARKSTONE_COBBLESTONE, 1.65F, 12.0F, 0, "dsc");
	public static final RegistryObject<Block> ABYSSAL_STONE_BRICK_SLAB = slab("abyssal_stone_brick_slab", ABYSSAL_STONE_BRICKS, 1.8F, 12.0F, 2, "asb");
	public static final RegistryObject<Block> ABYSSAL_COBBLESTONE_SLAB = slab("abyssal_cobblestone_slab", ABYSSAL_COBBLESTONE, 2.6F, 12.0F, 2, "abyssalcobblestone");
	public static final RegistryObject<Block> DREADSTONE_BRICK_SLAB = slab("dreadstone_brick_slab", DREADSTONE_BRICKS, 2.5F, 20.0F, 4, "drsb");
	public static final RegistryObject<Block> DREADSTONE_COBBLESTONE_SLAB = slab("dreadstone_cobblestone_slab", DREADSTONE_COBBLESTONE, 3.3F, 20.0F, 4, "dreadstonecobblestone");
	public static final RegistryObject<Block> ABYSSALNITE_STONE_BRICK_SLAB = slab("abyssalnite_stone_brick_slab", ABYSSALNITE_STONE_BRICKS, 2.5F, 20.0F, 4, "abydrsb");
	public static final RegistryObject<Block> ABYSSALNITE_COBBLESTONE_SLAB = slab("abyssalnite_cobblestone_slab", ABYSSALNITE_COBBLESTONE, 3.3F, 20.0F, 4, "abyssalnitecobblestone");
	public static final RegistryObject<Block> CORALIUM_STONE_BRICK_SLAB = slab("coralium_stone_brick_slab", CORALIUM_STONE_BRICKS, 1.5F, 10.0F, 0, "cstonebrick");
	public static final RegistryObject<Block> CORALIUM_COBBLESTONE_SLAB = slab("coralium_cobblestone_slab", CORALIUM_COBBLESTONE, 2.0F, 10.0F, 0, "coraliumcobblestone");
	public static final RegistryObject<Block> ETHAXIUM_BRICK_SLAB = slab("ethaxium_brick_slab", ETHAXIUM_BRICKS, 100.0F, Float.MAX_VALUE, 8, "eb");
	public static final RegistryObject<Block> DARK_ETHAXIUM_BRICK_SLAB = slab("dark_ethaxium_brick_slab", DARK_ETHAXIUM_BRICKS, 150.0F, Float.MAX_VALUE, 8, "deb");

	// Stairs
	public static final RegistryObject<Block> DARKSTONE_BRICK_STAIRS = stairs("darkstone_brick_stairs", DARKSTONE_BRICKS, 1.65F, 12.0F, 0, "dsb");
	public static final RegistryObject<Block> DARKSTONE_COBBLESTONE_STAIRS = stairs("darkstone_cobblestone_stairs", DARKSTONE_COBBLESTONE, 1.65F, 12.0F, 0, "dsc");
	public static final RegistryObject<Block> ABYSSAL_STONE_BRICK_STAIRS = stairs("abyssal_stone_brick_stairs", ABYSSAL_STONE_BRICKS, 1.65F, 12.0F, 2, "asb");
	public static final RegistryObject<Block> ABYSSAL_COBBLESTONE_STAIRS = stairs("abyssal_cobblestone_stairs", ABYSSAL_COBBLESTONE, 2.6F, 12.0F, 2, "abyssalcobblestone");
	public static final RegistryObject<Block> DREADSTONE_BRICK_STAIRS = stairs("dreadstone_brick_stairs", DREADSTONE_BRICKS, 2.5F, 20.0F, 4, "drsb");
	public static final RegistryObject<Block> DREADSTONE_COBBLESTONE_STAIRS = stairs("dreadstone_cobblestone_stairs", DREADSTONE_COBBLESTONE, 3.3F, 20.0F, 4, "dreadstonecobblestone");
	public static final RegistryObject<Block> ABYSSALNITE_STONE_BRICK_STAIRS = stairs("abyssalnite_stone_brick_stairs", ABYSSALNITE_STONE_BRICKS, 2.5F, 20.0F, 4, "abydrsb");
	public static final RegistryObject<Block> ABYSSALNITE_COBBLESTONE_STAIRS = stairs("abyssalnite_cobblestone_stairs", ABYSSALNITE_COBBLESTONE, 3.3F, 20.0F, 4, "abyssalnitecobblestone");
	public static final RegistryObject<Block> CORALIUM_STONE_BRICK_STAIRS = stairs("coralium_stone_brick_stairs", CORALIUM_STONE_BRICKS, 1.5F, 10.0F, 0, "cstonebrick");
	public static final RegistryObject<Block> CORALIUM_COBBLESTONE_STAIRS = stairs("coralium_cobblestone_stairs", CORALIUM_COBBLESTONE, 2.0F, 10.0F, 0, "coraliumcobblestone");
	public static final RegistryObject<Block> ETHAXIUM_BRICK_STAIRS = stairs("ethaxium_brick_stairs", ETHAXIUM_BRICKS, 100.0F, Float.MAX_VALUE, 8, "eb");
	public static final RegistryObject<Block> DARK_ETHAXIUM_BRICK_STAIRS = stairs("dark_ethaxium_brick_stairs", DARK_ETHAXIUM_BRICKS, 150.0F, Float.MAX_VALUE, 8, "deb");

	// Walls
	public static final RegistryObject<Block> DARKSTONE_COBBLESTONE_WALL = wall("darkstone_cobblestone_wall", DARKSTONE_COBBLESTONE, 1.65F, 12.0F, 0, "dsc");
	public static final RegistryObject<Block> ABYSSAL_COBBLESTONE_WALL = wall("abyssal_cobblestone_wall", ABYSSAL_COBBLESTONE, 2.6F, 12.0F, 2, "abyssalcobblestone");
	public static final RegistryObject<Block> DREADSTONE_COBBLESTONE_WALL = wall("dreadstone_cobblestone_wall", DREADSTONE_COBBLESTONE, 3.3F, 20.0F, 4, "dreadstonecobblestone");
	public static final RegistryObject<Block> ABYSSALNITE_COBBLESTONE_WALL = wall("abyssalnite_cobblestone_wall", ABYSSALNITE_COBBLESTONE, 3.3F, 20.0F, 4, "abyssalnitecobblestone");
	public static final RegistryObject<Block> CORALIUM_COBBLESTONE_WALL = wall("coralium_cobblestone_wall", CORALIUM_COBBLESTONE, 2.0F, 10.0F, 0, "coraliumcobblestone");

	// Fences
	public static final RegistryObject<Block> DARKSTONE_BRICK_FENCE = fence("darkstone_brick_fence", DARKSTONE_BRICKS, 1.65F, 12.0F, 0, MapColor.COLOR_BLACK, "dsb");
	public static final RegistryObject<Block> ABYSSAL_STONE_BRICK_FENCE = fence("abyssal_stone_brick_fence", ABYSSAL_STONE_BRICKS, 1.8F, 12.0F, 2, MapColor.COLOR_GREEN, "asb");
	public static final RegistryObject<Block> DREADSTONE_BRICK_FENCE = fence("dreadstone_brick_fence", DREADSTONE_BRICKS, 2.5F, 20.0F, 4, MapColor.COLOR_RED, "drsb");
	public static final RegistryObject<Block> ABYSSALNITE_STONE_BRICK_FENCE = fence("abyssalnite_stone_brick_fence", ABYSSALNITE_STONE_BRICKS, 2.5F, 20.0F, 4, MapColor.COLOR_PURPLE, "abydrsb");
	public static final RegistryObject<Block> CORALIUM_STONE_BRICK_FENCE = fence("coralium_stone_brick_fence", CORALIUM_STONE_BRICKS, 1.5F, 10.0F, 0, MapColor.COLOR_CYAN, "cstonebrick");
	public static final RegistryObject<Block> ETHAXIUM_BRICK_FENCE = fence("ethaxium_brick_fence", ETHAXIUM_BRICKS, 100.0F, Float.MAX_VALUE, 8, MapColor.WOOL, "eb");
	public static final RegistryObject<Block> DARK_ETHAXIUM_BRICK_FENCE = fence("dark_ethaxium_brick_fence", DARK_ETHAXIUM_BRICKS, 150.0F, Float.MAX_VALUE, 8, MapColor.STONE, "deb");

	// Redstone components
	public static final RegistryObject<Block> DARKSTONE_BUTTON = button("darkstone_button", DARKSTONE, 0.6F, 12.0F, 0, BlockSetType.STONE, 20, "ds");
	public static final RegistryObject<Block> ABYSSAL_STONE_BUTTON = button("abyssal_stone_button", ABYSSAL_STONE, 0.8F, 12.0F, 2, BlockSetType.STONE, 20, "as");
	public static final RegistryObject<Block> CORALIUM_STONE_BUTTON = button("coralium_stone_button", CORALIUM_STONE, 0.6F, 12.0F, 0, BlockSetType.STONE, 20, "cstone");
	public static final RegistryObject<Block> DARKSTONE_PRESSURE_PLATE = plate("darkstone_pressure_plate", DARKSTONE, 0.6F, 12.0F, 0, PressurePlateBlock.Sensitivity.MOBS, BlockSetType.STONE, "ds");
	public static final RegistryObject<Block> ABYSSAL_STONE_PRESSURE_PLATE = plate("abyssal_stone_pressure_plate", ABYSSAL_STONE, 0.8F, 12.0F, 2, PressurePlateBlock.Sensitivity.MOBS, BlockSetType.STONE, "as");
	public static final RegistryObject<Block> CORALIUM_STONE_PRESSURE_PLATE = plate("coralium_stone_pressure_plate", CORALIUM_STONE, 0.6F, 12.0F, 0, PressurePlateBlock.Sensitivity.MOBS, BlockSetType.STONE, "cstone");

	// Darklands Oak
	public static final RegistryObject<Block> DARKLANDS_OAK_PLANKS = register("darklands_oak_planks", () -> new Block(wood(MapColor.COLOR_BROWN, 2.0F, 5.0F)), b -> b.cube("dltplank").tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_WOOD = register("darklands_oak_wood", () -> new RotatedPillarBlock(wood(MapColor.COLOR_BROWN, 2.0F, 1.0F)), b -> b.pillar("dlttside", "dltttop").tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_WOOD_2 = register("darklands_oak_wood_2", () -> new RotatedPillarBlock(wood(MapColor.COLOR_BROWN, 2.0F, 1.0F).noOcclusion()), b -> b.custom("dltlog_2").tool(Tool.AXE).itemModel("dltlog2"));
	public static final RegistryObject<Block> DARKLANDS_OAK_LEAVES = register("darklands_oak_leaves", () -> new LeavesBlock(leaves(MapColor.COLOR_BLUE)),
			b -> b.model(Model.LEAVES, "dlt_l").tool(Tool.HOE).leaves("darklands_oak_sapling").tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> DARKLANDS_OAK_SAPLING = register("darklands_oak_sapling", () -> new SaplingBlock(new ACTreeGrower("darklands_oak_tree"), sapling()),
			b -> b.model(Model.CROSS, "dlts").tool(Tool.NONE).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> DARKLANDS_OAK_SLAB = register("darklands_oak_slab", () -> new SlabBlock(wood(MapColor.COLOR_BROWN, 2.0F, 5.0F)),
			b -> b.model(Model.SLAB, "dltplank").parent(BlockHandler.DARKLANDS_OAK_PLANKS).tool(Tool.AXE).drop(Drop.SLAB));
	public static final RegistryObject<Block> DARKLANDS_OAK_STAIRS = register("darklands_oak_stairs", () -> new StairBlock(() -> BlockHandler.DARKLANDS_OAK_PLANKS.get().defaultBlockState(), wood(MapColor.COLOR_BROWN, 2.0F, 5.0F)),
			b -> b.model(Model.STAIRS, "dltplank").parent(BlockHandler.DARKLANDS_OAK_PLANKS).tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_FENCE = register("darklands_oak_fence", () -> new FenceBlock(wood(MapColor.COLOR_BROWN, 2.0F, 5.0F)),
			b -> b.model(Model.FENCE, "dltplank").parent(BlockHandler.DARKLANDS_OAK_PLANKS).tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_BUTTON = register("darklands_oak_button", () -> new ButtonBlock(Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), BlockSetType.OAK, 30, true),
			b -> b.model(Model.BUTTON, "dltplank").parent(BlockHandler.DARKLANDS_OAK_PLANKS).tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_PRESSURE_PLATE = register("darklands_oak_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Properties.of().mapColor(MapColor.COLOR_BROWN).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY).ignitedByLava(), BlockSetType.OAK),
			b -> b.model(Model.PRESSURE_PLATE, "dltplank").parent(BlockHandler.DARKLANDS_OAK_PLANKS).tool(Tool.AXE));
	public static final RegistryObject<Block> DARKLANDS_OAK_DOOR = register("darklands_oak_door", () -> new DoorBlock(wood(MapColor.COLOR_BROWN, 3.0F, 15.0F).noOcclusion(), BlockSetType.OAK),
			b -> b.model(Model.DOOR, "door_dlt_lower", "door_dlt_upper").tool(Tool.AXE).drop(Drop.DOOR).tab(ACTabs.Tab.DECORATION).itemModel("door_dlt"));

	// Dreadlands wood
	public static final RegistryObject<Block> DREADLANDS_PLANKS = register("dreadlands_planks", () -> new Block(wood(MapColor.COLOR_RED, 2.0F, 5.0F)), b -> b.cube("drtplank").tool(Tool.AXE));
	public static final RegistryObject<Block> DREADLANDS_LOG = register("dreadlands_log", () -> new RotatedPillarBlock(wood(MapColor.COLOR_RED, 2.0F, 12.0F)), b -> b.pillar("drtside", "drttop").tool(Tool.AXE));
	public static final RegistryObject<Block> DREADLANDS_LEAVES = register("dreadlands_leaves", () -> new LeavesBlock(leaves(MapColor.COLOR_RED)),
			b -> b.model(Model.LEAVES, "drt_l").tool(Tool.HOE).leaves("dreadlands_sapling").tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> DREADLANDS_SAPLING = register("dreadlands_sapling", () -> new SaplingBlock(new ACTreeGrower("dreadlands_tree"), sapling()),
			b -> b.model(Model.CROSS, "drts").tool(Tool.NONE).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> DREADLANDS_WOOD_FENCE = register("dreadlands_wood_fence", () -> new FenceBlock(wood(MapColor.COLOR_RED, 2.0F, 5.0F)),
			b -> b.model(Model.FENCE, "drtplank").parent(BlockHandler.DREADLANDS_PLANKS).tool(Tool.AXE));
	public static final RegistryObject<Block> DREADLANDS_DOOR = register("dreadlands_door", () -> new DoorBlock(wood(MapColor.COLOR_RED, 3.0F, 15.0F).noOcclusion(), BlockSetType.OAK),
			b -> b.model(Model.DOOR, "door_drt_lower", "door_drt_upper").tool(Tool.AXE).drop(Drop.DOOR).tab(ACTabs.Tab.DECORATION).itemModel("door_drt"));

	// Terrain
	public static final RegistryObject<Block> DREADLANDS_GRASS = register("dreadlands_grass", () -> new BlockDreadGrass(Properties.of().mapColor(MapColor.COLOR_RED).strength(0.4F).sound(SoundType.GRASS)),
			b -> b.model(Model.NONE).tool(Tool.SHOVEL).dropOther("dreadlands_dirt"));
	public static final RegistryObject<Block> DREADLANDS_DIRT = register("dreadlands_dirt", () -> new BlockDreadlandsDirt(Properties.of().mapColor(MapColor.FIRE).strength(0.4F, r(2.0F)).sound(SoundType.GRAVEL)),
			b -> b.cube("dreadlandsdirt").tool(Tool.SHOVEL));
	public static final RegistryObject<Block> ABYSSAL_SAND = register("abyssal_sand", () -> new BlockAbyssalSand(Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.5F, r(2.5F)).sound(SoundType.SAND)),
			b -> b.cube("abyssalsand").tool(Tool.SHOVEL));
	public static final RegistryObject<Block> FUSED_ABYSSAL_SAND = register("fused_abyssal_sand", () -> new Block(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.SNARE).strength(0.5F, r(2.5F)).sound(SoundType.SAND)),
			b -> b.model(Model.NONE).tool(Tool.SHOVEL).dropOther("abyssal_sand"));
	public static final RegistryObject<Block> ABYSSAL_SAND_GLASS = register("abyssal_sand_glass", () -> new GlassBlock(Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion()
			.isValidSpawn((s, l, p, e) -> false).isRedstoneConductor((s, l, p) -> false).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)),
			b -> b.model(Model.GLASS, "abyssalsandglass").tool(Tool.NONE).drop(Drop.NONE));
	public static final RegistryObject<Block> LUMINOUS_THISTLE = register("luminous_thistle", () -> new BlockACPlant(plant().lightLevel(s -> 8), false),
			b -> b.model(Model.CROSS, "luminousthistle").tool(Tool.NONE).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> WASTELANDS_THORN = register("wastelands_thorn", () -> new BlockACPlant(plant(), true),
			b -> b.model(Model.CROSS, "wastelandsthorn").tool(Tool.NONE).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> SOLID_LAVA = register("solid_lava", () -> new BlockSolidLava(rock(MapColor.FIRE, 10.0F, r(100.0F))),
			b -> b.custom("solidlava").level(2).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> CALCIFIED_STONE = register("calcified_stone", () -> new Block(rock(MapColor.COLOR_GRAY, 12.0F, r(30.0F))),
			b -> b.cube("calcifiedstone").level(3).drop(Drop.CUSTOM));
	public static final RegistryObject<Block> ETHAXIUM_PILLAR = register("ethaxium_pillar", () -> new RotatedPillarBlock(rock(MapColor.WOOL, 100.0F, UNBREAKABLE_RESISTANCE)),
			b -> b.pillar("ebp", "ebp_top").level(8).immune(true, true));
	public static final RegistryObject<Block> DARK_ETHAXIUM_PILLAR = register("dark_ethaxium_pillar", () -> new RotatedPillarBlock(rock(MapColor.STONE, 150.0F, UNBREAKABLE_RESISTANCE)),
			b -> b.pillar("debp", "debp_top").level(8).immune(true, true));
	public static final RegistryObject<Block> MONOLITH_PILLAR = register("monolith_pillar", () -> new BlockMonolithPillar(rock(MapColor.COLOR_BLACK, 6.0F, r(24.0F))),
			b -> b.model(Model.NONE).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> DREADLANDS_INFUSED_POWERSTONE = register("dreadlands_infused_powerstone", () -> new BlockPSDL(rock(MapColor.STONE, 50.0F, r(3000.0F))),
			b -> b.custom("psdl").level(5).tab(ACTabs.Tab.DECORATION).itemModel("psdl"));
	public static final RegistryObject<Block> ALTAR = register("altar", () -> new BlockShaped(rock(MapColor.STONE, 4.0F, r(300.0F)), Block.box(1.6, 0, 1.6, 14.4, 11.2, 14.4)),
			b -> b.custom("altar").level(3).noItemTab().itemModel("altar"));

	// Ores
	public static final RegistryObject<Block> CORALIUM_ORE = ore("coralium_ore", 2, 3.0F, 6.0F, MapColor.STONE, "co", "coralium_gem", 1, 3, 0, 2);
	public static final RegistryObject<Block> ABYSSALNITE_ORE = ore("abyssalnite_ore", 2, 3.0F, 6.0F, MapColor.STONE, "ao", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> CORALIUM_INFUSED_STONE = ore("coralium_infused_stone", 3, 3.0F, 6.0F, MapColor.STONE, "cis", "coralium_pearl", 1, 1, 2, 5);
	public static final RegistryObject<Block> ABYSSAL_CORALIUM_ORE = ore("abyssal_coralium_ore", 3, 3.0F, 6.0F, MapColor.COLOR_GREEN, "acoro", "coralium_gem", 1, 3, 0, 2);
	public static final RegistryObject<Block> DREADED_ABYSSALNITE_ORE = ore("dreaded_abyssalnite_ore", 4, 2.5F, 20.0F, MapColor.COLOR_RED, "drso", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> DREADLANDS_ABYSSALNITE_ORE = ore("dreadlands_abyssalnite_ore", 4, 2.5F, 20.0F, MapColor.COLOR_RED, "abydrso", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> NITRE_ORE = ore("nitre_ore", 2, 3.0F, 6.0F, MapColor.STONE, "no", "nitre", 1, 3, 0, 2);
	public static final RegistryObject<Block> ABYSSAL_IRON_ORE = ore("abyssal_iron_ore", 2, 3.0F, 6.0F, MapColor.COLOR_GREEN, "aio", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> ABYSSAL_GOLD_ORE = ore("abyssal_gold_ore", 2, 5.0F, 10.0F, MapColor.COLOR_GREEN, "ago", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> ABYSSAL_DIAMOND_ORE = ore("abyssal_diamond_ore", 2, 5.0F, 10.0F, MapColor.COLOR_GREEN, "ado", "minecraft:diamond", 1, 1, 3, 7);
	public static final RegistryObject<Block> ABYSSAL_NITRE_ORE = ore("abyssal_nitre_ore", 2, 3.0F, 6.0F, MapColor.COLOR_GREEN, "ano", "nitre", 1, 3, 0, 2);
	public static final RegistryObject<Block> ABYSSAL_TIN_ORE = ore("abyssal_tin_ore", 2, 3.0F, 6.0F, MapColor.COLOR_GREEN, "ato", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> ABYSSAL_COPPER_ORE = ore("abyssal_copper_ore", 2, 3.0F, 6.0F, MapColor.COLOR_GREEN, "aco", null, 1, 1, 0, 0);
	public static final RegistryObject<Block> PEARLESCENT_CORALIUM_ORE = ore("pearlescent_coralium_ore", 5, 8.0F, 10.0F, MapColor.COLOR_GREEN, "apcoro", "coralium_pearl", 1, 2, 2, 5);
	public static final RegistryObject<Block> LIQUIFIED_CORALIUM_ORE = ore("liquified_coralium_ore", 4, 10.0F, 12.0F, MapColor.COLOR_GREEN, "alcoro", null, 1, 1, 0, 0);

	// Storage blocks (IngotBlock)
	public static final RegistryObject<Block> ABYSSALNITE_BLOCK = storage("abyssalnite_block", 2, MapColor.COLOR_PURPLE, "boa", false);
	public static final RegistryObject<Block> REFINED_CORALIUM_BLOCK = storage("refined_coralium_block", 5, MapColor.COLOR_CYAN, "boc", false);
	public static final RegistryObject<Block> DREADIUM_BLOCK = storage("dreadium_block", 6, MapColor.COLOR_RED, "bod", false);
	public static final RegistryObject<Block> ETHAXIUM_BLOCK = storage("ethaxium_block", 8, MapColor.WOOL, "boe", true);

	// Fluids
	public static final RegistryObject<Block> LIQUID_CORALIUM = register("liquid_coralium", () -> new BlockCLiquid(ACFluids.LIQUID_CORALIUM,
			Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).replaceable().noCollission().strength(100.0F, r(500.0F)).pushReaction(PushReaction.DESTROY)
			.noLootTable().liquid().sound(SoundType.EMPTY).lightLevel(s -> 15)),
			b -> b.model(Model.FLUID, "cwater_still").tool(Tool.NONE).drop(Drop.NONE).noItem());
	public static final RegistryObject<Block> LIQUID_ANTIMATTER = register("liquid_antimatter", () -> new BlockAntiliquid(ACFluids.LIQUID_ANTIMATTER,
			Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).replaceable().noCollission().strength(100.0F, r(500.0F)).pushReaction(PushReaction.DESTROY)
			.noLootTable().liquid().sound(SoundType.EMPTY).lightLevel(s -> 8)),
			b -> b.model(Model.FLUID, "anti_still").tool(Tool.NONE).drop(Drop.NONE).noItem());

	// Gateways and the fires that light them
	public static final RegistryObject<Block> ABYSSAL_GATEWAY = gateway("abyssal_gateway", "ag", ABYSSAL_STONE, () -> BlockHandler.CORALIUM_FIRE.get(),
			ACDimensions.ABYSSAL_WASTELAND, net.minecraft.world.level.Level.OVERWORLD, ACPoiTypes.ABYSSAL_GATEWAY, "abyssal_zombie");
	public static final RegistryObject<Block> DREADED_GATEWAY = gateway("dreaded_gateway", "dg", DREADSTONE, () -> BlockHandler.DREADED_FIRE.get(),
			ACDimensions.DREADLANDS, ACDimensions.ABYSSAL_WASTELAND, ACPoiTypes.DREADED_GATEWAY, "dreadling");
	public static final RegistryObject<Block> OMOTHOL_GATEWAY = gateway("omothol_gateway", "og", OMOTHOL_STONE, () -> BlockHandler.OMOTHOL_FIRE.get(),
			ACDimensions.OMOTHOL, ACDimensions.DREADLANDS, ACPoiTypes.OMOTHOL_GATEWAY, "minion_of_the_gatekeeper");
	public static final RegistryObject<Block> CORALIUM_FIRE = register("coralium_fire", () -> BlockACFire.coralium(fire(MapColor.COLOR_CYAN), ABYSSAL_GATEWAY),
			b -> b.model(Model.CROSS, "cfire_layer_0").tool(Tool.NONE).drop(Drop.NONE).noItem());
	public static final RegistryObject<Block> DREADED_FIRE = register("dreaded_fire", () -> BlockACFire.dread(fire(MapColor.COLOR_RED), DREADED_GATEWAY),
			b -> b.model(Model.CROSS, "dfire_layer_0").tool(Tool.NONE).drop(Drop.NONE).noItem());
	public static final RegistryObject<Block> OMOTHOL_FIRE = register("omothol_fire", () -> BlockACFire.omothol(fire(MapColor.COLOR_BLACK), OMOTHOL_GATEWAY),
			b -> b.model(Model.CROSS, "ofire_layer_0").tool(Tool.NONE).drop(Drop.NONE).noItem());
	public static final RegistryObject<Block> MIMIC_FIRE = register("mimic_fire", () -> new BlockMimicFire(fire(MapColor.FIRE)),
			b -> b.model(Model.NONE).tool(Tool.NONE).drop(Drop.NONE).noItem());

	// Potential Energy (1.12.2 tiered blocks: one block per dimension tier)
	public static final RegistryObject<Block> ENERGY_PEDESTAL = pe("energy_pedestal", t -> new BlockEnergyPedestal(peProps(), t), BlockPE.BASE, "energypedestal");
	public static final RegistryObject<Block> SACRIFICIAL_ALTAR = pe("sacrificial_altar", t -> new BlockSacrificialAltar(peProps(), t), BlockPE.BASE, "sacrificialaltar");
	public static final RegistryObject<Block> ENERGY_COLLECTOR = pe("energy_collector", t -> new BlockEnergyCollector(peProps(), t), BlockPE.BASE, "energycollector");
	public static final RegistryObject<Block> ENERGY_RELAY = pe("energy_relay", t -> new BlockEnergyRelay(peProps(), t), BlockPE.BASE, "energyrelay");
	public static final RegistryObject<Block> ENERGY_CONTAINER = pe("energy_container", t -> new BlockEnergyContainer(peProps(), t), BlockPE.BASE, "energycontainer");
	public static final List<RegistryObject<Block>> TIERED_ENERGY_PEDESTALS = new ArrayList<>(), TIERED_SACRIFICIAL_ALTARS = new ArrayList<>(),
			TIERED_ENERGY_COLLECTORS = new ArrayList<>(), TIERED_ENERGY_RELAYS = new ArrayList<>(), TIERED_ENERGY_CONTAINERS = new ArrayList<>();
	static {
		for(int i = 0; i < 4; i++) {
			String dim = BlockPE.TIER_NAMES[i];
			TIERED_ENERGY_PEDESTALS.add(pe(dim + "_energy_pedestal", t -> new BlockEnergyPedestal(peProps(), t), i, "tieredenergypedestal_" + i));
			TIERED_SACRIFICIAL_ALTARS.add(pe(dim + "_sacrificial_altar", t -> new BlockSacrificialAltar(peProps(), t), i, "tieredsacrificialaltar_" + i));
			TIERED_ENERGY_COLLECTORS.add(pe(dim + "_energy_collector", t -> new BlockEnergyCollector(peProps(), t), i, "tieredenergycollector_" + i));
			TIERED_ENERGY_RELAYS.add(pe(dim + "_energy_relay", t -> new BlockEnergyRelay(peProps(), t), i,
					new String[] {"minecraft:block/stone", "as", "drs", "os"}[i]));
			TIERED_ENERGY_CONTAINERS.add(pe(dim + "_energy_container", t -> new BlockEnergyContainer(peProps(), t), i, "tieredenergycontainer_" + i));
		}
	}

	public static final RegistryObject<Block> SHOGGOTH_OOZE = register("shoggoth_ooze", () -> new BlockShoggothOoze(Properties.of().mapColor(MapColor.COLOR_BLACK)
			.replaceable().forceSolidOff().randomTicks().strength(1.0F, r(1.0F)).sound(SoundType.SLIME_BLOCK).pushReaction(PushReaction.DESTROY)
			.isViewBlocking((state, level, pos) -> state.getValue(SnowLayerBlock.LAYERS) >= 8)),
			b -> b.model(Model.LAYERS, "shoggothooze").tool(Tool.SHOVEL).drop(Drop.NONE));
	public static final RegistryObject<Block> SHOGGOTH_BIOMASS = register("shoggoth_biomass", () -> new BlockShoggothBiomass(Properties.of().mapColor(MapColor.COLOR_GREEN)
			.strength(1.0F, r(18.0F)).sound(SoundType.SAND).lightLevel(s -> 7)),
			b -> b.cube("shoggothbiomass").tool(Tool.SHOVEL).drop(Drop.NONE));

	// Altar of Cha'garoth (forge:obj models); 1.12.2 harvest level 6
	public static final RegistryObject<Block> CHAGAROTH_ALTAR_TOP = register("chagaroth_altar_top", () -> new BlockDreadAltar(rock(MapColor.COLOR_RED, 30.0F, r(300.0F)).noOcclusion(), true),
			b -> b.custom("dreadaltartop").level(6).tab(ACTabs.Tab.DECORATION));
	public static final RegistryObject<Block> CHAGAROTH_ALTAR_BOTTOM = register("chagaroth_altar_bottom", () -> new BlockDreadAltar(rock(MapColor.COLOR_RED, 30.0F, r(300.0F)).noOcclusion(), false),
			b -> b.custom("dreadaltarbottom").level(6).tab(ACTabs.Tab.DECORATION));

	// Single mob spawners (invisible, placed by structures): mob, activation range
	public static final RegistryObject<Block> DREADGUARD_SPAWNER = mobSpawner("dreadguard_spawner", ACEntities.dreadguard, 16, false);
	public static final RegistryObject<Block> CHAGAROTH_SPAWNER = mobSpawner("chagaroth_spawner", ACEntities.chagaroth, 32, false);
	public static final RegistryObject<Block> JZAHAR_SPAWNER = mobSpawner("jzahar_spawner", ACEntities.jzahar, 12, true);
	public static final RegistryObject<Block> MINION_OF_THE_GATEKEEPER_SPAWNER = mobSpawner("minion_of_the_gatekeeper_spawner", ACEntities.minion_of_the_gatekeeper, 16, false);
	public static final List<RegistryObject<Block>> MOB_SPAWNERS = List.of(DREADGUARD_SPAWNER, CHAGAROTH_SPAWNER, JZAHAR_SPAWNER, MINION_OF_THE_GATEKEEPER_SPAWNER);

	// Ritual altars and pedestals (1.12.2 MATERIAL metadata blocks); they drop the block they were made from
	public static final List<RegistryObject<Block>> RITUAL_ALTARS = new ArrayList<>(), RITUAL_PEDESTALS = new ArrayList<>();
	static {
		String[] materials = {"stone", "darkstone", "abyssal_stone", "coralium_stone", "dreadstone", "abyssalnite_stone", "ethaxium", "dark_ethaxium"};
		String[] drops = {"minecraft:cobblestone", "darkstone_cobblestone", "abyssal_cobblestone", "coralium_cobblestone", "dreadstone_cobblestone",
				"abyssalnite_cobblestone", "ethaxium_bricks", "dark_ethaxium_bricks"};
		for(int i = 0; i < materials.length; i++) {
			int index = i;
			RITUAL_ALTARS.add(register("ritual_altar_" + materials[i], () -> new BlockRitualAltar(rock(MapColor.STONE, 6.0F, r(12.0F)).lightLevel(s -> 5)),
					b -> b.custom("ritualaltar_" + index).dropOther(drops[index]).noItemTab()));
			RITUAL_PEDESTALS.add(register("ritual_pedestal_" + materials[i], () -> new BlockRitualPedestal(rock(MapColor.STONE, 6.0F, r(12.0F))),
					b -> b.custom("ritualpedestal_" + index).dropOther(drops[index]).noItemTab()));
		}
	}

	// Ghoul heads and the crate
	public static final RegistryObject<Block> DEPTHS_GHOUL_HEAD = head("depths_ghoul_head", "depths_ghoul", "dghead");
	public static final RegistryObject<Block> PETE_HEAD = head("pete_head", "depths_ghoul_pete", "phead");
	public static final RegistryObject<Block> MR_WILSON_HEAD = head("mr_wilson_head", "depths_ghoul_wilson", "whead");
	public static final RegistryObject<Block> DR_ORANGE_HEAD = head("dr_orange_head", "depths_ghoul_orange", "ohead");
	public static final RegistryObject<Block> WOODEN_CRATE = register("wooden_crate", () -> new BlockMachine<>(wood(MapColor.WOOD, 2.5F, r(2.5F)),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrate::new, ACBlockEntities.CRATE, null, null, null, false),
			b -> b.custom("crate").tool(Tool.AXE).tab(ACTabs.Tab.DECORATION));

	public static final RegistryObject<Block> STATE_TRANSFORMER = register("state_transformer", () -> new BlockStateTransformer(rock(MapColor.STONE, 2.0F, r(4.0F))),
			b -> b.model(Model.NONE).tab(ACTabs.Tab.DECORATION).itemModel("statetransformer"));

	public static final RegistryObject<Block> ENERGY_DEPOSITIONER = register("energy_depositioner", () -> new BlockMachine<>(rock(MapColor.STONE, 2.0F, r(4.0F)),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyDepositioner::new, ACBlockEntities.ENERGY_DEPOSITIONER,
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyDepositioner::serverTick, null, Block.box(1.92, 0, 1.92, 14.08, 12, 14.08), false),
			b -> b.custom("energydepositioner").tab(ACTabs.Tab.DECORATION));

	public static final RegistryObject<Block> RENDING_PEDESTAL = register("rending_pedestal", () -> new BlockRendingPedestal(peProps()),
			b -> b.custom("rendingpedestal").drop(Drop.PE).item(ItemPEContainerBlock::new).tab(ACTabs.Tab.DECORATION));

	public static final RegistryObject<Block> MULTI_BLOCK = register("multi_block", () -> new BlockMultiblock(rock(MapColor.COLOR_BLACK, 6.0F, r(24.0F))),
			b -> b.cube("multiblock").dropOther("monolith_stone").noItemTab());

	public static final RegistryObject<Block> SPIRIT_ALTAR = register("spirit_altar", () -> new BlockSpiritAltar(Properties.of().mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops().strength(2.5F, r(20.0F)).sound(SoundType.METAL)),
			b -> b.custom("spirit_altar").tab(ACTabs.Tab.DECORATION).unlock(new DimensionCondition(ACDimensions.OMOTHOL)));

	// Statues (1.12.2 statue / decorativestatue metadata blocks)
	public static final List<RegistryObject<Block>> STATUES = new ArrayList<>(), DECORATIVE_STATUES = new ArrayList<>();
	static {
		String[] names = {"cthulhu", "hastur", "jzahar", "azathoth", "nyarlathotep", "yog_sothoth", "shub_niggurath"};
		DeityType[] deities = {DeityType.CTHULHU, DeityType.HASTUR, DeityType.JZAHAR, DeityType.AZATHOTH, DeityType.NYARLATHOTEP,
				DeityType.YOGSOTHOTH, DeityType.SHUBNIGGURATH};
		for(int i = 0; i < names.length; i++) {
			DeityType deity = deities[i];
			String model = names[i].replace("_", "") + "statue";
			STATUES.add(register(names[i] + "_statue", () -> new BlockStatue(peProps(), deity),
					b -> b.model(Model.CUSTOM_FACING, model).tab(ACTabs.Tab.DECORATION)));
			DECORATIVE_STATUES.add(register("decorative_" + names[i] + "_statue", () -> new BlockDecorativeStatue(peProps()),
					b -> b.model(Model.CUSTOM_FACING, model).tab(ACTabs.Tab.DECORATION)));
		}
	}

	// Machines
	public static final RegistryObject<Block> CRYSTALLIZER = register("crystallizer", () -> new BlockProcessor(rock(MapColor.STONE, 2.5F, r(12.0F)),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrystallizer::new, ACBlockEntities.CRYSTALLIZER),
			b -> b.model(Model.ORIENTABLE_LIT, "crystallizer_front_off", "crystallizer_front_on", "crystallizer_side", "crystallizer_top").tab(ACTabs.Tab.CRYSTALS));
	public static final RegistryObject<Block> TRANSMUTATOR = register("transmutator", () -> new BlockProcessor(rock(MapColor.STONE, 2.5F, r(12.0F)),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityTransmutator::new, ACBlockEntities.TRANSMUTATOR),
			b -> b.model(Model.ORIENTABLE_LIT, "transmutator_front_off", "transmutator_front_on", "transmutator_side", "transmutator_top").tab(ACTabs.Tab.DECORATION));

	// 1.12.2 "engraver_on": crate texture, smaller box, unbreakable by explosions
	public static final RegistryObject<Block> HOUSE = register("house", () -> new com.shinoow.abyssalcraft.common.blocks.BlockHouse(wood(MapColor.WOOD, 1.0F, Float.MAX_VALUE).noOcclusion()),
			b -> b.cube("crate").tool(Tool.AXE));
	public static final RegistryObject<Block> ENGRAVER = register("engraver", () -> new BlockMachine<>(rock(MapColor.STONE, 2.5F, r(12.0F)).noOcclusion(),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEngraver::new, ACBlockEntities.ENGRAVER,
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEngraver::serverTick, null, null, true),
			b -> b.model(Model.CUSTOM_FACING, "engraver").tab(ACTabs.Tab.DECORATION));

	public static final RegistryObject<Block> MATERIALIZER = register("materializer", () -> new BlockMachine<>(rock(MapColor.STONE, 100.0F, UNBREAKABLE_RESISTANCE),
			com.shinoow.abyssalcraft.common.blocks.tile.TileEntityMaterializer::new, ACBlockEntities.MATERIALIZER, null, null, null, true),
			b -> b.model(Model.ORIENTABLE, "materializer_front", "materializer_side", "materializer_top").level(8).immune(true, true).tab(ACTabs.Tab.DECORATION));

	// Crystal clusters (crystalcluster / crystalcluster2 metadata blocks)
	public static final List<RegistryObject<Block>> CRYSTAL_CLUSTERS = new ArrayList<>();
	static {
		for(int i = 0; i < com.shinoow.abyssalcraft.api.item.ACItems.CRYSTAL_NAMES.length; i++) {
			int index = i;
			CRYSTAL_CLUSTERS.add(register(com.shinoow.abyssalcraft.api.item.ACItems.CRYSTAL_NAMES[i] + "_crystal_cluster",
					() -> new BlockCrystalCluster(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(4.0F, r(8.0F)).sound(SoundType.GLASS).noOcclusion(), index),
					b -> b.custom("crystalcluster").level(3).tab(ACTabs.Tab.CRYSTALS).itemModel("crystalcluster")));
		}
	}

	/**
	 * Fluent registration metadata builder
	 */
	public static class Builder {
		final BlockInfo info;
		Builder(BlockInfo info) { this.info = info; }
		public Builder model(Model model, String... textures) { info.model = model; info.textures = textures; return this; }
		public Builder cube(String texture) { return model(Model.CUBE, texture); }
		public Builder column(String side, String end) { return model(Model.COLUMN, side, end); }
		public Builder pillar(String side, String end) { return model(Model.PILLAR, side, end); }
		public Builder custom(String model) { return model(Model.CUSTOM, model); }
		public Builder level(int level) { info.harvestLevel = level; return this; }
		public Builder tool(Tool tool) { info.tool = tool; return this; }
		public Builder drop(Drop drop) { info.drop = drop; return this; }
		public Builder dropOther(String item) { info.drop = Drop.OTHER; info.dropItem = item; return this; }
		public Builder leaves(String sapling) { info.drop = Drop.LEAVES; info.dropItem = sapling; return this; }
		public Builder tab(ACTabs.Tab tab) { info.tab = tab; return this; }
		public Builder noItemTab() { info.tab = null; return this; }
		public Builder noItem() { info.hasItem = false; info.tab = null; return this; }
		public Builder parent(Supplier<Block> parent) { info.parent = parent; return this; }
		public Builder immune(boolean dragon, boolean wither) { info.dragonImmune = dragon; info.witherImmune = wither; return this; }
		public Builder itemModel(String upstream) { info.itemModel = upstream; return this; }
		public Builder item(BiFunction<Block, Item.Properties, Item> factory) { info.itemFactory = factory; return this; }
		public Builder unlock(IUnlockCondition condition) { info.unlock = condition; return this; }
	}

	private static RegistryObject<Block> gateway(String name, String texture, Supplier<Block> frame, Supplier<Block> fire,
			net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> destination, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> origin,
			net.minecraft.resources.ResourceKey<net.minecraft.world.entity.ai.village.poi.PoiType> poi, String mob) {
		return register(name, () -> new BlockACPortal(Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(s -> 11)
				.pushReaction(PushReaction.BLOCK).noLootTable(), frame, fire, destination, origin, poi, mob),
				b -> b.model(Model.PORTAL, texture).tool(Tool.NONE).drop(Drop.NONE).noItem());
	}

	/** Any of the seven statues (1.12.2: statue with the wildcard metadata) */
	public static net.minecraft.world.item.crafting.Ingredient statueIngredient() {
		return net.minecraft.world.item.crafting.Ingredient.of(STATUES.stream().map(RegistryObject::get).toArray(Block[]::new));
	}

	/** 1.12.2 BlockSingleMobSpawner: no hardness, no collision, no model, no item in the creative tabs */
	private static RegistryObject<Block> mobSpawner(String name, String entity, int range, boolean playerNotBelow) {
		return register(name, () -> new BlockSingleMobSpawner(Properties.of().mapColor(MapColor.STONE).noCollission().noOcclusion().instabreak(),
				entity, range, playerNotBelow), b -> b.model(Model.NONE).tool(Tool.NONE).drop(Drop.NONE).noItem());
	}

	private static RegistryObject<Block> head(String name, String texture, String upstream) {
		return register(name, () -> new BlockGhoulHead(Properties.of().mapColor(MapColor.WOOL).strength(1.0F, r(6.0F)).sound(SoundType.WOOL),
				new net.minecraft.resources.ResourceLocation(AbyssalCraft.modid, "textures/model/" + texture + ".png")),
				b -> b.custom(upstream).tool(Tool.NONE).tab(ACTabs.Tab.DECORATION).itemModel(upstream)
				.unlock(new com.shinoow.abyssalcraft.api.necronomicon.condition.EntityCondition("abyssalcraft:depths_ghoul")));
	}

	private static Properties peProps() {
		return rock(MapColor.STONE, 6.0F, r(12.0F));
	}

	/**
	 * Registers a PE storing block (keeps its PE when broken)
	 * @param model upstream model (relays: frame texture of the tiered relay)
	 */
	private static RegistryObject<Block> pe(String name, java.util.function.IntFunction<Block> block, int tier, String model) {
		boolean relay = name.endsWith("energy_relay");
		IUnlockCondition condition = tier == BlockPE.ABYSSAL_WASTELAND ? new DimensionCondition(ACDimensions.ABYSSAL_WASTELAND)
				: tier == BlockPE.DREADLANDS ? new DimensionCondition(ACDimensions.DREADLANDS) : tier == BlockPE.OMOTHOL ? new DimensionCondition(ACDimensions.OMOTHOL) : null;
		return register(name, () -> block.apply(tier), b -> {
			b.drop(Drop.PE).tab(ACTabs.Tab.DECORATION).item(ItemPEContainerBlock::new).unlock(condition);
			if(relay)
				return tier == BlockPE.BASE ? b.model(Model.RELAY, "energyrelay") : b.model(Model.RELAY, "energyrelay", model);
			return b.custom(model);
		});
	}

	private static Properties fire(MapColor color) {
		return Properties.of().mapColor(color).replaceable().noCollission().instabreak().lightLevel(s -> 15).sound(SoundType.WOOL)
				.pushReaction(PushReaction.DESTROY).noLootTable();
	}

	public static <T extends Block> RegistryObject<Block> register(String name, Supplier<T> block, Function<Builder, Builder> data) {
		RegistryObject<Block> obj = BLOCKS.register(name, block);
		BlockInfo info = new BlockInfo(obj);
		data.apply(new Builder(info));
		INFO.put(name, info);
		return obj;
	}

	/**
	 * Converts a 1.12.2 setResistance value to 1.20.1 explosion resistance
	 */
	public static float r(float resistance) {
		return resistance == Float.MAX_VALUE ? UNBREAKABLE_RESISTANCE : resistance * 3.0F / 5.0F;
	}

	public static Properties rock(MapColor color, float hardness, float resistance) {
		return Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(hardness, resistance).sound(SoundType.STONE);
	}

	public static Properties wood(MapColor color, float hardness, float resistance) {
		return Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASS).strength(hardness, r(resistance)).sound(SoundType.WOOD).ignitedByLava();
	}

	private static Properties leaves(MapColor color) {
		return Properties.of().mapColor(color).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
				.isValidSpawn((s, l, p, e) -> false).isSuffocating((s, l, p) -> false).isViewBlocking((s, l, p) -> false)
				.ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor((s, l, p) -> false);
	}

	private static Properties sapling() {
		return Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY);
	}

	private static Properties plant() {
		return Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY);
	}

	private static RegistryObject<Block> stone(String name, float hardness, float resistance, int level, MapColor color, String texture, String cobble) {
		boolean immuneDragon = name.equals("ethaxium") || name.equals("omothol_stone");
		boolean immuneWither = name.equals("ethaxium");
		return register(name, () -> new Block(rock(color, hardness, resistance)), b -> {
			b.cube(texture).level(level).immune(immuneDragon, immuneWither);
			return cobble != null ? b.dropOther(cobble) : b;
		});
	}

	private static RegistryObject<Block> cobble(String name, float hardness, float resistance, int level, MapColor color, String texture) {
		return register(name, () -> new Block(rock(color, hardness, resistance)), b -> b.cube(texture).level(level));
	}

	private static RegistryObject<Block> bricks(String name, float hardness, float resistance, int level, MapColor color, String texture, boolean ethaxium) {
		return register(name, () -> new Block(rock(color, hardness, r(resistance))), b -> b.cube(texture).level(level).immune(ethaxium, ethaxium));
	}

	private static RegistryObject<Block> slab(String name, Supplier<Block> parent, float hardness, float resistance, int level, String texture) {
		return register(name, () -> new SlabBlock(rock(parent.get().defaultMapColor(), hardness, r(resistance))),
				b -> b.model(Model.SLAB, texture).parent(parent).level(level).drop(Drop.SLAB).immune(resistance == Float.MAX_VALUE, resistance == Float.MAX_VALUE));
	}

	private static RegistryObject<Block> stairs(String name, Supplier<Block> parent, float hardness, float resistance, int level, String texture) {
		return register(name, () -> new StairBlock(() -> parent.get().defaultBlockState(), rock(parent.get().defaultMapColor(), hardness, r(resistance))),
				b -> b.model(Model.STAIRS, texture).parent(parent).level(level).immune(resistance == Float.MAX_VALUE, resistance == Float.MAX_VALUE));
	}

	private static RegistryObject<Block> wall(String name, Supplier<Block> parent, float hardness, float resistance, int level, String texture) {
		return register(name, () -> new WallBlock(rock(parent.get().defaultMapColor(), hardness, r(resistance)).forceSolidOn()),
				b -> b.model(Model.WALL, texture).parent(parent).level(level));
	}

	private static RegistryObject<Block> fence(String name, Supplier<Block> parent, float hardness, float resistance, int level, MapColor color, String texture) {
		return register(name, () -> new FenceBlock(rock(color, hardness, r(resistance))),
				b -> b.model(Model.FENCE, texture).parent(parent).level(level).immune(resistance == Float.MAX_VALUE, resistance == Float.MAX_VALUE));
	}

	private static RegistryObject<Block> button(String name, Supplier<Block> parent, float hardness, float resistance, int level, BlockSetType type, int ticks, String texture) {
		return register(name, () -> new ButtonBlock(Properties.of().noCollission().strength(hardness, r(resistance)).pushReaction(PushReaction.DESTROY), type, ticks, false),
				b -> b.model(Model.BUTTON, texture).parent(parent).level(level));
	}

	private static RegistryObject<Block> plate(String name, Supplier<Block> parent, float hardness, float resistance, int level, PressurePlateBlock.Sensitivity sensitivity, BlockSetType type, String texture) {
		return register(name, () -> new PressurePlateBlock(sensitivity, Properties.of().mapColor(parent.get().defaultMapColor()).requiresCorrectToolForDrops().noCollission().strength(hardness, r(resistance)).pushReaction(PushReaction.DESTROY), type),
				b -> b.model(Model.PRESSURE_PLATE, texture).parent(parent).level(level));
	}

	private static RegistryObject<Block> ore(String name, int level, float hardness, float resistance, MapColor color, String texture, String drop, int min, int max, int xpMin, int xpMax) {
		return register(name, () -> new DropExperienceBlock(rock(color, hardness, r(resistance)), net.minecraft.util.valueproviders.UniformInt.of(xpMin, xpMax)), b -> {
			b.cube(texture).level(level);
			if(drop != null) {
				b.info.drop = Drop.ORE;
				b.info.dropItem = drop;
				b.info.dropMin = min;
				b.info.dropMax = max;
			}
			return b;
		});
	}

	private static RegistryObject<Block> storage(String name, int level, MapColor color, String texture, boolean ethaxium) {
		return register(name, () -> new Block(Properties.of().mapColor(color).requiresCorrectToolForDrops().strength(4.0F, ethaxium ? UNBREAKABLE_RESISTANCE : r(12.0F)).sound(SoundType.METAL)),
				b -> b.cube(texture).level(level).immune(ethaxium, ethaxium));
	}
}
