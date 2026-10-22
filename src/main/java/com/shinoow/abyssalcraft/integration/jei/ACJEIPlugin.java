/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation (integration/jei); 1.20.1 port - JEI 15 API, one file
 ******************************************************************************/
package com.shinoow.abyssalcraft.integration.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.FuelType;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.recipe.CrystallizerRecipes;
import com.shinoow.abyssalcraft.api.recipe.EngraverRecipes;
import com.shinoow.abyssalcraft.api.recipe.Materialization;
import com.shinoow.abyssalcraft.api.recipe.MaterializerRecipes;
import com.shinoow.abyssalcraft.api.recipe.TransmutatorRecipes;
import com.shinoow.abyssalcraft.api.recipe.UpgradeKitRecipes;
import com.shinoow.abyssalcraft.api.rending.Rending;
import com.shinoow.abyssalcraft.api.rending.RendingRegistry;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconCreationRitual;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconTransformationRitual;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.init.ItemHandler;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.util.IHiddenRitual;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * JEI: the Transmutator, Crystallizer (and their fuels), Engraver, Materializer, rituals, Rending and Upgrade Kit recipes.
 * Port: the three 1.12.2 ritual categories are one category (creation result and transformation shown when there).
 */
@JeiPlugin
public class ACJEIPlugin implements IModPlugin {

	record Transmutation(Ingredient input, ItemStack output) {}
	record Crystallization(Ingredient input, ItemStack output1, ItemStack output2) {}
	record Engraving(ItemStack coin, ItemStack engraving, ItemStack output) {}
	record Upgrade(ItemStack input, ItemStack kit, ItemStack output) {}
	record Fuel(ItemStack fuel, int ticks) {}

	static final RecipeType<Transmutation> TRANSMUTATION = RecipeType.create(AbyssalCraft.modid, "transmutation", Transmutation.class);
	static final RecipeType<Crystallization> CRYSTALLIZATION = RecipeType.create(AbyssalCraft.modid, "crystallization", Crystallization.class);
	static final RecipeType<Fuel> TRANSMUTATOR_FUEL = RecipeType.create(AbyssalCraft.modid, "transmutator_fuel", Fuel.class);
	static final RecipeType<Fuel> CRYSTALLIZER_FUEL = RecipeType.create(AbyssalCraft.modid, "crystallizer_fuel", Fuel.class);
	static final RecipeType<Engraving> ENGRAVING = RecipeType.create(AbyssalCraft.modid, "engraving", Engraving.class);
	static final RecipeType<Materialization> MATERIALIZATION = RecipeType.create(AbyssalCraft.modid, "materialization", Materialization.class);
	static final RecipeType<NecronomiconRitual> RITUAL = RecipeType.create(AbyssalCraft.modid, "ritual", NecronomiconRitual.class);
	static final RecipeType<Rending> RENDING = RecipeType.create(AbyssalCraft.modid, "rending", Rending.class);
	static final RecipeType<Upgrade> UPGRADE = RecipeType.create(AbyssalCraft.modid, "upgrade", Upgrade.class);

	@Override
	public ResourceLocation getPluginUid() {
		return AbyssalCraft.loc("jei");
	}

