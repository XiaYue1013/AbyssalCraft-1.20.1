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
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.transfer.caps.ItemTransferCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Item Transportation System
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class TransportTests {

	private static void use(GameTestHelper helper, Player player, BlockPos pos, Direction face) {
		BlockPos abs = helper.absolutePos(pos);
		player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), face, abs, false)));
	}

	/** Record a path to chest B, apply it to chest A, switch the Spirit Altar on: chest A's items travel into chest B */
	@GameTest(template = "empty", timeoutTicks = 400)
	public static void spiritAltarMovesItems(GameTestHelper helper) {
		BlockPos chestA = new BlockPos(1, 1, 1), chestB = new BlockPos(6, 1, 6), altar = new BlockPos(4, 1, 4);
		helper.setBlock(chestA, Blocks.CHEST);
		helper.setBlock(chestB, Blocks.CHEST);
		helper.setBlock(altar, ACBlocks.spirit_altar.get());
		((ChestBlockEntity) helper.getBlockEntity(chestA)).setItem(0, new ItemStack(Items.COBBLESTONE, 3));
		helper.assertTrue(ItemTransferCapability.getCap(helper.getBlockEntity(chestA)) != null, "chest has no item transfer capability");

		Player player = helper.makeMockPlayer();
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ACItems.configurator.get()));
		use(helper, player, chestB, Direction.UP); // mode 0: path ends in chest B
		player.getMainHandItem().getOrCreateTag().putInt("Mode", 1);
		use(helper, player, chestA, Direction.UP); // mode 1: apply to chest A
		helper.assertTrue(ItemTransferCapability.getCap(helper.getBlockEntity(chestA)).getTransferConfigurations().size() == 1, "configuration not applied");

		player.getMainHandItem().getOrCreateTag().putInt("Mode", 0);
		use(helper, player, altar, Direction.UP); // find inventories
		player.getMainHandItem().getOrCreateTag().putInt("Mode", 1);
		use(helper, player, altar, Direction.UP); // switch on
		helper.succeedWhen(() -> {
			ItemStack arrived = ((ChestBlockEntity) helper.getBlockEntity(chestB)).getItem(0);
			helper.assertTrue(arrived.is(Items.COBBLESTONE) && arrived.getCount() == 3, "items didn't arrive: " + arrived + " (chest A: " + ((ChestBlockEntity) helper.getBlockEntity(chestA)).getItem(0)
					+ ", running " + ItemTransferCapability.getCap(helper.getBlockEntity(chestA)).isRunning() + ", spirits "
					+ helper.getLevel().getEntitiesOfClass(com.shinoow.abyssalcraft.common.entity.EntitySpiritItem.class, new net.minecraft.world.phys.AABB(helper.absolutePos(altar)).inflate(10)).stream().map(e -> e.blockPosition().subtract(helper.absolutePos(BlockPos.ZERO)).toShortString()).toList() + ")");
			helper.assertTrue(((ChestBlockEntity) helper.getBlockEntity(chestA)).isEmpty(), "chest A not emptied");
		});
	}
}
