package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.common.command.CommandUnlockAllKnowledge;
import com.shinoow.abyssalcraft.common.handlers.IMCHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class IntegrationTests {

	/** IMC messages reach the API (plague immunity by entity id, a Transmutator recipe as a CompoundTag); bad ones are refused */
	@GameTest(template = "empty")
	public static void imcMessages(GameTestHelper helper) {
		helper.assertTrue(IMCHandler.handle("addCoraliumPlagueImmunity", "minecraft:mooshroom"), "immunity refused");
		var cow = helper.spawn(EntityType.MOOSHROOM, new BlockPos(1, 2, 1));
		helper.assertTrue(EntityUtil.isEntityCoralium(cow), "mooshroom not immune");
		cow.discard();
		CompoundTag recipe = new CompoundTag();
		recipe.put("input", new ItemStack(Items.TUFF).save(new CompoundTag()));
		recipe.put("output", new ItemStack(Items.CALCITE, 2).save(new CompoundTag()));
		recipe.putFloat("xp", 0.1F);
		helper.assertTrue(IMCHandler.handle("addTransmutation", recipe), "transmutation refused");
		ItemStack out = TransmutatorRecipes.instance().getTransmutationResult(new ItemStack(Items.TUFF));
		helper.assertTrue(out.is(Items.CALCITE) && out.getCount() == 2, "transmutation not added: " + out);
		helper.assertFalse(IMCHandler.handle("addTransmutation", "not a tag"), "bad value accepted");
		helper.assertFalse(IMCHandler.handle("unknownKey", recipe), "unknown key accepted");
		helper.assertFalse(IMCHandler.handle("addCrystallization", new CompoundTag()), "incomplete tag accepted");
		helper.succeed();
	}

	/** The nine advancements load as one tree */
	@GameTest(template = "empty")
	public static void advancements(GameTestHelper helper) {
		var advancements = helper.getLevel().getServer().getAdvancements();
		long count = advancements.getAllAdvancements().stream().filter(a -> a.getId().getNamespace().equals(AbyssalCraft.modid) && !a.getId().getPath().startsWith("recipes/")).count();
		var ethaxium = advancements.getAdvancement(AbyssalCraft.loc("ethaxium"));
		helper.assertTrue(count == 9 && ethaxium != null, count + " advancements");
		var root = ethaxium;
		int depth = 0;
		while(root.getParent() != null) {
			root = root.getParent();
			depth++;
		}
		helper.assertTrue(root.getId().equals(AbyssalCraft.loc("root")) && depth == 8, "tree: root " + root.getId() + ", depth " + depth);
		helper.succeed();
	}

	/** /acunlockallknowledge is registered and toggles the knowledge */
	@GameTest(template = "empty")
	public static void unlockCommand(GameTestHelper helper) throws Exception {
		helper.assertTrue(helper.getLevel().getServer().getCommands().getDispatcher().getRoot().getChild("acunlockallknowledge") != null, "command missing");
		var player = FakePlayerFactory.getMinecraft(helper.getLevel());
		var cap = NecroDataCapability.getCap(player);
		boolean before = cap.hasUnlockedAllKnowledge();
		CommandUnlockAllKnowledge.toggle(player);
		helper.assertTrue(cap.hasUnlockedAllKnowledge() != before, "not toggled");
		CommandUnlockAllKnowledge.toggle(player);
		helper.assertTrue(cap.hasUnlockedAllKnowledge() == before, "not toggled back");
		helper.succeed();
	}
}
