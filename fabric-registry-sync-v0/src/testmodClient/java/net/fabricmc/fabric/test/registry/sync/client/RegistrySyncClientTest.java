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

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.test.registry.sync.RegistrySyncTest;
import net.fabricmc.fabric.test.registry.sync.TestDynamicObject;
import net.fabricmc.fabric.test.registry.sync.TestNestedDynamicObject;

public final class RegistrySyncClientTest implements ClientModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier SYNCED_ID = new Identifier("fabric-registry-sync-v0-testmod", "synced");

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("Starting dynamic registry sync tests...");

			TestDynamicObject synced1 = handler.getRegistryManager()
					.get(RegistrySyncTest.TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);
			TestDynamicObject synced2 = handler.getRegistryManager()
					.get(RegistrySyncTest.TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);
			TestNestedDynamicObject synced3 = handler.getRegistryManager()
					.get(RegistrySyncTest.TEST_NESTED_DYNAMIC_REGISTRY_KEY)
					.get(SYNCED_ID);

			if (synced1 == null) {
				throw new AssertionError("Did not receive " + RegistrySyncTest.TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY + "/" + SYNCED_ID);
			}

			if (synced1.usesNetworkCodec()) {
				throw new AssertionError("Entries in " + RegistrySyncTest.TEST_SYNCED_1_DYNAMIC_REGISTRY_KEY + " should not use network codec");
			}

			if (synced2 == null) {
				throw new AssertionError("Did not receive " + RegistrySyncTest.TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY + "/" + SYNCED_ID);
			}

			// The client server check is needed since the registries are passed through in singleplayer.
			// The network codec flag would always be false in those cases.
			if (client.getServer() == null && !synced2.usesNetworkCodec()) {
				throw new AssertionError("Entries in " + RegistrySyncTest.TEST_SYNCED_2_DYNAMIC_REGISTRY_KEY + " should use network codec");
			}

			if (synced3 == null) {
				throw new AssertionError("Did not receive " + RegistrySyncTest.TEST_NESTED_DYNAMIC_REGISTRY_KEY + "/" + SYNCED_ID);
			}

			if (synced3.nested().value() != synced1) {
				throw new AssertionError("Did not match up synced nested entry to the other synced value");
			}

			LOGGER.info("Dynamic registry sync tests passed!");
		});
	}
}
