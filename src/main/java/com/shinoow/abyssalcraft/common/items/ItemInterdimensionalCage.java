/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (ItemInterdimensionalCage, InterdimensionalCageMessage); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import java.util.List;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/** One mob per cage, captured for volume * 100 PE. All capture and release mutations happen on the server. */
public class ItemInterdimensionalCage extends ItemACBasic implements IEnergyContainerItem {
	public ItemInterdimensionalCage() { super(new Properties().stacksTo(1)); }
	public static boolean isOccupied(ItemStack stack) { return stack.hasTag() && stack.getTag().contains("Entity", 10); }
	public static float captureCost(Mob mob) { return mob.getBbHeight() * mob.getBbWidth() * mob.getBbWidth() * 100; }
	private static boolean allowed(Mob mob) {
		return mob.canChangeDimensions() && !(mob instanceof EnderDragon) && !(mob instanceof WitherBoss);
	}

	public boolean capture(Player player, InteractionHand hand, LivingEntity target) {
		ItemStack stack = player.getItemInHand(hand);
		if(!(player.level() instanceof ServerLevel) || player.isSpectator() || stack.getItem() != this || isOccupied(stack)
				|| !(target instanceof Mob mob) || !mob.isAlive() || mob.isRemoved() || mob.isPassenger() || mob.isVehicle() || !allowed(mob)
				|| ACConfig.interdimensionalCageBlacklist.contains(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()).toString())
				|| mob.level() != player.level() || player.distanceToSqr(mob) > 9 || !player.hasLineOfSight(mob)) return false;
		float cost = captureCost(mob);
		if(!Float.isFinite(cost) || getContainedEnergy(stack) < cost) return false;
		CompoundTag data = new CompoundTag();
		if(!mob.save(data)) return false;
		stack.getOrCreateTag().put("Entity", data);
		stack.getOrCreateTag().putString("EntityName", mob.getName().getString());
		consumeEnergy(stack, cost); mob.discard();
		return true;
	}

	@Override public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if(isOccupied(stack)) return InteractionResult.PASS;
		if(player.level().isClientSide) return InteractionResult.SUCCESS;
		return capture(player, hand, target) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}

	/** Keeps the payload if spawning fails, including UUID conflicts or a blocked destination. */
	public boolean release(ItemStack stack, ServerLevel level, BlockPos pos) {
		if(stack.getItem() != this || !isOccupied(stack)) return false;
		var entity = EntityType.loadEntityRecursive(stack.getTag().getCompound("Entity").copy(), level, e -> {
			e.moveTo(Vec3.atBottomCenterOf(pos).add(0, 0.01D, 0)); return e;
		});
		if(!(entity instanceof Mob mob) || !allowed(mob) || !level.noCollision(entity) || !level.getWorldBorder().isWithinBounds(entity.getBoundingBox())) return false;
		if(!level.addFreshEntity(entity)) return false;
		stack.getTag().remove("Entity"); stack.getTag().remove("EntityName"); mob.playAmbientSound();
		return true;
	}

	@Override public InteractionResult useOn(UseOnContext context) {
		if(!isOccupied(context.getItemInHand())) return InteractionResult.PASS;
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		Player player = context.getPlayer();
		if(player == null || !context.getLevel().mayInteract(player, pos) || !player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand())) return InteractionResult.FAIL;
		if(context.getLevel().isClientSide) return InteractionResult.SUCCESS;
		return release(context.getItemInHand(), (ServerLevel) context.getLevel(), pos) ? InteractionResult.CONSUME : InteractionResult.FAIL;
	}
	@Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
		super.appendHoverText(stack, level, lines, flag);
		if(isOccupied(stack)) lines.add(Component.translatable("tooltip.cage.captured", stack.getTag().getString("EntityName")));
		else lines.add(Component.translatable("tooltip.cage.capture", Component.keybind("key.use_cage.desc")));
	}
	@Override public float getContainedEnergy(ItemStack stack) { return PEUtils.getContainedEnergy(stack); }
	@Override public int getMaxEnergy(ItemStack stack) { return 1000; }
	@Override public void addEnergy(ItemStack stack, float energy) { PEUtils.addEnergy(this, stack, energy); }
	@Override public float consumeEnergy(ItemStack stack, float energy) { return PEUtils.consumeEnergy(stack, energy); }
	@Override public boolean canAcceptPE(ItemStack stack) { return getContainedEnergy(stack) < getMaxEnergy(stack); }
	@Override public boolean canTransferPE(ItemStack stack) { return false; }
}
