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
package com.shinoow.abyssalcraft.lib.util.blocks;

import com.shinoow.abyssalcraft.api.block.ISingletonInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Utility class for common handling code used along with<br>
 * {@link ISingletonInventory} implementations.
 * @author shinoow
 *
 */
public class SingletonInventoryUtil {

	/**
	 * Call this in the block's use method to handle
	 * placing/removing something on/from the block (server side)
	 * @param world Current World
	 * @param pos Current position
	 * @param player Interacting Player
	 * @param heldItem Current held item
	 * @return True if successful, otherwise false
	 */
	public static boolean handleBlockActivation(Level world, BlockPos pos, Player player, ItemStack heldItem){
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile instanceof ISingletonInventory inv)
			if(!inv.getItem().isEmpty()){
				if(player.getInventory().add(inv.getItem().copy())){
					inv.setItem(ItemStack.EMPTY);
					playSound(world, pos);
					return true;
				}
			} else if(!heldItem.isEmpty()){
				ItemStack newItem = heldItem.copy();
				newItem.setCount(1);
				inv.setItem(newItem);
				if(!player.getAbilities().instabuild)
					heldItem.shrink(1);
				playSound(world, pos);
				return true;
			}
		return false;
	}

	private static void playSound(Level world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, world.random.nextFloat() - world.random.nextFloat() * 0.2F + 1);
	}

	/**
	 * Drops the item held by the block entity at the position (call when the block is removed)
	 */
	public static void dropItem(Level world, BlockPos pos) {
		if(world.getBlockEntity(pos) instanceof ISingletonInventory inv && !inv.getItem().isEmpty()) {
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inv.getItem());
			inv.setItem(ItemStack.EMPTY);
		}
	}
}
