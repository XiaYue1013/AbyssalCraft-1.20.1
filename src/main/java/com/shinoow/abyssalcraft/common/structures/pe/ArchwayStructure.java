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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * A decorative arch (any walls, stairs and slabs) with a statue standing on a monolith stone below it
 */
public class ArchwayStructure implements IPlaceOfPower {

	private BlockState[][][] data;

	@Override
	public String getIdentifier() {
		return "archway";
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
			return 1;
		return 0;
	}

	@Override
	public void construct(Level world, BlockPos pos) {
		PlaceOfPowerUtil.formBase(world, pos, this);
		PlaceOfPowerUtil.setComponent(world, pos.above(), true, pos);
	}

	@Override
	public void validate(Level world, BlockPos pos) {
		boolean valid = PlaceOfPowerUtil.isMultiblock(world, pos) && isStatue(world, pos.above()) && hasArch(world, pos);
		PlaceOfPowerUtil.setComponent(world, pos.above(), valid, pos);
	}

	@Override
	public boolean canConstruct(Level world, BlockPos pos, Player player) {
		return PlaceOfPowerUtil.isMonolithStone(world.getBlockState(pos)) && isStatue(world, pos.above()) && hasArch(world, pos);
	}

	/** The arch can face either way, and (like in 1.12.2) is checked leniently */
	private static boolean hasArch(Level world, BlockPos pos) {
		for(int i = 0; i < 3; i++)
			if(!is(world, pos.east(2).above(i), WallBlock.class) && !is(world, pos.west(2).above(i), WallBlock.class)
					&& !is(world, pos.north(2).above(i), WallBlock.class) && !is(world, pos.south(2).above(i), WallBlock.class))
				return false;
		BlockPos top = pos.above(3);
		if(!is(world, top.north(2), StairBlock.class) && !is(world, top.north(1), StairBlock.class) && !is(world, top, SlabBlock.class)
				&& !is(world, top.south(1), StairBlock.class) && !is(world, top.south(2), StairBlock.class)
				&& !is(world, top.east(2), StairBlock.class) && !is(world, top.east(1), StairBlock.class)
				&& !is(world, top.west(1), StairBlock.class) && !is(world, top.west(2), StairBlock.class))
			return false;
		BlockPos roof = pos.above(4);
		for(BlockPos p : new BlockPos[] {roof.north(2), roof.north(1), roof, roof.south(1), roof.south(2), roof.east(2), roof.east(1), roof.west(1), roof.west(2)})
			if(is(world, p, SlabBlock.class))
				return true;
		return false;
	}

	private static boolean is(Level world, BlockPos pos, Class<? extends Block> type) {
		return type.isInstance(world.getBlockState(pos).getBlock());
	}

	@Override
	public BlockState[][][] getRenderData() {
		if(data == null) {
			BlockState wall = ACBlocks.darkstone_cobblestone_wall.get().defaultBlockState();
			BlockState stairs = ACBlocks.darkstone_cobblestone_stairs.get().defaultBlockState();
			BlockState slab = ACBlocks.darkstone_cobblestone_slab.get().defaultBlockState();
			data = new BlockState[][][] {
				new BlockState[][] {new BlockState[] {wall}, new BlockState[] {null}, new BlockState[] {ACBlocks.monolith_stone.get().defaultBlockState()},
					new BlockState[] {null}, new BlockState[] {wall}},
				new BlockState[][] {new BlockState[] {wall}, new BlockState[] {null}, new BlockState[] {ACBlocks.cthulhu_statue.get().defaultBlockState()}, new BlockState[] {null},
					new BlockState[] {wall}},
				new BlockState[][] {new BlockState[] {wall}, new BlockState[] {null}, new BlockState[] {null}, new BlockState[] {null},
					new BlockState[] {wall}},
				new BlockState[][] {new BlockState[] {stairs.setValue(StairBlock.FACING, Direction.EAST)},
					new BlockState[] {stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP)},
					new BlockState[] {slab.setValue(SlabBlock.TYPE, SlabType.TOP)},
					new BlockState[] {stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP)},
					new BlockState[] {stairs.setValue(StairBlock.FACING, Direction.WEST)}},
				new BlockState[][] {new BlockState[] {slab}, new BlockState[] {slab}, new BlockState[] {slab}, new BlockState[] {slab}, new BlockState[] {slab}}};
		}
		return data;
	}

	@Override
	public BlockPos getActivationPointForRender() {
		return new BlockPos(2, 0, 0);
	}
}
