/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     1.20.1 port - automated verification (./gradlew runGameTestServer)
 ******************************************************************************/
package com.shinoow.abyssalcraft.gametest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.blocks.BlockACPortal;
import com.shinoow.abyssalcraft.common.world.TeleporterAC;
import com.shinoow.abyssalcraft.init.ACDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * GameTests for the four dimensions: terrain shape, blocks, biomes and travel through the gateways
 */
@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class DimensionTests {

	private static ServerLevel level(GameTestHelper helper, ResourceKey<Level> key) {
		ServerLevel level = helper.getLevel().getServer().getLevel(key);
		if(level == null)
			throw new net.minecraft.gametest.framework.GameTestAssertException(key.location() + " doesn't exist");
		return level;
	}

	/** Surface heights (solid blocks only) of a 32x32 grid, 16 blocks apart, computed by the generator */
	private static int[] heights(ServerLevel level, int originX, int originZ) {
		ChunkGenerator generator = level.getChunkSource().getGenerator();
		int[] heights = new int[32 * 32];
		for(int x = 0; x < 32; x++)
			for(int z = 0; z < 32; z++)
				heights[x * 32 + z] = generator.getBaseHeight(originX + x * 16, originZ + z * 16, Heightmap.Types.OCEAN_FLOOR_WG, level, level.getChunkSource().randomState());
		return heights;
	}

	private static double average(int[] values) {
		double sum = 0;
		for(int v : values) sum += v;
		return sum / values.length;
	}

	private static void log(String dimension, String text) {
		AbyssalCraft.LOGGER.info("[DimensionTests] {}: {}", dimension, text);
	}

	/** Bedrock at the bottom, darkstone cobblestone at y 5, darkstone brick pillars (chiseled at y 3) every 4 blocks below it */
	private static void assertFloor(GameTestHelper helper, LevelChunk chunk) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int x0 = chunk.getPos().getMinBlockX(), z0 = chunk.getPos().getMinBlockZ();
		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++) {
				boolean pillar = x % 4 == 2 && z % 4 == 2;
				helper.assertTrue(chunk.getBlockState(pos.set(x0 + x, 0, z0 + z)).is(Blocks.BEDROCK), "no bedrock at " + pos);
				helper.assertTrue(chunk.getBlockState(pos.set(x0 + x, 5, z0 + z)).is(ACBlocks.darkstone_cobblestone.get()), "no darkstone cobblestone ceiling at " + pos);
				Block expected = pillar ? ACBlocks.chiseled_darkstone_bricks.get() : Blocks.AIR;
				helper.assertTrue(chunk.getBlockState(pos.set(x0 + x, 3, z0 + z)).is(expected), "expected " + expected + " at " + pos);
			}
	}

	private static Map<Block, Integer> count(LevelChunk chunk, int minY, int maxY) {
		Map<Block, Integer> counts = new HashMap<>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int x0 = chunk.getPos().getMinBlockX(), z0 = chunk.getPos().getMinBlockZ();
		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++)
				for(int y = minY; y <= maxY; y++)
					counts.merge(chunk.getBlockState(pos.set(x0 + x, y, z0 + z)).getBlock(), 1, Integer::sum);
		return counts;
	}

	/** Abyssal Wasteland: 1.12.2 heights (surface around y 65), abyssal stone with Liquid Coralium below y 63, sand surface, floor */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void abyssalWastelandTerrain(GameTestHelper helper) {
		ServerLevel aw = level(helper, ACDimensions.ABYSSAL_WASTELAND);
		int[] heights = heights(aw, 10000, 10000);
		double avg = average(heights);
		long belowSea = java.util.Arrays.stream(heights).filter(h -> h < 63).count();
		log("abyssal_wasteland", String.format("average surface %.1f, %d of 1024 columns below sea level", avg, belowSea));
		helper.assertTrue(avg > 55 && avg < 90, "unexpected average surface height " + avg);

		Map<Block, Integer> total = new HashMap<>();
		for(int cx = 0; cx < 3; cx++)
			for(int cz = 0; cz < 3; cz++) {
				LevelChunk chunk = aw.getChunk(625 + cx, 625 + cz);
				assertFloor(helper, chunk);
				count(chunk, 6, 140).forEach((b, n) -> total.merge(b, n, Integer::sum));
			}
		log("abyssal_wasteland", "blocks " + summary(total));
		helper.assertTrue(total.getOrDefault(ACBlocks.abyssal_stone.get(), 0) > 10000, "hardly any abyssal stone");
		helper.assertTrue(total.getOrDefault(ACBlocks.fused_abyssal_sand.get(), 0) + total.getOrDefault(ACBlocks.abyssal_sand.get(), 0) > 0, "no abyssal sand surface");
		helper.assertTrue(total.getOrDefault(Blocks.STONE, 0) == 0, "vanilla stone generated");
		helper.succeed();
	}

	/** Dreadlands: four biomes, mountains clearly higher, dreadstone with abyssalnite stone below y 63, floor */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void dreadlandsTerrain(GameTestHelper helper) {
		ServerLevel dl = level(helper, ACDimensions.DREADLANDS);
		var sampler = dl.getChunkSource().randomState().sampler();
		var source = dl.getChunkSource().getGenerator().getBiomeSource();
		Map<ResourceKey<Biome>, Integer> biomes = new HashMap<>();
		double mountains = 0, plains = 0;
		int mountainCount = 0, plainsCount = 0;
		ChunkGenerator generator = dl.getChunkSource().getGenerator();
		for(int x = 0; x < 48; x++)
			for(int z = 0; z < 48; z++) {
				int bx = x * 48, bz = z * 48;
				Holder<Biome> biome = source.getNoiseBiome(QuartPos.fromBlock(bx), QuartPos.fromBlock(80), QuartPos.fromBlock(bz), sampler);
				ResourceKey<Biome> key = biome.unwrapKey().orElseThrow();
				biomes.merge(key, 1, Integer::sum);
				if(x % 3 == 0 && z % 3 == 0) {
					int h = generator.getBaseHeight(bx, bz, Heightmap.Types.OCEAN_FLOOR_WG, dl, dl.getChunkSource().randomState());
					if(key == ACBiomes.dreadlands_mountains) { mountains += h; mountainCount++; }
					else if(key == ACBiomes.dreadlands) { plains += h; plainsCount++; }
				}
			}
		log("dreadlands", "biomes " + biomes + String.format(", average height mountains %.1f (%d), dreadlands %.1f (%d)",
				mountains / mountainCount, mountainCount, plains / plainsCount, plainsCount));
		for(ResourceKey<Biome> key : new ResourceKey[] {ACBiomes.dreadlands, ACBiomes.purified_dreadlands, ACBiomes.dreadlands_forest, ACBiomes.dreadlands_mountains}) {
			int n = biomes.getOrDefault(key, 0);
			helper.assertTrue(n > 48 * 48 / 10 && n < 48 * 48 / 2, key.location() + " covers " + n + " of " + 48 * 48 + " samples");
		}
		helper.assertTrue(mountainCount > 0 && plainsCount > 0 && mountains / mountainCount > plains / plainsCount + 5, "mountains aren't higher");

		Map<Block, Integer> total = new HashMap<>();
		LevelChunk chunk = dl.getChunk(0, 0);
		assertFloor(helper, chunk);
		count(chunk, 6, 62).forEach((b, n) -> total.merge(b, n, Integer::sum));
		log("dreadlands", "blocks below sea level " + summary(total));
		// (caves and lava vary a lot with the seed, so only the solid blocks are judged)
		int solid = total.entrySet().stream().filter(e -> !e.getKey().defaultBlockState().isAir() && e.getKey() != Blocks.LAVA).mapToInt(Map.Entry::getValue).sum();
		int stone = total.getOrDefault(ACBlocks.dreadstone.get(), 0) + total.getOrDefault(ACBlocks.abyssalnite_stone.get(), 0);
		helper.assertTrue(solid > 2000 && stone > solid * 9 / 10, "not mostly dreadstone/abyssalnite stone: " + stone + " of " + solid + " solid blocks");
		helper.assertTrue(total.getOrDefault(Blocks.WATER, 0) == 0, "water below sea level");
		helper.succeed();
	}

	/** Omothol: floating omothol stone land, densest just above y 32 and thinning out upwards; always air below y 4 and from y 128 up */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void omotholTerrain(GameTestHelper helper) {
		ServerLevel omt = level(helper, ACDimensions.OMOTHOL);
		int[] bands = new int[16];
		int outside = 0;
		// the noise columns, not the chunks: Omothol's structures reach higher
		ChunkGenerator columns = omt.getChunkSource().getGenerator();
		for(int x = 0; x < 48; x++)
			for(int z = 0; z < 48; z++) {
				net.minecraft.world.level.NoiseColumn column = columns.getBaseColumn(x, z, omt, omt.getChunkSource().randomState());
				for(int y = 0; y < 256; y++)
					if(!column.getBlock(y).isAir()) {
						if(y < 4 || y >= 128) outside++;
						else bands[y / 8]++;
					}
			}
		StringBuilder profile = new StringBuilder();
		for(int i = 0; i < 16; i++)
			profile.append(i * 8).append('-').append(i * 8 + 7).append(':').append(bands[i] * 100 / (9 * 256 * 8)).append("% ");
		log("omothol", "solid blocks per height band near 0,0: " + profile.toString().trim());
		helper.assertTrue(outside == 0, outside + " blocks below y 4 or above y 127");

		// land coverage of a 1024x1024 area (the lowest noise octave spans ~190 blocks)
		ChunkGenerator generator = omt.getChunkSource().getGenerator();
		int land = 0;
		double top = 0;
		for(int x = 0; x < 32; x++)
			for(int z = 0; z < 32; z++) {
				int h = generator.getBaseHeight(x * 32, z * 32, Heightmap.Types.WORLD_SURFACE_WG, omt, omt.getChunkSource().randomState());
				if(h > omt.getMinBuildHeight()) {
					land++;
					top += h;
				}
			}
		log("omothol", String.format("%d of 1024 columns have land, average top %.1f", land, land > 0 ? top / land : 0));
		helper.assertTrue(land > 1024 * 15 / 100 && land < 1024 * 80 / 100 && top / land > 40 && top / land < 100, "unexpected land coverage");
		helper.succeed();
	}

	/** Dark Realm: darkstone terrain on a bedrock floor, solid below y 63 except caves */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void darkRealmTerrain(GameTestHelper helper) {
		ServerLevel dr = level(helper, ACDimensions.DARK_REALM);
		double avg = average(heights(dr, 0, 0));
		log("dark_realm", String.format("average surface %.1f", avg));
		helper.assertTrue(avg >= 63 && avg < 100, "unexpected average surface height " + avg);
		LevelChunk chunk = dr.getChunk(4, 4);
		Map<Block, Integer> blocks = count(chunk, 1, 62);
		log("dark_realm", "blocks below sea level " + summary(blocks));
		helper.assertTrue(blocks.getOrDefault(ACBlocks.darkstone.get(), 0) > 8000, "not darkstone");
		helper.assertTrue(chunk.getBlockState(new BlockPos(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ())).is(Blocks.BEDROCK), "no bedrock floor");
		helper.succeed();
	}

	/**
	 * A pig walks into an Abyssal Gateway, arrives in the Abyssal Wasteland next to a newly built gateway (not inside
	 * blocks), and going back through that gateway brings it to the original one (linking)
	 */
	@GameTest(template = "empty", timeoutTicks = 400)
	public static void gatewayTravel(GameTestHelper helper) {
		for(int x = 0; x < 8; x++)
			for(int z = 0; z < 8; z++)
				helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
		Player player = helper.makeMockPlayer();
		player.setYRot(0);
		ItemStack key = new ItemStack(ACItems.gateway_key.get());
		player.setItemInHand(InteractionHand.MAIN_HAND, key);
		BlockPos clicked = helper.absolutePos(new BlockPos(2, 0, 3));
		key.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(clicked), Direction.UP, clicked, false)));
		helper.assertBlockPresent(ACBlocks.abyssal_gateway.get(), new BlockPos(2, 2, 3));
		BlockPos origin = helper.absolutePos(new BlockPos(2, 2, 3));

		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 2, 3));
		UUID id = pig.getUUID();
		ServerLevel aw = level(helper, ACDimensions.ABYSSAL_WASTELAND);
		String[] result = {null};
		helper.succeedWhen(() -> {
			if(result[0] == null) {
				Entity arrived = aw.getEntity(id);
				helper.assertTrue(arrived != null, "pig hasn't arrived in the Abyssal Wasteland");
				result[0] = checkArrival(aw, arrived);
				if(result[0] == null) {
					// back through the new gateway: must arrive at the original gateway
					BlockPos gateway = BlockPos.betweenClosedStream(arrived.blockPosition().offset(-2, -2, -2), arrived.blockPosition().offset(2, 2, 2))
							.filter(p -> aw.getBlockState(p).is(ACBlocks.abyssal_gateway.get())).findFirst().map(BlockPos::immutable).orElseThrow();
					Entity back = TeleporterAC.changeDimension(arrived, Level.OVERWORLD, (BlockACPortal) ACBlocks.abyssal_gateway.get(), gateway);
					if(back == null || back.level() != helper.getLevel())
						result[0] = "pig didn't come back";
					else if(back.blockPosition().distManhattan(origin) > 3)
						result[0] = "pig came back at " + back.blockPosition() + ", far from the original gateway " + origin;
					else result[0] = "ok";
					if(back != null)
						back.discard();
				}
			}
			helper.assertTrue("ok".equals(result[0]), result[0]);
		});
	}

	/** @return null if the arrival is fine, otherwise what's wrong */
	private static String checkArrival(ServerLevel level, Entity arrived) {
		if(!level.noCollision(arrived))
			return "pig arrived inside blocks at " + arrived.blockPosition();
		boolean gateway = BlockPos.betweenClosedStream(arrived.blockPosition().offset(-2, -2, -2), arrived.blockPosition().offset(2, 2, 2))
				.anyMatch(p -> level.getBlockState(p).is(ACBlocks.abyssal_gateway.get()));
		if(!gateway)
			return "no gateway next to the arrival point " + arrived.blockPosition();
		BlockState below = level.getBlockState(arrived.blockPosition().below());
		if(below.isAir())
			return "pig arrived above air at " + arrived.blockPosition();
		return null;
	}

	/** Falling out of Omothol leads to the Dark Realm, protected for the fall and not inside blocks (a pig: test players can't travel) */
	@GameTest(template = "empty", timeoutTicks = 200)
	public static void omotholToDarkRealm(GameTestHelper helper) {
		ServerLevel omt = level(helper, ACDimensions.OMOTHOL);
		ServerLevel dr = level(helper, ACDimensions.DARK_REALM);
		Pig pig = EntityType.PIG.create(omt);
		pig.moveTo(1234.5, omt.getMinBuildHeight() - 1, 4321.5, 0, 0);
		omt.getChunk(1234 >> 4, 4321 >> 4);
		omt.addFreshEntity(pig);
		Entity arrived = com.shinoow.abyssalcraft.common.world.DarkRealmTeleporter.fallIntoDarkRealm(pig);
		helper.assertTrue(arrived != null && arrived.level() == dr, "pig isn't in the Dark Realm");
		helper.assertTrue(dr.noCollision(arrived), "pig arrived inside blocks at " + arrived.blockPosition());
		helper.assertTrue(arrived.getBlockX() == 1234 && arrived.getBlockZ() == 4321 && arrived.getY() >= 80, "pig arrived at " + arrived.position());
		helper.assertTrue(((net.minecraft.world.entity.LivingEntity) arrived).hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE), "no fall protection");
		arrived.discard();
		helper.succeed();
	}

	private static String summary(Map<Block, Integer> counts) {
		StringBuilder sb = new StringBuilder();
		counts.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(8)
		.forEach(e -> sb.append(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(e.getKey()).getPath()).append('=').append(e.getValue()).append(' '));
		return sb.toString().trim();
	}
}
