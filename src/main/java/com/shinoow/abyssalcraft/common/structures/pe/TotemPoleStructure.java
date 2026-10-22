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
package com.shinoow.abyssalcraft.common.structures.pe;

import static com.shinoow.abyssalcraft.common.structures.pe.PlaceOfPowerUtil.isStatue;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A monolith stone with three statues stacked on top of it
 */
public class TotemPoleStructure implements IPlaceOfPower {

	private BlockState[][][] data;

	@Override
	public String getIdentifier() {
		return "totempole";
	}

	@Override
	public int getBookType() {
		return 0;
	}

	@Override
	public IUnlockCondition getUnlockCondition() {
		return new DefaultCondition();
	}

	@Override
	public float getAmplifier(AmplifierType type) {
		if(type == AmplifierType.RANGE)
			return 3;
		return 0;
	}

	@Override
	public void construct(Level world, BlockPos pos) {
		PlaceOfPowerUtil.formBase(world, pos, this);
		for(int i = 1; i < 4; i++)
			PlaceOfPowerUtil.setComponent(world, pos.above(i), true, pos);
	}

	@Override
	public void validate(Level world, BlockPos pos) {
		boolean valid = PlaceOfPowerUtil.isMultiblock(world, pos) && hasStatues(world, pos);
		for(int i = 1; i < 4; i++)
			PlaceOfPowerUtil.setComponent(world, pos.above(i), valid, pos);
	}

	@Override
	public boolean canConstruct(Level world, BlockPos pos, Player player) {
		return PlaceOfPowerUtil.isMonolithStone(world.getBlockState(pos)) && hasStatues(world, pos);
	}

	private static boolean hasStatues(Level world, BlockPos pos) {
		return isStatue(world, pos.above()) && isStatue(world, pos.above(2)) && isStatue(world, pos.above(3));
	}

	@Override
	public BlockState[][][] getRenderData() {
		if(data == null)
			data = new BlockState[][][] {
				new BlockState[][] {new BlockState[] {ACBlocks.monolith_stone.get().defaultBlockState()}},
				new BlockState[][] {new BlockState[] {ACBlocks.yog_sothoth_statue.get().defaultBlockState()}},
				new BlockState[][] {new BlockState[] {ACBlocks.azathoth_statue.get().defaultBlockState()}},
				new BlockState[][] {new BlockState[] {ACBlocks.nyarlathotep_statue.get().defaultBlockState()}}};
		return data;
	}

	@Override
	public BlockPos getActivationPointForRender() {
		return new BlockPos(0, 0, 0);
	}
}
