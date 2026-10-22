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
import com.shinoow.abyssalcraft.common.blocks.BlockStateTransformer;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityEnergyDepositioner;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityStateTransformer;
import com.shinoow.abyssalcraft.common.items.ItemStoneTablet;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the State Transformer and the Energy Depositioner
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class TabletTests {

	private static final BlockPos POS = new BlockPos(1, 1, 1);

	/** Insert mode packs the stored stacks into the tablet (PE = items, scaled by stack size), extract mode unpacks them */
	@GameTest(template = "empty", timeoutTicks = 500)
	public static void stateTransformerRoundTrip(GameTestHelper helper) {
		helper.setBlock(POS, ACBlocks.state_transformer.get());
		TileEntityStateTransformer tile = (TileEntityStateTransformer) helper.getBlockEntity(POS);
		tile.setItem(0, new ItemStack(ACItems.stone_tablet.get()));
		helper.assertBlockProperty(POS, BlockStateTransformer.TABLET, true);
		tile.setItem(1, new ItemStack(Items.COBBLESTONE, 64));
		tile.setItem(5, new ItemStack(Items.ENDER_PEARL, 16));
		helper.runAtTickTime(210, () -> {
			ItemStack tablet = tile.getItem(0);
			helper.assertTrue(((ItemStoneTablet) tablet.getItem()).hasInventory(tablet), "tablet wasn't filled");
			helper.assertTrue(tablet.getTag().getFloat("PotEnergy") == 64 + 16 * 4, "wrong PE: " + tablet.getTag().getFloat("PotEnergy"));
			helper.assertTrue(tile.getItem(1).isEmpty() && tile.getItem(5).isEmpty(), "stacks not stored");
			helper.assertTrue(tile.toggleMode(), "mode not switched");
		});
		helper.succeedWhen(() -> {
			helper.assertTrue(tile.getItem(1).is(Items.COBBLESTONE) && tile.getItem(1).getCount() == 64, "cobblestone not restored");
			helper.assertTrue(tile.getItem(5).is(Items.ENDER_PEARL) && tile.getItem(5).getCount() == 16, "ender pearls not restored");
			helper.assertFalse(((ItemStoneTablet) tile.getItem(0).getItem()).hasInventory(tile.getItem(0)), "tablet still filled");
		});
	}

	/** The depositioner drains a filled tablet's PE and returns the emptied tablet */
	@GameTest(template = "empty", timeoutTicks = 300)
	public static void depositionerDrainsTablet(GameTestHelper helper) {
		helper.setBlock(POS, ACBlocks.energy_depositioner.get());
		TileEntityEnergyDepositioner tile = (TileEntityEnergyDepositioner) helper.getBlockEntity(POS);
		ItemStack tablet = ItemStoneTablet.withEmptyInventory(new ItemStack(ACItems.stone_tablet.get()));
		tablet.getTag().putFloat("PotEnergy", 1000);
		tile.setItem(0, tablet);
		helper.succeedWhen(() -> {
			ItemStack out = tile.getItem(1);
			helper.assertTrue(out.is(ACItems.stone_tablet.get()), "no tablet in the output");
			helper.assertFalse(((ItemStoneTablet) out.getItem()).hasInventory(out), "tablet not emptied");
			helper.assertTrue(tile.getContainedEnergy() > 900, "PE not drained: " + tile.getContainedEnergy());
		});
	}
}
