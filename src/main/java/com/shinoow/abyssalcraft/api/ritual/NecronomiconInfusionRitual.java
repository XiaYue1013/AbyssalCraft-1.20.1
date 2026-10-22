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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A Necronomicon Ritual that infuses the item placed on the altar into something else
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconInfusionRitual extends NecronomiconCreationRitual {

	private String[] tags;

	/**
	 * @param item The resulting item
	 * @param sacrifice The item that has to be placed on the altar
	 */
	public NecronomiconInfusionRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy,
			boolean requiresSacrifice, ItemStack item, Object sacrifice, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, item, offerings);
		this.sacrifice = sacrifice;
		setRitualParticle(EnumRitualParticle.ITEM_SMOKE_COMBO);
	}

	public NecronomiconInfusionRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy,
			ItemStack item, Object sacrifice, Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, item, sacrifice, offerings);
	}

	public NecronomiconInfusionRitual(String unlocalizedName, int bookType, float requiredEnergy, ItemStack item, Object sacrifice, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, item, sacrifice, offerings);
	}

	/**
	 * NBT tags that should be transferred from the sacrifice to the resulting item
	 */
	public NecronomiconInfusionRitual setTags(String... strings){
		tags = strings;
		return this;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return APIUtils.areObjectsEqual(getAltarItem(world, pos), sacrifice, false);
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		if(canCompleteRitual(world, pos, player)){
			CompoundTag data = new CompoundTag();
			if(tags != null && tags.length > 0){
				ItemStack stack = getAltarItem(world, pos);
				if(stack.hasTag())
					for(String s : tags)
						if(stack.getTag().contains(s))
							data.put(s, stack.getTag().get(s).copy());
			}
			super.completeRitualServer(world, pos, player);
			if(!data.isEmpty()){
				ItemStack stack = getAltarItem(world, pos).copy();
				for(String e : tags)
					if(data.contains(e))
						stack.getOrCreateTag().put(e, data.get(e));
				setAltarItem(world, pos, stack);
			}
		}
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
