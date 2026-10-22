/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (ContainerSpellbook, InventorySpellbook, SlotSpellOutput); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellRegistry;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import com.shinoow.abyssalcraft.init.ACMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Necronomicon spellbook: a parchment (slot 0) and up to five reagents inscribe the matching spell the player has
 * unlocked (output slot) - taking it uses one of each. Items left in the grid go back to the player on closing.
 */
public class ContainerSpellbook extends AbstractContainerMenu {

	public static final int OUTPUT = 6;

	private final SimpleContainer grid = new SimpleContainer(6) {
		@Override
		public void setChanged() {
			super.setChanged();
			slotsChanged(this);
		}
	};
	private final SimpleContainer output = new SimpleContainer(1);
	public final ItemStack book;
	private final InteractionHand hand;
	private final Player user;
	@Nullable
	public Spell currentSpell;

	public static ContainerSpellbook client(int id, Inventory inv, FriendlyByteBuf buf) {
		return new ContainerSpellbook(id, inv, buf.readEnum(InteractionHand.class));
	}

	public ContainerSpellbook(int id, Inventory inv, InteractionHand hand) {
		super(ACMenus.SPELLBOOK.get(), id);
		user = inv.player;
		this.hand = hand;
		book = user.getItemInHand(hand);
		int[][] spots = {{51, 91}, {51, 66}, {76, 87}, {65, 116}, {37, 116}, {26, 87}};
		for(int i = 0; i < 6; i++)
			addSlot(new Slot(grid, i, spots[i][0], spots[i][1]));
		addSlot(new Slot(output, 0, 134, 91) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				for(int i = 0; i < 6; i++)
					grid.removeItem(i, 1);
				super.onTake(player, stack);
			}
		});
		int offset = (8 - 4) * 18 - 1;
		for(int j = 0; j < 3; ++j)
			for(int k = 0; k < 9; ++k)
				addSlot(new Slot(inv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + offset));
		for(int j = 0; j < 9; ++j)
			addSlot(new Slot(inv, j, 8 + j * 18, 161 + offset));
	}

	@Override
	public void slotsChanged(net.minecraft.world.Container container) {
		if(container != grid) return;
		ItemStack[] reagents = new ItemStack[5];
		for(int i = 1; i < 6; i++)
			reagents[i - 1] = grid.getItem(i);
		ItemStack parchment = grid.getItem(0).copy();
		parchment.setCount(1);
		currentSpell = book.getItem() instanceof ItemNecronomicon necro ? SpellRegistry.instance().getSpell(necro.getBookType(), parchment, reagents) : null;
		output.setItem(0, currentSpell != null && isUnlocked(currentSpell) ? SpellRegistry.instance().inscribeSpell(currentSpell, parchment) : ItemStack.EMPTY);
		broadcastChanges();
	}

	public boolean isUnlocked(Spell spell) {
		return NecroDataCapability.getCap(user).isUnlocked(spell.getUnlockCondition(), user);
	}

	@Override
	public boolean stillValid(Player player) {
		return player.getItemInHand(hand) == book && book.getItem() instanceof ItemNecronomicon;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		clearContainer(player, grid);
	}

	@Override
	public void clicked(int slot, int button, ClickType type, Player player) {
		// the open book can't be moved
		if(slot >= 0 && slot < slots.size() && getSlot(slot).getItem() == book || type == ClickType.SWAP && player.getInventory().getItem(button) == book)
			return;
		super.clicked(slot, button, type, player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if(!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stack = slot.getItem(), copy = stack.copy();
		if(index <= OUTPUT) {
			if(!moveItemStackTo(stack, OUTPUT + 1, slots.size(), true)) return ItemStack.EMPTY;
			if(index == OUTPUT) slot.onQuickCraft(stack, copy);
		} else if(index < OUTPUT + 28) {
			if(!moveItemStackTo(stack, OUTPUT + 28, slots.size(), false)) return ItemStack.EMPTY;
		} else if(!moveItemStackTo(stack, OUTPUT + 1, OUTPUT + 28, false)) return ItemStack.EMPTY;
		if(stack.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();
		if(stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
		slot.onTake(player, stack);
		return copy;
	}
}
