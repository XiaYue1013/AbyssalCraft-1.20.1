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
 *     1.20.1 port - TileEntityTieredSacrificialAltar merged in (capacity and cooldown from the block's tier)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ISingletonInventory;
import com.shinoow.abyssalcraft.api.energy.IEnergyCollector;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.common.blocks.BlockPE;
import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Marks a nearby living (non undead/shadow, adult, non player) mob and collects PE equal to its max health when it dies.
 * After collecting a fifth of its capacity it has to cool down.
 */
public class TileEntitySacrificialAltar extends TileEntityPEStorage implements IEnergyCollector, ISingletonInventory {

	private ItemStack item = ItemStack.EMPTY;
	@Nullable
	private LivingEntity entity;
	private int collectionLimit;
	private int coolDown;

	public TileEntitySacrificialAltar(BlockPos pos, BlockState state) {
		super(ACBlockEntities.SACRIFICIAL_ALTAR.get(), pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		item = ItemStack.of(tag.getCompound("Item"));
		collectionLimit = tag.getInt("CollectionLimit");
		coolDown = tag.getInt("CoolDown");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		CompoundTag nbtItem = new CompoundTag();
		if(!item.isEmpty())
			item.save(nbtItem);
		tag.put("Item", nbtItem);
		tag.putInt("CollectionLimit", collectionLimit);
		tag.putInt("CoolDown", coolDown);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntitySacrificialAltar tile) {
		if(tile.isCoolingDown()) {
			tile.coolDown--;
			if(tile.coolDown == 0)
				tile.markForUpdate();
		}

		ItemStack item = tile.item;
		if(!item.isEmpty() && item.getItem() instanceof IEnergyContainerItem container)
			if(container.canAcceptPE(item) && tile.canTransferPE())
				container.addEnergy(item, tile.consumeEnergy(20));

		if(tile.entity == null){
			List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(8, 3, 8));
			for(LivingEntity mob : mobs)
				if(!(mob instanceof Player) && !(mob instanceof ArmorStand))
					if(mob.getMobType() != MobType.UNDEAD && mob.getMobType() != AbyssalCraftAPI.SHADOW)
						if(mob.isAlive())
							if(!mob.isBaby()){
								tile.entity = mob;
								break;
							}
		}

		if(tile.entity != null){
			if(tile.getContainedEnergy() < tile.getMaxEnergy())
				tile.entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0, false, false));
			if(!tile.entity.isAlive()){
				// killed (not despawned/unloaded): collect its max health as PE
				if(tile.entity.isDeadOrDying() && tile.entity.getLastDamageSource() != null && !tile.isCoolingDown()
						&& tile.getContainedEnergy() < tile.getMaxEnergy()){
					float num = tile.entity.getMaxHealth();
					tile.addEnergy(num);
					tile.collectionLimit += num;
				}
				tile.entity = null;
			}
		}

		if(tile.collectionLimit >= tile.getMaxEnergy() / 5){
			tile.collectionLimit = 0;
			tile.coolDown = tile.getCooldownStartNumber();
			tile.markForUpdate();
		}
	}

	@Override
	public ItemStack getItem(){
		return item;
	}

	@Override
	public void setItem(ItemStack item){
		this.item = item;
		markForUpdate();
	}

	public int getCooldownTimer(){
		return coolDown;
	}

	public boolean isCoolingDown(){
		return coolDown > 0;
	}

	protected int getCooldownStartNumber() {
		int tier = getTier();
		return tier == BlockPE.BASE ? 1200 : 1200 - 200 * (tier + 1);
	}

	@Override
	public int getMaxEnergy() {
		return BlockPE.tieredCapacity(5000, getTier());
	}

	/** Sacrificial Altars only collect PE from sacrifices */
	@Override
	public boolean canAcceptPE() {
		return false;
	}
}
