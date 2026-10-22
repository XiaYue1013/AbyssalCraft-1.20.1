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
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.common.blocks.BlockRitualAltar;
import com.shinoow.abyssalcraft.common.blocks.BlockRitualPedestal;
import com.shinoow.abyssalcraft.common.world.DarkRealmTeleporter;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

/**
 * J'zahar, Gatekeeper of the Mysteries: the Omothol boss. Melee up close, wither skulls at range, and spells on
 * cooldowns: earthquake, the shout (Uftoin... mglagln!), implosions and black holes. Banishes other bosses near him,
 * heals over time, ignores fire, explosions and magic. His 40 second death pulls everything in, levels the area,
 * throws the players next to him into the Dark Realm and leaves the Essence of the Gatekeeper.
 */
public class EntityJzahar extends Monster implements RangedAttackMob, IOmotholEntity {

	private static final UUID attackDamageBoostUUID = UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9");
	private static final AttributeModifier attackDamageBoost = new AttributeModifier(attackDamageBoostUUID, "Halloween Attack Damage Boost", 10.0D, AttributeModifier.Operation.ADDITION);
	/** Spell timers: earthquake, black hole, implosion, shout, shared cooldown */
	public static final int EARTHQUAKE = 0, BLACK_HOLE = 1, IMPLOSION = 2, SHOUT = 3, COOLDOWN = 4;
	@SuppressWarnings("unchecked")
	private static final EntityDataAccessor<Integer>[] TIMERS = new EntityDataAccessor[] {
			SynchedEntityData.defineId(EntityJzahar.class, EntityDataSerializers.INT), SynchedEntityData.defineId(EntityJzahar.class, EntityDataSerializers.INT),
			SynchedEntityData.defineId(EntityJzahar.class, EntityDataSerializers.INT), SynchedEntityData.defineId(EntityJzahar.class, EntityDataSerializers.INT),
			SynchedEntityData.defineId(EntityJzahar.class, EntityDataSerializers.INT)};
	private static final String[] TIMER_NAMES = {"EarthquakeTime", "BlackHoleTime", "ImplosionTime", "ShoutTime", "CooldownTime"};
	/** Client: shout particles */
	private static final byte SHOUT_EVENT = 23;
	public int deathTicks;
	private int talkTimer, shoutTicks, iframes;
	private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
	private boolean that;
	private boolean doShout;
	private final Goal meleeAttack = new MeleeAttackGoal(this, 0.35D, true);
	private final Goal rangedAttack = new RangedAttackGoal(this, 0.4D, 40, 20.0F);
	private Boolean ranged;
	private double deathPull = 0.05D;

