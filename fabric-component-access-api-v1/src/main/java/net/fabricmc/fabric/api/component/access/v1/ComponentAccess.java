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

package net.fabricmc.fabric.api.component.access.v1;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * Controls access to components within a component provider that has already
 * been located within a world via {@link ComponentType#getAccess()}.
 *
 * @param <T> Type parameter for the {@code ComponentType} to which this instance controls access.
 */
public interface ComponentAccess<T> {
	/**
	 * Identifies the type of the component to which this instance controls access.
	 *
	 * @return The type of the component to which this instance controls access
	 */
	ComponentType<T> componentType();

	/**
	 * Retrieves the component with the given access parameters, or {@link ComponentType#absent()} if the component
	 * is missing or inaccessible with the given parameters.
	 *
	 * @param side Side from which the component is being accessed
	 * @param id Identifier of a specific component or sub-component
	 * @return The component accessible via the given parameters
	 */
	T get(/* @Nullable */ Direction side, /* @Nullable */ Identifier id);

	/**
	 * Retrieves the component with the given access parameters, or {@link ComponentType#absent()} if the component
	 * is missing or inaccessible with the given parameters.
	 *
	 * @param side Side from which the component is being accessed
	 * @return The component accessible via the given parameters
	 */
	default T get(/* @Nullable */ Direction side) {
		return get(side, null);
	}

	/**
	 * Retrieves the component with the given access parameters, or {@link ComponentType#absent()} if the component
	 * is missing or inaccessible with the given parameters.
	 *
	 * @param id Identifier of a specific component or sub-component
	 * @return The component accessible via the given parameters
	 */
	default T get(/* @Nullable */ Identifier id) {
		return get(null, id);
	}

	/**
	 * Retrieves the component with default access parameters, or {@link ComponentType#absent()} if the component
	 * is missing or inaccessible with default parameters.
	 *
	 * @return The component accessible via the given parameters
	 */
	default T get() {
		return get(null, null);
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given action if the value is not {@link ComponentType#absent()}.
	 *
	 * @param side Side from which the component is being accessed
	 * @param id Identifier of a specific component or sub-component
	 * @param action Action to be applied to a non-absent component
	 * @return {@code true} if a non-absent component was successfully obtained and the action was applied to it
	 */
	default boolean acceptIfPresent(/* @Nullable */ Direction side, /* @Nullable */ Identifier id, Consumer<T> action) {
		final T access = get(side, id);

		if (access != componentType().absent()) {
			action.accept(access);
			return true;
		}

		return false;
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given action if the value is not {@link ComponentType#absent()}.
	 *
	 * @param side Side from which the component is being accessed
	 * @param action Action to be applied to a non-absent component
	 * @return {@code true} if a non-absent component was successfully obtained and the action was applied to it
	 */
	default boolean acceptIfPresent(/* @Nullable */ Direction side, Consumer<T> action) {
		return acceptIfPresent(side, null, action);
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given action if the value is not {@link ComponentType#absent()}.
	 *
	 * @param id Identifier of a specific component or sub-component
	 * @param action Action to be applied to a non-absent component
	 * @return {@code true} if a non-absent component was successfully obtained and the action was applied to it
	 */
	default boolean acceptIfPresent(/* @Nullable */ Identifier id, Consumer<T> action) {
		return acceptIfPresent(null, id, action);
	}

	/**
	 * Retrieves the component with default access parameters and applies the
	 * given action if the value is not {@link ComponentType#absent()}.
	 *
	 * @param action Action to be applied to a non-absent component
	 * @return {@code true} if a non-absent component was successfully obtained and the action was applied to it
	 */
	default boolean acceptIfPresent(Consumer<T> action) {
		return acceptIfPresent(null, null, action);
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given function if the component instance is not {@link ComponentType#absent()}, returning the result.
	 *
	 * @param side Side from which the component is being accessed
	 * @param id Identifier of a specific component or sub-component
	 * @param function Function to be applied to a non-absent component
	 * @return Function result if a non-absent component was successfully obtained or {@code null} otherwise
	 */
	default <V> V applyIfPresent(/* @Nullable */ Direction side, /* @Nullable */ Identifier id, Function<T, V> function) {
		final T access = get(side, id);

		if (access != componentType().absent()) {
			return function.apply(access);
		}

		return null;
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given function if the component instance is not {@link ComponentType#absent()}, returning the result.
	 *
	 * @param side Side from which the component is being accessed
	 * @param function Function to be applied to a non-absent component
	 * @return Function result if a non-absent component was successfully obtained or {@code null} otherwise
	 */
	default <V> V applyIfPresent(/* @Nullable */ Direction side, Function<T, V> function) {
		return applyIfPresent(side, null, function);
	}

	/**
	 * Retrieves the component with the given access parameters and applies the
	 * given function if the component instance is not {@link ComponentType#absent()}, returning the result.
	 *
	 * @param id Identifier of a specific component or sub-component
	 * @param function Function to be applied to a non-absent component
	 * @return Function result if a non-absent component was successfully obtained or {@code null} otherwise
	 */
	default <V> V applyIfPresent(/* @Nullable */ Identifier id, Function<T, V> function) {
		return applyIfPresent(null, id, function);
	}

	/**
	 * Retrieves the component with default access parameters and applies the
	 * given function if the component instance is not {@link ComponentType#absent()}, returning the result.
	 *
	 * @param function Function to be applied to a non-absent component
	 * @return Function result if a non-absent component was successfully obtained or {@code null} otherwise
	 */
	default <V> V applyIfPresent(Function<T, V> function) {
		return applyIfPresent(null, null, function);
	}
}
