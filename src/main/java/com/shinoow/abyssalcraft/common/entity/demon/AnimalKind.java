/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - what the four 1.12.2 Evil/Demon Pig, Cow, Chicken and Sheep classes differed in
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.demon;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** The animal an Evil or Demon Animal looks and sounds like */
public enum AnimalKind {
	PIG("pig", 15, () -> SoundEvents.PIG_AMBIENT, () -> SoundEvents.PIG_DEATH, () -> SoundEvents.PIG_STEP, 1.0F, () -> Items.PORKCHOP),
	COW("cow", 15, () -> SoundEvents.COW_AMBIENT, () -> SoundEvents.COW_DEATH, () -> SoundEvents.COW_STEP, 0.4F, () -> Items.LEATHER),
	CHICKEN("chicken", 10, () -> SoundEvents.CHICKEN_AMBIENT, () -> SoundEvents.CHICKEN_DEATH, () -> SoundEvents.CHICKEN_STEP, 1.0F, () -> Items.FEATHER),
	SHEEP("sheep", 12, () -> SoundEvents.SHEEP_AMBIENT, () -> SoundEvents.SHEEP_DEATH, () -> SoundEvents.SHEEP_STEP, 1.0F, () -> Items.WHITE_WOOL);

	public final String name;
	/** Max health (double in Hardcore Mode, attack damage 4 there) */
	public final double health;
	public final Supplier<SoundEvent> ambient, death, step;
	public final float volume;
	/** What shearing an Evil Animal gives (1 - 3) */
	public final Supplier<Item> shearDrop;

	AnimalKind(String name, double health, Supplier<SoundEvent> ambient, Supplier<SoundEvent> death, Supplier<SoundEvent> step, float volume, Supplier<Item> shearDrop) {
		this.name = name;
		this.health = health;
		this.ambient = ambient;
		this.death = death;
		this.step = step;
		this.volume = volume;
		this.shearDrop = shearDrop;
	}
}
