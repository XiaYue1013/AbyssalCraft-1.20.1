/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - automated verification (./gradlew runGameTestServer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.gametest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrate;
import com.shinoow.abyssalcraft.data.worldgen.ACStructureData;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * GameTests for the structures. The structure templates are restricted assets imported locally
 * (python tools/import_assets.py); without them these tests fail.
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class StructureTests {

	private static final String[] TEMPLATES = {"chagarothlair/chagarothlair_back", "chagarothlair/chagarothlair_front", "chagarothlair/chagarothlair_middle",
			"chagarothlair/chagarothlair_middle_left", "chagarothlair/chagarothlair_middle_right", "omothol/bar", "omothol/blacksmith", "omothol/church",
			"omothol/crates_1", "omothol/crates_2", "omothol/crates_3", "omothol/crates_4", "omothol/farm", "omothol/farmhouse", "omothol/house",
			"omothol/library", "omothol/storage", "omothol/temple", "omothol/tower_1", "omothol/tower_2", "shoggothlair/shoggothlair_1",
			"shoggothlair/shoggothlair_2", "shoggothlair/shoggothlair_3", "temple/jzahartemple_back", "temple/jzahartemple_front_left",
			"temple/jzahartemple_front_middle", "temple/jzahartemple_front_right", "temple/jzahartemple_middle_left", "temple/jzahartemple_middle_middle",
			"temple/jzahartemple_middle_right"};

	private static void log(String text) {
		AbyssalCraft.LOGGER.info("[StructureTests] {}", text);
	}

	/** Every AbyssalCraft block named by the converted templates exists, every template loads */
	@GameTest(template = "empty")
	public static void templatesConverted(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Set<String> unknown = new TreeSet<>();
		for(String name : TEMPLATES) {
			ResourceLocation file = AbyssalCraft.loc("structures/" + name + ".nbt");
			Resource resource = level.getServer().getResourceManager().getResource(file)
					.orElseThrow(() -> new GameTestAssertException(file + " missing, run python tools/import_assets.py"));
			try(InputStream in = resource.open()) {
				CompoundTag tag = NbtIo.readCompressed(in);
				for(Tag entry : tag.getList("palette", Tag.TAG_COMPOUND)) {
					String block = ((CompoundTag) entry).getString("Name");
					if(block.startsWith("abyssalcraft:") && !ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(block)))
						unknown.add(block + " (" + name + ")");
				}
			} catch(java.io.IOException e) {
				throw new GameTestAssertException("can't read " + file + ": " + e);
			}
			helper.assertTrue(level.getStructureManager().get(AbyssalCraft.loc(name)).filter(t -> t.getSize().getX() > 0).isPresent(), name + " doesn't load");
		}
		helper.assertTrue(unknown.isEmpty(), "unknown blocks in templates: " + unknown);
		helper.succeed();
	}

	/** Crates with a loot table fill when they're first looked into */
	@GameTest(template = "empty")
	public static void crateLootTable(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ACBlocks.wooden_crate.get());
		TileEntityCrate crate = (TileEntityCrate) helper.getBlockEntity(pos);
		crate.setLootTable(ACLoot.CHEST_OMOTHOL_STORAGE_JUNK, 42L);
		helper.assertFalse(crate.isEmpty(), "crate stayed empty");
		helper.succeed();
	}

	/** Finds the closest start of a structure, generates every chunk it touches and counts the blocks matching the predicate in it */
	private static int generate(GameTestHelper helper, ResourceKey<Level> dimension, ResourceKey<Structure> key, BlockPos from, int radius, Predicate<BlockState> counted) {
		ServerLevel level = helper.getLevel().getServer().getLevel(dimension);
		Holder<Structure> structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(key);
		BlockPos pos = from;
		if(radius > 0) { // (only random spread placements can be searched)
			Pair<BlockPos, Holder<Structure>> found = level.getChunkSource().getGenerator().findNearestMapStructure(level, HolderSet.direct(structure), from, radius, false);
			if(found == null)
				throw new GameTestAssertException("no " + key.location() + " within " + radius + " regions of " + from);
			pos = found.getFirst();
		}
		StructureStart start = level.structureManager().getStartForStructure(SectionPos.of(pos), structure.value(),
				level.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.STRUCTURE_STARTS));
		if(start == null || !start.isValid())
			throw new GameTestAssertException("no valid " + key.location() + " start at " + pos);
		BoundingBox box = start.getBoundingBox();
		for(int x = box.minX() >> 4; x <= box.maxX() >> 4; x++)
			for(int z = box.minZ() >> 4; z <= box.maxZ() >> 4; z++)
				level.getChunk(x, z);
		int count = 0;
		java.util.Map<String, Integer> blocks = new java.util.HashMap<>();
		for(BlockPos p : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
			BlockState state = level.getBlockState(p);
			blocks.merge(ForgeRegistries.BLOCKS.getKey(state.getBlock()).getPath(), 1, Integer::sum);
			if(counted.test(state))
				count++;
		}
		log(key.location() + " at " + pos + ", " + start.getPieces().size() + " pieces, box " + box + ", " + count + " matching blocks, "
				+ blocks.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(10).toList());
		return count;
	}

	private static Predicate<BlockState> abyssalcraft() {
		return state -> ForgeRegistries.BLOCKS.getKey(state.getBlock()).getNamespace().equals(AbyssalCraft.modid);
	}

	/** A Shoggoth Lair in the Abyssal Wasteland is built from its template */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void shoggothLairGenerates(GameTestHelper helper) {
		helper.assertTrue(generate(helper, ACDimensions.ABYSSAL_WASTELAND, ACStructureData.SHOGGOTH_LAIR, new BlockPos(20000, 64, 20000), 30,
				state -> state.is(ACBlocks.monolith_stone.get())) > 300, "the lair isn't built (lairs are mostly monolith stone)");
		// the "bm" data markers (9 - 45 per lair) become Shoggoth Biomass
		helper.assertTrue(generate(helper, ACDimensions.ABYSSAL_WASTELAND, ACStructureData.SHOGGOTH_LAIR, new BlockPos(20000, 64, 20000), 30,
				state -> state.is(ACBlocks.shoggoth_biomass.get())) >= 9, "no Shoggoth Biomass in the lair");
		helper.succeed();
	}

	/** Lairs also generate in overworld swamps and rivers */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void overworldShoggothLairGenerates(GameTestHelper helper) {
		helper.assertTrue(generate(helper, Level.OVERWORLD, ACStructureData.SHOGGOTH_LAIR_OVERWORLD, new BlockPos(20000, 64, -20000), 60,
				state -> state.is(ACBlocks.monolith_stone.get())) > 300, "the lair isn't built (lairs are mostly monolith stone)");
		helper.succeed();
	}

	/** The Temple of J'zahar stands at the Omothol origin */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void jzaharTempleGenerates(GameTestHelper helper) {
		int blocks = generate(helper, ACDimensions.OMOTHOL, ACStructureData.JZAHAR_TEMPLE, BlockPos.ZERO, 0, abyssalcraft());
		helper.assertTrue(blocks > 5000, "only " + blocks + " AbyssalCraft blocks in the temple");
		helper.succeed();
	}

	/** Omothol buildings stand on the floating land */
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void omotholBuildingGenerates(GameTestHelper helper) {
		List<String> problems = new ArrayList<>();
		int blocks = generate(helper, ACDimensions.OMOTHOL, ACStructureData.OMOTHOL_BUILDING, new BlockPos(3000, 64, 3000), 20, abyssalcraft());
		if(blocks < 50)
			problems.add("only " + blocks + " AbyssalCraft blocks in the building");
		helper.assertTrue(problems.isEmpty(), String.join(", ", problems));
		helper.succeed();
	}

	/** An Abyssal stronghold (on the rings around the origin) is built below sea level, with its portal room spawner */
	@GameTest(template = "empty", timeoutTicks = 2400)
	public static void abyssalStrongholdGenerates(GameTestHelper helper) {
		int bricks = generate(helper, ACDimensions.ABYSSAL_WASTELAND, ACStructureData.ABYSSAL_STRONGHOLD, BlockPos.ZERO, 100,
				state -> state.is(ACBlocks.abyssal_stone_bricks.get()) || state.is(ACBlocks.cracked_abyssal_stone_bricks.get()));
		helper.assertTrue(bricks > 1000, "only " + bricks + " abyssal stone bricks");
		helper.succeed();
	}

	/** A Dreadlands mine: dreadlands planks and fences below sea level */
	@GameTest(template = "empty", timeoutTicks = 2400)
	public static void dreadlandsMineGenerates(GameTestHelper helper) {
		int wood = generate(helper, ACDimensions.DREADLANDS, ACStructureData.DREADLANDS_MINE, new BlockPos(3000, 0, 3000), 100,
				state -> state.is(ACBlocks.dreadlands_planks.get()) || state.is(ACBlocks.dreadlands_wood_fence.get()));
		helper.assertTrue(wood > 50, "only " + wood + " dreadlands planks/fences");
		helper.succeed();
	}

	/** Every block-by-block structure builds (on the Abyssal Wasteland surface, far from everything else) */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void codedStructuresBuild(GameTestHelper helper) {
		ServerLevel aw = helper.getLevel().getServer().getLevel(ACDimensions.ABYSSAL_WASTELAND);
		List<Pair<String, java.util.function.Supplier<com.shinoow.abyssalcraft.common.world.gen.structure.LegacyStructure>>> structures = List.of(
				Pair.of("chains", com.shinoow.abyssalcraft.common.world.gen.structure.Chains::new),
				Pair.of("abyruin", com.shinoow.abyssalcraft.common.world.gen.structure.Abyruin::new),
				Pair.of("house1", com.shinoow.abyssalcraft.common.world.gen.structure.AChouse1::new),
				Pair.of("house2", com.shinoow.abyssalcraft.common.world.gen.structure.AChouse2::new),
				Pair.of("scion1", com.shinoow.abyssalcraft.common.world.gen.structure.ACscion1::new),
				Pair.of("scion2", com.shinoow.abyssalcraft.common.world.gen.structure.ACscion2::new),
				Pair.of("circular_shrine", com.shinoow.abyssalcraft.common.world.gen.structure.StructureCircularShrine::new),
				Pair.of("circular_shrine_columns", com.shinoow.abyssalcraft.common.world.gen.structure.StructureCircularShrineColumns::new),
				Pair.of("elevated_shrine", com.shinoow.abyssalcraft.common.world.gen.structure.StructureElevatedShrine::new),
				Pair.of("elevated_shrine_large", com.shinoow.abyssalcraft.common.world.gen.structure.StructureElevatedShrineLarge::new),
				Pair.of("ritual_grounds", com.shinoow.abyssalcraft.common.world.gen.structure.StructureRitualGrounds::new),
				Pair.of("ritual_grounds_columns", com.shinoow.abyssalcraft.common.world.gen.structure.StructureRitualGroundsColumns::new));
		List<String> problems = new ArrayList<>();
		StringBuilder report = new StringBuilder();
		for(int i = 0; i < structures.size(); i++) {
			int x = 60000 + i * 64, z = 60000;
			for(int cx = -1; cx <= 1; cx++)
				for(int cz = -1; cz <= 1; cz++)
					aw.getChunk((x >> 4) + cx, (z >> 4) + cz);
			int before = countAC(aw, x, z);
			BlockPos pos = new BlockPos(x, aw.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
			// the ruin only stands on abyssal stone or sand (the surface may be a coralium pool or a plant, depending on the seed)
			aw.setBlock(pos, ACBlocks.abyssal_stone.get().defaultBlockState(), 2);
			aw.setBlock(pos.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
			boolean built = structures.get(i).getSecond().get().generate(aw, net.minecraft.util.RandomSource.create(i), pos);
			int added = countAC(aw, x, z) - before;
			report.append(structures.get(i).getFirst()).append('=').append(added).append(' ');
			if(!built || added < 5)
				problems.add(structures.get(i).getFirst() + " (built " + built + ", " + added + " new AbyssalCraft blocks)");
		}
		log("coded structures, new AbyssalCraft blocks: " + report.toString().trim());
		helper.assertTrue(problems.isEmpty(), "not built: " + problems);
		helper.succeed();
	}

	/** AbyssalCraft blocks within 16 blocks of x, z (all heights), other than the terrain's own stone and sand */
	private static int countAC(ServerLevel level, int x, int z) {
		int count = 0;
		Predicate<BlockState> ac = abyssalcraft();
		for(BlockPos p : BlockPos.betweenClosed(x - 16, level.getMinBuildHeight(), z - 16, x + 16, level.getMaxBuildHeight() - 1, z + 16)) {
			BlockState state = level.getBlockState(p);
			if(ac.test(state) && !state.is(ACBlocks.abyssal_stone.get()) && !state.is(ACBlocks.abyssal_sand.get()) && !state.is(ACBlocks.fused_abyssal_sand.get())
					&& !state.is(ACBlocks.liquid_coralium.get()))
				count++;
		}
		return count;
	}
}
