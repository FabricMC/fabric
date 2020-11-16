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

package net.fabricmc.fabric.impl.networking.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;

@Environment(EnvType.CLIENT)
public final class ClientNetworkingImpl {
	public static final GlobalReceiverRegistry<ClientLoginNetworking.LoginQueryRequestHandler> LOGIN = new GlobalReceiverRegistry<>();
	public static final GlobalReceiverRegistry<ClientPlayNetworking.PlayChannelHandler> PLAY = new GlobalReceiverRegistry<>();

	public static ClientPlayNetworkAddon getAddon(ClientPlayNetworkHandler handler) {
		return ((ClientPlayNetworkHandlerExtensions) handler).getAddon();
	}

	public static ClientLoginNetworkAddon getAddon(ClientLoginNetworkHandler handler) {
		return ((ClientLoginNetworkHandlerExtensions) handler).getAddon();
	}

	public static Packet<?> createPlayC2SPacket(Identifier channelName, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(channelName, buf);
	}

	public static void clientInit() {
		// Register a login query handler for early channel registration.
		ClientLoginNetworking.registerGlobalReceiver(NetworkingImpl.EARLY_REGISTRATION_CHANNEL, (client, handler, buf, listenerAdder) -> {
			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);

			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) handler.getConnection()).getPendingChannelsNames().addAll(ids);
			NetworkingImpl.LOGGER.debug("Received accepted channels from the server");

			PacketByteBuf response = PacketByteBufs.create();
			Collection<Identifier> channels = ClientPlayNetworking.getGlobalReceivers();
			response.writeVarInt(channels.size());

			for (Identifier id : channels) {
				response.writeIdentifier(id);
			}

			NetworkingImpl.LOGGER.debug("Sent accepted channels to the server");
			return CompletableFuture.completedFuture(response);
		});
	}
}
