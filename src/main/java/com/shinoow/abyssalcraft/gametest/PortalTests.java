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

import java.util.Optional;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;
import com.shinoow.abyssalcraft.common.world.TeleporterAC;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the gateways (frame detection, lighting, linking). Travel between dimensions is tested
 * once the dimensions are ported.
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class PortalTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	private static InteractionResult useKey(GameTestHelper helper, ItemStack key, BlockPos clicked) {
		Player player = helper.makeMockPlayer();
		player.setYRot(0); // facing south: frame along the X axis
		player.setItemInHand(InteractionHand.MAIN_HAND, key);
		BlockPos abs = helper.absolutePos(clicked);
		return key.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
	}

	/** A Gateway Key used in the overworld builds an Abyssal Stone frame and the Coralium Fire lights it */
	@GameTest(template = "empty")
	public static void gatewayKeyBuildsLitGateway(GameTestHelper helper) {
		floor(helper);
		InteractionResult result = useKey(helper, new ItemStack(ACItems.gateway_key.get()), new BlockPos(2, 0, 3));
		helper.assertTrue(result.consumesAction(), "gateway key didn't build anything: " + result);
		for(int x = 1; x <= 4; x++) {
			helper.assertBlockPresent(ACBlocks.abyssal_stone.get(), new BlockPos(x, 1, 3));
			helper.assertBlockPresent(ACBlocks.abyssal_stone.get(), new BlockPos(x, 5, 3));
		}
		for(int x = 2; x <= 3; x++)
			for(int y = 2; y <= 4; y++) {
				BlockPos pos = new BlockPos(x, y, 3);
				helper.assertBlockPresent(ACBlocks.abyssal_gateway.get(), pos);
				helper.assertBlockProperty(pos, BlockACPortal.AXIS, Direction.Axis.X);
			}
		helper.succeed();
	}

	/** The Dreaded Gateway Key can't be used where it doesn't belong to, the R'lyehian one builds the Abyssal gateway in the overworld */
	@GameTest(template = "empty")
	public static void gatewayKeyDimensionRules(GameTestHelper helper) {
		floor(helper);
		InteractionResult result = useKey(helper, new ItemStack(ACItems.rlyehian_gateway_key.get()), new BlockPos(2, 0, 3));
		helper.assertTrue(result.consumesAction(), "r'lyehian gateway key didn't build anything in the overworld");
		helper.assertBlockPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(2, 2, 3));
		helper.succeed();
	}

	/** Breaking the frame breaks the whole gateway; the key refuses to build into obstructions */
	@GameTest(template = "empty")
	public static void brokenFrameBreaksGateway(GameTestHelper helper) {
		floor(helper);
		useKey(helper, new ItemStack(ACItems.gateway_key.get()), new BlockPos(2, 0, 3));
		helper.assertBlockPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(2, 3, 3));
		helper.setBlock(new BlockPos(1, 3, 3), Blocks.AIR);
		helper.assertBlockNotPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(2, 3, 3));
		helper.assertBlockNotPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(3, 2, 3));

		helper.setBlock(new BlockPos(6, 3, 6), Blocks.STONE);
		InteractionResult blocked = useKey(helper, new ItemStack(ACItems.gateway_key.get()), new BlockPos(5, 0, 6));
		helper.assertFalse(blocked.consumesAction(), "gateway key built into an obstruction");
		helper.assertBlockNotPresent(ACBlocks.abyssal_stone.get(), new BlockPos(5, 1, 6));
		helper.succeed();
	}

	/** A hand built frame of the wrong stone isn't lit by the fire */
	@GameTest(template = "empty")
	public static void wrongFrameStaysUnlit(GameTestHelper helper) {
		floor(helper);
		BlockState dreadstone = ACBlocks.dreadstone.get().defaultBlockState();
		for(int x = 1; x <= 4; x++) {
			helper.setBlock(new BlockPos(x, 1, 3), dreadstone);
			helper.setBlock(new BlockPos(x, 5, 3), dreadstone);
		}
		for(int y = 2; y <= 4; y++) {
			helper.setBlock(new BlockPos(1, y, 3), dreadstone);
			helper.setBlock(new BlockPos(4, y, 3), dreadstone);
		}
		helper.setBlock(new BlockPos(2, 2, 3), ACBlocks.coralium_fire.get());
		helper.assertBlockNotPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(2, 3, 3));
		helper.setBlock(new BlockPos(2, 2, 3), ACBlocks.dreaded_fire.get());
		helper.assertBlockPresent(ACBlocks.dreaded_gateway.get(), new BlockPos(3, 4, 3));
		helper.succeed();
	}

	/** A gateway created by the teleporter is found again through its point of interest (gateway linking) */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void createdGatewayIsFound(GameTestHelper helper) {
		floor(helper);
		BlockACPortal gateway = (BlockACPortal) ACBlocks.omothol_gateway.get();
		BlockPos target = helper.absolutePos(new BlockPos(3, 1, 3));
		TeleporterAC teleporter = new TeleporterAC(helper.getLevel(), gateway, target);
		Optional<BlockUtil.FoundRectangle> created = teleporter.createPortal(target, Direction.Axis.X);
		helper.assertTrue(created.isPresent(), "no gateway created");
		BlockPos corner = created.get().minCorner;
		helper.assertTrue(helper.getLevel().getBlockState(corner).is(gateway), "created gateway has no portal blocks");
		helper.assertTrue(helper.getLevel().getBlockState(corner.below()).is(ACBlocks.omothol_stone.get()), "created gateway has no frame");

		helper.runAfterDelay(2, () -> {
			Optional<BlockUtil.FoundRectangle> found = teleporter.findPortalAround(target, helper.getLevel().getWorldBorder());
			helper.assertTrue(found.isPresent(), "created gateway not found");
			helper.assertTrue(found.get().minCorner.equals(corner) && found.get().axis1Size == 2 && found.get().axis2Size == 3,
					"found " + found.get().minCorner + " " + found.get().axis1Size + "x" + found.get().axis2Size + ", expected " + corner + " 2x3");
			// clean up, the gateway can be outside of the test area
			for(int i = -1; i < 3; i++)
				for(int j = -1; j < 4; j++)
					helper.getLevel().setBlock(corner.offset(i, j, 0), Blocks.AIR.defaultBlockState(), 2);
			helper.succeed();
		});
	}
}
