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
 *     1.20.1 port - mode switching/filter/path clearing are validated server actions (ConfiguratorMessage); the
 *     path preview is drawn by the client while the item is held
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.transfer.ItemTransferConfiguration;
import com.shinoow.abyssalcraft.api.transfer.caps.IItemTransferCapability;
import com.shinoow.abyssalcraft.api.transfer.caps.ItemTransferCapability;
import com.shinoow.abyssalcraft.client.ClientPacketHandler;
import com.shinoow.abyssalcraft.common.blocks.tile.TileEntitySpiritAltar;
import com.shinoow.abyssalcraft.lib.util.SpiritItemUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/**
 * Spirit Tablet (Configurator): records a route (mode 0), applies it as an item transfer configuration to an
 * inventory (mode 1) or clears an inventory's configurations (mode 2). Used on a Spirit Altar it controls the altar.
 */
public class ItemConfigurator extends ItemACBasic {

	public ItemConfigurator() {
		super(new Properties().stacksTo(1));
	}

	public static int getMode(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt("Mode") : 0;
	}

	public static void cycleMode(ItemStack stack) {
		int mode = getMode(stack);
		stack.getOrCreateTag().putInt("Mode", mode == 0 ? 1 : mode == 1 ? 2 : 0);
	}

	public static List<BlockPos> getPath(ItemStack stack) {
		List<BlockPos> positions = new ArrayList<>();
		if(stack.hasTag())
			for(Tag t : stack.getTag().getList("Path", Tag.TAG_LONG))
				positions.add(BlockPos.of(((LongTag) t).getAsLong()));
		return positions;
	}

	/** Shows the recorded path while held in mode 0 */
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected)
	{
		if(selected && level.isClientSide && getMode(stack) == 0 && level.getGameTime() % 4 == 0) {
			List<BlockPos> path = getPath(stack);
			if(!path.isEmpty())
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.showRoute(path.toArray(new BlockPos[0]), 4, true));
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		Level w = context.getLevel();
		if(w.isClientSide)
			return InteractionResult.SUCCESS;
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		Direction side = context.getClickedFace();
		CompoundTag nbt = stack.getOrCreateTag();
		int mode = nbt.getInt("Mode");
		BlockEntity te = w.getBlockEntity(pos);
		if(player == null) return InteractionResult.PASS;
		if(te instanceof TileEntitySpiritAltar altar)
			altar.spiritTabletToggle(player, mode);
		else if(mode == 0) { // set path
			ListTag path = nbt.getList("Path", Tag.TAG_LONG);
			if(te != null && SpiritItemUtil.hasInventory(te)) {
				path.add(LongTag.valueOf(pos.asLong()));
				nbt.putInt("EntryFacing", side.get3DDataValue());
				player.sendSystemMessage(Component.translatable("message.configurator.1"));
			} else {
				path.add(LongTag.valueOf(pos.above().asLong()));
				player.sendSystemMessage(Component.translatable("message.configurator.2"));
			}
			nbt.put("Path", path);
		} else if(mode == 1) { // apply configuration
			if(player.mayUseItemAt(pos.relative(side), side, stack)) {
				IItemTransferCapability cap = te != null ? ItemTransferCapability.getCap(te) : null;
				if(cap != null) {
					NonNullList<ItemStack> filter = NonNullList.withSize(5, ItemStack.EMPTY);
					ContainerHelper.loadAllItems(nbt, filter);
					List<BlockPos> positions = getPath(stack);
					if(positions.isEmpty()) {
						player.sendSystemMessage(Component.translatable("message.configurator.error.1"));
						return InteractionResult.FAIL;
					}
					Direction facing = Direction.from3DDataValue(nbt.getInt("EntryFacing"));
					BlockEntity res = w.getBlockEntity(positions.get(positions.size()-1));
					if(res == null || SpiritItemUtil.getInventory(res, facing) == null) {
						player.sendSystemMessage(Component.translatable("message.configurator.error.2"));
						return InteractionResult.FAIL;
					}
					ItemTransferConfiguration cfg = new ItemTransferConfiguration(positions.toArray(new BlockPos[0]))
							.setExitFacing(side)
							.setEntryFacing(facing)
							.setFilter(filter)
							.setFilterSubtypes(nbt.getBoolean("FilterSubtype"))
							.setFilterNBT(nbt.getBoolean("FilterNBT"));
					cfg.setupSubtypeFilter();
					cap.addTransferConfiguration(cfg);
					te.setChanged();
					player.sendSystemMessage(Component.translatable("message.configurator.3"));
				} else player.sendSystemMessage(Component.translatable("message.configurator.error.3"));
			} else player.sendSystemMessage(Component.translatable("message.configurator.error.4"));
		} else if(mode == 2) // clear configuration
			if(player.mayUseItemAt(pos.relative(side), side, stack)) {
				IItemTransferCapability cap = te != null ? ItemTransferCapability.getCap(te) : null;
				if(cap != null) {
					cap.clearConfigurations();
					te.setChanged();
					player.sendSystemMessage(Component.translatable("message.configurator.4"));
				} else player.sendSystemMessage(Component.translatable("message.configurator.error.3"));
			} else player.sendSystemMessage(Component.translatable("message.configurator.error.4"));
		return InteractionResult.CONSUME;
	}

	public static Component getModeName(int mode) {
		return Component.translatable("tooltip.configurator.mode." + (mode >= 0 && mode <= 2 ? mode : 0));
	}

	@Override
	public void appendHoverText(ItemStack is, @Nullable Level level, List<Component> l, TooltipFlag flag){
		l.add(Component.translatable("tooltip.staff.mode.1").append(": ").append(getModeName(getMode(is)).copy().withStyle(ChatFormatting.GOLD)));
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.addConfiguratorKeyTooltip(l));
	}
}
