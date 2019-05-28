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
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.function.Function;

public final class StateIdTracker<T, S> implements RegistryIdRemapCallback<T> {
	private final Registry<T> registry;
	private final IdList<S> stateList;
	private final Function<T, Collection<S>> stateGetter;

	public static <T, S> void register(Registry<T> registry, IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		RegistryIdRemapCallback.event(registry).register(new StateIdTracker<>(registry, stateList, stateGetter));
	}

	private StateIdTracker(Registry<T> registry, IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		this.registry = registry;
		this.stateList = stateList;
		this.stateGetter = stateGetter;
	}

	@Override
	public void onRemap(RemapState<T> state) {
		((RemovableIdList) stateList).fabric_clear();

		Int2ObjectMap<T> sortedBlocks = new Int2ObjectRBTreeMap<>();
		registry.forEach((t) -> sortedBlocks.put(registry.getRawId(t), t));
		for (T b : sortedBlocks.values()) {
			stateGetter.apply(b).forEach(stateList::add);
		}
	}
}
