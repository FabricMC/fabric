/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.registry.listeners;

import net.fabricmc.fabric.registry.ExtendedIdList;
import net.fabricmc.fabric.registry.RegistryListener;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class IdListUpdater<K, V> implements RegistryListener<K> {
	public interface Container<V> {
		IdList<V> getIdListForRegistryUpdating();
	}

	private final IdList<V> mappers;
	private Map<Identifier, V> mapperCache = new HashMap<>();

	public IdListUpdater(Container<V> container) {
		this(container.getIdListForRegistryUpdating());
	}

	public IdListUpdater(IdList<V> mappers) {
		this.mappers = mappers;
	}

	@Override
	public void beforeRegistryCleared(Registry<K> registry) {
		mapperCache.clear();
		for (Identifier id : registry.keys()) {
			int rawId = registry.getRawId(registry.get(id));
			V mapper = mappers.getInt(rawId);
			if (mapper != null) {
				mapperCache.put(id, mapper);
			}
		}

		((ExtendedIdList) mappers).clear();
	}

	@Override
	public void beforeRegistryRegistration(Registry<K> registry, int id, Identifier identifier, K object, boolean isNew) {
		if (mapperCache.containsKey(identifier)) {
			mappers.add(mapperCache.get(identifier), id);
		}
	}
}
