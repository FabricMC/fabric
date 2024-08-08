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

package net.fabricmc.fabric.impl.modprotocol.client;

import java.util.HashMap;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.impl.modprotocol.ModProtocolImpl;
import net.fabricmc.fabric.impl.modprotocol.ModProtocolInit;
import net.fabricmc.fabric.impl.modprotocol.ModProtocolManager;
import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolRequestS2CPayload;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolResponseC2SPayload;

public final class ClientModProtocolInit implements ClientModInitializer {
	public void onInitializeClient() {
		ClientConfigurationNetworking.registerGlobalReceiver(ModProtocolRequestS2CPayload.ID, (payload, context) -> {
			var map = new HashMap<Identifier, ModProtocolImpl>(payload.modProtocol().size());

			for (ModProtocolImpl protocol : payload.modProtocol()) {
				map.put(protocol.id(), protocol);
			}

			ModProtocolManager.ValidationResult validate = ModProtocolManager.validateClient(map);

			if (validate.isSuccess()) {
				((RemoteProtocolStorage) context.networkHandler()).fabric$setRemoteProtocol(validate.supportedProtocols());
				context.responseSender().sendPacket(new ModProtocolResponseC2SPayload(validate.supportedProtocols()));
				return;
			}

			var b = new StringBuilder();
			b.append("Disconnected due to mismatched protocols!").append('\n');
			b.append("Missing entries:").append('\n');
			ModProtocolManager.appendTextEntries(validate.missing(), ModProtocolManager.LOCAL_MOD_PROTOCOLS_BY_ID, -1, text -> b.append(" - ").append(text.getString()));

			context.responseSender().disconnect(ModProtocolManager.constructMessage(validate.missing(), ModProtocolManager.LOCAL_MOD_PROTOCOLS_BY_ID));
			ModProtocolInit.LOGGER.warn(b.toString());
		});
	}
}
