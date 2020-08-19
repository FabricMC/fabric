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

package net.fabricmc.fabric.api.event.registry;

import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistryEvents;

@FunctionalInterface
public interface DynamicRegistryEntryAddedCallback {
	void onEntryAdded(int rawId, RegistryKey<?> key, Object object, MutableRegistry<?> registry);

	static Event<DynamicRegistryEntryAddedCallback> event(RegistryKey<? extends Registry<?>> registryKey) {
		if (!DynamicRegistryEvents.ADD_ENTRY_EVENTS.containsKey(registryKey)) {
			throw new IllegalArgumentException("Unsupported registry: " + registryKey);
		}

		return DynamicRegistryEvents.ADD_ENTRY_EVENTS.get(registryKey);
	}
}
