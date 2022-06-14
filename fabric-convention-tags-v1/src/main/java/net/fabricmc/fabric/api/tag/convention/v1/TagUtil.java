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

package net.fabricmc.fabric.api.tag.convention.v1;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A Helper class for dealing with {@link TagKey}s when their type has no easy way of querying if they are in a tag.
 */
public final class TagUtil {
	/**
	* See {@link TagUtil#isIn(DynamicRegistryManager, TagKey, Object)} to check tags that refer to entries in dynamic
	* registries.
	* @return if the entry is in the provided tag.
	*/
	public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
		return isIn(null, tagKey, entry);
	}

	/**
	 * @param registryManager the registry manager instance of the client or server. If the tag refers to entries
	 *                        within a dynamic registry, such as {@link net.minecraft.world.biome.Biome}s,
	 *                        this must be passed to correctly evaluate the tag. Otherwise, the registry is found by
	 *                        looking in {@link Registry#REGISTRIES}.
	 * @return if the entry is in the provided tag.
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean isIn(@Nullable DynamicRegistryManager registryManager, TagKey<T> tagKey, T entry) {
		Optional<? extends Registry<?>> maybeRegistry;

		if (registryManager != null) {
			maybeRegistry = registryManager.getOptional(tagKey.registry());
		} else {
			maybeRegistry = Registry.REGISTRIES.getOrEmpty(tagKey.registry().getValue());
		}

		if (maybeRegistry.isPresent()) {
			if (tagKey.isOf(maybeRegistry.get().getKey())) {
				Registry<T> registry = (Registry<T>) maybeRegistry.get();

				Optional<RegistryKey<T>> maybeKey = registry.getKey(entry);

				// Check synced tag
				return maybeKey.filter(registryKey -> registry.entryOf(registryKey).isIn(tagKey))
						.isPresent();
			}
		}

		return false;
	}

	/**
	 * @return if the entry is in the provided tag.
	 */
	public static <T> boolean isIn(TagKey<T> tagKey, RegistryEntry<T> registryEntry) {
		return registryEntry.isIn(tagKey);
	}
}
