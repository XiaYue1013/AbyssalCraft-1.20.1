/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.handlers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The 1.12.2 IMC messages (see {@link com.shinoow.abyssalcraft.api.IMCHelper}), now InterModComms.sendTo("abyssalcraft",
 * key, () -> value). Values: an ItemStack ("addCrystal"), a CompoundTag (recipes, ghoul armor; stacks as saved
 * ItemStack tags) or a String (entity ids for the plague lists, entity class names for "shoggothFood").
 * Port: the 1.12.2 ore dictionary names in the "addOredict..." messages are item tag ids.
 */
public class IMCHandler {

	public static void handle(InterModProcessEvent event) {
		Set<String> senders = new LinkedHashSet<>();
		event.getIMCStream().forEach(message -> {
			Object value;
			try {
				value = message.messageSupplier().get();
			} catch(Exception e) {
				warn("Failed to read IMC message %s from mod %s: %s", message.method(), message.senderModId(), e);
				return;
			}
			if(handle(message.method(), value)) {
				senders.add(message.senderModId());
				AbyssalCraft.LOGGER.info("Received IMC message {} from mod {}", message.method(), message.senderModId());
			} else warn("Received an invalid IMC message %s from mod %s", message.method(), message.senderModId());
		});
		if(!senders.isEmpty())
			AbyssalCraft.LOGGER.info("Received IMC messages from the following mods: {}", senders);
	}

	@SuppressWarnings("unchecked")
	public static boolean handle(String key, Object value) {
		switch(key) {
		case "shoggothFood":
			if(!(value instanceof String name)) return false;
			try {
				Class<?> clazz = Class.forName(name);
				if(!LivingEntity.class.isAssignableFrom(clazz)) return false;
				EntityUtil.addShoggothFood((Class<? extends LivingEntity>) clazz);
				return true;
			} catch(ClassNotFoundException e) {
				return false;
			}
		case "addCrystal":
			if(!(value instanceof ItemStack crystal) || crystal.isEmpty()) return false;
			AbyssalCraftAPI.addCrystal(crystal);
			return true;
		case "addCoraliumPlagueImmunity", "addCoraliumPlagueCarrier", "addDreadPlagueImmunity", "addDreadPlagueCarrier":
			if(!(value instanceof String entity)) return false;
			switch(key) {
			case "addCoraliumPlagueImmunity" -> EntityUtil.addCoraliumPlagueImmunity(entity);
			case "addCoraliumPlagueCarrier" -> EntityUtil.addCoraliumPlagueCarrier(entity);
			case "addDreadPlagueImmunity" -> EntityUtil.addDreadPlagueImmunity(entity);
			default -> EntityUtil.addDreadPlagueCarrier(entity);
			}
			return true;
		default:
			return value instanceof CompoundTag tag && handleTag(key, tag);
		}
	}

	private static boolean handleTag(String key, CompoundTag tag) {
		switch(key) {
		case "addCrystallization":
			if(!has(tag, "input", "output1", "output2", "xp")) return false;
			AbyssalCraftAPI.addCrystallization(stack(tag, "input"), stack(tag, "output1"), stack(tag, "output2"), tag.getFloat("xp"));
			return true;
		case "addSingleCrystallization":
			if(!has(tag, "input", "output", "xp")) return false;
			AbyssalCraftAPI.addSingleCrystallization(stack(tag, "input"), stack(tag, "output"), tag.getFloat("xp"));
			return true;
		case "addOredictCrystallization":
			if(!has(tag, "input", "output1", "output2", "xp")) return false;
			AbyssalCraftAPI.addCrystallization(itemTag(tag.getString("input")), first(tag.getString("output1"), tag.contains("quantity1") ? tag.getInt("quantity1") : 1),
					first(tag.getString("output2"), tag.contains("quantity2") ? tag.getInt("quantity2") : 1), tag.getFloat("xp"));
			return true;
		case "addSingleOredictCrystallization":
			if(!has(tag, "input", "output", "xp")) return false;
			AbyssalCraftAPI.addSingleCrystallization(itemTag(tag.getString("input")), first(tag.getString("output"), tag.contains("quantity") ? tag.getInt("quantity") : 1),
					tag.getFloat("xp"));
			return true;
		case "addTransmutation":
			if(!has(tag, "input", "output", "xp")) return false;
			AbyssalCraftAPI.addTransmutation(stack(tag, "input"), stack(tag, "output"), tag.getFloat("xp"));
			return true;
		case "addOredictTransmutation":
			if(!has(tag, "input", "output", "xp")) return false;
			AbyssalCraftAPI.addTransmutation(itemTag(tag.getString("input")), first(tag.getString("output"), tag.contains("quantity") ? tag.getInt("quantity") : 1),
					tag.getFloat("xp"));
			return true;
		case "addMaterialization":
			if(!has(tag, "input1", "output")) return false;
			java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
			for(int i = 1; i <= 5; i++)
				if(tag.contains("input" + i) && !stack(tag, "input" + i).isEmpty())
					inputs.add(stack(tag, "input" + i));
			AbyssalCraftAPI.addMaterialization(stack(tag, "output"), inputs.toArray(new ItemStack[0]));
			return true;
		case "addGhoulArmor":
			if(!has(tag, "helmet", "chestplate", "leggings", "boots", "res1", "res2")) return false;
			AbyssalCraftAPI.addGhoulArmorTextures(stack(tag, "helmet").getItem(), stack(tag, "chestplate").getItem(), stack(tag, "leggings").getItem(),
					stack(tag, "boots").getItem(), new ResourceLocation(tag.getString("res1")), new ResourceLocation(tag.getString("res2")));
			return true;
		case "addGhoulHelmet", "addGhoulChestplate", "addGhoulLeggings", "addGhoulBoots":
			String piece = key.substring("addGhoul".length()).toLowerCase();
			if(!has(tag, piece, "res")) return false;
			Item item = stack(tag, piece).getItem();
			ResourceLocation res = new ResourceLocation(tag.getString("res"));
			switch(piece) {
			case "helmet" -> AbyssalCraftAPI.addGhoulHelmetTexture(item, res);
			case "chestplate" -> AbyssalCraftAPI.addGhoulChestplateTexture(item, res);
			case "leggings" -> AbyssalCraftAPI.addGhoulLeggingsTexture(item, res);
			default -> AbyssalCraftAPI.addGhoulBootsTexture(item, res);
			}
			return true;
		default:
			return false;
		}
	}

	private static boolean has(CompoundTag tag, String... keys) {
		for(String key : keys)
			if(!tag.contains(key)) return false;
		return true;
	}

	private static ItemStack stack(CompoundTag tag, String key) {
		return ItemStack.of(tag.getCompound(key));
	}

	private static TagKey<Item> itemTag(String id) {
		return ItemTags.create(new ResourceLocation(id));
	}

	/** First item of a tag (once tags are loaded), times the quantity */
	private static Supplier<ItemStack> first(String tag, int quantity) {
		TagKey<Item> key = itemTag(tag);
		return () -> ForgeRegistries.ITEMS.tags().getTag(key).stream().findFirst().map(i -> new ItemStack(i, quantity)).orElse(ItemStack.EMPTY);
	}

	private static void warn(String format, Object... args) {
		AbyssalCraft.LOGGER.warn(String.format(format, args));
	}

	/** For tests: sends a message to ourselves */
	public static void send(String key, Supplier<?> value) {
		InterModComms.sendTo(AbyssalCraft.modid, key, value);
	}
}
