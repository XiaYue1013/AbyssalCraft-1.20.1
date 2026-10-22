package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.common.blocks.BlockODB;
import com.shinoow.abyssalcraft.common.entity.EntityODBPrimed;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class ODBTests {
	private static boolean disabled;
	private static int size;
	@BeforeBatch(batch = "odb_small")
	public static void smallExplosion(ServerLevel level) {
		disabled = ACConfig.no_odb_explosions; size = ACConfig.odbExplosionSize;
		ACConfig.no_odb_explosions = false; ACConfig.odbExplosionSize = 2;
	}
	@AfterBatch(batch = "odb_small")
	public static void restore(ServerLevel level) { ACConfig.no_odb_explosions = disabled; ACConfig.odbExplosionSize = size; }

	@GameTest(template = "empty")
	public static void odbDisabledDoesNotPrime(GameTestHelper helper) {
		boolean old = ACConfig.no_odb_explosions;
		ACConfig.no_odb_explosions = true;
		BlockPos pos = helper.absolutePos(new BlockPos(3, 1, 3));
		try {
			for(var block : java.util.List.of(BlockHandler.OBLIVION_DEATHBOMB, BlockHandler.ODB_CORE))
				((BlockODB) block.get()).onCaughtFire(block.get().defaultBlockState(), helper.getLevel(), pos, null, null);
			helper.assertTrue(helper.getLevel().getEntitiesOfClass(EntityODBPrimed.class, new AABB(pos).inflate(3)).isEmpty(), "disabled ODB primed");
		} finally { ACConfig.no_odb_explosions = old; }
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void odbFusePersists(GameTestHelper helper) {
		for(var type : java.util.List.of(ACEntityTypes.PRIMED_ODB, ACEntityTypes.PRIMED_ODB_CORE)) {
			EntityODBPrimed bomb = type.get().create(helper.getLevel());
			int expected = type == ACEntityTypes.PRIMED_ODB ? 200 : 40;
			helper.assertTrue(bomb.getFuse() == expected, "wrong initial fuse");
			CompoundTag tag = new CompoundTag(); bomb.saveWithoutId(tag);
			EntityODBPrimed restored = type.get().create(helper.getLevel()); restored.load(tag);
			helper.assertTrue(restored.getFuse() == expected, "fuse wrapped around during save");
		}
		helper.succeed();
	}

	@GameTest(template = "empty", batch = "odb_small", timeoutTicks = 100)
	public static void odbExplodesAndSummons(GameTestHelper helper) {
		BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));
		helper.getLevel().setBlockAndUpdate(pos, BlockHandler.OBLIVION_DEATHBOMB.get().defaultBlockState());
		((BlockODB) BlockHandler.OBLIVION_DEATHBOMB.get()).onCaughtFire(BlockHandler.OBLIVION_DEATHBOMB.get().defaultBlockState(), helper.getLevel(), pos, null, null);
		helper.getLevel().removeBlock(pos, false);
		var bombs = helper.getLevel().getEntities(ACEntityTypes.PRIMED_ODB.get(), new AABB(pos).inflate(2), e -> true);
		helper.assertTrue(bombs.size() == 1, "ignition didn't create one ODB");
		var bomb = bombs.get(0); bomb.setNoGravity(true); bomb.setDeltaMovement(0, 0, 0); bomb.setFuse(2);
		helper.getLevel().setBlockAndUpdate(pos.below(), Blocks.BEDROCK.defaultBlockState());
		helper.succeedWhen(() -> {
			var bosses = helper.getLevel().getEntities(ACEntityTypes.SACTHOTH.get(), new AABB(pos).inflate(5), e -> true);
			helper.assertTrue(bomb.isRemoved() && bosses.size() == 1, "waiting for crater and Sacthoth");
			helper.assertTrue(helper.getLevel().getBlockState(pos).is(Blocks.OBSIDIAN), "no obsidian platform");
			helper.assertTrue(helper.getLevel().getBlockState(pos.below()).is(Blocks.BEDROCK), "destroyed bedrock");
			bosses.forEach(e -> e.discard());
		});
	}
}
