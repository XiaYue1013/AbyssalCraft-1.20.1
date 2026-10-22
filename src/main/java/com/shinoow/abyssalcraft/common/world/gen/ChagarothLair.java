/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (chagarothlair)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen;

import java.util.List;

import com.shinoow.abyssalcraft.common.world.structure.ACTemplatePiece;
import com.shinoow.abyssalcraft.common.world.structure.StructureMarkers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * The lair of Cha'garoth, built when the Altar of Cha'garoth is used: five templates north of the altar, going down into
 * the mountain. The middle one has the boss spawner and the "Legendary Treasure of The Dreadlands" chest.
 */
public class ChagarothLair {

	/** The lair's templates for an altar top at pos (1.12.2 offsets) */
	public static List<ACTemplatePiece> pieces(StructureTemplateManager manager, BlockPos pos) {
		return List.of(
				piece(manager, "chagarothlair/chagarothlair_front", pos.offset(-8, -2, -22)),
				piece(manager, "chagarothlair/chagarothlair_middle", pos.offset(-8, -17, -54)),
				piece(manager, "chagarothlair/chagarothlair_middle_left", pos.offset(-24, 1, -41)),
				piece(manager, "chagarothlair/chagarothlair_middle_right", pos.offset(9, 1, -41)),
				piece(manager, "chagarothlair/chagarothlair_back", pos.offset(-9, -18, -84)));
	}

	private static ACTemplatePiece piece(StructureTemplateManager manager, String template, BlockPos pos) {
		return new ACTemplatePiece(manager, template, pos, Rotation.NONE, StructureMarkers.Kind.CHAGAROTH_LAIR, "");
	}

	/** Builds the lair (server side) */
	public static void generate(ServerLevel level, BlockPos pos) {
		for(ACTemplatePiece piece : pieces(level.getStructureManager(), pos))
			piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(), level.random, piece.getBoundingBox(),
					new ChunkPos(pos), pos);
	}
}
