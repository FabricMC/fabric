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

package net.fabricmc.fabric.api.event.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;

import net.fabricmc.fabric.mixin.registry.sync.RegistryLoaderAccessor;

public class DynamicRegistryRegistry {
	public static <T> RegistryLoader.Entry<T> register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(key, "Registry key cannot be null!");
		Objects.requireNonNull(codec, "Codec cannot be null!");
		return register(new RegistryLoader.Entry<>(key, codec));
	}

	public static <T> RegistryLoader.Entry<T> register(RegistryLoader.Entry<T> entry) {
		Objects.requireNonNull(entry, "Entry cannot be null!");
		RegistryLoader.DYNAMIC_REGISTRIES.stream().filter(it -> it.key().getValue().getPath().equals(entry.key().getValue().getPath())).findFirst().ifPresent(it -> {
			throw new IllegalStateException("Dynamic registry path clash between " + it + " and " + entry);
		});

		List<RegistryLoader.Entry<?>> list = new ArrayList<>(RegistryLoader.DYNAMIC_REGISTRIES);
		list.add(entry);
		RegistryLoaderAccessor.setDynamicRegistries(Collections.unmodifiableList(list));

		return entry;
	}

	private DynamicRegistryRegistry() { }
}
