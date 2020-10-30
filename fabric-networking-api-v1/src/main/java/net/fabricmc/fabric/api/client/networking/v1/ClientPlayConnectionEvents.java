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

package net.fabricmc.fabric.api.client.networking.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;

/**
 * Offers access to events related to the connection to a server on a logical client.
 */
@Environment(EnvType.CLIENT)
public final class ClientPlayConnectionEvents {
	/**
	 * An event for the initialization of the client play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the server.
	 * Use {@link ClientPlayNetworking#getPlaySender(ClientPlayNetworkHandler)} to obtain the packet sender in the callback.
	 */
	public static final Event<PlayInitialized> PLAY_INITIALIZED = EventFactory.createArrayBacked(PlayInitialized.class, callbacks -> (handler, client, sender) -> {
		for (PlayInitialized callback : callbacks) {
			callback.onPlayInitialized(handler, client, sender);
		}
	});

	/**
	 * An event for the disconnection of the client play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<PlayDisconnected> PLAY_DISCONNECTED = EventFactory.createArrayBacked(PlayDisconnected.class, callbacks -> (handler, client) -> {
		for (PlayDisconnected callback : callbacks) {
			callback.onPlayDisconnected(handler, client);
		}
	});

	private ClientPlayConnectionEvents() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface PlayInitialized {
		void onPlayInitialized(ClientPlayNetworkHandler handler, MinecraftClient client, PlayPacketSender sender);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface PlayDisconnected {
		void onPlayDisconnected(ClientPlayNetworkHandler handler, MinecraftClient client);
	}
}
