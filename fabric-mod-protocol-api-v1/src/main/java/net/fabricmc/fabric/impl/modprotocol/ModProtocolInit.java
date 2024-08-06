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

package net.fabricmc.fabric.impl.modprotocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolRequestS2CPayload;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolResponseC2SPayload;

public final class ModProtocolInit {
	public static final String MOD_ID = "fabric-mod-protocol-api-v1";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static void init() {
		var phase = Identifier.of("fabric", "mod_protocol");
		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.addPhaseOrdering(phase, Event.DEFAULT_PHASE);
		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.register(ModProtocolManager::setupClient);
		ModProtocolManager.collectModProtocols();
		PayloadTypeRegistry.configurationC2S().register(ModProtocolResponseC2SPayload.ID, ModProtocolResponseC2SPayload.PACKET_CODEC);
		PayloadTypeRegistry.configurationS2C().register(ModProtocolRequestS2CPayload.ID, ModProtocolRequestS2CPayload.PACKET_CODEC);
		ServerConfigurationNetworking.registerGlobalReceiver(ModProtocolResponseC2SPayload.ID, (payload, context) -> {
			// Todo: store result on the connection for further usage/api
			((RemoteProtocolStorage) context.networkHandler()).fabric$setRemoteProtocol(payload.supported());
			context.networkHandler().completeTask(ModProtocolManager.SyncConfigurationTask.KEY);
		});
	}
}
