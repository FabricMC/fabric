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
	private final Identifier id;

	private ContextKey(Class<C> clazz, Identifier id) {
		this.clazz = clazz;
		this.id = id;
	}

	/**
	 * @return the class type of the context object this key represents
	 */
	public Class<C> getContextClass() {
		return this.clazz;
	}

	/**
	 * @return the id of the context key
	 */
	public Identifier getId() {
		return this.id;
	}

	/**
	 * Gets a context key of a specified type and id, creating a new key if the context key of the type and id does not exist.
	 *
	 * @param type the class type of the context object
	 * @param id the id of the context key
	 * @param <C> the type of context object
	 * @return the context key which represents a type of context object
	 */
	public static synchronized <C> ContextKey<C> of(Class<C> type, Identifier id) {
		Objects.requireNonNull(type, "Class type cannot be null");
		Objects.requireNonNull(id, "Context key cannot be null");

		CONTEXT_KEYS.putIfAbsent(type, new HashMap<>());
		CONTEXT_KEYS.get(type).putIfAbsent(id, new ContextKey<>(type, id));

		//noinspection unchecked
		return (ContextKey<C>) CONTEXT_KEYS.get(type).get(id);
	}
}

