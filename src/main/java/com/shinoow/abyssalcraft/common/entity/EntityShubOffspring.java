/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityShubOffspring extends EntityACMonster {
	public EntityShubOffspring(EntityType<? extends EntityShubOffspring> type, Level level) {
		super(type, level, "shub_offspring");
		hardcore(80, 8);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.ATTACK_DAMAGE, 4);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
		targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override protected boolean halloweenPumpkin() { return false; }
	@Override protected float hardcoreDamage() { return 1.5F; }
	@Override protected SoundEvent getAmbientSound() { return SoundEvents.SHEEP_AMBIENT; }
	@Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SHEEP_HURT; }
	@Override protected SoundEvent getDeathSound() { return SoundEvents.SHEEP_DEATH; }
	@Override protected float getSoundVolume() { return 3; }
	@Override public float getVoicePitch() {
		return (random.nextFloat() - random.nextFloat()) * 0.2F + (random.nextBoolean() ? 1.5F : 0.5F);
	}
	@Override protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) { return dimensions.height * 0.7F; }
	@Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SHEEP_STEP, 0.15F, 1); }

	/** Rarest at a full moon, common at a new moon, and never below sea level or during the day. */
	public static boolean checkSpawnRules(EntityType<EntityShubOffspring> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
		float moon = level.getLevel().getMoonBrightness();
		return !level.getLevel().isDay() && pos.getY() >= level.getSeaLevel()
				&& (moon == 0 || random.nextFloat() + 0.01F > moon) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
	}
}
