/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Life Drain: 5 damage through armor to the target, 5 health to the caster, along a line of flames */
public class LifeDrainSpell extends TargetedSpell {

	public LifeDrainSpell() {
		super("lifedrain", 100, Items.APPLE);
		setRequiresCharging();
		setColor(0xa00404);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return true;
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		if(!target.hurt(AbyssalCraftAPI.damage(player.level(), AbyssalCraftAPI.hardcore_attack, player), 5)) return;
		player.heal(5);
		Vec3 from = Vec3.atCenterOf(player.blockPosition().above()), to = Vec3.atCenterOf(target.blockPosition());
		Vec3 dir = to.subtract(from).normalize();
		double distance = from.distanceTo(to);
		for(int i = 0; i < distance * 15; i++) {
			Vec3 p = from.add(dir.scale(i / 15D));
			((ServerLevel) player.level()).sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 0, dir.x * .1, .15, dir.z * .1, 1.0);
		}
	}
}
