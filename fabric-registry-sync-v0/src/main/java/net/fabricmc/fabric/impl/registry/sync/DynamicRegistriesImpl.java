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
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public final class DynamicRegistriesImpl {
	private static final List<RegistryLoader.Entry<?>> DYNAMIC_REGISTRIES = new ArrayList<>(RegistryLoader.DYNAMIC_REGISTRIES);
	private static final Set<RegistryKey<? extends Registry<?>>> DYNAMIC_REGISTRY_KEYS = new HashSet<>();

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
		return new SettingsImpl<>(entry);
	}

	private static <T> void addSyncedRegistry(RegistryKey<? extends Registry<T>> registryKey, Codec<T> networkCodec) {
		if (!(SerializableRegistries.REGISTRIES instanceof HashMap<?, ?>)) {
			SerializableRegistries.REGISTRIES = new HashMap<>(SerializableRegistries.REGISTRIES);
		}

		SerializableRegistries.REGISTRIES.put(registryKey, new SerializableRegistries.Info<>(registryKey, networkCodec));
	}

	private static final class SettingsImpl<T> implements DynamicRegistries.Settings<T> {
		private final RegistryLoader.Entry<T> owner;
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
	}
}
