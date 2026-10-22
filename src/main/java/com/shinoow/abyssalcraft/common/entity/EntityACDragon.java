/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (EntityDragonBoss, EntityDragonMinion); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.ICoraliumEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

/** Asorah and the Spectral Dragon share their flight, seven hitboxes and segmented animation history. */
public class EntityACDragon extends Monster implements ICoraliumEntity {
	private static final EntityDataAccessor<Integer> HEALER = SynchedEntityData.defineId(EntityACDragon.class, EntityDataSerializers.INT);
	public final DragonPart head, body, tail1, tail2, tail3, wing1, wing2;
	private final DragonPart[] parts;
	private final boolean boss;
	private final ServerBossEvent bossInfo = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
	public final double[][] ringBuffer = new double[64][3];
	public int ringBufferIndex = -1, deathTicks;
	public float prevAnimTime, animTime;
	private float yawVelocity;
	private double targetX, targetY = 100, targetZ;
	private LivingEntity flightTarget;
	private EntityACDragon healingPartner;

	public EntityACDragon(EntityType<? extends EntityACDragon> type, Level level) {
		super(type, level);
		boss = type == ACEntityTypes.ASORAH.get();
		float scale = boss ? 3 : 1;
		parts = new DragonPart[] {head = new DragonPart(this, true, 1.5F * scale, 1.5F * scale),
				body = new DragonPart(this, false, 2.5F * scale, 1.5F * scale),
				tail1 = new DragonPart(this, false, scale, scale), tail2 = new DragonPart(this, false, scale, scale), tail3 = new DragonPart(this, false, scale, scale),
				wing1 = new DragonPart(this, false, 2 * scale, scale), wing2 = new DragonPart(this, false, 2 * scale, scale)};
		setId(ENTITY_COUNTER.getAndAdd(parts.length + 1) + 1);
		setNoGravity(true);
		noPhysics = !boss;
		bossInfo.setDarkenScreen(true);
		if(ACConfig.hardcoreMode) getAttribute(Attributes.MAX_HEALTH).setBaseValue(boss ? 800 : 60);
		setHealth(getMaxHealth());
	}

