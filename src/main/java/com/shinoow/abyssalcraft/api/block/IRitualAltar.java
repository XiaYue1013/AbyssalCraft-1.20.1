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
package com.shinoow.abyssalcraft.api.block;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Interface for Ritual Altar block entities
 *
 * @author shinoow
 *
 */
public interface IRitualAltar extends ISingletonInventory {

	/**
	 * Attempts to start a ritual
	 */
	void performRitual(Level world, BlockPos pos, Player player);

	/**
	 * Whether or not a ritual can be started (cooldown etc.)
	 */
	boolean canPerform();

	int getRitualCooldown();

	boolean isPerformingRitual();

	void setRitualFields(@Nullable NecronomiconRitual ritual, @Nullable Mob sacrifice);

	void addPedestal(IRitualPedestal pedestal);

	List<IRitualPedestal> getPedestals();

	EnumRitualParticle getRitualParticle();
}
