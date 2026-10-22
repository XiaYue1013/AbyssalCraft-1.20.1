/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - entity types (1.12.2: init/EntityHandler)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.common.entity.EntityAbyssalZombie;
import com.shinoow.abyssalcraft.common.entity.EntityCoraliumSquid;
import com.shinoow.abyssalcraft.common.entity.EntityInkProjectile;
import com.shinoow.abyssalcraft.common.entity.EntityShubOffspring;
import com.shinoow.abyssalcraft.common.entity.EntityAcidProjectile;
import com.shinoow.abyssalcraft.common.entity.EntityChagaroth;
import com.shinoow.abyssalcraft.common.entity.EntityChagarothFist;
import com.shinoow.abyssalcraft.common.entity.EntityChagarothSpawn;
import com.shinoow.abyssalcraft.common.entity.EntityDreadSlug;
import com.shinoow.abyssalcraft.common.entity.EntityDreadSpawn;
import com.shinoow.abyssalcraft.common.entity.EntityDreadedCharge;
import com.shinoow.abyssalcraft.common.entity.EntityGreaterDreadSpawn;
import com.shinoow.abyssalcraft.common.entity.EntityLesserDreadbeast;
import com.shinoow.abyssalcraft.common.entity.EntityBlackHole;
import com.shinoow.abyssalcraft.common.entity.EntityGatekeeperEssence;
import com.shinoow.abyssalcraft.common.entity.EntityGatekeeperMinion;
import com.shinoow.abyssalcraft.common.entity.EntityImplosion;
import com.shinoow.abyssalcraft.common.entity.EntityJzahar;
import com.shinoow.abyssalcraft.common.entity.EntityDreadling;
import com.shinoow.abyssalcraft.common.entity.EntityAbyssalniteGolem;
import com.shinoow.abyssalcraft.common.entity.EntitySkeletonGoliath;
import com.shinoow.abyssalcraft.common.entity.EntityOmotholGhoul;
import com.shinoow.abyssalcraft.common.entity.demon.AnimalKind;
import com.shinoow.abyssalcraft.common.entity.demon.EntityDemonAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilSheep;
import com.shinoow.abyssalcraft.common.entity.anti.AntiAnimals;
import com.shinoow.abyssalcraft.common.entity.anti.AntiHumanoids;
import com.shinoow.abyssalcraft.common.entity.anti.AntiUndead;
import com.shinoow.abyssalcraft.common.entity.anti.EntityAntiCreeper;
import com.shinoow.abyssalcraft.common.entity.EntitySacthoth;
import com.shinoow.abyssalcraft.common.entity.EntityShadowMob;
import com.shinoow.abyssalcraft.common.entity.EntityDepthsGhoul;
import com.shinoow.abyssalcraft.common.entity.EntityDreadguard;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.common.entity.EntityRemnant;
import com.shinoow.abyssalcraft.common.entity.EntitySpiritItem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AbyssalCraft.modid);

	/** Mobs that get a spawn egg: type, background and spot color (1.12.2 egg colors) */
	public static final List<SpawnEgg> SPAWN_EGGS = new ArrayList<>();

	public record SpawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int background, int spots) {}

	public static final RegistryObject<EntityType<EntitySpiritItem>> SPIRIT_ITEM = ENTITY_TYPES.register("spirit_item",
			() -> EntityType.Builder.<EntitySpiritItem>of(EntitySpiritItem::new, MobCategory.MISC).sized(0.25F, 0.25F)
			.clientTrackingRange(4).updateInterval(1).build(AbyssalCraft.loc("spirit_item").toString()));

	public static final RegistryObject<EntityType<EntityDepthsGhoul>> DEPTHS_GHOUL = mob(ACEntities.depths_ghoul,
			EntityType.Builder.of(EntityDepthsGhoul::new, MobCategory.MONSTER).sized(1.0F, 3.0F), 0x36A880, 0x012626);
	public static final RegistryObject<EntityType<EntityAbyssalZombie>> ABYSSAL_ZOMBIE = mob(ACEntities.abyssal_zombie,
			EntityType.Builder.of(EntityAbyssalZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0x36A880, 0x052824);
	public static final RegistryObject<EntityType<EntityLesserShoggoth>> LESSER_SHOGGOTH = mob(ACEntities.lesser_shoggoth,
			EntityType.Builder.of(EntityLesserShoggoth::new, MobCategory.MONSTER).sized(1.8F, 2.6F), 0x133133, 0x342122);
	public static final RegistryObject<EntityType<EntityDreadguard>> DREADGUARD = mob(ACEntities.dreadguard,
			EntityType.Builder.of(EntityDreadguard::new, MobCategory.MONSTER).sized(1.0F, 3.0F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityDreadSpawn>> DREAD_SPAWN = mob(ACEntities.dread_spawn,
			EntityType.Builder.of(EntityDreadSpawn::new, MobCategory.MONSTER).sized(0.6F, 0.6F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityChagarothSpawn>> SPAWN_OF_CHAGAROTH = mob(ACEntities.spawn_of_chagaroth,
			EntityType.Builder.of(EntityChagarothSpawn::new, MobCategory.MONSTER).sized(0.6F, 0.6F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityChagarothFist>> FIST_OF_CHAGAROTH = mob(ACEntities.fist_of_chagaroth,
			EntityType.Builder.of(EntityChagarothFist::new, MobCategory.MONSTER).sized(0.6F, 1.8F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityGreaterDreadSpawn>> GREATER_DREAD_SPAWN = mob(ACEntities.greater_dread_spawn,
			EntityType.Builder.of(EntityGreaterDreadSpawn::new, MobCategory.MONSTER).sized(1.2F, 1.2F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityLesserDreadbeast>> LESSER_DREADBEAST = mob(ACEntities.lesser_dreadbeast,
			EntityType.Builder.of(EntityLesserDreadbeast::new, MobCategory.MONSTER).sized(1.8F, 1.8F).fireImmune(), 0xE60000, 0xCC0000);
	/** 1.12.2 resized it to 2.25 x 4.5 every tick */
	public static final RegistryObject<EntityType<EntityChagaroth>> CHAGAROTH = mob(ACEntities.chagaroth,
			EntityType.Builder.of(EntityChagaroth::new, MobCategory.MONSTER).sized(2.25F, 4.5F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityJzahar>> JZAHAR = mob(ACEntities.jzahar,
			EntityType.Builder.of(EntityJzahar::new, MobCategory.MONSTER).sized(1.5F, 5.7F).fireImmune(), 0x133133, 0x342122);
	public static final RegistryObject<EntityType<EntityGatekeeperMinion>> MINION_OF_THE_GATEKEEPER = mob(ACEntities.minion_of_the_gatekeeper,
			EntityType.Builder.of(EntityGatekeeperMinion::new, MobCategory.MONSTER).sized(0.8F, 2.7F), 0x133133, 0x342122);
	public static final RegistryObject<EntityType<EntityDreadling>> DREADLING = mob(ACEntities.dreadling,
			EntityType.Builder.of(EntityDreadling::new, MobCategory.MONSTER).sized(0.8F, 1.5F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntityAbyssalniteGolem>> ABYSSALNITE_GOLEM = mob(ACEntities.abyssalnite_golem,
			EntityType.Builder.of(EntityAbyssalniteGolem::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0x8A00E6, 0x6100A1);
	public static final RegistryObject<EntityType<EntityAbyssalniteGolem.Dreaded>> DREADED_ABYSSALNITE_GOLEM = mob(ACEntities.dreaded_abyssalnite_golem,
			EntityType.Builder.of(EntityAbyssalniteGolem.Dreaded::new, MobCategory.MONSTER).sized(0.6F, 1.8F).fireImmune(), 0xE60000, 0xCC0000);
	public static final RegistryObject<EntityType<EntitySkeletonGoliath>> SKELETON_GOLIATH = mob(ACEntities.skeleton_goliath,
			EntityType.Builder.of(EntitySkeletonGoliath::new, MobCategory.MONSTER).sized(1.0F, 4.5F), 0xD6D6C9, 0xC6C7AD);
	public static final RegistryObject<EntityType<EntityOmotholGhoul>> OMOTHOL_GHOUL = mob(ACEntities.omothol_ghoul,
			EntityType.Builder.of(EntityOmotholGhoul::new, MobCategory.MONSTER).sized(1.3F, 3.7F).fireImmune(), 0x133133, 0x342122);
	public static final RegistryObject<EntityType<EntityEvilAnimal>> EVIL_PIG = mob(ACEntities.evil_pig,
			EntityType.Builder.<EntityEvilAnimal>of((t, l) -> new EntityEvilAnimal(t, l, AnimalKind.PIG), MobCategory.MONSTER).sized(0.9F, 0.9F).fireImmune(), 15771042, 14377823);
	public static final RegistryObject<EntityType<EntityEvilAnimal>> EVIL_COW = mob(ACEntities.evil_cow,
			EntityType.Builder.<EntityEvilAnimal>of((t, l) -> new EntityEvilAnimal(t, l, AnimalKind.COW), MobCategory.MONSTER).sized(0.9F, 1.3F).fireImmune(), 4470310, 10592673);
	public static final RegistryObject<EntityType<EntityEvilAnimal>> EVIL_CHICKEN = mob(ACEntities.evil_chicken,
			EntityType.Builder.<EntityEvilAnimal>of((t, l) -> new EntityEvilAnimal(t, l, AnimalKind.CHICKEN), MobCategory.MONSTER).sized(0.3F, 0.7F).fireImmune(), 10592673, 16711680);
	public static final RegistryObject<EntityType<EntityEvilSheep>> EVIL_SHEEP = mob(ACEntities.evil_sheep,
			EntityType.Builder.of(EntityEvilSheep::new, MobCategory.MONSTER).sized(0.9F, 1.3F).fireImmune(), 15198183, 16758197);
	public static final RegistryObject<EntityType<EntityDemonAnimal>> DEMON_PIG = demon(ACEntities.demon_pig, AnimalKind.PIG, 0.9F, 0.9F, 15771042, 14377823);
	public static final RegistryObject<EntityType<EntityDemonAnimal>> DEMON_COW = demon(ACEntities.demon_cow, AnimalKind.COW, 0.9F, 1.3F, 4470310, 10592673);
	public static final RegistryObject<EntityType<EntityDemonAnimal>> DEMON_CHICKEN = demon(ACEntities.demon_chicken, AnimalKind.CHICKEN, 0.3F, 0.7F, 10592673, 16711680);
	public static final RegistryObject<EntityType<EntityDemonAnimal>> DEMON_SHEEP = demon(ACEntities.demon_sheep, AnimalKind.SHEEP, 0.9F, 1.3F, 15198183, 16758197);
	public static final RegistryObject<EntityType<AntiAnimals.AntiPig>> ANTI_PIG = mob(ACEntities.anti_pig,
			EntityType.Builder.of(AntiAnimals.AntiPig::new, MobCategory.CREATURE).sized(0.9F, 0.9F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiAnimals.AntiCow>> ANTI_COW = mob(ACEntities.anti_cow,
			EntityType.Builder.of(AntiAnimals.AntiCow::new, MobCategory.CREATURE).sized(0.9F, 1.3F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiAnimals.AntiChicken>> ANTI_CHICKEN = mob(ACEntities.anti_chicken,
			EntityType.Builder.of(AntiAnimals.AntiChicken::new, MobCategory.CREATURE).sized(0.3F, 0.7F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiAnimals.AntiBat>> ANTI_BAT = mob(ACEntities.anti_bat,
			EntityType.Builder.of(AntiAnimals.AntiBat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiUndead.AntiZombie>> ANTI_ZOMBIE = mob(ACEntities.anti_zombie,
			EntityType.Builder.of(AntiUndead.AntiZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiUndead.AntiAbyssalZombie>> ABYSSAL_ANTI_ZOMBIE = mob(ACEntities.abyssal_anti_zombie,
			EntityType.Builder.of(AntiUndead.AntiAbyssalZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiUndead.AntiSkeleton>> ANTI_SKELETON = mob(ACEntities.anti_skeleton,
			EntityType.Builder.of(AntiUndead.AntiSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiUndead.AntiSpider>> ANTI_SPIDER = mob(ACEntities.anti_spider,
			EntityType.Builder.of(AntiUndead.AntiSpider::new, MobCategory.MONSTER).sized(1.4F, 0.9F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntityAntiCreeper>> ANTI_CREEPER = mob(ACEntities.anti_creeper,
			EntityType.Builder.of(EntityAntiCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiHumanoids.AntiGhoul>> ANTI_GHOUL = mob(ACEntities.anti_ghoul,
			EntityType.Builder.of(AntiHumanoids.AntiGhoul::new, MobCategory.MONSTER).sized(1.0F, 3.0F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<AntiHumanoids.AntiPlayer>> ANTI_PLAYER = mob(ACEntities.anti_player,
			EntityType.Builder.of(AntiHumanoids.AntiPlayer::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0xFFFFFF, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntityShadowMob>> SHADOW_CREATURE = mob(ACEntities.shadow_creature,
			EntityType.Builder.<EntityShadowMob>of(EntityShadowMob::creature, MobCategory.MONSTER).sized(0.5F, 1.0F).fireImmune(), 0, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntityShadowMob>> SHADOW_MONSTER = mob(ACEntities.shadow_monster,
			EntityType.Builder.<EntityShadowMob>of(EntityShadowMob::monster, MobCategory.MONSTER).sized(0.6F, 1.8F).fireImmune(), 0, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntityShadowMob.ShadowBeast>> SHADOW_BEAST = mob(ACEntities.shadow_beast,
			EntityType.Builder.of(EntityShadowMob.ShadowBeast::new, MobCategory.MONSTER).sized(1.0F, 2.8F).fireImmune(), 0, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntitySacthoth>> SACTHOTH = mob(ACEntities.sacthoth,
			EntityType.Builder.of(EntitySacthoth::new, MobCategory.MONSTER).sized(1.2F, 3.8F).fireImmune(), 0, 0xFFFFFF);
	public static final RegistryObject<EntityType<EntityCoraliumSquid>> CORALIUM_SQUID = mob(ACEntities.coralium_squid,
			EntityType.Builder.of(EntityCoraliumSquid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F), 0x014e43, 0x148f7e);
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityACDragon>> ASORAH = mob(ACEntities.asorah,
			EntityType.Builder.of(com.shinoow.abyssalcraft.common.entity.EntityACDragon::new, MobCategory.MONSTER).sized(24, 12).fireImmune(), 0x476767, 0x768833);
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityACDragon>> SPECTRAL_DRAGON = mob(ACEntities.spectral_dragon,
			EntityType.Builder.of(com.shinoow.abyssalcraft.common.entity.EntityACDragon::new, MobCategory.MONSTER).sized(8, 3).fireImmune(), 0x433434, 0x344344);
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityODBPrimed>> PRIMED_ODB = projectile("primed_odb",
			EntityType.Builder.of(com.shinoow.abyssalcraft.common.entity.EntityODBPrimed::new, MobCategory.MISC).sized(0.98F, 0.98F).fireImmune());
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityODBPrimed>> PRIMED_ODB_CORE = projectile("primed_odb_core",
			EntityType.Builder.of(com.shinoow.abyssalcraft.common.entity.EntityODBPrimed::new, MobCategory.MISC).sized(0.98F, 0.98F).fireImmune());
	public static final RegistryObject<EntityType<EntityShubOffspring>> SHUB_OFFSPRING = mob(ACEntities.shub_offspring,
			EntityType.Builder.of(EntityShubOffspring::new, MobCategory.MONSTER).sized(1.0F, 2.9F), 0x2b2929, 0x211f1d);
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityCoraliumArrow>> CORALIUM_ARROW = projectile("coralium_arrow",
			EntityType.Builder.<com.shinoow.abyssalcraft.common.entity.EntityCoraliumArrow>of(com.shinoow.abyssalcraft.common.entity.EntityCoraliumArrow::new, MobCategory.MISC).sized(0.5F, 0.5F));
	public static final RegistryObject<EntityType<EntityInkProjectile>> INK_PROJECTILE = projectile("ink_projectile",
			EntityType.Builder.<EntityInkProjectile>of(EntityInkProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F));
	public static final RegistryObject<EntityType<com.shinoow.abyssalcraft.common.entity.EntityPSDLTracker>> PSDL_TRACKER = projectile("powerstone_tracker",
			EntityType.Builder.of(com.shinoow.abyssalcraft.common.entity.EntityPSDLTracker::new, MobCategory.MISC).sized(0.25F, 0.25F));
	public static final RegistryObject<EntityType<EntityRemnant>> REMNANT = mob(ACEntities.remnant,
			EntityType.Builder.of(EntityRemnant::new, MobCategory.MONSTER).sized(0.6F, 1.95F), 0x133133, 0x342122);

	/** 1.12.2 tracking range 64 blocks, updated every 10 ticks */
	public static final RegistryObject<EntityType<EntityAcidProjectile>> ACID_PROJECTILE = ENTITY_TYPES.register("acid_projectile",
			() -> EntityType.Builder.<EntityAcidProjectile>of(EntityAcidProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F)
			.clientTrackingRange(4).updateInterval(10).build(AbyssalCraft.loc("acid_projectile").toString()));

	public static final RegistryObject<EntityType<EntityDreadSlug>> DREAD_SLUG = projectile("dread_slug",
			EntityType.Builder.<EntityDreadSlug>of(EntityDreadSlug::new, MobCategory.MISC).sized(0.25F, 0.25F));
	public static final RegistryObject<EntityType<EntityDreadedCharge>> DREADED_CHARGE = projectile("dreaded_charge",
			EntityType.Builder.<EntityDreadedCharge>of(EntityDreadedCharge::new, MobCategory.MISC).sized(1.0F, 1.0F));

	public static final RegistryObject<EntityType<EntityImplosion>> IMPLOSION = projectile("implosion",
			EntityType.Builder.<EntityImplosion>of(EntityImplosion::new, MobCategory.MISC).sized(2.0F, 2.0F).fireImmune());
	public static final RegistryObject<EntityType<EntityBlackHole>> BLACK_HOLE = projectile("black_hole",
			EntityType.Builder.<EntityBlackHole>of(EntityBlackHole::new, MobCategory.MISC).sized(2.0F, 2.0F).fireImmune());
	public static final RegistryObject<EntityType<EntityGatekeeperEssence>> GATEKEEPER_ESSENCE = projectile("gatekeeper_essence",
			EntityType.Builder.<EntityGatekeeperEssence>of(EntityGatekeeperEssence::new, MobCategory.MISC).sized(0.25F, 0.25F).fireImmune());

	private static RegistryObject<EntityType<EntityDemonAnimal>> demon(String name, AnimalKind kind, float width, float height, int background, int spots) {
		return mob(name, EntityType.Builder.<EntityDemonAnimal>of((t, l) -> new EntityDemonAnimal(t, l, kind), MobCategory.MONSTER).sized(width, height).fireImmune(),
				background, spots);
	}

	/** 1.12.2 tracking range 64 blocks, updated every 10 ticks */
	private static <T extends net.minecraft.world.entity.Entity> RegistryObject<EntityType<T>> projectile(String name, EntityType.Builder<T> builder) {
		return ENTITY_TYPES.register(name, () -> builder.clientTrackingRange(4).updateInterval(10).build(AbyssalCraft.loc(name).toString()));
	}

	/** 1.12.2 tracking range 80 blocks, updated every 3 ticks */
	private static <T extends Mob> RegistryObject<EntityType<T>> mob(String name, EntityType.Builder<T> builder, int background, int spots) {
		RegistryObject<EntityType<T>> type = ENTITY_TYPES.register(name, () -> builder.clientTrackingRange(5).updateInterval(3).build(AbyssalCraft.loc(name).toString()));
		SPAWN_EGGS.add(new SpawnEgg(name, type, background, spots));
		return type;
	}

	/** Mod bus */
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ASORAH.get(), com.shinoow.abyssalcraft.common.entity.EntityACDragon.createAttributes(true).build());
		event.put(SPECTRAL_DRAGON.get(), com.shinoow.abyssalcraft.common.entity.EntityACDragon.createAttributes(false).build());
		event.put(CORALIUM_SQUID.get(), net.minecraft.world.entity.animal.Squid.createAttributes().build());
		event.put(SHUB_OFFSPRING.get(), EntityShubOffspring.createAttributes().build());
		event.put(DEPTHS_GHOUL.get(), EntityDepthsGhoul.createAttributes().build());
		event.put(ABYSSAL_ZOMBIE.get(), EntityAbyssalZombie.createAttributes().build());
		event.put(LESSER_SHOGGOTH.get(), EntityLesserShoggoth.createAttributes().build());
		event.put(REMNANT.get(), EntityRemnant.createAttributes().build());
		event.put(DREADGUARD.get(), EntityDreadguard.createAttributes().build());
		event.put(DREAD_SPAWN.get(), EntityDreadSpawn.createAttributes().build());
		event.put(SPAWN_OF_CHAGAROTH.get(), EntityChagarothSpawn.createAttributes().build());
		event.put(FIST_OF_CHAGAROTH.get(), EntityChagarothFist.createAttributes().build());
		event.put(GREATER_DREAD_SPAWN.get(), EntityGreaterDreadSpawn.createAttributes().build());
		event.put(LESSER_DREADBEAST.get(), EntityLesserDreadbeast.createAttributes().build());
		event.put(CHAGAROTH.get(), EntityChagaroth.createAttributes().build());
		event.put(JZAHAR.get(), EntityJzahar.createAttributes().build());
		event.put(DREADLING.get(), EntityDreadling.createAttributes().build());
		event.put(SHADOW_CREATURE.get(), EntityShadowMob.creatureAttributes().build());
		event.put(SHADOW_MONSTER.get(), EntityShadowMob.monsterAttributes().build());
		event.put(SHADOW_BEAST.get(), EntityShadowMob.ShadowBeast.createAttributes().build());
		event.put(SACTHOTH.get(), EntitySacthoth.createAttributes().build());
		event.put(ANTI_PIG.get(), AntiAnimals.AntiPig.createAttributes().build());
		event.put(ANTI_COW.get(), AntiAnimals.AntiCow.createAttributes().build());
		event.put(ANTI_CHICKEN.get(), AntiAnimals.AntiChicken.createAttributes().build());
		event.put(ANTI_BAT.get(), AntiAnimals.AntiBat.createAttributes().build());
		event.put(ANTI_ZOMBIE.get(), AntiUndead.AntiZombie.createAttributes().build());
		event.put(ABYSSAL_ANTI_ZOMBIE.get(), AntiUndead.AntiAbyssalZombie.createAttributes().build());
		event.put(ANTI_SKELETON.get(), AntiUndead.AntiSkeleton.createAttributes().build());
		event.put(ANTI_SPIDER.get(), AntiUndead.AntiSpider.createAttributes().build());
		event.put(ANTI_CREEPER.get(), EntityAntiCreeper.createAttributes().build());
		event.put(ANTI_GHOUL.get(), AntiHumanoids.AntiGhoul.createAttributes().build());
		event.put(ANTI_PLAYER.get(), AntiHumanoids.AntiPlayer.createAttributes().build());
		event.put(ABYSSALNITE_GOLEM.get(), EntityAbyssalniteGolem.createAttributes().build());
		event.put(DREADED_ABYSSALNITE_GOLEM.get(), EntityAbyssalniteGolem.createAttributes().build());
		event.put(SKELETON_GOLIATH.get(), EntitySkeletonGoliath.createAttributes().build());
		event.put(OMOTHOL_GHOUL.get(), EntityOmotholGhoul.createAttributes().build());
		event.put(EVIL_PIG.get(), EntityEvilAnimal.createAttributes(AnimalKind.PIG).build());
		event.put(EVIL_COW.get(), EntityEvilAnimal.createAttributes(AnimalKind.COW).build());
		event.put(EVIL_CHICKEN.get(), EntityEvilAnimal.createAttributes(AnimalKind.CHICKEN).build());
		event.put(EVIL_SHEEP.get(), EntityEvilAnimal.createAttributes(AnimalKind.SHEEP).build());
		event.put(DEMON_PIG.get(), EntityDemonAnimal.createAttributes(AnimalKind.PIG).build());
		event.put(DEMON_COW.get(), EntityDemonAnimal.createAttributes(AnimalKind.COW).build());
		event.put(DEMON_CHICKEN.get(), EntityDemonAnimal.createAttributes(AnimalKind.CHICKEN).build());
		event.put(DEMON_SHEEP.get(), EntityDemonAnimal.createAttributes(AnimalKind.SHEEP).build());
		event.put(MINION_OF_THE_GATEKEEPER.get(), EntityGatekeeperMinion.createAttributes().build());
	}

	/** Mod bus */
	public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
		event.register(SPECTRAL_DRAGON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(CORALIUM_SQUID.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCoraliumSquid::checkSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(SHUB_OFFSPRING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityShubOffspring::checkSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(DEPTHS_GHOUL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(ABYSSAL_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(LESSER_SHOGGOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(DREADGUARD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		for(EntityType<? extends net.minecraft.world.entity.animal.Animal> type : List.of(ANTI_PIG.get(), ANTI_COW.get(), ANTI_CHICKEN.get()))
			event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.animal.Animal::checkAnimalSpawnRules,
					SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(ANTI_BAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (t, l, r, p, rnd) -> net.minecraft.world.entity.ambient.Bat.checkBatSpawnRules(EntityType.BAT, l, r, p, rnd),
				SpawnPlacementRegisterEvent.Operation.REPLACE);
		for(EntityType<? extends Monster> type : List.of(ANTI_ZOMBIE.get(), ABYSSAL_ANTI_ZOMBIE.get(), ANTI_SKELETON.get(), ANTI_SPIDER.get(), ANTI_CREEPER.get(),
				ANTI_GHOUL.get(), ANTI_PLAYER.get(), SHADOW_CREATURE.get(), SHADOW_MONSTER.get(), SHADOW_BEAST.get(), DREADLING.get(), ABYSSALNITE_GOLEM.get(), DREADED_ABYSSALNITE_GOLEM.get(), SKELETON_GOLIATH.get(),
				OMOTHOL_GHOUL.get(), EVIL_PIG.get(), EVIL_COW.get(), EVIL_CHICKEN.get(), EVIL_SHEEP.get(), DEMON_PIG.get(), DEMON_COW.get(), DEMON_CHICKEN.get(),
				DEMON_SHEEP.get(), MINION_OF_THE_GATEKEEPER.get(), DREAD_SPAWN.get(), SPAWN_OF_CHAGAROTH.get(), FIST_OF_CHAGAROTH.get(), GREATER_DREAD_SPAWN.get(),
				LESSER_DREADBEAST.get()))
			event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
					SpawnPlacementRegisterEvent.Operation.REPLACE);
		event.register(REMNANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
				SpawnPlacementRegisterEvent.Operation.REPLACE);
	}
}
