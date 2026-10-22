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
 *     1.20.1 port - menu types (replaces the 1.12.2 GuiHandler ids)
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.inventory.ContainerConfigurator;
import com.shinoow.abyssalcraft.common.inventory.ContainerCrystalBag;
import com.shinoow.abyssalcraft.common.inventory.ContainerEnergyContainer;
import com.shinoow.abyssalcraft.common.inventory.ContainerEnergyDepositioner;
import com.shinoow.abyssalcraft.common.inventory.ContainerEngraver;
import com.shinoow.abyssalcraft.common.inventory.ContainerMaterializer;
import com.shinoow.abyssalcraft.common.inventory.ContainerProcessor;
import com.shinoow.abyssalcraft.common.inventory.ContainerRendingPedestal;
import com.shinoow.abyssalcraft.common.inventory.ContainerStateTransformer;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AbyssalCraft.modid);

	public static final RegistryObject<MenuType<ContainerProcessor>> CRYSTALLIZER = MENUS.register("crystallizer",
			() -> IForgeMenuType.create((id, inv, buf) -> ContainerProcessor.crystallizerClient(id, inv)));
	public static final RegistryObject<MenuType<ContainerProcessor>> TRANSMUTATOR = MENUS.register("transmutator",
			() -> IForgeMenuType.create((id, inv, buf) -> ContainerProcessor.transmutatorClient(id, inv)));
	public static final RegistryObject<MenuType<ContainerCrystalBag>> CRYSTAL_BAG = MENUS.register("crystal_bag",
			() -> IForgeMenuType.create(ContainerCrystalBag::client));
	public static final RegistryObject<MenuType<ContainerMaterializer>> MATERIALIZER = MENUS.register("materializer",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerMaterializer(id, inv)));
	public static final RegistryObject<MenuType<ContainerEngraver>> ENGRAVER = MENUS.register("engraver",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerEngraver(id, inv)));
	public static final RegistryObject<MenuType<ContainerStateTransformer>> STATE_TRANSFORMER = MENUS.register("state_transformer",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerStateTransformer(id, inv)));
	public static final RegistryObject<MenuType<ContainerEnergyDepositioner>> ENERGY_DEPOSITIONER = MENUS.register("energy_depositioner",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerEnergyDepositioner(id, inv)));
	public static final RegistryObject<MenuType<ContainerRendingPedestal>> RENDING_PEDESTAL = MENUS.register("rending_pedestal",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerRendingPedestal(id, inv)));
	public static final RegistryObject<MenuType<ContainerConfigurator>> CONFIGURATOR = MENUS.register("configurator",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerConfigurator(id, inv)));
	public static final RegistryObject<MenuType<com.shinoow.abyssalcraft.common.inventory.ContainerSpellbook>> SPELLBOOK = MENUS.register("spellbook",
			() -> IForgeMenuType.create(com.shinoow.abyssalcraft.common.inventory.ContainerSpellbook::client));
	public static final RegistryObject<MenuType<ContainerEnergyContainer>> ENERGY_CONTAINER = MENUS.register("energy_container",
			() -> IForgeMenuType.create((id, inv, buf) -> new ContainerEnergyContainer(id, inv)));
}
