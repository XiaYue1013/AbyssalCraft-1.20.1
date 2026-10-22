package com.shinoow.abyssalcraft.client.render.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.model.ACModelLayers;
import com.shinoow.abyssalcraft.client.model.entity.ModelShubOffspring;
import com.shinoow.abyssalcraft.common.entity.EntityShubOffspring;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class RenderShubOffspring extends RenderSimpleMob<EntityShubOffspring, ModelShubOffspring<EntityShubOffspring>> {
	public RenderShubOffspring(EntityRendererProvider.Context context) {
		super(context, new ModelShubOffspring<>(context.bakeLayer(ACModelLayers.SHUB_OFFSPRING)), "shub_offspring", 1);
		shadowRadius = 1;
		addLayer(new EyesLayer<EntityShubOffspring, ModelShubOffspring<EntityShubOffspring>>(this) {
			@Override public RenderType renderType() {
				return RenderType.eyes(AbyssalCraft.loc("textures/model/shub_offspring_eyes.png"));
			}
		});
	}
}
