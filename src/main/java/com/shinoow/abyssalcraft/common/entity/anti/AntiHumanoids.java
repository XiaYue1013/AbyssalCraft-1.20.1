/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (EntityAntiGhoul, EntityAntiPlayer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity.anti;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IAntiEntity;
import com.shinoow.abyssalcraft.common.entity.EntityACMonster;
import com.shinoow.abyssalcraft.common.entity.EntityDepthsGhoul;
import com.shinoow.abyssalcraft.common.handlers.PlagueEventHandler;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/** The antimatter Depths Ghoul and player */
public class AntiHumanoids {

	private static boolean blacklisted(ItemStack stack) {
		return ACConfig.mobItemPickupBlacklist.contains(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())));
	}

	private static void equip(Mob mob, DifficultyInstance difficulty, boolean pickup) {
		mob.setCanPickUpLoot(pickup);
		if(ACConfig.hardcoreMode)
			EntityUtil.suitUp(mob, false);
	}

	/** Anti-Ghoul: hunts Depths Ghouls; killed by Coralium or Dread damage it becomes an Omothol Ghoul */
	public static class AntiGhoul extends EntityACMonster implements IAntiEntity {

		public AntiGhoul(EntityType<? extends AntiGhoul> type, Level level) {
			super(type, level, "anti_ghoul");
			hardcore(120.0D, 10.0D);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 42.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
					.add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 5.0D);
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(0, new FloatGoal(this));
			goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
			goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 1.0D));
			goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
			goalSelector.addGoal(6, new RandomLookAroundGoal(this));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, AntiPlayer.class, 8.0F));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, AntiGhoul.class, 8.0F));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, AntiUndead.AntiZombie.class, 8.0F));
			goalSelector.addGoal(6, new LookAtPlayerGoal(this, AntiUndead.AntiSkeleton.class, 8.0F));
			targetSelector.addGoal(1, new HurtByTargetGoal(this));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityDepthsGhoul.class, true));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		}

		@Override
		protected void doPush(Entity entity) {
			if(!AntiMatter.collide(this, entity, EntityDepthsGhoul.class))
				super.doPush(entity);
		}

		@Override
		public boolean doHurtTarget(Entity target) {
			swing(InteractionHand.MAIN_HAND);
			swing(InteractionHand.OFF_HAND);
			return super.doHurtTarget(target);
		}

		@Override
		protected double halloweenDamage() {
			return 3.0D;
		}

		@Override
		protected SoundEvent getAmbientSound() {
			return ACSounds.ghoul_normal_ambient.get();
		}

		@Override
		protected SoundEvent getHurtSound(DamageSource source) {
			return ACSounds.ghoul_hurt.get();
		}

		@Override
		protected SoundEvent getDeathSound() {
			return ACSounds.ghoul_death.get();
		}

		@Override
		protected void playStepSound(BlockPos pos, BlockState state) {
			playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
		}

		@Override
		public MobType getMobType() {
			return MobType.UNDEAD;
		}

		@Override
		public boolean wantsToPickUp(ItemStack stack) {
			return !blacklisted(stack) && super.wantsToPickUp(stack);
		}

		@Override
		public void die(DamageSource source) {
			boolean wasAlive = !isRemoved() && !dead;
			super.die(source);
			if(wasAlive && !level().isClientSide && (source.is(AbyssalCraftAPI.coralium) || source.is(AbyssalCraftAPI.dread))) {
				Mob ghoul = ACEntityTypes.OMOTHOL_GHOUL.get().create(level());
				if(ghoul != null) {
					ghoul.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
					ghoul.finalizeSpawn((ServerLevel) level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.CONVERSION, null, null);
					level().addFreshEntity(ghoul);
				}
				discard();
			}
		}

		@Nullable
		@Override
		public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
			equip(this, difficulty, ACConfig.hardcoreMode || random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
			if(!ACConfig.hardcoreMode) {
				populateDefaultEquipmentSlots(random, difficulty);
				populateDefaultEquipmentEnchantments(random, difficulty);
			}
			return super.finalizeSpawn(level, difficulty, reason, data, tag);
		}
	}

	/** Anti-Player: what the Antimatter turns players into. Hunts players and other mobs; players it kills may come back as more of them */
	public static class AntiPlayer extends EntityACMonster implements IAntiEntity {

		public AntiPlayer(EntityType<? extends AntiPlayer> type, Level level) {
			super(type, level, "anti_player");
			hardcore(80.0D, 4.0D);
		}

		public static AttributeSupplier.Builder createAttributes() {
			return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D);
		}

		@Override
		protected void registerGoals() {
			goalSelector.addGoal(0, new FloatGoal(this));
			goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.35D, true));
			goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.35D));
			goalSelector.addGoal(4, new RandomStrollGoal(this, 0.35D));
			goalSelector.addGoal(5, new RandomLookAroundGoal(this));
			goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
			targetSelector.addGoal(1, new HurtByTargetGoal(this));
			targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
			targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, e -> e instanceof Bat || e instanceof Witch));
			targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 100, true, false, e -> !(e instanceof AntiPlayer)));
		}

		/** Only in Hardcore Mode do players and Anti-Players annihilate */
		@Override
		protected void doPush(Entity entity) {
			if(!ACConfig.hardcoreMode || !AntiMatter.collide(this, entity, Player.class))
				super.doPush(entity);
		}

		@Override
		protected SoundEvent getHurtSound(DamageSource source) {
			return ACSounds.antiplayer_hurt.get();
		}

		@Override
		protected void playStepSound(BlockPos pos, BlockState state) {
			playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
		}

		@Override
		public boolean wantsToPickUp(ItemStack stack) {
			return !blacklisted(stack) && super.wantsToPickUp(stack);
		}

		/** A player it kills comes back as an Anti-Player with the name turned around, half the time */
		@Override
		public boolean killedEntity(ServerLevel level, LivingEntity entity) {
			if(entity instanceof Player player && player.getRandom().nextBoolean()) {
				AntiPlayer anti = ACEntityTypes.ANTI_PLAYER.get().create(level);
				if(anti != null) {
					anti.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
					anti.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.CONVERSION, null, null);
					anti.setCustomName(Component.literal(PlagueEventHandler.reverseName(player.getName().getString())));
					anti.setPersistenceRequired();
					level.addFreshEntity(anti);
				}
			}
			return super.killedEntity(level, entity);
		}

		@Nullable
		@Override
		public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
			equip(this, difficulty, ACConfig.hardcoreMode || ACConfig.antiPlayersPickupLoot);
			if(!ACConfig.hardcoreMode) {
				populateDefaultEquipmentSlots(random, difficulty);
				populateDefaultEquipmentEnchantments(random, difficulty);
			}
			return super.finalizeSpawn(level, difficulty, reason, data, tag);
		}
	}
}
