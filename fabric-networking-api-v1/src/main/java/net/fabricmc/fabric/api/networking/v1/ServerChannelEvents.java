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

package net.fabricmc.fabric.api.networking.v1;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to events related to the registration of network channels for a server-side network handler.
 */
public final class ServerChannelEvents {
	/**
	 * An event for the server play network handler receiving an update indicating the connected client's ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Registered> REGISTERED = EventFactory.createArrayBacked(Registered.class, callbacks -> (handler, sender, server, channels) -> {
		for (Registered callback : callbacks) {
			callback.onChannelRegistered(handler, sender, server, channels);
		}
	});

	/**
	 * An event for the server play network handler receiving an update indicating the connected client's lack of ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Unregistered> UNREGISTERED = EventFactory.createArrayBacked(Unregistered.class, callbacks -> (handler, sender, server, channels) -> {
		for (Unregistered callback : callbacks) {
			callback.onChannelUnregistered(handler, sender, server, channels);
		}
	});

	private ServerChannelEvents() {
	}

	@FunctionalInterface
	public interface Registered {
		void onChannelRegistered(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server, List<Identifier> channels);
	}

	@FunctionalInterface
	public interface Unregistered {
		void onChannelUnregistered(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server, List<Identifier> channels);
	}
}
