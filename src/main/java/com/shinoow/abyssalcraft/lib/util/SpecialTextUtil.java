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
 *     1.20.1 port - the client side messages go through ClientPacketHandler (only loaded on clients)
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib.util;

import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.client.ClientPacketHandler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpecialTextUtil {

	/**
	 * Send a message as Asorah, The Fallen
	 * <br><b>(This should be called client-side only)</b>
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void AsorahText(String...text){
		customText("Asorah", text);
	}

	/**
	 * Send a message as Cha'garoth, The Dreadbeast
	 * <br><b>(This should be called client-side only)</b>
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void ChagarothText(String...text){
		customText("Cha'garoth", text);
	}

	/**
	 * Send a message as J'zahar, Gatekeeper of The Abyss
	 * <br><b>(This should be called client-side only)</b>
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void JzaharText(String...text){
		customText("J'zahar", text);
	}

	/**
	 * Send a message as Oblivionaire
	 * <br><b>(This should be called client-side only)</b>
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void OblivionaireText(String...text){
		customText("Oblivionaire", text);
	}

	/**
	 * Send a message as Sacthoth, Harbinger of Doom
	 * <br><b>(This should be called client-side only)</b>
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void SacthothText(String...text){
		customText("Sacthoth", text);
	}

	/**
	 * Send a message to a group as Asorah, The Fallen
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void AsorahGroup(Level world, String...text){
		customGroup(world, "Asorah", text);
	}

	/**
	 * Send a message to a group as Cha'garoth, The Dreadbeast
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void ChagarothGroup(Level world, String...text){
		customGroup(world, "Cha'garoth", text);
	}

	/**
	 * Send a message to a group as J'zahar, Gatekeeper of The Abyss
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void JzaharGroup(Level world, String...text){
		customGroup(world, "J'zahar", text);
	}

	/**
	 * Send a message to a group as Oblivionaire
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void OblivionaireGroup(Level world, String...text){
		customGroup(world, "Oblivionaire", text);
	}

	/**
	 * Send a message to a group as Sacthoth, Harbinger of Doom
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void SacthothGroup(Level world, String...text){
		customGroup(world, "Sacthoth", text);
	}

	/**
	 * Send a message as someone
	 * <br><b>(This should be called client-side only)</b>
	 * @param name A name
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void customText(String name, String...text){
		ClientPacketHandler.customText(name, text);
	}

	/**
	 * Send a message to every player in the world as someone
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param name A name
	 * @param text A message (or two, with the second being in aklo)
	 */
	public static void customGroup(Level world, String name, String...text){
		for(Player player : world.players())
			player.sendSystemMessage(Component.literal(name + ": " + pick(player, text)));
	}

	/**
	 * Send a translated message to every player in the world as someone
	 * <br><b>(This should be called server-side only)</b>
	 * @param world Current world
	 * @param name A name
	 * @param key Translation key of the message
	 */
	public static void translatedGroup(Level world, String name, String key, Object...args){
		for(Player player : world.players())
			player.sendSystemMessage(Component.literal(name + ": ").append(Component.translatable(key, args)));
	}

	/** The plain text for players carrying a Necronomicon, otherwise the Aklo one (if there is one) */
	public static String pick(Player player, String...text) {
		return text.length == 1 ? text[0] : text[EntityUtil.hasNecronomicon(player) ? 0 : 1];
	}
}
