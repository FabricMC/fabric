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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to events related to the registration of network channels for a client-side network handler.
 */
@Environment(EnvType.CLIENT)
public final class ClientChannelEvents {
	/**
	 * An event for the client play network handler receiving an update indicating the connected server's ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<Registered> REGISTERED = EventFactory.createArrayBacked(Registered.class, callbacks -> (handler, client, sender, channels) -> {
		for (Registered callback : callbacks) {
			callback.onChannelRegistered(handler, client, sender, channels);
		}
	});

	/**
	 * An event for the client play network handler receiving an update indicating the connected server's lack of ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<Unregistered> UNREGISTERED = EventFactory.createArrayBacked(Unregistered.class, callbacks -> (handler, client, sender, channels) -> {
		for (Unregistered callback : callbacks) {
			callback.onChannelUnregistered(handler, client, sender, channels);
		}
	});

	private ClientChannelEvents() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface Registered {
		void onChannelRegistered(ClientPlayNetworkHandler handler, MinecraftClient client, PlayPacketSender sender, List<Identifier> channels);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface Unregistered {
		void onChannelUnregistered(ClientPlayNetworkHandler handler, MinecraftClient client, PlayPacketSender sender, List<Identifier> channels);
	}
}
