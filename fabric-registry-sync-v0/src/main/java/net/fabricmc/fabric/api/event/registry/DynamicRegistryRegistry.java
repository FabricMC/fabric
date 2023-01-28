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

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;

import net.fabricmc.fabric.mixin.registry.sync.RegistryLoaderAccessor;

/**
 * Methods for registering dynamically loadable registries. These registries are loaded
 * after static registries are frozen and have full access to them, alongside any other
 * dynamic registry loaded before them.
 *
 * <p>NOTE: These registries are not reloadable.
 *
 * <p><b>Experimental feature</b>, dynamic registries are a pretty new feature, and Mojang
 * can make any changes to their implementation in the upcoming versions. Since we cannot guarantee this
 * will work in the next versions, we reserve the right to remove or change it without further notice.
 *
 * @apiNote The path used for {@code key} is used as path for resource search as well,
 * so {@code "fabric-api:dynamic_data"} would be searched in {@code "resources/data/modid/dynamic_data/"},
 * {@code modid} being mod id of any mod, it is recommended to use your mod id in
 * registry path to avoid path clashes, like {@code "fabric-api:fabric-api/dynamic_data"}.
 */
@ApiStatus.Experimental
public class DynamicRegistryRegistry {
	/**
	 * Registers a dynamically loaded registry.
	 *
	 * @param key Registry key for the registry
	 * @param codec The codec used for deserialization
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(key, "Registry key cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		register(new RegistryLoader.Entry<>(key, codec));
	}

	/**
	 * Registers a dynamically loaded registry.
	 *
	 * @param entry The entry
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void register(RegistryLoader.Entry<T> entry) {
		Objects.requireNonNull(entry, "Entry cannot be null!");
		validateKey(entry.key());
		MUTABLE_REGISTRIES.add(entry);
		RegistryLoaderAccessor.setDynamicRegistries(Collections.unmodifiableList(DynamicRegistryRegistry.MUTABLE_REGISTRIES));
	}

	/**
	 * Registers a dynamically loaded registry before a specific entry.
	 * If there is no matching entry with key, entry is added to the end of the list.
	 *
	 * @param before Entry key to register before.
	 * @param key Registry key for the registry
	 * @param codec The codec used for deserialization
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void registerBefore(RegistryKey<? extends Registry<?>> before, RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(before, "Before key cannot be null!");
		Objects.requireNonNull(key, "Registry key cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		registerBefore(before, new RegistryLoader.Entry<>(key, codec));
	}

	/**
	 * Registers a dynamically loaded registry before a specific entry.
	 * If there is no matching entry with key, entry is added to the end of the list.
	 *
	 * @param before Entry key to register before.
	 * @param entry The entry
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void registerBefore(RegistryKey<? extends Registry<?>> before, RegistryLoader.Entry<T> entry) {
		Objects.requireNonNull(before, "Before key cannot be null!");
		Objects.requireNonNull(entry, "Entry cannot be null!");
		validateKey(entry.key());
		MUTABLE_REGISTRIES.add(findIndex(before, false), entry);
		RegistryLoaderAccessor.setDynamicRegistries(Collections.unmodifiableList(DynamicRegistryRegistry.MUTABLE_REGISTRIES));
	}

	/**
	 * Registers a dynamically loaded registry after a specific entry.
	 * If there is no matching entry with key, entry is added to the end of the list.
	 *
	 * @param after Entry key to register after.
	 * @param key Registry key for the registry
	 * @param codec The codec used for deserialization
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void registerAfter(RegistryKey<? extends Registry<?>> after, RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(after, "After key cannot be null!");
		Objects.requireNonNull(key, "Registry key cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		registerAfter(after, new RegistryLoader.Entry<>(key, codec));
	}

	/**
	 * Registers a dynamically loaded registry after a specific entry.
	 * If there is no matching entry with key, entry is added to the end of the list.
	 *
	 * @param after Entry key to register after.
	 * @param entry The entry
	 * @throws IllegalStateException if key path clashes with an already registered entry
	 */
	public static <T> void registerAfter(RegistryKey<? extends Registry<?>> after, RegistryLoader.Entry<T> entry) {
		Objects.requireNonNull(after, "After key cannot be null!");
		Objects.requireNonNull(entry, "Entry cannot be null!");
		validateKey(entry.key());
		MUTABLE_REGISTRIES.add(findIndex(after, true), entry);
		RegistryLoaderAccessor.setDynamicRegistries(Collections.unmodifiableList(DynamicRegistryRegistry.MUTABLE_REGISTRIES));
	}

	// private

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRegistryRegistry.class);
	private static final List<RegistryLoader.Entry<?>> MUTABLE_REGISTRIES = new ArrayList<>(RegistryLoader.DYNAMIC_REGISTRIES);

	private static void validateKey(RegistryKey<?> key) {
		String path = key.getValue().getPath();

		for (RegistryLoader.Entry<?> entry : MUTABLE_REGISTRIES) {
			if (entry.key().getValue().getPath().equals(path)) {
				throw new IllegalStateException("Dynamic registry path clash between " + entry.key() + " and " + key);
			}
		}
	}

	private static int findIndex(RegistryKey<? extends Registry<?>> key, boolean after) {
		for (int i = 0; i < MUTABLE_REGISTRIES.size(); ++i) {
			if (keysEqual(MUTABLE_REGISTRIES.get(i).key(), key)) {
				return after ? i + 1 : i;
			}
		}

		LOGGER.warn("No matching entry for key: " + key);
		return MUTABLE_REGISTRIES.size();
	}

	private static boolean keysEqual(RegistryKey<?> first, RegistryKey<?> second) {
		return first.getRegistry().equals(second.getRegistry()) && first.getValue().equals(second.getValue());
	}

	private DynamicRegistryRegistry() { }
}
