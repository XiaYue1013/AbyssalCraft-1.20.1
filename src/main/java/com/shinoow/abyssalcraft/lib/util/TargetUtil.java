/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (AbyssalCraftClientEventHooks.getMouseOverExtended); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib.util;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Line of sight targeting, on either side. 1.12.2 found the target on the client and sent its id to the server; the
 * port asks the server the same question, so a client can't name an arbitrary entity.
 */
public class TargetUtil {

	/** The closest pickable entity on the player's line of sight within range that isn't behind a block */
	@Nullable
	public static Entity lookedAt(Player player, double range) {
		Vec3 eye = player.getEyePosition(), look = player.getLookAngle();
		Vec3 end = player.level().clip(new ClipContext(eye, eye.add(look.scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation();
		var hit = ProjectileUtil.getEntityHitResult(player.level(), player, eye, end,
				player.getBoundingBox().expandTowards(look.scale(range)).inflate(1), e -> !e.isSpectator() && e.isPickable());
		return hit != null ? hit.getEntity() : null;
	}

	/** The block the player looks at within range (fluids ignored), or null */
	@Nullable
	public static BlockHitResult lookedAtBlock(Player player, double range, ClipContext.Block shape) {
		Vec3 eye = player.getEyePosition();
		BlockHitResult hit = player.level().clip(new ClipContext(eye, eye.add(player.getLookAngle().scale(range)), shape, ClipContext.Fluid.NONE, player));
		return hit.getType() == HitResult.Type.BLOCK ? hit : null;
	}
}
