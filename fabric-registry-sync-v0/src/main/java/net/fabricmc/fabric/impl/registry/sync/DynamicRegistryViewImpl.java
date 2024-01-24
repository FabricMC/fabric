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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

public final class DynamicRegistryViewImpl implements DynamicRegistryView {
	private final Map<RegistryKey<? extends Registry<?>>, Registry<?>> registries;

	public DynamicRegistryViewImpl(Map<RegistryKey<? extends Registry<?>>, Registry<?>> registries) {
		this.registries = registries;
	}

	@Override
	public DynamicRegistryManager asDynamicRegistryManager() {
		return new DynamicRegistryManager.Immutable() {
			@SuppressWarnings("unchecked")
			public <T> Optional<Registry<T>> getOptional(RegistryKey<? extends Registry<? extends T>> key) {
				return Optional.ofNullable((Registry<T>) DynamicRegistryViewImpl.this.registries.get(key));
			}

			public Stream<Entry<?>> streamAllRegistries() {
				return DynamicRegistryViewImpl.this.stream()
						.map(this::entry);
			}

			private <T> Entry<T> entry(Registry<T> registry) {
				return new Entry<>(registry.getKey(), registry);
			}

			public Immutable toImmutable() {
				return this;
			}
		};
	}

	@Override
	public Stream<Registry<?>> stream() {
		return this.registries.values().stream();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<Registry<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
		return Optional.ofNullable((Registry<T>) this.registries.get(registryRef));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void registerEntryAdded(RegistryKey<? extends Registry<? extends T>> registryRef, RegistryEntryAddedCallback<T> callback) {
		Registry<T> registry = (Registry<T>) this.registries.get(registryRef);

		if (registry != null) {
			RegistryEntryAddedCallback.event(registry).register(callback);
		}
	}
}
