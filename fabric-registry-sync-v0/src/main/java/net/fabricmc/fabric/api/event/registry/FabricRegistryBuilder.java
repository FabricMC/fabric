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

import java.util.EnumSet;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.fabricmc.fabric.mixin.registry.sync.AccessorRegistry;

/**
 * Used to create custom registries, with specified registry attributes.
 *
 * <pre>
 * {@code
 *  SimpleRegistry<String> registry = FabricRegistryBuilder.createSimple(String.class, new Identifier("registry_sync", "fabric_registry"))
 * 													.attribute(RegistryAttribute.SYNCED)
 * 													.buildAndRegister();
 * 	}
 * </pre>
 *
 * @param <T> The type stored in the Registry
 * @param <R> The registry type
 */
public final class FabricRegistryBuilder<T, R extends MutableRegistry<T>> {
	/**
	 * Create a new {@link FabricRegistryBuilder}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registry The base registry type such as {@link net.minecraft.util.registry.SimpleRegistry} or {@link net.minecraft.util.registry.DefaultedRegistry}
	 * @param <T> The type stored in the Registry
	 * @param <R> The registry type
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T, R extends MutableRegistry<T>> FabricRegistryBuilder<T, R> from(R registry) {
		return new FabricRegistryBuilder<>(registry);
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link SimpleRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, SimpleRegistry<T>> createSimple(Class<T> type, Identifier registryId) {
		return from(new SimpleRegistry<T>(RegistryKey.ofRegistry(registryId), Lifecycle.stable()));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param defaultId The default registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, DefaultedRegistry<T>> createDefaulted(Class<T> type, Identifier registryId, Identifier defaultId) {
		return from(new DefaultedRegistry<T>(defaultId.toString(), RegistryKey.ofRegistry(registryId), Lifecycle.stable()));
	}

	private final R registry;
	private final EnumSet<RegistryAttribute> attributes = EnumSet.noneOf(RegistryAttribute.class);

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
	 * Applies the attributes to the registry and registers it.
	 * @return the registry instance with the attributes applied
	 */
	public R buildAndRegister() {
		FabricRegistry fabricRegistry = (FabricRegistry) registry;
		fabricRegistry.build(attributes);

		//noinspection unchecked
		AccessorRegistry.getROOT().add(((AccessorRegistry) registry).getRegistryKey(), registry);

		return registry;
	}
}
