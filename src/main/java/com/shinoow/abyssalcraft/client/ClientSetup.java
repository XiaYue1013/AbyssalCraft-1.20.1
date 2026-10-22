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
 *     1.20.1 port - client registration (replaces ClientProxy)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client;

import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.gui.GuiCrystalBag;
import com.shinoow.abyssalcraft.client.gui.GuiEngraver;
import com.shinoow.abyssalcraft.client.gui.GuiMaterializer;
import com.shinoow.abyssalcraft.client.gui.GuiProcessor;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.block.ModelDGhead;
import com.shinoow.abyssalcraft.client.model.entity.ModelChagaroth;
import com.shinoow.abyssalcraft.client.model.entity.ModelChagarothFist;
import com.shinoow.abyssalcraft.client.model.entity.ModelChagarothSpawn;
import com.shinoow.abyssalcraft.client.model.entity.ModelDreadSpawn;
import com.shinoow.abyssalcraft.client.render.entity.RenderDreadedCharge;
import com.shinoow.abyssalcraft.client.render.entity.RenderSimpleMob;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.client.particles.PEStreamParticleFX;
import com.shinoow.abyssalcraft.client.render.block.TileEntityGhoulHeadRenderer;
import com.shinoow.abyssalcraft.common.blocks.BlockCrystalCluster;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACMenus;
import com.shinoow.abyssalcraft.init.ACParticles;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.ACClientVars;
import com.shinoow.abyssalcraft.lib.client.render.TileEntityPedestalBlockRenderer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

/**
 * Client-only setup, never loaded on a dedicated server
 */
