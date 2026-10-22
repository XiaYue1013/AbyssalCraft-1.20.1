package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.EntityCoraliumArrow;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class WeaponTests {

	private static double attackDamage(Item item) {
		return 1 + new ItemStack(item).getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)
				.stream().mapToDouble(m -> m.getAmount()).sum();
	}

	/** 1.12.2 materials: hilt 5 damage / 200 uses, katana 15 damage / 1000 uses / enchantability 14, both repaired with Dreadium Crystals */
	@GameTest(template = "empty", batch = "weapons")
	public static void katanaStats(GameTestHelper helper) {
		Item hilt = ACItems.dreadium_katana_hilt.get(), katana = ACItems.dreadium_katana.get();
		helper.assertTrue(attackDamage(hilt) == 5 && attackDamage(katana) == 15, "damage " + attackDamage(hilt) + " / " + attackDamage(katana));
		helper.assertTrue(new ItemStack(hilt).getMaxDamage() == 200 && new ItemStack(katana).getMaxDamage() == 1000, "wrong durability");
		helper.assertTrue(katana.getEnchantmentValue() == 14 && hilt.getEnchantmentValue() == 0, "wrong enchantability");
		ItemStack crystal = new ItemStack(ACItems.crystal(14).get());
		helper.assertTrue(katana.isValidRepairItem(new ItemStack(katana), crystal) && hilt.isValidRepairItem(new ItemStack(hilt), crystal)
				&& !katana.isValidRepairItem(new ItemStack(katana), new ItemStack(ACItems.dreadium_ingot.get())), "wrong repair item");
		helper.succeed();
	}

	/** A fully drawn Coralium Longbow shoots a critical Coralium arrow; its hits give the Coralium Plague */
	@GameTest(template = "empty", batch = "weapons", timeoutTicks = 60)
	public static void longbowShootsCoraliumArrows(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
		var player = helper.makeMockPlayer();
		player.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)).getCenter());
		player.getInventory().add(new ItemStack(Items.ARROW));
		ItemStack bow = new ItemStack(ACItems.coralium_longbow.get());
		helper.assertTrue(bow.getMaxDamage() == 637 && bow.getItem().isValidRepairItem(bow, new ItemStack(ACItems.refined_coralium_ingot.get())), "wrong bow stats");
		bow.releaseUsing(helper.getLevel(), player, bow.getUseDuration() - 20);
		var shot = helper.getLevel().getEntitiesOfClass(EntityCoraliumArrow.class, new AABB(player.blockPosition()).inflate(4));
		helper.assertTrue(shot.size() == 1 && shot.get(0).getOwner() == player && shot.get(0).isCritArrow(), "shot " + shot);
		helper.assertTrue(shot.get(0).pickup == AbstractArrow.Pickup.ALLOWED && !player.getInventory().contains(new ItemStack(Items.ARROW)),
				"arrow not used up / can't be picked up");
		shot.get(0).discard();

		var pig = helper.spawn(EntityType.PIG, new BlockPos(5, 1, 5));
		pig.setNoAi(true);
		var arrow = new EntityCoraliumArrow(helper.getLevel(), player);
		arrow.setPos(pig.getX(), pig.getY() + 3, pig.getZ());
		arrow.shoot(0, -1, 0, 2.0F, 0);
		helper.getLevel().addFreshEntity(arrow);
		helper.succeedWhen(() -> {
			helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.coralium_plague.get()) && pig.getHealth() < pig.getMaxHealth(), "pig not shot or plagued");
			pig.discard();
			arrow.discard();
		});
	}
}
