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
 *     1.20.1 port - OreDictionary block lists replaced by tags, displacement driven by fluid ticks
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import java.util.function.Supplier;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.common.Tags;

/**
 * Liquid Coralium. Infects living things with the Coralium Plague, converts other liquids into itself and
 * transmutes stone and ores into their Abyssal Wasteland counterparts (outside the Abyssal Wasteland only if the
 * "liquidCoraliumTransmutation" config is enabled). Can't pollute oceans unless "oceanicCoraliumPollution" is enabled.
 */
public class BlockCLiquid extends LiquidBlock {

	private static final TagKey<Block> DUSTS_SALTPETER = forge("ores/saltpeter");

	public BlockCLiquid(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		super(fluid, properties);
	}

	private static TagKey<Block> forge(String path) {
		return BlockTags.create(new ResourceLocation("forge", path));
	}

	private static boolean isAny(BlockState state, String... ores) {
		for(String ore : ores)
			if(state.is(forge("ores/" + ore)))
				return true;
		return false;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if(!world.isClientSide && world.getBiome(pos).is(BiomeTags.IS_OCEAN) && !ACConfig.destroyOcean) {
			world.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
			return;
		}
		super.onPlace(state, world, pos, oldState, isMoving);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		super.entityInside(state, level, pos, entity);
		if(!level.isClientSide && entity instanceof LivingEntity living && !EntityUtil.isEntityCoralium(living)
				&& !living.hasEffect(AbyssalCraftAPI.coralium_plague.get()))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.coralium_plague.get(), 200));
	}

	/**
	 * Called from the fluid tick: converts/transmutes the blocks the liquid flows against (1.12.2 displaceIfPossible)
	 */
	public static void displaceNeighbours(Level world, BlockPos pos) {
		if(world.isClientSide) return;
		if(!ACConfig.shouldSpread && world.dimension() != ACDimensions.ABYSSAL_WASTELAND) return;
		for(Direction dir : new Direction[] {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
			BlockPos target = pos.relative(dir);
			if(!world.isLoaded(target)) continue;
			displace(world, target);
			if(ACConfig.breakLogic) {
				BlockState above = world.getBlockState(target.above());
				if(!above.getFluidState().isEmpty() && !isCoralium(above) && !above.is(ACBlocks.liquid_antimatter.get()))
					world.setBlockAndUpdate(target.above(), ACBlocks.liquid_coralium.get().defaultBlockState());
			}
		}
	}

	private static boolean isCoralium(BlockState state) {
		return state.is(ACBlocks.liquid_coralium.get());
	}

	private static boolean isBlacklisted(BlockState state) {
		return state.is(ACBlocks.abyssal_cobblestone.get()) || state.is(ACBlocks.abyssal_stone.get()) || state.is(ACBlocks.abyssal_coralium_ore.get())
				|| state.is(ACBlocks.abyssal_diamond_ore.get()) || state.is(ACBlocks.abyssal_gold_ore.get()) || state.is(ACBlocks.abyssal_iron_ore.get())
				|| state.is(ACBlocks.abyssal_nitre_ore.get()) || state.is(ACBlocks.abyssal_stone_bricks.get()) || state.is(ACBlocks.coralium_stone.get())
				|| state.is(ACBlocks.liquified_coralium_ore.get()) || state.is(ACBlocks.abyssal_tin_ore.get()) || state.is(ACBlocks.abyssal_copper_ore.get())
				|| state.is(ACBlocks.pearlescent_coralium_ore.get()) || state.is(BlockTags.WITHER_IMMUNE)
				|| state.is(ACBlocks.omothol_stone.get()) || state.is(ACBlocks.monolith_stone.get());
	}

	private static boolean isDust(BlockState state) {
		return isAny(state, "saltpeter", "sulfur", "lapis", "redstone", "coal", "coralium");
	}

	private static boolean isMetalloid(BlockState state) {
		return isAny(state, "iron", "gold", "abyssalnite", "copper", "tin", "aluminum", "brass", "silver", "zinc")
				|| state.is(ACBlocks.dreaded_abyssalnite_ore.get());
	}

	private static boolean isGem(BlockState state) {
		return isAny(state, "diamond", "emerald", "quartz", "sapphire", "ruby") || state.is(ACBlocks.coralium_infused_stone.get());
	}

	private static boolean isStone(BlockState state) {
		return state.is(Tags.Blocks.STONE) || state.is(Tags.Blocks.SANDSTONE) || state.is(Blocks.NETHERRACK) || state.is(Blocks.END_STONE);
	}

	private static boolean isBrick(BlockState state) {
		return state.is(Blocks.STONE_BRICKS) || state.is(Blocks.MOSSY_STONE_BRICKS) || state.is(Blocks.CRACKED_STONE_BRICKS)
				|| state.is(Blocks.CHISELED_STONE_BRICKS) || state.is(Blocks.NETHER_BRICKS) || state.is(Blocks.END_STONE_BRICKS)
				|| state.is(Blocks.RED_NETHER_BRICKS) || state.is(ACBlocks.darkstone_bricks.get()) || state.is(ACBlocks.chiseled_darkstone_bricks.get())
				|| state.is(ACBlocks.cracked_darkstone_bricks.get()) || state.is(ACBlocks.abyssalnite_stone_bricks.get())
				|| state.is(ACBlocks.chiseled_abyssalnite_stone_bricks.get()) || state.is(ACBlocks.cracked_abyssalnite_stone_bricks.get())
				|| state.is(ACBlocks.dreadstone_bricks.get()) || state.is(ACBlocks.chiseled_dreadstone_bricks.get())
				|| state.is(ACBlocks.cracked_dreadstone_bricks.get());
	}

	private static boolean isCobble(BlockState state) {
		return state.is(Tags.Blocks.COBBLESTONE) || state.is(Blocks.MOSSY_COBBLESTONE);
	}

	private static void displace(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if(state.isAir() || isCoralium(state) || isBlacklisted(state)) return;
		boolean ocean = world.getBiome(pos).is(BiomeTags.IS_OCEAN) && !ACConfig.destroyOcean;

		if(!state.getFluidState().isEmpty() && state.getBlock() instanceof LiquidBlock && !state.is(ACBlocks.liquid_antimatter.get())) {
			if(!ocean)
				world.setBlockAndUpdate(pos, ACBlocks.liquid_coralium.get().defaultBlockState());
			return;
		}

		BlockState result = null;
		if(isDust(state))
			result = (state.is(DUSTS_SALTPETER) ? ACBlocks.abyssal_nitre_ore : ACBlocks.abyssal_coralium_ore).get().defaultBlockState();
		else if(isMetalloid(state)) {
			if(isAny(state, "iron")) result = ACBlocks.abyssal_iron_ore.get().defaultBlockState();
			else if(isAny(state, "gold")) result = ACBlocks.abyssal_gold_ore.get().defaultBlockState();
			else if(isAny(state, "tin")) result = ACBlocks.abyssal_tin_ore.get().defaultBlockState();
			else if(isAny(state, "copper")) result = ACBlocks.abyssal_copper_ore.get().defaultBlockState();
			else result = ACBlocks.liquified_coralium_ore.get().defaultBlockState();
		} else if(isGem(state))
			result = (isAny(state, "diamond") ? ACBlocks.abyssal_diamond_ore : ACBlocks.pearlescent_coralium_ore).get().defaultBlockState();
		else if(isStone(state))
			result = ACBlocks.abyssal_stone.get().defaultBlockState();
		else if(isBrick(state))
			result = ACBlocks.abyssal_stone_bricks.get().defaultBlockState();
		else if(isCobble(state) && !(ocean && state.is(Blocks.COBBLESTONE)))
			result = ACBlocks.abyssal_cobblestone.get().defaultBlockState();

		if(result != null)
			world.setBlockAndUpdate(pos, result);
	}
}
