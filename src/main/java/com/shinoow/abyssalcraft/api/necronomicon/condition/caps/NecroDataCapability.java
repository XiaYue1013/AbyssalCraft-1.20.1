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
package com.shinoow.abyssalcraft.api.necronomicon.condition.caps;

import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.api.necronomicon.condition.ConditionProcessorRegistry;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Player knowledge (Necronomicon unlocks)
 * @author shinoow
 *
 */
public class NecroDataCapability implements INecroDataCapability {

	List<String> biome_triggers = new ArrayList<>();
	List<String> entity_triggers = new ArrayList<>();
	List<String> dimension_triggers = new ArrayList<>();
	List<String> artifact_triggers = new ArrayList<>();
	List<String> page_triggers = new ArrayList<>();
	List<String> whisper_triggers = new ArrayList<>();
	List<String> misc_triggers = new ArrayList<>();
	boolean hasAllKnowledge;
	long lastSyncTime = 0;
	int syncTimer = 0;

	/**
	 * Fetches the knowledge capability of a player
	 * @return The capability, or an empty (non-persisting) instance if the player has none (eg. a removed player)
	 */
	public static INecroDataCapability getCap(Player player){
		return player.getCapability(NecroDataCapabilityProvider.NECRO_DATA_CAP).orElseGet(NecroDataCapability::new);
	}

	@Override
	public boolean isUnlocked(IUnlockCondition cond, Player player) {
		if(cond.getType() == -1 || hasAllKnowledge && cond.getType() != 11) return true;
		else return ConditionProcessorRegistry.instance().getProcessor(cond.getType()).processUnlock(cond, this, player);
	}

	@Override
	public void triggerEntityUnlock(String name) {
		if(name != null && !entity_triggers.contains(name) && name.contains(":"))
			entity_triggers.add(name);
	}

	@Override
	public void triggerBiomeUnlock(String name) {
		if(name != null && !biome_triggers.contains(name))
			biome_triggers.add(name);
	}

	@Override
	public void triggerDimensionUnlock(String id) {
		if(id != null && !dimension_triggers.contains(id))
			dimension_triggers.add(id);
	}

	@Override
	public void triggerArtifactUnlock(String name) {
		if(name != null && !artifact_triggers.contains(name))
			artifact_triggers.add(name);
	}

	@Override
	public void triggerPageUnlock(String name) {
		if(name != null && !page_triggers.contains(name))
			page_triggers.add(name);
	}

	@Override
	public void triggerWhisperUnlock(String name) {
		if(name != null && !whisper_triggers.contains(name))
			whisper_triggers.add(name);
	}

	@Override
	public void triggerMiscUnlock(String name) {
		if(name != null && !misc_triggers.contains(name))
			misc_triggers.add(name);
	}

	@Override
	public void unlockAllKnowledge(boolean unlock) {
		hasAllKnowledge = unlock;
	}

	@Override
	public void setLastSyncTime(long time) {
		lastSyncTime = time;
	}

	@Override
	public List<String> getBiomeTriggers() {
		return biome_triggers;
	}

	@Override
	public List<String> getEntityTriggers() {
		return entity_triggers;
	}

	@Override
	public List<String> getDimensionTriggers() {
		return dimension_triggers;
	}

	@Override
	public List<String> getArtifactTriggers() {
		return artifact_triggers;
	}

	@Override
	public List<String> getPageTriggers() {
		return page_triggers;
	}

	@Override
	public List<String> getWhisperTriggers() {
		return whisper_triggers;
	}

	@Override
	public List<String> getMiscTriggers(){
		return misc_triggers;
	}

	@Override
	public boolean hasUnlockedAllKnowledge(){
		return hasAllKnowledge;
	}

	@Override
	public long getLastSyncTime() {
		return lastSyncTime;
	}

	@Override
	public void incrementSyncTimer() {
		syncTimer++;
	}

	@Override
	public void resetSyncTimer() {
		syncTimer = 0;
	}

	@Override
	public int getSyncTimer() {
		return syncTimer;
	}

	@Override
	public void copy(INecroDataCapability cap) {
		biome_triggers = new ArrayList<>(cap.getBiomeTriggers());
		entity_triggers = new ArrayList<>(cap.getEntityTriggers());
		dimension_triggers = new ArrayList<>(cap.getDimensionTriggers());
		artifact_triggers = new ArrayList<>(cap.getArtifactTriggers());
		page_triggers = new ArrayList<>(cap.getPageTriggers());
		whisper_triggers = new ArrayList<>(cap.getWhisperTriggers());
		misc_triggers = new ArrayList<>(cap.getMiscTriggers());
		hasAllKnowledge = cap.hasUnlockedAllKnowledge();
		lastSyncTime = cap.getLastSyncTime();
	}

	/**
	 * Serializes the knowledge (same layout as the 1.12.2 capability storage)
	 */
	public static CompoundTag write(INecroDataCapability instance) {
		CompoundTag properties = new CompoundTag();
		properties.put("entityTriggers", toList(instance.getEntityTriggers()));
		properties.put("biomeTriggers", toList(instance.getBiomeTriggers()));
		properties.put("dimensionTriggers", toList(instance.getDimensionTriggers()));
		properties.put("artifactTriggers", toList(instance.getArtifactTriggers()));
		properties.put("pageTriggers", toList(instance.getPageTriggers()));
		properties.put("whisperTriggers", toList(instance.getWhisperTriggers()));
		properties.put("miscTriggers", toList(instance.getMiscTriggers()));
		if(instance.hasUnlockedAllKnowledge())
			properties.putBoolean("HasAllKnowledge", true);
		return properties;
	}

	/**
	 * Deserializes the knowledge (unknown entity types are dropped, like in 1.12.2)
	 */
	public static void read(INecroDataCapability instance, CompoundTag properties) {
		ListTag l = properties.getList("entityTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++) {
			ResourceLocation id = ResourceLocation.tryParse(l.getString(i));
			if(id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id))
				instance.triggerEntityUnlock(l.getString(i));
		}
		l = properties.getList("biomeTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerBiomeUnlock(l.getString(i));
		l = properties.getList("dimensionTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerDimensionUnlock(l.getString(i));
		l = properties.getList("artifactTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerArtifactUnlock(l.getString(i));
		l = properties.getList("pageTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerPageUnlock(l.getString(i));
		l = properties.getList("whisperTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerWhisperUnlock(l.getString(i));
		l = properties.getList("miscTriggers", Tag.TAG_STRING);
		for(int i = 0; i < l.size(); i++)
			instance.triggerMiscUnlock(l.getString(i));
		instance.unlockAllKnowledge(properties.getBoolean("HasAllKnowledge"));
	}

	private static ListTag toList(List<String> list) {
		ListTag l = new ListTag();
		for(String name : list)
			if(name != null)
				l.add(StringTag.valueOf(name));
		return l;
	}
}
