/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Ooze Removal: clears the Shoggoth Ooze within 3 blocks */
public class OozeRemovalSpell extends Spell {

	public OozeRemovalSpell() {
		super("oozeremoval", 100F, ACConfig.foodstuff ? ACItems.washcloth.get() : Blocks.SPONGE);
		setRequiresCharging();
		setColor(0x000);
	}

	private static Iterable<BlockPos> area(Player player) {
		BlockPos pos = player.blockPosition();
		return BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3));
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		for(BlockPos p : area(player))
			if(world.getBlockState(p).is(ACBlocks.shoggoth_ooze.get()))
				return true;
		return false;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		for(BlockPos p : area(player))
			if(world.getBlockState(p).is(ACBlocks.shoggoth_ooze.get()))
				world.destroyBlock(p, false);
	}
}
