/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (ACExplosion, ExplosionUtil)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.lib.util.ScheduledProcess;
import com.shinoow.abyssalcraft.lib.util.Scheduler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Somewhat scalable explosion with spheroid craters (the Oblivion Deathbomb's, and the antimatter mobs' if
 * "nuclearAntimatterExplosions" is set). The inside of the crater is cleared without drops, a thousand blocks a tick;
 * the shell is blown up like a normal explosion (up to size 32) or cleared too. Antimatter explosions leave pools of
 * Liquid Antimatter.<br>
 * (1.12.2 wrote straight into the chunk sections and resent whole chunks; here the blocks are set without neighbor
 * updates, which keeps lighting and client sync working)
 */
public class ACExplosion extends Explosion {

	/** Block flags: tell clients, no neighbor or shape updates, no drops */
	private static final int FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
	private final Level level;
	@Nullable
	private final Entity exploder;
	private final double x, y, z;
	private final float size;
	private final boolean antimatter, smoke;
	private final List<BlockPos> innerBlocks = new ArrayList<>(), outerBlocks = new ArrayList<>();
	private final Map<Player, Vec3> knockback = new HashMap<>();
	@Nullable
	private Runnable onFinished;

	public ACExplosion(Level level, @Nullable Entity exploder, double x, double y, double z, float size, boolean antimatter, boolean smoke) {
		super(level, exploder, x, y, z, size, false, smoke ? BlockInteraction.DESTROY : BlockInteraction.KEEP);
		this.level = level;
		this.exploder = exploder;
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.antimatter = antimatter;
		this.smoke = smoke;
	}

	/**
	 * ExplosionUtil.newODBExplosion: sets off an explosion (server side)
	 * @param antimatter leaves Liquid Antimatter
	 * @param smoke destroys blocks (1.12.2 tied block damage to the smoke flag)
	 */
	public static ACExplosion explode(Level level, @Nullable Entity exploder, double x, double y, double z, float size, boolean antimatter, boolean smoke) {
		return explode(level, exploder, x, y, z, size, antimatter, smoke, null);
	}

