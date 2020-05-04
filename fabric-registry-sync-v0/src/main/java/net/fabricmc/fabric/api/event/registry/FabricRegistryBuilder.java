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

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;

public class FabricRegistryBuilder<T> {
	public static <T> FabricRegistryBuilder<T> create(Registry<T> registry) {
		return new FabricRegistryBuilder<>(registry);
	}

	private final Registry<T> registry;
	private final Set<RegistryAttribute> attributes = new HashSet<>();

	private FabricRegistryBuilder(Registry<T> registry) {
		this.registry = registry;
		addAttribute(RegistryAttribute.MODDED);
	}

	public FabricRegistryBuilder<T> addAttribute(RegistryAttribute attribute) {
		attributes.add(attribute);
		return this;
	}

	public Registry<T> build() {
		FabricRegistry fabricRegistry = (FabricRegistry) registry;
		fabricRegistry.build(attributes);
		return registry;
	}
}
