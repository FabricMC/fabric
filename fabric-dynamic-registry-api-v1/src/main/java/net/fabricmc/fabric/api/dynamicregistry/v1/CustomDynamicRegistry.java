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

package net.fabricmc.fabric.api.dynamicregistry.v1;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * A nicer wrapper around {@link net.minecraft.util.registry.DynamicRegistryManager}.
 *
 * @param <T> the type that the registry holds
 */
public class CustomDynamicRegistry<T> {
	private final SimpleRegistry<T> registry;
	private final Supplier<T> defaultValueSupplier;
	private final Codec<T> codec;

	public CustomDynamicRegistry(SimpleRegistry<T> registry, Supplier<T> defaultValueSupplier, Codec<T> codec) {
		this.registry = registry;
		this.defaultValueSupplier = defaultValueSupplier;
		this.codec = codec;
	}

	public SimpleRegistry<T> getRegistry() {
		return this.registry;
	}

	public RegistryKey<? extends Registry<T>> getRegistryRef() {
		return this.registry.getKey();
	}

	public Lifecycle getLifecycle() {
		return this.registry.method_31138();
	}

	public Supplier<T> getDefaultValueSupplier() {
		return this.defaultValueSupplier;
	}

	public DynamicRegistryManager.Info<?> getInfo() {
		return new DynamicRegistryManager.Info<>(this.getRegistryRef(), this.codec, null);
	}
}
