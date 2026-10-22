/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (RenderEvilPig, RenderEvilCow, RenderEvilChicken, RenderEvilSheep, RenderDemonPig,
 *                RenderDemonCow, RenderDemonChicken, RenderDemonSheep, LayerEvilSheepWool, LayerDemonSheepWool)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.entity.ModelACSheep;
import com.shinoow.abyssalcraft.common.entity.demon.EntityDemonAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilAnimal;
import com.shinoow.abyssalcraft.common.entity.demon.EntityEvilSheep;
import com.shinoow.abyssalcraft.common.entity.demon.ISheepAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

/** The Evil and Demon Animals: vanilla animal models, vanilla textures for the evil ones, AbyssalCraft ones for the demons */
public class RenderACAnimal<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {

	private final ResourceLocation texture;

	public RenderACAnimal(EntityRendererProvider.Context context, M model, ResourceLocation texture, float shadow) {
		super(context, model, shadow);
		this.texture = texture;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return texture;
	}

	private static ResourceLocation vanilla(String path) {
		return new ResourceLocation("textures/entity/" + path + ".png");
	}

	private static ResourceLocation ac(String path) {
		return AbyssalCraft.loc("textures/model/" + path + ".png");
	}

	public static RenderACAnimal<EntityEvilAnimal, PigModel<EntityEvilAnimal>> evilPig(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new PigModel<>(ctx.bakeLayer(ModelLayers.PIG)), vanilla("pig/pig"), 0.7F);
	}

	public static RenderACAnimal<EntityEvilAnimal, CowModel<EntityEvilAnimal>> evilCow(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new CowModel<>(ctx.bakeLayer(ModelLayers.COW)), vanilla("cow/cow"), 0.7F);
	}

	public static RenderACAnimal<EntityEvilAnimal, ChickenModel<EntityEvilAnimal>> evilChicken(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new ChickenModel<>(ctx.bakeLayer(ModelLayers.CHICKEN)), vanilla("chicken"), 0.3F) {
			@Override
			protected float getBob(EntityEvilAnimal entity, float partialTicks) {
				return bob(entity.flap, entity.oFlap, entity.flapSpeed, entity.oFlapSpeed, partialTicks);
			}
		};
	}

	public static RenderACAnimal<EntityDemonAnimal, PigModel<EntityDemonAnimal>> demonPig(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new PigModel<>(ctx.bakeLayer(ModelLayers.PIG)), ac("demon_pig"), 0.7F);
	}

	public static RenderACAnimal<EntityDemonAnimal, CowModel<EntityDemonAnimal>> demonCow(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new CowModel<>(ctx.bakeLayer(ModelLayers.COW)), ac("demon_cow"), 0.7F);
	}

	public static RenderACAnimal<EntityDemonAnimal, ChickenModel<EntityDemonAnimal>> demonChicken(EntityRendererProvider.Context ctx) {
		return new RenderACAnimal<>(ctx, new ChickenModel<>(ctx.bakeLayer(ModelLayers.CHICKEN)), ac("demon_chicken"), 0.3F) {
			@Override
			protected float getBob(EntityDemonAnimal entity, float partialTicks) {
				return bob(entity.flap, entity.oFlap, entity.flapSpeed, entity.oFlapSpeed, partialTicks);
			}
		};
	}

	/** ChickenRenderer#getBob */
	private static float bob(float flap, float oFlap, float flapSpeed, float oFlapSpeed, float partialTicks) {
		float f = Mth.lerp(partialTicks, oFlap, flap);
		float f1 = Mth.lerp(partialTicks, oFlapSpeed, flapSpeed);
		return (Mth.sin(f) + 1.0F) * f1;
	}

	public static RenderACAnimal<EntityDemonAnimal, ModelACSheep<EntityDemonAnimal>> demonSheep(EntityRendererProvider.Context ctx) {
		RenderACAnimal<EntityDemonAnimal, ModelACSheep<EntityDemonAnimal>> renderer = new RenderACAnimal<>(ctx,
				new ModelACSheep<>(ctx.bakeLayer(ModelLayers.SHEEP)), ac("demon_sheep"), 0.7F);
		renderer.addLayer(new WoolLayer<>(renderer, new ModelACSheep<>(ctx.bakeLayer(ModelLayers.SHEEP_FUR)), null, e -> ac("demon_sheep_fur")));
		return renderer;
	}

	public static RenderACAnimal<EntityEvilSheep, ModelACSheep<EntityEvilSheep>> evilSheep(EntityRendererProvider.Context ctx) {
		RenderACAnimal<EntityEvilSheep, ModelACSheep<EntityEvilSheep>> renderer = new RenderACAnimal<>(ctx,
				new ModelACSheep<>(ctx.bakeLayer(ModelLayers.SHEEP)), vanilla("sheep/sheep"), 0.7F);
		renderer.addLayer(new WoolLayer<>(renderer, new ModelACSheep<>(ctx.bakeLayer(ModelLayers.SHEEP_FUR)),
				new ModelACSheep<>(ctx.bakeLayer(ModelACSheep.SKIN_WOOL)), e -> e.hasKilledPlayer() ? null : vanilla("sheep/sheep_fur")));
		return renderer;
	}

	/** The wool, rainbow colored for "jeb_"; the Evil Sheep's is the skin of the player it killed */
	private static class WoolLayer<T extends Mob & ISheepAnimation> extends RenderLayer<T, ModelACSheep<T>> {

		private static final Map<UUID, GameProfile> PROFILES = new HashMap<>();
		private final ModelACSheep<T> wool;
		private final ModelACSheep<T> skinWool;
		private final java.util.function.Function<T, ResourceLocation> texture;

		WoolLayer(RenderLayerParent<T, ModelACSheep<T>> parent, ModelACSheep<T> wool, ModelACSheep<T> skinWool, java.util.function.Function<T, ResourceLocation> texture) {
			super(parent);
			this.wool = wool;
			this.skinWool = skinWool;
			this.texture = texture;
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks,
				float ageInTicks, float netHeadYaw, float headPitch) {
			if(entity.isInvisible()) return;
			ResourceLocation tex = texture.apply(entity);
			ModelACSheep<T> model = wool;
			if(tex == null && skinWool != null && entity instanceof EntityEvilSheep sheep) {
				tex = skin(sheep.getKilledPlayerUUID(), sheep.getKilledPlayerName());
				model = skinWool;
			}
			float r = 1.0F, g = 1.0F, b = 1.0F;
			if(entity.hasCustomName() && "jeb_".equals(entity.getName().getString())) {
				int i = entity.tickCount / 25 + entity.getId();
				int j = DyeColor.values().length;
				float f = (entity.tickCount % 25 + partialTicks) / 25.0F;
				float[] c1 = Sheep.getColorArray(DyeColor.byId(i % j));
				float[] c2 = Sheep.getColorArray(DyeColor.byId((i + 1) % j));
				r = c1[0] * (1.0F - f) + c2[0] * f;
				g = c1[1] * (1.0F - f) + c2[1] * f;
				b = c1[2] * (1.0F - f) + c2[2] * f;
			}
			getParentModel().copyPropertiesTo(model);
			model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
			model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(tex));
			model.renderToBuffer(poseStack, consumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), r, g, b, 1.0F);
		}

		/** The skin of a player (looked up once, then cached like a player head's) */
		private static ResourceLocation skin(UUID uuid, String name) {
			GameProfile profile = PROFILES.get(uuid);
			if(profile == null) {
				PROFILES.put(uuid, new GameProfile(uuid, name));
				SkullBlockEntity.updateGameprofile(new GameProfile(uuid, name), filled -> PROFILES.put(uuid, filled));
				return DefaultPlayerSkin.getDefaultSkin(uuid);
			}
			Minecraft minecraft = Minecraft.getInstance();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
			return map.containsKey(MinecraftProfileTexture.Type.SKIN)
					? minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)
					: DefaultPlayerSkin.getDefaultSkin(uuid);
		}
	}
}
