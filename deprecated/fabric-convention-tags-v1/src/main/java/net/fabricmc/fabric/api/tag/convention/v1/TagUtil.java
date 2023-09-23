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

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @deprecated Please use {@link net.fabricmc.fabric.api.tag.convention.v2.TagUtil}
 */
@Deprecated
public final class TagUtil {
	private TagUtil() {
	}

	/**
	 * @deprecated Please use {@link net.fabricmc.fabric.api.tag.convention.v2.TagUtil#isIn}
	 */
	public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
		return isIn(null, tagKey, entry);
	}

	/**
	 * @deprecated Please use {@link net.fabricmc.fabric.api.tag.convention.v2.TagUtil#isIn}
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean isIn(@Nullable DynamicRegistryManager registryManager, TagKey<T> tagKey, T entry) {
		Optional<? extends Registry<?>> maybeRegistry;
		Objects.requireNonNull(tagKey);
		Objects.requireNonNull(entry);

		if (registryManager != null) {
			maybeRegistry = registryManager.getOptional(tagKey.registry());
		} else {
			maybeRegistry = Registries.REGISTRIES.getOrEmpty(tagKey.registry().getValue());
		}

		if (maybeRegistry.isPresent()) {
			if (tagKey.isOf(maybeRegistry.get().getKey())) {
				Registry<T> registry = (Registry<T>) maybeRegistry.get();

				Optional<RegistryKey<T>> maybeKey = registry.getKey(entry);

				// Check synced tag
				if (maybeKey.isPresent()) {
					return registry.entryOf(maybeKey.get()).isIn(tagKey);
				}
			}
		}

		return false;
	}
}
