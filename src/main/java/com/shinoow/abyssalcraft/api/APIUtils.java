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
package com.shinoow.abyssalcraft.api;

import java.util.*;
import java.util.stream.Collectors;

import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.api.item.ICrystal;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.INecroDataCapability;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.recipe.EngraverRecipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

/**
 * Utility methods for the API.<br>
 * Offerings and other recipe components ("Objects") can be: {@link ItemStack}, {@link Item}, {@link Block},
 * {@link Ingredient}, an item {@link TagKey} (replacement for OreDictionary names), an array or a List of ItemStacks.
 *
 * @author shinoow
 *
 */
public class APIUtils {

	/** If true, unlockable items always show their real names (config: displayItemNames) */
	public static boolean display_names;

	/**
	 * Checks if an ItemStack is a crystal
	 */
	public static boolean isCrystal(ItemStack item){
		if(item.getItem() instanceof ICrystal || item.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ICrystal)
			return true;
		return AbyssalCraftAPI.getCrystals().stream().anyMatch(crystal -> areStacksEqual(item, crystal));
	}

	/**
	 * Checks if an ItemStack is a coin
	 */
	public static boolean isCoin(ItemStack item){
		return EngraverRecipes.instance().getCoinList().stream().anyMatch(coin -> areStacksEqual(item, coin));
	}

	/**
	 * Converts an Object to an ItemStack, if possible (used for rendering/display)
	 */
	@SuppressWarnings("unchecked")
	public static ItemStack convertToStack(Object obj){
		if(obj == null)
			return ItemStack.EMPTY;
		else if(obj instanceof ItemStack)
			return ((ItemStack)obj).copy();
		else if(obj instanceof Item)
			return new ItemStack((Item)obj);
		else if(obj instanceof Block)
			return new ItemStack((Block)obj);
		else if(obj instanceof Ingredient)
			return ((Ingredient) obj).getItems().length > 0 ? ((Ingredient) obj).getItems()[0].copy() : ItemStack.EMPTY;
		else if(obj instanceof TagKey)
			return AbyssalCraftAPI.firstOf((TagKey<Item>) obj, 1);
		else if(obj instanceof ItemStack[])
			return ((ItemStack[])obj).length > 0 ? ((ItemStack[])obj)[0].copy() : ItemStack.EMPTY;
		else if(obj instanceof List)
			return ((List<?>) obj).isEmpty() ? ItemStack.EMPTY : ((ItemStack)((List<?>) obj).get(0)).copy();
		else throw new ClassCastException("Not a Item, Block, ItemStack, Ingredient, Item Tag, Array of ItemStacks or List of ItemStacks!");
	}

	/**
	 * Converts an Object (see class description) to an Ingredient
	 */
	@SuppressWarnings("unchecked")
	public static Ingredient convertToIngredient(Object obj){
		if(obj == null)
			return Ingredient.EMPTY;
		else if(obj instanceof Ingredient)
			return (Ingredient) obj;
		else if(obj instanceof ItemStack)
			return Ingredient.of((ItemStack)obj);
		else if(obj instanceof Item)
			return Ingredient.of((Item)obj);
		else if(obj instanceof Block)
			return Ingredient.of((Block)obj);
		else if(obj instanceof TagKey)
			return Ingredient.of((TagKey<Item>) obj);
		else if(obj instanceof ItemStack[])
			return Ingredient.of((ItemStack[])obj);
		else if(obj instanceof List)
			return Ingredient.of(((List<ItemStack>) obj).toArray(new ItemStack[0]));
		else throw new ClassCastException("Not a Item, Block, ItemStack, Ingredient, Item Tag, Array of ItemStacks or List of ItemStacks!");
	}

	/**
	 * Checks if every non-empty stack in the second array matches exactly one entry in the first array
	 * @param array1 Recipe components
	 * @param array2 ItemStacks to compare
	 * @param nbt Whether or not to compare NBT (only the NBT present on the recipe component is compared)
	 */
	public static boolean areItemStackArraysEqual(Object[] array1, ItemStack[] array2, boolean nbt){
		List<Object> compareList = nonNullList(array1);
		List<ItemStack> itemList = Arrays.stream(array2).filter(s -> !s.isEmpty()).collect(Collectors.toList());

		if(itemList.size() == compareList.size())
			for(ItemStack stack : itemList)
				for(Object compare : compareList)
					if(areObjectsEqual(stack, compare, nbt)){
						compareList.remove(compare);
						break;
					}
		return compareList.isEmpty();
	}

