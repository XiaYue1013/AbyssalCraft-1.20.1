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
 *     1.20.1 port - dimension IDs replaced by dimension keys; the wrong dimension messages are sent by the server
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.init.BlockHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gateway Keys: build a lit gateway frame where they're used.<br>
 * 0 = Gateway Key (Abyssal Wasteland), 1 = Dreaded Gateway Key (Dreadlands), 2 = R'lyehian Gateway Key (Omothol)
 */
public class ItemPortalPlacer extends ItemACBasic {

	private final int key;

	public ItemPortalPlacer(int key){
		super(new Properties().stacksTo(1));
		this.key = key;
		if(key == 1)
			color(ChatFormatting.DARK_RED);
		else if(key == 2)
			color(ChatFormatting.BLUE);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag){
		list.add(Component.translatable("tooltip.portalplacer.1"));
		list.add(Component.translatable("tooltip.portalplacer.2"));
		if(key > 0)
			list.add(Component.translatable("tooltip.portalplacer.3"));
		if(level != null && !isCorrectDim(level.dimension()))
			list.add(Component.translatable("tooltip.portalplacer.4").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
	}

	public boolean isCorrectDim(ResourceKey<Level> dim){
		int override = AbyssalCraftAPI.getGatewayKeyOverride(dim);
		switch(key){
		case 0:
			return dim == Level.OVERWORLD || dim == ACDimensions.ABYSSAL_WASTELAND || override == 0;
		case 1:
			return dim == Level.OVERWORLD || dim == ACDimensions.ABYSSAL_WASTELAND || dim == ACDimensions.DREADLANDS
			|| override >= 0 && override < 2;
		case 2:
			return dim == Level.OVERWORLD || dim == ACDimensions.ABYSSAL_WASTELAND || dim == ACDimensions.DREADLANDS
			|| dim == ACDimensions.OMOTHOL || dim == ACDimensions.DARK_REALM || override >= 0;
		default:
			return false;
		}
	}

	private boolean dimWarning(ResourceKey<Level> dim){
		int override = AbyssalCraftAPI.getGatewayKeyOverride(dim);
		switch(key){
		case 0:
			return dim == ACDimensions.DREADLANDS || dim == ACDimensions.OMOTHOL || dim == ACDimensions.DARK_REALM || override > 0;
		case 1:
			return dim == ACDimensions.OMOTHOL || dim == ACDimensions.DARK_REALM || override > 1;
		default:
			return false;
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		Player player = context.getPlayer();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ResourceKey<Level> dim = world.dimension();

		if(!isCorrectDim(dim)) {
			if(!world.isClientSide && player != null)
				player.displayClientMessage(Component.translatable(dimWarning(dim) ? "message.portalplacer.error.2" : "message.portalplacer.error.1"), false);
			return InteractionResult.FAIL;
		}
		if(world.isClientSide)
			return InteractionResult.SUCCESS;

		int direction = Mth.floor((player != null ? player.getYRot() : 0) * 4.0F / 360.0F + 0.5D) & 3;
		boolean sneaking = player != null && player.isShiftKeyDown();
		int o = AbyssalCraftAPI.getGatewayKeyOverride(dim);

		BlockState abyssal = BlockHandler.ABYSSAL_STONE.get().defaultBlockState(), coraliumFire = BlockHandler.CORALIUM_FIRE.get().defaultBlockState();
		BlockState dreadstone = BlockHandler.DREADSTONE.get().defaultBlockState(), dreadFire = BlockHandler.DREADED_FIRE.get().defaultBlockState();
		BlockState omothol = BlockHandler.OMOTHOL_STONE.get().defaultBlockState(), omotholFire = BlockHandler.OMOTHOL_FIRE.get().defaultBlockState();

		switch(key){
		case 0:
			return buildPortal(world, pos, direction, abyssal, coraliumFire);
		case 1:
			if(dim == ACDimensions.ABYSSAL_WASTELAND && sneaking || dim == Level.OVERWORLD || o == 0)
				return buildPortal(world, pos, direction, abyssal, coraliumFire);
			else return buildPortal(world, pos, direction, dreadstone, dreadFire);
		case 2:
			if(dim == ACDimensions.ABYSSAL_WASTELAND && sneaking || dim == Level.OVERWORLD || o == 0)
				return buildPortal(world, pos, direction, abyssal, coraliumFire);
			else if(dim == ACDimensions.DREADLANDS && sneaking || dim == ACDimensions.ABYSSAL_WASTELAND || o == 1)
				return buildPortal(world, pos, direction, dreadstone, dreadFire);
			else return buildPortal(world, pos, direction, omothol, omotholFire);
		default:
			return InteractionResult.FAIL;
		}
	}

	/**
	 * Builds a 4x5 frame (2x3 opening) on top of the clicked block, facing the player, and lights it.
	 * Fails without changing anything if something is in the way.
	 */
	public static InteractionResult buildPortal(Level world, BlockPos pos, int direction, BlockState frame, BlockState fire){
		boolean alongZ = direction == 1 || direction == 3;
		int dx = alongZ ? 0 : 1, dz = alongZ ? 1 : 0;

		boolean b = true;
		for(int i = -1; i < 3; i++)
			if(!world.getBlockState(pos.offset(dx * i, 0, dz * i)).canBeReplaced())
				b = false;

		if(b) pos = pos.below();

		for(int y = 1; y < 6; y++)
			for(int i = -1; i < 3; i++) {
				BlockPos p = pos.offset(dx * i, y, dz * i);
				if(!world.getBlockState(p).canBeReplaced() || !world.isInWorldBounds(p))
					return InteractionResult.FAIL;
			}

		for(int i = -1; i < 3; i++) {
			world.setBlockAndUpdate(pos.offset(dx * i, 1, dz * i), frame);
			world.setBlockAndUpdate(pos.offset(dx * i, 5, dz * i), frame);
		}
		for(int y = 2; y < 5; y++) {
			world.setBlockAndUpdate(pos.offset(-dx, y, -dz), frame);
			world.setBlockAndUpdate(pos.offset(dx * 2, y, dz * 2), frame);
		}

		world.setBlockAndUpdate(pos.offset(dx, 2, dz), fire);

		return InteractionResult.CONSUME;
	}
}
