package com.shinoow.abyssalcraft.gametest;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.necronomicon.CraftingStack;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData.Chapter;
import com.shinoow.abyssalcraft.common.handlers.InternalNecroDataHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AbyssalCraft.modid)
@PrefixGameTestTemplate(false)
public class NecronomiconTests {

	/** The Necronomicon's internal entries exist and their chapters got their pages (items resolved to the new ids) */
	@GameTest(template = "empty")
	public static void bookContent(GameTestHelper helper) {
		var nd = AbyssalCraftAPI.getInternalNDHandler();
		helper.assertTrue(nd instanceof InternalNecroDataHandler, "internal handler not set");
		int pages = 0, crafting = 0;
		for(String id : new String[] {"greatoldones", "overworld", "abyssalwasteland", "dreadlands", "omothol", "darkrealm", "rituals", "potentialenergy", "miscinfo", "spells"}) {
			NecroData data = nd.getInternalNecroData(id);
			helper.assertTrue(data != null && !data.getContainedData().isEmpty(), id + " missing");
			for(var entry : data.getContainedData())
				if(entry instanceof Chapter chapter) {
					helper.assertTrue(chapter.getPageAmount() > 0 || chapter.getIdentifier().equals("coraliumplague") || chapter.getIdentifier().equals("dreadplague"),
							id + "/" + chapter.getIdentifier() + " has no pages");
					pages += chapter.getPageAmount();
					for(var page : chapter.getPages().values()) {
						if(page.getIcon() instanceof ItemStack stack)
							helper.assertFalse(stack.isEmpty(), id + "/" + chapter.getIdentifier() + " page " + page.getPageNumber() + " has an empty item");
						if(page.getIcon() instanceof CraftingStack c)
							crafting += c.getOutput().isEmpty() ? 0 : 1;
					}
				}
		}
		AbyssalCraft.LOGGER.info("[NecronomiconTests] {} pages, {} crafting pages", pages, crafting);
		helper.assertTrue(pages > 200 && crafting > 50, pages + " pages, " + crafting + " crafting pages");
		helper.assertTrue(InternalNecroDataHandler.patrons() != null, "no patrons page");
		helper.succeed();
	}
}
