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

import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.common.handlers.PlagueEventHandler;
import com.shinoow.abyssalcraft.common.util.ArmorUtil;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class PotionCplague extends MobEffect {

	public PotionCplague(int color) {
		super(MobEffectCategory.HARMFUL, color);
	}

	@Override
	public void applyEffectTick(LivingEntity par1EntityLivingBase, int par2){

		if(par1EntityLivingBase.level().isClientSide) return;

		if(par1EntityLivingBase.tickCount % 200 == 0 && par1EntityLivingBase.getRandom().nextFloat() > 0.7F
				&& !par1EntityLivingBase.level().getBiome(par1EntityLivingBase.blockPosition()).is(ACBiomes.purged)) {
			AABB axisalignedbb = par1EntityLivingBase.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
			List<LivingEntity> list = par1EntityLivingBase.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
			for(LivingEntity entity : list)
				if(entity.getRandom().nextBoolean() && par1EntityLivingBase != entity)
					entity.addEffect(PlagueEventHandler.getEffect(par1EntityLivingBase.getEffect(this)));
		}

		if(par1EntityLivingBase instanceof Player player && par1EntityLivingBase.tickCount % 200 == 0)
			NecroDataCapability.getCap(player).triggerMiscUnlock("coralium_plague");

		if(EntityUtil.isEntityCoralium(par1EntityLivingBase)) return;

		if(ArmorUtil.hasHelmetWithResistance(par1EntityLivingBase, AbyssalCraftAPI.coralium)) return;

		if(par1EntityLivingBase.tickCount % 40 >> par2 == 0)
			par1EntityLivingBase.hurt(AbyssalCraftAPI.damage(par1EntityLivingBase.level(), AbyssalCraftAPI.coralium), 2);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier)
	{
		return true;
	}

	@Override
	public List<ItemStack> getCurativeItems()
	{
		List<ItemStack> list = new ArrayList<>();
		list.add(new ItemStack(ACItems.coralium_antidote.get()));
		return list;
	}
}
