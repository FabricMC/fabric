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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.registry.sync.RegistryLoaderAccessor;

/**
 * @apiNote The path used for {@code key} is used as path for resource search as well,
 * so {@code "fabric-api:dynamic_data"} would be searched in {@code "resources/data/modid/dynamic_data/"},
 * {@code modid} being mod id of any mod, it is recommended to use your mod id in
 * registry path to avoid path clashes, like {@code "fabric-api:fabric-api/dynamic_data"}.
 */
public class DynamicRegistryRegistry {
	/**
	 * Registers a dynamically loaded registry.
	 *
	 * @param key Identifier for the registry
	 * @param codec The codec used for deserialization
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> RegistryLoader.Entry<T> register(Identifier key, Codec<T> codec) {
		Objects.requireNonNull(key, "Identifier cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		return register(new RegistryLoader.Entry<>(RegistryKey.ofRegistry(key), codec));
	}

	/**
	 * Registers a dynamically loaded registry.
	 *
	 * @param key Registry key for the registry
	 * @param codec The codec used for deserialization
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> RegistryLoader.Entry<T> register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(key, "Registry key cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		return register(new RegistryLoader.Entry<>(key, codec));
	}

	/**
	 * Registers a dynamically loaded registry.
	 *
	 * @param entry The entry
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> RegistryLoader.Entry<T> register(RegistryLoader.Entry<T> entry) {
		Objects.requireNonNull(entry, "Entry cannot be null!");
		String path = entry.key().getValue().getPath();
		RegistryLoader.DYNAMIC_REGISTRIES.stream().filter(e -> e.key().getValue().getPath().equals(path)).findFirst().ifPresent(e -> {
			throw new IllegalStateException("Dynamic registry path clash between " + e + " and " + entry);
		});

		DynamicRegistryRegistry.MUTABLE_REGISTRIES.add(entry);
		RegistryLoaderAccessor.setDynamicRegistries(Collections.unmodifiableList(DynamicRegistryRegistry.MUTABLE_REGISTRIES));

		return entry;
	}

	private static final List<RegistryLoader.Entry<?>> MUTABLE_REGISTRIES = new ArrayList<>(RegistryLoader.DYNAMIC_REGISTRIES);

	private DynamicRegistryRegistry() { }
}
