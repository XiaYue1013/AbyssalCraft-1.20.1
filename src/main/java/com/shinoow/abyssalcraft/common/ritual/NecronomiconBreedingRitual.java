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
 *     1.20.1 port - the per animal blocks merged into one loop over the animal kinds; tamed ocelots are cats now
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Every pair of animals of the same kind nearby gets a baby. Large herds risk one of them dying
 * (farm animals turn into their evil versions instead).
 */
public class NecronomiconBreedingRitual extends NecronomiconRitual {

	public NecronomiconBreedingRitual() {
		super("breeding", 0, 500F, new Object[]{Items.WHEAT, Items.POTATO, Items.CARROT, Items.MELON_SLICE,
				Blocks.PUMPKIN, Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS});
		setRitualParticle(EnumRitualParticle.SMOKE_PILLARS);
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return !world.getEntitiesOfClass(Animal.class, new AABB(pos).inflate(16, 3, 16)).isEmpty();
	}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player){
		if(!(world instanceof ServerLevel level)) return;
		// animal kinds in the 1.12.2 order, with the evil replacement (null = the animal dies instead)
		Map<Class<? extends Animal>, String> kinds = new LinkedHashMap<>();
		kinds.put(Cow.class, ACEntities.evil_cow);
		kinds.put(Chicken.class, ACEntities.evil_chicken);
		kinds.put(Pig.class, ACEntities.evil_pig);
		kinds.put(Sheep.class, ACEntities.evil_sheep);
		kinds.put(Horse.class, null);
		kinds.put(Cat.class, null);
		kinds.put(Ocelot.class, null);
		kinds.put(Wolf.class, null);
		kinds.put(Rabbit.class, null);

		Map<Class<? extends Animal>, List<Animal>> animals = new LinkedHashMap<>();
		for(Entity entity : world.getEntities(player, new AABB(pos).inflate(16, 3, 16)))
			for(Class<? extends Animal> kind : kinds.keySet())
				if(kind.isInstance(entity))
					animals.computeIfAbsent(kind, k -> new ArrayList<>()).add((Animal) entity);

		animals.forEach((kind, list) -> {
			int pairs = list.size() / 2;
			for(int i = 0; i < pairs; i++){
				Animal parent = list.get(i);
				AgeableMob child = parent.getBreedOffspring(level, parent);
				if(child != null) {
					child.setAge(-24000);
					child.moveTo(parent.getX(), parent.getY(), parent.getZ(), parent.getYRot(), parent.getXRot());
					level.addFreshEntityWithPassengers(child);
				}
			}
		});

		animals.forEach((kind, list) -> {
			if(list.size() > 2 && getDeathChance(world.random, list.size())) {
				Animal victim = list.get(world.random.nextInt(list.size()));
				String evil = kinds.get(kind);
				if(evil != null) {
					ResourceLocation id = new ResourceLocation(AbyssalCraft.modid, evil);
					if(ForgeRegistries.ENTITY_TYPES.containsKey(id) && ForgeRegistries.ENTITY_TYPES.getValue(id).create(level) instanceof Mob replacement) {
						replacement.moveTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot());
						replacement.finalizeSpawn(level, level.getCurrentDifficultyAt(victim.blockPosition()), MobSpawnType.CONVERSION, null, null);
						victim.discard();
						level.addFreshEntity(replacement);
					}
				} else victim.hurt(world.damageSources().magic(), 200000);
			}
		});
	}

	private boolean getDeathChance(RandomSource rand, int num){
		if(num < 5)
			return rand.nextInt(10) == 0;
		if(num > 5 && num < 10)
			return rand.nextInt(9) == 0;
		if(num > 10 && num < 15)
			return rand.nextInt(8) == 0;
		if(num > 15 && num < 20)
			return rand.nextInt(7) == 0;
		if(num > 20 && num < 25)
			return rand.nextInt(6) == 0;
		if(num > 25 && num < 30)
			return rand.nextInt(5) == 0;
		if(num > 30 && num < 35)
			return rand.nextInt(4) == 0;
		if(num > 35 && num < 40)
			return rand.nextInt(3) == 0;
		if(num > 40 && num < 45)
			return rand.nextInt(2) == 0;
		return true;
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player){}
}
