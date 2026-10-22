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
 *     1.20.1 port - BlockAbyssPortal, BlockDreadlandsPortal and BlockOmotholPortal only differed in their frame,
 *     fire, dimensions and spawned mob, so they are one parametrized class now
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.world.ACPortalShape;
import com.shinoow.abyssalcraft.common.world.TeleporterAC;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A gateway between two dimensions. Standing in it teleports to {@link #destination}, or back to {@link #origin}
 * when already in the destination.
 */
public class BlockACPortal extends Block {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	private final Supplier<Block> frame, fire;
	private final ResourceKey<Level> destination, origin;
	private final ResourceKey<PoiType> poi;
	private final String mob;

	/**
	 * @param frame Block the frame is made of
	 * @param fire Fire that lights the frame
	 * @param destination Dimension the gateway leads to
	 * @param origin Dimension the gateway leads back to when used in the destination
	 * @param poi Point of interest used to find linked gateways
	 * @param mob ID of the mob (in the abyssalcraft namespace) that rarely spawns from the gateway
	 */
	public BlockACPortal(Properties properties, Supplier<Block> frame, Supplier<Block> fire, ResourceKey<Level> destination, ResourceKey<Level> origin,
			ResourceKey<PoiType> poi, String mob) {
		super(properties);
		this.frame = frame;
		this.fire = fire;
		this.destination = destination;
		this.origin = origin;
		this.poi = poi;
		this.mob = mob;
		registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	public Block getFrameBlock() {
		return frame.get();
	}

	public Block getFireBlock() {
		return fire.get();
	}

	public ResourceKey<PoiType> getPoi() {
		return poi;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		boolean playerNearby = !ACConfig.portalSpawnsNearPlayer || level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false) != null;
		ResourceLocation id = new ResourceLocation(AbyssalCraft.modid, mob);
		if(!ForgeRegistries.ENTITY_TYPES.containsKey(id)) return; // mob not ported yet
		EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
		boolean nearbyMobs = level.getEntities((Entity) null, new AABB(pos).inflate(16), e -> e.getType() == type).size() < 10;

		if(level.dimension() != destination && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && rand.nextInt(2000) < level.getDifficulty().getId()
				&& playerNearby && nearbyMobs) {
			BlockPos blockpos = pos;
			while(!level.getBlockState(blockpos).isFaceSturdy(level, blockpos, Direction.UP) && blockpos.getY() > level.getMinBuildHeight())
				blockpos = blockpos.below();

			if(blockpos.getY() > level.getMinBuildHeight() && !level.getBlockState(blockpos.above()).isRedstoneConductor(level, blockpos.above())) {
				Entity entity = type.spawn(level, blockpos.above(), MobSpawnType.STRUCTURE);
				if(entity != null)
					entity.setPortalCooldown();
			}
		}
	}

	/**
	 * Lights the frame around the position, if there is one
	 * @return Whether or not a gateway was created
	 */
	public boolean tryToCreatePortal(LevelAccessor level, BlockPos pos) {
		return ACPortalShape.findEmptyPortalShape(level, pos, this, Direction.Axis.X).map(shape -> {
			shape.createPortalBlocks();
			return true;
		}).orElse(false);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis portalAxis = state.getValue(AXIS);
		boolean perpendicular = portalAxis != axis && axis.isHorizontal();
		return !perpendicular && !neighborState.is(this) && !new ACPortalShape(level, pos, this, portalAxis).isComplete()
				? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if(level instanceof ServerLevel && !entity.isPassenger() && !entity.isVehicle() && entity.isAlive() && entity.canChangeDimensions()) {
			boolean cooldown = entity.isOnPortalCooldown();
			if(entity instanceof ServerPlayer)
				entity.setPortalCooldown(ACConfig.portalCooldown);
			else entity.setPortalCooldown();
			if(!cooldown)
				TeleporterAC.changeDimension(entity, level.dimension() != destination ? destination : origin, this, pos);
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		switch(rotation) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch(state.getValue(AXIS)) {
			case Z:
				return state.setValue(AXIS, Direction.Axis.X);
			case X:
				return state.setValue(AXIS, Direction.Axis.Z);
			default:
				return state;
			}
		default:
			return state;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}
}
