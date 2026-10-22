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
import com.shinoow.abyssalcraft.api.recipe.MaterializerRecipes;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityCrystallizer;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEngraver;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityTransmutator;
import com.shinoow.abyssalcraft.common.items.ItemCrystalBag;
import com.shinoow.abyssalcraft.common.inventory.InventoryCrystalBag;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the item processing machines
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class MachineTests {

	private static final BlockPos POS = new BlockPos(1, 1, 1);

	/** Iron Ingot + Methane fuel -> Crystallized Iron (crystal rework enabled by default) */
	@GameTest(template = "empty", timeoutTicks = 300)
	public static void crystallizerMakesIronCrystal(GameTestHelper helper) {
		helper.setBlock(POS, ACBlocks.crystallizer.get());
		TileEntityCrystallizer tile = (TileEntityCrystallizer) helper.getBlockEntity(POS);
		tile.setItem(0, new ItemStack(Items.IRON_INGOT));
		tile.setItem(1, new ItemStack(ACItems.methane.get()));
		helper.succeedWhen(() -> {
			helper.assertTrue(tile.getItem(2).is(ACItems.crystal(0).get()), "no iron crystal yet");
			helper.assertTrue(tile.getItem(0).isEmpty(), "input not consumed");
			helper.assertTrue(tile.getItem(1).isEmpty(), "fuel not consumed");
			helper.assertTrue(helper.getBlockState(POS).getValue(com.shinoow.abyssalcraft.common.blocks.BlockProcessor.LIT), "not lit while running");
		});
	}

	/** Dense Carbon Cluster + Coralium Gem fuel -> Diamond */
	@GameTest(template = "empty", timeoutTicks = 300)
	public static void transmutatorMakesDiamond(GameTestHelper helper) {
		helper.setBlock(POS, ACBlocks.transmutator.get());
		TileEntityTransmutator tile = (TileEntityTransmutator) helper.getBlockEntity(POS);
		tile.setItem(0, new ItemStack(ACItems.dense_carbon_cluster.get()));
		tile.setItem(1, new ItemStack(ACItems.coralium_gem.get()));
		helper.succeedWhen(() -> helper.assertTrue(tile.getItem(2).is(Items.DIAMOND), "no diamond yet"));
	}

	/** Coin + Cthulhu Engraving -> Cthulhu Engraved Coin, template loses durability */
	@GameTest(template = "empty", timeoutTicks = 300)
	public static void engraverEngravesCoin(GameTestHelper helper) {
		helper.setBlock(POS, ACBlocks.engraver.get());
		TileEntityEngraver tile = (TileEntityEngraver) helper.getBlockEntity(POS);
		tile.setItem(0, new ItemStack(ACItems.coin.get(), 2));
		tile.setItem(1, new ItemStack(ACItems.cthulhu_engraving.get()));
		helper.succeedWhen(() -> {
			helper.assertTrue(tile.getItem(2).is(ACItems.cthulhu_engraved_coin.get()), "no engraved coin yet");
			helper.assertTrue(tile.getItem(1).getDamageValue() == 1, "engraving template not damaged");
		});
	}

	/** Crystal bag with an Iron Crystal can materialize an Iron Ingot, which consumes the crystal */
	@GameTest(template = "empty")
	public static void materializerConsumesCrystals(GameTestHelper helper) {
		ItemStack bag = new ItemStack(ACItems.small_crystal_bag.get());
		ItemCrystalBag.setInventorySize(bag);
		InventoryCrystalBag inv = new InventoryCrystalBag(bag, InteractionHand.MAIN_HAND);
		inv.setItem(0, new ItemStack(ACItems.crystal(0).get(), 2));
		helper.assertTrue(MaterializerRecipes.instance().getMaterializationResult(bag).stream().anyMatch(s -> s.is(Items.IRON_INGOT)),
				"iron ingot not offered");
		MaterializerRecipes.instance().processMaterialization(new ItemStack(Items.IRON_INGOT), bag);
		ItemStack[] left = MaterializerRecipes.instance().extractItemsFromBag(bag);
		helper.assertTrue(left != null && left.length == 1 && left[0].getCount() == 1, "crystal not consumed");
		helper.succeed();
	}
}
