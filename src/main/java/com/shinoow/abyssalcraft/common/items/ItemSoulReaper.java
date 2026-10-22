/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;
import com.shinoow.abyssalcraft.api.item.ACItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

/** Thirty damage, no enchantments, with escalating bonuses from the souls of its kills. */
public class ItemSoulReaper extends ItemACSword {
	private static final Tier TIER = new Tier() {
		public int getUses() { return 2000; }
		public float getSpeed() { return 1; }
		public float getAttackDamageBonus() { return 26; }
		public int getLevel() { return 0; }
		public int getEnchantmentValue() { return 0; }
		public Ingredient getRepairIngredient() { return Ingredient.of(ACItems.shadow_gem.get()); }
	};
	public ItemSoulReaper() { super(TIER, 3, -2.4F, null); }
	@Override public boolean isBookEnchantable(ItemStack stack, ItemStack book) { return false; }
	@Override public boolean isEnchantable(ItemStack stack) { return false; }
	public int getSouls(ItemStack stack) { return stack.hasTag() ? Math.max(0, Math.min(1000, stack.getTag().getInt("souls"))) : 0; }
	public void setSouls(int souls, ItemStack stack) { stack.getOrCreateTag().putInt("souls", Math.max(0, Math.min(1000, souls))); }

	@Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if(!attacker.level().isClientSide && target.isDeadOrDying()) setSouls(getSouls(stack) + 1, stack);
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if(level.isClientSide || !selected || !(entity instanceof LivingEntity holder)) return;
		int souls = getSouls(stack);
		if(souls < 60) return;
		holder.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, souls >= 500 ? 2 : souls >= 125 ? 1 : 0, false, false));
		if(souls >= 125) holder.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, souls >= 1000 ? 2 : souls >= 500 ? 1 : 0, false, false));
		if(souls >= 250) holder.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
		if(souls >= 500) holder.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false));
		if(souls >= 1000) {
			holder.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0, false, false));
			if(!holder.hasEffect(MobEffects.HEALTH_BOOST)) holder.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 2, false, false));
		}
	}

	@Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
		super.appendHoverText(stack, level, lines, flag);
		lines.add(Component.translatable("tooltip.soulreaper").append(": " + getSouls(stack) + "/1000"));
	}
}
