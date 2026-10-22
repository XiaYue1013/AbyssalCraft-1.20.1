/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - one structure template of an AbyssalCraft structure (1.12.2 placed them directly with
 *     Template#addBlocksToWorld); its data markers are handled by {@link StructureMarkers}
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.init.ACStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ACTemplatePiece extends TemplateStructurePiece {

	private final StructureMarkers.Kind kind;
	/** Extra marker data, e.g. the Omothol building number or the treasure crate */
	private final String data;

	public ACTemplatePiece(StructureTemplateManager manager, String template, BlockPos pos, Rotation rotation, StructureMarkers.Kind kind, String data) {
		super(ACStructures.TEMPLATE_PIECE.get(), 0, manager, AbyssalCraft.loc(template), AbyssalCraft.loc(template).toString(), settings(rotation, kind), pos);
		this.kind = kind;
		this.data = data;
	}

	public ACTemplatePiece(StructureTemplateManager manager, CompoundTag tag) {
		super(ACStructures.TEMPLATE_PIECE.get(), tag, manager, location -> settings(Rotation.valueOf(tag.getString("Rot")),
				StructureMarkers.Kind.valueOf(tag.getString("Kind"))));
		kind = StructureMarkers.Kind.valueOf(tag.getString("Kind"));
		data = tag.getString("Data");
	}

	/** 1.12.2 PlacementSettings defaults: structure blocks (data markers) aren't placed, rotation around the origin */
	public static StructurePlaceSettings settings(Rotation rotation) {
		return new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
	}

	private static StructurePlaceSettings settings(Rotation rotation, StructureMarkers.Kind kind) {
		StructurePlaceSettings settings = settings(rotation);
		if(kind == StructureMarkers.Kind.CHAGAROTH_LAIR)
			// 1.12.2 chagarothlair: structure voids keep what's there, 40 % of the dreadstone bricks are cracked
			settings.addProcessor(new BlockIgnoreProcessor(List.of(Blocks.STRUCTURE_VOID))).addProcessor(new RuleProcessor(List.of(
					new ProcessorRule(new RandomBlockMatchTest(ACBlocks.dreadstone_bricks.get(), 0.4F), AlwaysTrueTest.INSTANCE,
							ACBlocks.cracked_dreadstone_bricks.get().defaultBlockState()))));
		return settings;
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		super.addAdditionalSaveData(context, tag);
		tag.putString("Rot", placeSettings.getRotation().name());
		tag.putString("Kind", kind.name());
		tag.putString("Data", data);
	}

	@Override
	protected void handleDataMarker(String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
		StructureMarkers.handle(kind, data, placeSettings.getRotation(), marker, pos, level, random);
	}
}
