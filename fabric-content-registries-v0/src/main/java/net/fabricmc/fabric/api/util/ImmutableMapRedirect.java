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

package net.fabricmc.fabric.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public class ImmutableMapRedirect<K, V> {
	protected final Supplier<Map<K, V>> mapSupplier;
	protected final Consumer<Map<K, V>> mapSetter;

	public ImmutableMapRedirect(Supplier<Map<K, V>> mapSupplier, Consumer<Map<K, V>> mapSetter) {
		this.mapSupplier = mapSupplier;
		this.mapSetter = mapSetter;
	}

	public V get(K key) {
		return this.mapSupplier.get().get(key);
	}

	public void put(K key, V value) {
		this.ensureMutable();

		this.mapSupplier.get().put(key, value);
	}

	public V remove(K value) {
		this.ensureMutable();

		return this.mapSupplier.get().remove(value);
	}

	public boolean containsKey(K key) {
		return this.mapSupplier.get().containsKey(key);
	}

	public boolean containsValue(V value) {
		return this.mapSupplier.get().containsValue(value);
	}

	public Set<K> keySet() {
		return this.mapSupplier.get().keySet();
	}

	protected void ensureMutable() {
		Map<K, V> collection = this.mapSupplier.get();

		if (collection instanceof ImmutableMap) {
			this.mapSetter.accept(new HashMap<>(collection));
		}
	}
}
