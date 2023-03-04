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

import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.registry.sync.RegistriesAccessor;

/**
 * Used to create custom registries, with specified registry attributes.
 *
 * <p>See the following example for creating a {@link Registry} of String objects.
 *
 * <pre>
 * {@code
 *  RegistryKey<Registry<String>> registryKey = RegistryKey.ofRegistry(new Identifier("modid", "registry_name"));
 *  Registry<String> registry = FabricRegistryBuilder.createSimple(registryKey)
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
	 * @param registry The base registry type such as {@link net.minecraft.registry.SimpleRegistry} or {@link net.minecraft.registry.DefaultedRegistry}
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
	 * @param registryKey The registry {@link RegistryKey}
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, SimpleRegistry<T>> createSimple(RegistryKey<Registry<T>> registryKey) {
		return from(new SimpleRegistry<>(registryKey, Lifecycle.stable(), false));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registryKey The registry {@link RegistryKey}
	 * @param defaultId The default registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> FabricRegistryBuilder<T, SimpleDefaultedRegistry<T>> createDefaulted(RegistryKey<Registry<T>> registryKey, Identifier defaultId) {
		return from(new SimpleDefaultedRegistry<T>(defaultId.toString(), registryKey, Lifecycle.stable(), false));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link SimpleRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 * @deprecated Please migrate to {@link FabricRegistryBuilder#createSimple(RegistryKey)}
	 */
	@Deprecated
	public static <T> FabricRegistryBuilder<T, SimpleRegistry<T>> createSimple(Class<T> type, Identifier registryId) {
		return createSimple(RegistryKey.ofRegistry(registryId));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}, the registry has the {@link RegistryAttribute#MODDED} attribute by default.
	 *
	 * @param registryId The registry {@link Identifier} used as the registry id
	 * @param defaultId The default registry id
	 * @param <T> The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 * @deprecated Please migrate to {@link FabricRegistryBuilder#createDefaulted(RegistryKey, Identifier)}
	 */
	@Deprecated
	public static <T> FabricRegistryBuilder<T, SimpleDefaultedRegistry<T>> createDefaulted(Class<T> type, Identifier registryId, Identifier defaultId) {
		return createDefaulted(RegistryKey.ofRegistry(registryId), defaultId);
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
		final RegistryKey<?> key = registry.getKey();

		for (RegistryAttribute attribute : attributes) {
			RegistryAttributeHolder.get(key).addAttribute(attribute);
		}

		//noinspection unchecked
		RegistriesAccessor.getROOT().add((RegistryKey<MutableRegistry<?>>) key, registry, Lifecycle.stable());

		return registry;
	}
}
