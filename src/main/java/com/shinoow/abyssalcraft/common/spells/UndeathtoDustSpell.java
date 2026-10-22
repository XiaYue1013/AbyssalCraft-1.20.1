/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Undeath to Dust: an undead mob (not a boss) just disappears */
public class UndeathtoDustSpell extends TargetedSpell {

	public UndeathtoDustSpell() {
		super("undeathtodust", 1000F, Items.BONE);
		setScrollType(ScrollType.MODERATE);
		setRequiresCharging();
		setColor(0x1a1b1c);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return target.getMobType() == MobType.UNDEAD;
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		if(!target.canChangeDimensions() || target instanceof Player) return;
		target.discard();
		((ServerLevel) player.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, target.getX(), target.getY() + 2.0D, target.getZ(), 0, 0, 0, 0, 1.0);
	}
}
