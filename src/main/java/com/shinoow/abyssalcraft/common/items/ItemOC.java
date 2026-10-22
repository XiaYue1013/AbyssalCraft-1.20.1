/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Ritual ingredient; after defeating all four bosses, it can also be consumed for thirty seconds of power. */
public class ItemOC extends ItemACBasic {
	public static final IUnlockCondition KILLED_BOSSES = new IUnlockCondition() {
		public boolean areConditionObjectsEqual(Object object) { return false; }
		public Object getConditionObject() { return new String[] {"abyssalcraft:asorah", "abyssalcraft:chagaroth", "abyssalcraft:jzahar", "abyssalcraft:sacthoth"}; }
		public int getType() { return 11; }
	};
	public ItemOC() { color(ChatFormatting.DARK_RED); }
	@Override public boolean isFoil(ItemStack stack) { return true; }
	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide && NecroDataCapability.getCap(player).isUnlocked(KILLED_BOSSES, player)) {
			for(var effect : List.of(MobEffects.DAMAGE_BOOST, MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY, MobEffects.REGENERATION,
					MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_RESISTANCE, MobEffects.FIRE_RESISTANCE, MobEffects.WATER_BREATHING,
					MobEffects.JUMP, MobEffects.DIG_SPEED, MobEffects.GLOWING))
				player.addEffect(new MobEffectInstance(effect, 600, 6, false, false));
			stack.shrink(1);
			return InteractionResultHolder.consume(stack);
		}
		return InteractionResultHolder.pass(stack);
	}
	@Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
		super.appendHoverText(stack, level, lines, flag);
		lines.add(Component.translatable("tooltip.oc"));
	}
}