	public EntityJzahar(EntityType<? extends EntityJzahar> type, Level level) {
		super(type, level);
		xpReward = 0; // dropped during the death animation instead (5,000)
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(60.0D);
			setHealth(getMaxHealth());
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.ATTACK_DAMAGE, 30.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected Component getTypeName() {
		return super.getTypeName().copy().withStyle(ChatFormatting.BLUE);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		for(EntityDataAccessor<Integer> timer : TIMERS)
			entityData.define(timer, 0);
	}

	public int getTimer(int timer) {
		return timer >= 0 && timer < TIMERS.length ? entityData.get(TIMERS[timer]) : 0;
	}

	public void setTimer(int timer, int value) {
		if(timer >= 0 && timer < TIMERS.length)
			entityData.set(TIMERS[timer], value);
	}

	public void decrementTimer(int timer) {
		setTimer(timer, getTimer(timer) - 1);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.BLAZE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENDER_DRAGON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WITHER_DEATH;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/jzahar");
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
	public boolean canChangeDimensions() {
		return false;
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
		LivingEntity target = getTarget();
		// a second, wider melee attack once a second
		if(isAlive() && target != null && target.isAlive() && distanceToSqr(target) < getBbWidth() * getBbWidth() + target.getBbWidth() * target.getBbWidth() + 36D
				&& (tickCount + getId()) % 20 == 0)
			doHurtTarget(target);
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
	public boolean doHurtTarget(Entity target) {
		swing(InteractionHand.MAIN_HAND);
		boolean flag = super.doHurtTarget(target);
		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 4.5F * (float) Math.max(ACConfig.damageAmpl, 1.0D));
		return flag;
	}

	/**
	 * Big hits are cut to 5 - 9 and he can only be hurt every 1.5 seconds; fire, explosions and magic don't hurt him.
	 * Creative players hitting him may find themselves in survival (and dead), if J'zahar may break the fourth wall.
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount >= 20) amount = 5 + random.nextInt(5);
		if(source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC))
			return false;
		// (1.12.2 also ignored the radiation damage of NuclearCraft and HBM's mod)
		if(source.is(DamageTypes.FELL_OUT_OF_WORLD) && getY() <= level().getMinBuildHeight() && !level().isClientSide) {
			discard();
			return false;
		}
		if(!level().isClientSide) {
			// (1.12.2 cast the health ratio to int before multiplying, so this is 100 at full health and 10 otherwise)
			int health = getHealth() >= getMaxHealth() ? 100 : 10;
			if(source.getEntity() instanceof ServerPlayer player && player.isCreative() && random.nextInt(health) == 0 && ACConfig.jzaharBreaksFourthWall) {
				player.setGameMode(GameType.SURVIVAL);
				player.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), Integer.MAX_VALUE);
				if(player.isAlive()) {
					player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
					player.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), Integer.MAX_VALUE);
				}
				if(ACConfig.showBossDialogs)
					SpecialTextUtil.JzaharGroup(level(), "Whoops, I slipped!");
			}
		}
		if(iframes > 10)
			return false;
		else if(amount > 0) // don't activate iframes if no damage is dealt
			iframes = 30;
		return super.hurt(source, amount);
	}

	@Override
	public void kill() {
		super.hurt(damageSources().fellOutOfWorld(), Float.MAX_VALUE);
	}

	@Override
	public void die(DamageSource source) {
		bossInfo.setProgress(getHealth() / getMaxHealth());
		super.die(source);
	}

	private double getHeadX(int head) {
		if(head <= 0)
			return getX();
		float f = (yBodyRot + 180 * (head - 1)) / 180.0F * (float)Math.PI;
		return getX() + Math.cos(f) * 1.3D;
	}

	private double getHeadY(int head) {
		return head <= 0 ? getY() + 3.0D : getY() + 2.2D;
	}

	private double getHeadZ(int head) {
		if(head <= 0)
			return getZ();
		float f = (yBodyRot + 180 * (head - 1)) / 180.0F * (float)Math.PI;
		return getZ() + Math.sin(f) * 1.3D;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.90F;
	}

	@Override
	public void aiStep() {
		if(!level().isClientSide)
			serverUpdate();

		Vec3 motion = getDeltaMovement();
		if(motion.y > 0.42D)
			setDeltaMovement(motion.x, 0.42D, motion.z);

		super.aiStep();
	}

	private void serverUpdate() {
		if(ACConfig.jzaharHealingAmount > 0 && isAlive() && tickCount % ACConfig.jzaharHealingPace == 0)
			heal(ACConfig.jzaharHealingAmount);

		if(talkTimer > 0)
			talkTimer--;
		if(iframes > 0)
			--iframes;

		LivingEntity target = getTarget();
		if(target != null) {
			boolean useRanged = distanceToSqr(target) > 28D || target instanceof FlyingMob || target.getY() > getY() + 4D;
			if(ranged == null || ranged != useRanged) {
				ranged = useRanged;
				goalSelector.removeGoal(useRanged ? meleeAttack : rangedAttack);
				goalSelector.addGoal(2, useRanged ? rangedAttack : meleeAttack);
			}
		}

		banishOthers();

		if(deathTicks == 0)
			castSpells(target);
	}

	/** No other boss stays near the Gatekeeper; creative players get a word */
	private void banishOthers() {
		for(Entity entity : level().getEntities(this, getBoundingBox().inflate(64.0D))) {
			String message = null;
			if(entity instanceof EnderDragon || entity instanceof WitherBoss)
				message = "message.jzahar.banish.vanilla";
			else if(isAbyssalCraftBoss(entity))
				message = "message.jzahar.banish.ac";
			else if(entity instanceof EntityJzahar) {
				entity.discard();
				discard();
				EntityJzahar newgatekeeper = (EntityJzahar) getType().create(level());
				if(newgatekeeper != null && random.nextBoolean()) {
					newgatekeeper.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
					level().addFreshEntity(newgatekeeper);
				}
				explosionEffect(entity);
				explosionEffect(this);
				if(!that) {
					that = true;
					if(ACConfig.showBossDialogs)
						SpecialTextUtil.translatedGroup(level(), "J'zahar", "message.jzahar.banish.jzh");
				}
				continue;
			} else if(entity.getType().is(Tags.EntityTypes.BOSSES))
				message = "message.jzahar.banish.other";
			else if(entity instanceof Player player) {
				if(player.isCreative() && talkTimer == 0 && distanceTo(entity) <= 5) {
					talkTimer = 1200;
					if(ACConfig.showBossDialogs)
						if(EntityUtil.isPlayerCoralium(player))
							player.sendSystemMessage(Component.literal("J'zahar: <insert generic text here>"));
						else if(ACConfig.jzaharBreaksFourthWall) {
							player.sendSystemMessage(Component.literal("J'zahar: ").append(Component.translatable("message.jzahar.creative.1", player.getName())));
							player.sendSystemMessage(Component.literal("J'zahar: ").append(Component.translatable("message.jzahar.creative.2")));
						}
				}
				continue;
			}
			if(message != null) {
				entity.discard();
				explosionEffect(entity);
				if(ACConfig.showBossDialogs)
					SpecialTextUtil.translatedGroup(level(), "J'zahar", message);
			}
		}
	}

	private static boolean isAbyssalCraftBoss(Entity entity) {
		ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		return entity instanceof EntityChagaroth || key != null && AbyssalCraft.modid.equals(key.getNamespace())
				&& (key.getPath().equals("asorah") || key.getPath().equals("sacthoth"));
	}

	private void explosionEffect(Entity entity) {
		if(ACConfig.particleEntity && level() instanceof ServerLevel server)
			server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, entity.getX() + (random.nextFloat() - 0.5F) * 8.0F,
					entity.getY() + 2.0D + (random.nextFloat() - 0.5F) * 4.0F, entity.getZ() + (random.nextFloat() - 0.5F) * 8.0F, 1, 0, 0, 0, 0);
	}

	private void castSpells(@Nullable LivingEntity target) {
		for(int timer = 0; timer < TIMERS.length; timer++)
			decrementTimer(timer);

		// earthquake (400 ticks): everything on the ground around shakes
		if(getTimer(EARTHQUAKE) > 600)
			for(Entity entity : level().getEntities(this, getBoundingBox().inflate(64D)))
				if(entity.onGround() && entity instanceof LivingEntity living && !(entity instanceof IOmotholEntity)) {
					entity.push(Math.random() * 0.1D - 0.05D, Math.random() * 0.1D - 0.05D, Math.random() * 0.1D - 0.05D);
					living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
					if(random.nextInt(5) == 0)
						living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 3));
					entity.setXRot(entity.getXRot() + (float)(Math.random() * 4.0D - 2.0D));
					entity.setYRot(entity.getYRot() + (float)(Math.random() * 4.0D - 2.0D));
					if(random.nextInt(5) == 0) {
						entity.setDeltaMovement(entity.getDeltaMovement().x, 0.5D, entity.getDeltaMovement().z);
						entity.hurt(damageSources().hotFloor(), 1.0F);
					}
				}

