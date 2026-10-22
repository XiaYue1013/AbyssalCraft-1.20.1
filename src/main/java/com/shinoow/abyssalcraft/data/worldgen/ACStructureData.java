/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - structures and structure sets. Spacings approximate the 1.12.2 per chunk chances and minimum
 *     distances: lairs 1 in 200 chunks (swamps 1 in 35, Omothol 1 in 100) and at least 100 blocks apart, Omothol
 *     temples/towers every ~200 blocks, storages every ~300 blocks, buildings wherever the land allows.
 ******************************************************************************/
package com.shinoow.abyssalcraft.data.worldgen;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.world.structure.AbyssalStrongholdStructure;
import com.shinoow.abyssalcraft.common.world.structure.DreadlandsMineStructure;
import com.shinoow.abyssalcraft.common.world.structure.FixedStructurePlacement;
import com.shinoow.abyssalcraft.common.world.structure.JzaharTempleStructure;
import com.shinoow.abyssalcraft.common.world.structure.OmotholStructure;
import com.shinoow.abyssalcraft.common.world.structure.ShoggothLairStructure;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public class ACStructureData {

	public static final ResourceKey<Structure> SHOGGOTH_LAIR = structure("shoggoth_lair");
	public static final ResourceKey<Structure> SHOGGOTH_LAIR_OVERWORLD = structure("shoggoth_lair_overworld");
	public static final ResourceKey<Structure> SHOGGOTH_LAIR_OMOTHOL = structure("shoggoth_lair_omothol");
	public static final ResourceKey<Structure> OMOTHOL_BUILDING = structure("omothol_building");
	public static final ResourceKey<Structure> OMOTHOL_TEMPLE = structure("omothol_temple");
	public static final ResourceKey<Structure> OMOTHOL_TOWER = structure("omothol_tower");
	public static final ResourceKey<Structure> OMOTHOL_STORAGE = structure("omothol_storage");
	public static final ResourceKey<Structure> JZAHAR_TEMPLE = structure("jzahar_temple");
	public static final ResourceKey<Structure> ABYSSAL_STRONGHOLD = structure("abyssal_stronghold");
	public static final ResourceKey<Structure> DREADLANDS_MINE = structure("dreadlands_mine");

	public static final ResourceKey<StructureSet> SHOGGOTH_LAIRS = set("shoggoth_lairs");
	public static final ResourceKey<StructureSet> SHOGGOTH_LAIRS_OVERWORLD = set("shoggoth_lairs_overworld");
	public static final ResourceKey<StructureSet> SHOGGOTH_LAIRS_OMOTHOL = set("shoggoth_lairs_omothol");
	public static final ResourceKey<StructureSet> OMOTHOL_BUILDINGS = set("omothol_buildings");
	public static final ResourceKey<StructureSet> OMOTHOL_LANDMARKS = set("omothol_landmarks");
	public static final ResourceKey<StructureSet> JZAHAR_TEMPLES = set("jzahar_temple");
	public static final ResourceKey<StructureSet> ABYSSAL_STRONGHOLDS = set("abyssal_strongholds");
	public static final ResourceKey<StructureSet> DREADLANDS_MINES = set("dreadlands_mines");

	public static void bootstrapStructures(BootstapContext<Structure> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		context.register(SHOGGOTH_LAIR, new ShoggothLairStructure(settings(biomes, ACTags.Biomes.HAS_SHOGGOTH_LAIR), false));
		context.register(SHOGGOTH_LAIR_OVERWORLD, new ShoggothLairStructure(settings(biomes, ACTags.Biomes.HAS_SHOGGOTH_LAIR_OVERWORLD), false));
		context.register(SHOGGOTH_LAIR_OMOTHOL, new ShoggothLairStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES), true));
		context.register(OMOTHOL_BUILDING, new OmotholStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES), OmotholStructure.Kind.BUILDING));
		context.register(OMOTHOL_TEMPLE, new OmotholStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES), OmotholStructure.Kind.TEMPLE));
		context.register(OMOTHOL_TOWER, new OmotholStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES), OmotholStructure.Kind.TOWER));
		context.register(OMOTHOL_STORAGE, new OmotholStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES), OmotholStructure.Kind.STORAGE));
		context.register(JZAHAR_TEMPLE, new JzaharTempleStructure(settings(biomes, ACTags.Biomes.HAS_OMOTHOL_STRUCTURES)));
		context.register(ABYSSAL_STRONGHOLD, new AbyssalStrongholdStructure(new Structure.StructureSettings(biomes.getOrThrow(ACTags.Biomes.HAS_ABYSSAL_STRONGHOLD), Map.of(),
				GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
		context.register(DREADLANDS_MINE, new DreadlandsMineStructure(new Structure.StructureSettings(biomes.getOrThrow(ACTags.Biomes.HAS_DREADLANDS_MINE), Map.of(),
				GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
	}

	public static void bootstrapSets(BootstapContext<StructureSet> context) {
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		HolderGetter<StructureSet> sets = context.lookup(Registries.STRUCTURE_SET);
		context.register(SHOGGOTH_LAIRS, new StructureSet(structures.getOrThrow(SHOGGOTH_LAIR), spread(14, 7, 1426375310, Optional.empty())));
		context.register(SHOGGOTH_LAIRS_OVERWORLD, new StructureSet(structures.getOrThrow(SHOGGOTH_LAIR_OVERWORLD), spread(7, 6, 1426375311, Optional.empty())));
		context.register(SHOGGOTH_LAIRS_OMOTHOL, new StructureSet(structures.getOrThrow(SHOGGOTH_LAIR_OMOTHOL), spread(10, 6, 1426375312, Optional.empty())));
		// one landmark per region; the buildings stay out of the landmarks' way (1.12.2 StructureCity#tooClose)
		context.register(OMOTHOL_LANDMARKS, new StructureSet(List.of(StructureSet.entry(structures.getOrThrow(OMOTHOL_TEMPLE), 3),
				StructureSet.entry(structures.getOrThrow(OMOTHOL_TOWER), 3), StructureSet.entry(structures.getOrThrow(OMOTHOL_STORAGE), 2)),
				spread(13, 9, 1873625310, Optional.empty())));
		context.register(OMOTHOL_BUILDINGS, new StructureSet(structures.getOrThrow(OMOTHOL_BUILDING),
				spread(2, 1, 1873625311, Optional.of(new StructurePlacement.ExclusionZone(sets.getOrThrow(OMOTHOL_LANDMARKS), 2)))));
		// MapGenAbyStronghold: 128 strongholds on rings around the origin (distance 32, 3 on the first ring)
		context.register(ABYSSAL_STRONGHOLDS, new StructureSet(structures.getOrThrow(ABYSSAL_STRONGHOLD), new ConcentricRingsStructurePlacement(32, 3, 128,
				context.lookup(Registries.BIOME).getOrThrow(ACTags.Biomes.HAS_ABYSSAL_STRONGHOLD))));
		// MapGenDreadlandsMine: 0.4 % of the chunks (the same legacy chance check as 1.12.2 mineshafts)
		context.register(DREADLANDS_MINES, new StructureSet(structures.getOrThrow(DREADLANDS_MINE), new RandomSpreadStructurePlacement(Vec3i.ZERO,
				StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004F, 0, Optional.empty(), 1, 0, RandomSpreadType.LINEAR)));
		context.register(JZAHAR_TEMPLES, new StructureSet(structures.getOrThrow(JZAHAR_TEMPLE), new FixedStructurePlacement(new ChunkPos(0, 0), 1873625312)));
	}

	private static RandomSpreadStructurePlacement spread(int spacing, int separation, int salt, Optional<StructurePlacement.ExclusionZone> exclusion) {
		return new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, salt, exclusion, spacing, separation,
				RandomSpreadType.LINEAR);
	}

	private static Structure.StructureSettings settings(HolderGetter<Biome> biomes, TagKey<Biome> tag) {
		return new Structure.StructureSettings(biomes.getOrThrow(tag), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE);
	}

	private static ResourceKey<Structure> structure(String name) {
		return ResourceKey.create(Registries.STRUCTURE, AbyssalCraft.loc(name));
	}

	private static ResourceKey<StructureSet> set(String name) {
		return ResourceKey.create(Registries.STRUCTURE_SET, AbyssalCraft.loc(name));
	}
}
