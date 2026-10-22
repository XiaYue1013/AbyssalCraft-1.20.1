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
package com.shinoow.abyssalcraft.init;

import java.util.*;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * AbyssalCraft creative tabs (same set as 1.12.2)
 */
public class ACTabs {

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AbyssalCraft.modid);

	public enum Tab {
		BLOCKS("acblocks", () -> new ItemStack(ACBlocks.darkstone.get())),
		ITEMS("acitems", () -> new ItemStack(ACItems.necronomicon.get())),
		TOOLS("actools", () -> new ItemStack(ACItems.darkstone_axe.get())),
		COMBAT("acctools", () -> new ItemStack(ACItems.darkstone_sword.get())),
		FOOD("acfood", () -> new ItemStack(ACItems.coralium_plagued_flesh_on_a_bone.get())),
		DECORATION("acdblocks", () -> new ItemStack(ACBlocks.wooden_crate.get())),
		CRYSTALS("accrystals", () -> new ItemStack(ACBlocks.crystallizer.get())),
		COINS("accoins", () -> new ItemStack(ACItems.coin.get())),
		SPELLS("acspells", () -> new ItemStack(ACItems.greater_scroll.get()));

		private final String id;
		private final Supplier<ItemStack> icon;
		private final List<Supplier<? extends Item>> contents = new ArrayList<>();
		private RegistryObject<CreativeModeTab> tab;

		Tab(String id, Supplier<ItemStack> icon) {
			this.id = id;
			this.icon = icon;
		}

		public void add(Supplier<? extends Item> item) {
			contents.add(item);
		}

		public List<Supplier<? extends Item>> getContents() {
			return contents;
		}

		public RegistryObject<CreativeModeTab> get() {
			return tab;
		}
	}

	static {
		for(Tab t : Tab.values())
			t.tab = TABS.register(t.id, () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup." + t.id))
					.icon(() -> {
						try {
							ItemStack stack = t.icon.get();
							return stack.isEmpty() ? new ItemStack(Items.BARRIER) : stack;
						} catch(Exception e) {
							// icon item not registered (yet)
							return new ItemStack(Items.BARRIER);
						}
					})
					.displayItems((params, output) -> {
						for(Supplier<? extends Item> item : t.contents) {
							output.accept(item.get());
							// PE items are also listed fully charged (1.12.2 getSubItems)
							if(item.get() instanceof IEnergyContainerItem pe) {
								ItemStack full = new ItemStack(item.get());
								pe.addEnergy(full, pe.getMaxEnergy(full));
								output.accept(full);
							}
							// every spell on a Greater and the two unique scrolls (1.12.2 spells tab)
							if(t == Tab.SPELLS && item.get() == ACItems.oblivion_scroll.get())
								for(var spell : com.shinoow.abyssalcraft.api.spell.SpellRegistry.instance().getSpells())
									for(var scroll : java.util.List.of(ACItems.greater_scroll, ACItems.antimatter_scroll, ACItems.oblivion_scroll))
										output.accept(com.shinoow.abyssalcraft.api.spell.SpellRegistry.instance().inscribeSpell(spell, new ItemStack(scroll.get())));
							// and the Staff of the Gatekeeper in its rending mode
							if(item.get() instanceof com.shinoow.abyssalcraft.common.items.ItemStaff) {
								ItemStack rending = new ItemStack(item.get());
								com.shinoow.abyssalcraft.common.items.ItemStaff.switchMode(rending);
								output.accept(rending);
							}
						}
					})
					.build());
	}
}
