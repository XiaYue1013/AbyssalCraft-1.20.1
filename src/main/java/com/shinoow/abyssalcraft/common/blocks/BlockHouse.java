/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks;

import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.common.world.gen.structure.StructureHouse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * House (from the hidden "house" ritual): the author builds the ethaxium house with it. Anyone else gets a 10% chance
 * of a small house without windows or a door (sometimes inverted), otherwise the block just disappears.
 */
public class BlockHouse extends Block {

	private static final VoxelShape SHAPE = Block.box(3.2, 0, 3.2, 12.8, 12.8, 12.8);

	public BlockHouse(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(level.isClientSide)
			return InteractionResult.SUCCESS;
		if(EntityUtil.isPlayerCoralium(player)) {
			new StructureHouse().generate((ServerLevel) level, level.random, pos);
			return InteractionResult.CONSUME;
		}
		if(level.random.nextFloat() > 0.1F) {
			player.sendSystemMessage(Component.literal("Not in a dev environment, aborting structure generation test."));
			level.removeBlock(pos, false);
			return InteractionResult.CONSUME;
		}
		boolean inverted = level.random.nextBoolean();
		player.sendSystemMessage(Component.literal(inverted ? "Inverted house... still no windows or a door..." : "A house! Without windows! Nor a door!"));
		buildSmallHouse(level, pos, inverted);
		return InteractionResult.CONSUME;
	}

	/** 1.12.2's 5x5 house north of the block, entered by a stair step where the block was */
	public static void buildSmallHouse(Level level, BlockPos pos, boolean inverted) {
		BlockState planks = inverted ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.OAK_PLANKS.defaultBlockState();
		BlockState cobble = inverted ? Blocks.OAK_PLANKS.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState();
		BlockState stairs = (inverted ? Blocks.OAK_STAIRS : Blocks.COBBLESTONE_STAIRS).defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
		BlockState log = inverted ? Blocks.STONE.defaultBlockState() : Blocks.OAK_LOG.defaultBlockState();
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		level.removeBlock(pos, false);
		set(level, x, y, z - 1, stairs);
		for(int i = 0; i <= 4; i++)
			for(int j = 0; j <= 4; j++)
				set(level, x - 2 + i, y, z - (2 + j), cobble);
		for(int i = 0; i <= 2; i++) {
			set(level, x - 1, y + 1 + i, z - 2, planks);
			set(level, x + 1, y + 1 + i, z - 2, planks);
			for(int j = 0; j <= 2; j++)
				set(level, x - 1 + j, y + 1 + i, z - 6, planks);
		}
		for(int i = 0; i <= 4; i++)
			for(int j = 0; j <= 2; j++) {
				set(level, x - 2, y + 1 + j, z - 2 - i, planks);
				set(level, x + 2, y + 1 + j, z - 2 - i, planks);
			}
		set(level, x, y + 3, z - 2, planks);
		for(int i = 0; i <= 4; i++) {
			set(level, x - 2, y + 4, z - 2 - i, log);
			set(level, x + 2, y + 4, z - 2 - i, log);
		}
		for(int i = 0; i <= 2; i++)
			for(int j = 0; j <= 1; j++)
				set(level, x - 1 + i, y + 4, z - (2 + j * 4), log);
		for(int i = 0; i <= 2; i++)
			for(int j = 0; j <= 2; j++)
				set(level, x - 1 + i, y + 4, z - (3 + j), planks);
		for(int i = 0; i <= 2; i++)
			for(int j = 0; j <= 1; j++) {
				set(level, x - 2, y + 1 + i, z - (2 + j * 4), cobble);
				set(level, x + 2, y + 1 + i, z - (2 + j * 4), cobble);
			}
	}

	private static void set(Level level, int x, int y, int z, BlockState state) {
		level.setBlock(new BlockPos(x, y, z), state, Block.UPDATE_CLIENTS);
	}
}
