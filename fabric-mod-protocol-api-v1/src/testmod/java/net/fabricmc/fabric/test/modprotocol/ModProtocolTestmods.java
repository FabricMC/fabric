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

package net.fabricmc.fabric.test.modprotocol;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.modprotocol.v1.ModProtocolIds;
import net.fabricmc.fabric.api.modprotocol.v1.ModProtocolRegistry;
import net.fabricmc.fabric.api.modprotocol.v1.ServerModProtocolLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.impl.modprotocol.ModProtocolLocator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public final class ModProtocolTestmods implements ModInitializer {
	public static final String ID = "fabric-mod-protocol-api-v1-testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static Identifier id(String name) {
		return Identifier.of(ID, name);
	}

	public void onInitialize() {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(ID).get();

		ModProtocolRegistry.register(ModProtocolIds.special("test_modification"), "Hello there", "1.12.2", IntList.of(1, 2, 3), true, false);
		ModProtocolRegistry.register(ModProtocolIds.special("test_modification2"), "Test2", "1.0", IntList.of(1), false, true);
		ModProtocolRegistry.register(ModProtocolIds.special("test_modification3"), "Test3", "2.0", IntList.of(2), true, true);
		ModProtocolRegistry.register(ModProtocolIds.special("test_modification4"), "Test4", "2.0", IntList.of(2), true, true);
		//ModProtocolRegistry.register(ModProtocolIds.special("test_modification5"), "Test5", "1.0", IntList.of(1), true, true);
		ModProtocolRegistry.register(ModProtocolIds.special("test_modification6"), "Test6", "1.0", IntList.of(1), true, true);
		ModProtocolRegistry.register(ModProtocolIds.special("test_modification7"), "Test7", "1.0", IntList.of(1), true, true);

		CustomValue testificate = modContainer.getMetadata().getCustomValue("test_fabric:mod_protocol");
		CustomValue defaulted = modContainer.getMetadata().getCustomValue("test2_fabric:mod_protocol");

		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			Object2IntMap<Identifier> protocols = ServerModProtocolLookup.getAllSupportedProtocols(handler);
			LOGGER.info("Protocols supported by {}", handler.getDebugProfile().getName());

			for (Object2IntMap.Entry<Identifier> entry : protocols.object2IntEntrySet()) {
				LOGGER.info(" - {}: {}", entry.getKey(), entry.getIntValue());
			}
		}));

		LOGGER.info("Parser full array-like, {}", ModProtocolLocator.decodeFullDefinition(testificate, modContainer.getMetadata(), true));
		LOGGER.info("Parser default, no defaults: {}", ModProtocolLocator.decodeFullDefinition(testificate, modContainer.getMetadata(), false));
		LOGGER.info("Parser default, with defaults: {}", ModProtocolLocator.decodeFullDefinition(defaulted, modContainer.getMetadata(), false));
	}
}
