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
package com.shinoow.abyssalcraft.api.energy.disruption;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.event.ACEvents.DisruptionEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

/**
 * Handler for Disruption Entries
 * @author shinoow
 *
 * @since 1.5
 */
public class DisruptionHandler {

	private final List<DisruptionEntry> disruptions = new ArrayList<>();

	private final Logger logger = LogManager.getLogger("DisruptionHandler");

	private static final DisruptionHandler instance = new DisruptionHandler();

	public static DisruptionHandler instance(){
		return instance;
	}

	private DisruptionHandler(){}

	/**
	 * Registers a Disruption Entry
	 * @param disruption The Disruption Entry
	 */
	public void registerDisruption(DisruptionEntry disruption){
		for(DisruptionEntry entry : disruptions)
			if(disruption.getUnlocalizedName().equals(entry.getUnlocalizedName())){
				logger.error("Disruption Entry already registered: {}", disruption.getUnlocalizedName());
				return;
			}
		disruptions.add(disruption);
	}

	/**
	 * Used to fetch a list of all registered Disruption Entries
	 */
	public List<DisruptionEntry> getDisruptions(){
		return disruptions;
	}

	/**
	 * Fetches a Disruption Entry from its name (with or without the "ac.disruption." prefix)
	 * @param name Disruption name
	 * @return A Disruption Entry, or null if not found
	 */
	public DisruptionEntry disruptionFromName(String name){
		if(name.startsWith("ac.disruption."))
			name = name.substring("ac.disruption.".length());
		for(DisruptionEntry dis : disruptions)
			if(dis.getUnlocalizedName().substring("ac.disruption.".length()).equals(name))
				return dis;
		return null;
	}

	/**
	 * Generates a random Disruption (server side only)
	 * @param deity Deity Type (can be null)
	 * @param world Current World
	 * @param pos Current Position
	 * @param players Nearby players
	 */
	public void generateDisruption(DeityType deity, Level world, BlockPos pos, List<Player> players){
		DisruptionEntry disruption = getRandomDisruption(deity, world);
		if(disruption != null) {
			if(!MinecraftForge.EVENT_BUS.post(new DisruptionEvent(deity, world, pos, players, disruption)))
				disruption.disrupt(world, pos, players);
			AbyssalCraftAPI.getInternalMethodHandler().sendDisruption(deity, disruption.getUnlocalizedName().substring("ac.disruption.".length()), pos, world);
		}
	}

	/**
	 * Picks a random Disruption Entry that matches the deity (or has no deity)
	 */
	public DisruptionEntry getRandomDisruption(DeityType deity, Level world) {
		if(world.isClientSide) return null;
		List<DisruptionEntry> dis = new ArrayList<>();
		for(DisruptionEntry entry : disruptions)
			if(entry.getDeity() == null || deity != null && entry.getDeity() == deity)
				dis.add(entry);
		if(dis.isEmpty()) return null;
		return dis.get(world.random.nextInt(dis.size()));
	}
}
