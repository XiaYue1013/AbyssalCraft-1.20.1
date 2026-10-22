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
package com.shinoow.abyssalcraft.common.entity.demon;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Evil Sheep: grazes like a sheep, and once it has killed a player its wool is made of that player's skin (shearing it
 * then gives rotten flesh).<br>
 * (1.12.2 sent the player to the clients every second with EvilSheepMessage; here it's synced entity data)
 */
public class EntityEvilSheep extends EntityEvilAnimal implements ISheepAnimation {

	private static final EntityDataAccessor<Optional<UUID>> KILLED_UUID = SynchedEntityData.defineId(EntityEvilSheep.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<String> KILLED_NAME = SynchedEntityData.defineId(EntityEvilSheep.class, EntityDataSerializers.STRING);
	private static final byte EAT_EVENT = 10;
	private EatBlockGoal eatBlockGoal;
	private int eatAnimationTick;

	public EntityEvilSheep(EntityType<? extends EntityEvilSheep> type, Level level) {
		super(type, level, AnimalKind.SHEEP);
	}

	@Override
	protected void addIdleGoals() {
		eatBlockGoal = new EatBlockGoal(this);
		goalSelector.addGoal(3, eatBlockGoal);
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(KILLED_UUID, Optional.empty());
		entityData.define(KILLED_NAME, "");
	}

	@Override
	protected void customServerAiStep() {
		eatAnimationTick = eatBlockGoal.getEatAnimationTick();
		super.customServerAiStep();
	}

	@Override
	public void aiStep() {
		if(level().isClientSide)
			eatAnimationTick = Math.max(0, eatAnimationTick - 1);
		super.aiStep();
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == EAT_EVENT)
			eatAnimationTick = 40;
		else
			super.handleEntityEvent(id);
	}

	@Override
	public float getHeadEatPositionScale(float partialTick) {
		if(eatAnimationTick <= 0) return 0.0F;
		if(eatAnimationTick >= 4 && eatAnimationTick <= 36) return 1.0F;
		return eatAnimationTick < 4 ? (eatAnimationTick - partialTick) / 4.0F : -(eatAnimationTick - 40 - partialTick) / 4.0F;
	}

	@Override
	public float getHeadEatAngleScale(float partialTick) {
		if(eatAnimationTick > 4 && eatAnimationTick <= 36) {
			float f = (eatAnimationTick - 4 - partialTick) / 32.0F;
			return (float)Math.PI / 5F + 0.21991149F * Mth.sin(f * 28.7F);
		}
		return eatAnimationTick > 0 ? (float)Math.PI / 5F : getXRot() * ((float)Math.PI / 180F);
	}

	@Nullable
	public UUID getKilledPlayerUUID() {
		return entityData.get(KILLED_UUID).orElse(null);
	}

	public String getKilledPlayerName() {
		return entityData.get(KILLED_NAME);
	}

	public boolean hasKilledPlayer() {
		return getKilledPlayerUUID() != null && !getKilledPlayerName().isEmpty();
	}

	public void setKilledPlayer(Player player) {
		entityData.set(KILLED_UUID, Optional.of(player.getUUID()));
		entityData.set(KILLED_NAME, player.getGameProfile().getName());
	}

	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity entity) {
		if(entity instanceof Player player)
			setKilledPlayer(player);
		return super.killedEntity(level, entity);
	}

	@Override
	protected ItemStack getShearingDrop() {
		return new ItemStack(hasKilledPlayer() ? Items.ROTTEN_FLESH : Items.WHITE_WOOL);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if(tag.hasUUID("Player"))
			entityData.set(KILLED_UUID, Optional.of(tag.getUUID("Player")));
		else if(tag.contains("PlayerUUID")) // 1.12.2
			entityData.set(KILLED_UUID, Optional.of(UUID.fromString(tag.getString("PlayerUUID"))));
		if(tag.contains("PlayerName"))
			entityData.set(KILLED_NAME, tag.getString("PlayerName"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(getKilledPlayerUUID() != null)
			tag.putUUID("Player", getKilledPlayerUUID());
		if(!getKilledPlayerName().isEmpty())
			tag.putString("PlayerName", getKilledPlayerName());
	}
}
