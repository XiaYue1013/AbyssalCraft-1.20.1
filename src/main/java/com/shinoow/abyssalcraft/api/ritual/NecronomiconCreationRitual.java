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

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.APIUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A Necronomicon Ritual that creates an Item (placed on the altar)
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconCreationRitual extends NecronomiconRitual {

	private ItemStack item;

	public NecronomiconCreationRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, ItemStack item, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, offerings);
		this.item = item;
		if(item.getCount() > 1)
			item.setCount(1);
	}

	public NecronomiconCreationRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, ItemStack item, Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, item, offerings);
	}

	public NecronomiconCreationRitual(String unlocalizedName, int bookType, float requiredEnergy, ItemStack item, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, item, offerings);
	}

	/**
	 * Getter for the item that will be created
	 */
	public ItemStack getItem(){
		return item;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return true;
	}

	/**
	 * Strikes a lightning bolt at the altar (a real one, like in 1.12.2)
	 */
	protected static void strikeLightning(Level world, BlockPos pos) {
		if(world instanceof ServerLevel) {
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
			if(bolt != null) {
				bolt.moveTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D);
				world.addFreshEntity(bolt);
			}
		}
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		strikeLightning(world, pos);
		ItemStack stack = getAltarItem(world, pos);
		if(!APIUtils.areStacksEqual(stack, item, true))
			setAltarItem(world, pos, item.copy());
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
