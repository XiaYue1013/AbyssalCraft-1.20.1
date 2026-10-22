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
 *     1.20.1 port - shared inventory block entity base
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Base for AbyssalCraft block entities with an inventory (replaces the 1.12.2 IInventory/ISidedInventory boilerplate).
 * Exposes sided item handlers and syncs its NBT to clients.
 */
public abstract class TileEntityACInventory extends BaseContainerBlockEntity implements WorldlyContainer {

	protected NonNullList<ItemStack> items;
	private final String containerName;
	private LazyOptional<? extends IItemHandlerModifiable>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	protected TileEntityACInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, int size, String containerName) {
		super(type, pos, state);
		items = NonNullList.withSize(size, ItemStack.EMPTY);
		this.containerName = containerName;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(containerName);
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : items)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = ContainerHelper.removeItem(items, slot, count);
		if(!stack.isEmpty())
			setChanged();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return net.minecraft.world.Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, items);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		ContainerHelper.saveAllItems(tag, items);
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction face) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
		return true;
	}

	/**
	 * Whether or not the inventory contents should be sent to clients (for block entities that render their items)
	 */
	protected boolean syncContents() {
		return false;
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		if(syncContents())
			saveAdditional(tag);
		return tag;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return syncContents() ? ClientboundBlockEntityDataPacket.create(this) : null;
	}

	/**
	 * Marks the block entity as changed and sends the update to clients
	 */
	public void markForUpdate() {
		setChanged();
		if(level != null && !level.isClientSide)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (!remove && side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == Direction.UP)
				return handlers[0].cast();
			else if (side == Direction.DOWN)
				return handlers[1].cast();
			else
				return handlers[2].cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (LazyOptional<?> handler : handlers)
			handler.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
	}

}
