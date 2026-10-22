/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - the head/body hitboxes of the Lesser Shoggoth (1.12.2 MultiPartEntityPart)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;

/**
 * A hitbox of a Lesser Shoggoth; damage to it goes to the Shoggoth
 */
public class EntityLesserShoggothPart extends PartEntity<EntityLesserShoggoth> {

	public final String name;
	private EntityDimensions size;

	public EntityLesserShoggothPart(EntityLesserShoggoth parent, String name, float width, float height) {
		super(parent);
		this.name = name;
		size = EntityDimensions.scalable(width, height);
		refreshDimensions();
	}

	/** Changes the size of the hitbox (the Shoggoth grows up) */
	public void setSize(float width, float height) {
		if(size.width != width || size.height != height) {
			size = EntityDimensions.scalable(width, height);
			refreshDimensions();
		}
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Nullable
	@Override
	public ItemStack getPickResult() {
		return getParent().getPickResult();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return !isInvulnerableTo(source) && getParent().hurt(source, amount);
	}

	@Override
	public boolean is(Entity entity) {
		return this == entity || getParent() == entity;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return size;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
