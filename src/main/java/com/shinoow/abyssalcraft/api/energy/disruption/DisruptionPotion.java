/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.api.energy.disruption;

import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * A Disruption Entry that applies a potion effect to all nearby living entities
 * @author shinoow
 *
 * @since 1.5
 */
public class DisruptionPotion extends DisruptionEntry {

	private Supplier<? extends MobEffect> potion;

	public DisruptionPotion(String unlocalizedName, DeityType deity, Supplier<? extends MobEffect> potion) {
		super(unlocalizedName, deity);
		this.potion = potion;
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(!world.isClientSide){
			MobEffect effect = potion.get();
			List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(16));
			for(LivingEntity entity : entities)
				if(!EntityUtil.isEntityImmune(entity, effect))
					entity.addEffect(new MobEffectInstance(effect, 600));
		}
	}
}
