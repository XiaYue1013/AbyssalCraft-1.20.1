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

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACEntityTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * The Essence of the Gatekeeper J'zahar leaves behind: an item that hangs in the air where he died (no gravity, no
 * motion, no merging) until it's picked up or expires.
 */
public class EntityGatekeeperEssence extends ItemEntity {

	private int essenceAge;

	public EntityGatekeeperEssence(EntityType<? extends EntityGatekeeperEssence> type, Level level) {
		super(type, level);
	}

	public EntityGatekeeperEssence(Level level, double x, double y, double z) {
		this(ACEntityTypes.GATEKEEPER_ESSENCE.get(), level);
		setPos(x, y, z);
		setItem(new ItemStack(ACItems.essence_of_the_gatekeeper.get()));
		setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public void tick() {
		ItemStack item = getItem();
		if(!item.isEmpty() && item.onEntityItemUpdate(this)) return;
		if(item.isEmpty()) {
			discard();
			return;
		}
		baseTick();
		xo = getX();
		yo = getY();
		zo = getZ();
		if(essenceAge != -32768)
			++essenceAge;
		if(!level().isClientSide && essenceAge >= lifespan) {
			int hook = ForgeEventFactory.onItemExpire(this, item);
			if(hook < 0) discard();
			else lifespan += hook;
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putShort("EssenceAge", (short) essenceAge);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		essenceAge = tag.getShort("EssenceAge");
	}

	@Override
	public int getAge() {
		return essenceAge;
	}
}
