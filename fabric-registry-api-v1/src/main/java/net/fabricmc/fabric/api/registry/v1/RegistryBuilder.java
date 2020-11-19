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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.mixin.registry.RegistryAccessor;

/**
 * Used to create custom registries, with specified registry attributes.
 *
 * <pre>{@code
 *  SimpleRegistry<String> registry = FabricRegistryBuilder.createSimple(new Identifier("registry_sync", "fabric_registry"))
 * 		.attribute(RegistryAttributes.MODDED)
 * 		.buildAndRegister();
 * }</pre>
 *
 * @param <T> The type stored in the registry
 * @param <R> The registry type
 */
public final class RegistryBuilder<T, R extends MutableRegistry<T>> {
	private final R registry;
	private final Set<Identifier> attributes = new HashSet<>();

	/**
	 * Create a new {@link RegistryBuilder}, the registry has the {@link RegistryAttributes#MODDED} attribute by default.
	 *
	 * @param registry The base registry type such as {@link net.minecraft.util.registry.SimpleRegistry} or {@link net.minecraft.util.registry.DefaultedRegistry}
	 * @param <T> The type stored in the Registry
	 * @param <R> The registry type
	 * @return An instance of RegistryBuilder
	 */
	public static <T, R extends MutableRegistry<T>> RegistryBuilder<T, R> from(R registry) {
		Objects.requireNonNull(registry, "Registry cannot be null");
		return new RegistryBuilder<>(registry);
	}

	/**
	 * Create a new {@link RegistryBuilder} using a {@link SimpleRegistry}, the registry has the {@link RegistryAttributes#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of RegistryBuilder
	 */
	public static <T, R extends MutableRegistry<T>> RegistryBuilder<T, R> createSimple(Identifier registryId) {
		Objects.requireNonNull(registryId, "Registry id cannot be null");
		//noinspection unchecked
		return from((R) new SimpleRegistry<>(RegistryKey.ofRegistry(registryId), Lifecycle.stable()));
	}

	/**
	 * Create a new {@link RegistryBuilder} using a {@link DefaultedRegistry}, the registry has the {@link RegistryAttributes#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param defaultId The default registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of RegistryBuilder
	 */
	public static <T, R extends MutableRegistry<T>> RegistryBuilder<T, R> createDefaulted(Identifier registryId, Identifier defaultId) {
		Objects.requireNonNull(registryId, "Registry id cannot be null");
		Objects.requireNonNull(defaultId, "Default id cannot be null");
		//noinspection unchecked
		return from((R) new DefaultedRegistry<>(defaultId.toString(), RegistryKey.ofRegistry(registryId), Lifecycle.stable()));
	}

	private RegistryBuilder(R registry) {
		this.registry = registry;
	}

	/**
	 * Adds an attribute to the registry.
	 *
	 * @param attribute the attribute to add to the registry
	 * @return the instance of {@link RegistryBuilder}
	 */
	public RegistryBuilder<T, R> attribute(Identifier attribute) {
		Objects.requireNonNull(attribute, "Attribute cannot be null");
		this.attributes.add(attribute);
		return this;
	}

	/**
	 * Applies the attributes to the registry and registers it.
	 * @return the registry instance with the attributes applied
	 */
	public R buildAndRegister() {
		for (Identifier attribute : this.attributes) {
			RegistryAttributes.addAttribute(this.registry, attribute);
		}

		//noinspection unchecked,rawtypes
		RegistryAccessor.getRootRegistry().add((RegistryKey) this.registry.getKey(), this.registry, Lifecycle.stable());

		return this.registry;
	}
}
