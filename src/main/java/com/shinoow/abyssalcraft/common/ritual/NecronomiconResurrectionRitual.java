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
 *     1.20.1 port - dread spawn replacements looked up by entity id
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.ritual;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.world.data.NecromancyWorldSavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Brings back a named mob that died: a Name Tag with its name goes on the altar, the pedestals hold crystal shards,
 * crystals or clusters depending on the mob's size. Mobs that have been revived too often come back as dread spawns.
 */
public class NecronomiconResurrectionRitual extends NecronomiconRitual {

	public NecronomiconResurrectionRitual() {
		super("resurrection", 2, null, 1000F, true, new Object[]{element(3), element(5), element(6), element(4), element(7), element(2)});
		sacrifice = Items.NAME_TAG;
		setRitualParticle(EnumRitualParticle.SMOKE_PILLARS);
	}

	/** Shard, crystal and cluster of a crystal (index = needed size) */
	private static ItemStack[] element(int crystal) {
		return new ItemStack[] {new ItemStack(ACItems.crystalShard(crystal).get()), new ItemStack(ACItems.crystal(crystal).get()),
				new ItemStack(ACBlocks.crystalCluster(crystal).get())};
	}

	@Override
	public boolean requiresItemSacrifice(){
		return true;
	}

	private boolean mismatch(ItemStack stack, Object obj, int size){
		if(!stack.isEmpty() && obj instanceof ItemStack[] options)
			return !stack.is(options[size].getItem());
		return false;
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		if(world.getServer() != null && world.getBlockEntity(pos) instanceof IRitualAltar altar && altar.getItem().is(Items.NAME_TAG)){
			CompoundTag data = NecromancyWorldSavedData.get(world.getServer()).getDataForName(altar.getItem().getHoverName().getString());
			return data != null && altar.getPedestals().stream().noneMatch(p -> mismatch(p.getItem(), getOfferings()[0], data.getInt("ResurrectionRitualCrystalSize")));
		}
		return false;
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		if(world instanceof ServerLevel level && world.getBlockEntity(pos) instanceof IRitualAltar altar && altar.getItem().is(Items.NAME_TAG)){
			String name = altar.getItem().getHoverName().getString();
			NecromancyWorldSavedData cap = NecromancyWorldSavedData.get(level.getServer());
			CompoundTag data = cap.getDataForName(name);
			if(data == null) return;
			Entity e = EntityType.loadEntityRecursive(data.copy(), level, entity -> entity);
			if(e instanceof Mob mob){
				Mob entity = getEntity(mob, level, data.getInt("ResurrectionRitualCrystalSize"));
				LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
				if(bolt != null) {
					bolt.moveTo(pos.getX(), pos.getY() + 2, pos.getZ());
					bolt.setVisualOnly(true);
					level.addFreshEntity(bolt);
				}
				entity.moveTo(pos.getX(), pos.getY() + 1, pos.getZ(), entity.getYRot(), entity.getXRot());
				entity.setHealth(entity.getMaxHealth());
				entity.deathTime = 0;
				level.addFreshEntity(entity);
				cap.clearEntry(name);
				altar.setItem(ItemStack.EMPTY);
			}
		}
	}

	/** Reanimating something too often turns it into a dread spawn */
	private Mob getEntity(Mob e, ServerLevel level, int size){
		int i = e.getPersistentData().getInt("Reanimations");
		if(i > 3){
			boolean b = switch(i) {
			case 4 -> level.random.nextFloat() < 0.9;
			case 5 -> level.random.nextFloat() < 0.75;
			case 6 -> level.random.nextFloat() < 0.6;
			case 7 -> level.random.nextFloat() < 0.45;
			case 8 -> level.random.nextFloat() < 0.3;
			case 9 -> level.random.nextFloat() < 0.15;
			default -> true;
			};
			if(b){
				String id = size == 1 ? ACEntities.greater_dread_spawn : size == 2 ? ACEntities.lesser_dreadbeast : ACEntities.dread_spawn;
				ResourceLocation key = new ResourceLocation(AbyssalCraft.modid, id);
				if(ForgeRegistries.ENTITY_TYPES.containsKey(key) && ForgeRegistries.ENTITY_TYPES.getValue(key).create(level) instanceof Mob dread) {
					dread.setCustomName(e.getCustomName());
					return dread;
				}
			}
		}
		e.getPersistentData().putInt("Reanimations", i + 1);
		return e;
	}
}
