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

package net.fabricmc.fabric.api.component.accessor.v1;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.component.access.ComponentTypeRegistryImpl;

/**
 * Creates and retrieves {@code ComponentType} instances.
 *
 * <p>Because component types are simple and server-side-only this is currently
 * implemented as a simple ID:instance map and not an actual {@code Registry}.
 */
public interface ComponentTypeRegistry {
	/**
	 * Creates and returns a new component type with the given id and absent value.
	 *
	 * @param <T> Type parameter identifying the {@code Class} of the actual component instance
	 * @param id Name-spaced id for this component
	 * @param absentValue Component value to be returned when a component is not present
	 * @return A new {@code ComponentType} instance
	 *
	 * @throws IllegalStateException if the given id is already in use
	 */
	<T> ComponentType<T> createComponent(Identifier id, T absentValue);

	/**
	 * Returns the {@code ComponentType} instance associated with the given id, or {@code null} if not found.
	 *
	 * @param <T> Type parameter identifying the {@code Class} of the actual component instance
	 * @param id Name-spaced id for the component to be found
	 * @return the {@code ComponentType} instance associated with the given id
	 */
	<T> ComponentType<T> getComponent(Identifier id);

	/**
	 * The singleton ComponentTyoeRegistry instance.
	 */
	ComponentTypeRegistry INSTANCE = ComponentTypeRegistryImpl.INSTANCE;
}
