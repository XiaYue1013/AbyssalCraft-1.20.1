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
package com.shinoow.abyssalcraft.api.transfer.caps;

import com.shinoow.abyssalcraft.api.transfer.ItemTransferConfiguration;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Attaches {@link IItemTransferCapability} to block entities with an item handler
 */
public class ItemTransferCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

	public static final Capability<IItemTransferCapability> ITEM_TRANSFER_CAP = CapabilityManager.get(new CapabilityToken<>(){});

	private final IItemTransferCapability capability = new ItemTransferCapability();
	private final LazyOptional<IItemTransferCapability> optional = LazyOptional.of(() -> capability);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return ITEM_TRANSFER_CAP.orEmpty(cap, optional);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag properties = new CompoundTag();
		ListTag list = new ListTag();
		capability.getTransferConfigurations().stream().map(ItemTransferConfiguration::serializeNBT).forEach(list::add);
		properties.put("configurations", list);
		properties.putBoolean("isRunning", capability.isRunning());
		return properties;
	}

	@Override
	public void deserializeNBT(CompoundTag properties) {
		if(properties.contains("configurations")) {
			ListTag list = properties.getList("configurations", Tag.TAG_COMPOUND);
			for(Tag tag : list) {
				ItemTransferConfiguration config = new ItemTransferConfiguration();
				config.deserializeNBT((CompoundTag)tag);
				capability.addTransferConfiguration(config);
			}
		}
		capability.setRunning(properties.getBoolean("isRunning"));
	}

	public void invalidate() {
		optional.invalidate();
	}
}