		if(getTimer(SHOUT) < 0 && random.nextInt(20) == 0 && target != null && distanceToSqr(target) <= 9216D && !doShout && getTimer(COOLDOWN) < 0) {
			playSound(ACSounds.jzahar_shout.get(), 5F, 1F);
			SpecialTextUtil.JzaharGroup(level(), "Uftoin...");
			shoutTicks = getTimer(SHOUT) - 30;
			doShout = true;
			setTimer(COOLDOWN, 100);
		}

		if(getTimer(SHOUT) < shoutTicks && doShout)
			shout();

		if(getTimer(EARTHQUAKE) < 0 && random.nextInt(400) == 0 && target != null && target.onGround() && getTimer(COOLDOWN) < 0) {
			swing(InteractionHand.MAIN_HAND);
			setTimer(EARTHQUAKE, 1000);
			playSound(ACSounds.jzahar_earthquake.get(), 5F, 1F);
			SpecialTextUtil.JzaharGroup(level(), "Shugnah throd!");
			setTimer(COOLDOWN, 100);
		}

		if(getTimer(IMPLOSION) < 0 && target != null && getTimer(COOLDOWN) < 0) {
			swing(InteractionHand.MAIN_HAND);
			setTimer(IMPLOSION, 1200);
			playSound(ACSounds.jzahar_implosion.get(), 5F, 1F);
			SpecialTextUtil.JzaharGroup(level(), "Nilgh'ri mtli!");
			EntityImplosion implosion = new EntityImplosion(level(), this);
			BlockPos targetpos = target.blockPosition();
			implosion.setPos(targetpos.getX() + random.nextInt(10) * (random.nextBoolean() ? 1 : -1), targetpos.getY() + 2,
					targetpos.getZ() + random.nextInt(10) * (random.nextBoolean() ? 1 : -1));
			level().levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, implosion.blockPosition(), 0);
			level().addFreshEntity(implosion);
			setTimer(COOLDOWN, 100);
		}

		if(getTimer(BLACK_HOLE) < 0 && random.nextInt(800) == 0 && target != null && getTimer(COOLDOWN) < 0 && !ACConfig.no_black_holes) {
			swing(InteractionHand.MAIN_HAND);
			setTimer(BLACK_HOLE, 1600);
			playSound(ACSounds.jzahar_black_hole.get(), 5F, 1F);
			SpecialTextUtil.JzaharGroup(level(), "Ph'nilgh'ri n'ghft!");
			EntityBlackHole hole = new EntityBlackHole(level(), this);
			BlockPos targetpos = target.blockPosition();
			hole.setPos(targetpos.getX() + (5 + random.nextInt(10)) * (random.nextBoolean() ? 1 : -1), targetpos.getY() + 2,
					targetpos.getZ() + (5 + random.nextInt(10)) * (random.nextBoolean() ? 1 : -1));
			level().levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, hole.blockPosition(), 0);
			level().addFreshEntity(hole);
			setTimer(COOLDOWN, 100);
		}
	}

	/** ...mglagln! Blows everything in front of him (not Omothol entities) away */
	private void shout() {
		doShout = false;
		level().broadcastEntityEvent(this, SHOUT_EVENT);
		setTimer(SHOUT, 400);
		playSound(ACSounds.jzahar_blast.get(), 5F, 1F);
		SpecialTextUtil.JzaharGroup(level(), "...mglagln!");
		if(!isAlive()) return;
		Vec3 vector = getLookAngle();
		for(Entity entity : level().getEntitiesOfClass(Entity.class, getBoundingBox().inflate(64D).move(vector.x * 32D, vector.y * 32D, vector.z * 32D)))
			if(!(entity instanceof IOmotholEntity)) {
				double dx = vector.x;
				double dy = vector.z; // (sic, 1.12.2)
				double dz = vector.z;
				double spread = 10D;
				double velocity = 2D + random.nextDouble() * 8D;
				dx += random.nextGaussian() * 0.007499999832361937D * spread;
				dy += random.nextGaussian() * 0.007499999832361937D * spread;
				dz += random.nextGaussian() * 0.007499999832361937D * spread;
				dx *= velocity;
				dy *= velocity * 0.25D;
				dz *= velocity;
				entity.push(dx, dy, dz);
				entity.hurt(damageSources().flyIntoWall(), 2F * (float)velocity);
			}
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return false;
	}

	private void addShoutParticles() {
		Vec3 vector = getLookAngle();

		double px = getX() + vector.x * 5D;
		double py = getEyeY() + vector.y * 5D;
		double pz = getZ() + vector.z * 5D;

		for(int i = 0; i < 1000; i++) {
			double dx = vector.x;
			double dy = vector.y;
			double dz = vector.z;

			double spread = 10D;
			double velocity = 2D + random.nextDouble() * 18D;

			dx += random.nextGaussian() * 0.007499999832361937D * spread;
			dy += random.nextGaussian() * 0.007499999832361937D * spread;
			dz += random.nextGaussian() * 0.007499999832361937D * spread;
			dx *= velocity;
			dy *= velocity;
			dz *= velocity;

			level().addParticle(ParticleTypes.POOF, px + random.nextDouble() - 0.5D, py + random.nextDouble() - 0.5D, pz + random.nextDouble() - 0.5D, dx, dy, dz);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == SHOUT_EVENT)
			addShoutParticles();
		else
			super.handleEntityEvent(id);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if(deathTicks > 0)
			tag.putInt("DeathTicks", deathTicks);
		for(int i = 0; i < TIMER_NAMES.length; i++)
			tag.putInt(TIMER_NAMES[i], getTimer(i));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		deathTicks = tag.getInt("DeathTicks");
		for(int i = 0; i < TIMER_NAMES.length; i++)
			setTimer(i, tag.getInt(TIMER_NAMES[i]));
		if(hasCustomName())
			bossInfo.setName(getDisplayName());
	}

	/**
	 * 40 seconds: smoke, then everything within 32 blocks is pulled in; 5,000 XP at the end, the blocks within 9 blocks
	 * vanish (mobGriefing), players next to him are left at 1 HP and thrown into the Dark Realm, and the Essence of the
	 * Gatekeeper appears if a player is near
	 */
	@Override
	protected void tickDeath() {
		setDeltaMovement(Vec3.ZERO);
		++deathTicks;

		if(level().isClientSide) {
			deathParticles();
			return;
		}

		if(deathTicks == 410)
			playSound(ACSounds.jzahar_charge.get(), 1, 1);
		if(deathTicks >= 790 && deathTicks <= 800)
			playSound(SoundEvents.GENERIC_EXPLODE, 4, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
		if(deathTicks > 400 && deathTicks < 800) {
			float size = 32F;
			for(Entity entity : level().getEntities(this, getBoundingBox().inflate(size))) {
				double scale = (size - entity.distanceTo(this)) / size;
				Vec3 dir = entity.position().subtract(position()).normalize();
				entity.push(dir.x * -deathPull * scale, dir.y * -deathPull * scale, dir.z * -deathPull * scale);
			}
			deathPull += 0.0001;
		}

		if(deathTicks > 750 && deathTicks % 5 == 0) {
			int i = 500;
			while(i > 0) {
				int j = ExperienceOrb.getExperienceValue(i);
				i -= j;
				level().addFreshEntity(new ExperienceOrb(level(), getX(), getY(), getZ(), j));
			}
		}

		if(deathTicks == 790)
			finalBlast();

		if(ACConfig.showBossDialogs)
			for(int line = 1; line <= 10; line++)
				if(deathTicks == (line == 10 ? 800 : 20 + 80 * (line - 1)))
					SpecialTextUtil.translatedGroup(level(), "J'zahar", "message.jzahar.death." + line);

		if(deathTicks == 800)
			remove(RemovalReason.KILLED);
	}

	private void deathParticles() {
		if(deathTicks > 800) return;
		if(deathTicks < 400)
			level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY() + 2.5D, getZ(), 0, 0, 0);
		float f = (random.nextFloat() - 0.5F) * 3.0F;
		float f1 = (random.nextFloat() - 0.5F) * 2.0F;
		float f2 = (random.nextFloat() - 0.5F) * 3.0F;
		if(deathTicks >= 100 && deathTicks < 400)
			level().addParticle(ParticleTypes.SMOKE, getX() + f, getY() + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
		if(deathTicks >= 200 && deathTicks < 400) {
			level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + f, getY() + f1, getZ() + f2, 0.0D, 0.0D, 0.0D);
			level().addParticle(ParticleTypes.LAVA, getX(), getY() + 2.5D, getZ(), 0, 0, 0);
		}
		level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY() + 1.5D, getZ(), 0, 0, 0);
		if(deathTicks >= 790)
			level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1.5D, getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void finalBlast() {
		if(level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
			for(BlockPos pos : BlockPos.betweenClosed(blockPosition().offset(-9, -9, -9), blockPosition().offset(9, 9, 9))) {
				BlockState state = level().getBlockState(pos);
				if(!state.isAir()) {
					Block block = state.getBlock();
					if(block.getExplosionResistance() < 600000 && !(block instanceof BlockRitualPedestal) && !(block instanceof BlockRitualAltar))
						level().removeBlock(pos, false);
				}
			}
		ServerLevel darkRealm = level().getServer().getLevel(ACDimensions.DARK_REALM);
		for(Entity entity : level().getEntities(this, getBoundingBox().inflate(3, 1, 3)))
			if(entity instanceof ServerPlayer player) {
				player.setHealth(1);
				for(var effect : List.of(MobEffects.BLINDNESS, MobEffects.NIGHT_VISION, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN,
						MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.HUNGER, MobEffects.POISON))
					player.addEffect(new MobEffectInstance(effect, 2400, 5));
				player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 255));
				if(darkRealm != null)
					player.changeDimension(darkRealm, new DarkRealmTeleporter());
			} else if(entity instanceof LivingEntity || entity instanceof ItemEntity)
				entity.discard();
		if(level().getNearestPlayer(this, 48) != null)
			level().addFreshEntity(new EntityGatekeeperEssence(level(), getX(), getY(), getZ()));
	}

	@Override
	public void performRangedAttack(LivingEntity target, float velocity) {
		launchWitherSkullToCoords(0, target.getX(), target.getY() + target.getEyeHeight() * 0.35D, target.getZ(), random.nextFloat() < 0.001F);
	}

	private void launchWitherSkullToCoords(int head, double x, double y, double z, boolean dangerous) {
		level().levelEvent(null, LevelEvent.SOUND_WITHER_BOSS_SHOOT, blockPosition(), 0);
		double d3 = getHeadX(head);
		double d4 = getHeadY(head);
		double d5 = getHeadZ(head);
		WitherSkull skull = new WitherSkull(level(), this, x - d3, y - d4, z - d5);
		if(dangerous)
			skull.setDangerous(true);
		skull.setPosRaw(d3, d4, d5);
		level().addFreshEntity(skull);
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
