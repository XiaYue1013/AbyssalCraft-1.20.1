/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.spells;

import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.api.spell.SpellEnum.ScrollType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Teleport Home: back to the caster's bed (or respawn anchor), across dimensions */
public class TeleportHomeSpell extends Spell {

	public TeleportHomeSpell() {
		super("teleportHome", 1000F, ItemTags.BEDS);
		setRequiresCharging();
		setScrollType(ScrollType.MODERATE);
		setColor(0x0565ff);
	}

	@Override
	public boolean canCastSpell(Level world, BlockPos pos, Player player) {
		// the client doesn't know the respawn point; the server decides
		return !(player instanceof ServerPlayer sp) || sp.getRespawnPosition() != null;
	}

	@Override
	protected void castSpellClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void castSpellServer(Level world, BlockPos pos, Player player) {
		if(!(player instanceof ServerPlayer sp) || sp.getRespawnPosition() == null) return;
		ServerLevel home = sp.server.getLevel(sp.getRespawnDimension());
		if(home == null) return;
		BlockPos bed = sp.getRespawnPosition();
		Vec3 spot = Player.findRespawnPositionAndUseSpawnBlock(home, bed, sp.getRespawnAngle(), sp.isRespawnForced(), true).orElse(Vec3.atBottomCenterOf(bed));
		sp.teleportTo(home, spot.x, spot.y, spot.z, sp.getYRot(), sp.getXRot());
	}
}
