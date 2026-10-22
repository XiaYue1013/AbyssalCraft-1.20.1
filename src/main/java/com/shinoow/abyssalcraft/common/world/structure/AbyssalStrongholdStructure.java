/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (MapGenAbyStronghold)
 *     1.20.1 port - the rings placement is the structure set's concentric_rings placement (distance 32, spread 3,
 *     count 128, preferring the Abyssal Wasteland)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.init.ACStructures;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class AbyssalStrongholdStructure extends Structure {

	public static final Codec<AbyssalStrongholdStructure> CODEC = simpleCodec(AbyssalStrongholdStructure::new);

	public AbyssalStrongholdStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), builder -> generatePieces(builder, context)));
	}

	/** MapGenAbyStronghold.Start: built again until it has a portal room, then moved down below sea level */
	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		// the piece selection keeps its state in static fields (like 1.12.2 did), and starts are generated on several threads
		synchronized(StructureAbyStrongholdPieces.class) {
			StructureAbyStrongholdPieces.Stairs2 start;
			int attempts = 0;
			do {
				builder.clear();
				StructureAbyStrongholdPieces.prepareStructurePieces();
				start = new StructureAbyStrongholdPieces.Stairs2(0, context.random(), chunk.getBlockX(2), chunk.getBlockZ(2));
				builder.addPiece(start);
				start.addChildren(start, builder, context.random());
				List<StructureAbyStrongholdPieces.Stronghold> pending = start.field_75026_c;
				while(!pending.isEmpty()) {
					StructurePiece piece = pending.remove(context.random().nextInt(pending.size()));
					piece.addChildren(start, builder, context.random());
				}
				builder.moveBelowSeaLevel(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinY(), context.random(), 10);
			} while((builder.isEmpty() || start.strongholdPortalRoom == null) && ++attempts < 50);
		}
	}

	@Override
	public StructureType<?> type() {
		return ACStructures.ABYSSAL_STRONGHOLD.get();
	}
}
