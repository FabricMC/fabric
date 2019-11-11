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

package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;

/**
 * Helper for working with {@link ResourceManager} instances.
 */
public interface ResourceManagerHelper {
	/**
	 * Add a resource reload listener for a given registry.
	 *
	 * @param listener The resource reload listener.
	 * @deprecated Use {@link ResourceManagerHelper#registerReloadListener(IdentifiableResourceReloadListener)}
	 */
	@Deprecated
	default void addReloadListener(IdentifiableResourceReloadListener listener) {
		registerReloadListener(listener);
	}

	/**
	 * Register a resource reload listener for a given resource manager type.
	 *
	 * @param listener The resource reload listener.
	 */
	void registerReloadListener(IdentifiableResourceReloadListener listener);

	/**
	 * Get the ResourceManagerHelper instance for a given resource type.
	 *
	 * @param type The given resource type.
	 * @return The ResourceManagerHelper instance.
	 */
	static ResourceManagerHelper get(ResourceType type) {
		return ResourceManagerHelperImpl.get(type);
	}
}
