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

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.registry.RegistryAddObjectCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemapCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemoveObjectCallback;
import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class IdListTracker<V, OV> implements RegistryAddObjectCallback<V>, RegistryRemapCallback<V>, RegistryRemoveObjectCallback<V> {
	private final IdList<OV> mappers;
	private Map<Identifier, OV> mapperCache = new HashMap<>();

	private IdListTracker(IdList<OV> mappers) {
		this.mappers = mappers;
	}

	public static <V, OV> void register(Registry<V> registry, IdList<OV> mappers) {
		IdListTracker<V, OV> updater = new IdListTracker<>(mappers);
		RegistryAddObjectCallback.event(registry).register(updater);
		RegistryRemapCallback.event(registry).register(updater);
		RegistryRemoveObjectCallback.event(registry).register(updater);
	}

	@Override
	public void onAddObject(int rawId, Identifier id, V object) {
		if (mapperCache.containsKey(id)) {
			mappers.set(mapperCache.get(id), rawId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void remap(RemapState<V> state) {
		((RemovableIdList<OV>) mappers).fabric_remapIds(state.getRawIdChangeMap());
	}

	@Override
	public void onRemoveObject(int rawId, Identifier id, V object) {
		if (mappers.get(rawId) != null) {
			mapperCache.put(id, mappers.get(rawId));
			//noinspection unchecked
			((RemovableIdList<OV>) mappers).fabric_removeId(rawId);
		}
	}
}
