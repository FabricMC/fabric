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

package net.fabricmc.fabric.impl.network.login;

import net.fabricmc.fabric.mixin.network.LoginQueryRequestS2CPacketAccessor;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientLoginQueryResponseRegistry {
	public static final ClientLoginQueryResponseRegistry INSTANCE = new ClientLoginQueryResponseRegistry();
	protected static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, ClientLoginQueryResponder> responderMap = new HashMap<>();

	protected ClientLoginQueryResponseRegistry() {

	}

	public void register(Identifier id, ClientLoginQueryResponder responder) {
		if (responderMap.containsKey(id)) {
			LOGGER.warn("Registered duplicate client-side login query responder " + id + "!");
			LOGGER.trace(new Throwable());
		}

		responderMap.put(id, responder);
	}

	public Optional<LoginQueryResponseC2SPacket> respond(ClientLoginNetworkHandler handler, ClientConnection connection, LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccessor packetAccessor = (LoginQueryRequestS2CPacketAccessor) packet;
		ClientLoginQueryResponder responder = responderMap.get(packetAccessor.getChannel());
		if (responder != null) {
			Optional<PacketByteBuf> buf = responder.respond(handler, connection, packetAccessor.getChannel(), packetAccessor.getPayload());
			if (buf.isPresent()) {
				LoginQueryResponseC2SPacket response = new LoginQueryResponseC2SPacket(packetAccessor.getQueryId(), buf.get());
				return Optional.of(response);
			}
		}

		return Optional.empty();
	}
}
