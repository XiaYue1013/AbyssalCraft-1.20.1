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
 *     1.20.1 port - validation updates the statues around the base (1.12.2 looked two blocks below it)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.structures.pe;

import static com.shinoow.abyssalcraft.common.structures.pe.PlaceOfPowerUtil.isMonolithStone;
import static com.shinoow.abyssalcraft.common.structures.pe.PlaceOfPowerUtil.isStatue;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A 3x3 monolith stone shrine with pillars in the corners and four statues around its top
 */
public class BasicStructure implements IPlaceOfPower {

	private BlockState[][][] data;

	@Override
	public String getIdentifier() {
		return "basic";
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
			return 2;
		return 0;
	}

	@Override
	public void construct(Level world, BlockPos pos) {
		PlaceOfPowerUtil.formBase(world, pos, this);
		for(Direction dir : Direction.Plane.HORIZONTAL)
			PlaceOfPowerUtil.setComponent(world, pos.relative(dir), true, pos);
	}

	@Override
	public void validate(Level world, BlockPos pos) {
		boolean valid = PlaceOfPowerUtil.isMultiblock(world, pos) && isFormed(world, pos);
		for(Direction dir : Direction.Plane.HORIZONTAL)
			PlaceOfPowerUtil.setComponent(world, pos.relative(dir), valid, pos);
	}

	@Override
	public boolean canConstruct(Level world, BlockPos pos, Player player) {
		return isMonolithStone(world.getBlockState(pos)) && isFormed(world, pos);
	}

	/** Checks everything except the activation point itself */
	private static boolean isFormed(Level world, BlockPos pos) {
		if(isStatue(world, pos.north()) && isStatue(world, pos.south()) && isStatue(world, pos.east()) && isStatue(world, pos.west())) {
			BlockPos mid = pos.below();
			if(isMonolithStone(world.getBlockState(mid)) && isMonolithStone(world.getBlockState(mid.north()))
					&& isMonolithStone(world.getBlockState(mid.south()))
					&& isMonolithStone(world.getBlockState(mid.east()))
					&& isMonolithStone(world.getBlockState(mid.west()))
					&& isPillar(world, mid.east().south())
					&& isPillar(world, mid.east().north())
					&& isPillar(world, mid.west().north())
					&& isPillar(world, mid.west().south())) {
				BlockPos bottom = mid.below();
				for(int i = -1; i < 2; i++)
					for(int j = -1; j < 2; j++)
						if(!isMonolithStone(world.getBlockState(bottom.offset(i, 0, j))))
							return false;
				return true;
			}
		}
		return false;
	}

	private static boolean isPillar(Level world, BlockPos pos) {
		return world.getBlockState(pos).is(ACBlocks.monolith_pillar.get());
	}

	@Override
	public BlockState[][][] getRenderData() {
		if(data == null) {
			BlockState monolith_stone = ACBlocks.monolith_stone.get().defaultBlockState();
			BlockState monolith_pillar = ACBlocks.monolith_pillar.get().defaultBlockState();
			BlockState statue = ACBlocks.cthulhu_statue.get().defaultBlockState();
			BlockState[][] one = {new BlockState[]{monolith_stone, monolith_stone, monolith_stone}, new BlockState[]{monolith_stone, monolith_stone, monolith_stone},
					new BlockState[]{monolith_stone, monolith_stone, monolith_stone}};
			BlockState[][] two = {new BlockState[]{monolith_pillar, monolith_stone, monolith_pillar}, new BlockState[]{monolith_stone, monolith_stone, monolith_stone},
					new BlockState[]{monolith_pillar, monolith_stone, monolith_pillar}};
			BlockState[][] three = {new BlockState[]{null, statue.setValue(BlockStatue.FACING, Direction.WEST), null},
					new BlockState[]{statue, monolith_stone, statue.setValue(BlockStatue.FACING, Direction.SOUTH)},
					new BlockState[]{null, statue.setValue(BlockStatue.FACING, Direction.EAST), null}};
			data = new BlockState[][][] {one, two, three};
		}
		return data;
	}

	@Override
	public BlockPos getActivationPointForRender() {
		return new BlockPos(1, 2, 1);
	}
}
