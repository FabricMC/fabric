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

package net.fabricmc.fabric.api.resource.loader.v1;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events to directly register resource packs.
 *
 * <p>These packs are not configurable by users. If a resource pack is user-facing and comes from a mod JAR,
 * {@linkplain ResourcePackHelper#registerBuiltinResourcePack register a builtin pack} instead.
 */
// TODO: these events both need a test
// TODO: isFirst would be nice to not generate DRM resources after the first reload, for example?
public final class ResourcePackRegistrationEvents {
	private static final Map<ResourceType, Event<AfterMods>> afterModsEvents = new EnumMap<>(ResourceType.class);
	private static final Map<ResourceType, Event<AfterAll>> afterAllEvents = new EnumMap<>(ResourceType.class);

	/**
	 * Event to register resource packs right after mod resources.
	 */
	public static Event<AfterMods> afterMods(ResourceType resourceType) {
		return afterModsEvents.computeIfAbsent(resourceType, k -> EventFactory.createArrayBacked(AfterMods.class, listeners -> context -> {
			for (AfterMods listener : listeners) {
				listener.registerPacksAfterMods(context);
			}
		}));
	}

	/**
	 * Event to register resource packs at the end of the resource pack list (i.e. after all other packs).
	 * A resource manager is provided to allow for inspection of resources provided by previous packs.
	 */
	public static Event<AfterAll> afterAll(ResourceType resourceType) {
		return afterAllEvents.computeIfAbsent(resourceType, k -> EventFactory.createArrayBacked(AfterAll.class, listeners -> context -> {
			for (AfterAll listener : listeners) {
				listener.registerPacksAfterAll(context);
			}
		}));
	}

	public interface AfterMods {
		void registerPacksAfterMods(Context context);
	}

	public interface AfterAll {
		void registerPacksAfterAll(Context context);
	}

	public interface Context {
		/**
		 * Add a pack to the resource manager.
		 */
		void addPack(ResourcePack pack);

		/**
		 * Provides access to previously registered packs in the resource manager.
		 *
		 * <p>The returned resource manager is immutable. If access to packs registered with {@link #addPack(ResourcePack)} is required,
		 * the resource manager must be queried again after the last call to {@link #addPack(ResourcePack)}.
		 */
		ResourceManager getResourceManager();
	}

	private ResourcePackRegistrationEvents() {
	}
}
