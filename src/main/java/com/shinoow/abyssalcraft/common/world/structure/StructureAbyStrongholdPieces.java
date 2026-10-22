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
 *     1.20.1 port - translated from 1.12.2 StructureComponent to StructurePiece; upstream method names are kept and
 *     bridged by Stronghold (addChildren -> buildComponent, postProcess -> addComponentParts)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.init.ACStructures;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureAbyStrongholdPieces
{
	/** Abyssal Zombies (zombies until the AbyssalCraft mobs are ported) */
	private static EntityType<?> spawnerMob()
	{
		ResourceLocation id = AbyssalCraft.loc(AbyssalCraftAPI.ACEntities.abyssal_zombie);
		return ForgeRegistries.ENTITY_TYPES.containsKey(id) ? ForgeRegistries.ENTITY_TYPES.getValue(id) : EntityType.ZOMBIE;
	}
	private static final StructureAbyStrongholdPieces.PieceWeight[] pieceWeightArray = new StructureAbyStrongholdPieces.PieceWeight[] {new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.Straight.class, 40, 0), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.Prison.class, 5, 5), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.LeftTurn.class, 20, 0), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.RightTurn.class, 20, 0), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.RoomCrossing.class, 10, 6), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.StairsStraight.class, 5, 5), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.Stairs.class, 5, 5), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.Crossing.class, 5, 4), new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.ChestCorridor.class, 5, 4)
	{

		@Override
		public boolean canSpawnMoreStructuresOfType(int par1)
		{
			return super.canSpawnMoreStructuresOfType(par1) && par1 > 4;
		}
	}, new StructureAbyStrongholdPieces.PieceWeight(StructureAbyStrongholdPieces.PortalRoom.class, 20, 1)
	{

		@Override
		public boolean canSpawnMoreStructuresOfType(int par1)
		{
			return super.canSpawnMoreStructuresOfType(par1) && par1 > 5;
		}
	}
	};
	private static List<PieceWeight> structurePieceList;
	private static Class<?> strongComponentType;
	static int totalWeight;
	private static final StructureAbyStrongholdPieces.Stones strongholdStones = new StructureAbyStrongholdPieces.Stones(null);
	/** Loads a saved piece (all pieces share one piece type, "Kind" tells them apart) */
	public static StructurePiece load(CompoundTag tag)
	{
		return switch(tag.getString("Kind")) {
		case "ChestCorridor" -> new ChestCorridor(tag);
		case "Corridor" -> new Corridor(tag);
		case "Crossing" -> new Crossing(tag);
		case "LeftTurn" -> new LeftTurn(tag);
		case "PortalRoom" -> new PortalRoom(tag);
		case "Prison" -> new Prison(tag);
		case "RightTurn" -> new RightTurn(tag);
		case "RoomCrossing" -> new RoomCrossing(tag);
		case "Stairs" -> new Stairs(tag);
		case "Stairs2" -> new Stairs2(tag);
		case "Straight" -> new Straight(tag);
		case "StairsStraight" -> new StairsStraight(tag);
		default -> throw new IllegalArgumentException("Unknown Abyssal stronghold piece " + tag.getString("Kind"));
		};
	}


	/**
	 * sets up Arrays with the Structure pieces and their weights
	 */
	public static void prepareStructurePieces()
	{
		structurePieceList = new ArrayList<>();
		StructureAbyStrongholdPieces.PieceWeight[] apieceweight = pieceWeightArray;
		int i = apieceweight.length;

		for (int j = 0; j < i; ++j)
		{
			StructureAbyStrongholdPieces.PieceWeight pieceweight = apieceweight[j];
			pieceweight.instancesSpawned = 0;
			structurePieceList.add(pieceweight);
		}

		strongComponentType = null;
	}

	private static boolean canAddStructurePieces()
	{
		boolean flag = false;
		totalWeight = 0;
		StructureAbyStrongholdPieces.PieceWeight pieceweight;

		for (Iterator<PieceWeight> iterator = structurePieceList.iterator(); iterator.hasNext(); totalWeight += pieceweight.pieceWeight)
		{
			pieceweight = iterator.next();

			if (pieceweight.instancesLimit > 0 && pieceweight.instancesSpawned < pieceweight.instancesLimit)
				flag = true;
		}

		return flag;
	}

	/**
	 * translates the PieceWeight class to the Component class
	 */
	private static StructureAbyStrongholdPieces.Stronghold getStrongholdComponentFromWeightedPiece(Class<?> par0Class, StructurePieceAccessor par1List, RandomSource par2Random, int par3, int par4, int par5, Direction par6, int par7)
	{
		Object object = null;

		if (par0Class == StructureAbyStrongholdPieces.Straight.class)
			object = StructureAbyStrongholdPieces.Straight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.Prison.class)
			object = StructureAbyStrongholdPieces.Prison.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.LeftTurn.class)
			object = StructureAbyStrongholdPieces.LeftTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.RightTurn.class)
			object = LeftTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.RoomCrossing.class)
			object = StructureAbyStrongholdPieces.RoomCrossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.StairsStraight.class)
			object = StructureAbyStrongholdPieces.StairsStraight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.Stairs.class)
			object = StructureAbyStrongholdPieces.Stairs.getStrongholdStairsComponent(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.Crossing.class)
			object = StructureAbyStrongholdPieces.Crossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.ChestCorridor.class)
			object = StructureAbyStrongholdPieces.ChestCorridor.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
		else if (par0Class == StructureAbyStrongholdPieces.PortalRoom.class)
			object = StructureAbyStrongholdPieces.PortalRoom.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);

		return (StructureAbyStrongholdPieces.Stronghold)object;
	}

	private static StructureAbyStrongholdPieces.Stronghold getNextComponent(StructureAbyStrongholdPieces.Stairs2 par0ComponentStrongholdStairs2, StructurePieceAccessor par1List, RandomSource par2Random, int par3, int par4, int par5, Direction par6, int par7)
	{
		if (!canAddStructurePieces())
			return null;
		else
		{
			if (strongComponentType != null)
			{
				StructureAbyStrongholdPieces.Stronghold stronghold = getStrongholdComponentFromWeightedPiece(strongComponentType, par1List, par2Random, par3, par4, par5, par6, par7);
				strongComponentType = null;

				if (stronghold != null)
					return stronghold;
			}

			int k1 = 0;

			while (k1 < 5)
			{
				++k1;
				int j1 = par2Random.nextInt(totalWeight);
				Iterator<PieceWeight> iterator = structurePieceList.iterator();

				while (iterator.hasNext())
				{
					StructureAbyStrongholdPieces.PieceWeight pieceweight = iterator.next();
					j1 -= pieceweight.pieceWeight;

					if (j1 < 0)
					{
						if (!pieceweight.canSpawnMoreStructuresOfType(par7) || pieceweight == par0ComponentStrongholdStairs2.strongholdPieceWeight)
							break;

						StructureAbyStrongholdPieces.Stronghold stronghold1 = getStrongholdComponentFromWeightedPiece(pieceweight.pieceClass, par1List, par2Random, par3, par4, par5, par6, par7);

						if (stronghold1 != null)
						{
							++pieceweight.instancesSpawned;
							par0ComponentStrongholdStairs2.strongholdPieceWeight = pieceweight;

							if (!pieceweight.canSpawnMoreStructures())
								structurePieceList.remove(pieceweight);

							return stronghold1;
						}
					}
				}
			}

			BoundingBox structureboundingbox = StructureAbyStrongholdPieces.Corridor.func_74992_a(par1List, par2Random, par3, par4, par5, par6);

			if (structureboundingbox != null && structureboundingbox.minY() > 1)
				return new StructureAbyStrongholdPieces.Corridor(par7, par2Random, structureboundingbox, par6);
			else
				return null;
		}
	}

	private static StructurePiece getNextValidComponent(StructureAbyStrongholdPieces.Stairs2 par0ComponentStrongholdStairs2, StructurePieceAccessor par1List, RandomSource par2Random, int par3, int par4, int par5, Direction par6, int par7)
	{
		if (par7 > 50 || !(Math.abs(par3 - par0ComponentStrongholdStairs2.getBoundingBox().minX()) <= 112 && Math.abs(par5 - par0ComponentStrongholdStairs2.getBoundingBox().minZ()) <= 112))
			return null;
		else {
			StructureAbyStrongholdPieces.Stronghold stronghold = getNextComponent(par0ComponentStrongholdStairs2, par1List, par2Random, par3, par4, par5, par6, par7 + 1);

			if (stronghold != null)
			{
				par1List.addPiece(stronghold);
				par0ComponentStrongholdStairs2.field_75026_c.add(stronghold);
			}

			return stronghold;
		}
	}

	public static class Stairs extends StructureAbyStrongholdPieces.Stronghold
	{
		private boolean field_75024_a;
		public Stairs(CompoundTag tag)
		{
			super(tag);
			field_75024_a = tag.getBoolean("Source");
		}

		public Stairs(int par1, RandomSource par2Random, int par3, int par4)
		{
			super(par1);
			field_75024_a = true;
			setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(par2Random));
			field_143013_d = StructureAbyStrongholdPieces.Stronghold.Door.OPENING;

			switch (getOrientation())
			{
			case NORTH:
			case SOUTH:
				boundingBox = new BoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
				break;
			default:
				boundingBox = new BoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
			}
		}

		public Stairs(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			field_75024_a = false;
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("Source", field_75024_a);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			if (field_75024_a)
				StructureAbyStrongholdPieces.strongComponentType = StructureAbyStrongholdPieces.Crossing.class;

			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		/**
		 * performs some checks, then gives out a fresh Stairs component
		 */
		public static StructureAbyStrongholdPieces.Stairs getStrongholdStairsComponent(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -7, 0, 5, 11, 5, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.Stairs(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 4, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 7, 0);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 6, 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 5, 1, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 1, 6, 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 5, 2, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 4, 3, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 1, 5, 3, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 4, 3, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 3, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 4, 3, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 2, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 2, 1, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 3, 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 2, 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 1, 1, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 1, 2, 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 1, 2, par3StructureBoundingBox);
				if(true)
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 1, 1, 3, par3StructureBoundingBox);
				return true;
			}
		}
	}

	public static class Straight extends StructureAbyStrongholdPieces.Stronghold
	{
		private boolean expandsX;
		private boolean expandsZ;
		public Straight(CompoundTag tag)
		{
			super(tag);
			expandsX = tag.getBoolean("Left");
			expandsZ = tag.getBoolean("Right");
		}

		public Straight(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
			expandsX = par2Random.nextInt(2) == 0;
			expandsZ = par2Random.nextInt(2) == 0;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("Left", expandsX);
			par1NBTTagCompound.putBoolean("Right", expandsZ);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);

			if (expandsX)
				getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 2);

			if (expandsZ)
				getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 2);
		}

		public static StructureAbyStrongholdPieces.Straight findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.Straight(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 1, 0);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);

				if (expandsX)
					generateBox(par1World, par3StructureBoundingBox, 0, 1, 2, 0, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				if (expandsZ)
					generateBox(par1World, par3StructureBoundingBox, 4, 1, 2, 4, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				return true;
			}
		}
	}

	public static class PortalRoom extends StructureAbyStrongholdPieces.Stronghold
	{
		private boolean hasSpawner;
		public PortalRoom(CompoundTag tag)
		{
			super(tag);
			hasSpawner = tag.getBoolean("Mob");
		}

		public PortalRoom(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			boundingBox = par3StructureBoundingBox;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("Mob", hasSpawner);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			if (par1StructureComponent != null)
				((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent).strongholdPortalRoom = this;
		}

		public static StructureAbyStrongholdPieces.PortalRoom findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -4, -1, 0, 11, 8, 16, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.PortalRoom(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 7, 15, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
			byte b0 = 6;
			generateBox(par1World, par3StructureBoundingBox, 1, b0, 1, 1, b0, 14, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 9, b0, 1, 9, b0, 14, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 2, b0, 1, 8, b0, 2, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 2, b0, 14, 8, b0, 14, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 1, 1, 1, 2, 1, 4, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 8, 1, 1, 9, 1, 4, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 1, 1, 1, 1, 1, 3, ACBlocks.liquid_coralium.get().defaultBlockState(), ACBlocks.liquid_coralium.get().defaultBlockState(), false);
			generateBox(par1World, par3StructureBoundingBox, 9, 1, 1, 9, 1, 3, ACBlocks.liquid_coralium.get().defaultBlockState(), ACBlocks.liquid_coralium.get().defaultBlockState(), false);
			generateBox(par1World, par3StructureBoundingBox, 3, 1, 8, 7, 1, 12, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 4, 1, 9, 6, 1, 11, ACBlocks.liquid_coralium.get().defaultBlockState(), ACBlocks.liquid_coralium.get().defaultBlockState(), false);
			int i;

			for (i = 3; i < 14; i += 2)
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 3, i, 0, 4, i, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 10, 3, i, 10, 4, i, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), false);
			}

			for (i = 2; i < 9; i += 2)
				generateBox(par1World, par3StructureBoundingBox, i, 3, 15, i, 4, 15, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), false);

			generateBox(par1World, par3StructureBoundingBox, 4, 1, 5, 6, 1, 7, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 4, 2, 6, 6, 2, 7, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
			generateBox(par1World, par3StructureBoundingBox, 4, 3, 7, 6, 3, 7, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);

			if(true) {
				BlockState iblockstate3 = ACBlocks.abyssal_stone_brick_stairs.get().defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
				for (int j = 4; j <= 6; ++j)
				{
					placeBlock(par1World, iblockstate3, j, 1, 4, par3StructureBoundingBox);
					placeBlock(par1World, iblockstate3, j, 2, 5, par3StructureBoundingBox);
					placeBlock(par1World, iblockstate3, j, 3, 6, par3StructureBoundingBox);
				}
			}

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 3, 8, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 8, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 6, 3, 8, par3StructureBoundingBox);

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 3, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 6, 3, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 4, 4, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 5, 4, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 6, 4, 12, par3StructureBoundingBox);


			placeBlock(par1World, ACBlocks.dreadlands_infused_powerstone.get().defaultBlockState(), 5, 4, 10, par3StructureBoundingBox);

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 3, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 6, 3, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 3, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 6, 3, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 11, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 3, 11, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 6, 3, 11, par3StructureBoundingBox);

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 11, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 3, 4, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 3, 4, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 3, 4, 11, par3StructureBoundingBox);

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 3, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 3, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 3, 11, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 7, 4, 9, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 7, 4, 10, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 7, 4, 11, par3StructureBoundingBox);

			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 3, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 7, 4, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 3, 4, 12, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 3, 8, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 7, 4, 8, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 3, 8, par3StructureBoundingBox);
			placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 3, 4, 8, par3StructureBoundingBox);

			if (!hasSpawner)
			{
				i = getWorldY(3);
				BlockPos blockpos = new BlockPos(getWorldX(5, 6), i, getWorldZ(5, 6));

				if (par3StructureBoundingBox.isInside(blockpos))
				{
					hasSpawner = true;
					par1World.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
					if(par1World.getBlockEntity(blockpos) instanceof SpawnerBlockEntity spawner)
						spawner.setEntityId(spawnerMob(), par2Random);
				}
			}

			return true;
		}
	}

	public static class ChestCorridor extends StructureAbyStrongholdPieces.Stronghold
	{
		private boolean hasMadeChest;
		public ChestCorridor(CompoundTag tag)
		{
			super(tag);
			hasMadeChest = tag.getBoolean("Chest");
		}

		public ChestCorridor(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("Chest", hasMadeChest);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		public static StructureAbyStrongholdPieces.ChestCorridor findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.ChestCorridor(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 1, 0);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
				generateBox(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 1, 4, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), false);
				if(true) {
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 1, 1, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 1, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 2, 2, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 3, 2, 4, par3StructureBoundingBox);
				}
				int i;

				if(true)
					for (i = 2; i <= 4; ++i)
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 2, 1, i, par3StructureBoundingBox);

				if (!hasMadeChest && par3StructureBoundingBox.isInside(new BlockPos(getWorldX(3, 3), getWorldY(2), getWorldZ(3, 3)))){
					hasMadeChest = true;
					createChest(par1World, par3StructureBoundingBox, par2Random, 3, 2, 3, ACLoot.CHEST_ABYSSAL_STRONGHOLD_CORRIDOR);
				}

				return true;
			}
		}
	}

	public static class RoomCrossing extends StructureAbyStrongholdPieces.Stronghold
	{
		protected int roomType;
		public RoomCrossing(CompoundTag tag)
		{
			super(tag);
			roomType = tag.getInt("Type");
		}

		public RoomCrossing(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
			roomType = par2Random.nextInt(5);
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putInt("Type", roomType);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 4, 1);
			getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 4);
			getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 4);
		}

		public static StructureAbyStrongholdPieces.RoomCrossing findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -4, -1, 0, 11, 7, 11, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.RoomCrossing(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 6, 10, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 4, 1, 0);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 10, 6, 3, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 0, 1, 4, 0, 3, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 10, 1, 4, 10, 3, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				int i;

				switch (roomType)
				{
				case 0:
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 1, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 2, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 5, par3StructureBoundingBox);
					placeBlock(par1World, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, par3StructureBoundingBox);
					placeBlock(par1World, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, par3StructureBoundingBox);
					placeBlock(par1World, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, par3StructureBoundingBox);
					placeBlock(par1World, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, par3StructureBoundingBox);
					if(true) {
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 4, 1, 4, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 4, 1, 5, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 4, 1, 6, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 6, 1, 4, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 6, 1, 5, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 6, 1, 6, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 5, 1, 4, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), 5, 1, 6, par3StructureBoundingBox);
					}
					break;
				case 1:
					for (i = 0; i < 5; ++i)
					{
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 1, 3 + i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 7, 1, 3 + i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3 + i, 1, 3, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3 + i, 1, 7, par3StructureBoundingBox);
					}

					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 1, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 2, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 5, 3, 5, par3StructureBoundingBox);
					placeBlock(par1World, Blocks.WATER.defaultBlockState(), 5, 4, 5, par3StructureBoundingBox);
					break;
				case 2:
					for (i = 1; i <= 9; ++i)
					{
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 1, 3, i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 9, 3, i, par3StructureBoundingBox);
					}

					for (i = 1; i <= 9; ++i)
					{
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), i, 3, 1, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), i, 3, 9, par3StructureBoundingBox);
					}

					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 5, 1, 4, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 5, 1, 6, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 5, 3, 4, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 5, 3, 6, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 4, 1, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 6, 1, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 4, 3, 5, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 6, 3, 5, par3StructureBoundingBox);

					for (i = 1; i <= 3; ++i)
					{
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 4, i, 4, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 6, i, 4, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 4, i, 6, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_cobblestone.get().defaultBlockState(), 6, i, 6, par3StructureBoundingBox);
					}

					placeBlock(par1World, Blocks.TORCH.defaultBlockState(), 5, 3, 5, par3StructureBoundingBox);

					for (i = 2; i <= 8; ++i)
					{
						placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 2, 3, i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 3, 3, i, par3StructureBoundingBox);

						if (i <= 3 || i >= 7)
						{
							placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 4, 3, i, par3StructureBoundingBox);
							placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 5, 3, i, par3StructureBoundingBox);
							placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 6, 3, i, par3StructureBoundingBox);
						}

						placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 7, 3, i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.darklands_oak_planks.get().defaultBlockState(), 8, 3, i, par3StructureBoundingBox);
					}

					BlockState iblockstate = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
					placeBlock(par1World, iblockstate, 9, 1, 3, par3StructureBoundingBox);
					placeBlock(par1World, iblockstate, 9, 2, 3, par3StructureBoundingBox);
					placeBlock(par1World, iblockstate, 9, 3, 3, par3StructureBoundingBox);
					createChest(par1World, par3StructureBoundingBox, par2Random, 3, 4, 8, ACLoot.CHEST_ABYSSAL_STRONGHOLD_CROSSING);
				}

				return true;
			}
		}
	}

	public static class StairsStraight extends StructureAbyStrongholdPieces.Stronghold
	{
		public StairsStraight(CompoundTag tag)
		{
			super(tag);
		}

		public StairsStraight(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		public static StructureAbyStrongholdPieces.StairsStraight findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -7, 0, 5, 11, 8, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.StairsStraight(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 7, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 7, 0);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);


				for (int j = 0; j < 6; ++j)
				{
					if(true) {
						BlockState iblockstate = ACBlocks.abyssal_cobblestone_stairs.get().defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
						placeBlock(par1World, iblockstate, 1, 6 - j, 1 + j, par3StructureBoundingBox);
						placeBlock(par1World, iblockstate, 2, 6 - j, 1 + j, par3StructureBoundingBox);
						placeBlock(par1World, iblockstate, 3, 6 - j, 1 + j, par3StructureBoundingBox);
					}

					if (j < 5)
					{
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 5 - j, 1 + j, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 5 - j, 1 + j, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 5 - j, 1 + j, par3StructureBoundingBox);
					}
				}

				return true;
			}
		}
	}

	public static class Stairs2 extends StructureAbyStrongholdPieces.Stairs
	{
		public StructureAbyStrongholdPieces.PieceWeight strongholdPieceWeight;
		public StructureAbyStrongholdPieces.PortalRoom strongholdPortalRoom;
		public List<Stronghold> field_75026_c = new ArrayList<>();
		public Stairs2(CompoundTag tag)
		{
			super(tag);
		}

		public Stairs2(int par1, RandomSource par2Random, int par3, int par4)
		{
			super(0, par2Random, par3, par4);
		}

		//		@Override
		//		public BlockPos getBoundingBoxCenter()
		//		{
		//			return strongholdPortalRoom != null ? strongholdPortalRoom.getBoundingBoxCenter() : super.getBoundingBoxCenter();
		//		}
	}

	public static class Prison extends StructureAbyStrongholdPieces.Stronghold
	{
		public Prison(CompoundTag tag)
		{
			super(tag);
		}

		public Prison(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		public static StructureAbyStrongholdPieces.Prison findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 9, 5, 11, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.Prison(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 4, 10, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 1, 0);
				generateBox(par1World, par3StructureBoundingBox, 1, 1, 10, 3, 3, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 1, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 3, 4, 3, 3, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 7, 4, 3, 7, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 9, 4, 3, 9, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 4, 4, 3, 6, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 5, 1, 5, 7, 3, 5, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), false);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 4, 3, 2, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), 4, 3, 8, par3StructureBoundingBox);
				BlockState iblockstate = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
				BlockState iblockstate1 = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
				placeBlock(par1World, iblockstate, 4, 1, 2, par3StructureBoundingBox);
				placeBlock(par1World, iblockstate1, 4, 2, 2, par3StructureBoundingBox);
				placeBlock(par1World, iblockstate, 4, 1, 8, par3StructureBoundingBox);
				placeBlock(par1World, iblockstate1, 4, 2, 8, par3StructureBoundingBox);
				return true;
			}
		}
	}

	public static class LeftTurn extends StructureAbyStrongholdPieces.Stronghold
	{
		public LeftTurn(CompoundTag tag)
		{
			super(tag);
		}

		public LeftTurn(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			if (getOrientation() != Direction.NORTH && getOrientation() != Direction.EAST)
				getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
			else
				getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		public static StructureAbyStrongholdPieces.LeftTurn findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, 5, par5);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par0List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.LeftTurn(par6, par1Random, structureboundingbox, par5) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 4, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 1, 0);

				if (getOrientation() != Direction.NORTH && getOrientation() != Direction.EAST)
					generateBox(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				else
					generateBox(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				return true;
			}
		}
	}

	public static class RightTurn extends StructureAbyStrongholdPieces.LeftTurn
	{
		public RightTurn(CompoundTag tag)
		{
			super(tag);
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			if (getOrientation() != Direction.NORTH && getOrientation() != Direction.EAST)
				getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
			else
				getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 1, 1);
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 4, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 1, 1, 0);

				if (getOrientation() != Direction.NORTH && getOrientation() != Direction.EAST)
					generateBox(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				else
					generateBox(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				return true;
			}
		}
	}

	static class Stones extends StructurePiece.BlockSelector
	{
		Stones(Object unused) {}

		/** abyssal stone bricks, 1 in 5 cracked */
		@Override
		public void next(RandomSource random, int x, int y, int z, boolean wall)
		{
			if(wall)
				next = random.nextFloat() < 0.2F ? ACBlocks.cracked_abyssal_stone_bricks.get().defaultBlockState() : ACBlocks.abyssal_stone_bricks.get().defaultBlockState();
			else next = Blocks.AIR.defaultBlockState();
		}
	}

	public abstract static class Stronghold extends StructurePiece
	{
		protected StructureAbyStrongholdPieces.Stronghold.Door field_143013_d;
		protected Stronghold(int genDepth)
		{
			super(ACStructures.ABYSSAL_STRONGHOLD_PIECE.get(), genDepth, new BoundingBox(0, 0, 0, 0, 0, 0));
			field_143013_d = StructureAbyStrongholdPieces.Stronghold.Door.OPENING;
		}

		protected Stronghold(CompoundTag tag)
		{
			super(ACStructures.ABYSSAL_STRONGHOLD_PIECE.get(), tag);
			field_143013_d = StructureAbyStrongholdPieces.Stronghold.Door.valueOf(tag.getString("EntryDoor"));
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag)
		{
			tag.putString("Kind", getClass().getSimpleName());
			tag.putString("EntryDoor", field_143013_d.name());
		}

		@Override
		public void addChildren(StructurePiece start, StructurePieceAccessor pieces, RandomSource random)
		{
			buildComponent(start, pieces, random);
		}

		public void buildComponent(StructurePiece start, StructurePieceAccessor pieces, RandomSource random) {}

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot)
		{
			addComponentParts(level, random, box);
		}

		public abstract boolean addComponentParts(WorldGenLevel level, RandomSource random, BoundingBox box);

		/** 1.12.2 StructureComponent#isLiquidInStructureBoundingBox: any liquid on the piece's outer faces (within the chunk) */
		protected boolean isLiquidInStructureBoundingBox(WorldGenLevel level, BoundingBox box)
		{
			int i = Math.max(boundingBox.minX() - 1, box.minX()), j = Math.max(boundingBox.minY() - 1, box.minY()), k = Math.max(boundingBox.minZ() - 1, box.minZ());
			int l = Math.min(boundingBox.maxX() + 1, box.maxX()), i1 = Math.min(boundingBox.maxY() + 1, box.maxY()), j1 = Math.min(boundingBox.maxZ() + 1, box.maxZ());
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			for(int x = i; x <= l; ++x)
				for(int z = k; z <= j1; ++z)
					if(isLiquid(level, pos.set(x, j, z)) || isLiquid(level, pos.set(x, i1, z)))
						return true;
			for(int x = i; x <= l; ++x)
				for(int y = j; y <= i1; ++y)
					if(isLiquid(level, pos.set(x, y, k)) || isLiquid(level, pos.set(x, y, j1)))
						return true;
			for(int z = k; z <= j1; ++z)
				for(int y = j; y <= i1; ++y)
					if(isLiquid(level, pos.set(i, y, z)) || isLiquid(level, pos.set(l, y, z)))
						return true;
			return false;
		}

		private static boolean isLiquid(WorldGenLevel level, BlockPos pos)
		{
			return !level.getFluidState(pos).isEmpty();
		}

		protected void placeDoor(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox, StructureAbyStrongholdPieces.Stronghold.Door par4EnumDoor, int par5, int par6, int par7)
		{
			switch (StructureAbyStrongholdPieces.SwitchDoor.doorEnum[par4EnumDoor.ordinal()])
			{
			case 1:
			default:
				generateBox(par1World, par3StructureBoundingBox, par5, par6, par7, par5 + 3 - 1, par6 + 3 - 1, par7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				break;
			case 2:
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.darklands_oak_door.get().defaultBlockState(), par5 + 1, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.darklands_oak_door.get().defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
				break;
			case 3:
				placeBlock(par1World, Blocks.AIR.defaultBlockState(), par5 + 1, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, Blocks.AIR.defaultBlockState(), par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_brick_fence.get().defaultBlockState(), par5 + 2, par6, par7, par3StructureBoundingBox);
				break;
			case 4:
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), par5 + 2, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, Blocks.IRON_DOOR.defaultBlockState(), par5 + 1, par6, par7, par3StructureBoundingBox);
				placeBlock(par1World, Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_button.get().defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), par5 + 2, par6 + 1, par7 + 1, par3StructureBoundingBox);
				placeBlock(par1World, ACBlocks.abyssal_stone_button.get().defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), par5 + 2, par6 + 1, par7 - 1, par3StructureBoundingBox);
			}

		}

		protected StructureAbyStrongholdPieces.Stronghold.Door getRandomDoor(RandomSource par1Random)
		{
			int i = par1Random.nextInt(5);

			switch (i)
			{
			case 0:
			case 1:
			default:
				return StructureAbyStrongholdPieces.Stronghold.Door.OPENING;
			case 2:
				return StructureAbyStrongholdPieces.Stronghold.Door.WOOD_DOOR;
			case 3:
				return StructureAbyStrongholdPieces.Stronghold.Door.GRATES;
			case 4:
				return StructureAbyStrongholdPieces.Stronghold.Door.IRON_DOOR;
			}
		}

		/**
		 * Gets the next component in any cardinal direction
		 */
		protected StructurePiece getNextComponentNormal(StructureAbyStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, StructurePieceAccessor par2List, RandomSource par3Random, int par4, int par5)
		{
			switch (getOrientation())
			{
			case SOUTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par4, boundingBox.minY() + par5, boundingBox.maxZ() + 1, getOrientation(), getGenDepth());
			case WEST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() + par5, boundingBox.minZ() + par4, getOrientation(), getGenDepth());
			case NORTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par4, boundingBox.minY() + par5, boundingBox.minZ() - 1, getOrientation(), getGenDepth());
			case EAST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() + par5, boundingBox.minZ() + par4, getOrientation(), getGenDepth());
			default:
				return null;
			}
		}

		/**
		 * Gets the next component in the +/- X direction
		 */
		protected StructurePiece getNextComponentX(StructureAbyStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, StructurePieceAccessor par2List, RandomSource par3Random, int par4, int par5)
		{
			switch (getOrientation())
			{
			case SOUTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() + par4, boundingBox.minZ() + par5, Direction.WEST, getGenDepth());
			case WEST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par5, boundingBox.minY() + par4, boundingBox.minZ() - 1, Direction.NORTH, getGenDepth());
			case NORTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() + par4, boundingBox.minZ() + par5, Direction.WEST, getGenDepth());
			case EAST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par5, boundingBox.minY() + par4, boundingBox.minZ() - 1, Direction.NORTH, getGenDepth());
			default:
				return null;
			}
		}

		/**
		 * Gets the next component in the +/- Z direction
		 */
		protected StructurePiece getNextComponentZ(StructureAbyStrongholdPieces.Stairs2 par1ComponentStrongholdStairs2, StructurePieceAccessor par2List, RandomSource par3Random, int par4, int par5)
		{
			switch (getOrientation())
			{
			case SOUTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() + par4, boundingBox.minZ() + par5, Direction.EAST, getGenDepth());
			case WEST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par5, boundingBox.minY() + par4, boundingBox.maxZ() + 1, Direction.SOUTH, getGenDepth());
			case NORTH:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() + par4, boundingBox.minZ() + par5, Direction.EAST, getGenDepth());
			case EAST:
				return StructureAbyStrongholdPieces.getNextValidComponent(par1ComponentStrongholdStairs2, par2List, par3Random, boundingBox.minX() + par5, boundingBox.minY() + par4, boundingBox.maxZ() + 1, Direction.SOUTH, getGenDepth());
			default:
				return null;
			}
		}

		/**
		 * returns false if the Structure Bounding Box goes below 10
		 */
		protected static boolean canStrongholdGoDeeper(BoundingBox par0StructureBoundingBox)
		{
			return par0StructureBoundingBox != null && par0StructureBoundingBox.minY() > 10;
		}

		public enum Door
		{
			OPENING,
			WOOD_DOOR,
			GRATES,
			IRON_DOOR
		}
	}

	public static class Crossing extends StructureAbyStrongholdPieces.Stronghold
	{
		private boolean field_74996_b;
		private boolean field_74997_c;
		private boolean field_74995_d;
		private boolean field_74999_h;
		public Crossing(CompoundTag tag)
		{
			super(tag);
			field_74996_b = tag.getBoolean("leftLow");
			field_74997_c = tag.getBoolean("leftHigh");
			field_74995_d = tag.getBoolean("rightLow");
			field_74999_h = tag.getBoolean("rightHigh");
		}

		public Crossing(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			field_143013_d = getRandomDoor(par2Random);
			boundingBox = par3StructureBoundingBox;
			field_74996_b = par2Random.nextBoolean();
			field_74997_c = par2Random.nextBoolean();
			field_74995_d = par2Random.nextBoolean();
			field_74999_h = par2Random.nextInt(3) > 0;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("leftLow", field_74996_b);
			par1NBTTagCompound.putBoolean("leftHigh", field_74997_c);
			par1NBTTagCompound.putBoolean("rightLow", field_74995_d);
			par1NBTTagCompound.putBoolean("rightHigh", field_74999_h);
		}

		
		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			int i = 3;
			int j = 5;

			if (getOrientation() == Direction.WEST || getOrientation() == Direction.NORTH)
			{
				i = 8 - i;
				j = 8 - j;
			}

			getNextComponentNormal((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, 5, 1);

			if (field_74996_b)
				getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, i, 1);

			if (field_74997_c)
				getNextComponentX((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, j, 7);

			if (field_74995_d)
				getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, i, 1);

			if (field_74999_h)
				getNextComponentZ((StructureAbyStrongholdPieces.Stairs2)par1StructureComponent, par2List, par3Random, j, 7);
		}

		public static StructureAbyStrongholdPieces.Crossing findValidPlacement(StructurePieceAccessor par1List, RandomSource par1Random, int par2, int par3, int par4, Direction par62, int par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -4, -3, 0, 10, 9, 11, par62);
			/**
			 * returns false if the Structure Bounding Box goes below 10
			 */
			return canStrongholdGoDeeper(structureboundingbox) && par1List.findCollisionPiece(structureboundingbox) == null ? new StructureAbyStrongholdPieces.Crossing(par6, par1Random, structureboundingbox, par62) : null;
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 9, 8, 10, true, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				placeDoor(par1World, par2Random, par3StructureBoundingBox, field_143013_d, 4, 3, 0);

				if (field_74996_b)
					generateBox(par1World, par3StructureBoundingBox, 0, 3, 1, 0, 5, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				if (field_74995_d)
					generateBox(par1World, par3StructureBoundingBox, 9, 3, 1, 9, 5, 3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				if (field_74997_c)
					generateBox(par1World, par3StructureBoundingBox, 0, 5, 7, 0, 7, 9, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				if (field_74999_h)
					generateBox(par1World, par3StructureBoundingBox, 9, 5, 7, 9, 7, 9, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				generateBox(par1World, par3StructureBoundingBox, 5, 1, 10, 7, 3, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 1, 2, 1, 8, 2, 6, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 4, 1, 5, 4, 4, 9, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 8, 1, 5, 8, 4, 9, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 1, 4, 7, 3, 4, 9, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				generateBox(par1World, par3StructureBoundingBox, 1, 3, 5, 3, 3, 6, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				if(true) {
					generateBox(par1World, par3StructureBoundingBox, 1, 3, 4, 3, 3, 4, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 1, 4, 6, 3, 4, 6, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
				}
				generateBox(par1World, par3StructureBoundingBox, 5, 1, 7, 7, 1, 8, false, par2Random, StructureAbyStrongholdPieces.strongholdStones);
				if(true) {
					generateBox(par1World, par3StructureBoundingBox, 5, 1, 9, 7, 1, 9, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 5, 2, 7, 7, 2, 7, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 4, 5, 7, 4, 5, 9, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 8, 5, 7, 8, 5, 9, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 5, 5, 7, 7, 5, 9, ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), ACBlocks.abyssal_stone_brick_slab.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
				}
				return true;
			}
		}
	}

	public static class Corridor extends StructureAbyStrongholdPieces.Stronghold
	{
		private int field_74993_a;
		public Corridor(CompoundTag tag)
		{
			super(tag);
			field_74993_a = tag.getInt("Steps");
		}

		public Corridor(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par6)
		{
			super(par1);
			setOrientation(par6);
			boundingBox = par3StructureBoundingBox;
			field_74993_a = par6 != Direction.NORTH && par6 != Direction.SOUTH ? par3StructureBoundingBox.getXSpan() : par3StructureBoundingBox.getZSpan();
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putInt("Steps", field_74993_a);
		}

		
		public static BoundingBox func_74992_a(StructurePieceAccessor par1List, RandomSource par1Random, int par2, int par3, int par4, Direction par6)
		{
			BoundingBox structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, 4, par6);
			StructurePiece structurecomponent = par1List.findCollisionPiece(structureboundingbox);

			if (structurecomponent == null)
				return null;
			else
			{
				if (structurecomponent.getBoundingBox().minY() == structureboundingbox.minY())
					for (int i1 = 3; i1 >= 1; --i1)
					{
						structureboundingbox = BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, i1 - 1, par6);

						if (!structurecomponent.getBoundingBox().intersects(structureboundingbox))
							return BoundingBox.orientBox(par2, par3, par4, -1, -1, 0, 5, 5, i1, par6);
					}

				return null;
			}
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		@Override
		public boolean addComponentParts(WorldGenLevel par1World, RandomSource par2Random, BoundingBox par3StructureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
				return false;
			else
			{
				for (int i = 0; i < field_74993_a; ++i)
				{
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 0, 0, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 0, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 0, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 0, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 0, i, par3StructureBoundingBox);

					for (int j = 1; j <= 3; ++j)
					{
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 0, j, i, par3StructureBoundingBox);
						placeBlock(par1World, Blocks.AIR.defaultBlockState(), 1, j, i, par3StructureBoundingBox);
						placeBlock(par1World, Blocks.AIR.defaultBlockState(), 2, j, i, par3StructureBoundingBox);
						placeBlock(par1World, Blocks.AIR.defaultBlockState(), 3, j, i, par3StructureBoundingBox);
						placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, j, i, par3StructureBoundingBox);
					}

					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 0, 4, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 1, 4, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 2, 4, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 3, 4, i, par3StructureBoundingBox);
					placeBlock(par1World, ACBlocks.abyssal_stone_bricks.get().defaultBlockState(), 4, 4, i, par3StructureBoundingBox);
				}

				return true;
			}
		}
	}

	static final class SwitchDoor
	{
		static final int[] doorEnum = new int[StructureAbyStrongholdPieces.Stronghold.Door.values().length];
		static
		{
			try
			{
				doorEnum[StructureAbyStrongholdPieces.Stronghold.Door.OPENING.ordinal()] = 1;
			}
			catch (NoSuchFieldError var4)
			{

			}

			try
			{
				doorEnum[StructureAbyStrongholdPieces.Stronghold.Door.WOOD_DOOR.ordinal()] = 2;
			}
			catch (NoSuchFieldError var3)
			{

			}

			try
			{
				doorEnum[StructureAbyStrongholdPieces.Stronghold.Door.GRATES.ordinal()] = 3;
			}
			catch (NoSuchFieldError var2)
			{

			}

			try
			{
				doorEnum[StructureAbyStrongholdPieces.Stronghold.Door.IRON_DOOR.ordinal()] = 4;
			}
			catch (NoSuchFieldError var1)
			{

			}
		}
	}

	static class PieceWeight
	{
		public Class<?> pieceClass;
		/**
		 * This basically keeps track of the 'epicness' of a structure. Epic structure components have a higher
		 * 'weight', and Structures may only grow up to a certain 'weight' before generation is stopped
		 */
		public final int pieceWeight;
		public int instancesSpawned;
		/**
		 * How many Structure Pieces of this type may spawn in a structure
		 */
		public int instancesLimit;
		public PieceWeight(Class<?> par1Class, int par2, int par3)
		{
			pieceClass = par1Class;
			pieceWeight = par2;
			instancesLimit = par3;
		}

		public boolean canSpawnMoreStructuresOfType(int par1)
		{
			return instancesLimit == 0 || instancesSpawned < instancesLimit;
		}

		public boolean canSpawnMoreStructures()
		{
			return instancesLimit == 0 || instancesSpawned < instancesLimit;
		}
	}
}
