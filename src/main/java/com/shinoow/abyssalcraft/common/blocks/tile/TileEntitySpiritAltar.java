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
 *     1.20.1 port - inventories are found again when the altar loads (positions only, block entities are looked up
 *     when they're used so unloaded/replaced ones are never touched)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.transfer.ItemTransferConfiguration;
import com.shinoow.abyssalcraft.api.transfer.caps.IItemTransferCapability;
import com.shinoow.abyssalcraft.api.transfer.caps.ItemTransferCapability;
import com.shinoow.abyssalcraft.common.entity.EntitySpiritItem;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.DisplayRoutesMessage;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.lib.util.SpiritItemUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Spirit Altar: runs the item transfer routes configured (with the Spirit Tablet/Configurator) on the inventories
 * within 16 blocks, sending one item per route every second as a spirit item
 */
public class TileEntitySpiritAltar extends BlockEntity {

	private final Set<BlockPos> positions = new LinkedHashSet<>();
	private boolean enabled;
	private boolean located;

	public TileEntitySpiritAltar(BlockPos pos, BlockState state) {
		super(ACBlockEntities.SPIRIT_ALTAR.get(), pos, state);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		enabled = tag.getBoolean("Enabled");
	}

	@Override
	protected void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.putBoolean("Enabled", enabled);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** Controls the particles client side */
	public boolean isEnabled() {
		return enabled;
	}

	private List<BlockEntity> tiles() {
		List<BlockEntity> list = new ArrayList<>();
		for(BlockPos p : positions)
			if(level.isLoaded(p)) {
				BlockEntity te = level.getBlockEntity(p);
				if(te != null)
					list.add(te);
			}
		return list;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntitySpiritAltar tile) {
		if(!tile.located) {
			tile.calculatePositions();
			tile.located = true;
		}
		if(level.getGameTime() % 400 == 0)
			tile.positions.removeIf(p -> level.isLoaded(p) && !SpiritItemUtil.validPos(p, level));
		if(level.getGameTime() % 20 != 0) return;

		for(BlockEntity te : tile.tiles()) {
			IItemTransferCapability cap = ItemTransferCapability.getCap(te);
			if(cap == null || !cap.isRunning()) continue;
			for(ItemTransferConfiguration cfg : cap.getTransferConfigurations()) {
				IItemHandler inventory = SpiritItemUtil.getInventory(te, cfg.getExitFacing());
				if(inventory == null) continue;
				boolean hasFilter = cfg.getFilter() != null && cfg.getFilter().stream().anyMatch(i -> !i.isEmpty());
				ItemStack stack = ItemStack.EMPTY;
				int slot = -1;
				for(int i = 0; i < inventory.getSlots(); i++) {
					ItemStack s = inventory.getStackInSlot(i);
					if(!s.isEmpty() && (!hasFilter || isInFilter(cfg.getFilter(), s, cfg.filterByNBT()))) {
						stack = inventory.extractItem(i, 1, true);
						slot = i;
						break;
					}
				}
				if(stack.isEmpty() || slot < 0 || cfg.getRoute().length == 0) continue;
				BlockPos exitPos = cfg.getRoute()[cfg.getRoute().length-1];
				if(!level.isLoaded(exitPos)) continue;
				BlockEntity target = level.getBlockEntity(exitPos);
				IItemHandler exitInv = target != null ? SpiritItemUtil.getInventory(target, cfg.getEntryFacing()) : null;
				if(exitInv != null && ItemHandlerHelper.insertItem(exitInv, stack, true).isEmpty()) {//insertion would work
					stack = inventory.extractItem(slot, 1, false);
					BlockPos from = te.getBlockPos();
					EntitySpiritItem spirit = new EntitySpiritItem(level, from.getX() + 0.5D, from.getY(), from.getZ() + 0.5D, stack.copy());
					spirit.setRoute(cfg.getRoute());
					spirit.setFacing(cfg.getEntryFacing());
					level.addFreshEntity(spirit);
				}
			}
		}
	}

	private void calculatePositions() {
		positions.clear();
		BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();
		for(int x = worldPosition.getX() - 16; x < worldPosition.getX() + 16; x++)
			for(int y = worldPosition.getY() - 16; y < worldPosition.getY() + 16; y++)
				for(int z = worldPosition.getZ() - 16; z < worldPosition.getZ() + 16; z++) {
					pos1.set(x, y, z);
					if(!level.isLoaded(pos1)) continue;
					BlockEntity te = level.getBlockEntity(pos1);
					if(te != null && te != this && SpiritItemUtil.hasInventory(te))
						positions.add(pos1.immutable());
				}
	}

	/**
	 * Configurator used on the altar
	 * @param mode 0 = find the inventories nearby, 1 = toggle the transportation, 2 = clear the routes
	 * (sneaking shows the routes instead)
	 */
	public void spiritTabletToggle(Player player, int mode) {
		if(!(level instanceof ServerLevel serverLevel)) return;
		if(player.isShiftKeyDown()) { // show connected routes
			List<BlockPos[]> routes = new ArrayList<>();
			for(BlockEntity tile : tiles()) {
				IItemTransferCapability cap = ItemTransferCapability.getCap(tile);
				if(cap == null) continue;
				for(ItemTransferConfiguration cfg : cap.getTransferConfigurations()) {
					BlockPos[] route = new BlockPos[cfg.getRoute().length + 1];
					route[0] = tile.getBlockPos();
					System.arraycopy(cfg.getRoute(), 0, route, 1, cfg.getRoute().length);
					routes.add(route);
				}
			}
			if(player instanceof ServerPlayer serverPlayer)
				PacketDispatcher.sendTo(new DisplayRoutesMessage(routes), serverPlayer);
			player.displayClientMessage(Component.translatable("message.spiritaltar.1"), true);
		}
		else if(mode == 0) { // find the inventories nearby
			calculatePositions();
			player.displayClientMessage(Component.translatable("message.spiritaltar.2"), true);
		}
		else if(mode == 1) { // toggle transportation of connected routes
			enabled = !enabled;
			for(BlockEntity tile : tiles()) {
				IItemTransferCapability cap = ItemTransferCapability.getCap(tile);
				if(cap != null) {
					cap.setRunning(enabled);
					tile.setChanged();
				}
			}
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			serverLevel.sendParticles(enabled ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.ANGRY_VILLAGER, worldPosition.getX() + 0.5, worldPosition.getY() + 1,
					worldPosition.getZ() + 0.5, 1, 0, 0, 0, 1.0);
			player.displayClientMessage(Component.translatable(enabled ? "message.spiritaltar.3" : "message.spiritaltar.4"), true);
		}
		else if(mode == 2) { // clear connected routes
			for(BlockEntity tile : tiles()) {
				IItemTransferCapability cap = ItemTransferCapability.getCap(tile);
				if(cap != null) {
					cap.clearConfigurations();
					tile.setChanged();
				}
			}
			player.displayClientMessage(Component.translatable("message.spiritaltar.5"), true);
		}
	}

	private static boolean isInFilter(NonNullList<ItemStack> filter, ItemStack stack, boolean nbt) {
		for(ItemStack stack1 : filter)
			if(APIUtils.areStacksEqual(stack, stack1, nbt))
				return true;
		return false;
	}
}
