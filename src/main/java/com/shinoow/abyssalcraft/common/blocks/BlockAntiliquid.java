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
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * Liquid Antimatter. Solidifies Liquid Coralium into Coralium Stone, freezes water into Packed Ice, turns lava into
 * Obsidian, gives non-anti mobs a set of nasty effects and disintegrates dropped items (configurable).
 */
public class BlockAntiliquid extends LiquidBlock {

	public BlockAntiliquid(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		super.entityInside(state, level, pos, entity);
		if(level.isClientSide) return;
		if(entity instanceof LivingEntity living && !EntityUtil.isEntityAnti(living)){
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400));
			living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
			living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400));
			living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400));
			living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400));
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.antimatter_potion.get(), 200));
		}
		if(entity instanceof ItemEntity && ACConfig.antiItemDisintegration)
			entity.discard();
	}

	/**
	 * Called from the fluid tick (1.12.2 displaceIfPossible)
	 */
	public static void displaceNeighbours(Level world, BlockPos pos) {
		if(world.isClientSide) return;
		for(Direction dir : new Direction[] {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
			BlockPos target = pos.relative(dir);
			if(!world.isLoaded(target)) continue;
			BlockState state = world.getBlockState(target);
			if(state.is(ACBlocks.liquid_coralium.get()))
				world.setBlockAndUpdate(target, ACBlocks.coralium_stone.get().defaultBlockState());
			else if(state.getFluidState().is(FluidTags.WATER) && state.getBlock() instanceof LiquidBlock)
				world.setBlockAndUpdate(target, Blocks.PACKED_ICE.defaultBlockState());
			else if(state.getFluidState().is(FluidTags.LAVA) && state.getBlock() instanceof LiquidBlock)
				world.setBlockAndUpdate(target, Blocks.OBSIDIAN.defaultBlockState());
		}
	}
}
