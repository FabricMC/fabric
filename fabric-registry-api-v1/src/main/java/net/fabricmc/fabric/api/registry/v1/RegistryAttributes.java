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

import java.util.Set;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.RegistryExtensions;

/**
 * An enumeration of all built-in registry attributes.
 */
public final class RegistryAttributes {
	/**
	 * A registry attribute that states that a registry is modded.
	 */
	public static final Identifier MODDED = new Identifier("fabric-registry-api-v1", "modded");

	/**
	 * Adds an attribute to this registry.
	 *
	 * @param id the attribute id
	 */
	public static <T> void addAttribute(Registry<T> registry, Identifier id) {
		RegistryExtensions.get(registry).addAttribute(id);
	}

	/**
	 * Gets a set of all attributes this registry has.
	 *
	 * @return a set of attributes.
	 */
	public static <T> Set<Identifier> getAttributes(Registry<T> registry) {
		return RegistryExtensions.get(registry).getAttributes();
	}

	private RegistryAttributes() {
	}
}
