/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (MapGenDreadlandsMine, StructureDreadlandsMineStart)
 *     1.20.1 port - the 0.4 % chance per chunk is the structure set's frequency (legacy_type_3), the distance rule is
 *     checked here
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.init.ACStructures;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class DreadlandsMineStructure extends Structure {

	public static final Codec<DreadlandsMineStructure> CODEC = simpleCodec(DreadlandsMineStructure::new);

	public DreadlandsMineStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		// rarer close to the origin (1.12.2: rand.nextInt(80) < max(|chunk x|, |chunk z|))
		if(context.random().nextInt(80) >= Math.max(Math.abs(chunk.x), Math.abs(chunk.z)))
			return Optional.empty();
		return Optional.of(new GenerationStub(chunk.getWorldPosition(), builder -> generatePieces(builder, context)));
	}

	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		StructureDreadlandsMinePieces.Room room = new StructureDreadlandsMinePieces.Room(0, context.random(), chunk.getBlockX(2), chunk.getBlockZ(2));
		builder.addPiece(room);
		room.addChildren(room, builder, context.random());
		builder.moveBelowSeaLevel(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinY(), context.random(), 10);
	}

	@Override
	public StructureType<?> type() {
		return ACStructures.DREADLANDS_MINE.get();
	}
}
