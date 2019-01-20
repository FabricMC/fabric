/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.resources;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.resource.KeyedResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceListenerRegistry;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ResourceListenerRegistryImpl implements ResourceListenerRegistry {
	private static final Map<ResourceType, ResourceListenerRegistryImpl> registryMap = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	private final List<KeyedResourceReloadListener> addedListeners = new ArrayList<>();

	public static ResourceListenerRegistry get(ResourceType type) {
		return registryMap.computeIfAbsent(type, (t) -> new ResourceListenerRegistryImpl());
	}

	public static void sort(ResourceType type, List<ResourceReloadListener> listeners) {
		ResourceListenerRegistryImpl instance = registryMap.get(type);
		if (instance != null) {
			instance.sort(listeners);
		}
	}

	public void sort(List<ResourceReloadListener> listeners) {
		listeners.removeAll(addedListeners);

		// General rules:
		// - We *do not* touch the ordering of vanilla listeners. Ever.
		//   While dependency values are provided where possible, we cannot
		//   trust them 100%. Only code doesn't lie.
		// - We add all custom listeners after vanilla listeners. Same reasons.

		List<KeyedResourceReloadListener> listenersToAdd = Lists.newArrayList(addedListeners);
		Set<Identifier> resolvedIds = new HashSet<>();
		for (ResourceReloadListener listener : listeners) {
			if (listener instanceof KeyedResourceReloadListener) {
				resolvedIds.add(((KeyedResourceReloadListener) listener).getFabricId());
			}
		}

		int lastSize = -1;
		while (listeners.size() != lastSize) {
			lastSize = listeners.size();

			Iterator<KeyedResourceReloadListener> it = listenersToAdd.iterator();
			while (it.hasNext()) {
				KeyedResourceReloadListener listener = it.next();
				if (resolvedIds.containsAll(listener.getFabricIdDependencies())) {
					resolvedIds.add(listener.getFabricId());
					listeners.add(listener);
					it.remove();
				}
			}
		}

		for (KeyedResourceReloadListener listener : listenersToAdd) {
			LOGGER.warn("Could not resolve dependencies for listener: " + listener.getFabricId() + "!");
		}
	}

	public void add(KeyedResourceReloadListener listener) {
		addedListeners.add(listener);
	}
}
