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
package com.shinoow.abyssalcraft.common.blocks.tile;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.common.entity.EntityLesserShoggoth;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Spawns a Lesser Shoggoth every "biomassCooldown" ticks while a player is near, until there are too many around. After
 * five Shoggoths it turns into Monolith Stone.
 */
public class TileEntityShoggothBiomass extends BlockEntity {

	private int cooldown;
	private int spawnedShoggoths;

	public TileEntityShoggothBiomass(BlockPos pos, BlockState state) {
		super(ACBlockEntities.SHOGGOTH_BIOMASS.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		cooldown = nbttagcompound.getInt("Cooldown");
		spawnedShoggoths = nbttagcompound.getInt("SpawnedShoggoths");
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		nbttagcompound.putInt("Cooldown", cooldown);
		nbttagcompound.putInt("SpawnedShoggoths", spawnedShoggoths);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityShoggothBiomass tile) {
		if(level.getDifficulty() != Difficulty.PEACEFUL && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)){
			tile.cooldown++;
			if (tile.cooldown >= ACConfig.biomassCooldown) {
				tile.cooldown = level.random.nextInt(10);
				tile.resetNearbyBiomass(true);
				if(level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), ACConfig.biomassPlayerDistance, false) != null)
					tile.spawnShoggoth();
			}
		}
	}

	/** Spawns a Lesser Shoggoth unless there are too many around already */
	public void spawnShoggoth() {
		if(level.getEntitiesOfClass(EntityLesserShoggoth.class, new AABB(worldPosition).inflate(ACConfig.biomassShoggothDistance)).size() < ACConfig.biomassMaxSpawn){
			EntityLesserShoggoth mob = ACEntityTypes.LESSER_SHOGGOTH.get().create(level);
			setPosition(mob, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
			mob.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(worldPosition), MobSpawnType.SPAWNER, null, null);
			level.addFreshEntity(mob);
			spawnedShoggoths++;
			setChanged();
			if(spawnedShoggoths >= 5)
				level.setBlock(worldPosition, ACBlocks.monolith_stone.get().defaultBlockState(), 2);
		}
	}

	/** Pushes back the cooldown of the surrounding Biomass, so a patch doesn't spawn all its Shoggoths at once */
	public void resetNearbyBiomass(boolean again){
		int x = worldPosition.getX();
		int y = worldPosition.getY();
		int z = worldPosition.getZ();
		BlockPos[] positions = {
				new BlockPos(x - 1, y, z),
				new BlockPos(x, y, z - 1),
				new BlockPos(x - 1, y, z - 1),
				new BlockPos(x + 1, y, z),
				new BlockPos(x, y, z + 1),
				new BlockPos(x + 1, y, z + 1),
				new BlockPos(x - 1, y, z + 1),
				new BlockPos(x + 1, y, z - 1)
		};
		for(BlockPos pos : positions)
			if(level.getBlockEntity(pos) instanceof TileEntityShoggothBiomass tile) {
				tile.setCooldown(level.random.nextInt(30));
				if(again)
					tile.resetNearbyBiomass(false);
			}
	}

	/** Finds room for the new Shoggoth: on top, next to the Biomass, a bit further away, or high up with Resistance */
	private void setPosition(Mob entity, int x, int y, int z){
		if(level.isEmptyBlock(worldPosition.above()) && level.isEmptyBlock(worldPosition.above(2))){
			entity.moveTo(x, y + 1, z, entity.getYRot(), entity.getXRot());
			return;
		}
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!level.getBlockState(new BlockPos(x + i, y + 1, z + j)).isSolid()) {
					entity.moveTo(x + i, y + 1, z + j, entity.getYRot(), entity.getXRot());
					return;
				}
		for(int i = -4; i < 5; i+=4)
			for(int j = -4; j < 5; j+=4)
				if(!level.getBlockState(new BlockPos(x + i, y + 1, z + j)).isSolid()) {
					entity.moveTo(x + i, y + 1, z + j, entity.getYRot(), entity.getXRot());
					return;
				}
		if(!level.getBlockState(new BlockPos(x, y + 2, z)).isSolid()) {
			entity.moveTo(x, y + 2, z, entity.getYRot(), entity.getXRot());
			return;
		}
		entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 100));
		if(level.getBlockState(new BlockPos(x, y + 15, z)).isSolid())
			entity.moveTo(x, y + 20, z, entity.getYRot(), entity.getXRot());
		else
			entity.moveTo(x, y + 15, z, entity.getYRot(), entity.getXRot());
	}

	public int getCooldown(){
		return cooldown;
	}

	public void setCooldown(int cd){
		cooldown = cd;
		setChanged();
	}

	public int getSpawnedShoggoths() {
		return spawnedShoggoths;
	}
}
