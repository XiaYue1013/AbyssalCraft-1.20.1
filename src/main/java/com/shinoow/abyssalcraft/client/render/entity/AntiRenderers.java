/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (RenderAnti*: the vanilla renderers with the antimatter textures)
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelDG;
import com.shinoow.abyssalcraft.client.model.entity.ModelDGArmor;
import com.shinoow.abyssalcraft.client.render.entity.layers.LayerGhoulArmor;
import com.shinoow.abyssalcraft.client.render.entity.layers.LayerGhoulHeldItem;
import com.shinoow.abyssalcraft.common.entity.anti.AntiHumanoids;
import com.shinoow.abyssalcraft.common.entity.anti.AntiUndead;
import com.shinoow.abyssalcraft.common.entity.anti.EntityAntiCreeper;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

/** The antimatter mobs: vanilla models with the AbyssalCraft antimatter textures */
public class AntiRenderers {

	private static ResourceLocation tex(String name) {
		return AbyssalCraft.loc("textures/model/anti/" + name + ".png");
	}

	public static EntityRenderer<Pig> pig(EntityRendererProvider.Context ctx) {
		return new PigRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Pig entity) {
				return tex("pig");
			}
		};
	}

	public static EntityRenderer<Cow> cow(EntityRendererProvider.Context ctx) {
		return new CowRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Cow entity) {
				return tex("cow");
			}
		};
	}

	public static EntityRenderer<Chicken> chicken(EntityRendererProvider.Context ctx) {
		return new ChickenRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Chicken entity) {
				return tex("chicken");
			}
		};
	}

	public static EntityRenderer<Bat> bat(EntityRendererProvider.Context ctx) {
		return new BatRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Bat entity) {
				return tex("bat");
			}
		};
	}

	public static EntityRenderer<Zombie> zombie(EntityRendererProvider.Context ctx) {
		return new ZombieRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(Zombie entity) {
				return tex("zombie");
			}
		};
	}

	/** The 1.12.2 ModelZombie with a 64x32 texture */
	public static EntityRenderer<AntiUndead.AntiAbyssalZombie> abyssalZombie(EntityRendererProvider.Context ctx) {
		HumanoidMobRenderer<AntiUndead.AntiAbyssalZombie, ZombieModel<AntiUndead.AntiAbyssalZombie>> renderer = new HumanoidMobRenderer<>(ctx,
				new ZombieModel<>(ctx.bakeLayer(ACModelLayers.GOLEM)), 0.5F) {
			@Override
			public ResourceLocation getTextureLocation(AntiUndead.AntiAbyssalZombie entity) {
				return tex("abyssal_zombie");
			}
		};
		renderer.addLayer(new HumanoidArmorLayer<>(renderer, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), ctx.getModelManager()));
		return renderer;
	}

	public static EntityRenderer<AbstractSkeleton> skeleton(EntityRendererProvider.Context ctx) {
		return new SkeletonRenderer(ctx) {
			@Override
			public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
				return tex("skeleton");
			}
		};
	}

	/** The spider with antimatter eyes */
	public static EntityRenderer<Spider> spider(EntityRendererProvider.Context ctx) {
		return new SpiderRenderer<>(ctx) {
			{
				layers.removeIf(layer -> layer instanceof SpiderEyesLayer);
				addLayer(new EyesLayer<Spider, SpiderModel<Spider>>(this) {
					private final RenderType eyes = RenderType.eyes(tex("spider_eyes"));

					@Override
					public RenderType renderType() {
						return eyes;
					}
				});
			}

			@Override
			public ResourceLocation getTextureLocation(Spider entity) {
				return tex("spider");
			}
		};
	}

	/** CreeperRenderer for the Anti-Creeper: swelling and flashing before it explodes, the charge swirl when powered */
	public static class Creeper extends MobRenderer<EntityAntiCreeper, CreeperModel<EntityAntiCreeper>> {

		private static final ResourceLocation POWER = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

		public Creeper(EntityRendererProvider.Context ctx) {
			super(ctx, new CreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER)), 0.5F);
			CreeperModel<EntityAntiCreeper> armor = new CreeperModel<>(ctx.getModelSet().bakeLayer(ModelLayers.CREEPER_ARMOR));
			addLayer(new EnergySwirlLayer<EntityAntiCreeper, CreeperModel<EntityAntiCreeper>>(this) {
				@Override
				protected float xOffset(float ticks) {
					return ticks * 0.01F;
				}

				@Override
				protected ResourceLocation getTextureLocation() {
					return POWER;
				}

				@Override
				protected EntityModel<EntityAntiCreeper> model() {
					return armor;
				}
			});
		}

		@Override
		protected void scale(EntityAntiCreeper entity, PoseStack poseStack, float partialTick) {
			float f = entity.getSwelling(partialTick);
			float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
			f = Mth.clamp(f, 0.0F, 1.0F);
			f *= f;
			f *= f;
			float f2 = (1.0F + f * 0.4F) * f1;
			float f3 = (1.0F + f * 0.1F) / f1;
			poseStack.scale(f2, f3, f2);
		}

		@Override
		protected float getWhiteOverlayProgress(EntityAntiCreeper entity, float partialTick) {
			float f = entity.getSwelling(partialTick);
			return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
		}

		@Override
		public ResourceLocation getTextureLocation(EntityAntiCreeper entity) {
			return tex("creeper");
		}
	}

	public static EntityRenderer<AntiHumanoids.AntiGhoul> ghoul(EntityRendererProvider.Context ctx) {
		MobRenderer<AntiHumanoids.AntiGhoul, ModelDG<AntiHumanoids.AntiGhoul>> renderer = new MobRenderer<>(ctx, new ModelDG<>(ctx.bakeLayer(ACModelLayers.DEPTHS_GHOUL)), 0.8F) {
			@Override
			public ResourceLocation getTextureLocation(AntiHumanoids.AntiGhoul entity) {
				return tex("depths_ghoul");
			}
		};
		renderer.addLayer(new LayerGhoulHeldItem<>(renderer, ctx.getItemInHandRenderer()));
		renderer.addLayer(new LayerGhoulArmor<>(renderer, new ModelDGArmor<>(ctx.bakeLayer(ACModelLayers.GHOUL_INNER_ARMOR)),
				new ModelDGArmor<>(ctx.bakeLayer(ACModelLayers.GHOUL_OUTER_ARMOR))));
		renderer.addLayer(new CustomHeadLayer<>(renderer, ctx.getModelSet(), ctx.getItemInHandRenderer()));
		return renderer;
	}

	/** The 1.12.2 ModelBiped (64x32 Steve) with armor */
	public static EntityRenderer<AntiHumanoids.AntiPlayer> player(EntityRendererProvider.Context ctx) {
		HumanoidMobRenderer<AntiHumanoids.AntiPlayer, HumanoidModel<AntiHumanoids.AntiPlayer>> renderer = new HumanoidMobRenderer<>(ctx,
				new HumanoidModel<>(ctx.bakeLayer(ACModelLayers.GOLEM)), 0.5F) {
			@Override
			public ResourceLocation getTextureLocation(AntiHumanoids.AntiPlayer entity) {
				return tex("steve");
			}
		};
		renderer.addLayer(new HumanoidArmorLayer<>(renderer, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
		return renderer;
	}
}
