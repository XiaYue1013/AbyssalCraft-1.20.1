/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - model layers of the entity models (1.12.2 built its models in the renderers)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.model;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.entity.ModelDG;
import com.shinoow.abyssalcraft.client.model.entity.ModelDGArmor;
import com.shinoow.abyssalcraft.client.model.entity.ModelLesserShoggoth;
import com.shinoow.abyssalcraft.client.model.entity.ModelRemnant;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ACModelLayers {

	public static final ModelLayerLocation DEPTHS_GHOUL = layer("depths_ghoul", "main");
	public static final ModelLayerLocation GHOUL_INNER_ARMOR = layer("depths_ghoul", "inner_armor");
	public static final ModelLayerLocation GHOUL_OUTER_ARMOR = layer("depths_ghoul", "outer_armor");
	public static final ModelLayerLocation LESSER_SHOGGOTH = layer("lesser_shoggoth", "main");
	public static final ModelLayerLocation REMNANT = layer("remnant", "main");
	public static final ModelLayerLocation DREAD_SPAWN = layer("dread_spawn", "main");
	public static final ModelLayerLocation SPAWN_OF_CHAGAROTH = layer("spawn_of_chagaroth", "main");
	public static final ModelLayerLocation FIST_OF_CHAGAROTH = layer("fist_of_chagaroth", "main");
	public static final ModelLayerLocation CHAGAROTH = layer("chagaroth", "main");
	public static final ModelLayerLocation JZAHAR = layer("jzahar", "main");
	public static final ModelLayerLocation DREADLING = layer("dreadling", "main");
	public static final ModelLayerLocation SHUB_OFFSPRING = layer("shub_offspring", "main");
	public static final ModelLayerLocation AC_DRAGON = layer("ac_dragon", "main");
	public static final ModelLayerLocation SHADOW_CREATURE = layer("shadow_creature", "main");
	public static final ModelLayerLocation SHADOW_MONSTER = layer("shadow_monster", "main");
	public static final ModelLayerLocation SHADOW_BEAST = layer("shadow_beast", "main");
	public static final ModelLayerLocation SACTHOTH = layer("sacthoth", "main");
	/** 1.12.2 ModelBiped: a 64x32 humanoid */
	public static final ModelLayerLocation GOLEM = layer("abyssalnite_golem", "main");
	public static final ModelLayerLocation SKELETON_GOLIATH = layer("skeleton_goliath", "main");
	public static final ModelLayerLocation JZAHAR_SPAWNER = layer("jzahar_spawner", "main");
	public static final ModelLayerLocation MINION_OF_THE_GATEKEEPER = layer("minion_of_the_gatekeeper", "main");

	private static ModelLayerLocation layer(String model, String layer) {
		return new ModelLayerLocation(AbyssalCraft.loc(model), layer);
	}

	public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(DEPTHS_GHOUL, () -> ModelDG.createBodyLayer(CubeDeformation.NONE));
		event.registerLayerDefinition(GHOUL_INNER_ARMOR, () -> ModelDGArmor.createArmorLayer(new CubeDeformation(0.5F)));
		event.registerLayerDefinition(GHOUL_OUTER_ARMOR, () -> ModelDGArmor.createArmorLayer(new CubeDeformation(1.0F)));
		event.registerLayerDefinition(LESSER_SHOGGOTH, ModelLesserShoggoth::createBodyLayer);
		event.registerLayerDefinition(REMNANT, ModelRemnant::createBodyLayer);
		event.registerLayerDefinition(DREAD_SPAWN, com.shinoow.abyssalcraft.client.model.entity.ModelDreadSpawn::createBodyLayer);
		event.registerLayerDefinition(SPAWN_OF_CHAGAROTH, com.shinoow.abyssalcraft.client.model.entity.ModelChagarothSpawn::createBodyLayer);
		event.registerLayerDefinition(FIST_OF_CHAGAROTH, com.shinoow.abyssalcraft.client.model.entity.ModelChagarothFist::createBodyLayer);
		event.registerLayerDefinition(CHAGAROTH, com.shinoow.abyssalcraft.client.model.entity.ModelChagaroth::createBodyLayer);
		event.registerLayerDefinition(SHUB_OFFSPRING, com.shinoow.abyssalcraft.client.model.entity.ModelShubOffspring::createBodyLayer);
		event.registerLayerDefinition(AC_DRAGON, com.shinoow.abyssalcraft.client.model.entity.ModelACDragon::createBodyLayer);
		event.registerLayerDefinition(SHADOW_CREATURE, com.shinoow.abyssalcraft.client.model.entity.ModelShadowCreature::createBodyLayer);
		event.registerLayerDefinition(SHADOW_MONSTER, com.shinoow.abyssalcraft.client.model.entity.ModelShadowMonster::createBodyLayer);
		event.registerLayerDefinition(SHADOW_BEAST, com.shinoow.abyssalcraft.client.model.entity.ModelShadowBeast::createBodyLayer);
		event.registerLayerDefinition(SACTHOTH, com.shinoow.abyssalcraft.client.model.entity.ModelSacthoth::createBodyLayer);
		event.registerLayerDefinition(DREADLING, com.shinoow.abyssalcraft.client.model.entity.ModelDreadling::createBodyLayer);
		event.registerLayerDefinition(GOLEM, com.shinoow.abyssalcraft.client.model.entity.AbyssalZombieModel::createLegacyLayer);
		event.registerLayerDefinition(SKELETON_GOLIATH, () -> com.shinoow.abyssalcraft.client.model.entity.ModelSkeletonGoliath.createBodyLayer(CubeDeformation.NONE));
		event.registerLayerDefinition(com.shinoow.abyssalcraft.client.model.entity.ModelACSheep.SKIN_WOOL, com.shinoow.abyssalcraft.client.model.entity.ModelACSheep::createSkinWoolLayer);
		event.registerLayerDefinition(JZAHAR, () -> com.shinoow.abyssalcraft.client.model.entity.ModelJzahar.createBodyLayer(net.minecraft.client.model.geom.builders.CubeDeformation.NONE));
		event.registerLayerDefinition(JZAHAR_SPAWNER, com.shinoow.abyssalcraft.client.model.block.ModelJzaharSpawner::createBodyLayer);
		event.registerLayerDefinition(MINION_OF_THE_GATEKEEPER, com.shinoow.abyssalcraft.client.model.entity.ModelGatekeeperMinion::createBodyLayer);
	}
}
