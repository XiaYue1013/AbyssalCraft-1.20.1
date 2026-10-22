/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.client.gui.necronomicon.NecronomiconScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Development smoke test of everything that only shows on a client (./gradlew runClientSmokeTest): creates a flat
 * creative world, opens the Necronomicon screens, draws every AbyssalCraft item, summons every mob and visits the four
 * dimensions, taking a screenshot of each step (run/smoketest/screenshots), then quits. Only active with the
 * "abyssalcraft.smoketest" system property.
 */
@Mod.EventBusSubscriber(modid = AbyssalCraft.modid, value = Dist.CLIENT)
public class ClientSmokeTest {

	private static final boolean ENABLED = System.getProperty("abyssalcraft.smoketest") != null;
	private static final Deque<Runnable> steps = new ArrayDeque<>();
	private static final List<String> log = new ArrayList<>();
	private static int wait, stage;

	private static void log(String line) {
		AbyssalCraft.LOGGER.info("[SmokeTest] {}", line);
		log.add(line);
	}

	private static void command(String command) {
		Minecraft.getInstance().player.connection.sendCommand(command);
	}

	private static void shot(String name) {
		steps.add(() -> {
			Minecraft mc = Minecraft.getInstance();
			Screenshot.grab(mc.gameDirectory, "smoke_" + name + ".png", mc.getMainRenderTarget(), c -> {});
			log("screenshot " + name);
		});
	}

	private static void then(int ticks, Runnable action) {
		steps.add(() -> {
			action.run();
			wait = ticks;
		});
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(!ENABLED || event.phase != TickEvent.Phase.END) return;
		Minecraft mc = Minecraft.getInstance();
		// a fresh game directory starts on the accessibility onboarding screen
		if(stage == 0 && mc.screen instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
			mc.options.onboardAccessibility = false;
			mc.setScreen(new TitleScreen());
			return;
		}
		if(stage == 0 && mc.screen instanceof TitleScreen) {
			stage = 1;
			mc.options.pauseOnLostFocus = false;
			mc.getTutorial().setStep(net.minecraft.client.tutorial.TutorialSteps.NONE);
			LevelSettings settings = new LevelSettings("abyssalcraft_smoketest", GameType.CREATIVE, false, Difficulty.NORMAL, true, new GameRules(),
					WorldDataConfiguration.DEFAULT);
			mc.createWorldOpenFlows().createFreshLevel("smoketest_" + System.currentTimeMillis(), settings, new WorldOptions(1234L, false, false),
					access -> access.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions());
			log("creating world");
			return;
		}
		if(stage == 1 && mc.player != null && mc.level != null && mc.screen == null) {
			stage = 2;
			wait = 100;
			plan();
			return;
		}
		if(stage != 2) return;
		if(wait > 0) {
			wait--;
			return;
		}
		Runnable step = steps.poll();
		if(step != null)
			step.run();
		else {
			stage = 3;
			finish();
		}
	}

	private static void plan() {
		then(20, () -> {
			Minecraft.getInstance().options.chatVisibility().set(net.minecraft.world.entity.player.ChatVisiblity.HIDDEN);
			for(String c : new String[] {"gamerule doDaylightCycle false", "gamerule doMobSpawning false", "time set noon", "weather clear",
					"acunlockallknowledge", "effect give @s minecraft:night_vision infinite 0 true", "give @s abyssalcraft:abyssalnomicon"})
				command(c);
		});
		// Necronomicon
		for(NecronomiconScreens.Step step : NecronomiconScreens.STEPS) {
			then(10, () -> NecronomiconScreens.open(step, ClientSmokeTest::log));
			shot(step.name());
		}
		// every item, 18 x 9 a page
		List<Item> items = ForgeRegistries.ITEMS.getValues().stream().filter(i -> ForgeRegistries.ITEMS.getKey(i).getNamespace().equals(AbyssalCraft.modid)).toList();
		log(items.size() + " items");
		for(int page = 0; page * 162 < items.size(); page++) {
			List<Item> sub = items.subList(page * 162, Math.min(items.size(), (page + 1) * 162));
			int p = page;
			then(10, () -> Minecraft.getInstance().setScreen(new ItemGrid(sub)));
			shot("items_" + p);
		}
		then(5, () -> Minecraft.getInstance().setScreen(null));
		// every mob, in groups in front of the camera (flat world surface is y -60)
		List<EntityType<?>> mobs = ForgeRegistries.ENTITY_TYPES.getValues().stream()
				.filter(t -> ForgeRegistries.ENTITY_TYPES.getKey(t).getNamespace().equals(AbyssalCraft.modid) && t.getCategory() != MobCategory.MISC).toList();
		log(mobs.size() + " mobs");
		List<EntityType<?>> big = mobs.stream().filter(t -> t.getWidth() > 3).toList();
		List<EntityType<?>> small = mobs.stream().filter(t -> t.getWidth() <= 3).toList();
		for(int g = 0; g * 5 < small.size(); g++) {
			List<EntityType<?>> group = small.subList(g * 5, Math.min(small.size(), (g + 1) * 5));
			int n = g;
			then(40, () -> {
				command("kill @e[type=!player]");
				command("tp @s 0 -59 -8 0 10");
				for(int i = 0; i < group.size(); i++)
					command("summon " + ForgeRegistries.ENTITY_TYPES.getKey(group.get(i)) + " " + (-6 + i * 3) + " -60 0 {NoAI:1b,PersistenceRequired:1b,Silent:1b}");
				log("mobs_" + n + ": " + group.stream().map(t -> ForgeRegistries.ENTITY_TYPES.getKey(t).getPath()).toList());
			});
			shot("mobs_" + n);
		}
		for(EntityType<?> type : big) {
			String id = ForgeRegistries.ENTITY_TYPES.getKey(type).toString();
			then(40, () -> {
				command("kill @e[type=!player]");
				command("tp @s 0 -50 -40 0 10");
				command("summon " + id + " 0 -60 0 {NoAI:1b,PersistenceRequired:1b,Silent:1b}");
			});
			shot("boss_" + type.toShortString());
		}
		then(5, () -> command("kill @e[type=!player]"));
		// the dimensions, from above
		for(String dim : new String[] {"abyssal_wasteland", "dreadlands", "omothol", "dark_realm"}) {
			// Omothol is islands (void at 0, 0): look at the J'zahar temple; fly so nobody falls out of a dimension
			String spot = dim.equals("omothol") ? "0 110 -20 0 30" : "0 140 0 30 25";
			then(200, () -> {
				var player = Minecraft.getInstance().player;
				player.getAbilities().flying = true;
				player.onUpdateAbilities();
				command("execute in abyssalcraft:" + dim + " run tp @s " + spot);
			});
			shot("dim_" + dim);
		}
	}

	private static void finish() {
		try {
			File out = new File(Minecraft.getInstance().gameDirectory, "smoketest_log.txt");
			Files.write(out.toPath(), log);
		} catch(IOException e) {
			AbyssalCraft.LOGGER.error("[SmokeTest] can't write the log", e);
		}
		log("done");
		Minecraft.getInstance().stop();
	}

	/** Every given item in a grid */
	private static class ItemGrid extends Screen {
		private final List<Item> items;

		ItemGrid(List<Item> items) {
			super(Component.empty());
			this.items = items;
		}

		@Override
		public void render(GuiGraphics graphics, int mx, int my, float partialTick) {
			graphics.fill(0, 0, width, height, 0xFF808080);
			for(int i = 0; i < items.size(); i++)
				graphics.renderItem(new ItemStack(items.get(i)), 4 + i % 18 * 20, 4 + i / 18 * 20);
		}
	}
}
