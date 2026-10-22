/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.necronomicon.GuiInstance;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData;
import com.shinoow.abyssalcraft.api.necronomicon.NecroData.Page;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.NecronomiconCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonCategory;
import com.shinoow.abyssalcraft.client.gui.necronomicon.buttons.ButtonNextPage;
import com.shinoow.abyssalcraft.common.handlers.InternalNecroDataHandler;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The Necronomicon (all five book types). Index of the categories, plus the "huh?" and knowledge text pages; the
 * other screens extend this one for the shared page drawing.
 * Port: the 1.12.2 unicode font is the "uniform" font, Aklo is the abyssalcraft:aklo bitmap font.
 */
public class GuiNecronomicon extends Screen {

	public static final ResourceLocation UNIFORM = new ResourceLocation("minecraft", "uniform");
	public static final ResourceLocation AKLO = AbyssalCraft.loc("aklo");
	protected static final ResourceLocation MISSING_PICTURE = AbyssalCraft.loc("textures/gui/necronomicon/missing.png");
	protected static final ResourceLocation MISSING_ITEM = AbyssalCraft.loc("textures/gui/necronomicon/missing_item.png");
	protected static final ResourceLocation MISSING_RECIPE = AbyssalCraft.loc("textures/gui/necronomicon/missing_recipe.png");

	/** The last opened Necronomicon screen; reopening the book continues there */
	public static GuiNecronomicon currentNecro;

	public final int guiWidth = 255, guiHeight = 192;
	private ResourceLocation bookGuiTextures = AbyssalCraft.loc("textures/gui/necronomicon.png");
	private int bookTotalTurnups = 2;
	protected int currTurnup;
	private int bookType = -1;
	private static final int cycleTime = 1000;
	private long startTime, drawTime;
	protected boolean isInfo;
	private boolean isNecroInfo, isKnowledgeInfo;
	protected boolean isInvalid;
	protected String unknown50_1, unknown50_2, unknown95, unknownFull;
	protected static String sidebarIndex = "";
	protected ItemStack tooltipStack = ItemStack.EMPTY;
	protected GuiGraphics graphics;
	private Button buttonDone;
	private ButtonNextPage buttonNextPage, buttonPreviousPage;
	private final List<ButtonCategory> categories = new ArrayList<>();

	public GuiNecronomicon() {
		super(Component.empty());
		long time = System.currentTimeMillis();
		startTime = time - cycleTime;
		drawTime = time;
		unknown50_1 = NecronomiconText.getRandomAklo(0);
		unknown50_2 = NecronomiconText.getRandomAklo(0);
		unknown95 = NecronomiconText.getRandomAklo(1);
		unknownFull = NecronomiconText.getRandomAklo(2);
	}

	public GuiNecronomicon(int bookType) {
		this();
		withBookType(bookType);
	}

	public GuiNecronomicon withBookType(int type) {
		if(bookType == type) return this;
		bookType = type;
		String[] textures = {"necronomicon", "necronomicon_cor", "necronomicon_dre", "necronomicon_omt", "abyssalnomicon"};
		bookGuiTextures = AbyssalCraft.loc("textures/gui/" + textures[Math.max(0, Math.min(4, type))] + ".png");
		return this;
	}

	public int getBookType() {
		return bookType;
	}

	public ResourceLocation getGuiTexture() {
		return bookGuiTextures;
	}

	public int getTurnupLimit() {
		return bookTotalTurnups;
	}

	public void setTurnupLimit(int i) {
		bookTotalTurnups = i;
	}

	protected int left() {
		return (width - guiWidth) / 2;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		drawTime = System.currentTimeMillis();
		super.tick();
	}

	protected <T extends AbstractWidget> T add(T widget) {
		return addRenderableWidget(widget);
	}

	/** The "Done" button and the page arrows, used by every screen */
	protected Button doneButton() {
		return add(Button.builder(CommonComponents.GUI_DONE, b -> actionPerformed(0)).bounds(width / 2 - 100, 4 + guiHeight, 200, 20).build());
	}

	protected ButtonNextPage arrow(int id, int x, boolean forward, boolean isLong) {
		return add(new ButtonNextPage(left() + x, 2 + (isLong ? 167 : 154), forward, isLong, b -> actionPerformed(id)));
	}

	protected ButtonCategory category(int id, int x, int y, String label, boolean locked, Item icon) {
		return add(new ButtonCategory(left() + x, 2 + y, label, locked, icon, b -> actionPerformed(id)));
	}

