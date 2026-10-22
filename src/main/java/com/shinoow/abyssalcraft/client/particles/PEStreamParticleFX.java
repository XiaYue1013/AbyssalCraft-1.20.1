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
 *     1.20.1 port - sprite based particle, the color is picked by the provider
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class PEStreamParticleFX extends TextureSheetParticle {

	private static final int[][] COLORS = {{65, 63, 170}, {41, 89, 48}, {39, 80, 135}};

	private final SpriteSet sprites;
	private final float colorR, colorG, colorB;
	private final float scale;

	public PEStreamParticleFX(ClientLevel level, double x, double y, double z, SpriteSet sprites, int r, int g, int b) {
		super(level, x, y, z, 0, 0, 0);
		this.sprites = sprites;
		colorR = r / 255.0F;
		colorG = g / 255.0F;
		colorB = b / 255.0F;
		setColor(colorR, colorG, colorB);
		lifetime = 20;
		quadSize *= 0.75F;
		scale = quadSize;
		xd *= 0.1D;
		yd *= 0.1D;
		zd *= 0.1D;
		setSpriteFromAge(sprites);
	}

	@Override
	public float getQuadSize(float partialTicks) {
		return scale * Mth.clamp((age + partialTicks) / lifetime * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();
		xd *= 0.65D;
		yd *= 0.65D;
		zd *= 0.65D;
		if(random.nextInt(4) == 0)
			age--;
		if(!removed)
			setSpriteFromAge(sprites);
		float lifeCoeff = ((float) lifetime - (float) age) / lifetime;
		rCol = Math.min(1.0F, colorR * (1.5F - lifeCoeff));
		gCol = Math.min(1.0F, colorG * (1.5F - lifeCoeff));
		bCol = Math.min(1.0F, colorB * (1.5F - lifeCoeff));
		alpha = Mth.clamp(lifeCoeff, 0.0F, 1.0F);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
			int[] c = COLORS[level.random.nextInt(COLORS.length)];
			return new PEStreamParticleFX(level, x, y, z, sprites, c[0], c[1], c[2]);
		}
	}
}
