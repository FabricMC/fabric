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

package net.fabricmc.fabric.impl.registry.sync.trackers;

import java.util.Collection;
import java.util.function.Function;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.impl.registry.sync.RemovableIdList;

public final class StateIdTracker<T, S> implements RegistryIdRemapCallback<T>, RegistryEntryAddedCallback<T> {
	private final Logger logger = LogManager.getLogger();
	private final Registry<T> registry;
	private final IdList<S> stateList;
	private final Function<T, Collection<S>> stateGetter;
	private int currentHighestId = 0;

	public static <T, S> void register(Registry<T> registry, IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		StateIdTracker<T, S> tracker = new StateIdTracker<>(registry, stateList, stateGetter);
		RegistryEntryAddedCallback.event(registry).register(tracker);
		RegistryIdRemapCallback.event(registry).register(tracker);
	}

	private StateIdTracker(Registry<T> registry, IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		this.registry = registry;
		this.stateList = stateList;
		this.stateGetter = stateGetter;

		recalcHighestId();
	}

	@Override
	public void onEntryAdded(int rawId, Identifier id, T object) {
		if (rawId == currentHighestId + 1) {
			stateGetter.apply(object).forEach(stateList::add);
			currentHighestId = rawId;
		} else {
			logger.debug("[fabric-registry-sync] Non-sequential RegistryEntryAddedCallback for " + object.getClass().getSimpleName() + " ID tracker (at " + id + "), forcing state map recalculation...");
			recalcStateMap();
		}
	}

	@Override
	public void onRemap(RemapState<T> state) {
		recalcStateMap();
	}

	private void recalcStateMap() {
		((RemovableIdList) stateList).fabric_clear();

		Int2ObjectMap<T> sortedBlocks = new Int2ObjectRBTreeMap<>();

		currentHighestId = 0;
		registry.forEach((t) -> {
			int rawId = registry.getRawId(t);
			currentHighestId = Math.max(currentHighestId, rawId);
			sortedBlocks.put(rawId, t);
		});

		for (T b : sortedBlocks.values()) {
			stateGetter.apply(b).forEach(stateList::add);
		}
	}

	private void recalcHighestId() {
		currentHighestId = 0;

		for (T object : registry) {
			currentHighestId = Math.max(currentHighestId, registry.getRawId(object));
		}
	}
}
