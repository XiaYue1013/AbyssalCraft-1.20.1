/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 *     1.20.1 port - effects/potions from MiscHandler, brewing through Forge's brewing registry
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.potion.PotionAntimatter;
import com.shinoow.abyssalcraft.common.potion.PotionCplague;
import com.shinoow.abyssalcraft.common.potion.PotionDplague;
import com.shinoow.abyssalcraft.lib.ACClientVars;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AbyssalCraft.modid);
	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, AbyssalCraft.modid);

	public static final RegistryObject<MobEffect> CORALIUM_PLAGUE = EFFECTS.register("coralium_plague", () -> new PotionCplague(ACClientVars.CORALIUM_PLAGUE_COLOR));
	public static final RegistryObject<MobEffect> DREAD_PLAGUE = EFFECTS.register("dread_plague", () -> new PotionDplague(ACClientVars.DREAD_PLAGUE_COLOR));
	public static final RegistryObject<MobEffect> ANTIMATTER = EFFECTS.register("antimatter", () -> new PotionAntimatter(ACClientVars.ANTIMATTER_COLOR));

	public static final RegistryObject<Potion> CPLAGUE_NORMAL = potion("cplague", "coralium_plague", CORALIUM_PLAGUE, 3600, 0);
	public static final RegistryObject<Potion> CPLAGUE_LONG = potion("cplague_long", "coralium_plague", CORALIUM_PLAGUE, 9600, 0);
	public static final RegistryObject<Potion> DPLAGUE_NORMAL = potion("dplague", "dread_plague", DREAD_PLAGUE, 3600, 0);
	public static final RegistryObject<Potion> DPLAGUE_LONG = potion("dplague_long", "dread_plague", DREAD_PLAGUE, 9600, 0);
	public static final RegistryObject<Potion> DPLAGUE_STRONG = potion("dplague_strong", "dread_plague", DREAD_PLAGUE, 432, 1);
	public static final RegistryObject<Potion> ANTIMATTER_NORMAL = potion("antimatter", "antimatter", ANTIMATTER, 3600, 0);
	public static final RegistryObject<Potion> ANTIMATTER_LONG = potion("antimatter_long", "antimatter", ANTIMATTER, 9600, 0);

	private static RegistryObject<Potion> potion(String name, String baseName, Supplier<MobEffect> effect, int duration, int amplifier) {
		return POTIONS.register(name, () -> new Potion(baseName, new MobEffectInstance(effect.get(), duration, amplifier)));
	}

	/** Called during common setup */
	public static void addBrewing() {
		addBrewing(() -> Potions.AWKWARD, ACItems.coralium_plagued_flesh, CPLAGUE_NORMAL);
		addBrewing(() -> Potions.AWKWARD, ACItems.coralium_plagued_flesh_on_a_bone, CPLAGUE_NORMAL);
		addBrewing(CPLAGUE_NORMAL, () -> Items.REDSTONE, CPLAGUE_LONG);
		addBrewing(() -> Potions.AWKWARD, ACItems.dread_fragment, DPLAGUE_NORMAL);
		addBrewing(DPLAGUE_NORMAL, () -> Items.REDSTONE, DPLAGUE_LONG);
		addBrewing(DPLAGUE_NORMAL, () -> Items.GLOWSTONE_DUST, DPLAGUE_STRONG);
	}

	private static void addBrewing(Supplier<Potion> input, Supplier<Item> ingredient, Supplier<Potion> output) {
		BrewingRecipeRegistry.addRecipe(new PotionMix(input, ingredient, output));
	}

	/**
	 * Equivalent of the vanilla potion mixes (which are private): works on normal, splash and lingering potions
	 */
	private record PotionMix(Supplier<Potion> input, Supplier<Item> ingredient, Supplier<Potion> output) implements IBrewingRecipe {

		private static boolean isBottle(ItemStack stack) {
			return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION);
		}

		@Override
		public boolean isInput(ItemStack stack) {
			return isBottle(stack) && PotionUtils.getPotion(stack) == input.get();
		}

		@Override
		public boolean isIngredient(ItemStack stack) {
			return stack.is(ingredient.get());
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
			if(!isInput(input) || !isIngredient(ingredient)) return ItemStack.EMPTY;
			return PotionUtils.setPotion(new ItemStack(input.getItem()), output.get());
		}
	}
}
