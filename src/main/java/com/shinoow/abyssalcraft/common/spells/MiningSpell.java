/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.spell.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Mining: bores a 3x3 tunnel up to 128 blocks into the looked at face, worth 500 hardness. Stone, dirt, gravel and
 * sandstone become Solid Lava (which melts away), water is removed, Coralium Stone turns into Liquid Coralium, and the
 * ring around the tunnel is smelted (cobblestone -> stone, sand -> glass). As in 1.12.2, smeltable blocks inside the
 * tunnel are broken without drops.
 */
public class MiningSpell extends Spell {

	public MiningSpell() {
		super("mining", 500F, Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE);
		setRequiresCharging();
		setColor(0xc45b05);
	}

	@Nullable
	private static BlockState remains(BlockState state) {
		if(state.is(Blocks.STONE) || state.is(Blocks.DIRT) || state.is(Blocks.GRAVEL) || state.is(Blocks.SANDSTONE) || state.is(ACBlocks.dreadlands_dirt.get())
				|| state.is(ACBlocks.darkstone.get()) || state.is(ACBlocks.abyssal_stone.get()) || state.is(ACBlocks.dreadstone.get())
				|| state.is(ACBlocks.abyssalnite_stone.get()) || state.is(ACBlocks.omothol_stone.get()) || state.is(ACBlocks.monolith_stone.get()))
			return ACBlocks.solid_lava.get().defaultBlockState();
		if(state.getFluidState().is(FluidTags.WATER) && state.getBlock() instanceof LiquidBlock)
			return Blocks.AIR.defaultBlockState();
		if(state.is(ACBlocks.coralium_stone.get()))
			return ACBlocks.liquid_coralium.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 7);
		return null;
	}

	@Nullable
	private static BlockState smelted(BlockState state) {
		if(state.is(Blocks.COBBLESTONE)) return Blocks.STONE.defaultBlockState();
		if(state.is(ACBlocks.darkstone_cobblestone.get())) return ACBlocks.darkstone.get().defaultBlockState();
		if(state.is(ACBlocks.abyssal_cobblestone.get())) return ACBlocks.abyssal_stone.get().defaultBlockState();
		if(state.is(ACBlocks.dreadstone_cobblestone.get())) return ACBlocks.dreadstone.get().defaultBlockState();
		if(state.is(ACBlocks.abyssalnite_cobblestone.get())) return ACBlocks.abyssalnite_stone.get().defaultBlockState();
		if(state.is(ACBlocks.coralium_cobblestone.get())) return ACBlocks.coralium_stone.get().defaultBlockState();
		if(state.is(Blocks.SAND)) return Blocks.GLASS.defaultBlockState();
		if(state.is(Blocks.RED_SAND)) return Blocks.RED_STAINED_GLASS.defaultBlockState();
		if(state.is(ACBlocks.abyssal_sand.get())) return ACBlocks.abyssal_sand_glass.get().defaultBlockState();
		return null;
	}

	/** The (2 * radius + 1)^2 square around pos, across the facing axis */
	private static Set<BlockPos> square(BlockPos pos, Direction facing, int radius) {
		Set<BlockPos> set = new HashSet<>();
		Direction.Axis axis = facing.getAxis();
		for(int i = -radius; i <= radius; i++)
			for(int j = -radius; j <= radius; j++)
				set.add(axis == Direction.Axis.Y ? pos.offset(i, 0, j) : pos.offset(axis == Direction.Axis.Z ? i : 0, j, axis == Direction.Axis.X ? i : 0));
		return set;
	}

	private static float cost(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.getDestroySpeed(world, pos) * (state.liquid() ? 0.5F : 2F);
	}

	@Nullable
	private static BlockHitResult target(Player player) {
		var eye = player.getEyePosition();
		BlockHitResult hit = player.level().clip(new ClipContext(eye, eye.add(player.getLookAngle().scale(16)), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
		return hit.getType() == BlockHitResult.Type.BLOCK ? hit : null;
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		BlockHitResult hit = target(player);
		if(hit != null)
			for(BlockPos p : square(hit.getBlockPos(), hit.getDirection().getOpposite(), 1))
				if(remains(world.getBlockState(p)) != null && world.mayInteract(player, p))
					return true;
		return false;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		BlockHitResult hit = target(player);
		if(hit == null) return;
		Direction facing = hit.getDirection().getOpposite();
		float spent = 0;
		for(int i = 0; spent < 500 && i <= 128; i++) {
			BlockPos center = hit.getBlockPos().relative(facing, i);
			Set<BlockPos> inner = square(center, facing, 1);
			for(BlockPos p : inner) {
				if(spent >= 500) break;
				if(!world.mayInteract(player, p)) continue;
				BlockState state = remains(world.getBlockState(p));
				if(state != null) {
					spent += cost(world, p);
					if(i == 0)
						world.destroyBlock(p, false);
					world.setBlockAndUpdate(p, state);
					if(state.is(ACBlocks.solid_lava.get()))
						state.getBlock().setPlacedBy(world, p, state, player, ItemStack.EMPTY);
				} else if(smelted(world.getBlockState(p)) != null) {
					spent += cost(world, p);
					world.destroyBlock(p, false);
				}
			}
			Set<BlockPos> outer = square(center, facing, 2);
			outer.removeAll(inner);
			for(BlockPos p : outer) {
				if(spent >= 500) break;
				BlockState state = smelted(world.getBlockState(p));
				if(state != null && world.mayInteract(player, p)) {
					spent += cost(world, p);
					if(i == 0)
						world.destroyBlock(p, false);
					world.setBlockAndUpdate(p, state);
				}
			}
		}
	}
}
