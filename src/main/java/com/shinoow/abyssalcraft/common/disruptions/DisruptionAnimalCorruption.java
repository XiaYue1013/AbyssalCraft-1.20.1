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
 *     1.20.1 port - tamed ocelots became cats in 1.14, so tamed cats are the ones that go wild now
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.disruptions;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Shub-Niggurath corrupts the animals nearby: farm animals turn evil, horses undead, tamed pets turn on their owners
 */
public class DisruptionAnimalCorruption extends DisruptionEntry {

	public DisruptionAnimalCorruption() {
		super("animalCorruption", DeityType.SHUBNIGGURATH);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		if(!(world instanceof ServerLevel level)) return;
		List<Animal> animals = world.getEntitiesOfClass(Animal.class, new AABB(pos).inflate(64));
		for(Animal animal : animals)
			if(animal instanceof Cow)
				replace(level, animal, ACEntities.evil_cow);
			else if(animal instanceof Chicken)
				replace(level, animal, ACEntities.evil_chicken);
			else if(animal instanceof Pig)
				replace(level, animal, ACEntities.evil_pig);
			else if(animal instanceof Sheep)
				replace(level, animal, ACEntities.evil_sheep);
			else if(animal instanceof AbstractHorse && animal.getMobType() != MobType.UNDEAD){
				AbstractHorse horse = (world.random.nextBoolean() ? EntityType.ZOMBIE_HORSE : EntityType.SKELETON_HORSE).create(level);
				if(horse == null) continue;
				horse.copyPosition(animal);
				horse.setAge(animal.getAge());
				animal.die(world.damageSources().generic());
				animal.discard();
				level.addFreshEntity(horse);
			}
			else if(animal instanceof Wolf wolf && wolf.isTame() && !players.isEmpty()){
				wolf.setTame(false);
				wolf.setOwnerUUID(null);
				Player target = players.get(world.random.nextInt(players.size()));
				wolf.setPersistentAngerTarget(target.getUUID());
				wolf.startPersistentAngerTimer();
				wolf.setTarget(target);
			}
			else if(animal instanceof Cat cat && cat.isTame()){
				cat.setTame(false);
				cat.setOwnerUUID(null);
				cat.setOrderedToSit(false);
			}
			else if(animal instanceof Rabbit rabbit && rabbit.getVariant() != Rabbit.Variant.EVIL)
				rabbit.setVariant(Rabbit.Variant.EVIL);
	}

	/** Replaces an animal with its evil variant (nothing happens while that mob isn't registered) */
	private static void replace(ServerLevel level, Animal animal, String id) {
		ResourceLocation key = new ResourceLocation(AbyssalCraft.modid, id);
		if(!ForgeRegistries.ENTITY_TYPES.containsKey(key)) return;
		if(ForgeRegistries.ENTITY_TYPES.getValue(key).create(level) instanceof Mob evil) {
			evil.copyPosition(animal);
			evil.finalizeSpawn(level, level.getCurrentDifficultyAt(animal.blockPosition()), MobSpawnType.CONVERSION, null, null);
			animal.discard();
			level.addFreshEntity(evil);
		}
	}
}
