/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (StructureJzaharTemple, placed by MapGenOmothol / StructureOmotholPieces.Well)
 *     1.20.1 port - a structure at the Omothol origin (see FixedStructurePlacement). The rest of 1.12.2's
 *     StructureOmotholPieces (a village generator) was never called in 1.12.2 and isn't ported.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.init.ACStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * The Temple of J'zahar: seven templates around (4, 54, 7), lowered onto the first ground below y 54
 */
public class JzaharTempleStructure extends Structure {

	public static final Codec<JzaharTempleStructure> CODEC = simpleCodec(JzaharTempleStructure::new);

	public JzaharTempleStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		NoiseColumn column = context.chunkGenerator().getBaseColumn(4, 7, context.heightAccessor(), context.randomState());
		int y = 54;
		while(y > 2 && column.getBlock(y).isAir())
			y--;
		BlockPos pos = new BlockPos(4, y, 7);
		return Optional.of(new GenerationStub(pos, builder -> pieces(context.structureTemplateManager(), pos).forEach(builder::addPiece)));
	}

	/** The temple's templates for a temple whose entrance is at pos (also used to rebuild it) */
	public static List<ACTemplatePiece> pieces(StructureTemplateManager manager, BlockPos pos) {
		BlockPos front = pos.offset(-4, -2, -1);
		BlockPos middle = front.south(32);
		BlockPos back = middle.south(32).west(9);
		return List.of(
				piece(manager, "temple/jzahartemple_front_right", front.west(32)),
				piece(manager, "temple/jzahartemple_front_middle", front),
				piece(manager, "temple/jzahartemple_front_left", front.east(32)),
				piece(manager, "temple/jzahartemple_middle_right", middle.west(32)),
				piece(manager, "temple/jzahartemple_middle_middle", middle),
				piece(manager, "temple/jzahartemple_middle_left", middle.east(32)),
				piece(manager, "temple/jzahartemple_back", back));
	}

	private static ACTemplatePiece piece(StructureTemplateManager manager, String template, BlockPos pos) {
		return new ACTemplatePiece(manager, template, pos, Rotation.NONE, StructureMarkers.Kind.NONE, "");
	}

	@Override
	public StructureType<?> type() {
		return ACStructures.JZAHAR_TEMPLE.get();
	}
}
