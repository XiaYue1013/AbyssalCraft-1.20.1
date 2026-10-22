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
package com.shinoow.abyssalcraft.api.necronomicon.condition;

import net.minecraft.world.level.Level;

import net.minecraft.resources.ResourceKey;




public class DimensionCondition implements IUnlockCondition {

	String id;

	public DimensionCondition(ResourceKey<Level> id){
		this.id = id.location().toString();
	}

	public DimensionCondition(String id){
		this.id = id;
	}

	@Override
	public boolean areConditionObjectsEqual(Object stuff) {

		return id.equals(stuff);
	}

	@Override
	public Object getConditionObject() {

		return id;
	}

	@Override
	public int getType() {

		return 2;
	}

}
