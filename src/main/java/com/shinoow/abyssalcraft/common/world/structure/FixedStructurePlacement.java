/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - structure placement in exactly one chunk (1.12.2 MapGenOmothol#canSpawnStructureAtCoords)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.init.ACStructures;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public class FixedStructurePlacement extends StructurePlacement {

	public static final Codec<FixedStructurePlacement> CODEC = RecordCodecBuilder.create(i -> placementCodec(i)
			.and(i.group(Codec.INT.fieldOf("chunk_x").forGetter(p -> p.chunk.x), Codec.INT.fieldOf("chunk_z").forGetter(p -> p.chunk.z)))
			.apply(i, (offset, method, frequency, salt, exclusion, x, z) -> new FixedStructurePlacement(offset, method, frequency, salt, exclusion, new ChunkPos(x, z))));

	private final ChunkPos chunk;

	public FixedStructurePlacement(ChunkPos chunk, int salt) {
		this(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty(), chunk);
	}

	private FixedStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod method, float frequency, int salt, Optional<ExclusionZone> exclusion, ChunkPos chunk) {
		super(locateOffset, method, frequency, salt, exclusion);
		this.chunk = chunk;
	}

	@Override
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z) {
		return x == chunk.x && z == chunk.z;
	}

	@Override
	public StructurePlacementType<?> type() {
		return ACStructures.FIXED_PLACEMENT.get();
	}
}
