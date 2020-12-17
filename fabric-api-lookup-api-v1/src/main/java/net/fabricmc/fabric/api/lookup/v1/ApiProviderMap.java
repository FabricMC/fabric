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

package net.fabricmc.fabric.api.lookup.v1;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.impl.lookup.ApiProviderHashMap;

/**
 * The building block for creating your own Lookup.
 * You should store an instance of this interface in every instance of the Lookup.
 * This map allows very fast lock-free concurrent reads, and uses a copy-on-write strategy for writes.
 * This means in particular that writes are very expensive.
 * Note that keys are compared by reference (==) and not using .equals!
 */
public interface ApiProviderMap<K, V> {
	static <K, V> ApiProviderMap<K, V> create() {
		return new ApiProviderHashMap<>();
	}

	@Nullable
	V get(K key);

	V putIfAbsent(K key, V provider);
}
