/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.energy.IEnergyContainerItem;
import com.shinoow.abyssalcraft.api.spell.Spell;
import com.shinoow.abyssalcraft.common.inventory.ContainerSpellbook;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiSpellbook extends AbstractContainerScreen<ContainerSpellbook> {

	private static final ResourceLocation TEXTURE = AbyssalCraft.loc("textures/gui/container/spellcraft_test.png");

	public GuiSpellbook(ContainerSpellbook menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageHeight = 238;
		inventoryLabelY = imageHeight - 92;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		if(menu.book.getItem() instanceof IEnergyContainerItem pe)
			graphics.drawString(font, String.format("%d/%d PE", (int) pe.getContainedEnergy(menu.book), pe.getMaxEnergy(menu.book)), 15, 120, 4210752, false);
		graphics.drawString(font, playerInventoryTitle, 6, inventoryLabelY, 4210752, false);
		Spell spell = menu.currentSpell != null && menu.isUnlocked(menu.currentSpell) ? menu.currentSpell : null;
		graphics.drawString(font, Component.literal("Spell name: ").append(spell != null ? Component.translatable(spell.getUnlocalizedName()).withStyle(ChatFormatting.AQUA) : Component.empty()), 15, 15, 4210752, false);
		graphics.drawString(font, "PE per cast: " + (spell != null ? (int) spell.getReqEnergy() : ""), 15, 25, 4210752, false);
		graphics.drawString(font, Component.literal("Spell type: ").append(spell != null ? Component.literal(spell.requiresCharging() ? "Charging" : "Instant").withStyle(ChatFormatting.GOLD) : Component.empty()), 15, 35, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
