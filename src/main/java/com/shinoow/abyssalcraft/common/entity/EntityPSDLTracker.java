/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** The smoky eye of ender used by the Powerstone Tracker (1.12.2 also located Abyssal strongholds). */
public class EntityPSDLTracker extends Entity implements ItemSupplier {
	private double targetX, targetY, targetZ;
	private int life;
	private boolean survive;
	public EntityPSDLTracker(EntityType<? extends EntityPSDLTracker> type, Level level) { super(type, level); }
	@Override protected void defineSynchedData() {}
	@Override public ItemStack getItem() { return new ItemStack(ACItems.powerstone_tracker.get()); }
	@Override public boolean isAttackable() { return false; }
	@Override public float getLightLevelDependentMagicValue() { return 1; }

	public void moveTowards(BlockPos target) {
		double dx = target.getX() - getX(), dz = target.getZ() - getZ();
		double distance = Math.sqrt(dx * dx + dz * dz);
		targetX = distance > 12 ? getX() + dx / distance * 12 : target.getX();
		targetY = distance > 12 ? getY() + 8 : target.getY();
		targetZ = distance > 12 ? getZ() + dz / distance * 12 : target.getZ();
		life = 0; survive = random.nextInt(5) > 0;
	}

	@Override public void tick() {
		super.tick();
		Vec3 velocity = getDeltaMovement();
		double x = getX() + velocity.x, y = getY() + velocity.y, z = getZ() + velocity.z;
		double speed = velocity.horizontalDistance();
		setXRot(lerpRotation(xRotO, (float)(Mth.atan2(velocity.y, speed) * 180 / Math.PI)));
		setYRot(lerpRotation(yRotO, (float)(Mth.atan2(velocity.x, velocity.z) * 180 / Math.PI)));
		if(!level().isClientSide) {
			double dx = targetX - x, dz = targetZ - z, distance = Math.sqrt(dx * dx + dz * dz);
			double angle = Mth.atan2(dz, dx), horizontal = Mth.lerp(0.0025D, speed, distance), vertical = velocity.y;
			if(distance < 1) { horizontal *= 0.8D; vertical *= 0.8D; }
			vertical += ((getY() < targetY ? 1 : -1) - vertical) * 0.015D;
			setDeltaMovement(Math.cos(angle) * horizontal, vertical, Math.sin(angle) * horizontal);
		}
		if(level().isClientSide && ACConfig.particleEntity) {
			if(isInWater()) for(int i = 0; i < 4; i++)
				level().addParticle(ParticleTypes.BUBBLE, x - velocity.x * 0.25D, y - velocity.y * 0.25D, z - velocity.z * 0.25D, velocity.x, velocity.y, velocity.z);
			else level().addParticle(ParticleTypes.SMOKE, x - velocity.x * 0.25D + random.nextDouble() * 0.6D - 0.3D,
					y - velocity.y * 0.25D - 0.5D, z - velocity.z * 0.25D + random.nextDouble() * 0.6D - 0.3D, velocity.x, velocity.y, velocity.z);
		}
		setPos(x, y, z);
		if(!level().isClientSide && ++life > 80) {
			discard();
			if(survive) level().addFreshEntity(new ItemEntity(level(), x, y, z, getItem()));
			else level().levelEvent(2003, blockPosition(), 0);
		}
	}

	private static float lerpRotation(float previous, float current) {
		return previous + Mth.wrapDegrees(current - previous) * 0.2F;
	}

	@Override protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putDouble("TargetX", targetX); tag.putDouble("TargetY", targetY); tag.putDouble("TargetZ", targetZ);
		tag.putInt("Life", life); tag.putBoolean("Survive", survive);
	}
	@Override protected void readAdditionalSaveData(CompoundTag tag) {
		targetX = tag.getDouble("TargetX"); targetY = tag.getDouble("TargetY"); targetZ = tag.getDouble("TargetZ");
		life = tag.getInt("Life"); survive = tag.getBoolean("Survive");
	}
}
