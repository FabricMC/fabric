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

package net.fabricmc.fabric.impl.dimension;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.serialization.DynamicOps;

import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.mixin.dimension.RegistryOpsAccessor;
import net.fabricmc.fabric.mixin.dimension.SimpleRegistryAccessor;

/**
 * When it tries to deserialize a registry entry in {@link RegistryElementCodec#decode(DynamicOps, Object)},
 * It will eventually call {@link SimpleRegistry#getOrCreateEntry(RegistryKey)}.
 * When deserializing a dimension of an unloaded dimension mod or datapack,
 * its deserialization will fail, but it will leave registry entries of its registry objects(custom biomes, noise settings...).
 * These entries are invalid because the mods/datapacks providing the objects are not present.
 * Then in {@link SimpleRegistry#freeze()} it will throw an exception.
 * To avoid that, purge the invalid entries when deserialization fails.
 */
public class RegistryPurge {
	public static final Logger LOGGER = LoggerFactory.getLogger("RegistryPurge");

	public static <T> void purgeInvalidEntriesFromDynamicOps(DynamicOps<T> dynamicOps) {
		if (dynamicOps instanceof RegistryOps<T> registryOps) {
			DynamicRegistryManager manager = ((RegistryOpsAccessor) registryOps).fabric$getRegistryManager();
			purgeInvalidEntriesFromRegistryManager(manager);
		}
	}

	public static void purgeInvalidEntriesFromRegistryManager(DynamicRegistryManager manager) {
		manager.streamAllRegistries().forEach(entry -> {
			Registry<?> registry = entry.value();
			purgeInvalidEntriesFromRegistry(registry);
		});
	}

	public static void purgeInvalidEntriesFromRegistry(Registry<?> registry) {
		if (registry instanceof SimpleRegistry) {
			Map<RegistryKey<?>, RegistryEntry.Reference<?>> keyToEntry = ((SimpleRegistryAccessor) registry).fabric$getKeyToEntry();

			keyToEntry.entrySet().removeIf(entry -> {
				if (!entry.getValue().hasKeyAndValue()) {
					LOGGER.error("Removed invalid registry entry {}", entry.getKey().getValue());
					return true;
				}

				return false;
			});
		}
	}
}
