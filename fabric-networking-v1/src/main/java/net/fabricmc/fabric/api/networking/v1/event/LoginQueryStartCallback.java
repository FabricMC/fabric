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

package net.fabricmc.fabric.api.networking.v1.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;

/**
 * The callback for sending custom login queries to a client trying to log in.
 *
 * <p>This is fired from the server thread.
 */
@FunctionalInterface
public interface LoginQueryStartCallback {
	/**
	 * The event, fired on the server thread.
	 *
	 * <p>It is invoked when the login handling is ready to accept the player, which
	 * happens after authentication on online mode servers.
	 */
	Event<LoginQueryStartCallback> EVENT = EventFactory.createArrayBacked(LoginQueryStartCallback.class, callbacks -> (server, networkHandler, sender) -> {
		for (LoginQueryStartCallback callback : callbacks) {
			callback.onLoginQuery(server, networkHandler, sender);
		}
	});

	/**
	 * Notifies to send custom login query packets to the client.
	 *
	 * @param server         the minecraft server
	 * @param networkHandler the server login network handler
	 * @param sender         the sender that sends custom login query packets
	 */
	void onLoginQuery(MinecraftServer server, ServerLoginNetworkHandler networkHandler, PacketSender sender);
}