	private static List<Object> nonNullList(Object[] array){
		return Arrays.stream(array).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static Object[] makeArrayOf(Object stack, int size) {
		Object[] array = new Object[size];
		for(int i = 0; i < size; i++)
			array[i] = stack;
		return array;
	}

	/**
	 * Compares an ItemStack to an Object (see class description)
	 */
	@SuppressWarnings("unchecked")
	public static boolean areObjectsEqual(ItemStack stack, Object obj, boolean nbt){
		if(obj instanceof ItemStack)
			return areStacksEqual(stack, (ItemStack)obj, nbt);
		else if(obj instanceof Item)
			return areStacksEqual(stack, new ItemStack((Item)obj), nbt);
		else if(obj instanceof Block)
			return areStacksEqual(stack, new ItemStack((Block)obj), nbt);
		else if(obj instanceof Ingredient)
			return !stack.isEmpty() && ((Ingredient) obj).test(stack);
		else if(obj instanceof TagKey)
			return !stack.isEmpty() && stack.is((TagKey<Item>) obj);
		else if(obj instanceof ItemStack[]){
			for(ItemStack item : (ItemStack[])obj)
				if(areStacksEqual(stack, item, nbt))
					return true;
		} else if(obj instanceof List)
			for(ItemStack item :(List<ItemStack>)obj)
				if(areStacksEqual(stack, item, nbt))
					return true;
		return false;
	}

	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2, boolean nbt){
		if(stack1.isEmpty() || stack2.isEmpty()) return false;
		return nbt ? areStacksEqual(stack1, stack2) && areItemStackTagsEqual(stack2, stack1, 0) :
			areStacksEqual(stack1, stack2);
	}

	/**
	 * Compares two stacks by item (metadata no longer exists, damage is ignored like the old wildcard value)
	 */
	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.isEmpty() || stack2.isEmpty()) return false;
		return stack1.getItem() == stack2.getItem();
	}

	/**
	 * Checks if an item is locked (shown in Aklo) for the player
	 */
	public static boolean isLocked(ItemStack stack, Player player){
		if(!(stack.getItem() instanceof IUnlockableItem) || display_names || player == null) return false;
		INecroDataCapability cap = NecroDataCapability.getCap(player);
		return cap != null && !cap.isUnlocked(((IUnlockableItem) stack.getItem()).getUnlockCondition(stack), player);
	}

	/**
	 * Compares the NBT of two stacks
	 * @param stackA First stack (its NBT has to be contained within the second stack's NBT for strictness 0)
	 * @param stackB Second stack
	 * @param strictness 0 = A's tag is contained in B's, 1 = A has no tag or both are equal, 2 = exact match
	 */
	public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB, int strictness)
	{
		switch(strictness) {
		case 0:
			if (stackA.isEmpty() && stackB.isEmpty())
				return true;
			else if (!stackA.isEmpty() && !stackB.isEmpty())
			{
				if(stackA.getTag() == null)
					return true;
				else if(stackB.getTag() == null)
					return false;
				return compoundContainsCompound(stackA.getTag().copy(), stackB.getTag().copy());
			} else
				return false;
		case 1:
			if (stackA.isEmpty() && stackB.isEmpty())
				return true;
			else if (!stackA.isEmpty() && !stackB.isEmpty())
			{
				if (stackA.getTag() == null && stackB.getTag() != null)
					return false;
				else
					return stackA.getTag() == null || stackA.getTag().equals(stackB.getTag());
			} else
				return false;
		case 2:
			return ItemStack.isSameItemSameTags(stackA, stackB);
		default:
			return false;
		}
	}

	private static boolean compoundContainsCompound(CompoundTag tag, CompoundTag tag1) {
		for (String s : tag.getAllKeys())
		{
			Tag nbtbase = tag.get(s);

			if (nbtbase.getId() == Tag.TAG_COMPOUND)
			{
				if (tag1.contains(s, Tag.TAG_COMPOUND))
				{
					CompoundTag nbttagcompound = tag1.getCompound(s);
					if(!compoundContainsCompound((CompoundTag)nbtbase, nbttagcompound))
						return false;
				} else
					return false;
			} else if(tag1.contains(s))
				if(!tag1.get(s).equals(nbtbase))
					return false;
		}
		return true;
	}

	/**
	 * Checks if the stack is an Item with an inventory (shouldn't be placed inside another such item)
	 */
	public static boolean hasAnInventory(ItemStack stack) {
		Item item = stack.getItem();
		return item == ACItems.configurator.orElse(null)
				|| item instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock
				|| item == ACItems.small_crystal_bag.orElse(null)
				|| item == ACItems.medium_crystal_bag.orElse(null)
				|| item == ACItems.large_crystal_bag.orElse(null)
				|| item == ACItems.huge_crystal_bag.orElse(null)
				|| item == ACItems.stone_tablet.orElse(null);
	}
}
