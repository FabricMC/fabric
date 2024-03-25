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

package net.fabricmc.fabric.impl.lookup.custom;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;

public final class ApiProviderHashMap<K, V> implements ApiProviderMap<K, V>, @UnmodifiableView Map<K,V> {
	private volatile Map<K, V> lookups = new Reference2ReferenceOpenHashMap<>();

	@Override
	public int size() {
		return lookups.size();
	}

	@Override
	public boolean isEmpty() {
		return lookups.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return lookups.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return lookups.containsValue(value);
	}

	@Nullable
	@Override
	public V get(Object key) {
		Objects.requireNonNull(key, "Key may not be null.");

		return lookups.get(key);
	}
	@Override
	public @Nullable V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Set<K> keySet() {
		return Collections.unmodifiableSet(lookups.keySet());
	}

	@Override
	public @NotNull Collection<V> values() {
		return Collections.unmodifiableCollection(lookups.values());
	}


	@Override
	public @NotNull Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(lookups.entrySet());
	}

	@Override
	public synchronized @Nullable V putIfAbsent(K key, V provider) {
		Objects.requireNonNull(key, "Key may not be null.");
		Objects.requireNonNull(provider, "Provider may not be null.");

		// We use a copy-on-write strategy to allow any number of reads to concur with a write
		Map<K, V> lookupsCopy = new Reference2ReferenceOpenHashMap<>(lookups);
		V result = lookupsCopy.putIfAbsent(key, provider);
		lookups = lookupsCopy;

		return result;
	}

	@Override
	public @UnmodifiableView Map<K, V> asMap() {
		return this;
	}
}
