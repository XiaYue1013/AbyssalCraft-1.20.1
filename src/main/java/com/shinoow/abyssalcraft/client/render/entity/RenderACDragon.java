/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (RenderDragonBoss, RenderDragonMinion); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelACDragon;
import com.shinoow.abyssalcraft.common.entity.EntityACDragon;
import com.shinoow.abyssalcraft.lib.ACClientVars;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderACDragon extends MobRenderer<EntityACDragon, ModelACDragon<EntityACDragon>> {
	private static final ResourceLocation ASORAH = AbyssalCraft.loc("textures/model/boss/dragonboss.png");
	private static final ResourceLocation MINION = AbyssalCraft.loc("textures/model/elite/dragonminion.png");
	private static final ResourceLocation EXPLODING = AbyssalCraft.loc("textures/model/boss/dragonboss_exploding.png");
	public RenderACDragon(EntityRendererProvider.Context context) {
		super(context, new ModelACDragon<>(context.bakeLayer(ACModelLayers.AC_DRAGON)), 0.9F);
		addLayer(new EyesLayer<EntityACDragon, ModelACDragon<EntityACDragon>>(this) {
			@Override public RenderType renderType() { return RenderType.eyes(AbyssalCraft.loc("textures/model/boss/dragonboss_eyes.png")); }
			@Override public void render(PoseStack pose, MultiBufferSource buffers, int light, EntityACDragon dragon, float limbSwing, float limbSwingAmount,
					float partialTick, float age, float yaw, float pitch) {
				if(dragon.isAsorah()) super.render(pose, buffers, light, dragon, limbSwing, limbSwingAmount, partialTick, age, yaw, pitch);
			}
		});
		addLayer(new RenderLayer<EntityACDragon, ModelACDragon<EntityACDragon>>(this) {
			@Override public void render(PoseStack pose, MultiBufferSource buffers, int light, EntityACDragon dragon, float limbSwing, float limbSwingAmount,
					float partialTick, float age, float yaw, float pitch) {
				if(!dragon.isAsorah() || dragon.deathTicks <= 0) return;
				getParentModel().renderToBuffer(pose, buffers.getBuffer(RenderType.dragonExplosionAlpha(EXPLODING)), light, OverlayTexture.NO_OVERLAY, 1, 1, 1, dragon.deathTicks / 200F);
				float time = (dragon.deathTicks + partialTick) / 200F, fade = time > 0.8F ? (time - 0.8F) / 0.2F : 0;
				int[] color = ACClientVars.ASORAH_DEATH;
				DeathBeams.render(pose, buffers, (int)Math.ceil((time + time * time) / 2 * 60), fade, 360, time * 90, 20, 5 + fade * 10, 1 + fade * 2, false, color[0], color[1], color[2]);
			}
		});
	}
	@Override public ResourceLocation getTextureLocation(EntityACDragon entity) { return entity.isAsorah() ? ASORAH : MINION; }
	@Override protected RenderType getRenderType(EntityACDragon entity, boolean bodyVisible, boolean translucent, boolean glowing) {
		return !entity.isAsorah() && bodyVisible ? RenderType.entityTranslucent(MINION) : super.getRenderType(entity, bodyVisible, translucent, glowing);
	}
	@Override protected void scale(EntityACDragon entity, PoseStack pose, float partialTick) { float scale = entity.isAsorah() ? 1.5F : 0.5F; pose.scale(scale, scale, scale); }
	@Override protected void setupRotations(EntityACDragon dragon, PoseStack pose, float age, float yaw, float partialTick) {
		pose.mulPose(Axis.YP.rotationDegrees(-(float)dragon.getMovementOffsets(7, partialTick)[0]));
		pose.mulPose(Axis.XP.rotationDegrees((float)(dragon.getMovementOffsets(5, partialTick)[1] - dragon.getMovementOffsets(10, partialTick)[1]) * 10));
		pose.translate(0, 0, 1);
		if(dragon.deathTime > 0) pose.mulPose(Axis.ZP.rotationDegrees(Math.min(1, Mth.sqrt((dragon.deathTime + partialTick - 1) / 20F * 1.6F)) * getFlipDegrees(dragon)));
	}
	@Override public boolean shouldRender(EntityACDragon entity, Frustum frustum, double x, double y, double z) { return entity.isAsorah() || super.shouldRender(entity, frustum, x, y, z); }
	@Override public void render(EntityACDragon dragon, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
		super.render(dragon, yaw, partialTick, pose, buffers, light);
		EntityACDragon partner = dragon.getHealingPartner();
		if(dragon.isAsorah() && partner != null && partner.isAlive())
			EnderDragonRenderer.renderCrystalBeams((float)(partner.getX() - dragon.getX()), (float)(partner.getY() - dragon.getY()),
					(float)(partner.getZ() - dragon.getZ()), partialTick, dragon.tickCount, pose, buffers, light);
	}
}
