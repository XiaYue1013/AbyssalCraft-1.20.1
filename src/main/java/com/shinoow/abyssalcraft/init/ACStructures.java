/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - structure types, piece types and placements (the structures themselves are datapack entries)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.world.structure.ACTemplatePiece;
import com.shinoow.abyssalcraft.common.world.structure.AbyssalStrongholdStructure;
import com.shinoow.abyssalcraft.common.world.structure.DreadlandsMineStructure;
import com.shinoow.abyssalcraft.common.world.structure.FixedStructurePlacement;
import com.shinoow.abyssalcraft.common.world.structure.JzaharTempleStructure;
import com.shinoow.abyssalcraft.common.world.structure.OmotholStructure;
import com.shinoow.abyssalcraft.common.world.structure.ShoggothLairStructure;
import com.shinoow.abyssalcraft.common.world.structure.StructureAbyStrongholdPieces;
import com.shinoow.abyssalcraft.common.world.structure.StructureDreadlandsMinePieces;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACStructures {

	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, AbyssalCraft.modid);
	public static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, AbyssalCraft.modid);
	public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, AbyssalCraft.modid);

	public static final RegistryObject<StructureType<ShoggothLairStructure>> SHOGGOTH_LAIR = STRUCTURE_TYPES.register("shoggoth_lair", () -> () -> ShoggothLairStructure.CODEC);
	public static final RegistryObject<StructureType<OmotholStructure>> OMOTHOL = STRUCTURE_TYPES.register("omothol", () -> () -> OmotholStructure.CODEC);
	public static final RegistryObject<StructureType<AbyssalStrongholdStructure>> ABYSSAL_STRONGHOLD = STRUCTURE_TYPES.register("abyssal_stronghold", () -> () -> AbyssalStrongholdStructure.CODEC);
	public static final RegistryObject<StructureType<DreadlandsMineStructure>> DREADLANDS_MINE = STRUCTURE_TYPES.register("dreadlands_mine", () -> () -> DreadlandsMineStructure.CODEC);
	public static final RegistryObject<StructureType<JzaharTempleStructure>> JZAHAR_TEMPLE = STRUCTURE_TYPES.register("jzahar_temple", () -> () -> JzaharTempleStructure.CODEC);

	public static final RegistryObject<StructurePieceType> TEMPLATE_PIECE = PIECE_TYPES.register("template",
			() -> (StructurePieceType.StructureTemplateType) ACTemplatePiece::new);

	public static final RegistryObject<StructurePieceType> ABYSSAL_STRONGHOLD_PIECE = PIECE_TYPES.register("abyssal_stronghold",
			() -> (StructurePieceType.ContextlessType) StructureAbyStrongholdPieces::load);

	public static final RegistryObject<StructurePieceType> DREADLANDS_MINE_PIECE = PIECE_TYPES.register("dreadlands_mine",
			() -> (StructurePieceType.ContextlessType) StructureDreadlandsMinePieces::load);

	public static final RegistryObject<StructurePlacementType<FixedStructurePlacement>> FIXED_PLACEMENT = PLACEMENT_TYPES.register("fixed", () -> () -> FixedStructurePlacement.CODEC);
}
