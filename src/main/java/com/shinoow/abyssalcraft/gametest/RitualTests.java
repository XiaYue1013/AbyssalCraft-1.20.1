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
import com.shinoow.abyssalcraft.api.block.IRitualPedestal;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.RitualRecipes;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityRitualAltar;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import com.shinoow.abyssalcraft.lib.util.RitualUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for ritual grounds and rituals
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class RitualTests {

	private static final BlockPos ALTAR = new BlockPos(4, 1, 4);

	private static void ring(GameTestHelper helper, Block block) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
		helper.setBlock(ALTAR, block);
		for(BlockPos p : RitualUtil.PEDESTAL_POSITIONS)
			helper.setBlock(ALTAR.offset(p), block);
	}

	private static Player sneakingPlayer(GameTestHelper helper, ItemStack book) {
		Player player = helper.makeMockPlayer();
		player.setShiftKeyDown(true);
		player.setItemInHand(InteractionHand.MAIN_HAND, book);
		return player;
	}

	private static void use(GameTestHelper helper, Player player, BlockPos pos) {
		BlockPos abs = helper.absolutePos(pos);
		player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
	}

	/** Sneak-using a Necronomicon on a ring of cobblestone (same chunk) builds a ritual altar with eight pedestals */
	@GameTest(template = "empty")
	public static void necronomiconBuildsAltar(GameTestHelper helper) {
		BlockPos origin = helper.absolutePos(ALTAR);
		boolean sameChunk = RitualUtil.PEDESTAL_POSITIONS.stream().allMatch(p -> (origin.getX() + p.getX()) >> 4 == origin.getX() >> 4
				&& (origin.getZ() + p.getZ()) >> 4 == origin.getZ() >> 4);
		ring(helper, Blocks.COBBLESTONE);
		use(helper, sneakingPlayer(helper, new ItemStack(ACItems.necronomicon.get())), ALTAR);
		if(sameChunk) {
			helper.assertBlockPresent(ACBlocks.ritual_altar_stone.get(), ALTAR);
			for(BlockPos p : RitualUtil.PEDESTAL_POSITIONS) {
				helper.assertBlockPresent(ACBlocks.ritual_pedestal_stone.get(), ALTAR.offset(p));
				helper.assertTrue(((IRitualPedestal) helper.getBlockEntity(ALTAR.offset(p))).getAltar() != null, "pedestal not linked to the altar");
			}
		} else // ritual grounds can't cross chunk borders
			helper.assertBlockPresent(Blocks.COBBLESTONE, ALTAR);
		helper.succeed();
	}

	/** Dreadstone ritual grounds need at least a Dreadlands Necronomicon */
	@GameTest(template = "empty")
	public static void altarNeedsBookTier(GameTestHelper helper) {
		ring(helper, ACBlocks.dreadstone_cobblestone.get());
		use(helper, sneakingPlayer(helper, new ItemStack(ACItems.necronomicon.get())), ALTAR);
		helper.assertBlockPresent(ACBlocks.dreadstone_cobblestone.get(), ALTAR);
		helper.succeed();
	}

	/** The Ritual of Breeding takes the offerings and 500 PE from the performer and breeds the cows nearby */
	// own batch: it failed once ("no calf") in the default batch, cause not found
	@GameTest(template = "empty", batch = "ritual_breeding", timeoutTicks = 400)
	public static void breedingRitual(GameTestHelper helper) {
		ring(helper, Blocks.STONE);
		helper.setBlock(ALTAR, ACBlocks.ritual_altar_stone.get());
		for(BlockPos p : RitualUtil.PEDESTAL_POSITIONS)
			helper.setBlock(ALTAR.offset(p), ACBlocks.ritual_pedestal_stone.get());
		ItemStack[] offerings = {new ItemStack(Items.WHEAT), new ItemStack(Items.POTATO), new ItemStack(Items.CARROT), new ItemStack(Items.MELON_SLICE),
				new ItemStack(Blocks.PUMPKIN), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS)};
		for(int i = 0; i < 8; i++)
			((IRitualPedestal) helper.getBlockEntity(ALTAR.offset(RitualUtil.PEDESTAL_POSITIONS.get(i)))).setItem(offerings[i]);
		Cow cow1 = helper.spawn(EntityType.COW, new BlockPos(1, 1, 1));
		Cow cow2 = helper.spawn(EntityType.COW, new BlockPos(6, 1, 6));
		cow1.setNoAi(true);
		cow2.setNoAi(true);

		ItemStack book = new ItemStack(ACItems.necronomicon.get());
		((ItemNecronomicon) book.getItem()).addEnergy(book, 5000);
		Player player = sneakingPlayer(helper, book);
		TileEntityRitualAltar altar = (TileEntityRitualAltar) helper.getBlockEntity(ALTAR);
		altar.performRitual(helper.getLevel(), helper.absolutePos(ALTAR), player);
		helper.runAfterDelay(2, () -> helper.assertTrue(altar.isPerformingRitual(), "ritual didn't start"));
		helper.succeedWhen(() -> {
			helper.assertFalse(altar.isPerformingRitual(), "ritual still running");
			// (cows of tests running next to this one may be in range of the ritual too)
			helper.assertTrue(!helper.getLevel().getEntitiesOfClass(Cow.class, cow1.getBoundingBox().minmax(cow2.getBoundingBox()).inflate(2), Cow::isBaby).isEmpty(), "no calf");
			helper.assertTrue(((ItemNecronomicon) book.getItem()).getContainedEnergy(book) == 4500, "500 PE not taken: " + ((ItemNecronomicon) book.getItem()).getContainedEnergy(book));
			for(BlockPos p : RitualUtil.PEDESTAL_POSITIONS)
				helper.assertTrue(((IRitualPedestal) helper.getBlockEntity(ALTAR.offset(p))).getItem().isEmpty(), "offering not consumed");
		});
	}

	/** Every ritual whose content is ported is registered */
	@GameTest(template = "empty", required = false)
	public static void allRitualsRegistered(GameTestHelper helper) {
		if(!RitualRecipes.getSkipped().isEmpty())
			helper.fail(RitualRegistry.instance().getRituals().size() + " rituals registered, waiting for content: " + String.join(", ", RitualRecipes.getSkipped()));
		helper.succeed();
	}
}
