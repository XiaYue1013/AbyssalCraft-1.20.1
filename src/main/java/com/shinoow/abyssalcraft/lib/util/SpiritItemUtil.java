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
package com.shinoow.abyssalcraft.lib.util;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.transfer.caps.ItemTransferCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Utility class for Spirit Item-related stuff
 * @author shinoow
 *
 */
public class SpiritItemUtil {

	public static boolean validPos(BlockPos pos, Level world) {
		return validPos(pos, world, Direction.DOWN);
	}

	public static boolean validPos(BlockPos pos, Level world, @Nullable Direction face) {
		if(world.isLoaded(pos))
			return validTE(world.getBlockEntity(pos), face);
		return false;
	}

	public static boolean validTE(BlockEntity te) {
		return validTE(te, Direction.DOWN);
	}

	public static boolean validTE(@Nullable BlockEntity te, @Nullable Direction face) {
		if(te == null) return false;
		return ItemTransferCapability.getCap(te) != null && hasInventory(te, face);
	}

	@Nullable
	public static IItemHandler getInventory(BlockEntity te, @Nullable Direction face) {
		IItemHandler handler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, face).orElse(null);
		if(handler != null)
			return handler;
		else if(te instanceof WorldlyContainer sided && face != null)
			return new SidedInvWrapper(sided, face);
		else if(te instanceof Container container)
			return new InvWrapper(container);
		return null;
	}

	public static boolean hasInventory(BlockEntity te) {
		return hasInventory(te, Direction.DOWN);
	}

	public static boolean hasInventory(BlockEntity te, @Nullable Direction face) {
		return getInventory(te, face) != null;
	}
}