@Mod.EventBusSubscriber(modid = AbyssalCraft.modid, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			registerItemProperties();
			com.shinoow.abyssalcraft.client.gui.necronomicon.ClientNecronomicon.init();
			MenuScreens.register(ACMenus.CRYSTALLIZER.get(), GuiProcessor::new);
			MenuScreens.register(ACMenus.TRANSMUTATOR.get(), GuiProcessor::new);
			MenuScreens.register(ACMenus.ENGRAVER.get(), GuiEngraver::new);
			MenuScreens.register(ACMenus.CRYSTAL_BAG.get(), GuiCrystalBag::new);
			MenuScreens.register(ACMenus.SPELLBOOK.get(), com.shinoow.abyssalcraft.client.gui.GuiSpellbook::new);
			MenuScreens.register(ACMenus.MATERIALIZER.get(), GuiMaterializer::new);
			MenuScreens.register(ACMenus.ENERGY_CONTAINER.get(), com.shinoow.abyssalcraft.client.gui.GuiEnergyContainer::new);
			MenuScreens.register(ACMenus.STATE_TRANSFORMER.get(), com.shinoow.abyssalcraft.client.gui.GuiStateTransformer::new);
			MenuScreens.register(ACMenus.CONFIGURATOR.get(), com.shinoow.abyssalcraft.client.gui.GuiConfigurator::new);
			MenuScreens.register(ACMenus.RENDING_PEDESTAL.get(), com.shinoow.abyssalcraft.client.gui.GuiRendingPedestal::new);
			MenuScreens.register(ACMenus.ENERGY_DEPOSITIONER.get(), com.shinoow.abyssalcraft.client.gui.GuiEnergyDepositioner::new);
		});
	}

	/** Item model overrides (1.12.2 addPropertyOverride). Mod properties are namespaced, tools/import_assets.py prefixes them in the models */
	private static void registerItemProperties() {
		ItemProperties.register(ItemHandler.INTERDIMENSIONAL_CAGE.get(), AbyssalCraft.loc("captured"),
				(stack, level, entity, seed) -> com.shinoow.abyssalcraft.common.items.ItemInterdimensionalCage.isOccupied(stack) ? 1 : 0);
		ItemProperties.register(ItemHandler.CONFIGURATOR.get(), AbyssalCraft.loc("mode"),
				(stack, level, entity, seed) -> com.shinoow.abyssalcraft.common.items.ItemConfigurator.getMode(stack) * 0.5F);
		ItemProperties.register(ItemHandler.STAFF_OF_THE_GATEKEEPER.get(), AbyssalCraft.loc("mode"),
				(stack, level, entity, seed) -> com.shinoow.abyssalcraft.common.items.ItemStaff.getMode(stack));
		ItemProperties.register(ItemHandler.STONE_TABLET.get(), AbyssalCraft.loc("cursed"),
				(stack, level, entity, seed) -> ((com.shinoow.abyssalcraft.common.items.ItemStoneTablet) stack.getItem()).isCursed(stack) ? 1.0F : 0.0F);
		for(RegistryObject<Item> antidote : List.of(ItemHandler.CORALIUM_ANTIDOTE, ItemHandler.DREAD_ANTIDOTE))
			ItemProperties.register(antidote.get(), AbyssalCraft.loc("content"),
					(stack, level, entity, seed) -> com.shinoow.abyssalcraft.common.items.ItemAntidote.getContentToFloat(stack));
		for(RegistryObject<Item> scroll : List.of(ItemHandler.BASIC_SCROLL, ItemHandler.LESSER_SCROLL, ItemHandler.MODERATE_SCROLL, ItemHandler.GREATER_SCROLL))
			ItemProperties.register(scroll.get(), AbyssalCraft.loc("inscribed"),
					(stack, level, entity, seed) -> com.shinoow.abyssalcraft.common.items.ItemScroll.isInscribed(stack) ? 1 : 0);
		// same as the vanilla bow
		ItemProperties.register(ItemHandler.CORALIUM_LONGBOW.get(), new ResourceLocation("pull"), (stack, level, entity, seed) ->
				entity == null || entity.getUseItem() != stack ? 0 : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F);
		ItemProperties.register(ItemHandler.CORALIUM_LONGBOW.get(), new ResourceLocation("pulling"), (stack, level, entity, seed) ->
				entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);
	}

	@SubscribeEvent
	public static void itemColors(RegisterColorHandlersEvent.Item event) {
		int[] colors = ACClientVars.getCrystalColors();
		for(int i = 0; i < colors.length; i++) {
			int color = colors[i];
			event.register((stack, tint) -> color, ItemHandler.CRYSTALS.get(i).get(), ItemHandler.CRYSTAL_SHARDS.get(i).get(),
					ItemHandler.CRYSTAL_FRAGMENTS.get(i).get(), BlockHandler.CRYSTAL_CLUSTERS.get(i).get());
		}
		// inscribed scrolls show the spell color on their second layer
		event.register((stack, tint) -> tint == 1 ? com.shinoow.abyssalcraft.api.spell.SpellUtils.getSpellColor(stack) : 0xFFFFFF,
				ItemHandler.BASIC_SCROLL.get(), ItemHandler.LESSER_SCROLL.get(), ItemHandler.MODERATE_SCROLL.get(), ItemHandler.GREATER_SCROLL.get());
	}

	@SubscribeEvent
	public static void blockColors(RegisterColorHandlersEvent.Block event) {
		for(RegistryObject<Block> cluster : BlockHandler.CRYSTAL_CLUSTERS)
			event.register((state, level, pos, tint) -> ACClientVars.getCrystalColors()[((BlockCrystalCluster) state.getBlock()).getIndex()], cluster.get());
	}

	@SubscribeEvent
	public static void blockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ACBlockEntities.ENERGY_PEDESTAL.get(), TileEntityPedestalBlockRenderer.pedestal());
		event.registerBlockEntityRenderer(ACBlockEntities.SACRIFICIAL_ALTAR.get(), TileEntityPedestalBlockRenderer.altar());
		event.registerBlockEntityRenderer(ACBlockEntities.RITUAL_ALTAR.get(), TileEntityPedestalBlockRenderer.altar());
		event.registerBlockEntityRenderer(ACBlockEntities.GHOUL_HEAD.get(), TileEntityGhoulHeadRenderer::new);
		event.registerBlockEntityRenderer(ACBlockEntities.SINGLE_MOB_SPAWNER.get(), com.shinoow.abyssalcraft.client.render.block.TileEntityJzaharSpawnerRenderer::new);
		event.registerBlockEntityRenderer(ACBlockEntities.RENDING_PEDESTAL.get(), TileEntityPedestalBlockRenderer.pedestal());
		event.registerBlockEntityRenderer(ACBlockEntities.RITUAL_PEDESTAL.get(), TileEntityPedestalBlockRenderer.pedestal());
	}

	/** Keyed by the dimension types' "effects" (same ids as the dimensions) */
	@SubscribeEvent
	public static void dimensionEffects(net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent event) {
		event.register(com.shinoow.abyssalcraft.init.ACDimensions.ABYSSAL_WASTELAND_TYPE.location(), com.shinoow.abyssalcraft.client.render.sky.ACDimensionEffects.abyssalWasteland());
		event.register(com.shinoow.abyssalcraft.init.ACDimensions.DREADLANDS_TYPE.location(), com.shinoow.abyssalcraft.client.render.sky.ACDimensionEffects.dreadlands());
		event.register(com.shinoow.abyssalcraft.init.ACDimensions.OMOTHOL_TYPE.location(), com.shinoow.abyssalcraft.client.render.sky.ACDimensionEffects.omothol());
		event.register(com.shinoow.abyssalcraft.init.ACDimensions.DARK_REALM_TYPE.location(), com.shinoow.abyssalcraft.client.render.sky.ACDimensionEffects.darkRealm());
	}

	@SubscribeEvent
	public static void keyMappings(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
		ACKeyBindings.register(event);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@SubscribeEvent
	public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.SPIRIT_ITEM.get(),
				ctx -> (net.minecraft.client.renderer.entity.EntityRenderer) new net.minecraft.client.renderer.entity.ItemEntityRenderer(ctx));
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.DEPTHS_GHOUL.get(), com.shinoow.abyssalcraft.client.render.entity.RenderDepthsGhoul::new);
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.ABYSSAL_ZOMBIE.get(), com.shinoow.abyssalcraft.client.render.entity.RenderAbyssalZombie::new);
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.LESSER_SHOGGOTH.get(), com.shinoow.abyssalcraft.client.render.entity.RenderLesserShoggoth::new);
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.DREADGUARD.get(), com.shinoow.abyssalcraft.client.render.entity.RenderDreadguard::new);
		registerDreadRenderers(event);
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.REMNANT.get(), com.shinoow.abyssalcraft.client.render.entity.RenderRemnant::new);
		event.registerEntityRenderer(com.shinoow.abyssalcraft.init.ACEntityTypes.ACID_PROJECTILE.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
	}

	private static void registerBatch1Renderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ACEntityTypes.DREADLING.get(), ctx -> {
			var renderer = new RenderSimpleMob<>(ctx, new com.shinoow.abyssalcraft.client.model.entity.ModelDreadling<com.shinoow.abyssalcraft.common.entity.EntityDreadling>(ctx.bakeLayer(ACModelLayers.DREADLING)), "dreadling", 1.0F);
			renderer.addLayer(new net.minecraft.client.renderer.entity.layers.CustomHeadLayer<>(renderer, ctx.getModelSet(), ctx.getItemInHandRenderer()));
			return renderer;
		});
		event.registerEntityRenderer(ACEntityTypes.ABYSSALNITE_GOLEM.get(), ctx -> new RenderSimpleMob<>(ctx,
				new net.minecraft.client.model.HumanoidModel<>(ctx.bakeLayer(ACModelLayers.GOLEM)), "aby_warden", 1.0F));
		event.registerEntityRenderer(ACEntityTypes.DREADED_ABYSSALNITE_GOLEM.get(), ctx -> new RenderSimpleMob<>(ctx,
				new net.minecraft.client.model.HumanoidModel<>(ctx.bakeLayer(ACModelLayers.GOLEM)), "dread_warden", 1.0F));
		event.registerEntityRenderer(ACEntityTypes.SKELETON_GOLIATH.get(), ctx -> {
			var renderer = new RenderSimpleMob<>(ctx, new com.shinoow.abyssalcraft.client.model.entity.ModelSkeletonGoliath<com.shinoow.abyssalcraft.common.entity.EntitySkeletonGoliath>(
					ctx.bakeLayer(ACModelLayers.SKELETON_GOLIATH)), "elite/skeletongoliath", 1.5F);
			renderer.addLayer(new net.minecraft.client.renderer.entity.layers.CustomHeadLayer<>(renderer, ctx.getModelSet(), ctx.getItemInHandRenderer()));
			return renderer;
		});
		event.registerEntityRenderer(ACEntityTypes.OMOTHOL_GHOUL.get(), com.shinoow.abyssalcraft.client.render.entity.RenderOmotholGhoul::new);
		event.registerEntityRenderer(ACEntityTypes.EVIL_PIG.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::evilPig);
		event.registerEntityRenderer(ACEntityTypes.EVIL_COW.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::evilCow);
		event.registerEntityRenderer(ACEntityTypes.EVIL_CHICKEN.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::evilChicken);
		event.registerEntityRenderer(ACEntityTypes.EVIL_SHEEP.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::evilSheep);
		event.registerEntityRenderer(ACEntityTypes.DEMON_PIG.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::demonPig);
		event.registerEntityRenderer(ACEntityTypes.DEMON_COW.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::demonCow);
		event.registerEntityRenderer(ACEntityTypes.DEMON_CHICKEN.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::demonChicken);
		event.registerEntityRenderer(ACEntityTypes.DEMON_SHEEP.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACAnimal::demonSheep);
	}

	private static void registerDreadRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ACEntityTypes.DREAD_SPAWN.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelDreadSpawn<>(ctx.bakeLayer(ACModelLayers.DREAD_SPAWN)), "dread_spawn", 1.0F));
		event.registerEntityRenderer(ACEntityTypes.GREATER_DREAD_SPAWN.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelDreadSpawn<>(ctx.bakeLayer(ACModelLayers.DREAD_SPAWN)), "greater_dread_spawn", 2.0F));
		event.registerEntityRenderer(ACEntityTypes.SPAWN_OF_CHAGAROTH.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelChagarothSpawn<>(ctx.bakeLayer(ACModelLayers.SPAWN_OF_CHAGAROTH)), "spawn_of_chagaroth", 1.0F));
		event.registerEntityRenderer(ACEntityTypes.LESSER_DREADBEAST.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelChagarothSpawn<>(ctx.bakeLayer(ACModelLayers.SPAWN_OF_CHAGAROTH)), "elite/lesser_dreadbeast", 3.0F));
		event.registerEntityRenderer(ACEntityTypes.FIST_OF_CHAGAROTH.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelChagarothFist<>(ctx.bakeLayer(ACModelLayers.FIST_OF_CHAGAROTH)), "chagarothfist", 1.0F));
		event.registerEntityRenderer(ACEntityTypes.CHAGAROTH.get(), ctx -> new RenderSimpleMob<>(ctx, new ModelChagaroth(ctx.bakeLayer(ACModelLayers.CHAGAROTH)), "boss/chagaroth", 1.0F).alwaysRender());
		event.registerEntityRenderer(ACEntityTypes.DREAD_SLUG.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
		event.registerEntityRenderer(ACEntityTypes.DREADED_CHARGE.get(), RenderDreadedCharge::new);
		event.registerEntityRenderer(ACEntityTypes.JZAHAR.get(), com.shinoow.abyssalcraft.client.render.entity.RenderJzahar::new);
		registerBatch1Renderers(event);
		event.registerEntityRenderer(ACEntityTypes.CORALIUM_SQUID.get(), ctx -> new net.minecraft.client.renderer.entity.SquidRenderer<com.shinoow.abyssalcraft.common.entity.EntityCoraliumSquid>(ctx,
				new net.minecraft.client.model.SquidModel<>(ctx.bakeLayer(net.minecraft.client.model.geom.ModelLayers.SQUID))) {
			@Override public net.minecraft.resources.ResourceLocation getTextureLocation(com.shinoow.abyssalcraft.common.entity.EntityCoraliumSquid squid) {
				return AbyssalCraft.loc("textures/model/coraliumsquid.png");
			}
		});
		event.registerEntityRenderer(ACEntityTypes.SHUB_OFFSPRING.get(), com.shinoow.abyssalcraft.client.render.entity.RenderShubOffspring::new);
		event.registerEntityRenderer(ACEntityTypes.PRIMED_ODB.get(), com.shinoow.abyssalcraft.client.render.entity.RenderODB::new);
		event.registerEntityRenderer(ACEntityTypes.PRIMED_ODB_CORE.get(), com.shinoow.abyssalcraft.client.render.entity.RenderODB::new);
		event.registerEntityRenderer(ACEntityTypes.PSDL_TRACKER.get(), ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 1, true));
		event.registerEntityRenderer(ACEntityTypes.ASORAH.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACDragon::new);
		event.registerEntityRenderer(ACEntityTypes.SPECTRAL_DRAGON.get(), com.shinoow.abyssalcraft.client.render.entity.RenderACDragon::new);
		event.registerEntityRenderer(ACEntityTypes.CORALIUM_ARROW.get(), com.shinoow.abyssalcraft.client.render.entity.RenderCoraliumArrow::new);
		event.registerEntityRenderer(ACEntityTypes.INK_PROJECTILE.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
		event.registerEntityRenderer(ACEntityTypes.SHADOW_CREATURE.get(), ctx -> new com.shinoow.abyssalcraft.client.render.entity.RenderShadow<>(ctx,
				new com.shinoow.abyssalcraft.client.model.entity.ModelShadowCreature<>(ctx.bakeLayer(ACModelLayers.SHADOW_CREATURE)), "shadowcreature", true, false));
		event.registerEntityRenderer(ACEntityTypes.SHADOW_MONSTER.get(), ctx -> new com.shinoow.abyssalcraft.client.render.entity.RenderShadow<>(ctx,
				new com.shinoow.abyssalcraft.client.model.entity.ModelShadowMonster<>(ctx.bakeLayer(ACModelLayers.SHADOW_MONSTER)), "shadowmonster", true, false));
		event.registerEntityRenderer(ACEntityTypes.SHADOW_BEAST.get(), ctx -> new com.shinoow.abyssalcraft.client.render.entity.RenderShadow<>(ctx,
				new com.shinoow.abyssalcraft.client.model.entity.ModelShadowBeast<>(ctx.bakeLayer(ACModelLayers.SHADOW_BEAST)), "elite/shadowbeast", true, false));
		event.registerEntityRenderer(ACEntityTypes.SACTHOTH.get(), ctx -> com.shinoow.abyssalcraft.client.render.entity.RenderShadow.sacthoth(ctx,
				new com.shinoow.abyssalcraft.client.model.entity.ModelSacthoth(ctx.bakeLayer(ACModelLayers.SACTHOTH))));
		event.registerEntityRenderer(ACEntityTypes.ANTI_PIG.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::pig);
		event.registerEntityRenderer(ACEntityTypes.ANTI_COW.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::cow);
		event.registerEntityRenderer(ACEntityTypes.ANTI_CHICKEN.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::chicken);
		event.registerEntityRenderer(ACEntityTypes.ANTI_BAT.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::bat);
		event.registerEntityRenderer(ACEntityTypes.ANTI_ZOMBIE.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::zombie);
		event.registerEntityRenderer(ACEntityTypes.ABYSSAL_ANTI_ZOMBIE.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::abyssalZombie);
		event.registerEntityRenderer(ACEntityTypes.ANTI_SKELETON.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::skeleton);
		event.registerEntityRenderer(ACEntityTypes.ANTI_SPIDER.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::spider);
		event.registerEntityRenderer(ACEntityTypes.ANTI_CREEPER.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers.Creeper::new);
		event.registerEntityRenderer(ACEntityTypes.ANTI_GHOUL.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::ghoul);
		event.registerEntityRenderer(ACEntityTypes.ANTI_PLAYER.get(), com.shinoow.abyssalcraft.client.render.entity.AntiRenderers::player);
		event.registerEntityRenderer(ACEntityTypes.MINION_OF_THE_GATEKEEPER.get(), ctx -> {
			RenderSimpleMob<com.shinoow.abyssalcraft.common.entity.EntityGatekeeperMinion, com.shinoow.abyssalcraft.client.model.entity.ModelGatekeeperMinion<com.shinoow.abyssalcraft.common.entity.EntityGatekeeperMinion>> renderer =
					new RenderSimpleMob<>(ctx, new com.shinoow.abyssalcraft.client.model.entity.ModelGatekeeperMinion<>(ctx.bakeLayer(ACModelLayers.MINION_OF_THE_GATEKEEPER)), "elite/gatekeeperminion", 1.0F);
			renderer.addLayer(new net.minecraft.client.renderer.entity.layers.CustomHeadLayer<>(renderer, ctx.getModelSet(), ctx.getItemInHandRenderer()));
			return renderer;
		});
		event.registerEntityRenderer(ACEntityTypes.IMPLOSION.get(), com.shinoow.abyssalcraft.client.render.entity.RenderJzaharSpells.Implosion::new);
		event.registerEntityRenderer(ACEntityTypes.BLACK_HOLE.get(), com.shinoow.abyssalcraft.client.render.entity.RenderJzaharSpells.BlackHole::new);
		event.registerEntityRenderer(ACEntityTypes.GATEKEEPER_ESSENCE.get(), net.minecraft.client.renderer.entity.ItemEntityRenderer::new);
	}

	@SubscribeEvent
	public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelDGhead.LAYER, ModelDGhead::createLayer);
		com.shinoow.abyssalcraft.client.model.ACModelLayers.register(event);
	}

	@SubscribeEvent
	public static void particles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ACParticles.PE_STREAM.get(), PEStreamParticleFX.Provider::new);
		event.registerSpriteSet(ACParticles.BLUE_FLAME.get(), net.minecraft.client.particle.FlameParticle.Provider::new);
	}
}
