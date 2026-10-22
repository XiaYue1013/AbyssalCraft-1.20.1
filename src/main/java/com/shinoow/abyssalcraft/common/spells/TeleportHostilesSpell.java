/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Sacrificial Interdiction: kills a baby animal to send every monster within 50 blocks at least 50 blocks away
 * (random spot up to 48 blocks off on each axis, 20 tries, like an enderman)
 */
public class TeleportHostilesSpell extends TargetedSpell {

	public TeleportHostilesSpell() {
		super("teleporthostiles", 10000F, Items.SUGAR);
		setScrollType(ScrollType.GREATER);
		setRequiresCharging();
		setColor(0x31227c);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return target.getType().getCategory() == MobCategory.CREATURE && target.isBaby();
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		if(target.hurt(AbyssalCraftAPI.damage(player.level(), AbyssalCraftAPI.hardcore_attack, player), 200F))
			for(LivingEntity mob : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50)))
				if(mob.getType().getCategory() == MobCategory.MONSTER)
					teleportAway(mob, player);
	}

	public static boolean teleportAway(LivingEntity entity, Player player) {
		var random = entity.getRandom();
		for(int tries = 0; tries < 20; tries++) {
			double range = tries == 0 ? 64.0D : 96.0D;
			double x = entity.getX() + (random.nextDouble() - 0.5D) * range, y = entity.getY() + (random.nextInt(64) - 32), z = entity.getZ() + (random.nextDouble() - 0.5D) * range;
			if(player.distanceToSqr(x, y, z) < 2500) continue;
			var event = ForgeEventFactory.onEnderTeleport(entity, x, y, z);
			if(event.isCanceled()) continue;
			double ox = entity.getX(), oy = entity.getY(), oz = entity.getZ();
			if(entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
				entity.level().playSound(null, ox, oy, oz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
				entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
				return true;
			}
		}
		return false;
	}
}
