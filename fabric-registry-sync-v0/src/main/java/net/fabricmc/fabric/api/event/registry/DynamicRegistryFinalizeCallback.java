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

package net.fabricmc.fabric.api.event.registry;

import net.minecraft.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired just before dynamic registries are immutablized,
 * giving modders a last chance to access and change any registry
 * while it's still mutable.
 *
 * @see DynamicRegistryManager
 */
@FunctionalInterface
public interface DynamicRegistryFinalizeCallback {
	void onRegistryFinalize(DynamicRegistryManager registryManager);

	Event<DynamicRegistryFinalizeCallback> EVENT = EventFactory.createArrayBacked(DynamicRegistryFinalizeCallback.class, callbacks -> registryManager -> {
		for (DynamicRegistryFinalizeCallback callback : callbacks) {
			callback.onRegistryFinalize(registryManager);
		}
	});
}
