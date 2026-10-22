/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.ICoraliumEntity;
import com.shinoow.abyssalcraft.init.ACFluids;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** A plague-carrying squid that spits ink at nearby players. */
public class EntityCoraliumSquid extends Squid implements ICoraliumEntity, RangedAttackMob {

	public EntityCoraliumSquid(EntityType<? extends EntityCoraliumSquid> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(1, new RangedAttackGoal(this, 0.6D, 80, 8.0F));
	}

	/** In 1.12.2 Liquid Coralium had the water material. Keep its squid movement and air supply. */
	@Override
	public boolean isInWater() {
		return super.isInWater() || getFluidTypeHeight(ACFluids.LIQUID_CORALIUM_TYPE.get()) > 0;
	}

	@Override
	protected void handleAirSupply(int previousAirSupply) {
		if(isAlive() && isInWater()) setAirSupply(300);
		else super.handleAirSupply(previousAirSupply);
	}

	@Override
	protected void doPush(Entity entity) {
		if(!level().isClientSide && entity instanceof LivingEntity living && !EntityUtil.isEntityCoralium(living))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 100));
		super.doPush(entity);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if(!level().isClientSide) {
			Player player = level().getNearestPlayer(this, 7);
			if(player != null && !player.isCreative() && !player.isSpectator()) setTarget(player);
			if(getTarget() != null && (!getTarget().isAlive() || distanceToSqr(getTarget()) > 64
					|| getTarget() instanceof Player p && (p.isCreative() || p.isSpectator()))) setTarget(null);
		}
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		EntityInkProjectile ink = new EntityInkProjectile(level(), this);
		double dx = target.getX() - getX(), dz = target.getZ() - getZ();
		ink.shoot(dx, target.getEyeY() - 1.100000023841858D - ink.getY() + Math.sqrt(dx * dx + dz * dz) * 0.2D, dz, 1.6F, 12.0F);
		playSound(SoundEvents.SQUID_AMBIENT, 1, 1 / (random.nextFloat() * 0.4F + 0.8F));
		level().addFreshEntity(ink);
	}

	public static boolean checkSpawnRules(EntityType<EntityCoraliumSquid> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
		return pos.getY() > 45 && pos.getY() < level.getSeaLevel()
				&& (level.getFluidState(pos.below()).is(net.minecraft.tags.FluidTags.WATER)
						|| level.getFluidState(pos.below()).getFluidType() == ACFluids.LIQUID_CORALIUM_TYPE.get());
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return AbyssalCraft.loc("entities/coralium_squid");
	}
}
