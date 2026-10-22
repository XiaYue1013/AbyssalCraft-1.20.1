/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (BlockODB, BlockODBcore); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import javax.annotation.Nullable;
import com.shinoow.abyssalcraft.common.entity.EntityODBPrimed;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Vanilla TNT ignition hooks cover redstone, flint and steel, fire charges and burning projectiles. */
public class BlockODB extends TntBlock {
	private final boolean core;
	public BlockODB(Properties properties, boolean core) { super(properties); this.core = core; }

	@Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return core ? box(4, 0, 4, 12, 16, 12) : box(1.6D, 0, 1.6D, 16, 12.8D, 16);
	}

	@Override public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
		if(level.isClientSide || ACConfig.no_odb_explosions) return;
		EntityODBPrimed bomb = createBomb(level, pos, igniter);
		level.addFreshEntity(bomb);
		level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1, 1);
	}

	@Override public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if(level.isClientSide || ACConfig.no_odb_explosions) return;
		EntityODBPrimed bomb = createBomb(level, pos, explosion.getIndirectSourceEntity());
		bomb.setFuse(level.random.nextInt(bomb.getFuse() / 4) + bomb.getFuse() / 8);
		level.addFreshEntity(bomb);
	}

	private EntityODBPrimed createBomb(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
		EntityODBPrimed bomb = (core ? ACEntityTypes.PRIMED_ODB_CORE.get() : ACEntityTypes.PRIMED_ODB.get()).create(level);
		bomb.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0, 0);
		double angle = level.random.nextDouble() * Math.PI * 2;
		bomb.setDeltaMovement(-Math.sin(angle) * 0.02D, 0.2D, -Math.cos(angle) * 0.02D);
		bomb.setOwner(igniter);
		return bomb;
	}
}
