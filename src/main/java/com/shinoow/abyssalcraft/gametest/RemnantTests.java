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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.EntityRemnant;
import com.shinoow.abyssalcraft.common.world.structure.StructureMarkers;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the Remnants: trades, anger, Omothol spawns
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class RemnantTests {

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	private static EntityRemnant remnant(GameTestHelper helper, BlockPos pos, boolean ai) {
		EntityRemnant remnant = helper.spawn(ACEntityTypes.REMNANT.get(), pos);
		remnant.setNoAi(!ai);
		return remnant;
	}

	private static boolean hasCoin(ItemStack stack) {
		return stack.is(ACItems.elder_engraved_coin.get());
	}

	/**
	 * Every profession starts with one offer, and every offer pays or costs Elder Engraved Coins; each profession has its
	 * own loot table
	 */
	@GameTest(template = "empty")
	public static void remnantProfessions(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		for(int profession = 0; profession < 7; profession++)
			for(int i = 0; i < 30; i++) {
				EntityRemnant remnant = ACEntityTypes.REMNANT.get().create(helper.getLevel());
				remnant.setProfession(profession);
				MerchantOffers offers = remnant.getOffers();
				helper.assertTrue(offers.size() == 1, "profession " + profession + " starts with " + offers.size() + " offers");
				MerchantOffer offer = offers.get(0);
				helper.assertFalse(offer.getResult().isEmpty() || offer.getBaseCostA().isEmpty(), "empty offer for profession " + profession);
				// the scroll merchant's unique scrolls are bought with a Greater Scroll and an Oblivion Catalyst / Antimatter bucket (1.12.2)
				boolean uniqueScroll = offer.getResult().is(com.shinoow.abyssalcraft.api.item.ACItems.oblivion_scroll.get())
						|| offer.getResult().is(com.shinoow.abyssalcraft.api.item.ACItems.antimatter_scroll.get());
				helper.assertTrue(uniqueScroll || hasCoin(offer.getBaseCostA()) || hasCoin(offer.getCostB()) || hasCoin(offer.getResult()),
						"profession " + profession + " offer without coins: " + offer.getBaseCostA() + " + " + offer.getCostB() + " -> " + offer.getResult());
				if(i == 0)
					helper.assertTrue(server.getLootData().getLootTable(remnant.getLootTable()) != LootTable.EMPTY, remnant.getLootTable() + " doesn't exist");
			}
		helper.succeed();
	}

	/** Items with a negative price sell several for one coin (1.12.2 dropped these offers, see EntityRemnant.addCoinTrade) */
	@GameTest(template = "empty")
	public static void remnantBulkTrades(GameTestHelper helper) {
		List<MerchantOffer> list = new ArrayList<>();
		EntityRemnant.addCoinTrade(list, Items.BREAD, helper.getLevel().random, 1.0F);
		EntityRemnant.addCoinTrade(list, Items.SHEARS, helper.getLevel().random, 1.0F);
		helper.assertTrue(list.size() == 2, list.size() + " offers");
		MerchantOffer bread = list.get(0), shears = list.get(1);
		helper.assertTrue(hasCoin(bread.getBaseCostA()) && bread.getBaseCostA().getCount() == 1, "bread costs " + bread.getBaseCostA());
		helper.assertTrue(bread.getResult().is(Items.BREAD) && bread.getResult().getCount() >= 3 && bread.getResult().getCount() <= 4, "sells " + bread.getResult());
		helper.assertTrue(shears.getBaseCostA().getCount() == 3 && shears.getResult().getCount() == 1, "shears: " + shears.getBaseCostA() + " -> " + shears.getResult());
		helper.succeed();
	}

	/** Using the newest offer unlocks another one 40 ticks later (with Regeneration); offers are never duplicated */
	@GameTest(template = "empty", timeoutTicks = 700)
	public static void remnantUnlocksTrades(GameTestHelper helper) {
		floor(helper);
		EntityRemnant remnant = remnant(helper, new BlockPos(4, 1, 4), true);
		remnant.setProfession(5); // banker: 21 offers, 7 different ones (offers with the same items count as the same, 1.7.10 rules)
		int rounds = 12;
		for(int r = 0; r < rounds; r++)
			helper.runAtTickTime(1 + 45 * r, () -> {
				MerchantOffers offers = remnant.getOffers();
				remnant.notifyTrade(offers.get(offers.size() - 1));
			});
		helper.runAtTickTime(1 + 45 * rounds + 5, () -> {
			MerchantOffers offers = remnant.getOffers();
			Set<String> ids = new HashSet<>();
			for(MerchantOffer offer : offers)
				helper.assertTrue(ids.add(offer.getBaseCostA().getItem() + ">" + offer.getResult().getItem()), "duplicate offer " + offer.getResult());
			// (12 draws from 7: fewer than 3 different ones has a chance of about 1 in 170,000)
			helper.assertTrue(offers.size() >= 3 && offers.size() <= 7, offers.size() + " offers after " + rounds + " trades");
			helper.assertTrue(remnant.hasEffect(MobEffects.REGENERATION), "no Regeneration after restocking");
			helper.assertTrue(remnant.getWealth() >= 8 * rounds, "wealth " + remnant.getWealth());
			helper.succeed();
		});
	}

	/** Remnants only trade with players carrying a Necronomicon, one at a time */
	@GameTest(template = "empty")
	public static void remnantTradesWithNecronomicon(GameTestHelper helper) {
		floor(helper);
		EntityRemnant remnant = remnant(helper, new BlockPos(4, 1, 4), false);
		Player oblivious = helper.makeMockPlayer();
		helper.assertTrue(remnant.interact(oblivious, InteractionHand.MAIN_HAND).consumesAction(), "no reaction without a Necronomicon");
		helper.assertTrue(remnant.getTradingPlayer() == null, "traded without a Necronomicon");
		Player reader = helper.makeMockPlayer();
		reader.getInventory().add(new ItemStack(ACItems.necronomicon.get()));
		remnant.interact(reader, InteractionHand.MAIN_HAND);
		helper.assertTrue(remnant.getTradingPlayer() == reader, "didn't start trading");
		Player other = helper.makeMockPlayer();
		other.getInventory().add(new ItemStack(ACItems.necronomicon.get()));
		remnant.interact(other, InteractionHand.MAIN_HAND);
		helper.assertTrue(remnant.getTradingPlayer() == reader, "switched customers");
		remnant.setTradingPlayer(null);
		helper.succeed();
	}

	/** Hurting a Remnant makes it and the Remnants around it angry at the attacker, and angry Remnants don't trade */
	@GameTest(template = "empty")
	public static void remnantsEnrage(GameTestHelper helper) {
		floor(helper);
		EntityRemnant remnant = remnant(helper, new BlockPos(2, 1, 2), false);
		EntityRemnant friend = remnant(helper, new BlockPos(6, 1, 6), false);
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 1, 5));
		zombie.setNoAi(true);
		remnant.hurt(AbyssalCraftAPI.damage(helper.getLevel(), DamageTypes.MOB_ATTACK, zombie), 1.0F);
		helper.assertTrue(remnant.isAngry() && remnant.getTarget() == zombie, "the Remnant isn't angry at the zombie");
		helper.assertTrue(friend.isAngry() && friend.getTarget() == zombie, "the other Remnant didn't join in");
		Player reader = helper.makeMockPlayer();
		reader.getInventory().add(new ItemStack(ACItems.necronomicon.get()));
		remnant.interact(reader, InteractionHand.MAIN_HAND);
		helper.assertTrue(remnant.getTradingPlayer() == null, "an angry Remnant traded");
		helper.succeed();
	}

	/** "remnant" data markers in Omothol buildings become Remnants with a profession that fits the building */
	@GameTest(template = "empty")
	public static void omotholBuildingRemnants(GameTestHelper helper) {
		floor(helper);
		BlockPos library = helper.absolutePos(new BlockPos(2, 1, 2)), church = helper.absolutePos(new BlockPos(6, 1, 6));
		StructureMarkers.handle(StructureMarkers.Kind.OMOTHOL_BUILDING, "6", Rotation.NONE, "remnant", library, helper.getLevel(), helper.getLevel().random);
		StructureMarkers.handle(StructureMarkers.Kind.OMOTHOL_BUILDING, "2", Rotation.NONE, "remnant", church, helper.getLevel(), helper.getLevel().random);
		var librarians = helper.getLevel().getEntitiesOfClass(EntityRemnant.class, new net.minecraft.world.phys.AABB(library).inflate(1));
		var priests = helper.getLevel().getEntitiesOfClass(EntityRemnant.class, new net.minecraft.world.phys.AABB(church).inflate(1));
		helper.assertTrue(librarians.size() == 1 && librarians.get(0).getProfession() == 1, "library: " + librarians.size() + " Remnants");
		helper.assertTrue(priests.size() == 1 && priests.get(0).getProfession() == 2, "church: " + priests.size() + " Remnants");
		helper.assertFalse(librarians.get(0).removeWhenFarAway(1000), "Remnants despawn");
		// Omothol also spawns them naturally (1.12.2 weight 3, groups of 2 - 4)
		helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.omothol).getMobSettings()
				.getMobs(MobCategory.MONSTER).unwrap().stream().anyMatch(m -> m.type == ACEntityTypes.REMNANT.get() && m.getWeight().asInt() == 3
				&& m.minCount == 2 && m.maxCount == 4), "no Remnant spawns in Omothol");
		helper.succeed();
	}
}
