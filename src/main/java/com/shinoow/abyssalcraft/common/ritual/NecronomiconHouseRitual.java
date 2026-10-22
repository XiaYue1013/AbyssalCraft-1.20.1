/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconInfusionRitual;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.util.IHiddenRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Turns a wooden door into a House; only the author can perform it (hidden from the Necronomicon) */
public class NecronomiconHouseRitual extends NecronomiconInfusionRitual implements IHiddenRitual {

	public NecronomiconHouseRitual() {
		super("house", 0, 0, new ItemStack(BlockHandler.HOUSE.get()), new ItemStack[] {new ItemStack(Items.OAK_DOOR), new ItemStack(Items.SPRUCE_DOOR),
				new ItemStack(Items.BIRCH_DOOR), new ItemStack(Items.JUNGLE_DOOR), new ItemStack(Items.ACACIA_DOOR), new ItemStack(Items.DARK_OAK_DOOR)},
				ItemTags.PLANKS, ItemTags.WOODEN_STAIRS, ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.WOODEN_STAIRS);
		setRitualParticle(EnumRitualParticle.SPRINKLER);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return EntityUtil.isPlayerCoralium(player) && super.canCompleteRitual(world, pos, player);
	}
}
