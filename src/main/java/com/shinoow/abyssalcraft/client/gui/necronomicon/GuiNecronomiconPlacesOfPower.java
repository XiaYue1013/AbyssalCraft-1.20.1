/*******************************************************************************
 * AbyssalCraft — Copyright (c) 2012 - 2025 Shinoow. LGPL-3.0.
 * Contributors: Shinoow - implementation; 1.20.1 port
 ******************************************************************************/
package com.shinoow.abyssalcraft.client.gui.necronomicon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.energy.structure.StructureHandler;
import com.shinoow.abyssalcraft.lib.NecronomiconResources;
import com.shinoow.abyssalcraft.lib.NecronomiconText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Places of Power: a turning model of each structure (the activation block blinks between the real block and the
 * multiblock marker every two seconds), its size, amplifiers and the blocks it's built with (tooltip)
 */
public class GuiNecronomiconPlacesOfPower extends GuiNecronomiconPagedList {

	private final List<IPlaceOfPower> places = new ArrayList<>();
	private int ticksInBook;
	private boolean showMultiblock;
	private IPlaceOfPower tooltipObj;
	private float partialTick;

	public GuiNecronomiconPlacesOfPower(int bookType, GuiNecronomicon gui) {
		super(bookType, gui);
	}

	@Override
	protected boolean hasEntries() {
		return !places.isEmpty();
	}

	@Override
	protected void initEntries() {
		for(IPlaceOfPower place : StructureHandler.instance().getStructures())
			if(isUnlocked(place.getUnlockCondition()) && place.getBookType() <= getBookType())
				places.add(place);
		setTurnupLimit(places.size());
	}

	@Override
	public void tick() {
		if(!Screen.hasShiftDown())
			ticksInBook++;
		if(ticksInBook % 40 == 0)
			showMultiblock = !showMultiblock;
		super.tick();
	}

	@Override
	public void render(net.minecraft.client.gui.GuiGraphics graphics, int mx, int my, float partialTick) {
		this.partialTick = partialTick;
		super.render(graphics, mx, my, partialTick);
	}

	@Override
	protected void drawEntry(int index, int x, int y) {
		IPlaceOfPower place = places.get(index);
		int k = left();
		drawTitle(localize(NecronomiconText.LABEL_STRUCTURES));
		tooltipObj = null;
		drawPicture(NecronomiconResources.PLACE_OF_POWER, 0);
		BlockState[][][] data = place.getRenderData();
		int sizeY = data.length, sizeX = 0, sizeZ = 0;
		for(BlockState[][] layer : data) {
			sizeX = Math.max(sizeX, layer.length);
			for(BlockState[] row : layer)
				sizeZ = Math.max(sizeZ, row.length);
		}
		writeText(1, "Height: " + sizeY + " blocks", 127);
		writeText(1, "Width: " + sizeX + " blocks", 137);
		writeText(1, "Depth: " + sizeZ + " blocks", 147);
		writeText(2, "Range amplifier: " + place.getAmplifier(AmplifierType.RANGE));
		writeText(2, "Duration amplifier: " + place.getAmplifier(AmplifierType.DURATION), 38);
		writeText(2, "Power amplifier: " + place.getAmplifier(AmplifierType.POWER), 48);
		writeText(2, localize(place.getDescription()), 64);
		if(x > k + 15 && x < k + 115 && y > 25 && y < 125)
			tooltipObj = place;
		renderStructure(data, sizeX, sizeY, sizeZ, place.getActivationPointForRender(), k + 65, (int) (73 - (sizeY - 3) * 10 / Math.max(1, sizeY - 3)));
		if(tooltipObj != null) {
			List<Component> lines = new ArrayList<>();
			lines.add(Component.literal(localize(NecronomiconText.LABEL_BUILT_WITH) + ":"));
			Arrays.stream(localize(tooltipObj.getRequiredBlockNames()).split(";")).forEach(s -> lines.add(Component.literal(s).withStyle(ChatFormatting.GRAY)));
			graphics.renderComponentTooltip(font, lines, x, y);
		}
	}

	/** data is [y][x][z] */
	private void renderStructure(BlockState[][][] data, int sizeX, int sizeY, int sizeZ, BlockPos activation, int xPos, int yPos) {
		float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
		float scale = Math.min(90 / Math.max(1, diag), 90F / Math.max(1, sizeY));
		float time = ticksInBook * 0.5F + (Screen.hasShiftDown() ? 0 : partialTick);
		PoseStack pose = graphics.pose();
		pose.pushPose();
		pose.translate(xPos, yPos, 100);
		pose.scale(scale, -scale, scale);
		pose.mulPose(Axis.XP.rotationDegrees(-30F));
		pose.mulPose(Axis.YP.rotationDegrees(time + 45F));
		pose.translate(-sizeX / 2F, -sizeY / 2F, -sizeZ / 2F);
		MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
		var dispatcher = minecraft.getBlockRenderer();
		for(int y = 0; y < data.length; y++)
			for(int x = 0; x < data[y].length; x++)
				for(int z = 0; z < data[y][x].length; z++) {
					BlockState state = data[y][x][z];
					if(state == null || state.isAir()) continue;
					if(activation != null && activation.equals(new BlockPos(x, y, z)) && showMultiblock)
						state = ACBlocks.multi_block.get().defaultBlockState();
					pose.pushPose();
					pose.translate(x, y, z);
					if(state.getRenderShape() == RenderShape.MODEL)
						dispatcher.renderSingleBlock(state, pose, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
					else {
						// block entity rendered blocks (statues...) are drawn as their item
						pose.translate(0.5, 0.5, 0.5);
						minecraft.getItemRenderer().renderStatic(new ItemStack(state.getBlock()), ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT,
								OverlayTexture.NO_OVERLAY, pose, buffers, minecraft.level, 0);
					}
					pose.popPose();
				}
		buffers.endBatch();
		pose.popPose();
	}
}
