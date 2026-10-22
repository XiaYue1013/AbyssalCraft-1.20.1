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
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.IAmplifierCharm;
import com.shinoow.abyssalcraft.api.energy.IEnergyManipulator;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Ritual Charms. Used on a PE manipulator (statue) to activate an amplifier; an empty charm deactivates it
 */
public class ItemCharm extends ItemACBasic implements IAmplifierCharm {

	@Nullable
	private final DeityType deity;
	@Nullable
	private final AmplifierType amplifier;

	public ItemCharm(@Nullable AmplifierType amplifier, @Nullable DeityType deity) {
		super();
		this.amplifier = amplifier;
		this.deity = deity;
	}

	@Override
	public AmplifierType getAmplifier(ItemStack stack) {
		return amplifier;
	}

	@Override
	public DeityType getDeity(ItemStack stack) {
		return deity;
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag){
		l.add(Component.translatable("ac.text.amplifier").append(": " + EnergyEnum.getAmplifierName(getAmplifier(is))));
		l.add(Component.translatable("ac.text.deity").append(": " + EnergyEnum.getDeityName(getDeity(is))));
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		Level world = context.getLevel();
		ItemStack stack = context.getItemInHand();
		BlockEntity tile = world.getBlockEntity(context.getClickedPos());
		if(tile instanceof IEnergyManipulator manipulator){
			if(!world.isClientSide) {
				boolean success = false;
				if(!manipulator.isActive() && getAmplifier(stack) != null) {
					manipulator.setActive(getAmplifier(stack), getDeity(stack));
					stack.shrink(1);
					success = true;
				} else if(manipulator.isActive() && getAmplifier(stack) == null) {
					manipulator.setActiveAmplifier(null);
					manipulator.setActiveDeity(null);
					success = true;
				}
				if(success) {
					tile.setChanged();
					world.playSound(null, context.getClickedPos(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, world.random.nextFloat() - world.random.nextFloat() * 0.2F + 1);
				}
			}
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
