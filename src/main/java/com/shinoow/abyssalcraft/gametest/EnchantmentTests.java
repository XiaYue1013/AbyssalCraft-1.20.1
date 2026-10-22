package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.items.ItemDrainStaff;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class EnchantmentTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	/** Where each enchantment goes and what it's compatible with (1.12.2 rules) */
	@GameTest(template = "empty", batch = "enchantments")
	public static void enchantmentRules(GameTestHelper helper) {
		var coralium = AbyssalCraftAPI.coralium_enchantment.get();
		var dread = AbyssalCraftAPI.dread_enchantment.get();
		var lightPierce = AbyssalCraftAPI.light_pierce.get();
		var sapping = AbyssalCraftAPI.sapping.get();
		var multiRend = AbyssalCraftAPI.multi_rend.get();
		ItemStack sword = new ItemStack(Items.IRON_SWORD), axe = new ItemStack(Items.IRON_AXE), staff = new ItemStack(ACItems.staff_of_rending.get());
		helper.assertTrue(coralium.canEnchant(sword) && !coralium.canApplyAtEnchantingTable(sword) && !coralium.isAllowedOnBooks()
				&& !coralium.isCompatibleWith(dread), "weapon infusion rules");
		helper.assertTrue(lightPierce.canEnchant(axe) && lightPierce.getMaxLevel() == 5 && !lightPierce.isCompatibleWith(Enchantments.SHARPNESS)
				&& !Enchantments.SMITE.isCompatibleWith(lightPierce), "Light Pierce rules");
		helper.assertTrue(lightPierce.getDamageBonus(4, AbyssalCraftAPI.SHADOW) == 10 && lightPierce.getDamageBonus(4, MobType.UNDEAD) == 0, "Light Pierce damage");
		helper.assertTrue(sapping.canEnchant(staff) && multiRend.canEnchant(new ItemStack(ACItems.staff_of_the_gatekeeper.get()))
				&& !sapping.canEnchant(sword) && sapping.getMaxLevel() == 3, "staff enchantment rules");
		helper.assertTrue(AbyssalCraftAPI.iron_wall.get().canEnchant(new ItemStack(Items.IRON_CHESTPLATE)) && !AbyssalCraftAPI.iron_wall.get().canEnchant(new ItemStack(Items.IRON_HELMET)),
				"Iron Wall rules");
		if(ACConfig.plague_enchantments)
			helper.assertTrue(RitualRegistry.instance().getRitual("corEnchantment") != null && RitualRegistry.instance().getRitual("dreEnchantment") != null,
					"plague enchantment rituals missing");
		helper.succeed();
	}

	/** Coralium / Dread infused weapons plague what they hit; Iron Wall stops the knockback of a hit */
	@GameTest(template = "empty", batch = "enchantments")
	public static void enchantmentEffects(GameTestHelper helper) {
		floor(helper);
		var player = helper.makeMockPlayer();
		player.moveTo(helper.absoluteVec(new Vec3(1.5, 1, 1.5)));
		var coraliumTarget = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 1));
		var dreadTarget = helper.spawn(EntityType.PIG, new BlockPos(1, 1, 2));
		var walled = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 2));
		for(var pig : new net.minecraft.world.entity.Mob[] {coraliumTarget, dreadTarget, walled}) pig.setNoAi(true);

		ItemStack sword = new ItemStack(Items.IRON_SWORD);
		sword.enchant(AbyssalCraftAPI.coralium_enchantment.get(), 1);
		player.setItemInHand(InteractionHand.MAIN_HAND, sword);
		player.attack(coraliumTarget);
		ItemStack dreadSword = new ItemStack(Items.IRON_SWORD);
		dreadSword.enchant(AbyssalCraftAPI.dread_enchantment.get(), 1);
		player.setItemInHand(InteractionHand.MAIN_HAND, dreadSword);
		player.attack(dreadTarget);
		helper.assertTrue(coraliumTarget.hasEffect(AbyssalCraftAPI.coralium_plague.get()) && !coraliumTarget.hasEffect(AbyssalCraftAPI.dread_plague.get()), "Coralium hit");
		helper.assertTrue(dreadTarget.hasEffect(AbyssalCraftAPI.dread_plague.get()), "Dread hit");

		ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);
		chestplate.enchant(AbyssalCraftAPI.iron_wall.get(), 1);
		walled.setItemSlot(EquipmentSlot.CHEST, chestplate);
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		player.attack(walled);
		helper.assertTrue(walled.hurtTime > 0 && walled.getDeltaMovement().horizontalDistanceSqr() == 0, "Iron Wall wearer knocked back: " + walled.getDeltaMovement());
		for(var pig : new net.minecraft.world.entity.Mob[] {coraliumTarget, dreadTarget, walled}) pig.discard();
		helper.succeed();
	}

	/** Sapping adds to the drain, Multi-Rend drains the mobs around the target too */
	@GameTest(template = "empty", batch = "enchantments")
	public static void staffEnchantments(GameTestHelper helper) {
		floor(helper);
		ItemStack staff = new ItemStack(ACItems.staff_of_rending.get());
		staff.enchant(AbyssalCraftAPI.sapping.get(), 2);
		staff.enchant(AbyssalCraftAPI.multi_rend.get(), 1);
		IStaffOfRending rending = (IStaffOfRending) staff.getItem();
		ItemStack gatekeeper = new ItemStack(ACItems.staff_of_the_gatekeeper.get());
		gatekeeper.enchant(AbyssalCraftAPI.sapping.get(), 3);
		helper.assertTrue(rending.getDrainAmount(staff) == 3 && ((IStaffOfRending) gatekeeper.getItem()).getDrainAmount(gatekeeper) == 8, "Sapping drain");

		var player = helper.makeMockPlayer();
		player.moveTo(helper.absoluteVec(new Vec3(1.5, 1, 3.5)));
		player.lookAt(EntityAnchorArgument.Anchor.EYES, helper.absoluteVec(new Vec3(5.5, 1.5, 3.5)));
		var target = helper.spawn(ACEntityTypes.SHADOW_CREATURE.get(), new BlockPos(5, 1, 3));
		var near = helper.spawn(ACEntityTypes.SHADOW_CREATURE.get(), new BlockPos(6, 1, 5));
		target.setNoAi(true);
		near.setNoAi(true);
		ItemDrainStaff.useStaff(helper.getLevel(), player, staff);
		helper.assertTrue(rending.getEnergy(staff, "Shadow") == 6 && near.getHealth() < near.getMaxHealth(), "Multi-Rend energy " + rending.getEnergy(staff, "Shadow"));
		target.discard();
		near.discard();
		helper.succeed();
	}
}
