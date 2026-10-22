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
 *     1.20.1 port - the 1.12.2 effect classes replaced dying entities when the corpse was removed (end of the death
 *     animation, after the loot dropped). That now happens in onEntityRemoved, driven by the conversion tables below.
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.util.ArmorUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class PlagueEventHandler {

	/** A dying entity matching the predicate is replaced by the entity with the ID */
	private record Conversion(BiPredicate<LivingEntity, ServerLevel> test, String result) {}

	private static final List<Conversion> CORALIUM = new ArrayList<>(), DREAD = new ArrayList<>(), ANTIMATTER = new ArrayList<>();

	static {
		BiPredicate<LivingEntity, ServerLevel> abyssalZombieChance = (e, w) -> w.getDifficulty() == Difficulty.HARD && w.random.nextBoolean() || w.random.nextInt(8) == 0;
		CORALIUM.add(new Conversion((e, w) -> e instanceof Zombie && w.getLevelData().isHardcore() && w.random.nextInt(10) == 0, ACEntities.depths_ghoul));
		CORALIUM.add(new Conversion((e, w) -> e instanceof Zombie && abyssalZombieChance.test(e, w), ACEntities.abyssal_zombie));
		CORALIUM.add(new Conversion((e, w) -> e instanceof Player && abyssalZombieChance.test(e, w), ACEntities.abyssal_zombie));
		CORALIUM.add(new Conversion((e, w) -> e instanceof Squid && w.random.nextBoolean(), ACEntities.coralium_squid));

		DREAD.add(new Conversion((e, w) -> e instanceof Zombie || e instanceof Skeleton || is(e, ACEntities.abyssal_zombie) || is(e, ACEntities.anti_player)
				|| is(e, ACEntities.abyssal_anti_zombie) || is(e, ACEntities.anti_zombie) || is(e, ACEntities.anti_skeleton), ACEntities.dreadling));
		DREAD.add(new Conversion((e, w) -> e instanceof Player, ACEntities.dreadling));
		DREAD.add(new Conversion((e, w) -> is(e, ACEntities.skeleton_goliath), ACEntities.dreadguard));
		DREAD.add(new Conversion((e, w) -> e instanceof Pig, ACEntities.demon_pig));
		DREAD.add(new Conversion((e, w) -> e instanceof Cow, ACEntities.demon_cow));
		DREAD.add(new Conversion((e, w) -> e instanceof Chicken, ACEntities.demon_chicken));
		DREAD.add(new Conversion((e, w) -> e instanceof Sheep, ACEntities.demon_sheep));
		DREAD.add(new Conversion((e, w) -> is(e, ACEntities.abyssalnite_golem), ACEntities.dreaded_abyssalnite_golem));
		DREAD.add(new Conversion((e, w) -> true, ACEntities.dread_spawn));

		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Zombie, ACEntities.anti_zombie));
		ANTIMATTER.add(new Conversion((e, w) -> is(e, ACEntities.abyssal_zombie), ACEntities.abyssal_anti_zombie));
		ANTIMATTER.add(new Conversion((e, w) -> is(e, ACEntities.depths_ghoul), ACEntities.anti_ghoul));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Bat, ACEntities.anti_bat));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Chicken || is(e, ACEntities.demon_chicken), ACEntities.anti_chicken));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Cow || is(e, ACEntities.demon_cow), ACEntities.anti_cow));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Creeper, ACEntities.anti_creeper));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Pig || is(e, ACEntities.demon_pig), ACEntities.anti_pig));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Player, ACEntities.anti_player));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Skeleton, ACEntities.anti_skeleton));
		ANTIMATTER.add(new Conversion((e, w) -> e instanceof Spider, ACEntities.anti_spider));
	}

	private static boolean is(Entity entity, String id) {
		return new ResourceLocation(AbyssalCraft.modid, id).equals(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()));
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		LivingEntity entity = event.getEntity();
		if(entity.level().isClientSide || entity.level().getBiome(entity.blockPosition()).is(ACBiomes.purged)) return;

		if(entity.hasEffect(AbyssalCraftAPI.coralium_plague.get()))
			if(entity.getRandom().nextFloat() > 0.1F && !ACConfig.no_potion_clouds)
				createCloud(entity, AbyssalCraftAPI.coralium_plague.get());
			else spreadOnDeath(entity, AbyssalCraftAPI.coralium_plague.get());
		if(entity.hasEffect(AbyssalCraftAPI.dread_plague.get())) {
			if(entity.getRandom().nextFloat() > 0.1F && !ACConfig.no_potion_clouds)
				createCloud(entity, AbyssalCraftAPI.dread_plague.get());
			else spreadOnDeath(entity, AbyssalCraftAPI.dread_plague.get());
		} else if(EntityUtil.isDreadPlagueCarrier(entity) && !is(entity, ACEntities.demon_pig) && !is(entity, ACEntities.demon_cow)
				&& !is(entity, ACEntities.demon_chicken) && !is(entity, ACEntities.demon_sheep) && entity.level().dimension() != ACDimensions.DREADLANDS
				&& entity.level().dimension() != ACDimensions.OMOTHOL && entity.level().dimension() != ACDimensions.DARK_REALM)
			createCloud(entity, AbyssalCraftAPI.dread_plague.get());
	}

	/** Players aren't removed when they die, so they're converted at the moment of death */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDeath(LivingDeathEvent event) {
		if(!event.isCanceled() && event.getEntity() instanceof Player player && player.level() instanceof ServerLevel level) {
			// Forge fires LivingDeathEvent in both Player.die and LivingEntity.die for mock/base players.
			String key = "abyssalcraft:converted_death_tick";
			if(player.getPersistentData().contains(key) && player.getPersistentData().getLong(key) == level.getGameTime()) return;
			player.getPersistentData().putLong(key, level.getGameTime());
			convert(player, level);
		}
	}

	/** Mobs are converted when their corpse is removed, after the death animation */
	@SubscribeEvent
	public void onEntityRemoved(EntityLeaveLevelEvent event) {
		if(event.getEntity() instanceof LivingEntity entity && !(entity instanceof Player) && entity.level() instanceof ServerLevel level
				&& entity.getRemovalReason() == Entity.RemovalReason.KILLED && entity.isDeadOrDying())
			level.getServer().execute(() -> convert(entity, level));
	}

	private static void convert(LivingEntity entity, ServerLevel level) {
		if(AbyssalCraftEventHooks.convertToShadow(entity, level)) return;
		if(level.getBiome(entity.blockPosition()).is(ACBiomes.purged)) return;
		if(entity.hasEffect(AbyssalCraftAPI.coralium_plague.get()) && !EntityUtil.isEntityCoralium(entity)
				&& !ArmorUtil.hasHelmetWithResistance(entity, AbyssalCraftAPI.coralium) && apply(CORALIUM, entity, level))
			return;
		if(entity.hasEffect(AbyssalCraftAPI.dread_plague.get()) && !EntityUtil.isEntityDread(entity)
				&& !ArmorUtil.hasHelmetWithResistance(entity, AbyssalCraftAPI.dread) && level.random.nextBoolean() && apply(DREAD, entity, level))
			return;
		if(entity.hasEffect(AbyssalCraftAPI.antimatter_potion.get()) && !EntityUtil.isEntityAnti(entity)
				&& !entity.getItemBySlot(EquipmentSlot.CHEST).is(ACItems.ethaxium_chestplate.get()))
			apply(ANTIMATTER, entity, level);
	}

	private static boolean apply(List<Conversion> conversions, LivingEntity entity, ServerLevel level) {
		for(Conversion conversion : conversions)
			if(conversion.test().test(entity, level)) {
				ResourceLocation id = new ResourceLocation(AbyssalCraft.modid, conversion.result());
				if(!ForgeRegistries.ENTITY_TYPES.containsKey(id)) return false;
				Entity result = ForgeRegistries.ENTITY_TYPES.getValue(id).create(level);
				if(result == null) return false;
				result.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
				if(result instanceof Mob mob) {
					if(!conversion.result().equals(ACEntities.dread_spawn))
						mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.CONVERSION, null, null);
					if(entity.isBaby())
						mob.setBaby(true);
					if(entity instanceof Player player && conversion.result().equals(ACEntities.anti_player)) {
						mob.setCustomName(Component.literal(reverseName(player.getName().getString())));
						mob.setPersistenceRequired();
					}
				}
				level.addFreshEntity(result);
				return true;
			}
		return false;
	}

	/** Reverses the name, keeping the upper case letters at their positions */
	public static String reverseName(String name) {
		StringBuilder newname = new StringBuilder();
		int length = name.length();
		for(int i = length - 1; i >= 0; i--) {
			char c = name.charAt(i);
			newname.append(Character.isUpperCase(name.charAt(length - 1 - i)) ? Character.toUpperCase(c) : Character.toLowerCase(c));
		}
		return newname.toString();
	}

	private static void spreadOnDeath(LivingEntity entity, MobEffect effect) {
		AABB axisalignedbb = entity.getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
		List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
		MobEffectInstance potioneffect = entity.getEffect(effect);

		for (LivingEntity entitylivingbase : list)
			if (entitylivingbase.isAffectedByPotions() && !EntityUtil.isEntityImmune(entitylivingbase, effect))
			{
				double d0 = entity.distanceToSqr(entitylivingbase);

				if (d0 < 32.0D)
				{
					double d1 = 1.0D - Math.sqrt(d0) / 8.0D;

					int i = (int)(d1 * potioneffect.getDuration() + 0.5D);

					if (i > 20)
						entitylivingbase.addEffect(new MobEffectInstance(effect, i, potioneffect.getAmplifier(), potioneffect.isAmbient(), potioneffect.isVisible()));
				}
			}
		entity.level().levelEvent(2002, entity.blockPosition(), PotionUtils.getColor(Collections.singletonList(potioneffect)));
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event){
		LivingEntity target = event.getEntity();
		if(target.level().isClientSide || target.level().getBiome(target.blockPosition()).is(ACBiomes.purged)) return;

		DamageSource source = event.getSource();
		if(source.getEntity() instanceof LivingEntity attacker) {
			MobEffect coralium = AbyssalCraftAPI.coralium_plague.get(), dread = AbyssalCraftAPI.dread_plague.get();
			if(attacker.hasEffect(coralium) && !EntityUtil.isEntityCoralium(target))
				if(target.getRandom().nextFloat() > 0.5F)
					target.addEffect(getEffect(attacker.getEffect(coralium)));
			if(attacker.hasEffect(dread) && !EntityUtil.isEntityDread(target))
				if(target.getRandom().nextFloat() > 0.5F) {
					MobEffectInstance effect = target.getEffect(dread);
					effect = effect != null && effect.getAmplifier() > 0 ? getEffect(effect, effect.getAmplifier()) : getEffect(attacker.getEffect(dread));
					target.addEffect(effect);
				}
		}
	}

	public static MobEffectInstance getEffect(MobEffectInstance effect) {
		return getEffect(effect, effect.getAmplifier());
	}

	public static MobEffectInstance getEffect(MobEffectInstance effect, int amplifier) {
		int duration = effect.getDuration() >= 600 ? effect.getDuration() : 600;

		return new MobEffectInstance(effect.getEffect(), duration, amplifier);
	}

	public static void createCloud(LivingEntity entity, MobEffect potion) {
		AABB axisalignedbb = entity.getBoundingBox().inflate(3.0D, 3.0D, 3.0D);
		List<AreaEffectCloud> list = entity.level().getEntitiesOfClass(AreaEffectCloud.class, axisalignedbb);
		if(list.isEmpty() && !ACConfig.no_potion_clouds) {
			AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
			entityareaeffectcloud.addEffect(new MobEffectInstance(potion, 600));
			entityareaeffectcloud.setRadius(entity.getBbWidth());
			entityareaeffectcloud.setDuration(100 + entity.getRandom().nextInt(100));
			entityareaeffectcloud.setRadiusPerTick((3F - entityareaeffectcloud.getRadius()) / entityareaeffectcloud.getDuration());

			entity.level().addFreshEntity(entityareaeffectcloud);
		}
	}
}
