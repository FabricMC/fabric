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

package net.fabricmc.fabric.impl.resource.loader.addpacks;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.api.resource.loader.v1.ResourcePackRegistrationEvents;
import net.fabricmc.fabric.impl.resource.loader.FabricModResourcePack;

/**
 * Implementation code for {@link ResourcePackRegistrationEvents}. Placed in a separate package to group all the related
 * mixins (which just need to get the {@code List<ResourcePack>} at the right time and call this helper).
 */
public class ResourcePackRegistrationEventsImpl {
	/**
	 * Fire the events. Packs will be added to the list directly.
	 */
	public static ImmutableList<ResourcePack> addPacks(ResourceType resourceType, List<ResourcePack> resourcePacks) {
		List<ResourcePack> packs = new ArrayList<>(resourcePacks.size());

		for (ResourcePack pack : resourcePacks) {
			packs.add(pack);

			if (pack instanceof FabricModResourcePack) {
				ResourcePackRegistrationEvents.afterMods(resourceType).invoker().registerPacksAfterMods(new Context(resourceType, packs));
			}
		}

		ResourcePackRegistrationEvents.afterAll(resourceType).invoker().registerPacksAfterAll(new Context(resourceType, packs));
		return ImmutableList.copyOf(packs);
	}

	private static class Context implements ResourcePackRegistrationEvents.Context {
		private final ResourceType resourceType;
		private final List<ResourcePack> packs;
		// Lazily computed to not pay the price of resource manager creation if nothing is using it.
		@Nullable
		private ResourceManager resourceManager = null;

		private Context(ResourceType resourceType, List<ResourcePack> packs) {
			this.resourceType = resourceType;
			this.packs = packs;
		}

		@Override
		public void addPack(ResourcePack pack) {
			packs.add(pack);
			resourceManager = null;
		}

		@Override
		public ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LifecycledResourceManagerImpl(resourceType, List.copyOf(packs));
			}

			return resourceManager;
		}
	}
}
