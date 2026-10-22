/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityEvilAnimal, EntityEvilpig, EntityEvilCow, EntityEvilChicken)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.demon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.common.entity.EntityACMonster;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;

/**
 * Evil Pig, Cow, Chicken and Sheep: hostile animals that look like the real ones (and carry their names). They spawn
 * at night during a new moon; killed or sheared, they turn into their Demon Animal.
 */
public class EntityEvilAnimal extends EntityACMonster implements IForgeShearable {

	private final AnimalKind kind;
	/** Chicken wing flapping (EntityChicken fields) */
	public float flap, oFlap, flapSpeed, oFlapSpeed, flapping = 1.0F;

	public EntityEvilAnimal(EntityType<? extends EntityEvilAnimal> type, Level level, AnimalKind kind) {
		super(type, level, "evil_" + kind.name);
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
		addIdleGoals();
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	protected void addIdleGoals() {
		goalSelector.addGoal(3, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
	}

	/** Named like the animal it pretends to be */
	@Override
	protected Component getTypeName() {
		return Component.translatable("entity.minecraft." + kind.name);
	}

	@Override
	protected boolean halloweenPumpkin() {
		return false;
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

	@Override
	public void aiStep() {
		super.aiStep();
		if(kind == AnimalKind.CHICKEN) {
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
	}

	/** Killed by anything but a Lesser Shoggoth, a Demon Animal takes its place (config) */
	@Override
	public void die(DamageSource source) {
		boolean wasAlive = !isRemoved() && !dead;
		super.die(source);
		if(wasAlive && !level().isClientSide && !(source.getEntity() instanceof EntityLesserShoggoth) && ACConfig.demonAnimalsSpawnOnDeath) {
			becomeDemon();
			discard();
		}
	}

	/** Spawns this animal's Demon Animal in its place */
	public void becomeDemon() {
		EntityDemonAnimal demon = demonType().create(level());
		if(demon == null) return;
		demon.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
		if(level() instanceof ServerLevelAccessor server)
			demon.finalizeSpawn(server, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.CONVERSION, null, null);
		level().addFreshEntity(demon);
	}

	private EntityType<? extends EntityDemonAnimal> demonType() {
		return switch(kind) {
		case PIG -> ACEntityTypes.DEMON_PIG.get();
		case COW -> ACEntityTypes.DEMON_COW.get();
		case CHICKEN -> ACEntityTypes.DEMON_CHICKEN.get();
		case SHEEP -> ACEntityTypes.DEMON_SHEEP.get();
		};
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, Level level, BlockPos pos) {
		return isAlive();
	}

	/** 1 - 3 of the animal's shearing drop, and it turns into its Demon Animal */
	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level level, BlockPos pos, int fortune) {
		int i = 1 + random.nextInt(3);
		List<ItemStack> ret = new ArrayList<>();
		for(int j = 0; j < i; ++j)
			ret.add(getShearingDrop());
		level.playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.HOSTILE, 1.0F, 1.0F);
		level.playSound(null, this, SoundEvents.GHAST_HURT, SoundSource.HOSTILE, 1.0F, 0.2F);
		if(!level.isClientSide) {
			becomeDemon();
			discard();
		}
		return ret;
	}

	protected ItemStack getShearingDrop() {
		return new ItemStack(kind.shearDrop.get());
	}

	/** Only at night during a new moon, if the config wants that */
	@Override
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
		if(ACConfig.evilAnimalNewMoonSpawning && reason == MobSpawnType.NATURAL) {
			boolean day = level instanceof ServerLevelAccessor server && server.getLevel().isDay();
			if(day || level.getMoonPhase() != 4)
				return false;
		}
		return super.checkSpawnRules(level, reason);
	}
}
