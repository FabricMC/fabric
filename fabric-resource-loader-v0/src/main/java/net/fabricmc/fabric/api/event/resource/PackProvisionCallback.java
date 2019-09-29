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

package net.fabricmc.fabric.api.event.resource;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;

/**
 * Callback for registering custom resource pack providers.
 *
 * @param <T> the resource pack container type
 */
@FunctionalInterface
public interface PackProvisionCallback<T extends ResourcePackContainer> {

	/**
	 * The event for registering custom resource pack providers.
	 */
	Event<PackProvisionCallback<ClientResourcePackContainer>> RESOURCE = EventFactory.createArrayBacked(PackProvisionCallback.class,
		listeners -> manager -> {
			for (PackProvisionCallback<ClientResourcePackContainer> each : listeners) {
				each.registerTo(manager);
			}
		}
	);

	/**
	 * The event for registering custom data pack providers.
	 */
	Event<PackProvisionCallback<ResourcePackContainer>> DATA = EventFactory.createArrayBacked(PackProvisionCallback.class,
		listeners -> manager -> {
			for (PackProvisionCallback<ResourcePackContainer> each : listeners) {
				each.registerTo(manager);
			}
		}
	);

	/**
	 * Register the provider to the pack container manager.
	 *
	 * @param manager the manager
	 */
	void registerTo(ResourcePackContainerManager<T> manager);
}
