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
 *     1.20.1 port - built on the snow layer block (same shapes and layer stacking as the 1.12.2 ItemShoggothOoze)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Ooze left behind by Lesser Shoggoths. Slows down everything else, wears down leggings and boots and slowly
 * turns back into the dimension's ground in bright light (if enabled).
 */
public class BlockShoggothOoze extends SnowLayerBlock {

	private static final ResourceLocation LESSER_SHOGGOTH = new ResourceLocation(AbyssalCraft.modid, ACEntities.lesser_shoggoth);

	public BlockShoggothOoze(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return ACConfig.oozeExpire;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if(!LESSER_SHOGGOTH.equals(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()))){
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.4D, 1.0D, 0.4D));
			if(entity instanceof LivingEntity e && !world.isClientSide){
				ItemStack legs = e.getItemBySlot(EquipmentSlot.LEGS);
				ItemStack feet = e.getItemBySlot(EquipmentSlot.FEET);
				if(e.tickCount % 60 == 0 && !legs.isEmpty() && !legs.is(ACItems.ethaxium_leggings.get()))
					legs.hurtAndBreak(1, e, p -> p.broadcastBreakEvent(EquipmentSlot.LEGS));
				if(e.tickCount % 40 == 0 && !feet.isEmpty() && !feet.is(ACItems.ethaxium_boots.get()))
					feet.hurtAndBreak(1, e, p -> p.broadcastBreakEvent(EquipmentSlot.FEET));
			}
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if(ACConfig.oozeExpire && random.nextInt(10) == 0 && world.getMaxLocalRawBrightness(pos.above()) >= 13)
			if(state.getValue(LAYERS) == 8)
				world.setBlockAndUpdate(pos, getGround(world));
			else if(state.getValue(LAYERS) > 1)
				world.setBlockAndUpdate(pos, state.setValue(LAYERS, state.getValue(LAYERS) - 1));
			else world.removeBlock(pos, false);
	}

	/** What a full block of ooze turns into */
	private static BlockState getGround(Level world){
		if(world.dimension() == ACDimensions.ABYSSAL_WASTELAND)
			return ACBlocks.abyssal_sand.get().defaultBlockState();
		if(world.dimension() == ACDimensions.DREADLANDS)
			return ACBlocks.dreadlands_dirt.get().defaultBlockState();
		if(world.dimension() == ACDimensions.OMOTHOL)
			return ACBlocks.omothol_stone.get().defaultBlockState();
		if(world.dimension() == ACDimensions.DARK_REALM)
			return ACBlocks.darkstone.get().defaultBlockState();
		if(world.dimension() == Level.NETHER)
			return Blocks.NETHERRACK.defaultBlockState();
		if(world.dimension() == Level.END)
			return Blocks.END_STONE.defaultBlockState();
		return Blocks.DIRT.defaultBlockState();
	}

	/** Full blocks of ooze stay when their support is removed */
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if(state.getValue(LAYERS) == 8)
			return state;
		return super.updateShape(state, direction, neighbor, world, pos, neighborPos);
	}

	/** Not on Shoggoth Biomass or Monolith Stone; on leaves, full ooze and full solid blocks */
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState below = world.getBlockState(pos.below());
		if(below.is(ACBlocks.shoggoth_biomass.orElse(Blocks.AIR)) || below.is(ACBlocks.monolith_stone.get()))
			return false;
		if(below.is(BlockTags.LEAVES))
			return true;
		if(below.is(this))
			return below.getValue(LAYERS) == 8;
		return below.isFaceSturdy(world, pos.below(), Direction.UP) && below.blocksMotion();
	}
}
