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

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Omothol Ghoul: a big ghoul of Omothol whose hits slow and blind. Picks up and wears gear like the Depths Ghoul.
 */
public class EntityOmotholGhoul extends EntityACMonster implements IOmotholEntity {

	public EntityOmotholGhoul(EntityType<? extends EntityOmotholGhoul> type, Level level) {
		super(type, level, "omothol_ghoul");
		hardcore(300.0D, 30.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
				.add(Attributes.MAX_HEALTH, 150.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, false));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		swing(InteractionHand.MAIN_HAND);
		swing(InteractionHand.OFF_HAND);
		return super.doHurtTarget(target);
	}

	@Override
	protected void onHit(LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
		target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20));
		target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20));
	}

	@Override
	protected float hardcoreDamage() {
		return 3.0F;
	}

	@Override
	protected double halloweenDamage() {
		return 5.0D;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() - random.nextFloat() * 0.2F + 0.6F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ACSounds.ghoul_normal_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.ghoul_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.ghoul_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	/** Not the items of the "mobPickupItemBlacklist" config option */
	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		return !ACConfig.mobItemPickupBlacklist.contains(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()))) && super.wantsToPickUp(stack);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		setCanPickUpLoot(ACConfig.hardcoreMode || random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
		if(ACConfig.hardcoreMode)
			EntityUtil.suitUp(this, false);
		else {
			populateDefaultEquipmentSlots(random, difficulty);
			populateDefaultEquipmentEnchantments(random, difficulty);
		}
		return super.finalizeSpawn(level, difficulty, reason, data, tag);
	}
}
