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

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Minion of the Gatekeeper: J'zahar's guards in his temple. Hurting one now and then sets the Remnants around on the
 * attacker.
 */
public class EntityGatekeeperMinion extends Monster implements IOmotholEntity {

	private static final UUID attackDamageBoostUUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
	private static final AttributeModifier attackDamageBoost = new AttributeModifier(attackDamageBoostUUID, "Halloween Attack Damage Boost", 5.0D, AttributeModifier.Operation.ADDITION);

	public EntityGatekeeperMinion(EntityType<? extends EntityGatekeeperMinion> type, Level level) {
		super(type, level);
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(500.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(36.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
				.add(Attributes.MAX_HEALTH, 250.0D).add(Attributes.ATTACK_DAMAGE, 18.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, false));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityGatekeeperMinion.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityRemnant.class, 8.0F));
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
		boolean flag = super.doHurtTarget(target);
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 3 * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.shadow_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!level().isClientSide && random.nextInt(10) == 0) {
			if(source.getEntity() instanceof LivingEntity enemy)
				for(EntityRemnant remnant : level().getEntitiesOfClass(EntityRemnant.class, getBoundingBox().inflate(16D)))
					remnant.enrage(false, enemy);
			playSound(ACSounds.remnant_scream.get(), 3F, 1F);
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/minion_of_the_gatekeeper");
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		LocalDate date = LocalDate.now();
		boolean halloween = date.getMonthValue() == 10 && date.getDayOfMonth() == 31;
		if(getItemBySlot(EquipmentSlot.HEAD).isEmpty() && halloween && random.nextFloat() < 0.25F) {
			setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
			setDropChance(EquipmentSlot.HEAD, 0.0F);
		}
		AttributeInstance attribute = getAttribute(Attributes.ATTACK_DAMAGE);
		attribute.removeModifier(attackDamageBoost);
		if(halloween)
			attribute.addPermanentModifier(attackDamageBoost);
		return data;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.925F;
	}
}
