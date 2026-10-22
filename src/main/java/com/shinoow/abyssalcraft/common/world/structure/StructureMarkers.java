/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (StructureShoggothPit, StructureTemple, StructureStorage, StructureCity, chagarothlair)
 *     1.20.1 port - the data marker handling of the template structures in one place
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;
import java.util.Map;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrate;
import com.shinoow.abyssalcraft.common.entity.EntityRemnant;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class StructureMarkers {

	public enum Kind {
		NONE,
		/** data: lair variant 1 - 3 */
		SHOGGOTH_LAIR,
		OMOTHOL_TEMPLE,
		/** data: the data marker of the crate that gets the treasure */
		OMOTHOL_CRATES,
		/** data: the building number (StructureCity) */
		OMOTHOL_BUILDING,
		CHAGAROTH_LAIR
	}

	/** Facing of the statues in the three Shoggoth Lairs, by data marker */
	private static final List<Map<String, Direction>> LAIR_STATUES = List.of(
			Map.of("statue1", Direction.EAST, "statue2", Direction.EAST, "statue3", Direction.EAST, "statue4", Direction.EAST,
					"statue5", Direction.WEST, "statue6", Direction.WEST, "statue7", Direction.WEST, "statue8", Direction.WEST),
			Map.of("statue1", Direction.EAST, "statue2", Direction.SOUTH, "statue3", Direction.WEST),
			Map.of("statue1", Direction.SOUTH));

	private static final int FLAGS = Block.UPDATE_CLIENTS;

	public static void handle(Kind kind, String data, Rotation rotation, String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random) {
		switch(kind) {
		case SHOGGOTH_LAIR -> {
			Direction facing = LAIR_STATUES.get(Integer.parseInt(data) - 1).get(marker);
			if(facing != null)
				level.setBlock(pos, random.nextInt(10) < 6 ? ACBlocks.monolith_stone.get().defaultBlockState() : lairStatue(random, facing), FLAGS);
			else if(marker.startsWith("bm"))
				level.setBlock(pos, ACBlocks.shoggoth_biomass.get().defaultBlockState(), FLAGS);
		}
		case OMOTHOL_TEMPLE -> {
			Direction base = switch(marker) {
			case "statue_center" -> Direction.SOUTH;
			case "statue_left" -> Direction.EAST;
			case "statue_right" -> Direction.WEST;
			default -> null;
			};
			if(base != null) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), FLAGS);
				level.setBlock(pos.below(), statue(BlockHandler.STATUES, random, rotation.rotate(base)), FLAGS);
			}
		}
		case OMOTHOL_CRATES -> {
			if(marker.startsWith("crate"))
				crate(level, pos, random, marker.equals(data) ? ACLoot.CHEST_OMOTHOL_STORAGE_TREASURE : ACLoot.CHEST_OMOTHOL_STORAGE_JUNK);
		}
		case OMOTHOL_BUILDING -> omotholBuilding(Integer.parseInt(data), marker, pos, level, random);
		case CHAGAROTH_LAIR -> {
			if("chest".equals(marker)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), FLAGS);
				if(level.getBlockEntity(pos.below()) instanceof ChestBlockEntity chest)
					fillLegendaryTreasure(chest, random);
			}
		}
		default -> {}
		}
	}

	private static void omotholBuilding(int num, String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random) {
		if("chest".equals(marker))
			crate(level, pos, random, switch(num) {
			case 1 -> ACLoot.CHEST_OMOTHOL_BLACKSMITH;
			case 4 -> ACLoot.CHEST_OMOTHOL_FARMHOUSE;
			case 6 -> ACLoot.CHEST_OMOTHOL_LIBRARY;
			default -> ACLoot.CHEST_OMOTHOL_HOUSE;
			});
		else if("remnant".equals(marker)) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), FLAGS);
			EntityRemnant remnant = ACEntityTypes.REMNANT.get().create(level.getLevel());
			if(remnant == null) return;
			remnant.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
			remnant.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
			// a profession that fits the building
			remnant.setProfession(switch(num) {
			case 0 -> random.nextBoolean() ? 4 : 0;
			case 1 -> random.nextInt(3) == 0 ? 6 : 3;
			case 2 -> 2;
			case 4 -> 4;
			case 5 -> random.nextBoolean() ? 5 : 0;
			case 6 -> 1;
			default -> 0;
			});
			level.addFreshEntityWithPassengers(remnant);
		}
		else if(marker.startsWith("crystal")) {
			BlockState state = Blocks.AIR.defaultBlockState();
			if(random.nextBoolean())
				state = random.nextInt(100) == 0 ? ACBlocks.dreadlands_infused_powerstone.get().defaultBlockState()
						: BlockHandler.CRYSTAL_CLUSTERS.get(random.nextInt(BlockHandler.CRYSTAL_CLUSTERS.size())).get().defaultBlockState();
			level.setBlock(pos, state, FLAGS);
		} else if("pedestal".equals(marker))
			level.setBlock(pos, BlockHandler.TIERED_ENERGY_PEDESTALS.get(random.nextInt(4)).get().defaultBlockState(), FLAGS);
	}

	private static void crate(ServerLevelAccessor level, BlockPos pos, RandomSource random, ResourceLocation lootTable) {
		level.setBlock(pos, ACBlocks.wooden_crate.get().defaultBlockState(), FLAGS);
		if(level.getBlockEntity(pos) instanceof TileEntityCrate crate)
			crate.setLootTable(lootTable, random.nextLong());
	}

	/** 1 in 5 (if enabled) a working statue, otherwise a decorative one */
	private static BlockState lairStatue(RandomSource random, Direction facing) {
		return statue(random.nextInt(5) == 0 && ACConfig.generateStatuesInLairs ? BlockHandler.STATUES : BlockHandler.DECORATIVE_STATUES, random, facing);
	}

	private static BlockState statue(List<RegistryObject<Block>> statues, RandomSource random, Direction facing) {
		BlockState state = statues.get(random.nextInt(statues.size())).get().defaultBlockState();
		if(state.hasProperty(BlockStatue.FACING))
			return state.setValue(BlockStatue.FACING, facing);
		return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.setValue(HorizontalDirectionalBlock.FACING, facing) : state;
	}

	/** chagarothlair: a named dirt block (1 in 10 a fried egg), and 1 in 100 a set of crystals and an Oblivion Catalyst */
	private static void fillLegendaryTreasure(ChestBlockEntity chest, RandomSource random) {
		Component name = Component.literal("The Legendary Treasure of The Dreadlands").withStyle(ChatFormatting.GOLD);
		ItemStack treasure = random.nextInt(10) == 0 && ACConfig.foodstuff ? new ItemStack(ACItems.fried_egg.get()) : new ItemStack(Items.DIRT);
		chest.setItem(13, treasure.setHoverName(name));
		if(random.nextInt(100) == 0) {
			int[] crystals = {0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24};
			for(int i = 0; i < crystals.length; i++)
				chest.setItem(i < 13 ? i : i + 1, new ItemStack(ItemHandler.CRYSTALS.get(crystals[i]).get()));
			if(ACItems.oblivion_catalyst.isPresent())
				chest.setItem(21, new ItemStack(ACItems.oblivion_catalyst.get()));
		}
	}
}
