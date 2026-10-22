/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2026 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.energy.structure.StructureHandler;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.AbyssalCrafting;
import com.shinoow.abyssalcraft.common.handlers.AbyssalCraftEventHooks;
import com.shinoow.abyssalcraft.common.handlers.InternalMethodHandler;
import com.shinoow.abyssalcraft.common.handlers.KnowledgeEventHandler;
import com.shinoow.abyssalcraft.common.handlers.PlagueEventHandler;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.structures.pe.ArchwayStructure;
import com.shinoow.abyssalcraft.common.structures.pe.BasicStructure;
import com.shinoow.abyssalcraft.common.structures.pe.TotemPoleStructure;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEffects;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.ACFeatures;
import com.shinoow.abyssalcraft.init.ACFluids;
import com.shinoow.abyssalcraft.init.ACMenus;
import com.shinoow.abyssalcraft.init.ACParticles;
import com.shinoow.abyssalcraft.init.ACPoiTypes;
import com.shinoow.abyssalcraft.init.ACStructures;
import com.shinoow.abyssalcraft.init.ACTabs;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.Scheduler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AbyssalCraft.modid)
public class AbyssalCraft {

	public static final String modid = "abyssalcraft";
	public static final String name = "AbyssalCraft";
	public static final Logger LOGGER = LogManager.getLogger(name);

	public AbyssalCraft(FMLJavaModLoadingContext context) {
		IEventBus modBus = context.getModEventBus();

		context.registerConfig(ModConfig.Type.COMMON, ACConfig.COMMON_SPEC, "abyssalcraft-common.toml");
		context.registerConfig(ModConfig.Type.CLIENT, ACConfig.CLIENT_SPEC, "abyssalcraft-client.toml");
		modBus.addListener(ACConfig::onConfigLoad);
		modBus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.addListener((TagsUpdatedEvent e) -> AbyssalCrafting.onTagsLoaded());

		MinecraftForge.EVENT_BUS.register(new PlagueEventHandler());
		MinecraftForge.EVENT_BUS.register(new KnowledgeEventHandler());
		MinecraftForge.EVENT_BUS.register(new AbyssalCraftEventHooks());
		MinecraftForge.EVENT_BUS.register(new Scheduler.Events());
		modBus.addListener(KnowledgeEventHandler::registerCapabilities);
		modBus.addListener(ACEntityTypes::registerAttributes);
		modBus.addListener(ACEntityTypes::registerSpawnPlacements);
		modBus.addListener((net.minecraftforge.event.BuildCreativeModeTabContentsEvent e) -> {
			if(e.getTabKey() == net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS)
				ItemHandler.SPAWN_EGGS.forEach(egg -> e.accept(egg.get()));
		});

		ACFluids.FLUID_TYPES.register(modBus);
		ACFluids.FLUIDS.register(modBus);
		BlockHandler.BLOCKS.register(modBus);
		ItemHandler.registerBlockItems();
		ItemHandler.ITEMS.register(modBus);
		ACTabs.TABS.register(modBus);
		ACBlockEntities.BLOCK_ENTITIES.register(modBus);
		ACMenus.MENUS.register(modBus);
		ACPoiTypes.POI_TYPES.register(modBus);
		ACParticles.PARTICLES.register(modBus);
		ACSounds.SOUNDS.register(modBus);
		ACEntityTypes.ENTITY_TYPES.register(modBus);
		ACFeatures.FEATURES.register(modBus);
		com.shinoow.abyssalcraft.common.world.ConfigSpawnsBiomeModifier.SERIALIZERS.register(modBus);
		ACFeatures.CARVERS.register(modBus);
		ACFeatures.PLACEMENT_MODIFIERS.register(modBus);
		ACStructures.STRUCTURE_TYPES.register(modBus);
		ACStructures.PIECE_TYPES.register(modBus);
		ACStructures.PLACEMENT_TYPES.register(modBus);

		PacketDispatcher.registerPackets();
		AbyssalCraftAPI.setInternalMethodHandler(new InternalMethodHandler());
		AbyssalCraftAPI.setInternalNDHandler(new com.shinoow.abyssalcraft.common.handlers.InternalNecroDataHandler());
		modBus.addListener((net.minecraftforge.fml.event.lifecycle.InterModProcessEvent e) -> com.shinoow.abyssalcraft.common.handlers.IMCHandler.handle(e));
		ACEffects.EFFECTS.register(modBus);
		ACEffects.POTIONS.register(modBus);
		com.shinoow.abyssalcraft.init.ACEnchantments.ENCHANTMENTS.register(modBus);
		net.minecraftforge.common.crafting.CraftingHelper.register(com.shinoow.abyssalcraft.common.crafting.ConfigCondition.Serializer.INSTANCE);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(KnowledgeEventHandler::registerConditions);
		event.enqueueWork(AbyssalCrafting::addRecipes);
		event.enqueueWork(() -> AbyssalCraftAPI.getInternalNDHandler().registerInternalPages());
		event.enqueueWork(ACEffects::addBrewing);
		event.enqueueWork(ItemHandler::registerGhoulArmorTextures);
		event.enqueueWork(com.shinoow.abyssalcraft.common.entity.EntityRemnant::registerTradeAmounts);
		// dimension -> Necronomicon book type needed to perform rituals there
		RitualRegistry.instance().addDimensionToBookType(Level.OVERWORLD, 0);
		RitualRegistry.instance().addDimensionToBookType(ACDimensions.ABYSSAL_WASTELAND, 1);
		RitualRegistry.instance().addDimensionToBookType(ACDimensions.DREADLANDS, 2);
		RitualRegistry.instance().addDimensionToBookType(ACDimensions.OMOTHOL, 3);
		RitualRegistry.instance().addDimensionToBookType(ACDimensions.DARK_REALM, 3);
		if(net.minecraftforge.fml.ModList.get().isLoaded("terrablender"))
			event.enqueueWork(com.shinoow.abyssalcraft.integration.terrablender.ACTerraBlender::register);
		StructureHandler.instance().registerStructure(new BasicStructure());
		StructureHandler.instance().registerStructure(new TotemPoleStructure());
		StructureHandler.instance().registerStructure(new ArchwayStructure());
		// what Lesser Shoggoths hunt (1.12.2 also listed the evil/demon animals, added with them)
		EntityUtil.addShoggothFood(net.minecraft.world.entity.animal.Animal.class);
		EntityUtil.addShoggothFood(net.minecraft.world.entity.ambient.AmbientCreature.class);
		EntityUtil.addShoggothFood(net.minecraft.world.entity.animal.WaterAnimal.class);
		EntityUtil.addShoggothFood(net.minecraft.world.entity.monster.Spider.class);
	}

	public static ResourceLocation loc(String path) {
		return new ResourceLocation(modid, path);
	}
}
