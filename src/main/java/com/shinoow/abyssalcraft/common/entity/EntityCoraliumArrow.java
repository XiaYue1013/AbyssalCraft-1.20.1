/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Shot by the Coralium Longbow: a normal arrow (picked up as one) that gives what it hits 5 seconds of Coralium Plague.
 * The 1.12.2 arrow also turned zombies it killed into Depths Ghouls, but checked that before the zombie was flagged dead,
 * so that never happened; it isn't ported (the plague still turns zombies the usual way).
 */
public class EntityCoraliumArrow extends AbstractArrow {

	public EntityCoraliumArrow(EntityType<? extends EntityCoraliumArrow> type, Level level) {
		super(type, level);
	}

	public EntityCoraliumArrow(Level level, LivingEntity shooter) {
		super(ACEntityTypes.CORALIUM_ARROW.get(), shooter, level);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
		if(!EntityUtil.isEntityCoralium(target))
			target.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 100), getEffectSource());
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(Items.ARROW);
	}
}
