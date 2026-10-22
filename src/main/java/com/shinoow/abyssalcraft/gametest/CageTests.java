package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class CageTests {
	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}
	@GameTest(template = "empty", batch = "cage", timeoutTicks = 60)
	public static void cagePreservesMob(GameTestHelper helper) {
		floor(helper); var player = helper.makeMockPlayer();
		player.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)).getCenter());
		var stack = new ItemStack(ACItems.interdimensional_cage.get()); var cage = (ItemInterdimensionalCage) stack.getItem();
		player.setItemInHand(InteractionHand.MAIN_HAND, stack); cage.addEnergy(stack, 1000);
		var pig = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 1)); pig.setNoAi(true); pig.setCustomName(Component.literal("Traveller")); pig.setHealth(7); pig.setBaby(true);
		float cost = ItemInterdimensionalCage.captureCost(pig); var uuid = pig.getUUID();
		helper.assertTrue(cage.capture(player, InteractionHand.MAIN_HAND, pig), "capture failed");
		helper.assertTrue(pig.isRemoved() && ItemInterdimensionalCage.isOccupied(stack), "pig not stored");
		helper.assertTrue(Math.abs(cage.getContainedEnergy(stack) - (1000 - cost)) < 0.01F, "wrong capture cost");
		var second = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 1));
		helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, second), "occupied cage overwritten"); second.discard();
		BlockPos blocked = helper.absolutePos(new BlockPos(4, 1, 4)); helper.getLevel().setBlockAndUpdate(blocked, Blocks.STONE.defaultBlockState());
		helper.assertFalse(cage.release(stack, helper.getLevel(), blocked), "released into stone");
		helper.assertTrue(ItemInterdimensionalCage.isOccupied(stack), "blocked release lost the payload");
		helper.runAfterDelay(2, () -> {
			helper.assertTrue(cage.release(stack, helper.getLevel(), helper.absolutePos(new BlockPos(6, 1, 6))), "release failed");
			var restored = helper.getLevel().getEntity(uuid);
			helper.assertTrue(restored instanceof Pig && ((Pig)restored).isBaby() && ((Pig)restored).getHealth() == 7
					&& restored.getName().getString().equals("Traveller"), "mob data not preserved");
			helper.assertFalse(ItemInterdimensionalCage.isOccupied(stack), "released cage still occupied");
			helper.assertFalse(cage.release(stack, helper.getLevel(), helper.absolutePos(new BlockPos(6, 1, 5))), "empty cage duplicated a mob");
			restored.discard(); helper.succeed();
		});
	}

	@GameTest(template = "empty", batch = "cage_restrictions")
	public static void cageRejectsInvalidCapture(GameTestHelper helper) {
		floor(helper); var player = helper.makeMockPlayer(); player.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)).getCenter());
		var stack = new ItemStack(ACItems.interdimensional_cage.get()); var cage = (ItemInterdimensionalCage)stack.getItem(); player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		var pig = helper.spawn(EntityType.PIG, new BlockPos(3, 1, 1)); pig.setNoAi(true);
		helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, pig), "capture without PE"); cage.addEnergy(stack, 1000);
		var blacklist = ACConfig.interdimensionalCageBlacklist;
		try {
			ACConfig.interdimensionalCageBlacklist = java.util.List.of("minecraft:pig");
			helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, pig), "blacklisted mob captured");
		} finally { ACConfig.interdimensionalCageBlacklist = blacklist; }
		helper.setBlock(new BlockPos(2, 1, 1), Blocks.STONE); helper.setBlock(new BlockPos(2, 2, 1), Blocks.STONE);
		helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, pig), "captured through a wall");
		helper.setBlock(new BlockPos(2, 1, 1), Blocks.AIR); helper.setBlock(new BlockPos(2, 2, 1), Blocks.AIR);
		pig.moveTo(helper.absolutePos(new BlockPos(6, 1, 1)).getCenter());
		helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, pig), "captured out of reach");
		helper.assertFalse(cage.capture(player, InteractionHand.MAIN_HAND, player), "captured a player");
		helper.assertTrue(!pig.isRemoved() && !ItemInterdimensionalCage.isOccupied(stack) && cage.getContainedEnergy(stack) == 1000, "rejected captures mutated data or PE");
		pig.discard(); helper.succeed();
	}
}
