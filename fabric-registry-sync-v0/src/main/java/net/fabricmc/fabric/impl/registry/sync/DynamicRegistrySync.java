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

package net.fabricmc.fabric.impl.registry.sync;

import com.mojang.serialization.Lifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.mixin.registry.sync.AccessorRegistry;

/**
 * Handles synchronising changes to the built-in registries into the dynamic registry manager's template manager,
 * in case it gets classloaded early.
 */
public class DynamicRegistrySync {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Sets up a synchronisation that will propagate added entries to the given dynamic registry manager, which
	 * should be the <em>built-in</em> manager. It is never destroyed. We don't ever have to unregister
	 * the registry events.
	 */
	public static void setupSync(DynamicRegistryManager.Impl template) {
		LOGGER.debug("Setting up synchronisation of new BuiltinRegistries entries to the built-in DynamicRegistryManager");
		BuiltinRegistries.REGISTRIES.stream().forEach(source -> setupSync(source, template));
	}

	/**
	 * Sets up an event registration for the source registy that will ensure all entries added from now on
	 * are also added to the template for dynamic registry managers.
	 */
	private static <T> void setupSync(Registry<T> source, DynamicRegistryManager.Impl template) {
		@SuppressWarnings("unchecked") AccessorRegistry<T> sourceAccessor = (AccessorRegistry<T>) source;
		RegistryKey<? extends Registry<T>> sourceKey = source.getKey();
		MutableRegistry<T> target = template.get(sourceKey);

		RegistryEntryAddedCallback.event(source).register((rawId, id, object) -> {
			LOGGER.trace("Synchronizing {} from built-in registry {} into built-in dynamic registry manager template.",
					id, source.getKey());
			Lifecycle lifecycle = sourceAccessor.callGetEntryLifecycle(object);
			RegistryKey<T> entryKey = RegistryKey.of(sourceKey, id);
			target.set(rawId, entryKey, object, lifecycle);
		});
	}
}
