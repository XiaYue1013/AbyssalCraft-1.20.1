/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/** Grasp of Cthulhu: holds the target in place (cobweb drag, Slowness XV) and hurts it 1 a second */
public class GraspofCthulhuSpell extends TargetedSpell {

	public GraspofCthulhuSpell() {
		super("graspofcthulhu", 20, Items.COD);
		setScrollType(ScrollType.LESSER);
		setColor(0x2ba2ad);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return true;
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		target.setDeltaMovement(Vec3.ZERO);
		target.hurtMarked = true;
		target.setSprinting(false);
		target.setOnGround(true);
		target.makeStuckInBlock(Blocks.COBWEB.defaultBlockState(), new Vec3(0.25D, 0.05F, 0.25D));
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 14, false, false));
		if(target.tickCount % 20 == 0 && target.getHealth() > 1)
			target.hurt(AbyssalCraftAPI.damage(player.level(), AbyssalCraftAPI.hardcore_attack, player), 1);
	}
}
