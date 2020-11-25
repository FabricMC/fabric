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

package net.fabricmc.fabric.impl.resource.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.ModContainer;

public class ResourceManagerHelperImpl implements ResourceManagerHelper {
	private static final Map<ResourceType, ResourceManagerHelperImpl> registryMap = new HashMap<>();
	private static final Set<Pair<String, ModNioResourcePack>> builtinResourcePacks = new HashSet<>();
	private static final Logger LOGGER = LogManager.getLogger();

	private final Set<Identifier> addedListenerIds = new HashSet<>();
	private final Set<IdentifiableResourceReloadListener> addedListeners = new LinkedHashSet<>();

	public static ResourceManagerHelper get(ResourceType type) {
		return registryMap.computeIfAbsent(type, (t) -> new ResourceManagerHelperImpl());
	}

	/**
	 * Registers a built-in resource pack. Internal implementation.
	 *
	 * @param id The identifier of the resource pack.
	 * @param subPath The sub path in the mod resources.
	 * @param container The mod container.
	 * @param enabledByDefault True if enabled by default, else false.
	 * @return True if successfully registered the resource pack, else false.
	 * @see ResourceManagerHelper#registerBuiltinResourcePack(Identifier, String, ModContainer, boolean)
	 */
	public static boolean registerBuiltinResourcePack(Identifier id, String subPath, ModContainer container, boolean enabledByDefault) {
		String separator = container.getRootPath().getFileSystem().getSeparator();
		subPath = subPath.replace("/", separator);

		Path resourcePackPath = container.getRootPath().resolve(subPath).toAbsolutePath().normalize();

		if (!Files.exists(resourcePackPath)) {
			return false;
		}

		String name = id.getNamespace() + "/" + id.getPath();

		builtinResourcePacks.add(new Pair<>(name, new ModNioResourcePack(container.getMetadata(), resourcePackPath, null, enabledByDefault) {
			@Override
			public String getName() {
				return name; // Built-in resource pack provided by a mod, the name is overriden.
			}
		}));

		return true;
	}

	public static void registerBuiltinResourcePacks(ResourceType resourceType, Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		// Loop through each registered built-in resource packs and add them if valid.
		for (Pair<String, ModNioResourcePack> entry : builtinResourcePacks) {
			// Add the built-in pack only if namespaces for the specified resource type are present.
			if (!entry.getRight().getNamespaces(resourceType).isEmpty()) {
				// Make the resource pack profile for built-in pack, should never be always enabled.
				ResourcePackProfile profile = ResourcePackProfile.of(entry.getLeft(), false,
						entry::getRight, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
				if (profile != null) {
					consumer.accept(profile);
				}
			}
		}
	}

	public static void sort(ResourceType type, List<ResourceReloadListener> listeners) {
		ResourceManagerHelperImpl instance = registryMap.get(type);

		if (instance != null) {
			instance.sort(listeners);
		}
	}

	protected void sort(List<ResourceReloadListener> listeners) {
		listeners.removeAll(addedListeners);

		// General rules:
		// - We *do not* touch the ordering of vanilla listeners. Ever.
		//   While dependency values are provided where possible, we cannot
		//   trust them 100%. Only code doesn't lie.
		// - We addReloadListener all custom listeners after vanilla listeners. Same reasons.

		List<IdentifiableResourceReloadListener> listenersToAdd = Lists.newArrayList(addedListeners);
		Set<Identifier> resolvedIds = new HashSet<>();

		for (ResourceReloadListener listener : listeners) {
			if (listener instanceof IdentifiableResourceReloadListener) {
				resolvedIds.add(((IdentifiableResourceReloadListener) listener).getFabricId());
			}
		}

		int lastSize = -1;

		while (listeners.size() != lastSize) {
			lastSize = listeners.size();

			Iterator<IdentifiableResourceReloadListener> it = listenersToAdd.iterator();

			while (it.hasNext()) {
				IdentifiableResourceReloadListener listener = it.next();

				if (resolvedIds.containsAll(listener.getFabricDependencies())) {
					resolvedIds.add(listener.getFabricId());
					listeners.add(listener);
					it.remove();
				}
			}
		}

		for (IdentifiableResourceReloadListener listener : listenersToAdd) {
			LOGGER.warn("Could not resolve dependencies for listener: " + listener.getFabricId() + "!");
		}
	}

	@Override
	public void registerReloadListener(IdentifiableResourceReloadListener listener) {
		if (!addedListenerIds.add(listener.getFabricId())) {
			LOGGER.warn("Tried to register resource reload listener " + listener.getFabricId() + " twice!");
			return;
		}

		if (!addedListeners.add(listener)) {
			throw new RuntimeException("Listener with previously unknown ID " + listener.getFabricId() + " already in listener set!");
		}
	}
}
