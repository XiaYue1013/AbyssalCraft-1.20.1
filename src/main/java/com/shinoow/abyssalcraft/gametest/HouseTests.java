package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.blocks.BlockHouse;
import com.shinoow.abyssalcraft.common.world.gen.structure.StructureHouse;
import com.shinoow.abyssalcraft.lib.util.IHiddenRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class HouseTests {

	/** The hidden house ritual is only for the author; anyone else's House may build the small windowless house */
	@GameTest(template = "empty", batch = "house")
	public static void smallHouse(GameTestHelper helper) {
		var ritual = RitualRegistry.instance().getRitual("house");
		helper.assertTrue(ritual instanceof IHiddenRitual, "house ritual missing or not hidden");
		helper.assertFalse(ritual.canCompleteRitual(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), helper.makeMockPlayer()), "anyone can do the house ritual");

		BlockPos house = new BlockPos(3, 1, 7);
		helper.setBlock(house, ACBlocks.house.get());
		BlockHouse.buildSmallHouse(helper.getLevel(), helper.absolutePos(house), false);
		helper.assertBlockPresent(Blocks.COBBLESTONE_STAIRS, new BlockPos(3, 1, 6));
		helper.assertBlockProperty(new BlockPos(3, 1, 6), StairBlock.FACING, Direction.NORTH);
		helper.assertBlockPresent(Blocks.AIR, house);
		for(int x = 1; x <= 5; x++)
			for(int z = 1; z <= 5; z++)
				helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(x, 1, z));
		helper.assertBlockPresent(Blocks.OAK_PLANKS, new BlockPos(1, 3, 3));
		helper.assertBlockPresent(Blocks.OAK_LOG, new BlockPos(1, 5, 3));
		helper.assertBlockPresent(Blocks.AIR, new BlockPos(3, 2, 3));
		helper.succeed();
	}

	/** The author's house: 1.12.2 StructureHouse, built far away from the other tests */
	@GameTest(template = "empty", batch = "house", timeoutTicks = 100)
	public static void authorHouse(GameTestHelper helper) {
		var level = helper.getLevel();
		BlockPos origin = new BlockPos(-30000, 120, -30000);
		new StructureHouse().generate(level, level.random, origin);
		int ethaxium = 0, darkstone = 0, stairs = 0;
		for(BlockPos pos : BlockPos.betweenClosed(origin.offset(-12, 0, -26), origin.offset(12, 16, 2))) {
			var state = level.getBlockState(pos);
			if(state.is(ACBlocks.ethaxium_bricks.get())) ethaxium++;
			else if(state.is(ACBlocks.darkstone_bricks.get())) darkstone++;
			else if(state.is(ACBlocks.ethaxium_brick_stairs.get())) stairs++;
		}
		AbyssalCraft.LOGGER.info("[HouseTests] author house: {} ethaxium bricks, {} darkstone bricks, {} stairs", ethaxium, darkstone, stairs);
		helper.assertTrue(ethaxium > 1000 && darkstone > 400 && stairs > 80, "house blocks: " + ethaxium + " / " + darkstone + " / " + stairs);
		helper.succeed();
	}
}
