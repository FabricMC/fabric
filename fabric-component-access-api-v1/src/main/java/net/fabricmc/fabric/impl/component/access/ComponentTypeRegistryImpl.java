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

package net.fabricmc.fabric.impl.component.access;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.component.access.v1.ComponentType;
import net.fabricmc.fabric.api.component.access.v1.ComponentTypeRegistry;

public class ComponentTypeRegistryImpl implements ComponentTypeRegistry {
	private ComponentTypeRegistryImpl() { }

	public static final ComponentTypeRegistry INSTANCE = new ComponentTypeRegistryImpl();

	private static final Object2ObjectOpenHashMap<Identifier, ComponentType<?>> TYPES_BY_ID = new Object2ObjectOpenHashMap<>();

	@Override
	public <T> ComponentType<T> createComponent(Identifier id, T absentValue) {
		Preconditions.checkState(!TYPES_BY_ID.containsKey(id), "Component already registered with ID " + id.toString());
		final ComponentType<T> result = new ComponentTypeImpl<>(absentValue);
		TYPES_BY_ID.put(id, result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ComponentType<T> getComponent(Identifier id) {
		return (ComponentType<T>) TYPES_BY_ID.get(id);
	}
}
