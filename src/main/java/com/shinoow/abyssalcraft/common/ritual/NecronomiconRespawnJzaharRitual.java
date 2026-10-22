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
package com.shinoow.abyssalcraft.common.ritual;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.world.structure.ACTemplatePiece;
import com.shinoow.abyssalcraft.common.world.structure.JzaharTempleStructure;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.util.RitualUtil;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Rebuilds the Temple of J'zahar (with J'zahar's spawner) when performed on the temple's own altar spot
 */
public class NecronomiconRespawnJzaharRitual extends NecronomiconRitual {

	/** Where the ritual altar stands in the temple */
	public static final BlockPos ALTAR = new BlockPos(4, 54, 85);

	public NecronomiconRespawnJzaharRitual() {
		super("respawnJzahar", 3, ACDimensions.OMOTHOL, 20000F,
				ACItems.omothol_essence.get(), ACItems.shard_of_oblivion.get(), ACItems.omothol_essence.get(), ACItems.shard_of_oblivion.get(),
				ACItems.omothol_essence.get(), ACItems.shard_of_oblivion.get(), ACItems.omothol_essence.get(), ACItems.shard_of_oblivion.get());
	}

	@Override
	public boolean canCompleteRitual(Level world, BlockPos pos, Player player) {
		return pos.equals(ALTAR);
	}

	@Override
	protected void completeRitualClient(Level world, BlockPos pos, Player player) {}

	@Override
	protected void completeRitualServer(Level world, BlockPos pos, Player player) {
		rebuildTemple((ServerLevel) world);
		RitualUtil.tryAltar(world, pos, 4, player);
		SpecialTextUtil.translatedGroup(world, "J'zahar", "message.jzahar.respawn", player.getName());
	}

	/** 1.12.2 StructureJzaharTemple: the temple rebuilt on the first ground below (4, 53, 7) */
	public static boolean rebuildTemple(ServerLevel level) {
		BlockPos pos = new BlockPos(4, 53, 7);
		while(level.isEmptyBlock(pos) && pos.getY() > 2)
			pos = pos.below();
		if(pos.getY() <= 1) return false;
		for(ACTemplatePiece piece : JzaharTempleStructure.pieces(level.getStructureManager(), pos))
			piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(), level.random, piece.getBoundingBox(),
					new ChunkPos(pos), pos);
		return true;
	}
}
