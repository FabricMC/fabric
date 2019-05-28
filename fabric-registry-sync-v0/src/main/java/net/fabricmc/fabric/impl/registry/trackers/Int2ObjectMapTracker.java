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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.registry.RegistryAddEntryCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemoveEntryCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Int2ObjectMapTracker<V, OV> implements RegistryAddEntryCallback<V>, RegistryIdRemapCallback<V>, RegistryRemoveEntryCallback<V> {
	private final Int2ObjectMap<OV> mappers;
	private Map<Identifier, OV> removedMapperCache = new HashMap<>();

	private Int2ObjectMapTracker(Int2ObjectMap<OV> mappers) {
		this.mappers = mappers;
	}

	public static <V, OV> void register(Registry<V> registry, Int2ObjectMap<OV> mappers) {
		Int2ObjectMapTracker<V, OV> updater = new Int2ObjectMapTracker<>(mappers);
		RegistryAddEntryCallback.event(registry).register(updater);
		RegistryIdRemapCallback.event(registry).register(updater);
		RegistryRemoveEntryCallback.event(registry).register(updater);
	}

	@Override
	public void onAddObject(int rawId, Identifier id, V object) {
		if (removedMapperCache.containsKey(id)) {
			mappers.put(rawId, removedMapperCache.get(id));
		}
	}

	@Override
	public void onRemap(RemapState<V> state) {
		Int2ObjectMap<OV> oldMappers = new Int2ObjectOpenHashMap<>(mappers);

		mappers.clear();
		for (int i : oldMappers.keySet()) {
			int newI = state.getRawIdChangeMap().getOrDefault(i, i);
			if (mappers.containsKey(newI)) {
				throw new RuntimeException("Int2ObjectMap contained two equal IDs " + newI + " (" + state.getIdFromOld(i) + "/" + i + " -> " + state.getIdFromNew(newI) + "/" + newI + ")!");
			}

			mappers.put(newI, oldMappers.get(i));
		}
	}

	@Override
	public void onRemoveObject(int rawId, Identifier id, V object) {
		if (mappers.containsKey(rawId)) {
			removedMapperCache.put(id, mappers.remove(rawId));
		}
	}
}
