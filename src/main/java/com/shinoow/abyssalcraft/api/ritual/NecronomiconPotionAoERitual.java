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
package com.shinoow.abyssalcraft.api.ritual;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.entity.EntityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * A Necronomicon Ritual that applies a potion effect to every living entity nearby (except the performer)
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconPotionAoERitual extends NecronomiconRitual {

	private Supplier<? extends MobEffect> potion;

	public NecronomiconPotionAoERitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, Supplier<? extends MobEffect> potion, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, offerings);
		this.potion = potion;
	}

	public NecronomiconPotionAoERitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, Supplier<? extends MobEffect> potion, Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, potion, offerings);
	}

	public NecronomiconPotionAoERitual(String unlocalizedName, int bookType, float requiredEnergy, Supplier<? extends MobEffect> potion, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, potion, offerings);
	}

	/**
	 * Getter for the potion effect
	 */
	public MobEffect getPotionEffect(){
		return potion.get();
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		MobEffect effect = getPotionEffect();
		for(LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(16, 3, 16),
				e -> e != player && e.isAlive() && !EntityUtil.isEntityImmune(e, effect)))
			entity.addEffect(new MobEffectInstance(effect, 400));
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
