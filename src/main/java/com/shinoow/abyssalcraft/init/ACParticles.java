/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - particle types (1.12.2 spawned its particle classes directly through the proxy)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AbyssalCraft.modid);

	/** Potential Energy stream (random color out of the four PE colors) */
	/** Blue flame of active ritual altars and pedestals */
	public static final RegistryObject<SimpleParticleType> BLUE_FLAME = PARTICLES.register("blue_flame", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> PE_STREAM = PARTICLES.register("pe_stream", () -> new SimpleParticleType(false));
}
