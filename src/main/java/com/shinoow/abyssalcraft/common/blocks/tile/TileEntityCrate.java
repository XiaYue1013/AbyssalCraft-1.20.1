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
 *     1.20.1 port - loot tables like the 1.12.2 TileEntityLockableLoot (filled on first access)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.init.ACBlockEntities;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/**
 * Wooden Crate: 36 slot storage (opens the vanilla 9x4 chest screen, like 1.12.2 displayGUIChest)
 */
public class TileEntityCrate extends TileEntityACInventory {

	private static final int[] SLOTS = java.util.stream.IntStream.range(0, 36).toArray();

	@Nullable
	private ResourceLocation lootTable;
	private long lootTableSeed;

	public TileEntityCrate(BlockPos pos, BlockState state) {
		super(ACBlockEntities.CRATE.get(), pos, state, 36, "container.abyssalcraft.crate");
	}

	/** The crate is filled from the loot table the first time it's opened or accessed */
	public void setLootTable(ResourceLocation lootTable, long seed) {
		this.lootTable = lootTable;
		lootTableSeed = seed;
		setChanged();
	}

	private void unpackLootTable(@Nullable Player player) {
		if(lootTable != null && level instanceof ServerLevel serverLevel) {
			LootTable table = serverLevel.getServer().getLootData().getLootTable(lootTable);
			if(player instanceof ServerPlayer serverPlayer)
				CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, lootTable);
			lootTable = null;
			LootParams.Builder params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition));
			if(player != null)
				params.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
			table.fill(this, params.create(LootContextParamSets.CHEST), lootTableSeed);
			setChanged();
		}
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		unpackLootTable(inventory.player);
		return new ChestMenu(MenuType.GENERIC_9x4, id, inventory, this, 4);
	}

	@Override
	public boolean isEmpty() {
		unpackLootTable(null);
		return super.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		unpackLootTable(null);
		return super.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		unpackLootTable(null);
		return super.removeItem(slot, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		unpackLootTable(null);
		return super.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		unpackLootTable(null);
		super.setItem(slot, stack);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if(tag.contains("LootTable", 8)) {
			lootTable = new ResourceLocation(tag.getString("LootTable"));
			lootTableSeed = tag.getLong("LootTableSeed");
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if(lootTable != null) {
			tag.putString("LootTable", lootTable.toString());
			if(lootTableSeed != 0L)
				tag.putLong("LootTableSeed", lootTableSeed);
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}
}
