/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api;

import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.fabricmc.fabric.impl.item.CompostingChanceRegistryImpl;
import net.fabricmc.fabric.impl.item.FuelRegistryImpl;

/**
 * Proxy class for Minecraft's "global" property maps.
 */
public final class GlobalPropertyMaps {
	/**
	 * A map of items to fuel burning time, in in-game ticks.
	 */
	public static final Item2ObjectMap<Integer> FUEL = FuelRegistryImpl.INSTANCE;

	/**
	 * A map of items to an 0.0 - 1.0 chance of increasing the composter
	 * block's level.
	 */
	public static final Item2ObjectMap<Float> COMPOSTING_CHANCE = CompostingChanceRegistryImpl.INSTANCE;

	private GlobalPropertyMaps() {

	}
}
