/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Siren's Song: tames (and fully heals) a tameable animal or horse that isn't the caster's */
public class SirensSongSpell extends TargetedSpell {

	public SirensSongSpell() {
		super("sirenssong", 1000F, Items.WHEAT);
		setScrollType(ScrollType.LESSER);
		setRequiresCharging();
		setColor(0x1c8edb);
	}

	@Override
	protected boolean isValidTarget(Player player, LivingEntity target) {
		return target instanceof TamableAnimal animal && !animal.isOwnedBy(player)
				|| target instanceof AbstractHorse horse && !player.getUUID().equals(horse.getOwnerUUID());
	}

	@Override
	protected void affect(Player player, LivingEntity target) {
		if(target instanceof TamableAnimal animal)
			animal.tame(player);
		else if(target instanceof AbstractHorse horse)
			horse.tameWithName(player);
		target.setHealth(target.getMaxHealth());
	}
}
