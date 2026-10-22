/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (EntityODBPrimed, EntityODBcPrimed); 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import com.shinoow.abyssalcraft.common.world.ACExplosion;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** TNT physics and synchronized fuses; the full bomb also leaves an obsidian platform and summons Sacthoth. */
public class EntityODBPrimed extends PrimedTnt {
	private UUID ownerId;

	public EntityODBPrimed(EntityType<? extends EntityODBPrimed> type, Level level) {
		super(type, level);
		setFuse(isCore() ? 40 : 200);
	}

	public boolean isCore() { return getType() == ACEntityTypes.PRIMED_ODB_CORE.get(); }

	public void setOwner(@Nullable LivingEntity owner) { ownerId = owner == null ? null : owner.getUUID(); }

	@Nullable
	@Override public LivingEntity getOwner() {
		return ownerId != null && level() instanceof ServerLevel server && server.getEntity(ownerId) instanceof LivingEntity owner ? owner : null;
	}

	@Override public void tick() {
		super.tick();
		if(ACConfig.particleEntity && !isRemoved() && level() instanceof ServerLevel server)
			server.sendParticles(isCore() ? ParticleTypes.DRIPPING_WATER : ParticleTypes.PORTAL, getX(), getY() + 0.5, getZ(), isCore() ? 1 : 10, 0, 0, 0, 1);
	}

	@Override protected void explode() {
		if(ACConfig.no_odb_explosions || !(level() instanceof ServerLevel server)) return;
		double x = getX(), y = getY(), z = getZ();
		ACExplosion.explode(server, this, x, y, z, isCore() ? 16 : ACConfig.odbExplosionSize, false, true,
				isCore() ? null : () -> finishExplosion(server, BlockPos.containing(x, y, z)));
	}

	private static void finishExplosion(ServerLevel level, BlockPos center) {
		// The upstream's four nested loops repeatedly visited this same 17 x 17 square.
		for(BlockPos pos : BlockPos.betweenClosed(center.offset(-8, 0, -8), center.offset(8, 0, 8)))
			if(!level.isOutsideBuildHeight(pos) && level.getBlockState(pos).getExplosionResistance(level, pos, null) < 600000)
				level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
		EntitySacthoth boss = ACEntityTypes.SACTHOTH.get().create(level);
		if(boss == null) return;
		boss.moveTo(center.getX() + 0.5D, center.getY() + 1, center.getZ() + 0.5D, 0, 0);
		boss.finalizeSpawn(level, level.getCurrentDifficultyAt(boss.blockPosition()), MobSpawnType.TRIGGERED, null, null);
		level.addFreshEntity(boss);
		if(ACConfig.showBossDialogs) SpecialTextUtil.translatedGroup(level, "Sacthoth", "message.sacthoth.spawn.1");
	}

	@Override public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(ownerId != null) tag.putUUID("Owner", ownerId);
	}

	@Override public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		ownerId = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
	}
}
