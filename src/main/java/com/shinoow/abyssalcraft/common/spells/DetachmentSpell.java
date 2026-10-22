/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import java.util.ArrayList;
import java.util.List;

import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Detachment: a target wearing armor drops one random piece of its equipment (held items included) */
public class DetachmentSpell extends TargetedSpell {

	public DetachmentSpell() {
		super("detachment", 100, Items.IRON_INGOT);
		setScrollType(ScrollType.MODERATE);
		setRequiresCharging();
		setColor(0x463faa);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		for(ItemStack stack : target.getArmorSlots())
			if(!stack.isEmpty())
				return true;
		return false;
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		List<EquipmentSlot> slots = new ArrayList<>();
		for(EquipmentSlot slot : EquipmentSlot.values())
			if(!target.getItemBySlot(slot).isEmpty())
				slots.add(slot);
		EquipmentSlot slot = slots.get(player.getRandom().nextInt(slots.size()));
		target.spawnAtLocation(target.getItemBySlot(slot), 0);
		target.setItemSlot(slot, ItemStack.EMPTY);
	}
}
