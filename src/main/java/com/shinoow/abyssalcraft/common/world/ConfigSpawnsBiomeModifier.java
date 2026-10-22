/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - mob spawns in other mods'/vanilla biomes that follow config options (1.12.2 EntityHandler
 *     added them with EntityRegistry.addSpawn when the option was enabled)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Adds a spawn to the biomes if the config option is enabled. The weight can come from a config option too.
 * <code>{"type": "abyssalcraft:config_spawns", "biomes": "#...", "option": "depthsGhoulBiomeTagSpawning",
 * "spawn": {"type": "abyssalcraft:depths_ghoul", "weight": 10, "minCount": 1, "maxCount": 3}}</code>
 */
public record ConfigSpawnsBiomeModifier(HolderSet<Biome> biomes, String option, MobSpawnSettings.SpawnerData spawn, String weightOption) implements BiomeModifier {

	private static final Map<String, BooleanSupplier> OPTIONS = Map.of(
			"depthsGhoulBiomeTagSpawning", () -> ACConfig.depthsGhoulBiomeDictSpawn,
			"abyssalZombieBiomeTagSpawning", () -> ACConfig.abyssalZombieBiomeDictSpawn,
			"spectralDragons", () -> !ACConfig.no_spectral_dragons,
			"always", () -> true);
	private static final Map<String, IntSupplier> WEIGHTS = Map.of(
			"evilAnimalSpawnWeight", () -> ACConfig.evilAnimalSpawnWeight,
			"demonAnimalSpawnWeight", () -> ACConfig.demonAnimalSpawnWeight,
			"darkOffspringSpawnWeight", () -> ACConfig.darkOffspringSpawnWeight,
			"darkOffspringSpawnWeightDouble", () -> ACConfig.darkOffspringSpawnWeight * 2);

	public static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AbyssalCraft.modid);

	public static final RegistryObject<Codec<ConfigSpawnsBiomeModifier>> CODEC = SERIALIZERS.register("config_spawns", () -> RecordCodecBuilder.create(i -> i.group(
			Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigSpawnsBiomeModifier::biomes),
			Codec.STRING.comapFlatMap(o -> OPTIONS.containsKey(o) ? DataResult.success(o) : DataResult.error(() -> "Unknown option " + o), o -> o)
			.fieldOf("option").forGetter(ConfigSpawnsBiomeModifier::option),
			MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawn").forGetter(ConfigSpawnsBiomeModifier::spawn),
			Codec.STRING.comapFlatMap(o -> o.isEmpty() || WEIGHTS.containsKey(o) ? DataResult.success(o) : DataResult.error(() -> "Unknown weight option " + o), o -> o)
			.optionalFieldOf("weight_option", "").forGetter(ConfigSpawnsBiomeModifier::weightOption))
			.apply(i, ConfigSpawnsBiomeModifier::new)));

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if(phase != Phase.ADD || !biomes.contains(biome) || !OPTIONS.get(option).getAsBoolean())
			return;
		MobSpawnSettings.SpawnerData data = spawn;
		if(!weightOption.isEmpty()) {
			int weight = WEIGHTS.get(weightOption).getAsInt();
			if(weight <= 0)
				return;
			data = new MobSpawnSettings.SpawnerData(spawn.type, weight, spawn.minCount, spawn.maxCount);
		}
		builder.getMobSpawnSettings().addSpawn(data.type.getCategory(), data);
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return CODEC.get();
	}
}
