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

package net.fabricmc.fabric.impl.registry.sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;

import net.fabricmc.fabric.api.event.registry.DynamicRegistryEvents;

public final class DynamicRegistryRegistrationContextImpl implements DynamicRegistryEvents.RegistrationContext {
	private final List<RegistryLoader.Entry<?>> entries = new ArrayList<>();

	public List<RegistryLoader.Entry<?>> getEntries() {
		return entries;
	}

	@Override
	public <T> void add(RegistryKey<? extends Registry<T>> registryKey, Codec<T> codec) {
		add(new RegistryLoader.Entry<>(registryKey, codec));
	}

	@Override
	public void add(RegistryLoader.Entry<?> entry) {
		entries.add(entry);
	}

	@Override
	public void add(RegistryLoader.Entry<?>... entries) {
		add(Arrays.asList(entries));
	}

	@Override
	public void add(Collection<? extends RegistryLoader.Entry<?>> entries) {
		this.entries.addAll(entries);
	}
}
