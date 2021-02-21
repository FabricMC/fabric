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

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.lookup.custom.ApiLookupMapImpl;

/**
 * A a map meant to be used as the backing storage for custom {@code ApiLookup} instances,
 * to implement a custom equivalent of {@link net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup#get BlockApiLookup#get}.
 *
 * <pre>{@code
 *
 * }</pre>
 *
 * <p>Note: To store a lookup class with type parameters <code>&lt;A, C&gt;</code>,
 * it is recommended to store it as <code>&lt;?, ?&gt;</code> and perform an unchecked cast on queries internally.
 *
 * @param <L> The type of the lookup, similar to the existing .
 */
@ApiStatus.NonExtendable
public interface ApiLookupMap<L> extends Iterable<L> {
	/**
	 * Create a new instance.
	 *
	 * @param lookupFactory The factory for the Api lookups.
	 */
	static <L> ApiLookupMap<L> create(LookupFactory<L> lookupFactory) {
		Objects.requireNonNull(lookupFactory, "Lookup factory cannot be null");

		return new ApiLookupMapImpl<>(lookupFactory);
	}

	/**
	 * Retrieve the Api lookup associated with an identifier.
	 *
	 * @param lookupId The unique identifier of the lookup.
	 * @param apiClass The class of the queried Api.
	 * @param contextClass The class of the queried additional context.
	 * @return The unique lookup with the passed lookupId.
	 * @throws IllegalArgumentException If another {@code apiClass} or another {@code contextClass} was already registered with the same identifier.
	 */
	L getLookup(Identifier lookupId, Class<?> apiClass, Class<?> contextClass);

	interface LookupFactory<L> {
		L get(Class<?> apiClass, Class<?> contextClass);
	}
}
