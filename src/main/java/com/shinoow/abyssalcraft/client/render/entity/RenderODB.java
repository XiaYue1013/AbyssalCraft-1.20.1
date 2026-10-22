package com.shinoow.abyssalcraft.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.entity.EntityODBPrimed;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderODB extends EntityRenderer<EntityODBPrimed> {
	private final BlockRenderDispatcher blocks;
	public RenderODB(EntityRendererProvider.Context context) { super(context); blocks = context.getBlockRenderDispatcher(); shadowRadius = 0.5F; }

	@Override public void render(EntityODBPrimed bomb, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
		pose.pushPose();
		pose.translate(-0.5D, 0, -0.5D);
		float swell = 1 - (bomb.getFuse() - partialTick + 1) / 10;
		if(swell > 0) {
			swell = Mth.clamp(swell, 0, 1); swell *= swell; swell *= swell;
			float scale = 1 + swell * 0.3F;
			pose.translate(0.5D, 0, 0.5D); pose.scale(scale, scale, scale); pose.translate(-0.5D, 0, -0.5D);
		}
		TntMinecartRenderer.renderWhiteSolidBlock(blocks, (bomb.isCore() ? ACBlocks.odb_core : ACBlocks.oblivion_deathbomb).get().defaultBlockState(),
				pose, buffers, light, bomb.getFuse() / 5 % 2 == 0);
		pose.popPose();
		super.render(bomb, yaw, partialTick, pose, buffers, light);
	}
	@Override public ResourceLocation getTextureLocation(EntityODBPrimed bomb) { return TextureAtlas.LOCATION_BLOCKS; }
}
