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

package net.fabricmc.fabric.impl.registry.trackers.vanilla;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.fabric.api.event.registry.RegistryAddEntryCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemapCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemoveEntryCallback;
import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Objects;

public final class BiomeParentTracker implements RegistryAddEntryCallback<Biome>, RegistryRemoveEntryCallback<Biome>, RegistryRemapCallback<Biome> {
	private final Registry<Biome> registry;

	private BiomeParentTracker(Registry<Biome> registry) {
		this.registry = registry;
	}

	public static void register(Registry<Biome> registry) {
		BiomeParentTracker tracker = new BiomeParentTracker(registry);
		RegistryAddEntryCallback.event(registry).register(tracker);
		RegistryRemapCallback.event(registry).register(tracker);
		RegistryRemoveEntryCallback.event(registry).register(tracker);
	}

	@Override
	public void onAddObject(int rawId, Identifier id, Biome object) {
		if (object.hasParent()) {
			Biome.PARENT_BIOME_ID_MAP.set(object, registry.getRawId(registry.get(new Identifier(Objects.requireNonNull(object.getParent())))));
		}
	}

	@Override
	public void onRemap(RemapState<Biome> state) {
		for (Int2IntMap.Entry entry : state.getRawIdChangeMap().int2IntEntrySet()) {
			if (Biome.PARENT_BIOME_ID_MAP.get(entry.getIntKey()) != null) {
				//noinspection unchecked
				((RemovableIdList<Biome>) Biome.PARENT_BIOME_ID_MAP).fabric_remapId(entry.getIntKey(), entry.getIntValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRemoveObject(int rawId, Identifier id, Biome object) {
		((RemovableIdList<Biome>) Biome.PARENT_BIOME_ID_MAP).fabric_remove(object);
		((RemovableIdList<Biome>) Biome.PARENT_BIOME_ID_MAP).fabric_removeId(rawId);
	}
}