	@Override
	protected void init() {
		currentNecro = this;
		clearWidgets();
		categories.clear();
		buttonDone = doneButton();
		buttonNextPage = arrow(1, 215, true, false);
		buttonPreviousPage = arrow(2, 18, false, false);
		if(!isInfo) {
			String[] labels = {NecronomiconText.LABEL_INFORMATION, NecronomiconText.LABEL_SPELLBOOK, NecronomiconText.LABEL_RITUALS,
					NecronomiconText.LABEL_POTENTIAL_ENERGY, NecronomiconText.LABEL_HUH, NecronomiconText.LABEL_KNOWLEDGE, NecronomiconText.LABEL_MISC_INFORMATION};
			for(int i = 0; i < labels.length; i++)
				categories.add(category(3 + i, 14, 24 + 17 * i, labels[i], false, i == 4 && bookType == 4 ? ACItems.abyssalnomicon.get() : ACItems.necronomicon.get()));
			if(!AbyssalCraftAPI.getNecronomiconData().isEmpty())
				categories.add(category(10, 132, 24, NecronomiconText.LABEL_OTHER, false, ACItems.necronomicon.get()));
			sidebarIndex = "";
		}
		updateButtons();
	}

	protected Item getItem(int type) {
		return switch(type) {
		case 0 -> ACItems.necronomicon.get();
		case 1 -> ACItems.abyssal_wasteland_necronomicon.get();
		case 2 -> ACItems.dreadlands_necronomicon.get();
		case 3 -> ACItems.omothol_necronomicon.get();
		case 4 -> ACItems.abyssalnomicon.get();
		default -> ACItems.oblivion_catalyst.get();
		};
	}

	private void updateButtons() {
		buttonNextPage.visible = currTurnup < bookTotalTurnups - 1 && isInfo;
		buttonPreviousPage.visible = isInfo;
		buttonDone.visible = true;
	}

	protected void actionPerformed(int id) {
		if(id == 0) {
			isInfo = isNecroInfo = isKnowledgeInfo = false;
			currTurnup = 0;
			onClose();
		} else if(id == 1) {
			if(currTurnup < bookTotalTurnups - 1)
				++currTurnup;
		} else if(id == 2) {
			if(isInfo && currTurnup == 0) {
				isInfo = isNecroInfo = isKnowledgeInfo = false;
				init();
			} else if(currTurnup > 0)
				--currTurnup;
		} else if(id == 3)
			minecraft.setScreen(new GuiNecronomiconEntry(bookType, informationData(), this));
		else if(id == 4)
			minecraft.setScreen(new GuiNecronomiconSpells(bookType));
		else if(id == 5) {
			NecroData data = new NecroData("ritualinfo", NecronomiconText.LABEL_RITUALS, 0, NecronomiconText.RITUAL_INFO, AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("rituals"),
					new RitualGuiInstance(0, NecronomiconText.LABEL_NORMAL, "ritualsoverworld"),
					new RitualGuiInstance(1, NecronomiconText.LABEL_INFORMATION_ABYSSAL_WASTELAND, "ritualsabyssalwasteland"),
					new RitualGuiInstance(2, NecronomiconText.LABEL_INFORMATION_DREADLANDS, "ritualsdreadlands"),
					new RitualGuiInstance(3, NecronomiconText.LABEL_INFORMATION_OMOTHOL, "ritualsomothol"),
					new RitualGuiInstance(4, ACItems.abyssalnomicon.get().getDescriptionId(), "ritualsabyssalnomicon"));
			minecraft.setScreen(new GuiNecronomiconEntry(bookType, data, this));
		} else if(id == 6)
			minecraft.setScreen(new GuiNecronomiconEntry(bookType, AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("potentialenergy"), this, ACItems.necronomicon.get()));
		else if(id == 7) {
			isInfo = isNecroInfo = true;
			bookTotalTurnups = bookType == 4 ? 1 : 2;
			init();
		} else if(id == 8) {
			isInfo = isKnowledgeInfo = true;
			bookTotalTurnups = 2;
			init();
		} else if(id == 9)
			minecraft.setScreen(new GuiNecronomiconEntry(bookType, AbyssalCraftAPI.getInternalNDHandler().getInternalNecroData("miscinfo"), this, ACItems.necronomicon.get()));
		else if(id == 10)
			minecraft.setScreen(new GuiNecronomiconOther(bookType));
		if(buttonDone != null)
			updateButtons();
	}

