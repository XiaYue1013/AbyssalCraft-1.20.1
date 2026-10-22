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
package com.shinoow.abyssalcraft.common.disruptions;

import java.util.List;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainer;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.energy.IEnergyManipulator;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;
import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Drains nearby PE collectors (or, if there are none, the PE items of nearby players)
 */
public class DisruptionDrainNearbyPE extends DisruptionEntry {

	public DisruptionDrainNearbyPE() {
		super("potentialEnergyDrain", null);
	}

	@Override
	public void disrupt(Level world, BlockPos pos, List<Player> players) {
		int drained = 0;
		BlockEntity tile = world.getBlockEntity(pos);
		int range, depth, divisor;
		if(tile instanceof IEnergyManipulator manipulator){
			range = (int) (PEUtils.getRangeAmplifiers(world, pos, manipulator) + manipulator.getAmplifier(AmplifierType.RANGE)/2);
			depth = PEUtils.getRangeAmplifiers(world, pos, manipulator);
			divisor = 4;
		} else {
			range = 4;
			depth = 2;
			divisor = 2;
		}
		for(int x = -1*(3+range); x <= 3+range; x++)
			for(int y = 0; y <= depth; y++)
				for(int z = -1*(3+range); z <= 3+range; z++) {
					BlockPos pos1 = pos.offset(x, -y, z);
					BlockEntity target = world.getBlockEntity(pos1);
					if(PEUtils.isCollector(target)){
						IEnergyContainer collector = (IEnergyContainer) target;
						if(!world.isClientSide)
							collector.consumeEnergy(collector.getContainedEnergy()/(world.random.nextInt(4) == 0 ? 1 : divisor));
						drained++;
						for(int i = 0; i < 3; i++)
							world.addParticle(ParticleTypes.LARGE_SMOKE, pos1.getX() + 0.5D, pos1.getY() + 0.5D, pos1.getZ() + 0.5D, 0,0,0);
					}
				}

		if(drained == 0)
			for(Player player : players)
				for(ItemStack item : player.getInventory().items)
					if(item.getItem() instanceof IEnergyContainerItem container && container.getContainedEnergy(item) > 0){
						if(!world.isClientSide) {
							container.consumeEnergy(item, container.getContainedEnergy(item)/(world.random.nextInt(4) == 0 ? 2 : 10));
							if(isNecronomicon(item.getItem()))
								player.hurt(world.damageSources().magic(), 2);
						}
						for(int i = 0; i < 3; i++)
							world.addParticle(ParticleTypes.LARGE_SMOKE, player.getX() + (world.random.nextDouble() - 0.5D) * player.getBbWidth(),
									player.getY() + world.random.nextDouble() * player.getBbHeight(), player.getZ() + (world.random.nextDouble() - 0.5D) * player.getBbWidth(), 0,0,0);
					}
	}

	boolean isNecronomicon(Item item){
		return item == ACItems.necronomicon.orElse(null) || item == ACItems.abyssal_wasteland_necronomicon.orElse(null)
				|| item == ACItems.dreadlands_necronomicon.orElse(null) || item == ACItems.omothol_necronomicon.orElse(null)
				|| item == ACItems.abyssalnomicon.orElse(null);
	}
}
