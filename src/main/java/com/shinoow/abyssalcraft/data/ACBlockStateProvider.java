/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - data generation
 ******************************************************************************/
package com.shinoow.abyssalcraft.data;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;
import com.shinoow.abyssalcraft.common.blocks.BlockStateTransformer;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.init.BlockInfo;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Generates blockstates and block models from {@link BlockHandler#INFO}. Textures are the upstream texture names
 * (imported locally by tools/import_assets.py); custom-shaped models are referenced unchecked.
 */
public class ACBlockStateProvider extends BlockStateProvider {

	public ACBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, AbyssalCraft.modid, helper);
	}

	private ResourceLocation tex(String name) {
		return name.contains(":") ? new ResourceLocation(name) : modLoc("block/" + name);
	}

	private ModelFile existing(String model) {
		return new ModelFile.UncheckedModelFile(model.contains(":") ? new ResourceLocation(model) : modLoc("block/" + model));
	}

	@Override
	protected void registerStatesAndModels() {
		BlockHandler.INFO.forEach((name, info) -> {
			Block block = info.block.get();
			String[] t = info.textures;
			switch(info.model) {
			case CUBE:
				simpleBlock(block, models().cubeAll(name, tex(t[0])));
				break;
			case GLASS:
				simpleBlock(block, models().cubeAll(name, tex(t[0])).renderType("translucent"));
				break;
			case COLUMN:
				simpleBlock(block, models().cubeColumn(name, tex(t[0]), tex(t[1])));
				break;
			case BOTTOM_TOP:
				simpleBlock(block, models().cubeBottomTop(name, tex(t[0]), tex(t[1]), tex(t[2])));
				break;
			case PILLAR:
				axisBlock((RotatedPillarBlock) block, tex(t[0]), tex(t[1]));
				break;
			case SLAB:
				slabBlock((SlabBlock) block, BlockHandler.INFO.entrySet().stream().filter(e -> e.getValue().block == info.parent || e.getValue().block.get() == info.parent.get())
						.map(e -> modLoc("block/" + e.getKey())).findFirst().orElse(tex(t[0])), tex(t[0]));
				break;
			case STAIRS:
				stairsBlock((StairBlock) block, tex(t[0]));
				break;
			case WALL:
				wallBlock((WallBlock) block, tex(t[0]));
				models().wallInventory(name + "_inventory", tex(t[0]));
				break;
			case FENCE:
				fenceBlock((FenceBlock) block, tex(t[0]));
				models().fenceInventory(name + "_inventory", tex(t[0]));
				break;
			case BUTTON:
				buttonBlock((ButtonBlock) block, tex(t[0]));
				models().buttonInventory(name + "_inventory", tex(t[0]));
				break;
			case PRESSURE_PLATE:
				pressurePlateBlock((PressurePlateBlock) block, tex(t[0]));
				break;
			case DOOR:
				doorBlockWithRenderType((DoorBlock) block, tex(t[0]), tex(t[1]), "cutout");
				break;
			case LEAVES:
				simpleBlock(block, models().withExistingParent(name, mcLoc("block/leaves")).texture("all", tex(t[0])).renderType("cutout_mipped"));
				break;
			case CROSS:
				if(block instanceof SaplingBlock)
					getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder().modelFile(models().cross(name, tex(t[0])).renderType("cutout")).build());
				else
					simpleBlock(block, models().cross(name, tex(t[0])).renderType("cutout"));
				break;
			case CUSTOM:
				if(block instanceof RotatedPillarBlock)
					axisBlock((RotatedPillarBlock) block, existing(t[0]), existing(t[0]));
				else
					simpleBlock(block, existing(t[0]));
				break;
			case ORIENTABLE_LIT: {
				ModelFile off = models().orientable(name, tex(t[2]), tex(t[0]), tex(t[3]));
				ModelFile on = models().orientable(name + "_on", tex(t[2]), tex(t[1]), tex(t[3]));
				horizontalBlock(block, s -> s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? on : off);
				break;
			}
			case PORTAL: {
				ModelFile ns = models().withExistingParent(name + "_ns", mcLoc("block/nether_portal_ns")).texture("particle", tex(t[0])).texture("portal", tex(t[0])).renderType("translucent");
				ModelFile ew = models().withExistingParent(name + "_ew", mcLoc("block/nether_portal_ew")).texture("particle", tex(t[0])).texture("portal", tex(t[0])).renderType("translucent");
				getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder().modelFile(s.getValue(BlockACPortal.AXIS) == Direction.Axis.X ? ns : ew).build());
				break;
			}
			case LAYERS: {
				ModelFile full = models().cubeAll(name, tex(t[0]));
				getVariantBuilder(block).forAllStates(s -> {
					int layers = s.getValue(SnowLayerBlock.LAYERS);
					return ConfiguredModel.builder().modelFile(layers == 8 ? full : models().withExistingParent(name + "_height" + layers * 2, mcLoc("block/snow_height" + layers * 2))
							.texture("texture", tex(t[0])).texture("particle", tex(t[0]))).build();
				});
				break;
			}
			case RELAY: {
				ModelFile relay = t.length > 1 ? models().withExistingParent(name, modLoc("block/" + t[0])).texture("0", tex(t[1])).texture("particle", tex(t[1]))
						: existing(t[0]);
				getVariantBuilder(block).forAllStates(s -> {
					Direction dir = s.getValue(BlockStateProperties.FACING);
					int x = dir == Direction.UP ? 270 : dir == Direction.DOWN ? 90 : 0;
					int y = dir.getAxis().isHorizontal() ? ((int) dir.toYRot() + 180) % 360 : 0;
					return ConfiguredModel.builder().modelFile(relay).rotationX(x).rotationY(y).build();
				});
				break;
			}
			case FLUID:
				simpleBlock(block, models().getBuilder(name).texture("particle", tex(t[0])));
				break;
			case ORIENTABLE:
				horizontalBlock(block, models().orientable(name, tex(t[1]), tex(t[0]), tex(t[2])));
				break;
			case CUSTOM_FACING:
				horizontalBlock(block, existing(t[0]));
				break;
			case NONE:
				break;
			}
		});
		specialModels();
	}

	/**
	 * Blocks whose models aren't plain shapes of a single texture
	 */
	private void specialModels() {
		// the invisible single mob spawners: an empty model (vanilla's barrier style), only the break particle
		for(var spawner : com.shinoow.abyssalcraft.init.BlockHandler.MOB_SPAWNERS)
			simpleBlock(spawner.get(), models().getBuilder(spawner.getId().getPath()).texture("particle", mcLoc("block/spawner")));
		// Dreadlands Grass (upstream: grass model with snowy variant)
		ModelFile dreadGrass = models().withExistingParent("dreadlands_grass", mcLoc("block/grass_block"))
				.texture("particle", tex("dreadlandsdirt")).texture("bottom", tex("dreadlandsdirt"))
				.texture("top", tex("drgtop")).texture("side", tex("drgsides")).texture("overlay", tex("drgsides"));
		ModelFile dreadGrassSnowy = models().cubeBottomTop("dreadlands_grass_snowy", tex("drgsides_snowed"), tex("dreadlandsdirt"), mcLoc("block/snow"));
		getVariantBuilder(BlockHandler.DREADLANDS_GRASS.get()).forAllStates(s -> ConfiguredModel.builder()
				.modelFile(s.getValue(BlockStateProperties.SNOWY) ? dreadGrassSnowy : dreadGrass).build());

		// Fused Abyssal Sand (upstream: grass-style model)
		simpleBlock(BlockHandler.FUSED_ABYSSAL_SAND.get(), models().withExistingParent("fused_abyssal_sand", mcLoc("block/grass_block"))
				.texture("particle", tex("abyssalsand")).texture("bottom", tex("abyssalsand"))
				.texture("top", tex("fusedabyssalsand")).texture("side", tex("fusedabyssalsand_side")).texture("overlay", tex("fusedabyssalsand_side")));

		// Mimic Fire looks exactly like regular fire (same multipart as the vanilla blockstate)
		mimicFire();

		// State Transformer (upstream model faces south) with the stone tablet on top while one is inserted
		MultiPartBlockStateBuilder transformer = getMultipartBuilder(BlockHandler.STATE_TRANSFORMER.get());
		for(Direction dir : Direction.Plane.HORIZONTAL) {
			int rot = (int) dir.toYRot();
			transformer.part().modelFile(existing("statetransformer")).rotationY(rot).addModel().condition(BlockStateTransformer.FACING, dir).end();
			transformer.part().modelFile(existing("stonetablet")).rotationY(rot).addModel().condition(BlockStateTransformer.FACING, dir)
			.condition(BlockStateTransformer.TABLET, true).end();
		}

		// Monolith Pillar (upstream: wall post model)
		simpleBlock(BlockHandler.MONOLITH_PILLAR.get(), models().wallPost("monolith_pillar", tex("monolithpillar")));
	}

	private void mimicFire() {
		Block fire = BlockHandler.MIMIC_FIRE.get();
		MultiPartBlockStateBuilder builder = getMultipartBuilder(fire);
		ModelFile[] floor = {existing("minecraft:block/fire_floor0"), existing("minecraft:block/fire_floor1")};
		ModelFile[] side = {existing("minecraft:block/fire_side0"), existing("minecraft:block/fire_side1"), existing("minecraft:block/fire_side_alt0"), existing("minecraft:block/fire_side_alt1")};
		ModelFile[] up = {existing("minecraft:block/fire_up0"), existing("minecraft:block/fire_up1"), existing("minecraft:block/fire_up_alt0"), existing("minecraft:block/fire_up_alt1")};

		builder.part().modelFile(floor[0]).nextModel().modelFile(floor[1]).addModel()
		.condition(FireBlock.NORTH, false).condition(FireBlock.EAST, false).condition(FireBlock.SOUTH, false).condition(FireBlock.WEST, false).condition(FireBlock.UP, false).end();
		BooleanProperty[] sides = {FireBlock.NORTH, FireBlock.EAST, FireBlock.SOUTH, FireBlock.WEST};
		for(int i = 0; i < 4; i++) {
			int rot = i * 90;
			builder.part().modelFile(side[0]).rotationY(rot).nextModel().modelFile(side[1]).rotationY(rot).nextModel().modelFile(side[2]).rotationY(rot)
			.nextModel().modelFile(side[3]).rotationY(rot).addModel().useOr()
			.nestedGroup().condition(sides[i], true).end()
			.nestedGroup().condition(FireBlock.NORTH, false).condition(FireBlock.EAST, false).condition(FireBlock.SOUTH, false).condition(FireBlock.WEST, false).condition(FireBlock.UP, false).end()
			.end();
		}
		builder.part().modelFile(up[0]).nextModel().modelFile(up[1]).nextModel().modelFile(up[2]).nextModel().modelFile(up[3]).addModel()
		.condition(FireBlock.UP, true).end();
	}
}
