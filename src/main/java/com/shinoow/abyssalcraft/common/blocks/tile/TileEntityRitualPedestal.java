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
 *     1.20.1 port - the consumed offering is kept as an ItemStack (was item id + metadata) for the particles;
 *     particles are spawned by a client ticker
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.block.IRitualPedestal;
import com.shinoow.abyssalcraft.client.ClientPacketHandler;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACParticles;
import com.shinoow.abyssalcraft.lib.util.ParticleUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ritual Pedestal: holds an offering for the ritual altar it belongs to
 */
public class TileEntityRitualPedestal extends BlockEntity implements IRitualPedestal {

	private ItemStack item = ItemStack.EMPTY;
	/** The offering consumed by the current ritual (for the particles) */
	private ItemStack consumed = ItemStack.EMPTY;
	@Nullable
	private BlockPos altarPos;

	public TileEntityRitualPedestal(BlockPos pos, BlockState state) {
		super(ACBlockEntities.RITUAL_PEDESTAL.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		item = ItemStack.of(nbttagcompound.getCompound("Item"));
		altarPos = nbttagcompound.contains("AltarPos") ? BlockPos.of(nbttagcompound.getLong("AltarPos")) : null;
		consumed = ItemStack.of(nbttagcompound.getCompound("Consumed"));
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		CompoundTag nbtItem = new CompoundTag();
		if(!item.isEmpty())
			item.save(nbtItem);
		nbttagcompound.put("Item", nbtItem);
		if(altarPos != null)
			nbttagcompound.putLong("AltarPos", altarPos.asLong());
		CompoundTag nbtConsumed = new CompoundTag();
		if(!consumed.isEmpty())
			consumed.save(nbtConsumed);
		nbttagcompound.put("Consumed", nbtConsumed);
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

	private void markForUpdate() {
		setChanged();
		if(level != null && !level.isClientSide)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		IRitualAltar altar = getAltar();
		if(altar != null)
			altar.addPedestal(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, TileEntityRitualPedestal tile) {
		IRitualAltar altar = tile.getAltar();
		if(altar != null && altar.isPerformingRitual() && !tile.consumed.isEmpty()) {
			double xOffset = pos.getX() - tile.altarPos.getX();
			double zOffset = pos.getZ() - tile.altarPos.getZ();
			double velX = xOffset == 0 ? 0 : xOffset < 0 ? 0.5D : -0.5D;
			double velZ = zOffset == 0 ? 0 : zOffset < 0 ? 0.5D : -0.5D;
			tile.spawnParticles(0.5D, 0.5D, velX, velZ, altar);
		}
	}

	private void spawnParticles(double xOffset, double zOffset, double velX, double velZ, IRitualAltar altar) {
		ItemParticleOption item = new ItemParticleOption(ParticleTypes.ITEM, consumed);
		BlockPos pos = worldPosition;
		switch(altar.getRitualParticle()) {
		case GLYPHS:
			int x = velX != 0 && velZ != 0 ? 4 : 6;
			int z = velZ != 0 && velX != 0 ? 4 : 6;
			level.addParticle(ParticleTypes.ENCHANT, altarPos.getX() + xOffset, altarPos.getY() + 2.05, altarPos.getZ() + zOffset, velX*x,.15,velZ*z);
			level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 2.05, pos.getZ() + 0.5, 0, .15, 0);
			break;
		case ITEM:
			level.addParticle(item, pos.getX() + xOffset, pos.getY() + 0.95, pos.getZ() + zOffset, velX *0.2,.15,velZ*0.2);
			break;
		case ITEM_SMOKE_COMBO:
			if(level.random.nextBoolean())
				level.addParticle(item, pos.getX() + xOffset, pos.getY() + 0.95, pos.getZ() + zOffset, velX *0.2,.15,velZ*0.2);
			else level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, velX/2,0,velZ/2);
			break;
		case NONE:
			break;
		case SMOKE:
			level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, velX/2,0,velZ/2);
			break;
		case SMOKE_PILLARS:
			level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + xOffset, pos.getY() + 1, pos.getZ() + zOffset, 0,.15,0);
			break;
		case SPRINKLER:
			int t = altar.getRitualCooldown();
			while(t > 17)
				t -= 17;
			double v1 = 0.0;
			double v2 = 0.0;
			if(t % 2 == 0) {
				v1 = 0;
				v2 = 0.5;
			}
			if(t % 4 == 0) {
				v1 = 0.5;
				v2 = 0.5;
			}
			if(t % 6 == 0) {
				v1 = 0.5;
				v2 = 0;
			}
			if(t % 8 == 0) {
				v1 = 0.5;
				v2 = -0.5;
			}
			if(t % 10 == 0) {
				v1 = 0;
				v2 = -0.5;
			}
			if(t % 12 == 0) {
				v1 = -0.5;
				v2 = -0.5;
			}
			if(t % 14 == 0) {
				v1 = -0.5;
				v2 = 0;
			}
			if(t % 16 == 0) {
				v1 = -0.5;
				v2 = 0.5;
			}
			double v12 = v1 > 0 ? -0.1 : v1 < 0 ? 0.1 : 0;
			double v22 = v2 > 0 ? -0.1 : v2 < 0 ? 0.1 : 0;
			level.addParticle(item, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, v1,.15,v2);
			level.addParticle(item, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, v1+v12,.15,v2+v22);
			level.addParticle(item, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, v1+v12*2,.15,v2+v22*2);
			break;
		case PE_STREAM:
			if(altar.getRitualCooldown() % 20 == 0) {
				int j = ClientPacketHandler.particleIncrements();
				ParticleUtil.spawnParticleLine(pos, altarPos, 15, j, (vec1, vec2) -> {
					level.addParticle(ACParticles.PE_STREAM.get(), vec2.x, vec2.y, vec2.z, vec1.x * .1, .15, vec1.z * .1);
					return false;
				});
			}
			break;
		default:
			break;
		}
		level.addParticle(ACParticles.BLUE_FLAME.get(), pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, 0,0,0);
		level.addParticle(ParticleTypes.SMOKE, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, 0,0,0);
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

	@Nullable
	@Override
	public IRitualAltar getAltar() {
		return altarPos != null && level != null && level.getBlockEntity(altarPos) instanceof IRitualAltar altar ? altar : null;
	}

	@Override
	public void setAltar(BlockPos pos) {
		altarPos = pos;
		setChanged();
		IRitualAltar altar = getAltar();
		if(altar != null)
			altar.addPedestal(this);
	}

	/** Takes the offering, leaving its container item behind (e.g. the bucket of a bucket of milk) */
	@Override
	public void consumeItem() {
		if(!item.isEmpty()) {
			consumed = item.copyWithCount(1);
			setItem(item.hasCraftingRemainingItem() ? item.getCraftingRemainingItem() : ItemStack.EMPTY);
		} else {
			consumed = ItemStack.EMPTY;
			markForUpdate();
		}
	}
}
