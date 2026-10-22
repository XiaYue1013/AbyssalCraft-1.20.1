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
 *     1.20.1 port - registered through a DeferredRegister (same sound ids as 1.12.2, so sounds.json is unchanged)
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib;

import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Sound references
 * @author shinoow
 *
 */
public class ACSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AbyssalCraft.modid);

	public static final RegistryObject<SoundEvent> dreadguard_ambient = register("dreadguard.idle");
	public static final RegistryObject<SoundEvent> dreadguard_hurt = register("dreadguard.hit");
	public static final RegistryObject<SoundEvent> dreadguard_death = register("dreadguard.death");
	public static final RegistryObject<SoundEvent> ghoul_normal_ambient = register("ghoul.normal.idle");
	public static final RegistryObject<SoundEvent> ghoul_hurt = register("ghoul.hit");
	public static final RegistryObject<SoundEvent> ghoul_death = register("ghoul.death");
	public static final RegistryObject<SoundEvent> ghoul_pete_ambient = register("ghoul.pete.idle");
	public static final RegistryObject<SoundEvent> ghoul_wilson_ambient = register("ghoul.wilson.idle");
	public static final RegistryObject<SoundEvent> ghoul_orange_ambient = register("ghoul.orange.idle");
	public static final RegistryObject<SoundEvent> golem_death = register("golem.death");
	public static final RegistryObject<SoundEvent> golem_hurt = register("golem.hit");
	public static final RegistryObject<SoundEvent> golem_ambient = register("golem.idle");
	public static final RegistryObject<SoundEvent> sacthoth_death = register("sacthoth.death");
	public static final RegistryObject<SoundEvent> shadow_death = register("shadow.death");
	public static final RegistryObject<SoundEvent> shadow_hurt = register("shadow.hit");
	public static final RegistryObject<SoundEvent> remnant_scream = register("remnant.scream");
	public static final RegistryObject<SoundEvent> remnant_yes = register("remnant.yes");
	public static final RegistryObject<SoundEvent> remnant_no = register("remnant.no");
	public static final RegistryObject<SoundEvent> remnant_priest_chant = register("remnant.priest.chant");
	public static final RegistryObject<SoundEvent> shoggoth_ambient = register("shoggoth.idle");
	public static final RegistryObject<SoundEvent> shoggoth_hurt = register("shoggoth.hit");
	public static final RegistryObject<SoundEvent> shoggoth_death = register("shoggoth.death");
	public static final RegistryObject<SoundEvent> shoggoth_step = register("shoggoth.step");
	public static final RegistryObject<SoundEvent> shoggoth_shoot = register("shoggoth.shoot");
	public static final RegistryObject<SoundEvent> shoggoth_birth = register("shoggoth.birth");
	public static final RegistryObject<SoundEvent> shoggoth_consume = register("shoggoth.consume");
	public static final RegistryObject<SoundEvent> jzahar_charge = register("jzahar.charge");
	public static final RegistryObject<SoundEvent> cthulhu_chant = register("chant.cthulhu");
	public static final RegistryObject<SoundEvent> yog_sothoth_chant_1 = register("chant.yog_sothoth_1");
	public static final RegistryObject<SoundEvent> yog_sothoth_chant_2 = register("chant.yog_sothoth_2");
	public static final RegistryObject<SoundEvent> hastur_chant_1 = register("chant.hastur_1");
	public static final RegistryObject<SoundEvent> hastur_chant_2 = register("chant.hastur_2");
	public static final RegistryObject<SoundEvent> sleeping_chant = register("chant.sleeping");
	public static final RegistryObject<SoundEvent> cthugha_chant = register("chant.cthugha");
	public static final RegistryObject<SoundEvent> dread_spawn_ambient = register("dreadspawn.idle");
	public static final RegistryObject<SoundEvent> dread_spawn_hurt = register("dreadspawn.hit");
	public static final RegistryObject<SoundEvent> dread_spawn_death = register("dreadspawn.death");
	public static final RegistryObject<SoundEvent> abyssal_zombie_ambient = register("abyssalzombie.idle");
	public static final RegistryObject<SoundEvent> abyssal_zombie_hurt = register("abyssalzombie.hit");
	public static final RegistryObject<SoundEvent> abyssal_zombie_death = register("abyssalzombie.death");
	public static final RegistryObject<SoundEvent> antiplayer_hurt = register("antiplayer.hurt");
	public static final RegistryObject<SoundEvent> dreadguard_barf = register("dreadguard.barf");
	public static final RegistryObject<SoundEvent> jzahar_blast = register("jzahar.blast");
	public static final RegistryObject<SoundEvent> jzahar_shout = register("jzahar.shout");
	public static final RegistryObject<SoundEvent> jzahar_earthquake = register("jzahar.earthquake");
	public static final RegistryObject<SoundEvent> jzahar_implosion = register("jzahar.implosion");
	public static final RegistryObject<SoundEvent> jzahar_black_hole = register("jzahar.black_hole");

	private static RegistryObject<SoundEvent> register(String name) {
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(AbyssalCraft.loc(name)));
	}
}
