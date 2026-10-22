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
 *     1.20.1 port - SavedData stored with the overworld (1.12.2 MapStorage was global as well)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * The last 20 named mobs that died, by name, for the Resurrection ritual
 */
public class NecromancyWorldSavedData extends SavedData {

	private static final String DATA_NAME = "abyssalcraft_necromancy_data";

	private final List<Tuple<String, CompoundTag>> data = new ArrayList<>();

	public static NecromancyWorldSavedData load(CompoundTag properties) {
		NecromancyWorldSavedData instance = new NecromancyWorldSavedData();
		CompoundTag data = properties.getCompound("Data");
		for(String name : data.getAllKeys())
		{
			ListTag list = data.getList(name, Tag.TAG_COMPOUND);
			for(int i = 0; i < list.size(); i++)
				instance.storeDataInternal(name, list.getCompound(i));
		}
		return instance;
	}

	@Override
	public CompoundTag save(CompoundTag properties) {
		properties.putInt("Version", 1);
		CompoundTag data = new CompoundTag();
		for(Tuple<String, CompoundTag> t : getData())
			if(data.contains(t.getA()))
				data.getList(t.getA(), Tag.TAG_COMPOUND).add(t.getB());
			else {
				ListTag list = new ListTag();
				list.add(t.getB());
				data.put(t.getA(), list);
			}
		properties.put("Data", data);
		return properties;
	}

	@Nullable
	public CompoundTag getDataForName(String name) {
		for(Tuple<String, CompoundTag> t : data)
			if(t.getA().equals(name))
				return t.getB();
		return null;
	}

	/**
	 * Remembers a named mob
	 * @param size Crystal size needed to resurrect it (0 = shards, 1 = crystals, 2 = clusters)
	 */
	public void storeData(String name, CompoundTag data, int size) {
		data.putInt("ResurrectionRitualCrystalSize", size);
		storeDataInternal(name, data);
		setDirty();
	}

	private void storeDataInternal(String name, CompoundTag data)
	{
		if(this.data.size() == 20)
			this.data.remove(0);
		this.data.add(new Tuple<>(name, data));
	}

	public void clearEntry(String name) {
		for(int i = 0; i < data.size(); i++)
			if(data.get(i).getA().equals(name))
			{
				data.remove(i);
				break;
			}
		setDirty();
	}

	public List<Tuple<String, CompoundTag>> getData() {
		return data;
	}

	public static NecromancyWorldSavedData get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(NecromancyWorldSavedData::load, NecromancyWorldSavedData::new, DATA_NAME);
	}
}
