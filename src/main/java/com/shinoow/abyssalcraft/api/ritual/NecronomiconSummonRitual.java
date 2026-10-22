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
package com.shinoow.abyssalcraft.api.ritual;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A Necronomicon Ritual that summons an entity on top of the altar
 * @author shinoow
 *
 * @since 1.4
 */
public class NecronomiconSummonRitual extends NecronomiconRitual {

	private Supplier<? extends EntityType<? extends LivingEntity>> entity;
	private CompoundTag customNBT;

	public NecronomiconSummonRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, Supplier<? extends EntityType<? extends LivingEntity>> entity, Object...offerings) {
		super(unlocalizedName, bookType, dimension, requiredEnergy, requiresSacrifice, offerings);
		this.entity = entity;
		setRitualParticle(EnumRitualParticle.SMOKE_PILLARS);
	}

	public NecronomiconSummonRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, Supplier<? extends EntityType<? extends LivingEntity>> entity, Object...offerings) {
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, entity, offerings);
	}

	public NecronomiconSummonRitual(String unlocalizedName, int bookType, float requiredEnergy, Supplier<? extends EntityType<? extends LivingEntity>> entity, Object...offerings) {
		this(unlocalizedName, bookType, null, requiredEnergy, entity, offerings);
	}

	/**
	 * Getter for the entity type that will be summoned
	 */
	public EntityType<? extends LivingEntity> getEntity(){
		return entity.get();
	}

	/**
	 * Custom NBT merged into the summoned entity
	 */
	public NecronomiconSummonRitual setCustomNBT(CompoundTag customNBT) {
		this.customNBT = customNBT;
		return this;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		if(!(world instanceof ServerLevel level)) return;
		LivingEntity entityliving = getEntity().create(level);
		if(entityliving != null){
			entityliving.moveTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, entityliving.getYRot(), entityliving.getXRot());
			if(entityliving instanceof Mob mob)
				mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos.above()), MobSpawnType.MOB_SUMMONED, null, null);
			for (ServerPlayer entityplayermp : world.getEntitiesOfClass(ServerPlayer.class, entityliving.getBoundingBox().inflate(5)))
				CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp, entityliving);
			if(customNBT != null) {
				CompoundTag internal = new CompoundTag();
				entityliving.saveWithoutId(internal);
				internal.merge(customNBT);
				entityliving.load(internal);
			}
			world.addFreshEntity(entityliving);
			entityliving.setPortalCooldown();
		}
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){
		if(entity.get() == null) player.sendSystemMessage(Component.literal("The Entity could not be summoned."));
	}
}
