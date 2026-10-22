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
 *     1.20.1 port - general gameplay event hooks; the remaining 1.12.2 hooks are added with the content they belong to
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.handlers;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.api.transfer.caps.ItemTransferCapabilityProvider;
import com.shinoow.abyssalcraft.common.world.DarkRealmTeleporter;
import com.shinoow.abyssalcraft.common.world.data.NecromancyWorldSavedData;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Map;
import com.shinoow.abyssalcraft.api.item.ItemUpgradeKit;
import com.shinoow.abyssalcraft.api.recipe.UpgradeKitRecipes;
import com.shinoow.abyssalcraft.common.enchantments.EnchantmentWeaponInfusion;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;

public class AbyssalCraftEventHooks {

	/** Named mobs that die can be brought back with the Resurrection ritual */
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onDeath(LivingDeathEvent event){
		LivingEntity e = event.getEntity();
		if(event.isCanceled() || e.level().isClientSide || e.getServer() == null) return;
		e.getPersistentData().putBoolean("abyssalcraft:shadow_death", e.level().dimension() == ACDimensions.DARK_REALM
				|| event.getSource().is(AbyssalCraftAPI.shadow)
				|| event.getSource().getEntity() instanceof Mob killer && killer.getMobType() == AbyssalCraftAPI.SHADOW);
		if(e instanceof Mob && e.hasCustomName() && e.canChangeDimensions()) {
			CompoundTag data = new CompoundTag();
			if(e.save(data))
				NecromancyWorldSavedData.get(e.getServer()).storeData(e.getName().getString(), data, calculateSize(e.getBbHeight()));
		}
	}

	/**
	 * Anvil: an Upgrade Kit on the right turns the tool/armor on the left into the next tier, keeping its enchantments,
	 * name and capabilities (cost 1 - 10 levels by remaining durability, + enchantment levels, + 1 for renaming).
	 * Coralium / Dread infusion books can't be applied in an anvil.
	 */
	@SubscribeEvent
	public void upgradeKits(AnvilUpdateEvent event) {
		ItemStack input = event.getLeft(), right = event.getRight();
		if(!(right.getItem() instanceof ItemUpgradeKit kit)) {
			if(right.is(Items.ENCHANTED_BOOK) && EnchantmentHelper.getEnchantments(right).keySet().stream().anyMatch(e -> e instanceof EnchantmentWeaponInfusion))
				event.setCanceled(true);
			return;
		}
		if(!ACConfig.upgrade_kits) return;
		ItemStack stack = UpgradeKitRecipes.instance().getUpgrade(kit, input);
		if(stack.isEmpty()) return;
		float durability = input.isDamageableItem() ? (float) (input.getMaxDamage() - input.getDamageValue()) / input.getMaxDamage() : 1;
		int cost = Math.max(1, 10 - Math.min(9, (int) (durability * 10)));
		String name = event.getName();
		if(name == null || name.isBlank()) {
			if(input.hasCustomHoverName()) {
				cost += 1;
				stack.resetHoverName();
			}
		} else if(!name.equals(input.getHoverName().getString())) {
			cost += 1;
			stack.setHoverName(Component.literal(name));
		} else if(input.hasCustomHoverName())
			stack.setHoverName(input.getHoverName());
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(input);
		for(int level : enchantments.values())
			cost += level;
		EnchantmentHelper.setEnchantments(enchantments, stack);
		stack.setCount(input.getCount());
		CompoundTag caps = input.serializeNBT().getCompound("ForgeCaps");
		if(!caps.isEmpty()) {
			CompoundTag data = stack.serializeNBT();
			data.put("ForgeCaps", caps);
			stack = ItemStack.of(data);
		}
		event.setOutput(stack);
		event.setMaterialCost(1);
		event.setCost(cost);
	}

	/** Inventories can be part of the Item Transportation System (unless their block entity type is blacklisted) */
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<BlockEntity> event) {
		ResourceLocation rl = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(event.getObject().getType());
		if(rl != null && !ACConfig.itemTransportBlacklist.contains(rl.toString())) {
			ItemTransferCapabilityProvider provider = new ItemTransferCapabilityProvider();
			event.addCapability(new ResourceLocation(AbyssalCraft.modid, "itemtransfer"), provider);
			event.addListener(provider::invalidate);
		}
	}

	/** Falling out of the bottom of Omothol leads into the Dark Realm */
	@SubscribeEvent
	public void darkRealm(LivingTickEvent event) {
		if(event.getEntity() instanceof ServerPlayer player && player.level().dimension() == ACDimensions.OMOTHOL
				&& player.getY() <= player.level().getMinBuildHeight())
			DarkRealmTeleporter.fallIntoDarkRealm(player);
		LivingEntity entity = event.getEntity();
		if(entity.level().isClientSide && ACConfig.particleEntity && entity.level().dimension() == ACDimensions.DARK_REALM
				&& !(entity instanceof Player) && entity.getMobType() != AbyssalCraftAPI.SHADOW) {
			var random = entity.getRandom();
			entity.level().addParticle(ParticleTypes.LARGE_SMOKE,
					entity.getX() + (random.nextDouble() - 0.5D) * entity.getBbWidth(), entity.getY() + random.nextDouble() * entity.getBbHeight(),
					entity.getZ() + (random.nextDouble() - 0.5D) * entity.getBbWidth(), 0, 0, 0);
		}
	}

	/** Like plague conversions, mobs change after their corpse is removed; players change on death. */
	public static boolean convertToShadow(LivingEntity entity, ServerLevel level) {
		if(!entity.getPersistentData().getBoolean("abyssalcraft:shadow_death") || entity.getMobType() == AbyssalCraftAPI.SHADOW)
			return false;
		if(!(entity instanceof Player) && !level.random.nextBoolean()) return false;
		Mob shadow = (entity instanceof Player || entity.getBbHeight() >= 1.2F && entity.getBbHeight() < 2.2F
				? ACEntityTypes.SHADOW_MONSTER.get() : entity.getBbHeight() >= 2.2F
				? ACEntityTypes.SHADOW_BEAST.get() : ACEntityTypes.SHADOW_CREATURE.get()).create(level);
		if(shadow == null) return false;
		shadow.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
		shadow.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.CONVERSION, null, null);
		if(entity instanceof Player) {
			shadow.setCustomName(entity.getName());
			shadow.setPersistenceRequired();
		} else if(entity.hasCustomName())
			shadow.setCustomName(entity.getCustomName());
		if(!level.addFreshEntity(shadow)) return false;
		if(entity instanceof Player) level.levelEvent(1016, entity.blockPosition(), 0);
		return true;
	}

	/** The rooms under the Abyssal Wasteland and Dreadlands use the Dark Realm's spawn list. */
	@SubscribeEvent
	public void floorRoomSpawns(LevelEvent.PotentialSpawns event) {
		if(!(event.getLevel() instanceof ServerLevel level) || event.getPos().getY() > 5
				|| level.dimension() != ACDimensions.ABYSSAL_WASTELAND && level.dimension() != ACDimensions.DREADLANDS)
			return;
		List.copyOf(event.getSpawnerDataList()).forEach(event::removeSpawnerData);
		level.registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.dark_realm)
				.getMobSettings().getMobs(event.getMobCategory()).unwrap().forEach(event::addSpawnerData);
	}

	private static int calculateSize(float height){
		if(height >= 1.5f)
			return 2;
		if(height >= 0.75f)
			return 1;
		return 0;
	}
}
