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
package com.shinoow.abyssalcraft.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.common.blocks.BlockRitualAltar;
import com.shinoow.abyssalcraft.common.blocks.BlockRitualPedestal;
import com.shinoow.abyssalcraft.common.world.DarkRealmTeleporter;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

/**
 * J'zahar's black hole: for 15 seconds it pulls everything within 48 blocks in. Living things that get within 3 blocks
 * are thrown into a random other dimension, other entities that get within 2 blocks are destroyed, and blocks around it
 * are torn loose. Omothol entities aren't affected.
 */
public class EntityBlackHole extends Entity {

	@Nullable
	private EntityJzahar shootingEntity;
	private double speed = 0.05D;

	public EntityBlackHole(EntityType<? extends EntityBlackHole> type, Level level) {
		super(type, level);
	}

	public EntityBlackHole(Level level, EntityJzahar jzahar) {
		this(ACEntityTypes.BLACK_HOLE.get(), level);
		shootingEntity = jzahar;
		moveTo(jzahar.getX(), jzahar.getY(), jzahar.getZ(), jzahar.getYRot(), jzahar.getXRot());
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	public void tick() {
		super.tick();
		if(tickCount > 300)
			discard();
		for(Entity entity : level().getEntities(this, getBoundingBox().inflate(48D)))
			if(!(entity instanceof IOmotholEntity))
				if(entity.isAlive() && entity instanceof LivingEntity living) {
					if(entity.getY() < getY())
						entity.push(0, 0.025F, 0);
					double d1 = 7D;
					double d2 = getX() + (random.nextDouble() * 8D - 4D) - entity.getX();
					double d3 = getY() + (random.nextDouble() * 8D - 4D) - entity.getY();
					double d4 = getZ() + (random.nextDouble() * 8D - 4D) - entity.getZ();
					double d5 = d2 * d2 + d3 * d3 + d4 * d4;
					entity.push(d2 / d5 * d1 * speed, d3 / d5 * d1 * speed, d4 / d5 * d1 * speed);
					if(entity.distanceToSqr(this) <= 9D && shootingEntity != null && !level().isClientSide)
						throwIntoDimension(living);
				} else if(!(entity instanceof EntityBlackHole) && !(entity instanceof EntityImplosion)) {
					double d1 = 7D;
					double d2 = getX() + (random.nextDouble() * 4D - 2D) - entity.getX();
					double d3 = getY() + (random.nextDouble() * 4D - 2D) - entity.getY();
					double d4 = getZ() + (random.nextDouble() * 4D - 2D) - entity.getZ();
					double d5 = d2 * d2 + d3 * d3 + d4 * d4;
					entity.push(d2 / d5 * d1, d3 / d5 * d1, d4 / d5 * d1);
					if(entity.distanceToSqr(this) <= 4D && !level().isClientSide)
						entity.discard();
				}
		speed += 0.0005;
		if(!level().isClientSide)
			tearBlocks();
	}

	/** Sends a living entity (off portal cooldown) to a random other dimension, at a random spot near 0, 80, 0 */
	private void throwIntoDimension(LivingEntity living) {
		if(living.isOnPortalCooldown()) {
			living.setPortalCooldown();
			return;
		}
		ServerLevel destination = randomDimension(level().getServer(), level().dimension(), random);
		if(destination == null) return;
		if(living instanceof ServerPlayer player)
			player.setPortalCooldown(ACConfig.portalCooldown);
		else
			living.setPortalCooldown();
		living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 255));
		living.setPos(random.nextInt(100), 80, random.nextInt(100));
		Entity arrived = living.changeDimension(destination, new DarkRealmTeleporter());
		if(arrived instanceof LivingEntity moved && arrived != living)
			moved.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 255));
	}

	/**
	 * 1.12.2 InitHandler.getRandomDimension: any loaded dimension but the current one and those in the
	 * "realityMaelstromBlacklist" config option (20 tries, then the Dark Realm)
	 */
	@Nullable
	public static ServerLevel randomDimension(MinecraftServer server, ResourceKey<Level> current, RandomSource random) {
		List<ServerLevel> levels = new ArrayList<>();
		server.getAllLevels().forEach(levels::add);
		for(int tries = 0; tries < 20; tries++) {
			ServerLevel level = levels.get(random.nextInt(levels.size()));
			if(level.dimension() != current && !ACConfig.blackHoleBlacklist.contains(level.dimension().location().toString()))
				return level;
		}
		return server.getLevel(ACDimensions.DARK_REALM);
	}

	/** Each tick a tenth of the blocks within 6 blocks fall loose (liquids vanish), except very blast resistant and ritual blocks */
	private void tearBlocks() {
		BlockPos center = blockPosition();
		if(!level().hasChunksAt(center.offset(-32, -32, -32), center.offset(32, 32, 32))) return;
		for(BlockPos pos : BlockPos.betweenClosed(center.offset(-6, -6, -6), center.offset(6, 6, 6))) {
			BlockState state = level().getBlockState(pos);
			if(!state.isAir() && random.nextInt(10) == 0 && state.getBlock().getExplosionResistance() < 600000
					&& !(state.getBlock() instanceof BlockRitualAltar) && !(state.getBlock() instanceof BlockRitualPedestal))
				if(state.liquid())
					level().removeBlock(pos, false);
				else
					FallingBlockEntity.fall(level(), pos.immutable(), state);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}
}
