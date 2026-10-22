/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (ChunkProviderAbyss#populate, DarklandsStructureGenerator)
 *     1.20.1 port - places the block-by-block structures as features
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.gen.feature;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.world.gen.structure.*;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class LegacyStructureFeature extends Feature<LegacyStructureFeature.Type> {

	public enum Type implements StringRepresentable, FeatureConfiguration {
		/** Coralium cobblestone pillars reaching up to the top of the Abyssal Wasteland */
		CHAINS,
		/** Abyssal stone brick ruins with a dungeon chest and a stronghold chest in the basement */
		ABYSSAL_RUIN,
		/** DarklandsStructureGenerator types 0 - 4 */
		DARKLANDS_ANY, DARKLANDS_SHRINE, DARKLANDS_RITUAL_GROUNDS, DARKLANDS_HOUSE, DARKLANDS_MISC;

		public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

	public static final Codec<Type> CONFIG_CODEC = Type.CODEC.fieldOf("structure").codec();

	private static final List<Supplier<LegacyStructure>> SHRINES = List.of(StructureCircularShrine::new, StructureCircularShrineColumns::new,
			StructureElevatedShrine::new, StructureElevatedShrineLarge::new);
	private static final List<Supplier<LegacyStructure>> RITUAL_GROUNDS = List.of(StructureRitualGrounds::new, StructureRitualGroundsColumns::new);
	private static final List<Supplier<LegacyStructure>> HOUSES = List.of(AChouse1::new, AChouse2::new);
	private static final List<Supplier<LegacyStructure>> MISC = List.of(ACscion1::new, ACscion2::new);
	private static final List<Supplier<LegacyStructure>> ALL = List.of(AChouse1::new, AChouse2::new, ACscion1::new, ACscion2::new,
			StructureRitualGrounds::new, StructureCircularShrine::new, StructureCircularShrineColumns::new, StructureRitualGroundsColumns::new,
			StructureElevatedShrine::new, StructureElevatedShrineLarge::new);

	public LegacyStructureFeature() {
		super(CONFIG_CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<Type> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();
		BlockPos pos = context.origin();
		return switch(context.config()) {
		case CHAINS -> new Chains().generate(level, random, pos);
		case ABYSSAL_RUIN -> new Abyruin().generate(level, random, pos);
		case DARKLANDS_ANY -> darklands(ALL, level, random, pos, 3, Blocks.GRASS_BLOCK);
		case DARKLANDS_SHRINE -> darklands(SHRINES, level, random, pos, 3, Blocks.GRASS_BLOCK);
		case DARKLANDS_RITUAL_GROUNDS -> darklands(RITUAL_GROUNDS, level, random, pos, 3, Blocks.GRASS_BLOCK);
		case DARKLANDS_HOUSE -> darklands(HOUSES, level, random, pos, 5, Blocks.GRASS_BLOCK);
		case DARKLANDS_MISC -> darklands(MISC, level, random, pos, 3, Blocks.GRASS_BLOCK, ACBlocks.darkstone.get());
		};
	}

	/** DarklandsStructureGenerator#generateRandomStructure: needs flat ground of the given blocks, then a 3 % chance */
	private static boolean darklands(List<Supplier<LegacyStructure>> structures, WorldGenLevel level, RandomSource random, BlockPos pos, int bounds, Block... ground) {
		Supplier<LegacyStructure> structure = structures.get(random.nextInt(structures.size()));
		while(level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight() + 2)
			pos = pos.below();
		for(Block block : ground)
			if(isFlat(level, pos, bounds, block)) {
				if(random.nextFloat() < 0.03F)
					return structure.get().generate(level, random, pos);
				return false;
			}
		return false;
	}

	private static boolean isFlat(WorldGenLevel level, BlockPos pos, int bounds, Block block) {
		return level.getBlockState(pos).is(block) && level.getBlockState(pos.north(bounds)).is(block) && level.getBlockState(pos.south(bounds)).is(block)
				&& level.getBlockState(pos.west(bounds)).is(block) && level.getBlockState(pos.east(bounds)).is(block);
	}
}