	/** @param onFinished runs once the last block is cleared */
	public static ACExplosion explode(Level level, @Nullable Entity exploder, double x, double y, double z, float size, boolean antimatter, boolean smoke,
			@Nullable Runnable onFinished) {
		ACExplosion explosion = new ACExplosion(level, exploder, x, y, z, size, antimatter, smoke);
		explosion.onFinished = onFinished;
		if(ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;
		explosion.explode();
		explosion.finalizeExplosion(size <= 32);
		if(level instanceof ServerLevel server && size <= 32)
			for(ServerPlayer player : server.players())
				if(player.distanceToSqr(x, y, z) < 4096.0D) {
					Vec3 push = explosion.knockback.getOrDefault(player, Vec3.ZERO);
					for(List<BlockPos> positions : Lists.partition(explosion.getToBlow(), 4000))
						player.connection.send(new ClientboundExplodePacket(x, y, z, size, positions, push));
				}
		return explosion;
	}

	private void checkAndAdd(BlockPos pos, List<BlockPos> list) {
		if(level.isOutsideBuildHeight(pos)) return;
		BlockState state = level.getBlockState(pos);
		float resistance = exploder != null ? exploder.getBlockExplosionResistance(this, level, pos, state, state.getFluidState(), state.getExplosionResistance(level, pos, this))
				: state.getExplosionResistance(level, pos, this);
		if(resistance < 600000 && !state.isAir())
			list.add(pos);
	}

	/** Finds the crater blocks (an ellipsoid, 0.6 times as high as wide) and hurts and pushes the entities around */
	@Override
	public void explode() {
		double radiusX = size + 0.5;
		double radiusY = size * 0.6 + 0.5;
		double radiusZ = size + 0.5;
		BlockPos pos = BlockPos.containing(x, y, z);
		final double invRadiusX = 1 / radiusX;
		final double invRadiusY = 1 / radiusY;
		final double invRadiusZ = 1 / radiusZ;
		final int ceilRadiusX = (int) Math.ceil(radiusX);
		final int ceilRadiusY = (int) Math.ceil(radiusY);
		final int ceilRadiusZ = (int) Math.ceil(radiusZ);
		double nextXn = 0;
		forX: for(int x = 0; x <= ceilRadiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextYn = 0;
			forY: for(int y = 0; y <= ceilRadiusY; ++y) {
				final double yn = nextYn;
				nextYn = (y + 1) * invRadiusY;
				double nextZn = 0;
				forZ: for(int z = 0; z <= ceilRadiusZ; ++z) {
					final double zn = nextZn;
					nextZn = (z + 1) * invRadiusZ;
					double distanceSq = xn * xn + yn * yn + zn * zn;
					if(distanceSq > 1) {
						if(z == 0) {
							if(y == 0)
								break forX;
							break forY;
						}
						break forZ;
					}
					List<BlockPos> list = nextXn * nextXn + yn * yn + zn * zn <= 1 && xn * xn + nextYn * nextYn + zn * zn <= 1 && xn * xn + yn * yn + nextZn * nextZn <= 1
							? innerBlocks : outerBlocks;
					for(int sx = -1; sx <= 1; sx += 2)
						for(int sy = -1; sy <= 1; sy += 2)
							for(int sz = -1; sz <= 1; sz += 2)
								if((sx > 0 || x != 0) && (sy > 0 || y != 0) && (sz > 0 || z != 0))
									checkAndAdd(pos.offset(sx * x, sy * y, sz * z), list);
				}
			}
		}
		if(size <= 32) {
			getToBlow().addAll(innerBlocks);
			getToBlow().addAll(outerBlocks);
		}
		if(level.isClientSide) return;
		float f3 = size * 2.0F;
		List<Entity> list = level.getEntities(exploder, new AABB(Mth.floor(x - f3 - 1.0D), Mth.floor(y - f3 - 1.0D), Mth.floor(z - f3 - 1.0D),
				Mth.floor(x + f3 + 1.0D), Mth.floor(y + f3 + 1.0D), Mth.floor(z + f3 + 1.0D)));
		ForgeEventFactory.onExplosionDetonate(level, this, list, f3);
		for(Entity entity : list) {
			if(entity.ignoreExplosion()) continue;
			double d12 = Math.sqrt(entity.distanceToSqr(x, y, z)) / f3;
			if(d12 > 1.0D) continue;
			double d5 = entity.getX() - x;
			double d7 = entity.getEyeY() - y;
			double d9 = entity.getZ() - z;
			double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
			if(d13 == 0.0D) continue;
			d5 /= d13;
			d7 /= d13;
			d9 /= d13;
			double d10 = 1.0D - d12;
			entity.hurt(level.damageSources().explosion(this), (int)((d10 * d10 + d10) / 2.0D * 7.0D * f3 + 1.0D));
			double d11 = entity instanceof LivingEntity living ? ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, d10) : 1.0D;
			entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
			if(entity instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying))
				knockback.put(player, new Vec3(d5 * d10, d7 * d10, d9 * d10));
		}
	}

	/** Sound, particles, then the block clearing over the next ticks */
	@Override
	public void finalizeExplosion(boolean destroyShell) {
		level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
		if(level instanceof ServerLevel server)
			server.sendParticles(size >= 2.0F && smoke ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0);
		if(level.isClientSide) return;

		int delay = 1;
		int index = 0;
		if(smoke) {
			for(List<BlockPos> inner : Lists.partition(innerBlocks, 1000)) {
				schedule(delay, () -> inner.forEach(p -> level.setBlock(p, Blocks.AIR.defaultBlockState(), FLAGS)));
				if(++index % 1000 == 0)
					delay++;
			}
			for(List<BlockPos> outer : Lists.partition(outerBlocks, 100)) {
				schedule(delay, () -> {
					for(BlockPos p : outer)
						if(destroyShell)
							level.getBlockState(p).onBlockExploded(level, p, this);
						else
							level.setBlock(p, Blocks.AIR.defaultBlockState(), FLAGS);
				});
				if(++index % 100 == 0)
					delay++;
			}
		}
		if(antimatter)
			for(List<BlockPos> extra : Lists.partition(size <= 32 ? getToBlow() : outerBlocks, 100)) {
				schedule(delay, () -> {
					for(BlockPos p : extra)
						if(level.getBlockState(p).isAir() && level.getBlockState(p.below()).isCollisionShapeFullBlock(level, p.below()) && level.random.nextInt(3) == 0)
							level.setBlockAndUpdate(p, ACBlocks.liquid_antimatter.get().defaultBlockState());
				});
				if(++index % 100 == 0)
					delay++;
			}
		if(onFinished != null)
			schedule(delay + 10, onFinished);
	}

	private static void schedule(int delay, Runnable task) {
		Scheduler.schedule(new ScheduledProcess(delay) {
			@Override
			public void execute() {
				task.run();
			}
		});
	}

	@Override
	public Map<Player, Vec3> getHitPlayers() {
		return knockback;
	}

	/** The crater blocks for size 32 and less, otherwise only the inside */
	public List<BlockPos> getCraterBlocks() {
		return size <= 32 ? getToBlow() : innerBlocks;
	}

	public float getSize() {
		return size;
	}
}
