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

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Attaches {@link INecroDataCapability} to players
 */
public class NecroDataCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

	public static final Capability<INecroDataCapability> NECRO_DATA_CAP = CapabilityManager.get(new CapabilityToken<>(){});

	private final INecroDataCapability capability = new NecroDataCapability();
	private final LazyOptional<INecroDataCapability> optional = LazyOptional.of(() -> capability);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return NECRO_DATA_CAP.orEmpty(cap, optional);
	}

	@Override
	public CompoundTag serializeNBT() {
		return NecroDataCapability.write(capability);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		NecroDataCapability.read(capability, nbt);
	}

	public void invalidate() {
		optional.invalidate();
	}
}
