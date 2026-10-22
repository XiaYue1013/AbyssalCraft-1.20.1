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
 *     1.20.1 port - the eight 1.12.2 armor classes merged, effects keyed on the material
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items.armor;

import java.util.function.Consumer;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.client.ACClientItemExtensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * AbyssalCraft armor. Each set gives the effects it gave in 1.12.2 (toggleable with the "armorPotionEffects" config).
 */
public class ItemACArmor extends ArmorItem implements IUnlockableItem {

	private IUnlockCondition condition = new DefaultCondition();
	private final ACArmorMaterials acMaterial;
	private final ChatFormatting format;
	private final String texture;

	/**
	 * @param texture armor texture name in textures/armor (without the _1/_2 layer suffix)
	 */
	public ItemACArmor(ACArmorMaterials material, Type type, ChatFormatting format, String texture) {
		super(material, type, new Item.Properties());
		acMaterial = material;
		this.format = format;
		this.texture = texture;
	}

	public ACArmorMaterials getACMaterial() {
		return acMaterial;
	}

	@Override
	public Component getName(ItemStack stack) {
		Component name = super.getName(stack);
		return format != null ? name.copy().withStyle(format) : name;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return "abyssalcraft:textures/armor/" + texture + (slot == EquipmentSlot.LEGS ? "_2.png" : "_1.png");
	}

	private static void effect(Player player, MobEffect effect, int duration, int amplifier) {
		player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		if(world.isClientSide || !ACConfig.armorPotionEffects) return;
		Type slot = getType();
		boolean surface = world.dimensionType().natural();
		switch(acMaterial) {
		case ABYSSALNITE:
			if (slot == Type.HELMET)
				effect(player, MobEffects.WATER_BREATHING, 20, 0);
			if (slot == Type.BOOTS)
				effect(player, MobEffects.MOVEMENT_SPEED, 20, 0);
			break;
		case REFINED_CORALIUM:
			if (slot == Type.HELMET)
				effect(player, MobEffects.WATER_BREATHING, 20, 0);
			if (slot == Type.CHESTPLATE)
				effect(player, MobEffects.DAMAGE_RESISTANCE, 20, 0);
			if (slot == Type.BOOTS)
				effect(player, MobEffects.MOVEMENT_SPEED, 20, 0);
			break;
		case PLATED_CORALIUM:
			if (slot == Type.HELMET) {
				if(surface || ACConfig.nightVisionEverywhere)
					effect(player, MobEffects.NIGHT_VISION, 260, 0);
				player.removeEffect(AbyssalCraftAPI.coralium_plague.get());
			}
			if (slot == Type.BOOTS)
				if(player.isInWater()){
					effect(player, MobEffects.MOVEMENT_SPEED, 20, 2);
					effect(player, MobEffects.WATER_BREATHING, 20, 1);
				} else effect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
			break;
		case DEPTHS:
			if (slot == Type.HELMET) {
				effect(player, MobEffects.WATER_BREATHING, 20, 0);
				if(surface)
					effect(player, MobEffects.NIGHT_VISION, 260, 0);
				player.removeEffect(AbyssalCraftAPI.coralium_plague.get());
			}
			if (slot == Type.CHESTPLATE)
				effect(player, MobEffects.DAMAGE_RESISTANCE, 20, 0);
			if (slot == Type.LEGGINGS)
				effect(player, MobEffects.JUMP, 20, 1);
			if (slot == Type.BOOTS)
				effect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
			break;
		case DREAD:
			if (slot == Type.HELMET){
				if(surface)
					effect(player, MobEffects.NIGHT_VISION, 260, 0);
				player.removeEffect(AbyssalCraftAPI.dread_plague.get());
			} else
				effect(player, MobEffects.FIRE_RESISTANCE, 20, 3);
			break;
		case DREADIUM:
			if (slot == Type.CHESTPLATE)
				effect(player, MobEffects.DAMAGE_RESISTANCE, 20, 0);
			if (slot == Type.BOOTS)
				effect(player, MobEffects.MOVEMENT_SPEED, 20, 0);
			break;
		case DREADIUM_SAMURAI:
			if (slot == Type.HELMET) {
				if(surface)
					effect(player, MobEffects.NIGHT_VISION, 260, 0);
				player.removeEffect(AbyssalCraftAPI.dread_plague.get());
			}
			if (slot == Type.CHESTPLATE) {
				effect(player, MobEffects.DAMAGE_RESISTANCE, 20, 0);
				effect(player, MobEffects.DAMAGE_BOOST, 20, 0);
			}
			if (slot == Type.LEGGINGS)
				effect(player, MobEffects.FIRE_RESISTANCE, 20, 1);
			if (slot == Type.BOOTS)
				effect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
			break;
		case ETHAXIUM:
			if (slot == Type.HELMET) {
				if(surface && !world.isDay())
					effect(player, MobEffects.NIGHT_VISION, 260, 0);
				player.removeEffect(MobEffects.HUNGER);
				player.removeEffect(MobEffects.POISON);
			}
			if(slot == Type.CHESTPLATE){
				if(player.isOnFire() || world.dimensionType().ultraWarm())
					effect(player, MobEffects.FIRE_RESISTANCE, 20, 2);
				player.removeEffect(AbyssalCraftAPI.antimatter_potion.get());
				if(world.random.nextInt(200) == 0)
					effect(player, MobEffects.DAMAGE_BOOST, 60, 0);
			}
			if(slot == Type.LEGGINGS)
				if(world.random.nextInt(200) == 0)
					effect(player, MobEffects.REGENERATION, 60, 0);
			if(slot == Type.BOOTS)
				if(player.isInWater()){
					effect(player, MobEffects.MOVEMENT_SPEED, 20, 2);
					effect(player, MobEffects.WATER_BREATHING, 20, 1);
				} else effect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
			break;
		}
	}

	@Override
	public Item setUnlockCondition(IUnlockCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public IUnlockCondition getUnlockCondition(ItemStack stack) {
		return condition;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(ACClientItemExtensions.armor(this));
	}
}
