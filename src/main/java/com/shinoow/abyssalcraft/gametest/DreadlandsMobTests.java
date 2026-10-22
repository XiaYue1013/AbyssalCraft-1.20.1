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

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.*;
import com.shinoow.abyssalcraft.common.entity.demon.EntityDemonAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilSheep;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Dreadlands, Darklands and Omothol mobs of Phase 6 part 4 and the Evil/Demon Animals
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class DreadlandsMobTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	private static <T extends Mob> T mob(GameTestHelper helper, EntityType<T> type, BlockPos pos) {
		T mob = helper.spawn(type, pos);
		mob.setNoAi(true);
		return mob;
	}

	private static Pig sturdyPig(GameTestHelper helper, BlockPos pos) {
		Pig pig = mob(helper, EntityType.PIG, pos);
		pig.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500);
		pig.setHealth(500);
		return pig;
	}

	private static AABB area(GameTestHelper helper) {
		return new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
	}

	/** Every new mob has its loot table; the fire immune ones are; hits do what they did in 1.12.2 */
	@GameTest(template = "empty")
	public static void mobBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		List<EntityType<? extends Mob>> types = List.of(ACEntityTypes.DREADLING.get(), ACEntityTypes.ABYSSALNITE_GOLEM.get(), ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(),
				ACEntityTypes.SKELETON_GOLIATH.get(), ACEntityTypes.OMOTHOL_GHOUL.get(), ACEntityTypes.EVIL_PIG.get(), ACEntityTypes.EVIL_COW.get(),
				ACEntityTypes.EVIL_CHICKEN.get(), ACEntityTypes.EVIL_SHEEP.get(), ACEntityTypes.DEMON_PIG.get(), ACEntityTypes.DEMON_COW.get(),
				ACEntityTypes.DEMON_CHICKEN.get(), ACEntityTypes.DEMON_SHEEP.get());
		List<EntityType<?>> notFireImmune = List.of(ACEntityTypes.ABYSSALNITE_GOLEM.get(), ACEntityTypes.SKELETON_GOLIATH.get());
		for(EntityType<? extends Mob> type : types) {
			Mob mob = mob(helper, type, new BlockPos(1, 1, 1));
			helper.assertTrue(level.getServer().getLootData().getLootTable(mob.getLootTable()) != LootTable.EMPTY, type + " has no loot table");
			helper.assertTrue(mob.fireImmune() != notFireImmune.contains(type), type + " fire immunity");
			mob.discard();
		}
		EntityDreadling dreadling = mob(helper, ACEntityTypes.DREADLING.get(), new BlockPos(1, 1, 1));
		Pig pig = sturdyPig(helper, new BlockPos(2, 1, 1));
		helper.assertTrue(dreadling.doHurtTarget(pig) && pig.hasEffect(AbyssalCraftAPI.dread_plague.get()), "the Dreadling didn't give the Dread Plague");
		EntityOmotholGhoul ghoul = mob(helper, ACEntityTypes.OMOTHOL_GHOUL.get(), new BlockPos(5, 1, 5));
		Pig pig2 = sturdyPig(helper, new BlockPos(6, 1, 5));
		helper.assertTrue(ghoul.doHurtTarget(pig2) && pig2.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && pig2.hasEffect(MobEffects.BLINDNESS), "the Omothol Ghoul's hit");
		helper.succeed();
	}

	/** The Skeleton Goliath always holds its cudgel (which it doesn't drop from the hand) */
	@GameTest(template = "empty")
	public static void skeletonGoliathCudgel(GameTestHelper helper) {
		floor(helper);
		EntitySkeletonGoliath goliath = mob(helper, ACEntityTypes.SKELETON_GOLIATH.get(), new BlockPos(4, 1, 4));
		helper.assertTrue(goliath.getMainHandItem().is(ACItems.cudgel.get()), "holds " + goliath.getMainHandItem());
		ItemStack cudgel = new ItemStack(ACItems.cudgel.get());
		helper.assertTrue(cudgel.getMaxDamage() == 1500 && !cudgel.isEnchantable(), "cudgel durability / enchantability");
		helper.assertTrue(cudgel.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(m -> m.getAmount()).sum() == 19,
				"cudgel damage modifier");
		goliath.discard();
		helper.succeed();
	}

	/** Killed, an Evil Pig turns into a Demon Pig; sheared, an Evil Cow drops leather and turns into a Demon Cow */
	@GameTest(template = "empty")
	public static void evilAnimalsTurnDemonic(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntityEvilAnimal pig = mob(helper, ACEntityTypes.EVIL_PIG.get(), new BlockPos(2, 1, 2));
		helper.assertTrue(pig.getName().getString().equals(EntityType.PIG.getDescription().getString()), "the Evil Pig is called " + pig.getName().getString());
		pig.hurt(level.damageSources().generic(), 1000);
		EntityEvilAnimal cow = mob(helper, ACEntityTypes.EVIL_COW.get(), new BlockPos(6, 1, 6));
		helper.assertTrue(cow.isShearable(new ItemStack(Items.SHEARS), level, cow.blockPosition()), "not shearable");
		List<ItemStack> drops = cow.onSheared(null, new ItemStack(Items.SHEARS), level, cow.blockPosition(), 0);
		helper.assertTrue(!drops.isEmpty() && drops.size() <= 3 && drops.stream().allMatch(s -> s.is(Items.LEATHER)), "shearing gave " + drops);
		helper.succeedWhen(() -> {
			var demons = level.getEntitiesOfClass(EntityDemonAnimal.class, area(helper));
			helper.assertTrue(demons.stream().anyMatch(d -> d.getType() == ACEntityTypes.DEMON_PIG.get()), "no Demon Pig");
			helper.assertTrue(demons.stream().anyMatch(d -> d.getType() == ACEntityTypes.DEMON_COW.get()), "no Demon Cow");
			helper.assertTrue(cow.isRemoved() && pig.isRemoved(), "the evil animals are still here");
			demons.forEach(Entity::discard);
			level.getEntitiesOfClass(ItemEntity.class, area(helper)).forEach(Entity::discard);
		});
	}

	/** An Evil Sheep that killed a player wears the player's skin: shearing it gives rotten flesh */
	@GameTest(template = "empty")
	public static void evilSheepRemembersPlayer(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntityEvilSheep sheep = mob(helper, ACEntityTypes.EVIL_SHEEP.get(), new BlockPos(4, 1, 4));
		helper.assertFalse(sheep.hasKilledPlayer(), "starts with a player");
		helper.assertTrue(sheep.onSheared(null, new ItemStack(Items.SHEARS), level, sheep.blockPosition(), 0).get(0).is(Items.WHITE_WOOL), "plain wool");
		EntityEvilSheep killer = mob(helper, ACEntityTypes.EVIL_SHEEP.get(), new BlockPos(2, 1, 2));
		Player player = helper.makeMockPlayer();
		killer.setKilledPlayer(player);
		helper.assertTrue(killer.hasKilledPlayer() && killer.getKilledPlayerName().equals(player.getGameProfile().getName()), "player not remembered");
		helper.assertTrue(killer.onSheared(null, new ItemStack(Items.SHEARS), level, killer.blockPosition(), 0).get(0).is(Items.ROTTEN_FLESH), "no rotten flesh");
		helper.succeedWhen(() -> {
			level.getEntitiesOfClass(EntityDemonAnimal.class, area(helper)).forEach(Entity::discard);
			helper.assertTrue(true, "");
		});
	}

	/** Golems fight each other: the Abyssalnite Golem goes for Dreaded ones only, the Dreaded ones also for players */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void golemsFight(GameTestHelper helper) {
		floor(helper);
		EntityAbyssalniteGolem golem = helper.spawn(ACEntityTypes.ABYSSALNITE_GOLEM.get(), new BlockPos(2, 1, 2));
		EntityAbyssalniteGolem.Dreaded dreaded = helper.spawn(ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(), new BlockPos(5, 1, 5));
		helper.succeedWhen(() -> {
			helper.assertTrue(golem.getTarget() == dreaded, "the Abyssalnite Golem targets " + golem.getTarget());
			helper.assertTrue(dreaded.getTarget() == golem, "the Dreaded Golem targets " + dreaded.getTarget());
			golem.discard();
			dreaded.discard();
		});
	}

	/** Spawn lists: Dreadlands (golems, Dreadlings, Demon Animals), Omothol Ghouls, Skeleton Goliaths; Evil Animals in plains, Demon Animals in the Nether */
	@GameTest(template = "empty")
	public static void spawnLists(GameTestHelper helper) {
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		java.util.function.BiFunction<net.minecraft.resources.ResourceKey<Biome>, EntityType<?>, Integer> weight = (key, type) -> {
			int w = 0;
			for(MobCategory category : MobCategory.values())
				w += biomes.getOrThrow(key).getMobSettings().getMobs(category).unwrap().stream().filter(m -> m.type == type).mapToInt(m -> m.getWeight().asInt()).sum();
			return w;
		};
		helper.assertTrue(weight.apply(ACBiomes.dreadlands, ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get()) == 160, "Dreaded Golems in the Dreadlands");
		helper.assertTrue(weight.apply(ACBiomes.purified_dreadlands, ACEntityTypes.ABYSSALNITE_GOLEM.get()) == 160, "Abyssalnite Golems in the Purified Dreadlands");
		helper.assertTrue(weight.apply(ACBiomes.dreadlands_mountains, ACEntityTypes.DREADLING.get()) == 80, "Dreadlings in the mountains");
		helper.assertTrue(weight.apply(ACBiomes.dreadlands_forest, ACEntityTypes.DEMON_SHEEP.get()) == 45, "Demon Sheep in the forest");
		helper.assertTrue(weight.apply(ACBiomes.omothol, ACEntityTypes.OMOTHOL_GHOUL.get()) == 50, "Omothol Ghouls");
		helper.assertTrue(weight.apply(ACBiomes.abyssal_wastelands, ACEntityTypes.SKELETON_GOLIATH.get()) == 15, "Skeleton Goliaths");
		helper.assertTrue(weight.apply(Biomes.PLAINS, ACEntityTypes.EVIL_PIG.get()) > 0, "no Evil Pigs in plains");
		helper.assertTrue(weight.apply(Biomes.NETHER_WASTES, ACEntityTypes.DEMON_CHICKEN.get()) > 0, "no Demon Chickens in the Nether");
		helper.succeed();
	}
}
