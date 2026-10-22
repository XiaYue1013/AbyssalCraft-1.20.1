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
 *     1.20.1 port - rewritten as an ITeleporter. Linked gateways are found through their point of interest (saved
 *     with the world, so links survive restarts) instead of a 257x257 block scan and an in-memory cache. Gateways are
 *     created with the 1.12.2 shape (2x3 opening in a frame of the gateway's stone), and arrival positions are made
 *     collision free the same way vanilla nether portals do it.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public class TeleporterAC implements ITeleporter {

	private static final int SEARCH_RADIUS = 128, CREATE_RADIUS = 16;

	private final ServerLevel level;
	private final BlockACPortal portal;
	private final BlockState frame;
	private final BlockPos entryPos;

	/**
	 * @param level Destination
	 * @param portal Gateway the entity entered
	 * @param entryPos Position of the gateway block the entity entered
	 */
	public TeleporterAC(ServerLevel level, BlockACPortal portal, BlockPos entryPos) {
		this.level = level;
		this.portal = portal;
		frame = portal.getFrameBlock().defaultBlockState();
		this.entryPos = entryPos;
	}

	/**
	 * Sends an entity through a gateway. Nothing happens if the destination dimension doesn't exist
	 * (the entity stays where it is instead of being sent somewhere unsafe).
	 * @return The entity in the new dimension, or null if it didn't travel
	 */
	@Nullable
	public static Entity changeDimension(Entity entity, ResourceKey<Level> dimension, BlockACPortal portal, BlockPos entryPos) {
		MinecraftServer server = entity.getServer();
		if(server == null) return null;
		ServerLevel destination = server.getLevel(dimension);
		if(destination == null) {
			AbyssalCraft.LOGGER.warn("Gateway at {} in {} leads to {}, which doesn't exist", entryPos, entity.level().dimension().location(), dimension.location());
			if(entity instanceof Player player)
				player.displayClientMessage(Component.translatable("message.abyssalcraft.gateway.no_dimension", dimension.location().toString()), true);
			return null;
		}
		return entity.changeDimension(destination, new TeleporterAC(destination, portal, entryPos));
	}

	@Override
	public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
		return repositionEntity.apply(false);
	}

	@Nullable
	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
		WorldBorder border = destWorld.getWorldBorder();
		double scale = DimensionType.getTeleportationScale(entity.level().dimensionType(), destWorld.dimensionType());
		BlockPos target = border.clampToBounds(entity.getX() * scale, entity.getY(), entity.getZ() * scale);

		BlockState entered = entity.level().getBlockState(entryPos);
		Direction.Axis axis = entered.is(portal) ? entered.getValue(BlockACPortal.AXIS) : Direction.Axis.X;

		Optional<BlockUtil.FoundRectangle> exit = findPortalAround(target, border);
		if(exit.isEmpty())
			exit = createPortal(target, axis);
		if(exit.isEmpty()) {
			AbyssalCraft.LOGGER.warn("Unable to find or create a gateway in {} near {}", destWorld.dimension().location(), target);
			return null;
		}

		Vec3 relative;
		if(entered.is(portal)) {
			BlockUtil.FoundRectangle source = BlockUtil.getLargestRectangleAround(entryPos, axis, ACPortalShape.MAX_WIDTH, Direction.Axis.Y, ACPortalShape.MAX_HEIGHT,
					pos -> entity.level().getBlockState(pos) == entered);
			relative = PortalShape.getRelativePosition(source, axis, entity.position(), entity.getDimensions(entity.getPose()));
		} else relative = new Vec3(0.5D, 0.0D, 0.0D);

		return PortalShape.createPortalInfo(destWorld, exit.get(), axis, relative, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
	}

	@Override
	public boolean isVanilla() {
		return false;
	}

	@Override
	public boolean playTeleportSound(net.minecraft.server.level.ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		return false;
	}

	/**
	 * Finds the closest linked gateway (keeps its chunks loaded briefly, like vanilla portals)
	 */
	public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos pos, WorldBorder border) {
		PoiManager poimanager = level.getPoiManager();
		poimanager.ensureLoadedAndValid(level, pos, SEARCH_RADIUS);
		Optional<PoiRecord> optional = poimanager.getInSquare(type -> type.is(portal.getPoi()), pos, SEARCH_RADIUS, PoiManager.Occupancy.ANY)
				.filter(record -> border.isWithinBounds(record.getPos()))
				.sorted(Comparator.<PoiRecord>comparingDouble(record -> record.getPos().distSqr(pos)).thenComparingInt(record -> record.getPos().getY()))
				.filter(record -> level.getBlockState(record.getPos()).is(portal))
				.findFirst();
		return optional.map(record -> {
			BlockPos blockpos = record.getPos();
			level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
			BlockState state = level.getBlockState(blockpos);
			return BlockUtil.getLargestRectangleAround(blockpos, state.getValue(BlockACPortal.AXIS), ACPortalShape.MAX_WIDTH, Direction.Axis.Y, ACPortalShape.MAX_HEIGHT,
					p -> level.getBlockState(p) == state);
		});
	}

	/**
	 * Builds a new gateway near the position. Prefers a spot on solid ground with room around it, then any spot with
	 * solid ground, and as a last resort builds a small platform (so the arrival spot is never inside blocks or above the void).
	 */
	public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction.Axis axis) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double best = -1.0D;
		BlockPos bestPos = null;
		double fallback = -1.0D;
		BlockPos fallbackPos = null;
		WorldBorder border = level.getWorldBorder();
		int top = Math.min(level.getMaxBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight()) - 1;
		BlockPos.MutableBlockPos mutable = pos.mutable();

		for(BlockPos.MutableBlockPos column : BlockPos.spiralAround(pos, CREATE_RADIUS, Direction.EAST, Direction.SOUTH)) {
			int height = Math.min(top, level.getHeight(Heightmap.Types.MOTION_BLOCKING, column.getX(), column.getZ()));
			if(border.isWithinBounds(column) && border.isWithinBounds(column.move(direction, 1))) {
				column.move(direction.getOpposite(), 1);

				for(int y = height; y >= level.getMinBuildHeight(); --y) {
					column.setY(y);
					if(canPortalReplaceBlock(column)) {
						int start = y;
						while(y > level.getMinBuildHeight() && canPortalReplaceBlock(column.move(Direction.DOWN)))
							--y;

						if(y + 4 <= top) {
							int gap = start - y;
							if(gap <= 0 || gap >= 3) {
								column.setY(y);
								if(canHostFrame(column, mutable, direction, 0)) {
									double dist = pos.distSqr(column);
									if(canHostFrame(column, mutable, direction, -1) && canHostFrame(column, mutable, direction, 1) && (best == -1.0D || best > dist)) {
										best = dist;
										bestPos = column.immutable();
									}

									if(best == -1.0D && (fallback == -1.0D || fallback > dist)) {
										fallback = dist;
										fallbackPos = column.immutable();
									}
								}
							}
						}
					}
				}
			}
		}

		if(best == -1.0D && fallback != -1.0D) {
			bestPos = fallbackPos;
			best = fallback;
		}

		if(best == -1.0D) {
			int min = Math.max(level.getMinBuildHeight() + 1, 70);
			int max = top - 9;
			if(max < min)
				return Optional.empty();

			bestPos = new BlockPos(pos.getX(), Mth.clamp(pos.getY(), min, max), pos.getZ()).immutable();
			Direction side = direction.getClockWise();
			if(!border.isWithinBounds(bestPos))
				return Optional.empty();

			// platform of frame blocks with air above it, on both sides of the gateway
			for(int i = -1; i < 2; ++i)
				for(int j = 0; j < 2; ++j)
					for(int k = -1; k < 3; ++k) {
						BlockState state = k < 0 ? frame : Blocks.AIR.defaultBlockState();
						mutable.setWithOffset(bestPos, j * direction.getStepX() + i * side.getStepX(), k, j * direction.getStepZ() + i * side.getStepZ());
						level.setBlockAndUpdate(mutable, state);
					}
		}

		for(int i = -1; i < 3; ++i)
			for(int j = -1; j < 4; ++j)
				if(i == -1 || i == 2 || j == -1 || j == 3) {
					mutable.setWithOffset(bestPos, i * direction.getStepX(), j, i * direction.getStepZ());
					level.setBlock(mutable, frame, 3);
				}

		BlockState state = portal.defaultBlockState().setValue(BlockACPortal.AXIS, axis);

		for(int i = 0; i < 2; ++i)
			for(int j = 0; j < 3; ++j) {
				mutable.setWithOffset(bestPos, i * direction.getStepX(), j, i * direction.getStepZ());
				level.setBlock(mutable, state, 18);
			}

		return Optional.of(new BlockUtil.FoundRectangle(bestPos.immutable(), 2, 3));
	}

	private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.canBeReplaced() && state.getFluidState().isEmpty();
	}

	private boolean canHostFrame(BlockPos origin, BlockPos.MutableBlockPos mutable, Direction direction, int offset) {
		Direction side = direction.getClockWise();

		for(int i = -1; i < 3; ++i)
			for(int j = -1; j < 4; ++j) {
				mutable.setWithOffset(origin, direction.getStepX() * i + side.getStepX() * offset, j, direction.getStepZ() * i + side.getStepZ() * offset);
				if(j < 0 && !level.getBlockState(mutable).isSolid())
					return false;

				if(j >= 0 && !canPortalReplaceBlock(mutable))
					return false;
			}

		return true;
	}
}
