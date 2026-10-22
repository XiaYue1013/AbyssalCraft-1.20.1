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
package com.shinoow.abyssalcraft.api.ritual;

import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

/**
 * A Necronomicon Ritual that enchants the item placed on the altar
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconEnchantmentRitual extends NecronomiconRitual {

	private Supplier<EnchantmentInstance> enchantment;

	public NecronomiconEnchantmentRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, Supplier<EnchantmentInstance> enchantment, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, offerings);
		this.enchantment = enchantment;
		setRitualParticle(EnumRitualParticle.GLYPHS);
	}

	public NecronomiconEnchantmentRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, Supplier<EnchantmentInstance> enchantment, Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, enchantment, offerings);
	}

	public NecronomiconEnchantmentRitual(String unlocalizedName, int bookType, float requiredEnergy, Supplier<EnchantmentInstance> enchantment, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, enchantment, offerings);
	}

	@Override
	public boolean requiresItemSacrifice(){
		return true;
	}

	/**
	 * Getter for the enchantment
	 */
	public EnchantmentInstance getEnchantment(){
		return enchantment.get();
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return canEnchant(getAltarItem(world, pos));
	}

	private boolean canEnchant(ItemStack stack){
		if(stack.isEmpty()) return false;
		Enchantment ench = getEnchantment().enchantment;
		if(stack.isEnchanted()){
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			for(Enchantment e : enchantments.keySet())
				if(!e.isCompatibleWith(ench))
					return false;
		}
		return ench.canEnchant(stack);
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		ItemStack item = getAltarItem(world, pos);
		if(canEnchant(item)){
			ItemStack copy = item.copy();
			EnchantmentInstance data = getEnchantment();
			copy.enchant(data.enchantment, data.level);
			setAltarItem(world, pos, copy);
		}
	}
}
