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
package com.shinoow.abyssalcraft.lib.util;

/**
 * Something to execute after a number of server ticks
 */
public abstract class ScheduledProcess {

	private int time;

	/**
	 * @param time Delay in ticks (0 = right away)
	 */
	public ScheduledProcess(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	/**
	 * Counts down one tick
	 * @return True when the process should be executed
	 */
	public boolean tick() {
		return --time <= 0;
	}

	public abstract void execute();

	/** Convenience factory */
	public static ScheduledProcess of(int time, Runnable task) {
		return new ScheduledProcess(time) {
			@Override
			public void execute() {
				task.run();
			}
		};
	}
}
