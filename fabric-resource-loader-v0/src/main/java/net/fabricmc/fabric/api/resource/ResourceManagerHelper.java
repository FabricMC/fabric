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
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.ModContainer;

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

	/**
	 * Registers a built-in resource pack.
	 *
	 * <p>A built-in resource pack is an extra resource pack provided by your mod which is not always active, it's similar to the "Programmer Art" resource pack.
	 *
	 * <p>Why and when to use it? A built-in resource pack should be used to provide extra assets/data that should be optional with your mod but still directly provided by it.
	 * For example it could provide textures of your mod in another resolution, or could allow to provide different styles of your assets.
	 *
	 * <p>The {@code subPath} corresponds to a path in the JAR file which points to the resource pack folder. For example the subPath can be {@code "resourcepacks/extra"}.
	 *
	 * <p>Note about the enabled by default parameter: a resource pack cannot be enabled by default, only data packs can.
	 * Making this work for resource packs is near impossible without touching how Vanilla handles disabled resource packs.
	 *
	 * @param id The identifier of the resource pack.
	 * @param subPath The sub path in the mod resources.
	 * @param container The mod container.
	 * @param enabledByDefault True if enabled by default, else false.
	 * @return True if successfully registered the resource pack, else false.
	 */
	static boolean registerBuiltinResourcePack(Identifier id, String subPath, ModContainer container, boolean enabledByDefault) {
		return ResourceManagerHelperImpl.registerBuiltinResourcePack(id, subPath, container, enabledByDefault);
	}
}
