/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (StructureShoggothPit)
 *     1.20.1 port - a structure (the lairs reach 27 blocks out); the minimum distance between lairs is the
 *     structure set's spacing
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.init.ACStructures;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * A Shoggoth Lair, sunk 9 blocks into the ground. The lair reaches 19 - 27 blocks north of its entrance, so there
 * has to be ground there.
 */
public class ShoggothLairStructure extends Structure {

	public static final Codec<ShoggothLairStructure> CODEC = RecordCodecBuilder.create(i -> i.group(settingsCodec(i),
			Codec.BOOL.optionalFieldOf("ignore_config", false).forGetter(s -> s.ignoreConfig)).apply(i, ShoggothLairStructure::new));

	private static final BlockPos[] OFFSETS = {new BlockPos(-6, -9, -27), new BlockPos(-6, -9, -21), new BlockPos(-6, -9, -19)};

	/** The "shoggothLairs" option doesn't apply to Omothol (1.12.2 behaviour) */
	private final boolean ignoreConfig;

	public ShoggothLairStructure(StructureSettings settings, boolean ignoreConfig) {
		super(settings);
		this.ignoreConfig = ignoreConfig;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		if(!ignoreConfig && !ACConfig.generateShoggothLairs)
			return Optional.empty();
		int x = context.chunkPos().getMinBlockX() + context.random().nextInt(16);
		int z = context.chunkPos().getMinBlockZ() + context.random().nextInt(16);
		int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
		if(y <= context.heightAccessor().getMinBuildHeight() + 1)
			return Optional.empty();
		for(int distance : new int[] {13, 20, 27})
			if(!StructureUtil.hasGround(context, x, z - distance, y))
				return Optional.empty();
		int num = context.random().nextInt(3) + 1;
		BlockPos pos = new BlockPos(x, y, z);
		return Optional.of(new GenerationStub(pos, builder -> builder.addPiece(new ACTemplatePiece(context.structureTemplateManager(),
				"shoggothlair/shoggothlair_" + num, pos.offset(OFFSETS[num - 1]), Rotation.NONE, StructureMarkers.Kind.SHOGGOTH_LAIR, String.valueOf(num)))));
	}

	@Override
	public StructureType<?> type() {
		return ACStructures.SHOGGOTH_LAIR.get();
	}
}
