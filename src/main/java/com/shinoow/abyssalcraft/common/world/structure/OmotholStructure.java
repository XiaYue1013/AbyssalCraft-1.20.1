/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (StructureCity, StructureTemple, StructureTower, StructureStorage)
 *     1.20.1 port - one structure type for the Omothol buildings. The 1.12.2 minimum distances are the structure
 *     sets' spacing. Rotated buildings are placed so their footprint starts at the chosen spot (1.12.2 tried to,
 *     but its width swap had no effect).
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.init.ACStructures;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class OmotholStructure extends Structure {

	public enum Kind implements StringRepresentable {
		/** StructureCity: bar, blacksmith, church, farm, farmhouse, house or library */
		BUILDING,
		/** StructureTemple: a temple with three working statues */
		TEMPLE,
		/** StructureTower: two templates stacked */
		TOWER,
		/** StructureStorage: a storage hall with crates, one of them holding a Configurator Shard */
		STORAGE;

		public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

	public static final Codec<OmotholStructure> CODEC = RecordCodecBuilder.create(i -> i.group(settingsCodec(i),
			Kind.CODEC.fieldOf("kind").forGetter(s -> s.kind)).apply(i, OmotholStructure::new));

	private static final String[] BUILDINGS = {"omothol/bar", "omothol/blacksmith", "omothol/church", "omothol/farm", "omothol/farmhouse",
			"omothol/house", "omothol/library"};

	private final Kind kind;

	public OmotholStructure(StructureSettings settings, Kind kind) {
		super(settings);
		this.kind = kind;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		if(!ACConfig.generateOmotholStructures)
			return Optional.empty();
		RandomSource random = context.random();
		int x0 = context.chunkPos().getMinBlockX(), z0 = context.chunkPos().getMinBlockZ();
		return switch(kind) {
		case TOWER -> tower(context, x0 + random.nextInt(8) + 8, z0 + random.nextInt(8) + 8);
		case TEMPLE -> rotated(context, "omothol/temple", x0 + random.nextInt(2) + 1, z0 + random.nextInt(2) + 1, Rotation.getRandom(random), 0,
				StructureMarkers.Kind.OMOTHOL_TEMPLE, "");
		case STORAGE -> storage(context, x0 + random.nextInt(7) + 7, z0 + random.nextInt(7) + 7, Rotation.getRandom(random));
		case BUILDING -> {
			int num = random.nextInt(7);
			int offset = num == 2 || num == 5 || num == 6 ? random.nextInt(4) + 4 : random.nextInt(8) + 8;
			yield rotated(context, BUILDINGS[num], x0 + offset, z0 + offset, Rotation.getRandom(random), num == 0 ? -4 : num == 3 ? -1 : 0,
					StructureMarkers.Kind.OMOTHOL_BUILDING, String.valueOf(num));
		}
		};
	}

	/** The tower stands on the ground 6 blocks around its center */
	private Optional<GenerationStub> tower(GenerationContext context, int x, int z) {
		int y = StructureUtil.surface(context, x, z) - 1;
		if(y <= context.heightAccessor().getMinBuildHeight() + 1)
			return Optional.empty();
		for(int[] d : new int[][] {{6, 0}, {-6, 0}, {0, 6}, {0, -6}})
			if(!StructureUtil.hasGround(context, x + d[0], z + d[1], y))
				return Optional.empty();
		BlockPos pos = new BlockPos(x - 6, y + 1, z - 6);
		return Optional.of(new GenerationStub(pos, builder -> {
			builder.addPiece(new ACTemplatePiece(context.structureTemplateManager(), "omothol/tower_1", pos, Rotation.NONE, StructureMarkers.Kind.NONE, ""));
			builder.addPiece(new ACTemplatePiece(context.structureTemplateManager(), "omothol/tower_2", pos.above(32), Rotation.NONE, StructureMarkers.Kind.NONE, ""));
		}));
	}

	/**
	 * A rotated building whose footprint starts at x, z. Needs ground at its center and the middle of its four sides,
	 * and is raised to the ground at its center.
	 */
	private Optional<GenerationStub> rotated(GenerationContext context, String name, int x, int z, Rotation rotation, int yOffset, StructureMarkers.Kind marker, String data) {
		Optional<Placement> placement = place(context, name, x, z, rotation);
		return placement.map(p -> new GenerationStub(p.templatePos.offset(0, yOffset, 0), builder -> builder.addPiece(
				new ACTemplatePiece(context.structureTemplateManager(), name, p.templatePos.offset(0, yOffset, 0), rotation, marker, data))));
	}

	/** The storage hall with one of the four crate arrangements in its corner */
	private Optional<GenerationStub> storage(GenerationContext context, int x, int z, Rotation rotation) {
		Optional<Placement> placement = place(context, "omothol/storage", x, z, rotation);
		if(placement.isEmpty())
			return Optional.empty();
		String crates = "omothol/crates_" + (context.random().nextInt(4) + 1);
		Optional<StructureTemplate> crateTemplate = StructureUtil.template(context, AbyssalCraft.loc(crates));
		if(crateTemplate.isEmpty())
			return Optional.empty();
		// 1.12.2 gave each crate a 1 in 5 chance to hold the treasure until one did, which with this many crates is
		// almost always exactly one of them
		List<String> markers = StructureUtil.dataMarkers(crateTemplate.get(), rotation).stream().filter(m -> m.startsWith("crate")).toList();
		String treasure = markers.isEmpty() ? "" : markers.get(context.random().nextInt(markers.size()));
		// crates_N sit 4 blocks in from the storage's corner
		BoundingBox crateBox = crateTemplate.get().getBoundingBox(ACTemplatePiece.settings(rotation), BlockPos.ZERO);
		Placement p = placement.get();
		BlockPos footprint = p.footprint;
		BlockPos cratePos = footprint.offset(4 - crateBox.minX(), 0, 4 - crateBox.minZ());
		return Optional.of(new GenerationStub(p.templatePos, builder -> {
			builder.addPiece(new ACTemplatePiece(context.structureTemplateManager(), "omothol/storage", p.templatePos, rotation, StructureMarkers.Kind.NONE, ""));
			builder.addPiece(new ACTemplatePiece(context.structureTemplateManager(), crates, cratePos, rotation, StructureMarkers.Kind.OMOTHOL_CRATES, treasure));
		}));
	}

	private record Placement(BlockPos templatePos, BlockPos footprint) {}

	private Optional<Placement> place(GenerationContext context, String name, int x, int z, Rotation rotation) {
		Optional<StructureTemplate> template = StructureUtil.template(context, AbyssalCraft.loc(name));
		if(template.isEmpty() || template.get().getSize().getX() == 0) // templates are imported locally, see tools/import_assets.py
			return Optional.empty();
		int y = StructureUtil.surface(context, x, z) - 1;
		if(y <= context.heightAccessor().getMinBuildHeight() + 1)
			return Optional.empty();
		Vec3i size = template.get().getSize(rotation);
		int distX = size.getX() / 2, distZ = size.getZ() / 2;
		int cx = x + distX, cz = z + distZ;
		for(int[] d : new int[][] {{0, 0}, {distX, 0}, {-distX, 0}, {0, distZ}, {0, -distZ}})
			if(!StructureUtil.hasGround(context, cx + d[0], cz + d[1], y))
				return Optional.empty();
		y = Math.max(y, StructureUtil.surface(context, cx, cz));
		BoundingBox box = template.get().getBoundingBox(ACTemplatePiece.settings(rotation), BlockPos.ZERO);
		BlockPos footprint = new BlockPos(x, y, z);
		return Optional.of(new Placement(footprint.offset(-box.minX(), 0, -box.minZ()), footprint));
	}

	@Override
	public StructureType<?> type() {
		return ACStructures.OMOTHOL.get();
	}
}
