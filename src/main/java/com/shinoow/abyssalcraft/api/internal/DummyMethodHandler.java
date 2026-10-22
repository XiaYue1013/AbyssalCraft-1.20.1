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
package com.shinoow.abyssalcraft.api.internal;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class DummyMethodHandler implements IInternalMethodHandler {

	@Override
	public void sendDisruption(DeityType deity, String name, BlockPos pos, Level world) {}

	@Override
	public void spawnParticle(String particleName, Level world, double posX, double posY, double posZ, double velX, double velY, double velZ) {}

	@Override
	public void spawnPEStream(BlockPos posFrom, BlockPos posTo, Level world) {}

	@Override
	public void spawnPEStream(BlockPos posFrom, Entity target, Level world) {}

	@Override
	public void generateDarklandsStructure(int type, Level world, RandomSource random, BlockPos pos) {}

	@Override
	public void generateDarklandsStructure(int type, Level world, RandomSource random, BlockPos pos, BlockState spawnBlock, BlockState... extra) {}

	@Override
	public void completeRitualClient(BlockPos pos, Player player, String ritual) {}

	@Override
	public boolean isImmuneOrCarrier(String entity, int list) { return false; }
}
