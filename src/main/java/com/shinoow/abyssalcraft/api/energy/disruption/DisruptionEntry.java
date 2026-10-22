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

import java.util.List;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Base for Disruption Entries
 * @author shinoow
 *
 * @since 1.5
 */
public abstract class DisruptionEntry {

	private DeityType deity;
	private String unlocalizedName;

	/**
	 * A Disruption Entry
	 * @param unlocalizedName A string representing the disruption name
	 * @param deity Deity it's associated with (if any)
	 */
	public DisruptionEntry(String unlocalizedName, DeityType deity){
		this.deity = deity;
		this.unlocalizedName = unlocalizedName;
	}

	/**
	 * Getter for the Deity Type
	 */
	public DeityType getDeity(){
		return deity;
	}

	/**
	 * Getter for the unlocalized name
	 */
	public String getUnlocalizedName(){
		return "ac.disruption." + unlocalizedName;
	}

	/**
	 * Getter for the localized name (client side)
	 */
	public String getLocalizedName(){
		return Component.translatable(getUnlocalizedName()).getString();
	}

	/**
	 * Getter for the description (client side)
	 */
	public String getDescrption(){
		return Component.translatable(getUnlocalizedName() + ".desc").getString();
	}

	/**
	 * Triggers the disruption
	 * @param world Current World
	 * @param pos Current Position
	 * @param players Nearby players
	 */
	public abstract void disrupt(Level world, BlockPos pos, List<Player> players);
}
