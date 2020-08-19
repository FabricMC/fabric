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

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryEntryAddedCallback;
import net.fabricmc.fabric.mixin.registry.sync.DynamicRegistryManagerAccessor;

public final class DynamicRegistryEvents {
	public static Map<RegistryKey<? extends Registry<?>>, Event<DynamicRegistryEntryAddedCallback>> ADD_ENTRY_EVENTS;

	private DynamicRegistryEvents() {
	}

	static {
		ADD_ENTRY_EVENTS = Maps.newLinkedHashMap();

		for (RegistryKey<? extends Registry<?>> registryKey : DynamicRegistryManagerAccessor.getInfos().keySet()) {
			ADD_ENTRY_EVENTS.put(registryKey,
					EventFactory.createArrayBacked(
					DynamicRegistryEntryAddedCallback.class,
						callbacks -> (rawId, key, object, registry) -> {
							for (DynamicRegistryEntryAddedCallback callback : callbacks) {
								callback.onEntryAdded(rawId, key, object, registry);
							}
						}));
		}
	}
}
