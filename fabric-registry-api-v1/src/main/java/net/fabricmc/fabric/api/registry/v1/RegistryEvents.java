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

package net.fabricmc.fabric.api.registry.v1;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.registry.RegistryExtensions;

/**
 * Holds all registry events.
 * The event instances are available via a {@link RegistryExtensions registry's extensions} using {@link RegistryExtensions#get(Registry)}.
 */
public final class RegistryEvents {
	/**
	 * Gets the event that is called when an entry is added to this registry.
	 *
	 * @return the entry added event
	 */
	public static <T> Event<RegistryEvents.EntryAdded<T>> getEntryAddedEvent(Registry<T> registry) {
		return RegistryExtensions.get(registry).getEntryAddedEvent();
	}

	/**
	 * Gets the event that is called when an entry is removed from a registry.
	 *
	 * @return the entry removed event
	 */
	public static <T> Event<RegistryEvents.EntryRemoved<T>> getEntryRemovedEvent(Registry<T> registry) {
		return RegistryExtensions.get(registry).getEntryRemovedEvent();
	}

	@FunctionalInterface
	public interface EntryAdded<T> {
		/**
		 * Called when an entry is added to a registry.
		 *
		 * @param rawId the raw id of the registry entry
		 * @param id the id of the registry entry
		 * @param object the registry entry
		 */
		void onEntryAdded(int rawId, Identifier id, T object);
	}

	@FunctionalInterface
	public interface EntryRemoved<T> {
		/**
		 * Called when an entry is removed from a registry.
		 *
		 * @param rawId the raw id of the registry entry
		 * @param id the id of the registry entry
		 * @param object the registry entry
		 */
		void onEntryRemoved(int rawId, Identifier id, T object);
	}

	private RegistryEvents() {
	}
}
