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

package net.fabricmc.fabric.api.tag.client.v1;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.tag.client.ClientTagsLoader;

/**
 * Allows the use of tags by directly loading them from the installed mods.
 *
 * <p>Tags are loaded by the server, either the internal server in singleplayer or the connected server and
 * synced to the client. This can be a pain point for interoperability, as a tag that does not exist on the server
 * because it is part of a mod only present on the client will no longer be available to the client that may wish to
 * query it.
 *
 * <p>Client Tags resolve that issue by lazily reading the tag json files within the mods on the side of the caller,
 * directly, allowing for mods to query tags such as {@link net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags}
 * even when connected to a vanilla server.
 */
public final class ClientTags {
	private static final Map<TagKey<?>, ClientTagsLoader.LoadedTag> LOCAL_TAG_HIERARCHY = new ConcurrentHashMap<>();

	private ClientTags() {
	}

	/**
	 * Loads a tag into the cache, recursively loading any contained tags along with it.
	 *
	 * @param tagKey the {@code TagKey} to load
	 * @return a set of {@code Identifier}s this tag contains
	 */
	public static Set<Identifier> getOrCreateLocalTag(TagKey<?> tagKey) {
		return getOrCreatePartiallySyncedTag(tagKey).completeIds();
	}

	//todo should this be exposed and getOrCreateLocalTag deprecated?
	private static ClientTagsLoader.LoadedTag getOrCreatePartiallySyncedTag(TagKey<?> tagKey) {
		ClientTagsLoader.LoadedTag loadedTag = LOCAL_TAG_HIERARCHY.get(tagKey);

		if (loadedTag == null) {
			loadedTag = ClientTagsLoader.loadTag(tagKey);
			LOCAL_TAG_HIERARCHY.put(tagKey, loadedTag);
		}

		return loadedTag;
	}

	//todo this should probably be the default behavior of *withLocalFallback, with new way to do the old behavior
	// then again, arguably the javadoc describes the old behavior
	public static <T> boolean isInPartiallySyncedTag(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(registryEntry);

		// Check if the tag exists in the dynamic registry first
		Optional<? extends Registry<T>> maybeRegistry = getRegistry(tagKey);

		if (maybeRegistry.isPresent()) {
			if (maybeRegistry.get().getEntryList(tagKey).isPresent()) {
				return registryEntry.isIn(tagKey);
			}
		}

		if (registryEntry.getKey().isEmpty()) {
			// No key?
			return false;
		}

		ClientTagsLoader.LoadedTag wt = getOrCreatePartiallySyncedTag(tagKey);
		boolean isIn = wt.immediateChildIds().contains(registryEntry.getKey().get().getValue());
		Iterator<TagKey<?>> it = wt.immediateChildTags().iterator();

		while (!isIn && it.hasNext()) {
			isIn = isInPartiallySyncedTag((TagKey<T>) it.next(), registryEntry);
		}

		return isIn;
	}

	public static <T> boolean isInPartiallySyncedTag(TagKey<T> tagKey, T entry) {
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(entry);

		return getRegistryEntry(tagKey, entry).map(re -> isInPartiallySyncedTag(tagKey, re)).orElse(false);
	}

	/**
	 * Checks if an entry is in a tag.
	 *
	 * <p>If the synced tag does exist, it is queried. If it does not exist,
	 * the tag populated from the available mods is checked.
	 *
	 * @param tagKey the {@code TagKey} to being checked
	 * @param entry  the entry to check
	 * @return if the entry is in the given tag
	 */
	public static <T> boolean isInWithLocalFallback(TagKey<T> tagKey, T entry) {
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(entry);

		return getRegistryEntry(tagKey, entry).map(re -> isInWithLocalFallback(tagKey, re)).orElse(false);
	}

	/**
	 * Checks if an entry is in a tag, for use with entries from a dynamic registry,
	 * such as {@link net.minecraft.world.biome.Biome}s.
	 *
	 * <p>If the synced tag does exist, it is queried. If it does not exist,
	 * the tag populated from the available mods is checked.
	 *
	 * @param tagKey        the {@code TagKey} to be checked
	 * @param registryEntry the entry to check
	 * @return if the entry is in the given tag
	 */
	public static <T> boolean isInWithLocalFallback(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(registryEntry);

		// Check if the tag exists in the dynamic registry first
		Optional<? extends Registry<T>> maybeRegistry = getRegistry(tagKey);

		if (maybeRegistry.isPresent()) {
			if (maybeRegistry.get().getEntryList(tagKey).isPresent()) {
				return registryEntry.isIn(tagKey);
			}
		}

		if (registryEntry.getKey().isPresent()) {
			return isInLocal(tagKey, registryEntry.getKey().get());
		}

		return false;
	}

	/**
	 * Checks if an entry is in a tag provided by the available mods.
	 *
	 * @param tagKey      the {@code TagKey} to being checked
	 * @param registryKey the entry to check
	 * @return if the entry is in the given tag
	 */
	public static <T> boolean isInLocal(TagKey<T> tagKey, RegistryKey<T> registryKey) {
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(registryKey);

		if (tagKey.registry().getValue().equals(registryKey.getRegistry())) {
			// Check local tags
			Set<Identifier> ids = getOrCreateLocalTag(tagKey);
			return ids.contains(registryKey.getValue());
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<? extends Registry<T>> getRegistry(TagKey<T> tagKey) {
		Objects.requireNonNull(tagKey);

		// Check if the tag represents a dynamic registry
		if (MinecraftClient.getInstance() != null) {
			if (MinecraftClient.getInstance().world != null) {
				if (MinecraftClient.getInstance().world.getRegistryManager() != null) {
					Optional<? extends Registry<T>> maybeRegistry = MinecraftClient.getInstance().world
							.getRegistryManager().getOptional(tagKey.registry());
					if (maybeRegistry.isPresent()) return maybeRegistry;
				}
			}
		}

		return (Optional<? extends Registry<T>>) Registries.REGISTRIES.getOrEmpty(tagKey.registry().getValue());
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<RegistryEntry<T>> getRegistryEntry(TagKey<T> tagKey, T entry) {
		Optional<? extends Registry<?>> maybeRegistry = getRegistry(tagKey);

		if (maybeRegistry.isEmpty() || !tagKey.isOf(maybeRegistry.get().getKey())) {
			return Optional.empty();
		}

		Registry<T> registry = (Registry<T>) maybeRegistry.get();

		Optional<RegistryKey<T>> maybeKey = registry.getKey(entry);

		return maybeKey.map(registry::entryOf);
	}
}
