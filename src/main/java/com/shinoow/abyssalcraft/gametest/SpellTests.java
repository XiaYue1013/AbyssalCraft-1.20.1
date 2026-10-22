package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.spell.SpellRegistry;
import com.shinoow.abyssalcraft.api.spell.SpellUtils;
import com.shinoow.abyssalcraft.common.inventory.ContainerSpellbook;
import com.shinoow.abyssalcraft.common.items.ItemScroll;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class SpellTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	/** A player at (1, 1, 3) with a scroll inscribed with the spell in the main hand and a Necronomicon with 4000 PE in the hotbar */
	private static Player caster(GameTestHelper helper, String spell, ItemStack scroll) {
		Player player = helper.makeMockPlayer();
		Vec3 pos = helper.absoluteVec(new Vec3(1.5, 1, 3.5));
		player.moveTo(pos.x, pos.y, pos.z);
		player.getInventory().selected = 0;
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell." + spell), scroll));
		ItemStack book = new ItemStack(ACItems.necronomicon.get());
		((IEnergyContainerItem) book.getItem()).addEnergy(book, 4000);
		player.getInventory().setItem(1, book);
		return player;
	}

	private static float bookPE(Player player) {
		ItemStack book = player.getInventory().getItem(1);
		return ((IEnergyContainerItem) book.getItem()).getContainedEnergy(book);
	}

	private static void look(GameTestHelper helper, Player player, double x, double y, double z) {
		player.lookAt(EntityAnchorArgument.Anchor.EYES, helper.absoluteVec(new Vec3(x, y, z)));
	}

	/** The 13 spells are registered; the spellbook inscribes a known spell on a fitting scroll and uses up the inputs */
	@GameTest(template = "empty", batch = "spells")
	public static void spellbookInscribes(GameTestHelper helper) {
		helper.assertTrue(SpellRegistry.instance().getSpells().size() == 13, "spells: " + SpellRegistry.instance().getSpells().size());
		Player player = helper.makeMockPlayer();
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ACItems.necronomicon.get()));
		var menu = new ContainerSpellbook(0, player.getInventory(), InteractionHand.MAIN_HAND);
		menu.getSlot(0).set(new ItemStack(ACItems.basic_scroll.get(), 2));
		menu.getSlot(1).set(new ItemStack(Items.APPLE));
		ItemStack out = menu.getSlot(ContainerSpellbook.OUTPUT).getItem();
		helper.assertTrue(out.is(ACItems.basic_scroll.get()) && SpellUtils.getSpell(out) != null && SpellUtils.getSpell(out).getUnlocalizedName().equals("ac.spell.lifedrain"),
				"inscribed " + out + " " + out.getTag());
		menu.getSlot(ContainerSpellbook.OUTPUT).onTake(player, out);
		helper.assertTrue(menu.getSlot(0).getItem().getCount() == 1 && menu.getSlot(1).getItem().isEmpty(), "inputs not used up");
		// Grasp of Cthulhu needs a Lesser scroll at least
		menu.getSlot(1).set(new ItemStack(Items.COD));
		helper.assertTrue(menu.getSlot(ContainerSpellbook.OUTPUT).getItem().isEmpty(), "Grasp of Cthulhu on a Basic scroll");
		menu.getSlot(0).set(new ItemStack(ACItems.lesser_scroll.get()));
		helper.assertTrue(ItemScroll.isInscribed(menu.getSlot(ContainerSpellbook.OUTPUT).getItem()), "no Grasp of Cthulhu on a Lesser scroll");
		menu.removed(player);
		helper.succeed();
	}

	/** Targeted spells act on the looked at mob and take their PE on the server */
	@GameTest(template = "empty", batch = "spells")
	public static void targetedSpells(GameTestHelper helper) {
		floor(helper);
		var pig = helper.spawn(EntityType.PIG, new BlockPos(5, 1, 3));
		pig.setNoAi(true);
		Player player = caster(helper, "lifedrain", new ItemStack(ACItems.basic_scroll.get()));
		player.setHealth(10);
		look(helper, player, 5.5, 1.4, 3.5);
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertTrue(pig.getHealth() == pig.getMaxHealth() - 5 && player.getHealth() == 15 && bookPE(player) == 3900, "Life Drain: pig " + pig.getHealth()
				+ ", player " + player.getHealth() + ", PE " + bookPE(player));

		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.graspofcthulhu"), new ItemStack(ACItems.lesser_scroll.get())));
		SpellUtils.castInstantSpell(player.getMainHandItem(), helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(pig.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && bookPE(player) == 3880, "Grasp of Cthulhu");
		pig.discard();

		var zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(5, 1, 3));
		zombie.setNoAi(true);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.detachment"), new ItemStack(ACItems.moderate_scroll.get())));
		look(helper, player, 5.5, 1.8, 3.5);
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertTrue(zombie.getItemBySlot(EquipmentSlot.HEAD).isEmpty(), "Detachment kept the helmet");
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.undeathtodust"), new ItemStack(ACItems.moderate_scroll.get())));
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertTrue(zombie.isRemoved(), "Undeath to Dust");

		float pe = bookPE(player);
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.stealvigor"), new ItemStack(ACItems.basic_scroll.get())));
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertTrue(bookPE(player) == pe && !player.hasEffect(MobEffects.DAMAGE_BOOST), "a spell without a target was cast");
		helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, zombie.getBoundingBox().inflate(3)).forEach(e -> e.discard());
		helper.succeed();
	}

	/** Block spells: Entropy wears stone down and gives PE, Mining bores into stone, Ooze Removal clears ooze */
	@GameTest(template = "empty", batch = "spells")
	public static void blockSpells(GameTestHelper helper) {
		floor(helper);
		helper.setBlock(new BlockPos(5, 1, 3), Blocks.STONE);
		Player player = caster(helper, "entropy", new ItemStack(ACItems.greater_scroll.get()));
		look(helper, player, 5.0, 1.5, 3.5);
		float pe = bookPE(player);
		SpellUtils.castInstantSpell(player.getMainHandItem(), helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(5, 1, 3));
		helper.assertTrue(bookPE(player) >= pe + 5, "Entropy gave no PE");

		for(int y = 1; y <= 4; y++) for(int z = 2; z <= 5; z++) helper.setBlock(new BlockPos(6, y, z), Blocks.STONE);
		helper.setBlock(new BlockPos(5, 1, 3), Blocks.AIR);
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.mining"), new ItemStack(ACItems.basic_scroll.get())));
		look(helper, player, 6.0, 2.5, 3.5);
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertBlockPresent(ACBlocks.solid_lava.get(), new BlockPos(6, 2, 3));

		helper.setBlock(new BlockPos(2, 1, 2), ACBlocks.shoggoth_ooze.get());
		player.getInventory().setItem(0, SpellRegistry.instance().inscribeSpell(SpellRegistry.instance().getSpell("ac.spell.oozeremoval"), new ItemStack(ACItems.basic_scroll.get())));
		SpellUtils.castChargingSpell(player.getMainHandItem(), helper.getLevel(), player);
		helper.assertBlockPresent(Blocks.AIR, new BlockPos(2, 1, 2));
		helper.succeed();
	}
}
