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
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.common.blocks.BlockEnergyRelay;
import com.shinoow.abyssalcraft.common.blocks.itemblock.ItemPEContainerBlock;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyCollector;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyContainer;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyPedestal;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySacrificialAltar;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityStatue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Potential Energy blocks
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class EnergyTests {

	/**
	 * GameTest areas are generated underground, so tests that need the open sky are built on the surface above the
	 * test area: a stone floor with the given length along X, cleared above
	 */
	private static BlockPos surface(GameTestHelper helper, int length) {
		ServerLevel level = helper.getLevel();
		BlockPos origin = helper.absolutePos(new BlockPos(1, 0, 3));
		int y = 0;
		for(int x = 0; x < length; x++)
			y = Math.max(y, level.getHeight(Heightmap.Types.WORLD_SURFACE, origin.getX() + x, origin.getZ()));
		BlockPos start = new BlockPos(origin.getX(), y + 1, origin.getZ());
		for(int x = 0; x < length; x++) {
			level.setBlockAndUpdate(start.east(x).below(), Blocks.STONE.defaultBlockState());
			for(int h = 0; h < 3; h++)
				level.setBlockAndUpdate(start.east(x).above(h), Blocks.AIR.defaultBlockState());
		}
		return start;
	}

	private static void clearSurface(GameTestHelper helper, BlockPos start, int length) {
		for(int x = 0; x < length; x++) {
			helper.getLevel().setBlockAndUpdate(start.east(x), Blocks.AIR.defaultBlockState());
			helper.getLevel().setBlockAndUpdate(start.east(x).below(), Blocks.AIR.defaultBlockState());
		}
	}

	/** A statue under the open sky (no other manipulator next to it) sends PE to a pedestal 3 blocks away */
	@GameTest(template = "empty", timeoutTicks = 1000)
	public static void statueChargesPedestal(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos statuePos = surface(helper, 4).east(), pedestalPos = statuePos.east(3);
		level.setBlockAndUpdate(statuePos, ACBlocks.cthulhu_statue.get().defaultBlockState());
		level.setBlockAndUpdate(pedestalPos, ACBlocks.energy_pedestal.get().defaultBlockState());
		TileEntityEnergyPedestal tile = (TileEntityEnergyPedestal) level.getBlockEntity(pedestalPos);
		helper.succeedWhen(() -> {
			helper.assertTrue(tile.getContainedEnergy() > 0, "pedestal didn't receive PE (statue sees sky: " + level.canSeeSky(statuePos.above()) + ")");
			clearSurface(helper, statuePos.west(), 5);
		});
	}

	/** Statues next to each other don't work, even under the open sky */
	@GameTest(template = "empty", timeoutTicks = 500)
	public static void adjacentStatuesDontWork(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos start = surface(helper, 5), statuePos = start.east(), pedestalPos = statuePos.east(3);
		level.setBlockAndUpdate(start, ACBlocks.hastur_statue.get().defaultBlockState());
		level.setBlockAndUpdate(statuePos, ACBlocks.cthulhu_statue.get().defaultBlockState());
		level.setBlockAndUpdate(pedestalPos, ACBlocks.energy_pedestal.get().defaultBlockState());
		TileEntityEnergyPedestal tile = (TileEntityEnergyPedestal) level.getBlockEntity(pedestalPos);
		helper.runAfterDelay(450, () -> {
			helper.assertTrue(tile.getContainedEnergy() == 0, "adjacent statues sent PE");
			clearSurface(helper, start, 5);
			helper.succeed();
		});
	}

	/** Statues without a view of the sky don't send PE */
	@GameTest(template = "empty", timeoutTicks = 500)
	public static void statueNeedsSky(GameTestHelper helper) {
		BlockPos pedestal = new BlockPos(5, 1, 3);
		helper.setBlock(new BlockPos(2, 1, 3), ACBlocks.cthulhu_statue.get());
		helper.setBlock(new BlockPos(2, 3, 3), Blocks.STONE);
		helper.setBlock(pedestal, ACBlocks.energy_pedestal.get());
		TileEntityEnergyPedestal tile = (TileEntityEnergyPedestal) helper.getBlockEntity(pedestal);
		helper.runAfterDelay(450, () -> {
			helper.assertTrue(tile.getContainedEnergy() == 0, "statue without sky sent PE");
			helper.succeed();
		});
	}

	/** A relay pulls PE from the collector behind it and sends it to the container it faces */
	@GameTest(template = "empty", timeoutTicks = 200)
	public static void relayMovesEnergy(GameTestHelper helper) {
		BlockPos collector = new BlockPos(1, 1, 3), relay = new BlockPos(2, 1, 3), container = new BlockPos(5, 1, 3);
		helper.setBlock(collector, ACBlocks.energy_collector.get());
		helper.setBlock(relay, ACBlocks.energy_relay.get().defaultBlockState().setValue(BlockEnergyRelay.FACING, Direction.EAST));
		helper.setBlock(container, ACBlocks.energy_container.get());
		TileEntityEnergyCollector source = (TileEntityEnergyCollector) helper.getBlockEntity(collector);
		source.addEnergy(500);
		TileEntityEnergyContainer target = (TileEntityEnergyContainer) helper.getBlockEntity(container);
		helper.succeedWhen(() -> {
			helper.assertTrue(target.getContainedEnergy() > 0, "container received no PE");
			helper.assertTrue(source.getContainedEnergy() < 500, "collector wasn't drained");
		});
	}

	/** A redstone powered relay does nothing */
	@GameTest(template = "empty", timeoutTicks = 120)
	public static void poweredRelayIsDisabled(GameTestHelper helper) {
		BlockPos collector = new BlockPos(1, 1, 3), relay = new BlockPos(2, 1, 3), container = new BlockPos(5, 1, 3);
		helper.setBlock(collector, ACBlocks.energy_collector.get());
		helper.setBlock(relay, ACBlocks.energy_relay.get().defaultBlockState().setValue(BlockEnergyRelay.FACING, Direction.EAST));
		helper.setBlock(new BlockPos(2, 2, 3), Blocks.REDSTONE_BLOCK);
		helper.setBlock(container, ACBlocks.energy_container.get());
		((TileEntityEnergyCollector) helper.getBlockEntity(collector)).addEnergy(500);
		TileEntityEnergyContainer target = (TileEntityEnergyContainer) helper.getBlockEntity(container);
		helper.runAfterDelay(100, () -> {
			helper.assertTrue(target.getContainedEnergy() == 0, "powered relay transferred PE");
			helper.succeed();
		});
	}

	/** A cow killed next to a sacrificial altar gives PE equal to its max health */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void sacrificeGivesEnergy(GameTestHelper helper) {
		BlockPos altar = new BlockPos(3, 1, 3);
		helper.setBlock(altar, ACBlocks.sacrificial_altar.get());
		Cow cow = helper.spawn(EntityType.COW, new BlockPos(5, 1, 5));
		TileEntitySacrificialAltar tile = (TileEntitySacrificialAltar) helper.getBlockEntity(altar);
		helper.runAfterDelay(5, () -> cow.hurt(helper.getLevel().damageSources().generic(), 100));
		helper.succeedWhen(() -> helper.assertTrue(tile.getContainedEnergy() == cow.getMaxHealth(), "altar has " + tile.getContainedEnergy() + " PE"));
	}

	/** The energy container charges a PE item in its output slot; broken PE blocks keep their PE in the item */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void containerChargesItems(GameTestHelper helper) {
		BlockPos pos = new BlockPos(3, 1, 3);
		helper.setBlock(pos, ACBlocks.energy_container.get());
		TileEntityEnergyContainer tile = (TileEntityEnergyContainer) helper.getBlockEntity(pos);
		tile.addEnergy(1000);
		ItemStack collector = new ItemStack(ACBlocks.energy_collector.get());
		tile.setItem(1, collector);
		helper.succeedWhen(() -> {
			ItemStack stack = tile.getItem(1);
			helper.assertTrue(stack.getItem() instanceof IEnergyContainerItem item && item.getContainedEnergy(stack) >= 100, "item wasn't charged");
			helper.assertTrue(((ItemPEContainerBlock) stack.getItem()).getMaxEnergy(stack) == 1000, "wrong collector item capacity");
		});
	}

	/** Tiered blocks get the 1.12.2 tier capacities */
	@GameTest(template = "empty")
	public static void tieredCapacities(GameTestHelper helper) {
		BlockPos pos = new BlockPos(3, 1, 3);
		helper.setBlock(pos, ACBlocks.omothol_energy_container.get());
		helper.assertTrue(((TileEntityEnergyContainer) helper.getBlockEntity(pos)).getMaxEnergy() == 1200000, "omothol container capacity");
		helper.setBlock(pos, ACBlocks.dreadlands_energy_pedestal.get());
		helper.assertTrue(((TileEntityEnergyPedestal) helper.getBlockEntity(pos)).getMaxEnergy() == 12500, "dreadlands pedestal capacity");
		helper.succeed();
	}
}
