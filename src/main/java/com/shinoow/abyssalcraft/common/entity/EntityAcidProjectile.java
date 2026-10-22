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

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Lesser Shoggoth acid: hurts (and corrodes the armor of) what it hits and dissolves soft blocks. A Shoggoth giving
 * birth while fighting can launch its baby riding one. Rendered as a slimeball, like in 1.12.2.
 */
public class EntityAcidProjectile extends ThrowableItemProjectile {

	int damage = 0;

	public EntityAcidProjectile(EntityType<? extends EntityAcidProjectile> type, Level level) {
		super(type, level);
	}

	public EntityAcidProjectile(Level level, LivingEntity thrower) {
		super(ACEntityTypes.ACID_PROJECTILE.get(), thrower, level);
		damage = thrower.isBaby() ? 3 : 6;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SLIME_BALL;
	}

	@Override
	public void tick()
	{
		if(ACConfig.particleEntity && level().isClientSide) {
			Vec3 motion = getDeltaMovement();
			for(int i = 0; i < 8; i++)
				level().addParticle(ParticleTypes.ITEM_SLIME, getX() - motion.x * 0.25F + random.nextDouble() * 0.6D - 0.3D, getY() - motion.y * 0.25F - 0.5D, getZ() - motion.z * 0.25F + random.nextDouble() * 0.6D - 0.3D, 0, 0, 0);
		}
		super.tick();
	}

	@Override
	protected void onHit(HitResult result) {
		// a baby Shoggoth riding the acid is let off when it hits a wall
		if (isVehicle() && isInWall())
		{
			for (Entity entity : getPassengers()){
				entity.stopRiding();
				entity.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
				entity.setDeltaMovement(entity.getDeltaMovement().add(random.nextGaussian(), 0.5D, random.nextGaussian()));
			}
			discard();
		}
		super.onHit(result);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if(level().isClientSide)
			return;
		Entity hit = result.getEntity();
		Entity thrower = getOwner();
		if(ACConfig.hardcoreMode && hit instanceof Player)
			hit.hurt(new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(AbyssalCraftAPI.hardcore_attack), this, thrower), 1F);
		if(hit instanceof LivingEntity entity && !(hit instanceof EntityLesserShoggoth)){
			if(!EntityUtil.canEntityBlockDamageSource(damageSources().thrown(this, thrower), entity) || !ACConfig.shieldsBlockAcid) {
				if (entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.acid), damage))
				{
					corrodeArmor(entity, 2);
					// dissolves the blocks on the line between where it hit and the entity
					BlockPos pos = blockPosition();
					BlockPos pos1 = entity.blockPosition();
					Vec3 vec = Vec3.atLowerCornerOf(pos1.subtract(pos)).normalize();
					double d = Math.sqrt(pos1.distSqr(pos));
					if(!ACConfig.no_acid_breaking_blocks && level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
						for(int i = 0; i < d * d; i++){
							double i1 = i / d;
							BlockPos pos2 = BlockPos.containing(pos.getX() + vec.x * i1 + .5, pos.getY() + vec.y * i1 + .5, pos.getZ() + vec.z * i1 + .5);
							if(canDissolve(level(), pos2, thrower))
								level().destroyBlock(pos2, false);
						}
				}
			} else if(EntityUtil.damageShield(entity, damage*2))
				entity.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.acid), 1);
			discardWithPassengers();
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if(level().isClientSide)
			return;
		if(level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
			BlockPos pos = result.getBlockPos();
			if(canDissolve(level(), pos, getOwner()))
				level().destroyBlock(pos, false);
		}
		discardWithPassengers();
	}

	private void discardWithPassengers() {
		ejectPassengers();
		discard();
	}

	/** Damages all armor except Ethaxium armor */
	public static void corrodeArmor(LivingEntity entity, int amount) {
		for(EquipmentSlot slot : EquipmentSlot.values())
			if(slot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack armor = entity.getItemBySlot(slot);
				if(!armor.isEmpty() && !isEthaxium(armor))
					armor.hurtAndBreak(amount, entity, e -> e.broadcastBreakEvent(slot));
			}
	}

	private static boolean isEthaxium(ItemStack armor) {
		return armor.is(ACItems.ethaxium_helmet.get()) || armor.is(ACItems.ethaxium_chestplate.get()) || armor.is(ACItems.ethaxium_leggings.get())
				|| armor.is(ACItems.ethaxium_boots.get());
	}

	/** Soft blocks that aren't Shoggoth blocks, Monolith Stone or block entities */
	public static boolean canDissolve(Level level, BlockPos pos, Entity entity) {
		BlockState state = level.getBlockState(pos);
		float hardness = state.getDestroySpeed(level, pos);
		return !state.isAir() && hardness < ACConfig.acidResistanceHardness && hardness != -1 && !state.is(com.shinoow.abyssalcraft.api.block.ACBlocks.shoggoth_ooze.get())
				&& !state.is(com.shinoow.abyssalcraft.api.block.ACBlocks.monolith_stone.get()) && !state.is(com.shinoow.abyssalcraft.api.block.ACBlocks.shoggoth_biomass.get())
				&& !state.hasBlockEntity() && (entity == null || state.canEntityDestroy(level, pos, entity));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("AcidDamage", damage);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		damage = tag.getInt("AcidDamage");
	}
}
