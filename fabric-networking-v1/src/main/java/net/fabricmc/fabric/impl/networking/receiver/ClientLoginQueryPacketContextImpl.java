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

package net.fabricmc.fabric.impl.networking.receiver;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.ClientLoginQueryPacketContext;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;

public final class ClientLoginQueryPacketContextImpl implements ClientLoginQueryPacketContext {
	private final ClientLoginNetworkHandler handler;
	private final int queryId;

	public ClientLoginQueryPacketContextImpl(ClientLoginNetworkHandler handler, int queryId) {
		this.handler = handler;
		this.queryId = queryId;
	}

	@Override
	public MinecraftClient getEngine() {
		return MinecraftClient.getInstance();
	}

	@Override
	public ClientLoginNetworkHandler getNetworkHandler() {
		return handler;
	}

	@Override
	public void sendResponse(PacketByteBuf buffer) {
		handler.getConnection().send(new LoginQueryResponseC2SPacket(queryId, buffer == null ? PacketByteBufs.empty() : buffer));
	}
}
