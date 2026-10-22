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
 *     1.20.1 port - the death conversions moved to PlagueEventHandler (they ran when the dead entity was removed)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.potion;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class PotionAntimatter extends MobEffect {

	public PotionAntimatter(int color) {
		super(MobEffectCategory.HARMFUL, color);
	}

	@Override
	public void applyEffectTick(LivingEntity par1EntityLivingBase, int par2){

		if(par1EntityLivingBase.level().isClientSide) return;

		if(EntityUtil.isEntityAnti(par1EntityLivingBase)) return;

		if(par1EntityLivingBase.getItemBySlot(EquipmentSlot.CHEST).is(ACItems.ethaxium_chestplate.get()))
			return;

		par1EntityLivingBase.hurt(AbyssalCraftAPI.damage(par1EntityLivingBase.level(), AbyssalCraftAPI.antimatter), 5);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}
}
