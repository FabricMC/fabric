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

import net.fabricmc.fabric.impl.registry.ExtendedIdList;
import net.fabricmc.fabric.impl.registry.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPostRegisterCallback;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPreClearCallback;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Collection;
import java.util.function.Function;

public final class StateIdTracker<T, S> implements RegistryPreClearCallback<T>, RegistryPostRegisterCallback<T> {
	private final IdList<S> stateList;
	private final Function<T, Collection<S>> stateGetter;

	public static <T, S> void register(SimpleRegistry<T> registry, IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		StateIdTracker<T, S> tracker = new StateIdTracker<>(stateList, stateGetter);
		((ListenableRegistry<T>) registry).getPreClearEvent().register(tracker);
		((ListenableRegistry<T>) registry).getPostRegisterEvent().register(tracker);
	}

	private StateIdTracker(IdList<S> stateList, Function<T, Collection<S>> stateGetter) {
		this.stateList = stateList;
		this.stateGetter = stateGetter;
	}

	@Override
	public void onPreClear() {
		((ExtendedIdList) stateList).clear();
	}

	@Override
	public void onPostRegister(int rawId, Identifier id, T object) {
		stateGetter.apply(object).forEach(stateList::add);
	}
}
