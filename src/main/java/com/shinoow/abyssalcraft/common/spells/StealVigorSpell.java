/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Steal Vigor: one minute of Weakness/Slowness/Mining Fatigue II on the target, Strength/Speed/Haste II on the caster */
public class StealVigorSpell extends TargetedSpell {

	public StealVigorSpell() {
		super("stealvigor", 500F, Items.BEEF);
		setRequiresCharging();
		setColor(0xdb3d3d);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return true;
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1));
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1));
		target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, 1));
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
		player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1));
	}
}
