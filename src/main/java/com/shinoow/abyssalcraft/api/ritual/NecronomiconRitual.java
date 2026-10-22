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
package com.shinoow.abyssalcraft.api.ritual;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.ISingletonInventory;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DefaultCondition;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Base Necronomicon Ritual.<br>
 * Offerings are "Objects" as described in {@link com.shinoow.abyssalcraft.api.APIUtils}.
 * @author shinoow
 *
 * @since 1.4
 */
public abstract class NecronomiconRitual {

	private Object[] offerings = new Object[8];
	protected Object sacrifice;
	private String unlocalizedName;
	private int bookType;
	@Nullable
	private ResourceKey<Level> dimension;
	private float requiredEnergy;
	private boolean requiresSacrifice, nbtSensitive, nbtSensitiveSacrifice;
	private IUnlockCondition condition = new DefaultCondition();
	private EnumRitualParticle particle = EnumRitualParticle.ITEM;

	/**
	 * A Necronomicon Ritual
	 * @param unlocalizedName A string representing the ritual name
	 * @param bookType Necronomicon book type required
	 * @param dimension Dimension where the ritual can be performed (null = any dimension where rituals are allowed)
	 * @param requiredEnergy Amount of Potential Energy required to perform
	 * @param requiresSacrifice If the ritual requires a living sacrifice
	 * @param offerings Components used to perform the ritual, are consumed afterwards
	 */
	public NecronomiconRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, boolean requiresSacrifice, Object...offerings){
		this.unlocalizedName = unlocalizedName;
		this.bookType = bookType;
		this.dimension = dimension;
		this.requiredEnergy = requiredEnergy;
		this.requiresSacrifice = requiresSacrifice;
		if(offerings.length < 8){
			this.offerings = new Object[offerings.length];
			for(int i = 0; i < offerings.length; i++)
				this.offerings[i] = offerings[i];
		}
		else this.offerings = offerings;
	}

	public NecronomiconRitual(String unlocalizedName, int bookType, @Nullable ResourceKey<Level> dimension, float requiredEnergy, Object...offerings){
		this(unlocalizedName, bookType, dimension, requiredEnergy, false, offerings);
	}

	public NecronomiconRitual(String unlocalizedName, int bookType, float requiredEnergy, Object...offerings){
		this(unlocalizedName, bookType, null, requiredEnergy, offerings);
	}

	/**
	 * Marks the ritual's offerings as NBT sensitive
	 */
	public NecronomiconRitual setNBTSensitive(){
		nbtSensitive = true;
		return this;
	}

	/**
	 * Marks the ritual's sacrifice (item placed on the altar) as NBT sensitive
	 */
	public NecronomiconRitual setNBTSensitiveSacrifice(){
		nbtSensitiveSacrifice = true;
		return this;
	}

	public NecronomiconRitual setUnlockCondition(IUnlockCondition condition) {
		this.condition = condition;
		return this;
	}

	public NecronomiconRitual setRitualParticle(EnumRitualParticle particle) {
		this.particle = particle;
		return this;
	}

	public Object[] getOfferings(){
		return offerings;
	}

	public boolean requiresSacrifice(){
		return requiresSacrifice;
	}

	public int getBookType(){
		return bookType;
	}

	/**
	 * The dimension the ritual is restricted to, or null if it can be performed anywhere rituals are allowed
	 */
	@Nullable
	public ResourceKey<Level> getDimension(){
		return dimension;
	}

	public float getReqEnergy(){
		return requiredEnergy;
	}

	public String getUnlocalizedName(){
		return "ac.ritual." + unlocalizedName;
	}

	/**
	 * Ritual id without the "ac.ritual." prefix
	 */
	public String getId(){
		return unlocalizedName;
	}

	public String getLocalizedName(){
		return Component.translatable(getUnlocalizedName()).getString();
	}

	public String getDescription(){
		return Component.translatable(getUnlocalizedName() + ".desc").getString();
	}

	/**
	 * Whether or not the item placed on the altar is the target of the ritual (rather than an offering)
	 */
	public boolean requiresItemSacrifice(){
		return false;
	}

	public boolean isNBTSensitive(){
		return nbtSensitive;
	}

	public boolean isSacrificeNBTSensitive(){
		return nbtSensitiveSacrifice;
	}

	public IUnlockCondition getUnlockCondition() {
		return condition;
	}

	public EnumRitualParticle getRitualParticle() {
		return particle;
	}

	public Object getSacrifice(){
		return sacrifice;
	}

	/**
	 * Fetches the item placed on the altar at the position
	 */
	protected static ItemStack getAltarItem(Level world, BlockPos pos) {
		BlockEntity altar = world.getBlockEntity(pos);
		return altar instanceof ISingletonInventory ? ((ISingletonInventory) altar).getItem() : ItemStack.EMPTY;
	}

	/**
	 * Replaces the item placed on the altar at the position
	 */
	protected static void setAltarItem(Level world, BlockPos pos, ItemStack stack) {
		BlockEntity altar = world.getBlockEntity(pos);
		if(altar instanceof ISingletonInventory)
			((ISingletonInventory) altar).setItem(stack);
	}

	/**
	 * Additional checks that has to be done before the ritual can be performed
	 */
	public abstract boolean canCompleteRitual(Level world, BlockPos pos, Player player);

	/**
	 * Called when the ritual is completed (on both sides, the server notifies the client through the internal handler)
	 */
	public void completeRitual(Level world, BlockPos pos, Player player){
		if(!world.isClientSide) {
			completeRitualServer(world, pos, player);
			AbyssalCraftAPI.getInternalMethodHandler().completeRitualClient(pos, player, unlocalizedName);
		}
		if(world.isClientSide) completeRitualClient(world, pos, player);
	}

	protected abstract void completeRitualClient(Level world, BlockPos pos, Player player);

	protected abstract void completeRitualServer(Level world, BlockPos pos, Player player);
}
