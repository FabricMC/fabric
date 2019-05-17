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

package net.fabricmc.fabric.impl.registry.trackers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.impl.registry.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPreClearCallback;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPreRegisterCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Int2ObjectMapTracker<V, OV> implements RegistryPreClearCallback<V>, RegistryPreRegisterCallback<V> {
	private final Int2ObjectMap<OV> mappers;
	private final Registry<V> registry;
	private Map<Identifier, OV> mapperCache = new HashMap<>();

	private Int2ObjectMapTracker(Registry<V> registry, Int2ObjectMap<OV> mappers) {
		this.registry = registry;
		this.mappers = mappers;
	}

	public static <V, OV> void register(Registry<V> registry, Int2ObjectMap<OV> mappers) {
		Int2ObjectMapTracker<V, OV> updater = new Int2ObjectMapTracker<>(registry, mappers);
		((ListenableRegistry<V>) registry).getPreClearEvent().register(updater);
		((ListenableRegistry<V>) registry).getPreRegisterEvent().register(updater);
	}

	@Override
	public void onPreClear() {
		mapperCache.clear();
		for (Identifier id : registry.getIds()) {
			int rawId = registry.getRawId(registry.get(id));
			OV mapper = mappers.get(rawId);
			if (mapper != null) {
				mapperCache.put(id, mapper);
			}
		}

		mappers.clear();
	}

	@Override
	public void onPreRegister(int id, Identifier identifier, V object, boolean isNewToRegistry) {
		if (mapperCache.containsKey(identifier)) {
			mappers.put(id, mapperCache.get(identifier));
		}
	}
}
