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

package net.fabricmc.fabric.impl.object.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;

import net.fabricmc.fabric.mixin.object.builder.TrackedDataHandlerRegistryAccessor;

public class FabricTrackedDataRegistryImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(FabricTrackedDataRegistryImpl.class);

	private static List<TrackedDataHandler<?>> VANILLA_HANDLERS = new ArrayList<>();
	private static SortedMap<Identifier, TrackedDataHandler<?>> MODDED_HANDLERS = new TreeMap<>();
	private static List<TrackedDataHandler<?>> EXTERNAL_MODDED_HANDLERS = new ArrayList<>();

	private FabricTrackedDataRegistryImpl() {
	}

	public static void storeVanillaHandlers() {
		Int2ObjectBiMap<TrackedDataHandler<?>> dataHandlers = TrackedDataHandlerRegistryAccessor.fabric_getDataHandlers();

		for (TrackedDataHandler<?> handler : dataHandlers) {
			VANILLA_HANDLERS.add(handler);
		}

		LOGGER.debug("Stored {} vanilla handlers", VANILLA_HANDLERS.size());
	}

	/**
	 * Reorders handlers in {@link net.minecraft.entity.data.TrackedDataHandlerRegistry#DATA_HANDLERS} to have a consistent order between client and server.
	 *
	 * <p>The order used is the following:
	 *
	 * <ul>
	 *   <li>Vanilla handlers</li>
	 *   <li>Handlers in the Fabric API registry (sorted by ID)</li>
	 *   <li>External modded handlers</li>
	 * </ul>
	*/
	private static void reorderHandlers() {
		Int2ObjectBiMap<TrackedDataHandler<?>> dataHandlers = TrackedDataHandlerRegistryAccessor.fabric_getDataHandlers();
		LOGGER.debug("Reordering tracked data handlers containing {} entries", dataHandlers.size());

		// Store external modded handlers
		for (TrackedDataHandler<?> handler : dataHandlers) {
			if (VANILLA_HANDLERS.contains(handler)) continue;
			if (MODDED_HANDLERS.containsValue(handler)) continue;
			if (EXTERNAL_MODDED_HANDLERS.contains(handler)) continue;

			EXTERNAL_MODDED_HANDLERS.add(handler);
			LOGGER.warn("Tracked data handler {} is not managed by vanilla or Fabric API; it may be prone to desynchronization!", handler);
		}

		// Reset the map so that handlers can be added back in a new order
		dataHandlers.clear();

		// Add handlers back to map
		for (TrackedDataHandler<?> handler : VANILLA_HANDLERS) {
			dataHandlers.add(handler);
		}

		for (TrackedDataHandler<?> handler : MODDED_HANDLERS.values()) {
			dataHandlers.add(handler);
		}

		for (TrackedDataHandler<?> handler : EXTERNAL_MODDED_HANDLERS) {
			dataHandlers.add(handler);
		}

		LOGGER.debug("Finished reordering tracked data handlers containing {} entries", dataHandlers.size());
	}

	public static void registerHandler(Identifier id, TrackedDataHandler<?> handler) {
		Objects.requireNonNull(id, "Tracked data handler ID cannot be null!");
		Objects.requireNonNull(handler, "Tracked data handler cannot be null!");

		if (MODDED_HANDLERS.containsKey(id)) {
			throw new IllegalArgumentException("Tracked data handler ID already registered: " + id);
		} else if (MODDED_HANDLERS.containsValue(handler)) {
			throw new IllegalArgumentException("The same tracked data handler cannot be registered under multiple IDs");
		} else if (VANILLA_HANDLERS.contains(handler) || EXTERNAL_MODDED_HANDLERS.contains(handler)) {
			throw new IllegalArgumentException("Cannot register tracked data handler added via TrackedDataHandlerRegistry.register");
		}

		MODDED_HANDLERS.put(id, handler);
		reorderHandlers();
	}
}
