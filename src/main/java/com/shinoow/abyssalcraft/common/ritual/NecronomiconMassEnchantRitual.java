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
 *     1.20.1 port - enchantments keyed by Enchantment instead of numeric id
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.block.IRitualPedestal;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

/**
 * Merges the enchantments of eight enchanted books onto the item on the altar (levels add up to the configured maximum)
 */
public class NecronomiconMassEnchantRitual extends NecronomiconRitual {

	private List<ItemStack> books = new ArrayList<>();

	public NecronomiconMassEnchantRitual() {
		super("massEnchantment", 4, 50000, Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK,
				Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK, Items.ENCHANTED_BOOK);
		setRitualParticle(EnumRitualParticle.GLYPHS);
	}

	@Override
	public boolean requiresItemSacrifice(){
		return true;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		if(world.getBlockEntity(pos) instanceof IRitualAltar altar) {
			if(altar.getItem().is(Items.BOOK) && !ACConfig.enchantBooks)
				return false;
			if(altar.getItem().isEnchantable() || altar.getItem().is(Items.BOOK)) {
				books = altar.getPedestals().stream().map(IRitualPedestal::getItem).filter(i -> i.is(Items.ENCHANTED_BOOK)).collect(Collectors.toList());
				if(!ACConfig.enchantMergedBooks && books.stream().anyMatch(i -> i.hasTag() && i.getTag().contains("RepairCost"))) {
					player.displayClientMessage(Component.translatable("message.ritual.massenchanttamper"), true);
					return false;
				}
				return books.size() == 8;
			}
		}
		return false;
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		if(world.getBlockEntity(pos) instanceof IRitualAltar altar) {
			Map<Enchantment, Integer> enchantments = new LinkedHashMap<>();
			for(ItemStack book : books)
				EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(book)).forEach((ench, lvl) ->
				enchantments.merge(ench, Math.min(lvl, ACConfig.enchantmentMaxLevel), (k, v) -> Math.min(k+v, ACConfig.enchantmentMaxLevel)));

			if(!enchantments.isEmpty())
				if(altar.getItem().is(Items.BOOK) && ACConfig.enchantBooks) {
					ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
					enchantments.forEach((ench, lvl) -> EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(ench, lvl)));
					altar.setItem(book);
				} else if(!altar.getItem().is(Items.BOOK)) {
					ItemStack item = altar.getItem().copy();
					EnchantmentHelper.setEnchantments(enchantments, item);
					altar.setItem(item);
				}
		}
	}
}
