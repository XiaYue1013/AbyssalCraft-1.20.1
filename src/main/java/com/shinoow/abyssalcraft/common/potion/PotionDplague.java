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
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;

public class PotionDplague extends MobEffect {

	public PotionDplague(int color) {
		super(MobEffectCategory.HARMFUL, color);
	}

	@Override
	public void applyEffectTick(LivingEntity par1EntityLivingBase, int par2){

		Level world = par1EntityLivingBase.level();
		if(world.isClientSide) return;

		if(par1EntityLivingBase.tickCount % 100 == 0 && par1EntityLivingBase.getRandom().nextFloat() > 0.3F
				&& !world.getBiome(par1EntityLivingBase.blockPosition()).is(ACBiomes.purged)) {
			AABB axisalignedbb = par1EntityLivingBase.getBoundingBox().inflate(3.0D, 3.0D, 3.0D);
			List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
			MobEffectInstance own = par1EntityLivingBase.getEffect(this);
			for(LivingEntity entity : list)
				if(entity.getRandom().nextBoolean() && par1EntityLivingBase != entity)
					if(entity.hasEffect(this) && entity.getEffect(this).getAmplifier() > 0)
						entity.addEffect(PlagueEventHandler.getEffect(own, 1));
					else entity.addEffect(PlagueEventHandler.getEffect(own, entity.hasEffect(this) && par1EntityLivingBase.getRandom().nextInt(10) == 0 ? 1 : 0));
		}

		if(!ACConfig.no_dreadlands_spread)
			if(par2 > 0 || ACConfig.hardcoreMode)
				if(par1EntityLivingBase.tickCount % 100 == 0)
					if(world.dimension() != ACDimensions.DARK_REALM && world.dimension() != ACDimensions.OMOTHOL) {
						List<BlockPos> columns = new ArrayList<>();
						BlockPos center = par1EntityLivingBase.blockPosition();
						for(int x = center.getX() - 1; x <= center.getX() + 1; x++)
							for(int z = center.getZ() - 1; z <= center.getZ() + 1; z++) {
								BlockPos pos = new BlockPos(x, center.getY(), z);
								Holder<Biome> biome = world.getBiome(pos);
								if(!biome.is(ACBiomes.IS_DREADLANDS) && !biome.is(ACBiomes.purged))
									columns.add(pos);
							}
						BiomeUtil.updateBiomes(world, columns, ACBiomes.dreadlands);
					}

		if(par1EntityLivingBase instanceof Player player && par1EntityLivingBase.tickCount % 200 == 0)
			NecroDataCapability.getCap(player).triggerMiscUnlock("dread_plague");

		if(EntityUtil.isEntityDread(par1EntityLivingBase)) return;

		if(ArmorUtil.hasHelmetWithResistance(par1EntityLivingBase, AbyssalCraftAPI.dread)) return;

		if(par1EntityLivingBase.tickCount % 25 >> par2 == 0)
			par1EntityLivingBase.hurt(AbyssalCraftAPI.damage(world, AbyssalCraftAPI.dread), 1);

		if(par1EntityLivingBase instanceof Player player)
			player.causeFoodExhaustion(0.025F * (par2+2));
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
		list.add(new ItemStack(ACItems.dread_antidote.get()));
		return list;
	}
}
