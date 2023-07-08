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

package net.fabricmc.fabric.test.registry.sync.client;

import static net.fabricmc.fabric.test.registry.sync.CustomDynamicRegistryTest.TEST_EMPTY_SYNCED_DYNAMIC_REGISTRY_KEY;
import static net.fabricmc.fabric.test.registry.sync.CustomDynamicRegistryTest.TEST_NESTED_DYNAMIC_REGISTRY_KEY;
import static net.fabricmc.fabric.test.registry.sync.CustomDynamicRegistryTest.TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY;
import static net.fabricmc.fabric.test.registry.sync.CustomDynamicRegistryTest.TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.test.registry.sync.TestDynamicObject;
import net.fabricmc.fabric.test.registry.sync.TestNestedDynamicObject;

public final class DynamicRegistryClientTest implements ClientModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier SYNCED_ID = new Identifier("fabric-registry-sync-v0-testmod", "synced");

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("Starting dynamic registry sync tests...");

			TestDynamicObject synced1 = handler.getRegistryManager()
					.get(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);
			TestDynamicObject synced2 = handler.getRegistryManager()
					.get(TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);
			TestNestedDynamicObject simpleNested = handler.getRegistryManager()
					.get(TEST_NESTED_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);

			LOGGER.info("Synced - simple: {}", synced1);
			LOGGER.info("Synced - custom network codec: {}", synced2);
			LOGGER.info("Synced - simple nested: {}", simpleNested);

			if (synced1 == null) {
				didNotReceive(TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY, SYNCED_ID);
			}

			if (synced1.usesNetworkCodec()) {
				throw new AssertionError("Entries in " + TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY + " should not use network codec");
			}

			if (synced2 == null) {
				didNotReceive(TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY, SYNCED_ID);
			}

			// The client server check is needed since the registries are passed through in singleplayer.
			// The network codec flag would always be false in those cases.
			if (client.getServer() == null && !synced2.usesNetworkCodec()) {
				throw new AssertionError("Entries in " + TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY + " should use network codec");
			}

			if (simpleNested == null) {
				didNotReceive(TEST_NESTED_DYNAMIC_REGISTRY_KEY, SYNCED_ID);
			}

			if (simpleNested.nested().value() != synced1) {
				throw new AssertionError("Did not match up synced nested entry to the other synced value");
			}

			// If the registries weren't passed through in SP, check that the empty registry was skipped.
			if (client.getServer() == null && handler.getRegistryManager().getOptional(TEST_EMPTY_SYNCED_DYNAMIC_REGISTRY_KEY).isPresent()) {
				throw new AssertionError("Received empty registry that should have been skipped");
			}

			LOGGER.info("Dynamic registry sync tests passed!");
		});
	}

	private static void didNotReceive(RegistryKey<? extends Registry<?>> registryKey, Identifier entryId) {
		throw new AssertionError("Did not receive " + registryKey + "/" + entryId);
	}
}
