/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Hide from the Eye: 5 minutes of Invisibility for the looked at mob, or the caster */
public class InvisibilitySpell extends TargetedSpell {

	public InvisibilitySpell() {
		super("invisibility", 500F, Items.GLASS_BOTTLE);
		setRequiresCharging();
		setColor(0xaeb1b7);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return !target.hasEffect(MobEffects.INVISIBILITY);
	}

	@Nullable
	private LivingEntity subject(Player player) {
		LivingEntity target = target(player);
		return target != null ? target : player.hasEffect(MobEffects.INVISIBILITY) ? null : player;
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		return subject(player) != null;
	}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		LivingEntity subject = subject(player);
		if(subject != null)
			affect(player, subject);
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 6000, 0, false, false));
	}
}
