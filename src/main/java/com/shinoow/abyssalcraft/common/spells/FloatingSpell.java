/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.spell.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Floating (Abyssalnomicon tier): every cast lifts the caster a little and resets the fall */
public class FloatingSpell extends Spell {

	public FloatingSpell() {
		super("floating", 3, 15F, new Object[] {Items.FEATHER, Items.FEATHER});
		setColor(0xFFFFFF);
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {
		player.setDeltaMovement(player.getDeltaMovement().x, 0.119, player.getDeltaMovement().z);
	}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		player.fallDistance = 0;
		player.setDeltaMovement(player.getDeltaMovement().x, 0.119, player.getDeltaMovement().z);
	}
}
