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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.util.SpiritItemUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

/**
 * An item travelling along a route of the Item Transportation System (can't be picked up, passes through blocks)
 */
public class EntitySpiritItem extends ItemEntity {

	private BlockPos[] route = new BlockPos[0];
	private Direction facing;
	private int pathIndex;
	private double dX, dY, dZ;

	public EntitySpiritItem(EntityType<? extends EntitySpiritItem> type, Level level) {
		super(type, level);
		init();
	}

	public EntitySpiritItem(Level level, double x, double y, double z, ItemStack stack) {
		this(ACEntityTypes.SPIRIT_ITEM.get(), level);
		setPos(x, y, z);
		setItem(stack);
	}

	private void init() {
		setUnlimitedLifetime();
		setNeverPickUp();
		setDeltaMovement(Vec3.ZERO);
		noPhysics = true;
		setNoGravity(true);
	}

	public EntitySpiritItem setRoute(BlockPos[] route) {
		this.route = route;
		return this;
	}

	public EntitySpiritItem setFacing(Direction facing) {
		this.facing = facing;
		return this;
	}

	@Override
	public void tick() {
		super.tick();
		noPhysics = true;
		if(level().isClientSide) return;
		if(route.length == 0) {
			dropAndDiscard(getItem().copy());
			return;
		}
		if(pathIndex > route.length - 1)
			pathIndex = route.length - 1;
		BlockPos target = route[pathIndex];
		double d0 = getX() - target.getX();
		double d1 = getY() - target.getY();
		double d2 = getZ() - target.getZ();
		double d = d0 * d0 + d1 * d1 + d2 * d2;
		if(Math.floor(d) <= 1D) { // makes the item "enter" at a closer proximity
			if(pathIndex == route.length - 1 && blockPosition().equals(target)) {
				//journey is complete!
				BlockEntity te = level().getBlockEntity(target);
				IItemHandler inventory = te != null ? SpiritItemUtil.getInventory(te, facing) : null;
				if(inventory != null) {
					ItemStack res = ItemHandlerHelper.insertItemStacked(inventory, getItem().copy(), false);
					if(res.isEmpty())
						discard();
					else dropAndDiscard(res); // drop what didn't fit
				} else dropAndDiscard(getItem().copy());
				return;
			} else if(pathIndex < route.length - 1) {
				pathIndex++;
				dX = dY = dZ = 0;
			}
			// close to the target: keep drifting into it
		} else {// try to align the position to the middle of a block while moving
			BlockPos pos = blockPosition();
			if(target.getX() != pos.getX())
				dX = target.getX() + 0.5D > getX() ? 0.1 : -0.1;
			else {
				setPos(pos.getX() + 0.5D, getY(), getZ());
				dX = 0;
			}
			if(target.getY() != pos.getY())
				dY = target.getY() + 0.3D > getY() ? 0.05 : -0.05;
			else {
				setPos(getX(), pos.getY() + 0.2D, getZ());
				dY = 0;
			}
			if(target.getZ() != pos.getZ())
				dZ = target.getZ() + 0.5D > getZ() ? 0.1 : -0.1;
			else {
				setPos(getX(), getY(), pos.getZ() + 0.5D);
				dZ = 0;
			}
			setDeltaMovement(dX, target.getY() != pos.getY() ? dY : getDeltaMovement().y, dZ);
		}

		// preemptive self-destruction if the target isn't valid
		if(!isRemoved() && !SpiritItemUtil.validPos(route[route.length - 1], level(), facing))
			dropAndDiscard(getItem().copy());
	}

	/** Spirit items pass through blocks */
	@Override
	public void move(MoverType type, Vec3 movement) {
		setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
	}

	@Override
	protected void moveTowardsClosestSpace(double x, double y, double z) {
		//NO-OP
	}

	private void dropAndDiscard(ItemStack stack) {
		spawnAtLocation(stack, 1);
		discard();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag)
	{
		super.addAdditionalSaveData(tag);
		ListTag list = new ListTag();
		Arrays.stream(route).map(b -> LongTag.valueOf(b.asLong())).forEach(list::add);
		tag.put("route", list);
		tag.putInt("pathIndex", pathIndex);
		if(facing != null)
			tag.putInt("Facing", facing.get3DDataValue());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag)
	{
		super.readAdditionalSaveData(tag);
		ListTag list = tag.getList("route", Tag.TAG_LONG);
		List<BlockPos> positions = new ArrayList<>();
		for(Tag t : list)
			positions.add(BlockPos.of(((LongTag) t).getAsLong()));
		route = positions.toArray(new BlockPos[0]);
		pathIndex = tag.getInt("pathIndex");
		if(tag.contains("Facing"))
			facing = Direction.from3DDataValue(tag.getInt("Facing"));
		init();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