	public static AttributeSupplier.Builder createAttributes(boolean boss) {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, boss ? 400 : 30).add(Attributes.KNOCKBACK_RESISTANCE, boss ? 1 : 0);
	}
	public boolean isAsorah() { return boss; }
	@Override protected void registerGoals() {}
	@Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(HEALER, -1); }
	@Override public boolean isMultipartEntity() { return true; }
	@Override public PartEntity<?>[] getParts() { return parts; }
	@Override public void setId(int id) { super.setId(id); if(parts != null) for(int i = 0; i < parts.length; i++) parts[i].setId(id + i + 1); }
	@Override public boolean isPickable() { return false; }
	@Override public boolean isPushable() { return false; }
	@Override public boolean canChangeDimensions() { return !boss; }
	@Override public boolean removeWhenFarAway(double distance) { return !boss; }
	@Override public MobType getMobType() { return MobType.UNDEAD; }
	@Override public boolean canBeAffected(MobEffectInstance effect) { return !boss && super.canBeAffected(effect); }
	@Override protected ResourceLocation getDefaultLootTable() { return AbyssalCraft.loc("entities/" + (boss ? "asorah" : "spectral_dragon")); }
	@Override protected SoundEvent getAmbientSound() { return SoundEvents.ENDER_DRAGON_AMBIENT; }
	@Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENDER_DRAGON_HURT; }
	@Override protected float getSoundVolume() { return boss ? 5 : super.getSoundVolume(); }
	@Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
	@Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); if(boss) bossInfo.addPlayer(player); }
	@Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); if(boss) bossInfo.removePlayer(player); }
	@Override public void setCustomName(net.minecraft.network.chat.Component name) { super.setCustomName(name); if(bossInfo != null) bossInfo.setName(getDisplayName()); }

	public double[] getMovementOffsets(int age, float partialTick) {
		if(getHealth() <= 0) partialTick = 0;
		double t = 1 - partialTick; int current = (ringBufferIndex - age) & 63, previous = (current - 1) & 63;
		return new double[] {ringBuffer[current][0] + Mth.wrapDegrees(ringBuffer[previous][0] - ringBuffer[current][0]) * t,
				Mth.lerp(t, ringBuffer[current][1], ringBuffer[previous][1]), Mth.lerp(t, ringBuffer[current][2], ringBuffer[previous][2])};
	}

	@Override public void aiStep() {
		prevAnimTime = animTime;
		if(!isAlive()) return;
		Vec3 motion = getDeltaMovement();
		animTime += 0.2F / ((float) motion.horizontalDistance() * 10 + 1) * (float) Math.pow(2, motion.y);
		if(level().isClientSide) {
			// This entity supplies its own aiStep, including the interpolation normally done by LivingEntity.
			if(lerpSteps > 0) {
				double t = 1.0D / lerpSteps;
				setPos(Mth.lerp(t, getX(), lerpX), Mth.lerp(t, getY(), lerpY), Mth.lerp(t, getZ(), lerpZ));
				setYRot(getYRot() + (float) Mth.wrapDegrees(lerpYRot - getYRot()) / lerpSteps);
				setXRot(getXRot() + (float)(lerpXRot - getXRot()) / lerpSteps);
				--lerpSteps;
			}
			if(Mth.cos(prevAnimTime * Mth.TWO_PI) <= -0.3F && Mth.cos(animTime * Mth.TWO_PI) >= -0.3F)
				level().playLocalSound(getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_FLAP, getSoundSource(), 5, 0.8F + random.nextFloat() * 0.3F, false);
			if(ACConfig.particleEntity && boss) level().addParticle(ParticleTypes.DRIPPING_WATER, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0, -random.nextDouble(), 0);
		} else {
			updateHealing();
			if(!isNoAi()) fly();
			if(boss) {
				float health = getHealth() / getMaxHealth(); bossInfo.setProgress(health);
				bossInfo.setColor(health >= 0.75F ? BossEvent.BossBarColor.BLUE : health >= 0.5F ? BossEvent.BossBarColor.GREEN : health >= 0.25F ? BossEvent.BossBarColor.YELLOW : BossEvent.BossBarColor.RED);
			}
		}
		if(ringBufferIndex < 0) for(double[] entry : ringBuffer) { entry[0] = getYRot(); entry[1] = getY(); }
		ringBufferIndex = (ringBufferIndex + 1) & 63; ringBuffer[ringBufferIndex][0] = getYRot(); ringBuffer[ringBufferIndex][1] = getY();
		yBodyRot = getYRot();
		updateParts();
		if(!level().isClientSide && hurtTime == 0) contacts();
	}

	private void fly() {
		double dx = targetX - getX(), dy = targetY - getY(), dz = targetZ - getZ(), distance = dx * dx + dy * dy + dz * dz;
		if(flightTarget != null && flightTarget.isAlive()) {
			targetX = flightTarget.getX(); targetZ = flightTarget.getZ();
			targetY = flightTarget.getBoundingBox().minY + Math.min(10, 0.4D + Math.sqrt(dx * dx + dz * dz) / 80 - 1);
		} else { targetX += random.nextGaussian() * 2; targetZ += random.nextGaussian() * 2; }
		if(distance < 100 || distance > 22500 || horizontalCollision || verticalCollision) chooseTarget();
		Vec3 v = getDeltaMovement();
		double vertical = Mth.clamp(dy / Math.max(0.001D, Math.sqrt(dx * dx + dz * dz)), -0.6D, 0.6D);
		v = v.add(0, vertical * 0.1D, 0);
		setYRot(Mth.wrapDegrees(getYRot()));
		double turn = Mth.clamp(Mth.wrapDegrees(180 - Mth.atan2(dx, dz) * 180 / Math.PI - getYRot()), -50, 50);
		Vec3 forward = new Vec3(Mth.sin(getYRot() * Mth.DEG_TO_RAD), v.y, -Mth.cos(getYRot() * Mth.DEG_TO_RAD)).normalize();
		float alignment = Math.max(0, (float)(forward.dot(new Vec3(targetX - getX(), targetY - getY(), targetZ - getZ()).normalize()) + 0.5D) / 1.5F);
		double speed = v.horizontalDistance() + 1, limited = Math.min(speed, 40);
		yawVelocity = (float)(yawVelocity * 0.8F + turn * (0.7D / limited / speed));
		setYRot(getYRot() + yawVelocity * 0.1F);
		float factor = (float)(2 / (limited + 1));
		setDeltaMovement(v); moveRelative(0.06F * (alignment * factor + 1 - factor), new Vec3(0, 0, -1));
		move(MoverType.SELF, getDeltaMovement());
		Vec3 moved = getDeltaMovement();
		double drag = 0.8D + 0.15D * ((moved.normalize().dot(forward) + 1) / (boss ? 2 : 2.5D));
		setDeltaMovement(moved.multiply(drag, 0.91D, drag));
		if(!boss) checkInsideBlocks();
	}

	private void chooseTarget() {
		var players = level().players().stream().filter(p -> p.isAlive() && !p.isCreative() && !p.isSpectator()).toList();
		if(random.nextBoolean() && !players.isEmpty()) flightTarget = players.get(random.nextInt(players.size()));
		else {
			flightTarget = null;
			do { targetX = random.nextFloat() * 120 - 60; targetY = 70 + random.nextFloat() * 50; targetZ = random.nextFloat() * 120 - 60; }
			while(distanceToSqr(targetX, targetY, targetZ) <= 100);
		}
	}

	private void updateParts() {
		float tilt = (float)(getMovementOffsets(5, 1)[1] - getMovementOffsets(10, 1)[1]) * 10 * Mth.DEG_TO_RAD;
		float c = Mth.cos(tilt), s = -Mth.sin(tilt), yaw = getYRot() * Mth.DEG_TO_RAD, sin = Mth.sin(yaw), cos = Mth.cos(yaw);
		float bodyOffset = boss ? 1.5F : 0.1F, wingOffset = boss ? 6.5F : 2.2F, headOffset = boss ? 9.2F : 2.5F;
		place(body, getX() + sin * bodyOffset, getY(), getZ() - cos * bodyOffset);
		place(wing1, getX() + cos * wingOffset, getY() + (boss ? 2 : 1), getZ() + sin * wingOffset);
		place(wing2, getX() - cos * wingOffset, getY() + (boss ? 2 : 1), getZ() - sin * wingOffset);
		double[] old = getMovementOffsets(5, 1), now = getMovementOffsets(0, 1);
		place(head, getX() + Mth.sin(yaw - yawVelocity * 0.01F) * headOffset * c,
				getY() + now[1] - old[1] + s * headOffset, getZ() - Mth.cos(yaw - yawVelocity * 0.01F) * headOffset * c);
		DragonPart[] tails = {tail1, tail2, tail3};
		for(int i = 0; i < 3; i++) {
			double[] history = getMovementOffsets(12 + i * 2, 1);
			float angle = yaw + (float) Mth.wrapDegrees(history[0] - old[0]) * Mth.DEG_TO_RAD;
			float offset = (i + 1) * (boss ? 3 : 1.5F), base = boss ? 1.5F : 0.1F;
			place(tails[i], getX() - (sin * base + Mth.sin(angle) * offset) * c,
					getY() + history[1] - old[1] - (offset + base) * s + (boss ? 1.5D : 1), getZ() + (cos * base + Mth.cos(angle) * offset) * c);
		}
	}
	private static void place(DragonPart part, double x, double y, double z) {
		part.xo = part.getX(); part.yo = part.getY(); part.zo = part.getZ(); part.setPos(x, y, z);
	}

	private void contacts() {
		if(boss || ACConfig.hardcoreMode) for(DragonPart wing : new DragonPart[] {wing1, wing2})
			for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, wing.getBoundingBox().inflate(boss ? 5 : 1, boss ? 4 : 0.5D, boss ? 5 : 1).move(0, boss ? -4 : -0.5D, 0), e -> e != this)) {
				double dx = entity.getX() - body.getX(), dz = entity.getZ() - body.getZ(), length = Math.max(0.1D, dx * dx + dz * dz);
				entity.push(dx / length * 4, 0.2D, dz / length * 4);
			}
		for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, head.getBoundingBox().inflate(boss ? 1.5D : 0.25D), e -> e != this)) {
			if(boss) entity.hurt(damageSources().mobAttack(this), 10);
			else if(!EntityUtil.isEntityCoralium(entity)) entity.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 200));
			if(ACConfig.hardcoreMode && entity instanceof Player) entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), (float)(boss ? Math.max(ACConfig.damageAmpl, 1) : 1));
		}
	}

	public EntityACDragon getHealingPartner() {
		return level().getEntity(entityData.get(HEALER)) instanceof EntityACDragon dragon ? dragon : null;
	}
	private void updateHealing() {
		if(healingPartner != null && (!healingPartner.isAlive() || healingPartner.isRemoved())) {
			if(!boss) hurtPart(head, damageSources().explosion(null, null), 100);
			healingPartner = null;
		}
		if(healingPartner != null && tickCount % 10 == 0)
			if(boss && getHealth() < getMaxHealth()) heal(1);
			else if(!boss && healingPartner.getHealth() < healingPartner.getMaxHealth()) setHealth(getHealth() - 1);
		if(random.nextInt(10) == 0) healingPartner = level().getEntitiesOfClass(EntityACDragon.class, getBoundingBox().inflate(32), e -> e.isAlive() && e.boss != boss).stream()
				.min(java.util.Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
		entityData.set(HEALER, healingPartner == null ? -1 : healingPartner.getId());
	}

	/** Damage is accepted through the hitboxes, only from players or explosions. */
	public boolean hurtPart(DragonPart part, DamageSource source, float amount) {
		if(part != head) amount = amount / (boss ? 4 : 2) + 1;
		if(boss && amount > 30) amount = 10 + random.nextInt(10);
		targetX = getX() + Mth.sin(getYRot() * Mth.DEG_TO_RAD) * 5 + (random.nextFloat() - 0.5F) * 2;
		targetY = getY() + random.nextFloat() * 3 + 1; targetZ = getZ() - Mth.cos(getYRot() * Mth.DEG_TO_RAD) * 5 + (random.nextFloat() - 0.5F) * 2;
		flightTarget = null;
		if(source.getEntity() instanceof Player || source.is(DamageTypeTags.IS_EXPLOSION)) super.hurt(source, amount);
		return true;
	}
	@Override public boolean hurt(DamageSource source, float amount) {
		if(boss && source.is(DamageTypes.FELL_OUT_OF_WORLD) && getY() <= level().getMinBuildHeight() && !level().isClientSide) discard();
		return false;
	}
	@Override public void kill() { super.hurt(damageSources().fellOutOfWorld(), Float.MAX_VALUE); }

	@Override protected void tickDeath() {
		if(!boss) { super.tickDeath(); return; }
		++deathTicks;
		if(level().isClientSide && ACConfig.particleEntity && deathTicks >= 180)
			level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getRandomX(0.5D), getY() + 2 + random.nextDouble() * 4 - 2, getRandomZ(0.5D), 0, 0, 0);
		if(!level().isClientSide) {
			if(deathTicks == 1) level().globalLevelEvent(1018, blockPosition(), 0);
			if(deathTicks > 150 && deathTicks % 5 == 0) {
				int remaining = 500;
				while(remaining > 0) {
					int value = ExperienceOrb.getExperienceValue(remaining); remaining -= value;
					level().addFreshEntity(new ExperienceOrb(level(), getX(), getY(), getZ(), value));
					if(deathTicks == 160 || deathTicks == 180) {
						drop(new ItemStack(ACItems.chunk_of_coralium.get())); drop(new ItemStack(ACItems.refined_coralium_ingot.get())); drop(new ItemStack(ACItems.coralium_plagued_flesh.get()));
					}
				}
			}
			if(ACConfig.showBossDialogs && (deathTicks == 20 || deathTicks == 80 || deathTicks == 140))
				SpecialTextUtil.translatedGroup(level(), "Oblivionaire", "message.asorah.death." + (deathTicks == 20 ? 1 : deathTicks == 80 ? 2 : 3));
			if(deathTicks == 200) { drop(new ItemStack(ACItems.eye_of_the_abyss.get())); remove(RemovalReason.KILLED); }
		}
		move(MoverType.SELF, new Vec3(0, 0.1D, 0)); setYRot(getYRot() + 20); yBodyRot = getYRot();
	}
	private void drop(ItemStack stack) { level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), stack)); }
	@Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("DeathTicks", deathTicks); }
	@Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); deathTicks = tag.getInt("DeathTicks"); bossInfo.setName(getDisplayName()); }

	public static class DragonPart extends PartEntity<EntityACDragon> {
		private final EntityDimensions size;
		public final boolean head;
		DragonPart(EntityACDragon parent, boolean head, float width, float height) { super(parent); this.head = head; size = EntityDimensions.scalable(width, height); refreshDimensions(); }
		@Override public EntityDimensions getDimensions(Pose pose) { return size; }
		@Override public boolean isPickable() { return true; }
		@Override public boolean hurt(DamageSource source, float amount) { return !isInvulnerableTo(source) && getParent().hurtPart(this, source, amount); }
		@Override public boolean is(Entity other) { return other == this || other == getParent(); }
		@Override public boolean shouldBeSaved() { return false; }
		@Override protected void defineSynchedData() {}
		@Override protected void readAdditionalSaveData(CompoundTag tag) {}
		@Override protected void addAdditionalSaveData(CompoundTag tag) {}
	}
}
