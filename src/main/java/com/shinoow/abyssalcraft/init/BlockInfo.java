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
 *     1.20.1 port - data generation metadata
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;

/**
 * Registration metadata of a block, consumed by the data generators (models, blockstates, loot, tool tags).
 * Texture names are the upstream texture file names (without the "block/" prefix).
 */
public class BlockInfo {

	public enum Model {
		/** cube_all, tex[0] */
		CUBE,
		/** cube_column, tex[0] = side, tex[1] = end */
		COLUMN,
		/** rotated pillar (log/pillar), tex[0] = side, tex[1] = end */
		PILLAR,
		/** cube_bottom_top, tex[0] = side, tex[1] = bottom, tex[2] = top */
		BOTTOM_TOP,
		/** slab of tex[0], double slab model = tex[1] block model */
		SLAB,
		STAIRS,
		WALL,
		FENCE,
		BUTTON,
		PRESSURE_PLATE,
		DOOR,
		LEAVES,
		/** cross plant, tex[0] */
		CROSS,
		/** translucent cube_all, tex[0] */
		GLASS,
		/** horizontal facing block: tex = front, side, top */
		ORIENTABLE,
		/** horizontal facing + lit machine: tex = front_off, front_on, side, top */
		ORIENTABLE_LIT,
		/** existing (imported) model named tex[0], rotated by the horizontal FACING property */
		CUSTOM_FACING,
		/** blockstate pointing at an existing (imported) model named tex[0] */
		CUSTOM,
		/** nether portal style gateway (AXIS property), tex[0] = portal texture */
		PORTAL,
		/** snow-like layers (LAYERS 1-8), tex[0] */
		LAYERS,
		/** Energy Relay: tex[0] = model facing north (rotated by FACING), tex[1] = optional texture replacing "0" */
		RELAY,
		/** fluid block: model with only a particle texture tex[0] */
		FLUID,
		/** blockstate/model written by hand or by another provider */
		NONE
	}

	public enum Drop {
		SELF, NONE, OTHER, CUSTOM, SLAB, DOOR, LEAVES, ORE,
		/** itself, keeping the stored Potential Energy */
		PE
	}

	public enum Tool {
		PICKAXE, AXE, SHOVEL, HOE, NONE
	}

	public final Supplier<Block> block;
	public Model model = Model.CUBE;
	public String[] textures = new String[0];
	public Drop drop = Drop.SELF;
	/** For OTHER/ORE/LEAVES drops: the dropped item id (registry path in the abyssalcraft namespace, or a full id) */
	public String dropItem;
	public int dropMin = 1, dropMax = 1;
	public Tool tool = Tool.PICKAXE;
	/** 0 = any, 1 = stone, 2 = iron, 3 = diamond, 4 = abyssalnite, 5 = refined coralium, 6 = dreadium, 8 = ethaxium */
	public int harvestLevel = 0;
	public boolean dragonImmune, witherImmune;
	public ACTabs.Tab tab = ACTabs.Tab.BLOCKS;
	/** Block the slab/stairs/wall etc. is made from (for its textures and recipes) */
	public Supplier<Block> parent;
	public boolean hasItem = true;
	/** Upstream (1.12.2) item model to import for the block item instead of a generated one */
	public String itemModel;
	/** Block item factory, null = plain ItemBlockAC */
	@javax.annotation.Nullable
	public java.util.function.BiFunction<Block, net.minecraft.world.item.Item.Properties, net.minecraft.world.item.Item> itemFactory;
	/** Necronomicon unlock condition of the block item, null = always unlocked */
	@javax.annotation.Nullable
	public com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition unlock;

	public BlockInfo(Supplier<Block> block) {
		this.block = block;
	}
}
