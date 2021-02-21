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

package net.fabricmc.fabric.api.lookup.v1.custom;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.impl.lookup.custom.ApiProviderHashMap;

/**
 * A fast thread-safe copy-on-write map meant to be used as the backing storage for registered providers.
 *
 * <p>Note: This map allows very fast lock-free concurrent reads, but in exchange writes are very expensive and should not be too frequent.
 * Also, keys are compared by reference ({@code ==}) and not using {@link Object#equals}.
 *
 * @param <K> The key type of the map, compared by reference ({@code ==}).
 * @param <V> The value type of the map.
 */
@ApiStatus.NonExtendable
public interface ApiProviderMap<K, V> {
	/**
	 * Create a new instance.
	 */
	static <K, V> ApiProviderMap<K, V> create() {
		return new ApiProviderHashMap<>();
	}

	/**
	 * @see java.util.Map#get
	 */
	@Nullable
	V get(K key);

	/**
	 * @see java.util.Map#putIfAbsent
	 */
	V putIfAbsent(K key, V provider);
}
