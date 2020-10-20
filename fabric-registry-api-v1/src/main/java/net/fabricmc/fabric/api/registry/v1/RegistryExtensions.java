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

import java.util.Objects;
import java.util.Set;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.Event;

/**
 * Extensions for a registry.
 * All registries have an extensions instance that can be obtained using {@link RegistryExtensions#get(Registry)}.
 *
 * <p>With a registry extensions instance, you may attach custom attributes to a registry.
 * There are also events which are called when entries are added to or removed from a registry.
 *
 * @param <T> the type of object stored in the registry
 * @see RegistryAttributes
 * @see RegistryEvents
 */
public interface RegistryExtensions<T> {
	/**
	 * Gets the registry extensions for a registry,
	 *
	 * @param registry the registry
	 * @param <T> the type of object stored in the registry
	 * @return the registry extensions
	 */
	static <T> RegistryExtensions<T> get(Registry<T> registry) {
		Objects.requireNonNull(registry, "Registry cannot be null");

		//noinspection unchecked
		return (RegistryExtensions<T>) registry;
	}

	/**
	 * Gets the event that is called when an entry is added to this registry.
	 *
	 * @return the entry added event
	 */
	Event<RegistryEvents.EntryAdded<T>> getEntryAddedEvent();

	/**
	 * Gets the event that is called when an entry is removed from a registry.
	 *
	 * @return the entry removed event
	 */
	Event<RegistryEvents.EntryRemoved<T>> getEntryRemovedEvent();

	/**
	 * Adds an attribute to this registry.
	 *
	 * @param id the attribute id
	 */
	void addAttribute(Identifier id);

	/**
	 * Gets a set of all attributes this registry has.
	 *
	 * @return a set of attributes.
	 */
	Set<Identifier> getAttributes();

	/**
	 * Checks if an attribute is present in this registry.
	 *
	 * @param id the attribute id
	 * @return true if the attribute is present in this registry, otherwise false.
	 */
	boolean hasAttribute(Identifier id);
}
