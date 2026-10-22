/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (the spells, MobSpellMessage); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellUtils;
import com.shinoow.abyssalcraft.lib.util.TargetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A spell on the mob the caster looks at (15 blocks). 1.12.2 found it on the client and sent MobSpellMessage; the
 * server never saw the spell as castable, so it never took the PE. Here both sides look for the target, the server
 * applies the effect and takes the PE.
 */
public abstract class TargetedSpell extends Spell {

	public static final double RANGE = 15;

	protected TargetedSpell(String name, float energy, Object... reagents) {
		super(name, energy, reagents);
	}

	@Nullable
	protected LivingEntity target(Player player) {
		return TargetUtil.lookedAt(player, RANGE) instanceof LivingEntity target && target.isAlive() && SpellUtils.canPlayerHurt(player, target)
				&& isValidTarget(player, target) ? target : null;
	}

	protected abstract boolean isValidTarget(Player player, LivingEntity target);

	protected abstract void affect(Player player, LivingEntity target);

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		return target(player) != null;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		LivingEntity target = target(player);
		if(target != null)
			affect(player, target);
	}
}
