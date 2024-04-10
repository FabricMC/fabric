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

package net.fabricmc.fabric.test.registry.sync;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;

public final class CustomDynamicRegistryTest implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final RegistryKey<Registry<TestDynamicObject>> TEST_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "test_dynamic"));
	public static final RegistryKey<Registry<TestNestedDynamicObject>> TEST_NESTED_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "test_dynamic_nested"));
	public static final RegistryKey<Registry<TestDynamicObject>> TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "test_dynamic_synced_1"));
	public static final RegistryKey<Registry<TestDynamicObject>> TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "test_dynamic_synced_2"));
	public static final RegistryKey<Registry<TestDynamicObject>> TEST_EMPTY_SYNCED_DYNAMIC_REGISTRY_KEY =
			RegistryKey.ofRegistry(new Identifier("fabric", "test_dynamic_synced_empty"));

	private static final RegistryKey<TestDynamicObject> SYNCED_ENTRY_KEY =
			RegistryKey.of(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY, new Identifier("fabric-registry-sync-v0-testmod", "synced"));
	private static final TagKey<TestDynamicObject> TEST_DYNAMIC_OBJECT_TAG =
			TagKey.of(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY, new Identifier("fabric-registry-sync-v0-testmod", "test"));

	@Override
	public void onInitialize() {
		DynamicRegistries.register(TEST_DYNAMIC_REGISTRY_KEY, TestDynamicObject.CODEC);
		DynamicRegistries.registerSynced(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY, TestDynamicObject.CODEC);
		DynamicRegistries.registerSynced(TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY, TestDynamicObject.CODEC, TestDynamicObject.NETWORK_CODEC);
		DynamicRegistries.registerSynced(TEST_NESTED_DYNAMIC_REGISTRY_KEY, TestNestedDynamicObject.CODEC);
		DynamicRegistries.registerSynced(TEST_EMPTY_SYNCED_DYNAMIC_REGISTRY_KEY, TestDynamicObject.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);

		DynamicRegistrySetupCallback.EVENT.register(registryView -> {
			addListenerForDynamic(registryView, TEST_DYNAMIC_REGISTRY_KEY);
			addListenerForDynamic(registryView, TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY);
			addListenerForDynamic(registryView, TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY);
			addListenerForDynamic(registryView, TEST_NESTED_DYNAMIC_REGISTRY_KEY);
		});

		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
			// Check that the tag has applied
			RegistryEntry.Reference<TestDynamicObject> entry = registries.get(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY)
					.getEntry(SYNCED_ENTRY_KEY)
					.orElseThrow();

			if (!entry.isIn(TEST_DYNAMIC_OBJECT_TAG)) {
				LOGGER.error("Required dynamic registry entry is not in the expected tag! client: " + client);
			}

			LOGGER.info("Found {} in tag {} (client: {})", entry, TEST_DYNAMIC_OBJECT_TAG, client);
		});
	}

	private static void addListenerForDynamic(DynamicRegistryView registryView, RegistryKey<? extends Registry<?>> key) {
		registryView.registerEntryAdded(key, (rawId, id, object) -> {
			LOGGER.info("Loaded entry of {}: {} = {}", key, id, object);
		});
	}
}