	private NecroData informationData() {
		var nd = AbyssalCraftAPI.getInternalNDHandler();
		List<com.shinoow.abyssalcraft.api.necronomicon.INecroData> data = new ArrayList<>();
		data.add(new NecroData.Chapter("acinfo", NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0,
				new Page(1, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.ABYSSALCRAFT_1, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_1),
				new Page(2, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.BLANK, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_2),
				new Page(3, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.ABYSSALCRAFT_2, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_3),
				new Page(4, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.BLANK, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_4),
				new Page(5, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.ABYSSALCRAFT_3, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_5),
				new Page(6, NecronomiconText.LABEL_INFORMATION_ABYSSALCRAFT, 0, NecronomiconResources.BLANK, NecronomiconText.INFORMATION_ABYSSALCRAFT_PAGE_6)));
		data.add(nd.getInternalNecroData("greatoldones"));
		data.add(new Page(1, NecronomiconText.LABEL_INFORMATION_ABYSSALNOMICON, 4, NecronomiconText.INFORMATION_ABYSSALNOMICON));
		if(InternalNecroDataHandler.patrons() != null)
			data.add(InternalNecroDataHandler.patrons());
		data.add(new GuiInstance(0, NecronomiconText.LABEL_INFORMATION_MACHINES, "machines") {
			@Override public IUnlockCondition getCondition() { return new NecronomiconCondition(1); }
			@Override public Screen getOpenGui(int bookType, Screen parent) { return new GuiNecronomiconMachines(bookType, (GuiNecronomicon) parent); }
		});
		for(String id : new String[] {"overworld", "abyssalwasteland", "dreadlands", "omothol", "darkrealm"})
			data.add(nd.getInternalNecroData(id));
		return new NecroData("information", NecronomiconText.LABEL_INFORMATION, 0, data.toArray(new com.shinoow.abyssalcraft.api.necronomicon.INecroData[0]));
	}

	// ---- drawing ----

	@Override
	public void render(GuiGraphics graphics, int mx, int my, float partialTick) {
		this.graphics = graphics;
		int k = left();
		graphics.blit(bookGuiTextures, k, 2, 0, 0, guiWidth, guiHeight);
		super.render(graphics, mx, my, partialTick);
		if(isInfo) {
			if(isNecroInfo || isKnowledgeInfo)
				drawTitle(localize(isNecroInfo ? NecronomiconText.LABEL_HUH : NecronomiconText.LABEL_KNOWLEDGE));
			String s = I18n.get("necronomicon.turnupindicator", currTurnup + 1, bookTotalTurnups);
			if(getTurnupLimit() > 1)
				graphics.drawString(font, styled(s, false), k - font.width(styled(s, false)) + guiWidth - 22, 18, 0, false);
			drawInformationText(mx, my);
			if(sidebarIndex != null && !sidebarIndex.isEmpty())
				graphics.drawWordWrap(font, styled(sidebarIndex, false), k + 17, 10, 256, 0xC40000);
		} else
			drawIndexText();
	}

	/** The page title in the top left */
	protected void drawTitle(String title) {
		graphics.drawWordWrap(font, styled(title, false), left() + 17, 18, 116, 0xC40000);
	}

	protected void drawInformationText(int x, int y) {
		if(isNecroInfo) {
			if(currTurnup == 0) {
				writeText(1, bookType < 4 ? NecronomiconText.NECRONOMICON_PAGE_1 : NecronomiconText.ABYSSALNOMICON_PAGE_1);
				writeText(2, bookType < 4 ? NecronomiconText.NECRONOMICON_PAGE_2 : NecronomiconText.ABYSSALNOMICON_PAGE_2);
			} else if(currTurnup == 1 && bookType < 4) {
				writeText(1, NecronomiconText.NECRONOMICON_PAGE_3);
				writeText(2, NecronomiconText.NECRONOMICON_PAGE_4);
			}
		} else if(isKnowledgeInfo)
			if(currTurnup == 0) {
				writeText(1, NecronomiconText.KNOWLEDGE_INFO_1);
				writeText(2, NecronomiconText.KNOWLEDGE_INFO_2);
			} else if(currTurnup == 1) {
				writeText(1, NecronomiconText.KNOWLEDGE_INFO_3);
				writeText(2, unknown95, 28, true);
			}
	}

	protected void drawIndexText() {
		graphics.drawString(font, styled(localize(NecronomiconText.LABEL_INDEX), false), left() + 17, 18, 0, false);
	}

	/** Book text: the small "uniform" font, or Aklo */
	public static MutableComponent styled(String text, boolean aklo) {
		return Component.literal(text).withStyle(s -> s.withFont(aklo ? AKLO : UNIFORM));
	}

