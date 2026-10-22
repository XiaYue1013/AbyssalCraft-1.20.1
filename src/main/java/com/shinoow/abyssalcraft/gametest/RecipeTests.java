package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class RecipeTests {

	private static ItemStack craft(GameTestHelper helper, int width, ItemStack... grid) {
		var container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
			@Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
			@Override public boolean stillValid(Player player) { return true; }
		}, width, grid.length / width);
		for(int i = 0; i < grid.length; i++)
			container.setItem(i, grid[i]);
		var level = helper.getLevel();
		return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level)
				.map(r -> r.assemble(container, level.registryAccess())).orElse(ItemStack.EMPTY);
	}

	private static ItemStack smelt(GameTestHelper helper, ItemStack input) {
		var level = helper.getLevel();
		var container = new SimpleContainer(input);
		return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level)
				.map(r -> r.assemble(container, level.registryAccess())).orElse(ItemStack.EMPTY);
	}

	/** Crafting (shaped, tags, buckets, 1x1) and smelting (counts, configured) work with the 1.12.2 recipes */
	@GameTest(template = "empty", batch = "recipes")
	public static void craftingAndSmelting(GameTestHelper helper) {
		long count = helper.getLevel().getRecipeManager().getRecipes().stream().filter(r -> r.getId().getNamespace().equals(AbyssalCraft.modid)).count();
		helper.assertTrue(count >= 490, "only " + count + " AbyssalCraft recipes");

		ItemStack stone = new ItemStack(ACBlocks.abyssal_stone.get());
		ItemStack bricks = craft(helper, 2, stone.copy(), stone.copy(), stone.copy(), stone.copy());
		helper.assertTrue(bricks.is(ACBlocks.abyssal_stone_bricks.get().asItem()) && bricks.getCount() == 4, "abyssal stone bricks: " + bricks);
		ItemStack ingot = new ItemStack(ACItems.abyssalnite_ingot.get()), stick = new ItemStack(Items.STICK);
		ItemStack sword = craft(helper, 3, ItemStack.EMPTY, ingot.copy(), ItemStack.EMPTY, ItemStack.EMPTY, ingot.copy(), ItemStack.EMPTY, ItemStack.EMPTY, stick, ItemStack.EMPTY);
		helper.assertTrue(sword.is(ACItems.abyssalnite_sword.get()), "abyssalnite sword (tagged stick): " + sword);
		ItemStack button = craft(helper, 1, stone.copy());
		helper.assertTrue(button.is(ACBlocks.abyssal_stone_button.get().asItem()), "1x1 button: " + button);

		ItemStack iron = smelt(helper, new ItemStack(ACItems.coin.get()));
		helper.assertTrue(iron.is(Items.IRON_INGOT) && iron.getCount() == 4, "coin smelting: " + iron);
		helper.assertTrue(smelt(helper, new ItemStack(ACItems.chunk_of_coralium.get())).getCount() == 2, "chunk of coralium makes 2 ingots");
		helper.assertTrue(smelt(helper, new ItemStack(ACBlocks.darkstone_cobblestone.get())).is(ACBlocks.darkstone.get().asItem()), "darkstone smelting");
		helper.assertTrue(smelt(helper, new ItemStack(Items.EGG)).is(ACItems.fried_egg.get()), "fried egg (foodstuffs on)");
		helper.assertTrue(smelt(helper, new ItemStack(ACItems.ethaxium_helmet.get())).is(ACItems.ethaxium_nugget.get()), "armor smelting (smeltingRecipes on)");
		helper.succeed();
	}

	/** Upgrade Kits in the anvil: next tier, enchantments kept, 1.12.2 cost; plague books are refused */
	@GameTest(template = "empty", batch = "recipes")
	public static void upgradeKitAnvil(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		ItemStack pick = new ItemStack(Items.STONE_PICKAXE);
		pick.enchant(Enchantments.BLOCK_EFFICIENCY, 2);
		pick.setDamageValue(pick.getMaxDamage() / 2);
		var event = new AnvilUpdateEvent(pick, new ItemStack(ACItems.iron_upgrade_kit.get()), "", 0, player);
		MinecraftForge.EVENT_BUS.post(event);
		ItemStack out = event.getOutput();
		helper.assertTrue(out.is(Items.IRON_PICKAXE) && out.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY) == 2, "upgrade: " + out);
		// half durability: 5 levels, + 2 for Efficiency II
		helper.assertTrue(event.getCost() == 7 && event.getMaterialCost() == 1, "cost " + event.getCost());

		var armor = new AnvilUpdateEvent(new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(ACItems.abyssalnite_upgrade_kit.get()), "", 0, player);
		MinecraftForge.EVENT_BUS.post(armor);
		helper.assertTrue(armor.getOutput().is(ACItems.abyssalnite_chestplate.get()), "armor upgrade: " + armor.getOutput());
		var wrong = new AnvilUpdateEvent(new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(ACItems.iron_upgrade_kit.get()), "", 0, player);
		MinecraftForge.EVENT_BUS.post(wrong);
		helper.assertTrue(wrong.getOutput().isEmpty(), "wrong tier upgraded");

		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		net.minecraft.world.item.EnchantedBookItem.addEnchantment(book, new net.minecraft.world.item.enchantment.EnchantmentInstance(
				com.shinoow.abyssalcraft.api.AbyssalCraftAPI.coralium_enchantment.get(), 1));
		helper.assertTrue(MinecraftForge.EVENT_BUS.post(new AnvilUpdateEvent(new ItemStack(Items.IRON_SWORD), book, "", 0, player)), "Coralium book applied in an anvil");
		helper.succeed();
	}
}
