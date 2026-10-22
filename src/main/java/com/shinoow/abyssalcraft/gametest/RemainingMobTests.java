package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.common.entity.EntityInkProjectile;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class RemainingMobTests {
	@GameTest(template = "empty", batch = "tracker", timeoutTicks = 110)
	public static void trackerFliesAndReturnsItem(GameTestHelper helper) {
		var tracker = ACEntityTypes.PSDL_TRACKER.get().create(helper.getLevel());
		BlockPos start = helper.absolutePos(new BlockPos(3, 3, 3));
		tracker.moveTo(start.getX(), start.getY(), start.getZ(), 0, 0);
		tracker.moveTowards(start.offset(100, 0, 0));
		var tag = new net.minecraft.nbt.CompoundTag(); tracker.saveWithoutId(tag); tag.putBoolean("Survive", true);
		var restored = ACEntityTypes.PSDL_TRACKER.get().create(helper.getLevel()); restored.load(tag);
		helper.getLevel().addFreshEntity(restored);
		helper.runAfterDelay(30, () -> {
			helper.assertTrue(restored.getX() > start.getX() + 1 && restored.getY() > start.getY(), "tracker didn't fly toward the target");
			helper.assertFalse(restored.isRemoved(), "tracker expired early");
		});
		helper.runAfterDelay(85, () -> {
			helper.assertTrue(restored.isRemoved(), "tracker didn't expire");
			var items = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(start).inflate(20),
					e -> e.getItem().is(com.shinoow.abyssalcraft.api.item.ACItems.powerstone_tracker.get()));
			helper.assertTrue(items.size() == 1, "tracker didn't return its item");
			items.forEach(e -> e.discard()); helper.succeed();
		});
	}

	private static void floor(GameTestHelper helper) {
		for(int x = 0; x < 8; x++) for(int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
	}

	@GameTest(template = "empty")
	public static void remainingMobBasics(GameTestHelper helper) {
		floor(helper);
		var shub = helper.spawn(ACEntityTypes.SHUB_OFFSPRING.get(), new BlockPos(2, 1, 2));
		shub.setNoAi(true);
		helper.assertTrue(shub.getMaxHealth() == (ACConfig.hardcoreMode ? 80 : 40), "offspring health");
		helper.assertTrue(shub.getAttributeValue(Attributes.ATTACK_DAMAGE) == (ACConfig.hardcoreMode ? 8 : 4), "offspring damage");
		helper.assertFalse(shub.fireImmune(), "offspring should not be fire immune");
		var squid = helper.spawn(ACEntityTypes.CORALIUM_SQUID.get(), new BlockPos(5, 1, 5));
		helper.assertTrue(EntityUtil.isEntityCoralium(squid), "squid is not a Coralium mob");
		var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
		var water = biomes.getOrThrow(ACBiomes.abyssal_wastelands).getMobSettings().getMobs(MobCategory.WATER_CREATURE).unwrap();
		helper.assertTrue(water.stream().anyMatch(e -> e.type == ACEntityTypes.CORALIUM_SQUID.get() && e.getWeight().asInt() == 10 && e.minCount == 4 && e.maxCount == 4), "squid spawn list");
		for(var biome : java.util.List.of(Biomes.FOREST, Biomes.DARK_FOREST, ACBiomes.darklands_forest)) {
			int weight = biomes.getOrThrow(biome).getMobSettings().getMobs(MobCategory.MONSTER).unwrap().stream()
					.filter(e -> e.type == ACEntityTypes.SHUB_OFFSPRING.get()).mapToInt(e -> e.getWeight().asInt()).sum();
			helper.assertTrue(weight == ACConfig.darkOffspringSpawnWeight * (biome == Biomes.FOREST ? 1 : 2), "offspring spawn weight " + weight);
		}
		squid.discard(); shub.discard();
		helper.succeed();
	}

	@GameTest(template = "empty", timeoutTicks = 60)
	public static void inkProjectileHits(GameTestHelper helper) {
		floor(helper);
		var squid = helper.spawn(ACEntityTypes.CORALIUM_SQUID.get(), new BlockPos(1, 1, 1));
		squid.setNoAi(true);
		var golem = helper.spawn(EntityType.IRON_GOLEM, new BlockPos(6, 1, 6));
		golem.setNoAi(true);
		var ink = new EntityInkProjectile(helper.getLevel(), squid);
		BlockPos start = helper.absolutePos(new BlockPos(6, 2, 3));
		ink.moveTo(start.getX() + 0.5, start.getY(), start.getZ() + 0.5);
		ink.shoot(0, 0, 1, 0.5F, 0);
		ink.setNoGravity(true);
		helper.getLevel().addFreshEntity(ink);
		helper.succeedWhen(() -> {
			helper.assertTrue(golem.getHealth() == golem.getMaxHealth() - 2, "ink damage: " + golem.getHealth());
			helper.assertTrue(ink.isRemoved(), "ink still flying");
		});
	}

	@GameTest(template = "empty", timeoutTicks = 420)
	public static void squidLivesInCoralium(GameTestHelper helper) {
		floor(helper);
		// Liquid Coralium turns into cobblestone in oceans, and the test world's seed changes every run (see FluidTests)
		BlockPos center = helper.absolutePos(new BlockPos(3, 2, 3));
		com.shinoow.abyssalcraft.common.util.BiomeUtil.updateBiomes(helper.getLevel(), BlockPos.betweenClosedStream(center.offset(-12, 0, -12), center.offset(12, 0, 12))
				.map(BlockPos::immutable).toList(), net.minecraft.world.level.biome.Biomes.PLAINS);
		for(int x = 2; x < 6; x++) for(int z = 2; z < 6; z++) for(int y = 1; y < 4; y++)
			helper.setBlock(new BlockPos(x, y, z), ACBlocks.liquid_coralium.get());
		var squid = helper.spawn(ACEntityTypes.CORALIUM_SQUID.get(), new BlockPos(3, 2, 3));
		squid.setNoAi(true);
		squid.setNoGravity(true);
		squid.setAirSupply(1);
		var damage = new java.util.concurrent.atomic.AtomicReference<String>("none");
		helper.onEachTick(() -> {
			if(squid.getLastDamageSource() != null && damage.get().equals("none"))
				damage.set(squid.getLastDamageSource().getMsgId() + " by " + squid.getLastDamageSource().getEntity() + " at tick " + helper.getTick()
						+ " in " + helper.getLevel().getBlockState(BlockPos.containing(squid.getEyePosition())) + " at " + helper.relativePos(BlockPos.containing(squid.getEyePosition())));
		});
		helper.runAfterDelay(350, () -> {
			helper.assertTrue(squid.isInWater() && squid.getHealth() == squid.getMaxHealth() && squid.getAirSupply() > 0,
					"squid in water=" + squid.isInWater() + ", health=" + squid.getHealth() + ", air=" + squid.getAirSupply() + ", position=" + squid.position() + ", damage=" + damage.get());
			squid.discard();
			helper.succeed();
		});
	}

	@GameTest(template = "empty", timeoutTicks = 60)
	public static void squidContactInfects(GameTestHelper helper) {
		floor(helper);
		var squid = helper.spawn(ACEntityTypes.CORALIUM_SQUID.get(), new BlockPos(3, 1, 3));
		squid.setNoAi(true);
		var pig = helper.spawn(EntityType.PIG, new BlockPos(3, 1, 3));
		pig.setNoAi(true);
		helper.succeedWhen(() -> helper.assertTrue(pig.hasEffect(AbyssalCraftAPI.coralium_plague.get()), "no plague on contact"));
	}
}
