/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 *     1.20.1 port - the deity comes from the block (one block per deity), the facing from the block state
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.IEnergyManipulator;
import com.shinoow.abyssalcraft.api.energy.IEnergyTransporterItem;
import com.shinoow.abyssalcraft.api.energy.PEUtils;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureComponent;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.common.blocks.BlockStatue;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.init.ACDimensions;
import com.shinoow.abyssalcraft.lib.ACConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Statue of a Great Old One: a PE manipulator that sends PE to nearby collectors, dropped PE items and the
 * PE item a nearby player (carrying a Necronomicon) holds. Builds up tolerance, and disrupts at 100.
 */
public class TileEntityStatue extends BlockEntity implements IEnergyManipulator, IStructureComponent {

	private int timer;
	private final int timerMax = 120;
	private AmplifierType currentAmplifier;
	private DeityType currentDeity;
	private int tolerance;
	private boolean isMultiblock;
	private BlockPos basePos;
	private Set<BlockPos> positions = new HashSet<>();

	public TileEntityStatue(BlockPos pos, BlockState state) {
		super(ACBlockEntities.STATUE.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		timer = nbttagcompound.getInt("Timer");
		tolerance = nbttagcompound.getInt("Tolerance");
		PEUtils.readManipulatorNBT(this, nbttagcompound);
		isMultiblock = nbttagcompound.getBoolean("IsMultiblock");
		basePos = nbttagcompound.contains("BasePosition") ? BlockPos.of(nbttagcompound.getLong("BasePosition")) : null;
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		nbttagcompound.putInt("Timer", timer);
		nbttagcompound.putInt("Tolerance", tolerance);
		PEUtils.writeManipulatorNBT(this, nbttagcompound);
		nbttagcompound.putBoolean("IsMultiblock", isMultiblock);
		if(basePos != null)
			nbttagcompound.putLong("BasePosition", basePos.asLong());
	}

	/** Sets the tolerance (used when a statue that was picked up is placed again) */
	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
		setChanged();
	}

	@Override
	public void setActive(AmplifierType amp, DeityType deity){
		if(!isActive()){
			setActiveDeity(deity);
			setActiveAmplifier(amp);
		}
	}

	@Override
	public boolean isActive(){
		return currentAmplifier != null;
	}

	@Override
	public float getEnergyQuanta() {

		return isActive() ? 10 * (Math.max(getAmplifier(AmplifierType.POWER), 1.0F) + PEUtils.getStructureAmplifier(level, this, AmplifierType.POWER)) : 5;
	}

	@Override
	public DeityType getDeity(BlockState state) {
		return state.getBlock() instanceof BlockStatue statue ? statue.getDeity() : null;
	}

	@Override
	public float getAmplifier(AmplifierType type){

		if(type == currentAmplifier)
			switch(type){
			case DURATION:
				return currentDeity == getDeity(getBlockState()) ? 4 : 2;
			case POWER:
				return currentDeity == getDeity(getBlockState()) ? 2.5F : 1.5F;
			case RANGE:
				return currentDeity == getDeity(getBlockState()) ? 6 : 4;
			default:
				return 0;
			}
		else return 0;
	}

	@Override
	public DeityType getActiveDeity() {

		return currentDeity;
	}

	@Override
	public AmplifierType getActiveAmplifier() {

		return currentAmplifier;
	}

	@Override
	public void setActiveDeity(DeityType deity) {

		currentDeity = deity;
	}

	@Override
	public void setActiveAmplifier(AmplifierType amplifier) {

		currentAmplifier = amplifier;
	}

	@Override
	public void addTolerance(int num) {
		if(!isInMultiblock()) {
			tolerance += num;
			setChanged();
		}
	}

	@Override
	public int getTolerance() {

		return tolerance;
	}

	@Override
	public boolean canTransferPE() {

		return true;
	}

	@Override
	public void disrupt() {
		tolerance = 0;
		setChanged();
		if(level instanceof ServerLevel serverLevel && level.dimension() != ACDimensions.OMOTHOL && !isInMultiblock() && !ACConfig.no_disruptions) {
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
			if(bolt != null) {
				bolt.moveTo(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ());
				bolt.setVisualOnly(true);
				serverLevel.addFreshEntity(bolt);
			}
			DisruptionHandler.instance().generateDisruption(getDeity(getBlockState()), level, worldPosition,
					level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(16, 16, 16)));
		}
	}

	@Override
	public Set<BlockPos> getEnergyCollectors(){
		return positions;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityStatue tile) {

		if(tile.isActive())
			((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 0, 0, 0, 0, 1.0);
		else PEUtils.clearManipulatorData(tile);

		int range = (int) (7 + PEUtils.getRangeAmplifiers(level, pos, tile)*4 + tile.getAmplifier(AmplifierType.RANGE));

		if(level.canSeeSky(pos.above()) || tile.isMultiblock)
			if(PEUtils.checkForAdjacentManipulators(level, pos) || tile.isMultiblock){
				if(level.getGameTime() % 200 == 0)
					PEUtils.locateCollectors(level, pos, tile);
				Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), range, false);
				if(player != null && EntityUtil.hasNecronomicon(player)){
					ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
					ItemStack item1 = player.getItemInHand(InteractionHand.OFF_HAND);
					if(item.getItem() instanceof IEnergyTransporterItem || item1.getItem() instanceof IEnergyTransporterItem){
						tile.timer++;
						if(tile.timer >= (int)(tile.timerMax / (Math.max(tile.getAmplifier(AmplifierType.DURATION), 1.0F) + PEUtils.getStructureAmplifier(level, tile, AmplifierType.DURATION)))){
							tile.timer = level.random.nextInt(10);
							PEUtils.transferPEToNearbyPlayers(level, pos, tile, range);
						}
					}
				}

				PEUtils.transferPEToNearbyDroppedItems(level, pos, tile, range);
				PEUtils.transferPEToCollectors(level, pos, tile);
			}
		if(tile.tolerance >= 100)
			tile.disrupt();
	}

	@Override
	public boolean isInMultiblock() {

		return isMultiblock;
	}

	@Override
	public void setInMultiblock(boolean bool) {
		isMultiblock = bool;
		setChanged();
	}

	@Nullable
	@Override
	public BlockPos getBasePosition() {

		return basePos;
	}

	@Override
	public void setBasePosition(BlockPos pos) {
		basePos = pos;
		setChanged();
	}
}
