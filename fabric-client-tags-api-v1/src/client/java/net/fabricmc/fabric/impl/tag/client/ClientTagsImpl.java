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

package net.fabricmc.fabric.impl.tag.client;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public class ClientTagsImpl {
	private static final Map<TagKey<?>, ClientTagsLoader.LoadedTag> LOCAL_TAG_HIERARCHY = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> Optional<? extends Registry<T>> getRegistry(TagKey<T> tagKey) {
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
	public static <T> Optional<RegistryEntry<T>> getRegistryEntry(TagKey<T> tagKey, T entry) {
		Optional<? extends Registry<?>> maybeRegistry = getRegistry(tagKey);

		if (maybeRegistry.isEmpty() || !tagKey.isOf(maybeRegistry.get().getKey())) {
			return Optional.empty();
		}

		Registry<T> registry = (Registry<T>) maybeRegistry.get();

		Optional<RegistryKey<T>> maybeKey = registry.getKey(entry);

		return maybeKey.map(registry::entryOf);
	}

	public static ClientTagsLoader.LoadedTag getOrCreatePartiallySyncedTag(TagKey<?> tagKey) {
		ClientTagsLoader.LoadedTag loadedTag = LOCAL_TAG_HIERARCHY.get(tagKey);

		if (loadedTag == null) {
			loadedTag = ClientTagsLoader.loadTag(tagKey);
			LOCAL_TAG_HIERARCHY.put(tagKey, loadedTag);
		}

		return loadedTag;
	}
}
