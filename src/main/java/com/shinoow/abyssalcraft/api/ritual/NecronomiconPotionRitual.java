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

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A Necronomicon Ritual that gives the performer a potion effect (and a random bad one)
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconPotionRitual extends NecronomiconRitual {

	private Supplier<? extends MobEffect> potion;

	public NecronomiconPotionRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice,
			Supplier<? extends MobEffect> potion, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, offerings);
		this.potion = potion;
	}

	public NecronomiconPotionRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, Supplier<? extends MobEffect> potion,
			Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, potion, offerings);
	}

	public NecronomiconPotionRitual(String unlocalizedName, int bookType, float requiredEnergy, Supplier<? extends MobEffect> potion, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, potion, offerings);
	}

	/**
	 * Getter for the potion effect
	 */
	public MobEffect getPotionEffect(){
		return potion.get();
	}

	private MobEffect getBadEffect(RandomSource rand){
		switch(rand.nextInt(5)){
		case 0:
			return MobEffects.BLINDNESS;
		case 1:
			return MobEffects.CONFUSION;
		case 2:
			return MobEffects.DIG_SLOWDOWN;
		case 3:
			return MobEffects.HUNGER;
		default:
			return MobEffects.WEAKNESS;
		}
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		player.addEffect(new MobEffectInstance(getPotionEffect(), 2400));
		player.addEffect(new MobEffectInstance(getBadEffect(world.random), 600));
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
