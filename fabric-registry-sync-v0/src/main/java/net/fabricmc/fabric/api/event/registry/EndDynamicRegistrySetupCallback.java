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

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event gets triggered when a new {@link DynamicRegistryManager} gets created and
 * filled with new entries.
 */
@ApiStatus.Experimental
public interface EndDynamicRegistrySetupCallback {
	/**
	 * Called when a new {@link DynamicRegistryManager} gets created and
	 * filled with new entries.
	 *
	 * <p>Registry managers are "layered" and there are multiple loading
	 * phases corresponding to the source and type of the loaded contents.
	 * (See {@link net.minecraft.util.registry.ServerDynamicRegistryType}.)
	 * This event is called for each loading phase.
	 * @param registryManager the registry manager that contains only the entries loaded at the current phase
	 * @param combinedRegistryManager the registry manager that combines all entries previously loaded
	 */
	void onEndDynamicRegistrySetup(DynamicRegistryManager registryManager, DynamicRegistryManager combinedRegistryManager);

	Event<EndDynamicRegistrySetupCallback> EVENT = EventFactory.createArrayBacked(
			EndDynamicRegistrySetupCallback.class,
			callbacks -> (registryManager, combinedRegistryManager) -> {
				for (EndDynamicRegistrySetupCallback callback : callbacks) {
					callback.onEndDynamicRegistrySetup(registryManager, combinedRegistryManager);
				}
			}
	);
}
