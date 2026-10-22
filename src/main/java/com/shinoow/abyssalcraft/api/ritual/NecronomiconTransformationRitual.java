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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.block.ISingletonInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A Necronomicon Ritual that transforms all 8 offerings into something else (placed back on the pedestals)
 * @author shinoow
 *
 * @since 1.12.0
 */
public class NecronomiconTransformationRitual extends NecronomiconRitual {

	private ItemStack output, input;

	private static final List<BlockPos> PEDESTAL_POSITIONS = Arrays.asList(
			new BlockPos(-3, 0, 0), new BlockPos(0, 0, -3),
			new BlockPos(3, 0, 0), new BlockPos(0, 0, 3),
			new BlockPos(-2, 0, 2), new BlockPos(-2, 0, -2),
			new BlockPos(2, 0, 2), new BlockPos(2, 0, -2)
			);

	public NecronomiconTransformationRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, ItemStack input, ItemStack output) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, APIUtils.makeArrayOf(input, 8));
		this.input = input;
		this.output = output;
		setRitualParticle(EnumRitualParticle.PE_STREAM);
	}

	public NecronomiconTransformationRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, ItemStack input, ItemStack output) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, input, output);
	}

	public NecronomiconTransformationRitual(String unlocalizedName, int bookType, float requiredEnergy, ItemStack input, ItemStack output) {
		this(unlocalizedName, bookType, null, requiredEnergy, input, output);
	}

	/**
	 * Used for display (shows the input and output on every pedestal)
	 */
	public Object[] getCombinedContent() {
		return APIUtils.makeArrayOf(new ItemStack[] {input, output}, 8);
	}

	public ItemStack getInput() {
		return input;
	}

	public ItemStack getOutput() {
		return output;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		NecronomiconCreationRitual.strikeLightning(world, pos);
		// Takes each pedestal and places the output Item on them
		for(BlockPos pos1 : PEDESTAL_POSITIONS) {
			BlockPos pedPos = pos.offset(pos1);
			BlockEntity pedestal = world.getBlockEntity(pedPos);
			if(pedestal instanceof ISingletonInventory) {
				((ISingletonInventory) pedestal).setItem(output.copy());
				world.sendBlockUpdated(pedPos, world.getBlockState(pedPos), world.getBlockState(pedPos), 2);
			}
		}
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
