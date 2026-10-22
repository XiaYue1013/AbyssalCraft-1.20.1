/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAntiPig, EntityAntiCow, EntityAntiChicken, EntityAntiBat)
 *     1.20.1 port - built on the vanilla animals, with what 1.12.2 didn't have switched off
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.anti;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** The passive antimatter mobs: they breed like the real ones and blow up when they touch them */
public class AntiAnimals {

	private static ResourceLocation loot(String name) {
		return AbyssalCraft.loc("entities/" + name);
	}

	/** Entity#thunderHit: just burns and hurts (vanilla pigs become zombified piglins) */
	private static void thunderHit(Entity entity, LightningBolt bolt) {
		entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
		if(entity.getRemainingFireTicks() == 0)
			entity.setSecondsOnFire(8);
		entity.hurt(entity.damageSources().lightningBolt(), 5.0F);
	}

	public static class AntiPig extends Pig implements IAntiEntity {

		public AntiPig(EntityType<? extends AntiPig> type, Level level) {
			super(type, level);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Pig.createAttributes().add(Attributes.MAX_HEALTH, 20.0D);
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Pig.class))
				super.doPush(entity);
		}

		/** Carrots only (1.12.2) */
		@Override
		public boolean isFood(ItemStack stack) {
			return stack.is(Items.CARROT);
		}

		@Override
		public boolean isSaddleable() {
			return false;
		}

		@Override
		public void thunderHit(ServerLevel level, LightningBolt bolt) {
			AntiAnimals.thunderHit(this, bolt);
		}

		@Nullable
		@Override
		public Pig getBreedOffspring(ServerLevel level, AgeableMob mate) {
			return ACEntityTypes.ANTI_PIG.get().create(level);
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_pig");
		}
	}

	public static class AntiCow extends Cow implements IAntiEntity {

		public AntiCow(EntityType<? extends AntiCow> type, Level level) {
			super(type, level);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Cow.createAttributes().add(Attributes.MAX_HEALTH, 20.0D);
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Cow.class))
				super.doPush(entity);
		}

		/** Milked, it gives Liquid Antimatter */
		@Override
		public InteractionResult mobInteract(Player player, InteractionHand hand) {
			ItemStack stack = player.getItemInHand(hand);
			if(stack.is(Items.BUCKET) && !isBaby()) {
				player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(ACItems.liquid_antimatter_bucket.get())));
				return InteractionResult.sidedSuccess(level().isClientSide);
			}
			return super.mobInteract(player, hand);
		}

		@Nullable
		@Override
		public Cow getBreedOffspring(ServerLevel level, AgeableMob mate) {
			return ACEntityTypes.ANTI_COW.get().create(level);
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_cow");
		}
	}

	public static class AntiChicken extends Chicken implements IAntiEntity {

		public AntiChicken(EntityType<? extends AntiChicken> type, Level level) {
			super(type, level);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Chicken.createAttributes().add(Attributes.MAX_HEALTH, 8.0D);
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, Chicken.class))
				super.doPush(entity);
		}

		@Nullable
		@Override
		public Chicken getBreedOffspring(ServerLevel level, AgeableMob mate) {
			return ACEntityTypes.ANTI_CHICKEN.get().create(level);
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_chicken");
		}
	}

	/** (the 1.12.2 Anti Bat never collided with anything, like the vanilla bat) */
	public static class AntiBat extends Bat implements IAntiEntity {

		public AntiBat(EntityType<? extends AntiBat> type, Level level) {
			super(type, level);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Bat.createAttributes().add(Attributes.MAX_HEALTH, 12.0D);
		}

		@Override
		protected ResourceLocation getDefaultLootTable() {
			return loot("anti_bat");
		}
	}
}
