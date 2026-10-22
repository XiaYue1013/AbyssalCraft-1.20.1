/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (ItemDrainStaff, StaffOfRendingMessage); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.rending.Rending;
import com.shinoow.abyssalcraft.api.rending.RendingRegistry;
import com.shinoow.abyssalcraft.lib.util.items.IStaffOfRending;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.RegistryObject;

/**
 * Staff of Rending (1.12.2 "drainstaff" metadata 0 - 3: normal, Abyssal Wasteland, Dreadlands, Omothol).
 * Using it hurts the mob looked at (up to 50 blocks) and collects energy for every Rending that mob qualifies for;
 * using it again turns full energy into the Rending's item.
 * Port change: the target is found on the server (1.12.2 sent the client's target, any entity id, to the server).
 */
public class ItemDrainStaff extends ItemACBasic implements IStaffOfRending {

	public static final double RANGE = 50;

	private final int tier;

	public ItemDrainStaff(int tier) {
		super(new Properties().stacksTo(1));
		this.tier = tier;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide)
			useStaff(level, player, stack);
		return InteractionResultHolder.pass(stack);
	}

	/** Rends the looked at mob, then hands out the items of every full Rending */
	public static void useStaff(Level level, Player player, ItemStack stack) {
		Entity target = lookedAt(player, RANGE);
		if(target != null)
			rend(player, target, stack);
		IStaffOfRending staff = (IStaffOfRending) stack.getItem();
		for(Rending r : RendingRegistry.instance().getRendings())
			if(staff.getEnergy(stack, r.getName()) >= r.getMaxEnergy()) {
				staff.setEnergy(0, stack, r.getName());
				ItemStack output = r.getOutput();
				if(!player.getInventory().add(output))
					player.drop(output, true);
			}
	}

	/** The closest entity on the player's line of sight (1.12.2 getMouseOverExtended) */
	@Nullable
	public static Entity lookedAt(Player player, double range) {
		return com.shinoow.abyssalcraft.lib.util.TargetUtil.lookedAt(player, range);
	}

	/** Hurts the target and the mobs around it (Multi-Rend) */
	public static void rend(Player player, Entity target, ItemStack stack) {
		if(drain(player, target, stack) && level(AbyssalCraftAPI.multi_rend, stack) == 1)
			for(Entity e : player.level().getEntities(target, target.getBoundingBox().inflate(3)))
				drain(player, e, stack);
	}

	/** One hit per target; every Rending it qualifies for gets the drain amount */
	private static boolean drain(Player player, Entity entity, ItemStack stack) {
		Mob mob = entity instanceof Mob m ? m : entity instanceof PartEntity<?> part && part.getParent() instanceof Mob m ? m : null;
		if(mob == null || !entity.isAlive()) return false;
		IStaffOfRending staff = (IStaffOfRending) stack.getItem();
		boolean success = false;
		for(Rending r : RendingRegistry.instance().getRendings())
			if(r.isApplicable(mob) && (success || entity.hurt(player.damageSources().playerAttack(player), staff.getDrainAmount(stack)))) {
				staff.increaseEnergy(stack, r.getName());
				success = true;
			}
		return success;
	}

	static int level(RegistryObject<Enchantment> enchantment, ItemStack stack) {
		return enchantment.isPresent() ? EnchantmentHelper.getItemEnchantmentLevel(enchantment.get(), stack) : 0;
	}

	@Override
	public int getDrainAmount(ItemStack stack) {
		return tier + 1 + Mth.clamp(level(AbyssalCraftAPI.sapping, stack), 0, 3);
	}

	@Override
	public void increaseEnergy(ItemStack stack, String type) {
		stack.getOrCreateTag().putInt("energy" + type, getEnergy(stack, type) + getDrainAmount(stack));
	}

	@Override
	public void setEnergy(int amount, ItemStack stack, String type) {
		stack.getOrCreateTag().putInt("energy" + type, amount);
	}

	@Override
	public int getEnergy(ItemStack stack, String type) {
		return stack.hasTag() ? stack.getTag().getInt("energy" + type) : 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		addEnergyTooltip(this, stack, tooltip);
	}

	static void addEnergyTooltip(IStaffOfRending staff, ItemStack stack, List<Component> tooltip) {
		for(Rending r : RendingRegistry.instance().getRendings())
			tooltip.add(Component.translatable(r.getTooltip()).append(String.format(": %d/%d", staff.getEnergy(stack, r.getName()), r.getMaxEnergy())));
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 5 * (tier + 1);
	}
}
