/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - terrain checks for structure starts. 1.12.2 generated its structures while decorating and looked
 *     at the generated blocks; 1.20.1 structures are placed before that, from the generator's noise heights.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

final class StructureUtil {

	private StructureUtil() {}

	/** y of the first air block above the surface at x, z (the minimum build height if there's nothing) */
	static int surface(GenerationContext context, int x, int z) {
		return context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
	}

	/** Whether the column at x, z has a block at height y or above */
	static boolean hasGround(GenerationContext context, int x, int z, int y) {
		return surface(context, x, z) > y;
	}

	static Optional<StructureTemplate> template(GenerationContext context, ResourceLocation location) {
		return context.structureTemplateManager().get(location);
	}

	/** Names of the data markers in a template (same order every time for the same template) */
	static List<String> dataMarkers(StructureTemplate template, Rotation rotation) {
		return template.filterBlocks(BlockPos.ZERO, ACTemplatePiece.settings(rotation), Blocks.STRUCTURE_BLOCK).stream()
				.filter(info -> info.nbt() != null && "DATA".equals(info.nbt().getString("mode")))
				.map(info -> info.nbt().getString("metadata")).toList();
	}
}
