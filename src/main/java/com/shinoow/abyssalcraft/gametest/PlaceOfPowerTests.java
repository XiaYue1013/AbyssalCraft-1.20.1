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
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureBase;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityStatue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Places of Power (multiblock structures that amplify statues)
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class PlaceOfPowerTests {

	private static void form(GameTestHelper helper, BlockPos pos) {
		Player player = helper.makeMockPlayer();
		player.setShiftKeyDown(true);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ACItems.necronomicon.get()));
		BlockPos abs = helper.absolutePos(pos);
		player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
	}

	private static void assertFormed(GameTestHelper helper, BlockPos base, String identifier, float range, BlockPos... statues) {
		helper.assertBlockPresent(ACBlocks.multi_block.get(), base);
		IStructureBase tile = (IStructureBase) helper.getBlockEntity(base);
		helper.assertTrue(tile.getMultiblock() != null && identifier.equals(tile.getMultiblock().getIdentifier()), "wrong structure: " + tile.getMultiblock());
		for(BlockPos pos : statues) {
			TileEntityStatue statue = (TileEntityStatue) helper.getBlockEntity(pos);
			helper.assertTrue(statue.isInMultiblock() && helper.absolutePos(base).equals(statue.getBasePosition()), "statue at " + pos + " not part of the structure");
			helper.assertTrue(PEUtils.getStructureAmplifier(helper.getLevel(), statue, AmplifierType.RANGE) == range, "statue at " + pos + " has no range boost");
		}
	}

	/** A monolith stone with three statues on top forms a Totem Pole; removing a statue breaks it on the next validation */
	@GameTest(template = "empty", timeoutTicks = 250)
	public static void totemPole(GameTestHelper helper) {
		BlockPos base = new BlockPos(4, 1, 4);
		helper.setBlock(base, ACBlocks.monolith_stone.get());
		helper.setBlock(base.above(), ACBlocks.yog_sothoth_statue.get());
		helper.setBlock(base.above(2), ACBlocks.azathoth_statue.get());
		helper.setBlock(base.above(3), ACBlocks.nyarlathotep_statue.get());
		form(helper, base);
		assertFormed(helper, base, "totempole", 3, base.above(), base.above(2), base.above(3));

		helper.setBlock(base.above(3), Blocks.AIR);
		helper.succeedWhen(() -> {
			TileEntityStatue statue = (TileEntityStatue) helper.getBlockEntity(base.above());
			helper.assertFalse(statue.isInMultiblock(), "statue still part of a broken structure");
			helper.assertTrue(PEUtils.getStructureAmplifier(helper.getLevel(), statue, AmplifierType.RANGE) == 0, "broken structure still boosts");
		});
	}

	/** The 3x3 shrine: monolith stone base, pillars in the corners, four statues around the top */
	@GameTest(template = "empty")
	public static void basicShrine(GameTestHelper helper) {
		BlockPos top = new BlockPos(4, 3, 4);
		for(int x = -1; x < 2; x++)
			for(int z = -1; z < 2; z++) {
				helper.setBlock(top.offset(x, -2, z), ACBlocks.monolith_stone.get());
				helper.setBlock(top.offset(x, -1, z), x != 0 && z != 0 ? ACBlocks.monolith_pillar.get() : ACBlocks.monolith_stone.get());
			}
		helper.setBlock(top, ACBlocks.monolith_stone.get());
		Block[] statues = {ACBlocks.cthulhu_statue.get(), ACBlocks.hastur_statue.get(), ACBlocks.jzahar_statue.get(), ACBlocks.shub_niggurath_statue.get()};
		BlockPos[] around = {top.north(), top.south(), top.east(), top.west()};
		for(int i = 0; i < 4; i++)
			helper.setBlock(around[i], statues[i]);

		// a missing pillar prevents forming
		helper.setBlock(top.offset(1, -1, 1), ACBlocks.monolith_stone.get());
		form(helper, top);
		helper.assertBlockPresent(ACBlocks.monolith_stone.get(), top);

		helper.setBlock(top.offset(1, -1, 1), ACBlocks.monolith_pillar.get());
		form(helper, top);
		assertFormed(helper, top, "basic", 2, around);
		helper.succeed();
	}

	/** Any walls, stairs and slabs form an archway over a statue standing on monolith stone */
	@GameTest(template = "empty")
	public static void archway(GameTestHelper helper) {
		BlockPos base = new BlockPos(4, 1, 4);
		helper.setBlock(base, ACBlocks.monolith_stone.get());
		helper.setBlock(base.above(), ACBlocks.cthulhu_statue.get());
		for(int y = 0; y < 3; y++) {
			helper.setBlock(base.west(2).above(y), Blocks.COBBLESTONE_WALL);
			helper.setBlock(base.east(2).above(y), Blocks.MOSSY_COBBLESTONE_WALL);
		}
		form(helper, base);
		helper.assertBlockPresent(ACBlocks.monolith_stone.get(), base); // no arch on top yet

		helper.setBlock(base.above(3).west(2), Blocks.STONE_BRICK_STAIRS);
		helper.setBlock(base.above(3).west(1), Blocks.STONE_BRICK_STAIRS);
		helper.setBlock(base.above(3), Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
		helper.setBlock(base.above(3).east(1), Blocks.STONE_BRICK_STAIRS);
		helper.setBlock(base.above(3).east(2), Blocks.STONE_BRICK_STAIRS);
		for(int x = -2; x < 3; x++)
			helper.setBlock(base.above(4).east(x), Blocks.STONE_BRICK_SLAB);
		form(helper, base);
		assertFormed(helper, base, "archway", 1, base.above());
		helper.succeed();
	}
}
