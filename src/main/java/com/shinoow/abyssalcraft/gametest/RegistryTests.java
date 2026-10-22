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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACTags;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry level checks
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class RegistryTests {

	/**
	 * Every block/item of the 1.11.3 baseline (the API lookup holders) has to be registered.
	 * This test intentionally fails until the port is complete and lists what's missing.
	 */
	@GameTest(template = "empty", required = false)
	public static void allBaselineContentRegistered(GameTestHelper helper) {
		List<String> missing = new ArrayList<>();
		for(Class<?> holder : new Class<?>[] {ACBlocks.class, ACItems.class})
			for(Field f : holder.getFields())
				if(f.getType() == RegistryObject.class)
					try {
						RegistryObject<?> ro = (RegistryObject<?>) f.get(null);
						if(!ro.isPresent())
							missing.add(ro.getId().getPath());
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
		if(!missing.isEmpty())
			helper.fail(missing.size() + " not registered yet: " + String.join(", ", missing));
		helper.succeed();
	}

	/** Abyssalnite tools can mine dreadstone (1.12.2 harvest level 4), diamond and netherite tools can't */
	@GameTest(template = "empty")
	public static void harvestLevels(GameTestHelper helper) {
		var dreadstone = ACBlocks.dreadstone.get().defaultBlockState();
		helper.assertTrue(new ItemStack(ACItems.abyssalnite_pickaxe.get()).isCorrectToolForDrops(dreadstone), "abyssalnite pickaxe can't mine dreadstone");
		helper.assertFalse(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(dreadstone), "diamond pickaxe can mine dreadstone");
		helper.assertFalse(new ItemStack(Items.NETHERITE_PICKAXE).isCorrectToolForDrops(dreadstone), "netherite pickaxe can mine dreadstone");
		var ethaxium = ACBlocks.ethaxium_bricks.get().defaultBlockState();
		helper.assertFalse(new ItemStack(ACItems.dreadium_pickaxe.get()).isCorrectToolForDrops(ethaxium), "dreadium pickaxe can mine ethaxium");
		helper.assertTrue(new ItemStack(ACItems.ethaxium_pickaxe.get()).isCorrectToolForDrops(ethaxium), "ethaxium pickaxe can't mine ethaxium");
		helper.assertTrue(ethaxium.is(net.minecraft.tags.BlockTags.WITHER_IMMUNE), "ethaxium not wither immune");
		helper.assertTrue(new ItemStack(ACItems.crystal(5).get()).is(ACTags.Items.CRYSTALS), "crystal tag missing");
		helper.succeed();
	}
}
