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

package net.fabricmc.fabric.api.item.v1;

import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.impl.item.ShieldRegistryImpl;

/**
 * Registry for defining an item as a shield.
 * Shields should also override several {@code use} methods to work properly (see {@link net.minecraft.item.ShieldItem} for reference).
 */
public interface ShieldRegistry {
	/**
	 * @param item the item to define as shield
	 */
	default void add(ItemConvertible item) {
		add(item, 100);
	}

	/**
	 * @param item the item to define as shield
	 * @param axeDisableDuration how long the cooldown lasts when the shield gets hit by an axe (0 to disable)
	 */
	static void add(ItemConvertible item, int axeDisableDuration) {
		ShieldRegistryImpl.add(item, axeDisableDuration);
	}

	/**
	 * @param item the item to get from the registry
	 * @return whether or not the item is registered as a shield
	 */
	static boolean isShield(ItemConvertible item) {
		return ShieldRegistryImpl.isShield(item);
	}

	/**
	 * @param item the item to get from the registry
	 * @return the axe cooldown duration for the shield or null if it is not registered
	 */
	static int getAxeDisableDuration(ItemConvertible item) {
		return ShieldRegistryImpl.getAxeDisableDuration(item);
	}
}
