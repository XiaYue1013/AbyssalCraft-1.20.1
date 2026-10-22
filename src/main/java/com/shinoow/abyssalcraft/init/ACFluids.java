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
 *     1.20.1 port - FluidType + ForgeFlowingFluid
 ******************************************************************************/
package com.shinoow.abyssalcraft.init;

import java.util.function.Consumer;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.BlockAntiliquid;
import com.shinoow.abyssalcraft.common.blocks.BlockCLiquid;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Liquid Coralium and Liquid Antimatter
 */
public class ACFluids {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AbyssalCraft.modid);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AbyssalCraft.modid);

	public static final RegistryObject<FluidType> LIQUID_CORALIUM_TYPE = FLUID_TYPES.register("liquid_coralium",
			() -> type(FluidType.Properties.create().descriptionId("block.abyssalcraft.liquid_coralium").density(3000).temperature(350).lightLevel(15)
					.canExtinguish(true).supportsBoating(true).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
					"cwater_still", "cwater_flow"));
	public static final RegistryObject<FluidType> LIQUID_ANTIMATTER_TYPE = FLUID_TYPES.register("liquid_antimatter",
			() -> type(FluidType.Properties.create().descriptionId("block.abyssalcraft.liquid_antimatter").density(4000).viscosity(1500).temperature(100).lightLevel(8)
					.canExtinguish(true).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
					"anti_still", "anti_flow"));

	public static final RegistryObject<ForgeFlowingFluid> LIQUID_CORALIUM = FLUIDS.register("liquid_coralium", () -> new Source(coraliumProps(), true));
	public static final RegistryObject<ForgeFlowingFluid> FLOWING_LIQUID_CORALIUM = FLUIDS.register("flowing_liquid_coralium", () -> new Flowing(coraliumProps(), true));
	public static final RegistryObject<ForgeFlowingFluid> LIQUID_ANTIMATTER = FLUIDS.register("liquid_antimatter", () -> new Source(antimatterProps(), false));
	public static final RegistryObject<ForgeFlowingFluid> FLOWING_LIQUID_ANTIMATTER = FLUIDS.register("flowing_liquid_antimatter", () -> new Flowing(antimatterProps(), false));

	private static ForgeFlowingFluid.Properties coraliumProps() {
		return new ForgeFlowingFluid.Properties(LIQUID_CORALIUM_TYPE, LIQUID_CORALIUM, FLOWING_LIQUID_CORALIUM)
				.block(() -> (BlockCLiquid) BlockHandler.LIQUID_CORALIUM.get()).bucket(() -> ItemHandler.LIQUID_CORALIUM_BUCKET.get()).tickRate(5);
	}

	private static ForgeFlowingFluid.Properties antimatterProps() {
		return new ForgeFlowingFluid.Properties(LIQUID_ANTIMATTER_TYPE, LIQUID_ANTIMATTER, FLOWING_LIQUID_ANTIMATTER)
				.block(() -> (BlockAntiliquid) BlockHandler.LIQUID_ANTIMATTER.get()).bucket(() -> ItemHandler.LIQUID_ANTIMATTER_BUCKET.get()).tickRate(10);
	}

	private static FluidType type(FluidType.Properties properties, String still, String flow) {
		return new FluidType(properties) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(new IClientFluidTypeExtensions() {
					@Override
					public ResourceLocation getStillTexture() {
						return new ResourceLocation("abyssalcraft", "block/" + still);
					}

					@Override
					public ResourceLocation getFlowingTexture() {
						return new ResourceLocation("abyssalcraft", "block/" + flow);
					}
				});
			}
		};
	}

	private static void displace(Level level, BlockPos pos, boolean coralium) {
		if(coralium)
			BlockCLiquid.displaceNeighbours(level, pos);
		else
			BlockAntiliquid.displaceNeighbours(level, pos);
	}

	public static class Source extends ForgeFlowingFluid.Source {
		private final boolean coralium;

		public Source(Properties properties, boolean coralium) {
			super(properties);
			this.coralium = coralium;
		}

		@Override
		public void tick(Level level, BlockPos pos, FluidState state) {
			displace(level, pos, coralium);
			super.tick(level, pos, state);
		}

		@Override
		protected boolean isRandomlyTicking() {
			return true;
		}

		@Override
		protected void randomTick(Level level, BlockPos pos, FluidState state, net.minecraft.util.RandomSource random) {
			displace(level, pos, coralium);
		}
	}

	public static class Flowing extends ForgeFlowingFluid.Flowing {
		private final boolean coralium;

		public Flowing(Properties properties, boolean coralium) {
			super(properties);
			this.coralium = coralium;
		}

		@Override
		public void tick(Level level, BlockPos pos, FluidState state) {
			displace(level, pos, coralium);
			super.tick(level, pos, state);
		}
	}
}
