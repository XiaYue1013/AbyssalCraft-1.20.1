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
 ******************************************************************************/
package com.shinoow.abyssalcraft.api.entity;

import java.util.*;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Utility class for Entities
 * @author shinoow
 *
 * @since 1.0
 */
public final class EntityUtil {

	private EntityUtil(){}

	private static final List<Class<? extends LivingEntity>> shoggothFood = new ArrayList<>();
	private static final List<String> dread_carriers = new ArrayList<>();
	private static final List<String> dread_immunity = new ArrayList<>();
	private static final List<String> coralium_carriers = new ArrayList<>();
	private static final List<String> coralium_immunity = new ArrayList<>();

	private static String getKey(LivingEntity entity) {
		ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		return key == null ? null : key.toString();
	}

	/**
	 * Checks if the entity is immune to the Coralium Plague
	 * @param par1 The entity to check
	 * @return True if the entity is immune, otherwise false
	 */
	public static boolean isEntityCoralium(LivingEntity par1){
		String key = getKey(par1);
		return par1 instanceof ICoraliumEntity || par1 instanceof IOmotholEntity || par1 instanceof Player && isPlayerCoralium((Player)par1)
				|| key != null && (coralium_immunity.contains(key)
						|| AbyssalCraftAPI.getInternalMethodHandler().isImmuneOrCarrier(key, 2));
	}

	/**
	 * Checks if a Player has the Coralium Plague immunity (the author's easter egg, kept from 1.12.2)
	 * @param par1 The Player to check
	 * @return True if the Player is immune, otherwise false
	 */
	public static boolean isPlayerCoralium(Player par1){
		if(!FMLEnvironment.production)
			return par1.getGameProfile().getName().equals("shinoow") || par1.getGameProfile().getName().equals("Oblivionaire");
		else return par1.getUUID().equals(Vars.uuid1) || par1.getUUID().equals(Vars.uuid2);
	}

	/**
	 * Checks if the entity is immune to the Dread Plague
	 * @param par1 The entity to check
	 * @return True if the entity is immune, otherwise false
	 */
	public static boolean isEntityDread(LivingEntity par1){
		String key = getKey(par1);
		return par1 instanceof IDreadEntity || par1 instanceof IOmotholEntity || key != null && (dread_immunity.contains(key)
				|| AbyssalCraftAPI.getInternalMethodHandler().isImmuneOrCarrier(key, 0));
	}

	/**
	 * Checks if the entity is immune to Antimatter
	 * @param par1 The entity to check
	 * @return True if the entity is immune, otherwise false
	 */
	public static boolean isEntityAnti(LivingEntity par1){
		return par1 instanceof IAntiEntity || par1 instanceof IOmotholEntity;
	}

	/**
	 * Checks if a Player has a Necronomicon
	 * @param player The Player to check
	 * @return True if the Player has a Necronomicon, otherwise false
	 */
	public static boolean hasNecronomicon(Player player){
		return player.getInventory().hasAnyMatching(stack -> stack.is(ACItems.necronomicon.get()) || stack.is(ACItems.abyssal_wasteland_necronomicon.get())
				|| stack.is(ACItems.dreadlands_necronomicon.get()) || stack.is(ACItems.omothol_necronomicon.get())
				|| stack.is(ACItems.abyssalnomicon.get()));
	}

	/**
	 * Checks if the entity is immune to one of AbyssalCraft's plague/antimatter effects
	 * @param entity Entity to check
	 * @param potion Effect to check
	 * @return True if the entity is immune to the effect
	 */
	public static boolean isEntityImmune(LivingEntity entity, MobEffect potion){
		return potion == AbyssalCraftAPI.coralium_plague.get() && isEntityCoralium(entity) ||
				potion == AbyssalCraftAPI.dread_plague.get() && isEntityDread(entity) ||
				potion == AbyssalCraftAPI.antimatter_potion.get() && isEntityAnti(entity);
	}

	/**
	 * Adds the Entity class to a list of entities Lesser Shoggoths are allowed to eat
	 */
	public static void addShoggothFood(Class<? extends LivingEntity> clazz){
		shoggothFood.add(clazz);
	}

	public static List<Class<? extends LivingEntity>> getShoggothFood(){
		return shoggothFood;
	}

	/**
	 * Checks if the Entity is food for Lesser Shoggoths
	 */
	public static boolean isShoggothFood(LivingEntity entity){
		for(Class<? extends LivingEntity> c : shoggothFood)
			if(c.isAssignableFrom(entity.getClass()))
				return true;
		return false;
	}

