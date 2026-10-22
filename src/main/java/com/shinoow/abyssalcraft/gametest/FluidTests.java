/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - automated verification (./gradlew runGameTestServer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for Liquid Coralium and Liquid Antimatter
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class FluidTests {

	/** Liquid Antimatter freezes adjacent water into Packed Ice and turns lava into Obsidian */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void antimatterFreezesWaterAndLava(GameTestHelper helper) {
		BlockPos water = new BlockPos(0, 1, 1), anti = new BlockPos(1, 1, 1), lava = new BlockPos(2, 1, 1);
		for(int x = 0; x < 3; x++)
			helper.setBlock(new BlockPos(x, 0, 1), Blocks.STONE);
		helper.setBlock(water, Blocks.WATER);
		helper.setBlock(lava, Blocks.LAVA);
		helper.setBlock(anti, ACBlocks.liquid_antimatter.get());
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(Blocks.PACKED_ICE, water);
			helper.assertBlockPresent(Blocks.OBSIDIAN, lava);
		});
	}

	/** Liquid Coralium transmutes adjacent stone into Abyssal Stone and gives mobs the Coralium Plague */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void coraliumTransmutesAndInfects(GameTestHelper helper) {
		BlockPos liquid = new BlockPos(1, 1, 1), stone = new BlockPos(1, 0, 1);
		// Liquid Coralium turns into cobblestone in oceans (unless enabled), and the test world's seed changes every run
		// (biome lookups are blurred across neighbouring 4x4 cells, so the whole area is changed)
		BlockPos center = helper.absolutePos(liquid);
		com.shinoow.abyssalcraft.common.util.BiomeUtil.updateBiomes(helper.getLevel(), BlockPos.betweenClosedStream(center.offset(-12, 0, -12), center.offset(12, 0, 12))
				.map(BlockPos::immutable).toList(), net.minecraft.world.level.biome.Biomes.PLAINS);
		helper.setBlock(stone, Blocks.STONE);
		helper.setBlock(liquid, ACBlocks.liquid_coralium.get());
		Pig pig = helper.spawn(EntityType.PIG, liquid);
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(ACBlocks.abyssal_stone.get(), stone);
			helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.coralium_plague.get()), "pig not infected");
		});
	}
}
