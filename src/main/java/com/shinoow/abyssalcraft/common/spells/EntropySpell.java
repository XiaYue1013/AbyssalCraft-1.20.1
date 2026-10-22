/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import com.shinoow.abyssalcraft.lib.util.TargetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Entropy: wears the looked at block down a step (stone -> cobblestone -> gravel -> sand -> nothing) for 5 - 9 PE */
public class EntropySpell extends Spell {

	public EntropySpell() {
		super("entropy", 0, Items.COAL, ACBlocks.darkstone_cobblestone.get());
		setScrollType(ScrollType.GREATER);
		setColor(0x171f68);
	}

	@Nullable
	private static BlockState degrade(BlockState state) {
		if(state.is(ACBlocks.abyssalnite_stone.get())) return ACBlocks.abyssalnite_cobblestone.get().defaultBlockState();
		if(state.is(ACBlocks.abyssal_stone.get())) return ACBlocks.abyssal_cobblestone.get().defaultBlockState();
		if(state.is(ACBlocks.coralium_stone.get())) return ACBlocks.coralium_cobblestone.get().defaultBlockState();
		if(state.is(ACBlocks.darkstone.get())) return ACBlocks.darkstone_cobblestone.get().defaultBlockState();
		if(state.is(ACBlocks.dreadstone.get())) return ACBlocks.dreadstone_cobblestone.get().defaultBlockState();
		if(state.is(Blocks.STONE)) return Blocks.COBBLESTONE.defaultBlockState();
		if(state.is(Blocks.COBBLESTONE) || state.is(ACBlocks.darkstone_cobblestone.get()) || state.is(ACBlocks.abyssal_cobblestone.get())
				|| state.is(ACBlocks.dreadstone_cobblestone.get()) || state.is(ACBlocks.abyssalnite_cobblestone.get()) || state.is(ACBlocks.coralium_cobblestone.get()))
			return Blocks.GRAVEL.defaultBlockState();
		if(state.is(Blocks.GRAVEL)) return Blocks.SAND.defaultBlockState();
		if(state.is(Blocks.SAND)) return Blocks.AIR.defaultBlockState();
		return null;
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		BlockHitResult hit = TargetUtil.lookedAtBlock(player, 16, ClipContext.Block.OUTLINE);
		return hit != null && degrade(world.getBlockState(hit.getBlockPos())) != null;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		BlockHitResult hit = TargetUtil.lookedAtBlock(player, 16, ClipContext.Block.OUTLINE);
		if(hit == null) return;
		BlockPos target = hit.getBlockPos();
		BlockState state = degrade(world.getBlockState(target));
		if(state == null || !world.mayInteract(player, target)) return;
		world.destroyBlock(target, false);
		world.setBlockAndUpdate(target, state);
		AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(pos, target, world);
		for(int i = 0; i < 9; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if(stack.getItem() instanceof IEnergyContainerItem pe) {
				pe.addEnergy(stack, 5 + world.random.nextInt(5));
				break;
			}
		}
	}
}
