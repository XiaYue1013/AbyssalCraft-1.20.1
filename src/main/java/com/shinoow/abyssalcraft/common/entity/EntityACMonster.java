/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - what most 1.12.2 AbyssalCraft EntityMob subclasses repeated
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * Base of the AbyssalCraft monsters: Hardcore Mode stats and extra damage, the Halloween pumpkin and damage boost, a
 * named entity loot table, and a hook for what their hits do.
 */
public abstract class EntityACMonster extends Monster {

	private static final UUID HALLOWEEN_BOOST_UUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");

	private final String lootTable;

	/** @param lootTable entity loot table name (entities/...) */
	protected EntityACMonster(EntityType<? extends EntityACMonster> type, Level level, String lootTable) {
		super(type, level);
		this.lootTable = lootTable;
	}

	/** Sets the Hardcore Mode health and damage (1.12.2 applyEntityAttributes); call from the constructor */
	protected void hardcore(double health, double damage) {
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
			setHealth(getMaxHealth());
		}
	}

	/** Extra absolute damage to players in Hardcore Mode (times the damage amplifier), 0 for none */
	protected float hardcoreDamage() {
		return 1.5F;
	}

	/** Called when a melee hit lands */
	protected void onHit(LivingEntity target) {}

	/** Gives a hit target an effect for 5 seconds */
	protected static void effect(LivingEntity target, MobEffect effect) {
		target.addEffect(new MobEffectInstance(effect, 100));
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if(flag && target instanceof LivingEntity living)
			onHit(living);
		if(ACConfig.hardcoreMode && hardcoreDamage() > 0 && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), hardcoreDamage() * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	/** Whether it may wear a pumpkin on Halloween (25 %) */
	protected boolean halloweenPumpkin() {
		return true;
	}

	/** Extra attack damage on Halloween, 0 for none */
	protected double halloweenDamage() {
		return 0;
	}

	public static boolean isHalloween() {
		LocalDate date = LocalDate.now();
		return date.getMonthValue() == 10 && date.getDayOfMonth() == 31;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		boolean halloween = isHalloween();
		if(halloweenPumpkin() && halloween && getItemBySlot(EquipmentSlot.HEAD).isEmpty() && random.nextFloat() < 0.25F) {
			setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
			setDropChance(EquipmentSlot.HEAD, 0.0F);
		}
		if(halloweenDamage() > 0) {
			AttributeInstance attribute = getAttribute(Attributes.ATTACK_DAMAGE);
			attribute.removeModifier(HALLOWEEN_BOOST_UUID);
			if(halloween)
				attribute.addPermanentModifier(new AttributeModifier(HALLOWEEN_BOOST_UUID, "Halloween Attack Damage Boost", halloweenDamage(), AttributeModifier.Operation.ADDITION));
		}
		return data;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/" + lootTable);
	}
}
