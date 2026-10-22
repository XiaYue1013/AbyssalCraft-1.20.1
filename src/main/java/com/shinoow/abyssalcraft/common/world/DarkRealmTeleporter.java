/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (TeleporterDarkRealm, AbyssalCraftEventHooks#darkRealm)
 *     1.20.1 port - the arrival spot is checked: 1.12.2 always used y 80, which could be inside darkstone
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.init.ACDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

/**
 * Falling out of the bottom of Omothol leads into the Dark Realm: no portal, the player keeps their x and z
 */
public class DarkRealmTeleporter implements ITeleporter {

	private static final int ARRIVAL_Y = 80;

	/**
	 * Sends an entity (in 1.12.2 only players fell through) below Omothol's floor to the Dark Realm, protected for the fall
	 * @return The entity in the Dark Realm, or null if it didn't travel
	 */
	@Nullable
	public static Entity fallIntoDarkRealm(LivingEntity entity) {
		ServerLevel darkRealm = entity.getServer() == null ? null : entity.getServer().getLevel(ACDimensions.DARK_REALM);
		if(darkRealm == null) return null;
		entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 255));
		entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20));
		Entity arrived = entity.changeDimension(darkRealm, new DarkRealmTeleporter());
		if(arrived instanceof LivingEntity living && arrived != entity) // a new entity for non-players: keep the protection
			for(MobEffectInstance effect : entity.getActiveEffects())
				living.addEffect(new MobEffectInstance(effect));
		return arrived;
	}

	@Override
	public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
		return repositionEntity.apply(false);
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
		BlockPos pos = safeArrival(destWorld, BlockPos.containing(entity.getX(), ARRIVAL_Y, entity.getZ()));
		return new PortalInfo(Vec3.atBottomCenterOf(pos), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	/** y 80 if there's room for a player there, otherwise the closest free spot above it, or the surface */
	static BlockPos safeArrival(ServerLevel level, BlockPos pos) {
		level.getChunk(pos);
		for(BlockPos.MutableBlockPos p = pos.mutable(); p.getY() < level.getMaxBuildHeight() - 2; p.move(0, 1, 0))
			if(isFree(level, p) && isFree(level, p.above()))
				return p.immutable();
		return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
	}

	private static boolean isFree(ServerLevel level, BlockPos pos) {
		return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty() && level.getFluidState(pos).isEmpty();
	}

	@Override
	public boolean isVanilla() {
		return false;
	}

	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		return false;
	}
}
