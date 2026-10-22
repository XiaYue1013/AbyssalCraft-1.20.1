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
 *     1.20.1 port - item behaviour (the book GUI is ported separately)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.energy.IEnergyTransporterItem;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.api.energy.structure.StructureHandler;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.ShouldSyncMessage;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.RitualUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * The Necronomicon (book types 0-3) and the Abyssalnomicon (4). Stores PE, builds ritual altars/Places of Power
 * (sneak + use on a block) and starts rituals (sneak + use on an altar).
 */
public class ItemNecronomicon extends ItemACBasic implements IEnergyTransporterItem {

	private final int bookType;
	private final int maxEnergy;

	public ItemNecronomicon(int type, int maxEnergy){
		super(new Properties().stacksTo(1));
		bookType = type;
		this.maxEnergy = maxEnergy;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(!player.isShiftKeyDown()){
			if(player instanceof ServerPlayer serverPlayer && ACConfig.syncDataOnBookOpening)
				PacketDispatcher.sendTo(new ShouldSyncMessage(NecroDataCapability.getCap(player).getLastSyncTime()), serverPlayer);
			if(level.isClientSide)
				net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
						() -> () -> com.shinoow.abyssalcraft.client.gui.necronomicon.ClientNecronomicon.open(getBookType()));
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		Player player = context.getPlayer();
		Level w = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if(player != null && player.isShiftKeyDown())
			if(!(w.getBlockEntity(pos) instanceof IRitualAltar altar)){
				if(RitualUtil.tryAltar(w, pos, bookType, player) || StructureHandler.instance().tryFormStructure(w, pos, bookType, player)){
					w.playSound(player, pos, ACSounds.remnant_scream.get(), player.getSoundSource(), 3F, 1F);
					return InteractionResult.sidedSuccess(w.isClientSide);
				}
			} else {
				altar.performRitual(w, pos, player);
				return InteractionResult.sidedSuccess(w.isClientSide);
			}
		return InteractionResult.PASS;
	}

	public int getBookType(){
		return bookType;
	}

	@Override
	public float getContainedEnergy(ItemStack stack) {
		return PEUtils.getContainedEnergy(stack);
	}

	@Override
	public int getMaxEnergy(ItemStack stack) {
		return maxEnergy;
	}

	@Override
	public void addEnergy(ItemStack stack, float energy) {
		PEUtils.addEnergy(this, stack, energy);
	}

	@Override
	public float consumeEnergy(ItemStack stack, float energy) {
		return PEUtils.consumeEnergy(stack, energy);
	}

	@Override
	public boolean canAcceptPE(ItemStack stack) {
		return getContainedEnergy(stack) < getMaxEnergy(stack);
	}

	@Override
	public boolean canTransferPE(ItemStack stack) {
		return getContainedEnergy(stack) > 0;
	}

	@Override
	public boolean canAcceptPEExternally(ItemStack stack) {
		return getContainedEnergy(stack) < getMaxEnergy(stack);
	}

	@Override
	public boolean canTransferPEExternally(ItemStack stack) {
		return getContainedEnergy(stack) > 0;
	}
}
