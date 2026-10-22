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
import com.shinoow.abyssalcraft.common.entity.EntitySacthoth;
import com.shinoow.abyssalcraft.common.entity.EntityShadowMob;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.ACDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the shadow mobs and Sacthoth
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class ShadowTests {

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

	private static AABB area(GameTestHelper helper) {
		return new AABB(helper.absolutePos(BlockPos.ZERO)).inflate(16);
	}

	/** Shadow mobs: Shadow type, fire and poison immune, loot tables; spawns in the Darklands, Omothol and the Dark Realm */
	@GameTest(template = "empty")
	public static void shadowBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		for(EntityType<? extends Mob> type : List.of(ACEntityTypes.SHADOW_CREATURE.get(), ACEntityTypes.SHADOW_MONSTER.get(), ACEntityTypes.SHADOW_BEAST.get())) {
			Mob mob = mob(helper, type, new BlockPos(1, 1, 1));
			helper.assertTrue(mob.getMobType() == AbyssalCraftAPI.SHADOW && mob.fireImmune(), type + " isn't a fire immune shadow");
			helper.assertFalse(mob.addEffect(new MobEffectInstance(MobEffects.POISON, 100)), type + " was poisoned");
			helper.assertTrue(level.getServer().getLootData().getLootTable(mob.getLootTable()) != LootTable.EMPTY, type + " has no loot table");
			mob.discard();
		}
		var biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
		java.util.function.BiFunction<ResourceKey<Biome>, EntityType<?>, Integer> weight = (key, type) -> biomes.getOrThrow(key).getMobSettings()
				.getMobs(MobCategory.MONSTER).unwrap().stream().filter(m -> m.type == type).mapToInt(m -> m.getWeight().asInt()).sum();
		helper.assertTrue(weight.apply(ACBiomes.darklands, ACEntityTypes.SHADOW_CREATURE.get()) == 70, "Darklands Shadow Creatures");
		helper.assertTrue(weight.apply(ACBiomes.darklands_mountains, ACEntityTypes.SHADOW_BEAST.get()) == 40, "Darklands Mountains Shadow Beasts");
		helper.assertTrue(weight.apply(ACBiomes.dark_realm, ACEntityTypes.SHADOW_MONSTER.get()) == 40, "Dark Realm Shadow Monsters");
		helper.assertTrue(weight.apply(ACBiomes.omothol, ACEntityTypes.SHADOW_CREATURE.get()) == 18, "Omothol Shadow Creatures");
		helper.succeed();
	}

	/** The Shadow Beast's breath blinds and slows what it looks at */
	@GameTest(template = "empty", timeoutTicks = 80)
	public static void shadowBeastBreathes(GameTestHelper helper) {
		floor(helper);
		EntityShadowMob.ShadowBeast beast = mob(helper, ACEntityTypes.SHADOW_BEAST.get(), new BlockPos(1, 1, 3));
		IronGolem golem = mob(helper, EntityType.IRON_GOLEM, new BlockPos(4, 1, 3));
		beast.lookAt(EntityAnchorArgument.Anchor.EYES, golem.getEyePosition());
		beast.setTarget(golem);
		beast.setBreathTimer(-300);
		helper.succeedWhen(() -> {
			helper.assertTrue(beast.getBreathTimer() > 0, "not breathing");
			helper.assertTrue(golem.hasEffect(MobEffects.BLINDNESS) && golem.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "the golem wasn't blinded and slowed");
			helper.assertTrue(beast.getHealth() == beast.getMaxHealth(), "the beast hurt itself");
		});
	}

	/** Sacthoth: arrows and explosions don't hurt him, big hits are cut, no effects */
	@GameTest(template = "empty", batch = "sacthoth")
	public static void sacthothBasics(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntitySacthoth sacthoth = mob(helper, ACEntityTypes.SACTHOTH.get(), new BlockPos(4, 1, 4));
		Arrow arrow = new Arrow(level, sacthoth.getX(), sacthoth.getY(), sacthoth.getZ());
		helper.assertFalse(sacthoth.hurt(level.damageSources().arrow(arrow, null), 10), "hurt by an arrow");
		helper.assertFalse(sacthoth.hurt(level.damageSources().explosion(null, null), 10), "hurt by an explosion");
		helper.assertFalse(sacthoth.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100)), "got an effect");
		float health = sacthoth.getHealth();
		sacthoth.hurt(level.damageSources().generic(), 500);
		float lost = health - sacthoth.getHealth();
		helper.assertTrue(lost >= 10 && lost < 20, "a 500 hit did " + lost);
		sacthoth.discard();
		helper.succeed();
	}

	/** Killed, Sacthoth releases 11 Shadow Creatures, 6 Shadow Monsters and a Shadow Beast over 10 seconds, then is gone */
	@GameTest(template = "empty", batch = "sacthoth", timeoutTicks = 300)
	public static void sacthothDeath(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		EntitySacthoth sacthoth = mob(helper, ACEntityTypes.SACTHOTH.get(), new BlockPos(4, 1, 4));
		sacthoth.kill();
		helper.assertTrue(sacthoth.isDeadOrDying(), "/kill didn't kill him");
		helper.succeedWhen(() -> {
			helper.assertTrue(sacthoth.isRemoved(), "still dying");
			int creatures = level.getEntities(ACEntityTypes.SHADOW_CREATURE.get(), area(helper), e -> true).size();
			int monsters = level.getEntities(ACEntityTypes.SHADOW_MONSTER.get(), area(helper), e -> true).size();
			int beasts = level.getEntities(ACEntityTypes.SHADOW_BEAST.get(), area(helper), e -> true).size();
			helper.assertTrue(creatures == 11 && monsters == 6 && beasts == 1, creatures + " creatures, " + monsters + " monsters, " + beasts + " beasts");
			helper.assertFalse(level.getEntitiesOfClass(ExperienceOrb.class, area(helper)).isEmpty(), "no XP");
			helper.assertTrue(level.getEntitiesOfClass(ItemEntity.class, area(helper), e -> e.getItem().is(com.shinoow.abyssalcraft.api.item.ACItems.sacthoths_soul_harvesting_blade.get())).size() == 1,
					"Sacthoth didn't drop exactly one Soul Harvesting Blade");
			level.getEntitiesOfClass(Entity.class, area(helper), e -> e instanceof EntityShadowMob || e instanceof ExperienceOrb || e instanceof ItemEntity)
			.forEach(Entity::discard);
		});
	}
	/** Shadow damage takes priority over plague conversion for a dead player. Ordinary deaths don't produce shadows. */
	@GameTest(template = "empty", batch = "shadow_player_conversion")
	public static void shadowPlayerConversion(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		var existing = level.getEntitiesOfClass(Entity.class, area(helper)).stream().map(Entity::getUUID).collect(java.util.stream.Collectors.toSet());
		Player player = helper.makeMockPlayer();
		player.moveTo(helper.absolutePos(new BlockPos(3, 1, 3)).getCenter());
		player.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 200));
		player.hurt(AbyssalCraftAPI.damage(level, AbyssalCraftAPI.shadow), 1000);
		helper.assertTrue(player.isDeadOrDying(), "player survived");
		var shadows = level.getEntities(ACEntityTypes.SHADOW_MONSTER.get(), area(helper), e -> !existing.contains(e.getUUID()));
		helper.assertTrue(shadows.size() == 1, "expected one player shadow, got " + shadows.size());
		helper.assertTrue(shadows.get(0).getName().getString().equals(player.getName().getString())
				&& shadows.get(0).isPersistenceRequired(), "player name or persistence lost");
		helper.assertTrue(level.getEntities(ACEntityTypes.DREADLING.get(), area(helper), e -> !existing.contains(e.getUUID())).isEmpty(), "also converted to a Dreadling");
		shadows.forEach(Entity::discard);
		Player ordinary = helper.makeMockPlayer();
		ordinary.moveTo(player.position());
		ordinary.hurt(level.damageSources().generic(), 1000);
		helper.assertTrue(level.getEntitiesOfClass(EntityShadowMob.class, area(helper), e -> !existing.contains(e.getUUID())).isEmpty(), "ordinary death made a shadow");
		helper.succeed();
	}

	/** Corpse removal converts non-shadow mobs by height, preserving names, without recursively converting shadows. */
	@GameTest(template = "empty", batch = "shadow_mob_conversion", timeoutTicks = 100)
	public static void shadowMobConversions(GameTestHelper helper) {
		floor(helper);
		ServerLevel level = helper.getLevel();
		for(EntityType<? extends Mob> type : List.of(EntityType.PIG, EntityType.ZOMBIE, EntityType.IRON_GOLEM))
			for(int i = 0; i < 32; i++) {
				Mob victim = mob(helper, type, new BlockPos(3, 1, 3));
				victim.setCustomName(Component.literal(type.toShortString()));
				victim.hurt(AbyssalCraftAPI.damage(level, AbyssalCraftAPI.shadow), 1000);
			}
		EntityShadowMob shadow = mob(helper, ACEntityTypes.SHADOW_CREATURE.get(), new BlockPos(6, 1, 6));
		shadow.setCustomName(Component.literal("already_shadow"));
		shadow.hurt(AbyssalCraftAPI.damage(level, AbyssalCraftAPI.shadow), 1000);
		helper.runAfterDelay(30, () -> {
			for(var entry : List.of(ACEntityTypes.SHADOW_CREATURE, ACEntityTypes.SHADOW_MONSTER, ACEntityTypes.SHADOW_BEAST)) {
				// only this test's area: other tests running next to it have shadow mobs too
				var shadows = level.getEntities(entry.get(), new AABB(helper.absoluteVec(Vec3.ZERO), helper.absoluteVec(new Vec3(8, 4, 8))), e -> e.isAlive());
				helper.assertTrue(!shadows.isEmpty() && shadows.size() <= 32, "wrong shadow count: " + shadows.size());
				String name = entry == ACEntityTypes.SHADOW_CREATURE ? "pig" : entry == ACEntityTypes.SHADOW_MONSTER ? "zombie" : "iron_golem";
				helper.assertTrue(shadows.stream().allMatch(e -> e.getName().getString().equals(name)), "incorrect size or name, or recursive conversion");
				shadows.forEach(Entity::discard);
			}
			helper.succeed();
		});
	}

	/** Natural spawning in the floor rooms swaps every category, without changing the surface or overworld lists. */
	@GameTest(template = "empty")
	public static void floorRoomSpawnLists(GameTestHelper helper) {
		for(var key : List.of(ACDimensions.ABYSSAL_WASTELAND, ACDimensions.DREADLANDS)) {
			ServerLevel level = helper.getLevel().getServer().getLevel(key);
			var dark = level.registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.dark_realm);
			for(MobCategory category : MobCategory.values()) {
				var original = level.getBiome(new BlockPos(0, 3, 0)).value().getMobSettings().getMobs(category);
				LevelEvent.PotentialSpawns floor = new LevelEvent.PotentialSpawns(level, category, new BlockPos(0, 3, 0), original);
				MinecraftForge.EVENT_BUS.post(floor);
				helper.assertTrue(floor.getSpawnerDataList().equals(dark.getMobSettings().getMobs(category).unwrap()), "wrong floor list in " + key);
				LevelEvent.PotentialSpawns surface = new LevelEvent.PotentialSpawns(level, category, new BlockPos(0, 6, 0), original);
				MinecraftForge.EVENT_BUS.post(surface);
				helper.assertTrue(surface.getSpawnerDataList().equals(original.unwrap()), "surface list changed");
			}
		}
		helper.succeed();
	}

}
