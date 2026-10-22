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
 *     bridged by Mine (addChildren -> buildComponent, postProcess -> addComponentParts)
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.world.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.init.ACStructures;
import com.shinoow.abyssalcraft.lib.ACLoot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class StructureDreadlandsMinePieces
{

	/** Loads a saved piece (all pieces share one piece type, "Kind" tells them apart) */
	public static StructurePiece load(CompoundTag tag)
	{
		return switch(tag.getString("Kind")) {
		case "Corridor" -> new Corridor(tag);
		case "Cross" -> new Cross(tag);
		case "Room" -> new Room(tag);
		case "Stairs" -> new Stairs(tag);
		default -> throw new IllegalArgumentException("Unknown Dreadlands mine piece " + tag.getString("Kind"));
		};
	}

	/** 1.12.2 bounding boxes could be changed in place */
	static class MutableBox
	{
		int minX, minY, minZ, maxX, maxY, maxZ;

		MutableBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
		{
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		BoundingBox box()
		{
			return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	public abstract static class Mine extends StructurePiece
	{
		protected Mine(int genDepth)
		{
			super(ACStructures.DREADLANDS_MINE_PIECE.get(), genDepth, new BoundingBox(0, 0, 0, 0, 0, 0));
		}

		protected Mine(CompoundTag tag)
		{
			super(ACStructures.DREADLANDS_MINE_PIECE.get(), tag);
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag)
		{
			tag.putString("Kind", getClass().getSimpleName());
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
					if(!level.getFluidState(pos.set(x, j, z)).isEmpty() || !level.getFluidState(pos.set(x, i1, z)).isEmpty())
						return true;
			for(int x = i; x <= l; ++x)
				for(int y = j; y <= i1; ++y)
					if(!level.getFluidState(pos.set(x, y, k)).isEmpty() || !level.getFluidState(pos.set(x, y, j1)).isEmpty())
						return true;
			for(int z = k; z <= j1; ++z)
				for(int y = j; y <= i1; ++y)
					if(!level.getFluidState(pos.set(i, y, z)).isEmpty() || !level.getFluidState(pos.set(l, y, z)).isEmpty())
						return true;
			return false;
		}
	}


	private static StructurePiece getRandomComponent(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5, int par6)
	{
		int j1 = par1Random.nextInt(100);
		BoundingBox structureboundingbox;

		if (j1 >= 80)
		{
			structureboundingbox = StructureDreadlandsMinePieces.Cross.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

			if (structureboundingbox != null)
				return new StructureDreadlandsMinePieces.Cross(par6, par1Random, structureboundingbox, par5);
		}
		else if (j1 >= 70)
		{
			structureboundingbox = StructureDreadlandsMinePieces.Stairs.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

			if (structureboundingbox != null)
				return new StructureDreadlandsMinePieces.Stairs(par6, par1Random, structureboundingbox, par5);
		}
		else
		{
			structureboundingbox = StructureDreadlandsMinePieces.Corridor.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);

			if (structureboundingbox != null)
				return new StructureDreadlandsMinePieces.Corridor(par6, par1Random, structureboundingbox, par5);
		}

		return null;
	}

	private static StructurePiece getNextMineShaftComponent(StructurePiece par0StructureComponent, StructurePieceAccessor par1List, RandomSource par2Random, int par3, int par4, int par5, Direction par6, int par7)
	{
		if (par7 > 8 || !(Math.abs(par3 - par0StructureComponent.getBoundingBox().minX()) <= 80 && Math.abs(par5 - par0StructureComponent.getBoundingBox().minZ()) <= 80))
			return null;
		else {
			StructurePiece structurecomponent1 = getRandomComponent(par1List, par2Random, par3, par4, par5, par6, par7 + 1);

			if (structurecomponent1 != null)
			{
				par1List.addPiece(structurecomponent1);
				structurecomponent1.addChildren(par0StructureComponent, par1List, par2Random);
			}

			return structurecomponent1;
		}
	}

	public static class Corridor extends Mine
	{
		private boolean hasRails;
		/**
		 * A count of the different sections of this mine. The space between ceiling supports.
		 */
		private int sectionCount;
		public Corridor(CompoundTag tag)
		{
			super(tag);
			hasRails = tag.getBoolean("hr");
			sectionCount = tag.getInt("Num");
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("hr", hasRails);
			par1NBTTagCompound.putInt("Num", sectionCount);
		}


		public Corridor(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			boundingBox = par3StructureBoundingBox;
			hasRails = par2Random.nextInt(3) == 0;

			if (getOrientation() != Direction.NORTH && getOrientation() != Direction.SOUTH)
				sectionCount = par3StructureBoundingBox.getXSpan() / 5;
			else
				sectionCount = par3StructureBoundingBox.getZSpan() / 5;
		}

		@SuppressWarnings("incomplete-switch")
		public static BoundingBox findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5)
		{
			MutableBox structureboundingbox = new MutableBox(par2, par3, par4, par2, par3 + 2, par4);
			int i1;

			for (i1 = par1Random.nextInt(3) + 2; i1 > 0; --i1)
			{
				int j1 = i1 * 5;

				switch (par5)
				{
				case SOUTH:
					structureboundingbox.maxX = par2 + 2;
					structureboundingbox.maxZ = par4 + j1 - 1;
					break;
				case WEST:
					structureboundingbox.minX = par2 - (j1 - 1);
					structureboundingbox.maxZ = par4 + 2;
					break;
				case NORTH:
					structureboundingbox.maxX = par2 + 2;
					structureboundingbox.minZ = par4 - (j1 - 1);
					break;
				case EAST:
					structureboundingbox.maxX = par2 + j1 - 1;
					structureboundingbox.maxZ = par4 + 2;
				}

				if (par0List.findCollisionPiece(structureboundingbox.box()) == null)
					break;
			}

			return i1 > 0 ? structureboundingbox.box() : null;
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		@SuppressWarnings("incomplete-switch")
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			int i = getGenDepth();
			int j = par3Random.nextInt(4);

			switch (getOrientation())
			{
			case SOUTH:
				if (j <= 1)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.maxZ() + 1, getOrientation(), i);
				else if (j == 2)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.maxZ() - 3, Direction.WEST, i);
				else
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.maxZ() - 3, Direction.EAST, i);

				break;
			case WEST:
				if (j <= 1)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ(), getOrientation(), i);
				else if (j == 2)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ() - 1, Direction.NORTH, i);
				else
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.maxZ() + 1, Direction.SOUTH, i);

				break;
			case NORTH:
				if (j <= 1)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ() - 1, getOrientation(), i);
				else if (j == 2)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ(), Direction.WEST, i);
				else
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ(), Direction.EAST, i);

				break;
			case EAST:
				if (j <= 1)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ(), getOrientation(), i);
				else if (j == 2)
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() - 3, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.minZ() - 1, Direction.NORTH, i);
				else
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() - 3, boundingBox.minY() - 1 + par3Random.nextInt(3), boundingBox.maxZ() + 1, Direction.SOUTH, i);
			}

			if (i < 8)
			{
				int k;
				int l;

				if (getOrientation() != Direction.NORTH && getOrientation() != Direction.SOUTH)
					for (k = boundingBox.minX() + 3; k + 3 <= boundingBox.maxX(); k += 5)
					{
						l = par3Random.nextInt(5);

						if (l == 0)
							StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, k, boundingBox.minY(), boundingBox.minZ() - 1, Direction.NORTH, i + 1);
						else if (l == 1)
							StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, k, boundingBox.minY(), boundingBox.maxZ() + 1, Direction.SOUTH, i + 1);
					}
				else
					for (k = boundingBox.minZ() + 3; k + 3 <= boundingBox.maxZ(); k += 5)
					{
						l = par3Random.nextInt(5);

						if (l == 0)
							StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY(), k, Direction.SOUTH, i + 1);
						else if (l == 1)
							StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY(), k, Direction.WEST, i + 1);
					}
			}
		}

		/**
		 * Used to generate chests with items in it. ex: Temple Chests, Village Blacksmith Chests, Mineshaft Chests.
		 */
		/** Mineshaft chests are minecarts with a chest on a rail */
		@Override
		protected boolean createChest(WorldGenLevel world, BoundingBox structureboundingbox, RandomSource rand, int x, int y, int z, ResourceLocation loot)
		{
			BlockPos blockpos = getWorldPos(x, y, z);
			if (structureboundingbox.isInside(blockpos) && world.getBlockState(blockpos).isAir())
			{
				BlockState iblockstate = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, rand.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
				placeBlock(world, iblockstate, x, y, z, structureboundingbox);
				MinecartChest minecart = new MinecartChest(world.getLevel(), blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
				minecart.setLootTable(loot, rand.nextLong());
				world.addFreshEntity(minecart);
				return true;
			} else
				return false;
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
				int i = sectionCount * 5 - 1;
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 0, 2, 1, i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateMaybeBox(par1World, par3StructureBoundingBox, par2Random, 0.8F, 0, 2, 0, 2, 2, i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false, false);

				int j;
				int k;

				for (j = 0; j < sectionCount; ++j)
				{
					k = 2 + j * 5;
					generateBox(par1World, par3StructureBoundingBox, 0, 0, k, 0, 1, k, ACBlocks.dreadlands_wood_fence.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, 2, 0, k, 2, 1, k, ACBlocks.dreadlands_wood_fence.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

					if (par2Random.nextInt(4) == 0)
					{
						generateBox(par1World, par3StructureBoundingBox, 0, 2, k, 0, 2, k, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
						generateBox(par1World, par3StructureBoundingBox, 2, 2, k, 2, 2, k, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					} else
						generateBox(par1World, par3StructureBoundingBox, 0, 2, k, 2, 2, k, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

					maybeGenerateBlock(par1World, par3StructureBoundingBox, par2Random, 0.05F, 1, 2, k - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
					maybeGenerateBlock(par1World, par3StructureBoundingBox, par2Random, 0.05F, 1, 2, k + 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));

					if (par2Random.nextInt(100) == 0)
						createChest(par1World, par3StructureBoundingBox, par2Random, 2, 0, k - 1, ACLoot.CHEST_DREADLANDS_MINESHAFT);

					if (par2Random.nextInt(100) == 0)
						createChest(par1World, par3StructureBoundingBox, par2Random, 2, 0, k + 1, ACLoot.CHEST_DREADLANDS_MINESHAFT);
				}

				for (j = 0; j <= 2; ++j)
					for (k = 0; k <= i; ++k)
					{
						byte b0 = -1;
						BlockState block1 = getBlock(par1World, j, b0, k, par3StructureBoundingBox);

						if (block1.isAir())
						{
							byte b1 = -1;
							placeBlock(par1World, ACBlocks.dreadlands_planks.get().defaultBlockState(), j, b1, k, par3StructureBoundingBox);
						}
					}

				if (hasRails){

					BlockState iblockstate = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

					for (j = 0; j <= i; ++j)
					{
						BlockState block = getBlock(par1World, 1, -1, j, par3StructureBoundingBox);

						if (!block.isAir() && block.isSolid())
							maybeGenerateBlock(par1World, par3StructureBoundingBox, par2Random, 0.7F, 1, 0, j, iblockstate);
					}
				}

				return true;
			}
		}
	}

	public static class Cross extends Mine
	{
		private Direction corridorDirection;
		private boolean isMultipleFloors;
		public Cross(CompoundTag tag)
		{
			super(tag);
			isMultipleFloors = tag.getBoolean("tf");
			corridorDirection = Direction.from2DDataValue(tag.getInt("D"));
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			par1NBTTagCompound.putBoolean("tf", isMultipleFloors);
			par1NBTTagCompound.putInt("D", corridorDirection.get2DDataValue());
		}


		public Cross(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			corridorDirection = par4;
			boundingBox = par3StructureBoundingBox;
			isMultipleFloors = par3StructureBoundingBox.getYSpan() > 3;
		}

		@SuppressWarnings("incomplete-switch")
		public static BoundingBox findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5)
		{
			MutableBox structureboundingbox = new MutableBox(par2, par3, par4, par2, par3 + 2, par4);

			if (par1Random.nextInt(4) == 0)
				structureboundingbox.maxY += 4;

			switch (par5)
			{
			case SOUTH:
				structureboundingbox.minX = par2 - 1;
				structureboundingbox.maxX = par2 + 3;
				structureboundingbox.maxZ = par4 + 4;
				break;
			case WEST:
				structureboundingbox.minX = par2 - 4;
				structureboundingbox.minZ = par4 - 1;
				structureboundingbox.maxZ = par4 + 3;
				break;
			case NORTH:
				structureboundingbox.minX = par2 - 1;
				structureboundingbox.maxX = par2 + 3;
				structureboundingbox.minZ = par4 - 4;
				break;
			case EAST:
				structureboundingbox.maxX = par2 + 4;
				structureboundingbox.minZ = par4 - 1;
				structureboundingbox.maxZ = par4 + 3;
			}

			return par0List.findCollisionPiece(structureboundingbox.box()) != null ? null : structureboundingbox.box();
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		@SuppressWarnings("incomplete-switch")
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			int i = getGenDepth();

			switch (corridorDirection)
			{
			case SOUTH:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.maxZ() + 1, Direction.SOUTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.WEST, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.EAST, i);
				break;
			case WEST:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ() - 1, Direction.NORTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.maxZ() + 1, Direction.SOUTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.WEST, i);
				break;
			case NORTH:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ() - 1, Direction.NORTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.WEST, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.EAST, i);
				break;
			case EAST:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ() - 1, Direction.NORTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.maxZ() + 1, Direction.SOUTH, i);
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY(), boundingBox.minZ() + 1, Direction.EAST, i);
			}

			if (isMultipleFloors)
			{
				if (par3Random.nextBoolean())
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY() + 3 + 1, boundingBox.minZ() - 1, Direction.NORTH, i);

				if (par3Random.nextBoolean())
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() + 3 + 1, boundingBox.minZ() + 1, Direction.WEST, i);

				if (par3Random.nextBoolean())
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() + 3 + 1, boundingBox.minZ() + 1, Direction.EAST, i);

				if (par3Random.nextBoolean())
					StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + 1, boundingBox.minY() + 3 + 1, boundingBox.maxZ() + 1, Direction.SOUTH, i);
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
				if (isMultipleFloors)
				{
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX() - 1, boundingBox.minY() + 3 - 1, boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ() + 1, boundingBox.maxX(), boundingBox.minY() + 3 - 1, boundingBox.maxZ() - 1, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.maxY() - 2, boundingBox.minZ(), boundingBox.maxX() - 1, boundingBox.maxY(), boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.maxY() - 2, boundingBox.minZ() + 1, boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ() - 1, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.minY() + 3, boundingBox.minZ() + 1, boundingBox.maxX() - 1, boundingBox.minY() + 3, boundingBox.maxZ() - 1, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				}
				else
				{
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX() - 1, boundingBox.maxY(), boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
					generateBox(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ() + 1, boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ() - 1, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				}

				generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.minZ() + 1, boundingBox.minX() + 1, boundingBox.maxY(), boundingBox.minZ() + 1, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, boundingBox.minX() + 1, boundingBox.minY(), boundingBox.maxZ() - 1, boundingBox.minX() + 1, boundingBox.maxY(), boundingBox.maxZ() - 1, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, boundingBox.maxX() - 1, boundingBox.minY(), boundingBox.minZ() + 1, boundingBox.maxX() - 1, boundingBox.maxY(), boundingBox.minZ() + 1, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, boundingBox.maxX() - 1, boundingBox.minY(), boundingBox.maxZ() - 1, boundingBox.maxX() - 1, boundingBox.maxY(), boundingBox.maxZ() - 1, ACBlocks.dreadlands_planks.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				for (int i = boundingBox.minX(); i <= boundingBox.maxX(); ++i)
					for (int j = boundingBox.minZ(); j <= boundingBox.maxZ(); ++j)
						if (getBlock(par1World, i, boundingBox.minY() - 1, j, par3StructureBoundingBox).isAir())
							placeBlock(par1World, ACBlocks.dreadlands_planks.get().defaultBlockState(), i, boundingBox.minY() - 1, j, par3StructureBoundingBox);

				return true;
			}
		}
	}

	public static class Room extends Mine
	{
		/** StructurePieceAccessor of other Mineshaft components linked to this room. */
		private List<BoundingBox> roomsLinkedToTheRoom = new LinkedList<>();
		public Room(CompoundTag tag)
		{
			super(tag);
			ListTag entrances = tag.getList("Entrances", Tag.TAG_INT_ARRAY);
			for (int i = 0; i < entrances.size(); ++i) {
				int[] a = entrances.getIntArray(i);
				roomsLinkedToTheRoom.add(new BoundingBox(a[0], a[1], a[2], a[3], a[4], a[5]));
			}
		}

		public Room(int par1, RandomSource par2Random, int par3, int par4)
		{
			super(par1);
			boundingBox = new BoundingBox(par3, 50, par4, par3 + 7 + par2Random.nextInt(6), 54 + par2Random.nextInt(6), par4 + 7 + par2Random.nextInt(6));
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			int i = getGenDepth();
			int k = boundingBox.getYSpan() - 3 - 1;

			if (k <= 0)
				k = 1;

			int j;
			StructurePiece structurecomponent1;
			BoundingBox structureboundingbox;

			for (j = 0; j < boundingBox.getXSpan(); j += 4)
			{
				j += par3Random.nextInt(boundingBox.getXSpan());

				if (j + 3 > boundingBox.getXSpan())
					break;

				structurecomponent1 = StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + j, boundingBox.minY() + par3Random.nextInt(k) + 1, boundingBox.minZ() - 1, Direction.NORTH, i);

				if (structurecomponent1 != null)
				{
					structureboundingbox = structurecomponent1.getBoundingBox();
					roomsLinkedToTheRoom.add(new BoundingBox(structureboundingbox.minX(), structureboundingbox.minY(), boundingBox.minZ(), structureboundingbox.maxX(), structureboundingbox.maxY(), boundingBox.minZ() + 1));
				}
			}

			for (j = 0; j < boundingBox.getXSpan(); j += 4)
			{
				j += par3Random.nextInt(boundingBox.getXSpan());

				if (j + 3 > boundingBox.getXSpan())
					break;

				structurecomponent1 = StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() + j, boundingBox.minY() + par3Random.nextInt(k) + 1, boundingBox.maxZ() + 1, Direction.SOUTH, i);

				if (structurecomponent1 != null)
				{
					structureboundingbox = structurecomponent1.getBoundingBox();
					roomsLinkedToTheRoom.add(new BoundingBox(structureboundingbox.minX(), structureboundingbox.minY(), boundingBox.maxZ() - 1, structureboundingbox.maxX(), structureboundingbox.maxY(), boundingBox.maxZ()));
				}
			}

			for (j = 0; j < boundingBox.getZSpan(); j += 4)
			{
				j += par3Random.nextInt(boundingBox.getZSpan());

				if (j + 3 > boundingBox.getZSpan())
					break;

				structurecomponent1 = StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY() + par3Random.nextInt(k) + 1, boundingBox.minZ() + j, Direction.WEST, i);

				if (structurecomponent1 != null)
				{
					structureboundingbox = structurecomponent1.getBoundingBox();
					roomsLinkedToTheRoom.add(new BoundingBox(boundingBox.minX(), structureboundingbox.minY(), structureboundingbox.minZ(), boundingBox.minX() + 1, structureboundingbox.maxY(), structureboundingbox.maxZ()));
				}
			}

			for (j = 0; j < boundingBox.getZSpan(); j += 4)
			{
				j += par3Random.nextInt(boundingBox.getZSpan());

				if (j + 3 > boundingBox.getZSpan())
					break;

				structurecomponent1 = StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY() + par3Random.nextInt(k) + 1, boundingBox.minZ() + j, Direction.EAST, i);

				if (structurecomponent1 != null)
				{
					structureboundingbox = structurecomponent1.getBoundingBox();
					roomsLinkedToTheRoom.add(new BoundingBox(boundingBox.maxX() - 1, structureboundingbox.minY(), structureboundingbox.minZ(), boundingBox.maxX(), structureboundingbox.maxY(), structureboundingbox.maxZ()));
				}
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
				generateBox(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ(), ACBlocks.dreadlands_grass.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), true);
				generateBox(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.minY() + 1, boundingBox.minZ(), boundingBox.maxX(), Math.min(boundingBox.minY() + 3, boundingBox.maxY()), boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				Iterator<BoundingBox> iterator = roomsLinkedToTheRoom.iterator();

				while (iterator.hasNext())
				{
					BoundingBox structureboundingbox1 = iterator.next();
					generateBox(par1World, par3StructureBoundingBox, structureboundingbox1.minX(), structureboundingbox1.maxY() - 2, structureboundingbox1.minZ(), structureboundingbox1.maxX(), structureboundingbox1.maxY(), structureboundingbox1.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				}

				generateUpperHalfSphere(par1World, par3StructureBoundingBox, boundingBox.minX(), boundingBox.minY() + 4, boundingBox.minZ(), boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), false);
				return true;
			}
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);
			ListTag entrances = new ListTag();
			for (BoundingBox box : roomsLinkedToTheRoom)
				entrances.add(new IntArrayTag(new int[] {box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()}));
			par1NBTTagCompound.put("Entrances", entrances);
		}

	}

	public static class Stairs extends Mine
	{
		public Stairs(CompoundTag tag)
		{
			super(tag);
		}

		public Stairs(int par1, RandomSource par2Random, BoundingBox par3StructureBoundingBox, Direction par4)
		{
			super(par1);
			setOrientation(par4);
			boundingBox = par3StructureBoundingBox;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag par1NBTTagCompound)
		{
			super.addAdditionalSaveData(context, par1NBTTagCompound);}

		/**
		 * Trys to find a valid place to put this component.
		 */
		@SuppressWarnings("incomplete-switch")
		public static BoundingBox findValidPlacement(StructurePieceAccessor par0List, RandomSource par1Random, int par2, int par3, int par4, Direction par5)
		{
			MutableBox structureboundingbox = new MutableBox(par2, par3 - 5, par4, par2, par3 + 2, par4);

			switch (par5)
			{
			case SOUTH:
				structureboundingbox.maxX = par2 + 2;
				structureboundingbox.maxZ = par4 + 8;
				break;
			case WEST:
				structureboundingbox.minX = par2 - 8;
				structureboundingbox.maxZ = par4 + 2;
				break;
			case NORTH:
				structureboundingbox.maxX = par2 + 2;
				structureboundingbox.minZ = par4 - 8;
				break;
			case EAST:
				structureboundingbox.maxX = par2 + 8;
				structureboundingbox.maxZ = par4 + 2;
			}

			return par0List.findCollisionPiece(structureboundingbox.box()) != null ? null : structureboundingbox.box();
		}

		/**
		 * Initiates construction of the Structure Component picked, at the current Location of StructGen
		 */
		@Override
		@SuppressWarnings("incomplete-switch")
		public void buildComponent(StructurePiece par1StructureComponent, StructurePieceAccessor par2List, RandomSource par3Random)
		{
			int i = getGenDepth();

			switch (getOrientation())
			{
			case SOUTH:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ() + 1, Direction.SOUTH, i);
				break;
			case WEST:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX() - 1, boundingBox.minY(), boundingBox.minZ(), Direction.WEST, i);
				break;
			case NORTH:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ() - 1, Direction.NORTH, i);
				break;
			case EAST:
				StructureDreadlandsMinePieces.getNextMineShaftComponent(par1StructureComponent, par2List, par3Random, boundingBox.maxX() + 1, boundingBox.minY(), boundingBox.minZ(), Direction.EAST, i);
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
				generateBox(par1World, par3StructureBoundingBox, 0, 5, 0, 2, 7, 1, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
				generateBox(par1World, par3StructureBoundingBox, 0, 0, 7, 2, 2, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				for (int i = 0; i < 5; ++i)
					generateBox(par1World, par3StructureBoundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

				return true;
			}
		}
	}
}
