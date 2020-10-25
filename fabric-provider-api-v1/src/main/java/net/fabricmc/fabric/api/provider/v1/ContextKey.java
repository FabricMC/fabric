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

package net.fabricmc.fabric.api.provider.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

/**
 * Unique reference to a type of context.
 */
public final class ContextKey<C> {
	/**
	 * A context key which represents no context.
	 * Typically the context parameter for this type is {@code null}.
	 */
	public static final ContextKey<@Nullable Void> NO_CONTEXT = of(Void.class, new Identifier("fabric-provider-api-v1", "no_context"));
	private static final Map<Class<?>, Map<Identifier, ContextKey<?>>> CONTEXT_KEYS = new HashMap<>();
	private final Class<C> clazz;
	private final Identifier identifier;

	private ContextKey(Class<C> clazz, Identifier identifier) {
		this.clazz = clazz;
		this.identifier = identifier;
	}

	public Class<C> getContextClass() {
		return this.clazz;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public static synchronized <C> ContextKey<C> of(Class<C> clazz, Identifier identifier) {
		Objects.requireNonNull(clazz, "Class type cannot be null");
		Objects.requireNonNull(identifier, "Context key cannot be null");

		CONTEXT_KEYS.putIfAbsent(clazz, new HashMap<>());
		CONTEXT_KEYS.get(clazz).putIfAbsent(identifier, new ContextKey<>(clazz, identifier));

		//noinspection unchecked
		return (ContextKey<C>) CONTEXT_KEYS.get(clazz).get(identifier);
	}
}

