package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.rending.RendingRegistry;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.items.ItemDrainStaff;
import com.shinoow.abyssalcraft.common.items.ItemStaff;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class StaffTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	/** A player at (1, 1, 3) looking at the middle of the 1 block tall Shadow Creature at (5, 1, 3) */
	private static Player player(GameTestHelper helper, ItemStack stack) {
		Player player = helper.makeMockPlayer();
		Vec3 pos = helper.absoluteVec(new Vec3(1.5, 1, 3.5));
		player.moveTo(pos.x, pos.y, pos.z);
		aim(helper, player);
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		return player;
	}

	private static void aim(GameTestHelper helper, Player player) {
		player.lookAt(EntityAnchorArgument.Anchor.EYES, helper.absoluteVec(new Vec3(5.5, 1.5, 3.5)));
	}

	/** Staff of Rending: drains the looked at shadow mob (not through walls), turns 200 Shadow energy into a Shadow Gem */
	@GameTest(template = "empty", batch = "staffs")
	public static void staffRendsShadowMob(GameTestHelper helper) {
		floor(helper);
		helper.assertTrue(RendingRegistry.instance().getRendings().size() == 4, "rendings " + RendingRegistry.instance().getRendings().size());
		for(String ritual : new String[] {"jzaharStaff", "sorAWupgrade", "sorDLupgrade", "sorOMTupgrade"})
			helper.assertTrue(RitualRegistry.instance().getRitual(ritual) != null, ritual + " not registered");
		ItemStack stack = new ItemStack(ItemHandler.OMOTHOL_STAFF_OF_RENDING.get());
		IStaffOfRending staff = (IStaffOfRending) stack.getItem();
		Player player = player(helper, stack);
		var shadow = helper.spawn(ACEntityTypes.SHADOW_CREATURE.get(), new BlockPos(5, 1, 3));
		shadow.setNoAi(true);
		helper.assertTrue(staff.getDrainAmount(stack) == 4, "Omothol staff drains " + staff.getDrainAmount(stack));

		helper.setBlock(new BlockPos(3, 1, 3), Blocks.STONE);
		helper.setBlock(new BlockPos(3, 2, 3), Blocks.STONE);
		ItemDrainStaff.useStaff(helper.getLevel(), player, stack);
		helper.assertTrue(staff.getEnergy(stack, "Shadow") == 0 && shadow.getHealth() == shadow.getMaxHealth(), "drained through a wall");
		helper.setBlock(new BlockPos(3, 1, 3), Blocks.AIR);
		helper.setBlock(new BlockPos(3, 2, 3), Blocks.AIR);

		ItemDrainStaff.useStaff(helper.getLevel(), player, stack);
		helper.assertTrue(staff.getEnergy(stack, "Shadow") == 4 && shadow.getMaxHealth() - shadow.getHealth() == 4,
				"energy " + staff.getEnergy(stack, "Shadow") + ", health " + shadow.getHealth() + ", looking at " + ItemDrainStaff.lookedAt(player, ItemDrainStaff.RANGE));
		helper.assertTrue(staff.getEnergy(stack, "Abyssal") == 0, "a shadow mob gave Abyssal energy");

		shadow.discard();
		staff.setEnergy(200, stack, "Shadow");
		ItemDrainStaff.useStaff(helper.getLevel(), player, stack);
		helper.assertTrue(staff.getEnergy(stack, "Shadow") == 0 && player.getInventory().contains(new ItemStack(ACItems.shadow_gem.get())), "no Shadow Gem");
		helper.succeed();
	}

	/** Staff of the Gatekeeper: R'lyehian Gateway Key in mode 0, Staff of Rending (drains 5) in mode 1 */
	@GameTest(template = "empty", batch = "staffs")
	public static void gatekeeperStaffModes(GameTestHelper helper) {
		floor(helper);
		ItemStack stack = new ItemStack(ACItems.staff_of_the_gatekeeper.get());
		Player player = player(helper, stack);
		var shadow = helper.spawn(ACEntityTypes.SHADOW_CREATURE.get(), new BlockPos(5, 1, 3));
		shadow.setNoAi(true);

		helper.assertTrue(ItemStaff.getMode(stack) == 0, "doesn't start in gateway mode");
		stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(shadow.getHealth() == shadow.getMaxHealth() && ((IStaffOfRending) stack.getItem()).getEnergy(stack, "Shadow") == 0,
				"gateway mode drained a mob");

		// gateway mode: builds the overworld (Abyssal Wasteland) gateway like a R'lyehian Gateway Key
		player.setYRot(0);
		BlockPos clicked = helper.absolutePos(new BlockPos(2, 0, 6));
		var result = stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(clicked), Direction.UP, clicked, false)));
		helper.assertTrue(result.consumesAction(), "gateway mode built nothing: " + result);
		helper.assertBlockPresent(ACBlocks.abyssal_stone.get(), new BlockPos(2, 1, 6));

		helper.assertTrue(ItemStaff.switchMode(stack) == 1, "mode didn't switch");
		aim(helper, player);
		stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(((IStaffOfRending) stack.getItem()).getEnergy(stack, "Shadow") == 5 && shadow.getMaxHealth() - shadow.getHealth() == 5,
				"rending mode drained " + ((IStaffOfRending) stack.getItem()).getEnergy(stack, "Shadow"));
		BlockPos other = helper.absolutePos(new BlockPos(6, 0, 1));
		helper.assertFalse(stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(other), Direction.UP, other, false)))
				.consumesAction(), "rending mode built a gateway");
		shadow.discard();
		helper.succeed();
	}
}