	private static ResourceLocation tex(String name) {
		return AbyssalCraft.loc("textures/gui/container/" + name + ".png");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new Category<>(TRANSMUTATION, "container.abyssalcraft.transmutator", gui.createDrawable(tex("transmutator_nei"), 55, 16, 82, 54),
						gui.createDrawableItemLike(ACBlocks.transmutator.get()), (b, r) -> {
							b.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(r.input());
							b.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(r.output());
						}, null),
				new Category<>(CRYSTALLIZATION, "container.abyssalcraft.crystallizer", gui.createDrawable(tex("crystallizer_nei"), 55, 16, 116, 54),
						gui.createDrawableItemLike(ACBlocks.crystallizer.get()), (b, r) -> {
							b.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(r.input());
							b.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(r.output1());
							if(!r.output2().isEmpty())
								b.addSlot(RecipeIngredientRole.OUTPUT, 78, 19).addItemStack(r.output2());
						}, null),
				new Category<>(TRANSMUTATOR_FUEL, "jei.abyssalcraft.fuel.transmutator", gui.createBlankDrawable(140, 36),
						gui.createDrawableItemLike(ACBlocks.transmutator.get()), ACJEIPlugin::fuelSlot, ACJEIPlugin::fuelText),
				new Category<>(CRYSTALLIZER_FUEL, "jei.abyssalcraft.fuel.crystallizer", gui.createBlankDrawable(140, 36),
						gui.createDrawableItemLike(ACBlocks.crystallizer.get()), ACJEIPlugin::fuelSlot, ACJEIPlugin::fuelText),
				new Category<>(ENGRAVING, "container.abyssalcraft.engraver", gui.createDrawable(tex("engraver_nei"), 55, 16, 82, 54),
						gui.createDrawableItemLike(ACBlocks.engraver.get()), (b, r) -> {
							b.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(r.coin());
							b.addSlot(RecipeIngredientRole.INPUT, 1, 37).addItemStack(r.engraving());
							b.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(r.output());
						}, null),
				new Category<>(MATERIALIZATION, "container.abyssalcraft.materializer", gui.createDrawable(tex("materializer_nei"), 32, 47, 116, 72),
						gui.createDrawableItemLike(ACBlocks.materializer.get()), (b, r) -> {
							int[] xs = {3, 26, 50, 73, 96};
							for(int i = 0; i < Math.min(5, r.input.length); i++)
								b.addSlot(RecipeIngredientRole.INPUT, xs[i], 3).addItemStack(r.input[i]);
							b.addSlot(RecipeIngredientRole.OUTPUT, 50, 55).addItemStack(r.output);
						}, null),
				new Category<>(RITUAL, "jei.abyssalcraft.ritual", gui.createDrawable(tex("ritual_nei"), 5, 11, 166, 140),
						gui.createDrawableItemLike(ACItems.necronomicon.get()), ACJEIPlugin::ritualSlots, ACJEIPlugin::ritualText),
				new Category<>(RENDING, "jei.abyssalcraft.rending", gui.createBlankDrawable(166, 100),
						gui.createDrawableItemLike(ACItems.staff_of_rending.get()), (b, r) -> b.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStack(r.getOutput()),
						(r, g) -> g.drawWordWrap(Minecraft.getInstance().font, Component.translatable(r.getJeiDescription()), 0, 22, 166, 0x404040)),
				new Category<>(UPGRADE, "jei.abyssalcraft.upgrade", gui.createDrawable(new ResourceLocation("textures/gui/container/anvil.png"), 15, 46, 146, 26),
						gui.createDrawableItemLike(Items.ANVIL), (b, r) -> {
							b.addSlot(RecipeIngredientRole.INPUT, 12, 1).addItemStack(r.input());
							b.addSlot(RecipeIngredientRole.INPUT, 61, 1).addItemStack(r.kit());
							b.addSlot(RecipeIngredientRole.OUTPUT, 119, 1).addItemStack(r.output());
						}, null));
	}

	private static void fuelSlot(IRecipeLayoutBuilder b, Fuel fuel) {
		b.addSlot(RecipeIngredientRole.INPUT, 1, 10).addItemStack(fuel.fuel());
	}

	private static void fuelText(Fuel fuel, GuiGraphics g) {
		g.drawString(Minecraft.getInstance().font, I18n.get("jei.abyssalcraft.fuel.burn_time", fuel.ticks()), 24, 14, 0x404040, false);
	}

	private static final int[][] PEDESTALS = {{58, 30}, {84, 40}, {94, 66}, {84, 92}, {58, 103}, {32, 92}, {22, 66}, {32, 40}};

	private static void ritualSlots(IRecipeLayoutBuilder b, NecronomiconRitual ritual) {
		Object[] offerings = ritual instanceof NecronomiconTransformationRitual t ? t.getCombinedContent() : ritual.getOfferings();
		for(int i = 0; i < Math.min(8, offerings.length); i++)
			if(offerings[i] != null)
				b.addSlot(RecipeIngredientRole.INPUT, PEDESTALS[i][0] + 1, PEDESTALS[i][1] + 1).addItemStacks(stacks(offerings[i]));
		if(ritual.getSacrifice() != null)
			b.addSlot(RecipeIngredientRole.INPUT, 59, 67).addItemStacks(stacks(ritual.getSacrifice()));
		b.addSlot(RecipeIngredientRole.CATALYST, 1, 134).addItemStack(new ItemStack(book(ritual.getBookType())));
		if(ritual instanceof NecronomiconCreationRitual creation)
			b.addSlot(RecipeIngredientRole.OUTPUT, 59, 140).addItemStack(creation.getItem());
	}

	private static void ritualText(NecronomiconRitual ritual, GuiGraphics g) {
		var font = Minecraft.getInstance().font;
		g.drawString(font, I18n.get(ritual.getUnlocalizedName()), 0, 0, 0x404040, false);
		g.drawString(font, (int) ritual.getReqEnergy() + " PE", 120, 0, 0x404040, false);
	}

	private static Item book(int type) {
		return switch(type) {
		case 1 -> ACItems.abyssal_wasteland_necronomicon.get();
		case 2 -> ACItems.dreadlands_necronomicon.get();
		case 3 -> ACItems.omothol_necronomicon.get();
		case 4 -> ACItems.abyssalnomicon.get();
		default -> ACItems.necronomicon.get();
		};
	}

	/** A ritual offering (item, block, stack, stack array, list, tag, ingredient) as the stacks it accepts */
	private static List<ItemStack> stacks(Object o) {
		List<ItemStack> list = new ArrayList<>();
		if(o instanceof ItemStack[] array)
			list.addAll(List.of(array));
		else if(o instanceof List<?> l)
			l.forEach(e -> list.addAll(stacks(e)));
		else if(o instanceof Ingredient ingredient)
			list.addAll(List.of(ingredient.getItems()));
		else if(o instanceof net.minecraft.tags.TagKey<?> tag)
			list.addAll(List.of(Ingredient.of((net.minecraft.tags.TagKey<Item>) tag).getItems()));
		else {
			ItemStack stack = APIUtils.convertToStack(o);
			if(!stack.isEmpty()) list.add(stack);
		}
		return list;
	}

	/** Inscribed scrolls are different ingredients per spell */
	@Override
	public void registerItemSubtypes(mezz.jei.api.registration.ISubtypeRegistration registration) {
		for(var scroll : List.of(ACItems.basic_scroll, ACItems.lesser_scroll, ACItems.moderate_scroll, ACItems.greater_scroll, ACItems.antimatter_scroll, ACItems.oblivion_scroll))
			registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, scroll.get(),
					(stack, context) -> stack.hasTag() ? stack.getTag().getString("Spell") : mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter.NONE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(TRANSMUTATION, TransmutatorRecipes.instance().getTransmutationList().stream()
				.map(t -> new Transmutation(t.input(), t.output().get())).filter(t -> !t.output().isEmpty()).toList());
		registration.addRecipes(CRYSTALLIZATION, CrystallizerRecipes.instance().getCrystallizationList().stream()
				.map(c -> new Crystallization(c.input(), c.output1().get(), c.output2() != null ? c.output2().get() : ItemStack.EMPTY))
				.filter(c -> !c.output1().isEmpty()).toList());
		List<Fuel> transmutatorFuels = new ArrayList<>(), crystallizerFuels = new ArrayList<>();
		for(Item item : ForgeRegistries.ITEMS) {
			ItemStack stack = new ItemStack(item);
			int t = AbyssalCraftAPI.getFuelValue(stack, FuelType.TRANSMUTATOR), c = AbyssalCraftAPI.getFuelValue(stack, FuelType.CRYSTALLIZER);
			if(t > 0) transmutatorFuels.add(new Fuel(stack, t));
			if(c > 0) crystallizerFuels.add(new Fuel(stack, c));
		}
		registration.addRecipes(TRANSMUTATOR_FUEL, transmutatorFuels);
		registration.addRecipes(CRYSTALLIZER_FUEL, crystallizerFuels);
		List<Engraving> engravings = new ArrayList<>();
		for(ItemStack coin : EngraverRecipes.instance().getCoinList())
			for(var engraving : EngraverRecipes.instance().getEngravings().keySet()) {
				ItemStack out = EngraverRecipes.instance().getEngravingResult(coin, engraving);
				if(!out.isEmpty())
					engravings.add(new Engraving(coin, new ItemStack(engraving), out));
			}
		registration.addRecipes(ENGRAVING, engravings);
		registration.addRecipes(MATERIALIZATION, MaterializerRecipes.instance().getMaterializationList());
		registration.addRecipes(RITUAL, RitualRegistry.instance().getRituals().stream().filter(r -> !(r instanceof IHiddenRitual)).toList());
		registration.addRecipes(RENDING, RendingRegistry.instance().getRendings());
		if(ACConfig.upgrade_kits) {
			List<Upgrade> upgrades = new ArrayList<>();
			UpgradeKitRecipes.instance().getAllUpgrades().forEach((kit, map) -> map.forEach((in, out) -> upgrades.add(new Upgrade(in, new ItemStack(kit), out))));
			registration.addRecipes(UPGRADE, upgrades);
		}
		registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
				List.of(new ItemStack(ACBlocks.house.get()), new ItemStack(ACBlocks.multi_block.get())));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ACBlocks.transmutator.get()), TRANSMUTATION, TRANSMUTATOR_FUEL);
		registration.addRecipeCatalyst(new ItemStack(ACBlocks.crystallizer.get()), CRYSTALLIZATION, CRYSTALLIZER_FUEL);
		registration.addRecipeCatalyst(new ItemStack(ACBlocks.engraver.get()), ENGRAVING);
		registration.addRecipeCatalyst(new ItemStack(ACBlocks.materializer.get()), MATERIALIZATION);
		for(int i = 0; i <= 4; i++)
			registration.addRecipeCatalyst(new ItemStack(book(i)), RITUAL);
		for(Supplier<Item> staff : List.<Supplier<Item>>of(ItemHandler.STAFF_OF_RENDING, ItemHandler.ABYSSAL_WASTELAND_STAFF_OF_RENDING,
				ItemHandler.DREADLANDS_STAFF_OF_RENDING, ItemHandler.OMOTHOL_STAFF_OF_RENDING, ItemHandler.STAFF_OF_THE_GATEKEEPER))
			registration.addRecipeCatalyst(new ItemStack(staff.get()), RENDING);
		if(ACConfig.upgrade_kits)
			for(Item anvil : List.of(Items.ANVIL, Items.CHIPPED_ANVIL, Items.DAMAGED_ANVIL))
				registration.addRecipeCatalyst(new ItemStack(anvil), UPGRADE);
	}

	/** A category from its parts */
	private static class Category<T> implements IRecipeCategory<T> {
		private final RecipeType<T> type;
		private final Component title;
		private final IDrawable background, icon;
		private final BiConsumer<IRecipeLayoutBuilder, T> slots;
		private final BiConsumer<T, GuiGraphics> extra;

		Category(RecipeType<T> type, String title, IDrawable background, IDrawable icon, BiConsumer<IRecipeLayoutBuilder, T> slots, BiConsumer<T, GuiGraphics> extra) {
			this.type = type;
			this.title = Component.translatable(title);
			this.background = background;
			this.icon = icon;
			this.slots = slots;
			this.extra = extra;
		}

		@Override public RecipeType<T> getRecipeType() { return type; }
		@Override public Component getTitle() { return title; }
		@Override public IDrawable getBackground() { return background; }
		@Override public IDrawable getIcon() { return icon; }
		@Override public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) { slots.accept(builder, recipe); }

		@Override
		public void draw(T recipe, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY) {
			if(extra != null)
				extra.accept(recipe, graphics);
		}
	}
}
