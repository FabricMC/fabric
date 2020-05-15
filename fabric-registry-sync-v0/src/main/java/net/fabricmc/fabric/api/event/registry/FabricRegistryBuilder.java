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

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;

/**
 * Used to create custom registry's, with specified registry attributes.
 *
 * <pre>
 * {@code
 *  MutableRegistry<String> exampleRegistry = FabricRegistryBuilder.create(new SimpleRegistry<String>())
 * 												.attribute(RegistryAttribute.SYNCED)
 * 												.build();
 * 	Registry.REGISTRIES.add(new Identifier("mod_id", "example_registry"), exampleRegistry);
 * 	}
 * </pre>
 *
 * @param <T> The type stored in the Registry
 * @param <R> The registry type
 */
public class FabricRegistryBuilder<T, R extends Registry<T>> {
	/**
	 * Create a new {@link FabricRegistryBuilder}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registry The base registry type such as {@link net.minecraft.util.registry.SimpleRegistry} or {@link net.minecraft.util.registry.DefaultedRegistry}
	 * @param <T> The type stored in the Registry
	 * @param <R> The registry type
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T, R extends Registry<T>> FabricRegistryBuilder<T, R> from(R registry) {
		return new FabricRegistryBuilder<>(registry);
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link SimpleRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param type A class matching the type being stored in the registry
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, SimpleRegistry<T>> createSimple(Class<T> type) {
		return from(new SimpleRegistry<>());
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param type A class matching the type being stored in the registry
	 * @param defaultId The default registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, DefaultedRegistry<T>> createDefaulted(Class<T> type, Identifier defaultId) {
		return from(new DefaultedRegistry<>(defaultId.toString()));
	}

	private final R registry;
	private final Set<RegistryAttribute> attributes = new HashSet<>();

	private FabricRegistryBuilder(R registry) {
		this.registry = registry;
		attribute(RegistryAttribute.MODDED);
	}

	/**
	 * Add a {@link RegistryAttribute} to the registry.
	 *
	 * @param attribute the {@link RegistryAttribute} to add to the registry
	 * @return the instance of {@link FabricRegistryBuilder}
	 */
	public FabricRegistryBuilder<T, R> attribute(RegistryAttribute attribute) {
		attributes.add(attribute);
		return this;
	}

	/**
	 * Applies the attributes to the registry.
	 * @return the registry instance with the attributes applied
	 */
	public R build() {
		FabricRegistry fabricRegistry = (FabricRegistry) registry;
		fabricRegistry.build(attributes);
		return registry;
	}
}
