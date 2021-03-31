/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.fluid.v1;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.state.property.IntProperty;

public class FluidProperties {
	private static final Int2ObjectMap<IntProperty> PROPERTY_CACHE = new Int2ObjectArrayMap<>();

	/**
	 * State index 0 represents a still fluid, and all other indexes represent a flowing fluid with the same level as the index.
	 * The last index is the equivalent of a falling fluid.
	 *
	 * @param maxLevel The max level of the fluid.
	 * @return The property used for BlockStates and FluidStates for fluids with this max level.
	 */
	public static IntProperty getStateIndexProperty(int maxLevel) {
		if (maxLevel < 1) {
			throw new IllegalArgumentException("maxLevel cannot be less than 1");
		}

		IntProperty property = PROPERTY_CACHE.get(maxLevel);

		if (property == null) {
			property = IntProperty.of("state_index", 0, maxLevel);
			PROPERTY_CACHE.put(maxLevel, property);
		}

		return property;
	}

	private FluidProperties() { }
}
