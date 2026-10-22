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

import java.util.UUID;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.common.entity.ai.LesserShoggothAttackGoal;
import com.shinoow.abyssalcraft.common.entity.ai.LesserShoggothBuildMonolithGoal;
import com.shinoow.abyssalcraft.common.entity.ai.LookAtTypeGoal;
import com.shinoow.abyssalcraft.common.entity.ai.WorshipGoal;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.entity.PartEntity;

/**
 * Lesser Shoggoth: a huge, climbing, acid spitting blob that eats animals and breeds from what it eats, spreads ooze
 * and builds monoliths with its kin. Its type (texture, loot, attack effect) depends on the dimension it spawned in:
 * 0 overworld, 1 Abyssal Wasteland (Coralium Plague), 2 Dreadlands (Dread Plague), 3 Omothol (Slowness), 4 Dark Realm
 * (Blindness, translucent shadow).
 */
public class EntityLesserShoggoth extends Monster implements IOmotholEntity {

	private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityLesserShoggoth.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> CHILD = SynchedEntityData.defineId(EntityLesserShoggoth.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityLesserShoggoth.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FOOD = SynchedEntityData.defineId(EntityLesserShoggoth.class, EntityDataSerializers.INT);
	private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.3D, AttributeModifier.Operation.MULTIPLY_BASE);
	private static final String[] LOOT = {"lesser_shoggoth", "lesser_abyssal_shoggoth", "lesser_dreaded_shoggoth", "lesser_omothol_shoggoth", "lesser_shadow_shoggoth"};

	private int monolithTimer;
	public boolean isBuilding, isAssisting;
	public final EntityLesserShoggothPart shoggothHead, shoggothBody;
	private final EntityLesserShoggothPart[] shoggothParts;

