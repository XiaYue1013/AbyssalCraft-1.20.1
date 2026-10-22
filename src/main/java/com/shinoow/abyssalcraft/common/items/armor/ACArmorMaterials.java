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
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items.armor;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Armor materials (1.12.2 values: durability factor, {boots, legs, chest, helmet} protection, enchantability, toughness)
 */
public enum ACArmorMaterials implements ArmorMaterial {

	ABYSSALNITE("abyssalnite", 35, new int[]{3, 6, 8, 3}, 13, 1.0F, () -> Ingredient.of(ACItems.abyssalnite_ingot.get())),
	DREAD("dread", 36, new int[]{3, 6, 8, 3}, 15, 1.0F, () -> Ingredient.of(ACItems.dreaded_shard_of_abyssalnite.get())),
	REFINED_CORALIUM("coralium", 37, new int[]{3, 6, 8, 3}, 14, 1.0F, () -> Ingredient.of(ACItems.refined_coralium_ingot.get())),
	PLATED_CORALIUM("coraliump", 55, new int[]{4, 7, 9, 4}, 14, 3.0F, () -> Ingredient.of(ACItems.coralium_plate.get())),
	DEPTHS("depths", 33, new int[]{3, 6, 8, 3}, 25, 1.5F, () -> Ingredient.of(ACItems.coralium_gem_cluster_9.get())),
	DREADIUM("dreadium", 40, new int[]{3, 6, 8, 3}, 15, 1.0F, () -> Ingredient.of(ACItems.dreadium_ingot.get())),
	DREADIUM_SAMURAI("dreadiums", 45, new int[]{3, 6, 8, 3}, 20, 1.5F, () -> Ingredient.of(ACItems.dreadium_plate.get())),
	ETHAXIUM("ethaxium", 50, new int[]{3, 6, 8, 3}, 25, 2.0F, () -> Ingredient.of(ACItems.ethaxium_ingot.get()));

	/** 1.12.2 base durability per slot: boots, legs, chest, helmet */
	private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

	private final String name;
	private final int durabilityFactor;
	private final int[] protection;
	private final int enchantability;
	private final float toughness;
	private final Supplier<Ingredient> repair;

	ACArmorMaterials(String name, int durabilityFactor, int[] protection, int enchantability, float toughness, Supplier<Ingredient> repair) {
		this.name = name;
		this.durabilityFactor = durabilityFactor;
		this.protection = protection;
		this.enchantability = enchantability;
		this.toughness = toughness;
		this.repair = repair;
	}

	private static int index(ArmorItem.Type type) {
		return switch (type) {
		case BOOTS -> 0;
		case LEGGINGS -> 1;
		case CHESTPLATE -> 2;
		case HELMET -> 3;
		};
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[index(type)] * durabilityFactor;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return protection[index(type)];
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_IRON;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repair.get();
	}

	@Override
	public String getName() {
		return "abyssalcraft:" + name;
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}
}
