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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IDreadEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.util.BiomeUtil;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Cha'garoth, the Dreadbeast: a rooted three headed boss in its Dreadlands lair. The middle head bites and vomits, the
 * side heads spit Dread Slugs (some carrying Dread Spawn, Spawn or Fists of Cha'garoth) and Dreaded Charges. It turns
 * the land around it into Dreadlands, keeps spawning helpers and heals over time.
 */
public class EntityChagaroth extends Monster implements IDreadEntity {

	private static final UUID attackDamageBoostUUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
	private static final AttributeModifier attackDamageBoost = new AttributeModifier(attackDamageBoostUUID, "Halloween Attack Damage Boost", 8D, AttributeModifier.Operation.ADDITION);
	private static final EntityDataAccessor<Integer> FIRST_HEAD_TARGET = SynchedEntityData.defineId(EntityChagaroth.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SECOND_HEAD_TARGET = SynchedEntityData.defineId(EntityChagaroth.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> THIRD_HEAD_TARGET = SynchedEntityData.defineId(EntityChagaroth.class, EntityDataSerializers.INT);
	@SuppressWarnings("unchecked")
	private static final EntityDataAccessor<Integer>[] HEAD_TARGETS = new EntityDataAccessor[] {FIRST_HEAD_TARGET, SECOND_HEAD_TARGET, THIRD_HEAD_TARGET};
	private static final EntityDataAccessor<Integer> BARF_TIMER = SynchedEntityData.defineId(EntityChagaroth.class, EntityDataSerializers.INT);
	/** Client: starts the vomit particles */
	private static final byte BARF_EVENT = 23;
	public int deathTicks;
	private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
	private final int[] nextHeadUpdate = new int[2];

	public EntityChagaroth(EntityType<? extends EntityChagaroth> type, Level level) {
		super(type, level);
		xpReward = 0; // 1.12.2 replaced the death update that dropped it with its own (5,000 XP)
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(2000.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.0D)
				.add(Attributes.MAX_HEALTH, 1000.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.0D, true) {
			// reaches further than its width, as it can't move
			@Override
			protected double getAttackReachSqr(LivingEntity target) {
				return mob.getBbWidth() * 1.5F * mob.getBbWidth() * 1.5F + target.getBbWidth();
			}
		});
		goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, false, entity -> !EntityUtil.isDreadPlagueCarrier(entity)));
	}

	@Override
	protected Component getTypeName() {
		return super.getTypeName().copy().withStyle(ChatFormatting.DARK_RED);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FIRST_HEAD_TARGET, 0);
		entityData.define(SECOND_HEAD_TARGET, 0);
		entityData.define(THIRD_HEAD_TARGET, 0);
		entityData.define(BARF_TIMER, 0);
	}

	public int getWatchedTargetId(int head) {
		return entityData.get(HEAD_TARGETS[head]);
	}

	/** Updates the target entity ID */
	public void updateWatchedTargetId(int targetOffset, int newId) {
		entityData.set(HEAD_TARGETS[targetOffset], newId);
	}

	public int getBarfTimer() {
		return entityData.get(BARF_TIMER);
	}

	public void setBarfTimer(int time) {
		entityData.set(BARF_TIMER, time);
	}

	public void decrementBarfTimer() {
		setBarfTimer(getBarfTimer() - 1);
	}

	public boolean isMouthOpen() {
		return getBarfTimer() > 0;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);

		if(flag && target instanceof LivingEntity living && !EntityUtil.isEntityDread(living))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 4.5F * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ACSounds.dreadguard_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ACSounds.dreadguard_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.dreadguard_death.get();
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/chagaroth");
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	@Override
	public int getArmorValue() {
		return 10;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public boolean requiresCustomPersistence() {
		return true;
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		bossInfo.setProgress(getHealth() / getMaxHealth());
		float health = getHealth(), max = getMaxHealth();
		if(health > max * 0.75)
			bossInfo.setColor(BossEvent.BossBarColor.BLUE);
		else if(health < max * 0.75 && health > max / 2)
			bossInfo.setColor(BossEvent.BossBarColor.GREEN);
		else if(health < max / 2 && health > max / 4)
			bossInfo.setColor(BossEvent.BossBarColor.YELLOW);
		else if(health < max / 4 && health > 0)
			bossInfo.setColor(BossEvent.BossBarColor.RED);

		// the side heads pick their own targets and spit at them
		for(int i = 1; i < 3; ++i)
			if(tickCount >= nextHeadUpdate[i - 1]) {
				nextHeadUpdate[i - 1] = tickCount + 10 + random.nextInt(10);

				int k1 = getWatchedTargetId(i);

				if(k1 > 0) {
					Entity entity = level().getEntity(k1);

					if(entity instanceof LivingEntity living && entity.isAlive() && distanceToSqr(entity) <= 48D * 48D && hasLineOfSight(entity)) {
						if(entity instanceof Player player && player.getAbilities().invulnerable)
							updateWatchedTargetId(i, 0);
						else
							launchDreadSlugToEntity(i + 1, living);
					} else
						updateWatchedTargetId(i, 0);
				} else {
					List<LivingEntity> list = new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(48D),
							entity -> !entity.isSpectator() && !EntityUtil.isEntityDread(entity)));

					for(int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2) {
						LivingEntity entitylivingbase = list.get(random.nextInt(list.size()));

						if(entitylivingbase != this && entitylivingbase.isAlive() && hasLineOfSight(entitylivingbase)) {
							if(entitylivingbase instanceof Player player) {
								if(!player.getAbilities().invulnerable) updateWatchedTargetId(i, entitylivingbase.getId());
							} else
								updateWatchedTargetId(i, entitylivingbase.getId());

							break;
						}

						list.remove(entitylivingbase);
					}
				}
			}

		updateWatchedTargetId(0, getTarget() != null ? getTarget().getId() : 0);
	}

	private void launchDreadSlugToEntity(int head, LivingEntity target) {
		launchDreadSlugToCoords(head, target.getX(), target.getY() + 0.5D, target.getZ());
	}

	/** Spits at (x, y, z): Dread Slugs (one or two), a slug carrying a mob, or a Dreaded Charge */
	private void launchDreadSlugToCoords(int head, double x, double y, double z) {
		double d0 = getHeadX(head);
		double d1 = getHeadY(head);
		double d2 = getHeadZ(head);
		double d3 = x - d0;
		double d4 = y - d1;
		double d5 = z - d2;
		float f1 = (float) Math.sqrt(d3 * d3 + d5 * d5) * 0.2F;
		float f2 = (float) Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
		shootSlug(d0, d1, d2, d3, d4, d5, f1, f2);

		switch(random.nextInt(5)) {
		case 0:
			shootSlug(d0, d1, d2, d3, d4, d5, f1, f2);
			nextHeadUpdate[head - 2] = tickCount + 10;
			break;
		case 1:
			throwMob(ACEntityTypes.DREAD_SPAWN.get(), d0, d1, d2, d3, d4 + f1, d5);
			nextHeadUpdate[head - 2] = tickCount + 20;
			break;
		case 2:
			throwMob(ACEntityTypes.SPAWN_OF_CHAGAROTH.get(), d0, d1, d2, d3, d4 + f1, d5);
			nextHeadUpdate[head - 2] = tickCount + 20;
			break;
		case 3:
			throwMob(ACEntityTypes.FIST_OF_CHAGAROTH.get(), d0, d1, d2, d3, d4 + f1, d5);
			nextHeadUpdate[head - 2] = tickCount + 20;
			break;
		case 4:
			level().levelEvent(null, LevelEvent.SOUND_GHAST_FIREBALL, blockPosition(), 0);
			d4 = y + 0.5D - d1;
			EntityDreadedCharge charge = new EntityDreadedCharge(level(), this, d3, d4, d5);
			charge.moveTo(d0, d1, d2, charge.getYRot(), charge.getXRot());
			level().addFreshEntity(charge);
			nextHeadUpdate[head - 2] = tickCount + 100;
			break;
		}
	}

	/**
	 * A Dread Slug aimed a bit above the target (arc), with the 1.12.2 extra push straight toward it
	 * @param arc added to dy for the aim
	 * @param distance distance to the target
	 */
	private void shootSlug(double x, double y, double z, double dx, double dy, double dz, float arc, float distance) {
		EntityDreadSlug slug = new EntityDreadSlug(level(), this);
		slug.setPos(x, y, z);
		slug.shoot(dx, dy + arc, dz, 1.75F, 1.0F);
		slug.setDeltaMovement(slug.getDeltaMovement().add(dx / distance * 0.8D * 0.8D, dy / distance * 0.8D * 0.8D, dz / distance * 0.8D * 0.8D));
		level().addFreshEntity(slug);
	}

	/** A Dread Slug thrown high up with a mob riding it */
	private void throwMob(EntityType<? extends Mob> type, double x, double y, double z, double dx, double dy, double dz) {
		Mob mob = type.create(level());
		if(mob == null) return;
		EntityDreadSlug slug = new EntityDreadSlug(level(), this);
		slug.setPos(x, y, z);
		mob.moveTo(x, y, z, getYRot(), 0.0F);
		slug.shoot(dx, dy + random.nextDouble() * 150D, dz, 1.3F, 1.0F);
		level().addFreshEntity(slug);
		if(level() instanceof ServerLevelAccessor server)
			mob.finalizeSpawn(server, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
		level().addFreshEntity(mob);
		mob.startRiding(slug, true);
	}

	private double getHeadX(int head) {
		if(head <= 0)
			return getX();
		float f = (yBodyRot + 180 * (head - 1)) * 0.017453292F;
		return getX() + Math.cos(f) * 1.4D;
	}

	private double getHeadY(int head) {
		return getY() + getEyeHeight() * 0.8D;
	}

	private double getHeadZ(int head) {
		if(head <= 0)
			return getZ();
		float f = (yBodyRot + 180 * (head - 1)) * 0.017453292F;
		return getZ() + Math.sin(f) * 1.4D;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	public void aiStep() {
		if(!level().isClientSide) {
			if(ACConfig.chagarothHealingAmount > 0 && isAlive() && tickCount % ACConfig.chagarothHealingPace == 0)
				heal(ACConfig.chagarothHealingAmount);

			if(tickCount % 40 == 0)
				spreadDreadlands();
		}

		// rooted to the ground
		setSprinting(false);
		Vec3 motion = getDeltaMovement();
		setDeltaMovement(0.0D, Math.min(motion.y, 0.0D), 0.0D);
		setOnGround(true);

		if(!level().isClientSide) {
			int timer = getBarfTimer();
			if(getTarget() != null && distanceToSqr(getTarget()) <= 256D && timer <= -200) setBarfTimer(150);

			if(timer > 0)
				barf(timer);

			decrementBarfTimer();

			if(isAlive())
				spawnHelpers();
		}

		super.aiStep();
	}

	/** Turns the 7x7 columns around it into Dreadlands (not the Purged biome) */
	private void spreadDreadlands() {
		BlockPos center = blockPosition();
		List<BlockPos> columns = new ArrayList<>();
		for(int x = center.getX() - 3; x <= center.getX() + 3; x++)
			for(int z = center.getZ() - 3; z <= center.getZ() + 3; z++) {
				BlockPos pos = new BlockPos(x, center.getY(), z);
				Holder<Biome> biome = level().getBiome(pos);
				if(!biome.is(ACBiomes.IS_DREADLANDS) && !biome.is(ACBiomes.purged))
					columns.add(pos);
			}
		BiomeUtil.updateBiomes(level(), columns, ACBiomes.dreadlands);
	}

	/** Dread Spawn / Spawn of Cha'garoth every 30 s, a Fist every minute, a Dreadguard every 2 and a Greater Dread Spawn every 4 */
	private void spawnHelpers() {
		AABB area = getBoundingBox().inflate(32);
		if(tickCount % 600 == 0)
			if(random.nextBoolean() && level().getEntitiesOfClass(EntityDreadSpawn.class, area).size() < 20)
				spawnHelper(ACEntityTypes.DREAD_SPAWN.get(), false);
			else if(level().getEntitiesOfClass(EntityChagarothSpawn.class, area).size() < 20)
				spawnHelper(ACEntityTypes.SPAWN_OF_CHAGAROTH.get(), false);

		if(tickCount % 1200 == 0)
			spawnHelper(ACEntityTypes.FIST_OF_CHAGAROTH.get(), false);

		if(tickCount % 2400 == 0)
			spawnHelper(ACEntityTypes.DREADGUARD.get(), true);

		if(tickCount % 4800 == 0 && level().getEntitiesOfClass(EntityGreaterDreadSpawn.class, area).size() < 10)
			spawnHelper(ACEntityTypes.GREATER_DREAD_SPAWN.get(), true);
	}

	private void spawnHelper(EntityType<? extends Mob> type, boolean push) {
		Mob mob = type.create(level());
		if(mob == null) return;
		mob.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
		if(push)
			mob.setDeltaMovement(mob.getDeltaMovement().add(1, 0, 0));
		level().addFreshEntity(mob);
		if(level() instanceof ServerLevelAccessor server)
			mob.finalizeSpawn(server, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
	}

	/** Vomits at what it looks at: Dread damage and Dread Plague II around it */
	private void barf(int timer) {
		level().broadcastEntityEvent(this, BARF_EVENT);
		if(tickCount % 5 == 0 && timer > 30) {
			BlockPos pos = BlockPos.containing(getX() + 0.5D, getEyeY() + 0.5D, getZ() + 0.5D);
			for(float spread : new float[] {0.6F, 0.5F, 0.4F})
				level().playSound(null, pos, ACSounds.dreadguard_barf.get(), getSoundSource(), 0.7F + random.nextFloat(), random.nextFloat() * spread + 0.2F);
		}
		Entity target = LookTarget.find(this, 8D, 20D, 8.0F, e -> !EntityUtil.isEntityDread(e));
		if(target != null) {
			// (1.12.2 also hit Cha'garoth itself when it stood next to the target)
			for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.0D), e -> e.isAlive() && e != this))
				if(random.nextInt(3) == 0)
					if(entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.dread), barfDamage(entity)))
						entity.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 200, 1));

			if(target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.dread), barfDamage(target)))
				if(target instanceof LivingEntity living)
					living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 200, 1));
		}
	}

	/** 15 at Cha'garoth, less further away (none past 7.5 blocks) */
	private float barfDamage(Entity entity) {
		return Math.max(0.0F, (float)(7.5D - distanceTo(entity)) * 2F);
	}

	/** Cha'garoth can't get potion effects */
	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return false;
	}

	protected void addMouthParticles() {
		Vec3 vector = getLookAngle();

		double px = getX() + vector.x * 1.5D;
		double py = getY() + getBbHeight() * 0.75F;
		double pz = getZ() + vector.z * 1.5D;

		ItemParticleOption shard = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItems.dreaded_shard_of_abyssalnite.get()));
		ItemParticleOption fragment = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItems.dread_fragment.get()));
		for(int i = 0; i < 75; i++) {
			double dx = vector.x;
			double dy = vector.y;
			double dz = vector.z;

			double spread = 15.0D + random.nextDouble() * 5.0D;
			double velocity = 0.5D + random.nextDouble();

			dx += random.nextGaussian() * 0.007499999832361937D * spread;
			dy += random.nextGaussian() * 0.007499999832361937D;
			dz += random.nextGaussian() * 0.007499999832361937D * spread;
			dx *= velocity;
			dy *= velocity;
			dz *= velocity;

			level().addParticle(shard, px + random.nextDouble() - 0.5D, py + random.nextDouble() - 0.5D, pz + random.nextDouble() - 0.5D, dx, dy, dz);
			level().addParticle(fragment, px + random.nextDouble() - 0.5D, py + random.nextDouble() - 0.5D, pz + random.nextDouble() - 0.5D, dx, dy, dz);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == BARF_EVENT) addMouthParticles();
		else
			super.handleEntityEvent(id);
	}

	/** Pushes what touches it away, barely moves itself */
	@Override
	public void push(Entity entityIn) {
		if(!isPassengerOfSameVehicle(entityIn) && !entityIn.noPhysics && !noPhysics) {
			double d0 = entityIn.getX() - getX();
			double d1 = entityIn.getZ() - getZ();
			double d2 = Math.max(Math.abs(d0), Math.abs(d1));

			if(d2 >= 0.01D) {
				d2 = Math.sqrt(d2);
				d0 = d0 / d2;
				d1 = d1 / d2;
				double d3 = 1.0D / d2;

				if(d3 > 1.0D) d3 = 1.0D;

				d0 = d0 * d3 * 0.2D;
				d1 = d1 * d3 * 0.2D;

				entityIn.push(d0, 0.0D, d1);
			}
		}
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if(deathTicks > 0)
			tag.putInt("DeathTicks", deathTicks);
		tag.putInt("BarfTimer", getBarfTimer());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		deathTicks = tag.getInt("DeathTicks");
		setBarfTimer(tag.getInt("BarfTimer"));
		if(hasCustomName())
			bossInfo.setName(getDisplayName());
	}

	@Override
	public void die(DamageSource source) {
		bossInfo.setProgress(getHealth() / getMaxHealth());
		super.die(source);
	}

	/** Big hits are cut down to 10 - 19; falling out of the world removes it */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount > 30) amount = 10 + random.nextInt(10);

		if(source.is(DamageTypes.FELL_OUT_OF_WORLD) && getY() <= level().getMinBuildHeight() && !level().isClientSide) {
			discard();
			return false;
		}

		return super.hurt(source, amount);
	}

	@Override
	public void kill() {
		super.hurt(damageSources().fellOutOfWorld(), Float.MAX_VALUE);
	}

	/** 10 seconds of explosions, 5,000 XP, Dreadlands materials, then the Dread-plagued Gateway Key */
	@Override
	protected void tickDeath() {
		++deathTicks;

		if(deathTicks <= 200 && level().isClientSide && ACConfig.particleEntity) {
			float f = (random.nextFloat() - 0.5F) * 8.0F;
			float f1 = (random.nextFloat() - 0.5F) * 4.0F;
			float f2 = (random.nextFloat() - 0.5F) * 8.0F;
			level().addParticle(ParticleTypes.FLAME, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			level().addParticle(ParticleTypes.LAVA, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			level().addParticle(ParticleTypes.POOF, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			if(deathTicks >= 190)
				level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX() + f, getY() + 2.0D + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
		}

		if(level().isClientSide) return;

		if(deathTicks > 150 && deathTicks % 5 == 0) {
			int i = 500;

			while(i > 0) {
				int j = ExperienceOrb.getExperienceValue(i);
				i -= j;
				level().addFreshEntity(new ExperienceOrb(level(), getX(), getY(), getZ(), j));
				if(deathTicks == 160 || deathTicks == 180) {
					drop(new ItemStack(ACItems.dread_fragment.get(), 4));
					drop(new ItemStack(ACItems.dreaded_chunk_of_abyssalnite.get(), 2));
					drop(new ItemStack(ACItems.dreaded_shard_of_abyssalnite.get()));
					drop(new ItemStack(ACItems.dreadium_ingot.get()));
				}
			}
		}

		if(ACConfig.showBossDialogs)
			switch(deathTicks) {
			case 20 -> SpecialTextUtil.translatedGroup(level(), "Cha'garoth", "message.chagaroth.death.1");
			case 80 -> SpecialTextUtil.translatedGroup(level(), "Cha'garoth", "message.chagaroth.death.2");
			case 140 -> SpecialTextUtil.translatedGroup(level(), "Cha'garoth", "message.chagaroth.death.3");
			case 200 -> SpecialTextUtil.translatedGroup(level(), "Cha'garoth", "message.chagaroth.death.4");
			default -> {}
			}
		if(deathTicks == 200) {
			level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ACItems.dread_plagued_gateway_key.get())));
			remove(RemovalReason.KILLED);
		}
	}

	private void drop(ItemStack stack) {
		level().addFreshEntity(new ItemEntity(level(), getX() + posneg(3), getY() + random.nextInt(3), getZ() + posneg(3), stack));
	}

	private int posneg(int num) {
		return random.nextBoolean() ? random.nextInt(num) : -1 * random.nextInt(num);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);

		AttributeInstance attribute = getAttribute(Attributes.ATTACK_DAMAGE);
		LocalDate date = LocalDate.now();

		attribute.removeModifier(attackDamageBoost);

		if(date.getMonthValue() == 10 && date.getDayOfMonth() == 31)
			attribute.addPermanentModifier(attackDamageBoost);

		return data;
	}
}
