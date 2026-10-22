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
 *     1.20.1 port - ticked by the server tick event, cleared when the server stops (processes don't outlive the server)
 ******************************************************************************/
package com.shinoow.abyssalcraft.lib.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Runs {@link ScheduledProcess}es on the server thread after their delay (used to spread large world edits over time)
 */
public class Scheduler {

	private static final List<ScheduledProcess> queue = new ArrayList<>();

	public static void schedule(ScheduledProcess process) {
		if(process.getTime() == 0)
			process.execute();
		else
			queue.add(process);
	}

	public static void tick() {
		for(int i = queue.size() - 1; i >= 0; i--) {
			ScheduledProcess process = queue.get(i);
			if(process.tick()) {
				queue.remove(i);
				process.execute();
			}
		}
	}

	/** Forge bus listener */
	public static class Events {

		@SubscribeEvent
		public void onServerTick(TickEvent.ServerTickEvent event) {
			if(event.phase == TickEvent.Phase.END)
				tick();
		}

		@SubscribeEvent
		public void onServerStopped(ServerStoppedEvent event) {
			queue.clear();
		}
	}
}
