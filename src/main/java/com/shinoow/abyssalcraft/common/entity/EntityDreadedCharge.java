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
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Dreaded Charge: Cha'garoth's fireball. Where it lands the land turns into Dreadlands, everything hit takes Dread
 * damage, and a Dread Plague cloud is left behind (moved onto a mob within 8 blocks, as in 1.12.2).
 */
public class EntityDreadedCharge extends AbstractHurtingProjectile {

	public EntityDreadedCharge(EntityType<? extends EntityDreadedCharge> type, Level level) {
		super(type, level);
	}

	public EntityDreadedCharge(Level level, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(ACEntityTypes.DREADED_CHARGE.get(), shooter, accelX, accelY, accelZ, level);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.FLAME;
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if(tickCount <= 5 || level().isClientSide) return;

		BlockPos center = blockPosition();
		List<BlockPos> columns = new ArrayList<>();
		for(int x = center.getX() - 4; x < center.getX() + 4; x++)
			for(int z = center.getZ() - 4; z < center.getZ() + 4; z++) {
				BlockPos pos = new BlockPos(x, center.getY(), z);
				Holder<Biome> biome = level().getBiome(pos);
				if(!biome.is(ACBiomes.IS_DREADLANDS) && !biome.is(ACBiomes.purged))
					columns.add(pos);
			}
		BiomeUtil.updateBiomes(level(), columns, ACBiomes.dreadlands);

		if(result instanceof EntityHitResult hit) {
			hit.getEntity().hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.dread), 4);
			hit.getEntity().invulnerableTime = 0;
		}
		AreaEffectCloud cloud = new AreaEffectCloud(level(), getX(), getY(), getZ());
		cloud.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 400));
		cloud.setRadius(2.0F);
		cloud.setDuration(200 + random.nextInt(200));
		cloud.setRadiusPerTick((3F - cloud.getRadius()) / cloud.getDuration());
		for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8.0D)))
			if(getOwner() != null && distanceToSqr(entity) < 64.0D)
				cloud.setPos(entity.getX(), entity.getY(), entity.getZ());

		if(level() instanceof ServerLevel server)
			for(int k = 0; k < 200; ++k) {
				float f2 = random.nextFloat() * 4.0F;
				float f3 = random.nextFloat() * ((float)Math.PI * 2F);
				double d3 = Mth.cos(f3) * f2;
				double d4 = 0.01D + random.nextDouble() * 0.5D;
				double d5 = Mth.sin(f3) * f2;
				server.sendParticles(ParticleTypes.FLAME, center.getX() + d3 * 0.1D, center.getY() + 0.3D, center.getZ() + d5 * 0.1D, 0, d3 * f2, d4, d5 * f2, 1.0D);
			}
		level().playSound(null, center, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.MASTER, 1.0F, random.nextFloat() * 0.1F + 0.9F);
		level().addFreshEntity(cloud);
		level().explode(this, getX(), getY() + 1.0D, getZ(), 3.0F, false, Level.ExplosionInteraction.NONE);
		discard();
	}
}
