package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.necronomicon.condition.*;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.items.ItemOC;
import com.shinoow.abyssalcraft.common.items.ItemSoulReaper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class ProgressionItemTests {
	private static IUnlockCondition condition(int type, Object value) {
		return new IUnlockCondition() {
			public boolean areConditionObjectsEqual(Object other) { return value.equals(other); }
			public Object getConditionObject() { return value; }
			public int getType() { return type; }
		};
	}

	@GameTest(template = "empty")
	public static void knowledgeConditionsUnlock(GameTestHelper helper) {
		var cap = new NecroDataCapability(); var player = helper.makeMockPlayer();
		helper.assertFalse(cap.isUnlocked(new EntityCondition("minecraft:pig"), player), "unearned entity knowledge");
		cap.triggerEntityUnlock("minecraft:pig"); cap.triggerBiomeUnlock("minecraft:plains"); cap.triggerDimensionUnlock("minecraft:overworld");
		cap.triggerArtifactUnlock("artifact"); cap.triggerPageUnlock("page"); cap.triggerWhisperUnlock("whisper"); cap.triggerMiscUnlock("misc");
		Object[] values = {"minecraft:plains", "minecraft:pig", "minecraft:overworld", new String[] {"missing", "minecraft:plains"},
				new String[] {"missing", "minecraft:pig"},
				(com.google.common.base.Predicate<net.minecraft.world.level.biome.Biome>) b -> b == player.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).get(net.minecraft.world.level.biome.Biomes.PLAINS),
				(com.google.common.base.Predicate<EntityType<?>>) t -> t == EntityType.PIG,
				"artifact", "page", "whisper", "misc", new String[] {"minecraft:pig"}};
		for(int i = 0; i < values.length; i++) helper.assertTrue(cap.isUnlocked(condition(i, values[i]), player), "condition " + i + " didn't unlock");
		cap.unlockAllKnowledge(true);
		helper.assertTrue(cap.isUnlocked(new EntityCondition("missing"), player), "unlock-all didn't work");
		helper.assertFalse(cap.isUnlocked(ItemOC.KILLED_BOSSES, player), "unlock-all bypassed the four boss kills");
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void catalystRequiresAllBosses(GameTestHelper helper) {
		var player = helper.makeMockPlayer(); var cap = NecroDataCapability.getCap(player);
		var stack = new ItemStack(ACItems.oblivion_catalyst.get(), 2); player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		var catalyst = (ItemOC) stack.getItem();
		catalyst.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(stack.getCount() == 2 && !player.hasEffect(MobEffects.DAMAGE_BOOST), "unearned catalyst bonus");
		for(String boss : (String[]) ItemOC.KILLED_BOSSES.getConditionObject()) cap.triggerEntityUnlock(boss);
		catalyst.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(stack.getCount() == 1 && player.getEffect(MobEffects.DAMAGE_BOOST) != null
				&& player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() == 6 && player.getEffect(MobEffects.DAMAGE_BOOST).getDuration() == 600, "missing catalyst effects");
		helper.assertTrue(RitualRegistry.instance().getRitual("oblivionCatalyst") != null && RitualRegistry.instance().getRitual("summonSacthoth") != null, "missing newly enabled rituals");
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void soulReaperBonuses(GameTestHelper helper) {
		var stack = new ItemStack(ACItems.sacthoths_soul_harvesting_blade.get()); var blade = (ItemSoulReaper) stack.getItem();
		var player = helper.makeMockPlayer();
		helper.assertTrue(stack.getMaxDamage() == 2000 && !stack.isEnchantable(), "blade durability or enchantability");
		var victim = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 2)); victim.setNoAi(true);
		blade.hurtEnemy(stack, victim, player); helper.assertTrue(blade.getSouls(stack) == 0, "living victim gave a soul");
		victim.setHealth(0); blade.hurtEnemy(stack, victim, player);
		helper.assertTrue(blade.getSouls(stack) == 1 && stack.getDamageValue() == 2, "kill didn't give a soul and consume durability"); victim.discard();
		for(int souls : new int[] {59, 60, 125, 250, 500, 1000}) {
			player.removeAllEffects(); blade.setSouls(souls, stack); blade.inventoryTick(stack, helper.getLevel(), player, 0, true);
			helper.assertTrue(player.hasEffect(MobEffects.DAMAGE_BOOST) == (souls >= 60), "strength threshold " + souls);
			helper.assertTrue(player.hasEffect(MobEffects.MOVEMENT_SPEED) == (souls >= 125), "speed threshold " + souls);
			helper.assertTrue(player.hasEffect(MobEffects.DAMAGE_RESISTANCE) == (souls >= 250), "resistance threshold " + souls);
			helper.assertTrue(player.hasEffect(MobEffects.FIRE_RESISTANCE) == (souls >= 500), "fire resistance threshold " + souls);
			helper.assertTrue(player.hasEffect(MobEffects.HEALTH_BOOST) == (souls >= 1000), "health boost threshold " + souls);
		}
		player.removeEffect(MobEffects.HEALTH_BOOST); player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 2));
		blade.inventoryTick(stack, helper.getLevel(), player, 0, true);
		helper.assertTrue(player.getEffect(MobEffects.HEALTH_BOOST).getDuration() == 500, "health boost kept resetting");
		blade.setSouls(5000, stack); helper.assertTrue(blade.getSouls(stack) == 1000, "soul cap");
		helper.succeed();
	}
}
