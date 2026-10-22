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
package com.shinoow.abyssalcraft.api.item;

import java.util.List;
import java.util.function.Consumer;

import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Upgrade Kit, used to upgrade tools and armor to the next tier in a crafting table
 * @author shinoow
 *
 * @since 1.3
 */
public class ItemUpgradeKit extends Item implements IUnlockableItem {

	private IUnlockCondition condition = new DefaultCondition();
	public final String typeName;
	public final String typeName2;

	/**
	 * @param from Material name to upgrade from
	 * @param to Material name to upgrade to
	 */
	public ItemUpgradeKit(String from, String to){
		super(new Item.Properties().stacksTo(16));
		typeName = from;
		typeName2 = to;
	}

	@Override
	public void appendHoverText(ItemStack is, Level level, List<Component> l, TooltipFlag flag){
		l.add(Component.literal(typeName + " To " + typeName2));
	}

	@Override
	public Item setUnlockCondition(IUnlockCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public IUnlockCondition getUnlockCondition(ItemStack stack) {
		return condition;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(UnlockableItemClientExtensions.INSTANCE);
	}
}