	public EntityLesserShoggoth(EntityType<? extends EntityLesserShoggoth> type, Level level) {
		super(type, level);
		shoggothParts = new EntityLesserShoggothPart[] {shoggothHead = new EntityLesserShoggothPart(this, "head", 1.0F, 1.0F), shoggothBody = new EntityLesserShoggothPart(this, "body", 1.0F, 1.0F)};
		setId(ENTITY_COUNTER.getAndAdd(shoggothParts.length + 1) + 1); // part ids follow the Shoggoth's id (like the Ender Dragon)
		if(ACConfig.hardcoreMode) { // 1.12.2 applied this in applyEntityAttributes
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16.0D);
			setHealth(getMaxHealth());
		}
	}

	/** 1.12.2 EntityMob defaults (movement speed 0.7, the AI moves at 0.35 of that) */
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.7D).add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
				.add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 8.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new LesserShoggothAttackGoal(this, 0.35D, true));
		goalSelector.addGoal(3, new FleeSunGoal(this, 0.35D));
		goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(5, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Zombie.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Skeleton.class, 8.0F));
		for(String mob : new String[] {ACEntities.depths_ghoul, ACEntities.abyssal_zombie, ACEntities.skeleton_goliath, ACEntities.dreadling,
				ACEntities.dread_spawn, ACEntities.demon_pig, ACEntities.dreadguard, ACEntities.omothol_ghoul, ACEntities.remnant,
				ACEntities.lesser_shoggoth, ACEntities.minion_of_the_gatekeeper})
			goalSelector.addGoal(7, new LookAtTypeGoal(this, mob, 8.0F));
		goalSelector.addGoal(8, new LesserShoggothBuildMonolithGoal(this));
		goalSelector.addGoal(9, new WorshipGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 20, true, false, EntityUtil::isShoggothFood));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, 10, true, false, entity -> !(entity instanceof ZombieVillager)));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Skeleton.class, true, false));
	}

	@Override
	protected PathNavigation createNavigation(Level level)
	{
		return new WallClimberNavigation(this, level);
	}

	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		entityData.define(CHILD, false);
		entityData.define(TYPE, 0);
		entityData.define(FOOD, 0);
		entityData.define(CLIMBING, (byte)0);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if(CHILD.equals(key))
			refreshDimensions();
		super.onSyncedDataUpdated(key);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void tick()
	{
		super.tick();

		if (!level().isClientSide)
			setBesideClimbableBlock(horizontalCollision);
	}

	@Override
	public boolean isBaby()
	{
		return entityData.get(CHILD);
	}

	@Override
	public void setBaby(boolean par1)
	{
		entityData.set(CHILD, par1);

		if (!level().isClientSide)
		{
			AttributeInstance attributeinstance = getAttribute(Attributes.MOVEMENT_SPEED);
			attributeinstance.removeModifier(babySpeedBoostModifier);

			if (par1) {
				attributeinstance.addTransientModifier(babySpeedBoostModifier);
				getAttribute(Attributes.MAX_HEALTH).setBaseValue(ACConfig.hardcoreMode ? 100.0D : 50.0D);
				getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ACConfig.hardcoreMode ? 8.0D : 4.0D);
			} else {
				getAttribute(Attributes.MAX_HEALTH).setBaseValue(ACConfig.hardcoreMode ? 200.0D : 100.0D);
				getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ACConfig.hardcoreMode ? 16.0D : 8.0D);
				setHealth(ACConfig.hardcoreMode ? 200.0F : 100.0F);
			}
		}
	}

	public int getShoggothType() {
		return entityData.get(TYPE);
	}

	public void setShoggothType(int par1) {
		entityData.set(TYPE, par1);
	}

	public void setFoodLevel(int par1){
		entityData.set(FOOD, par1);
	}

	public int getFoodLevel(){
		return entityData.get(FOOD);
	}

	/** Eating a mob gives a point per 0.25 of its width x height */
	public void feed(LivingEntity entity){
		int food = getFoodLevel() + getPointsFromSize(entity.getBbHeight() * entity.getBbWidth());
		entityData.set(FOOD, food);
		playSound(ACSounds.shoggoth_consume.get(), getSoundVolume(), getVoicePitch());
	}

	private int getPointsFromSize(float size) {
		int food = 0;
		for(float sizeNum = size; sizeNum > 0; sizeNum-=0.25)
			food++;
		return food;
	}

	@Override
	public boolean onClimbable()
	{
		return isBesideClimbableBlock();
	}

	/**
	 * Reduces this Shoggoth's monolith timer
	 */
	public void reduceMonolithTimer(){
		monolithTimer = Math.max(monolithTimer - 200, 0);
	}

	public void resetMonolithTimer() {
		monolithTimer = 0;
	}

	public int getMonolithTimer() {
		return monolithTimer;
	}

	@Override
	public boolean isPushedByFluid()
	{
		return false;
	}

	@Override
	public void aiStep()
	{
		super.aiStep();

		if(isInWater())
			setDeltaMovement(getDeltaMovement().add(getLookAngle().scale(0.005D)));

		if(!isBaby())
			monolithTimer++;

		if(getFoodLevel() >= 8 && !level().isClientSide && tickCount % 20 == 0){
			setFoodLevel(getFoodLevel() - 8);
			if(!isBaby())
				giveBirth();
			else setBaby(false);
		}

		if(!level().isClientSide){
			int x = Mth.floor(getX() - 0.25F);
			int y = Mth.floor(getY());
			int z = Mth.floor(getZ() - 0.25F);

			spawnOoze(x, y, z);
			if(!isBaby()){
				spawnOoze(x - 1, y, z);
				spawnOoze(x, y, z - 1);
				spawnOoze(x - 1, y, z - 1);
			}
		}

		if(tickCount % 40 == 0 && ACConfig.consumeItems && !level().isClientSide)
			for(ItemEntity entity : level().getEntitiesOfClass(ItemEntity.class, getBoundingBox())) {
				if(entity.getItem().isEdible()) {
					int food = getFoodLevel() + entity.getItem().getCount();
					entityData.set(FOOD, food);
					playSound(ACSounds.shoggoth_consume.get(), getSoundVolume(), getVoicePitch());
				} else playSound(SoundEvents.ITEM_BREAK, getSoundVolume(), 1.0F);
				entity.discard();
			}

		if(level().isClientSide && getShoggothType() == 4 && ACConfig.particleEntity) {
			float brightness = getLightLevelDependentMagicValue();
			for (int i = 0; i < 2 * (brightness > 0.1f ? brightness : 0); ++i)
				level().addParticle(ParticleTypes.LARGE_SMOKE, getX() + (random.nextDouble() - 0.5D) * getBbWidth(), getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0.0D, 0.0D, 0.0D);
		}

		updateParts();
	}

	/** A baby is born; if the Shoggoth is fighting, it may be launched at the target riding a ball of acid */
	private void giveBirth() {
		EntityLesserShoggoth shoggoth = ACEntityTypes.LESSER_SHOGGOTH.get().create(level());
		shoggoth.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
		shoggoth.finalizeSpawn((ServerLevel) level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.BREEDING, null, null);
		shoggoth.setBaby(true);
		shoggoth.setShoggothType(getShoggothType());
		level().addFreshEntity(shoggoth);
		playSound(ACSounds.shoggoth_birth.get(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
		LivingEntity target = getTarget();
		if(target != null && target.isAlive() && getRandom().nextInt(3) == 0) {
			EntityAcidProjectile acidprojectile = new EntityAcidProjectile(level(), this);
			double d0 = target.getX() - getX();
			double d1 = target.getY() + target.getEyeHeight() - 1.100000023841858D - acidprojectile.getY();
			double d2 = target.getZ() - getZ();
			float f1 = (float) Math.sqrt(d0 * d0 + d2 * d2) * 0.2F;
			acidprojectile.shoot(d0, d1 + f1, d2, 0.8F, 8.0F);
			playSound(ACSounds.shoggoth_shoot.get(), 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
			level().addFreshEntity(acidprojectile);
			shoggoth.startRiding(acidprojectile, true);
		}
	}

	/** Positions the head and body hitboxes */
	private void updateParts() {
		double a = Math.toRadians(getYRot());
		double offsetx = -Math.sin(a);
		double offsetz = Math.cos(a);
		float scale = isBaby() ? 0.5F : 1.0F;

		shoggothBody.setSize(1.8F * scale, 2.2F * scale);
		shoggothHead.setSize(1.6F * scale, 1.2F * scale);
		movePart(shoggothBody, getX() - offsetx * 0.5F * scale, getY(), getZ() - offsetz * 0.5F * scale);
		movePart(shoggothHead, getX() + offsetx * 0.5D * scale, getY() + 1.5F * scale, getZ() + offsetz * 0.5D * scale);
	}

	private void movePart(EntityLesserShoggothPart part, double x, double y, double z) {
		part.xo = part.xOld = part.getX();
		part.yo = part.yOld = part.getY();
		part.zo = part.zOld = part.getZ();
		part.setPos(x, y, z);
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	public PartEntity<?>[] getParts() {
		return shoggothParts;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < shoggothParts.length; i++)
			shoggothParts[i].setId(id + i + 1);
	}

	/**
	 * Spreads Shoggoth Ooze (or makes the ooze it's standing in thicker)
	 */
	private void spawnOoze(int x, int y, int z){
		BlockPos pos = new BlockPos(x, y, z);
		if(ACConfig.shoggothOoze) {
			BlockState state = level().getBlockState(pos);
			if((state.isAir() || state.canBeReplaced()) && ACBlocks.shoggoth_ooze.get().defaultBlockState().canSurvive(level(), pos)
					&& !state.is(ACBlocks.shoggoth_ooze.get()) && state.getFluidState().isEmpty())
				level().setBlockAndUpdate(pos, ACBlocks.shoggoth_ooze.get().defaultBlockState());
			else if(state.is(ACBlocks.shoggoth_ooze.get()) && state.getValue(SnowLayerBlock.LAYERS) < 8
					&& tickCount % 10 == 0 && random.nextInt(5) == 0)
				level().setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) + 1));
		}
	}

	public boolean isBesideClimbableBlock()
	{
		return (entityData.get(CLIMBING) & 1) != 0;
	}

	public void setBesideClimbableBlock(boolean par1)
	{
		byte b0 = entityData.get(CLIMBING);

		if (par1)
			b0 = (byte)(b0 | 1);
		else
			b0 &= -2;

		entityData.set(CLIMBING, b0);
	}

	@Override
	public boolean doHurtTarget(Entity par1Entity)
	{
		boolean flag = super.doHurtTarget(par1Entity);

		if (flag && par1Entity instanceof LivingEntity living)
			switch(getShoggothType()){
			case 1:
				if(!EntityUtil.isEntityCoralium(living))
					living.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 100));
				break;
			case 2:
				if(!EntityUtil.isEntityDread(living))
					living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
				break;
			case 3:
				living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
				break;
			case 4:
				living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
				break;
			}

		if(ACConfig.hardcoreMode && par1Entity instanceof Player)
			par1Entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 3 * (float) Math.max(ACConfig.damageAmpl, 1.0D));

		return flag;
	}

	@Override
	public boolean hurt(DamageSource par1DamageSource, float par2)
	{
		if(par1DamageSource.is(DamageTypeTags.IS_PROJECTILE) && !ACConfig.no_projectile_damage_immunity){
			playSound(SoundEvents.SLIME_JUMP, getSoundVolume(), ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			return false;
		}
		// suffocating: dissolves the blocks above it (but not in a monolith)
		if(par1DamageSource.is(DamageTypes.IN_WALL))
			if(!level().getBlockState(blockPosition()).is(ACBlocks.monolith_stone.get()) && level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
				sprayAcid(true);
			else return false;
		if(par1DamageSource.is(DamageTypes.CACTUS)) return false;

		return super.hurt(par1DamageSource, par2);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return ACSounds.shoggoth_ambient.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ACSounds.shoggoth_hurt.get();
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return ACSounds.shoggoth_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state)
	{
		playSound(ACSounds.shoggoth_step.get(), 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getDefaultLootTable(){
		int type = getShoggothType();
		return AbyssalCraft.loc("entities/" + LOOT[type >= 0 && type < LOOT.length ? type : 0]);
	}

	@Override
	public MobType getMobType()
	{
		return getShoggothType() == 4 ? AbyssalCraftAPI.SHADOW : MobType.UNDEFINED;
	}

	/** Spits acid at the target */
	public void sprayAcidAt(Entity target) {
		EntityAcidProjectile acidprojectile = new EntityAcidProjectile(level(), this);
		double d0 = target.getX() - getX();
		double d1 = target.getY() + target.getEyeHeight() - 1.100000023841858D - acidprojectile.getY();
		double d2 = target.getZ() - getZ();
		float f1 = (float) Math.sqrt(d0 * d0 + d2 * d2) * 0.2F;
		acidprojectile.shoot(d0, d1 + f1, d2, 1.0F, 12.0F);
		playSound(ACSounds.shoggoth_shoot.get(), 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
		level().addFreshEntity(acidprojectile);
	}

	/**
	 * Sprays acid around itself: hurts everything next to it (corroding armor or shields) and dissolves the blocks at
	 * the top (or bottom) of its hitbox
	 */
	public void sprayAcid(boolean above) {
		AABB aabb = getBoundingBox().inflate(1, 0, 1);
		level().broadcastEntityEvent(this, (byte)23);
		for(LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, aabb, e -> !(e instanceof EntityLesserShoggoth)))
			if(!EntityUtil.canEntityBlockDamageSource(damageSources().mobAttack(this), entity) || !ACConfig.shieldsBlockAcid) {
				if(entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.acid), (float)(4.5D - distanceTo(entity))))
					EntityAcidProjectile.corrodeArmor(entity, isBaby() ? 4 : 8);
			} else if(EntityUtil.damageShield(entity, isBaby() ? 6 : 12))
				entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.acid), 1);
		int y = Mth.floor(above ? aabb.maxY : aabb.minY);
		for(int i = Mth.floor(aabb.minX); i < aabb.maxX+1; i++)
			for(int j = Mth.floor(aabb.minZ); j < aabb.maxZ+1; j++){
				BlockPos pos = new BlockPos(i, y, j);
				if(EntityAcidProjectile.canDissolve(level(), pos, this))
					level().destroyBlock(pos, false);
			}
	}

	protected void addAcidParticles()
	{
		AABB aabb = getBoundingBox();
		Vec3 vector = getLookAngle();

		for(int i = Mth.floor(aabb.minX); i < aabb.maxX+1; i++)
			for(int j = Mth.floor(aabb.minY); j < aabb.maxY+1; j++)
				for(int k = Mth.floor(aabb.minZ); k < aabb.maxZ+1; k++){
					double spread = 5.0D + getRandom().nextDouble() * 2.5D;
					double velocity = 0.5D + getRandom().nextDouble() * 0.5D;
					double dx = (vector.x + getRandom().nextGaussian() * 0.007499999832361937D * spread) * velocity;
					double dy = (vector.y + getRandom().nextGaussian() * 0.007499999832361937D * spread) * velocity;
					double dz = (vector.z + getRandom().nextGaussian() * 0.007499999832361937D * spread) * velocity;

					level().addParticle(ParticleTypes.ITEM_SLIME, i, j, k, dx, dy, dz);
				}
	}

	@Override
	public void handleEntityEvent(byte id)
	{
		if (id == 23) addAcidParticles();
		else
			super.handleEntityEvent(id);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag par1NBTTagCompound)
	{
		super.readAdditionalSaveData(par1NBTTagCompound);

		if(par1NBTTagCompound.getBoolean("IsBaby"))
			setBaby(true);

		if (par1NBTTagCompound.contains("ShoggothType"))
			setShoggothType(par1NBTTagCompound.getInt("ShoggothType"));

		if(par1NBTTagCompound.contains("FoodLevel"))
			setFoodLevel(par1NBTTagCompound.getInt("FoodLevel"));

		monolithTimer = par1NBTTagCompound.getInt("MonolithTimer");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag par1NBTTagCompound)
	{
		super.addAdditionalSaveData(par1NBTTagCompound);

		if (isBaby())
			par1NBTTagCompound.putBoolean("IsBaby", true);

		par1NBTTagCompound.putInt("ShoggothType", getShoggothType());
		par1NBTTagCompound.putInt("FoodLevel", getFoodLevel());
		par1NBTTagCompound.putInt("MonolithTimer", monolithTimer);
	}

	@Override
	public boolean killedEntity(ServerLevel level, LivingEntity par1EntityLivingBase)
	{
		boolean drop = super.killedEntity(level, par1EntityLivingBase);

		if(EntityUtil.isShoggothFood(par1EntityLivingBase))
			feed(par1EntityLivingBase);
		return drop;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance potioneffectIn) {
		if(potioneffectIn.getEffect() == MobEffects.POISON)
			return false;
		return super.canBeAffected(potioneffectIn);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
	{
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);

		setShoggothType(0);

		if(level.getLevel().dimension() == ACDimensions.ABYSSAL_WASTELAND)
			setShoggothType(1);
		if(level.getLevel().dimension() == ACDimensions.DREADLANDS)
			setShoggothType(2);
		if(level.getLevel().dimension() == ACDimensions.OMOTHOL)
			setShoggothType(3);
		if(level.getLevel().dimension() == ACDimensions.DARK_REALM)
			setShoggothType(4);

		if (data == null)
			data = new GroupData(random.nextFloat() < ForgeConfig.SERVER.zombieBabyChance.get());

		if (data instanceof GroupData groupdata && groupdata.isBaby)
			setBaby(true);

		return data;
	}

	static class GroupData implements SpawnGroupData
	{
		public final boolean isBaby;

		GroupData(boolean isBaby)
		{
			this.isBaby = isBaby;
		}
	}
}
