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
 *     1.20.1 port - the BlockAbyssPortal/BlockDreadlandsPortal/BlockOmotholPortal "Size" classes merged into one
 *     frame detector (same rules: 2-21 wide, 3-21 high, frame of a single stone type, corners not needed)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A portal frame of an AbyssalCraft gateway
 */
public class ACPortalShape {

	public static final int MIN_WIDTH = 2, MAX_WIDTH = 21, MIN_HEIGHT = 3, MAX_HEIGHT = 21;

	private final LevelAccessor level;
	private final BlockACPortal portal;
	private final Direction.Axis axis;
	private final Direction rightDir;
	private int numPortalBlocks;
	@Nullable
	private BlockPos bottomLeft;
	private int height;
	private final int width;

	/**
	 * Finds an unlit frame around the position (either axis)
	 */
	public static Optional<ACPortalShape> findEmptyPortalShape(LevelAccessor level, BlockPos pos, BlockACPortal portal, Direction.Axis axis) {
		return findPortalShape(level, pos, portal, shape -> shape.isValid() && shape.numPortalBlocks == 0, axis);
	}

	public static Optional<ACPortalShape> findPortalShape(LevelAccessor level, BlockPos pos, BlockACPortal portal, Predicate<ACPortalShape> predicate, Direction.Axis axis) {
		Optional<ACPortalShape> optional = Optional.of(new ACPortalShape(level, pos, portal, axis)).filter(predicate);
		if(optional.isPresent())
			return optional;
		Direction.Axis other = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		return Optional.of(new ACPortalShape(level, pos, portal, other)).filter(predicate);
	}

	public ACPortalShape(LevelAccessor level, BlockPos pos, BlockACPortal portal, Direction.Axis axis) {
		this.level = level;
		this.portal = portal;
		this.axis = axis;
		rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		bottomLeft = calculateBottomLeft(pos);
		if(bottomLeft == null) {
			bottomLeft = pos;
			width = 1;
			height = 1;
		} else {
			width = calculateWidth();
			if(width > 0)
				height = calculateHeight();
		}
	}

	private boolean isFrame(BlockState state) {
		return state.is(portal.getFrameBlock());
	}

	/** Air, the gateway's own fire and the gateway itself can be inside a frame */
	private boolean isEmpty(BlockState state) {
		return state.isAir() || state.is(portal.getFireBlock()) || state.is(portal);
	}

	@Nullable
	private BlockPos calculateBottomLeft(BlockPos pos) {
		for(int i = Math.max(level.getMinBuildHeight(), pos.getY() - MAX_HEIGHT); pos.getY() > i && isEmpty(level.getBlockState(pos.below())); pos = pos.below());

		Direction direction = rightDir.getOpposite();
		int j = getDistanceUntilEdgeAboveFrame(pos, direction) - 1;
		return j < 0 ? null : pos.relative(direction, j);
	}

	private int calculateWidth() {
		int i = getDistanceUntilEdgeAboveFrame(bottomLeft, rightDir);
		return i >= MIN_WIDTH && i <= MAX_WIDTH ? i : 0;
	}

	private int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction) {
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for(int i = 0; i <= MAX_WIDTH; ++i) {
			mutable.set(pos).move(direction, i);
			BlockState state = level.getBlockState(mutable);
			if(!isEmpty(state)) {
				if(isFrame(state))
					return i;
				break;
			}

			if(!isFrame(level.getBlockState(mutable.move(Direction.DOWN))))
				break;
		}

		return 0;
	}

	private int calculateHeight() {
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int i = getDistanceUntilTop(mutable);
		return i >= MIN_HEIGHT && i <= MAX_HEIGHT && hasTopFrame(mutable, i) ? i : 0;
	}

	private boolean hasTopFrame(BlockPos.MutableBlockPos mutable, int height) {
		for(int i = 0; i < width; ++i)
			if(!isFrame(level.getBlockState(mutable.set(bottomLeft).move(Direction.UP, height).move(rightDir, i))))
				return false;

		return true;
	}

	private int getDistanceUntilTop(BlockPos.MutableBlockPos mutable) {
		for(int i = 0; i < MAX_HEIGHT; ++i) {
			if(!isFrame(level.getBlockState(mutable.set(bottomLeft).move(Direction.UP, i).move(rightDir, -1))))
				return i;

			if(!isFrame(level.getBlockState(mutable.set(bottomLeft).move(Direction.UP, i).move(rightDir, width))))
				return i;

			for(int j = 0; j < width; ++j) {
				BlockState state = level.getBlockState(mutable.set(bottomLeft).move(Direction.UP, i).move(rightDir, j));
				if(!isEmpty(state))
					return i;

				if(state.is(portal))
					++numPortalBlocks;
			}
		}

		return MAX_HEIGHT;
	}

	public boolean isValid() {
		return bottomLeft != null && width >= MIN_WIDTH && width <= MAX_WIDTH && height >= MIN_HEIGHT && height <= MAX_HEIGHT;
	}

	/** Fills the frame with portal blocks */
	public void createPortalBlocks() {
		BlockState state = portal.defaultBlockState().setValue(BlockACPortal.AXIS, axis);
		BlockPos.betweenClosed(bottomLeft, bottomLeft.relative(Direction.UP, height - 1).relative(rightDir, width - 1))
		.forEach(pos -> level.setBlock(pos, state, 18));
	}

	/** True if the frame is intact and completely filled with portal blocks */
	public boolean isComplete() {
		return isValid() && numPortalBlocks == width * height;
	}
}
