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

package net.fabricmc.fabric.api.registry.v1;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class DynamicRegistryEvents {
	/**
	 * This event gets triggered when a new {@link DynamicRegistryManager} gets created, but before it gets filled.
	 * Therefore, this is the ideal place to register callbacks to dynamic registries.
	 *
	 * <p>For example, the following code is used to register a callback that gets triggered for any registered Biome, both JSON and code defined.
	 *
	 * <pre>{@code
	 * DynamicRegistryEvents.SETUP.register(registryManager -> {
	 *  // Get the registry from the dynamic registry manager
	 * 	Registry<Biome> biomes = registryManager.get(Registry.BIOME_KEY);
	 *
	 * 	RegistryExtensions.get(biomes).getEntryAddedEvent().register((rawId, id, object) -> {
	 * 		// Do something
	 * 	});
	 * });
	 * }</pre>
	 */
	public static final Event<Setup> SETUP = EventFactory.createArrayBacked(Setup.class, callbacks -> registryManager -> {
		for (Setup callback : callbacks) {
			callback.onRegistrySetup(registryManager);
		}
	});

	@FunctionalInterface
	public interface Setup {
		void onRegistrySetup(DynamicRegistryManager registryManager);
	}

	private DynamicRegistryEvents() {
	}
}
