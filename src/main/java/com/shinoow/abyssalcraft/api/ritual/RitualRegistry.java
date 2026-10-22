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
package com.shinoow.abyssalcraft.api.ritual;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.APIUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Registry class for Necronomicon Rituals
 * @author shinoow
 *
 * @since 1.4
 */
public class RitualRegistry {

	private final Map<ResourceKey<Level>, Integer> dimToBookType = new HashMap<>();
	private final Map<ResourceKey<Level>, String> dimToName = new HashMap<>();
	private final List<NecronomiconRitual> rituals = new ArrayList<>();
	private final Map<ResourceKey<Level>, Integer> configDimToBookType = new HashMap<>();
	private final Map<ResourceKey<Level>, String> configDimToName = new HashMap<>();

	private final Logger logger = LogManager.getLogger("RitualRegistry");

	private static final RitualRegistry instance = new RitualRegistry();

	public static RitualRegistry instance(){
		return instance;
	}

	private RitualRegistry(){}

	private static boolean isForbidden(ResourceKey<Level> dim) {
		return dim == Level.NETHER || dim == Level.END;
	}

	/**
	 * Used to map a dimension to a Necronomicon book type, in order to allow rituals to be performed there
	 * @param dim The dimension
	 * @param bookType The book type required (0 = Necronomicon ... 4 = Abyssalnomicon)
	 */
	public void addDimensionToBookType(ResourceKey<Level> dim, int bookType){
		if(bookType <= 4 && bookType >= 0)
			if(!isForbidden(dim))
				dimToBookType.put(dim, bookType);
			else logger.error("You're not allowed to register that Dimension: {}", dim.location());
		else logger.error("Necronomicon book type does not exist: {}", bookType);
	}

	/**
	 * Used to give a dimension a display name (used for dimension-specific rituals)
	 */
	public void addDimensionToName(ResourceKey<Level> dim, String name){
		if(!isForbidden(dim))
			dimToName.put(dim, name);
		else logger.error("You're not allowed to register that Dimension: {}", dim.location());
	}

	public void addDimensionToBookTypeAndName(ResourceKey<Level> dim, int bookType, String name){
		addDimensionToBookType(dim, bookType);
		addDimensionToName(dim, name);
	}

	/**
	 * Config-defined dimension mappings (wiped and re-added whenever the config reloads)
	 */
	public void addDimensionToBookTypeConfig(ResourceKey<Level> dim, int bookType) {
		if(bookType <= 4 && bookType >= 0)
			configDimToBookType.put(dim, bookType);
		else logger.error("Necronomicon book type does not exist: {}", bookType);
	}

	public void addDimensionToNameConfig(ResourceKey<Level> dim, String name) {
		configDimToName.put(dim, name);
	}

	public void addDimensionToBookTypeConfig(ResourceKey<Level> dim, int bookType, String name){
		addDimensionToBookTypeConfig(dim, bookType);
		addDimensionToNameConfig(dim, name);
	}

	public void wipeConfig() {
		configDimToBookType.clear();
		configDimToName.clear();
	}

	/**
	 * Checks if a ritual action (altar creation, ritual) can be performed in the dimension with the book type
	 */
	public boolean canPerformAction(ResourceKey<Level> dim, int bookType){
		if(dimToBookType.containsKey(dim))
			return bookType >= dimToBookType.get(dim);
		else if(configDimToBookType.containsKey(dim))
			return bookType >= configDimToBookType.get(dim);
		return false;
	}

	/**
	 * Checks if the book type is exactly the one mapped to the dimension
	 */
	public boolean sameBookType(ResourceKey<Level> dim, int bookType){
		if(dimToBookType.containsKey(dim))
			return bookType == dimToBookType.get(dim);
		else if(configDimToBookType.containsKey(dim))
			return bookType == configDimToBookType.get(dim);
		return false;
	}

	/**
	 * Fetches the book type mapped to a dimension, or -1
	 */
	public int getBookType(ResourceKey<Level> dim){
		if(dimToBookType.containsKey(dim))
			return dimToBookType.get(dim);
		return configDimToBookType.getOrDefault(dim, -1);
	}

	/**
	 * Registers a Necronomicon Ritual
	 */
	public void registerRitual(NecronomiconRitual ritual){
		if(ritual.getBookType() <= 4 && ritual.getBookType() >= 0){
			for(NecronomiconRitual entry : rituals)
				if(ritual.getUnlocalizedName().equals(entry.getUnlocalizedName())){
					logger.error("Necronomicon Ritual already registered: {}", ritual.getUnlocalizedName());
					return;
				}
			rituals.add(ritual);
		} else logger.error("Necronomicon book type does not exist: {}", ritual.getBookType());
	}

	public List<NecronomiconRitual> getRituals(){
		return rituals;
	}

	/**
	 * Fetches a ritual by its id (without the "ac.ritual." prefix)
	 */
	public NecronomiconRitual getRitual(String id){
		for(NecronomiconRitual ritual : rituals)
			if(ritual.getId().equals(id))
				return ritual;
		return null;
	}

	public Map<ResourceKey<Level>, String> getDimensionNameMappings(){
		Map<ResourceKey<Level>, String> names = new HashMap<>(configDimToName);
		names.putAll(dimToName);
		return names;
	}

	/**
	 * Attempts to find a ritual matching the setup
	 * @param dimension Current dimension
	 * @param bookType Book type of the Necronomicon used
	 * @param offerings Items on the pedestals
	 * @param sacrifice Item on the altar
	 * @return The ritual, or null if none matches
	 */
	public NecronomiconRitual getRitual(ResourceKey<Level> dimension, int bookType, ItemStack[] offerings, ItemStack sacrifice){
		return rituals.stream().filter(ritual -> areRitualsSame(ritual, dimension, bookType, offerings, sacrifice)).findFirst().orElse(null);
	}

	private boolean areRitualsSame(NecronomiconRitual ritual, ResourceKey<Level> dimension, int bookType, ItemStack[] offerings, ItemStack sacrifice){
		if(ritual.getDimension() == null || ritual.getDimension() == dimension)
			if(ritual.getBookType() <= bookType)
				if(ritual.getOfferings() != null && offerings != null)
					if(APIUtils.areItemStackArraysEqual(ritual.getOfferings(), offerings, ritual.isNBTSensitive()))
						if(ritual.requiresItemSacrifice() || ritual.getSacrifice() == null && sacrifice.isEmpty() ||
						APIUtils.areObjectsEqual(sacrifice, ritual.getSacrifice(), ritual.isSacrificeNBTSensitive()))
							return true;
		return false;
	}
}