	protected void writeText(int page, String text) {
		writeText(page, text, 28);
	}

	protected void writeText(int page, String text, boolean aklo) {
		writeText(page, text, 28, aklo);
	}

	protected void writeText(int page, String text, int height) {
		writeText(page, text, height, 0, false);
	}

	protected void writeText(int page, String text, int height, boolean aklo) {
		writeText(page, text, height, 0, aklo);
	}

	protected void writeText(int page, String text, int height, int width) {
		writeText(page, text, height, width, false);
	}

	/** Text wrapped to a page (1 = left, 2 = right), 107 pixels wide */
	protected void writeText(int page, String text, int height, int width, boolean aklo) {
		if(page < 1 || page > 2) throw new IndexOutOfBoundsException("Page " + page);
		if(text == null) return;
		graphics.drawWordWrap(font, styled(localize(text), aklo), left() + (page == 1 ? 17 : 135) + width, height, 107, 0);
	}

	protected String localize(String str) {
		return I18n.get(str);
	}

	protected boolean isUnlocked(IUnlockCondition condition) {
		if(condition instanceof NecronomiconCondition)
			return getBookType() >= (int) condition.getConditionObject();
		return NecroDataCapability.getCap(minecraft.player).isUnlocked(condition, minecraft.player);
	}

	/** The full-size page images: book picture, crafting grid, ritual circle... */
	protected void drawPicture(ResourceLocation texture, int x) {
		graphics.blit(texture, left() + x, 2, 0, 0, 256, 256);
	}

	private static List<ItemStack> list(Object obj) {
		List<ItemStack> list = new ArrayList<>();
		if(obj instanceof ItemStack[] stacks)
			for(ItemStack stack : stacks) { if(!stack.isEmpty()) list.add(stack.copy()); }
		else if(obj instanceof List<?> l)
			for(Object o : l) { if(o instanceof ItemStack stack && !stack.isEmpty()) list.add(stack.copy()); }
		else if(obj instanceof Ingredient ingredient)
			for(ItemStack stack : ingredient.getItems()) { if(!stack.isEmpty()) list.add(stack.copy()); }
		else if(obj instanceof TagKey<?> tag && tag.isFor(net.minecraft.core.registries.Registries.ITEM))
			for(Item item : ForgeRegistries.ITEMS.tags().getTag((TagKey<Item>) tag)) list.add(new ItemStack(item));
		return list;
	}

	/** Item, block, stack, or a list/tag/ingredient cycling once a second */
	public void renderObject(int x, int y, @Nullable Object obj, int mx, int my) {
		if(obj == null) return;
		if(obj instanceof ItemStack[] || obj instanceof List || obj instanceof Ingredient || obj instanceof TagKey) {
			List<ItemStack> list = list(obj);
			if(!list.isEmpty())
				renderItem(x, y, list.get((int) ((drawTime - startTime) / cycleTime) % list.size()), mx, my);
		} else renderItem(x, y, APIUtils.convertToStack(obj), mx, my);
	}

	public void renderItem(int x, int y, @Nullable ItemStack stack, int mx, int my) {
		if(stack == null || stack.isEmpty()) return;
		if(mx > x && mx < x + 16 && my > y && my < y + 16)
			tooltipStack = stack;
		graphics.renderItem(stack, x, y);
		graphics.renderItemDecorations(font, stack, x, y);
	}

	public void renderTooltip(int x, int y) {
		if(!tooltipStack.isEmpty())
			graphics.renderTooltip(font, tooltipStack, x, y);
	}

	protected void updateSidebarIndex(GuiNecronomicon gui) {
		String str = "";
		GuiNecronomicon current = gui;
		while(current instanceof GuiNecronomiconEntry entry) {
			if(entry.data != null)
				str = str.isEmpty() ? localize(entry.data.getTitle()) : localize(entry.data.getTitle()) + " > " + str;
			current = entry.parent;
		}
		sidebarIndex = str;
	}

	private static class RitualGuiInstance extends GuiInstance {
		RitualGuiInstance(int displayIcon, String title, String identifier) {
			super(displayIcon, title, identifier);
		}

		@Override
		public IUnlockCondition getCondition() {
			return new NecronomiconCondition(displayIcon);
		}

		@Override
		public Screen getOpenGui(int bookType, Screen parent) {
			return new GuiNecronomiconRitualEntry(bookType, (GuiNecronomicon) parent, displayIcon);
		}
	}
}
