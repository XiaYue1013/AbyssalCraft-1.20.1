/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation (BlockDreadAltarTop, BlockDreadAltarBottom)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.common.world.gen.ChagarothLair;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Altar of Cha'garoth: the bottom goes anywhere in the Dreadlands Mountains, the top on it at y 41. Right-clicking the
 * top builds the lair of Cha'garoth. Both give the Dread Plague to what touches them.
 */
public class BlockDreadAltar extends Block {

	/** The y the top has to be at */
	public static final int ALTAR_Y = 41;
	private static final VoxelShape TOP_SHAPE = Block.box(3.2, 0, 3.2, 12.8, 11.2, 12.8);

	private final boolean top;

	public BlockDreadAltar(Properties properties, boolean top) {
		super(properties);
		this.top = top;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return top ? TOP_SHAPE : Shapes.block();
	}

	private static boolean inMountains(Level level, BlockPos pos) {
		return level.getBiome(pos).is(ACBiomes.dreadlands_mountains);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(placer == null || level.isClientSide) return;
		if(level.dimension() != ACDimensions.DREADLANDS)
			placer.sendSystemMessage(Component.translatable("message.dreadaltar.error.1"));
		else if(!inMountains(level, pos))
			placer.sendSystemMessage(Component.translatable("message.dreadaltar.error.2"));
		else if(top && level.getBlockState(pos.below()).is(ACBlocks.chagaroth_altar_bottom.get()))
			placer.sendSystemMessage(pos.getY() == ALTAR_Y ? Component.translatable("message.dreadaltartop.enter") : heightMessage(pos, "You need to"));
	}

	private static Component heightMessage(BlockPos pos, String start) {
		return Component.literal(pos.getY() < ALTAR_Y ? start + " place the altar " + (ALTAR_Y - pos.getY()) + " blocks higher."
				: start + " place the altar " + (pos.getY() - ALTAR_Y) + " blocks lower.");
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(!top || level.isClientSide || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
		if(level.dimension() != ACDimensions.DREADLANDS)
			player.sendSystemMessage(Component.translatable("message.dreadaltar.error.3"));
		else if(!inMountains(level, pos))
			player.sendSystemMessage(Component.translatable("message.dreadaltar.error.2"));
		else if(level.getBlockState(pos.below()).is(ACBlocks.chagaroth_altar_bottom.get()))
			if(pos.getY() == ALTAR_Y) {
				SpecialTextUtil.translatedGroup(level, "Cha'garoth", "message.dreadaltartop.spawn");
				ChagarothLair.generate((ServerLevel) level, pos);
			} else
				player.sendSystemMessage(heightMessage(pos, "You still need to"));
		return InteractionResult.PASS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if(!level.isClientSide && entity instanceof LivingEntity living && !EntityUtil.isEntityDread(living))
			living.addEffect(new MobEffectInstance(AbyssalCraftAPI.dread_plague.get(), 100));
	}
}
