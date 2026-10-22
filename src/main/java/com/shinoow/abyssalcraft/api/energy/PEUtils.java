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
 ******************************************************************************/
package com.shinoow.abyssalcraft.api.energy;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureBase;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureComponent;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;


/**
 * Backbone for handling interactions between<br>
 * various PE mechanics
 * @author shinoow
 *
 */
public class PEUtils {

	/**
	 * Attempts to transfer PE from a Manipulator to nearby Players
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param manipulator PE Manipulator
	 * @param range Transfer Range
	 */
	public static void transferPEToNearbyPlayers(Level world, BlockPos pos, IEnergyManipulator manipulator, int range){

		List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(pos).inflate(range),
				EntityUtil::hasNecronomicon);

		for(Player player : players){
			ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack item1 = player.getItemInHand(InteractionHand.OFF_HAND);
			if(canStackAcceptPE(item) || canStackAcceptPE(item1))
				if(manipulator.canTransferPE()){
					transferPEToStack(item, manipulator);
					transferPEToStack(item1, manipulator);
					AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(pos, player, world);
				}
		}
	}

	private static boolean canStackAcceptPE(ItemStack stack) {
		if(stack.getItem() instanceof IEnergyTransporterItem)
			return ((IEnergyTransporterItem) stack.getItem()).canAcceptPEExternally(stack);
		else return false;
	}

	/**
	 * Attempts to transfer PE from a Manipulator to nearby Dropped Items<br>
	 * (that are Energy Container Items)
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param manipulator PE Manipulator
	 * @param range Transfer Range
	 */
	public static void transferPEToNearbyDroppedItems(Level world, BlockPos pos, IEnergyManipulator manipulator, int range){

		List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(range),
				e -> canStackAcceptPE(e.getItem()));

		for(ItemEntity item : items)
			if(world.random.nextInt(120-(int)(20 * manipulator.getAmplifier(AmplifierType.DURATION))) == 0)
				if(manipulator.canTransferPE()){
					transferPEToStack(item.getItem(), manipulator);
					AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(pos, item, world);
				}
	}

	/**
	 * Shortcut method for Transferring PE to an ItemStack
	 * @param stack ItemStack to potentially transfer PE to
	 * @param manipulator PE Manipulator to transfer said PE
	 */
	private static void transferPEToStack(ItemStack stack, IEnergyManipulator manipulator){
		if(stack.getItem() instanceof IEnergyTransporterItem)
			if(((IEnergyTransporterItem) stack.getItem()).canAcceptPEExternally(stack)){
				((IEnergyTransporterItem) stack.getItem()).addEnergy(stack, manipulator.getEnergyQuanta());
				manipulator.addTolerance(manipulator.isActive() ? 4 : 2);
			}
	}

	/**
	 * Attempts to transfer PE from a Manipulator to nearby Collectors
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param manipulator PE Manipulator
	 * @param boost Any range boost applied to the manipulator
	 * @deprecated Use {@link PEUtils#transferPEToCollectors(Level, BlockPos, IEnergyManipulator)} instead
	 */
	@Deprecated
	public static void transferPEToCollectors(Level world, BlockPos pos, IEnergyManipulator manipulator, int boost){

		int xp = pos.getX();
		int yp = pos.getY();
		int zp = pos.getZ();

		List<BlockEntity> collectors = new ArrayList<>();

		BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();

		outer: for(int x = -1*(3+boost); x <= 3+boost; x++)
			for(int y = 0; y <= getRangeAmplifiers(world, pos, manipulator); y++)
				for(int z = -1*(3+boost); z <= 3+boost; z++)
					if(x < -2 || x > 2 || z < -2 || z > 2){
						if(collectors.size() == 20)
							break outer;
						pos1.set(xp + x, yp - y, zp + z);
						BlockEntity te = world.getBlockEntity(pos1);
						if(isCollector(te))
							collectors.add(te);
					}

		for(BlockEntity tile : collectors)
			if(checkForAdjacentCollectors(world, tile.getBlockPos()))
				if(world.random.nextInt(120-(int)(20 * manipulator.getAmplifier(AmplifierType.DURATION))) == 0)
					if(((IEnergyCollector) tile).canAcceptPE() && manipulator.canTransferPE()){
						((IEnergyCollector) tile).addEnergy(manipulator.getEnergyQuanta());
						manipulator.addTolerance(manipulator.isActive() ? 2 : 1);
						AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(pos, tile.getBlockPos(), world);
					}
	}

	/**
	 * Attempts to transfer PE from a Manipulator to nearby Collectors
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param manipulator PE Manipulator
	 */
	public static void transferPEToCollectors(Level world, BlockPos pos, IEnergyManipulator manipulator){
		int timeDiscount = (int)(20 * manipulator.getAmplifier(AmplifierType.DURATION));

		manipulator.getEnergyCollectors().stream().map(p -> world.getBlockEntity(p)).filter(PEUtils::isCollector).forEach(tile -> {
			if(checkForAdjacentCollectors(world, tile.getBlockPos()))
				if(world.random.nextInt(120-timeDiscount) == 0)
					if(((IEnergyCollector) tile).canAcceptPE() && manipulator.canTransferPE()){
						((IEnergyCollector) tile).addEnergy(manipulator.getEnergyQuanta());
						manipulator.addTolerance(manipulator.isActive() ? 2 : 1);
						AbyssalCraftAPI.getInternalMethodHandler().spawnPEStream(pos, tile.getBlockPos(), world);
					}
		});
	}

	/**
	 * Locates and stores the positions of Energy Containers in range of the Manipulator
	 * @param world Current Level
	 * @param pos Current Position
	 * @param manipulator PE Manipulator
	 */
	public static void locateCollectors(Level world, BlockPos pos, IEnergyManipulator manipulator) {
		manipulator.getEnergyCollectors().clear();

		Set<BlockPos> positions = manipulator.getEnergyCollectors();

		int boost = (int) (getRangeAmplifiers(world, pos, manipulator) + manipulator.getAmplifier(AmplifierType.RANGE)/2);

		BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();

		outer: for(int x = -1*(3+boost); x <= 3+boost; x++)
			for(int y = 0; y <= getRangeAmplifiers(world, pos, manipulator); y++)
				for(int z = -1*(3+boost); z <= 3+boost; z++)
					if(x < -2 || x > 2 || z < -2 || z > 2){
						if(positions.size() == 20)
							break outer;
						pos1.set(pos.getX() + x, pos.getY() - y, pos.getZ() + z);
						BlockEntity te = world.getBlockEntity(pos1);
						if(isCollector(te))
							positions.add(pos1.immutable());
					}
	}

	/**
	 * Utility method for clearing the active Deity and Amplifier<br>
	 * when a PE Manipulator no longer is active. Should be called in<br>
	 * the BlockEntity ticker whenever the manipulator isn't active<br>
	 * to ensure the data is properly erased when it should be.
	 * @param manipulator PE Manipulator to reset
	 */
	public static void clearManipulatorData(IEnergyManipulator manipulator){
		if(manipulator.getActiveAmplifier() != null || manipulator.getActiveDeity() != null){
			manipulator.setActiveDeity(null);
			manipulator.setActiveAmplifier(null);
			((BlockEntity) manipulator).setChanged();
		}
	}

	/**
	 * Utility method for reading specific NBT data in a PE Manipulator.<br>
	 * Call in your BlockEntity's load!
	 * @param manipulator PE Manipulator
	 * @param compound NBT Tag Compound
	 */
	public static void readManipulatorNBT(IEnergyManipulator manipulator, CompoundTag compound){
		if(compound.contains("ActiveDeity") && !compound.getString("ActiveDeity").equals(""))
			manipulator.setActiveDeity(DeityType.valueOf(compound.getString("ActiveDeity")));
		if(compound.contains("ActiveAmplifier") && !compound.getString("ActiveAmplifier").equals(""))
			manipulator.setActiveAmplifier(AmplifierType.valueOf(compound.getString("ActiveAmplifier")));
	}

	/**
	 * Utility method for writing specific NBT data in a PE Manipulator.<br>
	 * Call in your BlockEntity's saveAdditional!
	 * @param manipulator PE Manipulator
	 * @param compound NBT Tag Compound
	 */
	public static void writeManipulatorNBT(IEnergyManipulator manipulator, CompoundTag compound){
		if(manipulator.getActiveDeity() != null)
			compound.putString("ActiveDeity", manipulator.getActiveDeity().name());
		else compound.putString("ActiveDeity", "");
		if(manipulator.getActiveAmplifier() != null)
			compound.putString("ActiveAmplifier", manipulator.getActiveAmplifier().name());
		else compound.putString("ActiveAmplifier", "");
	}

	/**
	 * Checks for any range amplifying blocks below a specific BlockPos
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @return A number between 0 and 2, representing the amount of range amplifiers below this BlockPos
	 */
	public static int getRangeAmplifiers(Level world, BlockPos pos, IEnergyManipulator manipulator){
		Block block1 = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock();
		Block block2 = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 2, pos.getZ())).getBlock();
		int num = 0;
		if(block1 != null && block1 instanceof IEnergyAmplifier &&
				((IEnergyAmplifier) block1).getAmplifierType() == AmplifierType.RANGE)
			num = 1;
		if(block1 != null && block1 instanceof IEnergyAmplifier &&
				((IEnergyAmplifier) block1).getAmplifierType() == AmplifierType.RANGE
				&& block2 != null && block2 instanceof IEnergyAmplifier &&
				((IEnergyAmplifier) block2).getAmplifierType() == AmplifierType.RANGE)
			num = 2;

		if(manipulator instanceof IStructureComponent)
			num += PEUtils.getStructureAmplifier(world, (IStructureComponent) manipulator, AmplifierType.RANGE);

		return num;
	}

	/**
	 * Checks if the Structure Component is currently part of a structure, then attempts to fetch any amplifiers
	 * @param world Current Level
	 * @param component Component being checked
	 * @param type Amplifier Type to check for
	 * @return A value to amplify a stat with, or 0
	 */
	public static float getStructureAmplifier(Level world, IStructureComponent component, AmplifierType type) {

		float amplifier = 0;

		if(component.isInMultiblock() && component.getBasePosition() != null) {
			if(world.getBlockEntity(component.getBasePosition()) instanceof IStructureBase)
				amplifier = ((IStructureBase) world.getBlockEntity(component.getBasePosition())).getAmplifier(type);
		} else {
			component.setInMultiblock(false);
			component.setBasePosition(null);
		}

		return amplifier;
	}

	/**
	 * Checks if a BlockEntity is a PE Collector (extends IEnergyCollector)
	 * @param tile BlockEntity to check
	 * @return True if the BlockEntity is a PE Collector, otherwise false
	 */
	public static boolean isCollector(BlockEntity tile){
		if(tile != null) return tile instanceof IEnergyCollector;
		return false;
	}

	/**
	 * Checks if a BlockEntity is a PE Manipulator (extends IEnergyManipulator)
	 * @param tile BlockEntity to check
	 * @return True if the BlockEntity is a PE Manipulator, otherwise false
	 */
	public static boolean isManipulator(BlockEntity tile){
		if(tile != null) return tile instanceof IEnergyManipulator;
		return false;
	}

	/**
	 * Checks if a BlockEntity is a PE Container (extends IEnergyContainer)
	 * @param tile BlockEntity to check
	 * @return True if the BlockEntity is a PE Collector, otherwise false
	 */
	public static boolean isContainer(BlockEntity tile){
		if(tile != null) return tile instanceof IEnergyContainer;
		return false;
	}

	/**
	 * Checks that the BlockPos has no adjacent PE Collectors
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @return True if the BlockPos has no adjacent PE Collectors, otherwise false
	 */
	public static boolean checkForAdjacentCollectors(Level world, BlockPos pos){
		for(Direction face : Direction.values())
			if(isCollector(world.getBlockEntity(pos.relative(face))))
				return false;
		return true;
	}

	/**
	 * Checks that the BlockPos has no adjacent PE Manipulators
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @return True if the BlockPos has no adjacent PE Manipulators, otherwise false
	 */
	public static boolean checkForAdjacentManipulators(Level world, BlockPos pos){
		for(Direction face : Direction.values())
			if(isManipulator(world.getBlockEntity(pos.relative(face))))
				return false;
		if(isManipulator(world.getBlockEntity(pos.above(2)))) //Might remove this extra check
			return false;
		if(isManipulator(world.getBlockEntity(pos.below(2)))) // ^
			return false;
		return true;
	}

	/**
	 * Checks for anything accepting PE in the given direction
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param face Direction to check
	 * @param dist Transport distance
	 * @return True if a PE Container is found, otherwise false
	 */
	public static boolean canTransfer(Level world, BlockPos pos, Direction face, int dist){

		for(int i = 1; i < dist+1; i++){
			if(world.getBlockState(pos.relative(face, i)).isCollisionShapeFullBlock(world, pos.relative(face, i)) && !world.isEmptyBlock(pos.relative(face, i))) return false;
			if(isContainer(world.getBlockEntity(pos.relative(face, i))))
				return ((IEnergyContainer) world.getBlockEntity(pos.relative(face, i))).canAcceptPE();
		}
		return false;
	}

	/**
	 * Looks for a PE Collector in the given direction
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param face Direction to check
	 * @param dist Transport distance
	 * @return A PE Collector if one is found, otherwise null
	 */
	public static IEnergyCollector getCollector(Level world, BlockPos pos, Direction face, int dist){
		for(int i = 1; i < dist+1; i++)
			if(isCollector(world.getBlockEntity(pos.relative(face, i))))
				return (IEnergyCollector) world.getBlockEntity(pos.relative(face, i));
		return null;
	}

	/**
	 * Looks for a PE Container in the given distance
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param face Direction to check
	 * @param dist Transport distance
	 * @return A BlockEntity that can accept PE, otherwise null
	 */
	public static IEnergyContainer getContainer(Level world, BlockPos pos, Direction face, int dist){
		for(int i = 1; i < dist+1; i++)
			if(isContainer(world.getBlockEntity(pos.relative(face, i))))
				return (IEnergyContainer) world.getBlockEntity(pos.relative(face, i));
		return null;
	}

	/**
	 * Attempts to collect PE from nearby PE Collectors
	 * @param relay PE Relay
	 * @param world Current Level
	 * @param pos Current BlockPos
	 * @param face Direction to collect from
	 * @param amount Amount of PE to drain
	 */
	public static void collectNearbyPE(IEnergyTransporter relay, Level world, BlockPos pos, Direction face, float amount){
		if(relay.canAcceptPE()){
			IEnergyContainer container = getContainer(world, pos, face, 1);
			if(container != null && container.canTransferPE())
				relay.addEnergy(container.consumeEnergy(amount));

		}
	}

	/**
	 * Gets the current contained PE from a ItemStack<br>
	 * Unless you need special code, this can be called in {@link IEnergyContainerItem#getContainedEnergy(ItemStack)}
	 * @param stack ItemStack containing energy
	 * @return The contained energy, if any
	 */
	public static float getContainedEnergy(ItemStack stack){
		float energy;
		CompoundTag tag = stack.getOrCreateTag();
		if(tag.contains("PotEnergy"))
			energy = tag.getFloat("PotEnergy");
		else {
			energy = 0;
			tag.putFloat("PotEnergy", energy);
		}
		return energy;
	}

	/**
	 * Adds PE to the supplied ItemStack<br>
	 * Unless you need special code, this can be called in {@link IEnergyContainerItem#addEnergy(ItemStack, float)}
	 * @param container PE container Item instance
	 * @param stack ItemStack containing the Item
	 * @param energy Energy quanta to add
	 */
	public static void addEnergy(IEnergyContainerItem container, ItemStack stack, float energy){
		float contained = container.getContainedEnergy(stack);
		if(contained + energy >= container.getMaxEnergy(stack))
			stack.getOrCreateTag().putFloat("PotEnergy", container.getMaxEnergy(stack));
		else stack.getOrCreateTag().putFloat("PotEnergy", contained += energy);
	}

	/**
	 * Consumes PE from the supplied ItemStack<br>
	 * Unless you need special code, this can be called in {@link IEnergyContainerItem#consumeEnergy(ItemStack, float)}
	 * @param stack ItemStack containing energy
	 * @param energy Energy quanta to consume
	 * @return The amount of energy consumed
	 */
	public static float consumeEnergy(ItemStack stack, float energy){
		float contained = getContainedEnergy(stack);
		if(energy < contained){
			stack.getOrCreateTag().putFloat("PotEnergy", contained -= energy);
			return energy;
		} else {
			stack.getOrCreateTag().putFloat("PotEnergy", 0);
			return contained;
		}
	}
}