	/**
	 * Checks whether or not the target can block the damage source with a shield
	 */
	public static boolean canEntityBlockDamageSource(DamageSource damageSource, LivingEntity target)
	{
		if (!damageSource.is(DamageTypeTags.BYPASSES_SHIELD) && target.isBlocking())
		{
			Vec3 vec3d = damageSource.getSourcePosition();

			if (vec3d != null)
			{
				Vec3 vec3d1 = target.getViewVector(1.0F);
				Vec3 vec3d2 = vec3d.vectorTo(target.position()).normalize();
				vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);

				if (vec3d2.dot(vec3d1) < 0.0D)
					return true;
			}
		}

		return false;
	}

	/**
	 * Damages the shield an entity is currently blocking with
	 * @return True if a shield was damaged
	 */
	public static boolean damageShield(LivingEntity entity, int amount) {
		ItemStack shield = entity.getUseItem();
		if(!shield.isEmpty() && shield.canPerformAction(ToolActions.SHIELD_BLOCK)) {
			shield.hurtAndBreak(amount, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
			return true;
		}
		return false;
	}

	/**
	 * Gives the Mob random armor (at least chainmail) and a sword (or bow)
	 */
	public static void suitUp(Mob entity, boolean bow) {
		RandomSource rng = entity.getRandom();
		int i = rng.nextInt(4);
		float f = 0.1F;

		if (rng.nextFloat() < 0.095F)
			++i;

		if (rng.nextFloat() < 0.095F)
			++i;

		if (rng.nextFloat() < 0.095F)
			++i;

		i = Math.max(i, 2); // chainmail at minimum
		i = Math.min(i, 4); // in case RNGesus really smiles upon you

		for (EquipmentSlot entityequipmentslot : EquipmentSlot.values())
			if (entityequipmentslot.getType() == EquipmentSlot.Type.ARMOR)
			{
				ItemStack itemstack = entity.getItemBySlot(entityequipmentslot);

				if (itemstack.isEmpty())
				{
					Item item = Mob.getEquipmentForSlot(entityequipmentslot, i);

					if (item != null)
						entity.setItemSlot(entityequipmentslot, EnchantmentHelper.enchantItem(rng, new ItemStack(item), (int)(5.0F + f * rng.nextInt(18)), false));
				}
			}
		Item sword = Items.STONE_SWORD;
		switch(i) {
		case 2:
			sword = Items.STONE_SWORD;
			break;
		case 3:
			sword = Items.IRON_SWORD;
			break;
		case 4:
			sword = Items.DIAMOND_SWORD;
			break;
		}
		if(bow)
			sword = Items.BOW;
		entity.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(rng, new ItemStack(sword), (int)(5.0F + f * rng.nextInt(18)), false));
	}

	private static boolean isRegistered(String entity) {
		ResourceLocation id = ResourceLocation.tryParse(entity);
		return id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id);
	}

	/**
	 * Adds the entity to a list of entities considered immune to the Dread Plague
	 * @param entity Entity ID string
	 */
	public static void addDreadPlagueImmunity(String entity) {
		if(isRegistered(entity))
			dread_immunity.add(entity);
	}

	/**
	 * Adds the entity to a list of entities considered carriers of the Dread Plague (also makes it immune)
	 * @param entity Entity ID string
	 */
	public static void addDreadPlagueCarrier(String entity) {
		addDreadPlagueImmunity(entity);
		if(isRegistered(entity))
			dread_carriers.add(entity);
	}

	/**
	 * Adds the entity to a list of entities considered immune to the Coralium Plague
	 * @param entity Entity ID string
	 */
	public static void addCoraliumPlagueImmunity(String entity) {
		if(isRegistered(entity))
			coralium_immunity.add(entity);
	}

	/**
	 * Adds the entity to a list of entities considered carriers of the Coralium Plague (also makes it immune)
	 * @param entity Entity ID string
	 */
	public static void addCoraliumPlagueCarrier(String entity) {
		addCoraliumPlagueImmunity(entity);
		if(isRegistered(entity))
			coralium_carriers.add(entity);
	}

	/**
	 * Checks if an entity carries the Dread Plague (applies it on attack)
	 */
	public static boolean isDreadPlagueCarrier(LivingEntity entity) {
		String key = getKey(entity);
		return entity instanceof IDreadEntity || key != null && (dread_carriers.contains(key)
				|| AbyssalCraftAPI.getInternalMethodHandler().isImmuneOrCarrier(key, 1));
	}

	/**
	 * Checks if an entity carries the Coralium Plague (applies it on attack)
	 */
	public static boolean isCoraliumPlagueCarrier(LivingEntity entity) {
		String key = getKey(entity);
		return entity instanceof ICoraliumEntity || key != null && (coralium_carriers.contains(key)
				|| AbyssalCraftAPI.getInternalMethodHandler().isImmuneOrCarrier(key, 3));
	}

	static class Vars{
		static UUID uuid1 = UUID.fromString("a5d8abca-0979-4bb0-825a-f1ccda0b350b");
		static UUID uuid2 = UUID.fromString("08f3211c-d425-47fd-afd8-f0e7f94152c4");
	}
}
