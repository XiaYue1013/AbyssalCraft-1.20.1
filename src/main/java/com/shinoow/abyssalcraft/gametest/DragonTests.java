package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.entity.EntityACDragon;
import com.shinoow.abyssalcraft.init.ACEntityTypes;
import com.shinoow.abyssalcraft.lib.ACConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class DragonTests {
	private static EntityACDragon dragon(GameTestHelper helper, boolean boss, BlockPos pos) {
		var dragon = helper.spawn((boss ? ACEntityTypes.ASORAH : ACEntityTypes.SPECTRAL_DRAGON).get(), pos);
		dragon.setNoAi(true); return dragon;
	}

	@GameTest(template = "empty", batch = "dragon_basics")
	public static void dragonHitboxes(GameTestHelper helper) {
		var player = helper.makeMockPlayer();
		for(boolean boss : new boolean[] {false, true}) {
			var dragon = dragon(helper, boss, new BlockPos(3, 2, 3));
			helper.assertTrue(dragon.getParts().length == 7 && dragon.fireImmune() && dragon.isNoGravity(), "dragon basics");
			var ids = new java.util.HashSet<Integer>(); ids.add(dragon.getId());
			for(var part : dragon.getParts()) helper.assertTrue(ids.add(part.getId()) && part.getId() > dragon.getId(), "duplicate part id");
			float health = dragon.getHealth();
			helper.assertFalse(dragon.hurt(helper.getLevel().damageSources().playerAttack(player), 20), "parent bounding box took damage");
			dragon.body.hurt(helper.getLevel().damageSources().playerAttack(player), 20);
			helper.assertTrue(health - dragon.getHealth() == (boss ? 6 : 11), "body reduction: " + (health - dragon.getHealth()));
			if(boss) {
				dragon.invulnerableTime = 0; health = dragon.getHealth();
				dragon.head.hurt(helper.getLevel().damageSources().playerAttack(player), 500);
				helper.assertTrue(health - dragon.getHealth() >= 10 && health - dragon.getHealth() < 20, "Asorah damage cap");
				helper.assertFalse(dragon.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100)), "Asorah got an effect");
			}
			dragon.discard();
		}
		helper.assertTrue(RitualRegistry.instance().getRitual("summonAsorah") != null && RitualRegistry.instance().getRitual("asorahGatewayKey") != null, "Asorah progression rituals missing");
		var spawns = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(ACBiomes.abyssal_wastelands).getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
		helper.assertTrue(spawns.stream().anyMatch(e -> e.type == ACEntityTypes.SPECTRAL_DRAGON.get()) == !ACConfig.no_spectral_dragons, "spectral dragon spawn config");
		helper.succeed();
	}

	@GameTest(template = "empty", batch = "dragon_healing", timeoutTicks = 240)
	public static void spectralDragonHealsAsorah(GameTestHelper helper) {
		var boss = dragon(helper, true, new BlockPos(3, 2, 3));
		var minion = dragon(helper, false, new BlockPos(3, 2, 23));
		boss.setHealth(boss.getMaxHealth() - 10); float initial = boss.getHealth();
		helper.succeedWhen(() -> {
			helper.assertTrue(boss.getHealingPartner() == minion && minion.getHealingPartner() == boss, "healing partners not linked");
			helper.assertTrue(boss.getHealth() > initial && minion.getHealth() < minion.getMaxHealth(), "boss didn't heal at the minion's expense");
			boss.discard(); minion.discard();
		});
	}

	@GameTest(template = "empty", batch = "dragon_partner_death", timeoutTicks = 240)
	public static void spectralDragonDiesWithAsorah(GameTestHelper helper) {
		var boss = dragon(helper, true, new BlockPos(3, 2, 3));
		var minion = dragon(helper, false, new BlockPos(3, 2, 23));
		helper.succeedWhen(() -> {
			if(boss.isAlive()) {
				helper.assertTrue(minion.getHealingPartner() == boss, "partner not acquired yet");
				boss.kill();
			}
			helper.assertTrue(minion.isDeadOrDying(), "minion survived losing Asorah");
			boss.discard(); minion.discard();
		});
	}

	@GameTest(template = "empty", batch = "dragon_flight", timeoutTicks = 60)
	public static void spectralDragonFlies(GameTestHelper helper) {
		var dragon = helper.spawn(ACEntityTypes.SPECTRAL_DRAGON.get(), new BlockPos(3, 2, 3));
		var origin = dragon.position();
		helper.runAfterDelay(30, () -> {
			helper.assertTrue(dragon.position().distanceToSqr(origin) > 1 && Double.isFinite(dragon.getY()), "dragon isn't flying");
			helper.assertTrue(dragon.noPhysics, "spectral dragon lost its phasing");
			dragon.discard(); helper.succeed();
		});
	}

	@GameTest(template = "empty", batch = "asorah_death", timeoutTicks = 260)
	public static void asorahDeathDropsEye(GameTestHelper helper) {
		var boss = dragon(helper, true, new BlockPos(3, 2, 3));
		var area = new AABB(boss.blockPosition()).inflate(40);
		boss.kill(); helper.assertTrue(boss.isDeadOrDying(), "/kill didn't kill Asorah");
		helper.runAfterDelay(100, () -> helper.assertFalse(boss.isRemoved(), "Asorah disappeared before the death animation"));
		helper.succeedWhen(() -> {
			helper.assertTrue(boss.isRemoved() && boss.deathTicks >= 200, "death animation not complete");
			var items = helper.getLevel().getEntitiesOfClass(ItemEntity.class, area, e -> e.getItem().is(ACItems.eye_of_the_abyss.get()));
			helper.assertTrue(items.size() == 1 && items.get(0).getItem().getCount() == 1, "missing or duplicated Eye of the Abyss");
			helper.assertFalse(helper.getLevel().getEntitiesOfClass(ExperienceOrb.class, area).isEmpty(), "Asorah dropped no XP");
			helper.getLevel().getEntitiesOfClass(Entity.class, area, e -> e instanceof ItemEntity || e instanceof ExperienceOrb).forEach(Entity::discard);
		});
	}
}
