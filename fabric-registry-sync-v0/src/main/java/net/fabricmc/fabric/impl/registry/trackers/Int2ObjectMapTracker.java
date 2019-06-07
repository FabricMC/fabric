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
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Int2ObjectMapTracker<V, OV> implements RegistryEntryAddedCallback<V>, RegistryIdRemapCallback<V>, RegistryEntryRemovedCallback<V> {

	private final Int2ObjectMap<OV> mappers;
	private final Map<Integer, Identifier> mapIds = new HashMap<>();
	private Map<Identifier, OV> removedMapperCache = new HashMap<>();
	private final String name;

	private Int2ObjectMapTracker(Int2ObjectMap<OV> mappers, String name) {
		this.mappers = mappers;
		this.name = name;
	}

	public static <V, OV> void register(Registry<V> registry, String name, Int2ObjectMap<OV> mappers) {
		Int2ObjectMapTracker<V, OV> updater = new Int2ObjectMapTracker<>(mappers, name);
		RegistryEntryAddedCallback.event(registry).register(updater);
		RegistryIdRemapCallback.event(registry).register(updater);
		RegistryEntryRemovedCallback.event(registry).register(updater);
	}

	@Override
	public void onEntryAdded(int rawId, Identifier id, V object) {
		if (removedMapperCache.containsKey(id)) {
			mappers.put(rawId, removedMapperCache.get(id));
		}
	}

	@Override
	public void onRemap(RemapState<V> state) {
		Int2ObjectMap<OV> oldMappers = new Int2ObjectOpenHashMap<>(mappers);

		mappers.clear();
		mapIds.clear();
		for (int i : oldMappers.keySet()) {
			int newI = state.getRawIdChangeMap().getOrDefault(i, i);
			Identifier id = state.getIdFromNew(i);
			if (mappers.containsKey(newI)) {
				if (!mapIds.get(newI).equals(id)) throw new RuntimeException("Int2ObjectMap " + name + " contained two objects with int ID " + newI + " (" + mapIds.get(newI) + "/" + newI + " vs " +id + "/" + newI + ")!");
			} else {
				mappers.put(newI, oldMappers.get(i));
				mapIds.put(newI, id);
			}
		}
	}

	@Override
	public void onEntryRemoved(int rawId, Identifier id, V object) {
		if (mappers.containsKey(rawId)) {
			removedMapperCache.put(id, mappers.remove(rawId));
		}
	}
}
