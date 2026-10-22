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
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import com.shinoow.abyssalcraft.common.inventory.ContainerCrystalBag;
import com.shinoow.abyssalcraft.common.inventory.InventoryCrystalBag;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * Crystal Bag, stores crystals. Small/Medium/Large/Huge hold 18/36/54/72 stacks
 */
public class ItemCrystalBag extends ItemACBasic {

	private final int size;

	public ItemCrystalBag(int size) {
		super(new Properties().stacksTo(1));
		this.size = size;
	}

	/**
	 * Inventory size of the bag (stored in NBT so upgraded bags keep their contents)
	 */
	public static int getInventorySize(ItemStack stack) {
		setInventorySize(stack);
		return stack.getTag().getInt("InvSize");
	}

	public static void setInventorySize(ItemStack stack){
		if(!stack.getOrCreateTag().contains("InvSize") && stack.getItem() instanceof ItemCrystalBag bag)
			stack.getTag().putInt("InvSize", bag.size);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		setInventorySize(stack);
		if (!world.isClientSide && !player.isShiftKeyDown())
			NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((id, inv, p) ->
			new ContainerCrystalBag(id, inv, new InventoryCrystalBag(stack, hand)), stack.getHoverName()),
					buf -> {
						buf.writeEnum(hand);
						buf.writeVarInt(getInventorySize(stack));
					});
		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag) {
		l.add(Component.translatable("tooltip.crystalbag"));
	}
}
