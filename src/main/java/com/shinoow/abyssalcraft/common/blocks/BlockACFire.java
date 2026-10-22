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
 *     1.20.1 port - BlockCoraliumfire, BlockDreadFire and BlockOmotholFire merged (they only differed in the
 *     gateway they light and the effect they apply)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Eternal fire that lights a gateway frame. Burns and afflicts entities standing in it.
 */
public class BlockACFire extends Block {

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	private final Supplier<Block> portal;
	private final Supplier<MobEffect> effect;
	private final Predicate<LivingEntity> immune;

	/**
	 * @param portal The gateway this fire lights
	 * @param effect Effect applied (for 5 seconds) to living entities in the fire
	 * @param immune Entities that don't get the effect
	 */
	public BlockACFire(Properties properties, Supplier<Block> portal, Supplier<MobEffect> effect, Predicate<LivingEntity> immune) {
		super(properties);
		this.portal = portal;
		this.effect = effect;
		this.immune = immune;
	}

	public static BlockACFire coralium(Properties properties, Supplier<Block> portal) {
		return new BlockACFire(properties, portal, AbyssalCraftAPI.coralium_plague, EntityUtil::isEntityCoralium);
	}

	public static BlockACFire dread(Properties properties, Supplier<Block> portal) {
		return new BlockACFire(properties, portal, AbyssalCraftAPI.dread_plague, EntityUtil::isEntityDread);
	}

	public static BlockACFire omothol(Properties properties, Supplier<Block> portal) {
		return new BlockACFire(properties, portal, () -> MobEffects.BLINDNESS, e -> false);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos below = pos.below();
		return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if(oldState.is(state.getBlock()) || level.isClientSide) return;
		if(portal.get() instanceof BlockACPortal gateway && gateway.tryToCreatePortal(level, pos))
			return;
		if(!state.canSurvive(level, pos))
			level.removeBlock(pos, false);
	}

	/** Burns like regular fire (1.12.2: isBurning), plus the fire's effect */
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if(!entity.fireImmune()) {
			entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
			if(entity.getRemainingFireTicks() == 0)
				entity.setSecondsOnFire(8);
		}
		entity.hurt(level.damageSources().inFire(), 1.0F);

		if(!level.isClientSide && entity instanceof LivingEntity living && !immune.test(living))
			living.addEffect(new MobEffectInstance(effect.get(), 100));
	}

	@Override
	public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
