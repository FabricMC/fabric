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

package net.fabricmc.fabric.impl.client.registry.sync;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public final class RegistryRemovalChecker {
	private static final int VERSION = 1;
	public static final Logger LOGGER = LoggerFactory.getLogger(RegistryRemovalChecker.class);
	private static final Gson GSON = new Gson();
	public static final String FILE_NAME = "fabric_registry_removal_check.json";
	private static final boolean DISABLED = Boolean.getBoolean("fabric.registry.debug.disableRemovalCheck");
	private final Set<String> missingNamespaces;
	private final Set<RegistryKey<?>> missingKeys;
	private final Set<Pair<Block, String>> missingBlockStates = new HashSet<>();

	@SuppressWarnings("unchecked, rawtypes")
	public RegistryRemovalChecker(JsonObject root) {
		JsonObject json = root.getAsJsonObject("entries");
		Set<RegistryKey<?>> keys = new HashSet<>();

		for (Map.Entry<String, JsonElement> registry : json.entrySet()) {
			Identifier registryId = Identifier.tryParse(registry.getKey());

			if (registryId == null) continue;

			RegistryKey<? extends Registry<?>> registryRef = RegistryKey.ofRegistry(registryId);

			for (Map.Entry<String, JsonElement> namespacedEntries : registry.getValue().getAsJsonObject().entrySet()) {
				for (JsonElement entry : namespacedEntries.getValue().getAsJsonArray()) {
					Identifier entryId = Identifier.of(namespacedEntries.getKey(), entry.getAsString());

					if (entryId == null) continue;

					keys.add(RegistryKey.of((RegistryKey) registryRef, entryId));
				}
			}

			Registry<?> clientRegistry = Registries.REGISTRIES.get(registryId);

			if (clientRegistry != null) keys.removeAll(clientRegistry.getKeys());
		}

		for (Map.Entry<String, JsonElement> blockNsEntry : root.getAsJsonObject("blockStates").entrySet()) {
			for (Map.Entry<String, JsonElement> blockEntry : blockNsEntry.getValue().getAsJsonObject().entrySet()) {
				Identifier id = Identifier.of(blockNsEntry.getKey(), blockEntry.getKey());

				if (id == null || !Registries.BLOCK.containsId(id)) continue;

				Block block = Registries.BLOCK.get(id);
				Set<Pair<Block, String>> missing = new HashSet<>();

				for (JsonElement state : blockEntry.getValue().getAsJsonArray()) {
					if (block.getStateManager().getProperty(state.getAsString()) == null) {
						missing.add(Pair.of(block, state.getAsString()));
					}
				}

				if (!missing.isEmpty()) {
					keys.add(RegistryKey.of(RegistryKeys.BLOCK, id));
					this.missingBlockStates.addAll(missing);
				}
			}
		}

		this.missingKeys = Collections.unmodifiableSet(keys);
		this.missingNamespaces = keys.stream().map(RegistryKey::getValue).map(Identifier::getNamespace).collect(Collectors.toUnmodifiableSet());
	}

	public Set<String> getMissingNamespaces() {
		return missingNamespaces;
	}

	public Set<RegistryKey<?>> getMissingKeys() {
		return missingKeys;
	}

	public Set<Pair<Block, String>> getMissingBlockStates() {
		return missingBlockStates;
	}

	public static JsonObject serializeRegistries() {
		JsonObject json = new JsonObject();
		json.addProperty("version", VERSION);

		// Sort the entries first
		Map<String, Map<String, Set<String>>> entriesMap = new TreeMap<>();
		Map<String, Map<String, Set<String>>> blockStatesMap = new TreeMap<>();

		for (Registry<?> registry : Registries.REGISTRIES) {
			if (registry.size() == 0 || !RegistryAttributeHolder.get(registry).hasAttribute(RegistryAttribute.REMOVAL_CHECKED)) continue;

			Map<String, Set<String>> nsToEntries = new TreeMap<>();

			for (Identifier id : registry.getIds()) {
				if (!id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
					nsToEntries.computeIfAbsent(id.getNamespace(), ns -> new TreeSet<>()).add(id.getPath());
				}
			}

			entriesMap.put(registry.getKey().getValue().toString(), nsToEntries);
		}

		for (Map.Entry<RegistryKey<Block>, Block> blockEntry : Registries.BLOCK.getEntrySet()) {
			Identifier blockId = blockEntry.getKey().getValue();

			if (
					blockId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)
					|| blockEntry.getValue().getStateManager().getProperties().isEmpty()
			) {
				continue;
			}

			Set<String> states = blockStatesMap.computeIfAbsent(blockId.getNamespace(), ns -> new TreeMap<>()).computeIfAbsent(blockId.getPath(), ns -> new TreeSet<>());

			for (Property<?> property : blockEntry.getValue().getStateManager().getProperties()) {
				states.add(property.getName());
			}
		}

		JsonObject entries = new JsonObject();

		for (Map.Entry<String, Map<String, Set<String>>> registry : entriesMap.entrySet()) {
			JsonObject registryJson = new JsonObject();

			for (Map.Entry<String, Set<String>> nsToEntries : registry.getValue().entrySet()) {
				registryJson.add(nsToEntries.getKey(), Util.make(new JsonArray(), arr -> nsToEntries.getValue().forEach(arr::add)));
			}

			entries.add(registry.getKey(), registryJson);
		}

		JsonObject blockStates = new JsonObject();

		for (Map.Entry<String, Map<String, Set<String>>> registry : blockStatesMap.entrySet()) {
			JsonObject registryJson = new JsonObject();

			for (Map.Entry<String, Set<String>> nsToEntries : registry.getValue().entrySet()) {
				registryJson.add(nsToEntries.getKey(), Util.make(new JsonArray(), arr -> nsToEntries.getValue().forEach(arr::add)));
			}

			entries.add(registry.getKey(), registryJson);
		}

		json.add("entries", entries);
		json.add("blockStates", blockStates);
		return json;
	}

	@Nullable
	public static RegistryRemovalChecker runCheck(Path jsonFile) {
		if (DISABLED) {
			LOGGER.info("Registry removal check was disabled via system property.");
			return null;
		}

		try {
			if (Files.exists(jsonFile)) {
				// Do not run removal check if the file is missing
				JsonObject json = JsonHelper.deserialize(Files.newBufferedReader(jsonFile, StandardCharsets.UTF_8));
				return new RegistryRemovalChecker(json);
			}
		} catch (IOException | JsonParseException e) {
			LOGGER.warn("Could not read {}, registry removal check disabled", FILE_NAME, e);
		}

		return null;
	}

	public static void write(Path jsonFile) {
		if (DISABLED) return;

		try {
			JsonObject json = serializeRegistries();
			Files.writeString(jsonFile, GSON.toJson(json), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.warn("Could not write {}", FILE_NAME, e);
		}
	}
}
