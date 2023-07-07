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

package net.fabricmc.fabric.impl.registry.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public final class DynamicRegistriesImpl {
	private static final List<RegistryLoader.Entry<?>> DYNAMIC_REGISTRIES = new ArrayList<>(RegistryLoader.DYNAMIC_REGISTRIES);
	private static final Set<RegistryKey<? extends Registry<?>>> DYNAMIC_REGISTRY_KEYS = new HashSet<>();
	private static final Map<RegistryKey<? extends Registry<?>>, SettingsImpl<?>> ALL_SETTINGS = new HashMap<>();

	static {
		for (RegistryLoader.Entry<?> vanillaEntry : RegistryLoader.DYNAMIC_REGISTRIES) {
			DYNAMIC_REGISTRY_KEYS.add(vanillaEntry.key());
		}
	}

	private DynamicRegistriesImpl() {
	}

	public static @Unmodifiable List<RegistryLoader.Entry<?>> getDynamicRegistries() {
		return List.copyOf(DYNAMIC_REGISTRIES);
	}

	public static <T> DynamicRegistries.Settings<T> register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(key, "Registry key cannot be null");
		Objects.requireNonNull(codec, "Codec cannot be null");

		if (!DYNAMIC_REGISTRY_KEYS.add(key)) {
			throw new IllegalArgumentException("Dynamic registry " + key + " has already been registered!");
		}

		var entry = new RegistryLoader.Entry<>(key, codec);
		DYNAMIC_REGISTRIES.add(entry);
		var settings = new SettingsImpl<>(entry);
		ALL_SETTINGS.put(key, settings);
		return settings;
	}

	public static <T> SimpleRegistry<T> createDynamicRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		SettingsImpl<?> settings = ALL_SETTINGS.get(key);

		if (settings != null && settings.defaultId != null) {
			return new SimpleDefaultedRegistry<>(settings.defaultId.toString(), key, lifecycle, false);
		}

		return new SimpleRegistry<>(key, lifecycle);
	}

	private static <T> void addSyncedRegistry(RegistryKey<? extends Registry<T>> registryKey, Codec<T> networkCodec) {
		if (!(SerializableRegistries.REGISTRIES instanceof HashMap<?, ?>)) {
			SerializableRegistries.REGISTRIES = new HashMap<>(SerializableRegistries.REGISTRIES);
		}

		SerializableRegistries.REGISTRIES.put(registryKey, new SerializableRegistries.Info<>(registryKey, networkCodec));
	}

	private static final class SettingsImpl<T> implements DynamicRegistries.Settings<T> {
		private final RegistryLoader.Entry<T> owner;
		private @Nullable Identifier defaultId = null;
		private boolean synced = false;

		private SettingsImpl(RegistryLoader.Entry<T> owner) {
			this.owner = owner;
		}

		@Override
		public DynamicRegistries.Settings<T> synced() {
			return synced(owner.elementCodec());
		}

		@Override
		public DynamicRegistries.Settings<T> synced(Codec<T> networkCodec) {
			Objects.requireNonNull(networkCodec, "Network codec cannot be null");

			if (synced) {
				throw new IllegalStateException("Registry " + owner.key() + " has already been marked as synced!");
			}

			this.synced = true;
			addSyncedRegistry(owner.key(), networkCodec);
			return this;
		}

		@Override
		public DynamicRegistries.Settings<T> defaultKey(RegistryKey<T> key) {
			Objects.requireNonNull(key, "Default key cannot be null");

			if (!key.isOf(owner.key())) {
				throw new IllegalArgumentException("Cannot set %s as the default value of %s - it's for a different registry"
						.formatted(key, owner.key()));
			}

			return defaultId(key.getValue());
		}

		@Override
		public DynamicRegistries.Settings<T> defaultId(Identifier id) {
			Objects.requireNonNull(id, "Default ID cannot be null");
			defaultId = id;
			return this;
		}
	}
}
