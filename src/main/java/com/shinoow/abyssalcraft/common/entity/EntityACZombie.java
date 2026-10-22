/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (the shared parts of EntityAbyssalZombie and EntityDepthsGhoul)
 *     1.20.1 port - one base class for the zombie-like AbyssalCraft mobs
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Undead Coralium mob: can be a baby, burns in daylight (except in the Abyssal Wasteland), infects what it hits with
 * the Coralium Plague (always in the Abyssal Wasteland, elsewhere if "coraliumPlagueSpreading" is enabled) and deals
 * extra armor piercing damage to players in Hardcore Mode
 */
public abstract class EntityACZombie extends Monster {

	private static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(EntityACZombie.class, EntityDataSerializers.BOOLEAN);
	private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier BABY_SPEED_BOOST = new AttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
	private static final UUID HALLOWEEN_BOOST_ID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
	private static final AttributeModifier HALLOWEEN_BOOST = new AttributeModifier(HALLOWEEN_BOOST_ID, "Halloween Attack Damage Boost", 3.0D, AttributeModifier.Operation.ADDITION);

	private final int burnSeconds;

	/**
	 * @param hardcoreHealth Max health in Hardcore Mode
	 * @param hardcoreDamage Attack damage in Hardcore Mode
	 * @param burnSeconds How long it burns in daylight
	 */
	protected EntityACZombie(EntityType<? extends EntityACZombie> type, Level level, double hardcoreHealth, double hardcoreDamage, int burnSeconds) {
		super(type, level);
		this.burnSeconds = burnSeconds;
		if(ACConfig.hardcoreMode) { // 1.12.2 applied this per entity in applyEntityAttributes
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(hardcoreHealth);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(hardcoreDamage);
			setHealth(getMaxHealth());
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(CHILD, false);
	}

	@Override
	public boolean isBaby() {
		return entityData.get(CHILD);
	}

	@Override
	public void setBaby(boolean baby) {
		entityData.set(CHILD, baby);
		if(!level().isClientSide) {
			AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
			speed.removeModifier(BABY_SPEED_BOOST);
			if(baby)
				speed.addTransientModifier(BABY_SPEED_BOOST);
		}
	}

	/** (LivingEntity halves the size of babies) */
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if(CHILD.equals(key))
			refreshDimensions();
		super.onSyncedDataUpdated(key);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public int getArmorValue() {
		return Math.min(super.getArmorValue() + 2, 20);
	}

	@Override
	public void aiStep() {
		if(isAlive() && !isBaby() && level().dimension() != ACDimensions.ABYSSAL_WASTELAND && isSunBurnTick()) {
			ItemStack helmet = getItemBySlot(EquipmentSlot.HEAD);
			if(!helmet.isEmpty()) {
				if(helmet.isDamageableItem()) {
					helmet.setDamageValue(helmet.getDamageValue() + random.nextInt(2));
					if(helmet.getDamageValue() >= helmet.getMaxDamage()) {
						broadcastBreakEvent(EquipmentSlot.HEAD);
						setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
					}
				}
			} else
				setSecondsOnFire(burnSeconds);
		}
		super.aiStep();
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if(flag) {
			if(target instanceof LivingEntity living && !EntityUtil.isEntityCoralium(living)
					&& (level().dimension() == ACDimensions.ABYSSAL_WASTELAND || ACConfig.shouldInfect))
				living.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 100));
			if(getMainHandItem().isEmpty() && isOnFire() && random.nextFloat() < level().getDifficulty().getId() * 0.3F)
				target.setSecondsOnFire(2 * level().getDifficulty().getId());
		}
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 1.5F * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	/** Mobs don't pick up the items of the "mobPickupItemBlacklist" config option */
	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		return !ACConfig.mobItemPickupBlacklist.contains(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()))) && super.wantsToPickUp(stack);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		if(data == null)
			data = new GroupData(random.nextFloat() < ForgeConfig.SERVER.zombieBabyChance.get());
		if(data instanceof GroupData group && group.isBaby)
			setBaby(true);
		setCanPickUpLoot(ACConfig.hardcoreMode || random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
		if(ACConfig.hardcoreMode)
			EntityUtil.suitUp(this, false);
		else {
			populateDefaultEquipmentSlots(random, difficulty);
			populateDefaultEquipmentEnchantments(random, difficulty);
		}
		LocalDate date = LocalDate.now();
		boolean halloween = date.getMonthValue() == 10 && date.getDayOfMonth() == 31;
		if(getItemBySlot(EquipmentSlot.HEAD).isEmpty() && halloween && random.nextFloat() < 0.25F) {
			setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
			armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
		}
		AttributeInstance damage = getAttribute(Attributes.ATTACK_DAMAGE);
		damage.removeModifier(HALLOWEEN_BOOST);
		if(halloween)
			damage.addPermanentModifier(HALLOWEEN_BOOST);
		return data;
	}

	/** Converts zombies, villagers (and turns killed players into one, too) on Normal and Hard */
	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity killed) {
		boolean result = super.killedEntity(level, killed);
		Difficulty difficulty = level.getDifficulty();
		if((difficulty == Difficulty.HARD || difficulty == Difficulty.NORMAL) && canConvert(killed)
				&& (difficulty == Difficulty.HARD || random.nextBoolean())) {
			EntityACZombie converted = createConversion(level);
			if(converted != null) {
				converted.copyPosition(killed);
				if(!(killed instanceof Player))
					killed.discard();
				converted.finalizeSpawn(level, level.getCurrentDifficultyAt(converted.blockPosition()), MobSpawnType.CONVERSION, new GroupData(false), null);
				converted.setBaby(killed.isBaby());
				level.addFreshEntity(converted);
				if(!isSilent())
					level.levelEvent(null, 1026, blockPosition(), 0);
				return false;
			}
		}
		return result;
	}

	/** Which killed mobs turn into this kind of mob */
	protected boolean canConvert(LivingEntity killed) {
		return false;
	}

	@Nullable
	protected EntityACZombie createConversion(ServerLevel level) {
		return null;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(isBaby())
			tag.putBoolean("IsBaby", true);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if(tag.getBoolean("IsBaby"))
			setBaby(true);
	}

	public static class GroupData implements SpawnGroupData {
		public final boolean isBaby;

		public GroupData(boolean isBaby) {
			this.isBaby = isBaby;
		}
	}
}
