/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - automated verification (./gradlew runGameTestServer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.EntityDreadguard;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Dreadguard
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class DreadguardTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	/** Spawns in dreaded abyssalnite armor, is undead and fire immune, gives the Dread Plague in melee, drops dreaded shards */
	@GameTest(template = "empty")
	public static void dreadguardBasics(GameTestHelper helper) {
		floor(helper);
		EntityDreadguard guard = helper.spawn(ACEntityTypes.DREADGUARD.get(), new BlockPos(2, 1, 2));
		guard.setNoAi(true);
		guard.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(guard.blockPosition()), MobSpawnType.SPAWN_EGG, null, null);
		helper.assertTrue(guard.getItemBySlot(EquipmentSlot.CHEST).is(ACItems.dreaded_abyssalnite_chestplate.get())
				&& guard.getItemBySlot(EquipmentSlot.FEET).is(ACItems.dreaded_abyssalnite_boots.get()), "no dreaded abyssalnite armor");
		helper.assertTrue(guard.getMobType() == MobType.UNDEAD && guard.fireImmune(), "not undead / fire immune");
		helper.assertTrue(helper.getLevel().getServer().getLootData().getLootTable(guard.getLootTable()) != LootTable.EMPTY, "no loot table");
		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(3, 1, 2));
		pig.setNoAi(true);
		pig.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(200);
		pig.setHealth(200);
		helper.assertTrue(guard.doHurtTarget(pig), "melee missed");
		helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.dread_plague.get()), "no Dread Plague from the hit");
		// equipment attributes apply on the next tick: armor pieces + the 2 extra points
		helper.succeedWhen(() -> helper.assertTrue(guard.getArmorValue() > 2, "armor " + guard.getArmorValue()));
	}

	/** With a target within 8 blocks it stops and vomits at what it looks at: Dread damage and Dread Plague, but not on itself */
	@GameTest(template = "empty", timeoutTicks = 80)
	public static void dreadguardBarfs(GameTestHelper helper) {
		floor(helper);
		EntityDreadguard guard = helper.spawn(ACEntityTypes.DREADGUARD.get(), new BlockPos(1, 1, 3));
		guard.setNoAi(true);
		IronGolem golem = helper.spawn(EntityType.IRON_GOLEM, new BlockPos(4, 1, 3));
		golem.setNoAi(true);
		guard.lookAt(EntityAnchorArgument.Anchor.EYES, golem.getEyePosition());
		guard.setTarget(golem);
		guard.setBarfTimer(-200);
		helper.succeedWhen(() -> {
			helper.assertTrue(guard.getBarfTimer() > 0, "not barfing (timer " + guard.getBarfTimer() + ")");
			helper.assertTrue(golem.getHealth() < golem.getMaxHealth() && golem.hasEffect(AbyssalCraftAPI.dread_plague.get()), "the golem wasn't hit");
			helper.assertTrue(guard.getHealth() == guard.getMaxHealth(), "hurt itself");
		});
	}

	/** Dreadlands spawns: 8 in every Dreadlands biome, +1 in the Purified Dreadlands, +20 (1 - 2) in the mountains */
	@GameTest(template = "empty")
	public static void dreadguardSpawns(GameTestHelper helper) {
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		for(var key : java.util.List.of(ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest, ACBiomes.dreadlands_mountains)) {
			int weight = biomes.getOrThrow(key).getMobSettings().getMobs(MobCategory.MONSTER).unwrap().stream()
					.filter(m -> m.type == ACEntityTypes.DREADGUARD.get()).mapToInt(m -> m.getWeight().asInt()).sum();
			int expected = key == ACBiomes.purified_dreadlands ? 9 : key == ACBiomes.dreadlands_mountains ? 28 : 8;
			helper.assertTrue(weight == expected, key.location() + " Dreadguard weight " + weight);
		}
		helper.succeed();
	}
}
