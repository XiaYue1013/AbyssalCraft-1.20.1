/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityDemonAnimal, EntityDemonPig, EntityDemonCow, EntityDemonChicken, EntityDemonSheep)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.demon;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.common.entity.EntityACMonster;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Demon Pig, Cow, Chicken and Sheep: hostile, fire immune Dreadlands/Nether animals. Once set alight (with flint and
 * steel or fire, if the config allows it outside the Nether) they leave a trail of fire until they get wet.
 */
public class EntityDemonAnimal extends EntityACMonster implements IDreadEntity, ISheepAnimation {

	private final AnimalKind kind;
	private boolean canBurn;
	/** Chicken wing flapping (EntityChicken fields) */
	public float flap, oFlap, flapSpeed, oFlapSpeed, flapping = 1.0F;

	public EntityDemonAnimal(EntityType<? extends EntityDemonAnimal> type, Level level, AnimalKind kind) {
		super(type, level, "demon_" + kind.name);
		this.kind = kind;
		hardcore(kind.health * 2, 4.0D);
	}

	public static AttributeSupplier.Builder createAttributes(AnimalKind kind) {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, kind.health);
	}

	public AnimalKind getKind() {
		return kind;
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(3, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected boolean halloweenPumpkin() {
		return false;
	}

	@Override
	public float getVoicePitch() {
		return 0.2F;
	}

	@Override
	protected float getSoundVolume() {
		return kind.volume;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return kind.ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.GHAST_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return kind.death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(kind.step.get(), 0.15F, 1.0F);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return kind != AnimalKind.CHICKEN && super.causeFallDamage(distance, multiplier, source);
	}

	public boolean canBurn() {
		return canBurn;
	}

	private static boolean burningAllowed() {
		return ACConfig.demonAnimalFire || ACConfig.hardcoreMode;
	}

	@Override
	public void setSecondsOnFire(int seconds) {
		if(burningAllowed())
			canBurn = true;
		super.setSecondsOnFire(seconds);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(burningAllowed() && stack.is(Items.FLINT_AND_STEEL) && !canBurn) {
			level().playSound(player, getX(), getY(), getZ(), SoundEvents.FLINTANDSTEEL_USE, getSoundSource(), 1.0F, random.nextFloat() * 0.4F + 0.8F);
			player.swing(hand);
			canBurn = true;
			if(!level().isClientSide)
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			return InteractionResult.sidedSuccess(level().isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if(kind == AnimalKind.CHICKEN)
			flapWings();
		if(burningAllowed() && !canBurn && level().getBlockStatesIfLoaded(getBoundingBox().deflate(0.001D))
				.anyMatch(s -> s.is(BlockTags.FIRE) || s.is(Blocks.LAVA)))
			canBurn = true;
		if(canBurn && isInWaterRainOrBubble())
			canBurn = false;
		if(!level().isClientSide && canBurn)
			for(int l = 0; l < 4; ++l) {
				BlockPos pos = BlockPos.containing(getX() + (l % 2 * 2 - 1) * 0.25F, getY(), getZ() + (l / 2 % 2 * 2 - 1) * 0.25F);
				if(level().getBlockState(pos).isAir() && BaseFireBlock.canBePlacedAt(level(), pos, Direction.NORTH))
					level().setBlockAndUpdate(pos, ACConfig.mimicFire ? BlockHandler.MIMIC_FIRE.get().defaultBlockState() : BaseFireBlock.getState(level(), pos));
			}
	}

	/** EntityChicken wing animation, and the chicken's slow fall */
	private void flapWings() {
		oFlap = flap;
		oFlapSpeed = flapSpeed;
		flapSpeed += (onGround() ? -1.0F : 4.0F) * 0.3F;
		flapSpeed = Math.max(0.0F, Math.min(1.0F, flapSpeed));
		if(!onGround() && flapping < 1.0F)
			flapping = 1.0F;
		flapping *= 0.9F;
		if(!onGround() && getDeltaMovement().y < 0.0D)
			setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
		flap += flapping * 2.0F;
	}

	@Override
	public float getHeadEatPositionScale(float partialTick) {
		return 0.0F;
	}

	@Override
	public float getHeadEatAngleScale(float partialTick) {
		return getXRot() * ((float)Math.PI / 180F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		canBurn = tag.getBoolean("CanBurn");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("CanBurn", canBurn);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		if(level.getLevel().dimension() == Level.OVERWORLD && ACConfig.demonAnimalFire && random.nextInt(3) == 0
				|| level.getLevel().dimension() == Level.NETHER && random.nextBoolean())
			canBurn = true;
		return data;
	}
}
